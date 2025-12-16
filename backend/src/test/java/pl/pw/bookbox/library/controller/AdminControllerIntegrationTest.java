package pl.pw.bookbox.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.pw.bookbox.library.dto.LoanRequestDto;
import pl.pw.bookbox.library.dto.UserRegistrationDto;
import pl.pw.bookbox.library.model.User;
import pl.pw.bookbox.library.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void cannot_delete_user_with_loans() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.email = "admintest1@example.com";
        dto.password = "secret";
        dto.name = "AdminTest1";

        String reg = mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        User u = objectMapper.readValue(reg, User.class);

        // pick an available book
        String booksJson = mockMvc.perform(get("/api/books")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        var books = objectMapper.readTree(booksJson);
        long bookId = -1;
        for (var b : books) {
            if (b.get("available").asBoolean()) { bookId = b.get("id").asLong(); break; }
        }
        if (bookId == -1) throw new RuntimeException("No available book in DB for test");

        LoanRequestDto loan = new LoanRequestDto();
        loan.userId = u.getId();
        loan.bookId = bookId;

        mockMvc.perform(post("/api/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loan)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/admin/users/" + u.getId()).header("X-ADMIN-KEY", "admin-secret"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Cannot delete user with existing loans"));
    }

    @Test
    public void can_delete_user_without_loans() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.email = "admintest2@example.com";
        dto.password = "secret";
        dto.name = "AdminTest2";

        String reg = mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        User u = objectMapper.readValue(reg, User.class);

        // ensure no loans
        mockMvc.perform(delete("/api/admin/users/" + u.getId()).header("X-ADMIN-KEY", "admin-secret"))
                .andExpect(status().isOk());

        // user should be removed
        mockMvc.perform(get("/api/admin/users").header("X-ADMIN-KEY", "admin-secret"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + u.getId() + ")]").doesNotExist());
    }
}

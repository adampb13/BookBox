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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class UserLoanIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void register_and_create_loan() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.email = "test@example.com";
        dto.password = "secret";
        dto.name = "Test";

        // register
        String reg = mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        User u = objectMapper.readValue(reg, User.class);

        // create loan for book id 1
        LoanRequestDto loan = new LoanRequestDto();
        loan.userId = u.getId();
        loan.bookId = 1L;

        mockMvc.perform(post("/api/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loan)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(1));

        // user loans
        mockMvc.perform(get("/api/loans/user/" + u.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(u.getId()));
    }
}

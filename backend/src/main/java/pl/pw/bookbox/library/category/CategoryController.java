package pl.pw.bookbox.library.category;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Lista kategorii – publiczna
    @GetMapping
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    // Szczegóły kategorii – publiczne
    @GetMapping("/{id}")
    public ResponseEntity<Category> getOne(@PathVariable Long id) {
        return categoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Dodawanie kategorii (realnie dla admina – kontrola w SecurityConfig / roli)
    @PostMapping
    public ResponseEntity<Category> create(@Valid @RequestBody CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            // prosto: 400 jeśli nazwa już istnieje
            return ResponseEntity.badRequest().build();
        }
        Category category = new Category(request.getName(), request.getDescription());
        Category saved = categoryRepository.save(category);
        return ResponseEntity.ok(saved);
    }

    // Edycja kategorii
    @PutMapping("/{id}")
    public ResponseEntity<Category> update(@PathVariable Long id,
                                           @Valid @RequestBody CategoryRequest request) {
        return categoryRepository.findById(id)
                .map(existing -> {
                    existing.setName(request.getName());
                    existing.setDescription(request.getDescription());
                    Category saved = categoryRepository.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Usuwanie kategorii
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!categoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        categoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

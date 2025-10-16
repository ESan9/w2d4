package emanuelesanna.w2d4.controllers;

import emanuelesanna.w2d4.entities.Blog;
import emanuelesanna.w2d4.exceptions.ValidationException;
import emanuelesanna.w2d4.payloads.NewBlogDTO;
import emanuelesanna.w2d4.services.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/blogPosts")
public class BlogController {
    @Autowired
    private BlogService blogService;

    // 1. GET http://localhost:3001/blogPosts?page=0&size=10&sortBy=id
    @GetMapping
    public Page<Blog> findAllBlogs(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(defaultValue = "id") String sortBy) {
        return this.blogService.findAllBlogs(page, size, sortBy);
    }

    // 2. POST http://localhost:3001/blogPosts (+ payload)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Blog createBlog(@RequestBody @Validated NewBlogDTO payload, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            // Se ci sono errori di validazione (es. email non valida o campo mancante), lancia un'eccezione
            throw new ValidationException(validationResult.getFieldErrors()
                    .stream().map(fieldError -> fieldError.getDefaultMessage()).toList());
        }
        return this.blogService.saveBlog(payload);
    }

    // 3. GET http://localhost:3001/blogPosts/{blogId}
    @GetMapping("/{blogId}")
    public Blog findBlogById(@PathVariable UUID blogId) {
        return this.blogService.findById(blogId);
    }

    // 4. PUT http://localhost:3001/blogPosts/{blogId} (+ payload)
    @PutMapping("/{blogId}")
    public Blog findByIdAndUpdate(@PathVariable UUID blogId, @RequestBody @Validated NewBlogDTO payload, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            // Lancia l'eccezione di validazione se i dati aggiornati non rispettano i vincoli del DTO.
            throw new ValidationException(validationResult.getFieldErrors()
                    .stream().map(fieldError -> fieldError.getDefaultMessage()).toList());
        }
        return this.blogService.findByIdAndUpdate(blogId, payload);
    }

    // 5. DELETE http://localhost:3001/blogPosts/{blogId}
    @DeleteMapping("/{blogId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void findByIdAndDelete(@PathVariable UUID blogId) {
        this.blogService.findByIdAndDelete(blogId);
    }
}
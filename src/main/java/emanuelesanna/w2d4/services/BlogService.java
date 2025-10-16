package emanuelesanna.w2d4.services;

import emanuelesanna.w2d4.entities.Author;
import emanuelesanna.w2d4.entities.Blog;
import emanuelesanna.w2d4.exceptions.NotFoundException;
import emanuelesanna.w2d4.payloads.NewBlogDTO;
import emanuelesanna.w2d4.repositories.AuthorsRepository;
import emanuelesanna.w2d4.repositories.BlogsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class BlogService {
    @Autowired
    private BlogsRepository blogsRepository;

    @Autowired
    private AuthorsRepository authorsRepository;

    public Page<Blog> findAllBlogs(int blogsNumber, int blogsSize, String sortBy) {
        if (blogsSize > 50) blogsSize = 50;
        Pageable pageable = PageRequest.of(blogsNumber, blogsSize, Sort.by(sortBy).ascending());
        return this.blogsRepository.findAll(pageable);
    }

    public Blog saveBlog(NewBlogDTO payload) {
        // 1. Trova l'autore tramite l'ID fornito nel payload
        Author author = authorsRepository.findById(payload.authorId())
                .orElseThrow(() -> new NotFoundException(payload.authorId()));

        // 2. Crea un nuovo oggetto Blog con i dati del payload
        Blog newBlog = new Blog(payload.categoria(), payload.titolo(), payload.contenuto(), payload.tempoDiLettura());
        newBlog.setAuthor(author); // Setta l'autore sul blog

        // 3. Salva il blog
        return blogsRepository.save(newBlog);
    }

    public Blog findById(UUID blogId) {
        return this.blogsRepository.findById(blogId).orElseThrow(() -> new NotFoundException(blogId));
    }

    public Blog findByIdAndUpdate(UUID blogId, NewBlogDTO payload) {
        Blog found = this.findById(blogId);
        found.setCategoria(payload.categoria());
        found.setTitolo(payload.titolo());
        found.setContenuto(payload.contenuto());
        found.setTempoDiLettura(payload.tempoDiLettura());
        log.info("Il blog con ID " + found.getId() + " è stato modificato correttamente.");
        return this.blogsRepository.save(found);
    }

    public void findByIdAndDelete(UUID blogId) {
        Blog found = this.findById(blogId);
        this.blogsRepository.delete(found);
        log.info("Il blog con ID " + blogId + " è stato eliminato correttamente.");
    }
}
package emanuelesanna.w2d4.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import emanuelesanna.w2d4.entities.Author;
import emanuelesanna.w2d4.entities.Blog;
import emanuelesanna.w2d4.exceptions.BadRequestException;
import emanuelesanna.w2d4.exceptions.NotFoundException;
import emanuelesanna.w2d4.payloads.NewAuthorDTO;
import emanuelesanna.w2d4.repositories.AuthorsRepository;
import emanuelesanna.w2d4.repositories.BlogsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class AuthorService {

    @Autowired
    private Cloudinary imageUploader;

    @Autowired
    private AuthorsRepository authorsRepository;

    @Autowired
    private BlogsRepository blogsRepository;

    public Page<Author> findAllAuthors(int authorsNumber, int authorsSize, String sortBy) {
        if (authorsSize > 50) authorsSize = 50;
        Pageable pageable = PageRequest.of(authorsNumber, authorsSize, Sort.by(sortBy).ascending());
        return this.authorsRepository.findAll(pageable);
    }

    public Author saveAuthor(NewAuthorDTO payload) {
        // 1. Verifichiamo che l'email passata non sia già in uso
        this.authorsRepository.findByEmail(payload.email()).ifPresent(author -> {
                    throw new BadRequestException("L'email " + author.getEmail() + " è già in uso!");
                }
        );

        Author newAuthor = new Author(payload.nome(), payload.email(), payload.dataDiNascita());
        newAuthor.setAvatarURL("https://ui-avatars.com/api/?name=" + payload.nome());

        Author savedAuthor = this.authorsRepository.save(newAuthor);

        log.info("L'autore con id: " + savedAuthor.getId() + " è stato salvato correttamente");
        return savedAuthor;
    }

    public Author findById(UUID authorId) {
        return this.authorsRepository.findById(authorId).orElseThrow(() -> new NotFoundException(authorId));
    }

    public Author findByIdAndUpdate(UUID authorId, NewAuthorDTO payload) {
        // 1. Cerco l'utente nel db
        Author found = this.findById(authorId);

        // 2. Controllo che la nuova email non sia già in uso
        if (!found.getEmail().equals(payload.email())) { // Il controllo sull'email lo faccio solo quando effettivamente
            // mi viene passata un'email diversa da quella precedente
            this.authorsRepository.findByEmail(payload.email()).ifPresent(author -> {
                        throw new BadRequestException("L'email " + author.getEmail() + " è già in uso!");
                    }
            );
        }

        // 3. Modifico l'utente trovato nel db
        found.setNome(payload.nome());
        found.setEmail(payload.email());
        found.setDataDiNascita(payload.dataDiNascita());
        found.setAvatarURL("https://ui-avatars.com/api/?name=" + payload.nome());

        // 4. Salvo
        Author modifiedAuthor = this.authorsRepository.save(found);

        // 5. Log
        log.info("L'autore con id " + modifiedAuthor.getId() + " è stato modificato correttamente");

        // 6. Return dell'utente modificato
        return modifiedAuthor;
    }

    public void findByIdAndDelete(UUID authorId) {
        Author found = this.findById(authorId);
        List<Blog> founds = this.blogsRepository.findBlogByAuthorId(authorId);
        this.blogsRepository.deleteAll(founds);
        this.authorsRepository.delete(found);
    }

    public Author uploadAvatar(UUID authorId, MultipartFile file) {
        // 1. Cerco l'autore (lancia NotFoundException se non esiste) - LOGICA CORRETTA
        Author foundAuthor = this.findById(authorId);

        try {
            // 2. Upload del file su Cloudinary
            Map result = imageUploader.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageURL = (String) result.get("url");

            // 3. Aggiorno l'URL dell'avatar e salvo l'autore nel DB
            foundAuthor.setAvatarURL(imageURL);
            Author modifiedAuthor = this.authorsRepository.save(foundAuthor);

            // 4. Log con placeholder
            log.info("L'avatar dell'autore con id {} è stato aggiornato correttamente. Nuovo URL: {}", authorId, imageURL);

            // 5. Restituisco l'oggetto Author aggiornato
            return modifiedAuthor;

        } catch (IOException e) {
            
            log.error("Errore durante l'upload dell'immagine per l'autore {}: {}", authorId, e.getMessage());
            throw new RuntimeException("Errore del servizio di storage durante l'upload dell'immagine.", e);
        }
    }
}

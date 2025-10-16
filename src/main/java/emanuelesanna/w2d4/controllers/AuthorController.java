package emanuelesanna.w2d4.controllers;

import emanuelesanna.w2d4.entities.Author;
import emanuelesanna.w2d4.exceptions.ValidationException;
import emanuelesanna.w2d4.payloads.NewAuthorDTO;
import emanuelesanna.w2d4.services.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/authors")
public class AuthorController {
    @Autowired
    private AuthorService authorService;

    // 1 GET http://localhost:3001/authors 200 OK
    // Mappa la richiesta per ottenere tutti gli autori con supporto per paginazione e ordinamento.
    @GetMapping
    public Page<Author> findAll(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(defaultValue = "id") String sortBy) {
        // Mettere dei valori di default nei query params è solitamente una buona idea per far si che non
        // ci siano errori se il client non li passa
        return this.authorService.findAllAuthors(page, size, sortBy);
    }

    // 2 POST http://localhost:3001/authors (+ payload) 201 CREATED
    // Gestisce la creazione di un nuovo autore.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Best practice: usa 201 Created per le operazioni POST che creano risorse
    public Author createAuthor(@RequestBody @Validated NewAuthorDTO payload, BindingResult validationResult) {
        // @Validated serve per "attivare" la validazione
        // BindingResult è un oggetto che contiene tutti gli errori e anche dei metodi comodi da usare tipo .hasErrors()
        if (validationResult.hasErrors()) {
            // Se ci sono errori di validazione (es. email non valida o campo mancante), lancia un'eccezione
            throw new ValidationException(validationResult.getFieldErrors()
                    .stream().map(fieldError -> fieldError.getDefaultMessage()).toList());
        }
        return this.authorService.saveAuthor(payload);
    }

    // 3 GET http://localhost:3001/authors/{authorId} 200 OK
    // Ricerca un autore specifico tramite ID.
    @GetMapping("/{authorId}")
    public Author findById(@PathVariable UUID authorId) {
        return this.authorService.findById(authorId);
    }

    // 4 PUT http://localhost:3001/authors/{authorId} + payload 200 OK
    // Aggiorna completamente un autore esistente tramite ID.
    @PutMapping("/{authorId}")
    public Author findByIdAndUpdate(@PathVariable UUID authorId, @RequestBody @Validated NewAuthorDTO payload, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult.getFieldErrors()
                    .stream().map(fieldError -> fieldError.getDefaultMessage()).toList());
        }
        return this.authorService.findByIdAndUpdate(authorId, payload);
    }

    // 5 DELETE http://localhost:3001/authors/{authorId} 204 NC
    // Cancella un autore specifico tramite ID.
    @DeleteMapping("/{authorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Best practice: usa 204 No Content per le operazioni DELETE riuscite
    public void findByIdAndDelete(@PathVariable UUID authorId) {
        this.authorService.findByIdAndDelete(authorId);
    }

    // PATCH: Endpoint per l'upload dell'avatar (operazione parziale per aggiornare solo l'avatarURL)
    @PatchMapping("/{authorId}/avatar")
//    Aggiunto @PathVariable UUID authorId per identificare l'autore da aggiornare.
    @ResponseStatus(HttpStatus.OK)
    public Author uploadImage(@PathVariable UUID authorId, @RequestParam("avatar") MultipartFile file) throws IOException {
        // "avatar" deve corrispondere ESATTAMENTE al nome del campo del MultiPart che contiene il file
        // che è quello che verrà inserito dal frontend. Se non corrisponde non troverò il file.
        // Ho cambiato il tipo di ritorno a 'Author' per dare un feedback completo (l'autore aggiornato).

        System.out.println(file.getSize());
        System.out.println(file.getOriginalFilename());

        //  Passa l'ID dell'autore al service per aggiornare il record corretto.
        return this.authorService.uploadAvatar(authorId, file);
    }
}
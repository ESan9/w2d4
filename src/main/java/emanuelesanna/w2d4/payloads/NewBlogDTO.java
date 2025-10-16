package emanuelesanna.w2d4.payloads;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record NewBlogDTO(
        @NotBlank(message = "La categoria è obbligatoria!")
        @Size(min = 2, max = 30, message = "La categoria deve avere una lunghezza compresa tra 2 e 30 caratteri")
        String categoria,
        @NotBlank(message = "Il titolo è obbligatorio!")
        @Size(min = 2, max = 30, message = "Il titolo deve avere una lunghezza compresa tra 2 e 30 caratteri")
        String titolo,
        @NotBlank(message = "Il contenuto è obbligatorio!")
        @Size(min = 5, max = 3000, message = "Il contenuto deve avere una lunghezza compresa tra 5 e 3000 caratteri")
        String contenuto,
        @NotNull(message = "Il tempo di lettura è obbligatorio!")
        @Min(value = 1, message = "Il tempo di lettura non può essere inferiore a 1 minuto")
        @Max(value = 120, message = "Il tempo di lettura non può superare i 120 minuti")
        int tempoDiLettura,
        @NotNull(message = "L'ID dell'autore è obbligatorio!")
        UUID authorId) {
}

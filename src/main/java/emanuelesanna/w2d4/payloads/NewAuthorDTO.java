package emanuelesanna.w2d4.payloads;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record NewAuthorDTO(
        @NotBlank(message = "Il nome è obbligatorio.")
        @Size(min = 2, max = 50, message = "Il nome deve avere tra 2 e 50 caratteri.")
        String nome,
        @NotBlank(message = "L'email è obbligatoria.")
        @Email(message = "Il formato dell'email non è valido.")
        @Size(max = 100, message = "L'email non può superare i 100 caratteri.")
        String email,
        @NotNull(message = "La data di nascita è obbligatoria.")
        @Past(message = "La data di nascita non può essere futura.")
        LocalDate dataDiNascita) {
}


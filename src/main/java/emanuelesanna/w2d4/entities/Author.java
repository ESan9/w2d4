package emanuelesanna.w2d4.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "authors")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Author {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;
    private String nome;
    private String email;
    private LocalDate dataDiNascita;
    private String avatarURL;

    public Author(String nome, String email, LocalDate dataDiNascita) {
        this.nome = nome;
        this.email = email;
        this.dataDiNascita = dataDiNascita;
    }
}

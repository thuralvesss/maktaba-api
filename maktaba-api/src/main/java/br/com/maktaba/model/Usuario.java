package br.com.maktaba.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username;

    private String senhaHash;

    private LocalDate dataNascimento;

    // Alterado para uma coleção de Strings para validar de 1 a 8 gêneros facilmente
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_interesses", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "genero")
    private Set<String> interessesLiterarios = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
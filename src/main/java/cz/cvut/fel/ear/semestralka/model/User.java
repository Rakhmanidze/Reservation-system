package cz.cvut.fel.ear.semestralka.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@Table(name = "app_user")
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@AllArgsConstructor
@NamedQueries({
        @NamedQuery(
                name = "User.findByEmail",
                query = "SELECT u FROM User u WHERE u.email = :email"
        ),
        @NamedQuery(
                name = "User.countByEmail",
                query = "SELECT COUNT(u) FROM User u WHERE u.email = :email"
        )
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @OneToOne
    @JoinColumn(name = "membership_id")
    private Membership membership;

    @Column(name = "user_email", unique = true, nullable = false)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "room_number")
    private String roomNumber;

    @Column(name = "password")
    private String password;
}
package cz.cvut.fel.ear.semestralka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserDto {
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String roomNumber;
    private String phoneNumber;
    private Integer membershipId;
    private String password;
}

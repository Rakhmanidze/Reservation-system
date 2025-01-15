package cz.cvut.fel.ear.semestralka.dto;


import cz.cvut.fel.ear.semestralka.model.enums.MembershipStatus;
import cz.cvut.fel.ear.semestralka.model.enums.TypeOfMembership;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class MembershipDto {
    private Integer membershipId;
    private TypeOfMembership type;
    private MembershipStatus status;
    private LocalDate start;
    private LocalDate end;
}

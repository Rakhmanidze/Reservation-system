package cz.cvut.fel.ear.semestralka.dto;

import cz.cvut.fel.ear.semestralka.model.enums.TypeOfMembership;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class FacilityDto {
    private Integer facilityId;
    private String description;
    private String name;
    private int capacity;
    private boolean isAvailable;
    private TypeOfMembership requiredMembershipType;
}

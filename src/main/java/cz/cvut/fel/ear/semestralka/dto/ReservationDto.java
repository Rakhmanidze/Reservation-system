package cz.cvut.fel.ear.semestralka.dto;

import cz.cvut.fel.ear.semestralka.model.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ReservationDto {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private int capacity;
    private Integer facilityId;
    private Integer userId;
    private ReservationStatus status;
}

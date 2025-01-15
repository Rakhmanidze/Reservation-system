package cz.cvut.fel.ear.semestralka.dto;

import cz.cvut.fel.ear.semestralka.model.Membership;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PaymentDto {
    private int paymentId;
    private int amount;
    private LocalDate date;
    private List<Integer> membershipIds;
}

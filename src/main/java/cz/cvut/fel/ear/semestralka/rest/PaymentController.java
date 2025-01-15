package cz.cvut.fel.ear.semestralka.rest;


import cz.cvut.fel.ear.semestralka.dto.PaymentDto;
import cz.cvut.fel.ear.semestralka.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Void> addPayment(@RequestBody PaymentDto paymentDto) {
        paymentDto.setDate(LocalDate.now());
        paymentService.addPayment(paymentDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<PaymentDto>> getPayments() {
        List<PaymentDto> paymentDtos = paymentService.getAllPayments();
        return ResponseEntity.ok(paymentDtos);
    }
}

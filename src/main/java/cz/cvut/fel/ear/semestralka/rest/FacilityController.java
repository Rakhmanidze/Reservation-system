package cz.cvut.fel.ear.semestralka.rest;

import cz.cvut.fel.ear.semestralka.dto.FacilityDto;
import cz.cvut.fel.ear.semestralka.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facilities")
@RequiredArgsConstructor
public class FacilityController {
    private final FacilityService facilityService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> addFacility(@RequestBody FacilityDto facilityDto) {
        facilityService.addFacility(facilityDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<FacilityDto>> getAllFacilities() {
        List<FacilityDto> facilityDtos = facilityService.getAllFacilities();
        return ResponseEntity.ok(facilityDtos);
    }

    @DeleteMapping("/{facilityId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteFacility(@PathVariable Integer facilityId) {
        facilityService.deleteFacility(facilityId);
        return ResponseEntity.noContent().build();
    }


}

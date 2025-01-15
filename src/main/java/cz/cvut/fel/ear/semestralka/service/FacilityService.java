package cz.cvut.fel.ear.semestralka.service;

import cz.cvut.fel.ear.semestralka.dao.FacilityDao;
import cz.cvut.fel.ear.semestralka.dto.FacilityDto;
import cz.cvut.fel.ear.semestralka.exception.FacilityException;
import cz.cvut.fel.ear.semestralka.model.Facility;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class FacilityService {
    private final FacilityDao facilityDao;

    @Transactional
    public void addFacility(FacilityDto facilityDto) {
        Facility facility = mapToEntity(facilityDto);
        facilityDao.persist(facility);
    }

    @Transactional
    public List<FacilityDto> getAllFacilities() {
        List<Facility> facilities = facilityDao.findAll();
        return facilities.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional
    public void deleteFacility(Integer facilityId) {
        Facility facility = facilityDao.find(facilityId);
        if(facility == null) {
            throw new FacilityException("Facility not found with id: " + facilityId);
        }
        facilityDao.remove(facility);
    }


    public FacilityDto mapToDto(Facility facility) {
        return FacilityDto.builder()
                .facilityId(facility.getFacilityId())
                .name(facility.getName())
                .description(facility.getDescription())
                .capacity(facility.getCapacity())
                .isAvailable(facility.isAvailable())
                .requiredMembershipType(facility.getRequiredMembershipType())
                .build();
    }

    public Facility mapToEntity(FacilityDto facilityDto) {
        return Facility.builder()
                .name(facilityDto.getName())
                .description(facilityDto.getDescription())
                .capacity(facilityDto.getCapacity())
                .isAvailable(facilityDto.isAvailable())
                .requiredMembershipType(facilityDto.getRequiredMembershipType())
                .build();
    }
}
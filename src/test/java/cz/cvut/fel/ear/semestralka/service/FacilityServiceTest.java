package cz.cvut.fel.ear.semestralka.service;

import cz.cvut.fel.ear.semestralka.dao.FacilityDao;
import cz.cvut.fel.ear.semestralka.dao.GenericDao;
import cz.cvut.fel.ear.semestralka.dto.FacilityDto;
import cz.cvut.fel.ear.semestralka.exception.FacilityException;
import cz.cvut.fel.ear.semestralka.model.Facility;
import cz.cvut.fel.ear.semestralka.model.enums.TypeOfMembership;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.client.ExpectedCount;

import java.util.Arrays;
import java.util.List;

import static org.apache.catalina.security.SecurityUtil.remove;
import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.ExpectedCount.never;
import static org.springframework.test.web.client.ExpectedCount.times;

public class FacilityServiceTest {

    @Mock
    private FacilityDao facilityDao;

    @InjectMocks
    private FacilityService facilityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addFacility_ShouldPersistFacility() {
        FacilityDto dto = new FacilityDto(null, "Gym", "Large gym", 100, true, TypeOfMembership.BASIC);

        facilityService.addFacility(dto);

        ArgumentCaptor<Facility> facilityCaptor = ArgumentCaptor.forClass(Facility.class);
        verify(facilityDao).persist(facilityCaptor.capture());

        Facility capturedFacility = facilityCaptor.getValue();

        assertEquals("Gym", capturedFacility.getDescription());
        assertEquals("Large gym", capturedFacility.getName());
        assertEquals(100, capturedFacility.getCapacity());
        assertTrue(capturedFacility.isAvailable());
        assertEquals(TypeOfMembership.BASIC, capturedFacility.getRequiredMembershipType());
    }

    @Test
    void getAllFacilities_ReturnsFacilityList() {
        List<Facility> facilities = Arrays.asList(
                Facility.builder().facilityId(1).name("Gym").description("Large gym").capacity(100).isAvailable(true).requiredMembershipType(TypeOfMembership.BASIC).build(),
                Facility.builder().facilityId(2).name("Pool").description("Olympic size pool").capacity(50).isAvailable(true).requiredMembershipType(TypeOfMembership.ADVANCED).build()
        );
        when(facilityDao.findAll()).thenReturn(facilities);

        List<FacilityDto> result = facilityService.getAllFacilities();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Gym", result.get(0).getName());
        assertEquals("Pool", result.get(1).getName());
    }

    @Test
    void deleteFacility_RemovesFacility_WhenFacilityExists() {
        Integer facilityId = 1;
        Facility facility = Facility.builder().facilityId(facilityId).name("Gym").description("Large gym").capacity(100).isAvailable(true).requiredMembershipType(TypeOfMembership.BASIC).build();
        when(facilityDao.find(facilityId)).thenReturn(facility);

        facilityService.deleteFacility(facilityId);

        verify(facilityDao).remove(facility);
    }

}

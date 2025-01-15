package cz.cvut.fel.ear.semestralka.dao;

import cz.cvut.fel.ear.semestralka.model.Facility;
import org.springframework.stereotype.Repository;

@Repository
public class FacilityDao extends BaseDao<Facility> {
    public FacilityDao() {super(Facility.class);}
}

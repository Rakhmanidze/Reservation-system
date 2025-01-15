package cz.cvut.fel.ear.semestralka.model.enums;

public enum TypeOfMembership {
    BASIC, ADVANCED, NONE;

    public boolean canAccess(TypeOfMembership requiredType) {
        if (this == ADVANCED) {
            return true;
        } else return this == BASIC && requiredType == BASIC;
    }
}

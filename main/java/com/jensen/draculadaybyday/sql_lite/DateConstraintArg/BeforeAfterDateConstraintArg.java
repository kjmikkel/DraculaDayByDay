package com.jensen.draculadaybyday.sql_lite.DateConstraintArg;

public abstract class BeforeAfterDateConstraintArg extends DateConstraintArg {

    boolean inclusive;

    BeforeAfterDateConstraintArg() {

    }

    BeforeAfterDateConstraintArg(boolean inclusive) {
        this.inclusive = inclusive;
    }

    public boolean getInclusive() {
        return inclusive;
    }

    public void setInclusive(boolean inclusive) { this.inclusive = inclusive; }

    @Override
    public boolean isValueDefault() {
        return super.isValueDefault() && !inclusive;
    }

}

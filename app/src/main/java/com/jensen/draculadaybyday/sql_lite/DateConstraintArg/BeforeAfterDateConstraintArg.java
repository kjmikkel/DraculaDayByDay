package com.jensen.draculadaybyday.sql_lite.DateConstraintArg;

public abstract class BeforeAfterDateConstraintArg extends DateConstraintArg {

    protected boolean inclusive;

    public BeforeAfterDateConstraintArg() {

    }

    public BeforeAfterDateConstraintArg(boolean inclusive) {
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

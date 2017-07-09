package com.jensen.draculadaybyday.presentation;

public enum FontEnum {
    DROID_SANS, DROID_SERIF, DROID_SANS_MONO, RAVALI, HARRINGTON, VICTORIAN, VICTORIAN_WITH_INITIAL, NONE;

    public boolean withInitial() {
        return this.name().contains("WITH_INITIAL");
    }
}

package com.topostechnology.rest.client.response;

public class FreeUnit {

    public String unusedamt;
    public String name;

    public FreeUnit() {
    }

    public FreeUnit(String unusedamt, String name) {
        this.unusedamt = unusedamt;
        this.name = name;
    }

    public String getUnusedamt() {
        return unusedamt;
    }

    public void setUnusedamt(String unusedamt) {
        this.unusedamt = unusedamt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

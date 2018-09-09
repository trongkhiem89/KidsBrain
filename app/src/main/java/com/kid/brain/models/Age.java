package com.kid.brain.models;

import java.io.Serializable;

/**
 * Created by khiemnt on 4/17/17.
 */

public class Age implements Serializable {

    private int days;
    private int months;
    private int years;

    private Age() {
    }

    public Age(int days, int months, int years) {
        this.days = days;
        this.months = months;
        this.years = years;
    }

    public int getDays() {
        return this.days;
    }

    public int getMonths() {
        return this.months;
    }

    public int getYears() {
        return this.years;
    }

    @Override
    public String toString() {
        return years + " Years, " + months + " Months, " + days + " Days";
    }
}

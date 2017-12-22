package com.itlibrium.cooldomain.domain;

import java.time.Duration;

public class InterventionDuration {

    public static InterventionDuration FromHours(int hours) {
        return new InterventionDuration(Duration.ofHours(hours));
    }

    public static InterventionDuration subtract(InterventionDuration duration1, InterventionDuration duration2){
        if(duration1.value.compareTo(duration2.value) == -1)
            return new InterventionDuration(Duration.ZERO);

        return new InterventionDuration(duration1.value.minus(duration1.value));
    }

    private Duration value;

    public double getHours() {
        return value.toMinutes() / 60;
    }

    private InterventionDuration(Duration value) {
        if (value.getSeconds() < 0) throw new IllegalArgumentException();
        this.value = value;
    }
}

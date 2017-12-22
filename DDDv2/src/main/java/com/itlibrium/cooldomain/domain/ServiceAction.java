package com.itlibrium.cooldomain.domain;

import lombok.Getter;

import java.util.List;

public class ServiceAction {
    @Getter
    private final ServiceActionType Type;
    @Getter
    private final InterventionDuration Duration;
    @Getter
    private final List<Integer> SparePartIds;

    public ServiceAction(ServiceActionType type, InterventionDuration duration, List<Integer> sparePartIds) {
        Type = type;
        Duration = duration;
        SparePartIds = sparePartIds;
    }
}

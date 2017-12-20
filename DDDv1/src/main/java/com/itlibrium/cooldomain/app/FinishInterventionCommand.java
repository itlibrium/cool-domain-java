package com.itlibrium.cooldomain.app;

import com.itlibrium.cooldomain.domain.ServiceAction;
import lombok.Getter;

import java.util.Collection;

public class FinishInterventionCommand {
    @Getter
    private final int interventionId;
    @Getter
    private final Collection<ServiceAction> serviceActions;

    public FinishInterventionCommand(int interventionId, Collection<ServiceAction> serviceActions) {
        this.interventionId = interventionId;
        this.serviceActions = serviceActions;
    }
}

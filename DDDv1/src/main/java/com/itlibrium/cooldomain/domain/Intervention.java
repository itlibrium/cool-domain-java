package com.itlibrium.cooldomain.domain;

import com.itlibrium.cooldomain.BusinessException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Intervention {
    private final int id;
    @Getter
    private final int clientId;
    @Getter
    private final int engineerId;
    private final List<ServiceActionType> serviceActionTypes;

    private List<ServiceAction> serviceActions;

    @Getter
    private Money price;

    public static Intervention createFor(int clientId, int engineerId, Collection<ServiceActionType> serviceActionTypes) {
        return new Intervention(clientId, engineerId, serviceActionTypes);
    }

    private Intervention(int clientId, int engineerId, Collection<ServiceActionType> serviceActionTypes) {
        id = 0;
        this.clientId = clientId;
        this.engineerId = engineerId;
        this.serviceActionTypes = new ArrayList<>(serviceActionTypes);
    }

    public Intervention(int id, int clientId, int engineerId, List<ServiceActionType> serviceActionTypes,
                        Collection<ServiceAction> serviceActions) {
        this.id = id;
        this.clientId = clientId;
        this.engineerId = engineerId;
        this.serviceActionTypes = serviceActionTypes;
        this.serviceActions = new ArrayList<>(serviceActions);
    }

    public void finish(Collection<ServiceAction> serviceActions, PricePolicy pricePolicy)
            throws BusinessException {
        if (this.serviceActions != null)
            throw new BusinessException("Nie można zakończyć interwencji więcej niż raz");

        this.serviceActions = new ArrayList<>(serviceActions);
        price =  this.serviceActions.stream()
            .map(pricePolicy::apply)
            .reduce(Money.ZERO, Money::sum);
    }
}

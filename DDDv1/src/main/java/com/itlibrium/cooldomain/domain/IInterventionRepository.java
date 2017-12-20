package com.itlibrium.cooldomain.domain;

public interface IInterventionRepository {
    Intervention get(int id);
    void save(Intervention intervention);
}

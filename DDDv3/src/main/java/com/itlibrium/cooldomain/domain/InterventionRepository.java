package com.itlibrium.cooldomain.domain;

public interface InterventionRepository {
    Intervention get(int id);
    void save(Intervention intervention);
}

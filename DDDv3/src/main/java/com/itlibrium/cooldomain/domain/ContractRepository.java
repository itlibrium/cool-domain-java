package com.itlibrium.cooldomain.domain;

public interface ContractRepository {
    Contract getForClient(int id);

    void save(Contract contract);
}

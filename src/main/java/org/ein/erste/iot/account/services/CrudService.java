package org.ein.erste.iot.account.services;

public interface CrudService<E, V, I> {

    E create(V dto);

    E read(I id);

    V readDTO(I id);

    E update(I id, V dto);

    void delete(I id);
}

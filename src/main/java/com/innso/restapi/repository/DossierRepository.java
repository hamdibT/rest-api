package com.innso.restapi.repository;

import com.innso.restapi.model.Dossier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DossierRepository extends CrudRepository<Dossier, Long> {
}

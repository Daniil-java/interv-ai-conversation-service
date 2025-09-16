package com.kuklin.aiconversationservice.repositories;

import com.kuklin.aiconversationservice.entities.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {

    @Query(value = "SELECT m FROM Model m WHERE m.modelName = :model")
    Optional<Model> findByModelName(String model);

}

package com.example.demo.model.db.repository;

import com.example.demo.model.db.entity.Car;
import com.example.demo.model.dto.response.CarInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarRepo extends JpaRepository<Car, Long> {

    @Query("select c from Car c where c.status <> '2'")
    Page<Car> findAllNotDeleted(Pageable request);

    @Query("select c from Car c where c.status <> '2' and c.brand like %:brand%")
    Page<Car> findAllNotDeletedBrand(Pageable request, @Param("brand") String brand);

    @Query("select c from Car c where c.status <> '2' and c.model like %:model%")
    Page<Car> findAllNotDeletedModel(Pageable request, @Param("model") String model);

    @Query("select c from Car c where c.status <> '2' and c.model like %:model% and c.brand like %:brand%")
    Page<Car> findAllNotDeleted(Pageable request, @Param("brand") String brand, @Param("model") String model);

    Optional<Car> findByModelAndAndBrand (String model, String brand);
}

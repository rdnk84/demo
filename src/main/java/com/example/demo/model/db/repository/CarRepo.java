package com.example.demo.model.db.repository;

import com.example.demo.model.db.entity.Car;
import com.example.demo.model.dto.response.CarInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CarRepo extends JpaRepository<Car, Long> {

    @Query("select c from Car c where c.status <> '2'")
    Page<Car> findAllNotDeleted(Pageable request);

    @Query("select c from Car c where c.status <> '2' and (c.brand like %:filter% or c.model like %:filter%)")
    Page<Car> findAllNotDeleted(Pageable request, @Param("filter") String filter);



}

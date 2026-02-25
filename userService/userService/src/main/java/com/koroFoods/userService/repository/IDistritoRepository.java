package com.koroFoods.userService.repository;

import com.koroFoods.userService.model.Distrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDistritoRepository  extends JpaRepository<Distrito, Integer> {
}

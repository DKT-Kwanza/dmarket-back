package com.dmarket.repository.user;

import com.dmarket.domain.user.Mileage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MileageRepository extends JpaRepository<Mileage, Long> {
}

package com.dmarket.repository.order;

import com.dmarket.domain.order.Return;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnRepository extends JpaRepository<Return, Long> {
}

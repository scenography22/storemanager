package com.example.storemanager.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesOrderDetailRepository extends JpaRepository<SalesOrderDetail, Long> {

}

package com.example.storemanager.order;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SalesOrderController {
	private SalesOrderRepository salesOrderRepo;
	private SalesOrderDetailRepository salesOrderDetailRepo;

	@Autowired
	public SalesOrderController(SalesOrderRepository salesOrderRepo, SalesOrderDetailRepository salesOrderDetailRepo) {
		this.salesOrderRepo = salesOrderRepo;
		this.salesOrderDetailRepo = salesOrderDetailRepo;
	}

	// -----------------------------------------------------
	// Read
	// product 목록 조회
	@RequestMapping(value = "/sales-orders", method = RequestMethod.GET)
	public List<SalesOrder> getSalesOrders(HttpServletRequest req) {
		return salesOrderRepo.findAll(Sort.by("id").descending());
	}
}

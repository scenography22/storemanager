package com.example.storemanager.order;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
	// sales-order 목록 조회
	@RequestMapping(value = "/sales-orders", method = RequestMethod.GET)
	public List<SalesOrder> getSalesOrders(HttpServletRequest req) {
		return salesOrderRepo.findAll(Sort.by("id").descending());
	}

	// -----------------------------------------------------
	// Update
	// product 1건 수정
	@RequestMapping(value = "/sales-order/{id}", method = RequestMethod.PUT)
	public SalesOrder modifyStatus(@PathVariable("id") long id, @RequestParam("orderStatus") String orderStatus,
			HttpServletResponse res) {

		SalesOrder salesOrder = salesOrderRepo.findById(id).orElse(null);

		if (salesOrder == null) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c1 = Calendar.getInstance();
		String Today = sdf.format(c1.getTime());
		salesOrder.setModifiedTime(Today);
		salesOrder.setOrderStatus(orderStatus);

		salesOrderRepo.save(salesOrder);

		return salesOrder;
	}
}

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
public class PurchaseOrderController {
	private PurchaseOrderRepository purchaseOrderRepo;

	@Autowired
	public PurchaseOrderController(PurchaseOrderRepository purchaseOrderRepo) {
		this.purchaseOrderRepo = purchaseOrderRepo;
	}

	@RequestMapping(value = "/purchase-orders", method = RequestMethod.GET)
	public List<PurchaseOrder> getProducts(HttpServletRequest req) {
		return purchaseOrderRepo.findAll(Sort.by("id").descending());
	}

	@RequestMapping(value = "/purchase-order/{id}", method = RequestMethod.PUT)
	public PurchaseOrder modifyStatus(@PathVariable("id") long id, @RequestParam("purchaseState") String purchaseState,
			HttpServletResponse res) {

		PurchaseOrder purchaseOrder = purchaseOrderRepo.findById(id).orElse(null);

		if (purchaseOrder == null) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c1 = Calendar.getInstance();
		String Today = sdf.format(c1.getTime());
		purchaseOrder.setModifiedTime(Today);
		purchaseOrder.setPurchaseState(purchaseState);

		purchaseOrderRepo.save(purchaseOrder);

		return purchaseOrder;
	}
}

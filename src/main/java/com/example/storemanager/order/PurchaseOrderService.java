package com.example.storemanager.order;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PurchaseOrderService {
	private PurchaseOrderRepository orderRepo;

	@Autowired
	public PurchaseOrderService(PurchaseOrderRepository orderRepo) {
		this.orderRepo = orderRepo;
	}

	@RabbitListener(queues = "store.order")
	public void receiveOrder(PurchaseOrder order) {

		PurchaseOrder purchaseOrder = PurchaseOrder.builder().orderNumber(order.getOrderNumber())
				.userName(order.getUserName()).userAddress(order.getUserAddress())
				.purchaseState(order.getPurchaseState()).orderDate(order.getOrderDate()).category(order.getCategory())
				.productName(order.getProductName()).quantity(order.getQuantity()).price(order.getPrice()).build();

		System.out.println(purchaseOrder);
		orderRepo.save(purchaseOrder);

		System.out.println("----- RECEIVE LOG -----");
		System.err.println(order);
	}
}

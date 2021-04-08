package com.example.storemanager.order;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PurchaseOrderService {
	private PurchaseOrderRepository orderRepo;
	private PurchaseOrderDetailRepository detailRepo;

	@Autowired
	public PurchaseOrderService(PurchaseOrderRepository orderRepo, PurchaseOrderDetailRepository detailRepo) {
		this.orderRepo = orderRepo;
		this.detailRepo = detailRepo;
	}

	@RabbitListener(queues = "commerce.purchaseorder")
	public void receiveOrder(PurchaseOrder order) {

		PurchaseOrder purchaseOrder = PurchaseOrder.builder().amount(order.getAmount()).userName(order.getUserName())
				.userAddress(order.getUserAddress()).status("결제완료").orderDate(order.getOrderDate()).build();

		System.out.println(purchaseOrder);
		orderRepo.save(purchaseOrder);
		System.out.println("----- ORDER LOG -----");
		System.out.println(order);
	}
}

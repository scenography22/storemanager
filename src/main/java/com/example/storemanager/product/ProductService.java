package com.example.storemanager.product;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
	private RabbitTemplate rabbit;

	@Autowired
	public ProductService(RabbitTemplate rabbit) {
		this.rabbit = rabbit;
	}

	public void sendProduct(Product product) {
		System.out.println("-- PRODUCT LOG --");
		System.err.println(product);

		// exchange, topic(routing key), message
		rabbit.convertAndSend("amq.topic", "dw.product", product);
	}

	public void sendProductImage(ProductImage productImage) {
		System.out.println("-- PRODUCT IMAGE LOG --");
		System.err.println(productImage);

		rabbit.convertAndSend("product.image", productImage);
	}
}
package com.example.storemanager.order;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.storemanager.product.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(indexes = { @Index(name = "idx_sales_order_id", columnList = "salesOrderId") })
public class SalesOrderDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private Long salesOrderId;

	@ManyToOne
	@JoinColumn(name = "productId")
	private Product product;
	private long price;
	private int quantity;
}

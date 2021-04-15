package com.example.storemanager.order;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PurchaseOrder {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String orderNumber;
	private String userName;
	private String userAddress;
	private String purchaseState;

	private String category;
	private String productName;
	private long quantity;
	private long price;

	private String orderDate;
	private String modifiedTime;

	@OneToMany
	@JoinColumn(name = "purchaseOrderId")
	private List<PurchaseOrderDetail> details;

}

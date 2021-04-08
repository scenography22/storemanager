package com.example.storemanager.order;

import java.util.List;

import javax.persistence.CascadeType;
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
public class SalesOrder {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private long amount;
	private String name;
	private String address;
	private String orderDate;

	private String status;
	private long purchaseOrderId;

	@OneToMany(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "salesOrderId")
	private List<SalesOrderDetail> salesOrderDetails;
}

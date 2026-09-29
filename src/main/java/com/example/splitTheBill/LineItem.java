package com.example.splitTheBill;

import java.math.BigDecimal;

public class LineItem {
	private String name;
	private int quantity;
	private BigDecimal unitPrice;
    private String personAssignedTo;

	public LineItem() {
	}

    public LineItem(String name, int quantity, BigDecimal unitPrice) {
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

	public LineItem(String name, int quantity, BigDecimal unitPrice, String personAssignedTo) {
		this.name = name;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.personAssignedTo = personAssignedTo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getQuantity() {
		return quantity;
	}

    public String getPersonAssignedTo() {
        return personAssignedTo;
    }

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

    public void setPersonAssignedTo(String personAssignedTo) {
        this.personAssignedTo = personAssignedTo;
    }
}
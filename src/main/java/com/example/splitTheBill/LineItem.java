package com.example.splitTheBill;

import java.math.BigDecimal;

public class LineItem {
    private long id;
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
        this.id = -1; // id will be set when the LineItem is saved to the database
    }

	public LineItem(String name, int quantity, BigDecimal unitPrice, String personAssignedTo) {
		this.name = name;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.personAssignedTo = personAssignedTo;
		this.id = -1; // id will be set when the LineItem is saved to the database
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

    public Long getId() {
        return id;
    }

	public void setId(long id) {
		this.id = id;
	}
}
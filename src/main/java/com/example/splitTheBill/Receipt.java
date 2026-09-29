package com.example.splitTheBill;

import java.util.ArrayList;
import java.util.List;

public class Receipt {
    private long id;
	private List<LineItem> lineItems = new ArrayList<>();

	public Receipt() {
	}

	public Receipt(List<LineItem> lineItems) {
		this.lineItems = lineItems;
		this.id = -1; // id will be set when the Receipt is saved to the database
	}

	public List<LineItem> getLineItems() {
		return lineItems;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setLineItems(List<LineItem> lineItems) {
		this.lineItems = lineItems;
	}

    public void assignLineItemToPerson(LineItem lineItem, String person) {
        lineItem.setPersonAssignedTo(person);
    }
}
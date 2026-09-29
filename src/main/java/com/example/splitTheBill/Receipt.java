package com.example.splitTheBill;

import java.util.ArrayList;
import java.util.List;

public class Receipt {
	private List<LineItem> lineItems = new ArrayList<>();

	public Receipt() {
	}

	public Receipt(List<LineItem> lineItems) {
		this.lineItems = lineItems;
	}

	public List<LineItem> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<LineItem> lineItems) {
		this.lineItems = lineItems;
	}

    public void assignLineItemToPerson(LineItem lineItem, String person) {
        lineItem.setPersonAssignedTo(person);
    }
}
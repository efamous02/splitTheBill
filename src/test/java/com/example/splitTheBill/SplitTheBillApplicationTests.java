package com.example.splitTheBill;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SplitTheBillApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void lineItemAssignmentsAreAList() {
		LineItem item = new LineItem("Pizza", 1, new BigDecimal("12.00"));

		assertEquals(List.of(), item.getPersonAssignedTo());

		item.setPersonAssignedTo(List.of("Alice", "Bob", "Alice", " "));

		assertEquals(List.of("Alice", "Bob"), item.getPersonAssignedTo());
	}

	@Test
	void controllerAcceptsMultipleAssigneesAndClearsThem() {
		ReceiptController controller = new ReceiptController();
		LineItem item = controller.addItem(1, new LineItem("Pizza", 1, new BigDecimal("12.00"))).getBody();

		LineItem assigned = controller.assignItem(1, item.getId(),
				new ReceiptController.AssignmentRequest(List.of("Alice", "Bob")));
		assertEquals(List.of("Alice", "Bob"), assigned.getPersonAssignedTo());

		controller.removeAssignment(1, item.getId());
		assertEquals(List.of(), assigned.getPersonAssignedTo());
	}

	@Test
	void controllerListsAllReceiptsInIdOrder() {
		ReceiptController controller = new ReceiptController();
		controller.addItem(2, new LineItem("Cake", 1, new BigDecimal("8.00")));
		controller.addItem(1, new LineItem("Pizza", 1, new BigDecimal("12.00")));

		assertEquals(List.of(1L, 2L), controller.getReceipts().stream().map(Receipt::getId).toList());
	}
}

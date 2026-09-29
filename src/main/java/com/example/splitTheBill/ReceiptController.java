package com.example.splitTheBill;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {
	private final Map<Long, Receipt> receipts = new ConcurrentHashMap<>();
	private long nextItemId = 1;

	@GetMapping("/{receiptId}")
	public synchronized Receipt getReceipt(@PathVariable long receiptId) {
		return findReceipt(receiptId);
	}

	@PostMapping("/{receiptId}/items")
	public synchronized ResponseEntity<LineItem> addItem(
			@PathVariable long receiptId,
			@RequestBody LineItem itemRequest) {
		Receipt receipt = receipts.computeIfAbsent(receiptId, id -> {
			Receipt newReceipt = new Receipt();
			newReceipt.setId(id);
			return newReceipt;
		});

		LineItem item = new LineItem(itemRequest.getName(), itemRequest.getQuantity(), itemRequest.getUnitPrice());
		item.setId(nextItemId++);
		receipt.getLineItems().add(item);
		return ResponseEntity.status(HttpStatus.CREATED).body(item);
	}

	@PatchMapping("/{receiptId}/items/{itemId}")
	public synchronized LineItem updateItem(
			@PathVariable long receiptId,
			@PathVariable long itemId,
			@RequestBody LineItemPatch patch) {
		LineItem item = findItem(receiptId, itemId);
		if (patch.name() != null) {
			item.setName(patch.name());
		}
		if (patch.quantity() != null) {
			item.setQuantity(patch.quantity());
		}
		if (patch.unitPrice() != null) {
			item.setUnitPrice(patch.unitPrice());
		}
		return item;
	}

	@DeleteMapping("/{receiptId}/items/{itemId}")
	public synchronized ResponseEntity<Void> deleteItem(
			@PathVariable long receiptId,
			@PathVariable long itemId) {
		Receipt receipt = findReceipt(receiptId);
		LineItem item = findItem(receipt, itemId);
		receipt.getLineItems().remove(item);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{receiptId}/items/{itemId}/assignment")
	public synchronized LineItem assignItem(
			@PathVariable long receiptId,
			@PathVariable long itemId,
			@RequestBody AssignmentRequest assignment) {
		LineItem item = findItem(receiptId, itemId);
		item.setPersonAssignedTo(assignment.personAssignedTo());
		return item;
	}

	@DeleteMapping("/{receiptId}/items/{itemId}/assignment")
	public synchronized ResponseEntity<Void> removeAssignment(
			@PathVariable long receiptId,
			@PathVariable long itemId) {
		LineItem item = findItem(receiptId, itemId);
		item.setPersonAssignedTo(null);
		return ResponseEntity.noContent().build();
	}

	private Receipt findReceipt(long receiptId) {
		Receipt receipt = receipts.get(receiptId);
		if (receipt == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Receipt not found");
		}
		return receipt;
	}

	private LineItem findItem(long receiptId, long itemId) {
		return findItem(findReceipt(receiptId), itemId);
	}

	private LineItem findItem(Receipt receipt, long itemId) {
		return receipt.getLineItems().stream()
				.filter(item -> item.getId() == itemId)
				.findFirst()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Line item not found"));
	}

	public record LineItemPatch(String name, Integer quantity, BigDecimal unitPrice) {
	}

	public record AssignmentRequest(String personAssignedTo) {
	}
}
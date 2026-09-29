# Split the Bill API

A small Spring Boot API for splitting a bill among friends.

The application stores receipts and line items in memory. A receipt contains one or more line items, and each line item can be assigned to a person.

## Features

- Retrieve a receipt
- Add line items
- Update line item details
- Remove line items
- Assign a line item to a person
- Remove an assignment

## Running the application

Start the Spring Boot application:

```bash
./mvnw spring-boot:run

```markdown
# Split the Bill API

A simple API for creating receipts, adding items, and assigning items to friends.

The application runs at:

```text
http://localhost:8080
```

## Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| `GET` | `/api/receipts/{receiptId}` | View a receipt |
| `POST` | `/api/receipts/{receiptId}/items` | Add an item |
| `PATCH` | `/api/receipts/{receiptId}/items/{itemId}` | Update an item |
| `DELETE` | `/api/receipts/{receiptId}/items/{itemId}` | Delete an item |
| `PUT` | `/api/receipts/{receiptId}/items/{itemId}/assignment` | Assign an item |
| `DELETE` | `/api/receipts/{receiptId}/items/{itemId}/assignment` | Remove an assignment |

## Add an item

```bash
curl -X POST http://localhost:8080/api/receipts/1/items \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Pizza",
    "quantity": 1,
    "unitPrice": 18.99
  }'
```

A receipt is created automatically if it does not already exist.

## View a receipt

```bash
curl http://localhost:8080/api/receipts/1
```

Example response:

```json
{
  "id": 1,
  "lineItems": [
    {
      "id": 1,
      "name": "Pizza",
      "quantity": 1,
      "unitPrice": 18.99,
      "personAssignedTo": null
    }
  ]
}
```

## Update an item

```bash
curl -X PATCH http://localhost:8080/api/receipts/1/items/1 \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 2
  }'
```

## Assign an item

```bash
curl -X PUT http://localhost:8080/api/receipts/1/items/1/assignment \
  -H "Content-Type: application/json" \
  -d '{
    "personAssignedTo": "Alice"
  }'
```

## Remove an assignment

```bash
curl -X DELETE \
  http://localhost:8080/api/receipts/1/items/1/assignment
```

## Delete an item

```bash
curl -X DELETE \
  http://localhost:8080/api/receipts/1/items/1
```

Data is currently stored in memory, so it will be lost when the application restarts.
```
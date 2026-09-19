# Các Quy Ước API — spingddd

## 1. Quy Ước Endpoint REST

### Đặt Hàng (Create Order)
- **Phương thức:** `POST`
- **URL:** `/order/createOrder`
- **Headers:** `Content-Type: application/json`
- **Request Body (`OrderRequest`):**
```json
{
  "productId": 1,
  "quantity": 2
}
```
*(Lưu ý hiện tại `userId` đang được mock tạm thời là `1L` trong `OrderController`)*

- **Response thành công:**
  - **Mã trạng thái:** `200 OK`
  - **Body (String):** `"Order created successfully"`

---

## 2. Quy Chuẩn Xử Lý Lỗi (Error Handling)

Tất cả các ngoại lệ nghiệp vụ được xử lý tập trung tại `GlobalExceptionHandler` (`@RestControllerAdvice`):

| Ngoại lệ | Mã HTTP | Định dạng phản hồi | Trường hợp xảy ra |
|---|---|---|---|
| `BadRequestException` | `400 BAD REQUEST` | Tin nhắn văn bản (String) | Hết hàng tồn kho, số lượng <= 0 |
| `NotfoundException` | `404 NOT FOUND` | Tin nhắn văn bản (String) | Không tìm thấy Product hoặc Inventory |
| Ngoại lệ Bất xử lý | `500 INTERNAL SERVER ERROR` | Mặc định Spring Boot | Lỗi hệ thống, crash DB, timeout Kafka |

---

## 3. Công Cụ Test API

- Dự án tích hợp **Bruno Collection** tại thư mục:
  `spring4ddd-bruno/`
- Tệp request mẫu: `spring4ddd-bruno/create-order.yml`
- Khi chỉnh sửa hoặc bổ sung endpoint REST, bắt buộc cập nhật tương ứng vào Bruno collection này.

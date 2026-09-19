# Tài Liệu Kiến Trúc — spingddd

## 1. Tổng Quan Kiến Trúc DDD (Domain-Driven Design)

Dự án áp dụng mô hình kiến trúc Layered Architecture chuẩn DDD gồm 4 lớp chính:

```
[ Interfaces Layer ]       --> REST Controller, Request/Response DTOs (OV)
        │
        ▼
[ Application Layer ]      --> Commands, Application Services, Cronjobs, Exceptions
        │
        ▼
[ Domain Layer ]           --> Models (Aggregates), Domain Services, Repository Ports (Interfaces)
        ▲
        │ (implements)
[ Infrastructure Layer ]   --> Database Adapters (DO, JPA Repositories), Redis, Kafka Producers/Consumers
```

### Quy Tắc Phụ Thuộc (Dependency Rule)
- `interfaces` -> `application` -> `domain`
- `infrastructure` -> `domain` (implement các Port interfaces của Domain)
- **Domain Layer là trung tâm thuần túy:** KHÔNG import bất kỳ framework nào như Spring, JPA, Redis, Kafka.

---

## 2. Các Pattern Cốt Lõi

### A. Redis Lua Trừ Tồn Kho (Giảm tồn kho hiệu năng cao)
1. Khi có request đặt hàng, `OrderService` thực thi script Lua `deduct_inventory.lua` trên Redis.
2. Trả về kết quả nhanh chóng:
   - `1`: Trừ tồn kho cache thành công.
   - `0`: Hết hàng -> Ném `BadRequestException`.
   - `-1`: Cache miss -> Nạp lại số lượng từ DB PostgreSQL lên Redis rồi thử lại.

### B. Transactional Outbox Pattern (Nhất quán cuối cùng)
1. Trong `OrderService.createOrder()` (Transaction DB):
   - Tạo và lưu `Order` vào bảng `orders` (status = 0: PENDING).
   - Tạo bản ghi `OutboxEvent` (payload chứa `PlaceOrderMQMessage`) và lưu vào bảng `outbox_event`.
   - Cả 2 thao tác được commit atomic trong cùng một DB Transaction.
2. `OutboxPublisherJob` chạy định kỳ (`@Scheduled` 1 giây):
   - Lấy các sự kiện `status = 0` (PENDING).
   - Gửi sự kiện lên Kafka qua `KafkaOrderProducer.sendAndAwaitAck()` (chờ ACK 5 giây).
   - Đánh dấu `status = 1` (PUBLISHED).

### C. Idempotent Consumer
1. `OrderConsummer` lắng nghe Kafka topic `order-place-topic`.
2. Kiểm tra tính trùng lặp (Idempotency):
   - Tìm kiếm đơn hàng theo `token`. Nếu `order == null` hoặc `status == 1` (COMPLETED) -> Bỏ qua.
3. Trừ tồn kho chính thức trong PostgreSQL (`inventoryRepository.updateAvalibleQuality`).
4. Cập nhật trạng thái đơn hàng `status = 1` (COMPLETED).

---

## 3. Vai Trò Các Gói Mã Nguồn

| Package | Chức năng |
|---|---|
| `com.ntd.spingddd.interfaces.controller` | Đón nhận REST request (`/order/createOrder`). |
| `com.ntd.spingddd.interfaces.ov` | DTOs (Request / Response). |
| `com.ntd.spingddd.application.command` | Command objects (`record` + `@Builder`). |
| `com.ntd.spingddd.application.service` | Orchestration logic, Transaction management. |
| `com.ntd.spingddd.application.cronjob` | Job quét và đẩy Outbox event lên Kafka. |
| `com.ntd.spingddd.domain.model` | Domain Entities (`Order`, `Product`, `Inventory`, `OutboxEvent`). |
| `com.ntd.spingddd.domain.repository` | Port Interfaces cho Repository. |
| `com.ntd.spingddd.infrastructure.jpa` | Spring Data JPA Repositories (`OrderJpa`, v.v.). |
| `com.ntd.spingddd.infrastructure.repository` | JPA Entities (`DO`), MapStruct `Converter`, `RepositoryImpl`. |
| `com.ntd.spingddd.infrastructure.mq` | Kafka Producer & Message Payload structure. |

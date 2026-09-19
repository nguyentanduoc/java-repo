# Quy Tắc Dự Án Cốt Lõi (Trung Tâm) — spingddd

> Quy tắc cốt lõi. Mọi AI Agent phải tuân thủ.

## 1. Ngăn Xếp Công Nghệ (KHÔNG tự ý thay đổi)
- Java 25 | Spring Boot 4.1.1 | Gradle
- PostgreSQL 18.6 (cổng 15432) | Redis 8.8.1 (16379, mật khẩu 123456a) | Kafka 3.7.0 KRaft (19094)
- Độ bao phủ: JaCoCo 0.8.14 — 100% chỉ thị (tối thiểu = 1.0)
- **Điều kiện tiên quyết:** 
  - Trước khi hoàn thành bất kỳ task nào, source code PHẢI biên dịch thành công (không có lỗi compile) và vượt qua tất cả các bài kiểm tra.
  - Sau khi chạy Unit Test, agent PHẢI xuất và báo cáo độ bao phủ của JaCoCo để đảm bảo đạt 100% chỉ thị (instructions/branches/lines).

## 2. Quy Tắc Mã Nguồn & Đặt Tên
- Quy tắc phụ thuộc: interfaces -> application -> domain <- infrastructure.
- DI: @RequiredArgsConstructor + trường final; KHÔNG dùng @Autowired.
- Bảng Orders: "orders". Token: MQ- + UUID 16 ký tự, không có dấu gạch ngang.
- Ghi nhật ký: @Slf4j, không bao giờ dùng System.out. Ngoại lệ: NotfoundException (404), BadRequestException (400).

## 3. Cấu Trúc Thư Mục (DDD)
src/main/java/com/ntd/spingddd/
  interfaces/      # Controllers, DTOs
  application/     # Commands, Services, Cronjobs, Exceptions
  domain/          # Models, Cổng Repository
  infrastructure/  # Config, JPA, MQ, DOs, Converters
src/main/resources/
  application.yml, db/01-init-product.sql (DDL), scripts/deduct_inventory.lua (trả về 1/0/-1)
src/test/java/.../e2e/ (AbstractE2ETest, OrderCreateE2ETest, OrderConsumerIdempotencyTest)
src/test/resources/application-test.yml (create-drop, kafka giả)
- Agent PHẢI cập nhật `MEMORIES.md` với định dạng `[YYYY-MM-DD] [Agent] - [Vấn đề] -> [Cách giải quyết]` mỗi khi gặp lỗi hệ thống (ví dụ: PowerShell, build, test, database) để rút kinh nghiệm cho lần sau.


## 4. Tài Liệu (Tầng 2 — docs/)
| Agent | Cách đọc |
|---|---|
| Claude Code | Đọc toàn bộ docs/ trước nhiệm vụ phức tạp. |
| Cline | LUÔN đọc docs/architecture.md & docs/database.md. |
| Cursor | Chỉ đọc tệp tài liệu liên quan. |
| Copilot | Chỉ tải 1 tệp tài liệu liên quan đến ngữ cảnh. |
Chỉ thị chung: nếu nhiệm vụ liên quan đến CÔNG CỤ, agent PHẢI đọc docs/skills/[Tên_Công_Cụ].md.

## 5. Kỹ Năng Theo Yêu Cầu (Tầng 3 — docs/skills/)
Hiện có 2 tệp thực sự: Docker.md (4 dịch vụ: Postgres, Redis, Redis-Commander, Kafka), Testing.md (chiến lược E2E).

## 6. Bộ Nhớ Chung Bền Vững (Tầng 4 — MEMORIES.md)
Khi sửa lỗi phức tạp hoặc quyết định kiến trúc mới, HÃY THÊM dòng này vào MEMORIES.md:
[Ngày] [Agent] - [Vấn đề] -> [Cách giải quyết].
---
*Cập nhật: 2026-09-19*


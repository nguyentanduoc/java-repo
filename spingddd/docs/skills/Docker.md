# Skill: Docker

> Quy trình chuẩn cho việc làm việc với Docker trong dự án `spingddd`.

## 1. Cấu hình & Dịch vụ

- **Tệp cấu hình:** `docker-compose.yml` ở thư mục gốc.
- **Mạng nội bộ:** `app-network` (driver bridge).
- **Các dịch vụ (theo `docker-compose.yml`):**
    - **`postgres`** (`image: postgres:18.6-trixie`) — Port host `15432` -> Container `5432`. User/password: `postgres`/`postgres`.
    - **`redis`** (`image: redis:8.8.1-trixie`) — Port host `16379` -> Container `6379`. Password: `123456a`.
    - **`redis-commander`** — Port host `18081`. (Dành cho quản lý Redis trên Web).
    - **`kafka`** (`image: apache/kafka:3.7.0`) — KRaft mode. Port host `19094` (EXTERNAL cho Spring Boot host) và `9092` (INTERNAL cho container khác). Healthcheck bằng `kafka-topics.sh`.

## 2. Lệnh Thực hành

| Tác vụ | Lệnh |
|---|---|
| Bắt đầu toàn bộ dịch vụ | `docker-compose up -d` |
| Dừng & xóa | `docker-compose down` |
| Xem logs một dịch vụ | `docker-compose logs -f postgres` / `docker-compose logs -f kafka` |
| Vào trong PostgreSQL | `docker exec -it postgres_container psql -U postgres -d postgres` |

## 3. Quy tắc Khi Thay đổi Docker
- **KHÔNG thay đổi cổng host** (`15432`, `16379`, `19094`) nếu chưa được yêu cầu, bởi vì `application.yml` và test đều cấu hình sẵn các port này.
- **KHÔNG commit dữ liệu** trong thư mục `docker/volumes/` (đã được bỏ qua trong `.gitignore` qua quy tắc `docker/volumes/postgres/`).
- Nếu cần thêm volume mới, nhớ cập nhật `.gitignore` tương ứng.

## 4. Mối quan hệ với Cấu hình ứng dụng
- `application.yml` (dev) kết nối tới địa chỉ này.
- `application-test.yml` dùng địa chỉ localhost:5432 cho Testcontainers, không phụ thuộc trực tiếp vào Docker Compose này.

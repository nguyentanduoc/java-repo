# Hướng Dẫn Coding Rules (spingddd)

## 1. Nguyên Tắc Tổng Quan
- **Không có cảnh báo (Warning-free):** Mọi code phải sạch sẽ, không có warning từ trình biên dịch hoặc IDE.
- **Imports:** Tất cả các lớp phải được import ở phần trên của file. KHÔNG được sử dụng tên lớp đầy đủ (fully qualified name) trong mã nguồn trừ trường hợp xung đột tên (ambiguity).
- **Format:** Sử dụng Tab cho thụt đầu dòng (indentation) như các tệp hiện tại.

## 2. Quy Tắc Viết Code
- **Dependency Injection:** Dùng `@RequiredArgsConstructor` với các trường `private final`. Tuyệt đối không dùng `@Autowired`.
- **Logging:** Luôn sử dụng `@Slf4j` và `log.info/debug/error()`. KHÔNG dùng `System.out` hoặc `System.err`.
- **Ngoại lệ:** Sử dụng `NotfoundException` cho 404 và `BadRequestException` cho 400.
- **Cấu trúc lớp:**
  - `interfaces` -> `application` -> `domain` <- `infrastructure`.
- **Tên bảng/Trường:** Bảng `orders`. Token MQ: `MQ-` + UUID (16 ký tự, không gạch ngang).

## 3. Review Code
- Mọi thay đổi phải đảm bảo biên dịch thành công và passing tests.
- JaCoCo coverage phải duy trì 100%.

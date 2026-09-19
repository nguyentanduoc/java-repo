# Bộ Nhớ Chung Bền Vững

_Các ghi chú được các agent thêm vào đây theo định dạng_:

```
[YYYY-MM-DD] [Agent] - [Vấn đề] -> [Cách giải quyết].
```

---

_Cập nhật lần cuối: 2026-09-19_

## Lịch sử ghi nhớ

[2026-09-19] [Cline] - Cấu hình VS Code mặc định PowerShell nhưng Cline vẫn gửi lệnh theo bash syntax (||, /dev/null) gây lỗi PowerShell parser -> Thêm `"terminal.integrated.defaultProfile.windows": "PowerShell"` và `"cline.terminal.shell.windows": "powershell.exe"` (kèm `"-NoProfile" "-ExecutionPolicy" "Bypass"`) vào `settings.json`; luôn viết lại câu lệnh theo PowerShell: dùng `;` hoặc `if (-not $?)` thay vì `||`, `$null` thay vì `/dev/null`, `$env:VAR` thay vì `$VAR`.

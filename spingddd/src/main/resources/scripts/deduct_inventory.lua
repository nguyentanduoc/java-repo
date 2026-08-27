local inventory_key = KEYS[1]
local deduct_quantity = tonumber(ARGV[1])

-- Kiểm tra xem key có tồn tại trong Redis không
if redis.call('EXISTS', inventory_key) == 0 then
    return -1 -- Cache miss
end

local current_quantity = tonumber(redis.call('GET', inventory_key))

-- Kiểm tra xem tồn kho có đủ để trừ không
if current_quantity >= deduct_quantity then
    redis.call('DECRBY', inventory_key, deduct_quantity)
    return 1 -- Thành công
else
    return 0 -- Hết hàng hoặc không đủ
end
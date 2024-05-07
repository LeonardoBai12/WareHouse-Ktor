SELECT
    user_id,
    ware_id,
    quantity,
    timestamp
FROM withdraw
WHERE uuid = ?;
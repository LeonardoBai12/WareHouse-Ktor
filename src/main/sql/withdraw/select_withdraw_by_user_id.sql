SELECT
    uuid,
    ware_id,
    quantity,
    timestamp
FROM withdraw
WHERE user_id = ?;
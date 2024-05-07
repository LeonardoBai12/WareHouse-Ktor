SELECT
    uuid,
    user_id,
    quantity,
    timestamp
FROM withdraw
WHERE ware_id = ?;
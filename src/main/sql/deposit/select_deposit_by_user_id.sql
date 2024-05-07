SELECT
    uuid,
    ware_id,
    quantity,
    timestamp
FROM deposit
WHERE user_id = ?;
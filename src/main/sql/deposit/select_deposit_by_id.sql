SELECT
    user_id,
    ware_id,
    quantity,
    timestamp
FROM deposit
WHERE uuid = ?;
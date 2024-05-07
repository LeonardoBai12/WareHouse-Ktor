SELECT
    uuid,
    user_id,
    quantity,
    timestamp
FROM deposit
WHERE ware_id = ?;
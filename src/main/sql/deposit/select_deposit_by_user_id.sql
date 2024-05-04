SELECT
    uuid,
    ware_id,
    quantity
FROM deposit
WHERE user_id = ?;
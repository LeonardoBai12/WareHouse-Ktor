SELECT
    uuid,
    user_id,
    quantity
FROM deposit
WHERE ware_id = ?;
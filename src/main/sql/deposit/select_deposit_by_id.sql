SELECT
    user_id,
    ware_id,
    quantity
FROM deposit
WHERE uuid = ?;
SELECT
    uuid,
    ware_id,
    quantity
FROM withdraw
WHERE user_id = ?;
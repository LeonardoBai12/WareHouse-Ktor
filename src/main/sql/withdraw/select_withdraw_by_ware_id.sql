SELECT
    uuid,
    user_id,
    quantity
FROM withdraw
WHERE ware_id = ?;
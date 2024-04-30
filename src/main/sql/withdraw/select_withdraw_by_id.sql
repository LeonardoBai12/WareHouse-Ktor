SELECT
    user_id,
    ware_id,
    quantity,
FROM withdraw
WHERE uuid = ?;
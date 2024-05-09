SELECT
    uuid,
    user_id,
    ware_id,
    quantity,
    timestamp
FROM withdraw
WHERE (user_id = COALESCE(CAST(? AS UUID), user_id))
AND (ware_id = COALESCE(CAST(? AS UUID), ware_id));
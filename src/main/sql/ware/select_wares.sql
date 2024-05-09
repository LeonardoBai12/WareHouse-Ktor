SELECT
    uuid,
    user_id,
    name,
    brand,
    description,
    weight_per_unit,
    weight_unit,
    available_quantity,
    quantity_unit,
    ware_location,
    timestamp
FROM ware
WHERE (LOWER(name) LIKE CONCAT('%', LOWER(COALESCE(?, name)), '%'))
AND (LOWER(brand) LIKE CONCAT('%', LOWER(COALESCE(?, brand)), '%'))
AND (user_id = COALESCE(CAST(? AS UUID), user_id));
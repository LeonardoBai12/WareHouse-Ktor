SELECT
    uuid,
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
WHERE user_id = ?;
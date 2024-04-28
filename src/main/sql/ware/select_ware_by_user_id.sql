SELECT
    user_id,
    name,
    description,
    weight_per_unit,
    weight_unit,
    total_quantity,
    available_quantity,
    quantity_unit,
    ware_location
FROM ware
WHERE user_id = ?;
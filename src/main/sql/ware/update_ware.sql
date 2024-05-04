UPDATE ware SET
    name = ?,
    brand = ?,
    description = ?,
    weight_per_unit = ?,
    weight_unit = ?,
    quantity_unit = ?,
    ware_location = ?
WHERE uuid = ?;
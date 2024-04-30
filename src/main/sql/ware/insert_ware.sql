INSERT INTO ware (
    uuid,
    user_id,
    name,
    description,
    weight_per_unit,
    weight_unit,
    total_quantity,
    available_quantity,
    quantity_unit,
    ware_location
) VALUES (
    ?, -- uuid
    ?, -- user_id
    ?, -- name
    ?, -- description
    ?, -- weight_per_unit
    ?, -- weight_unit
    ?, -- total_quantity
    ?, -- available_quantity
    ?, -- quantity_unit
    ? -- ware_location
);
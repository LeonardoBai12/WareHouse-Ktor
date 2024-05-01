INSERT INTO ware (
    uuid,
    user_id,
    name,
    brand,
    description,
    weight_per_unit,
    weight_unit,
    available_quantity,
    quantity_unit,
    ware_location
) VALUES (
    ?, -- uuid
    ?, -- user_id
    ?, -- name
    ?, -- brand
    ?, -- description
    ?, -- weight_per_unit
    ?, -- weight_unit
    ?, -- available_quantity
    ?, -- quantity_unit
    ? -- ware_location
);
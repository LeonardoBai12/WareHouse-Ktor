CREATE OR REPLACE FUNCTION update_available_quantity_on_deposit()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE ware
    SET available_quantity = available_quantity + NEW.quantity
    WHERE uuid = NEW.ware_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
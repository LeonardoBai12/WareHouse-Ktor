CREATE OR REPLACE FUNCTION update_available_quantity_on_withdraw()
RETURNS TRIGGER AS $$
DECLARE
    current_available_quantity INTEGER;
BEGIN
    SELECT available_quantity INTO current_available_quantity FROM ware WHERE uuid = NEW.ware_id;

    IF NEW.quantity > current_available_quantity THEN
        RAISE EXCEPTION 'Withdrawal quantity (%) exceeds available quantity (%)', NEW.quantity, current_available_quantity;
    END IF;

    UPDATE ware
    SET available_quantity = available_quantity - NEW.quantity
    WHERE uuid = NEW.ware_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
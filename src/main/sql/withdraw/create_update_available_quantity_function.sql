CREATE OR REPLACE FUNCTION update_available_quantity()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.quantity > (SELECT available_quantity FROM ware WHERE uuid = NEW.ware_id) THEN
        RAISE EXCEPTION 'Withdrawal quantity exceeds available quantity';
    END IF;

    UPDATE ware
    SET available_quantity = available_quantity - NEW.quantity
    WHERE uuid = NEW.ware_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
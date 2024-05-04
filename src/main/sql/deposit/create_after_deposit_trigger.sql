CREATE OR REPLACE TRIGGER after_deposit_trigger
AFTER INSERT ON deposit
FOR EACH ROW
EXECUTE FUNCTION update_available_quantity_on_deposit();
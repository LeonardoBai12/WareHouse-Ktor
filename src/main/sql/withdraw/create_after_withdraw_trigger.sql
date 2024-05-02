CREATE OR REPLACE TRIGGER after_withdraw_trigger
AFTER INSERT ON withdraw
FOR EACH ROW
EXECUTE FUNCTION update_available_quantity_on_withdraw();
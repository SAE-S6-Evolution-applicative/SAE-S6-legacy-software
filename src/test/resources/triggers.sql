CREATE TRIGGER prevent_bill_deletion
    BEFORE DELETE ON bills
    FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'A bill cannot be deleted';
END;
@@

CREATE TRIGGER prevent_bill_update
    BEFORE UPDATE ON bills
    FOR EACH ROW
BEGIN
    IF NEW.total_amount != OLD.total_amount THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'total_amount cannot be updated';
    END IF;
END;
@@

CREATE TRIGGER prevent_bill_detail_deletion
    BEFORE DELETE ON bill_details
    FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'A bill detail cannot be deleted';
END;
@@

CREATE TRIGGER prevent_bill_details_update
    BEFORE UPDATE ON bill_details
    FOR EACH ROW
BEGIN
    IF NEW.bill_id != OLD.bill_id THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'bill_id cannot be updated';
    END IF;

    IF NEW.quantity != OLD.quantity THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'quantity cannot be updated';
    END IF;

    IF NEW.unit_price != OLD.unit_price THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'unit_price cannot be updated';
    END IF;

    IF NEW.line_total != OLD.line_total THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'line_total cannot be updated';
    END IF;
END;
@@
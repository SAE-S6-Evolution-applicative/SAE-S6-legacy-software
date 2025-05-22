-- Début de la transaction
START TRANSACTION;

-- Insertion des médecins avec IDs explicites
INSERT INTO doctors (id, doctor_number, first_name, last_name, specialization, phone_number, email, department) VALUES
(1, 'DR001', 'Pierre', 'Dubois', 'Cardiologie', '0123456789', 'pierre.dubois@hopital.fr', 'Cardiologie'),
(2, 'DR002', 'Marie', 'Laurent', 'Pédiatrie', '0123456790', 'marie.laurent@hopital.fr', 'Pédiatrie'),
(3, 'DR003', 'Jean', 'Martin', 'Neurologie', '0123456791', 'jean.martin@hopital.fr', 'Neurologie');

-- Insertion des patients avec IDs explicites
INSERT INTO patients (id, patient_number, first_name, last_name, date_of_birth, gender, address, phone_number) VALUES
(1, 'PT001', 'Sophie', 'Moreau', '1985-06-15', 'F', '12 rue des Lilas, Paris', '0687654321'),
(2, 'PT002', 'Lucas', 'Bernard', '1990-03-22', 'M', '45 avenue Victor Hugo, Lyon', '0687654322'),
(3, 'PT003', 'Emma', 'Petit', '1978-11-30', 'F', '8 rue de la Paix, Marseille', '0687654323');

-- Insérer d'abord les données patient_history avec IDs explicites
INSERT INTO patient_history (id, patient_id, visit_date, diagnosis, symptoms, notes) VALUES
(1, 1, '2024-02-15', 'Hypertension artérielle', 'Maux de tête, vertiges', 'Patient à surveiller régulièrement'),
(2, 2, '2024-02-15', 'Rhinopharyngite', 'Fièvre, congestion nasale', 'Évolution favorable'),
(3, 3, '2024-02-15', 'Migraine chronique', 'Céphalées intenses', 'Traitement préventif mis en place');

-- Insertion des rendez-vous
INSERT INTO appointments (appointment_number, patient_id, doctor_id, appointment_date, status, description, room_number) VALUES
('RDV001', 1, 1, '2024-02-15 09:00:00', 'CONFIRMÉ', 'Consultation cardiologique', 'A101'),
('RDV002', 2, 2, '2024-02-15 10:30:00', 'CONFIRMÉ', 'Suivi pédiatrique', 'B202'),
('RDV003', 3, 3, '2024-02-15 14:00:00', 'CONFIRMÉ', 'Consultation neurologique', 'C303');

-- Insertion des dossiers médicaux
INSERT INTO medical_records (record_number, patient_id, diagnosis, treatment, prescription, notes, record_date, doctor_id, blood_pressure, temperature, weight) VALUES
('DM001', 1, 'Hypertension artérielle', 'Traitement médicamenteux', 'Bétabloquants', 'Patient à surveiller', '2024-02-15', 1, '140/90', 37.2, 75.5),
('DM002', 2, 'Rhinopharyngite', 'Traitement symptomatique', 'Paracétamol', 'Repos conseillé', '2024-02-15', 2, '110/70', 38.5, 25.0),
('DM003', 3, 'Migraine chronique', 'Traitement préventif', 'Antimigraineux', 'Suivi régulier nécessaire', '2024-02-15', 3, '120/80', 36.8, 65.0);

-- Ensuite seulement, insérer les lab_results
INSERT INTO lab_results (patient_history_id, test_name, result_value, test_date, notes) VALUES
(1, 'Numération formule sanguine', 'Normal', '2024-02-15', 'Pas d''anomalie détectée'),
(2, 'Test COVID-19', 'Négatif', '2024-02-15', 'Test PCR'),
(3, 'Bilan lipidique', 'Cholestérol légèrement élevé', '2024-02-15', 'Régime alimentaire conseillé');

-- Insertion des traitements
INSERT INTO treatments (name, patient_history_id, treatment_date, notes) VALUES
('Traitement antihypertenseur', 1, '2024-02-15', 'Suivi mensuel'),
('Antibiothérapie', 2, '2024-02-15', 'Traitement sur 7 jours'),
('Traitement antimigraineux', 3, '2024-02-15', 'Traitement préventif quotidien');

-- Insertion des ordonnances
INSERT INTO prescriptions (prescription_number, patient_id, notes, total_cost, is_billed, inventory_updated) VALUES
('ORD001', 1,  'Prendre matin et soir', 45.60, true, true),
('ORD002', 2,  'Prendre si fièvre', 22.30, true, true),
('ORD003', 3,  'Prendre dès les premiers symptômes', 35.80, true, true);

-- Insertion des factures
INSERT INTO bills (id, bill_number, patient_id, doctor_id, bill_date, total_amount, status) VALUES
(1, 'FAC001', 1, 1, '2024-02-15', 150.00, 'PAID'),
(2, 'FAC002', 2, 2, '2024-02-15', 80.00, 'PENDING'),
(3, 'FAC003', 3, 3, '2024-02-15', 120.00, 'PAID');

-- Insertion des actions medicals
INSERT INTO medical_act (id, name, price, active) VALUES
(1, 'CONSULTATION', 50.0, true),
(2, 'XRAY', 150.0, true),
(3, 'CHIRURGIE', 1000.0, false);

-- Insertion des détails des factures avec référence aux actes médicaux
INSERT INTO bill_details (bill_id, medical_act, quantity, line_total) VALUES
(1, 1, 1, 50.00),  -- Consultation (acte médical ID 1) pour la première facture
(2, 1, 1, 50.00),  -- Consultation (acte médical ID 1) pour la deuxième facture
(3, 1, 1, 50.00);  -- Consultation (acte médical ID 1) pour la troisième facture

-- Insertion des assurances
INSERT INTO insurance (policy_number, patient_id, provider, coverage_percentage, max_coverage, expiry_date) VALUES
('POL001', 1, 'Assurance Santé Plus', 80.00, 2000.00, '2025-12-31'),
('POL002', 2, 'MutuelleSanté', 90.00, 3000.00, '2025-12-31'),
('POL003', 3, 'AssurMed', 75.00, 1500.00, '2025-12-31');

-- Insert medicines first
INSERT INTO medicine (id, name, unit_price) VALUES
(1, 'Paracetamol 500mg', 0.15),
(2, 'Amoxicillin 1g', 0.45),
(3, 'Beta-blocker 50mg', 0.30),
(4, 'Syringes 10ml', 0.25),
(5, 'Medical gloves M', 0.10),
(6, 'Hand sanitizer', 3.50),
(7, 'Sterile compresses', 0.05),
(8, 'Hypoallergenic tape', 2.20),
(9, 'Sumatriptan 50mg', 1.20),
(10, 'Surgical masks', 5.00);

-- Then insert inventory items that reference the medicines
INSERT INTO inventory (id, medicine_id, quantity, reorder_level, last_restocked) VALUES
(1,1, 1000, 200, CURRENT_DATE()), -- Paracetamol
(2,2, 500, 100, CURRENT_DATE()),  -- Amoxicillin
(3,3, 300, 100, CURRENT_DATE()),  -- Beta-blocker
(4,4, 2000, 500, CURRENT_DATE()), -- Syringes
(5,5, 5000, 1000, CURRENT_DATE()), -- Medical gloves
(6,6, 200, 50, CURRENT_DATE()),   -- Hand sanitizer
(7,7, 3000, 500, CURRENT_DATE()), -- Sterile compresses
(8,8, 150, 30, CURRENT_DATE()),   -- Hypoallergenic tape
(9,9, 200, 50, CURRENT_DATE()),   -- Sumatriptan
(10,10, 1000, 200, CURRENT_DATE()); -- Surgical masks

-- Ensuite les factures fournisseurs
INSERT INTO supplier_invoices (invoice_number, supplier_name, invoice_date, total_amount) VALUES
('FOUR001', 'Pharmacie Centrale', '2024-02-15', 1200.00),
('FOUR002', 'MedEquip', '2024-02-15', 2500.00),
('FOUR003', 'LabSupplies', '2024-02-15', 800.00);

-- Enfin les détails des factures fournisseurs
INSERT INTO supplier_invoice_details (invoice_id, inventory_id, quantity, unit_price) VALUES
(1, 1, 100, 12.00),
(2, 2, 5, 500.00),
(3, 3, 200, 4.00);

-- Insertion de l'historique des prix
INSERT INTO price_history (inventory_id, old_price, new_price, change_date) VALUES
(1, 10.00, 12.00, '2024-01-01'),
(2, 450.00, 500.00, '2024-01-15'),
(3, 3.50, 4.00, '2024-02-01');

-- Commit de la transaction si tout s'est bien passé
COMMIT;
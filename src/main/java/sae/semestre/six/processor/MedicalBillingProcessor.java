/*
 * MedicalBillingProcessor.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicalBillingProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MedicalBillingProcessor.class);

    private static volatile MedicalBillingProcessor instance;

    private final Map<String, Double> priceList = new HashMap<>();

    private final List<String> pendingBills = new ArrayList<>();

    private double totalRevenue = 0.0;

    private MedicalBillingProcessor() {
        priceList.put("CONSULTATION", 50.0);
        priceList.put("XRAY", 150.0);
        priceList.put("SURGERY", 1000.0);
    }

    public static MedicalBillingProcessor getInstance() {
        if (instance == null) {
            synchronized (MedicalBillingProcessor.class) {
                if (instance == null) {
                    instance = new MedicalBillingProcessor();
                }
            }
        }
        return instance;
    }

    public void processBilling(String patientId, String doctorId, String[] treatments) {
        double total = 0.0;
        String billId = "BILL" + System.currentTimeMillis();

        String billDetails = "";
        billDetails += "Bill ID: " + billId + "\n";
        billDetails += "Patient: " + patientId + "\n";
        billDetails += "Doctor: " + doctorId + "\n";

        for (String treatment : treatments) {

            double price = priceList.get(treatment);
            total += price;
            billDetails += treatment + ": $" + price + "\n";
        }

        if (total > 500) {
            total = total * 0.9;
        }

        billDetails += "Total: $" + total + "\n";

        logger.info("Billing: {}", billDetails);

        pendingBills.add(billId);
        totalRevenue += total;
    }


    public void updatePrices(String treatment, double newPrice) {
        priceList.put(treatment, newPrice);

        recalculateAllPendingBills();
    }


    private void recalculateAllPendingBills() {
        for (String billId : pendingBills) {

            processBilling(billId, "RECALC", new String[]{"CONSULTATION"});
        }
    }


    public Map<String, Double> getPriceList() {
        return priceList;
    }


    public double calculateInsurance(double billAmount) {

        return 0.0;
    }
} 
package com.ddp.kicknstyle.util;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class ValidationUtil {
    public static boolean validateTextField(TextField field, String fieldName, boolean allowDecimal) {
        if (field.getText().trim().isEmpty()) {
            showValidationAlert(fieldName + " is required");
            return false;
        }

        try {
            double value = Double.parseDouble(field.getText());
            if (value <= 0) {
                showValidationAlert(fieldName + " must be greater than zero");
                return false;
            }

            // If not allowing decimals, check for whole number
            if (!allowDecimal && (value != Math.floor(value))) {
                showValidationAlert(fieldName + " must be a whole number");
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            showValidationAlert("Invalid " + fieldName + " format");
            return false;
        }
    }

    private static void showValidationAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static String formatCurrency(double value) {
        return String.format("₱ %.2f", value);
    }

    public static double calculateTotalCost(int quantity, double unitCost) {
        return quantity * unitCost;
    }
}
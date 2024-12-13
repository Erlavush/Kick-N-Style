package com.ddp.kicknstyle.util;

import java.util.List;

import com.ddp.kicknstyle.model.SalesData;

import javafx.scene.chart.XYChart;

public class DashboardChartUtil {
    public static XYChart.Series<String, Number> createMonthlySalesSeries(List<SalesData> salesData) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Sales");

        for (SalesData data : salesData) {
            series.getData().add(new XYChart.Data<>(data.getMonth(), data.getSales()));
        }

        return series;
    }
}
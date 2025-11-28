package com.ttcsn.algorithm;
import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.ttcsn.model.dto.GenData;

public class FireflyOutput {

    /**
     * Xuất dữ liệu sang JSON (tùy chọn, để lưu backup)
     */
    public static void saveJson(List<GenData> bestOfGen, String path) throws IOException {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(path)) {
            gson.toJson(bestOfGen, writer);
        }
    }

    /**
     * Xuất HTML với dữ liệu nhúng sẵn, dùng Chart.js vẽ line chart
     */
    public static void saveHtml(List<GenData> bestOfGen, String path) throws IOException {
        Gson gson = new Gson();
        String jsonData = gson.toJson(bestOfGen);

        String htmlContent = ""
                + "<!DOCTYPE html>\n"
                + "<html lang=\"en\">\n"
                + "<head>\n"
                + "    <meta charset=\"UTF-8\">\n"
                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                + "    <title>Firefly Algorithm Chart</title>\n"
                + "    <script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n"
                + "    <style>\n"
                + "        * {\n"
                + "            margin: 0;\n"
                + "            padding: 0;\n"
                + "            box-sizing: border-box;\n"
                + "        }\n"
                + "        body {\n"
                + "            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n"
                + "            display: flex;\n"
                + "            flex-direction: column;\n"
                + "            align-items: center;\n"
                + "            justify-content: center;\n"
                + "            min-height: 100vh;\n"
                + "            padding: 40px 20px;\n"
                + "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n"
                + "        }\n"
                + "        .container {\n"
                + "            background: #ffffff;\n"
                + "            border-radius: 20px;\n"
                + "            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);\n"
                + "            padding: 40px;\n"
                + "            max-width: 1000px;\n"
                + "            width: 100%;\n"
                + "        }\n"
                + "        h2 {\n"
                + "            color: #2c3e50;\n"
                + "            text-align: center;\n"
                + "            margin-bottom: 30px;\n"
                + "            font-size: 28px;\n"
                + "            font-weight: 600;\n"
                + "            position: relative;\n"
                + "            padding-bottom: 15px;\n"
                + "        }\n"
                + "        h2::after {\n"
                + "            content: '';\n"
                + "            position: absolute;\n"
                + "            bottom: 0;\n"
                + "            left: 50%;\n"
                + "            transform: translateX(-50%);\n"
                + "            width: 80px;\n"
                + "            height: 4px;\n"
                + "            background: linear-gradient(90deg, #667eea, #764ba2);\n"
                + "            border-radius: 2px;\n"
                + "        }\n"
                + "        .chart-wrapper {\n"
                + "            position: relative;\n"
                + "            padding: 20px;\n"
                + "            background: #f8f9fa;\n"
                + "            border-radius: 15px;\n"
                + "            box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.06);\n"
                + "        }\n"
                + "        canvas {\n"
                + "            background-color: #ffffff;\n"
                + "            border-radius: 10px;\n"
                + "            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.07);\n"
                + "        }\n"
                + "        @media (max-width: 768px) {\n"
                + "            .container {\n"
                + "                padding: 25px;\n"
                + "            }\n"
                + "            h2 {\n"
                + "                font-size: 24px;\n"
                + "            }\n"
                + "            .chart-wrapper {\n"
                + "                padding: 15px;\n"
                + "            }\n"
                + "        }\n"
                + "    </style>\n"
                + "</head>\n"
                + "<body>\n"
                + "    <div class=\"container\">\n"
                + "        <h2>Độ sáng tối ưu theo Gen</h2>\n"
                + "        <div class=\"chart-wrapper\">\n"
                + "            <canvas id=\"brightnessChart\"></canvas>\n"
                + "        </div>\n"
                + "    </div>\n"
                + "    <script>\n"
                + "        const data = " + jsonData + ";\n"
                + "        const labels = data.map(d => d.gen);\n"
                + "        const brightness = data.map(d => d.brightness);\n"
                + "        const ctx = document.getElementById('brightnessChart').getContext('2d');\n"
                + "        new Chart(ctx, {\n"
                + "            type: 'line',\n"
                + "            data: {\n"
                + "                labels: labels,\n"
                + "                datasets: [{\n"
                + "                    label: 'Độ sáng',\n"
                + "                    data: brightness,\n"
                + "                    fill: true,\n"
                + "                    backgroundColor: 'rgba(102, 126, 234, 0.1)',\n"
                + "                    borderColor: 'rgb(102, 126, 234)',\n"
                + "                    borderWidth: 3,\n"
                + "                    tension: 0.4,\n"
                + "                    pointRadius: 4,\n"
                + "                    pointHoverRadius: 6,\n"
                + "                    pointBackgroundColor: 'rgb(102, 126, 234)',\n"
                + "                    pointBorderColor: '#fff',\n"
                + "                    pointBorderWidth: 2,\n"
                + "                    pointHoverBackgroundColor: 'rgb(118, 75, 162)',\n"
                + "                    pointHoverBorderColor: '#fff'\n"
                + "                }]\n"
                + "            },\n"
                + "            options: {\n"
                + "                responsive: true,\n"
                + "                maintainAspectRatio: true,\n"
                + "                plugins: {\n"
                + "                    legend: {\n"
                + "                        display: true,\n"
                + "                        labels: {\n"
                + "                            font: { size: 14, weight: '600' },\n"
                + "                            color: '#2c3e50',\n"
                + "                            padding: 15\n"
                + "                        }\n"
                + "                    }\n"
                + "                },\n"
                + "                scales: {\n"
                + "                    x: {\n"
                + "                        title: {\n"
                + "                            display: true,\n"
                + "                            text: 'Gen',\n"
                + "                            font: { size: 14, weight: '600' },\n"
                + "                            color: '#2c3e50'\n"
                + "                        },\n"
                + "                        grid: { color: 'rgba(0, 0, 0, 0.05)' },\n"
                + "                        ticks: { color: '#555' }\n"
                + "                    },\n"
                + "                    y: {\n"
                + "                        title: {\n"
                + "                            display: true,\n"
                + "                            text: 'Độ sáng',\n"
                + "                            font: { size: 14, weight: '600' },\n"
                + "                            color: '#2c3e50'\n"
                + "                        },\n"
                + "                        grid: { color: 'rgba(0, 0, 0, 0.05)' },\n"
                + "                        ticks: { color: '#555' }\n"
                + "                    }\n"
                + "                }\n"
                + "            }\n"
                + "        });\n"
                + "    </script>\n"
                + "</body>\n"
                + "</html>";

        try (FileWriter writer = new FileWriter(path)) {
            writer.write(htmlContent);
        }
    }

    /**
     * Mở file HTML trên trình duyệt mặc định
     */
    public static void openHtml(String htmlPath) throws IOException {
        File htmlFile = new File(htmlPath);
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(htmlFile.toURI());
        } else {
            System.out.println("Không hỗ trợ mở trình duyệt tự động. Mở file: " + htmlFile.getAbsolutePath());
        }
    }

    /**
     * Xuất JSON + HTML và mở trình duyệt
     */
    public static void exportAndOpen(List<GenData> bestOfGen) throws IOException {
        String outputDir = "src/main/resources/output/";
        new File(outputDir).mkdirs(); // tạo thư mục nếu chưa có

        String jsonPath = outputDir + "firefly_best_gen.json";
        String htmlPath = outputDir + "firefly_chart.html";

        saveJson(bestOfGen, jsonPath);       // xuất JSON (tùy chọn)
        saveHtml(bestOfGen, htmlPath);       // xuất HTML
        openHtml(htmlPath);                  // mở trình duyệt
    }
}
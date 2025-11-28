package com.ttcsn.algorithm;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.google.gson.Gson;
import com.ttcsn.config.Constant;
import com.ttcsn.model.dto.FireflyPoint;

public class FireflyOutput {

    // Đường dẫn file
    private static final String OUTPUT_DIR = "src/main/resources/output/";
    private static final String JSON_FILE = OUTPUT_DIR + "firefly_data.json";
    private static final String HTML_FILE = OUTPUT_DIR + "firefly_comparison_chart.html";

    private static class ChartDataWrapper {
        List<FireflyPoint> initial;
        List<FireflyPoint> finals;
        double budget;

        public ChartDataWrapper(List<FireflyPoint> initial, List<FireflyPoint> finals, double budget) {
            this.initial = initial;
            this.finals = finals;
            this.budget = budget;
        }
    }

    public static void saveDataToJson(List<FireflyPoint> initial, List<FireflyPoint> finals) {
        new File(OUTPUT_DIR).mkdirs();
        ChartDataWrapper data = new ChartDataWrapper(initial, finals, Constant.MAX_COST); // Dùng MAX_COST
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(JSON_FILE)) {
            gson.toJson(data, writer);
            System.out.println("--> [OK] Đã lưu dữ liệu JSON tại: " + JSON_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportComparisonCharts() throws IOException {
        File jsonFile = new File(JSON_FILE);
        if (!jsonFile.exists()) return;

        String jsonContent = new String(Files.readAllBytes(Paths.get(JSON_FILE)));

        String htmlContent = ""
                + "<!DOCTYPE html><html lang='vi'><head><meta charset='UTF-8'><title>Báo cáo Thuật toán Firefly</title>"
                + "<script src='https://cdn.jsdelivr.net/npm/chart.js'></script>"
                + "<script src='https://cdnjs.cloudflare.com/ajax/libs/chartjs-plugin-annotation/3.0.1/chartjs-plugin-annotation.min.js'></script>"
                + "<style>"
                + "  body{font-family:'Segoe UI', sans-serif; background:#f4f4f9; padding:20px; display:flex; flex-direction:column; align-items:center;}"
                + "  .container{display:flex; gap:20px; justify-content:center; flex-wrap:wrap; width: 100%;}"
                + "  .box{background:white; padding:20px; width:45%; min-width:500px; border-radius:12px; box-shadow:0 4px 15px rgba(0,0,0,0.1);}"
                + "  h1{color:#2c3e50; margin-bottom:10px;}"
                + "  h2{text-align:center; font-size:1.3em; color:#555; margin-bottom:15px; border-bottom: 2px solid #eee; padding-bottom:10px;}"
                + "</style>"
                + "</head><body>"

                + "<h1>So sánh Hiệu quả Tối ưu hóa (Bubble Chart)</h1>"
                + "<div class='container'>"
                + "  <div class='box'><h2>Lúc Khởi tạo (Gen 0)</h2><canvas id='c1'></canvas></div>"
                + "  <div class='box'><h2>Kết quả Cuối cùng (Gen Max)</h2><canvas id='c2'></canvas></div>"
                + "</div>"

                + "<script>"
                + "  const rawData = " + jsonContent + ";"
                + "  const budget = rawData.budget;"

                // --- HÀM GỘP DỮ LIỆU (GROUPING) ---
                // Nếu Cost và Time giống nhau -> Tăng biến 'count' lên
                + "  function groupData(data) {"
                + "     const map = new Map();"
                + "     data.forEach(p => {"
                // Làm tròn số liệu một chút để dễ gộp nhóm (Cost lấy nguyên, Time lấy 4 số lẻ)
                + "         const key = Math.floor(p.cost) + '_' + p.time.toFixed(4);"
                + "         if (!map.has(key)) {"
                + "             map.set(key, { x: p.cost, y: p.time, isValid: p.isValid, count: 0 });"
                + "         }"
                + "         map.get(key).count++;"
                + "     });"
                + "     return Array.from(map.values());"
                + "  }"

                // --- HÀM TÁCH DỮ LIỆU SAU KHI GỘP ---
                + "  function process(list) {"
                + "     const grouped = groupData(list);"
                + "     return {"
                + "       valid: grouped.filter(p => p.isValid),"
                + "       invalid: grouped.filter(p => !p.isValid)"
                + "     };"
                + "  }"

                + "  const p1 = process(rawData.initial);"
                + "  const p2 = process(rawData.finals);"

                // --- HÀM VẼ BIỂU ĐỒ ---
                + "  function draw(id, pData) {"
                + "    new Chart(document.getElementById(id), {"
                + "      type: 'scatter',"
                + "      data: { datasets: ["
                + "        { "
                + "           label:'Hợp lệ', data:pData.valid, "
                + "           backgroundColor:'rgba(46, 204, 113, 0.6)', "
                + "           borderColor:'rgba(46, 204, 113, 1)', borderWidth: 1"
                + "        },"
                + "        { "
                + "           label:'Vi phạm', data:pData.invalid, "
                + "           backgroundColor:'rgba(231, 76, 60, 0.7)', "
                + "           borderColor:'rgba(231, 76, 60, 1)', borderWidth: 2,"
                + "           pointStyle:'cross'"
                + "        }"
                + "      ]},"
                + "      options: {"
                + "        elements: {"
                + "           point: {"
                // --- ĐÂY LÀ CHỖ QUYẾT ĐỊNH KÍCH THƯỚC ---
                // Công thức: Bán kính = 5 + (số lượng trùng lặp * 1.5)
                // Ví dụ: 1 con -> r=5. 10 con -> r=20.
                + "              radius: function(context) {"
                + "                 const val = context.raw;"
                + "                 if (!val || !val.count) return 5;"
                + "                 return 5 + (val.count - 1) * 2;"
                + "              },"
                + "              hoverRadius: function(context) {"
                + "                 const val = context.raw;"
                + "                 if (!val || !val.count) return 7;"
                + "                 return 7 + (val.count - 1) * 2;"
                + "              }"
                + "           }"
                + "        },"
                + "        plugins: { "
                + "           tooltip: { callbacks: { label: (ctx) => `SL: ${ctx.raw.count} | Cost: ${ctx.parsed.x.toLocaleString()} | Time: ${ctx.parsed.y.toFixed(2)}` } },"
                + "           annotation: { annotations: { line1: { type:'line', xMin:budget, xMax:budget, borderColor:'rgb(255, 99, 132)', borderWidth:2, borderDash:[5,5], label: {display: true, content: 'Budget', position: 'start', backgroundColor: 'rgba(255, 99, 132, 0.8)'} } } } "
                + "        },"
                + "        scales: { "
                + "           x: { title:{display:true, text:'Chi phí (VNĐ)'}, ticks: { callback: function(val) { return val.toLocaleString(); } } }, "
                + "           y: { title:{display:true, text:'Thời gian (Giờ)'} } "
                + "        }"
                + "      }"
                + "    });"
                + "  }"

                + "  draw('c1', p1); draw('c2', p2);"
                + "</script></body></html>";

        try (FileWriter writer = new FileWriter(HTML_FILE)) {
            writer.write(htmlContent);
            System.out.println("--> [OK] Đã xuất biểu đồ HTML tại: " + HTML_FILE);
        }

        File file = new File(HTML_FILE);
        if (Desktop.isDesktopSupported() && file.exists()) {
            Desktop.getDesktop().browse(file.toURI());
        }
    }
}
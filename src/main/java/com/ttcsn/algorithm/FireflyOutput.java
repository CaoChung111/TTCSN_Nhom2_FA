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
import com.ttcsn.model.dto.ComparisonPoint;
import com.ttcsn.model.dto.GenBrightness;

public class FireflyOutput {

    private static final String OUTPUT_DIR = "src/main/resources/output/";
    private static final String JSON_COMPARISON = OUTPUT_DIR + "firefly_comparison.json";
    private static final String JSON_GEN = OUTPUT_DIR + "firefly_gen.json";
    private static final String HTML_FILE = OUTPUT_DIR + "firefly_complete_report.html";

    // Wrapper cho dữ liệu So sánh (Bubble Chart)
    private static class ComparisonData {
        List<ComparisonPoint> initial;
        List<ComparisonPoint> finals;
        double budget;

        public ComparisonData(List<ComparisonPoint> initial, List<ComparisonPoint> finals, double budget) {
            this.initial = initial;
            this.finals = finals;
            this.budget = budget;
        }
    }

    /**
     * Lưu dữ liệu So sánh (Cost vs Time)
     */
    public static void saveComparisonData(List<ComparisonPoint> initial, List<ComparisonPoint> finals) {
        new File(OUTPUT_DIR).mkdirs();
        ComparisonData data = new ComparisonData(initial, finals, Constant.MAX_COST);
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(JSON_COMPARISON)) {
            gson.toJson(data, writer);
            System.out.println("--> [OK] Đã lưu dữ liệu So sánh tại: " + JSON_COMPARISON);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lưu dữ liệu Độ sáng theo Gen
     */
    public static void saveGenData(List<GenBrightness> genData) {
        new File(OUTPUT_DIR).mkdirs();
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(JSON_GEN)) {
            gson.toJson(genData, writer);
            System.out.println("--> [OK] Đã lưu dữ liệu Gen tại: " + JSON_GEN);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Xuất HTML với tất cả biểu đồ
     */
    public static void exportCompleteReport() throws IOException {
        // Đọc dữ liệu từ các file JSON
        String comparisonJson = readJsonFile(JSON_COMPARISON);
        String genJson = readJsonFile(JSON_GEN);
        String stabilityJson = readJsonFile("src/main/resources/output/stability_stats.json");

        String htmlContent = ""
                + "<!DOCTYPE html><html lang='vi'><head><meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "<title>Báo cáo Thuật toán Firefly</title>"
                + "<script src='https://cdn.jsdelivr.net/npm/chart.js'></script>"
                + "<script src='https://cdnjs.cloudflare.com/ajax/libs/chartjs-plugin-annotation/3.0.1/chartjs-plugin-annotation.min.js'></script>"
                + "<style>"
                + "  * { margin:0; padding:0; box-sizing:border-box; }"
                + "  body { font-family:'Segoe UI', sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding:40px 20px; }"
                + "  .header { text-align:center; color:white; margin-bottom:40px; }"
                + "  .header h1 { font-size:2.5em; margin-bottom:10px; text-shadow: 2px 2px 4px rgba(0,0,0,0.3); }"
                + "  .header p { font-size:1.1em; opacity:0.9; }"
                + "  .container { max-width:1400px; margin:0 auto; }"
                + "  .section { background:white; border-radius:20px; padding:30px; margin-bottom:30px; box-shadow:0 20px 60px rgba(0,0,0,0.3); }"
                + "  .section h2 { color:#2c3e50; font-size:1.8em; margin-bottom:25px; text-align:center; padding-bottom:15px; border-bottom:3px solid #667eea; }"
                + "  .bubble-charts { display:flex; gap:20px; justify-content:center; flex-wrap:wrap; }"
                + "  .bubble-box { flex:1; min-width:450px; background:#f8f9fa; padding:20px; border-radius:12px; }"
                + "  .bubble-box h3 { text-align:center; color:#555; margin-bottom:15px; font-size:1.2em; }"
                + "  .chart-wrapper { background:#f8f9fa; padding:20px; border-radius:15px; }"
                + "  canvas { background:white; border-radius:10px; }"
                + "  @media (max-width: 768px) { .bubble-box { min-width:100%; } }"
                + "</style>"
                + "</head><body>"
                
                // Header
                + "<div class='header'>"
                + "  <h1>🔥 Báo cáo Thuật toán Firefly</h1>"
                + "  <p>Phân tích tối ưu hóa đa mục tiêu</p>"
                + "</div>"
                
                + "<div class='container'>"
                
                // Section 1: Bubble Charts (So sánh Cost vs Time)
                + "  <div class='section'>"
                + "    <h2>📊 So sánh Hiệu quả Tối ưu hóa (Cost vs Time)</h2>"
                + "    <div class='bubble-charts'>"
                + "      <div class='bubble-box'><h3>Lúc Khởi tạo (Gen 0)</h3><canvas id='bubbleInit'></canvas></div>"
                + "      <div class='bubble-box'><h3>Kết quả Cuối cùng (Gen Max)</h3><canvas id='bubbleFinal'></canvas></div>"
                + "    </div>"
                + "  </div>"
                
                // Section 2: Line Chart (Độ sáng theo Gen)
                + "  <div class='section'>"
                + "    <h2>📈 Độ sáng tối ưu theo Gen</h2>"
                + "    <div class='chart-wrapper'><canvas id='lineChart'></canvas></div>"
                + "  </div>"
                
                // Section 3: Stability Chart (Độ ổn định qua các lần chạy)
                + "  <div class='section'>"
                + "    <h2>🎯 Độ ổn định qua các lần chạy</h2>"
                + "    <div class='chart-wrapper'><canvas id='stabilityChart'></canvas></div>"
                + "  </div>"
                
                + "</div>"
                
                + "<script>"
                // Dữ liệu
                + "  const comparisonData = " + (comparisonJson.isEmpty() ? "{}" : comparisonJson) + ";"
                + "  const genData = " + (genJson.isEmpty() ? "[]" : genJson) + ";"
                + "  const stabilityData = " + (stabilityJson.isEmpty() ? "[]" : stabilityJson) + ";"
                
                // === BUBBLE CHART (Cost vs Time) ===
                + "  function groupData(data) {"
                + "     const map = new Map();"
                + "     data.forEach(p => {"
                + "         const key = Math.floor(p.cost) + '_' + p.time.toFixed(4);"
                + "         if (!map.has(key)) map.set(key, { x: p.cost, y: p.time, isValid: p.isValid, count: 0 });"
                + "         map.get(key).count++;"
                + "     });"
                + "     return Array.from(map.values());"
                + "  }"
                
                + "  function processBubble(list) {"
                + "     const grouped = groupData(list);"
                + "     return { valid: grouped.filter(p => p.isValid), invalid: grouped.filter(p => !p.isValid) };"
                + "  }"
                
                + "  if (comparisonData.initial && comparisonData.finals) {"
                + "    const p1 = processBubble(comparisonData.initial);"
                + "    const p2 = processBubble(comparisonData.finals);"
                + "    const budget = comparisonData.budget || 0;"
                
                + "    function drawBubble(id, pData) {"
                + "      new Chart(document.getElementById(id), {"
                + "        type: 'scatter',"
                + "        data: { datasets: ["
                + "          { label:'Hợp lệ', data:pData.valid, backgroundColor:'rgba(46, 204, 113, 0.6)', borderColor:'rgba(46, 204, 113, 1)', borderWidth: 1 },"
                + "          { label:'Vi phạm', data:pData.invalid, backgroundColor:'rgba(231, 76, 60, 0.7)', borderColor:'rgba(231, 76, 60, 1)', borderWidth: 2, pointStyle:'cross' }"
                + "        ]},"
                + "        options: {"
                + "          elements: { point: { "
                + "            radius: function(ctx) { const v = ctx.raw; return v && v.count ? 5 + (v.count - 1) * 2 : 5; },"
                + "            hoverRadius: function(ctx) { const v = ctx.raw; return v && v.count ? 7 + (v.count - 1) * 2 : 7; }"
                + "          }},"
                + "          plugins: { "
                + "            tooltip: { callbacks: { label: (ctx) => `SL: ${ctx.raw.count} | Cost: ${ctx.parsed.x.toLocaleString()} | Time: ${ctx.parsed.y.toFixed(2)}` } },"
                + "            annotation: { annotations: { line1: { type:'line', xMin:budget, xMax:budget, borderColor:'rgb(255, 99, 132)', borderWidth:2, borderDash:[5,5], label: {display: true, content: 'Budget', position: 'start', backgroundColor: 'rgba(255, 99, 132, 0.8)'} } } }"
                + "          },"
                + "          scales: { "
                + "            x: { title:{display:true, text:'Chi phí (VNĐ)'}, ticks: { callback: function(val) { return val.toLocaleString(); } } }, "
                + "            y: { title:{display:true, text:'Thời gian (Giờ)'} }"
                + "          }"
                + "        }"
                + "      });"
                + "    }"
                
                + "    drawBubble('bubbleInit', p1);"
                + "    drawBubble('bubbleFinal', p2);"
                + "  }"
                
                // === LINE CHART (Độ sáng theo Gen) ===
                + "  if (genData.length > 0) {"
                + "    const labels = genData.map((d, index) => index);" // Gen 0, 1, 2, ...
                + "    const brightness = genData.map(d => d.brightness);"
                + "    new Chart(document.getElementById('lineChart'), {"
                + "      type: 'line',"
                + "      data: {"
                + "        labels: labels,"
                + "        datasets: [{"
                + "          label: 'Độ sáng',"
                + "          data: brightness,"
                + "          fill: true,"
                + "          backgroundColor: 'rgba(102, 126, 234, 0.1)',"
                + "          borderColor: 'rgb(102, 126, 234)',"
                + "          borderWidth: 3,"
                + "          tension: 0.4,"
                + "          pointRadius: 4,"
                + "          pointHoverRadius: 6,"
                + "          pointBackgroundColor: 'rgb(102, 126, 234)',"
                + "          pointBorderColor: '#fff',"
                + "          pointBorderWidth: 2"
                + "        }]"
                + "      },"
                + "      options: {"
                + "        responsive: true,"
                + "        plugins: { "
                + "          legend: { labels: { font: { size: 14, weight: '600' }, color: '#2c3e50' } },"
                + "          tooltip: { callbacks: { label: (ctx) => `Gen ${ctx.parsed.x} | Độ sáng: ${ctx.parsed.y.toFixed(4)}` } }"
                + "        },"
                + "        scales: {"
                + "          x: { title: { display: true, text: 'Gen', font: { size: 14, weight: '600' }, color: '#2c3e50' } },"
                + "          y: { title: { display: true, text: 'Độ sáng', font: { size: 14, weight: '600' }, color: '#2c3e50' } }"
                + "        }"
                + "      }"
                + "    });"
                + "  }"
                
                // === STABILITY CHART (Biểu đồ cột kép) ===
                + "  if (stabilityData.length > 0) {"
                + "    const runLabels = stabilityData.map(d => 'Lần ' + d.runNumber);"
                + "    const correctPercent = stabilityData.map(d => d.correctPercent);"
                + "    const incorrectPercent = stabilityData.map(d => d.incorrectPercent);"
                + "    new Chart(document.getElementById('stabilityChart'), {"
                + "      type: 'bar',"
                + "      data: {"
                + "        labels: runLabels,"
                + "        datasets: ["
                + "          { "
                + "            label: '✅ Lộ trình ĐÚNG (%)', "
                + "            data: correctPercent, "
                + "            backgroundColor: 'rgba(46, 204, 113, 0.7)', "
                + "            borderColor: 'rgba(46, 204, 113, 1)', "
                + "            borderWidth: 2"
                + "          },"
                + "          { "
                + "            label: '❌ Lộ trình SAI (%)', "
                + "            data: incorrectPercent, "
                + "            backgroundColor: 'rgba(231, 76, 60, 0.7)', "
                + "            borderColor: 'rgba(231, 76, 60, 1)', "
                + "            borderWidth: 2"
                + "          }"
                + "        ]"
                + "      },"
                + "      options: {"
                + "        responsive: true,"
                + "        plugins: { "
                + "          legend: { labels: { font: { size: 14, weight: '600' }, color: '#2c3e50' } },"
                + "          tooltip: { "
                + "            callbacks: { "
                + "              label: (ctx) => ctx.dataset.label + ': ' + ctx.parsed.y.toFixed(1) + '%' "
                + "            } "
                + "          }"
                + "        },"
                + "        scales: {"
                + "          x: { "
                + "            title: { display: true, text: 'Số lần chạy', font: { size: 14, weight: '600' }, color: '#2c3e50' },"
                + "            stacked: false"
                + "          },"
                + "          y: { "
                + "            title: { display: true, text: 'Tỉ lệ (%)', font: { size: 14, weight: '600' }, color: '#2c3e50' },"
                + "            min: 0,"
                + "            max: 100,"
                + "            stacked: false"
                + "          }"
                + "        }"
                + "      }"
                + "    });"
                + "  } else {"
                + "    document.getElementById('stabilityChart').parentElement.innerHTML = '<p style=\"text-align:center; color:#999; padding:40px;\">Chưa có dữ liệu độ ổn định. Hãy chạy thuật toán nhiều lần!</p>';"
                + "  }"
                
                + "</script></body></html>";

        try (FileWriter writer = new FileWriter(HTML_FILE)) {
            writer.write(htmlContent);
            System.out.println("--> [OK] Đã xuất báo cáo hoàn chỉnh tại: " + HTML_FILE);
        }

        // Mở trình duyệt
        File file = new File(HTML_FILE);
        if (Desktop.isDesktopSupported() && file.exists()) {
            Desktop.getDesktop().browse(file.toURI());
        }
    }

    /**
     * Đọc nội dung file JSON
     */
    private static String readJsonFile(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) return "{}";
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            return "{}";
        }
    }

    /**
     * Phương thức tiện ích để xuất tất cả
     * @param initialPoints - List<ComparisonPoint> với cost, time, isValid cho Gen 0
     * @param finalPoints - List<ComparisonPoint> với cost, time, isValid cho Gen cuối
     * @param genBrightness - List<GenBrightness> chỉ chứa brightness theo từng Gen
     */
    public static void exportAll(List<ComparisonPoint> initialPoints, 
                                  List<ComparisonPoint> finalPoints,
                                  List<GenBrightness> genBrightness) throws IOException {
        saveComparisonData(initialPoints, finalPoints);
        saveGenData(genBrightness);
        exportCompleteReport();
    }
}
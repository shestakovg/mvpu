package Helpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class pdfService {
        private static final String HEADER_COLOR    = "#808080";
        private static float TEXT_SIZE = 8f;
        private static final int PAGE_WIDTH  = 595;
        private static final int PAGE_HEIGHT = 842;
        private static final int MARGIN      = 40;
        private static final int ROW_HEIGHT  = 18;
        private static final int LINE_HEIGHT     = 12;   // height per text line
        private static final int CELL_PADDING_V  = 4;    // top+bottom padding
        private static final int CELL_PADDING_H  = 4;    // left+right padding

        // Column widths
        private static final float COL_W_NUM    = 30f;
        private static final float COL_W_SKU    = 230f;
        private static final float COL_W_QTY    = 50f;
        private static final float COL_W_PRICE  = 80f;
        private static final float COL_W_AMOUNT = 80f;

        // Column X positions
        private static final float COL_X_NUM    = MARGIN;
        private static final float COL_X_SKU    = COL_X_NUM    + COL_W_NUM;
        private static final float COL_X_QTY    = COL_X_SKU    + COL_W_SKU;
        private static final float COL_X_PRICE  = COL_X_QTY    + COL_W_QTY;
        private static final float COL_X_AMOUNT = COL_X_PRICE  + COL_W_PRICE;
        private static final float TABLE_RIGHT  = COL_X_AMOUNT + COL_W_AMOUNT;

        // Vertical line X positions (for drawing grid)
        private float[] VERTICAL_LINES = {
                COL_X_NUM,
                COL_X_SKU,
                COL_X_QTY,
                COL_X_PRICE,
                COL_X_AMOUNT,
                TABLE_RIGHT
        };

        // Track row Y positions for horizontal lines
        private List<Float> horizontalLines = new ArrayList<>();
        private float tableStartY;
        private pdfService.Order order;

        public File createPdf(Context context, pdfService.Order order) throws IOException {
            this.order = order;
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                    PAGE_WIDTH, PAGE_HEIGHT, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            int y = MARGIN + 20;
            y = drawTitle(canvas, "Клієнт: " + order.clientName, y);
            y = drawTitle(canvas, "Заказ: "+ order.orderName, y);
            y += 15;

            // Start table
            tableStartY = y;
            horizontalLines.clear();
            horizontalLines.add((float) y);  // top border

            y = drawHeaderText(canvas, y);
            horizontalLines.add((float) y);  // line under header

            int num = 1;
            float totalAmount = 0;

            for (OrderItem item : order.orderItems) {
                float amount = (item.quantity1 + item.quantity2) * item.price;
                totalAmount += amount;

                // New page if needed
                if (y + ROW_HEIGHT > PAGE_HEIGHT - MARGIN - 40) {
                    // Draw grid for the current page first
                    drawGrid(canvas, tableStartY, y);

                    document.finishPage(page);
                    pageInfo = new PdfDocument.PageInfo.Builder(
                            PAGE_WIDTH, PAGE_HEIGHT,
                            document.getPages().size() + 1).create();
                    page = document.startPage(pageInfo);
                    canvas = page.getCanvas();

                    // Reset for new page
                    y = MARGIN + 20;
                    tableStartY = y;
                    horizontalLines.clear();
                    horizontalLines.add((float) y);

                    y = drawHeaderText(canvas, y);
                    horizontalLines.add((float) y);
                }

                y = drawRowText(canvas, num++, item.sku, item.quantity1 + item.quantity2, item.price, amount, y);
                horizontalLines.add((float) y);
            }

            // Total row
            y = drawTotalRowText(canvas, totalAmount, y);
            horizontalLines.add((float) y);  // bottom border

            // Draw the complete grid (vertical + horizontal lines)
            drawGrid(canvas, tableStartY, y);

            document.finishPage(page);

            File file = new File(context.getExternalFilesDir(null),  order.orderUUID+".pdf");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                document.writeTo(fos);
            }
            document.close();
            return file;
        }

        private void drawGrid(Canvas canvas, float topY, float bottomY) {
            Paint borderPaint = new Paint();
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setColor(Color.BLACK);
            borderPaint.setStrokeWidth(1f);
            borderPaint.setAntiAlias(false);  // crisp lines

            // Vertical lines (top to bottom of table)
            for (float x : VERTICAL_LINES) {
                canvas.drawLine(x, topY, x, bottomY, borderPaint);
            }

            // Horizontal lines (left to right of table)
            for (float lineY : horizontalLines) {
                canvas.drawLine(MARGIN, lineY, TABLE_RIGHT, lineY, borderPaint);
            }
        }

        // ----------------------------------------------------------------
        // BACKGROUNDS & TEXT (no borders here — grid handles it)

        private int drawTitle(Canvas canvas, String title, int y) {
            Paint paint = new Paint();
            paint.setTextSize(14f);
            paint.setFakeBoldText(true);
            paint.setColor(Color.BLACK);
            canvas.drawText(title, MARGIN, y, paint);
            return y + 24;
        }

        private int drawHeaderText(Canvas canvas, int y) {
            // Header background (full row blue)
            Paint bgPaint = new Paint();
            bgPaint.setStyle(Paint.Style.FILL);
            bgPaint.setColor(Color.parseColor(HEADER_COLOR));
            canvas.drawRect(MARGIN, y, TABLE_RIGHT, y + ROW_HEIGHT, bgPaint);

            Paint textPaint = new Paint();
            textPaint.setTextSize(TEXT_SIZE);
            textPaint.setFakeBoldText(true);
            textPaint.setColor(Color.WHITE);

            float textY = y + (ROW_HEIGHT / 2f) + (textPaint.getTextSize() / 3f);

            canvas.drawText("#",        COL_X_NUM    + 5, textY, textPaint);
            canvas.drawText("Номенклатура", COL_X_SKU    + 5, textY, textPaint);
            canvas.drawText("Кількість",      COL_X_QTY    + 5, textY, textPaint);
            //canvas.drawText("Кількість Б-Д",      COL_X_QTY    + 5, textY, textPaint);
            canvas.drawText("Ціна",    COL_X_PRICE  + 5, textY, textPaint);
            canvas.drawText("Сума",   COL_X_AMOUNT + 5, textY, textPaint);

            return y + ROW_HEIGHT;
        }

        private int drawRowText(Canvas canvas, int num, String sku,
                                int qty, float price, float amount, int y) {

            Paint textPaint = new Paint();
            textPaint.setTextSize(TEXT_SIZE);
            textPaint.setColor(Color.BLACK);
            textPaint.setAntiAlias(true);
            textPaint.setSubpixelText(true);

            // Wrap SKU into multiple lines
            float skuMaxWidth = COL_W_SKU - (CELL_PADDING_H * 2);
            List<String> skuLines = wrapText(sku, skuMaxWidth, textPaint);

            // Calculate dynamic row height
            int rowHeight = Math.max(
                    LINE_HEIGHT + CELL_PADDING_V * 2,
                    skuLines.size() * LINE_HEIGHT + CELL_PADDING_V * 2
            );

            // Alternating row background
            if (num % 2 == 0) {
                Paint bgPaint = new Paint();
                bgPaint.setStyle(Paint.Style.FILL);
                bgPaint.setColor(Color.parseColor("#F5F5F5"));
                canvas.drawRect(MARGIN, y, TABLE_RIGHT, y + rowHeight, bgPaint);
            }

            // Draw SKU lines
            float skuY = y + CELL_PADDING_V + LINE_HEIGHT - 3;  // baseline of first line
            for (String line : skuLines) {
                canvas.drawText(line, COL_X_SKU + CELL_PADDING_H, skuY, textPaint);
                skuY += LINE_HEIGHT;
            }

            // Draw other columns vertically centered in the row
            float singleLineY = y + (rowHeight / 2f) + (textPaint.getTextSize() / 3f);

            canvas.drawText(String.valueOf(num),
                    COL_X_NUM + CELL_PADDING_H, singleLineY, textPaint);
            canvas.drawText(NumberFormatter.formatQty(qty),
                    COL_X_QTY + CELL_PADDING_H, singleLineY, textPaint);
            canvas.drawText(NumberFormatter.formatAmount(price),
                    COL_X_PRICE + CELL_PADDING_H, singleLineY, textPaint);
            canvas.drawText(NumberFormatter.formatAmount(amount),
                    COL_X_AMOUNT + CELL_PADDING_H, singleLineY, textPaint);

            return y + rowHeight;
        }

        private int drawTotalRowText(Canvas canvas, float total, int y) {
            // Total row background
            Paint bgPaint = new Paint();
            bgPaint.setStyle(Paint.Style.FILL);
            bgPaint.setColor(Color.parseColor(HEADER_COLOR));
            canvas.drawRect(MARGIN, y, TABLE_RIGHT, y + ROW_HEIGHT, bgPaint);

            Paint textPaint = new Paint();
            textPaint.setTextSize(TEXT_SIZE);
            textPaint.setFakeBoldText(true);
            textPaint.setColor(Color.WHITE);

            float textY = y + (ROW_HEIGHT / 2f) + (textPaint.getTextSize() / 3f);

            canvas.drawText("Всього:",                      COL_X_PRICE  + 5, textY, textPaint);
            canvas.drawText(NumberFormatter.formatAmount(total),  COL_X_AMOUNT + 5, textY, textPaint);

            return y + ROW_HEIGHT;
        }

    /**
     * Splits text into lines that fit within maxWidth.
     * Breaks on word boundaries when possible.
     */
        private List<String> wrapText(String text, float maxWidth, Paint paint) {
            List<String> lines = new ArrayList<>();
            if (text == null || text.isEmpty()) {
                lines.add("");
                return lines;
            }

            String[] words = text.split(" ");
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                String testLine = currentLine.length() == 0
                        ? word
                        : currentLine + " " + word;

                if (paint.measureText(testLine) <= maxWidth) {
                    currentLine.setLength(0);
                    currentLine.append(testLine);
                } else {
                    // Current line is full
                    if (currentLine.length() > 0) {
                        lines.add(currentLine.toString());
                        currentLine.setLength(0);
                    }

                    // If single word is too long ? break by character
                    if (paint.measureText(word) > maxWidth) {
                        StringBuilder partial = new StringBuilder();
                        for (char c : word.toCharArray()) {
                            if (paint.measureText(partial.toString() + c) <= maxWidth) {
                                partial.append(c);
                            } else {
                                lines.add(partial.toString());
                                partial.setLength(0);
                                partial.append(c);
                            }
                        }
                        if (partial.length() > 0) {
                            currentLine.append(partial);
                        }
                    } else {
                        currentLine.append(word);
                    }
                }
            }

            if (currentLine.length() > 0) {
                lines.add(currentLine.toString());
            }

            return lines;
        }


        // ----------------------------------------------------------------

        public static class OrderItem {
            public String sku;
            public int quantity1;
            public int quantity2;
            public float price;

            public OrderItem(String sku, int quantity1, int quantity2, float price) {
                this.sku = sku;
                this.quantity1 = quantity1;
                this.quantity2 = quantity2;
                this.price = price;
            }
        }

        public static class Order {
            public final String clientName;
            public final String orderName;
            public final String orderUUID;
            public final List<OrderItem> orderItems;

            public Order(String clientName, String orderName, String orderUUID, List<OrderItem> orderItems) {
                this.clientName = clientName;
                this.orderName = orderName;
                this.orderUUID = orderUUID;
                this.orderItems = orderItems;
            }
        }

    public static class NumberFormatter {
        private static final NumberFormat INTEGER_FORMAT;
        private static final NumberFormat DECIMAL_FORMAT;

        static {
            // Force consistent formatting regardless of device locale
            Locale locale = new Locale("uk", "UA");  // Ukrainian: space separator, comma decimal

            INTEGER_FORMAT = NumberFormat.getIntegerInstance(locale);

            DECIMAL_FORMAT = NumberFormat.getNumberInstance(locale);
            DECIMAL_FORMAT.setMinimumFractionDigits(2);
            DECIMAL_FORMAT.setMaximumFractionDigits(2);
            DECIMAL_FORMAT.setGroupingUsed(true);
        }

        public static String formatQty(int qty) {
            return INTEGER_FORMAT.format(qty);
        }

        public static String formatAmount(float amount) {
            return DECIMAL_FORMAT.format(amount);
        }

        public static String formatAmount(double amount) {
            return DECIMAL_FORMAT.format(amount);
        }
    }
}

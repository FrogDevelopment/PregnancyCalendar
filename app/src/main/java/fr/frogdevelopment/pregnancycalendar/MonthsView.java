package fr.frogdevelopment.pregnancycalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.view.View;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;
import org.threeten.bp.temporal.ChronoUnit;

public class MonthsView extends View {


    // defines paint and canvas
    private final Paint drawPaint;

    public MonthsView(Context context) {
        super(context);
        this.drawPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawPaint.setColor(Color.BLACK);
//        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(5);
//        drawPaint.setStyle(Paint.Style.STROKE);
//        drawPaint.setStrokeJoin(Paint.Join.ROUND);
//        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setTextSize(50);

        // Set a pixels value to offset the line from canvas edge
        int offsetY = 75;
        int offsetX = canvas.getWidth() / 4;

        int start = offsetY;
        int end = canvas.getHeight() - offsetY;

        Shader shader = new LinearGradient(
                offsetX,
                start,
                offsetX,
                end,
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_green_dark),
                Shader.TileMode.MIRROR /*or REPEAT*/);

        drawPaint.setShader(shader);

        // draw vertical line
        canvas.drawLine(offsetX, start, offsetX, end, drawPaint);

        int length = end - start;
        int lengthMonth = length / 9;
        int positionMonth = offsetY + 3; // fixme pourquoi ce décalage de px ?
        int positionText = positionMonth + lengthMonth / 2;

        LocalDate currentMonth = PregnancyUtils.conceptionDate;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
        for (int i = 0; i <= 9; i++) {
            // line month
            canvas.drawLine(
                    offsetX - 100, // startX
                    positionMonth, // startY
                    offsetX + 100, // stopX
                    positionMonth, // stopY
                    drawPaint // Paint
            );

            // label month
            canvas.drawText(
                    currentMonth.format(dateTimeFormatter),
                    offsetX + 120,
                    positionMonth,
                    drawPaint
            );

            currentMonth = currentMonth.plusMonths(1);

            if (i < 9) {
                // number month
                canvas.drawText(
                        getContext().getString(R.string.month_month, i + 1),
                        offsetX - 200,
                        positionText,
                        drawPaint
                );
            }

            positionMonth = positionMonth + lengthMonth;
            positionText = positionMonth + lengthMonth / 2 + 20;
        }


        drawPaint.setShader(null);

        addDate(canvas, offsetX, offsetY, length, getResources().getColor(R.color.colorPrimary), getContext().getString(R.string.months_today), LocalDate.now());

//        addDate(canvas, offsetX, offsetY, length, Color.RED, "test", LocalDate.of(2016, 11, 5));
    }

    private void addDate(Canvas canvas, int offsetX, int offsetY, int length, int color, String text, LocalDate date) {
//        long duration = ChronoUnit.DAYS.between(PregnancyUtils.conceptionDate, PregnancyUtils.getBirthRangeEnd(PregnancyUtils.amenorrheaDate));
        long duration = PregnancyUtils.NB_DAYS_CONCEPTION_TO_BIRTH;
        long now = ChronoUnit.DAYS.between(PregnancyUtils.conceptionDate, date);

        int y = (int) (now * length / duration) + offsetY;

        drawTriangle(canvas, offsetX, y, color, text);
    }

    private void drawTriangle(Canvas canvas, int x, int y, int color, String text) {
        Path path = new Path();
        path.moveTo(x, y);
        path.lineTo(x + 50, y - 25);
        path.lineTo(x + 50, y + 25);
        path.close();

        drawPaint.setColor(color);
        drawPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, drawPaint);

        canvas.drawText(text, x + 75, y + 15, drawPaint);
    }
}

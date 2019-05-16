package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.ui.ActionBar.Theme;

public class TimerDrawable extends Drawable {
    private Paint linePaint = new Paint(1);
    private Paint paint = new Paint(1);
    private int time = 0;
    private int timeHeight = 0;
    private StaticLayout timeLayout;
    private TextPaint timePaint = new TextPaint(1);
    private float timeWidth = 0.0f;

    public int getOpacity() {
        return 0;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public TimerDrawable(Context context) {
        this.timePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.timePaint.setTextSize((float) AndroidUtilities.dp(11.0f));
        this.linePaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        this.linePaint.setStyle(Style.STROKE);
    }

    public void setTime(int i) {
        String stringBuilder;
        this.time = i;
        int i2 = this.time;
        String str = "";
        StringBuilder stringBuilder2;
        if (i2 < 1 || i2 >= 60) {
            i2 = this.time;
            if (i2 < 60 || i2 >= 3600) {
                i2 = this.time;
                if (i2 < 3600 || i2 >= 86400) {
                    i2 = this.time;
                    if (i2 < 86400 || i2 >= 604800) {
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(str);
                        stringBuilder2.append((((i / 60) / 60) / 24) / 7);
                        stringBuilder = stringBuilder2.toString();
                        if (stringBuilder.length() < 2) {
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(stringBuilder);
                            stringBuilder2.append("w");
                            stringBuilder = stringBuilder2.toString();
                        } else if (stringBuilder.length() > 2) {
                            stringBuilder = "c";
                        }
                    } else {
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(str);
                        stringBuilder2.append(((i / 60) / 60) / 24);
                        stringBuilder = stringBuilder2.toString();
                        if (stringBuilder.length() < 2) {
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(stringBuilder);
                            stringBuilder2.append("d");
                            stringBuilder = stringBuilder2.toString();
                        }
                    }
                } else {
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(str);
                    stringBuilder2.append((i / 60) / 60);
                    stringBuilder = stringBuilder2.toString();
                    if (stringBuilder.length() < 2) {
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(stringBuilder);
                        stringBuilder2.append("h");
                        stringBuilder = stringBuilder2.toString();
                    }
                }
            } else {
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(str);
                stringBuilder2.append(i / 60);
                stringBuilder = stringBuilder2.toString();
                if (stringBuilder.length() < 2) {
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(stringBuilder);
                    stringBuilder2.append("m");
                    stringBuilder = stringBuilder2.toString();
                }
            }
        } else {
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(str);
            stringBuilder2.append(i);
            stringBuilder = stringBuilder2.toString();
            if (stringBuilder.length() < 2) {
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(stringBuilder);
                stringBuilder2.append("s");
                stringBuilder = stringBuilder2.toString();
            }
        }
        str = stringBuilder;
        this.timeWidth = this.timePaint.measureText(str);
        try {
            this.timeLayout = new StaticLayout(str, this.timePaint, (int) Math.ceil((double) this.timeWidth), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.timeHeight = this.timeLayout.getHeight();
        } catch (Exception e) {
            this.timeLayout = null;
            FileLog.e(e);
        }
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        int intrinsicWidth = getIntrinsicWidth();
        int intrinsicHeight = getIntrinsicHeight();
        String str = "chat_secretTimerBackground";
        String str2 = "chat_secretTimerText";
        if (this.time == 0) {
            this.paint.setColor(Theme.getColor(str));
            this.linePaint.setColor(Theme.getColor(str2));
            canvas.drawCircle(AndroidUtilities.dpf2(9.0f), AndroidUtilities.dpf2(9.0f), AndroidUtilities.dpf2(7.5f), this.paint);
            canvas.drawCircle(AndroidUtilities.dpf2(9.0f), AndroidUtilities.dpf2(9.0f), AndroidUtilities.dpf2(8.0f), this.linePaint);
            this.paint.setColor(Theme.getColor(str2));
            Canvas canvas2 = canvas;
            canvas2.drawLine((float) AndroidUtilities.dp(9.0f), (float) AndroidUtilities.dp(9.0f), (float) AndroidUtilities.dp(13.0f), (float) AndroidUtilities.dp(9.0f), this.linePaint);
            canvas2.drawLine((float) AndroidUtilities.dp(9.0f), (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(9.0f), (float) AndroidUtilities.dp(9.5f), this.linePaint);
            canvas.drawRect(AndroidUtilities.dpf2(7.0f), AndroidUtilities.dpf2(0.0f), AndroidUtilities.dpf2(11.0f), AndroidUtilities.dpf2(1.5f), this.paint);
        } else {
            this.paint.setColor(Theme.getColor(str));
            this.timePaint.setColor(Theme.getColor(str2));
            canvas.drawCircle((float) AndroidUtilities.dp(9.5f), (float) AndroidUtilities.dp(9.5f), (float) AndroidUtilities.dp(9.5f), this.paint);
        }
        if (this.time != 0 && this.timeLayout != null) {
            int i = 0;
            if (AndroidUtilities.density == 3.0f) {
                i = -1;
            }
            double d = (double) (intrinsicWidth / 2);
            double ceil = Math.ceil((double) (this.timeWidth / 2.0f));
            Double.isNaN(d);
            canvas.translate((float) (((int) (d - ceil)) + i), (float) ((intrinsicHeight - this.timeHeight) / 2));
            this.timeLayout.draw(canvas);
        }
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(19.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(19.0f);
    }
}

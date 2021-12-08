package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class TimerDrawable extends Drawable {
    private Paint linePaint = new Paint(1);
    private Paint paint = new Paint(1);
    private int time = 0;
    private int timeHeight = 0;
    private StaticLayout timeLayout;
    private TextPaint timePaint = new TextPaint(1);
    private float timeWidth = 0.0f;

    public TimerDrawable(Context context) {
        this.timePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.timePaint.setTextSize((float) AndroidUtilities.dp(11.0f));
        this.linePaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        this.linePaint.setStyle(Paint.Style.STROKE);
    }

    public void setTime(int value) {
        String timeString;
        this.time = value;
        if (value >= 1 && value < 60) {
            timeString = "" + value;
            if (timeString.length() < 2) {
                timeString = timeString + LocaleController.getString("SecretChatTimerSeconds", NUM);
            }
        } else if (value >= 60 && value < 3600) {
            timeString = "" + (value / 60);
            if (timeString.length() < 2) {
                timeString = timeString + LocaleController.getString("SecretChatTimerMinutes", NUM);
            }
        } else if (value >= 3600 && value < 86400) {
            timeString = "" + ((value / 60) / 60);
            if (timeString.length() < 2) {
                timeString = timeString + LocaleController.getString("SecretChatTimerHours", NUM);
            }
        } else if (value >= 86400 && value < 604800) {
            timeString = "" + (((value / 60) / 60) / 24);
            if (timeString.length() < 2) {
                timeString = timeString + LocaleController.getString("SecretChatTimerDays", NUM);
            }
        } else if (value < 2592000 || value > 2678400) {
            timeString = "" + ((((value / 60) / 60) / 24) / 7);
            if (timeString.length() < 2) {
                timeString = timeString + LocaleController.getString("SecretChatTimerWeeks", NUM);
            } else if (timeString.length() > 2) {
                timeString = "c";
            }
        } else {
            timeString = "" + ((((value / 60) / 60) / 24) / 30);
            if (timeString.length() < 2) {
                timeString = timeString + LocaleController.getString("SecretChatTimerMonths", NUM);
            }
        }
        this.timeWidth = this.timePaint.measureText(timeString);
        try {
            StaticLayout staticLayout = new StaticLayout(timeString, this.timePaint, (int) Math.ceil((double) this.timeWidth), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.timeLayout = staticLayout;
            this.timeHeight = staticLayout.getHeight();
        } catch (Exception e) {
            this.timeLayout = null;
            FileLog.e((Throwable) e);
        }
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        int width = getIntrinsicWidth();
        int height = getIntrinsicHeight();
        if (this.time == 0) {
            this.paint.setColor(Theme.getColor("chat_secretTimerBackground"));
            this.linePaint.setColor(Theme.getColor("chat_secretTimerText"));
            canvas.drawCircle(AndroidUtilities.dpf2(9.0f), AndroidUtilities.dpf2(9.0f), AndroidUtilities.dpf2(7.5f), this.paint);
            canvas.drawCircle(AndroidUtilities.dpf2(9.0f), AndroidUtilities.dpf2(9.0f), AndroidUtilities.dpf2(8.0f), this.linePaint);
            this.paint.setColor(Theme.getColor("chat_secretTimerText"));
            Canvas canvas2 = canvas;
            canvas2.drawLine((float) AndroidUtilities.dp(9.0f), (float) AndroidUtilities.dp(9.0f), (float) AndroidUtilities.dp(13.0f), (float) AndroidUtilities.dp(9.0f), this.linePaint);
            canvas2.drawLine((float) AndroidUtilities.dp(9.0f), (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(9.0f), (float) AndroidUtilities.dp(9.5f), this.linePaint);
            canvas.drawRect(AndroidUtilities.dpf2(7.0f), AndroidUtilities.dpf2(0.0f), AndroidUtilities.dpf2(11.0f), AndroidUtilities.dpf2(1.5f), this.paint);
        } else {
            this.paint.setColor(Theme.getColor("chat_secretTimerBackground"));
            this.timePaint.setColor(Theme.getColor("chat_secretTimerText"));
            canvas.drawCircle((float) AndroidUtilities.dp(9.5f), (float) AndroidUtilities.dp(9.5f), (float) AndroidUtilities.dp(9.5f), this.paint);
        }
        if (this.time != 0 && this.timeLayout != null) {
            int xOffxet = 0;
            if (AndroidUtilities.density == 3.0f) {
                xOffxet = -1;
            }
            double d = (double) (width / 2);
            double ceil = Math.ceil((double) (this.timeWidth / 2.0f));
            Double.isNaN(d);
            canvas.translate((float) (((int) (d - ceil)) + xOffxet), (float) ((height - this.timeHeight) / 2));
            this.timeLayout.draw(canvas);
        }
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
    }

    public int getOpacity() {
        return -2;
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(19.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(19.0f);
    }
}

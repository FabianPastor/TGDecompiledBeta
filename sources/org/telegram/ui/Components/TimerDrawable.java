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

    public int getOpacity() {
        return -2;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public TimerDrawable(Context context) {
        this.timePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.timePaint.setTextSize((float) AndroidUtilities.dp(11.0f));
        this.linePaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        this.linePaint.setStyle(Paint.Style.STROKE);
    }

    public void setTime(int i) {
        String str;
        this.time = i;
        if (i < 1 || i >= 60) {
            int i2 = this.time;
            if (i2 < 60 || i2 >= 3600) {
                int i3 = this.time;
                if (i3 < 3600 || i3 >= 86400) {
                    int i4 = this.time;
                    if (i4 < 86400 || i4 >= 604800) {
                        str = "" + ((((i / 60) / 60) / 24) / 7);
                        if (str.length() < 2) {
                            str = str + LocaleController.getString("SecretChatTimerWeeks", NUM);
                        } else if (str.length() > 2) {
                            str = "c";
                        }
                    } else {
                        str = "" + (((i / 60) / 60) / 24);
                        if (str.length() < 2) {
                            str = str + LocaleController.getString("SecretChatTimerDays", NUM);
                        }
                    }
                } else {
                    str = "" + ((i / 60) / 60);
                    if (str.length() < 2) {
                        str = str + LocaleController.getString("SecretChatTimerHours", NUM);
                    }
                }
            } else {
                str = "" + (i / 60);
                if (str.length() < 2) {
                    str = str + LocaleController.getString("SecretChatTimerMinutes", NUM);
                }
            }
        } else {
            str = "" + i;
            if (str.length() < 2) {
                str = str + LocaleController.getString("SecretChatTimerSeconds", NUM);
            }
        }
        String str2 = str;
        this.timeWidth = this.timePaint.measureText(str2);
        try {
            StaticLayout staticLayout = new StaticLayout(str2, this.timePaint, (int) Math.ceil((double) this.timeWidth), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.timeLayout = staticLayout;
            this.timeHeight = staticLayout.getHeight();
        } catch (Exception e) {
            this.timeLayout = null;
            FileLog.e((Throwable) e);
        }
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        int intrinsicWidth = getIntrinsicWidth();
        int intrinsicHeight = getIntrinsicHeight();
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

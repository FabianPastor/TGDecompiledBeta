package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import androidx.core.content.ContextCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class TimerDrawable extends Drawable {
    Context context;
    ColorFilter currentColorFilter;
    private Drawable currentTtlIcon;
    private int iconColor;
    private boolean isStaticIcon;
    private Paint linePaint = new Paint(1);
    private boolean overrideColor;
    private Paint paint = new Paint(1);
    Theme.ResourcesProvider resourcesProvider;
    private int time = -1;
    private int timeHeight = 0;
    private StaticLayout timeLayout;
    private TextPaint timePaint = new TextPaint(1);
    private float timeWidth = 0.0f;

    public int getOpacity() {
        return -2;
    }

    public void setAlpha(int i) {
    }

    public TimerDrawable(Context context2, Theme.ResourcesProvider resourcesProvider2) {
        this.context = context2;
        this.resourcesProvider = resourcesProvider2;
        this.timePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rcondensedbold.ttf"));
        this.linePaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        this.linePaint.setStyle(Paint.Style.STROKE);
    }

    public void setTime(int i) {
        String str;
        if (this.time != i) {
            this.time = i;
            Drawable mutate = ContextCompat.getDrawable(this.context, i == 0 ? NUM : NUM).mutate();
            this.currentTtlIcon = mutate;
            mutate.setColorFilter(this.currentColorFilter);
            invalidateSelf();
            int i2 = this.time;
            if (i2 >= 1 && i2 < 60) {
                str = "" + i;
                if (str.length() < 2) {
                    str = str + LocaleController.getString("SecretChatTimerSeconds", NUM);
                }
            } else if (i2 >= 60 && i2 < 3600) {
                str = "" + (i / 60);
                if (str.length() < 2) {
                    str = str + LocaleController.getString("SecretChatTimerMinutes", NUM);
                }
            } else if (i2 >= 3600 && i2 < 86400) {
                str = "" + ((i / 60) / 60);
                if (str.length() < 2) {
                    str = str + LocaleController.getString("SecretChatTimerHours", NUM);
                }
            } else if (i2 >= 86400 && i2 < 604800) {
                str = "" + (((i / 60) / 60) / 24);
                if (str.length() < 2) {
                    str = str + LocaleController.getString("SecretChatTimerDays", NUM);
                }
            } else if (i2 < 2678400) {
                str = "" + ((((i / 60) / 60) / 24) / 7);
                if (str.length() < 2) {
                    str = str + LocaleController.getString("SecretChatTimerWeeks", NUM);
                } else if (str.length() > 2) {
                    str = "c";
                }
            } else {
                str = "" + ((((i / 60) / 60) / 24) / 30);
                if (str.length() < 2) {
                    str = str + LocaleController.getString("SecretChatTimerMonths", NUM);
                }
            }
            String str2 = str;
            this.timePaint.setTextSize((float) AndroidUtilities.dp(11.0f));
            float measureText = this.timePaint.measureText(str2);
            this.timeWidth = measureText;
            if (measureText > ((float) AndroidUtilities.dp(13.0f))) {
                this.timePaint.setTextSize((float) AndroidUtilities.dp(9.0f));
                this.timeWidth = this.timePaint.measureText(str2);
            }
            if (this.timeWidth > ((float) AndroidUtilities.dp(13.0f))) {
                this.timePaint.setTextSize((float) AndroidUtilities.dp(6.0f));
                this.timeWidth = this.timePaint.measureText(str2);
            }
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
    }

    public static TimerDrawable getTtlIcon(int i) {
        TimerDrawable timerDrawable = new TimerDrawable(ApplicationLoader.applicationContext, (Theme.ResourcesProvider) null);
        timerDrawable.setTime(i);
        timerDrawable.isStaticIcon = true;
        return timerDrawable;
    }

    public void draw(Canvas canvas) {
        int intrinsicWidth = getIntrinsicWidth();
        int intrinsicHeight = getIntrinsicHeight();
        if (!this.isStaticIcon) {
            if (!this.overrideColor) {
                this.paint.setColor(Theme.getColor("actionBarDefault", this.resourcesProvider));
            }
            this.timePaint.setColor(Theme.getColor("actionBarDefaultTitle", this.resourcesProvider));
        } else {
            this.timePaint.setColor(Theme.getColor("actionBarDefaultSubmenuItemIcon", this.resourcesProvider));
        }
        if (this.currentTtlIcon != null) {
            if (!this.isStaticIcon) {
                canvas.drawCircle((float) getBounds().centerX(), (float) getBounds().centerY(), ((float) getBounds().width()) / 2.0f, this.paint);
                int color = Theme.getColor("actionBarDefaultTitle", this.resourcesProvider);
                if (this.iconColor != color) {
                    this.iconColor = color;
                    this.currentTtlIcon.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                }
            }
            Rect rect = AndroidUtilities.rectTmp2;
            rect.set(getBounds().centerX() - AndroidUtilities.dp(10.5f), getBounds().centerY() - AndroidUtilities.dp(10.5f), (getBounds().centerX() - AndroidUtilities.dp(10.5f)) + this.currentTtlIcon.getIntrinsicWidth(), (getBounds().centerY() - AndroidUtilities.dp(10.5f)) + this.currentTtlIcon.getIntrinsicHeight());
            this.currentTtlIcon.setBounds(rect);
            this.currentTtlIcon.draw(canvas);
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

    public void setColorFilter(ColorFilter colorFilter) {
        this.currentColorFilter = colorFilter;
        if (this.isStaticIcon) {
            this.currentTtlIcon.setColorFilter(colorFilter);
        }
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(23.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(23.0f);
    }

    public void setBackgroundColor(int i) {
        this.overrideColor = true;
        this.paint.setColor(i);
    }
}

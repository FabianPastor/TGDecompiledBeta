package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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
    private int currentTtlIconId;
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

    public TimerDrawable(Context context2, Theme.ResourcesProvider resourcesProvider2) {
        this.context = context2;
        this.resourcesProvider = resourcesProvider2;
        this.timePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rcondensedbold.ttf"));
        this.linePaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        this.linePaint.setStyle(Paint.Style.STROKE);
    }

    public void setTime(int value) {
        String timeString;
        if (this.time != value) {
            this.time = value;
            Drawable mutate = ContextCompat.getDrawable(this.context, value == 0 ? NUM : NUM).mutate();
            this.currentTtlIcon = mutate;
            mutate.setColorFilter(this.currentColorFilter);
            invalidateSelf();
            int i = this.time;
            if (i >= 1 && i < 60) {
                timeString = "" + value;
                if (timeString.length() < 2) {
                    timeString = timeString + LocaleController.getString("SecretChatTimerSeconds", NUM);
                }
            } else if (i >= 60 && i < 3600) {
                timeString = "" + (value / 60);
                if (timeString.length() < 2) {
                    timeString = timeString + LocaleController.getString("SecretChatTimerMinutes", NUM);
                }
            } else if (i >= 3600 && i < 86400) {
                timeString = "" + ((value / 60) / 60);
                if (timeString.length() < 2) {
                    timeString = timeString + LocaleController.getString("SecretChatTimerHours", NUM);
                }
            } else if (i >= 86400 && i < 604800) {
                timeString = "" + (((value / 60) / 60) / 24);
                if (timeString.length() < 2) {
                    timeString = timeString + LocaleController.getString("SecretChatTimerDays", NUM);
                }
            } else if (i < 2678400) {
                timeString = "" + ((((value / 60) / 60) / 24) / 7);
                if (timeString.length() < 2) {
                    timeString = timeString + LocaleController.getString("SecretChatTimerWeeks", NUM);
                } else if (timeString.length() > 2) {
                    timeString = "c";
                }
            } else if (i < 31449600) {
                timeString = "" + ((((value / 60) / 60) / 24) / 30);
                if (timeString.length() < 2) {
                    timeString = timeString + LocaleController.getString("SecretChatTimerMonths", NUM);
                }
            } else {
                timeString = "" + ((((value / 60) / 60) / 24) / 364);
                if (timeString.length() < 2) {
                    timeString = timeString + LocaleController.getString("SecretChatTimerYears", NUM);
                }
            }
            this.timePaint.setTextSize((float) AndroidUtilities.dp(11.0f));
            float measureText = this.timePaint.measureText(timeString);
            this.timeWidth = measureText;
            if (measureText > ((float) AndroidUtilities.dp(13.0f))) {
                this.timePaint.setTextSize((float) AndroidUtilities.dp(9.0f));
                this.timeWidth = this.timePaint.measureText(timeString);
            }
            if (this.timeWidth > ((float) AndroidUtilities.dp(13.0f))) {
                this.timePaint.setTextSize((float) AndroidUtilities.dp(6.0f));
                this.timeWidth = this.timePaint.measureText(timeString);
            }
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
    }

    public static TimerDrawable getTtlIcon(int ttl) {
        TimerDrawable timerDrawable = new TimerDrawable(ApplicationLoader.applicationContext, (Theme.ResourcesProvider) null);
        timerDrawable.setTime(ttl);
        timerDrawable.isStaticIcon = true;
        return timerDrawable;
    }

    public void draw(Canvas canvas) {
        int width = getIntrinsicWidth();
        int height = getIntrinsicHeight();
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
                int iconColor2 = Theme.getColor("actionBarDefaultTitle", this.resourcesProvider);
                if (this.iconColor != iconColor2) {
                    this.iconColor = iconColor2;
                    this.currentTtlIcon.setColorFilter(new PorterDuffColorFilter(iconColor2, PorterDuff.Mode.MULTIPLY));
                }
            }
            AndroidUtilities.rectTmp2.set(getBounds().centerX() - AndroidUtilities.dp(10.5f), getBounds().centerY() - AndroidUtilities.dp(10.5f), (getBounds().centerX() - AndroidUtilities.dp(10.5f)) + this.currentTtlIcon.getIntrinsicWidth(), (getBounds().centerY() - AndroidUtilities.dp(10.5f)) + this.currentTtlIcon.getIntrinsicHeight());
            this.currentTtlIcon.setBounds(AndroidUtilities.rectTmp2);
            this.currentTtlIcon.draw(canvas);
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
        this.currentColorFilter = cf;
        if (this.isStaticIcon) {
            this.currentTtlIcon.setColorFilter(cf);
        }
    }

    public int getOpacity() {
        return -2;
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(23.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(23.0f);
    }

    public void setBackgroundColor(int currentActionBarColor) {
        this.overrideColor = true;
        this.paint.setColor(currentActionBarColor);
    }
}

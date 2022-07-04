package org.telegram.ui.Components.voip;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.voip.VoIPService;

public class VoIPTimerView extends View {
    Paint activePaint = new Paint(1);
    String currentTimeStr;
    Paint inactivePaint = new Paint(1);
    RectF rectF = new RectF();
    private int signalBarCount = 4;
    TextPaint textPaint = new TextPaint(1);
    StaticLayout timerLayout;
    Runnable updater = new VoIPTimerView$$ExternalSyntheticLambda0(this);

    /* renamed from: lambda$new$0$org-telegram-ui-Components-voip-VoIPTimerView  reason: not valid java name */
    public /* synthetic */ void m1622lambda$new$0$orgtelegramuiComponentsvoipVoIPTimerView() {
        if (getVisibility() == 0) {
            updateTimer();
        }
    }

    public VoIPTimerView(Context context) {
        super(context);
        this.textPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
        this.textPaint.setColor(-1);
        this.textPaint.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        this.activePaint.setColor(ColorUtils.setAlphaComponent(-1, 229));
        this.inactivePaint.setColor(ColorUtils.setAlphaComponent(-1, 102));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        StaticLayout timerLayout2 = this.timerLayout;
        if (timerLayout2 != null) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), timerLayout2.getHeight());
        } else {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(15.0f));
        }
    }

    public void updateTimer() {
        removeCallbacks(this.updater);
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            String str = AndroidUtilities.formatLongDuration((int) (service.getCallDuration() / 1000));
            String str2 = this.currentTimeStr;
            if (str2 == null || !str2.equals(str)) {
                this.currentTimeStr = str;
                if (this.timerLayout == null) {
                    requestLayout();
                }
                String str3 = this.currentTimeStr;
                TextPaint textPaint2 = this.textPaint;
                this.timerLayout = new StaticLayout(str3, textPaint2, (int) textPaint2.measureText(str3), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
            postDelayed(this.updater, 300);
            invalidate();
        }
    }

    public void setVisibility(int visibility) {
        if (getVisibility() != visibility) {
            if (visibility == 0) {
                this.currentTimeStr = "00:00";
                String str = this.currentTimeStr;
                TextPaint textPaint2 = this.textPaint;
                this.timerLayout = new StaticLayout(str, textPaint2, (int) textPaint2.measureText(str), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                updateTimer();
            } else {
                this.currentTimeStr = null;
                this.timerLayout = null;
            }
        }
        super.setVisibility(visibility);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        StaticLayout timerLayout2 = this.timerLayout;
        int totalWidth = timerLayout2 == null ? 0 : timerLayout2.getWidth() + AndroidUtilities.dp(21.0f);
        canvas.save();
        canvas.translate(((float) (getMeasuredWidth() - totalWidth)) / 2.0f, 0.0f);
        canvas.save();
        canvas.translate(0.0f, ((float) (getMeasuredHeight() - AndroidUtilities.dp(11.0f))) / 2.0f);
        for (int i = 0; i < 4; i++) {
            Paint p = i + 1 > this.signalBarCount ? this.inactivePaint : this.activePaint;
            this.rectF.set(AndroidUtilities.dpf2(4.16f) * ((float) i), AndroidUtilities.dpf2(2.75f) * ((float) (3 - i)), (AndroidUtilities.dpf2(4.16f) * ((float) i)) + AndroidUtilities.dpf2(2.75f), (float) AndroidUtilities.dp(11.0f));
            canvas.drawRoundRect(this.rectF, AndroidUtilities.dpf2(0.7f), AndroidUtilities.dpf2(0.7f), p);
        }
        canvas.restore();
        if (timerLayout2 != null) {
            canvas.translate((float) AndroidUtilities.dp(21.0f), 0.0f);
            timerLayout2.draw(canvas);
        }
        canvas.restore();
    }

    public void setSignalBarCount(int count) {
        this.signalBarCount = count;
        invalidate();
    }
}

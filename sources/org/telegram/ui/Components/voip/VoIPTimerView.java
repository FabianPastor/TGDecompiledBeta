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
    private int signalBarCount;
    TextPaint textPaint;
    StaticLayout timerLayout;
    Runnable updater;

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$VoIPTimerView() {
        if (getVisibility() == 0) {
            updateTimer();
        }
    }

    public VoIPTimerView(Context context) {
        super(context);
        TextPaint textPaint2 = new TextPaint(1);
        this.textPaint = textPaint2;
        this.signalBarCount = 4;
        this.updater = new Runnable() {
            public final void run() {
                VoIPTimerView.this.lambda$new$0$VoIPTimerView();
            }
        };
        textPaint2.setTextSize((float) AndroidUtilities.dp(15.0f));
        this.textPaint.setColor(-1);
        this.textPaint.setShadowLayer((float) AndroidUtilities.dp(3.0f), 0.0f, (float) AndroidUtilities.dp(0.6666667f), NUM);
        this.activePaint.setColor(ColorUtils.setAlphaComponent(-1, 229));
        this.inactivePaint.setColor(ColorUtils.setAlphaComponent(-1, 102));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        StaticLayout staticLayout = this.timerLayout;
        if (staticLayout != null) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), staticLayout.getHeight());
        } else {
            setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(15.0f));
        }
    }

    public void updateTimer() {
        removeCallbacks(this.updater);
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            String formatLongDuration = AndroidUtilities.formatLongDuration((int) (sharedInstance.getCallDuration() / 1000));
            String str = this.currentTimeStr;
            if (str == null || !str.equals(formatLongDuration)) {
                this.currentTimeStr = formatLongDuration;
                if (this.timerLayout == null) {
                    requestLayout();
                }
                String str2 = this.currentTimeStr;
                TextPaint textPaint2 = this.textPaint;
                this.timerLayout = new StaticLayout(str2, textPaint2, (int) textPaint2.measureText(str2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
            postDelayed(this.updater, 300);
            invalidate();
        }
    }

    public void setVisibility(int i) {
        if (getVisibility() != i) {
            if (i == 0) {
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
        super.setVisibility(i);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i;
        StaticLayout staticLayout = this.timerLayout;
        int i2 = 0;
        if (staticLayout == null) {
            i = 0;
        } else {
            i = staticLayout.getWidth() + AndroidUtilities.dp(21.0f);
        }
        canvas.save();
        canvas.translate(((float) (getMeasuredWidth() - i)) / 2.0f, 0.0f);
        canvas.save();
        canvas.translate(0.0f, ((float) (getMeasuredHeight() - AndroidUtilities.dp(11.0f))) / 2.0f);
        while (i2 < 4) {
            int i3 = i2 + 1;
            Paint paint = i3 > this.signalBarCount ? this.inactivePaint : this.activePaint;
            float f = (float) i2;
            this.rectF.set(AndroidUtilities.dpf2(4.16f) * f, AndroidUtilities.dpf2(2.75f) * ((float) (3 - i2)), (AndroidUtilities.dpf2(4.16f) * f) + AndroidUtilities.dpf2(2.75f), (float) AndroidUtilities.dp(11.0f));
            canvas.drawRoundRect(this.rectF, AndroidUtilities.dpf2(0.7f), AndroidUtilities.dpf2(0.7f), paint);
            i2 = i3;
        }
        canvas.restore();
        if (staticLayout != null) {
            canvas.translate((float) AndroidUtilities.dp(21.0f), 0.0f);
            staticLayout.draw(canvas);
        }
        canvas.restore();
    }

    public void setSignalBarCount(int i) {
        this.signalBarCount = i;
        invalidate();
    }
}

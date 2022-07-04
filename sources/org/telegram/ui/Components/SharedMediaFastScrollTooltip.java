package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import java.util.Random;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class SharedMediaFastScrollTooltip extends FrameLayout {
    public SharedMediaFastScrollTooltip(Context context) {
        super(context);
        TextView textView = new TextView(context);
        textView.setText(LocaleController.getString("SharedMediaFastScrollHint", NUM));
        textView.setTextSize(1, 14.0f);
        textView.setMaxLines(3);
        textView.setTextColor(Theme.getColor("chat_gifSaveHintText"));
        setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("chat_gifSaveHintBackground")));
        addView(textView, LayoutHelper.createFrame(-2, -2.0f, 16, 46.0f, 8.0f, 8.0f, 8.0f));
        addView(new TooltipDrawableView(this, context), LayoutHelper.createFrame(29, 32.0f, 0, 8.0f, 8.0f, 8.0f, 8.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(300.0f), View.MeasureSpec.getSize(i) - AndroidUtilities.dp(32.0f)), Integer.MIN_VALUE), i2);
    }

    private class TooltipDrawableView extends View {
        Paint fadePaint;
        Paint fadePaintBack;
        float fromProgress = 0.0f;
        Paint paint = new Paint(1);
        Paint paint2 = new Paint(1);
        float progress = 1.0f;
        Random random = new Random();
        float toProgress;

        public TooltipDrawableView(SharedMediaFastScrollTooltip sharedMediaFastScrollTooltip, Context context) {
            super(context);
            this.paint.setColor(ColorUtils.setAlphaComponent(Theme.getColor("chat_gifSaveHintText"), 76));
            this.paint2.setColor(Theme.getColor("chat_gifSaveHintText"));
            this.fadePaint = new Paint();
            this.fadePaint.setShader(new LinearGradient(0.0f, (float) AndroidUtilities.dp(4.0f), 0.0f, 0.0f, new int[]{0, -1}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP));
            this.fadePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            this.fadePaintBack = new Paint();
            this.fadePaintBack.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, (float) AndroidUtilities.dp(4.0f), new int[]{0, -1}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP));
            this.fadePaintBack.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            Canvas canvas2 = canvas;
            super.onDraw(canvas);
            canvas.saveLayerAlpha(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), 255, 31);
            int measuredWidth = (getMeasuredWidth() / 2) - AndroidUtilities.dp(3.0f);
            int dp = ((AndroidUtilities.dp(1.0f) + measuredWidth) * 7) + AndroidUtilities.dp(1.0f);
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT;
            float f = this.progress;
            float interpolation = cubicBezierInterpolator.getInterpolation(f > 0.4f ? (f - 0.4f) / 0.6f : 0.0f);
            float f2 = (this.fromProgress * (1.0f - interpolation)) + (this.toProgress * interpolation);
            canvas.save();
            canvas2.translate(0.0f, ((float) (-(dp - (getMeasuredHeight() - AndroidUtilities.dp(4.0f))))) * f2);
            int i = 0;
            for (int i2 = 7; i < i2; i2 = 7) {
                int dp2 = AndroidUtilities.dp(3.0f) + ((AndroidUtilities.dp(1.0f) + measuredWidth) * i);
                RectF rectF = AndroidUtilities.rectTmp;
                float f3 = (float) dp2;
                float f4 = (float) (dp2 + measuredWidth);
                rectF.set(0.0f, f3, (float) measuredWidth, f4);
                canvas2.drawRoundRect(rectF, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), this.paint);
                rectF.set((float) (AndroidUtilities.dp(1.0f) + measuredWidth), f3, (float) (AndroidUtilities.dp(1.0f) + measuredWidth + measuredWidth), f4);
                canvas2.drawRoundRect(rectF, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), this.paint);
                i++;
            }
            canvas.restore();
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.dp(4.0f), this.fadePaint);
            canvas2.translate(0.0f, (float) (getMeasuredHeight() - AndroidUtilities.dp(4.0f)));
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.dp(4.0f), this.fadePaintBack);
            canvas.restore();
            float dp3 = ((float) AndroidUtilities.dp(3.0f)) + (((float) (getMeasuredHeight() - AndroidUtilities.dp(21.0f))) * f2);
            RectF rectF2 = AndroidUtilities.rectTmp;
            rectF2.set((float) (getMeasuredWidth() - AndroidUtilities.dp(3.0f)), dp3, (float) getMeasuredWidth(), ((float) AndroidUtilities.dp(15.0f)) + dp3);
            canvas2.drawRoundRect(rectF2, (float) AndroidUtilities.dp(1.5f), (float) AndroidUtilities.dp(1.5f), this.paint2);
            float centerY = rectF2.centerY();
            float dp4 = (float) (measuredWidth + AndroidUtilities.dp(0.5f));
            rectF2.set(dp4 - ((float) AndroidUtilities.dp(8.0f)), centerY - ((float) AndroidUtilities.dp(3.0f)), dp4 + ((float) AndroidUtilities.dp(8.0f)), centerY + ((float) AndroidUtilities.dp(3.0f)));
            canvas2.drawRoundRect(rectF2, (float) AndroidUtilities.dp(3.0f), (float) AndroidUtilities.dp(3.0f), this.paint2);
            float f5 = this.progress + 0.016f;
            this.progress = f5;
            if (f5 > 1.0f) {
                this.fromProgress = this.toProgress;
                float abs = ((float) Math.abs(this.random.nextInt() % 1001)) / 1000.0f;
                this.toProgress = abs;
                if (abs > this.fromProgress) {
                    this.toProgress = abs + 0.3f;
                } else {
                    this.toProgress = abs - 0.3f;
                }
                this.toProgress = Math.max(0.0f, Math.min(1.0f, this.toProgress));
                this.progress = 0.0f;
            }
            invalidate();
        }
    }
}

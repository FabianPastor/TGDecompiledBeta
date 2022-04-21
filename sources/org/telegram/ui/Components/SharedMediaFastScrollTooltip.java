package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(300.0f), View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(32.0f)), Integer.MIN_VALUE), heightMeasureSpec);
    }

    private class TooltipDrawableView extends View {
        Paint fadePaint;
        Paint fadePaintBack;
        float fromProgress = 0.0f;
        Paint paint = new Paint(1);
        Paint paint2 = new Paint(1);
        float progress = 1.0f;
        Random random = new Random();
        final /* synthetic */ SharedMediaFastScrollTooltip this$0;
        float toProgress;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public TooltipDrawableView(SharedMediaFastScrollTooltip sharedMediaFastScrollTooltip, Context context) {
            super(context);
            this.this$0 = sharedMediaFastScrollTooltip;
            this.paint.setColor(ColorUtils.setAlphaComponent(Theme.getColor("chat_gifSaveHintText"), 76));
            this.paint2.setColor(Theme.getColor("chat_gifSaveHintText"));
            this.fadePaint = new Paint();
            this.fadePaint.setShader(new LinearGradient(0.0f, (float) AndroidUtilities.dp(4.0f), 0.0f, 0.0f, new int[]{0, -1}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP));
            this.fadePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            this.fadePaintBack = new Paint();
            Paint paint3 = this.fadePaintBack;
            paint3.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, (float) AndroidUtilities.dp(4.0f), new int[]{0, -1}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP));
            this.fadePaintBack.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            Canvas canvas2 = canvas;
            super.onDraw(canvas);
            canvas.saveLayerAlpha(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), 255, 31);
            float f = 3.0f;
            int rectSize = (getMeasuredWidth() / 2) - AndroidUtilities.dp(3.0f);
            float f2 = 1.0f;
            int i = 7;
            int totalHeight = ((AndroidUtilities.dp(1.0f) + rectSize) * 7) + AndroidUtilities.dp(1.0f);
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT;
            float f3 = this.progress;
            float progress2 = cubicBezierInterpolator.getInterpolation(f3 > 0.4f ? (f3 - 0.4f) / 0.6f : 0.0f);
            float p = (this.fromProgress * (1.0f - progress2)) + (this.toProgress * progress2);
            canvas.save();
            canvas2.translate(0.0f, ((float) (-(totalHeight - (getMeasuredHeight() - AndroidUtilities.dp(4.0f))))) * p);
            int i2 = 0;
            while (i2 < i) {
                int y = AndroidUtilities.dp(f) + ((AndroidUtilities.dp(f2) + rectSize) * i2);
                AndroidUtilities.rectTmp.set(0.0f, (float) y, (float) rectSize, (float) (y + rectSize));
                canvas2.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), this.paint);
                AndroidUtilities.rectTmp.set((float) (AndroidUtilities.dp(f2) + rectSize), (float) y, (float) (AndroidUtilities.dp(f2) + rectSize + rectSize), (float) (y + rectSize));
                canvas2.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), this.paint);
                i2++;
                i = 7;
                f = 3.0f;
                f2 = 1.0f;
            }
            canvas.restore();
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.dp(4.0f), this.fadePaint);
            canvas2.translate(0.0f, (float) (getMeasuredHeight() - AndroidUtilities.dp(4.0f)));
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.dp(4.0f), this.fadePaintBack);
            canvas.restore();
            float y2 = ((float) AndroidUtilities.dp(3.0f)) + (((float) (getMeasuredHeight() - AndroidUtilities.dp(21.0f))) * p);
            AndroidUtilities.rectTmp.set((float) (getMeasuredWidth() - AndroidUtilities.dp(3.0f)), y2, (float) getMeasuredWidth(), ((float) AndroidUtilities.dp(15.0f)) + y2);
            canvas2.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(1.5f), (float) AndroidUtilities.dp(1.5f), this.paint2);
            float cy = AndroidUtilities.rectTmp.centerY();
            float cx = (float) (AndroidUtilities.dp(0.5f) + rectSize);
            AndroidUtilities.rectTmp.set(cx - ((float) AndroidUtilities.dp(8.0f)), cy - ((float) AndroidUtilities.dp(3.0f)), ((float) AndroidUtilities.dp(8.0f)) + cx, ((float) AndroidUtilities.dp(3.0f)) + cy);
            canvas2.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(3.0f), (float) AndroidUtilities.dp(3.0f), this.paint2);
            float f4 = this.progress + 0.016f;
            this.progress = f4;
            if (f4 > 1.0f) {
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

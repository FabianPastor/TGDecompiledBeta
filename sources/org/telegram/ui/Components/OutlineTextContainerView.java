package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.text.TextPaint;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class OutlineTextContainerView extends FrameLayout {
    private static final SimpleFloatPropertyCompat<OutlineTextContainerView> ERROR_PROGRESS_PROPERTY = new SimpleFloatPropertyCompat("errorProgress", OutlineTextContainerView$$ExternalSyntheticLambda1.INSTANCE, OutlineTextContainerView$$ExternalSyntheticLambda3.INSTANCE).setMultiplier(100.0f);
    private static final int PADDING_LEFT = 14;
    private static final int PADDING_TEXT = 4;
    private static final SimpleFloatPropertyCompat<OutlineTextContainerView> SELECTION_PROGRESS_PROPERTY = new SimpleFloatPropertyCompat("selectionProgress", OutlineTextContainerView$$ExternalSyntheticLambda0.INSTANCE, OutlineTextContainerView$$ExternalSyntheticLambda2.INSTANCE).setMultiplier(100.0f);
    private static final float SPRING_MULTIPLIER = 100.0f;
    private EditText attachedEditText;
    /* access modifiers changed from: private */
    public float errorProgress;
    private SpringAnimation errorSpring = new SpringAnimation(this, ERROR_PROGRESS_PROPERTY);
    private boolean forceUseCenter;
    private String mText = "";
    private Paint outlinePaint = new Paint(1);
    private RectF rect = new RectF();
    /* access modifiers changed from: private */
    public float selectionProgress;
    private SpringAnimation selectionSpring = new SpringAnimation(this, SELECTION_PROGRESS_PROPERTY);
    private float strokeWidthRegular = ((float) Math.max(2, AndroidUtilities.dp(0.5f)));
    private float strokeWidthSelected = ((float) AndroidUtilities.dp(1.5f));
    private TextPaint textPaint = new TextPaint(1);

    static /* synthetic */ void lambda$static$1(OutlineTextContainerView obj, float value) {
        obj.selectionProgress = value;
        if (!obj.forceUseCenter) {
            Paint paint = obj.outlinePaint;
            float f = obj.strokeWidthRegular;
            paint.setStrokeWidth(f + ((obj.strokeWidthSelected - f) * value));
            obj.updateColor();
        }
        obj.invalidate();
    }

    static /* synthetic */ void lambda$static$3(OutlineTextContainerView obj, float value) {
        obj.errorProgress = value;
        obj.updateColor();
    }

    public OutlineTextContainerView(Context context) {
        super(context);
        setWillNotDraw(false);
        this.textPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        this.outlinePaint.setStyle(Paint.Style.STROKE);
        this.outlinePaint.setStrokeCap(Paint.Cap.ROUND);
        this.outlinePaint.setStrokeWidth(this.strokeWidthRegular);
        updateColor();
        setPadding(0, AndroidUtilities.dp(6.0f), 0, 0);
    }

    public void setForceUseCenter(boolean forceUseCenter2) {
        this.forceUseCenter = forceUseCenter2;
        invalidate();
    }

    public EditText getAttachedEditText() {
        return this.attachedEditText;
    }

    public void attachEditText(EditText attachedEditText2) {
        this.attachedEditText = attachedEditText2;
        invalidate();
    }

    public void setText(String text) {
        this.mText = text;
        invalidate();
    }

    private void setColor(int color) {
        this.outlinePaint.setColor(color);
        invalidate();
    }

    public void updateColor() {
        this.textPaint.setColor(ColorUtils.blendARGB(ColorUtils.blendARGB(Theme.getColor("windowBackgroundWhiteHintText"), Theme.getColor("windowBackgroundWhiteValueText"), this.selectionProgress), Theme.getColor("dialogTextRed"), this.errorProgress));
        setColor(ColorUtils.blendARGB(ColorUtils.blendARGB(Theme.getColor("windowBackgroundWhiteInputField"), Theme.getColor("windowBackgroundWhiteInputFieldActivated"), this.selectionProgress), Theme.getColor("dialogTextRed"), this.errorProgress));
    }

    public void animateSelection(float newValue) {
        animateSelection(newValue, true);
    }

    public void animateSelection(float newValue, boolean animate) {
        if (!animate) {
            this.selectionProgress = newValue;
            if (!this.forceUseCenter) {
                Paint paint = this.outlinePaint;
                float f = this.strokeWidthRegular;
                paint.setStrokeWidth(f + ((this.strokeWidthSelected - f) * newValue));
            }
            updateColor();
            return;
        }
        animateSpring(this.selectionSpring, newValue);
    }

    public void animateError(float newValue) {
        animateSpring(this.errorSpring, newValue);
    }

    private void animateSpring(SpringAnimation spring, float newValue) {
        float newValue2 = newValue * 100.0f;
        if (spring.getSpring() == null || newValue2 != spring.getSpring().getFinalPosition()) {
            spring.cancel();
            spring.setSpring(new SpringForce(newValue2).setStiffness(500.0f).setDampingRatio(1.0f).setFinalPosition(newValue2)).start();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        super.onDraw(canvas);
        float textOffset = (this.textPaint.getTextSize() / 2.0f) - ((float) AndroidUtilities.dp(1.75f));
        float topY = ((float) getPaddingTop()) + textOffset;
        float centerY = (((float) getHeight()) / 2.0f) + (this.textPaint.getTextSize() / 2.0f);
        EditText editText = this.attachedEditText;
        boolean useCenter = (editText != null && editText.length() == 0 && TextUtils.isEmpty(this.attachedEditText.getHint())) || this.forceUseCenter;
        float textY = useCenter ? ((centerY - topY) * (1.0f - this.selectionProgress)) + topY : topY;
        float stroke = this.outlinePaint.getStrokeWidth();
        float f = 0.75f;
        if (useCenter) {
            f = 0.75f + ((1.0f - this.selectionProgress) * 0.25f);
        }
        float scaleX = f;
        float textWidth = this.textPaint.measureText(this.mText) * scaleX;
        canvas.save();
        this.rect.set((float) (getPaddingLeft() + AndroidUtilities.dp(10.0f)), (float) getPaddingTop(), (float) ((getWidth() - AndroidUtilities.dp(18.0f)) - getPaddingRight()), ((float) getPaddingTop()) + (stroke * 2.0f));
        canvas2.clipRect(this.rect, Region.Op.DIFFERENCE);
        this.rect.set(((float) getPaddingLeft()) + stroke, ((float) getPaddingTop()) + stroke, (((float) getWidth()) - stroke) - ((float) getPaddingRight()), (((float) getHeight()) - stroke) - ((float) getPaddingBottom()));
        canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.outlinePaint);
        canvas.restore();
        float left = (float) (getPaddingLeft() + AndroidUtilities.dp(10.0f));
        float lineY = ((float) getPaddingTop()) + stroke;
        float fromLeft = left + (textWidth / 2.0f);
        float f2 = textOffset;
        float scaleX2 = scaleX;
        canvas.drawLine(fromLeft + ((((left + textWidth) + ((float) AndroidUtilities.dp(10.0f))) - fromLeft) * (useCenter ? this.selectionProgress : 1.0f)), lineY, ((((float) getWidth()) - stroke) - ((float) getPaddingRight())) - ((float) AndroidUtilities.dp(6.0f)), lineY, this.outlinePaint);
        float fromRight = (textWidth / 2.0f) + left + ((float) AndroidUtilities.dp(4.0f));
        canvas.drawLine(left, lineY, fromRight + ((left - fromRight) * (useCenter ? this.selectionProgress : 1.0f)), lineY, this.outlinePaint);
        canvas.save();
        canvas2.scale(scaleX2, scaleX2, (float) (getPaddingLeft() + AndroidUtilities.dp(18.0f)), textY);
        canvas2.drawText(this.mText, (float) (getPaddingLeft() + AndroidUtilities.dp(14.0f)), textY, this.textPaint);
        canvas.restore();
    }
}

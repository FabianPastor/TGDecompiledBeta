package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.view.animation.LinearInterpolator;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.MessageEnterTransitionContainer;

public class VoiceMessageEnterTransition implements MessageEnterTransitionContainer.Transition {
    private final ValueAnimator animator;
    final Paint circlePaint = new Paint(1);
    MessageEnterTransitionContainer container;
    float fromRadius;
    private final Matrix gradientMatrix;
    private final Paint gradientPaint;
    private final LinearGradient gradientShader;
    float lastToCx;
    float lastToCy;
    private final RecyclerListView listView;
    private final int messageId;
    private final ChatMessageCell messageView;
    float progress;
    /* access modifiers changed from: private */
    public final ChatActivityEnterView.RecordCircle recordCircle;
    private final Theme.ResourcesProvider resourcesProvider;

    public VoiceMessageEnterTransition(final ChatMessageCell chatMessageCell, ChatActivityEnterView chatActivityEnterView, RecyclerListView recyclerListView, final MessageEnterTransitionContainer messageEnterTransitionContainer, Theme.ResourcesProvider resourcesProvider2) {
        this.resourcesProvider = resourcesProvider2;
        this.messageView = chatMessageCell;
        this.container = messageEnterTransitionContainer;
        this.listView = recyclerListView;
        this.fromRadius = chatActivityEnterView.getRecordCicle().drawingCircleRadius;
        chatMessageCell.setEnterTransitionInProgress(true);
        ChatActivityEnterView.RecordCircle recordCicle = chatActivityEnterView.getRecordCicle();
        this.recordCircle = recordCicle;
        recordCicle.voiceEnterTransitionInProgress = true;
        recordCicle.skipDraw = true;
        this.gradientMatrix = new Matrix();
        Paint paint = new Paint(1);
        this.gradientPaint = paint;
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        LinearGradient linearGradient = new LinearGradient(0.0f, (float) AndroidUtilities.dp(12.0f), 0.0f, 0.0f, 0, -16777216, Shader.TileMode.CLAMP);
        this.gradientShader = linearGradient;
        paint.setShader(linearGradient);
        this.messageId = chatMessageCell.getMessageObject().stableId;
        messageEnterTransitionContainer.addTransition(this);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.animator = ofFloat;
        ofFloat.addUpdateListener(new VoiceMessageEnterTransition$$ExternalSyntheticLambda0(this, messageEnterTransitionContainer));
        ofFloat.setInterpolator(new LinearInterpolator());
        ofFloat.setDuration(220);
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                chatMessageCell.setEnterTransitionInProgress(false);
                messageEnterTransitionContainer.removeTransition(VoiceMessageEnterTransition.this);
                VoiceMessageEnterTransition.this.recordCircle.skipDraw = false;
            }
        });
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(MessageEnterTransitionContainer messageEnterTransitionContainer, ValueAnimator valueAnimator) {
        this.progress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        messageEnterTransitionContainer.invalidate();
    }

    public void start() {
        this.animator.start();
    }

    public void onDraw(Canvas canvas) {
        float f;
        float f2;
        float f3;
        float f4;
        int i;
        Canvas canvas2 = canvas;
        float f5 = this.progress;
        float f6 = f5 > 0.6f ? 1.0f : f5 / 0.6f;
        ChatActivityEnterView.RecordCircle recordCircle2 = this.recordCircle;
        float x = (recordCircle2.drawingCx + recordCircle2.getX()) - this.container.getX();
        ChatActivityEnterView.RecordCircle recordCircle3 = this.recordCircle;
        float y = (recordCircle3.drawingCy + recordCircle3.getY()) - this.container.getY();
        if (this.messageView.getMessageObject().stableId != this.messageId) {
            f2 = this.lastToCx;
            f = this.lastToCy;
        } else {
            f = ((this.messageView.getRadialProgress().getProgressRect().centerY() + this.messageView.getY()) + this.listView.getY()) - this.container.getY();
            f2 = ((this.messageView.getRadialProgress().getProgressRect().centerX() + this.messageView.getX()) + this.listView.getX()) - this.container.getX();
        }
        this.lastToCx = f2;
        this.lastToCy = f;
        float interpolation = CubicBezierInterpolator.DEFAULT.getInterpolation(f5);
        float interpolation2 = CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(f5);
        float f7 = ((1.0f - interpolation2) * x) + (f2 * interpolation2);
        float f8 = 1.0f - interpolation;
        float f9 = (y * f8) + (f * interpolation);
        float height = this.messageView.getRadialProgress().getProgressRect().height() / 2.0f;
        float var_ = (this.fromRadius * f8) + (height * interpolation);
        float y2 = (this.listView.getY() - this.container.getY()) + ((float) this.listView.getMeasuredHeight());
        if (this.container.getMeasuredHeight() > 0) {
            int measuredHeight = (int) ((((float) this.container.getMeasuredHeight()) * f8) + (y2 * interpolation));
            f4 = var_;
            f3 = f9;
            canvas.saveLayerAlpha(0.0f, (float) (this.container.getMeasuredHeight() - AndroidUtilities.dp(400.0f)), (float) this.container.getMeasuredWidth(), (float) this.container.getMeasuredHeight(), 255, 31);
            i = measuredHeight;
        } else {
            f4 = var_;
            f3 = f9;
            canvas.save();
            i = 0;
        }
        this.circlePaint.setColor(ColorUtils.blendARGB(getThemedColor("chat_messagePanelVoiceBackground"), getThemedColor(this.messageView.getRadialProgress().getCircleColorKey()), interpolation));
        float var_ = f3;
        this.recordCircle.drawWaves(canvas2, f7, var_, 1.0f - f6);
        float var_ = f4;
        canvas2.drawCircle(f7, var_, var_, this.circlePaint);
        canvas.save();
        float var_ = var_ / height;
        canvas2.scale(var_, var_, f7, var_);
        canvas2.translate(f7 - this.messageView.getRadialProgress().getProgressRect().centerX(), var_ - this.messageView.getRadialProgress().getProgressRect().centerY());
        this.messageView.getRadialProgress().setOverrideAlpha(interpolation);
        this.messageView.getRadialProgress().setDrawBackground(false);
        this.messageView.getRadialProgress().draw(canvas2);
        this.messageView.getRadialProgress().setDrawBackground(true);
        this.messageView.getRadialProgress().setOverrideAlpha(1.0f);
        canvas.restore();
        if (this.container.getMeasuredHeight() > 0) {
            float var_ = (float) i;
            this.gradientMatrix.setTranslate(0.0f, var_);
            this.gradientShader.setLocalMatrix(this.gradientMatrix);
            canvas.drawRect(0.0f, var_, (float) this.container.getMeasuredWidth(), (float) this.container.getMeasuredHeight(), this.gradientPaint);
        }
        canvas.restore();
        this.recordCircle.drawIcon(canvas2, (int) x, (int) y, 1.0f - f5);
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}

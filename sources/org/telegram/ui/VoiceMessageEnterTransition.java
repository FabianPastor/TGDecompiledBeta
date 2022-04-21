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

    public VoiceMessageEnterTransition(ChatMessageCell messageView2, ChatActivityEnterView chatActivityEnterView, RecyclerListView listView2, MessageEnterTransitionContainer container2, Theme.ResourcesProvider resourcesProvider2) {
        final ChatMessageCell chatMessageCell = messageView2;
        final MessageEnterTransitionContainer messageEnterTransitionContainer = container2;
        this.resourcesProvider = resourcesProvider2;
        this.messageView = chatMessageCell;
        this.container = messageEnterTransitionContainer;
        this.listView = listView2;
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
        this.messageId = messageView2.getMessageObject().stableId;
        messageEnterTransitionContainer.addTransition(this);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.animator = ofFloat;
        ofFloat.addUpdateListener(new VoiceMessageEnterTransition$$ExternalSyntheticLambda0(this, messageEnterTransitionContainer));
        ofFloat.setInterpolator(new LinearInterpolator());
        ofFloat.setDuration(220);
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                chatMessageCell.setEnterTransitionInProgress(false);
                messageEnterTransitionContainer.removeTransition(VoiceMessageEnterTransition.this);
                VoiceMessageEnterTransition.this.recordCircle.skipDraw = false;
            }
        });
    }

    /* renamed from: lambda$new$0$org-telegram-ui-VoiceMessageEnterTransition  reason: not valid java name */
    public /* synthetic */ void m3471lambda$new$0$orgtelegramuiVoiceMessageEnterTransition(MessageEnterTransitionContainer container2, ValueAnimator valueAnimator) {
        this.progress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        container2.invalidate();
    }

    public void start() {
        this.animator.start();
    }

    public void onDraw(Canvas canvas) {
        float toCx;
        float toCy;
        float progress2;
        float cy;
        float radius;
        float cx;
        int clipBottom;
        Canvas canvas2 = canvas;
        float moveProgress = this.progress;
        float f = this.progress;
        float hideWavesProgress = f > 0.6f ? 1.0f : f / 0.6f;
        float fromCx = (this.recordCircle.drawingCx + this.recordCircle.getX()) - this.container.getX();
        float fromCy = (this.recordCircle.drawingCy + this.recordCircle.getY()) - this.container.getY();
        if (this.messageView.getMessageObject().stableId != this.messageId) {
            toCx = this.lastToCx;
            toCy = this.lastToCy;
        } else {
            float toCy2 = ((this.messageView.getRadialProgress().getProgressRect().centerY() + this.messageView.getY()) + this.listView.getY()) - this.container.getY();
            toCx = ((this.messageView.getRadialProgress().getProgressRect().centerX() + this.messageView.getX()) + this.listView.getX()) - this.container.getX();
            toCy = toCy2;
        }
        this.lastToCx = toCx;
        this.lastToCy = toCy;
        float progress3 = CubicBezierInterpolator.DEFAULT.getInterpolation(moveProgress);
        float xProgress = CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(moveProgress);
        float cx2 = ((1.0f - xProgress) * fromCx) + (toCx * xProgress);
        float cy2 = ((1.0f - progress3) * fromCy) + (toCy * progress3);
        float toRadius = this.messageView.getRadialProgress().getProgressRect().height() / 2.0f;
        float radius2 = (this.fromRadius * (1.0f - progress3)) + (toRadius * progress3);
        float listViewBottom = (this.listView.getY() - this.container.getY()) + ((float) this.listView.getMeasuredHeight());
        if (this.container.getMeasuredHeight() > 0) {
            radius = radius2;
            cy = cy2;
            cx = cx2;
            progress2 = progress3;
            float f2 = toCy;
            canvas.saveLayerAlpha(0.0f, (float) (this.container.getMeasuredHeight() - AndroidUtilities.dp(400.0f)), (float) this.container.getMeasuredWidth(), (float) this.container.getMeasuredHeight(), 255, 31);
            clipBottom = (int) ((((float) this.container.getMeasuredHeight()) * (1.0f - progress3)) + (listViewBottom * progress3));
        } else {
            radius = radius2;
            cy = cy2;
            cx = cx2;
            progress2 = progress3;
            float f3 = toCy;
            canvas.save();
            clipBottom = 0;
        }
        float progress4 = progress2;
        this.circlePaint.setColor(ColorUtils.blendARGB(getThemedColor("chat_messagePanelVoiceBackground"), getThemedColor(this.messageView.getRadialProgress().getCircleColorKey()), progress4));
        float cy3 = cy;
        this.recordCircle.drawWaves(canvas2, cx, cy3, 1.0f - hideWavesProgress);
        float radius3 = radius;
        canvas2.drawCircle(cx, cy3, radius3, this.circlePaint);
        canvas.save();
        float scale = radius3 / toRadius;
        canvas2.scale(scale, scale, cx, cy3);
        canvas2.translate(cx - this.messageView.getRadialProgress().getProgressRect().centerX(), cy3 - this.messageView.getRadialProgress().getProgressRect().centerY());
        this.messageView.getRadialProgress().setOverrideAlpha(progress4);
        this.messageView.getRadialProgress().setDrawBackground(false);
        this.messageView.getRadialProgress().draw(canvas2);
        this.messageView.getRadialProgress().setDrawBackground(true);
        this.messageView.getRadialProgress().setOverrideAlpha(1.0f);
        canvas.restore();
        if (this.container.getMeasuredHeight() > 0) {
            float f4 = scale;
            this.gradientMatrix.setTranslate(0.0f, (float) clipBottom);
            this.gradientShader.setLocalMatrix(this.gradientMatrix);
            float f5 = radius3;
            float f6 = cy3;
            float f7 = progress4;
            canvas.drawRect(0.0f, (float) clipBottom, (float) this.container.getMeasuredWidth(), (float) this.container.getMeasuredHeight(), this.gradientPaint);
        } else {
            float f8 = radius3;
            float f9 = cy3;
            float var_ = progress4;
        }
        canvas.restore();
        this.recordCircle.drawIcon(canvas2, (int) fromCx, (int) fromCy, 1.0f - moveProgress);
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}

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
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.RecyclerListView;

public class VoiceMessageEnterTransition {
    private final ValueAnimator animator;
    final Paint circlePaint = new Paint(1);
    float fromRadius;
    float progress;

    public VoiceMessageEnterTransition(FrameLayout frameLayout, ChatMessageCell chatMessageCell, ChatActivityEnterView chatActivityEnterView, RecyclerListView recyclerListView) {
        this.fromRadius = chatActivityEnterView.getRecordCicle().drawingCircleRadius;
        chatMessageCell.setVoiceTransitionInProgress(true);
        ChatActivityEnterView.RecordCircle recordCicle = chatActivityEnterView.getRecordCicle();
        chatActivityEnterView.startMessageTransition();
        recordCicle.voiceEnterTransitionInProgress = true;
        recordCicle.skipDraw = true;
        final Matrix matrix = new Matrix();
        final Paint paint = new Paint(1);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        final LinearGradient linearGradient = new LinearGradient(0.0f, (float) AndroidUtilities.dp(12.0f), 0.0f, 0.0f, 0, -16777216, Shader.TileMode.CLAMP);
        paint.setShader(linearGradient);
        final int i = chatMessageCell.getMessageObject().stableId;
        final ChatActivityEnterView.RecordCircle recordCircle = recordCicle;
        final ChatMessageCell chatMessageCell2 = chatMessageCell;
        final RecyclerListView recyclerListView2 = recyclerListView;
        AnonymousClass1 r0 = new View(frameLayout.getContext()) {
            float lastToCx;
            float lastToCy;

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                float f;
                float f2;
                float f3;
                float f4;
                int i;
                Canvas canvas2 = canvas;
                super.onDraw(canvas);
                float f5 = VoiceMessageEnterTransition.this.progress;
                float f6 = f5 > 0.6f ? 1.0f : f5 / 0.6f;
                ChatActivityEnterView.RecordCircle recordCircle = recordCircle;
                float x = (recordCircle.drawingCx + recordCircle.getX()) - getX();
                ChatActivityEnterView.RecordCircle recordCircle2 = recordCircle;
                float y = (recordCircle2.drawingCy + recordCircle2.getY()) - getY();
                if (chatMessageCell2.getMessageObject().stableId != i) {
                    f2 = this.lastToCx;
                    f = this.lastToCy;
                } else {
                    f = ((chatMessageCell2.getRadialProgress().getProgressRect().centerY() + chatMessageCell2.getY()) + recyclerListView2.getY()) - getY();
                    f2 = ((chatMessageCell2.getRadialProgress().getProgressRect().centerX() + chatMessageCell2.getX()) + recyclerListView2.getX()) - getX();
                }
                this.lastToCx = f2;
                this.lastToCy = f;
                float interpolation = CubicBezierInterpolator.DEFAULT.getInterpolation(f5);
                float interpolation2 = CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(f5);
                float f7 = ((1.0f - interpolation2) * x) + (f2 * interpolation2);
                float f8 = 1.0f - interpolation;
                float f9 = (y * f8) + (f * interpolation);
                float height = chatMessageCell2.getRadialProgress().getProgressRect().height() / 2.0f;
                float var_ = (VoiceMessageEnterTransition.this.fromRadius * f8) + (height * interpolation);
                float y2 = (recyclerListView2.getY() - getY()) + ((float) recyclerListView2.getMeasuredHeight());
                if (getMeasuredHeight() > 0) {
                    int measuredHeight = (int) ((((float) getMeasuredHeight()) * f8) + (y2 * interpolation));
                    f4 = var_;
                    f3 = f9;
                    canvas.saveLayerAlpha(0.0f, (float) (getMeasuredHeight() - AndroidUtilities.dp(400.0f)), (float) getMeasuredWidth(), (float) getMeasuredHeight(), 255, 31);
                    i = measuredHeight;
                } else {
                    f4 = var_;
                    f3 = f9;
                    canvas.save();
                    i = 0;
                }
                VoiceMessageEnterTransition.this.circlePaint.setColor(ColorUtils.blendARGB(Theme.getColor("chat_messagePanelVoiceBackground"), Theme.getColor(chatMessageCell2.getRadialProgress().getCircleColorKey()), interpolation));
                float var_ = f3;
                recordCircle.drawWaves(canvas2, f7, var_, 1.0f - f6);
                float var_ = f4;
                canvas2.drawCircle(f7, var_, var_, VoiceMessageEnterTransition.this.circlePaint);
                canvas.save();
                float var_ = var_ / height;
                canvas2.scale(var_, var_, f7, var_);
                canvas2.translate(f7 - chatMessageCell2.getRadialProgress().getProgressRect().centerX(), var_ - chatMessageCell2.getRadialProgress().getProgressRect().centerY());
                chatMessageCell2.getRadialProgress().setOverrideAlpha(interpolation);
                chatMessageCell2.getRadialProgress().setDrawBackground(false);
                chatMessageCell2.getRadialProgress().draw(canvas2);
                chatMessageCell2.getRadialProgress().setDrawBackground(true);
                chatMessageCell2.getRadialProgress().setOverrideAlpha(1.0f);
                canvas.restore();
                if (getMeasuredHeight() > 0) {
                    float var_ = (float) i;
                    matrix.setTranslate(0.0f, var_);
                    linearGradient.setLocalMatrix(matrix);
                    canvas.drawRect(0.0f, var_, (float) getMeasuredWidth(), (float) getMeasuredHeight(), paint);
                }
                canvas.restore();
                recordCircle.drawIcon(canvas2, (int) x, (int) y, 1.0f - f5);
                recordCircle.skipDraw = false;
                canvas.save();
                canvas2.translate(recordCircle.getX() - getX(), recordCircle.getY() - getY());
                recordCircle.draw(canvas2);
                canvas.restore();
                recordCircle.skipDraw = true;
            }
        };
        final FrameLayout frameLayout2 = frameLayout;
        frameLayout2.addView(r0);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.animator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(r0) {
            public final /* synthetic */ View f$1;

            {
                this.f$1 = r2;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                VoiceMessageEnterTransition.this.lambda$new$0$VoiceMessageEnterTransition(this.f$1, valueAnimator);
            }
        });
        ofFloat.setInterpolator(new LinearInterpolator());
        ofFloat.setDuration(220);
        final AnonymousClass1 r2 = r0;
        final ChatMessageCell chatMessageCell3 = chatMessageCell;
        final ChatActivityEnterView.RecordCircle recordCircle2 = recordCicle;
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (r2.getParent() != null) {
                    chatMessageCell3.setVoiceTransitionInProgress(false);
                    frameLayout2.removeView(r2);
                    recordCircle2.skipDraw = false;
                }
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$VoiceMessageEnterTransition(View view, ValueAnimator valueAnimator) {
        this.progress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        view.invalidate();
    }

    public void start() {
        this.animator.start();
    }
}

package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewConfiguration;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class AcceptDeclineView extends View {
    private Paint acceptCirclePaint = new Paint(1);
    private FabBackgroundDrawable acceptDrawable;
    private StaticLayout acceptLayout;
    Rect acceptRect = new Rect();
    float bigRadius;
    private int buttonWidth;
    private Drawable callDrawable;
    private Drawable cancelDrawable;
    boolean captured;
    private FabBackgroundDrawable declineDrawable;
    private StaticLayout declineLayout;
    Rect declineRect = new Rect();
    boolean expandBigRadius = true;
    boolean expandSmallRadius = true;
    Animator leftAnimator;
    boolean leftDrag;
    float leftOffsetX;
    Listener listener;
    float maxOffset;
    private StaticLayout retryLayout;
    boolean retryMod;
    Animator rightAnimator;
    float rigthOffsetX;
    Drawable rippleDrawable;
    float smallRadius;
    boolean startDrag;
    float startX;
    float startY;
    float touchSlop;

    public interface Listener {
        void onAccept();

        void onDicline();
    }

    public AcceptDeclineView(Context context) {
        super(context);
        this.touchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
        this.buttonWidth = AndroidUtilities.dp(70.0f);
        FabBackgroundDrawable fabBackgroundDrawable = new FabBackgroundDrawable();
        this.acceptDrawable = fabBackgroundDrawable;
        fabBackgroundDrawable.setColor(-12207027);
        FabBackgroundDrawable fabBackgroundDrawable2 = new FabBackgroundDrawable();
        this.declineDrawable = fabBackgroundDrawable2;
        fabBackgroundDrawable2.setColor(-1696188);
        FabBackgroundDrawable fabBackgroundDrawable3 = this.declineDrawable;
        int i = this.buttonWidth;
        fabBackgroundDrawable3.setBounds(0, 0, i, i);
        FabBackgroundDrawable fabBackgroundDrawable4 = this.acceptDrawable;
        int i2 = this.buttonWidth;
        fabBackgroundDrawable4.setBounds(0, 0, i2, i2);
        TextPaint textPaint = new TextPaint(1);
        textPaint.setTextSize((float) AndroidUtilities.dp(11.0f));
        textPaint.setColor(-1);
        TextPaint textPaint2 = textPaint;
        this.acceptLayout = new StaticLayout("Accept", textPaint2, (int) textPaint.measureText("Accept"), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        this.declineLayout = new StaticLayout("Decline", textPaint2, (int) textPaint.measureText("Decline"), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        this.retryLayout = new StaticLayout("Retry", textPaint2, (int) textPaint.measureText("Retry"), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        this.callDrawable = ContextCompat.getDrawable(context, NUM).mutate();
        Drawable mutate = ContextCompat.getDrawable(context, NUM).mutate();
        this.cancelDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
        this.acceptCirclePaint.setColor(NUM);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(52.0f), 0, ColorUtils.setAlphaComponent(-1, 76));
        this.rippleDrawable = createSimpleSelectorCircleDrawable;
        createSimpleSelectorCircleDrawable.setCallback(this);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.maxOffset = (((float) getMeasuredWidth()) / 2.0f) - ((((float) this.buttonWidth) / 2.0f) + ((float) AndroidUtilities.dp(46.0f)));
        int dp = (this.buttonWidth - AndroidUtilities.dp(28.0f)) / 2;
        this.callDrawable.setBounds(dp, dp, AndroidUtilities.dp(28.0f) + dp, AndroidUtilities.dp(28.0f) + dp);
        this.cancelDrawable.setBounds(dp, dp, AndroidUtilities.dp(28.0f) + dp, AndroidUtilities.dp(28.0f) + dp);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x000f, code lost:
        if (r0 != 3) goto L_0x0179;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r12) {
        /*
            r11 = this;
            int r0 = r12.getAction()
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L_0x012a
            r3 = 2
            r4 = 0
            if (r0 == r2) goto L_0x0093
            if (r0 == r3) goto L_0x0013
            r5 = 3
            if (r0 == r5) goto L_0x0093
            goto L_0x0179
        L_0x0013:
            boolean r0 = r11.captured
            if (r0 == 0) goto L_0x0179
            float r0 = r12.getX()
            float r3 = r11.startX
            float r0 = r0 - r3
            boolean r3 = r11.startDrag
            if (r3 != 0) goto L_0x003f
            float r3 = java.lang.Math.abs(r0)
            float r5 = r11.touchSlop
            int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r3 <= 0) goto L_0x003f
            float r12 = r12.getX()
            r11.startX = r12
            r11.startDrag = r2
            r11.setPressed(r1)
            android.view.ViewParent r12 = r11.getParent()
            r12.requestDisallowInterceptTouchEvent(r2)
            r0 = 0
        L_0x003f:
            boolean r12 = r11.startDrag
            if (r12 == 0) goto L_0x0092
            boolean r12 = r11.leftDrag
            if (r12 == 0) goto L_0x006c
            r11.leftOffsetX = r0
            int r12 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r12 >= 0) goto L_0x0050
            r11.leftOffsetX = r4
            goto L_0x0092
        L_0x0050:
            float r12 = r11.maxOffset
            int r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r0 <= 0) goto L_0x0092
            r11.leftOffsetX = r12
            long r3 = android.os.SystemClock.uptimeMillis()
            long r5 = android.os.SystemClock.uptimeMillis()
            r7 = 1
            r8 = 0
            r9 = 0
            r10 = 0
            android.view.MotionEvent r12 = android.view.MotionEvent.obtain(r3, r5, r7, r8, r9, r10)
            r11.dispatchTouchEvent(r12)
            goto L_0x0092
        L_0x006c:
            r11.rigthOffsetX = r0
            int r12 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r12 <= 0) goto L_0x0075
            r11.rigthOffsetX = r4
            goto L_0x0092
        L_0x0075:
            float r12 = r11.maxOffset
            float r1 = -r12
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x0092
            float r12 = -r12
            r11.rigthOffsetX = r12
            long r3 = android.os.SystemClock.uptimeMillis()
            long r5 = android.os.SystemClock.uptimeMillis()
            r7 = 1
            r8 = 0
            r9 = 0
            r10 = 0
            android.view.MotionEvent r12 = android.view.MotionEvent.obtain(r3, r5, r7, r8, r9, r10)
            r11.dispatchTouchEvent(r12)
        L_0x0092:
            return r2
        L_0x0093:
            float r12 = r12.getY()
            float r0 = r11.startY
            float r12 = r12 - r0
            boolean r0 = r11.captured
            if (r0 == 0) goto L_0x011b
            boolean r0 = r11.leftDrag
            r5 = 1061997773(0x3f4ccccd, float:0.8)
            if (r0 == 0) goto L_0x00e0
            float[] r0 = new float[r3]
            float r3 = r11.leftOffsetX
            r0[r1] = r3
            r0[r2] = r4
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
            org.telegram.ui.Components.voip.-$$Lambda$AcceptDeclineView$z6Onz1j2xHKE1brlfczii8ntp14 r2 = new org.telegram.ui.Components.voip.-$$Lambda$AcceptDeclineView$z6Onz1j2xHKE1brlfczii8ntp14
            r2.<init>()
            r0.addUpdateListener(r2)
            r0.start()
            r11.leftAnimator = r0
            org.telegram.ui.Components.voip.AcceptDeclineView$Listener r0 = r11.listener
            if (r0 == 0) goto L_0x011b
            boolean r0 = r11.startDrag
            if (r0 != 0) goto L_0x00d0
            float r12 = java.lang.Math.abs(r12)
            float r0 = r11.touchSlop
            int r12 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1))
            if (r12 < 0) goto L_0x00da
        L_0x00d0:
            float r12 = r11.leftOffsetX
            float r0 = r11.maxOffset
            float r0 = r0 * r5
            int r12 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1))
            if (r12 <= 0) goto L_0x011b
        L_0x00da:
            org.telegram.ui.Components.voip.AcceptDeclineView$Listener r12 = r11.listener
            r12.onDicline()
            goto L_0x011b
        L_0x00e0:
            float[] r0 = new float[r3]
            float r3 = r11.rigthOffsetX
            r0[r1] = r3
            r0[r2] = r4
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
            org.telegram.ui.Components.voip.-$$Lambda$AcceptDeclineView$v1R-WPl_eAn67uRSKCTITsDpzwY r2 = new org.telegram.ui.Components.voip.-$$Lambda$AcceptDeclineView$v1R-WPl_eAn67uRSKCTITsDpzwY
            r2.<init>()
            r0.addUpdateListener(r2)
            r0.start()
            r11.rightAnimator = r0
            org.telegram.ui.Components.voip.AcceptDeclineView$Listener r0 = r11.listener
            if (r0 == 0) goto L_0x011b
            boolean r0 = r11.startDrag
            if (r0 != 0) goto L_0x010b
            float r12 = java.lang.Math.abs(r12)
            float r0 = r11.touchSlop
            int r12 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1))
            if (r12 < 0) goto L_0x0116
        L_0x010b:
            float r12 = r11.rigthOffsetX
            float r12 = -r12
            float r0 = r11.maxOffset
            float r0 = r0 * r5
            int r12 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1))
            if (r12 <= 0) goto L_0x011b
        L_0x0116:
            org.telegram.ui.Components.voip.AcceptDeclineView$Listener r12 = r11.listener
            r12.onAccept()
        L_0x011b:
            android.view.ViewParent r12 = r11.getParent()
            r12.requestDisallowInterceptTouchEvent(r1)
            r11.captured = r1
            r11.startDrag = r1
            r11.setPressed(r1)
            goto L_0x0179
        L_0x012a:
            float r0 = r12.getX()
            r11.startX = r0
            float r0 = r12.getY()
            r11.startY = r0
            android.animation.Animator r0 = r11.leftAnimator
            if (r0 != 0) goto L_0x0154
            android.graphics.Rect r0 = r11.declineRect
            float r3 = r12.getX()
            int r3 = (int) r3
            float r4 = r12.getY()
            int r4 = (int) r4
            boolean r0 = r0.contains(r3, r4)
            if (r0 == 0) goto L_0x0154
            r11.captured = r2
            r11.leftDrag = r2
            r11.setPressed(r2)
            return r2
        L_0x0154:
            android.animation.Animator r0 = r11.rightAnimator
            if (r0 != 0) goto L_0x0179
            android.graphics.Rect r0 = r11.acceptRect
            float r3 = r12.getX()
            int r3 = (int) r3
            float r12 = r12.getY()
            int r12 = (int) r12
            boolean r12 = r0.contains(r3, r12)
            if (r12 == 0) goto L_0x0179
            r11.captured = r2
            r11.leftDrag = r1
            r11.setPressed(r2)
            android.animation.Animator r12 = r11.rightAnimator
            if (r12 == 0) goto L_0x0178
            r12.cancel()
        L_0x0178:
            return r2
        L_0x0179:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.AcceptDeclineView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public /* synthetic */ void lambda$onTouchEvent$0$AcceptDeclineView(ValueAnimator valueAnimator) {
        this.leftOffsetX = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        this.leftAnimator = null;
    }

    public /* synthetic */ void lambda$onTouchEvent$1$AcceptDeclineView(ValueAnimator valueAnimator) {
        this.rigthOffsetX = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        this.rightAnimator = null;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (!this.retryMod) {
            if (this.expandSmallRadius) {
                float dp = this.smallRadius + (((float) AndroidUtilities.dp(2.0f)) * 0.04f);
                this.smallRadius = dp;
                if (dp > ((float) AndroidUtilities.dp(4.0f))) {
                    this.smallRadius = (float) AndroidUtilities.dp(4.0f);
                    this.expandSmallRadius = false;
                }
            } else {
                float dp2 = this.smallRadius - (((float) AndroidUtilities.dp(2.0f)) * 0.04f);
                this.smallRadius = dp2;
                if (dp2 < 0.0f) {
                    this.smallRadius = 0.0f;
                    this.expandSmallRadius = true;
                }
            }
            if (this.expandBigRadius) {
                float dp3 = this.bigRadius + (((float) AndroidUtilities.dp(4.0f)) * 0.03f);
                this.bigRadius = dp3;
                if (dp3 > ((float) AndroidUtilities.dp(10.0f))) {
                    this.bigRadius = (float) AndroidUtilities.dp(10.0f);
                    this.expandBigRadius = false;
                }
            } else {
                float dp4 = this.bigRadius - (((float) AndroidUtilities.dp(5.0f)) * 0.03f);
                this.bigRadius = dp4;
                if (dp4 < ((float) AndroidUtilities.dp(5.0f))) {
                    this.bigRadius = (float) AndroidUtilities.dp(5.0f);
                    this.expandBigRadius = true;
                }
            }
            invalidate();
        }
        this.bigRadius += ((float) AndroidUtilities.dp(8.0f)) * 0.005f;
        canvas.save();
        canvas.translate(0.0f, (float) AndroidUtilities.dp(40.0f));
        canvas.save();
        canvas.translate(this.leftOffsetX + ((float) AndroidUtilities.dp(46.0f)), 0.0f);
        this.declineDrawable.draw(canvas);
        canvas.save();
        canvas.translate((((float) this.buttonWidth) / 2.0f) - (((float) this.declineLayout.getWidth()) / 2.0f), (float) (this.buttonWidth + AndroidUtilities.dp(8.0f)));
        this.declineLayout.draw(canvas);
        this.declineRect.set(AndroidUtilities.dp(46.0f), AndroidUtilities.dp(40.0f), AndroidUtilities.dp(46.0f) + this.buttonWidth, AndroidUtilities.dp(40.0f) + this.buttonWidth);
        canvas.restore();
        if (this.retryMod) {
            this.cancelDrawable.draw(canvas);
        } else {
            this.callDrawable.draw(canvas);
        }
        if (this.leftDrag) {
            this.rippleDrawable.setBounds(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), this.buttonWidth - AndroidUtilities.dp(4.0f), this.buttonWidth - AndroidUtilities.dp(4.0f));
            this.rippleDrawable.draw(canvas);
        }
        canvas.restore();
        canvas.save();
        canvas.translate(((this.rigthOffsetX + ((float) getMeasuredWidth())) - ((float) AndroidUtilities.dp(46.0f))) - ((float) this.buttonWidth), 0.0f);
        if (!this.retryMod) {
            int i = this.buttonWidth;
            canvas.drawCircle(((float) i) / 2.0f, ((float) i) / 2.0f, ((((float) i) / 2.0f) - ((float) AndroidUtilities.dp(4.0f))) + this.bigRadius, this.acceptCirclePaint);
            int i2 = this.buttonWidth;
            canvas.drawCircle(((float) i2) / 2.0f, ((float) i2) / 2.0f, ((((float) i2) / 2.0f) - ((float) AndroidUtilities.dp(4.0f))) + this.smallRadius, this.acceptCirclePaint);
        }
        this.acceptDrawable.draw(canvas);
        this.acceptRect.set((getMeasuredWidth() - AndroidUtilities.dp(46.0f)) - this.buttonWidth, AndroidUtilities.dp(40.0f), getMeasuredWidth() - AndroidUtilities.dp(46.0f), AndroidUtilities.dp(40.0f) + this.buttonWidth);
        if (!this.leftDrag) {
            this.rippleDrawable.setBounds(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), this.buttonWidth - AndroidUtilities.dp(4.0f), this.buttonWidth - AndroidUtilities.dp(4.0f));
            this.rippleDrawable.draw(canvas);
        }
        if (this.retryMod) {
            canvas.save();
            canvas.translate((((float) this.buttonWidth) / 2.0f) - (((float) this.retryLayout.getWidth()) / 2.0f), (float) (this.buttonWidth + AndroidUtilities.dp(8.0f)));
            this.retryLayout.draw(canvas);
            canvas.restore();
        } else {
            canvas.save();
            canvas.translate((((float) this.buttonWidth) / 2.0f) - (((float) this.acceptLayout.getWidth()) / 2.0f), (float) (this.buttonWidth + AndroidUtilities.dp(8.0f)));
            this.acceptLayout.draw(canvas);
            canvas.restore();
        }
        canvas.save();
        int i3 = this.buttonWidth;
        canvas.rotate(-135.0f, ((float) i3) / 2.0f, ((float) i3) / 2.0f);
        this.callDrawable.draw(canvas);
        canvas.restore();
        canvas.restore();
        canvas.restore();
    }

    public void setListener(Listener listener2) {
        this.listener = listener2;
    }

    public void setRetryMod(boolean z) {
        this.retryMod = z;
        if (z) {
            this.declineDrawable.setColor(-1);
        } else {
            this.declineDrawable.setColor(-1696188);
        }
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        this.rippleDrawable.setState(getDrawableState());
    }

    public boolean verifyDrawable(Drawable drawable) {
        return this.rippleDrawable == drawable || super.verifyDrawable(drawable);
    }

    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.rippleDrawable;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }
}

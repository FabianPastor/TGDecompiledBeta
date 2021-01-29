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
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.widget.Button;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class AcceptDeclineView extends View {
    private Paint acceptCirclePaint = new Paint(1);
    private FabBackgroundDrawable acceptDrawable;
    /* access modifiers changed from: private */
    public StaticLayout acceptLayout;
    Rect acceptRect = new Rect();
    private AcceptDeclineAccessibilityNodeProvider accessibilityNodeProvider;
    Drawable arrowDrawable;
    float arrowProgress;
    float bigRadius;
    private int buttonWidth;
    private Drawable callDrawable;
    private Drawable cancelDrawable;
    boolean captured;
    private FabBackgroundDrawable declineDrawable;
    /* access modifiers changed from: private */
    public StaticLayout declineLayout;
    Rect declineRect = new Rect();
    boolean expandBigRadius = true;
    boolean expandSmallRadius = true;
    Animator leftAnimator;
    boolean leftDrag;
    float leftOffsetX;
    Paint linePaint = new Paint(1);
    Listener listener;
    float maxOffset;
    /* access modifiers changed from: private */
    public StaticLayout retryLayout;
    boolean retryMod;
    Animator rightAnimator;
    float rigthOffsetX;
    Drawable rippleDrawable;
    private boolean screenWasWakeup;
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
        this.buttonWidth = AndroidUtilities.dp(60.0f);
        FabBackgroundDrawable fabBackgroundDrawable = new FabBackgroundDrawable();
        this.acceptDrawable = fabBackgroundDrawable;
        fabBackgroundDrawable.setColor(-12531895);
        FabBackgroundDrawable fabBackgroundDrawable2 = new FabBackgroundDrawable();
        this.declineDrawable = fabBackgroundDrawable2;
        fabBackgroundDrawable2.setColor(-1041108);
        FabBackgroundDrawable fabBackgroundDrawable3 = this.declineDrawable;
        int i = this.buttonWidth;
        fabBackgroundDrawable3.setBounds(0, 0, i, i);
        FabBackgroundDrawable fabBackgroundDrawable4 = this.acceptDrawable;
        int i2 = this.buttonWidth;
        fabBackgroundDrawable4.setBounds(0, 0, i2, i2);
        TextPaint textPaint = new TextPaint(1);
        textPaint.setTextSize((float) AndroidUtilities.dp(11.0f));
        textPaint.setColor(-1);
        String string = LocaleController.getString("AcceptCall", NUM);
        String string2 = LocaleController.getString("DeclineCall", NUM);
        String string3 = LocaleController.getString("RetryCall", NUM);
        TextPaint textPaint2 = textPaint;
        this.acceptLayout = new StaticLayout(string, textPaint2, (int) textPaint.measureText(string), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        this.declineLayout = new StaticLayout(string2, textPaint2, (int) textPaint.measureText(string2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        this.retryLayout = new StaticLayout(string3, textPaint2, (int) textPaint.measureText(string3), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        this.callDrawable = ContextCompat.getDrawable(context, NUM).mutate();
        Drawable mutate = ContextCompat.getDrawable(context, NUM).mutate();
        this.cancelDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
        this.acceptCirclePaint.setColor(NUM);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(52.0f), 0, ColorUtils.setAlphaComponent(-1, 76));
        this.rippleDrawable = createSimpleSelectorCircleDrawable;
        createSimpleSelectorCircleDrawable.setCallback(this);
        this.arrowDrawable = ContextCompat.getDrawable(context, NUM);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.maxOffset = (((float) getMeasuredWidth()) / 2.0f) - ((((float) this.buttonWidth) / 2.0f) + ((float) AndroidUtilities.dp(46.0f)));
        int dp = (this.buttonWidth - AndroidUtilities.dp(28.0f)) / 2;
        this.callDrawable.setBounds(dp, dp, AndroidUtilities.dp(28.0f) + dp, AndroidUtilities.dp(28.0f) + dp);
        this.cancelDrawable.setBounds(dp, dp, AndroidUtilities.dp(28.0f) + dp, AndroidUtilities.dp(28.0f) + dp);
        this.linePaint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        this.linePaint.setColor(-1);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0016, code lost:
        if (r0 != 3) goto L_0x01ae;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r12) {
        /*
            r11 = this;
            boolean r0 = r11.isEnabled()
            r1 = 0
            if (r0 != 0) goto L_0x0008
            return r1
        L_0x0008:
            int r0 = r12.getAction()
            r2 = 1
            if (r0 == 0) goto L_0x0143
            r3 = 2
            r4 = 0
            if (r0 == r2) goto L_0x00a4
            if (r0 == r3) goto L_0x001a
            r5 = 3
            if (r0 == r5) goto L_0x00a4
            goto L_0x01ae
        L_0x001a:
            boolean r0 = r11.captured
            if (r0 == 0) goto L_0x01ae
            float r0 = r12.getX()
            float r3 = r11.startX
            float r0 = r0 - r3
            boolean r3 = r11.startDrag
            if (r3 != 0) goto L_0x0050
            float r3 = java.lang.Math.abs(r0)
            float r5 = r11.touchSlop
            int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r3 <= 0) goto L_0x0050
            boolean r3 = r11.retryMod
            if (r3 != 0) goto L_0x004b
            float r12 = r12.getX()
            r11.startX = r12
            r11.startDrag = r2
            r11.setPressed(r1)
            android.view.ViewParent r12 = r11.getParent()
            r12.requestDisallowInterceptTouchEvent(r2)
            r0 = 0
            goto L_0x0050
        L_0x004b:
            r11.setPressed(r1)
            r11.captured = r1
        L_0x0050:
            boolean r12 = r11.startDrag
            if (r12 == 0) goto L_0x00a3
            boolean r12 = r11.leftDrag
            if (r12 == 0) goto L_0x007d
            r11.leftOffsetX = r0
            int r12 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r12 >= 0) goto L_0x0061
            r11.leftOffsetX = r4
            goto L_0x00a3
        L_0x0061:
            float r12 = r11.maxOffset
            int r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r0 <= 0) goto L_0x00a3
            r11.leftOffsetX = r12
            long r3 = android.os.SystemClock.uptimeMillis()
            long r5 = android.os.SystemClock.uptimeMillis()
            r7 = 1
            r8 = 0
            r9 = 0
            r10 = 0
            android.view.MotionEvent r12 = android.view.MotionEvent.obtain(r3, r5, r7, r8, r9, r10)
            r11.dispatchTouchEvent(r12)
            goto L_0x00a3
        L_0x007d:
            r11.rigthOffsetX = r0
            int r12 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r12 <= 0) goto L_0x0086
            r11.rigthOffsetX = r4
            goto L_0x00a3
        L_0x0086:
            float r12 = r11.maxOffset
            float r1 = -r12
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x00a3
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
        L_0x00a3:
            return r2
        L_0x00a4:
            float r12 = r12.getY()
            float r0 = r11.startY
            float r12 = r12 - r0
            boolean r0 = r11.captured
            if (r0 == 0) goto L_0x0134
            boolean r0 = r11.leftDrag
            r5 = 1061997773(0x3f4ccccd, float:0.8)
            if (r0 == 0) goto L_0x00f5
            float[] r0 = new float[r3]
            float r3 = r11.leftOffsetX
            r0[r1] = r3
            r0[r2] = r4
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
            org.telegram.ui.Components.voip.-$$Lambda$AcceptDeclineView$XEKpzvhtApUfBXFcAbsBBZXtimc r2 = new org.telegram.ui.Components.voip.-$$Lambda$AcceptDeclineView$XEKpzvhtApUfBXFcAbsBBZXtimc
            r2.<init>()
            r0.addUpdateListener(r2)
            r0.start()
            r11.leftAnimator = r0
            org.telegram.ui.Components.voip.AcceptDeclineView$Listener r0 = r11.listener
            if (r0 == 0) goto L_0x0134
            boolean r0 = r11.startDrag
            if (r0 != 0) goto L_0x00e5
            float r12 = java.lang.Math.abs(r12)
            float r0 = r11.touchSlop
            int r12 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1))
            if (r12 >= 0) goto L_0x00e5
            boolean r12 = r11.screenWasWakeup
            if (r12 == 0) goto L_0x00ef
        L_0x00e5:
            float r12 = r11.leftOffsetX
            float r0 = r11.maxOffset
            float r0 = r0 * r5
            int r12 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1))
            if (r12 <= 0) goto L_0x0134
        L_0x00ef:
            org.telegram.ui.Components.voip.AcceptDeclineView$Listener r12 = r11.listener
            r12.onDicline()
            goto L_0x0134
        L_0x00f5:
            float[] r0 = new float[r3]
            float r3 = r11.rigthOffsetX
            r0[r1] = r3
            r0[r2] = r4
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
            org.telegram.ui.Components.voip.-$$Lambda$AcceptDeclineView$lk93wO5tJWrNgRQERin5X2GqM8o r2 = new org.telegram.ui.Components.voip.-$$Lambda$AcceptDeclineView$lk93wO5tJWrNgRQERin5X2GqM8o
            r2.<init>()
            r0.addUpdateListener(r2)
            r0.start()
            r11.rightAnimator = r0
            org.telegram.ui.Components.voip.AcceptDeclineView$Listener r0 = r11.listener
            if (r0 == 0) goto L_0x0134
            boolean r0 = r11.startDrag
            if (r0 != 0) goto L_0x0124
            float r12 = java.lang.Math.abs(r12)
            float r0 = r11.touchSlop
            int r12 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1))
            if (r12 >= 0) goto L_0x0124
            boolean r12 = r11.screenWasWakeup
            if (r12 == 0) goto L_0x012f
        L_0x0124:
            float r12 = r11.rigthOffsetX
            float r12 = -r12
            float r0 = r11.maxOffset
            float r0 = r0 * r5
            int r12 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1))
            if (r12 <= 0) goto L_0x0134
        L_0x012f:
            org.telegram.ui.Components.voip.AcceptDeclineView$Listener r12 = r11.listener
            r12.onAccept()
        L_0x0134:
            android.view.ViewParent r12 = r11.getParent()
            r12.requestDisallowInterceptTouchEvent(r1)
            r11.captured = r1
            r11.startDrag = r1
            r11.setPressed(r1)
            goto L_0x01ae
        L_0x0143:
            float r0 = r12.getX()
            r11.startX = r0
            float r0 = r12.getY()
            r11.startY = r0
            android.animation.Animator r0 = r11.leftAnimator
            r3 = 1112539136(0x42500000, float:52.0)
            if (r0 != 0) goto L_0x017c
            android.graphics.Rect r0 = r11.declineRect
            float r4 = r12.getX()
            int r4 = (int) r4
            float r5 = r12.getY()
            int r5 = (int) r5
            boolean r0 = r0.contains(r4, r5)
            if (r0 == 0) goto L_0x017c
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r0 = -51130(0xfffffffffffvar_, float:NaN)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r12, r1, r0)
            r11.rippleDrawable = r12
            r11.captured = r2
            r11.leftDrag = r2
            r11.setPressed(r2)
            return r2
        L_0x017c:
            android.animation.Animator r0 = r11.rightAnimator
            if (r0 != 0) goto L_0x01ae
            android.graphics.Rect r0 = r11.acceptRect
            float r4 = r12.getX()
            int r4 = (int) r4
            float r12 = r12.getY()
            int r12 = (int) r12
            boolean r12 = r0.contains(r4, r12)
            if (r12 == 0) goto L_0x01ae
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r0 = -11677354(0xffffffffff4dd156, float:-2.7357867E38)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r12, r1, r0)
            r11.rippleDrawable = r12
            r11.captured = r2
            r11.leftDrag = r1
            r11.setPressed(r2)
            android.animation.Animator r12 = r11.rightAnimator
            if (r12 == 0) goto L_0x01ad
            r12.cancel()
        L_0x01ad:
            return r2
        L_0x01ae:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.AcceptDeclineView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onTouchEvent$0 */
    public /* synthetic */ void lambda$onTouchEvent$0$AcceptDeclineView(ValueAnimator valueAnimator) {
        this.leftOffsetX = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        this.leftAnimator = null;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onTouchEvent$1 */
    public /* synthetic */ void lambda$onTouchEvent$1$AcceptDeclineView(ValueAnimator valueAnimator) {
        this.rigthOffsetX = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        this.rightAnimator = null;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float f;
        Canvas canvas2 = canvas;
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
        float f2 = 0.6f;
        if (this.screenWasWakeup && !this.retryMod) {
            float f3 = this.arrowProgress + 0.010666667f;
            this.arrowProgress = f3;
            if (f3 > 1.0f) {
                this.arrowProgress = 0.0f;
            }
            int dp5 = (int) (((float) AndroidUtilities.dp(40.0f)) + (((float) this.buttonWidth) / 2.0f));
            float dp6 = (float) (AndroidUtilities.dp(46.0f) + this.buttonWidth + AndroidUtilities.dp(8.0f));
            float measuredWidth = (((float) getMeasuredWidth()) / 2.0f) - ((float) AndroidUtilities.dp(8.0f));
            float dp7 = (float) AndroidUtilities.dp(10.0f);
            float f4 = 0.13333333f;
            int i = 0;
            while (i < 3) {
                float f5 = (float) i;
                float f6 = ((((measuredWidth - dp6) - dp7) / 3.0f) * f5) + dp6;
                int i2 = (int) f6;
                float f7 = f5 * f4;
                float f8 = this.arrowProgress;
                float f9 = 0.5f;
                if (f8 <= f7 || f8 >= f7 + f2) {
                    f = dp7;
                } else {
                    float var_ = (f8 - f7) / f2;
                    f = dp7;
                    if (((double) var_) > 0.5d) {
                        var_ = 1.0f - var_;
                    }
                    f9 = var_ + 0.5f;
                }
                canvas.save();
                canvas2.clipRect(this.leftOffsetX + ((float) AndroidUtilities.dp(46.0f)) + ((float) (this.buttonWidth / 2)), 0.0f, (float) getMeasuredHeight(), (float) (getMeasuredWidth() >> 1));
                this.arrowDrawable.setAlpha((int) (255.0f * f9));
                Drawable drawable = this.arrowDrawable;
                drawable.setBounds(i2, dp5 - (drawable.getIntrinsicHeight() / 2), this.arrowDrawable.getIntrinsicWidth() + i2, (this.arrowDrawable.getIntrinsicHeight() / 2) + dp5);
                this.arrowDrawable.draw(canvas2);
                canvas.restore();
                int measuredWidth2 = (int) (((float) getMeasuredWidth()) - f6);
                canvas.save();
                canvas2.clipRect((float) (getMeasuredWidth() >> 1), 0.0f, ((this.rigthOffsetX + ((float) getMeasuredWidth())) - ((float) AndroidUtilities.dp(46.0f))) - ((float) (this.buttonWidth / 2)), (float) getMeasuredHeight());
                canvas2.rotate(180.0f, ((float) measuredWidth2) - (((float) this.arrowDrawable.getIntrinsicWidth()) / 2.0f), (float) dp5);
                Drawable drawable2 = this.arrowDrawable;
                drawable2.setBounds(measuredWidth2 - drawable2.getIntrinsicWidth(), dp5 - (this.arrowDrawable.getIntrinsicHeight() / 2), measuredWidth2, (this.arrowDrawable.getIntrinsicHeight() / 2) + dp5);
                this.arrowDrawable.draw(canvas2);
                canvas.restore();
                i++;
                dp7 = f;
                f2 = 0.6f;
                f4 = 0.13333333f;
            }
            invalidate();
        }
        this.bigRadius += ((float) AndroidUtilities.dp(8.0f)) * 0.005f;
        canvas.save();
        canvas2.translate(0.0f, (float) AndroidUtilities.dp(40.0f));
        canvas.save();
        canvas2.translate(this.leftOffsetX + ((float) AndroidUtilities.dp(46.0f)), 0.0f);
        this.declineDrawable.draw(canvas2);
        canvas.save();
        canvas2.translate((((float) this.buttonWidth) / 2.0f) - (((float) this.declineLayout.getWidth()) / 2.0f), (float) (this.buttonWidth + AndroidUtilities.dp(8.0f)));
        this.declineLayout.draw(canvas2);
        this.declineRect.set(AndroidUtilities.dp(46.0f), AndroidUtilities.dp(40.0f), AndroidUtilities.dp(46.0f) + this.buttonWidth, AndroidUtilities.dp(40.0f) + this.buttonWidth);
        canvas.restore();
        if (this.retryMod) {
            this.cancelDrawable.draw(canvas2);
        } else {
            this.callDrawable.draw(canvas2);
        }
        if (this.leftDrag) {
            this.rippleDrawable.setBounds(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), this.buttonWidth - AndroidUtilities.dp(4.0f), this.buttonWidth - AndroidUtilities.dp(4.0f));
            this.rippleDrawable.draw(canvas2);
        }
        canvas.restore();
        canvas.save();
        canvas2.translate(((this.rigthOffsetX + ((float) getMeasuredWidth())) - ((float) AndroidUtilities.dp(46.0f))) - ((float) this.buttonWidth), 0.0f);
        if (!this.retryMod) {
            int i3 = this.buttonWidth;
            canvas2.drawCircle(((float) i3) / 2.0f, ((float) i3) / 2.0f, ((((float) i3) / 2.0f) - ((float) AndroidUtilities.dp(4.0f))) + this.bigRadius, this.acceptCirclePaint);
            int i4 = this.buttonWidth;
            canvas2.drawCircle(((float) i4) / 2.0f, ((float) i4) / 2.0f, ((((float) i4) / 2.0f) - ((float) AndroidUtilities.dp(4.0f))) + this.smallRadius, this.acceptCirclePaint);
        }
        this.acceptDrawable.draw(canvas2);
        this.acceptRect.set((getMeasuredWidth() - AndroidUtilities.dp(46.0f)) - this.buttonWidth, AndroidUtilities.dp(40.0f), getMeasuredWidth() - AndroidUtilities.dp(46.0f), AndroidUtilities.dp(40.0f) + this.buttonWidth);
        if (this.retryMod) {
            canvas.save();
            canvas2.translate((((float) this.buttonWidth) / 2.0f) - (((float) this.retryLayout.getWidth()) / 2.0f), (float) (this.buttonWidth + AndroidUtilities.dp(8.0f)));
            this.retryLayout.draw(canvas2);
            canvas.restore();
        } else {
            canvas.save();
            canvas2.translate((((float) this.buttonWidth) / 2.0f) - (((float) this.acceptLayout.getWidth()) / 2.0f), (float) (this.buttonWidth + AndroidUtilities.dp(8.0f)));
            this.acceptLayout.draw(canvas2);
            canvas.restore();
        }
        canvas.save();
        canvas2.translate((float) (-AndroidUtilities.dp(1.0f)), (float) AndroidUtilities.dp(1.0f));
        canvas2.rotate(-135.0f, (float) this.callDrawable.getBounds().centerX(), (float) this.callDrawable.getBounds().centerY());
        this.callDrawable.draw(canvas2);
        canvas.restore();
        if (!this.leftDrag) {
            this.rippleDrawable.setBounds(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), this.buttonWidth - AndroidUtilities.dp(4.0f), this.buttonWidth - AndroidUtilities.dp(4.0f));
            this.rippleDrawable.draw(canvas2);
        }
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
            this.screenWasWakeup = false;
            return;
        }
        this.declineDrawable.setColor(-1696188);
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

    public boolean onHoverEvent(MotionEvent motionEvent) {
        AcceptDeclineAccessibilityNodeProvider acceptDeclineAccessibilityNodeProvider = this.accessibilityNodeProvider;
        if (acceptDeclineAccessibilityNodeProvider == null || !acceptDeclineAccessibilityNodeProvider.onHoverEvent(motionEvent)) {
            return super.onHoverEvent(motionEvent);
        }
        return true;
    }

    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        if (this.accessibilityNodeProvider == null) {
            this.accessibilityNodeProvider = new AcceptDeclineAccessibilityNodeProvider(this, 2) {
                private final int[] coords = {0, 0};

                /* access modifiers changed from: protected */
                public CharSequence getVirtualViewText(int i) {
                    if (i == 0) {
                        AcceptDeclineView acceptDeclineView = AcceptDeclineView.this;
                        if (acceptDeclineView.retryMod) {
                            if (acceptDeclineView.retryLayout != null) {
                                return AcceptDeclineView.this.retryLayout.getText();
                            }
                            return null;
                        } else if (acceptDeclineView.acceptLayout != null) {
                            return AcceptDeclineView.this.acceptLayout.getText();
                        } else {
                            return null;
                        }
                    } else if (i != 1 || AcceptDeclineView.this.declineLayout == null) {
                        return null;
                    } else {
                        return AcceptDeclineView.this.declineLayout.getText();
                    }
                }

                /* access modifiers changed from: protected */
                public void getVirtualViewBoundsInScreen(int i, Rect rect) {
                    getVirtualViewBoundsInParent(i, rect);
                    AcceptDeclineView.this.getLocationOnScreen(this.coords);
                    int[] iArr = this.coords;
                    rect.offset(iArr[0], iArr[1]);
                }

                /* access modifiers changed from: protected */
                public void getVirtualViewBoundsInParent(int i, Rect rect) {
                    if (i == 0) {
                        rect.set(AcceptDeclineView.this.acceptRect);
                    } else if (i == 1) {
                        rect.set(AcceptDeclineView.this.declineRect);
                    } else {
                        rect.setEmpty();
                    }
                }
            };
        }
        return this.accessibilityNodeProvider;
    }

    public void setScreenWasWakeup(boolean z) {
        this.screenWasWakeup = z;
    }

    private static abstract class AcceptDeclineAccessibilityNodeProvider extends AccessibilityNodeProvider {
        private final AccessibilityManager accessibilityManager;
        private int currentFocusedVirtualViewId;
        private final View hostView;
        private final Rect rect;
        private final int virtualViewsCount;

        /* access modifiers changed from: protected */
        public abstract void getVirtualViewBoundsInParent(int i, Rect rect2);

        /* access modifiers changed from: protected */
        public abstract void getVirtualViewBoundsInScreen(int i, Rect rect2);

        /* access modifiers changed from: protected */
        public abstract CharSequence getVirtualViewText(int i);

        private AcceptDeclineAccessibilityNodeProvider(View view, int i) {
            this.rect = new Rect();
            this.currentFocusedVirtualViewId = -1;
            this.hostView = view;
            this.virtualViewsCount = i;
            this.accessibilityManager = (AccessibilityManager) ContextCompat.getSystemService(view.getContext(), AccessibilityManager.class);
        }

        public AccessibilityNodeInfo createAccessibilityNodeInfo(int i) {
            if (i == -1) {
                AccessibilityNodeInfo obtain = AccessibilityNodeInfo.obtain(this.hostView);
                obtain.setPackageName(this.hostView.getContext().getPackageName());
                for (int i2 = 0; i2 < this.virtualViewsCount; i2++) {
                    obtain.addChild(this.hostView, i2);
                }
                return obtain;
            }
            AccessibilityNodeInfo obtain2 = AccessibilityNodeInfo.obtain(this.hostView, i);
            obtain2.setPackageName(this.hostView.getContext().getPackageName());
            int i3 = Build.VERSION.SDK_INT;
            if (i3 >= 21) {
                obtain2.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
            }
            obtain2.setText(getVirtualViewText(i));
            obtain2.setClassName(Button.class.getName());
            if (i3 >= 24) {
                obtain2.setImportantForAccessibility(true);
            }
            obtain2.setVisibleToUser(true);
            obtain2.setClickable(true);
            obtain2.setEnabled(true);
            obtain2.setParent(this.hostView);
            getVirtualViewBoundsInScreen(i, this.rect);
            obtain2.setBoundsInScreen(this.rect);
            return obtain2;
        }

        public boolean performAction(int i, int i2, Bundle bundle) {
            if (i == -1) {
                return this.hostView.performAccessibilityAction(i2, bundle);
            }
            if (i2 != 64) {
                return false;
            }
            sendAccessibilityEventForVirtualView(i, 32768);
            return false;
        }

        public boolean onHoverEvent(MotionEvent motionEvent) {
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            if (motionEvent.getAction() == 9 || motionEvent.getAction() == 7) {
                for (int i = 0; i < this.virtualViewsCount; i++) {
                    getVirtualViewBoundsInParent(i, this.rect);
                    if (this.rect.contains(x, y)) {
                        if (i != this.currentFocusedVirtualViewId) {
                            this.currentFocusedVirtualViewId = i;
                            sendAccessibilityEventForVirtualView(i, 32768);
                        }
                        return true;
                    }
                }
            } else if (motionEvent.getAction() == 10 && this.currentFocusedVirtualViewId != -1) {
                this.currentFocusedVirtualViewId = -1;
                return true;
            }
            return false;
        }

        private void sendAccessibilityEventForVirtualView(int i, int i2) {
            ViewParent parent;
            if (this.accessibilityManager.isTouchExplorationEnabled() && (parent = this.hostView.getParent()) != null) {
                AccessibilityEvent obtain = AccessibilityEvent.obtain(i2);
                obtain.setPackageName(this.hostView.getContext().getPackageName());
                obtain.setSource(this.hostView, i);
                parent.requestSendAccessibilityEvent(this.hostView, obtain);
            }
        }
    }
}

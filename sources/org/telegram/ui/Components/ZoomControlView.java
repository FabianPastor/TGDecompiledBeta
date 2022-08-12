package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.Components.AnimationProperties;

public class ZoomControlView extends View {
    public final Property<ZoomControlView, Float> ZOOM_PROPERTY = new AnimationProperties.FloatProperty<ZoomControlView>("clipProgress") {
        public void setValue(ZoomControlView zoomControlView, float f) {
            float unused = ZoomControlView.this.zoom = f;
            if (ZoomControlView.this.delegate != null) {
                ZoomControlView.this.delegate.didSetZoom(ZoomControlView.this.zoom);
            }
            ZoomControlView.this.invalidate();
        }

        public Float get(ZoomControlView zoomControlView) {
            return Float.valueOf(ZoomControlView.this.zoom);
        }
    };
    private float animatingToZoom;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    /* access modifiers changed from: private */
    public ZoomControlViewDelegate delegate;
    private Drawable filledProgressDrawable;
    private Drawable knobDrawable;
    private boolean knobPressed;
    private float knobStartX;
    private float knobStartY;
    private int minusCx;
    private int minusCy;
    private Drawable minusDrawable;
    private int plusCx;
    private int plusCy;
    private Drawable plusDrawable;
    private boolean pressed;
    private Drawable pressedKnobDrawable;
    private Drawable progressDrawable;
    private int progressEndX;
    private int progressEndY;
    private int progressStartX;
    private int progressStartY;
    /* access modifiers changed from: private */
    public float zoom;

    public interface ZoomControlViewDelegate {
        void didSetZoom(float f);
    }

    public ZoomControlView(Context context) {
        super(context);
        this.minusDrawable = context.getResources().getDrawable(R.drawable.zoom_minus);
        this.plusDrawable = context.getResources().getDrawable(R.drawable.zoom_plus);
        this.progressDrawable = context.getResources().getDrawable(R.drawable.zoom_slide);
        this.filledProgressDrawable = context.getResources().getDrawable(R.drawable.zoom_slide_a);
        this.knobDrawable = context.getResources().getDrawable(R.drawable.zoom_round);
        this.pressedKnobDrawable = context.getResources().getDrawable(R.drawable.zoom_round_b);
    }

    public float getZoom() {
        if (this.animatorSet != null) {
            return this.animatingToZoom;
        }
        return this.zoom;
    }

    public void setZoom(float f, boolean z) {
        ZoomControlViewDelegate zoomControlViewDelegate;
        if (f != this.zoom) {
            if (f < 0.0f) {
                f = 0.0f;
            } else if (f > 1.0f) {
                f = 1.0f;
            }
            this.zoom = f;
            if (z && (zoomControlViewDelegate = this.delegate) != null) {
                zoomControlViewDelegate.didSetZoom(f);
            }
            invalidate();
        }
    }

    public void setDelegate(ZoomControlViewDelegate zoomControlViewDelegate) {
        this.delegate = zoomControlViewDelegate;
    }

    /* JADX WARNING: Removed duplicated region for block: B:89:0x01da  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x01f1 A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:98:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r15) {
        /*
            r14 = this;
            float r0 = r15.getX()
            float r1 = r15.getY()
            int r2 = r15.getAction()
            int r3 = r14.getMeasuredWidth()
            int r4 = r14.getMeasuredHeight()
            r5 = 0
            r6 = 1
            if (r3 <= r4) goto L_0x001a
            r3 = 1
            goto L_0x001b
        L_0x001a:
            r3 = 0
        L_0x001b:
            int r4 = r14.progressStartX
            float r7 = (float) r4
            int r8 = r14.progressEndX
            int r9 = r8 - r4
            float r9 = (float) r9
            float r10 = r14.zoom
            float r9 = r9 * r10
            float r7 = r7 + r9
            int r7 = (int) r7
            int r9 = r14.progressStartY
            float r11 = (float) r9
            int r12 = r14.progressEndY
            int r13 = r12 - r9
            float r13 = (float) r13
            float r13 = r13 * r10
            float r11 = r11 + r13
            int r10 = (int) r11
            if (r2 == r6) goto L_0x0078
            if (r2 != 0) goto L_0x003a
            goto L_0x0078
        L_0x003a:
            r7 = 2
            if (r2 != r7) goto L_0x01d7
            boolean r7 = r14.knobPressed
            if (r7 == 0) goto L_0x01d7
            if (r3 == 0) goto L_0x004e
            float r1 = r14.knobStartX
            float r0 = r0 + r1
            float r1 = (float) r4
            float r0 = r0 - r1
            int r8 = r8 - r4
            float r1 = (float) r8
            float r0 = r0 / r1
            r14.zoom = r0
            goto L_0x0058
        L_0x004e:
            float r0 = r14.knobStartY
            float r1 = r1 + r0
            float r0 = (float) r9
            float r1 = r1 - r0
            int r12 = r12 - r9
            float r0 = (float) r12
            float r1 = r1 / r0
            r14.zoom = r1
        L_0x0058:
            float r0 = r14.zoom
            r1 = 0
            int r3 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r3 >= 0) goto L_0x0062
            r14.zoom = r1
            goto L_0x006a
        L_0x0062:
            r1 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x006a
            r14.zoom = r1
        L_0x006a:
            org.telegram.ui.Components.ZoomControlView$ZoomControlViewDelegate r0 = r14.delegate
            if (r0 == 0) goto L_0x0073
            float r1 = r14.zoom
            r0.didSetZoom(r1)
        L_0x0073:
            r14.invalidate()
            goto L_0x01d7
        L_0x0078:
            r4 = 1101004800(0x41a00000, float:20.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r8 = r7 - r8
            float r8 = (float) r8
            int r8 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
            if (r8 < 0) goto L_0x00b8
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = r4 + r7
            float r4 = (float) r4
            int r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r4 > 0) goto L_0x00b8
            r4 = 1103626240(0x41CLASSNAME, float:25.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r8 = r10 - r8
            float r8 = (float) r8
            int r8 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
            if (r8 < 0) goto L_0x00b8
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = r4 + r10
            float r4 = (float) r4
            int r4 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r4 > 0) goto L_0x00b8
            if (r2 != 0) goto L_0x00b5
            r14.knobPressed = r6
            float r3 = (float) r7
            float r0 = r0 - r3
            r14.knobStartX = r0
            float r0 = (float) r10
            float r1 = r1 - r0
            r14.knobStartY = r1
            r14.invalidate()
        L_0x00b5:
            r0 = 1
            goto L_0x01d8
        L_0x00b8:
            int r4 = r14.minusCx
            r7 = 1098907648(0x41800000, float:16.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 - r8
            float r4 = (float) r4
            r8 = 3
            r9 = 1048576000(0x3e800000, float:0.25)
            int r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r4 < 0) goto L_0x010a
            int r4 = r14.minusCx
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 + r10
            float r4 = (float) r4
            int r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r4 > 0) goto L_0x010a
            int r4 = r14.minusCy
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 - r10
            float r4 = (float) r4
            int r4 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r4 < 0) goto L_0x010a
            int r4 = r14.minusCy
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 + r10
            float r4 = (float) r4
            int r4 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r4 > 0) goto L_0x010a
            if (r2 != r6) goto L_0x0107
            float r0 = r14.getZoom()
            float r0 = r0 / r9
            double r0 = (double) r0
            double r0 = java.lang.Math.floor(r0)
            float r0 = (float) r0
            float r0 = r0 * r9
            float r0 = r0 - r9
            boolean r0 = r14.animateToZoom(r0)
            if (r0 == 0) goto L_0x0107
            r14.performHapticFeedback(r8)
            goto L_0x00b5
        L_0x0107:
            r14.pressed = r6
            goto L_0x00b5
        L_0x010a:
            int r4 = r14.plusCx
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 - r10
            float r4 = (float) r4
            int r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r4 < 0) goto L_0x0159
            int r4 = r14.plusCx
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 + r10
            float r4 = (float) r4
            int r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r4 > 0) goto L_0x0159
            int r4 = r14.plusCy
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 - r10
            float r4 = (float) r4
            int r4 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r4 < 0) goto L_0x0159
            int r4 = r14.plusCy
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r4 = r4 + r7
            float r4 = (float) r4
            int r4 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r4 > 0) goto L_0x0159
            if (r2 != r6) goto L_0x0155
            float r0 = r14.getZoom()
            float r0 = r0 / r9
            double r0 = (double) r0
            double r0 = java.lang.Math.floor(r0)
            float r0 = (float) r0
            float r0 = r0 * r9
            float r0 = r0 + r9
            boolean r0 = r14.animateToZoom(r0)
            if (r0 == 0) goto L_0x0155
            r14.performHapticFeedback(r8)
            goto L_0x00b5
        L_0x0155:
            r14.pressed = r6
            goto L_0x00b5
        L_0x0159:
            r4 = 1092616192(0x41200000, float:10.0)
            if (r3 == 0) goto L_0x019a
            int r1 = r14.progressStartX
            float r1 = (float) r1
            int r1 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r1 < 0) goto L_0x01d7
            int r1 = r14.progressEndX
            float r1 = (float) r1
            int r1 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r1 > 0) goto L_0x01d7
            if (r2 != 0) goto L_0x0173
            r14.knobStartX = r0
            r14.pressed = r6
            goto L_0x00b5
        L_0x0173:
            float r1 = r14.knobStartX
            float r1 = r1 - r0
            float r1 = java.lang.Math.abs(r1)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 > 0) goto L_0x00b5
            int r1 = r14.progressStartX
            float r3 = (float) r1
            float r0 = r0 - r3
            int r3 = r14.progressEndX
            int r3 = r3 - r1
            float r1 = (float) r3
            float r0 = r0 / r1
            r14.zoom = r0
            org.telegram.ui.Components.ZoomControlView$ZoomControlViewDelegate r1 = r14.delegate
            if (r1 == 0) goto L_0x0195
            r1.didSetZoom(r0)
        L_0x0195:
            r14.invalidate()
            goto L_0x00b5
        L_0x019a:
            int r0 = r14.progressStartY
            float r0 = (float) r0
            int r0 = (r1 > r0 ? 1 : (r1 == r0 ? 0 : -1))
            if (r0 < 0) goto L_0x01d7
            int r0 = r14.progressEndY
            float r0 = (float) r0
            int r0 = (r1 > r0 ? 1 : (r1 == r0 ? 0 : -1))
            if (r0 > 0) goto L_0x01d7
            if (r2 != r6) goto L_0x01b0
            r14.knobStartY = r1
            r14.pressed = r6
            goto L_0x00b5
        L_0x01b0:
            float r0 = r14.knobStartY
            float r0 = r0 - r1
            float r0 = java.lang.Math.abs(r0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r3 = (float) r3
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 > 0) goto L_0x00b5
            int r0 = r14.progressStartY
            float r3 = (float) r0
            float r1 = r1 - r3
            int r3 = r14.progressEndY
            int r3 = r3 - r0
            float r0 = (float) r3
            float r1 = r1 / r0
            r14.zoom = r1
            org.telegram.ui.Components.ZoomControlView$ZoomControlViewDelegate r0 = r14.delegate
            if (r0 == 0) goto L_0x01d2
            r0.didSetZoom(r1)
        L_0x01d2:
            r14.invalidate()
            goto L_0x00b5
        L_0x01d7:
            r0 = 0
        L_0x01d8:
            if (r2 != r6) goto L_0x01e1
            r14.pressed = r5
            r14.knobPressed = r5
            r14.invalidate()
        L_0x01e1:
            if (r0 != 0) goto L_0x01f1
            boolean r0 = r14.pressed
            if (r0 != 0) goto L_0x01f1
            boolean r0 = r14.knobPressed
            if (r0 != 0) goto L_0x01f1
            boolean r15 = super.onTouchEvent(r15)
            if (r15 == 0) goto L_0x01f2
        L_0x01f1:
            r5 = 1
        L_0x01f2:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ZoomControlView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private boolean animateToZoom(float f) {
        if (f < 0.0f || f > 1.0f) {
            return false;
        }
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
        this.animatingToZoom = f;
        AnimatorSet animatorSet3 = new AnimatorSet();
        this.animatorSet = animatorSet3;
        animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, this.ZOOM_PROPERTY, new float[]{f})});
        this.animatorSet.setDuration(180);
        this.animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                AnimatorSet unused = ZoomControlView.this.animatorSet = null;
            }
        });
        this.animatorSet.start();
        return true;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int measuredWidth = getMeasuredWidth() / 2;
        int measuredHeight = getMeasuredHeight() / 2;
        boolean z = getMeasuredWidth() > getMeasuredHeight();
        if (z) {
            this.minusCx = AndroidUtilities.dp(41.0f);
            this.minusCy = measuredHeight;
            this.plusCx = getMeasuredWidth() - AndroidUtilities.dp(41.0f);
            this.plusCy = measuredHeight;
            this.progressStartX = this.minusCx + AndroidUtilities.dp(18.0f);
            this.progressStartY = measuredHeight;
            this.progressEndX = this.plusCx - AndroidUtilities.dp(18.0f);
            this.progressEndY = measuredHeight;
        } else {
            this.minusCx = measuredWidth;
            this.minusCy = AndroidUtilities.dp(41.0f);
            this.plusCx = measuredWidth;
            this.plusCy = getMeasuredHeight() - AndroidUtilities.dp(41.0f);
            this.progressStartX = measuredWidth;
            this.progressStartY = this.minusCy + AndroidUtilities.dp(18.0f);
            this.progressEndX = measuredWidth;
            this.progressEndY = this.plusCy - AndroidUtilities.dp(18.0f);
        }
        this.minusDrawable.setBounds(this.minusCx - AndroidUtilities.dp(7.0f), this.minusCy - AndroidUtilities.dp(7.0f), this.minusCx + AndroidUtilities.dp(7.0f), this.minusCy + AndroidUtilities.dp(7.0f));
        this.minusDrawable.draw(canvas);
        this.plusDrawable.setBounds(this.plusCx - AndroidUtilities.dp(7.0f), this.plusCy - AndroidUtilities.dp(7.0f), this.plusCx + AndroidUtilities.dp(7.0f), this.plusCy + AndroidUtilities.dp(7.0f));
        this.plusDrawable.draw(canvas);
        int i = this.progressEndX;
        int i2 = this.progressStartX;
        int i3 = this.progressEndY;
        int i4 = this.progressStartY;
        float f = this.zoom;
        int i5 = (int) (((float) i2) + (((float) (i - i2)) * f));
        int i6 = (int) (((float) i4) + (((float) (i3 - i4)) * f));
        if (z) {
            this.progressDrawable.setBounds(i2, i4 - AndroidUtilities.dp(3.0f), this.progressEndX, this.progressStartY + AndroidUtilities.dp(3.0f));
            this.filledProgressDrawable.setBounds(this.progressStartX, this.progressStartY - AndroidUtilities.dp(3.0f), i5, this.progressStartY + AndroidUtilities.dp(3.0f));
        } else {
            this.progressDrawable.setBounds(i4, 0, i3, AndroidUtilities.dp(6.0f));
            this.filledProgressDrawable.setBounds(this.progressStartY, 0, i6, AndroidUtilities.dp(6.0f));
            canvas.save();
            canvas.rotate(90.0f);
            canvas.translate(0.0f, (float) ((-this.progressStartX) - AndroidUtilities.dp(3.0f)));
        }
        this.progressDrawable.draw(canvas);
        this.filledProgressDrawable.draw(canvas);
        if (!z) {
            canvas.restore();
        }
        Drawable drawable = this.knobPressed ? this.pressedKnobDrawable : this.knobDrawable;
        int intrinsicWidth = drawable.getIntrinsicWidth() / 2;
        drawable.setBounds(i5 - intrinsicWidth, i6 - intrinsicWidth, i5 + intrinsicWidth, i6 + intrinsicWidth);
        drawable.draw(canvas);
    }
}

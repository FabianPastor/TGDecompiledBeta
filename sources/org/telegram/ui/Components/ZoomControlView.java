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
import org.telegram.ui.Components.AnimationProperties.FloatProperty;

public class ZoomControlView extends View {
    public final Property<ZoomControlView, Float> ZOOM_PROPERTY = new FloatProperty<ZoomControlView>("clipProgress") {
        public void setValue(ZoomControlView zoomControlView, float f) {
            ZoomControlView.this.zoom = f;
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
    private AnimatorSet animatorSet;
    private ZoomControlViewDelegate delegate;
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
    private float zoom;

    public interface ZoomControlViewDelegate {
        void didSetZoom(float f);
    }

    public ZoomControlView(Context context) {
        super(context);
        this.minusDrawable = context.getResources().getDrawable(NUM);
        this.plusDrawable = context.getResources().getDrawable(NUM);
        this.progressDrawable = context.getResources().getDrawable(NUM);
        this.filledProgressDrawable = context.getResources().getDrawable(NUM);
        this.knobDrawable = context.getResources().getDrawable(NUM);
        this.pressedKnobDrawable = context.getResources().getDrawable(NUM);
    }

    public float getZoom() {
        if (this.animatorSet != null) {
            return this.animatingToZoom;
        }
        return this.zoom;
    }

    public void setZoom(float f, boolean z) {
        if (f != this.zoom) {
            if (f < 0.0f) {
                f = 0.0f;
            } else if (f > 1.0f) {
                f = 1.0f;
            }
            this.zoom = f;
            if (z) {
                ZoomControlViewDelegate zoomControlViewDelegate = this.delegate;
                if (zoomControlViewDelegate != null) {
                    zoomControlViewDelegate.didSetZoom(this.zoom);
                }
            }
            invalidate();
        }
    }

    public void setDelegate(ZoomControlViewDelegate zoomControlViewDelegate) {
        this.delegate = zoomControlViewDelegate;
    }

    /* JADX WARNING: Removed duplicated region for block: B:89:0x01de  */
    public boolean onTouchEvent(android.view.MotionEvent r15) {
        /*
        r14 = this;
        r0 = r15.getX();
        r1 = r15.getY();
        r2 = r15.getAction();
        r3 = r14.getMeasuredWidth();
        r4 = r14.getMeasuredHeight();
        r5 = 0;
        r6 = 1;
        if (r3 <= r4) goto L_0x001a;
    L_0x0018:
        r3 = 1;
        goto L_0x001b;
    L_0x001a:
        r3 = 0;
    L_0x001b:
        r4 = r14.progressStartX;
        r7 = (float) r4;
        r8 = r14.progressEndX;
        r9 = r8 - r4;
        r9 = (float) r9;
        r10 = r14.zoom;
        r9 = r9 * r10;
        r7 = r7 + r9;
        r7 = (int) r7;
        r9 = r14.progressStartY;
        r11 = (float) r9;
        r12 = r14.progressEndY;
        r13 = r12 - r9;
        r13 = (float) r13;
        r13 = r13 * r10;
        r11 = r11 + r13;
        r10 = (int) r11;
        if (r2 == r6) goto L_0x0078;
    L_0x0037:
        if (r2 != 0) goto L_0x003a;
    L_0x0039:
        goto L_0x0078;
    L_0x003a:
        r7 = 2;
        if (r2 != r7) goto L_0x01db;
    L_0x003d:
        r7 = r14.knobPressed;
        if (r7 == 0) goto L_0x01db;
    L_0x0041:
        if (r3 == 0) goto L_0x004e;
    L_0x0043:
        r1 = r14.knobStartX;
        r0 = r0 + r1;
        r1 = (float) r4;
        r0 = r0 - r1;
        r8 = r8 - r4;
        r1 = (float) r8;
        r0 = r0 / r1;
        r14.zoom = r0;
        goto L_0x0058;
    L_0x004e:
        r0 = r14.knobStartY;
        r1 = r1 + r0;
        r0 = (float) r9;
        r1 = r1 - r0;
        r12 = r12 - r9;
        r0 = (float) r12;
        r1 = r1 / r0;
        r14.zoom = r1;
    L_0x0058:
        r0 = r14.zoom;
        r1 = 0;
        r3 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));
        if (r3 >= 0) goto L_0x0062;
    L_0x005f:
        r14.zoom = r1;
        goto L_0x006a;
    L_0x0062:
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));
        if (r0 <= 0) goto L_0x006a;
    L_0x0068:
        r14.zoom = r1;
    L_0x006a:
        r0 = r14.delegate;
        if (r0 == 0) goto L_0x0073;
    L_0x006e:
        r1 = r14.zoom;
        r0.didSetZoom(r1);
    L_0x0073:
        r14.invalidate();
        goto L_0x01db;
    L_0x0078:
        r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r8 = r7 - r8;
        r8 = (float) r8;
        r8 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1));
        if (r8 < 0) goto L_0x00b8;
    L_0x0085:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r7;
        r4 = (float) r4;
        r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r4 > 0) goto L_0x00b8;
    L_0x008f:
        r4 = NUM; // 0x41CLASSNAME float:25.0 double:5.45263811E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r8 = r10 - r8;
        r8 = (float) r8;
        r8 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1));
        if (r8 < 0) goto L_0x00b8;
    L_0x009c:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r10;
        r4 = (float) r4;
        r4 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1));
        if (r4 > 0) goto L_0x00b8;
    L_0x00a6:
        if (r2 != 0) goto L_0x00b5;
    L_0x00a8:
        r14.knobPressed = r6;
        r3 = (float) r7;
        r0 = r0 - r3;
        r14.knobStartX = r0;
        r0 = (float) r10;
        r1 = r1 - r0;
        r14.knobStartY = r1;
        r14.invalidate();
    L_0x00b5:
        r0 = 1;
        goto L_0x01dc;
    L_0x00b8:
        r4 = r14.minusCx;
        r7 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r4 = r4 - r8;
        r4 = (float) r4;
        r8 = 3;
        r9 = NUM; // 0x3e800000 float:0.25 double:5.180653787E-315;
        r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r4 < 0) goto L_0x010a;
    L_0x00c9:
        r4 = r14.minusCx;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r4 = r4 + r10;
        r4 = (float) r4;
        r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r4 > 0) goto L_0x010a;
    L_0x00d5:
        r4 = r14.minusCy;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r4 = r4 - r10;
        r4 = (float) r4;
        r4 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1));
        if (r4 < 0) goto L_0x010a;
    L_0x00e1:
        r4 = r14.minusCy;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r4 = r4 + r10;
        r4 = (float) r4;
        r4 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1));
        if (r4 > 0) goto L_0x010a;
    L_0x00ed:
        if (r2 != r6) goto L_0x0107;
    L_0x00ef:
        r0 = r14.getZoom();
        r0 = r0 / r9;
        r0 = (double) r0;
        r0 = java.lang.Math.floor(r0);
        r0 = (float) r0;
        r0 = r0 * r9;
        r0 = r0 - r9;
        r0 = r14.animateToZoom(r0);
        if (r0 == 0) goto L_0x0107;
    L_0x0103:
        r14.performHapticFeedback(r8);
        goto L_0x00b5;
    L_0x0107:
        r14.pressed = r6;
        goto L_0x00b5;
    L_0x010a:
        r4 = r14.plusCx;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r4 = r4 - r10;
        r4 = (float) r4;
        r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r4 < 0) goto L_0x0159;
    L_0x0116:
        r4 = r14.plusCx;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r4 = r4 + r10;
        r4 = (float) r4;
        r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r4 > 0) goto L_0x0159;
    L_0x0122:
        r4 = r14.plusCy;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r4 = r4 - r10;
        r4 = (float) r4;
        r4 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1));
        if (r4 < 0) goto L_0x0159;
    L_0x012e:
        r4 = r14.plusCy;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r4 = r4 + r7;
        r4 = (float) r4;
        r4 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1));
        if (r4 > 0) goto L_0x0159;
    L_0x013a:
        if (r2 != r6) goto L_0x0155;
    L_0x013c:
        r0 = r14.getZoom();
        r0 = r0 / r9;
        r0 = (double) r0;
        r0 = java.lang.Math.floor(r0);
        r0 = (float) r0;
        r0 = r0 * r9;
        r0 = r0 + r9;
        r0 = r14.animateToZoom(r0);
        if (r0 == 0) goto L_0x0155;
    L_0x0150:
        r14.performHapticFeedback(r8);
        goto L_0x00b5;
    L_0x0155:
        r14.pressed = r6;
        goto L_0x00b5;
    L_0x0159:
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        if (r3 == 0) goto L_0x019c;
    L_0x015d:
        r1 = r14.progressStartX;
        r1 = (float) r1;
        r1 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));
        if (r1 < 0) goto L_0x01db;
    L_0x0164:
        r1 = r14.progressEndX;
        r1 = (float) r1;
        r1 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));
        if (r1 > 0) goto L_0x01db;
    L_0x016b:
        if (r2 != 0) goto L_0x0173;
    L_0x016d:
        r14.knobStartX = r0;
        r14.pressed = r6;
        goto L_0x00b5;
    L_0x0173:
        r1 = r14.knobStartX;
        r1 = r1 - r0;
        r1 = java.lang.Math.abs(r1);
        r3 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = (float) r3;
        r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r1 > 0) goto L_0x00b5;
    L_0x0183:
        r1 = r14.progressStartX;
        r3 = (float) r1;
        r0 = r0 - r3;
        r3 = r14.progressEndX;
        r3 = r3 - r1;
        r1 = (float) r3;
        r0 = r0 / r1;
        r14.zoom = r0;
        r0 = r14.delegate;
        if (r0 == 0) goto L_0x0197;
    L_0x0192:
        r1 = r14.zoom;
        r0.didSetZoom(r1);
    L_0x0197:
        r14.invalidate();
        goto L_0x00b5;
    L_0x019c:
        r0 = r14.progressStartY;
        r0 = (float) r0;
        r0 = (r1 > r0 ? 1 : (r1 == r0 ? 0 : -1));
        if (r0 < 0) goto L_0x01db;
    L_0x01a3:
        r0 = r14.progressEndY;
        r0 = (float) r0;
        r0 = (r1 > r0 ? 1 : (r1 == r0 ? 0 : -1));
        if (r0 > 0) goto L_0x01db;
    L_0x01aa:
        if (r2 != r6) goto L_0x01b2;
    L_0x01ac:
        r14.knobStartY = r1;
        r14.pressed = r6;
        goto L_0x00b5;
    L_0x01b2:
        r0 = r14.knobStartY;
        r0 = r0 - r1;
        r0 = java.lang.Math.abs(r0);
        r3 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = (float) r3;
        r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1));
        if (r0 > 0) goto L_0x00b5;
    L_0x01c2:
        r0 = r14.progressStartY;
        r3 = (float) r0;
        r1 = r1 - r3;
        r3 = r14.progressEndY;
        r3 = r3 - r0;
        r0 = (float) r3;
        r1 = r1 / r0;
        r14.zoom = r1;
        r0 = r14.delegate;
        if (r0 == 0) goto L_0x01d6;
    L_0x01d1:
        r1 = r14.zoom;
        r0.didSetZoom(r1);
    L_0x01d6:
        r14.invalidate();
        goto L_0x00b5;
    L_0x01db:
        r0 = 0;
    L_0x01dc:
        if (r2 != r6) goto L_0x01e5;
    L_0x01de:
        r14.pressed = r5;
        r14.knobPressed = r5;
        r14.invalidate();
    L_0x01e5:
        if (r0 != 0) goto L_0x01f5;
    L_0x01e7:
        r0 = r14.pressed;
        if (r0 != 0) goto L_0x01f5;
    L_0x01eb:
        r0 = r14.knobPressed;
        if (r0 != 0) goto L_0x01f5;
    L_0x01ef:
        r15 = super.onTouchEvent(r15);
        if (r15 == 0) goto L_0x01f6;
    L_0x01f5:
        r5 = 1;
    L_0x01f6:
        return r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ZoomControlView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private boolean animateToZoom(float f) {
        if (f < 0.0f || f > 1.0f) {
            return false;
        }
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.animatingToZoom = f;
        this.animatorSet = new AnimatorSet();
        animatorSet = this.animatorSet;
        Animator[] animatorArr = new Animator[1];
        animatorArr[0] = ObjectAnimator.ofFloat(this, this.ZOOM_PROPERTY, new float[]{f});
        animatorSet.playTogether(animatorArr);
        this.animatorSet.setDuration(180);
        this.animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                ZoomControlView.this.animatorSet = null;
            }
        });
        this.animatorSet.start();
        return true;
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        int measuredWidth = getMeasuredWidth() / 2;
        int measuredHeight = getMeasuredHeight() / 2;
        Object obj = getMeasuredWidth() > getMeasuredHeight() ? 1 : null;
        if (obj != null) {
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
        measuredWidth = this.progressEndX;
        measuredHeight = this.progressStartX;
        measuredWidth -= measuredHeight;
        int i = this.progressEndY;
        int i2 = this.progressStartY;
        int i3 = i - i2;
        float f = (float) measuredHeight;
        float f2 = (float) measuredWidth;
        float f3 = this.zoom;
        measuredWidth = (int) (f + (f2 * f3));
        i3 = (int) (((float) i2) + (((float) i3) * f3));
        if (obj != null) {
            this.progressDrawable.setBounds(measuredHeight, i2 - AndroidUtilities.dp(3.0f), this.progressEndX, this.progressStartY + AndroidUtilities.dp(3.0f));
            this.filledProgressDrawable.setBounds(this.progressStartX, this.progressStartY - AndroidUtilities.dp(3.0f), measuredWidth, this.progressStartY + AndroidUtilities.dp(3.0f));
        } else {
            this.progressDrawable.setBounds(i2, 0, i, AndroidUtilities.dp(6.0f));
            this.filledProgressDrawable.setBounds(this.progressStartY, 0, i3, AndroidUtilities.dp(6.0f));
            canvas.save();
            canvas.rotate(90.0f);
            canvas.translate(0.0f, (float) ((-this.progressStartX) - AndroidUtilities.dp(3.0f)));
        }
        this.progressDrawable.draw(canvas);
        this.filledProgressDrawable.draw(canvas);
        if (obj == null) {
            canvas.restore();
        }
        Drawable drawable = this.knobPressed ? this.pressedKnobDrawable : this.knobDrawable;
        int intrinsicWidth = drawable.getIntrinsicWidth() / 2;
        drawable.setBounds(measuredWidth - intrinsicWidth, i3 - intrinsicWidth, measuredWidth + intrinsicWidth, i3 + intrinsicWidth);
        drawable.draw(canvas);
    }
}

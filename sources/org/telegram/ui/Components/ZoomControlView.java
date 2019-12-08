package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build.VERSION;
import android.util.Property;
import android.util.StateSet;
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
    private Paint blackPaint = new Paint(1);
    private Paint blackStrokePaint = new Paint(1);
    private ZoomControlViewDelegate delegate;
    private boolean drawRipple;
    private boolean knobPressed;
    private float knobStartX;
    private float knobStartY;
    private int minusCx;
    private int minusCy;
    private int plusCx;
    private int plusCy;
    private boolean pressed;
    private int[] pressedState = new int[]{16842910, 16842919};
    private int progressEndX;
    private int progressEndY;
    private int progressStartX;
    private int progressStartY;
    private RippleDrawable rippleDrawable;
    private Paint ripplePaint;
    private Paint whitePaint = new Paint(1);
    private Paint whiteStrokePaint = new Paint(1);
    private float zoom;

    public interface ZoomControlViewDelegate {
        void didSetZoom(float f);
    }

    public ZoomControlView(Context context) {
        super(context);
        this.blackPaint.setColor(NUM);
        this.whitePaint.setColor(-1);
        this.whiteStrokePaint.setColor(-1);
        this.whiteStrokePaint.setStyle(Style.STROKE);
        this.whiteStrokePaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.blackStrokePaint.setColor(NUM);
        this.blackStrokePaint.setStyle(Style.STROKE);
        this.blackStrokePaint.setStrokeWidth((float) (AndroidUtilities.dp(2.0f) + 2));
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

    public void setDrawRipple(boolean z) {
        if (VERSION.SDK_INT >= 21 && z != this.drawRipple) {
            this.drawRipple = z;
            if (this.rippleDrawable == null) {
                Drawable drawable;
                this.ripplePaint = new Paint(1);
                this.ripplePaint.setColor(-1);
                if (VERSION.SDK_INT >= 23) {
                    drawable = null;
                } else {
                    drawable = new Drawable() {
                        public int getOpacity() {
                            return 0;
                        }

                        public void setAlpha(int i) {
                        }

                        public void setColorFilter(ColorFilter colorFilter) {
                        }

                        public void draw(Canvas canvas) {
                            Rect bounds = getBounds();
                            canvas.drawCircle((float) bounds.centerX(), (float) bounds.centerY(), (float) AndroidUtilities.dp(18.0f), ZoomControlView.this.ripplePaint);
                        }
                    };
                }
                this.rippleDrawable = new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{NUM}), null, drawable);
                if (VERSION.SDK_INT >= 23) {
                    this.rippleDrawable.setRadius(AndroidUtilities.dp(16.0f));
                }
                this.rippleDrawable.setCallback(this);
            }
            this.rippleDrawable.setState(z ? this.pressedState : StateSet.NOTHING);
            invalidate();
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean verifyDrawable(Drawable drawable) {
        if (!super.verifyDrawable(drawable)) {
            Drawable drawable2 = this.rippleDrawable;
            if (drawable2 == null || drawable != drawable2) {
                return false;
            }
        }
        return true;
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
        r14.setDrawRipple(r6);
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
        r14.setDrawRipple(r5);
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
        Canvas canvas2 = canvas;
        int measuredWidth = getMeasuredWidth() / 2;
        int measuredHeight = getMeasuredHeight() / 2;
        Object obj = getMeasuredWidth() > getMeasuredHeight() ? 1 : null;
        if (obj != null) {
            this.minusCx = AndroidUtilities.dp(32.0f);
            this.minusCy = measuredHeight;
            this.plusCx = getMeasuredWidth() - AndroidUtilities.dp(32.0f);
            this.plusCy = measuredHeight;
            this.progressStartX = this.minusCx + AndroidUtilities.dp(44.0f);
            this.progressStartY = measuredHeight;
            this.progressEndX = this.plusCx - AndroidUtilities.dp(44.0f);
            this.progressEndY = measuredHeight;
        } else {
            this.minusCx = measuredWidth;
            this.minusCy = AndroidUtilities.dp(32.0f);
            this.plusCx = measuredWidth;
            this.plusCy = getMeasuredHeight() - AndroidUtilities.dp(32.0f);
            this.progressStartX = measuredWidth;
            this.progressStartY = this.minusCy + AndroidUtilities.dp(44.0f);
            this.progressEndX = measuredWidth;
            this.progressEndY = this.plusCy - AndroidUtilities.dp(44.0f);
        }
        canvas2.drawCircle((float) this.minusCx, (float) this.minusCy, (float) AndroidUtilities.dp(16.0f), this.blackPaint);
        canvas2.drawCircle((float) this.plusCx, (float) this.plusCy, (float) AndroidUtilities.dp(16.0f), this.blackPaint);
        int i = this.progressEndX - this.progressStartX;
        int i2 = this.progressEndY - this.progressStartY;
        int max = Math.max(i, i2) / 4;
        int i3 = max / 6;
        this.whitePaint.setAlpha(127);
        for (int i4 = 0; i4 < 5; i4++) {
            int i5;
            float f;
            if (obj != null) {
                i5 = this.progressStartX + (max * i4);
                f = (float) i5;
                float f2 = (float) measuredHeight;
                canvas2.drawCircle(f, f2, (float) AndroidUtilities.dp(4.0f), this.blackStrokePaint);
                canvas2.drawCircle(f, f2, (float) AndroidUtilities.dp(4.0f), this.whiteStrokePaint);
            } else {
                i5 = this.progressStartY + (max * i4);
                float f3 = (float) measuredWidth;
                f = (float) i5;
                canvas2.drawCircle(f3, f, (float) AndroidUtilities.dp(4.0f), this.blackStrokePaint);
                canvas2.drawCircle(f3, f, (float) AndroidUtilities.dp(4.0f), this.whiteStrokePaint);
            }
            if (i4 != 4) {
                for (int i6 = 0; i6 < 5; i6++) {
                    if (obj != null) {
                        canvas2.drawCircle((float) (((i6 + 1) * i3) + i5), (float) measuredHeight, (float) AndroidUtilities.dp(1.0f), this.whitePaint);
                    } else {
                        canvas2.drawCircle((float) measuredWidth, (float) (((i6 + 1) * i3) + i5), (float) AndroidUtilities.dp(1.0f), this.whitePaint);
                    }
                }
            }
        }
        this.whitePaint.setAlpha(255);
        canvas.drawRect((float) (this.minusCx - AndroidUtilities.dp(8.0f)), (float) (this.minusCy - AndroidUtilities.dp(1.0f)), (float) (this.minusCx + AndroidUtilities.dp(8.0f)), (float) (this.minusCy + AndroidUtilities.dp(1.0f)), this.whitePaint);
        canvas.drawRect((float) (this.plusCx - AndroidUtilities.dp(8.0f)), (float) (this.plusCy - AndroidUtilities.dp(1.0f)), (float) (this.plusCx + AndroidUtilities.dp(8.0f)), (float) (this.plusCy + AndroidUtilities.dp(1.0f)), this.whitePaint);
        canvas.drawRect((float) (this.plusCx - AndroidUtilities.dp(1.0f)), (float) (this.plusCy - AndroidUtilities.dp(8.0f)), (float) (this.plusCx + AndroidUtilities.dp(1.0f)), (float) (this.plusCy + AndroidUtilities.dp(8.0f)), this.whitePaint);
        float f4 = (float) this.progressStartX;
        float f5 = (float) i;
        float f6 = this.zoom;
        measuredWidth = (int) (f4 + (f5 * f6));
        measuredHeight = (int) (((float) this.progressStartY) + (((float) i2) * f6));
        RippleDrawable rippleDrawable = this.rippleDrawable;
        if (rippleDrawable != null) {
            rippleDrawable.setBounds(measuredWidth - AndroidUtilities.dp(10.0f), measuredHeight - AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f) + measuredWidth, AndroidUtilities.dp(10.0f) + measuredHeight);
            this.rippleDrawable.draw(canvas2);
        }
        f4 = (float) measuredWidth;
        f5 = (float) measuredHeight;
        canvas2.drawCircle(f4, f5, (float) (AndroidUtilities.dp(8.0f) - 1), this.blackStrokePaint);
        canvas2.drawCircle(f4, f5, (float) AndroidUtilities.dp(8.0f), this.whitePaint);
    }
}

package org.telegram.ui.Components.Premium;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.OverScroller;
import j$.util.Comparator$CC;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;

public class CarouselView extends View implements PagerHeaderView {
    static final Interpolator sQuinticInterpolator = CarouselView$$ExternalSyntheticLambda1.INSTANCE;
    boolean autoPlayEnabled = true;
    ValueAnimator autoScrollAnimation;
    /* access modifiers changed from: private */
    public Runnable autoScrollRunnable = new Runnable() {
        public void run() {
            CarouselView carouselView = CarouselView.this;
            if (carouselView.autoPlayEnabled) {
                carouselView.scrollToInternal(carouselView.offsetAngle + (360.0f / ((float) carouselView.drawingObjects.size())));
            }
        }
    };
    int cX;
    int cY;
    Comparator<DrawingObject> comparator = Comparator$CC.comparingInt(CarouselView$$ExternalSyntheticLambda2.INSTANCE);
    /* access modifiers changed from: private */
    public final ArrayList<? extends DrawingObject> drawingObjects;
    /* access modifiers changed from: private */
    public final ArrayList<? extends DrawingObject> drawingObjectsSorted;
    boolean firstScroll = true;
    boolean firstScroll1 = true;
    boolean firstScrollEnabled = true;
    GestureDetector gestureDetector;
    float lastFlingX;
    float lastFlingY;
    int lastSelected;
    float offsetAngle = 0.0f;
    OverScroller overScroller = new OverScroller(getContext(), sQuinticInterpolator);
    boolean scrolled;

    public static class DrawingObject {
        public double angle;
        CarouselView carouselView;
        public float x;
        public float y;
        float yRelative;

        public boolean checkTap(float f, float f2) {
            return false;
        }

        public void draw(Canvas canvas, float f, float f2, float f3) {
        }

        public void hideAnimation() {
        }

        public void onAttachToWindow(View view, int i) {
        }

        public void onDetachFromWindow() {
        }

        public void select() {
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ float lambda$static$0(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2 * f2 * f2) + 1.0f;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$new$1(DrawingObject drawingObject) {
        return (int) (drawingObject.yRelative * 100.0f);
    }

    public CarouselView(Context context, final ArrayList<? extends DrawingObject> arrayList) {
        super(context);
        this.gestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
            double lastAngle;

            public void onLongPress(MotionEvent motionEvent) {
            }

            public void onShowPress(MotionEvent motionEvent) {
            }

            public boolean onDown(MotionEvent motionEvent) {
                double measuredHeight = (double) CarouselView.this.getMeasuredHeight();
                Double.isNaN(measuredHeight);
                if (((double) motionEvent.getY()) > measuredHeight * 0.2d) {
                    double measuredHeight2 = (double) CarouselView.this.getMeasuredHeight();
                    Double.isNaN(measuredHeight2);
                    if (((double) motionEvent.getY()) < measuredHeight2 * 0.9d) {
                        CarouselView.this.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                ValueAnimator valueAnimator = CarouselView.this.autoScrollAnimation;
                if (valueAnimator != null) {
                    valueAnimator.removeAllListeners();
                    CarouselView.this.autoScrollAnimation.cancel();
                    CarouselView.this.autoScrollAnimation = null;
                }
                AndroidUtilities.cancelRunOnUIThread(CarouselView.this.autoScrollRunnable);
                CarouselView.this.overScroller.abortAnimation();
                this.lastAngle = Math.atan2((double) (motionEvent.getX() - ((float) CarouselView.this.cX)), (double) (motionEvent.getY() - ((float) CarouselView.this.cY)));
                float size = 360.0f / ((float) arrayList.size());
                CarouselView carouselView = CarouselView.this;
                carouselView.lastSelected = (int) (carouselView.offsetAngle / size);
                for (int i = 0; i < arrayList.size(); i++) {
                    ((DrawingObject) arrayList.get(i)).hideAnimation();
                }
                return true;
            }

            public boolean onSingleTapUp(MotionEvent motionEvent) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                for (int size = CarouselView.this.drawingObjectsSorted.size() - 1; size >= 0; size--) {
                    if (((DrawingObject) CarouselView.this.drawingObjectsSorted.get(size)).checkTap(x, y)) {
                        if (((DrawingObject) CarouselView.this.drawingObjectsSorted.get(size)).angle % 360.0d != 270.0d) {
                            double d = ((270.0d - (((DrawingObject) CarouselView.this.drawingObjectsSorted.get(size)).angle % 360.0d)) + 180.0d) % 360.0d;
                            if (d > 180.0d) {
                                d = -(360.0d - d);
                            }
                            CarouselView carouselView = CarouselView.this;
                            carouselView.scrollToInternal(carouselView.offsetAngle + ((float) d));
                            CarouselView.this.performHapticFeedback(3);
                        }
                        return true;
                    }
                }
                return false;
            }

            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                double atan2 = Math.atan2((double) (motionEvent2.getX() - ((float) CarouselView.this.cX)), (double) (motionEvent2.getY() - ((float) CarouselView.this.cY)));
                double d = this.lastAngle - atan2;
                this.lastAngle = atan2;
                CarouselView carouselView = CarouselView.this;
                double d2 = (double) carouselView.offsetAngle;
                double degrees = Math.toDegrees(d);
                Double.isNaN(d2);
                carouselView.offsetAngle = (float) (d2 + degrees);
                CarouselView.this.checkSelectedHaptic();
                CarouselView.this.invalidate();
                return true;
            }

            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                CarouselView carouselView = CarouselView.this;
                carouselView.lastFlingY = 0.0f;
                carouselView.lastFlingX = 0.0f;
                double atan2 = Math.atan2((double) (motionEvent2.getX() - ((float) CarouselView.this.cX)), (double) (motionEvent2.getY() - ((float) CarouselView.this.cY)));
                double cos = Math.cos(atan2);
                double d = (double) f;
                Double.isNaN(d);
                double sin = Math.sin(atan2);
                double d2 = (double) f2;
                Double.isNaN(d2);
                CarouselView.this.overScroller.fling(0, 0, (int) ((float) ((cos * d) - (sin * d2))), 0, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
                if (CarouselView.this.overScroller.isFinished()) {
                    CarouselView.this.scheduleAutoscroll();
                }
                CarouselView.this.invalidate();
                return true;
            }
        });
        this.drawingObjects = arrayList;
        this.drawingObjectsSorted = new ArrayList<>(arrayList);
        for (int i = 0; i < arrayList.size() / 2; i++) {
            float f = (float) i;
            ((DrawingObject) arrayList.get(i)).y = ((float) arrayList.size()) / f;
            ((DrawingObject) arrayList.get((arrayList.size() - 1) - i)).y = ((float) arrayList.size()) / f;
        }
        Collections.sort(arrayList, this.comparator);
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            ((DrawingObject) arrayList.get(i2)).carouselView = this;
        }
    }

    /* access modifiers changed from: private */
    public void checkSelectedHaptic() {
        int size = (int) (this.offsetAngle / (360.0f / ((float) this.drawingObjects.size())));
        if (this.lastSelected != size) {
            this.lastSelected = size;
            performHapticFeedback(3);
        }
    }

    /* access modifiers changed from: private */
    public void scrollToInternal(final float f) {
        if (Math.abs(f - this.offsetAngle) >= 1.0f || this.autoScrollAnimation != null) {
            AndroidUtilities.cancelRunOnUIThread(this.autoScrollRunnable);
            ValueAnimator valueAnimator = this.autoScrollAnimation;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.autoScrollAnimation.cancel();
                this.autoScrollAnimation = null;
            }
            float f2 = this.offsetAngle;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.autoScrollAnimation = ofFloat;
            ofFloat.addUpdateListener(new CarouselView$$ExternalSyntheticLambda0(this, f2, f));
            this.autoScrollAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    CarouselView carouselView = CarouselView.this;
                    carouselView.offsetAngle = f;
                    carouselView.autoScrollAnimation = null;
                    carouselView.invalidate();
                    AndroidUtilities.runOnUIThread(new CarouselView$3$$ExternalSyntheticLambda0(this));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onAnimationEnd$0() {
                    if (!CarouselView.this.drawingObjectsSorted.isEmpty()) {
                        ((DrawingObject) CarouselView.this.drawingObjectsSorted.get(CarouselView.this.drawingObjectsSorted.size() - 1)).select();
                    }
                    CarouselView.this.scheduleAutoscroll();
                }
            });
            this.autoScrollAnimation.setInterpolator(new OvershootInterpolator());
            this.autoScrollAnimation.setDuration(600);
            this.autoScrollAnimation.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$scrollToInternal$2(float f, float f2, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.offsetAngle = (f * (1.0f - floatValue)) + (f2 * floatValue);
        invalidate();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.scrolled = true;
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            this.scrolled = false;
            getParent().requestDisallowInterceptTouchEvent(false);
            invalidate();
        }
        return this.gestureDetector.onTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.cX = getMeasuredWidth() >> 1;
        this.cY = getMeasuredHeight() >> 1;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        for (int i = 0; i < 2; i++) {
            for (int i2 = 0; i2 < this.drawingObjectsSorted.size(); i2++) {
                ((DrawingObject) this.drawingObjectsSorted.get(i2)).onAttachToWindow(this, i);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (int i = 0; i < this.drawingObjects.size(); i++) {
            ((DrawingObject) this.drawingObjects.get(i)).onDetachFromWindow();
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0074, code lost:
        if (java.lang.Math.abs(r8 % r2) > 2.0d) goto L_0x0076;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r14) {
        /*
            r13 = this;
            java.util.ArrayList<? extends org.telegram.ui.Components.Premium.CarouselView$DrawingObject> r0 = r13.drawingObjects
            int r0 = r0.size()
            double r0 = (double) r0
            r2 = 4645040803167600640(0xNUM, double:360.0)
            java.lang.Double.isNaN(r0)
            double r2 = r2 / r0
            android.widget.OverScroller r0 = r13.overScroller
            boolean r0 = r0.computeScrollOffset()
            r1 = 0
            r4 = 0
            if (r0 == 0) goto L_0x0052
            android.widget.OverScroller r0 = r13.overScroller
            int r0 = r0.getCurrX()
            float r5 = r13.lastFlingX
            float r0 = (float) r0
            float r6 = r5 - r0
            r7 = 1034147594(0x3da3d70a, float:0.08)
            int r1 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r1 == 0) goto L_0x003e
            float r1 = r6 * r7
            float r1 = java.lang.Math.abs(r1)
            r5 = 1050253722(0x3e99999a, float:0.3)
            int r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r1 >= 0) goto L_0x003e
            android.widget.OverScroller r1 = r13.overScroller
            r1.abortAnimation()
        L_0x003e:
            r13.lastFlingX = r0
            float r0 = r13.offsetAngle
            float r6 = r6 * r7
            float r0 = r0 + r6
            r13.offsetAngle = r0
            r13.checkSelectedHaptic()
            r13.invalidate()
            r13.scheduleAutoscroll()
            goto L_0x00cd
        L_0x0052:
            boolean r0 = r13.firstScroll1
            r5 = 4611686018427387904(0xNUM, double:2.0)
            r7 = 1119092736(0x42b40000, float:90.0)
            if (r0 != 0) goto L_0x0076
            boolean r0 = r13.firstScroll
            if (r0 != 0) goto L_0x0076
            boolean r0 = r13.scrolled
            if (r0 != 0) goto L_0x00cd
            android.animation.ValueAnimator r0 = r13.autoScrollAnimation
            if (r0 != 0) goto L_0x00cd
            float r0 = r13.offsetAngle
            float r0 = r0 - r7
            double r8 = (double) r0
            java.lang.Double.isNaN(r8)
            double r8 = r8 % r2
            double r8 = java.lang.Math.abs(r8)
            int r0 = (r8 > r5 ? 1 : (r8 == r5 ? 0 : -1))
            if (r0 <= 0) goto L_0x00cd
        L_0x0076:
            boolean r0 = r13.firstScroll1
            if (r0 == 0) goto L_0x008a
            float r0 = r13.offsetAngle
            double r8 = (double) r0
            r10 = 4636033603912859648(0xNUM, double:90.0)
            double r10 = r10 + r2
            java.lang.Double.isNaN(r8)
            double r8 = r8 + r10
            float r0 = (float) r8
            r13.offsetAngle = r0
        L_0x008a:
            float r0 = r13.offsetAngle
            float r0 = r0 - r7
            double r7 = (double) r0
            java.lang.Double.isNaN(r7)
            double r7 = r7 % r2
            float r0 = (float) r7
            float r7 = java.lang.Math.abs(r0)
            double r7 = (double) r7
            double r5 = r2 / r5
            int r9 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r9 <= 0) goto L_0x00ae
            int r1 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r1 >= 0) goto L_0x00a8
            double r0 = (double) r0
            java.lang.Double.isNaN(r0)
            double r0 = r0 + r2
            goto L_0x00ad
        L_0x00a8:
            double r0 = (double) r0
            java.lang.Double.isNaN(r0)
            double r0 = r0 - r2
        L_0x00ad:
            float r0 = (float) r0
        L_0x00ae:
            r13.firstScroll1 = r4
            boolean r1 = r13.firstScroll
            if (r1 == 0) goto L_0x00c7
            boolean r1 = r13.firstScrollEnabled
            if (r1 == 0) goto L_0x00c7
            r13.firstScroll = r4
            float r1 = r13.offsetAngle
            r5 = 1127481344(0x43340000, float:180.0)
            float r1 = r1 - r5
            r13.offsetAngle = r1
            float r1 = r1 - r0
            float r1 = r1 + r5
            r13.scrollToInternal(r1)
            goto L_0x00cd
        L_0x00c7:
            float r1 = r13.offsetAngle
            float r1 = r1 - r0
            r13.scrollToInternal(r1)
        L_0x00cd:
            int r0 = r13.getMeasuredWidth()
            float r0 = (float) r0
            int r1 = r13.getMeasuredHeight()
            float r1 = (float) r1
            r5 = 1067869798(0x3fa66666, float:1.3)
            float r1 = r1 * r5
            float r0 = java.lang.Math.min(r0, r1)
            r1 = 1124859904(0x430CLASSNAME, float:140.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r0 = r0 - r1
            r1 = 1056964608(0x3var_, float:0.5)
            float r0 = r0 * r1
            r1 = 1058642330(0x3var_a, float:0.6)
            float r1 = r1 * r0
            r5 = 0
        L_0x00f2:
            java.util.ArrayList<? extends org.telegram.ui.Components.Premium.CarouselView$DrawingObject> r6 = r13.drawingObjects
            int r6 = r6.size()
            if (r5 >= r6) goto L_0x0147
            java.util.ArrayList<? extends org.telegram.ui.Components.Premium.CarouselView$DrawingObject> r6 = r13.drawingObjects
            java.lang.Object r6 = r6.get(r5)
            org.telegram.ui.Components.Premium.CarouselView$DrawingObject r6 = (org.telegram.ui.Components.Premium.CarouselView.DrawingObject) r6
            float r7 = r13.offsetAngle
            double r7 = (double) r7
            double r9 = (double) r5
            java.lang.Double.isNaN(r9)
            double r9 = r9 * r2
            java.lang.Double.isNaN(r7)
            double r7 = r7 + r9
            r6.angle = r7
            double r7 = java.lang.Math.toRadians(r7)
            double r7 = java.lang.Math.cos(r7)
            double r9 = r6.angle
            r11 = 4629137466983448576(0x403eNUM, double:30.0)
            double r7 = r7 * r11
            double r9 = r9 - r7
            double r7 = java.lang.Math.toRadians(r9)
            double r7 = java.lang.Math.cos(r7)
            float r7 = (float) r7
            float r7 = r7 * r0
            int r8 = r13.cX
            float r8 = (float) r8
            float r7 = r7 + r8
            r6.x = r7
            double r7 = java.lang.Math.toRadians(r9)
            double r7 = java.lang.Math.sin(r7)
            float r7 = (float) r7
            r6.yRelative = r7
            float r7 = r7 * r1
            int r8 = r13.cY
            float r8 = (float) r8
            float r7 = r7 + r8
            r6.y = r7
            int r5 = r5 + 1
            goto L_0x00f2
        L_0x0147:
            java.util.ArrayList<? extends org.telegram.ui.Components.Premium.CarouselView$DrawingObject> r0 = r13.drawingObjectsSorted
            java.util.Comparator<org.telegram.ui.Components.Premium.CarouselView$DrawingObject> r1 = r13.comparator
            java.util.Collections.sort(r0, r1)
        L_0x014e:
            java.util.ArrayList<? extends org.telegram.ui.Components.Premium.CarouselView$DrawingObject> r0 = r13.drawingObjectsSorted
            int r0 = r0.size()
            if (r4 >= r0) goto L_0x0179
            java.util.ArrayList<? extends org.telegram.ui.Components.Premium.CarouselView$DrawingObject> r0 = r13.drawingObjectsSorted
            java.lang.Object r0 = r0.get(r4)
            org.telegram.ui.Components.Premium.CarouselView$DrawingObject r0 = (org.telegram.ui.Components.Premium.CarouselView.DrawingObject) r0
            r1 = 1045220557(0x3e4ccccd, float:0.2)
            r2 = 1060320051(0x3var_, float:0.7)
            float r3 = r0.yRelative
            r5 = 1065353216(0x3var_, float:1.0)
            float r3 = r3 + r5
            float r3 = r3 * r2
            r2 = 1073741824(0x40000000, float:2.0)
            float r3 = r3 / r2
            float r3 = r3 + r1
            float r1 = r0.x
            float r2 = r0.y
            r0.draw(r14, r1, r2, r3)
            int r4 = r4 + 1
            goto L_0x014e
        L_0x0179:
            r13.invalidate()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Premium.CarouselView.onDraw(android.graphics.Canvas):void");
    }

    /* access modifiers changed from: package-private */
    public void scheduleAutoscroll() {
        AndroidUtilities.cancelRunOnUIThread(this.autoScrollRunnable);
        if (this.autoPlayEnabled) {
            AndroidUtilities.runOnUIThread(this.autoScrollRunnable, 3000);
        }
    }

    public void setOffset(float f) {
        boolean z = true;
        if (f >= ((float) getMeasuredWidth()) || f <= ((float) (-getMeasuredWidth()))) {
            this.overScroller.abortAnimation();
            ValueAnimator valueAnimator = this.autoScrollAnimation;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.autoScrollAnimation.cancel();
                this.autoScrollAnimation = null;
            }
            this.firstScroll = true;
            this.firstScroll1 = true;
            this.offsetAngle = 0.0f;
        }
        setAutoPlayEnabled(f == 0.0f);
        if (Math.abs(f) >= ((float) getMeasuredWidth()) * 0.2f) {
            z = false;
        }
        setFirstScrollEnabled(z);
        float clamp = 1.0f - Utilities.clamp(Math.abs(f) / ((float) getMeasuredWidth()), 1.0f, 0.0f);
        setScaleX(clamp);
        setScaleY(clamp);
    }

    public void autoplayToNext() {
        AndroidUtilities.cancelRunOnUIThread(this.autoScrollRunnable);
        if (this.autoPlayEnabled) {
            ArrayList<? extends DrawingObject> arrayList = this.drawingObjectsSorted;
            int indexOf = this.drawingObjects.indexOf((DrawingObject) arrayList.get(arrayList.size() - 1)) - 1;
            if (indexOf < 0) {
                indexOf = this.drawingObjects.size() - 1;
            }
            ((DrawingObject) this.drawingObjects.get(indexOf)).select();
            AndroidUtilities.runOnUIThread(this.autoScrollRunnable, 16);
        }
    }

    /* access modifiers changed from: package-private */
    public void setAutoPlayEnabled(boolean z) {
        if (this.autoPlayEnabled != z) {
            this.autoPlayEnabled = z;
            if (z) {
                scheduleAutoscroll();
            } else {
                AndroidUtilities.cancelRunOnUIThread(this.autoScrollRunnable);
            }
            invalidate();
        }
    }

    /* access modifiers changed from: package-private */
    public void setFirstScrollEnabled(boolean z) {
        if (this.firstScrollEnabled != z) {
            this.firstScrollEnabled = z;
            invalidate();
        }
    }
}

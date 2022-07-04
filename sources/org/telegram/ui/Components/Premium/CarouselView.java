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
import j$.util.Comparator;
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
            if (CarouselView.this.autoPlayEnabled) {
                CarouselView carouselView = CarouselView.this;
                carouselView.scrollToInternal(carouselView.offsetAngle + (360.0f / ((float) CarouselView.this.drawingObjects.size())));
            }
        }
    };
    int cX;
    int cY;
    Comparator<DrawingObject> comparator = Comparator.CC.comparingInt(CarouselView$$ExternalSyntheticLambda2.INSTANCE);
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

    static /* synthetic */ float lambda$static$0(float t) {
        float t2 = t - 1.0f;
        return (t2 * t2 * t2 * t2 * t2) + 1.0f;
    }

    static /* synthetic */ int lambda$new$1(DrawingObject value) {
        return (int) (value.yRelative * 100.0f);
    }

    public CarouselView(Context context, final ArrayList<? extends DrawingObject> drawingObjects2) {
        super(context);
        this.gestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
            double lastAngle;

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
                if (CarouselView.this.autoScrollAnimation != null) {
                    CarouselView.this.autoScrollAnimation.removeAllListeners();
                    CarouselView.this.autoScrollAnimation.cancel();
                    CarouselView.this.autoScrollAnimation = null;
                }
                AndroidUtilities.cancelRunOnUIThread(CarouselView.this.autoScrollRunnable);
                CarouselView.this.overScroller.abortAnimation();
                this.lastAngle = Math.atan2((double) (motionEvent.getX() - ((float) CarouselView.this.cX)), (double) (motionEvent.getY() - ((float) CarouselView.this.cY)));
                float aStep = 360.0f / ((float) drawingObjects2.size());
                CarouselView carouselView = CarouselView.this;
                carouselView.lastSelected = (int) (carouselView.offsetAngle / aStep);
                for (int i = 0; i < drawingObjects2.size(); i++) {
                    ((DrawingObject) drawingObjects2.get(i)).hideAnimation();
                }
                return true;
            }

            public void onShowPress(MotionEvent motionEvent) {
            }

            public boolean onSingleTapUp(MotionEvent motionEvent) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                for (int i = CarouselView.this.drawingObjectsSorted.size() - 1; i >= 0; i--) {
                    if (((DrawingObject) CarouselView.this.drawingObjectsSorted.get(i)).checkTap(x, y)) {
                        if (((DrawingObject) CarouselView.this.drawingObjectsSorted.get(i)).angle % 360.0d != 270.0d) {
                            double toAngle = ((270.0d - (((DrawingObject) CarouselView.this.drawingObjectsSorted.get(i)).angle % 360.0d)) + 180.0d) % 360.0d;
                            if (toAngle > 180.0d) {
                                toAngle = -(360.0d - toAngle);
                            }
                            CarouselView carouselView = CarouselView.this;
                            carouselView.scrollToInternal(carouselView.offsetAngle + ((float) toAngle));
                            CarouselView.this.performHapticFeedback(3);
                        }
                        return true;
                    }
                }
                return false;
            }

            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float dx, float dy) {
                double angle = Math.atan2((double) (motionEvent1.getX() - ((float) CarouselView.this.cX)), (double) (motionEvent1.getY() - ((float) CarouselView.this.cY)));
                this.lastAngle = angle;
                CarouselView carouselView = CarouselView.this;
                double d = (double) carouselView.offsetAngle;
                double degrees = Math.toDegrees(this.lastAngle - angle);
                Double.isNaN(d);
                carouselView.offsetAngle = (float) (d + degrees);
                CarouselView.this.checkSelectedHaptic();
                CarouselView.this.invalidate();
                return true;
            }

            public void onLongPress(MotionEvent motionEvent) {
            }

            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float velocityX, float velocityY) {
                CarouselView carouselView = CarouselView.this;
                carouselView.lastFlingY = 0.0f;
                carouselView.lastFlingX = 0.0f;
                double angle = Math.atan2((double) (motionEvent1.getX() - ((float) CarouselView.this.cX)), (double) (motionEvent1.getY() - ((float) CarouselView.this.cY)));
                double cos = Math.cos(angle);
                double d = (double) velocityX;
                Double.isNaN(d);
                double d2 = cos * d;
                double sin = Math.sin(angle);
                double d3 = (double) velocityY;
                Double.isNaN(d3);
                CarouselView.this.overScroller.fling(0, 0, (int) ((float) (d2 - (sin * d3))), 0, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
                if (CarouselView.this.overScroller.isFinished()) {
                    CarouselView.this.scheduleAutoscroll();
                }
                CarouselView.this.invalidate();
                return true;
            }
        });
        this.drawingObjects = drawingObjects2;
        this.drawingObjectsSorted = new ArrayList<>(drawingObjects2);
        for (int i = 0; i < drawingObjects2.size() / 2; i++) {
            ((DrawingObject) drawingObjects2.get(i)).y = ((float) drawingObjects2.size()) / ((float) i);
            ((DrawingObject) drawingObjects2.get((drawingObjects2.size() - 1) - i)).y = ((float) drawingObjects2.size()) / ((float) i);
        }
        Collections.sort(drawingObjects2, this.comparator);
        for (int i2 = 0; i2 < drawingObjects2.size(); i2++) {
            ((DrawingObject) drawingObjects2.get(i2)).carouselView = this;
        }
    }

    /* access modifiers changed from: private */
    public void checkSelectedHaptic() {
        int selected = (int) (this.offsetAngle / (360.0f / ((float) this.drawingObjects.size())));
        if (this.lastSelected != selected) {
            this.lastSelected = selected;
            performHapticFeedback(3);
        }
    }

    /* access modifiers changed from: private */
    public void scrollToInternal(final float scrollTo) {
        if (Math.abs(scrollTo - this.offsetAngle) >= 1.0f || this.autoScrollAnimation != null) {
            AndroidUtilities.cancelRunOnUIThread(this.autoScrollRunnable);
            ValueAnimator valueAnimator = this.autoScrollAnimation;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.autoScrollAnimation.cancel();
                this.autoScrollAnimation = null;
            }
            float from = this.offsetAngle;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.autoScrollAnimation = ofFloat;
            ofFloat.addUpdateListener(new CarouselView$$ExternalSyntheticLambda0(this, from, scrollTo));
            this.autoScrollAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    CarouselView.this.offsetAngle = scrollTo;
                    CarouselView.this.autoScrollAnimation = null;
                    CarouselView.this.invalidate();
                    AndroidUtilities.runOnUIThread(new CarouselView$3$$ExternalSyntheticLambda0(this));
                }

                /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-Components-Premium-CarouselView$3  reason: not valid java name */
                public /* synthetic */ void m1229xeCLASSNAMEa6() {
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

    /* renamed from: lambda$scrollToInternal$2$org-telegram-ui-Components-Premium-CarouselView  reason: not valid java name */
    public /* synthetic */ void m1228x2d4b5a8(float from, float scrollTo, ValueAnimator animation) {
        float f = ((Float) animation.getAnimatedValue()).floatValue();
        this.offsetAngle = ((1.0f - f) * from) + (scrollTo * f);
        invalidate();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == 0) {
            this.scrolled = true;
        } else if (event.getAction() == 1 || event.getAction() == 3) {
            this.scrolled = false;
            getParent().requestDisallowInterceptTouchEvent(false);
            invalidate();
        }
        return this.gestureDetector.onTouchEvent(event);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.cX = getMeasuredWidth() >> 1;
        this.cY = getMeasuredHeight() >> 1;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        for (int k = 0; k < 2; k++) {
            for (int i = 0; i < this.drawingObjectsSorted.size(); i++) {
                ((DrawingObject) this.drawingObjectsSorted.get(i)).onAttachToWindow(this, k);
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
        if (java.lang.Math.abs(r7 % r2) > 2.0d) goto L_0x0076;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r15) {
        /*
            r14 = this;
            java.util.ArrayList<? extends org.telegram.ui.Components.Premium.CarouselView$DrawingObject> r0 = r14.drawingObjects
            int r0 = r0.size()
            double r0 = (double) r0
            r2 = 4645040803167600640(0xNUM, double:360.0)
            java.lang.Double.isNaN(r0)
            double r2 = r2 / r0
            android.widget.OverScroller r0 = r14.overScroller
            boolean r0 = r0.computeScrollOffset()
            r1 = 0
            if (r0 == 0) goto L_0x0052
            android.widget.OverScroller r0 = r14.overScroller
            int r0 = r0.getCurrX()
            float r4 = r14.lastFlingX
            float r5 = (float) r0
            float r5 = r4 - r5
            r6 = 1034147594(0x3da3d70a, float:0.08)
            int r1 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r1 == 0) goto L_0x003d
            float r1 = r5 * r6
            float r1 = java.lang.Math.abs(r1)
            r4 = 1050253722(0x3e99999a, float:0.3)
            int r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r1 >= 0) goto L_0x003d
            android.widget.OverScroller r1 = r14.overScroller
            r1.abortAnimation()
        L_0x003d:
            float r1 = (float) r0
            r14.lastFlingX = r1
            float r1 = r14.offsetAngle
            float r6 = r6 * r5
            float r1 = r1 + r6
            r14.offsetAngle = r1
            r14.checkSelectedHaptic()
            r14.invalidate()
            r14.scheduleAutoscroll()
        L_0x0050:
            goto L_0x00cf
        L_0x0052:
            boolean r0 = r14.firstScroll1
            r4 = 4611686018427387904(0xNUM, double:2.0)
            r6 = 1119092736(0x42b40000, float:90.0)
            if (r0 != 0) goto L_0x0076
            boolean r0 = r14.firstScroll
            if (r0 != 0) goto L_0x0076
            boolean r0 = r14.scrolled
            if (r0 != 0) goto L_0x0050
            android.animation.ValueAnimator r0 = r14.autoScrollAnimation
            if (r0 != 0) goto L_0x0050
            float r0 = r14.offsetAngle
            float r0 = r0 - r6
            double r7 = (double) r0
            java.lang.Double.isNaN(r7)
            double r7 = r7 % r2
            double r7 = java.lang.Math.abs(r7)
            int r0 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
            if (r0 <= 0) goto L_0x0050
        L_0x0076:
            boolean r0 = r14.firstScroll1
            if (r0 == 0) goto L_0x008a
            float r0 = r14.offsetAngle
            double r7 = (double) r0
            r9 = 4636033603912859648(0xNUM, double:90.0)
            double r9 = r9 + r2
            java.lang.Double.isNaN(r7)
            double r7 = r7 + r9
            float r0 = (float) r7
            r14.offsetAngle = r0
        L_0x008a:
            float r0 = r14.offsetAngle
            float r0 = r0 - r6
            double r6 = (double) r0
            java.lang.Double.isNaN(r6)
            double r6 = r6 % r2
            float r0 = (float) r6
            float r6 = java.lang.Math.abs(r0)
            double r6 = (double) r6
            double r4 = r2 / r4
            int r8 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r8 <= 0) goto L_0x00af
            int r1 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r1 >= 0) goto L_0x00a9
            double r4 = (double) r0
            java.lang.Double.isNaN(r4)
            double r4 = r4 + r2
            float r0 = (float) r4
            goto L_0x00af
        L_0x00a9:
            double r4 = (double) r0
            java.lang.Double.isNaN(r4)
            double r4 = r4 - r2
            float r0 = (float) r4
        L_0x00af:
            r1 = 0
            r14.firstScroll1 = r1
            boolean r4 = r14.firstScroll
            if (r4 == 0) goto L_0x00c9
            boolean r4 = r14.firstScrollEnabled
            if (r4 == 0) goto L_0x00c9
            r14.firstScroll = r1
            float r1 = r14.offsetAngle
            r4 = 1127481344(0x43340000, float:180.0)
            float r1 = r1 - r4
            r14.offsetAngle = r1
            float r1 = r1 - r0
            float r1 = r1 + r4
            r14.scrollToInternal(r1)
            goto L_0x00cf
        L_0x00c9:
            float r1 = r14.offsetAngle
            float r1 = r1 - r0
            r14.scrollToInternal(r1)
        L_0x00cf:
            int r0 = r14.getMeasuredWidth()
            float r0 = (float) r0
            int r1 = r14.getMeasuredHeight()
            float r1 = (float) r1
            r4 = 1067869798(0x3fa66666, float:1.3)
            float r1 = r1 * r4
            float r0 = java.lang.Math.min(r0, r1)
            r1 = 1124859904(0x430CLASSNAME, float:140.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r0 = r0 - r1
            r1 = 1056964608(0x3var_, float:0.5)
            float r0 = r0 * r1
            r1 = 1058642330(0x3var_a, float:0.6)
            float r1 = r1 * r0
            r4 = 0
        L_0x00f4:
            java.util.ArrayList<? extends org.telegram.ui.Components.Premium.CarouselView$DrawingObject> r5 = r14.drawingObjects
            int r5 = r5.size()
            if (r4 >= r5) goto L_0x014e
            java.util.ArrayList<? extends org.telegram.ui.Components.Premium.CarouselView$DrawingObject> r5 = r14.drawingObjects
            java.lang.Object r5 = r5.get(r4)
            org.telegram.ui.Components.Premium.CarouselView$DrawingObject r5 = (org.telegram.ui.Components.Premium.CarouselView.DrawingObject) r5
            float r6 = r14.offsetAngle
            double r6 = (double) r6
            double r8 = (double) r4
            java.lang.Double.isNaN(r8)
            double r8 = r8 * r2
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r8
            r5.angle = r6
            double r6 = r5.angle
            double r6 = java.lang.Math.toRadians(r6)
            double r6 = java.lang.Math.cos(r6)
            r8 = r6
            double r10 = r5.angle
            r12 = 4629137466983448576(0x403eNUM, double:30.0)
            double r12 = r12 * r8
            double r10 = r10 - r12
            double r12 = java.lang.Math.toRadians(r10)
            double r12 = java.lang.Math.cos(r12)
            float r12 = (float) r12
            float r12 = r12 * r0
            int r13 = r14.cX
            float r13 = (float) r13
            float r12 = r12 + r13
            r5.x = r12
            double r12 = java.lang.Math.toRadians(r10)
            double r12 = java.lang.Math.sin(r12)
            float r12 = (float) r12
            r5.yRelative = r12
            float r12 = r5.yRelative
            float r12 = r12 * r1
            int r13 = r14.cY
            float r13 = (float) r13
            float r12 = r12 + r13
            r5.y = r12
            int r4 = r4 + 1
            goto L_0x00f4
        L_0x014e:
            java.util.ArrayList<? extends org.telegram.ui.Components.Premium.CarouselView$DrawingObject> r4 = r14.drawingObjectsSorted
            java.util.Comparator<org.telegram.ui.Components.Premium.CarouselView$DrawingObject> r5 = r14.comparator
            java.util.Collections.sort(r4, r5)
            r4 = 0
        L_0x0156:
            java.util.ArrayList<? extends org.telegram.ui.Components.Premium.CarouselView$DrawingObject> r5 = r14.drawingObjectsSorted
            int r5 = r5.size()
            if (r4 >= r5) goto L_0x0181
            java.util.ArrayList<? extends org.telegram.ui.Components.Premium.CarouselView$DrawingObject> r5 = r14.drawingObjectsSorted
            java.lang.Object r5 = r5.get(r4)
            org.telegram.ui.Components.Premium.CarouselView$DrawingObject r5 = (org.telegram.ui.Components.Premium.CarouselView.DrawingObject) r5
            r6 = 1045220557(0x3e4ccccd, float:0.2)
            r7 = 1060320051(0x3var_, float:0.7)
            float r8 = r5.yRelative
            r9 = 1065353216(0x3var_, float:1.0)
            float r8 = r8 + r9
            float r8 = r8 * r7
            r7 = 1073741824(0x40000000, float:2.0)
            float r8 = r8 / r7
            float r8 = r8 + r6
            float r6 = r5.x
            float r7 = r5.y
            r5.draw(r15, r6, r7, r8)
            int r4 = r4 + 1
            goto L_0x0156
        L_0x0181:
            r14.invalidate()
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

    public void setOffset(float translationX) {
        boolean z = true;
        if (translationX >= ((float) getMeasuredWidth()) || translationX <= ((float) (-getMeasuredWidth()))) {
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
        setAutoPlayEnabled(translationX == 0.0f);
        if (Math.abs(translationX) >= ((float) getMeasuredWidth()) * 0.2f) {
            z = false;
        }
        setFirstScrollEnabled(z);
        float s = Utilities.clamp(Math.abs(translationX) / ((float) getMeasuredWidth()), 1.0f, 0.0f);
        setScaleX(1.0f - s);
        setScaleY(1.0f - s);
    }

    public void autoplayToNext() {
        AndroidUtilities.cancelRunOnUIThread(this.autoScrollRunnable);
        if (this.autoPlayEnabled) {
            ArrayList<? extends DrawingObject> arrayList = this.drawingObjectsSorted;
            int i = this.drawingObjects.indexOf((DrawingObject) arrayList.get(arrayList.size() - 1)) - 1;
            if (i < 0) {
                i = this.drawingObjects.size() - 1;
            }
            ((DrawingObject) this.drawingObjects.get(i)).select();
            AndroidUtilities.runOnUIThread(this.autoScrollRunnable, 16);
        }
    }

    public static class DrawingObject {
        public double angle;
        CarouselView carouselView;
        public float x;
        public float y;
        float yRelative;

        public void onAttachToWindow(View parentView, int i) {
        }

        public void onDetachFromWindow() {
        }

        public void draw(Canvas canvas, float cX, float cY, float scale) {
        }

        public boolean checkTap(float x2, float y2) {
            return false;
        }

        public void select() {
        }

        public void hideAnimation() {
        }
    }

    /* access modifiers changed from: package-private */
    public void setAutoPlayEnabled(boolean autoPlayEnabled2) {
        if (this.autoPlayEnabled != autoPlayEnabled2) {
            this.autoPlayEnabled = autoPlayEnabled2;
            if (autoPlayEnabled2) {
                scheduleAutoscroll();
            } else {
                AndroidUtilities.cancelRunOnUIThread(this.autoScrollRunnable);
            }
            invalidate();
        }
    }

    /* access modifiers changed from: package-private */
    public void setFirstScrollEnabled(boolean b) {
        if (this.firstScrollEnabled != b) {
            this.firstScrollEnabled = b;
            invalidate();
        }
    }
}

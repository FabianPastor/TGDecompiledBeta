package org.telegram.ui.Components;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

public class GestureDetector2 {
    public static final int DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();
    private static final int TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
    private boolean mAlwaysInBiggerTapRegion;
    private boolean mAlwaysInTapRegion;
    /* access modifiers changed from: private */
    public MotionEvent mCurrentDownEvent;
    private MotionEvent mCurrentMotionEvent;
    /* access modifiers changed from: private */
    public boolean mDeferConfirmSingleTap;
    /* access modifiers changed from: private */
    public OnDoubleTapListener mDoubleTapListener;
    private int mDoubleTapSlopSquare;
    private int mDoubleTapTouchSlopSquare;
    private float mDownFocusX;
    private float mDownFocusY;
    private final Handler mHandler;
    private boolean mIgnoreNextUpEvent;
    private boolean mInContextClick;
    private boolean mInLongPress;
    private boolean mIsDoubleTapping;
    private boolean mIsLongpressEnabled;
    private float mLastFocusX;
    private float mLastFocusY;
    /* access modifiers changed from: private */
    public final OnGestureListener mListener;
    private int mMaximumFlingVelocity;
    private int mMinimumFlingVelocity;
    private MotionEvent mPreviousUpEvent;
    /* access modifiers changed from: private */
    public boolean mStillDown;
    private int mTouchSlopSquare;
    private VelocityTracker mVelocityTracker;

    public interface OnDoubleTapListener {
        boolean canDoubleTap(MotionEvent motionEvent);

        boolean onDoubleTap(MotionEvent motionEvent);

        boolean onDoubleTapEvent(MotionEvent motionEvent);

        boolean onSingleTapConfirmed(MotionEvent motionEvent);
    }

    public interface OnGestureListener {
        boolean onDown(MotionEvent motionEvent);

        boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2);

        void onLongPress(MotionEvent motionEvent);

        boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2);

        void onShowPress(MotionEvent motionEvent);

        boolean onSingleTapUp(MotionEvent motionEvent);

        void onUp(MotionEvent motionEvent);
    }

    static {
        ViewConfiguration.getLongPressTimeout();
    }

    private class GestureHandler extends Handler {
        GestureHandler() {
        }

        GestureHandler(Handler handler) {
            super(handler.getLooper());
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                GestureDetector2.this.mListener.onShowPress(GestureDetector2.this.mCurrentDownEvent);
            } else if (i == 2) {
                GestureDetector2.this.dispatchLongPress();
            } else if (i != 3) {
                throw new RuntimeException("Unknown message " + message);
            } else if (GestureDetector2.this.mDoubleTapListener == null) {
            } else {
                if (!GestureDetector2.this.mStillDown) {
                    GestureDetector2.this.mDoubleTapListener.onSingleTapConfirmed(GestureDetector2.this.mCurrentDownEvent);
                } else {
                    boolean unused = GestureDetector2.this.mDeferConfirmSingleTap = true;
                }
            }
        }
    }

    public GestureDetector2(Context context, OnGestureListener onGestureListener) {
        this(context, onGestureListener, (Handler) null);
    }

    public GestureDetector2(Context context, OnGestureListener onGestureListener, Handler handler) {
        if (handler != null) {
            this.mHandler = new GestureHandler(handler);
        } else {
            this.mHandler = new GestureHandler();
        }
        this.mListener = onGestureListener;
        if (onGestureListener instanceof OnDoubleTapListener) {
            setOnDoubleTapListener((OnDoubleTapListener) onGestureListener);
        }
        init(context);
    }

    private void init(Context context) {
        int i;
        int i2;
        int i3;
        if (this.mListener != null) {
            this.mIsLongpressEnabled = true;
            if (context == null) {
                i = ViewConfiguration.getTouchSlop();
                i3 = 100;
                this.mMinimumFlingVelocity = ViewConfiguration.getMinimumFlingVelocity();
                this.mMaximumFlingVelocity = ViewConfiguration.getMaximumFlingVelocity();
                i2 = i;
            } else {
                ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
                int scaledTouchSlop = viewConfiguration.getScaledTouchSlop();
                i2 = viewConfiguration.getScaledTouchSlop();
                int scaledDoubleTapSlop = viewConfiguration.getScaledDoubleTapSlop();
                this.mMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
                this.mMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
                i = scaledTouchSlop;
                i3 = scaledDoubleTapSlop;
            }
            this.mTouchSlopSquare = i * i;
            this.mDoubleTapTouchSlopSquare = i2 * i2;
            this.mDoubleTapSlopSquare = i3 * i3;
            return;
        }
        throw new NullPointerException("OnGestureListener must not be null");
    }

    public void setOnDoubleTapListener(OnDoubleTapListener onDoubleTapListener) {
        this.mDoubleTapListener = onDoubleTapListener;
    }

    public void setIsLongpressEnabled(boolean z) {
        this.mIsLongpressEnabled = z;
    }

    /* JADX WARNING: Removed duplicated region for block: B:137:0x02a8  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x02be  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r19) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            int r2 = r19.getAction()
            android.view.MotionEvent r3 = r0.mCurrentMotionEvent
            if (r3 == 0) goto L_0x000f
            r3.recycle()
        L_0x000f:
            android.view.MotionEvent r3 = android.view.MotionEvent.obtain(r19)
            r0.mCurrentMotionEvent = r3
            android.view.VelocityTracker r3 = r0.mVelocityTracker
            if (r3 != 0) goto L_0x001f
            android.view.VelocityTracker r3 = android.view.VelocityTracker.obtain()
            r0.mVelocityTracker = r3
        L_0x001f:
            android.view.VelocityTracker r3 = r0.mVelocityTracker
            r3.addMovement(r1)
            r2 = r2 & 255(0xff, float:3.57E-43)
            r3 = 6
            r4 = 1
            r5 = 0
            if (r2 != r3) goto L_0x002d
            r6 = 1
            goto L_0x002e
        L_0x002d:
            r6 = 0
        L_0x002e:
            if (r6 == 0) goto L_0x0035
            int r7 = r19.getActionIndex()
            goto L_0x0036
        L_0x0035:
            r7 = -1
        L_0x0036:
            int r8 = r19.getPointerCount()
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
        L_0x003e:
            if (r10 >= r8) goto L_0x0050
            if (r7 != r10) goto L_0x0043
            goto L_0x004d
        L_0x0043:
            float r13 = r1.getX(r10)
            float r11 = r11 + r13
            float r13 = r1.getY(r10)
            float r12 = r12 + r13
        L_0x004d:
            int r10 = r10 + 1
            goto L_0x003e
        L_0x0050:
            if (r6 == 0) goto L_0x0055
            int r6 = r8 + -1
            goto L_0x0056
        L_0x0055:
            r6 = r8
        L_0x0056:
            float r6 = (float) r6
            float r11 = r11 / r6
            float r12 = r12 / r6
            r6 = 3
            r7 = 2
            if (r2 == 0) goto L_0x0250
            r10 = 1000(0x3e8, float:1.401E-42)
            if (r2 == r4) goto L_0x01b2
            if (r2 == r7) goto L_0x00cb
            if (r2 == r6) goto L_0x00c6
            r4 = 5
            if (r2 == r4) goto L_0x00b9
            if (r2 == r3) goto L_0x006c
            goto L_0x02f0
        L_0x006c:
            r0.mLastFocusX = r11
            r0.mDownFocusX = r11
            r0.mLastFocusY = r12
            r0.mDownFocusY = r12
            android.view.VelocityTracker r2 = r0.mVelocityTracker
            int r3 = r0.mMaximumFlingVelocity
            float r3 = (float) r3
            r2.computeCurrentVelocity(r10, r3)
            int r2 = r19.getActionIndex()
            int r3 = r1.getPointerId(r2)
            android.view.VelocityTracker r4 = r0.mVelocityTracker
            float r4 = r4.getXVelocity(r3)
            android.view.VelocityTracker r6 = r0.mVelocityTracker
            float r3 = r6.getYVelocity(r3)
            r6 = 0
        L_0x0091:
            if (r6 >= r8) goto L_0x02f0
            if (r6 != r2) goto L_0x0096
            goto L_0x00b6
        L_0x0096:
            int r7 = r1.getPointerId(r6)
            android.view.VelocityTracker r10 = r0.mVelocityTracker
            float r10 = r10.getXVelocity(r7)
            float r10 = r10 * r4
            android.view.VelocityTracker r11 = r0.mVelocityTracker
            float r7 = r11.getYVelocity(r7)
            float r7 = r7 * r3
            float r10 = r10 + r7
            int r7 = (r10 > r9 ? 1 : (r10 == r9 ? 0 : -1))
            if (r7 >= 0) goto L_0x00b6
            android.view.VelocityTracker r1 = r0.mVelocityTracker
            r1.clear()
            goto L_0x02f0
        L_0x00b6:
            int r6 = r6 + 1
            goto L_0x0091
        L_0x00b9:
            r0.mLastFocusX = r11
            r0.mDownFocusX = r11
            r0.mLastFocusY = r12
            r0.mDownFocusY = r12
            r18.cancelTaps()
            goto L_0x02f0
        L_0x00c6:
            r18.cancel()
            goto L_0x02f0
        L_0x00cb:
            boolean r2 = r0.mInLongPress
            if (r2 != 0) goto L_0x02f0
            boolean r2 = r0.mInContextClick
            if (r2 == 0) goto L_0x00d5
            goto L_0x02f0
        L_0x00d5:
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 29
            if (r2 < r3) goto L_0x00e0
            int r2 = r19.getClassification()
            goto L_0x00e1
        L_0x00e0:
            r2 = 0
        L_0x00e1:
            android.os.Handler r8 = r0.mHandler
            boolean r8 = r8.hasMessages(r7)
            float r9 = r0.mLastFocusX
            float r9 = r9 - r11
            float r10 = r0.mLastFocusY
            float r10 = r10 - r12
            boolean r13 = r0.mIsDoubleTapping
            if (r13 == 0) goto L_0x00fa
            org.telegram.ui.Components.GestureDetector2$OnDoubleTapListener r6 = r0.mDoubleTapListener
            boolean r1 = r6.onDoubleTapEvent(r1)
            r1 = r1 | r5
            goto L_0x0193
        L_0x00fa:
            boolean r13 = r0.mAlwaysInTapRegion
            if (r13 == 0) goto L_0x0172
            float r13 = r0.mDownFocusX
            float r13 = r11 - r13
            int r13 = (int) r13
            float r14 = r0.mDownFocusY
            float r14 = r12 - r14
            int r14 = (int) r14
            int r13 = r13 * r13
            int r14 = r14 * r14
            int r13 = r13 + r14
            int r14 = r0.mTouchSlopSquare
            int r15 = android.os.Build.VERSION.SDK_INT
            if (r15 < r3) goto L_0x0117
            if (r2 != r4) goto L_0x0117
            r15 = 1
            goto L_0x0118
        L_0x0117:
            r15 = 0
        L_0x0118:
            if (r8 == 0) goto L_0x011e
            if (r15 == 0) goto L_0x011e
            r15 = 1
            goto L_0x011f
        L_0x011e:
            r15 = 0
        L_0x011f:
            if (r15 == 0) goto L_0x0148
            if (r13 <= r14) goto L_0x0142
            android.os.Handler r15 = r0.mHandler
            r15.removeMessages(r7)
            int r15 = android.view.ViewConfiguration.getLongPressTimeout()
            long r3 = (long) r15
            android.os.Handler r15 = r0.mHandler
            android.os.Message r6 = r15.obtainMessage(r7, r5, r5)
            long r16 = r19.getDownTime()
            float r3 = (float) r3
            r4 = 1073741824(0x40000000, float:2.0)
            float r3 = r3 * r4
            long r3 = (long) r3
            long r3 = r16 + r3
            r15.sendMessageAtTime(r6, r3)
        L_0x0142:
            float r3 = (float) r14
            r4 = 1082130432(0x40800000, float:4.0)
            float r3 = r3 * r4
            int r14 = (int) r3
        L_0x0148:
            if (r13 <= r14) goto L_0x016a
            org.telegram.ui.Components.GestureDetector2$OnGestureListener r3 = r0.mListener
            android.view.MotionEvent r4 = r0.mCurrentDownEvent
            boolean r1 = r3.onScroll(r4, r1, r9, r10)
            r0.mLastFocusX = r11
            r0.mLastFocusY = r12
            r0.mAlwaysInTapRegion = r5
            android.os.Handler r3 = r0.mHandler
            r4 = 3
            r3.removeMessages(r4)
            android.os.Handler r3 = r0.mHandler
            r4 = 1
            r3.removeMessages(r4)
            android.os.Handler r3 = r0.mHandler
            r3.removeMessages(r7)
            goto L_0x016b
        L_0x016a:
            r1 = 0
        L_0x016b:
            int r3 = r0.mDoubleTapTouchSlopSquare
            if (r13 <= r3) goto L_0x0193
            r0.mAlwaysInBiggerTapRegion = r5
            goto L_0x0193
        L_0x0172:
            float r3 = java.lang.Math.abs(r9)
            r4 = 1065353216(0x3var_, float:1.0)
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 >= 0) goto L_0x0187
            float r3 = java.lang.Math.abs(r10)
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 < 0) goto L_0x0185
            goto L_0x0187
        L_0x0185:
            r1 = 0
            goto L_0x0193
        L_0x0187:
            org.telegram.ui.Components.GestureDetector2$OnGestureListener r3 = r0.mListener
            android.view.MotionEvent r4 = r0.mCurrentDownEvent
            boolean r1 = r3.onScroll(r4, r1, r9, r10)
            r0.mLastFocusX = r11
            r0.mLastFocusY = r12
        L_0x0193:
            int r3 = android.os.Build.VERSION.SDK_INT
            r4 = 29
            if (r3 < r4) goto L_0x024d
            if (r2 != r7) goto L_0x019d
            r4 = 1
            goto L_0x019e
        L_0x019d:
            r4 = 0
        L_0x019e:
            if (r4 == 0) goto L_0x024d
            if (r8 == 0) goto L_0x024d
            android.os.Handler r2 = r0.mHandler
            r2.removeMessages(r7)
            android.os.Handler r2 = r0.mHandler
            android.os.Message r3 = r2.obtainMessage(r7, r5, r5)
            r2.sendMessage(r3)
            goto L_0x024d
        L_0x01b2:
            r0.mStillDown = r5
            org.telegram.ui.Components.GestureDetector2$OnGestureListener r2 = r0.mListener
            r2.onUp(r1)
            android.view.MotionEvent r2 = android.view.MotionEvent.obtain(r19)
            boolean r3 = r0.mIsDoubleTapping
            if (r3 == 0) goto L_0x01c9
            org.telegram.ui.Components.GestureDetector2$OnDoubleTapListener r3 = r0.mDoubleTapListener
            boolean r1 = r3.onDoubleTapEvent(r1)
            r1 = r1 | r5
            goto L_0x0229
        L_0x01c9:
            boolean r3 = r0.mInLongPress
            if (r3 == 0) goto L_0x01d6
            android.os.Handler r1 = r0.mHandler
            r3 = 3
            r1.removeMessages(r3)
            r0.mInLongPress = r5
            goto L_0x0228
        L_0x01d6:
            boolean r3 = r0.mAlwaysInTapRegion
            if (r3 == 0) goto L_0x01f1
            boolean r3 = r0.mIgnoreNextUpEvent
            if (r3 != 0) goto L_0x01f1
            org.telegram.ui.Components.GestureDetector2$OnGestureListener r3 = r0.mListener
            boolean r3 = r3.onSingleTapUp(r1)
            boolean r4 = r0.mDeferConfirmSingleTap
            if (r4 == 0) goto L_0x01ef
            org.telegram.ui.Components.GestureDetector2$OnDoubleTapListener r4 = r0.mDoubleTapListener
            if (r4 == 0) goto L_0x01ef
            r4.onSingleTapConfirmed(r1)
        L_0x01ef:
            r1 = r3
            goto L_0x0229
        L_0x01f1:
            boolean r3 = r0.mIgnoreNextUpEvent
            if (r3 != 0) goto L_0x0228
            android.view.VelocityTracker r3 = r0.mVelocityTracker
            int r4 = r1.getPointerId(r5)
            int r6 = r0.mMaximumFlingVelocity
            float r6 = (float) r6
            r3.computeCurrentVelocity(r10, r6)
            float r6 = r3.getYVelocity(r4)
            float r3 = r3.getXVelocity(r4)
            float r4 = java.lang.Math.abs(r6)
            int r8 = r0.mMinimumFlingVelocity
            float r8 = (float) r8
            int r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r4 > 0) goto L_0x021f
            float r4 = java.lang.Math.abs(r3)
            int r8 = r0.mMinimumFlingVelocity
            float r8 = (float) r8
            int r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r4 <= 0) goto L_0x0228
        L_0x021f:
            org.telegram.ui.Components.GestureDetector2$OnGestureListener r4 = r0.mListener
            android.view.MotionEvent r8 = r0.mCurrentDownEvent
            boolean r1 = r4.onFling(r8, r1, r3, r6)
            goto L_0x0229
        L_0x0228:
            r1 = 0
        L_0x0229:
            android.view.MotionEvent r3 = r0.mPreviousUpEvent
            if (r3 == 0) goto L_0x0230
            r3.recycle()
        L_0x0230:
            r0.mPreviousUpEvent = r2
            android.view.VelocityTracker r2 = r0.mVelocityTracker
            if (r2 == 0) goto L_0x023c
            r2.recycle()
            r2 = 0
            r0.mVelocityTracker = r2
        L_0x023c:
            r0.mIsDoubleTapping = r5
            r0.mDeferConfirmSingleTap = r5
            r0.mIgnoreNextUpEvent = r5
            android.os.Handler r2 = r0.mHandler
            r3 = 1
            r2.removeMessages(r3)
            android.os.Handler r2 = r0.mHandler
            r2.removeMessages(r7)
        L_0x024d:
            r5 = r1
            goto L_0x02f0
        L_0x0250:
            r0.mDeferConfirmSingleTap = r5
            org.telegram.ui.Components.GestureDetector2$OnDoubleTapListener r2 = r0.mDoubleTapListener
            if (r2 == 0) goto L_0x029b
            boolean r2 = r2.canDoubleTap(r1)
            if (r2 == 0) goto L_0x0298
            android.os.Handler r2 = r0.mHandler
            r3 = 3
            boolean r2 = r2.hasMessages(r3)
            if (r2 == 0) goto L_0x026a
            android.os.Handler r4 = r0.mHandler
            r4.removeMessages(r3)
        L_0x026a:
            android.view.MotionEvent r3 = r0.mCurrentDownEvent
            if (r3 == 0) goto L_0x028e
            android.view.MotionEvent r4 = r0.mPreviousUpEvent
            if (r4 == 0) goto L_0x028e
            if (r2 == 0) goto L_0x028e
            boolean r2 = r0.isConsideredDoubleTap(r3, r4, r1)
            if (r2 == 0) goto L_0x028e
            r2 = 1
            r0.mIsDoubleTapping = r2
            org.telegram.ui.Components.GestureDetector2$OnDoubleTapListener r2 = r0.mDoubleTapListener
            android.view.MotionEvent r3 = r0.mCurrentDownEvent
            boolean r2 = r2.onDoubleTap(r3)
            r2 = r2 | r5
            org.telegram.ui.Components.GestureDetector2$OnDoubleTapListener r3 = r0.mDoubleTapListener
            boolean r3 = r3.onDoubleTapEvent(r1)
            r2 = r2 | r3
            goto L_0x029c
        L_0x028e:
            android.os.Handler r2 = r0.mHandler
            int r3 = DOUBLE_TAP_TIMEOUT
            long r3 = (long) r3
            r6 = 3
            r2.sendEmptyMessageDelayed(r6, r3)
            goto L_0x029b
        L_0x0298:
            r2 = 1
            r0.mDeferConfirmSingleTap = r2
        L_0x029b:
            r2 = 0
        L_0x029c:
            r0.mLastFocusX = r11
            r0.mDownFocusX = r11
            r0.mLastFocusY = r12
            r0.mDownFocusY = r12
            android.view.MotionEvent r3 = r0.mCurrentDownEvent
            if (r3 == 0) goto L_0x02ab
            r3.recycle()
        L_0x02ab:
            android.view.MotionEvent r3 = android.view.MotionEvent.obtain(r19)
            r0.mCurrentDownEvent = r3
            r3 = 1
            r0.mAlwaysInTapRegion = r3
            r0.mAlwaysInBiggerTapRegion = r3
            r0.mStillDown = r3
            r0.mInLongPress = r5
            boolean r3 = r0.mIsLongpressEnabled
            if (r3 == 0) goto L_0x02d8
            android.os.Handler r3 = r0.mHandler
            r3.removeMessages(r7)
            android.os.Handler r3 = r0.mHandler
            android.os.Message r4 = r3.obtainMessage(r7, r5, r5)
            android.view.MotionEvent r5 = r0.mCurrentDownEvent
            long r5 = r5.getDownTime()
            int r7 = android.view.ViewConfiguration.getLongPressTimeout()
            long r7 = (long) r7
            long r5 = r5 + r7
            r3.sendMessageAtTime(r4, r5)
        L_0x02d8:
            android.os.Handler r3 = r0.mHandler
            android.view.MotionEvent r4 = r0.mCurrentDownEvent
            long r4 = r4.getDownTime()
            int r6 = TAP_TIMEOUT
            long r6 = (long) r6
            long r4 = r4 + r6
            r6 = 1
            r3.sendEmptyMessageAtTime(r6, r4)
            org.telegram.ui.Components.GestureDetector2$OnGestureListener r3 = r0.mListener
            boolean r1 = r3.onDown(r1)
            r5 = r2 | r1
        L_0x02f0:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GestureDetector2.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void cancel() {
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(2);
        this.mHandler.removeMessages(3);
        this.mVelocityTracker.recycle();
        this.mVelocityTracker = null;
        this.mIsDoubleTapping = false;
        this.mStillDown = false;
        this.mAlwaysInTapRegion = false;
        this.mAlwaysInBiggerTapRegion = false;
        this.mDeferConfirmSingleTap = false;
        this.mInLongPress = false;
        this.mInContextClick = false;
        this.mIgnoreNextUpEvent = false;
    }

    private void cancelTaps() {
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(2);
        this.mHandler.removeMessages(3);
        this.mIsDoubleTapping = false;
        this.mAlwaysInTapRegion = false;
        this.mAlwaysInBiggerTapRegion = false;
        this.mDeferConfirmSingleTap = false;
        this.mInLongPress = false;
        this.mInContextClick = false;
        this.mIgnoreNextUpEvent = false;
    }

    private boolean isConsideredDoubleTap(MotionEvent motionEvent, MotionEvent motionEvent2, MotionEvent motionEvent3) {
        if (!this.mAlwaysInBiggerTapRegion) {
            return false;
        }
        long eventTime = motionEvent3.getEventTime() - motionEvent2.getEventTime();
        if (eventTime > ((long) DOUBLE_TAP_TIMEOUT) || eventTime < 40) {
            return false;
        }
        int x = ((int) motionEvent.getX()) - ((int) motionEvent3.getX());
        int y = ((int) motionEvent.getY()) - ((int) motionEvent3.getY());
        if ((x * x) + (y * y) < this.mDoubleTapSlopSquare) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void dispatchLongPress() {
        this.mHandler.removeMessages(3);
        this.mDeferConfirmSingleTap = false;
        this.mInLongPress = true;
        this.mListener.onLongPress(this.mCurrentDownEvent);
    }
}

package org.telegram.ui.Components;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

public class GestureDetector2 {
    private static final int DOUBLE_TAP_MIN_TIME = 40;
    public static final int DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();
    private static final int LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();
    private static final int LONG_PRESS = 2;
    private static final int SHOW_PRESS = 1;
    private static final int TAP = 3;
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

    public interface OnGestureListener {
        boolean onDown(MotionEvent motionEvent);

        boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2);

        void onLongPress(MotionEvent motionEvent);

        boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2);

        void onShowPress(MotionEvent motionEvent);

        boolean onSingleTapUp(MotionEvent motionEvent);

        void onUp(MotionEvent motionEvent);
    }

    public interface OnDoubleTapListener {
        boolean canDoubleTap(MotionEvent motionEvent);

        boolean onDoubleTap(MotionEvent motionEvent);

        boolean onDoubleTapEvent(MotionEvent motionEvent);

        boolean onSingleTapConfirmed(MotionEvent motionEvent);

        /* renamed from: org.telegram.ui.Components.GestureDetector2$OnDoubleTapListener$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static boolean $default$canDoubleTap(OnDoubleTapListener _this, MotionEvent e) {
                return true;
            }
        }
    }

    private class GestureHandler extends Handler {
        GestureHandler() {
        }

        GestureHandler(Handler handler) {
            super(handler.getLooper());
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    GestureDetector2.this.mListener.onShowPress(GestureDetector2.this.mCurrentDownEvent);
                    return;
                case 2:
                    GestureDetector2.this.dispatchLongPress();
                    return;
                case 3:
                    if (GestureDetector2.this.mDoubleTapListener == null) {
                        return;
                    }
                    if (!GestureDetector2.this.mStillDown) {
                        GestureDetector2.this.mDoubleTapListener.onSingleTapConfirmed(GestureDetector2.this.mCurrentDownEvent);
                        return;
                    } else {
                        boolean unused = GestureDetector2.this.mDeferConfirmSingleTap = true;
                        return;
                    }
                default:
                    throw new RuntimeException("Unknown message " + msg);
            }
        }
    }

    @Deprecated
    public GestureDetector2(OnGestureListener listener, Handler handler) {
        this((Context) null, listener, handler);
    }

    @Deprecated
    public GestureDetector2(OnGestureListener listener) {
        this((Context) null, listener, (Handler) null);
    }

    public GestureDetector2(Context context, OnGestureListener listener) {
        this(context, listener, (Handler) null);
    }

    public GestureDetector2(Context context, OnGestureListener listener, Handler handler) {
        if (handler != null) {
            this.mHandler = new GestureHandler(handler);
        } else {
            this.mHandler = new GestureHandler();
        }
        this.mListener = listener;
        if (listener instanceof OnDoubleTapListener) {
            setOnDoubleTapListener((OnDoubleTapListener) listener);
        }
        init(context);
    }

    public GestureDetector2(Context context, OnGestureListener listener, Handler handler, boolean unused) {
        this(context, listener, handler);
    }

    private void init(Context context) {
        int doubleTapTouchSlop;
        int touchSlop;
        int touchSlop2;
        if (this.mListener != null) {
            this.mIsLongpressEnabled = true;
            if (context == null) {
                touchSlop2 = ViewConfiguration.getTouchSlop();
                touchSlop = touchSlop2;
                doubleTapTouchSlop = 100;
                this.mMinimumFlingVelocity = ViewConfiguration.getMinimumFlingVelocity();
                this.mMaximumFlingVelocity = ViewConfiguration.getMaximumFlingVelocity();
            } else {
                ViewConfiguration configuration = ViewConfiguration.get(context);
                int touchSlop3 = configuration.getScaledTouchSlop();
                int doubleTapTouchSlop2 = configuration.getScaledTouchSlop();
                int doubleTapSlop = configuration.getScaledDoubleTapSlop();
                this.mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
                this.mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
                touchSlop2 = touchSlop3;
                touchSlop = doubleTapTouchSlop2;
                doubleTapTouchSlop = doubleTapSlop;
            }
            this.mTouchSlopSquare = touchSlop2 * touchSlop2;
            this.mDoubleTapTouchSlopSquare = touchSlop * touchSlop;
            this.mDoubleTapSlopSquare = doubleTapTouchSlop * doubleTapTouchSlop;
            return;
        }
        throw new NullPointerException("OnGestureListener must not be null");
    }

    public void setOnDoubleTapListener(OnDoubleTapListener onDoubleTapListener) {
        this.mDoubleTapListener = onDoubleTapListener;
    }

    public void setIsLongpressEnabled(boolean isLongpressEnabled) {
        this.mIsLongpressEnabled = isLongpressEnabled;
    }

    public boolean isLongpressEnabled() {
        return this.mIsLongpressEnabled;
    }

    /* JADX WARNING: Removed duplicated region for block: B:153:0x03b1  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x03c8  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r36) {
        /*
            r35 = this;
            r0 = r35
            r1 = r36
            int r2 = r36.getAction()
            android.view.MotionEvent r3 = r0.mCurrentMotionEvent
            if (r3 == 0) goto L_0x000f
            r3.recycle()
        L_0x000f:
            android.view.MotionEvent r3 = android.view.MotionEvent.obtain(r36)
            r0.mCurrentMotionEvent = r3
            android.view.VelocityTracker r3 = r0.mVelocityTracker
            if (r3 != 0) goto L_0x001f
            android.view.VelocityTracker r3 = android.view.VelocityTracker.obtain()
            r0.mVelocityTracker = r3
        L_0x001f:
            android.view.VelocityTracker r3 = r0.mVelocityTracker
            r3.addMovement(r1)
            r3 = r2 & 255(0xff, float:3.57E-43)
            r4 = 6
            if (r3 != r4) goto L_0x002b
            r3 = 1
            goto L_0x002c
        L_0x002b:
            r3 = 0
        L_0x002c:
            if (r3 == 0) goto L_0x0033
            int r4 = r36.getActionIndex()
            goto L_0x0034
        L_0x0033:
            r4 = -1
        L_0x0034:
            r7 = 0
            r8 = 0
            int r9 = r36.getPointerCount()
            r10 = 0
        L_0x003b:
            if (r10 >= r9) goto L_0x004d
            if (r4 != r10) goto L_0x0040
            goto L_0x004a
        L_0x0040:
            float r11 = r1.getX(r10)
            float r7 = r7 + r11
            float r11 = r1.getY(r10)
            float r8 = r8 + r11
        L_0x004a:
            int r10 = r10 + 1
            goto L_0x003b
        L_0x004d:
            if (r3 == 0) goto L_0x0052
            int r10 = r9 + -1
            goto L_0x0053
        L_0x0052:
            r10 = r9
        L_0x0053:
            float r11 = (float) r10
            float r11 = r7 / r11
            float r12 = (float) r10
            float r12 = r8 / r12
            r13 = 0
            r14 = r2 & 255(0xff, float:3.57E-43)
            r15 = 1000(0x3e8, float:1.401E-42)
            r6 = 2
            switch(r14) {
                case 0: goto L_0x0343;
                case 1: goto L_0x0289;
                case 2: goto L_0x0122;
                case 3: goto L_0x010d;
                case 4: goto L_0x0062;
                case 5: goto L_0x00f0;
                case 6: goto L_0x0074;
                default: goto L_0x0062;
            }
        L_0x0062:
            r18 = r2
            r19 = r3
            r20 = r4
            r24 = r7
            r28 = r8
            r29 = r9
            r30 = r10
            r31 = r13
            goto L_0x03fb
        L_0x0074:
            r0.mLastFocusX = r11
            r0.mDownFocusX = r11
            r0.mLastFocusY = r12
            r0.mDownFocusY = r12
            android.view.VelocityTracker r5 = r0.mVelocityTracker
            int r6 = r0.mMaximumFlingVelocity
            float r6 = (float) r6
            r5.computeCurrentVelocity(r15, r6)
            int r5 = r36.getActionIndex()
            int r6 = r1.getPointerId(r5)
            android.view.VelocityTracker r14 = r0.mVelocityTracker
            float r14 = r14.getXVelocity(r6)
            android.view.VelocityTracker r15 = r0.mVelocityTracker
            float r15 = r15.getYVelocity(r6)
            r16 = 0
            r18 = r2
            r2 = r16
        L_0x009e:
            if (r2 >= r9) goto L_0x00de
            if (r2 != r5) goto L_0x00a9
            r19 = r3
            r20 = r4
            r16 = r5
            goto L_0x00d5
        L_0x00a9:
            r19 = r3
            int r3 = r1.getPointerId(r2)
            r20 = r4
            android.view.VelocityTracker r4 = r0.mVelocityTracker
            float r4 = r4.getXVelocity(r3)
            float r4 = r4 * r14
            r16 = r5
            android.view.VelocityTracker r5 = r0.mVelocityTracker
            float r5 = r5.getYVelocity(r3)
            float r5 = r5 * r15
            float r17 = r4 + r5
            r21 = 0
            int r21 = (r17 > r21 ? 1 : (r17 == r21 ? 0 : -1))
            if (r21 >= 0) goto L_0x00d3
            r21 = r3
            android.view.VelocityTracker r3 = r0.mVelocityTracker
            r3.clear()
            goto L_0x00e4
        L_0x00d3:
            r21 = r3
        L_0x00d5:
            int r2 = r2 + 1
            r5 = r16
            r3 = r19
            r4 = r20
            goto L_0x009e
        L_0x00de:
            r19 = r3
            r20 = r4
            r16 = r5
        L_0x00e4:
            r24 = r7
            r28 = r8
            r29 = r9
            r30 = r10
            r31 = r13
            goto L_0x03fb
        L_0x00f0:
            r18 = r2
            r19 = r3
            r20 = r4
            r0.mLastFocusX = r11
            r0.mDownFocusX = r11
            r0.mLastFocusY = r12
            r0.mDownFocusY = r12
            r35.cancelTaps()
            r24 = r7
            r28 = r8
            r29 = r9
            r30 = r10
            r31 = r13
            goto L_0x03fb
        L_0x010d:
            r18 = r2
            r19 = r3
            r20 = r4
            r35.cancel()
            r24 = r7
            r28 = r8
            r29 = r9
            r30 = r10
            r31 = r13
            goto L_0x03fb
        L_0x0122:
            r18 = r2
            r19 = r3
            r20 = r4
            boolean r2 = r0.mInLongPress
            if (r2 != 0) goto L_0x027d
            boolean r2 = r0.mInContextClick
            if (r2 == 0) goto L_0x013c
            r24 = r7
            r28 = r8
            r29 = r9
            r30 = r10
            r31 = r13
            goto L_0x03fb
        L_0x013c:
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 29
            if (r2 < r3) goto L_0x0147
            int r2 = r36.getClassification()
            goto L_0x0148
        L_0x0147:
            r2 = 0
        L_0x0148:
            android.os.Handler r4 = r0.mHandler
            boolean r4 = r4.hasMessages(r6)
            float r14 = r0.mLastFocusX
            float r14 = r14 - r11
            float r15 = r0.mLastFocusY
            float r15 = r15 - r12
            boolean r6 = r0.mIsDoubleTapping
            if (r6 == 0) goto L_0x0173
            org.telegram.ui.Components.GestureDetector2$OnDoubleTapListener r6 = r0.mDoubleTapListener
            if (r6 == 0) goto L_0x0164
            boolean r6 = r6.onDoubleTapEvent(r1)
            if (r6 == 0) goto L_0x0164
            r6 = 1
            goto L_0x0165
        L_0x0164:
            r6 = 0
        L_0x0165:
            r6 = r6 | r13
            r32 = r4
            r13 = r6
            r24 = r7
            r28 = r8
            r29 = r9
            r30 = r10
            goto L_0x025a
        L_0x0173:
            boolean r6 = r0.mAlwaysInTapRegion
            if (r6 == 0) goto L_0x022b
            float r6 = r0.mDownFocusX
            float r6 = r11 - r6
            int r6 = (int) r6
            float r5 = r0.mDownFocusY
            float r5 = r12 - r5
            int r5 = (int) r5
            int r22 = r6 * r6
            int r23 = r5 * r5
            int r3 = r22 + r23
            r22 = r5
            int r5 = r0.mTouchSlopSquare
            r23 = r6
            int r6 = android.os.Build.VERSION.SDK_INT
            r24 = r7
            r7 = 29
            if (r6 < r7) goto L_0x019a
            r6 = 1
            if (r2 != r6) goto L_0x019a
            r6 = 1
            goto L_0x019b
        L_0x019a:
            r6 = 0
        L_0x019b:
            if (r4 == 0) goto L_0x01a1
            if (r6 == 0) goto L_0x01a1
            r7 = 1
            goto L_0x01a2
        L_0x01a1:
            r7 = 0
        L_0x01a2:
            if (r7 == 0) goto L_0x01ef
            r25 = 1073741824(0x40000000, float:2.0)
            if (r3 <= r5) goto L_0x01da
            r26 = r6
            android.os.Handler r6 = r0.mHandler
            r27 = r7
            r7 = 2
            r6.removeMessages(r7)
            int r6 = android.view.ViewConfiguration.getLongPressTimeout()
            r28 = r8
            long r7 = (long) r6
            android.os.Handler r6 = r0.mHandler
            r29 = r9
            r30 = r10
            r31 = r13
            r9 = 2
            r10 = 0
            android.os.Message r13 = r6.obtainMessage(r9, r10, r10)
            long r9 = r36.getDownTime()
            r32 = r4
            float r4 = (float) r7
            r33 = 1073741824(0x40000000, float:2.0)
            float r4 = r4 * r33
            r33 = r7
            long r7 = (long) r4
            long r9 = r9 + r7
            r6.sendMessageAtTime(r13, r9)
            goto L_0x01e8
        L_0x01da:
            r32 = r4
            r26 = r6
            r27 = r7
            r28 = r8
            r29 = r9
            r30 = r10
            r31 = r13
        L_0x01e8:
            float r4 = (float) r5
            r6 = 1082130432(0x40800000, float:4.0)
            float r4 = r4 * r6
            int r5 = (int) r4
            goto L_0x01fd
        L_0x01ef:
            r32 = r4
            r26 = r6
            r27 = r7
            r28 = r8
            r29 = r9
            r30 = r10
            r31 = r13
        L_0x01fd:
            if (r3 <= r5) goto L_0x0221
            org.telegram.ui.Components.GestureDetector2$OnGestureListener r4 = r0.mListener
            android.view.MotionEvent r6 = r0.mCurrentDownEvent
            boolean r13 = r4.onScroll(r6, r1, r14, r15)
            r0.mLastFocusX = r11
            r0.mLastFocusY = r12
            r4 = 0
            r0.mAlwaysInTapRegion = r4
            android.os.Handler r4 = r0.mHandler
            r6 = 3
            r4.removeMessages(r6)
            android.os.Handler r4 = r0.mHandler
            r6 = 1
            r4.removeMessages(r6)
            android.os.Handler r4 = r0.mHandler
            r6 = 2
            r4.removeMessages(r6)
            goto L_0x0223
        L_0x0221:
            r13 = r31
        L_0x0223:
            int r4 = r0.mDoubleTapTouchSlopSquare
            if (r3 <= r4) goto L_0x022a
            r6 = 0
            r0.mAlwaysInBiggerTapRegion = r6
        L_0x022a:
            goto L_0x025a
        L_0x022b:
            r32 = r4
            r24 = r7
            r28 = r8
            r29 = r9
            r30 = r10
            r31 = r13
            float r3 = java.lang.Math.abs(r14)
            r4 = 1065353216(0x3var_, float:1.0)
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 >= 0) goto L_0x024d
            float r3 = java.lang.Math.abs(r15)
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 < 0) goto L_0x024a
            goto L_0x024d
        L_0x024a:
            r13 = r31
            goto L_0x025a
        L_0x024d:
            org.telegram.ui.Components.GestureDetector2$OnGestureListener r3 = r0.mListener
            android.view.MotionEvent r4 = r0.mCurrentDownEvent
            boolean r3 = r3.onScroll(r4, r1, r14, r15)
            r0.mLastFocusX = r11
            r0.mLastFocusY = r12
            r13 = r3
        L_0x025a:
            int r3 = android.os.Build.VERSION.SDK_INT
            r4 = 29
            if (r3 < r4) goto L_0x03fd
            r3 = 2
            if (r2 != r3) goto L_0x0265
            r5 = 1
            goto L_0x0266
        L_0x0265:
            r5 = 0
        L_0x0266:
            r3 = r5
            if (r3 == 0) goto L_0x027b
            if (r32 == 0) goto L_0x027b
            android.os.Handler r4 = r0.mHandler
            r5 = 2
            r4.removeMessages(r5)
            android.os.Handler r4 = r0.mHandler
            r6 = 0
            android.os.Message r5 = r4.obtainMessage(r5, r6, r6)
            r4.sendMessage(r5)
        L_0x027b:
            goto L_0x03fd
        L_0x027d:
            r24 = r7
            r28 = r8
            r29 = r9
            r30 = r10
            r31 = r13
            goto L_0x03fb
        L_0x0289:
            r18 = r2
            r19 = r3
            r20 = r4
            r24 = r7
            r28 = r8
            r29 = r9
            r30 = r10
            r31 = r13
            r2 = 0
            r0.mStillDown = r2
            org.telegram.ui.Components.GestureDetector2$OnGestureListener r2 = r0.mListener
            r2.onUp(r1)
            android.view.MotionEvent r2 = android.view.MotionEvent.obtain(r36)
            boolean r3 = r0.mIsDoubleTapping
            if (r3 == 0) goto L_0x02b9
            org.telegram.ui.Components.GestureDetector2$OnDoubleTapListener r3 = r0.mDoubleTapListener
            if (r3 == 0) goto L_0x02b5
            boolean r3 = r3.onDoubleTapEvent(r1)
            if (r3 == 0) goto L_0x02b5
            r3 = 1
            goto L_0x02b6
        L_0x02b5:
            r3 = 0
        L_0x02b6:
            r13 = r31 | r3
            goto L_0x031b
        L_0x02b9:
            boolean r3 = r0.mInLongPress
            if (r3 == 0) goto L_0x02c7
            android.os.Handler r3 = r0.mHandler
            r4 = 3
            r3.removeMessages(r4)
            r3 = 0
            r0.mInLongPress = r3
            goto L_0x0319
        L_0x02c7:
            boolean r3 = r0.mAlwaysInTapRegion
            if (r3 == 0) goto L_0x02e1
            boolean r3 = r0.mIgnoreNextUpEvent
            if (r3 != 0) goto L_0x02e1
            org.telegram.ui.Components.GestureDetector2$OnGestureListener r3 = r0.mListener
            boolean r13 = r3.onSingleTapUp(r1)
            boolean r3 = r0.mDeferConfirmSingleTap
            if (r3 == 0) goto L_0x031b
            org.telegram.ui.Components.GestureDetector2$OnDoubleTapListener r3 = r0.mDoubleTapListener
            if (r3 == 0) goto L_0x031b
            r3.onSingleTapConfirmed(r1)
            goto L_0x031b
        L_0x02e1:
            boolean r3 = r0.mIgnoreNextUpEvent
            if (r3 != 0) goto L_0x0319
            android.view.VelocityTracker r3 = r0.mVelocityTracker
            r4 = 0
            int r5 = r1.getPointerId(r4)
            int r4 = r0.mMaximumFlingVelocity
            float r4 = (float) r4
            r3.computeCurrentVelocity(r15, r4)
            float r4 = r3.getYVelocity(r5)
            float r6 = r3.getXVelocity(r5)
            float r7 = java.lang.Math.abs(r4)
            int r8 = r0.mMinimumFlingVelocity
            float r8 = (float) r8
            int r7 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r7 > 0) goto L_0x0310
            float r7 = java.lang.Math.abs(r6)
            int r8 = r0.mMinimumFlingVelocity
            float r8 = (float) r8
            int r7 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r7 <= 0) goto L_0x0319
        L_0x0310:
            org.telegram.ui.Components.GestureDetector2$OnGestureListener r7 = r0.mListener
            android.view.MotionEvent r8 = r0.mCurrentDownEvent
            boolean r13 = r7.onFling(r8, r1, r6, r4)
            goto L_0x031b
        L_0x0319:
            r13 = r31
        L_0x031b:
            android.view.MotionEvent r3 = r0.mPreviousUpEvent
            if (r3 == 0) goto L_0x0322
            r3.recycle()
        L_0x0322:
            r0.mPreviousUpEvent = r2
            android.view.VelocityTracker r3 = r0.mVelocityTracker
            if (r3 == 0) goto L_0x032e
            r3.recycle()
            r3 = 0
            r0.mVelocityTracker = r3
        L_0x032e:
            r3 = 0
            r0.mIsDoubleTapping = r3
            r0.mDeferConfirmSingleTap = r3
            r0.mIgnoreNextUpEvent = r3
            android.os.Handler r3 = r0.mHandler
            r4 = 1
            r3.removeMessages(r4)
            android.os.Handler r3 = r0.mHandler
            r4 = 2
            r3.removeMessages(r4)
            goto L_0x03fd
        L_0x0343:
            r18 = r2
            r19 = r3
            r20 = r4
            r24 = r7
            r28 = r8
            r29 = r9
            r30 = r10
            r31 = r13
            r3 = 0
            r0.mDeferConfirmSingleTap = r3
            org.telegram.ui.Components.GestureDetector2$OnDoubleTapListener r2 = r0.mDoubleTapListener
            if (r2 == 0) goto L_0x03a3
            boolean r2 = r2.canDoubleTap(r1)
            if (r2 == 0) goto L_0x03a0
            android.os.Handler r2 = r0.mHandler
            r3 = 3
            boolean r2 = r2.hasMessages(r3)
            if (r2 == 0) goto L_0x036e
            android.os.Handler r4 = r0.mHandler
            r4.removeMessages(r3)
        L_0x036e:
            android.view.MotionEvent r3 = r0.mCurrentDownEvent
            if (r3 == 0) goto L_0x0394
            android.view.MotionEvent r4 = r0.mPreviousUpEvent
            if (r4 == 0) goto L_0x0394
            if (r2 == 0) goto L_0x0394
            boolean r3 = r0.isConsideredDoubleTap(r3, r4, r1)
            if (r3 == 0) goto L_0x0394
            r3 = 1
            r0.mIsDoubleTapping = r3
            org.telegram.ui.Components.GestureDetector2$OnDoubleTapListener r3 = r0.mDoubleTapListener
            android.view.MotionEvent r4 = r0.mCurrentDownEvent
            boolean r3 = r3.onDoubleTap(r4)
            r3 = r31 | r3
            org.telegram.ui.Components.GestureDetector2$OnDoubleTapListener r4 = r0.mDoubleTapListener
            boolean r4 = r4.onDoubleTapEvent(r1)
            r3 = r3 | r4
            r13 = r3
            goto L_0x039f
        L_0x0394:
            android.os.Handler r3 = r0.mHandler
            int r4 = DOUBLE_TAP_TIMEOUT
            long r4 = (long) r4
            r6 = 3
            r3.sendEmptyMessageDelayed(r6, r4)
            r13 = r31
        L_0x039f:
            goto L_0x03a5
        L_0x03a0:
            r2 = 1
            r0.mDeferConfirmSingleTap = r2
        L_0x03a3:
            r13 = r31
        L_0x03a5:
            r0.mLastFocusX = r11
            r0.mDownFocusX = r11
            r0.mLastFocusY = r12
            r0.mDownFocusY = r12
            android.view.MotionEvent r2 = r0.mCurrentDownEvent
            if (r2 == 0) goto L_0x03b4
            r2.recycle()
        L_0x03b4:
            android.view.MotionEvent r2 = android.view.MotionEvent.obtain(r36)
            r0.mCurrentDownEvent = r2
            r2 = 1
            r0.mAlwaysInTapRegion = r2
            r0.mAlwaysInBiggerTapRegion = r2
            r0.mStillDown = r2
            r2 = 0
            r0.mInLongPress = r2
            boolean r3 = r0.mIsLongpressEnabled
            if (r3 == 0) goto L_0x03e3
            android.os.Handler r3 = r0.mHandler
            r4 = 2
            r3.removeMessages(r4)
            android.os.Handler r3 = r0.mHandler
            android.os.Message r2 = r3.obtainMessage(r4, r2, r2)
            android.view.MotionEvent r4 = r0.mCurrentDownEvent
            long r4 = r4.getDownTime()
            int r6 = android.view.ViewConfiguration.getLongPressTimeout()
            long r6 = (long) r6
            long r4 = r4 + r6
            r3.sendMessageAtTime(r2, r4)
        L_0x03e3:
            android.os.Handler r2 = r0.mHandler
            android.view.MotionEvent r3 = r0.mCurrentDownEvent
            long r3 = r3.getDownTime()
            int r5 = TAP_TIMEOUT
            long r5 = (long) r5
            long r3 = r3 + r5
            r5 = 1
            r2.sendEmptyMessageAtTime(r5, r3)
            org.telegram.ui.Components.GestureDetector2$OnGestureListener r2 = r0.mListener
            boolean r2 = r2.onDown(r1)
            r13 = r13 | r2
            goto L_0x03fd
        L_0x03fb:
            r13 = r31
        L_0x03fd:
            return r13
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

    private boolean isConsideredDoubleTap(MotionEvent firstDown, MotionEvent firstUp, MotionEvent secondDown) {
        if (!this.mAlwaysInBiggerTapRegion) {
            return false;
        }
        long deltaTime = secondDown.getEventTime() - firstUp.getEventTime();
        if (deltaTime > ((long) DOUBLE_TAP_TIMEOUT) || deltaTime < 40) {
            return false;
        }
        int deltaX = ((int) firstDown.getX()) - ((int) secondDown.getX());
        int deltaY = ((int) firstDown.getY()) - ((int) secondDown.getY());
        if ((deltaX * deltaX) + (deltaY * deltaY) < this.mDoubleTapSlopSquare) {
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

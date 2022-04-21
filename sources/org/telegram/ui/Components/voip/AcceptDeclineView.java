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
import android.os.SystemClock;
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
    long capturedTime;
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

        void onDecline();
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public AcceptDeclineView(Context context) {
        super(context);
        Context context2 = context;
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
        String acceptStr = LocaleController.getString("AcceptCall", NUM);
        String declineStr = LocaleController.getString("DeclineCall", NUM);
        String retryStr = LocaleController.getString("RetryCall", NUM);
        TextPaint textPaint2 = textPaint;
        StaticLayout staticLayout = r6;
        StaticLayout staticLayout2 = new StaticLayout(acceptStr, textPaint2, (int) textPaint.measureText(acceptStr), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        this.acceptLayout = staticLayout;
        this.declineLayout = new StaticLayout(declineStr, textPaint2, (int) textPaint.measureText(declineStr), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        this.retryLayout = new StaticLayout(retryStr, textPaint2, (int) textPaint.measureText(retryStr), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        this.callDrawable = ContextCompat.getDrawable(context2, NUM).mutate();
        Drawable mutate = ContextCompat.getDrawable(context2, NUM).mutate();
        this.cancelDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
        this.acceptCirclePaint.setColor(NUM);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(52.0f), 0, ColorUtils.setAlphaComponent(-1, 76));
        this.rippleDrawable = createSimpleSelectorCircleDrawable;
        createSimpleSelectorCircleDrawable.setCallback(this);
        this.arrowDrawable = ContextCompat.getDrawable(context2, NUM);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.maxOffset = (((float) getMeasuredWidth()) / 2.0f) - ((((float) this.buttonWidth) / 2.0f) + ((float) AndroidUtilities.dp(46.0f)));
        int padding = (this.buttonWidth - AndroidUtilities.dp(28.0f)) / 2;
        this.callDrawable.setBounds(padding, padding, AndroidUtilities.dp(28.0f) + padding, AndroidUtilities.dp(28.0f) + padding);
        this.cancelDrawable.setBounds(padding, padding, AndroidUtilities.dp(28.0f) + padding, AndroidUtilities.dp(28.0f) + padding);
        this.linePaint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        this.linePaint.setColor(-1);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        switch (event.getAction()) {
            case 0:
                this.startX = event.getX();
                this.startY = event.getY();
                if (this.leftAnimator == null && this.declineRect.contains((int) event.getX(), (int) event.getY())) {
                    this.rippleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(52.0f), 0, -51130);
                    this.captured = true;
                    this.leftDrag = true;
                    setPressed(true);
                    return true;
                } else if (this.rightAnimator == null && this.acceptRect.contains((int) event.getX(), (int) event.getY())) {
                    this.rippleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(52.0f), 0, -11677354);
                    this.captured = true;
                    this.leftDrag = false;
                    setPressed(true);
                    Animator animator = this.rightAnimator;
                    if (animator != null) {
                        animator.cancel();
                    }
                    return true;
                }
                break;
            case 1:
            case 3:
                float dy = event.getY() - this.startY;
                if (this.captured) {
                    if (this.leftDrag) {
                        ValueAnimator animator2 = ValueAnimator.ofFloat(new float[]{this.leftOffsetX, 0.0f});
                        animator2.addUpdateListener(new AcceptDeclineView$$ExternalSyntheticLambda0(this));
                        animator2.start();
                        this.leftAnimator = animator2;
                        if (this.listener != null && ((!this.startDrag && Math.abs(dy) < this.touchSlop && !this.screenWasWakeup) || this.leftOffsetX > this.maxOffset * 0.8f)) {
                            this.listener.onDecline();
                        }
                    } else {
                        ValueAnimator animator3 = ValueAnimator.ofFloat(new float[]{this.rigthOffsetX, 0.0f});
                        animator3.addUpdateListener(new AcceptDeclineView$$ExternalSyntheticLambda1(this));
                        animator3.start();
                        this.rightAnimator = animator3;
                        if (this.listener != null && ((!this.startDrag && Math.abs(dy) < this.touchSlop && !this.screenWasWakeup) || (-this.rigthOffsetX) > this.maxOffset * 0.8f)) {
                            this.listener.onAccept();
                        }
                    }
                }
                getParent().requestDisallowInterceptTouchEvent(false);
                this.captured = false;
                this.startDrag = false;
                setPressed(false);
                break;
            case 2:
                if (this.captured) {
                    float dx = event.getX() - this.startX;
                    if (!this.startDrag && Math.abs(dx) > this.touchSlop) {
                        if (!this.retryMod) {
                            this.startX = event.getX();
                            dx = 0.0f;
                            this.startDrag = true;
                            setPressed(false);
                            getParent().requestDisallowInterceptTouchEvent(true);
                        } else {
                            setPressed(false);
                            this.captured = false;
                        }
                    }
                    if (this.startDrag) {
                        if (this.leftDrag) {
                            this.leftOffsetX = dx;
                            if (dx < 0.0f) {
                                this.leftOffsetX = 0.0f;
                            } else {
                                float f = this.maxOffset;
                                if (dx > f) {
                                    this.leftOffsetX = f;
                                    dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, 0.0f, 0.0f, 0));
                                }
                            }
                        } else {
                            this.rigthOffsetX = dx;
                            if (dx > 0.0f) {
                                this.rigthOffsetX = 0.0f;
                            } else {
                                float f2 = this.maxOffset;
                                if (dx < (-f2)) {
                                    this.rigthOffsetX = -f2;
                                    dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, 0.0f, 0.0f, 0));
                                }
                            }
                        }
                    }
                    return true;
                }
                break;
        }
        return false;
    }

    /* renamed from: lambda$onTouchEvent$0$org-telegram-ui-Components-voip-AcceptDeclineView  reason: not valid java name */
    public /* synthetic */ void m4544x4fc0e726(ValueAnimator valueAnimator) {
        this.leftOffsetX = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        this.leftAnimator = null;
    }

    /* renamed from: lambda$onTouchEvent$1$org-telegram-ui-Components-voip-AcceptDeclineView  reason: not valid java name */
    public /* synthetic */ void m4545x934CLASSNAMEe7(ValueAnimator valueAnimator) {
        this.rigthOffsetX = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        this.rightAnimator = null;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float startX2;
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
        float f = 1.0f;
        if (this.screenWasWakeup && !this.retryMod) {
            float f2 = this.arrowProgress + 0.010666667f;
            this.arrowProgress = f2;
            if (f2 > 1.0f) {
                this.arrowProgress = 0.0f;
            }
            int cY = (int) (((float) AndroidUtilities.dp(40.0f)) + (((float) this.buttonWidth) / 2.0f));
            float startX3 = (float) (AndroidUtilities.dp(46.0f) + this.buttonWidth + AndroidUtilities.dp(8.0f));
            float endX = (((float) getMeasuredWidth()) / 2.0f) - ((float) AndroidUtilities.dp(8.0f));
            float lineLength = (float) AndroidUtilities.dp(10.0f);
            float stepProgress = (1.0f - 0.6f) / 3.0f;
            int i = 0;
            while (i < 3) {
                int x = (int) (((((endX - startX3) - lineLength) / 3.0f) * ((float) i)) + startX3);
                float alpha = 0.5f;
                float startAlphaFrom = ((float) i) * stepProgress;
                float f3 = this.arrowProgress;
                if (f3 <= startAlphaFrom || f3 >= startAlphaFrom + 0.6f) {
                    startX2 = startX3;
                } else {
                    float p = (f3 - startAlphaFrom) / 0.6f;
                    startX2 = startX3;
                    if (((double) p) > 0.5d) {
                        p = f - p;
                    }
                    alpha = p + 0.5f;
                }
                canvas.save();
                canvas2.clipRect(this.leftOffsetX + ((float) AndroidUtilities.dp(46.0f)) + ((float) (this.buttonWidth / 2)), 0.0f, (float) getMeasuredHeight(), (float) (getMeasuredWidth() >> 1));
                this.arrowDrawable.setAlpha((int) (255.0f * alpha));
                Drawable drawable = this.arrowDrawable;
                drawable.setBounds(x, cY - (drawable.getIntrinsicHeight() / 2), this.arrowDrawable.getIntrinsicWidth() + x, (this.arrowDrawable.getIntrinsicHeight() / 2) + cY);
                this.arrowDrawable.draw(canvas2);
                canvas.restore();
                int x2 = (int) (((float) getMeasuredWidth()) - (startX2 + ((((endX - startX2) - lineLength) / 3.0f) * ((float) i))));
                canvas.save();
                canvas2.clipRect((float) (getMeasuredWidth() >> 1), 0.0f, ((this.rigthOffsetX + ((float) getMeasuredWidth())) - ((float) AndroidUtilities.dp(46.0f))) - ((float) (this.buttonWidth / 2)), (float) getMeasuredHeight());
                canvas2.rotate(180.0f, ((float) x2) - (((float) this.arrowDrawable.getIntrinsicWidth()) / 2.0f), (float) cY);
                Drawable drawable2 = this.arrowDrawable;
                drawable2.setBounds(x2 - drawable2.getIntrinsicWidth(), cY - (this.arrowDrawable.getIntrinsicHeight() / 2), x2, (this.arrowDrawable.getIntrinsicHeight() / 2) + cY);
                this.arrowDrawable.draw(canvas2);
                canvas.restore();
                i++;
                startX3 = startX2;
                f = 1.0f;
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
            int i2 = this.buttonWidth;
            canvas2.drawCircle(((float) i2) / 2.0f, ((float) i2) / 2.0f, ((((float) i2) / 2.0f) - ((float) AndroidUtilities.dp(4.0f))) + this.bigRadius, this.acceptCirclePaint);
            int i3 = this.buttonWidth;
            canvas2.drawCircle(((float) i3) / 2.0f, ((float) i3) / 2.0f, ((((float) i3) / 2.0f) - ((float) AndroidUtilities.dp(4.0f))) + this.smallRadius, this.acceptCirclePaint);
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

    public void setRetryMod(boolean retryMod2) {
        this.retryMod = retryMod2;
        if (retryMod2) {
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

    public boolean onHoverEvent(MotionEvent event) {
        AcceptDeclineAccessibilityNodeProvider acceptDeclineAccessibilityNodeProvider = this.accessibilityNodeProvider;
        if (acceptDeclineAccessibilityNodeProvider == null || !acceptDeclineAccessibilityNodeProvider.onHoverEvent(event)) {
            return super.onHoverEvent(event);
        }
        return true;
    }

    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        if (this.accessibilityNodeProvider == null) {
            this.accessibilityNodeProvider = new AcceptDeclineAccessibilityNodeProvider(this, 2) {
                private static final int ACCEPT_VIEW_ID = 0;
                private static final int DECLINE_VIEW_ID = 1;
                private final int[] coords = {0, 0};

                /* access modifiers changed from: protected */
                public CharSequence getVirtualViewText(int virtualViewId) {
                    if (virtualViewId == 0) {
                        if (AcceptDeclineView.this.retryMod) {
                            if (AcceptDeclineView.this.retryLayout != null) {
                                return AcceptDeclineView.this.retryLayout.getText();
                            }
                            return null;
                        } else if (AcceptDeclineView.this.acceptLayout != null) {
                            return AcceptDeclineView.this.acceptLayout.getText();
                        } else {
                            return null;
                        }
                    } else if (virtualViewId != 1 || AcceptDeclineView.this.declineLayout == null) {
                        return null;
                    } else {
                        return AcceptDeclineView.this.declineLayout.getText();
                    }
                }

                /* access modifiers changed from: protected */
                public void getVirtualViewBoundsInScreen(int virtualViewId, Rect outRect) {
                    getVirtualViewBoundsInParent(virtualViewId, outRect);
                    AcceptDeclineView.this.getLocationOnScreen(this.coords);
                    int[] iArr = this.coords;
                    outRect.offset(iArr[0], iArr[1]);
                }

                /* access modifiers changed from: protected */
                public void getVirtualViewBoundsInParent(int virtualViewId, Rect outRect) {
                    if (virtualViewId == 0) {
                        outRect.set(AcceptDeclineView.this.acceptRect);
                    } else if (virtualViewId == 1) {
                        outRect.set(AcceptDeclineView.this.declineRect);
                    } else {
                        outRect.setEmpty();
                    }
                }

                /* access modifiers changed from: protected */
                public void onVirtualViewClick(int virtualViewId) {
                    if (AcceptDeclineView.this.listener == null) {
                        return;
                    }
                    if (virtualViewId == 0) {
                        AcceptDeclineView.this.listener.onAccept();
                    } else if (virtualViewId == 1) {
                        AcceptDeclineView.this.listener.onDecline();
                    }
                }
            };
        }
        return this.accessibilityNodeProvider;
    }

    public void setScreenWasWakeup(boolean screenWasWakeup2) {
        this.screenWasWakeup = screenWasWakeup2;
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

        /* access modifiers changed from: protected */
        public abstract void onVirtualViewClick(int i);

        private AcceptDeclineAccessibilityNodeProvider(View hostView2, int virtualViewsCount2) {
            this.rect = new Rect();
            this.currentFocusedVirtualViewId = -1;
            this.hostView = hostView2;
            this.virtualViewsCount = virtualViewsCount2;
            this.accessibilityManager = (AccessibilityManager) ContextCompat.getSystemService(hostView2.getContext(), AccessibilityManager.class);
        }

        public AccessibilityNodeInfo createAccessibilityNodeInfo(int virtualViewId) {
            AccessibilityNodeInfo nodeInfo;
            if (virtualViewId == -1) {
                nodeInfo = AccessibilityNodeInfo.obtain(this.hostView);
                nodeInfo.setPackageName(this.hostView.getContext().getPackageName());
                for (int i = 0; i < this.virtualViewsCount; i++) {
                    nodeInfo.addChild(this.hostView, i);
                }
            } else {
                nodeInfo = AccessibilityNodeInfo.obtain(this.hostView, virtualViewId);
                nodeInfo.setPackageName(this.hostView.getContext().getPackageName());
                if (Build.VERSION.SDK_INT >= 21) {
                    nodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
                }
                nodeInfo.setText(getVirtualViewText(virtualViewId));
                nodeInfo.setClassName(Button.class.getName());
                if (Build.VERSION.SDK_INT >= 24) {
                    nodeInfo.setImportantForAccessibility(true);
                }
                nodeInfo.setVisibleToUser(true);
                nodeInfo.setClickable(true);
                nodeInfo.setEnabled(true);
                nodeInfo.setParent(this.hostView);
                getVirtualViewBoundsInScreen(virtualViewId, this.rect);
                nodeInfo.setBoundsInScreen(this.rect);
            }
            return nodeInfo;
        }

        public boolean performAction(int virtualViewId, int action, Bundle arguments) {
            if (virtualViewId == -1) {
                return this.hostView.performAccessibilityAction(action, arguments);
            }
            if (action == 64) {
                sendAccessibilityEventForVirtualView(virtualViewId, 32768);
                return false;
            } else if (action != 16) {
                return false;
            } else {
                onVirtualViewClick(virtualViewId);
                return true;
            }
        }

        public boolean onHoverEvent(MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (event.getAction() == 9 || event.getAction() == 7) {
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
                return false;
            } else if (event.getAction() != 10 || this.currentFocusedVirtualViewId == -1) {
                return false;
            } else {
                this.currentFocusedVirtualViewId = -1;
                return true;
            }
        }

        private void sendAccessibilityEventForVirtualView(int virtualViewId, int eventType) {
            ViewParent parent;
            if (this.accessibilityManager.isTouchExplorationEnabled() && (parent = this.hostView.getParent()) != null) {
                AccessibilityEvent event = AccessibilityEvent.obtain(eventType);
                event.setPackageName(this.hostView.getContext().getPackageName());
                event.setSource(this.hostView, virtualViewId);
                parent.requestSendAccessibilityEvent(this.hostView, event);
            }
        }
    }
}

package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class BotCommandsMenuContainer extends FrameLayout implements NestedScrollingParent {
    Paint backgroundPaint = new Paint();
    /* access modifiers changed from: private */
    public ObjectAnimator currentAnimation = null;
    boolean dismissed = true;
    private boolean entering;
    public RecyclerListView listView;
    private NestedScrollingParentHelper nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
    float scrollYOffset;
    Drawable shadowDrawable;
    Paint topBackground = new Paint(1);

    public BotCommandsMenuContainer(Context context) {
        super(context);
        this.shadowDrawable = context.getResources().getDrawable(NUM).mutate();
        AnonymousClass1 r0 = new RecyclerListView(context) {
            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                if (BotCommandsMenuContainer.this.listView.getLayoutManager() == null || BotCommandsMenuContainer.this.listView.getAdapter() == null || BotCommandsMenuContainer.this.listView.getAdapter().getItemCount() == 0) {
                    super.dispatchDraw(canvas);
                    return;
                }
                View firstView = BotCommandsMenuContainer.this.listView.getLayoutManager().findViewByPosition(0);
                float y = 0.0f;
                if (firstView != null) {
                    y = firstView.getY();
                }
                if (y < 0.0f) {
                    y = 0.0f;
                }
                BotCommandsMenuContainer.this.scrollYOffset = y;
                float y2 = y - ((float) AndroidUtilities.dp(8.0f));
                if (y2 > 0.0f) {
                    BotCommandsMenuContainer.this.shadowDrawable.setBounds(-AndroidUtilities.dp(8.0f), ((int) y2) - AndroidUtilities.dp(24.0f), getMeasuredWidth() + AndroidUtilities.dp(8.0f), (int) y2);
                    BotCommandsMenuContainer.this.shadowDrawable.draw(canvas);
                }
                canvas.drawRect(0.0f, y2, (float) getMeasuredWidth(), (float) (getMeasuredHeight() + AndroidUtilities.dp(16.0f)), BotCommandsMenuContainer.this.backgroundPaint);
                AndroidUtilities.rectTmp.set((((float) getMeasuredWidth()) / 2.0f) - ((float) AndroidUtilities.dp(12.0f)), y2 - ((float) AndroidUtilities.dp(4.0f)), (((float) getMeasuredWidth()) / 2.0f) + ((float) AndroidUtilities.dp(12.0f)), y2);
                canvas.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), BotCommandsMenuContainer.this.topBackground);
                super.dispatchDraw(canvas);
            }
        };
        this.listView = r0;
        r0.setClipToPadding(false);
        addView(this.listView);
        updateColors();
        setClipChildren(false);
    }

    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return !this.dismissed && nestedScrollAxes == 2;
    }

    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        this.nestedScrollingParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
        if (!this.dismissed) {
            cancelCurrentAnimation();
        }
    }

    public void onStopNestedScroll(View target) {
        this.nestedScrollingParentHelper.onStopNestedScroll(target);
        if (!this.dismissed) {
            checkDismiss();
        }
    }

    private void checkDismiss() {
        if (!this.dismissed) {
            if (this.listView.getTranslationY() > ((float) AndroidUtilities.dp(16.0f))) {
                dismiss();
            } else {
                playEnterAnim(false);
            }
        }
    }

    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (!this.dismissed) {
            cancelCurrentAnimation();
            if (dyUnconsumed != 0) {
                float currentTranslation = this.listView.getTranslationY() - ((float) dyUnconsumed);
                if (currentTranslation < 0.0f) {
                    currentTranslation = 0.0f;
                }
                this.listView.setTranslationY(currentTranslation);
                invalidate();
            }
        }
    }

    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (!this.dismissed) {
            cancelCurrentAnimation();
            float currentTranslation = this.listView.getTranslationY();
            if (currentTranslation > 0.0f && dy > 0) {
                float currentTranslation2 = currentTranslation - ((float) dy);
                consumed[1] = dy;
                if (currentTranslation2 < 0.0f) {
                    currentTranslation2 = 0.0f;
                }
                this.listView.setTranslationY(currentTranslation2);
                invalidate();
            }
        }
    }

    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    public int getNestedScrollAxes() {
        return this.nestedScrollingParentHelper.getNestedScrollAxes();
    }

    private void cancelCurrentAnimation() {
        ObjectAnimator objectAnimator = this.currentAnimation;
        if (objectAnimator != null) {
            objectAnimator.removeAllListeners();
            this.currentAnimation.cancel();
            this.currentAnimation = null;
        }
    }

    public void show() {
        if (getVisibility() != 0) {
            setVisibility(0);
            this.listView.scrollToPosition(0);
            this.entering = true;
            this.dismissed = false;
        } else if (this.dismissed) {
            this.dismissed = false;
            cancelCurrentAnimation();
            playEnterAnim(false);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.entering && !this.dismissed) {
            RecyclerListView recyclerListView = this.listView;
            recyclerListView.setTranslationY((float) ((recyclerListView.getMeasuredHeight() - this.listView.getPaddingTop()) + AndroidUtilities.dp(16.0f)));
            playEnterAnim(true);
            this.entering = false;
        }
    }

    private void playEnterAnim(boolean firstTime) {
        if (!this.dismissed) {
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.listView, TRANSLATION_Y, new float[]{this.listView.getTranslationY(), 0.0f});
            this.currentAnimation = ofFloat;
            if (firstTime) {
                ofFloat.setDuration(320);
                this.currentAnimation.setInterpolator(new OvershootInterpolator(0.8f));
            } else {
                ofFloat.setDuration(150);
                this.currentAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
            }
            this.currentAnimation.start();
        }
    }

    public void dismiss() {
        if (!this.dismissed) {
            this.dismissed = true;
            cancelCurrentAnimation();
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.listView, TRANSLATION_Y, new float[]{this.listView.getTranslationY(), (((float) getMeasuredHeight()) - this.scrollYOffset) + ((float) AndroidUtilities.dp(40.0f))});
            this.currentAnimation = ofFloat;
            ofFloat.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    BotCommandsMenuContainer.this.setVisibility(8);
                    ObjectAnimator unused = BotCommandsMenuContainer.this.currentAnimation = null;
                }
            });
            this.currentAnimation.setDuration(150);
            this.currentAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.currentAnimation.start();
            onDismiss();
        }
    }

    /* access modifiers changed from: protected */
    public void onDismiss() {
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() != 0 || ev.getY() >= this.scrollYOffset - ((float) AndroidUtilities.dp(24.0f))) {
            return super.dispatchTouchEvent(ev);
        }
        return false;
    }

    public void updateColors() {
        this.topBackground.setColor(Theme.getColor("dialogGrayLine"));
        this.backgroundPaint.setColor(Theme.getColor("windowBackgroundWhite"));
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhite"), PorterDuff.Mode.MULTIPLY));
        invalidate();
    }
}

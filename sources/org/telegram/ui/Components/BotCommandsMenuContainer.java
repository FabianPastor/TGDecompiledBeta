package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
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

    /* access modifiers changed from: protected */
    public void onDismiss() {
    }

    public boolean onNestedFling(View view, float f, float f2, boolean z) {
        return false;
    }

    public boolean onNestedPreFling(View view, float f, float f2) {
        return false;
    }

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
                View findViewByPosition = BotCommandsMenuContainer.this.listView.getLayoutManager().findViewByPosition(0);
                float y = findViewByPosition != null ? findViewByPosition.getY() : 0.0f;
                if (y < 0.0f) {
                    y = 0.0f;
                }
                BotCommandsMenuContainer.this.scrollYOffset = y;
                float dp = y - ((float) AndroidUtilities.dp(8.0f));
                if (dp > 0.0f) {
                    int i = (int) dp;
                    BotCommandsMenuContainer.this.shadowDrawable.setBounds(-AndroidUtilities.dp(8.0f), i - AndroidUtilities.dp(24.0f), getMeasuredWidth() + AndroidUtilities.dp(8.0f), i);
                    BotCommandsMenuContainer.this.shadowDrawable.draw(canvas);
                }
                canvas.drawRect(0.0f, dp, (float) getMeasuredWidth(), (float) (getMeasuredHeight() + AndroidUtilities.dp(16.0f)), BotCommandsMenuContainer.this.backgroundPaint);
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set((((float) getMeasuredWidth()) / 2.0f) - ((float) AndroidUtilities.dp(12.0f)), dp - ((float) AndroidUtilities.dp(4.0f)), (((float) getMeasuredWidth()) / 2.0f) + ((float) AndroidUtilities.dp(12.0f)), dp);
                canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), BotCommandsMenuContainer.this.topBackground);
                super.dispatchDraw(canvas);
            }
        };
        this.listView = r0;
        r0.setOverScrollMode(2);
        this.listView.setClipToPadding(false);
        addView(this.listView);
        updateColors();
        setClipChildren(false);
    }

    public boolean onStartNestedScroll(View view, View view2, int i) {
        return !this.dismissed && i == 2;
    }

    public void onNestedScrollAccepted(View view, View view2, int i) {
        this.nestedScrollingParentHelper.onNestedScrollAccepted(view, view2, i);
        if (!this.dismissed) {
            cancelCurrentAnimation();
        }
    }

    public void onStopNestedScroll(View view) {
        this.nestedScrollingParentHelper.onStopNestedScroll(view);
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

    public void onNestedScroll(View view, int i, int i2, int i3, int i4) {
        if (!this.dismissed) {
            cancelCurrentAnimation();
            if (i4 != 0) {
                float translationY = this.listView.getTranslationY() - ((float) i4);
                if (translationY < 0.0f) {
                    translationY = 0.0f;
                }
                this.listView.setTranslationY(translationY);
                invalidate();
            }
        }
    }

    public void onNestedPreScroll(View view, int i, int i2, int[] iArr) {
        if (!this.dismissed) {
            cancelCurrentAnimation();
            float translationY = this.listView.getTranslationY();
            float f = 0.0f;
            if (translationY > 0.0f && i2 > 0) {
                float f2 = translationY - ((float) i2);
                iArr[1] = i2;
                if (f2 >= 0.0f) {
                    f = f2;
                }
                this.listView.setTranslationY(f);
                invalidate();
            }
        }
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
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.entering && !this.dismissed) {
            RecyclerListView recyclerListView = this.listView;
            recyclerListView.setTranslationY((float) ((recyclerListView.getMeasuredHeight() - this.listView.getPaddingTop()) + AndroidUtilities.dp(16.0f)));
            playEnterAnim(true);
            this.entering = false;
        }
    }

    private void playEnterAnim(boolean z) {
        if (!this.dismissed) {
            RecyclerListView recyclerListView = this.listView;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(recyclerListView, FrameLayout.TRANSLATION_Y, new float[]{recyclerListView.getTranslationY(), 0.0f});
            this.currentAnimation = ofFloat;
            if (z) {
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
            RecyclerListView recyclerListView = this.listView;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(recyclerListView, FrameLayout.TRANSLATION_Y, new float[]{recyclerListView.getTranslationY(), (((float) getMeasuredHeight()) - this.scrollYOffset) + ((float) AndroidUtilities.dp(40.0f))});
            this.currentAnimation = ofFloat;
            ofFloat.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
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

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() != 0 || motionEvent.getY() >= this.scrollYOffset - ((float) AndroidUtilities.dp(24.0f))) {
            return super.dispatchTouchEvent(motionEvent);
        }
        return false;
    }

    public void updateColors() {
        this.topBackground.setColor(Theme.getColor("key_sheet_scrollUp"));
        this.backgroundPaint.setColor(Theme.getColor("windowBackgroundWhite"));
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhite"), PorterDuff.Mode.MULTIPLY));
        invalidate();
    }
}

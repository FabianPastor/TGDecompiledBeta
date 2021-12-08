package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.SizeNotifierFrameLayout;

public class TrendingStickersAlert extends BottomSheet {
    private final AlertContainerView alertContainerView;
    /* access modifiers changed from: private */
    public final TrendingStickersLayout layout;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    /* access modifiers changed from: private */
    public final GradientDrawable shapeDrawable = new GradientDrawable();
    /* access modifiers changed from: private */
    public final int topOffset = AndroidUtilities.dp(12.0f);

    public TrendingStickersAlert(Context context, BaseFragment parentFragment, TrendingStickersLayout trendingStickersLayout, Theme.ResourcesProvider resourcesProvider) {
        super(context, true, resourcesProvider);
        AlertContainerView alertContainerView2 = new AlertContainerView(context);
        this.alertContainerView = alertContainerView2;
        alertContainerView2.addView(trendingStickersLayout, LayoutHelper.createFrame(-1, -1.0f));
        this.containerView = alertContainerView2;
        this.layout = trendingStickersLayout;
        trendingStickersLayout.setParentFragment(parentFragment);
        trendingStickersLayout.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private int scrolledY;

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 0) {
                    this.scrolledY = 0;
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                this.scrolledY += dy;
                if (recyclerView.getScrollState() == 1 && Math.abs(this.scrolledY) > AndroidUtilities.dp(96.0f)) {
                    View view = TrendingStickersAlert.this.layout.findFocus();
                    if (view == null) {
                        view = TrendingStickersAlert.this.layout;
                    }
                    AndroidUtilities.hideKeyboard(view);
                }
                if (dy != 0) {
                    TrendingStickersAlert.this.updateLayout();
                }
            }
        });
    }

    public void show() {
        super.show();
        setHeavyOperationsEnabled(false);
    }

    public void dismiss() {
        super.dismiss();
        this.layout.recycle();
        setHeavyOperationsEnabled(true);
    }

    public void setHeavyOperationsEnabled(boolean enabled) {
        NotificationCenter.getGlobalInstance().postNotificationName(enabled ? NotificationCenter.startAllHeavyOperations : NotificationCenter.stopAllHeavyOperations, 2);
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public TrendingStickersLayout getLayout() {
        return this.layout;
    }

    /* access modifiers changed from: private */
    public void updateLayout() {
        if (this.layout.update()) {
            this.scrollOffsetY = this.layout.getContentTopOffset();
            this.containerView.invalidate();
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> descriptions = new ArrayList<>();
        TrendingStickersLayout trendingStickersLayout = this.layout;
        trendingStickersLayout.getClass();
        trendingStickersLayout.getThemeDescriptions(descriptions, new TrendingStickersAlert$$ExternalSyntheticLambda0(trendingStickersLayout));
        descriptions.add(new ThemeDescription(this.alertContainerView, 0, (Class[]) null, (Paint) null, new Drawable[]{this.shadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        descriptions.add(new ThemeDescription(this.alertContainerView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_sheet_scrollUp"));
        return descriptions;
    }

    public void setAllowNestedScroll(boolean allowNestedScroll) {
        this.allowNestedScroll = allowNestedScroll;
    }

    private class AlertContainerView extends SizeNotifierFrameLayout {
        /* access modifiers changed from: private */
        public boolean gluedToTop = false;
        private boolean ignoreLayout = false;
        private final Paint paint = new Paint(1);
        private float[] radii = new float[8];
        private float statusBarAlpha = 0.0f;
        private ValueAnimator statusBarAnimator;
        private boolean statusBarVisible = false;

        public AlertContainerView(Context context) {
            super(context);
            setWillNotDraw(false);
            setPadding(TrendingStickersAlert.this.backgroundPaddingLeft, 0, TrendingStickersAlert.this.backgroundPaddingLeft, 0);
            setDelegate(new SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate(TrendingStickersAlert.this) {
                private boolean lastIsWidthGreater;
                private int lastKeyboardHeight;

                public void onSizeChanged(int keyboardHeight, boolean isWidthGreater) {
                    if (this.lastKeyboardHeight != keyboardHeight || this.lastIsWidthGreater != isWidthGreater) {
                        this.lastKeyboardHeight = keyboardHeight;
                        this.lastIsWidthGreater = isWidthGreater;
                        if (keyboardHeight > AndroidUtilities.dp(20.0f) && !AlertContainerView.this.gluedToTop) {
                            TrendingStickersAlert.this.setAllowNestedScroll(false);
                            boolean unused = AlertContainerView.this.gluedToTop = true;
                        }
                    }
                }
            });
        }

        /* access modifiers changed from: protected */
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            AndroidUtilities.runOnUIThread(new TrendingStickersAlert$AlertContainerView$$ExternalSyntheticLambda1(this), 200);
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (ev.getAction() != 0 || TrendingStickersAlert.this.scrollOffsetY == 0 || ev.getY() >= ((float) TrendingStickersAlert.this.scrollOffsetY)) {
                return super.onInterceptTouchEvent(ev);
            }
            TrendingStickersAlert.this.dismiss();
            return true;
        }

        public boolean onTouchEvent(MotionEvent e) {
            return !TrendingStickersAlert.this.isDismissed() && super.onTouchEvent(e);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), NUM));
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            int statusBarHeight = Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
            int keyboardHeight = measureKeyboardHeight();
            int padding = (int) (((float) ((View.MeasureSpec.getSize(getMeasuredHeight()) - statusBarHeight) + keyboardHeight)) * 0.2f);
            this.ignoreLayout = true;
            if (keyboardHeight > AndroidUtilities.dp(20.0f)) {
                TrendingStickersAlert.this.layout.glueToTop(true);
                TrendingStickersAlert.this.setAllowNestedScroll(false);
                this.gluedToTop = true;
            } else {
                TrendingStickersAlert.this.layout.glueToTop(false);
                TrendingStickersAlert.this.setAllowNestedScroll(true);
                this.gluedToTop = false;
            }
            TrendingStickersAlert.this.layout.setContentViewPaddingTop(padding);
            if (getPaddingTop() != statusBarHeight) {
                setPadding(TrendingStickersAlert.this.backgroundPaddingLeft, statusBarHeight, TrendingStickersAlert.this.backgroundPaddingLeft, 0);
            }
            this.ignoreLayout = false;
            super.onLayout(changed, l, t, r, b);
        }

        public void requestLayout() {
            if (!this.ignoreLayout) {
                super.requestLayout();
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            TrendingStickersAlert.this.updateLayout();
            super.onDraw(canvas);
            float fraction = getFraction();
            int offset = (int) (((float) TrendingStickersAlert.this.topOffset) * (1.0f - fraction));
            int translationY = (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) - TrendingStickersAlert.this.topOffset;
            canvas.save();
            canvas.translate(0.0f, TrendingStickersAlert.this.layout.getTranslationY() + ((float) translationY));
            TrendingStickersAlert.this.shadowDrawable.setBounds(0, (TrendingStickersAlert.this.scrollOffsetY - TrendingStickersAlert.this.backgroundPaddingTop) + offset, getMeasuredWidth(), getMeasuredHeight() + (translationY < 0 ? -translationY : 0));
            TrendingStickersAlert.this.shadowDrawable.draw(canvas);
            if (fraction > 0.0f && fraction < 1.0f) {
                float radius = ((float) AndroidUtilities.dp(12.0f)) * fraction;
                TrendingStickersAlert.this.shapeDrawable.setColor(TrendingStickersAlert.this.getThemedColor("dialogBackground"));
                float[] fArr = this.radii;
                fArr[3] = radius;
                fArr[2] = radius;
                fArr[1] = radius;
                fArr[0] = radius;
                TrendingStickersAlert.this.shapeDrawable.setCornerRadii(this.radii);
                TrendingStickersAlert.this.shapeDrawable.setBounds(TrendingStickersAlert.this.backgroundPaddingLeft, TrendingStickersAlert.this.scrollOffsetY + offset, getWidth() - TrendingStickersAlert.this.backgroundPaddingLeft, TrendingStickersAlert.this.scrollOffsetY + offset + AndroidUtilities.dp(24.0f));
                TrendingStickersAlert.this.shapeDrawable.draw(canvas);
            }
            canvas.restore();
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            float fraction = getFraction();
            canvas.save();
            boolean z = false;
            canvas.translate(0.0f, (TrendingStickersAlert.this.layout.getTranslationY() + ((float) (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0))) - ((float) TrendingStickersAlert.this.topOffset));
            int w = AndroidUtilities.dp(36.0f);
            int h = AndroidUtilities.dp(4.0f);
            int offset = (int) (((float) h) * 2.0f * (1.0f - fraction));
            TrendingStickersAlert.this.shapeDrawable.setCornerRadius((float) AndroidUtilities.dp(2.0f));
            int sheetScrollUpColor = TrendingStickersAlert.this.getThemedColor("key_sheet_scrollUp");
            TrendingStickersAlert.this.shapeDrawable.setColor(ColorUtils.setAlphaComponent(sheetScrollUpColor, (int) (((float) Color.alpha(sheetScrollUpColor)) * fraction)));
            TrendingStickersAlert.this.shapeDrawable.setBounds((getWidth() - w) / 2, TrendingStickersAlert.this.scrollOffsetY + AndroidUtilities.dp(10.0f) + offset, (getWidth() + w) / 2, TrendingStickersAlert.this.scrollOffsetY + AndroidUtilities.dp(10.0f) + offset + h);
            TrendingStickersAlert.this.shapeDrawable.draw(canvas);
            canvas.restore();
            if (fraction == 0.0f && Build.VERSION.SDK_INT >= 21 && !TrendingStickersAlert.this.isDismissed()) {
                z = true;
            }
            setStatusBarVisible(z, true);
            if (this.statusBarAlpha > 0.0f) {
                int color = TrendingStickersAlert.this.getThemedColor("dialogBackground");
                this.paint.setColor(Color.argb((int) (this.statusBarAlpha * 255.0f), (int) (((float) Color.red(color)) * 0.8f), (int) (((float) Color.green(color)) * 0.8f), (int) (((float) Color.blue(color)) * 0.8f)));
                canvas.drawRect((float) TrendingStickersAlert.this.backgroundPaddingLeft, 0.0f, (float) (getMeasuredWidth() - TrendingStickersAlert.this.backgroundPaddingLeft), (float) AndroidUtilities.statusBarHeight, this.paint);
            }
        }

        public void setTranslationY(float translationY) {
            TrendingStickersAlert.this.layout.setTranslationY(translationY);
            invalidate();
        }

        public float getTranslationY() {
            return TrendingStickersAlert.this.layout.getTranslationY();
        }

        private float getFraction() {
            return Math.min(1.0f, Math.max(0.0f, ((float) TrendingStickersAlert.this.scrollOffsetY) / (((float) TrendingStickersAlert.this.topOffset) * 2.0f)));
        }

        private void setStatusBarVisible(boolean visible, boolean animated) {
            if (this.statusBarVisible != visible) {
                ValueAnimator valueAnimator = this.statusBarAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                this.statusBarVisible = visible;
                float f = 1.0f;
                if (animated) {
                    ValueAnimator valueAnimator2 = this.statusBarAnimator;
                    if (valueAnimator2 == null) {
                        float[] fArr = new float[2];
                        fArr[0] = this.statusBarAlpha;
                        if (!visible) {
                            f = 0.0f;
                        }
                        fArr[1] = f;
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                        this.statusBarAnimator = ofFloat;
                        ofFloat.addUpdateListener(new TrendingStickersAlert$AlertContainerView$$ExternalSyntheticLambda0(this));
                        this.statusBarAnimator.setDuration(200);
                    } else {
                        float[] fArr2 = new float[2];
                        fArr2[0] = this.statusBarAlpha;
                        if (!visible) {
                            f = 0.0f;
                        }
                        fArr2[1] = f;
                        valueAnimator2.setFloatValues(fArr2);
                    }
                    this.statusBarAnimator.start();
                    return;
                }
                if (!visible) {
                    f = 0.0f;
                }
                this.statusBarAlpha = f;
                invalidate();
            }
        }

        /* renamed from: lambda$setStatusBarVisible$0$org-telegram-ui-Components-TrendingStickersAlert$AlertContainerView  reason: not valid java name */
        public /* synthetic */ void m2696x25fea2ec(ValueAnimator a) {
            this.statusBarAlpha = ((Float) a.getAnimatedValue()).floatValue();
            invalidate();
        }
    }
}

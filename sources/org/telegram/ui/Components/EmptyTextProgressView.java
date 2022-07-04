package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class EmptyTextProgressView extends FrameLayout {
    private boolean inLayout;
    private RLottieImageView lottieImageView;
    private View progressView;
    private final Theme.ResourcesProvider resourcesProvider;
    private int showAtPos;
    private TextView textView;
    private LinearLayout textViewLayout;

    public EmptyTextProgressView(Context context) {
        this(context, (View) null, (Theme.ResourcesProvider) null);
    }

    public EmptyTextProgressView(Context context, View progressView2) {
        this(context, progressView2, (Theme.ResourcesProvider) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public EmptyTextProgressView(Context context, View progressView2, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        Context context2 = context;
        View progressView3 = progressView2;
        this.resourcesProvider = resourcesProvider2;
        if (progressView3 == null) {
            progressView3 = new RadialProgressView(context2);
            addView(progressView3, LayoutHelper.createFrame(-2, -2.0f));
        } else {
            addView(progressView3, LayoutHelper.createFrame(-1, -1.0f));
        }
        this.progressView = progressView3;
        LinearLayout linearLayout = new LinearLayout(context2);
        this.textViewLayout = linearLayout;
        linearLayout.setPadding(AndroidUtilities.dp(20.0f), 0, AndroidUtilities.dp(20.0f), 0);
        this.textViewLayout.setGravity(1);
        this.textViewLayout.setClipChildren(false);
        this.textViewLayout.setClipToPadding(false);
        this.textViewLayout.setOrientation(1);
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.lottieImageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        this.lottieImageView.setImportantForAccessibility(2);
        this.lottieImageView.setVisibility(8);
        this.textViewLayout.addView(this.lottieImageView, LayoutHelper.createLinear(150, 150, 17, 0, 0, 0, 20));
        TextView textView2 = new TextView(context2);
        this.textView = textView2;
        textView2.setTextSize(1, 20.0f);
        this.textView.setTextColor(getThemedColor("emptyListPlaceholder"));
        this.textView.setGravity(1);
        this.textView.setText(LocaleController.getString("NoResult", NUM));
        this.textViewLayout.addView(this.textView, LayoutHelper.createLinear(-2, -2, 17));
        addView(this.textViewLayout, LayoutHelper.createFrame(-2, -2.0f));
        AndroidUtilities.updateViewVisibilityAnimated(this.textView, false, 2.0f, false);
        AndroidUtilities.updateViewVisibilityAnimated(progressView3, false, 1.0f, false);
        setOnTouchListener(EmptyTextProgressView$$ExternalSyntheticLambda0.INSTANCE);
    }

    static /* synthetic */ boolean lambda$new$0(View v, MotionEvent event) {
        return true;
    }

    public void showProgress() {
        showProgress(true);
    }

    public void showProgress(boolean animated) {
        AndroidUtilities.updateViewVisibilityAnimated(this.textView, false, 0.9f, animated);
        AndroidUtilities.updateViewVisibilityAnimated(this.progressView, true, 1.0f, animated);
    }

    public void showTextView() {
        AndroidUtilities.updateViewVisibilityAnimated(this.textView, true, 0.9f, true);
        AndroidUtilities.updateViewVisibilityAnimated(this.progressView, false, 1.0f, true);
    }

    public void setText(String text) {
        this.textView.setText(text);
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setLottie(int resource, int w, int h) {
        this.lottieImageView.setVisibility(resource != 0 ? 0 : 8);
        if (resource != 0) {
            this.lottieImageView.setAnimation(resource, w, h);
            this.lottieImageView.playAnimation();
        }
    }

    public void setProgressBarColor(int color) {
        View view = this.progressView;
        if (view instanceof RadialProgressView) {
            ((RadialProgressView) view).setProgressColor(color);
        }
    }

    public void setTopImage(int resId) {
        if (resId == 0) {
            this.textView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
            return;
        }
        Drawable drawable = getContext().getResources().getDrawable(resId).mutate();
        if (drawable != null) {
            drawable.setColorFilter(new PorterDuffColorFilter(getThemedColor("emptyListPlaceholder"), PorterDuff.Mode.MULTIPLY));
        }
        this.textView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, drawable, (Drawable) null, (Drawable) null);
        this.textView.setCompoundDrawablePadding(AndroidUtilities.dp(1.0f));
    }

    public void setTextSize(int size) {
        this.textView.setTextSize(1, (float) size);
    }

    public void setShowAtCenter(boolean value) {
        this.showAtPos = value;
    }

    public void setShowAtTop(boolean value) {
        this.showAtPos = value ? 2 : 0;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int y;
        this.inLayout = true;
        int width = r - l;
        int height = b - t;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                int x = (width - child.getMeasuredWidth()) / 2;
                View view = this.progressView;
                if (child != view || !(view instanceof FlickerLoadingView)) {
                    int y2 = this.showAtPos;
                    if (y2 == 2) {
                        y = ((AndroidUtilities.dp(100.0f) - child.getMeasuredHeight()) / 2) + getPaddingTop();
                    } else if (y2 == 1) {
                        y = (((height / 2) - child.getMeasuredHeight()) / 2) + getPaddingTop();
                    } else {
                        y = ((height - child.getMeasuredHeight()) / 2) + getPaddingTop();
                    }
                } else {
                    y = ((height - child.getMeasuredHeight()) / 2) + getPaddingTop();
                }
                child.layout(x, y, child.getMeasuredWidth() + x, child.getMeasuredHeight() + y);
            }
        }
        this.inLayout = false;
    }

    public void requestLayout() {
        if (!this.inLayout) {
            super.requestLayout();
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}

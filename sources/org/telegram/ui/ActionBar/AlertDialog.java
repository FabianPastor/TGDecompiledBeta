package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AndroidUtilities.LinkMovementMethodMy;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LineProgressView;
import org.telegram.ui.Components.RadialProgressView;

public class AlertDialog extends Dialog implements Callback {
    private Rect backgroundPaddings = new Rect();
    protected FrameLayout buttonsLayout;
    private boolean canCacnel = true;
    private AlertDialog cancelDialog;
    private ScrollView contentScrollView;
    private int currentProgress;
    private View customView;
    private int customViewOffset = 20;
    private boolean dismissDialogByButtons = true;
    private Runnable dismissRunnable = new AlertDialog$$Lambda$0(this);
    private int[] itemIcons;
    private ArrayList<AlertDialogCell> itemViews = new ArrayList();
    private CharSequence[] items;
    private int lastScreenWidth;
    private LineProgressView lineProgressView;
    private TextView lineProgressViewPercent;
    private CharSequence message;
    private TextView messageTextView;
    private OnClickListener negativeButtonListener;
    private CharSequence negativeButtonText;
    private OnClickListener neutralButtonListener;
    private CharSequence neutralButtonText;
    private OnClickListener onBackButtonListener;
    private OnCancelListener onCancelListener;
    private OnClickListener onClickListener;
    private OnDismissListener onDismissListener;
    private OnScrollChangedListener onScrollChangedListener;
    private OnClickListener positiveButtonListener;
    private CharSequence positiveButtonText;
    private FrameLayout progressViewContainer;
    private int progressViewStyle;
    private TextView progressViewTextView;
    private LinearLayout scrollContainer;
    private CharSequence secondTitle;
    private TextView secondTitleTextView;
    private BitmapDrawable[] shadow = new BitmapDrawable[2];
    private AnimatorSet[] shadowAnimation = new AnimatorSet[2];
    private Drawable shadowDrawable;
    private boolean[] shadowVisibility = new boolean[2];
    private CharSequence subtitle;
    private TextView subtitleTextView;
    private CharSequence title;
    private FrameLayout titleContainer;
    private TextView titleTextView;
    private int topBackgroundColor;
    private Drawable topDrawable;
    private int topHeight = 132;
    private ImageView topImageView;
    private int topResId;

    public static class AlertDialogCell extends FrameLayout {
        private ImageView imageView;
        private TextView textView;

        public AlertDialogCell(Context context) {
            int i = 3;
            super(context);
            setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 2));
            setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
            this.imageView = new ImageView(context);
            this.imageView.setScaleType(ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogIcon"), Mode.MULTIPLY));
            addView(this.imageView, LayoutHelper.createFrame(-2, 40, (LocaleController.isRTL ? 5 : 3) | 16));
            this.textView = new TextView(context);
            this.textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TruncateAt.END);
            this.textView.setTextColor(Theme.getColor("dialogTextBlack"));
            this.textView.setTextSize(1, 16.0f);
            View view = this.textView;
            if (LocaleController.isRTL) {
                i = 5;
            }
            addView(view, LayoutHelper.createFrame(-2, -2, i | 16));
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
        }

        public void setTextColor(int color) {
            this.textView.setTextColor(color);
        }

        public void setGravity(int gravity) {
            this.textView.setGravity(gravity);
        }

        public void setTextAndIcon(CharSequence text, int icon) {
            this.textView.setText(text);
            if (icon != 0) {
                this.imageView.setImageResource(icon);
                this.imageView.setVisibility(0);
                this.textView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(56.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(56.0f) : 0, 0);
                return;
            }
            this.imageView.setVisibility(4);
            this.textView.setPadding(0, 0, 0, 0);
        }
    }

    public static class Builder {
        private AlertDialog alertDialog;

        protected Builder(AlertDialog alert) {
            this.alertDialog = alert;
        }

        public Builder(Context context) {
            this.alertDialog = new AlertDialog(context, 0);
        }

        public Builder(Context context, int progressViewStyle) {
            this.alertDialog = new AlertDialog(context, progressViewStyle);
        }

        public Context getContext() {
            return this.alertDialog.getContext();
        }

        public Builder setItems(CharSequence[] items, OnClickListener onClickListener) {
            this.alertDialog.items = items;
            this.alertDialog.onClickListener = onClickListener;
            return this;
        }

        public Builder setItems(CharSequence[] items, int[] icons, OnClickListener onClickListener) {
            this.alertDialog.items = items;
            this.alertDialog.itemIcons = icons;
            this.alertDialog.onClickListener = onClickListener;
            return this;
        }

        public Builder setView(View view) {
            this.alertDialog.customView = view;
            return this;
        }

        public Builder setTitle(CharSequence title) {
            this.alertDialog.title = title;
            return this;
        }

        public Builder setSubtitle(CharSequence subtitle) {
            this.alertDialog.subtitle = subtitle;
            return this;
        }

        public Builder setTopImage(int resId, int backgroundColor) {
            this.alertDialog.topResId = resId;
            this.alertDialog.topBackgroundColor = backgroundColor;
            return this;
        }

        public Builder setTopImage(Drawable drawable, int backgroundColor) {
            this.alertDialog.topDrawable = drawable;
            this.alertDialog.topBackgroundColor = backgroundColor;
            return this;
        }

        public Builder setMessage(CharSequence message) {
            this.alertDialog.message = message;
            return this;
        }

        public Builder setPositiveButton(CharSequence text, OnClickListener listener) {
            this.alertDialog.positiveButtonText = text;
            this.alertDialog.positiveButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(CharSequence text, OnClickListener listener) {
            this.alertDialog.negativeButtonText = text;
            this.alertDialog.negativeButtonListener = listener;
            return this;
        }

        public Builder setNeutralButton(CharSequence text, OnClickListener listener) {
            this.alertDialog.neutralButtonText = text;
            this.alertDialog.neutralButtonListener = listener;
            return this;
        }

        public Builder setOnBackButtonListener(OnClickListener listener) {
            this.alertDialog.onBackButtonListener = listener;
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener listener) {
            this.alertDialog.setOnCancelListener(listener);
            return this;
        }

        public Builder setCustomViewOffset(int offset) {
            this.alertDialog.customViewOffset = offset;
            return this;
        }

        public AlertDialog create() {
            return this.alertDialog;
        }

        public AlertDialog show() {
            this.alertDialog.show();
            return this.alertDialog;
        }

        public Runnable getDismissRunnable() {
            return this.alertDialog.dismissRunnable;
        }

        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            this.alertDialog.setOnDismissListener(onDismissListener);
            return this;
        }
    }

    public AlertDialog(Context context, int progressStyle) {
        super(context, R.style.TransparentDialog);
        if (progressStyle != 3) {
            this.shadowDrawable = context.getResources().getDrawable(R.drawable.popup_fixed_alert).mutate();
            this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(getThemeColor("dialogBackground"), Mode.MULTIPLY));
            this.shadowDrawable.getPadding(this.backgroundPaddings);
        }
        this.progressViewStyle = progressStyle;
    }

    protected void onCreate(Bundle savedInstanceState) {
        FrameLayout frameLayout;
        View view;
        int i;
        int i2;
        View radialProgressView;
        super.onCreate(savedInstanceState);
        LinearLayout containerView = new LinearLayout(getContext()) {
            private boolean inLayout;

            public boolean onTouchEvent(MotionEvent event) {
                if (AlertDialog.this.progressViewStyle != 3) {
                    return super.onTouchEvent(event);
                }
                AlertDialog.this.showCancelAlert();
                return false;
            }

            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (AlertDialog.this.progressViewStyle != 3) {
                    return super.onInterceptTouchEvent(ev);
                }
                AlertDialog.this.showCancelAlert();
                return false;
            }

            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                if (AlertDialog.this.progressViewStyle == 3) {
                    AlertDialog.this.progressViewContainer.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(86.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(86.0f), NUM));
                    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
                    return;
                }
                LayoutParams layoutParams;
                this.inLayout = true;
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int maxContentHeight = (MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop()) - getPaddingBottom();
                int availableHeight = maxContentHeight;
                int availableWidth = (width - getPaddingLeft()) - getPaddingRight();
                int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(availableWidth - AndroidUtilities.dp(48.0f), NUM);
                int childFullWidthMeasureSpec = MeasureSpec.makeMeasureSpec(availableWidth, NUM);
                if (AlertDialog.this.buttonsLayout != null) {
                    int count = AlertDialog.this.buttonsLayout.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = AlertDialog.this.buttonsLayout.getChildAt(a);
                        if (child instanceof TextView) {
                            ((TextView) child).setMaxWidth(AndroidUtilities.dp((float) ((availableWidth - AndroidUtilities.dp(24.0f)) / 2)));
                        }
                    }
                    AlertDialog.this.buttonsLayout.measure(childFullWidthMeasureSpec, heightMeasureSpec);
                    layoutParams = (LayoutParams) AlertDialog.this.buttonsLayout.getLayoutParams();
                    availableHeight -= (AlertDialog.this.buttonsLayout.getMeasuredHeight() + layoutParams.bottomMargin) + layoutParams.topMargin;
                }
                if (AlertDialog.this.secondTitleTextView != null) {
                    AlertDialog.this.secondTitleTextView.measure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(childWidthMeasureSpec), Integer.MIN_VALUE), heightMeasureSpec);
                }
                if (AlertDialog.this.titleTextView != null) {
                    if (AlertDialog.this.secondTitleTextView != null) {
                        AlertDialog.this.titleTextView.measure(MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(childWidthMeasureSpec) - AlertDialog.this.secondTitleTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f), NUM), heightMeasureSpec);
                    } else {
                        AlertDialog.this.titleTextView.measure(childWidthMeasureSpec, heightMeasureSpec);
                    }
                }
                if (AlertDialog.this.titleContainer != null) {
                    AlertDialog.this.titleContainer.measure(childWidthMeasureSpec, heightMeasureSpec);
                    layoutParams = (LayoutParams) AlertDialog.this.titleContainer.getLayoutParams();
                    availableHeight -= (AlertDialog.this.titleContainer.getMeasuredHeight() + layoutParams.bottomMargin) + layoutParams.topMargin;
                }
                if (AlertDialog.this.subtitleTextView != null) {
                    AlertDialog.this.subtitleTextView.measure(childWidthMeasureSpec, heightMeasureSpec);
                    layoutParams = (LayoutParams) AlertDialog.this.subtitleTextView.getLayoutParams();
                    availableHeight -= (AlertDialog.this.subtitleTextView.getMeasuredHeight() + layoutParams.bottomMargin) + layoutParams.topMargin;
                }
                if (AlertDialog.this.topImageView != null) {
                    AlertDialog.this.topImageView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) AlertDialog.this.topHeight), NUM));
                    availableHeight -= AlertDialog.this.topImageView.getMeasuredHeight() - AndroidUtilities.dp(8.0f);
                }
                if (AlertDialog.this.progressViewStyle == 0) {
                    layoutParams = (LayoutParams) AlertDialog.this.contentScrollView.getLayoutParams();
                    int dp;
                    if (AlertDialog.this.customView != null) {
                        dp = (AlertDialog.this.titleTextView == null && AlertDialog.this.messageTextView.getVisibility() == 8 && AlertDialog.this.items == null) ? AndroidUtilities.dp(16.0f) : 0;
                        layoutParams.topMargin = dp;
                        layoutParams.bottomMargin = AlertDialog.this.buttonsLayout == null ? AndroidUtilities.dp(8.0f) : 0;
                    } else if (AlertDialog.this.items != null) {
                        dp = (AlertDialog.this.titleTextView == null && AlertDialog.this.messageTextView.getVisibility() == 8) ? AndroidUtilities.dp(8.0f) : 0;
                        layoutParams.topMargin = dp;
                        layoutParams.bottomMargin = AndroidUtilities.dp(8.0f);
                    } else if (AlertDialog.this.messageTextView.getVisibility() == 0) {
                        layoutParams.topMargin = AlertDialog.this.titleTextView == null ? AndroidUtilities.dp(19.0f) : 0;
                        layoutParams.bottomMargin = AndroidUtilities.dp(20.0f);
                    }
                    availableHeight -= layoutParams.bottomMargin + layoutParams.topMargin;
                    AlertDialog.this.contentScrollView.measure(childFullWidthMeasureSpec, MeasureSpec.makeMeasureSpec(availableHeight, Integer.MIN_VALUE));
                    availableHeight -= AlertDialog.this.contentScrollView.getMeasuredHeight();
                } else {
                    if (AlertDialog.this.progressViewContainer != null) {
                        AlertDialog.this.progressViewContainer.measure(childWidthMeasureSpec, MeasureSpec.makeMeasureSpec(availableHeight, Integer.MIN_VALUE));
                        layoutParams = (LayoutParams) AlertDialog.this.progressViewContainer.getLayoutParams();
                        availableHeight -= (AlertDialog.this.progressViewContainer.getMeasuredHeight() + layoutParams.bottomMargin) + layoutParams.topMargin;
                    } else if (AlertDialog.this.messageTextView != null) {
                        AlertDialog.this.messageTextView.measure(childWidthMeasureSpec, MeasureSpec.makeMeasureSpec(availableHeight, Integer.MIN_VALUE));
                        if (AlertDialog.this.messageTextView.getVisibility() != 8) {
                            layoutParams = (LayoutParams) AlertDialog.this.messageTextView.getLayoutParams();
                            availableHeight -= (AlertDialog.this.messageTextView.getMeasuredHeight() + layoutParams.bottomMargin) + layoutParams.topMargin;
                        }
                    }
                    if (AlertDialog.this.lineProgressView != null) {
                        AlertDialog.this.lineProgressView.measure(childWidthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(4.0f), NUM));
                        layoutParams = (LayoutParams) AlertDialog.this.lineProgressView.getLayoutParams();
                        availableHeight -= (AlertDialog.this.lineProgressView.getMeasuredHeight() + layoutParams.bottomMargin) + layoutParams.topMargin;
                        AlertDialog.this.lineProgressViewPercent.measure(childWidthMeasureSpec, MeasureSpec.makeMeasureSpec(availableHeight, Integer.MIN_VALUE));
                        layoutParams = (LayoutParams) AlertDialog.this.lineProgressViewPercent.getLayoutParams();
                        availableHeight -= (AlertDialog.this.lineProgressViewPercent.getMeasuredHeight() + layoutParams.bottomMargin) + layoutParams.topMargin;
                    }
                }
                setMeasuredDimension(width, ((maxContentHeight - availableHeight) + getPaddingTop()) + getPaddingBottom());
                this.inLayout = false;
                if (AlertDialog.this.lastScreenWidth != AndroidUtilities.displaySize.x) {
                    AndroidUtilities.runOnUIThread(new AlertDialog$1$$Lambda$0(this));
                }
            }

            final /* synthetic */ void lambda$onMeasure$0$AlertDialog$1() {
                int maxWidth;
                AlertDialog.this.lastScreenWidth = AndroidUtilities.displaySize.x;
                int calculatedWidth = AndroidUtilities.displaySize.x - AndroidUtilities.dp(56.0f);
                if (!AndroidUtilities.isTablet()) {
                    maxWidth = AndroidUtilities.dp(356.0f);
                } else if (AndroidUtilities.isSmallTablet()) {
                    maxWidth = AndroidUtilities.dp(446.0f);
                } else {
                    maxWidth = AndroidUtilities.dp(496.0f);
                }
                Window window = AlertDialog.this.getWindow();
                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                params.copyFrom(window.getAttributes());
                params.width = (Math.min(maxWidth, calculatedWidth) + AlertDialog.this.backgroundPaddings.left) + AlertDialog.this.backgroundPaddings.right;
                window.setAttributes(params);
            }

            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                if (AlertDialog.this.progressViewStyle == 3) {
                    int x = ((r - l) - AlertDialog.this.progressViewContainer.getMeasuredWidth()) / 2;
                    int y = ((b - t) - AlertDialog.this.progressViewContainer.getMeasuredHeight()) / 2;
                    AlertDialog.this.progressViewContainer.layout(x, y, AlertDialog.this.progressViewContainer.getMeasuredWidth() + x, AlertDialog.this.progressViewContainer.getMeasuredHeight() + y);
                } else if (AlertDialog.this.contentScrollView != null) {
                    if (AlertDialog.this.onScrollChangedListener == null) {
                        AlertDialog.this.onScrollChangedListener = new AlertDialog$1$$Lambda$1(this);
                        AlertDialog.this.contentScrollView.getViewTreeObserver().addOnScrollChangedListener(AlertDialog.this.onScrollChangedListener);
                    }
                    AlertDialog.this.onScrollChangedListener.onScrollChanged();
                }
            }

            final /* synthetic */ void lambda$onLayout$1$AlertDialog$1() {
                boolean z;
                boolean z2 = false;
                AlertDialog alertDialog = AlertDialog.this;
                if (AlertDialog.this.titleTextView == null || AlertDialog.this.contentScrollView.getScrollY() <= AlertDialog.this.scrollContainer.getTop()) {
                    z = false;
                } else {
                    z = true;
                }
                alertDialog.runShadowAnimation(0, z);
                AlertDialog alertDialog2 = AlertDialog.this;
                if (AlertDialog.this.buttonsLayout != null && AlertDialog.this.contentScrollView.getScrollY() + AlertDialog.this.contentScrollView.getHeight() < AlertDialog.this.scrollContainer.getBottom()) {
                    z2 = true;
                }
                alertDialog2.runShadowAnimation(1, z2);
                AlertDialog.this.contentScrollView.invalidate();
            }

            public void requestLayout() {
                if (!this.inLayout) {
                    super.requestLayout();
                }
            }

            public boolean hasOverlappingRendering() {
                return false;
            }
        };
        containerView.setOrientation(1);
        if (this.progressViewStyle == 3) {
            containerView.setBackgroundDrawable(null);
        } else {
            containerView.setBackgroundDrawable(this.shadowDrawable);
        }
        containerView.setFitsSystemWindows(VERSION.SDK_INT >= 21);
        setContentView(containerView);
        boolean hasButtons = (this.positiveButtonText == null && this.negativeButtonText == null && this.neutralButtonText == null) ? false : true;
        if (!(this.topResId == 0 && this.topDrawable == null)) {
            this.topImageView = new ImageView(getContext());
            if (this.topDrawable != null) {
                this.topImageView.setImageDrawable(this.topDrawable);
            } else {
                this.topImageView.setImageResource(this.topResId);
            }
            this.topImageView.setScaleType(ScaleType.CENTER);
            this.topImageView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.popup_fixed_top));
            this.topImageView.getBackground().setColorFilter(new PorterDuffColorFilter(this.topBackgroundColor, Mode.MULTIPLY));
            this.topImageView.setPadding(0, 0, 0, 0);
            containerView.addView(this.topImageView, LayoutHelper.createLinear(-1, this.topHeight, 51, -8, -8, 0, 0));
        }
        if (this.title != null) {
            this.titleContainer = new FrameLayout(getContext());
            containerView.addView(this.titleContainer, LayoutHelper.createLinear(-2, -2, 24.0f, 0.0f, 24.0f, 0.0f));
            this.titleTextView = new TextView(getContext());
            this.titleTextView.setText(this.title);
            this.titleTextView.setTextColor(getThemeColor("dialogTextBlack"));
            this.titleTextView.setTextSize(1, 20.0f);
            this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            frameLayout = this.titleContainer;
            view = this.titleTextView;
            i = (LocaleController.isRTL ? 5 : 3) | 48;
            i2 = this.subtitle != null ? 2 : this.items != null ? 14 : 10;
            frameLayout.addView(view, LayoutHelper.createFrame(-2, -2.0f, i, 0.0f, 19.0f, 0.0f, (float) i2));
        }
        if (!(this.secondTitle == null || this.title == null)) {
            this.secondTitleTextView = new TextView(getContext());
            this.secondTitleTextView.setText(this.secondTitle);
            this.secondTitleTextView.setTextColor(getThemeColor("dialogTextGray3"));
            this.secondTitleTextView.setTextSize(1, 18.0f);
            this.secondTitleTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
            this.titleContainer.addView(this.secondTitleTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 3 : 5) | 48, 0.0f, 21.0f, 0.0f, 0.0f));
        }
        if (this.subtitle != null) {
            this.subtitleTextView = new TextView(getContext());
            this.subtitleTextView.setText(this.subtitle);
            this.subtitleTextView.setTextColor(getThemeColor("dialogIcon"));
            this.subtitleTextView.setTextSize(1, 14.0f);
            this.subtitleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            View view2 = this.subtitleTextView;
            i = (LocaleController.isRTL ? 5 : 3) | 48;
            if (this.items != null) {
                i2 = 14;
            } else {
                i2 = 10;
            }
            containerView.addView(view2, LayoutHelper.createLinear(-2, -2, i, 24, 0, 24, i2));
        }
        if (this.progressViewStyle == 0) {
            this.shadow[0] = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.header_shadow).mutate();
            this.shadow[1] = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.header_shadow_reverse).mutate();
            this.shadow[0].setAlpha(0);
            this.shadow[1].setAlpha(0);
            this.shadow[0].setCallback(this);
            this.shadow[1].setCallback(this);
            this.contentScrollView = new ScrollView(getContext()) {
                protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                    boolean result = super.drawChild(canvas, child, drawingTime);
                    if (AlertDialog.this.shadow[0].getPaint().getAlpha() != 0) {
                        AlertDialog.this.shadow[0].setBounds(0, getScrollY(), getMeasuredWidth(), getScrollY() + AndroidUtilities.dp(3.0f));
                        AlertDialog.this.shadow[0].draw(canvas);
                    }
                    if (AlertDialog.this.shadow[1].getPaint().getAlpha() != 0) {
                        AlertDialog.this.shadow[1].setBounds(0, (getScrollY() + getMeasuredHeight()) - AndroidUtilities.dp(3.0f), getMeasuredWidth(), getScrollY() + getMeasuredHeight());
                        AlertDialog.this.shadow[1].draw(canvas);
                    }
                    return result;
                }
            };
            this.contentScrollView.setVerticalScrollBarEnabled(false);
            AndroidUtilities.setScrollViewEdgeEffectColor(this.contentScrollView, getThemeColor("dialogScrollGlow"));
            containerView.addView(this.contentScrollView, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 0.0f));
            this.scrollContainer = new LinearLayout(getContext());
            this.scrollContainer.setOrientation(1);
            this.contentScrollView.addView(this.scrollContainer, new FrameLayout.LayoutParams(-1, -2));
        }
        this.messageTextView = new TextView(getContext());
        this.messageTextView.setTextColor(getThemeColor("dialogTextBlack"));
        this.messageTextView.setTextSize(1, 16.0f);
        this.messageTextView.setMovementMethod(new LinkMovementMethodMy());
        this.messageTextView.setLinkTextColor(getThemeColor("dialogTextLink"));
        this.messageTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        if (this.progressViewStyle == 1) {
            int i3;
            int i4;
            this.progressViewContainer = new FrameLayout(getContext());
            containerView.addView(this.progressViewContainer, LayoutHelper.createLinear(-1, 44, 51, 23, this.title == null ? 24 : 0, 23, 24));
            radialProgressView = new RadialProgressView(getContext());
            radialProgressView.setProgressColor(getThemeColor("dialogProgressCircle"));
            this.progressViewContainer.addView(radialProgressView, LayoutHelper.createFrame(44, 44, (LocaleController.isRTL ? 5 : 3) | 48));
            this.messageTextView.setLines(1);
            this.messageTextView.setEllipsize(TruncateAt.END);
            frameLayout = this.progressViewContainer;
            view = this.messageTextView;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            i |= 16;
            if (LocaleController.isRTL) {
                i3 = 0;
            } else {
                i3 = 62;
            }
            float f = (float) i3;
            if (LocaleController.isRTL) {
                i4 = 62;
            } else {
                i4 = 0;
            }
            frameLayout.addView(view, LayoutHelper.createFrame(-2, -2.0f, i, f, 0.0f, (float) i4, 0.0f));
        } else if (this.progressViewStyle == 2) {
            containerView.addView(this.messageTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 24, this.title == null ? 19 : 0, 24, 20));
            this.lineProgressView = new LineProgressView(getContext());
            this.lineProgressView.setProgress(((float) this.currentProgress) / 100.0f, false);
            this.lineProgressView.setProgressColor(getThemeColor("dialogLineProgress"));
            this.lineProgressView.setBackColor(getThemeColor("dialogLineProgressBackground"));
            containerView.addView(this.lineProgressView, LayoutHelper.createLinear(-1, 4, 19, 24, 0, 24, 0));
            this.lineProgressViewPercent = new TextView(getContext());
            this.lineProgressViewPercent.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.lineProgressViewPercent.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.lineProgressViewPercent.setTextColor(getThemeColor("dialogTextGray2"));
            this.lineProgressViewPercent.setTextSize(1, 14.0f);
            containerView.addView(this.lineProgressViewPercent, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 23, 4, 23, 24));
            updateLineProgressTextView();
        } else if (this.progressViewStyle == 3) {
            setCanceledOnTouchOutside(false);
            setCancelable(false);
            this.progressViewContainer = new FrameLayout(getContext());
            this.progressViewContainer.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), Theme.getColor("dialog_inlineProgressBackground")));
            containerView.addView(this.progressViewContainer, LayoutHelper.createLinear(86, 86, 17));
            radialProgressView = new RadialProgressView(getContext());
            radialProgressView.setProgressColor(getThemeColor("dialog_inlineProgress"));
            this.progressViewContainer.addView(radialProgressView, LayoutHelper.createLinear(86, 86));
        } else {
            LinearLayout linearLayout = this.scrollContainer;
            view = this.messageTextView;
            i = (LocaleController.isRTL ? 5 : 3) | 48;
            i2 = (this.customView == null && this.items == null) ? 0 : this.customViewOffset;
            linearLayout.addView(view, LayoutHelper.createLinear(-2, -2, i, 24, 0, 24, i2));
        }
        if (TextUtils.isEmpty(this.message)) {
            this.messageTextView.setVisibility(8);
        } else {
            this.messageTextView.setText(this.message);
            this.messageTextView.setVisibility(0);
        }
        if (this.items != null) {
            int a = 0;
            while (a < this.items.length) {
                if (this.items[a] != null) {
                    AlertDialogCell cell = new AlertDialogCell(getContext());
                    cell.setTextAndIcon(this.items[a], this.itemIcons != null ? this.itemIcons[a] : 0);
                    cell.setTag(Integer.valueOf(a));
                    this.itemViews.add(cell);
                    this.scrollContainer.addView(cell, LayoutHelper.createLinear(-1, 50));
                    cell.setOnClickListener(new AlertDialog$$Lambda$1(this));
                }
                a++;
            }
        }
        if (this.customView != null) {
            if (this.customView.getParent() != null) {
                ((ViewGroup) this.customView.getParent()).removeView(this.customView);
            }
            this.scrollContainer.addView(this.customView, LayoutHelper.createLinear(-1, -2));
        }
        if (hasButtons) {
            this.buttonsLayout = new FrameLayout(getContext()) {
                protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                    int count = getChildCount();
                    View positiveButton = null;
                    int width = right - left;
                    for (int a = 0; a < count; a++) {
                        View child = getChildAt(a);
                        Integer tag = (Integer) child.getTag();
                        if (tag == null) {
                            int l;
                            int t;
                            int w = child.getMeasuredWidth();
                            int h = child.getMeasuredHeight();
                            if (positiveButton != null) {
                                l = positiveButton.getLeft() + ((positiveButton.getMeasuredWidth() - w) / 2);
                                t = positiveButton.getTop() + ((positiveButton.getMeasuredHeight() - h) / 2);
                            } else {
                                t = 0;
                                l = 0;
                            }
                            child.layout(l, t, l + w, t + h);
                        } else if (tag.intValue() == -1) {
                            positiveButton = child;
                            if (LocaleController.isRTL) {
                                child.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + child.getMeasuredWidth(), getPaddingTop() + child.getMeasuredHeight());
                            } else {
                                child.layout((width - getPaddingRight()) - child.getMeasuredWidth(), getPaddingTop(), (width - getPaddingRight()) + child.getMeasuredWidth(), getPaddingTop() + child.getMeasuredHeight());
                            }
                        } else if (tag.intValue() == -2) {
                            int x;
                            if (LocaleController.isRTL) {
                                x = getPaddingLeft();
                                if (positiveButton != null) {
                                    x += positiveButton.getMeasuredWidth() + AndroidUtilities.dp(8.0f);
                                }
                                child.layout(x, getPaddingTop(), child.getMeasuredWidth() + x, getPaddingTop() + child.getMeasuredHeight());
                            } else {
                                x = (width - getPaddingRight()) - child.getMeasuredWidth();
                                if (positiveButton != null) {
                                    x -= positiveButton.getMeasuredWidth() + AndroidUtilities.dp(8.0f);
                                }
                                child.layout(x, getPaddingTop(), child.getMeasuredWidth() + x, getPaddingTop() + child.getMeasuredHeight());
                            }
                        } else if (tag.intValue() == -3) {
                            if (LocaleController.isRTL) {
                                child.layout((width - getPaddingRight()) - child.getMeasuredWidth(), getPaddingTop(), width - getPaddingRight(), getPaddingTop() + child.getMeasuredHeight());
                            } else {
                                child.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + child.getMeasuredWidth(), getPaddingTop() + child.getMeasuredHeight());
                            }
                        }
                    }
                }

                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    int totalWidth = 0;
                    int availableWidth = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
                    int count = getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = getChildAt(a);
                        if ((child instanceof TextView) && child.getTag() != null) {
                            totalWidth += child.getMeasuredWidth();
                        }
                    }
                    if (totalWidth > availableWidth) {
                        View negative = findViewWithTag(Integer.valueOf(-2));
                        View neuntral = findViewWithTag(Integer.valueOf(-3));
                        if (negative != null && neuntral != null) {
                            if (negative.getMeasuredWidth() < neuntral.getMeasuredWidth()) {
                                neuntral.measure(MeasureSpec.makeMeasureSpec(neuntral.getMeasuredWidth() - (totalWidth - availableWidth), NUM), MeasureSpec.makeMeasureSpec(neuntral.getMeasuredHeight(), NUM));
                            } else {
                                negative.measure(MeasureSpec.makeMeasureSpec(negative.getMeasuredWidth() - (totalWidth - availableWidth), NUM), MeasureSpec.makeMeasureSpec(negative.getMeasuredHeight(), NUM));
                            }
                        }
                    }
                }
            };
            this.buttonsLayout.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            containerView.addView(this.buttonsLayout, LayoutHelper.createLinear(-1, 52));
            if (this.positiveButtonText != null) {
                radialProgressView = new TextView(getContext()) {
                    public void setEnabled(boolean enabled) {
                        super.setEnabled(enabled);
                        setAlpha(enabled ? 1.0f : 0.5f);
                    }
                };
                radialProgressView.setMinWidth(AndroidUtilities.dp(64.0f));
                radialProgressView.setTag(Integer.valueOf(-1));
                radialProgressView.setTextSize(1, 14.0f);
                radialProgressView.setTextColor(getThemeColor("dialogButton"));
                radialProgressView.setGravity(17);
                radialProgressView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                radialProgressView.setText(this.positiveButtonText.toString().toUpperCase());
                radialProgressView.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
                radialProgressView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                this.buttonsLayout.addView(radialProgressView, LayoutHelper.createFrame(-2, 36, 53));
                radialProgressView.setOnClickListener(new AlertDialog$$Lambda$2(this));
            }
            if (this.negativeButtonText != null) {
                radialProgressView = new TextView(getContext()) {
                    public void setEnabled(boolean enabled) {
                        super.setEnabled(enabled);
                        setAlpha(enabled ? 1.0f : 0.5f);
                    }
                };
                radialProgressView.setMinWidth(AndroidUtilities.dp(64.0f));
                radialProgressView.setTag(Integer.valueOf(-2));
                radialProgressView.setTextSize(1, 14.0f);
                radialProgressView.setTextColor(getThemeColor("dialogButton"));
                radialProgressView.setGravity(17);
                radialProgressView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                radialProgressView.setEllipsize(TruncateAt.END);
                radialProgressView.setSingleLine(true);
                radialProgressView.setText(this.negativeButtonText.toString().toUpperCase());
                radialProgressView.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
                radialProgressView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                this.buttonsLayout.addView(radialProgressView, LayoutHelper.createFrame(-2, 36, 53));
                radialProgressView.setOnClickListener(new AlertDialog$$Lambda$3(this));
            }
            if (this.neutralButtonText != null) {
                radialProgressView = new TextView(getContext()) {
                    public void setEnabled(boolean enabled) {
                        super.setEnabled(enabled);
                        setAlpha(enabled ? 1.0f : 0.5f);
                    }
                };
                radialProgressView.setMinWidth(AndroidUtilities.dp(64.0f));
                radialProgressView.setTag(Integer.valueOf(-3));
                radialProgressView.setTextSize(1, 14.0f);
                radialProgressView.setTextColor(getThemeColor("dialogButton"));
                radialProgressView.setGravity(17);
                radialProgressView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                radialProgressView.setEllipsize(TruncateAt.END);
                radialProgressView.setSingleLine(true);
                radialProgressView.setText(this.neutralButtonText.toString().toUpperCase());
                radialProgressView.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
                radialProgressView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                this.buttonsLayout.addView(radialProgressView, LayoutHelper.createFrame(-2, 36, 51));
                radialProgressView.setOnClickListener(new AlertDialog$$Lambda$4(this));
            }
        }
        Window window = getWindow();
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(window.getAttributes());
        if (this.progressViewStyle == 3) {
            params.width = -1;
        } else {
            int maxWidth;
            params.dimAmount = 0.6f;
            params.flags |= 2;
            this.lastScreenWidth = AndroidUtilities.displaySize.x;
            int calculatedWidth = AndroidUtilities.displaySize.x - AndroidUtilities.dp(48.0f);
            if (!AndroidUtilities.isTablet()) {
                maxWidth = AndroidUtilities.dp(356.0f);
            } else if (AndroidUtilities.isSmallTablet()) {
                maxWidth = AndroidUtilities.dp(446.0f);
            } else {
                maxWidth = AndroidUtilities.dp(496.0f);
            }
            params.width = (Math.min(maxWidth, calculatedWidth) + this.backgroundPaddings.left) + this.backgroundPaddings.right;
        }
        if (this.customView != null) {
            if (canTextInput(this.customView)) {
                params.softInputMode = 4;
                window.setAttributes(params);
            }
        }
        params.flags |= 131072;
        window.setAttributes(params);
    }

    final /* synthetic */ void lambda$onCreate$0$AlertDialog(View v) {
        if (this.onClickListener != null) {
            this.onClickListener.onClick(this, ((Integer) v.getTag()).intValue());
        }
        dismiss();
    }

    final /* synthetic */ void lambda$onCreate$1$AlertDialog(View v) {
        if (this.positiveButtonListener != null) {
            this.positiveButtonListener.onClick(this, -1);
        }
        if (this.dismissDialogByButtons) {
            dismiss();
        }
    }

    final /* synthetic */ void lambda$onCreate$2$AlertDialog(View v) {
        if (this.negativeButtonListener != null) {
            this.negativeButtonListener.onClick(this, -2);
        }
        if (this.dismissDialogByButtons) {
            cancel();
        }
    }

    final /* synthetic */ void lambda$onCreate$3$AlertDialog(View v) {
        if (this.neutralButtonListener != null) {
            this.neutralButtonListener.onClick(this, -2);
        }
        if (this.dismissDialogByButtons) {
            dismiss();
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        if (this.onBackButtonListener != null) {
            this.onBackButtonListener.onClick(this, -2);
        }
    }

    private void showCancelAlert() {
        if (this.canCacnel && this.cancelDialog == null) {
            Builder builder = new Builder(getContext());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.getString("StopLoading", R.string.StopLoading));
            builder.setPositiveButton(LocaleController.getString("WaitMore", R.string.WaitMore), null);
            builder.setNegativeButton(LocaleController.getString("Stop", R.string.Stop), new AlertDialog$$Lambda$5(this));
            builder.setOnDismissListener(new AlertDialog$$Lambda$6(this));
            this.cancelDialog = builder.show();
        }
    }

    final /* synthetic */ void lambda$showCancelAlert$4$AlertDialog(DialogInterface dialogInterface, int i) {
        if (this.onCancelListener != null) {
            this.onCancelListener.onCancel(this);
        }
        dismiss();
    }

    final /* synthetic */ void lambda$showCancelAlert$5$AlertDialog(DialogInterface dialog) {
        this.cancelDialog = null;
    }

    private void runShadowAnimation(final int num, boolean show) {
        if ((show && !this.shadowVisibility[num]) || (!show && this.shadowVisibility[num])) {
            this.shadowVisibility[num] = show;
            if (this.shadowAnimation[num] != null) {
                this.shadowAnimation[num].cancel();
            }
            this.shadowAnimation[num] = new AnimatorSet();
            if (this.shadow[num] != null) {
                AnimatorSet animatorSet = this.shadowAnimation[num];
                Animator[] animatorArr = new Animator[1];
                Object obj = this.shadow[num];
                String str = "alpha";
                int[] iArr = new int[1];
                iArr[0] = show ? 255 : 0;
                animatorArr[0] = ObjectAnimator.ofInt(obj, str, iArr);
                animatorSet.playTogether(animatorArr);
            }
            this.shadowAnimation[num].setDuration(150);
            this.shadowAnimation[num].addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (AlertDialog.this.shadowAnimation[num] != null && AlertDialog.this.shadowAnimation[num].equals(animation)) {
                        AlertDialog.this.shadowAnimation[num] = null;
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (AlertDialog.this.shadowAnimation[num] != null && AlertDialog.this.shadowAnimation[num].equals(animation)) {
                        AlertDialog.this.shadowAnimation[num] = null;
                    }
                }
            });
            try {
                this.shadowAnimation[num].start();
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    public void setProgressStyle(int style) {
        this.progressViewStyle = style;
    }

    public void setDismissDialogByButtons(boolean value) {
        this.dismissDialogByButtons = value;
    }

    public void setProgress(int progress) {
        this.currentProgress = progress;
        if (this.lineProgressView != null) {
            this.lineProgressView.setProgress(((float) progress) / 100.0f, true);
            updateLineProgressTextView();
        }
    }

    private void updateLineProgressTextView() {
        this.lineProgressViewPercent.setText(String.format("%d%%", new Object[]{Integer.valueOf(this.currentProgress)}));
    }

    public void setCanCacnel(boolean value) {
        this.canCacnel = value;
    }

    private boolean canTextInput(View v) {
        if (v.onCheckIsTextEditor()) {
            return true;
        }
        if (!(v instanceof ViewGroup)) {
            return false;
        }
        ViewGroup vg = (ViewGroup) v;
        int i = vg.getChildCount();
        while (i > 0) {
            i--;
            if (canTextInput(vg.getChildAt(i))) {
                return true;
            }
        }
        return false;
    }

    public void dismiss() {
        if (this.cancelDialog != null) {
            this.cancelDialog.dismiss();
        }
        super.dismiss();
    }

    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
    }

    public void setTopImage(int resId, int backgroundColor) {
        this.topResId = resId;
        this.topBackgroundColor = backgroundColor;
    }

    public void setTopHeight(int value) {
        this.topHeight = value;
    }

    public void setTopImage(Drawable drawable, int backgroundColor) {
        this.topDrawable = drawable;
        this.topBackgroundColor = backgroundColor;
    }

    public void setTitle(CharSequence text) {
        this.title = text;
        if (this.titleTextView != null) {
            this.titleTextView.setText(text);
        }
    }

    public void setSecondTitle(CharSequence text) {
        this.secondTitle = text;
    }

    public void setPositiveButton(CharSequence text, OnClickListener listener) {
        this.positiveButtonText = text;
        this.positiveButtonListener = listener;
    }

    public void setNegativeButton(CharSequence text, OnClickListener listener) {
        this.negativeButtonText = text;
        this.negativeButtonListener = listener;
    }

    public void setNeutralButton(CharSequence text, OnClickListener listener) {
        this.neutralButtonText = text;
        this.neutralButtonListener = listener;
    }

    public void setItemColor(int item, int color, int icon) {
        if (item >= 0 && item < this.itemViews.size()) {
            AlertDialogCell cell = (AlertDialogCell) this.itemViews.get(item);
            cell.textView.setTextColor(color);
            cell.imageView.setColorFilter(new PorterDuffColorFilter(icon, Mode.MULTIPLY));
        }
    }

    public void setMessage(CharSequence text) {
        this.message = text;
        if (this.messageTextView == null) {
            return;
        }
        if (TextUtils.isEmpty(this.message)) {
            this.messageTextView.setVisibility(8);
            return;
        }
        this.messageTextView.setText(this.message);
        this.messageTextView.setVisibility(0);
    }

    public void setButton(int type, CharSequence text, OnClickListener listener) {
        switch (type) {
            case -3:
                this.neutralButtonText = text;
                this.neutralButtonListener = listener;
                return;
            case -2:
                this.negativeButtonText = text;
                this.negativeButtonListener = listener;
                return;
            case -1:
                this.positiveButtonText = text;
                this.positiveButtonListener = listener;
                return;
            default:
                return;
        }
    }

    public View getButton(int type) {
        if (this.buttonsLayout != null) {
            return this.buttonsLayout.findViewWithTag(Integer.valueOf(type));
        }
        return null;
    }

    public void invalidateDrawable(Drawable who) {
        this.contentScrollView.invalidate();
        this.scrollContainer.invalidate();
    }

    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        if (this.contentScrollView != null) {
            this.contentScrollView.postDelayed(what, when);
        }
    }

    public void unscheduleDrawable(Drawable who, Runnable what) {
        if (this.contentScrollView != null) {
            this.contentScrollView.removeCallbacks(what);
        }
    }

    public void setOnCancelListener(OnCancelListener listener) {
        this.onCancelListener = listener;
        super.setOnCancelListener(listener);
    }

    public void setPositiveButtonListener(OnClickListener listener) {
        this.positiveButtonListener = listener;
    }

    protected int getThemeColor(String key) {
        return Theme.getColor(key);
    }
}

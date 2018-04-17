package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
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
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LineProgressView;
import org.telegram.ui.Components.RadialProgressView;

public class AlertDialog extends Dialog implements Callback {
    private Rect backgroundPaddings = new Rect();
    private FrameLayout buttonsLayout;
    private ScrollView contentScrollView;
    private int currentProgress;
    private View customView;
    private int customViewOffset = 20;
    private int[] itemIcons;
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
    private OnClickListener onClickListener;
    private OnDismissListener onDismissListener;
    private OnScrollChangedListener onScrollChangedListener;
    private OnClickListener positiveButtonListener;
    private CharSequence positiveButtonText;
    private FrameLayout progressViewContainer;
    private int progressViewStyle;
    private TextView progressViewTextView;
    private LinearLayout scrollContainer;
    private BitmapDrawable[] shadow = new BitmapDrawable[2];
    private AnimatorSet[] shadowAnimation = new AnimatorSet[2];
    private Drawable shadowDrawable;
    private boolean[] shadowVisibility = new boolean[2];
    private CharSequence subtitle;
    private TextView subtitleTextView;
    private CharSequence title;
    private TextView titleTextView;
    private int topBackgroundColor;
    private Drawable topDrawable;
    private ImageView topImageView;
    private int topResId;

    /* renamed from: org.telegram.ui.ActionBar.AlertDialog$3 */
    class C07293 implements View.OnClickListener {
        C07293() {
        }

        public void onClick(View v) {
            if (AlertDialog.this.onClickListener != null) {
                AlertDialog.this.onClickListener.onClick(AlertDialog.this, ((Integer) v.getTag()).intValue());
            }
            AlertDialog.this.dismiss();
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.AlertDialog$6 */
    class C07326 implements View.OnClickListener {
        C07326() {
        }

        public void onClick(View v) {
            if (AlertDialog.this.positiveButtonListener != null) {
                AlertDialog.this.positiveButtonListener.onClick(AlertDialog.this, -1);
            }
            AlertDialog.this.dismiss();
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.AlertDialog$8 */
    class C07348 implements View.OnClickListener {
        C07348() {
        }

        public void onClick(View v) {
            if (AlertDialog.this.negativeButtonListener != null) {
                AlertDialog.this.negativeButtonListener.onClick(AlertDialog.this, -2);
            }
            AlertDialog.this.cancel();
        }
    }

    public static class AlertDialogCell extends FrameLayout {
        private ImageView imageView;
        private TextView textView;

        public AlertDialogCell(Context context) {
            super(context);
            setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 2));
            setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
            this.imageView = new ImageView(context);
            this.imageView.setScaleType(ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogIcon), Mode.MULTIPLY));
            int i = 3;
            addView(this.imageView, LayoutHelper.createFrame(24, 24, (LocaleController.isRTL ? 5 : 3) | 16));
            this.textView = new TextView(context);
            this.textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TruncateAt.END);
            this.textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
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

        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            this.alertDialog.setOnDismissListener(onDismissListener);
            return this;
        }
    }

    public AlertDialog(Context context, int progressStyle) {
        super(context, R.style.TransparentDialog);
        this.shadowDrawable = context.getResources().getDrawable(R.drawable.popup_fixed_alert).mutate();
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(getThemeColor(Theme.key_dialogBackground), Mode.MULTIPLY));
        this.shadowDrawable.getPadding(this.backgroundPaddings);
        this.progressViewStyle = progressStyle;
    }

    protected void onCreate(Bundle savedInstanceState) {
        boolean hasButtons;
        int i;
        View view;
        int i2;
        LinearLayout linearLayout;
        View view2;
        int i3;
        int i4;
        int a;
        TextView textView;
        int calculatedWidth;
        int maxWidth;
        Window window;
        LayoutParams params;
        super.onCreate(savedInstanceState);
        LinearLayout containerView = new LinearLayout(getContext()) {
            private boolean inLayout;

            /* renamed from: org.telegram.ui.ActionBar.AlertDialog$1$1 */
            class C07251 implements Runnable {
                C07251() {
                }

                public void run() {
                    int maxWidth;
                    AlertDialog.this.lastScreenWidth = AndroidUtilities.displaySize.x;
                    int calculatedWidth = AndroidUtilities.displaySize.x - AndroidUtilities.dp(56.0f);
                    if (!AndroidUtilities.isTablet()) {
                        maxWidth = AndroidUtilities.dp(356.0f);
                    } else if (AndroidUtilities.isSmallTablet()) {
                        maxWidth = AndroidUtilities.dp(NUM);
                    } else {
                        maxWidth = AndroidUtilities.dp(496.0f);
                    }
                    Window window = AlertDialog.this.getWindow();
                    LayoutParams params = new LayoutParams();
                    params.copyFrom(window.getAttributes());
                    params.width = (Math.min(maxWidth, calculatedWidth) + AlertDialog.this.backgroundPaddings.left) + AlertDialog.this.backgroundPaddings.right;
                    window.setAttributes(params);
                }
            }

            /* renamed from: org.telegram.ui.ActionBar.AlertDialog$1$2 */
            class C07262 implements OnScrollChangedListener {
                C07262() {
                }

                public void onScrollChanged() {
                    AlertDialog alertDialog = AlertDialog.this;
                    boolean z = false;
                    boolean z2 = AlertDialog.this.titleTextView != null && AlertDialog.this.contentScrollView.getScrollY() > AlertDialog.this.scrollContainer.getTop();
                    alertDialog.runShadowAnimation(0, z2);
                    alertDialog = AlertDialog.this;
                    if (AlertDialog.this.buttonsLayout != null && AlertDialog.this.contentScrollView.getScrollY() + AlertDialog.this.contentScrollView.getHeight() < AlertDialog.this.scrollContainer.getBottom()) {
                        z = true;
                    }
                    alertDialog.runShadowAnimation(1, z);
                    AlertDialog.this.contentScrollView.invalidate();
                }
            }

            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int count;
                this.inLayout = true;
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int availableHeight = (MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop()) - getPaddingBottom();
                int maxContentHeight = availableHeight;
                int availableWidth = (width - getPaddingLeft()) - getPaddingRight();
                int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(availableWidth - AndroidUtilities.dp(48.0f), NUM);
                int childFullWidthMeasureSpec = MeasureSpec.makeMeasureSpec(availableWidth, NUM);
                if (AlertDialog.this.buttonsLayout != null) {
                    count = AlertDialog.this.buttonsLayout.getChildCount();
                    for (int a = 0; a < count; a++) {
                        ((TextView) AlertDialog.this.buttonsLayout.getChildAt(a)).setMaxWidth(AndroidUtilities.dp((float) ((availableWidth - AndroidUtilities.dp(24.0f)) / 2)));
                    }
                    AlertDialog.this.buttonsLayout.measure(childFullWidthMeasureSpec, heightMeasureSpec);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) AlertDialog.this.buttonsLayout.getLayoutParams();
                    availableHeight -= (AlertDialog.this.buttonsLayout.getMeasuredHeight() + layoutParams.bottomMargin) + layoutParams.topMargin;
                }
                if (AlertDialog.this.titleTextView != null) {
                    AlertDialog.this.titleTextView.measure(childWidthMeasureSpec, heightMeasureSpec);
                    LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) AlertDialog.this.titleTextView.getLayoutParams();
                    availableHeight -= (AlertDialog.this.titleTextView.getMeasuredHeight() + layoutParams2.bottomMargin) + layoutParams2.topMargin;
                }
                if (AlertDialog.this.subtitleTextView != null) {
                    AlertDialog.this.subtitleTextView.measure(childWidthMeasureSpec, heightMeasureSpec);
                    layoutParams2 = (LinearLayout.LayoutParams) AlertDialog.this.subtitleTextView.getLayoutParams();
                    availableHeight -= (AlertDialog.this.subtitleTextView.getMeasuredHeight() + layoutParams2.bottomMargin) + layoutParams2.topMargin;
                }
                if (AlertDialog.this.topImageView != null) {
                    AlertDialog.this.topImageView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(132.0f), NUM));
                    availableHeight -= AlertDialog.this.topImageView.getMeasuredHeight() - AndroidUtilities.dp(8.0f);
                }
                LinearLayout.LayoutParams layoutParams3;
                if (AlertDialog.this.progressViewStyle == 0) {
                    layoutParams3 = (LinearLayout.LayoutParams) AlertDialog.this.contentScrollView.getLayoutParams();
                    if (AlertDialog.this.customView != null) {
                        count = (AlertDialog.this.titleTextView == null && AlertDialog.this.messageTextView.getVisibility() == 8 && AlertDialog.this.items == null) ? AndroidUtilities.dp(16.0f) : 0;
                        layoutParams3.topMargin = count;
                        layoutParams3.bottomMargin = AlertDialog.this.buttonsLayout == null ? AndroidUtilities.dp(8.0f) : 0;
                    } else if (AlertDialog.this.items != null) {
                        count = (AlertDialog.this.titleTextView == null && AlertDialog.this.messageTextView.getVisibility() == 8) ? AndroidUtilities.dp(8.0f) : 0;
                        layoutParams3.topMargin = count;
                        layoutParams3.bottomMargin = AndroidUtilities.dp(8.0f);
                    } else if (AlertDialog.this.messageTextView.getVisibility() == 0) {
                        layoutParams3.topMargin = AlertDialog.this.titleTextView == null ? AndroidUtilities.dp(19.0f) : 0;
                        layoutParams3.bottomMargin = AndroidUtilities.dp(20.0f);
                    }
                    availableHeight -= layoutParams3.bottomMargin + layoutParams3.topMargin;
                    AlertDialog.this.contentScrollView.measure(childFullWidthMeasureSpec, MeasureSpec.makeMeasureSpec(availableHeight, Integer.MIN_VALUE));
                    availableHeight -= AlertDialog.this.contentScrollView.getMeasuredHeight();
                } else {
                    if (AlertDialog.this.progressViewContainer != null) {
                        AlertDialog.this.progressViewContainer.measure(childWidthMeasureSpec, MeasureSpec.makeMeasureSpec(availableHeight, Integer.MIN_VALUE));
                        layoutParams2 = (LinearLayout.LayoutParams) AlertDialog.this.progressViewContainer.getLayoutParams();
                        availableHeight -= (AlertDialog.this.progressViewContainer.getMeasuredHeight() + layoutParams2.bottomMargin) + layoutParams2.topMargin;
                    } else if (AlertDialog.this.messageTextView != null) {
                        AlertDialog.this.messageTextView.measure(childWidthMeasureSpec, MeasureSpec.makeMeasureSpec(availableHeight, Integer.MIN_VALUE));
                        if (AlertDialog.this.messageTextView.getVisibility() != 8) {
                            layoutParams2 = (LinearLayout.LayoutParams) AlertDialog.this.messageTextView.getLayoutParams();
                            availableHeight -= (AlertDialog.this.messageTextView.getMeasuredHeight() + layoutParams2.bottomMargin) + layoutParams2.topMargin;
                        }
                    }
                    if (AlertDialog.this.lineProgressView != null) {
                        AlertDialog.this.lineProgressView.measure(childWidthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(4.0f), NUM));
                        layoutParams3 = (LinearLayout.LayoutParams) AlertDialog.this.lineProgressView.getLayoutParams();
                        availableHeight -= (AlertDialog.this.lineProgressView.getMeasuredHeight() + layoutParams3.bottomMargin) + layoutParams3.topMargin;
                        AlertDialog.this.lineProgressViewPercent.measure(childWidthMeasureSpec, MeasureSpec.makeMeasureSpec(availableHeight, Integer.MIN_VALUE));
                        layoutParams3 = (LinearLayout.LayoutParams) AlertDialog.this.lineProgressViewPercent.getLayoutParams();
                        availableHeight -= (AlertDialog.this.lineProgressViewPercent.getMeasuredHeight() + layoutParams3.bottomMargin) + layoutParams3.topMargin;
                    }
                }
                setMeasuredDimension(width, ((maxContentHeight - availableHeight) + getPaddingTop()) + getPaddingBottom());
                this.inLayout = false;
                if (AlertDialog.this.lastScreenWidth != AndroidUtilities.displaySize.x) {
                    AndroidUtilities.runOnUIThread(new C07251());
                }
            }

            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                if (AlertDialog.this.contentScrollView != null) {
                    if (AlertDialog.this.onScrollChangedListener == null) {
                        AlertDialog.this.onScrollChangedListener = new C07262();
                        AlertDialog.this.contentScrollView.getViewTreeObserver().addOnScrollChangedListener(AlertDialog.this.onScrollChangedListener);
                    }
                    AlertDialog.this.onScrollChangedListener.onScrollChanged();
                }
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
        containerView.setBackgroundDrawable(this.shadowDrawable);
        containerView.setFitsSystemWindows(VERSION.SDK_INT >= 21);
        setContentView(containerView);
        if (r0.positiveButtonText == null && r0.negativeButtonText == null) {
            if (r0.neutralButtonText == null) {
                hasButtons = false;
                if (!(r0.topResId == 0 && r0.topDrawable == null)) {
                    r0.topImageView = new ImageView(getContext());
                    if (r0.topDrawable == null) {
                        r0.topImageView.setImageDrawable(r0.topDrawable);
                    } else {
                        r0.topImageView.setImageResource(r0.topResId);
                    }
                    r0.topImageView.setScaleType(ScaleType.CENTER);
                    r0.topImageView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.popup_fixed_top));
                    r0.topImageView.getBackground().setColorFilter(new PorterDuffColorFilter(r0.topBackgroundColor, Mode.MULTIPLY));
                    r0.topImageView.setPadding(0, 0, 0, 0);
                    containerView.addView(r0.topImageView, LayoutHelper.createLinear(-1, 132, (LocaleController.isRTL ? 5 : 3) | 48, -8, -8, 0, 0));
                }
                i = 14;
                if (r0.title != null) {
                    r0.titleTextView = new TextView(getContext());
                    r0.titleTextView.setText(r0.title);
                    r0.titleTextView.setTextColor(getThemeColor(Theme.key_dialogTextBlack));
                    r0.titleTextView.setTextSize(1, 20.0f);
                    r0.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    r0.titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                    view = r0.titleTextView;
                    i2 = (LocaleController.isRTL ? 5 : 3) | 48;
                    int i5 = r0.subtitle == null ? 2 : r0.items == null ? 14 : 10;
                    containerView.addView(view, LayoutHelper.createLinear(-2, -2, i2, 24, 19, 24, i5));
                }
                if (r0.subtitle != null) {
                    r0.subtitleTextView = new TextView(getContext());
                    r0.subtitleTextView.setText(r0.subtitle);
                    r0.subtitleTextView.setTextColor(getThemeColor(Theme.key_dialogIcon));
                    r0.subtitleTextView.setTextSize(1, 14.0f);
                    r0.subtitleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                    view = r0.subtitleTextView;
                    i2 = (LocaleController.isRTL ? 5 : 3) | 48;
                    if (r0.items == null) {
                        i = 10;
                    }
                    containerView.addView(view, LayoutHelper.createLinear(-2, -2, i2, 24, 0, 24, i));
                }
                if (r0.progressViewStyle == 0) {
                    r0.shadow[0] = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.header_shadow).mutate();
                    r0.shadow[1] = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.header_shadow_reverse).mutate();
                    r0.shadow[0].setAlpha(0);
                    r0.shadow[1].setAlpha(0);
                    r0.shadow[0].setCallback(r0);
                    r0.shadow[1].setCallback(r0);
                    r0.contentScrollView = new ScrollView(getContext()) {
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
                    r0.contentScrollView.setVerticalScrollBarEnabled(false);
                    AndroidUtilities.setScrollViewEdgeEffectColor(r0.contentScrollView, getThemeColor(Theme.key_dialogScrollGlow));
                    containerView.addView(r0.contentScrollView, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 0.0f));
                    r0.scrollContainer = new LinearLayout(getContext());
                    r0.scrollContainer.setOrientation(1);
                    r0.contentScrollView.addView(r0.scrollContainer, new FrameLayout.LayoutParams(-1, -2));
                }
                r0.messageTextView = new TextView(getContext());
                r0.messageTextView.setTextColor(getThemeColor(Theme.key_dialogTextBlack));
                r0.messageTextView.setTextSize(1, 16.0f);
                r0.messageTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                if (r0.progressViewStyle == 1) {
                    r0.progressViewContainer = new FrameLayout(getContext());
                    containerView.addView(r0.progressViewContainer, LayoutHelper.createLinear(-1, 44, 51, 23, r0.title != null ? 24 : 0, 23, 24));
                    RadialProgressView progressView = new RadialProgressView(getContext());
                    progressView.setProgressColor(getThemeColor(Theme.key_dialogProgressCircle));
                    r0.progressViewContainer.addView(progressView, LayoutHelper.createFrame(44, 44, (LocaleController.isRTL ? 5 : 3) | 48));
                    r0.messageTextView.setLines(1);
                    r0.messageTextView.setSingleLine(true);
                    r0.messageTextView.setEllipsize(TruncateAt.END);
                    r0.progressViewContainer.addView(r0.messageTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, (float) (LocaleController.isRTL ? 0 : 62), 0.0f, (float) (LocaleController.isRTL ? 62 : 0), 0.0f));
                } else if (r0.progressViewStyle != 2) {
                    containerView.addView(r0.messageTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 24, r0.title != null ? 19 : 0, 24, 20));
                    r0.lineProgressView = new LineProgressView(getContext());
                    r0.lineProgressView.setProgress(((float) r0.currentProgress) / 100.0f, false);
                    r0.lineProgressView.setProgressColor(getThemeColor(Theme.key_dialogLineProgress));
                    r0.lineProgressView.setBackColor(getThemeColor(Theme.key_dialogLineProgressBackground));
                    containerView.addView(r0.lineProgressView, LayoutHelper.createLinear(-1, 4, 19, 24, 0, 24, 0));
                    r0.lineProgressViewPercent = new TextView(getContext());
                    r0.lineProgressViewPercent.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    r0.lineProgressViewPercent.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                    r0.lineProgressViewPercent.setTextColor(getThemeColor(Theme.key_dialogTextGray2));
                    r0.lineProgressViewPercent.setTextSize(1, 14.0f);
                    containerView.addView(r0.lineProgressViewPercent, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 23, 4, 23, 24));
                    updateLineProgressTextView();
                } else {
                    linearLayout = r0.scrollContainer;
                    view2 = r0.messageTextView;
                    i3 = (LocaleController.isRTL ? 5 : 3) | 48;
                    if (r0.customView == null) {
                        if (r0.items != null) {
                            i4 = 0;
                            linearLayout.addView(view2, LayoutHelper.createLinear(-2, -2, i3, 24, 0, 24, i4));
                        }
                    }
                    i4 = r0.customViewOffset;
                    linearLayout.addView(view2, LayoutHelper.createLinear(-2, -2, i3, 24, 0, 24, i4));
                }
                if (TextUtils.isEmpty(r0.message)) {
                    r0.messageTextView.setText(r0.message);
                    r0.messageTextView.setVisibility(0);
                } else {
                    r0.messageTextView.setVisibility(8);
                }
                if (r0.items != null) {
                    a = 0;
                    while (a < r0.items.length) {
                        if (r0.items[a] == null) {
                            AlertDialogCell cell = new AlertDialogCell(getContext());
                            cell.setTextAndIcon(r0.items[a], r0.itemIcons == null ? r0.itemIcons[a] : 0);
                            r0.scrollContainer.addView(cell, LayoutHelper.createLinear(-1, 48));
                            cell.setTag(Integer.valueOf(a));
                            cell.setOnClickListener(new C07293());
                        }
                        a++;
                    }
                }
                if (r0.customView != null) {
                    if (r0.customView.getParent() != null) {
                        ((ViewGroup) r0.customView.getParent()).removeView(r0.customView);
                    }
                    r0.scrollContainer.addView(r0.customView, LayoutHelper.createLinear(-1, -2));
                }
                if (hasButtons) {
                    r0.buttonsLayout = new FrameLayout(getContext()) {
                        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                            int count = getChildCount();
                            View positiveButton = null;
                            int width = right - left;
                            for (int a = 0; a < count; a++) {
                                View child = getChildAt(a);
                                if (((Integer) child.getTag()).intValue() == -1) {
                                    positiveButton = child;
                                    if (LocaleController.isRTL) {
                                        child.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + child.getMeasuredWidth(), getPaddingTop() + child.getMeasuredHeight());
                                    } else {
                                        child.layout((width - getPaddingRight()) - child.getMeasuredWidth(), getPaddingTop(), (width - getPaddingRight()) + child.getMeasuredWidth(), getPaddingTop() + child.getMeasuredHeight());
                                    }
                                } else if (((Integer) child.getTag()).intValue() == -2) {
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
                                } else if (LocaleController.isRTL) {
                                    child.layout((width - getPaddingRight()) - child.getMeasuredWidth(), getPaddingTop(), (width - getPaddingRight()) + child.getMeasuredWidth(), getPaddingTop() + child.getMeasuredHeight());
                                } else {
                                    child.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + child.getMeasuredWidth(), getPaddingTop() + child.getMeasuredHeight());
                                }
                            }
                        }
                    };
                    r0.buttonsLayout.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
                    containerView.addView(r0.buttonsLayout, LayoutHelper.createLinear(-1, 52));
                    if (r0.positiveButtonText != null) {
                        textView = new TextView(getContext()) {
                            public void setEnabled(boolean enabled) {
                                super.setEnabled(enabled);
                                setAlpha(enabled ? 1.0f : 0.5f);
                            }
                        };
                        textView.setMinWidth(AndroidUtilities.dp(64.0f));
                        textView.setTag(Integer.valueOf(-1));
                        textView.setTextSize(1, 14.0f);
                        textView.setTextColor(getThemeColor(Theme.key_dialogButton));
                        textView.setGravity(17);
                        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        textView.setText(r0.positiveButtonText.toString().toUpperCase());
                        textView.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
                        textView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                        r0.buttonsLayout.addView(textView, LayoutHelper.createFrame(-2, 36, 53));
                        textView.setOnClickListener(new C07326());
                    }
                    if (r0.negativeButtonText != null) {
                        textView = new TextView(getContext()) {
                            public void setEnabled(boolean enabled) {
                                super.setEnabled(enabled);
                                setAlpha(enabled ? 1.0f : 0.5f);
                            }
                        };
                        textView.setMinWidth(AndroidUtilities.dp(64.0f));
                        textView.setTag(Integer.valueOf(-2));
                        textView.setTextSize(1, 14.0f);
                        textView.setTextColor(getThemeColor(Theme.key_dialogButton));
                        textView.setGravity(17);
                        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        textView.setText(r0.negativeButtonText.toString().toUpperCase());
                        textView.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
                        textView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                        r0.buttonsLayout.addView(textView, LayoutHelper.createFrame(-2, 36, 53));
                        textView.setOnClickListener(new C07348());
                    }
                    if (r0.neutralButtonText != null) {
                        textView = new TextView(getContext()) {
                            public void setEnabled(boolean enabled) {
                                super.setEnabled(enabled);
                                setAlpha(enabled ? 1.0f : 0.5f);
                            }
                        };
                        textView.setMinWidth(AndroidUtilities.dp(64.0f));
                        textView.setTag(Integer.valueOf(-3));
                        textView.setTextSize(1, 14.0f);
                        textView.setTextColor(getThemeColor(Theme.key_dialogButton));
                        textView.setGravity(17);
                        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        textView.setText(r0.neutralButtonText.toString().toUpperCase());
                        textView.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
                        textView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                        r0.buttonsLayout.addView(textView, LayoutHelper.createFrame(-2, 36, 51));
                        textView.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if (AlertDialog.this.neutralButtonListener != null) {
                                    AlertDialog.this.neutralButtonListener.onClick(AlertDialog.this, -2);
                                }
                                AlertDialog.this.dismiss();
                            }
                        });
                    }
                }
                r0.lastScreenWidth = AndroidUtilities.displaySize.x;
                calculatedWidth = AndroidUtilities.displaySize.x - AndroidUtilities.dp(48.0f);
                if (AndroidUtilities.isTablet()) {
                    maxWidth = AndroidUtilities.dp(356.0f);
                } else if (AndroidUtilities.isSmallTablet()) {
                    maxWidth = AndroidUtilities.dp(496.0f);
                } else {
                    maxWidth = AndroidUtilities.dp(NUM);
                }
                window = getWindow();
                params = new LayoutParams();
                params.copyFrom(window.getAttributes());
                params.dimAmount = 0.6f;
                params.width = (Math.min(maxWidth, calculatedWidth) + r0.backgroundPaddings.left) + r0.backgroundPaddings.right;
                params.flags |= 2;
                if (r0.customView == null || !canTextInput(r0.customView)) {
                    params.flags |= 131072;
                }
                window.setAttributes(params);
            }
        }
        hasButtons = true;
        r0.topImageView = new ImageView(getContext());
        if (r0.topDrawable == null) {
            r0.topImageView.setImageResource(r0.topResId);
        } else {
            r0.topImageView.setImageDrawable(r0.topDrawable);
        }
        r0.topImageView.setScaleType(ScaleType.CENTER);
        r0.topImageView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.popup_fixed_top));
        r0.topImageView.getBackground().setColorFilter(new PorterDuffColorFilter(r0.topBackgroundColor, Mode.MULTIPLY));
        r0.topImageView.setPadding(0, 0, 0, 0);
        if (LocaleController.isRTL) {
        }
        containerView.addView(r0.topImageView, LayoutHelper.createLinear(-1, 132, (LocaleController.isRTL ? 5 : 3) | 48, -8, -8, 0, 0));
        i = 14;
        if (r0.title != null) {
            r0.titleTextView = new TextView(getContext());
            r0.titleTextView.setText(r0.title);
            r0.titleTextView.setTextColor(getThemeColor(Theme.key_dialogTextBlack));
            r0.titleTextView.setTextSize(1, 20.0f);
            r0.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            if (LocaleController.isRTL) {
            }
            r0.titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            view = r0.titleTextView;
            if (LocaleController.isRTL) {
            }
            i2 = (LocaleController.isRTL ? 5 : 3) | 48;
            if (r0.subtitle == null) {
                if (r0.items == null) {
                }
            }
            containerView.addView(view, LayoutHelper.createLinear(-2, -2, i2, 24, 19, 24, i5));
        }
        if (r0.subtitle != null) {
            r0.subtitleTextView = new TextView(getContext());
            r0.subtitleTextView.setText(r0.subtitle);
            r0.subtitleTextView.setTextColor(getThemeColor(Theme.key_dialogIcon));
            r0.subtitleTextView.setTextSize(1, 14.0f);
            if (LocaleController.isRTL) {
            }
            r0.subtitleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            view = r0.subtitleTextView;
            if (LocaleController.isRTL) {
            }
            i2 = (LocaleController.isRTL ? 5 : 3) | 48;
            if (r0.items == null) {
                i = 10;
            }
            containerView.addView(view, LayoutHelper.createLinear(-2, -2, i2, 24, 0, 24, i));
        }
        if (r0.progressViewStyle == 0) {
            r0.shadow[0] = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.header_shadow).mutate();
            r0.shadow[1] = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.header_shadow_reverse).mutate();
            r0.shadow[0].setAlpha(0);
            r0.shadow[1].setAlpha(0);
            r0.shadow[0].setCallback(r0);
            r0.shadow[1].setCallback(r0);
            r0.contentScrollView = /* anonymous class already generated */;
            r0.contentScrollView.setVerticalScrollBarEnabled(false);
            AndroidUtilities.setScrollViewEdgeEffectColor(r0.contentScrollView, getThemeColor(Theme.key_dialogScrollGlow));
            containerView.addView(r0.contentScrollView, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 0.0f));
            r0.scrollContainer = new LinearLayout(getContext());
            r0.scrollContainer.setOrientation(1);
            r0.contentScrollView.addView(r0.scrollContainer, new FrameLayout.LayoutParams(-1, -2));
        }
        r0.messageTextView = new TextView(getContext());
        r0.messageTextView.setTextColor(getThemeColor(Theme.key_dialogTextBlack));
        r0.messageTextView.setTextSize(1, 16.0f);
        if (LocaleController.isRTL) {
        }
        r0.messageTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        if (r0.progressViewStyle == 1) {
            r0.progressViewContainer = new FrameLayout(getContext());
            if (r0.title != null) {
            }
            containerView.addView(r0.progressViewContainer, LayoutHelper.createLinear(-1, 44, 51, 23, r0.title != null ? 24 : 0, 23, 24));
            RadialProgressView progressView2 = new RadialProgressView(getContext());
            progressView2.setProgressColor(getThemeColor(Theme.key_dialogProgressCircle));
            if (LocaleController.isRTL) {
            }
            r0.progressViewContainer.addView(progressView2, LayoutHelper.createFrame(44, 44, (LocaleController.isRTL ? 5 : 3) | 48));
            r0.messageTextView.setLines(1);
            r0.messageTextView.setSingleLine(true);
            r0.messageTextView.setEllipsize(TruncateAt.END);
            if (LocaleController.isRTL) {
            }
            if (LocaleController.isRTL) {
            }
            if (LocaleController.isRTL) {
            }
            r0.progressViewContainer.addView(r0.messageTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, (float) (LocaleController.isRTL ? 0 : 62), 0.0f, (float) (LocaleController.isRTL ? 62 : 0), 0.0f));
        } else if (r0.progressViewStyle != 2) {
            linearLayout = r0.scrollContainer;
            view2 = r0.messageTextView;
            if (LocaleController.isRTL) {
            }
            i3 = (LocaleController.isRTL ? 5 : 3) | 48;
            if (r0.customView == null) {
                if (r0.items != null) {
                    i4 = 0;
                    linearLayout.addView(view2, LayoutHelper.createLinear(-2, -2, i3, 24, 0, 24, i4));
                }
            }
            i4 = r0.customViewOffset;
            linearLayout.addView(view2, LayoutHelper.createLinear(-2, -2, i3, 24, 0, 24, i4));
        } else {
            if (LocaleController.isRTL) {
            }
            if (r0.title != null) {
            }
            containerView.addView(r0.messageTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 24, r0.title != null ? 19 : 0, 24, 20));
            r0.lineProgressView = new LineProgressView(getContext());
            r0.lineProgressView.setProgress(((float) r0.currentProgress) / 100.0f, false);
            r0.lineProgressView.setProgressColor(getThemeColor(Theme.key_dialogLineProgress));
            r0.lineProgressView.setBackColor(getThemeColor(Theme.key_dialogLineProgressBackground));
            containerView.addView(r0.lineProgressView, LayoutHelper.createLinear(-1, 4, 19, 24, 0, 24, 0));
            r0.lineProgressViewPercent = new TextView(getContext());
            r0.lineProgressViewPercent.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            if (LocaleController.isRTL) {
            }
            r0.lineProgressViewPercent.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            r0.lineProgressViewPercent.setTextColor(getThemeColor(Theme.key_dialogTextGray2));
            r0.lineProgressViewPercent.setTextSize(1, 14.0f);
            if (LocaleController.isRTL) {
            }
            containerView.addView(r0.lineProgressViewPercent, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 23, 4, 23, 24));
            updateLineProgressTextView();
        }
        if (TextUtils.isEmpty(r0.message)) {
            r0.messageTextView.setVisibility(8);
        } else {
            r0.messageTextView.setText(r0.message);
            r0.messageTextView.setVisibility(0);
        }
        if (r0.items != null) {
            a = 0;
            while (a < r0.items.length) {
                if (r0.items[a] == null) {
                    AlertDialogCell cell2 = new AlertDialogCell(getContext());
                    if (r0.itemIcons == null) {
                    }
                    cell2.setTextAndIcon(r0.items[a], r0.itemIcons == null ? r0.itemIcons[a] : 0);
                    r0.scrollContainer.addView(cell2, LayoutHelper.createLinear(-1, 48));
                    cell2.setTag(Integer.valueOf(a));
                    cell2.setOnClickListener(new C07293());
                }
                a++;
            }
        }
        if (r0.customView != null) {
            if (r0.customView.getParent() != null) {
                ((ViewGroup) r0.customView.getParent()).removeView(r0.customView);
            }
            r0.scrollContainer.addView(r0.customView, LayoutHelper.createLinear(-1, -2));
        }
        if (hasButtons) {
            r0.buttonsLayout = /* anonymous class already generated */;
            r0.buttonsLayout.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            containerView.addView(r0.buttonsLayout, LayoutHelper.createLinear(-1, 52));
            if (r0.positiveButtonText != null) {
                textView = /* anonymous class already generated */;
                textView.setMinWidth(AndroidUtilities.dp(64.0f));
                textView.setTag(Integer.valueOf(-1));
                textView.setTextSize(1, 14.0f);
                textView.setTextColor(getThemeColor(Theme.key_dialogButton));
                textView.setGravity(17);
                textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView.setText(r0.positiveButtonText.toString().toUpperCase());
                textView.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
                textView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                r0.buttonsLayout.addView(textView, LayoutHelper.createFrame(-2, 36, 53));
                textView.setOnClickListener(new C07326());
            }
            if (r0.negativeButtonText != null) {
                textView = /* anonymous class already generated */;
                textView.setMinWidth(AndroidUtilities.dp(64.0f));
                textView.setTag(Integer.valueOf(-2));
                textView.setTextSize(1, 14.0f);
                textView.setTextColor(getThemeColor(Theme.key_dialogButton));
                textView.setGravity(17);
                textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView.setText(r0.negativeButtonText.toString().toUpperCase());
                textView.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
                textView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                r0.buttonsLayout.addView(textView, LayoutHelper.createFrame(-2, 36, 53));
                textView.setOnClickListener(new C07348());
            }
            if (r0.neutralButtonText != null) {
                textView = /* anonymous class already generated */;
                textView.setMinWidth(AndroidUtilities.dp(64.0f));
                textView.setTag(Integer.valueOf(-3));
                textView.setTextSize(1, 14.0f);
                textView.setTextColor(getThemeColor(Theme.key_dialogButton));
                textView.setGravity(17);
                textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView.setText(r0.neutralButtonText.toString().toUpperCase());
                textView.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
                textView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                r0.buttonsLayout.addView(textView, LayoutHelper.createFrame(-2, 36, 51));
                textView.setOnClickListener(/* anonymous class already generated */);
            }
        }
        r0.lastScreenWidth = AndroidUtilities.displaySize.x;
        calculatedWidth = AndroidUtilities.displaySize.x - AndroidUtilities.dp(48.0f);
        if (AndroidUtilities.isTablet()) {
            maxWidth = AndroidUtilities.dp(356.0f);
        } else if (AndroidUtilities.isSmallTablet()) {
            maxWidth = AndroidUtilities.dp(496.0f);
        } else {
            maxWidth = AndroidUtilities.dp(NUM);
        }
        window = getWindow();
        params = new LayoutParams();
        params.copyFrom(window.getAttributes());
        params.dimAmount = 0.6f;
        params.width = (Math.min(maxWidth, calculatedWidth) + r0.backgroundPaddings.left) + r0.backgroundPaddings.right;
        params.flags |= 2;
        params.flags |= 131072;
        window.setAttributes(params);
    }

    public void onBackPressed() {
        super.onBackPressed();
        if (this.onBackButtonListener != null) {
            this.onBackButtonListener.onClick(this, -2);
        }
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
                FileLog.m3e(e);
            }
        }
    }

    public void setProgressStyle(int style) {
        this.progressViewStyle = style;
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
        super.dismiss();
    }

    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
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
        return this.buttonsLayout.findViewWithTag(Integer.valueOf(type));
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

    protected int getThemeColor(String key) {
        return Theme.getColor(key);
    }
}

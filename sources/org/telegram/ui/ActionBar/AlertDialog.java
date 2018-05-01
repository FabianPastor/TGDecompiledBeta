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
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
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
    class C07363 implements View.OnClickListener {
        C07363() {
        }

        public void onClick(View view) {
            if (AlertDialog.this.onClickListener != null) {
                AlertDialog.this.onClickListener.onClick(AlertDialog.this, ((Integer) view.getTag()).intValue());
            }
            AlertDialog.this.dismiss();
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.AlertDialog$6 */
    class C07396 implements View.OnClickListener {
        C07396() {
        }

        public void onClick(View view) {
            if (AlertDialog.this.positiveButtonListener != null) {
                AlertDialog.this.positiveButtonListener.onClick(AlertDialog.this, -1);
            }
            AlertDialog.this.dismiss();
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.AlertDialog$8 */
    class C07418 implements View.OnClickListener {
        C07418() {
        }

        public void onClick(View view) {
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
            context = this.textView;
            if (LocaleController.isRTL) {
                i = 5;
            }
            addView(context, LayoutHelper.createFrame(-2, -2, i | 16));
        }

        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(NUM), NUM));
        }

        public void setTextColor(int i) {
            this.textView.setTextColor(i);
        }

        public void setGravity(int i) {
            this.textView.setGravity(i);
        }

        public void setTextAndIcon(CharSequence charSequence, int i) {
            this.textView.setText(charSequence);
            if (i != 0) {
                this.imageView.setImageResource(i);
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

        protected Builder(AlertDialog alertDialog) {
            this.alertDialog = alertDialog;
        }

        public Builder(Context context) {
            this.alertDialog = new AlertDialog(context, 0);
        }

        public Builder(Context context, int i) {
            this.alertDialog = new AlertDialog(context, i);
        }

        public Context getContext() {
            return this.alertDialog.getContext();
        }

        public Builder setItems(CharSequence[] charSequenceArr, OnClickListener onClickListener) {
            this.alertDialog.items = charSequenceArr;
            this.alertDialog.onClickListener = onClickListener;
            return this;
        }

        public Builder setItems(CharSequence[] charSequenceArr, int[] iArr, OnClickListener onClickListener) {
            this.alertDialog.items = charSequenceArr;
            this.alertDialog.itemIcons = iArr;
            this.alertDialog.onClickListener = onClickListener;
            return this;
        }

        public Builder setView(View view) {
            this.alertDialog.customView = view;
            return this;
        }

        public Builder setTitle(CharSequence charSequence) {
            this.alertDialog.title = charSequence;
            return this;
        }

        public Builder setSubtitle(CharSequence charSequence) {
            this.alertDialog.subtitle = charSequence;
            return this;
        }

        public Builder setTopImage(int i, int i2) {
            this.alertDialog.topResId = i;
            this.alertDialog.topBackgroundColor = i2;
            return this;
        }

        public Builder setTopImage(Drawable drawable, int i) {
            this.alertDialog.topDrawable = drawable;
            this.alertDialog.topBackgroundColor = i;
            return this;
        }

        public Builder setMessage(CharSequence charSequence) {
            this.alertDialog.message = charSequence;
            return this;
        }

        public Builder setPositiveButton(CharSequence charSequence, OnClickListener onClickListener) {
            this.alertDialog.positiveButtonText = charSequence;
            this.alertDialog.positiveButtonListener = onClickListener;
            return this;
        }

        public Builder setNegativeButton(CharSequence charSequence, OnClickListener onClickListener) {
            this.alertDialog.negativeButtonText = charSequence;
            this.alertDialog.negativeButtonListener = onClickListener;
            return this;
        }

        public Builder setNeutralButton(CharSequence charSequence, OnClickListener onClickListener) {
            this.alertDialog.neutralButtonText = charSequence;
            this.alertDialog.neutralButtonListener = onClickListener;
            return this;
        }

        public Builder setOnBackButtonListener(OnClickListener onClickListener) {
            this.alertDialog.onBackButtonListener = onClickListener;
            return this;
        }

        public Builder setCustomViewOffset(int i) {
            this.alertDialog.customViewOffset = i;
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

    public AlertDialog(Context context, int i) {
        super(context, C0446R.style.TransparentDialog);
        this.shadowDrawable = context.getResources().getDrawable(C0446R.drawable.popup_fixed_alert).mutate();
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(getThemeColor(Theme.key_dialogBackground), Mode.MULTIPLY));
        this.shadowDrawable.getPadding(this.backgroundPaddings);
        this.progressViewStyle = i;
    }

    protected void onCreate(Bundle bundle) {
        boolean z;
        int i;
        View view;
        int i2;
        LinearLayout linearLayout;
        View view2;
        int i3;
        int i4;
        int i5;
        int dp;
        int dp2;
        Window window;
        LayoutParams layoutParams;
        super.onCreate(bundle);
        View c07341 = new LinearLayout(getContext()) {
            private boolean inLayout;

            /* renamed from: org.telegram.ui.ActionBar.AlertDialog$1$1 */
            class C07321 implements Runnable {
                C07321() {
                }

                public void run() {
                    int dp;
                    AlertDialog.this.lastScreenWidth = AndroidUtilities.displaySize.x;
                    int dp2 = AndroidUtilities.displaySize.x - AndroidUtilities.dp(56.0f);
                    if (!AndroidUtilities.isTablet()) {
                        dp = AndroidUtilities.dp(356.0f);
                    } else if (AndroidUtilities.isSmallTablet()) {
                        dp = AndroidUtilities.dp(446.0f);
                    } else {
                        dp = AndroidUtilities.dp(496.0f);
                    }
                    Window window = AlertDialog.this.getWindow();
                    LayoutParams layoutParams = new LayoutParams();
                    layoutParams.copyFrom(window.getAttributes());
                    layoutParams.width = (Math.min(dp, dp2) + AlertDialog.this.backgroundPaddings.left) + AlertDialog.this.backgroundPaddings.right;
                    window.setAttributes(layoutParams);
                }
            }

            /* renamed from: org.telegram.ui.ActionBar.AlertDialog$1$2 */
            class C07332 implements OnScrollChangedListener {
                C07332() {
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

            public boolean hasOverlappingRendering() {
                return false;
            }

            protected void onMeasure(int i, int i2) {
                this.inLayout = true;
                i = MeasureSpec.getSize(i);
                int size = (MeasureSpec.getSize(i2) - getPaddingTop()) - getPaddingBottom();
                int paddingLeft = (i - getPaddingLeft()) - getPaddingRight();
                int makeMeasureSpec = MeasureSpec.makeMeasureSpec(paddingLeft - AndroidUtilities.dp(48.0f), NUM);
                int makeMeasureSpec2 = MeasureSpec.makeMeasureSpec(paddingLeft, NUM);
                if (AlertDialog.this.buttonsLayout != null) {
                    int childCount = AlertDialog.this.buttonsLayout.getChildCount();
                    for (int i3 = 0; i3 < childCount; i3++) {
                        ((TextView) AlertDialog.this.buttonsLayout.getChildAt(i3)).setMaxWidth(AndroidUtilities.dp((float) ((paddingLeft - AndroidUtilities.dp(24.0f)) / 2)));
                    }
                    AlertDialog.this.buttonsLayout.measure(makeMeasureSpec2, i2);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) AlertDialog.this.buttonsLayout.getLayoutParams();
                    paddingLeft = size - ((AlertDialog.this.buttonsLayout.getMeasuredHeight() + layoutParams.bottomMargin) + layoutParams.topMargin);
                } else {
                    paddingLeft = size;
                }
                if (AlertDialog.this.titleTextView != null) {
                    AlertDialog.this.titleTextView.measure(makeMeasureSpec, i2);
                    LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) AlertDialog.this.titleTextView.getLayoutParams();
                    paddingLeft -= (AlertDialog.this.titleTextView.getMeasuredHeight() + layoutParams2.bottomMargin) + layoutParams2.topMargin;
                }
                if (AlertDialog.this.subtitleTextView != null) {
                    AlertDialog.this.subtitleTextView.measure(makeMeasureSpec, i2);
                    LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) AlertDialog.this.subtitleTextView.getLayoutParams();
                    paddingLeft -= (AlertDialog.this.subtitleTextView.getMeasuredHeight() + layoutParams3.bottomMargin) + layoutParams3.topMargin;
                }
                if (AlertDialog.this.topImageView != 0) {
                    AlertDialog.this.topImageView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(132.0f), NUM));
                    paddingLeft -= AlertDialog.this.topImageView.getMeasuredHeight() - AndroidUtilities.dp(8.0f);
                }
                if (AlertDialog.this.progressViewStyle == 0) {
                    layoutParams3 = (LinearLayout.LayoutParams) AlertDialog.this.contentScrollView.getLayoutParams();
                    if (AlertDialog.this.customView != null) {
                        makeMeasureSpec = (AlertDialog.this.titleTextView == null && AlertDialog.this.messageTextView.getVisibility() == 8 && AlertDialog.this.items == null) ? AndroidUtilities.dp(16.0f) : 0;
                        layoutParams3.topMargin = makeMeasureSpec;
                        layoutParams3.bottomMargin = AlertDialog.this.buttonsLayout == null ? AndroidUtilities.dp(8.0f) : 0;
                    } else if (AlertDialog.this.items != null) {
                        makeMeasureSpec = (AlertDialog.this.titleTextView == null && AlertDialog.this.messageTextView.getVisibility() == 8) ? AndroidUtilities.dp(8.0f) : 0;
                        layoutParams3.topMargin = makeMeasureSpec;
                        layoutParams3.bottomMargin = AndroidUtilities.dp(8.0f);
                    } else if (AlertDialog.this.messageTextView.getVisibility() == 0) {
                        layoutParams3.topMargin = AlertDialog.this.titleTextView == null ? AndroidUtilities.dp(19.0f) : 0;
                        layoutParams3.bottomMargin = AndroidUtilities.dp(20.0f);
                    }
                    paddingLeft -= layoutParams3.bottomMargin + layoutParams3.topMargin;
                    AlertDialog.this.contentScrollView.measure(makeMeasureSpec2, MeasureSpec.makeMeasureSpec(paddingLeft, Integer.MIN_VALUE));
                    paddingLeft -= AlertDialog.this.contentScrollView.getMeasuredHeight();
                } else {
                    if (AlertDialog.this.progressViewContainer != 0) {
                        AlertDialog.this.progressViewContainer.measure(makeMeasureSpec, MeasureSpec.makeMeasureSpec(paddingLeft, Integer.MIN_VALUE));
                        layoutParams3 = (LinearLayout.LayoutParams) AlertDialog.this.progressViewContainer.getLayoutParams();
                        paddingLeft -= (AlertDialog.this.progressViewContainer.getMeasuredHeight() + layoutParams3.bottomMargin) + layoutParams3.topMargin;
                    } else if (AlertDialog.this.messageTextView != 0) {
                        AlertDialog.this.messageTextView.measure(makeMeasureSpec, MeasureSpec.makeMeasureSpec(paddingLeft, Integer.MIN_VALUE));
                        if (AlertDialog.this.messageTextView.getVisibility() != 8) {
                            layoutParams3 = (LinearLayout.LayoutParams) AlertDialog.this.messageTextView.getLayoutParams();
                            paddingLeft -= (AlertDialog.this.messageTextView.getMeasuredHeight() + layoutParams3.bottomMargin) + layoutParams3.topMargin;
                        }
                    }
                    if (AlertDialog.this.lineProgressView != 0) {
                        AlertDialog.this.lineProgressView.measure(makeMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(4.0f), NUM));
                        layoutParams3 = (LinearLayout.LayoutParams) AlertDialog.this.lineProgressView.getLayoutParams();
                        paddingLeft -= (AlertDialog.this.lineProgressView.getMeasuredHeight() + layoutParams3.bottomMargin) + layoutParams3.topMargin;
                        AlertDialog.this.lineProgressViewPercent.measure(makeMeasureSpec, MeasureSpec.makeMeasureSpec(paddingLeft, Integer.MIN_VALUE));
                        layoutParams3 = (LinearLayout.LayoutParams) AlertDialog.this.lineProgressViewPercent.getLayoutParams();
                        paddingLeft -= (AlertDialog.this.lineProgressViewPercent.getMeasuredHeight() + layoutParams3.bottomMargin) + layoutParams3.topMargin;
                    }
                }
                setMeasuredDimension(i, ((size - paddingLeft) + getPaddingTop()) + getPaddingBottom());
                this.inLayout = false;
                if (AlertDialog.this.lastScreenWidth != AndroidUtilities.displaySize.x) {
                    AndroidUtilities.runOnUIThread(new C07321());
                }
            }

            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                if (AlertDialog.this.contentScrollView) {
                    if (!AlertDialog.this.onScrollChangedListener) {
                        AlertDialog.this.onScrollChangedListener = new C07332();
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
        };
        c07341.setOrientation(1);
        c07341.setBackgroundDrawable(this.shadowDrawable);
        c07341.setFitsSystemWindows(VERSION.SDK_INT >= 21);
        setContentView(c07341);
        if (r0.positiveButtonText == null && r0.negativeButtonText == null) {
            if (r0.neutralButtonText == null) {
                z = false;
                if (!(r0.topResId == 0 && r0.topDrawable == null)) {
                    r0.topImageView = new ImageView(getContext());
                    if (r0.topDrawable == null) {
                        r0.topImageView.setImageDrawable(r0.topDrawable);
                    } else {
                        r0.topImageView.setImageResource(r0.topResId);
                    }
                    r0.topImageView.setScaleType(ScaleType.CENTER);
                    r0.topImageView.setBackgroundDrawable(getContext().getResources().getDrawable(C0446R.drawable.popup_fixed_top));
                    r0.topImageView.getBackground().setColorFilter(new PorterDuffColorFilter(r0.topBackgroundColor, Mode.MULTIPLY));
                    r0.topImageView.setPadding(0, 0, 0, 0);
                    c07341.addView(r0.topImageView, LayoutHelper.createLinear(-1, 132, (LocaleController.isRTL ? 5 : 3) | 48, -8, -8, 0, 0));
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
                    int i6 = r0.subtitle == null ? 2 : r0.items == null ? 14 : 10;
                    c07341.addView(view, LayoutHelper.createLinear(-2, -2, i2, 24, 19, 24, i6));
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
                    c07341.addView(view, LayoutHelper.createLinear(-2, -2, i2, 24, 0, 24, i));
                }
                if (r0.progressViewStyle == 0) {
                    r0.shadow[0] = (BitmapDrawable) getContext().getResources().getDrawable(C0446R.drawable.header_shadow).mutate();
                    r0.shadow[1] = (BitmapDrawable) getContext().getResources().getDrawable(C0446R.drawable.header_shadow_reverse).mutate();
                    r0.shadow[0].setAlpha(0);
                    r0.shadow[1].setAlpha(0);
                    r0.shadow[0].setCallback(r0);
                    r0.shadow[1].setCallback(r0);
                    r0.contentScrollView = new ScrollView(getContext()) {
                        protected boolean drawChild(Canvas canvas, View view, long j) {
                            view = super.drawChild(canvas, view, j);
                            if (AlertDialog.this.shadow[0].getPaint().getAlpha() != null) {
                                AlertDialog.this.shadow[0].setBounds(0, getScrollY(), getMeasuredWidth(), getScrollY() + AndroidUtilities.dp(3.0f));
                                AlertDialog.this.shadow[0].draw(canvas);
                            }
                            if (AlertDialog.this.shadow[1].getPaint().getAlpha() != null) {
                                AlertDialog.this.shadow[1].setBounds(0, (getScrollY() + getMeasuredHeight()) - AndroidUtilities.dp(3.0f), getMeasuredWidth(), getScrollY() + getMeasuredHeight());
                                AlertDialog.this.shadow[1].draw(canvas);
                            }
                            return view;
                        }
                    };
                    r0.contentScrollView.setVerticalScrollBarEnabled(false);
                    AndroidUtilities.setScrollViewEdgeEffectColor(r0.contentScrollView, getThemeColor(Theme.key_dialogScrollGlow));
                    c07341.addView(r0.contentScrollView, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 0.0f));
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
                    c07341.addView(r0.progressViewContainer, LayoutHelper.createLinear(-1, 44, 51, 23, r0.title != null ? 24 : 0, 23, 24));
                    view = new RadialProgressView(getContext());
                    view.setProgressColor(getThemeColor(Theme.key_dialogProgressCircle));
                    r0.progressViewContainer.addView(view, LayoutHelper.createFrame(44, 44, (LocaleController.isRTL ? 5 : 3) | 48));
                    r0.messageTextView.setLines(1);
                    r0.messageTextView.setSingleLine(true);
                    r0.messageTextView.setEllipsize(TruncateAt.END);
                    r0.progressViewContainer.addView(r0.messageTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, (float) (LocaleController.isRTL ? 0 : 62), 0.0f, (float) (LocaleController.isRTL ? 62 : 0), 0.0f));
                } else if (r0.progressViewStyle != 2) {
                    c07341.addView(r0.messageTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 24, r0.title != null ? 19 : 0, 24, 20));
                    r0.lineProgressView = new LineProgressView(getContext());
                    r0.lineProgressView.setProgress(((float) r0.currentProgress) / 100.0f, false);
                    r0.lineProgressView.setProgressColor(getThemeColor(Theme.key_dialogLineProgress));
                    r0.lineProgressView.setBackColor(getThemeColor(Theme.key_dialogLineProgressBackground));
                    c07341.addView(r0.lineProgressView, LayoutHelper.createLinear(-1, 4, 19, 24, 0, 24, 0));
                    r0.lineProgressViewPercent = new TextView(getContext());
                    r0.lineProgressViewPercent.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    r0.lineProgressViewPercent.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                    r0.lineProgressViewPercent.setTextColor(getThemeColor(Theme.key_dialogTextGray2));
                    r0.lineProgressViewPercent.setTextSize(1, 14.0f);
                    c07341.addView(r0.lineProgressViewPercent, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 23, 4, 23, 24));
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
                    i5 = 0;
                    while (i5 < r0.items.length) {
                        if (r0.items[i5] == null) {
                            view2 = new AlertDialogCell(getContext());
                            view2.setTextAndIcon(r0.items[i5], r0.itemIcons == null ? r0.itemIcons[i5] : 0);
                            r0.scrollContainer.addView(view2, LayoutHelper.createLinear(-1, 48));
                            view2.setTag(Integer.valueOf(i5));
                            view2.setOnClickListener(new C07363());
                        }
                        i5++;
                    }
                }
                if (r0.customView != null) {
                    if (r0.customView.getParent() != null) {
                        ((ViewGroup) r0.customView.getParent()).removeView(r0.customView);
                    }
                    r0.scrollContainer.addView(r0.customView, LayoutHelper.createLinear(-1, -2));
                }
                if (z) {
                    r0.buttonsLayout = new FrameLayout(getContext()) {
                        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                            z = getChildCount();
                            i3 -= i;
                            i = 0;
                            for (boolean z2 = false; z2 < z; z2++) {
                                i4 = getChildAt(z2);
                                if (((Integer) i4.getTag()).intValue() == -1) {
                                    if (LocaleController.isRTL != 0) {
                                        i4.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + i4.getMeasuredWidth(), getPaddingTop() + i4.getMeasuredHeight());
                                    } else {
                                        i4.layout((i3 - getPaddingRight()) - i4.getMeasuredWidth(), getPaddingTop(), (i3 - getPaddingRight()) + i4.getMeasuredWidth(), getPaddingTop() + i4.getMeasuredHeight());
                                    }
                                    i = i4;
                                } else if (((Integer) i4.getTag()).intValue() == -2) {
                                    int paddingLeft;
                                    if (LocaleController.isRTL) {
                                        paddingLeft = getPaddingLeft();
                                        if (i != 0) {
                                            paddingLeft += i.getMeasuredWidth() + AndroidUtilities.dp(8.0f);
                                        }
                                        i4.layout(paddingLeft, getPaddingTop(), i4.getMeasuredWidth() + paddingLeft, getPaddingTop() + i4.getMeasuredHeight());
                                    } else {
                                        paddingLeft = (i3 - getPaddingRight()) - i4.getMeasuredWidth();
                                        if (i != 0) {
                                            paddingLeft -= i.getMeasuredWidth() + AndroidUtilities.dp(8.0f);
                                        }
                                        i4.layout(paddingLeft, getPaddingTop(), i4.getMeasuredWidth() + paddingLeft, getPaddingTop() + i4.getMeasuredHeight());
                                    }
                                } else if (LocaleController.isRTL) {
                                    i4.layout((i3 - getPaddingRight()) - i4.getMeasuredWidth(), getPaddingTop(), (i3 - getPaddingRight()) + i4.getMeasuredWidth(), getPaddingTop() + i4.getMeasuredHeight());
                                } else {
                                    i4.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + i4.getMeasuredWidth(), getPaddingTop() + i4.getMeasuredHeight());
                                }
                            }
                        }
                    };
                    r0.buttonsLayout.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
                    c07341.addView(r0.buttonsLayout, LayoutHelper.createLinear(-1, 52));
                    if (r0.positiveButtonText != null) {
                        c07341 = new TextView(getContext()) {
                            public void setEnabled(boolean z) {
                                super.setEnabled(z);
                                setAlpha(z ? true : true);
                            }
                        };
                        c07341.setMinWidth(AndroidUtilities.dp(64.0f));
                        c07341.setTag(Integer.valueOf(-1));
                        c07341.setTextSize(1, 14.0f);
                        c07341.setTextColor(getThemeColor(Theme.key_dialogButton));
                        c07341.setGravity(17);
                        c07341.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        c07341.setText(r0.positiveButtonText.toString().toUpperCase());
                        c07341.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
                        c07341.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                        r0.buttonsLayout.addView(c07341, LayoutHelper.createFrame(-2, 36, 53));
                        c07341.setOnClickListener(new C07396());
                    }
                    if (r0.negativeButtonText != null) {
                        c07341 = new TextView(getContext()) {
                            public void setEnabled(boolean z) {
                                super.setEnabled(z);
                                setAlpha(z ? true : true);
                            }
                        };
                        c07341.setMinWidth(AndroidUtilities.dp(64.0f));
                        c07341.setTag(Integer.valueOf(-2));
                        c07341.setTextSize(1, 14.0f);
                        c07341.setTextColor(getThemeColor(Theme.key_dialogButton));
                        c07341.setGravity(17);
                        c07341.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        c07341.setText(r0.negativeButtonText.toString().toUpperCase());
                        c07341.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
                        c07341.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                        r0.buttonsLayout.addView(c07341, LayoutHelper.createFrame(-2, 36, 53));
                        c07341.setOnClickListener(new C07418());
                    }
                    if (r0.neutralButtonText != null) {
                        c07341 = new TextView(getContext()) {
                            public void setEnabled(boolean z) {
                                super.setEnabled(z);
                                setAlpha(z ? true : true);
                            }
                        };
                        c07341.setMinWidth(AndroidUtilities.dp(64.0f));
                        c07341.setTag(Integer.valueOf(-3));
                        c07341.setTextSize(1, 14.0f);
                        c07341.setTextColor(getThemeColor(Theme.key_dialogButton));
                        c07341.setGravity(17);
                        c07341.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        c07341.setText(r0.neutralButtonText.toString().toUpperCase());
                        c07341.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
                        c07341.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                        r0.buttonsLayout.addView(c07341, LayoutHelper.createFrame(-2, 36, 51));
                        c07341.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                if (AlertDialog.this.neutralButtonListener != null) {
                                    AlertDialog.this.neutralButtonListener.onClick(AlertDialog.this, -2);
                                }
                                AlertDialog.this.dismiss();
                            }
                        });
                    }
                }
                r0.lastScreenWidth = AndroidUtilities.displaySize.x;
                dp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(48.0f);
                if (AndroidUtilities.isTablet()) {
                    dp2 = AndroidUtilities.dp(356.0f);
                } else if (AndroidUtilities.isSmallTablet()) {
                    dp2 = AndroidUtilities.dp(496.0f);
                } else {
                    dp2 = AndroidUtilities.dp(446.0f);
                }
                window = getWindow();
                layoutParams = new LayoutParams();
                layoutParams.copyFrom(window.getAttributes());
                layoutParams.dimAmount = 0.6f;
                layoutParams.width = (Math.min(dp2, dp) + r0.backgroundPaddings.left) + r0.backgroundPaddings.right;
                layoutParams.flags |= 2;
                if (r0.customView == null || !canTextInput(r0.customView)) {
                    layoutParams.flags |= 131072;
                }
                window.setAttributes(layoutParams);
            }
        }
        z = true;
        r0.topImageView = new ImageView(getContext());
        if (r0.topDrawable == null) {
            r0.topImageView.setImageResource(r0.topResId);
        } else {
            r0.topImageView.setImageDrawable(r0.topDrawable);
        }
        r0.topImageView.setScaleType(ScaleType.CENTER);
        r0.topImageView.setBackgroundDrawable(getContext().getResources().getDrawable(C0446R.drawable.popup_fixed_top));
        r0.topImageView.getBackground().setColorFilter(new PorterDuffColorFilter(r0.topBackgroundColor, Mode.MULTIPLY));
        r0.topImageView.setPadding(0, 0, 0, 0);
        if (LocaleController.isRTL) {
        }
        c07341.addView(r0.topImageView, LayoutHelper.createLinear(-1, 132, (LocaleController.isRTL ? 5 : 3) | 48, -8, -8, 0, 0));
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
            c07341.addView(view, LayoutHelper.createLinear(-2, -2, i2, 24, 19, 24, i6));
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
            c07341.addView(view, LayoutHelper.createLinear(-2, -2, i2, 24, 0, 24, i));
        }
        if (r0.progressViewStyle == 0) {
            r0.shadow[0] = (BitmapDrawable) getContext().getResources().getDrawable(C0446R.drawable.header_shadow).mutate();
            r0.shadow[1] = (BitmapDrawable) getContext().getResources().getDrawable(C0446R.drawable.header_shadow_reverse).mutate();
            r0.shadow[0].setAlpha(0);
            r0.shadow[1].setAlpha(0);
            r0.shadow[0].setCallback(r0);
            r0.shadow[1].setCallback(r0);
            r0.contentScrollView = /* anonymous class already generated */;
            r0.contentScrollView.setVerticalScrollBarEnabled(false);
            AndroidUtilities.setScrollViewEdgeEffectColor(r0.contentScrollView, getThemeColor(Theme.key_dialogScrollGlow));
            c07341.addView(r0.contentScrollView, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 0.0f));
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
            c07341.addView(r0.progressViewContainer, LayoutHelper.createLinear(-1, 44, 51, 23, r0.title != null ? 24 : 0, 23, 24));
            view = new RadialProgressView(getContext());
            view.setProgressColor(getThemeColor(Theme.key_dialogProgressCircle));
            if (LocaleController.isRTL) {
            }
            r0.progressViewContainer.addView(view, LayoutHelper.createFrame(44, 44, (LocaleController.isRTL ? 5 : 3) | 48));
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
            c07341.addView(r0.messageTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 24, r0.title != null ? 19 : 0, 24, 20));
            r0.lineProgressView = new LineProgressView(getContext());
            r0.lineProgressView.setProgress(((float) r0.currentProgress) / 100.0f, false);
            r0.lineProgressView.setProgressColor(getThemeColor(Theme.key_dialogLineProgress));
            r0.lineProgressView.setBackColor(getThemeColor(Theme.key_dialogLineProgressBackground));
            c07341.addView(r0.lineProgressView, LayoutHelper.createLinear(-1, 4, 19, 24, 0, 24, 0));
            r0.lineProgressViewPercent = new TextView(getContext());
            r0.lineProgressViewPercent.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            if (LocaleController.isRTL) {
            }
            r0.lineProgressViewPercent.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            r0.lineProgressViewPercent.setTextColor(getThemeColor(Theme.key_dialogTextGray2));
            r0.lineProgressViewPercent.setTextSize(1, 14.0f);
            if (LocaleController.isRTL) {
            }
            c07341.addView(r0.lineProgressViewPercent, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 23, 4, 23, 24));
            updateLineProgressTextView();
        }
        if (TextUtils.isEmpty(r0.message)) {
            r0.messageTextView.setVisibility(8);
        } else {
            r0.messageTextView.setText(r0.message);
            r0.messageTextView.setVisibility(0);
        }
        if (r0.items != null) {
            i5 = 0;
            while (i5 < r0.items.length) {
                if (r0.items[i5] == null) {
                    view2 = new AlertDialogCell(getContext());
                    if (r0.itemIcons == null) {
                    }
                    view2.setTextAndIcon(r0.items[i5], r0.itemIcons == null ? r0.itemIcons[i5] : 0);
                    r0.scrollContainer.addView(view2, LayoutHelper.createLinear(-1, 48));
                    view2.setTag(Integer.valueOf(i5));
                    view2.setOnClickListener(new C07363());
                }
                i5++;
            }
        }
        if (r0.customView != null) {
            if (r0.customView.getParent() != null) {
                ((ViewGroup) r0.customView.getParent()).removeView(r0.customView);
            }
            r0.scrollContainer.addView(r0.customView, LayoutHelper.createLinear(-1, -2));
        }
        if (z) {
            r0.buttonsLayout = /* anonymous class already generated */;
            r0.buttonsLayout.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            c07341.addView(r0.buttonsLayout, LayoutHelper.createLinear(-1, 52));
            if (r0.positiveButtonText != null) {
                c07341 = /* anonymous class already generated */;
                c07341.setMinWidth(AndroidUtilities.dp(64.0f));
                c07341.setTag(Integer.valueOf(-1));
                c07341.setTextSize(1, 14.0f);
                c07341.setTextColor(getThemeColor(Theme.key_dialogButton));
                c07341.setGravity(17);
                c07341.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                c07341.setText(r0.positiveButtonText.toString().toUpperCase());
                c07341.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
                c07341.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                r0.buttonsLayout.addView(c07341, LayoutHelper.createFrame(-2, 36, 53));
                c07341.setOnClickListener(new C07396());
            }
            if (r0.negativeButtonText != null) {
                c07341 = /* anonymous class already generated */;
                c07341.setMinWidth(AndroidUtilities.dp(64.0f));
                c07341.setTag(Integer.valueOf(-2));
                c07341.setTextSize(1, 14.0f);
                c07341.setTextColor(getThemeColor(Theme.key_dialogButton));
                c07341.setGravity(17);
                c07341.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                c07341.setText(r0.negativeButtonText.toString().toUpperCase());
                c07341.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
                c07341.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                r0.buttonsLayout.addView(c07341, LayoutHelper.createFrame(-2, 36, 53));
                c07341.setOnClickListener(new C07418());
            }
            if (r0.neutralButtonText != null) {
                c07341 = /* anonymous class already generated */;
                c07341.setMinWidth(AndroidUtilities.dp(64.0f));
                c07341.setTag(Integer.valueOf(-3));
                c07341.setTextSize(1, 14.0f);
                c07341.setTextColor(getThemeColor(Theme.key_dialogButton));
                c07341.setGravity(17);
                c07341.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                c07341.setText(r0.neutralButtonText.toString().toUpperCase());
                c07341.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
                c07341.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                r0.buttonsLayout.addView(c07341, LayoutHelper.createFrame(-2, 36, 51));
                c07341.setOnClickListener(/* anonymous class already generated */);
            }
        }
        r0.lastScreenWidth = AndroidUtilities.displaySize.x;
        dp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(48.0f);
        if (AndroidUtilities.isTablet()) {
            dp2 = AndroidUtilities.dp(356.0f);
        } else if (AndroidUtilities.isSmallTablet()) {
            dp2 = AndroidUtilities.dp(496.0f);
        } else {
            dp2 = AndroidUtilities.dp(446.0f);
        }
        window = getWindow();
        layoutParams = new LayoutParams();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.dimAmount = 0.6f;
        layoutParams.width = (Math.min(dp2, dp) + r0.backgroundPaddings.left) + r0.backgroundPaddings.right;
        layoutParams.flags |= 2;
        layoutParams.flags |= 131072;
        window.setAttributes(layoutParams);
    }

    public void onBackPressed() {
        super.onBackPressed();
        if (this.onBackButtonListener != null) {
            this.onBackButtonListener.onClick(this, -2);
        }
    }

    private void runShadowAnimation(final int i, boolean z) {
        if ((z && !this.shadowVisibility[i]) || (!z && this.shadowVisibility[i])) {
            this.shadowVisibility[i] = z;
            if (this.shadowAnimation[i] != null) {
                this.shadowAnimation[i].cancel();
            }
            this.shadowAnimation[i] = new AnimatorSet();
            if (this.shadow[i] != null) {
                AnimatorSet animatorSet = this.shadowAnimation[i];
                Animator[] animatorArr = new Animator[1];
                Object obj = this.shadow[i];
                String str = "alpha";
                int[] iArr = new int[1];
                iArr[0] = z ? true : false;
                animatorArr[0] = ObjectAnimator.ofInt(obj, str, iArr);
                animatorSet.playTogether(animatorArr);
            }
            this.shadowAnimation[i].setDuration(150);
            this.shadowAnimation[i].addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (AlertDialog.this.shadowAnimation[i] != null && AlertDialog.this.shadowAnimation[i].equals(animator) != null) {
                        AlertDialog.this.shadowAnimation[i] = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (AlertDialog.this.shadowAnimation[i] != null && AlertDialog.this.shadowAnimation[i].equals(animator) != null) {
                        AlertDialog.this.shadowAnimation[i] = null;
                    }
                }
            });
            try {
                this.shadowAnimation[i].start();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    public void setProgressStyle(int i) {
        this.progressViewStyle = i;
    }

    public void setProgress(int i) {
        this.currentProgress = i;
        if (this.lineProgressView != null) {
            this.lineProgressView.setProgress(((float) i) / NUM, true);
            updateLineProgressTextView();
        }
    }

    private void updateLineProgressTextView() {
        this.lineProgressViewPercent.setText(String.format("%d%%", new Object[]{Integer.valueOf(this.currentProgress)}));
    }

    private boolean canTextInput(View view) {
        if (view.onCheckIsTextEditor()) {
            return true;
        }
        if (!(view instanceof ViewGroup)) {
            return false;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        int childCount = viewGroup.getChildCount();
        while (childCount > 0) {
            childCount--;
            if (canTextInput(viewGroup.getChildAt(childCount))) {
                return true;
            }
        }
        return false;
    }

    public void dismiss() {
        super.dismiss();
    }

    public void setCanceledOnTouchOutside(boolean z) {
        super.setCanceledOnTouchOutside(z);
    }

    public void setMessage(CharSequence charSequence) {
        this.message = charSequence;
        if (this.messageTextView == null) {
            return;
        }
        if (TextUtils.isEmpty(this.message) == null) {
            this.messageTextView.setText(this.message);
            this.messageTextView.setVisibility(0);
            return;
        }
        this.messageTextView.setVisibility(8);
    }

    public void setButton(int i, CharSequence charSequence, OnClickListener onClickListener) {
        switch (i) {
            case -3:
                this.neutralButtonText = charSequence;
                this.neutralButtonListener = onClickListener;
                return;
            case -2:
                this.negativeButtonText = charSequence;
                this.negativeButtonListener = onClickListener;
                return;
            case -1:
                this.positiveButtonText = charSequence;
                this.positiveButtonListener = onClickListener;
                return;
            default:
                return;
        }
    }

    public View getButton(int i) {
        return this.buttonsLayout.findViewWithTag(Integer.valueOf(i));
    }

    public void invalidateDrawable(Drawable drawable) {
        this.contentScrollView.invalidate();
        this.scrollContainer.invalidate();
    }

    public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
        if (this.contentScrollView != null) {
            this.contentScrollView.postDelayed(runnable, j);
        }
    }

    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        if (this.contentScrollView != null) {
            this.contentScrollView.removeCallbacks(runnable);
        }
    }

    protected int getThemeColor(String str) {
        return Theme.getColor(str);
    }
}

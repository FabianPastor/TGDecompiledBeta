package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LineProgressView;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.spoilers.SpoilersTextView;

public class AlertDialog extends Dialog implements Drawable.Callback {
    /* access modifiers changed from: private */
    public float aspectRatio;
    /* access modifiers changed from: private */
    public Rect backgroundPaddings;
    protected ViewGroup buttonsLayout;
    private boolean canCacnel;
    private AlertDialog cancelDialog;
    /* access modifiers changed from: private */
    public boolean checkFocusable;
    /* access modifiers changed from: private */
    public ScrollView contentScrollView;
    private int currentProgress;
    /* access modifiers changed from: private */
    public View customView;
    /* access modifiers changed from: private */
    public int customViewHeight;
    /* access modifiers changed from: private */
    public int customViewOffset;
    /* access modifiers changed from: private */
    public String dialogButtonColorKey;
    /* access modifiers changed from: private */
    public float dimAlpha;
    /* access modifiers changed from: private */
    public boolean dimEnabled;
    private boolean dismissDialogByButtons;
    /* access modifiers changed from: private */
    public Runnable dismissRunnable;
    /* access modifiers changed from: private */
    public boolean drawBackground;
    private boolean focusable;
    /* access modifiers changed from: private */
    public int[] itemIcons;
    private ArrayList<AlertDialogCell> itemViews;
    /* access modifiers changed from: private */
    public CharSequence[] items;
    /* access modifiers changed from: private */
    public int lastScreenWidth;
    /* access modifiers changed from: private */
    public LineProgressView lineProgressView;
    /* access modifiers changed from: private */
    public TextView lineProgressViewPercent;
    /* access modifiers changed from: private */
    public CharSequence message;
    /* access modifiers changed from: private */
    public TextView messageTextView;
    /* access modifiers changed from: private */
    public boolean messageTextViewClickable;
    /* access modifiers changed from: private */
    public DialogInterface.OnClickListener negativeButtonListener;
    /* access modifiers changed from: private */
    public CharSequence negativeButtonText;
    /* access modifiers changed from: private */
    public DialogInterface.OnClickListener neutralButtonListener;
    /* access modifiers changed from: private */
    public CharSequence neutralButtonText;
    /* access modifiers changed from: private */
    public boolean notDrawBackgroundOnTopView;
    /* access modifiers changed from: private */
    public DialogInterface.OnClickListener onBackButtonListener;
    private DialogInterface.OnCancelListener onCancelListener;
    /* access modifiers changed from: private */
    public DialogInterface.OnClickListener onClickListener;
    /* access modifiers changed from: private */
    public DialogInterface.OnDismissListener onDismissListener;
    /* access modifiers changed from: private */
    public ViewTreeObserver.OnScrollChangedListener onScrollChangedListener;
    /* access modifiers changed from: private */
    public DialogInterface.OnClickListener positiveButtonListener;
    /* access modifiers changed from: private */
    public CharSequence positiveButtonText;
    /* access modifiers changed from: private */
    public FrameLayout progressViewContainer;
    /* access modifiers changed from: private */
    public int progressViewStyle;
    private TextView progressViewTextView;
    private final Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public LinearLayout scrollContainer;
    private CharSequence secondTitle;
    /* access modifiers changed from: private */
    public TextView secondTitleTextView;
    /* access modifiers changed from: private */
    public BitmapDrawable[] shadow;
    /* access modifiers changed from: private */
    public AnimatorSet[] shadowAnimation;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;
    private boolean[] shadowVisibility;
    private Runnable showRunnable;
    /* access modifiers changed from: private */
    public CharSequence subtitle;
    /* access modifiers changed from: private */
    public TextView subtitleTextView;
    /* access modifiers changed from: private */
    public CharSequence title;
    /* access modifiers changed from: private */
    public FrameLayout titleContainer;
    /* access modifiers changed from: private */
    public TextView titleTextView;
    /* access modifiers changed from: private */
    public boolean topAnimationAutoRepeat;
    /* access modifiers changed from: private */
    public int topAnimationId;
    /* access modifiers changed from: private */
    public int topAnimationSize;
    /* access modifiers changed from: private */
    public int topBackgroundColor;
    /* access modifiers changed from: private */
    public Drawable topDrawable;
    /* access modifiers changed from: private */
    public int topHeight;
    /* access modifiers changed from: private */
    public RLottieImageView topImageView;
    /* access modifiers changed from: private */
    public int topResId;
    /* access modifiers changed from: private */
    public View topView;
    /* access modifiers changed from: private */
    public boolean verticalButtons;

    /* renamed from: lambda$new$0$org-telegram-ui-ActionBar-AlertDialog  reason: not valid java name */
    public /* synthetic */ void m1264lambda$new$0$orgtelegramuiActionBarAlertDialog() {
        if (!isShowing()) {
            try {
                show();
            } catch (Exception e) {
            }
        }
    }

    public static class AlertDialogCell extends FrameLayout {
        /* access modifiers changed from: private */
        public ImageView imageView;
        private final Theme.ResourcesProvider resourcesProvider;
        /* access modifiers changed from: private */
        public TextView textView;

        public AlertDialogCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
            super(context);
            this.resourcesProvider = resourcesProvider2;
            setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor("dialogButtonSelector"), 2));
            setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogIcon"), PorterDuff.Mode.MULTIPLY));
            int i = 5;
            addView(this.imageView, LayoutHelper.createFrame(-2, 40, (LocaleController.isRTL ? 5 : 3) | 16));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setTextColor(getThemedColor("dialogTextBlack"));
            this.textView.setTextSize(1, 16.0f);
            addView(this.textView, LayoutHelper.createFrame(-2, -2, (!LocaleController.isRTL ? 3 : i) | 16));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
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

        private int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }
    }

    public AlertDialog(Context context, int progressStyle) {
        this(context, progressStyle, (Theme.ResourcesProvider) null);
    }

    public AlertDialog(Context context, int progressStyle, Theme.ResourcesProvider resourcesProvider2) {
        super(context, NUM);
        this.customViewHeight = -2;
        this.shadow = new BitmapDrawable[2];
        this.shadowVisibility = new boolean[2];
        this.shadowAnimation = new AnimatorSet[2];
        this.customViewOffset = 20;
        this.dialogButtonColorKey = "dialogButton";
        this.topHeight = 132;
        this.messageTextViewClickable = true;
        this.canCacnel = true;
        this.dismissDialogByButtons = true;
        this.checkFocusable = true;
        this.dismissRunnable = new AlertDialog$$ExternalSyntheticLambda6(this);
        this.showRunnable = new AlertDialog$$ExternalSyntheticLambda7(this);
        this.itemViews = new ArrayList<>();
        this.dimEnabled = true;
        this.dimAlpha = 0.6f;
        this.topAnimationAutoRepeat = true;
        this.resourcesProvider = resourcesProvider2;
        this.backgroundPaddings = new Rect();
        if (progressStyle != 3) {
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            this.shadowDrawable = mutate;
            mutate.setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
            this.shadowDrawable.getPadding(this.backgroundPaddings);
        }
        this.progressViewStyle = progressStyle;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        int maxWidth;
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

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int i;
                float f;
                int h;
                int i2 = heightMeasureSpec;
                if (AlertDialog.this.progressViewStyle == 3) {
                    AlertDialog.this.progressViewContainer.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(86.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(86.0f), NUM));
                    setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
                    return;
                }
                this.inLayout = true;
                int width = View.MeasureSpec.getSize(widthMeasureSpec);
                int availableHeight = (View.MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop()) - getPaddingBottom();
                int maxContentHeight = availableHeight;
                int availableWidth = (width - getPaddingLeft()) - getPaddingRight();
                int childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(availableWidth - AndroidUtilities.dp(48.0f), NUM);
                int childFullWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(availableWidth, NUM);
                if (AlertDialog.this.buttonsLayout != null) {
                    int count = AlertDialog.this.buttonsLayout.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = AlertDialog.this.buttonsLayout.getChildAt(a);
                        if (child instanceof TextView) {
                            ((TextView) child).setMaxWidth(AndroidUtilities.dp((float) ((availableWidth - AndroidUtilities.dp(24.0f)) / 2)));
                        }
                    }
                    AlertDialog.this.buttonsLayout.measure(childFullWidthMeasureSpec, i2);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) AlertDialog.this.buttonsLayout.getLayoutParams();
                    availableHeight -= (AlertDialog.this.buttonsLayout.getMeasuredHeight() + layoutParams.bottomMargin) + layoutParams.topMargin;
                }
                if (AlertDialog.this.secondTitleTextView != null) {
                    AlertDialog.this.secondTitleTextView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(childWidthMeasureSpec), Integer.MIN_VALUE), i2);
                }
                if (AlertDialog.this.titleTextView != null) {
                    if (AlertDialog.this.secondTitleTextView != null) {
                        AlertDialog.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec((View.MeasureSpec.getSize(childWidthMeasureSpec) - AlertDialog.this.secondTitleTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f), NUM), i2);
                    } else {
                        AlertDialog.this.titleTextView.measure(childWidthMeasureSpec, i2);
                    }
                }
                if (AlertDialog.this.titleContainer != null) {
                    AlertDialog.this.titleContainer.measure(childWidthMeasureSpec, i2);
                    LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) AlertDialog.this.titleContainer.getLayoutParams();
                    availableHeight -= (AlertDialog.this.titleContainer.getMeasuredHeight() + layoutParams2.bottomMargin) + layoutParams2.topMargin;
                }
                if (AlertDialog.this.subtitleTextView != null) {
                    AlertDialog.this.subtitleTextView.measure(childWidthMeasureSpec, i2);
                    LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) AlertDialog.this.subtitleTextView.getLayoutParams();
                    availableHeight -= (AlertDialog.this.subtitleTextView.getMeasuredHeight() + layoutParams3.bottomMargin) + layoutParams3.topMargin;
                }
                if (AlertDialog.this.topImageView != null) {
                    AlertDialog.this.topImageView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) AlertDialog.this.topHeight), NUM));
                    availableHeight -= AlertDialog.this.topImageView.getMeasuredHeight() - AndroidUtilities.dp(8.0f);
                }
                if (AlertDialog.this.topView != null) {
                    int w = width - AndroidUtilities.dp(16.0f);
                    if (AlertDialog.this.aspectRatio == 0.0f) {
                        h = (int) (354.0f * (((float) w) / 936.0f));
                    } else {
                        h = (int) (((float) w) * AlertDialog.this.aspectRatio);
                    }
                    AlertDialog.this.topView.measure(View.MeasureSpec.makeMeasureSpec(w, NUM), View.MeasureSpec.makeMeasureSpec(h, NUM));
                    AlertDialog.this.topView.getLayoutParams().height = h;
                    availableHeight -= AlertDialog.this.topView.getMeasuredHeight();
                }
                if (AlertDialog.this.progressViewStyle == 0) {
                    LinearLayout.LayoutParams layoutParams4 = (LinearLayout.LayoutParams) AlertDialog.this.contentScrollView.getLayoutParams();
                    if (AlertDialog.this.customView != null) {
                        layoutParams4.topMargin = (AlertDialog.this.titleTextView == null && AlertDialog.this.messageTextView.getVisibility() == 8 && AlertDialog.this.items == null) ? AndroidUtilities.dp(16.0f) : 0;
                        layoutParams4.bottomMargin = AlertDialog.this.buttonsLayout == null ? AndroidUtilities.dp(8.0f) : 0;
                    } else if (AlertDialog.this.items != null) {
                        if (AlertDialog.this.titleTextView == null && AlertDialog.this.messageTextView.getVisibility() == 8) {
                            f = 8.0f;
                            i = AndroidUtilities.dp(8.0f);
                        } else {
                            f = 8.0f;
                            i = 0;
                        }
                        layoutParams4.topMargin = i;
                        layoutParams4.bottomMargin = AndroidUtilities.dp(f);
                    } else if (AlertDialog.this.messageTextView.getVisibility() == 0) {
                        layoutParams4.topMargin = AlertDialog.this.titleTextView == null ? AndroidUtilities.dp(19.0f) : 0;
                        layoutParams4.bottomMargin = AndroidUtilities.dp(20.0f);
                    }
                    int availableHeight2 = availableHeight - (layoutParams4.bottomMargin + layoutParams4.topMargin);
                    AlertDialog.this.contentScrollView.measure(childFullWidthMeasureSpec, View.MeasureSpec.makeMeasureSpec(availableHeight2, Integer.MIN_VALUE));
                    availableHeight = availableHeight2 - AlertDialog.this.contentScrollView.getMeasuredHeight();
                } else {
                    if (AlertDialog.this.progressViewContainer != null) {
                        AlertDialog.this.progressViewContainer.measure(childWidthMeasureSpec, View.MeasureSpec.makeMeasureSpec(availableHeight, Integer.MIN_VALUE));
                        LinearLayout.LayoutParams layoutParams5 = (LinearLayout.LayoutParams) AlertDialog.this.progressViewContainer.getLayoutParams();
                        availableHeight -= (AlertDialog.this.progressViewContainer.getMeasuredHeight() + layoutParams5.bottomMargin) + layoutParams5.topMargin;
                    } else if (AlertDialog.this.messageTextView != null) {
                        AlertDialog.this.messageTextView.measure(childWidthMeasureSpec, View.MeasureSpec.makeMeasureSpec(availableHeight, Integer.MIN_VALUE));
                        if (AlertDialog.this.messageTextView.getVisibility() != 8) {
                            LinearLayout.LayoutParams layoutParams6 = (LinearLayout.LayoutParams) AlertDialog.this.messageTextView.getLayoutParams();
                            availableHeight -= (AlertDialog.this.messageTextView.getMeasuredHeight() + layoutParams6.bottomMargin) + layoutParams6.topMargin;
                        }
                    }
                    if (AlertDialog.this.lineProgressView != null) {
                        AlertDialog.this.lineProgressView.measure(childWidthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(4.0f), NUM));
                        LinearLayout.LayoutParams layoutParams7 = (LinearLayout.LayoutParams) AlertDialog.this.lineProgressView.getLayoutParams();
                        int availableHeight3 = availableHeight - ((AlertDialog.this.lineProgressView.getMeasuredHeight() + layoutParams7.bottomMargin) + layoutParams7.topMargin);
                        AlertDialog.this.lineProgressViewPercent.measure(childWidthMeasureSpec, View.MeasureSpec.makeMeasureSpec(availableHeight3, Integer.MIN_VALUE));
                        LinearLayout.LayoutParams layoutParams8 = (LinearLayout.LayoutParams) AlertDialog.this.lineProgressViewPercent.getLayoutParams();
                        availableHeight = availableHeight3 - ((AlertDialog.this.lineProgressViewPercent.getMeasuredHeight() + layoutParams8.bottomMargin) + layoutParams8.topMargin);
                    }
                }
                setMeasuredDimension(width, (maxContentHeight - availableHeight) + getPaddingTop() + getPaddingBottom());
                this.inLayout = false;
                if (AlertDialog.this.lastScreenWidth != AndroidUtilities.displaySize.x) {
                    AndroidUtilities.runOnUIThread(new AlertDialog$1$$ExternalSyntheticLambda1(this));
                }
            }

            /* renamed from: lambda$onMeasure$0$org-telegram-ui-ActionBar-AlertDialog$1  reason: not valid java name */
            public /* synthetic */ void m1272lambda$onMeasure$0$orgtelegramuiActionBarAlertDialog$1() {
                int maxWidth;
                int unused = AlertDialog.this.lastScreenWidth = AndroidUtilities.displaySize.x;
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
                params.width = Math.min(maxWidth, calculatedWidth) + AlertDialog.this.backgroundPaddings.left + AlertDialog.this.backgroundPaddings.right;
                try {
                    window.setAttributes(params);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                if (AlertDialog.this.progressViewStyle == 3) {
                    int x = ((r - l) - AlertDialog.this.progressViewContainer.getMeasuredWidth()) / 2;
                    int y = ((b - t) - AlertDialog.this.progressViewContainer.getMeasuredHeight()) / 2;
                    AlertDialog.this.progressViewContainer.layout(x, y, AlertDialog.this.progressViewContainer.getMeasuredWidth() + x, AlertDialog.this.progressViewContainer.getMeasuredHeight() + y);
                } else if (AlertDialog.this.contentScrollView != null) {
                    if (AlertDialog.this.onScrollChangedListener == null) {
                        ViewTreeObserver.OnScrollChangedListener unused = AlertDialog.this.onScrollChangedListener = new AlertDialog$1$$ExternalSyntheticLambda0(this);
                        AlertDialog.this.contentScrollView.getViewTreeObserver().addOnScrollChangedListener(AlertDialog.this.onScrollChangedListener);
                    }
                    AlertDialog.this.onScrollChangedListener.onScrollChanged();
                }
            }

            /* renamed from: lambda$onLayout$1$org-telegram-ui-ActionBar-AlertDialog$1  reason: not valid java name */
            public /* synthetic */ void m1271lambda$onLayout$1$orgtelegramuiActionBarAlertDialog$1() {
                AlertDialog alertDialog = AlertDialog.this;
                boolean z = false;
                alertDialog.runShadowAnimation(0, alertDialog.titleTextView != null && AlertDialog.this.contentScrollView.getScrollY() > AlertDialog.this.scrollContainer.getTop());
                AlertDialog alertDialog2 = AlertDialog.this;
                if (alertDialog2.buttonsLayout != null && AlertDialog.this.contentScrollView.getScrollY() + AlertDialog.this.contentScrollView.getHeight() < AlertDialog.this.scrollContainer.getBottom()) {
                    z = true;
                }
                alertDialog2.runShadowAnimation(1, z);
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

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                if (AlertDialog.this.drawBackground) {
                    AlertDialog.this.shadowDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    if (AlertDialog.this.topView == null || !AlertDialog.this.notDrawBackgroundOnTopView) {
                        AlertDialog.this.shadowDrawable.draw(canvas);
                    } else {
                        int clipTop = AlertDialog.this.topView.getBottom();
                        canvas.save();
                        canvas.clipRect(0, clipTop, getMeasuredWidth(), getMeasuredHeight());
                        AlertDialog.this.shadowDrawable.draw(canvas);
                        canvas.restore();
                    }
                }
                super.dispatchDraw(canvas);
            }
        };
        containerView.setOrientation(1);
        if (this.progressViewStyle == 3) {
            containerView.setBackgroundDrawable((Drawable) null);
            containerView.setPadding(0, 0, 0, 0);
            this.drawBackground = false;
        } else if (this.notDrawBackgroundOnTopView) {
            Rect rect = new Rect();
            this.shadowDrawable.getPadding(rect);
            containerView.setPadding(rect.left, rect.top, rect.right, rect.bottom);
            this.drawBackground = true;
        } else {
            containerView.setBackgroundDrawable((Drawable) null);
            containerView.setPadding(0, 0, 0, 0);
            containerView.setBackgroundDrawable(this.shadowDrawable);
            this.drawBackground = false;
        }
        containerView.setFitsSystemWindows(Build.VERSION.SDK_INT >= 21);
        setContentView(containerView);
        boolean hasButtons = (this.positiveButtonText == null && this.negativeButtonText == null && this.neutralButtonText == null) ? false : true;
        if (this.topResId == 0 && this.topAnimationId == 0 && this.topDrawable == null) {
            View view = this.topView;
            if (view != null) {
                view.setPadding(0, 0, 0, 0);
                containerView.addView(this.topView, LayoutHelper.createLinear(-1, this.topHeight, 51, 0, 0, 0, 0));
            }
        } else {
            RLottieImageView rLottieImageView = new RLottieImageView(getContext());
            this.topImageView = rLottieImageView;
            Drawable drawable = this.topDrawable;
            if (drawable != null) {
                rLottieImageView.setImageDrawable(drawable);
            } else {
                int i = this.topResId;
                if (i != 0) {
                    rLottieImageView.setImageResource(i);
                } else {
                    rLottieImageView.setAutoRepeat(this.topAnimationAutoRepeat);
                    RLottieImageView rLottieImageView2 = this.topImageView;
                    int i2 = this.topAnimationId;
                    int i3 = this.topAnimationSize;
                    rLottieImageView2.setAnimation(i2, i3, i3);
                    this.topImageView.playAnimation();
                }
            }
            this.topImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.topImageView.setBackgroundDrawable(getContext().getResources().getDrawable(NUM));
            this.topImageView.getBackground().setColorFilter(new PorterDuffColorFilter(this.topBackgroundColor, PorterDuff.Mode.MULTIPLY));
            this.topImageView.setPadding(0, 0, 0, 0);
            containerView.addView(this.topImageView, LayoutHelper.createLinear(-1, this.topHeight, 51, -8, -8, 0, 0));
        }
        if (this.title != null) {
            FrameLayout frameLayout = new FrameLayout(getContext());
            this.titleContainer = frameLayout;
            containerView.addView(frameLayout, LayoutHelper.createLinear(-2, -2, 24.0f, 0.0f, 24.0f, 0.0f));
            TextView textView = new TextView(getContext());
            this.titleTextView = textView;
            textView.setText(this.title);
            this.titleTextView.setTextColor(getThemedColor("dialogTextBlack"));
            this.titleTextView.setTextSize(1, 20.0f);
            this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.titleContainer.addView(this.titleTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 0.0f, 19.0f, 0.0f, (float) (this.subtitle != null ? 2 : this.items != null ? 14 : 10)));
        }
        if (!(this.secondTitle == null || this.title == null)) {
            TextView textView2 = new TextView(getContext());
            this.secondTitleTextView = textView2;
            textView2.setText(this.secondTitle);
            this.secondTitleTextView.setTextColor(getThemedColor("dialogTextGray3"));
            this.secondTitleTextView.setTextSize(1, 18.0f);
            this.secondTitleTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
            this.titleContainer.addView(this.secondTitleTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 3 : 5) | 48, 0.0f, 21.0f, 0.0f, 0.0f));
        }
        if (this.subtitle != null) {
            TextView textView3 = new TextView(getContext());
            this.subtitleTextView = textView3;
            textView3.setText(this.subtitle);
            this.subtitleTextView.setTextColor(getThemedColor("dialogIcon"));
            this.subtitleTextView.setTextSize(1, 14.0f);
            this.subtitleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            containerView.addView(this.subtitleTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 24, 0, 24, this.items != null ? 14 : 10));
        }
        if (this.progressViewStyle == 0) {
            this.shadow[0] = (BitmapDrawable) getContext().getResources().getDrawable(NUM).mutate();
            this.shadow[1] = (BitmapDrawable) getContext().getResources().getDrawable(NUM).mutate();
            this.shadow[0].setAlpha(0);
            this.shadow[1].setAlpha(0);
            this.shadow[0].setCallback(this);
            this.shadow[1].setCallback(this);
            AnonymousClass2 r4 = new ScrollView(getContext()) {
                /* access modifiers changed from: protected */
                public boolean drawChild(Canvas canvas, View child, long drawingTime) {
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
            this.contentScrollView = r4;
            r4.setVerticalScrollBarEnabled(false);
            AndroidUtilities.setScrollViewEdgeEffectColor(this.contentScrollView, getThemedColor("dialogScrollGlow"));
            containerView.addView(this.contentScrollView, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 0.0f));
            LinearLayout linearLayout = new LinearLayout(getContext());
            this.scrollContainer = linearLayout;
            linearLayout.setOrientation(1);
            this.contentScrollView.addView(this.scrollContainer, new FrameLayout.LayoutParams(-1, -2));
        }
        SpoilersTextView spoilersTextView = new SpoilersTextView(getContext());
        this.messageTextView = spoilersTextView;
        spoilersTextView.setTextColor(getThemedColor("dialogTextBlack"));
        this.messageTextView.setTextSize(1, 16.0f);
        this.messageTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        this.messageTextView.setLinkTextColor(getThemedColor("dialogTextLink"));
        if (!this.messageTextViewClickable) {
            this.messageTextView.setClickable(false);
            this.messageTextView.setEnabled(false);
        }
        this.messageTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        int i4 = this.progressViewStyle;
        if (i4 == 1) {
            FrameLayout frameLayout2 = new FrameLayout(getContext());
            this.progressViewContainer = frameLayout2;
            containerView.addView(frameLayout2, LayoutHelper.createLinear(-1, 44, 51, 23, this.title == null ? 24 : 0, 23, 24));
            RadialProgressView progressView = new RadialProgressView(getContext(), this.resourcesProvider);
            progressView.setProgressColor(getThemedColor("dialogProgressCircle"));
            this.progressViewContainer.addView(progressView, LayoutHelper.createFrame(44, 44, (LocaleController.isRTL ? 5 : 3) | 48));
            this.messageTextView.setLines(1);
            this.messageTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.progressViewContainer.addView(this.messageTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, (float) (LocaleController.isRTL ? 0 : 62), 0.0f, (float) (LocaleController.isRTL ? 62 : 0), 0.0f));
        } else if (i4 == 2) {
            containerView.addView(this.messageTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 24, this.title == null ? 19 : 0, 24, 20));
            LineProgressView lineProgressView2 = new LineProgressView(getContext());
            this.lineProgressView = lineProgressView2;
            lineProgressView2.setProgress(((float) this.currentProgress) / 100.0f, false);
            this.lineProgressView.setProgressColor(getThemedColor("dialogLineProgress"));
            this.lineProgressView.setBackColor(getThemedColor("dialogLineProgressBackground"));
            containerView.addView(this.lineProgressView, LayoutHelper.createLinear(-1, 4, 19, 24, 0, 24, 0));
            TextView textView4 = new TextView(getContext());
            this.lineProgressViewPercent = textView4;
            textView4.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.lineProgressViewPercent.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.lineProgressViewPercent.setTextColor(getThemedColor("dialogTextGray2"));
            this.lineProgressViewPercent.setTextSize(1, 14.0f);
            containerView.addView(this.lineProgressViewPercent, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 23, 4, 23, 24));
            updateLineProgressTextView();
        } else if (i4 == 3) {
            setCanceledOnTouchOutside(false);
            setCancelable(false);
            FrameLayout frameLayout3 = new FrameLayout(getContext());
            this.progressViewContainer = frameLayout3;
            frameLayout3.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), getThemedColor("dialog_inlineProgressBackground")));
            containerView.addView(this.progressViewContainer, LayoutHelper.createLinear(86, 86, 17));
            RadialProgressView progressView2 = new RadialProgressView(getContext(), this.resourcesProvider);
            progressView2.setProgressColor(getThemedColor("dialog_inlineProgress"));
            this.progressViewContainer.addView(progressView2, LayoutHelper.createLinear(86, 86));
        } else {
            this.scrollContainer.addView(this.messageTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 24, 0, 24, (this.customView == null && this.items == null) ? 0 : this.customViewOffset));
        }
        if (!TextUtils.isEmpty(this.message)) {
            this.messageTextView.setText(this.message);
            this.messageTextView.setVisibility(0);
        } else {
            this.messageTextView.setVisibility(8);
        }
        if (this.items != null) {
            int a = 0;
            while (true) {
                CharSequence[] charSequenceArr = this.items;
                if (a >= charSequenceArr.length) {
                    break;
                }
                if (charSequenceArr[a] != null) {
                    AlertDialogCell cell = new AlertDialogCell(getContext(), this.resourcesProvider);
                    CharSequence charSequence = this.items[a];
                    int[] iArr = this.itemIcons;
                    cell.setTextAndIcon(charSequence, iArr != null ? iArr[a] : 0);
                    cell.setTag(Integer.valueOf(a));
                    this.itemViews.add(cell);
                    this.scrollContainer.addView(cell, LayoutHelper.createLinear(-1, 50));
                    cell.setOnClickListener(new AlertDialog$$ExternalSyntheticLambda2(this));
                }
                a++;
            }
        }
        View view2 = this.customView;
        if (view2 != null) {
            if (view2.getParent() != null) {
                ((ViewGroup) this.customView.getParent()).removeView(this.customView);
            }
            this.scrollContainer.addView(this.customView, LayoutHelper.createLinear(-1, this.customViewHeight));
        }
        if (hasButtons) {
            if (!this.verticalButtons) {
                int buttonsWidth = 0;
                TextPaint paint = new TextPaint();
                paint.setTextSize((float) AndroidUtilities.dp(14.0f));
                CharSequence charSequence2 = this.positiveButtonText;
                if (charSequence2 != null) {
                    buttonsWidth = (int) (((float) 0) + paint.measureText(charSequence2, 0, charSequence2.length()) + ((float) AndroidUtilities.dp(10.0f)));
                }
                CharSequence charSequence3 = this.negativeButtonText;
                if (charSequence3 != null) {
                    buttonsWidth = (int) (((float) buttonsWidth) + paint.measureText(charSequence3, 0, charSequence3.length()) + ((float) AndroidUtilities.dp(10.0f)));
                }
                CharSequence charSequence4 = this.neutralButtonText;
                if (charSequence4 != null) {
                    buttonsWidth = (int) (((float) buttonsWidth) + paint.measureText(charSequence4, 0, charSequence4.length()) + ((float) AndroidUtilities.dp(10.0f)));
                }
                if (buttonsWidth > AndroidUtilities.displaySize.x - AndroidUtilities.dp(110.0f)) {
                    this.verticalButtons = true;
                }
            }
            if (this.verticalButtons != 0) {
                LinearLayout linearLayout2 = new LinearLayout(getContext());
                linearLayout2.setOrientation(1);
                this.buttonsLayout = linearLayout2;
            } else {
                this.buttonsLayout = new FrameLayout(getContext()) {
                    /* access modifiers changed from: protected */
                    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                        int t;
                        int l;
                        int count = getChildCount();
                        View positiveButton = null;
                        int width = right - left;
                        for (int a = 0; a < count; a++) {
                            View child = getChildAt(a);
                            Integer tag = (Integer) child.getTag();
                            if (tag == null) {
                                int w = child.getMeasuredWidth();
                                int h = child.getMeasuredHeight();
                                if (positiveButton != null) {
                                    l = positiveButton.getLeft() + ((positiveButton.getMeasuredWidth() - w) / 2);
                                    t = positiveButton.getTop() + ((positiveButton.getMeasuredHeight() - h) / 2);
                                } else {
                                    l = 0;
                                    t = 0;
                                }
                                child.layout(l, t, l + w, t + h);
                            } else if (tag.intValue() == -1) {
                                positiveButton = child;
                                if (LocaleController.isRTL) {
                                    child.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + child.getMeasuredWidth(), getPaddingTop() + child.getMeasuredHeight());
                                } else {
                                    child.layout((width - getPaddingRight()) - child.getMeasuredWidth(), getPaddingTop(), width - getPaddingRight(), getPaddingTop() + child.getMeasuredHeight());
                                }
                            } else if (tag.intValue() == -2) {
                                if (LocaleController.isRTL) {
                                    int x = getPaddingLeft();
                                    if (positiveButton != null) {
                                        x += positiveButton.getMeasuredWidth() + AndroidUtilities.dp(8.0f);
                                    }
                                    child.layout(x, getPaddingTop(), child.getMeasuredWidth() + x, getPaddingTop() + child.getMeasuredHeight());
                                } else {
                                    int x2 = (width - getPaddingRight()) - child.getMeasuredWidth();
                                    if (positiveButton != null) {
                                        x2 -= positiveButton.getMeasuredWidth() + AndroidUtilities.dp(8.0f);
                                    }
                                    child.layout(x2, getPaddingTop(), child.getMeasuredWidth() + x2, getPaddingTop() + child.getMeasuredHeight());
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

                    /* access modifiers changed from: protected */
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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
                            View negative = findViewWithTag(-2);
                            View neuntral = findViewWithTag(-3);
                            if (negative != null && neuntral != null) {
                                if (negative.getMeasuredWidth() < neuntral.getMeasuredWidth()) {
                                    neuntral.measure(View.MeasureSpec.makeMeasureSpec(neuntral.getMeasuredWidth() - (totalWidth - availableWidth), NUM), View.MeasureSpec.makeMeasureSpec(neuntral.getMeasuredHeight(), NUM));
                                } else {
                                    negative.measure(View.MeasureSpec.makeMeasureSpec(negative.getMeasuredWidth() - (totalWidth - availableWidth), NUM), View.MeasureSpec.makeMeasureSpec(negative.getMeasuredHeight(), NUM));
                                }
                            }
                        }
                    }
                };
            }
            this.buttonsLayout.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            containerView.addView(this.buttonsLayout, LayoutHelper.createLinear(-1, 52));
            if (this.positiveButtonText != null) {
                TextView textView5 = new TextView(getContext()) {
                    public void setEnabled(boolean enabled) {
                        super.setEnabled(enabled);
                        setAlpha(enabled ? 1.0f : 0.5f);
                    }

                    public void setTextColor(int color) {
                        super.setTextColor(color);
                        setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(color));
                    }
                };
                textView5.setMinWidth(AndroidUtilities.dp(64.0f));
                textView5.setTag(-1);
                textView5.setTextSize(1, 14.0f);
                textView5.setTextColor(getThemedColor(this.dialogButtonColorKey));
                textView5.setGravity(17);
                textView5.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView5.setText(this.positiveButtonText.toString().toUpperCase());
                textView5.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(getThemedColor(this.dialogButtonColorKey)));
                textView5.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                if (this.verticalButtons) {
                    this.buttonsLayout.addView(textView5, LayoutHelper.createLinear(-2, 36, LocaleController.isRTL ? 3 : 5));
                } else {
                    this.buttonsLayout.addView(textView5, LayoutHelper.createFrame(-2, 36, 53));
                }
                textView5.setOnClickListener(new AlertDialog$$ExternalSyntheticLambda3(this));
            }
            if (this.negativeButtonText != null) {
                TextView textView6 = new TextView(getContext()) {
                    public void setEnabled(boolean enabled) {
                        super.setEnabled(enabled);
                        setAlpha(enabled ? 1.0f : 0.5f);
                    }

                    public void setTextColor(int color) {
                        super.setTextColor(color);
                        setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(color));
                    }
                };
                textView6.setMinWidth(AndroidUtilities.dp(64.0f));
                textView6.setTag(-2);
                textView6.setTextSize(1, 14.0f);
                textView6.setTextColor(getThemedColor(this.dialogButtonColorKey));
                textView6.setGravity(17);
                textView6.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView6.setEllipsize(TextUtils.TruncateAt.END);
                textView6.setSingleLine(true);
                textView6.setText(this.negativeButtonText.toString().toUpperCase());
                textView6.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(getThemedColor(this.dialogButtonColorKey)));
                textView6.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                if (this.verticalButtons) {
                    this.buttonsLayout.addView(textView6, 0, LayoutHelper.createLinear(-2, 36, LocaleController.isRTL ? 3 : 5));
                } else {
                    this.buttonsLayout.addView(textView6, LayoutHelper.createFrame(-2, 36, 53));
                }
                textView6.setOnClickListener(new AlertDialog$$ExternalSyntheticLambda4(this));
            }
            if (this.neutralButtonText != null) {
                TextView textView7 = new TextView(getContext()) {
                    public void setEnabled(boolean enabled) {
                        super.setEnabled(enabled);
                        setAlpha(enabled ? 1.0f : 0.5f);
                    }

                    public void setTextColor(int color) {
                        super.setTextColor(color);
                        setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(color));
                    }
                };
                textView7.setMinWidth(AndroidUtilities.dp(64.0f));
                textView7.setTag(-3);
                textView7.setTextSize(1, 14.0f);
                textView7.setTextColor(getThemedColor(this.dialogButtonColorKey));
                textView7.setGravity(17);
                textView7.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView7.setEllipsize(TextUtils.TruncateAt.END);
                textView7.setSingleLine(true);
                textView7.setText(this.neutralButtonText.toString().toUpperCase());
                textView7.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(getThemedColor(this.dialogButtonColorKey)));
                textView7.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                if (this.verticalButtons) {
                    this.buttonsLayout.addView(textView7, 1, LayoutHelper.createLinear(-2, 36, LocaleController.isRTL ? 3 : 5));
                } else {
                    this.buttonsLayout.addView(textView7, LayoutHelper.createFrame(-2, 36, 51));
                }
                textView7.setOnClickListener(new AlertDialog$$ExternalSyntheticLambda5(this));
            }
            if (this.verticalButtons) {
                for (int i5 = 1; i5 < this.buttonsLayout.getChildCount(); i5++) {
                    ((ViewGroup.MarginLayoutParams) this.buttonsLayout.getChildAt(i5).getLayoutParams()).topMargin = AndroidUtilities.dp(6.0f);
                }
            }
        }
        Window window = getWindow();
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(window.getAttributes());
        if (this.progressViewStyle == 3) {
            params.width = -1;
        } else {
            if (this.dimEnabled) {
                params.dimAmount = this.dimAlpha;
                params.flags |= 2;
            } else {
                params.dimAmount = 0.0f;
                params.flags ^= 2;
            }
            this.lastScreenWidth = AndroidUtilities.displaySize.x;
            int calculatedWidth = AndroidUtilities.displaySize.x - AndroidUtilities.dp(48.0f);
            if (!AndroidUtilities.isTablet()) {
                maxWidth = AndroidUtilities.dp(356.0f);
            } else if (AndroidUtilities.isSmallTablet()) {
                maxWidth = AndroidUtilities.dp(446.0f);
            } else {
                maxWidth = AndroidUtilities.dp(496.0f);
            }
            params.width = Math.min(maxWidth, calculatedWidth) + this.backgroundPaddings.left + this.backgroundPaddings.right;
        }
        View view3 = this.customView;
        if (view3 == null || !this.checkFocusable || !canTextInput(view3)) {
            params.flags |= 131072;
        } else {
            params.softInputMode = 4;
        }
        if (Build.VERSION.SDK_INT >= 28) {
            params.layoutInDisplayCutoutMode = 0;
        }
        window.setAttributes(params);
    }

    /* renamed from: lambda$onCreate$1$org-telegram-ui-ActionBar-AlertDialog  reason: not valid java name */
    public /* synthetic */ void m1265lambda$onCreate$1$orgtelegramuiActionBarAlertDialog(View v) {
        DialogInterface.OnClickListener onClickListener2 = this.onClickListener;
        if (onClickListener2 != null) {
            onClickListener2.onClick(this, ((Integer) v.getTag()).intValue());
        }
        dismiss();
    }

    /* renamed from: lambda$onCreate$2$org-telegram-ui-ActionBar-AlertDialog  reason: not valid java name */
    public /* synthetic */ void m1266lambda$onCreate$2$orgtelegramuiActionBarAlertDialog(View v) {
        DialogInterface.OnClickListener onClickListener2 = this.positiveButtonListener;
        if (onClickListener2 != null) {
            onClickListener2.onClick(this, -1);
        }
        if (this.dismissDialogByButtons) {
            dismiss();
        }
    }

    /* renamed from: lambda$onCreate$3$org-telegram-ui-ActionBar-AlertDialog  reason: not valid java name */
    public /* synthetic */ void m1267lambda$onCreate$3$orgtelegramuiActionBarAlertDialog(View v) {
        DialogInterface.OnClickListener onClickListener2 = this.negativeButtonListener;
        if (onClickListener2 != null) {
            onClickListener2.onClick(this, -2);
        }
        if (this.dismissDialogByButtons) {
            cancel();
        }
    }

    /* renamed from: lambda$onCreate$4$org-telegram-ui-ActionBar-AlertDialog  reason: not valid java name */
    public /* synthetic */ void m1268lambda$onCreate$4$orgtelegramuiActionBarAlertDialog(View v) {
        DialogInterface.OnClickListener onClickListener2 = this.neutralButtonListener;
        if (onClickListener2 != null) {
            onClickListener2.onClick(this, -2);
        }
        if (this.dismissDialogByButtons) {
            dismiss();
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        DialogInterface.OnClickListener onClickListener2 = this.onBackButtonListener;
        if (onClickListener2 != null) {
            onClickListener2.onClick(this, -2);
        }
    }

    public void setFocusable(boolean value) {
        if (this.focusable != value) {
            this.focusable = value;
            Window window = getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            if (this.focusable) {
                params.softInputMode = 16;
                params.flags &= -131073;
            } else {
                params.softInputMode = 48;
                params.flags |= 131072;
            }
            window.setAttributes(params);
        }
    }

    public void setBackgroundColor(int color) {
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
    }

    public void setTextColor(int color) {
        TextView textView = this.titleTextView;
        if (textView != null) {
            textView.setTextColor(color);
        }
        TextView textView2 = this.messageTextView;
        if (textView2 != null) {
            textView2.setTextColor(color);
        }
    }

    /* access modifiers changed from: private */
    public void showCancelAlert() {
        if (this.canCacnel && this.cancelDialog == null) {
            Builder builder = new Builder(getContext());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.getString("StopLoading", NUM));
            builder.setPositiveButton(LocaleController.getString("WaitMore", NUM), (DialogInterface.OnClickListener) null);
            builder.setNegativeButton(LocaleController.getString("Stop", NUM), new AlertDialog$$ExternalSyntheticLambda0(this));
            builder.setOnDismissListener(new AlertDialog$$ExternalSyntheticLambda1(this));
            try {
                this.cancelDialog = builder.show();
            } catch (Exception e) {
            }
        }
    }

    /* renamed from: lambda$showCancelAlert$5$org-telegram-ui-ActionBar-AlertDialog  reason: not valid java name */
    public /* synthetic */ void m1269lambda$showCancelAlert$5$orgtelegramuiActionBarAlertDialog(DialogInterface dialogInterface, int i) {
        DialogInterface.OnCancelListener onCancelListener2 = this.onCancelListener;
        if (onCancelListener2 != null) {
            onCancelListener2.onCancel(this);
        }
        dismiss();
    }

    /* renamed from: lambda$showCancelAlert$6$org-telegram-ui-ActionBar-AlertDialog  reason: not valid java name */
    public /* synthetic */ void m1270lambda$showCancelAlert$6$orgtelegramuiActionBarAlertDialog(DialogInterface dialog) {
        this.cancelDialog = null;
    }

    /* access modifiers changed from: private */
    public void runShadowAnimation(final int num, boolean show) {
        if ((show && !this.shadowVisibility[num]) || (!show && this.shadowVisibility[num])) {
            this.shadowVisibility[num] = show;
            AnimatorSet[] animatorSetArr = this.shadowAnimation;
            if (animatorSetArr[num] != null) {
                animatorSetArr[num].cancel();
            }
            this.shadowAnimation[num] = new AnimatorSet();
            BitmapDrawable[] bitmapDrawableArr = this.shadow;
            if (bitmapDrawableArr[num] != null) {
                AnimatorSet animatorSet = this.shadowAnimation[num];
                Animator[] animatorArr = new Animator[1];
                BitmapDrawable bitmapDrawable = bitmapDrawableArr[num];
                int[] iArr = new int[1];
                iArr[0] = show ? 255 : 0;
                animatorArr[0] = ObjectAnimator.ofInt(bitmapDrawable, "alpha", iArr);
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
            } catch (Exception e) {
                FileLog.e((Throwable) e);
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
        LineProgressView lineProgressView2 = this.lineProgressView;
        if (lineProgressView2 != null) {
            lineProgressView2.setProgress(((float) progress) / 100.0f, true);
            updateLineProgressTextView();
        }
    }

    private void updateLineProgressTextView() {
        this.lineProgressViewPercent.setText(String.format("%d%%", new Object[]{Integer.valueOf(this.currentProgress)}));
    }

    public void setCanCancel(boolean value) {
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
        DialogInterface.OnDismissListener onDismissListener2 = this.onDismissListener;
        if (onDismissListener2 != null) {
            onDismissListener2.onDismiss(this);
        }
        AlertDialog alertDialog = this.cancelDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        try {
            super.dismiss();
        } catch (Throwable th) {
        }
        AndroidUtilities.cancelRunOnUIThread(this.showRunnable);
    }

    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
    }

    public void setTopImage(int resId, int backgroundColor) {
        this.topResId = resId;
        this.topBackgroundColor = backgroundColor;
    }

    public void setTopAnimation(int resId, int backgroundColor) {
        setTopAnimation(resId, 94, backgroundColor);
    }

    public void setTopAnimation(int resId, int size, int backgroundColor) {
        this.topAnimationId = resId;
        this.topAnimationSize = size;
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
        TextView textView = this.titleTextView;
        if (textView != null) {
            textView.setText(text);
        }
    }

    public void setSecondTitle(CharSequence text) {
        this.secondTitle = text;
    }

    public void setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
        this.positiveButtonText = text;
        this.positiveButtonListener = listener;
    }

    public void setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
        this.negativeButtonText = text;
        this.negativeButtonListener = listener;
    }

    public void setNeutralButton(CharSequence text, DialogInterface.OnClickListener listener) {
        this.neutralButtonText = text;
        this.neutralButtonListener = listener;
    }

    public void setItemColor(int item, int color, int icon) {
        if (item >= 0 && item < this.itemViews.size()) {
            AlertDialogCell cell = this.itemViews.get(item);
            cell.textView.setTextColor(color);
            cell.imageView.setColorFilter(new PorterDuffColorFilter(icon, PorterDuff.Mode.MULTIPLY));
        }
    }

    public int getItemsCount() {
        return this.itemViews.size();
    }

    public void setMessage(CharSequence text) {
        this.message = text;
        if (this.messageTextView == null) {
            return;
        }
        if (!TextUtils.isEmpty(text)) {
            this.messageTextView.setText(this.message);
            this.messageTextView.setVisibility(0);
            return;
        }
        this.messageTextView.setVisibility(8);
    }

    public void setMessageTextViewClickable(boolean value) {
        this.messageTextViewClickable = value;
    }

    public void setButton(int type, CharSequence text, DialogInterface.OnClickListener listener) {
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
        ViewGroup viewGroup = this.buttonsLayout;
        if (viewGroup != null) {
            return viewGroup.findViewWithTag(Integer.valueOf(type));
        }
        return null;
    }

    public void invalidateDrawable(Drawable who) {
        this.contentScrollView.invalidate();
        this.scrollContainer.invalidate();
    }

    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        ScrollView scrollView = this.contentScrollView;
        if (scrollView != null) {
            scrollView.postDelayed(what, when);
        }
    }

    public void unscheduleDrawable(Drawable who, Runnable what) {
        ScrollView scrollView = this.contentScrollView;
        if (scrollView != null) {
            scrollView.removeCallbacks(what);
        }
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener listener) {
        this.onCancelListener = listener;
        super.setOnCancelListener(listener);
    }

    public void setPositiveButtonListener(DialogInterface.OnClickListener listener) {
        this.positiveButtonListener = listener;
    }

    /* access modifiers changed from: protected */
    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    public void showDelayed(long delay) {
        AndroidUtilities.cancelRunOnUIThread(this.showRunnable);
        AndroidUtilities.runOnUIThread(this.showRunnable, delay);
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return null;
    }

    public static class Builder {
        private AlertDialog alertDialog;

        protected Builder(AlertDialog alert) {
            this.alertDialog = alert;
        }

        public Builder(Context context) {
            this(context, (Theme.ResourcesProvider) null);
        }

        public Builder(Context context, Theme.ResourcesProvider resourcesProvider) {
            this(context, 0, resourcesProvider);
        }

        public Builder(Context context, int progressViewStyle, Theme.ResourcesProvider resourcesProvider) {
            this.alertDialog = new AlertDialog(context, progressViewStyle, resourcesProvider);
        }

        public Context getContext() {
            return this.alertDialog.getContext();
        }

        public Builder forceVerticalButtons() {
            boolean unused = this.alertDialog.verticalButtons = true;
            return this;
        }

        public Builder setItems(CharSequence[] items, DialogInterface.OnClickListener onClickListener) {
            CharSequence[] unused = this.alertDialog.items = items;
            DialogInterface.OnClickListener unused2 = this.alertDialog.onClickListener = onClickListener;
            return this;
        }

        public Builder setCheckFocusable(boolean value) {
            boolean unused = this.alertDialog.checkFocusable = value;
            return this;
        }

        public Builder setItems(CharSequence[] items, int[] icons, DialogInterface.OnClickListener onClickListener) {
            CharSequence[] unused = this.alertDialog.items = items;
            int[] unused2 = this.alertDialog.itemIcons = icons;
            DialogInterface.OnClickListener unused3 = this.alertDialog.onClickListener = onClickListener;
            return this;
        }

        public Builder setView(View view) {
            return setView(view, -2);
        }

        public Builder setView(View view, int height) {
            View unused = this.alertDialog.customView = view;
            int unused2 = this.alertDialog.customViewHeight = height;
            return this;
        }

        public Builder setTitle(CharSequence title) {
            CharSequence unused = this.alertDialog.title = title;
            return this;
        }

        public Builder setSubtitle(CharSequence subtitle) {
            CharSequence unused = this.alertDialog.subtitle = subtitle;
            return this;
        }

        public Builder setTopImage(int resId, int backgroundColor) {
            int unused = this.alertDialog.topResId = resId;
            int unused2 = this.alertDialog.topBackgroundColor = backgroundColor;
            return this;
        }

        public Builder setTopView(View view) {
            View unused = this.alertDialog.topView = view;
            return this;
        }

        public Builder setDialogButtonColorKey(String key) {
            String unused = this.alertDialog.dialogButtonColorKey = key;
            return this;
        }

        public Builder setTopAnimation(int resId, int size, boolean autoRepeat, int backgroundColor) {
            int unused = this.alertDialog.topAnimationId = resId;
            int unused2 = this.alertDialog.topAnimationSize = size;
            boolean unused3 = this.alertDialog.topAnimationAutoRepeat = autoRepeat;
            int unused4 = this.alertDialog.topBackgroundColor = backgroundColor;
            return this;
        }

        public Builder setTopAnimation(int resId, int backgroundColor) {
            return setTopAnimation(resId, 94, true, backgroundColor);
        }

        public Builder setTopImage(Drawable drawable, int backgroundColor) {
            Drawable unused = this.alertDialog.topDrawable = drawable;
            int unused2 = this.alertDialog.topBackgroundColor = backgroundColor;
            return this;
        }

        public Builder setMessage(CharSequence message) {
            CharSequence unused = this.alertDialog.message = message;
            return this;
        }

        public Builder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
            CharSequence unused = this.alertDialog.positiveButtonText = text;
            DialogInterface.OnClickListener unused2 = this.alertDialog.positiveButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
            CharSequence unused = this.alertDialog.negativeButtonText = text;
            DialogInterface.OnClickListener unused2 = this.alertDialog.negativeButtonListener = listener;
            return this;
        }

        public Builder setNeutralButton(CharSequence text, DialogInterface.OnClickListener listener) {
            CharSequence unused = this.alertDialog.neutralButtonText = text;
            DialogInterface.OnClickListener unused2 = this.alertDialog.neutralButtonListener = listener;
            return this;
        }

        public Builder setOnBackButtonListener(DialogInterface.OnClickListener listener) {
            DialogInterface.OnClickListener unused = this.alertDialog.onBackButtonListener = listener;
            return this;
        }

        public Builder setOnCancelListener(DialogInterface.OnCancelListener listener) {
            this.alertDialog.setOnCancelListener(listener);
            return this;
        }

        public Builder setCustomViewOffset(int offset) {
            int unused = this.alertDialog.customViewOffset = offset;
            return this;
        }

        public Builder setMessageTextViewClickable(boolean value) {
            boolean unused = this.alertDialog.messageTextViewClickable = value;
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

        public Builder setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
            this.alertDialog.setOnDismissListener(onDismissListener);
            return this;
        }

        public void setTopViewAspectRatio(float aspectRatio) {
            float unused = this.alertDialog.aspectRatio = aspectRatio;
        }

        public Builder setDimEnabled(boolean dimEnabled) {
            boolean unused = this.alertDialog.dimEnabled = dimEnabled;
            return this;
        }

        public Builder setDimAlpha(float dimAlpha) {
            float unused = this.alertDialog.dimAlpha = dimAlpha;
            return this;
        }

        public void notDrawBackgroundOnTopView(boolean b) {
            boolean unused = this.alertDialog.notDrawBackgroundOnTopView = b;
        }

        public void setButtonsVertical(boolean vertical) {
            boolean unused = this.alertDialog.verticalButtons = vertical;
        }

        public Builder setOnPreDismissListener(DialogInterface.OnDismissListener onDismissListener) {
            DialogInterface.OnDismissListener unused = this.alertDialog.onDismissListener = onDismissListener;
            return this;
        }
    }
}

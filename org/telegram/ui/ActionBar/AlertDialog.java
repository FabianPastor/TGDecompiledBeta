package org.telegram.ui.ActionBar;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.Components.LayoutHelper;

public class AlertDialog extends Dialog {
    private FrameLayout buttonsLayout;
    private boolean cancelable;
    private boolean cancelableByTouchOutside;
    private LinearLayout containerView;
    private View customView;
    private boolean dismissed;
    private int[] itemIcons;
    private ArrayList<AlertDialogCell> itemViews = new ArrayList();
    private CharSequence[] items;
    private WindowInsets lastInsets;
    private int maxHeight;
    private CharSequence message;
    private TextView messageTextView;
    private OnClickListener negativeButtonListener;
    private CharSequence negativeButtonText;
    private OnClickListener neutralButtonListener;
    private CharSequence neutralButtonText;
    private OnClickListener onClickListener;
    private OnDismissListener onDismissListener;
    private OnClickListener positiveButtonListener;
    private CharSequence positiveButtonText;
    private Drawable shadowDrawable;
    private CharSequence title;
    private TextView titleTextView;

    private class AlertDialogCell extends FrameLayout {
        private ImageView imageView;
        private TextView textView;
        final /* synthetic */ AlertDialog this$0;

        public AlertDialogCell(AlertDialog alertDialog, Context context) {
            int i = 3;
            this.this$0 = alertDialog;
            super(context);
            setBackgroundDrawable(Theme.getSelectorDrawable(false));
            setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
            this.imageView = new ImageView(context);
            this.imageView.setScaleType(ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogIcon), Mode.MULTIPLY));
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

        public Builder(Context context) {
            this.alertDialog = new AlertDialog(context);
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

    public AlertDialog(Context context) {
        super(context, R.style.TransparentDialog);
        this.shadowDrawable = context.getResources().getDrawable(R.drawable.popup_fixed).mutate();
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), Mode.MULTIPLY));
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius((float) AndroidUtilities.dp(2.0f));
        drawable.setColor(Theme.getColor(Theme.key_dialogBackground));
        this.shadowDrawable = drawable;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected void onCreate(Bundle savedInstanceState) {
        int maxWidth;
        super.onCreate(savedInstanceState);
        this.containerView = new LinearLayout(getContext()) {
            private boolean inLayout;

            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int a;
                this.inLayout = true;
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = MeasureSpec.getSize(heightMeasureSpec);
                int availableHeight = (height - getPaddingTop()) - getPaddingBottom();
                int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp(48.0f), NUM);
                int childFullWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, NUM);
                if (AlertDialog.this.buttonsLayout != null) {
                    int count = AlertDialog.this.buttonsLayout.getChildCount();
                    for (a = 0; a < count; a++) {
                        ((TextView) AlertDialog.this.buttonsLayout.getChildAt(a)).setMaxWidth(AndroidUtilities.dp((float) ((width - AndroidUtilities.dp(24.0f)) / 2)));
                    }
                    AlertDialog.this.buttonsLayout.measure(childFullWidthMeasureSpec, heightMeasureSpec);
                    LayoutParams layoutParams = (LayoutParams) AlertDialog.this.buttonsLayout.getLayoutParams();
                    availableHeight -= (AlertDialog.this.buttonsLayout.getMeasuredHeight() + layoutParams.bottomMargin) + layoutParams.topMargin;
                }
                if (AlertDialog.this.titleTextView != null) {
                    AlertDialog.this.titleTextView.measure(childWidthMeasureSpec, heightMeasureSpec);
                    layoutParams = (LayoutParams) AlertDialog.this.titleTextView.getLayoutParams();
                    availableHeight -= (AlertDialog.this.titleTextView.getMeasuredHeight() + layoutParams.bottomMargin) + layoutParams.topMargin;
                }
                if (AlertDialog.this.messageTextView != null) {
                    AlertDialog.this.messageTextView.measure(childWidthMeasureSpec, MeasureSpec.makeMeasureSpec(availableHeight, Integer.MIN_VALUE));
                    if (AlertDialog.this.messageTextView.getVisibility() != 8) {
                        layoutParams = (LayoutParams) AlertDialog.this.messageTextView.getLayoutParams();
                        availableHeight -= (AlertDialog.this.messageTextView.getMeasuredHeight() + layoutParams.bottomMargin) + layoutParams.topMargin;
                    }
                }
                if (!AlertDialog.this.itemViews.isEmpty()) {
                    for (a = 0; a < AlertDialog.this.itemViews.size(); a++) {
                        View item = (View) AlertDialog.this.itemViews.get(a);
                        item.measure(childFullWidthMeasureSpec, MeasureSpec.makeMeasureSpec(availableHeight, Integer.MIN_VALUE));
                        layoutParams = (LayoutParams) item.getLayoutParams();
                        availableHeight -= (item.getMeasuredHeight() + layoutParams.bottomMargin) + layoutParams.topMargin;
                    }
                }
                if (AlertDialog.this.customView != null) {
                    AlertDialog.this.customView.measure(childFullWidthMeasureSpec, MeasureSpec.makeMeasureSpec(availableHeight, Integer.MIN_VALUE));
                    layoutParams = (LayoutParams) AlertDialog.this.customView.getLayoutParams();
                    availableHeight -= (AlertDialog.this.customView.getMeasuredHeight() + layoutParams.bottomMargin) + layoutParams.topMargin;
                }
                setMeasuredDimension(width, height - availableHeight);
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
        };
        this.containerView.setOrientation(1);
        this.containerView.setBackgroundDrawable(this.shadowDrawable);
        this.containerView.setFitsSystemWindows(VERSION.SDK_INT >= 21);
        setContentView(this.containerView);
        boolean hasButtons = (this.positiveButtonText == null && this.negativeButtonText == null && this.neutralButtonText == null) ? false : true;
        if (this.title != null) {
            this.titleTextView = new TextView(getContext());
            this.titleTextView.setText(this.title);
            this.titleTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            this.titleTextView.setTextSize(1, 20.0f);
            this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.containerView.addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 24, 24, 24, 20));
        }
        this.messageTextView = new TextView(getContext());
        this.messageTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.messageTextView.setTextSize(1, 16.0f);
        this.messageTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.containerView.addView(this.messageTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 24, this.title == null ? 24 : 0, 24, this.customView != null ? 20 : 24));
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
                    AlertDialogCell cell = new AlertDialogCell(this, getContext());
                    cell.setTextAndIcon(this.items[a], this.itemIcons != null ? this.itemIcons[a] : 0);
                    cell.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
                    this.containerView.addView(cell, LayoutHelper.createLinear(-1, 48, 51, 0, 0, 0, a == this.items.length + -1 ? 10 : 0));
                    cell.setTag(Integer.valueOf(a));
                    cell.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if (AlertDialog.this.onClickListener != null) {
                                AlertDialog.this.onClickListener.onClick(AlertDialog.this, ((Integer) v.getTag()).intValue());
                            }
                            AlertDialog.this.dismiss();
                        }
                    });
                    this.itemViews.add(cell);
                }
                a++;
            }
        }
        if (this.customView != null) {
            if (this.customView.getParent() != null) {
                ((ViewGroup) this.customView.getParent()).removeView(this.customView);
            }
            LinearLayout linearLayout = this.containerView;
            View view = this.customView;
            float f = (this.title == null && this.message == null) ? 24.0f : 0.0f;
            linearLayout.addView(view, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, f, 0.0f, 16.0f));
        }
        if (hasButtons) {
            View anonymousClass4;
            this.buttonsLayout = new FrameLayout(getContext()) {
                protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                    int count = getChildCount();
                    View positiveButton = null;
                    int width = right - left;
                    for (int a = 0; a < count; a++) {
                        View child = getChildAt(a);
                        if (((Integer) child.getTag()).intValue() == -1) {
                            positiveButton = child;
                            child.layout((width - getPaddingRight()) - child.getMeasuredWidth(), getPaddingTop(), (width - getPaddingRight()) + child.getMeasuredWidth(), getPaddingTop() + child.getMeasuredHeight());
                        } else if (((Integer) child.getTag()).intValue() == -2) {
                            int x = (width - getPaddingRight()) - child.getMeasuredWidth();
                            if (positiveButton != null) {
                                x -= positiveButton.getMeasuredWidth() + AndroidUtilities.dp(8.0f);
                            }
                            child.layout(x, getPaddingTop(), child.getMeasuredWidth() + x, getPaddingTop() + child.getMeasuredHeight());
                        } else {
                            child.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + child.getMeasuredWidth(), getPaddingTop() + child.getMeasuredHeight());
                        }
                    }
                }
            };
            this.buttonsLayout.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            this.containerView.addView(this.buttonsLayout, LayoutHelper.createLinear(-1, 52));
            if (this.positiveButtonText != null) {
                anonymousClass4 = new TextView(getContext()) {
                    public void setEnabled(boolean enabled) {
                        super.setEnabled(enabled);
                        setAlpha(enabled ? 1.0f : 0.5f);
                    }
                };
                anonymousClass4.setMinWidth(AndroidUtilities.dp(64.0f));
                anonymousClass4.setTag(Integer.valueOf(-1));
                anonymousClass4.setTextSize(1, 14.0f);
                anonymousClass4.setTextColor(Theme.getColor(Theme.key_dialogButton));
                anonymousClass4.setGravity(17);
                anonymousClass4.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                anonymousClass4.setText(this.positiveButtonText.toString().toUpperCase());
                anonymousClass4.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
                anonymousClass4.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                this.buttonsLayout.addView(anonymousClass4, LayoutHelper.createFrame(-2, 36, 53));
                anonymousClass4.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (AlertDialog.this.positiveButtonListener != null) {
                            AlertDialog.this.positiveButtonListener.onClick(AlertDialog.this, -1);
                        }
                        AlertDialog.this.dismiss();
                    }
                });
            }
            if (this.negativeButtonText != null) {
                anonymousClass4 = new TextView(getContext()) {
                    public void setEnabled(boolean enabled) {
                        super.setEnabled(enabled);
                        setAlpha(enabled ? 1.0f : 0.5f);
                    }
                };
                anonymousClass4.setMinWidth(AndroidUtilities.dp(64.0f));
                anonymousClass4.setTag(Integer.valueOf(-2));
                anonymousClass4.setTextSize(1, 14.0f);
                anonymousClass4.setTextColor(Theme.getColor(Theme.key_dialogButton));
                anonymousClass4.setGravity(17);
                anonymousClass4.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                anonymousClass4.setText(this.negativeButtonText.toString().toUpperCase());
                anonymousClass4.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
                anonymousClass4.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                this.buttonsLayout.addView(anonymousClass4, LayoutHelper.createFrame(-2, 36, 53));
                anonymousClass4.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (AlertDialog.this.negativeButtonListener != null) {
                            AlertDialog.this.negativeButtonListener.onClick(AlertDialog.this, -2);
                        }
                        AlertDialog.this.cancel();
                    }
                });
            }
            if (this.neutralButtonText != null) {
                anonymousClass4 = new TextView(getContext()) {
                    public void setEnabled(boolean enabled) {
                        super.setEnabled(enabled);
                        setAlpha(enabled ? 1.0f : 0.5f);
                    }
                };
                anonymousClass4.setMinWidth(AndroidUtilities.dp(64.0f));
                anonymousClass4.setTag(Integer.valueOf(-1));
                anonymousClass4.setTextSize(1, 14.0f);
                anonymousClass4.setTextColor(Theme.getColor(Theme.key_dialogButton));
                anonymousClass4.setGravity(17);
                anonymousClass4.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                anonymousClass4.setText(this.neutralButtonText.toString().toUpperCase());
                anonymousClass4.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable());
                anonymousClass4.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                this.buttonsLayout.addView(anonymousClass4, LayoutHelper.createFrame(-2, 36, 51));
                anonymousClass4.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (AlertDialog.this.neutralButtonListener != null) {
                            AlertDialog.this.neutralButtonListener.onClick(AlertDialog.this, -2);
                        }
                        AlertDialog.this.dismiss();
                    }
                });
            }
        }
        int calculatedWidth = AndroidUtilities.displaySize.x - AndroidUtilities.dp(56.0f);
        if (!AndroidUtilities.isTablet()) {
            maxWidth = AndroidUtilities.dp(356.0f);
        } else if (AndroidUtilities.isSmallTablet()) {
            maxWidth = AndroidUtilities.dp(446.0f);
        } else {
            maxWidth = AndroidUtilities.dp(496.0f);
        }
        Window window = getWindow();
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(window.getAttributes());
        params.dimAmount = 0.6f;
        this.maxHeight = AndroidUtilities.displaySize.y - AndroidUtilities.dp(104.0f);
        params.width = Math.min(maxWidth, calculatedWidth);
        params.flags |= 2;
        if (this.customView != null) {
        }
        params.flags |= 131072;
        window.setAttributes(params);
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
        this.dismissed = true;
        super.dismiss();
    }

    public void setCanceledOnTouchOutside(boolean cancel) {
        this.cancelableByTouchOutside = cancel;
        super.setCanceledOnTouchOutside(cancel);
    }

    public void setCancelable(boolean value) {
        this.cancelable = value;
    }

    public void setMessage(CharSequence message) {
        if (TextUtils.isEmpty(message)) {
            this.messageTextView.setVisibility(8);
            return;
        }
        this.messageTextView.setText(message);
        this.messageTextView.setVisibility(0);
    }

    public View getButton(int type) {
        return this.buttonsLayout.findViewWithTag(Integer.valueOf(type));
    }
}

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
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LineProgressView;

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
            TextView textView = this.textView;
            if (LocaleController.isRTL) {
                i = 5;
            }
            addView(textView, LayoutHelper.createFrame(-2, -2, i | 16));
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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
        super(context, NUM);
        if (progressStyle != 3) {
            this.shadowDrawable = context.getResources().getDrawable(NUM).mutate();
            this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(getThemeColor("dialogBackground"), Mode.MULTIPLY));
            this.shadowDrawable.getPadding(this.backgroundPaddings);
        }
        this.progressViewStyle = progressStyle;
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x0929  */
    public void onCreate(android.os.Bundle r25) {
        /*
        r24 = this;
        super.onCreate(r25);
        r12 = new org.telegram.ui.ActionBar.AlertDialog$1;
        r2 = r24.getContext();
        r0 = r24;
        r12.<init>(r2);
        r2 = 1;
        r12.setOrientation(r2);
        r0 = r24;
        r2 = r0.progressViewStyle;
        r3 = 3;
        if (r2 != r3) goto L_0x043e;
    L_0x0019:
        r2 = 0;
        r12.setBackgroundDrawable(r2);
    L_0x001d:
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 21;
        if (r2 < r3) goto L_0x0447;
    L_0x0023:
        r2 = 1;
    L_0x0024:
        r12.setFitsSystemWindows(r2);
        r0 = r24;
        r0.setContentView(r12);
        r0 = r24;
        r2 = r0.positiveButtonText;
        if (r2 != 0) goto L_0x003e;
    L_0x0032:
        r0 = r24;
        r2 = r0.negativeButtonText;
        if (r2 != 0) goto L_0x003e;
    L_0x0038:
        r0 = r24;
        r2 = r0.neutralButtonText;
        if (r2 == 0) goto L_0x044a;
    L_0x003e:
        r13 = 1;
    L_0x003f:
        r0 = r24;
        r2 = r0.topResId;
        if (r2 != 0) goto L_0x004b;
    L_0x0045:
        r0 = r24;
        r2 = r0.topDrawable;
        if (r2 == 0) goto L_0x00c3;
    L_0x004b:
        r2 = new android.widget.ImageView;
        r3 = r24.getContext();
        r2.<init>(r3);
        r0 = r24;
        r0.topImageView = r2;
        r0 = r24;
        r2 = r0.topDrawable;
        if (r2 == 0) goto L_0x044d;
    L_0x005e:
        r0 = r24;
        r2 = r0.topImageView;
        r0 = r24;
        r3 = r0.topDrawable;
        r2.setImageDrawable(r3);
    L_0x0069:
        r0 = r24;
        r2 = r0.topImageView;
        r3 = android.widget.ImageView.ScaleType.CENTER;
        r2.setScaleType(r3);
        r0 = r24;
        r2 = r0.topImageView;
        r3 = r24.getContext();
        r3 = r3.getResources();
        r4 = NUM; // 0x7var_c7 float:1.79455E38 double:1.052935728E-314;
        r3 = r3.getDrawable(r4);
        r2.setBackgroundDrawable(r3);
        r0 = r24;
        r2 = r0.topImageView;
        r2 = r2.getBackground();
        r3 = new android.graphics.PorterDuffColorFilter;
        r0 = r24;
        r4 = r0.topBackgroundColor;
        r5 = android.graphics.PorterDuff.Mode.MULTIPLY;
        r3.<init>(r4, r5);
        r2.setColorFilter(r3);
        r0 = r24;
        r2 = r0.topImageView;
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r2.setPadding(r3, r4, r5, r6);
        r0 = r24;
        r0 = r0.topImageView;
        r22 = r0;
        r2 = -1;
        r0 = r24;
        r3 = r0.topHeight;
        r4 = 51;
        r5 = -8;
        r6 = -8;
        r7 = 0;
        r8 = 0;
        r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r3, r4, r5, r6, r7, r8);
        r0 = r22;
        r12.addView(r0, r2);
    L_0x00c3:
        r0 = r24;
        r2 = r0.title;
        if (r2 == 0) goto L_0x0164;
    L_0x00c9:
        r2 = new android.widget.FrameLayout;
        r3 = r24.getContext();
        r2.<init>(r3);
        r0 = r24;
        r0.titleContainer = r2;
        r0 = r24;
        r8 = r0.titleContainer;
        r2 = -2;
        r3 = -2;
        r4 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r5 = 0;
        r6 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r7 = 0;
        r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r3, r4, r5, r6, r7);
        r12.addView(r8, r2);
        r2 = new android.widget.TextView;
        r3 = r24.getContext();
        r2.<init>(r3);
        r0 = r24;
        r0.titleTextView = r2;
        r0 = r24;
        r2 = r0.titleTextView;
        r0 = r24;
        r3 = r0.title;
        r2.setText(r3);
        r0 = r24;
        r2 = r0.titleTextView;
        r3 = "dialogTextBlack";
        r0 = r24;
        r3 = r0.getThemeColor(r3);
        r2.setTextColor(r3);
        r0 = r24;
        r2 = r0.titleTextView;
        r3 = 1;
        r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r2.setTextSize(r3, r4);
        r0 = r24;
        r2 = r0.titleTextView;
        r3 = "fonts/rmedium.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r2.setTypeface(r3);
        r0 = r24;
        r3 = r0.titleTextView;
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 == 0) goto L_0x045a;
    L_0x0131:
        r2 = 5;
    L_0x0132:
        r2 = r2 | 48;
        r3.setGravity(r2);
        r0 = r24;
        r0 = r0.titleContainer;
        r22 = r0;
        r0 = r24;
        r0 = r0.titleTextView;
        r23 = r0;
        r2 = -2;
        r3 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 == 0) goto L_0x045d;
    L_0x014a:
        r4 = 5;
    L_0x014b:
        r4 = r4 | 48;
        r5 = 0;
        r6 = NUM; // 0x41980000 float:19.0 double:5.43709615E-315;
        r7 = 0;
        r0 = r24;
        r8 = r0.subtitle;
        if (r8 == 0) goto L_0x0460;
    L_0x0157:
        r8 = 2;
    L_0x0158:
        r8 = (float) r8;
        r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3, r4, r5, r6, r7, r8);
        r0 = r22;
        r1 = r23;
        r0.addView(r1, r2);
    L_0x0164:
        r0 = r24;
        r2 = r0.secondTitle;
        if (r2 == 0) goto L_0x01d6;
    L_0x016a:
        r0 = r24;
        r2 = r0.title;
        if (r2 == 0) goto L_0x01d6;
    L_0x0170:
        r2 = new android.widget.TextView;
        r3 = r24.getContext();
        r2.<init>(r3);
        r0 = r24;
        r0.secondTitleTextView = r2;
        r0 = r24;
        r2 = r0.secondTitleTextView;
        r0 = r24;
        r3 = r0.secondTitle;
        r2.setText(r3);
        r0 = r24;
        r2 = r0.secondTitleTextView;
        r3 = "dialogTextGray3";
        r0 = r24;
        r3 = r0.getThemeColor(r3);
        r2.setTextColor(r3);
        r0 = r24;
        r2 = r0.secondTitleTextView;
        r3 = 1;
        r4 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r2.setTextSize(r3, r4);
        r0 = r24;
        r3 = r0.secondTitleTextView;
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 == 0) goto L_0x046e;
    L_0x01aa:
        r2 = 3;
    L_0x01ab:
        r2 = r2 | 48;
        r3.setGravity(r2);
        r0 = r24;
        r0 = r0.titleContainer;
        r22 = r0;
        r0 = r24;
        r0 = r0.secondTitleTextView;
        r23 = r0;
        r2 = -2;
        r3 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 == 0) goto L_0x0471;
    L_0x01c3:
        r4 = 3;
    L_0x01c4:
        r4 = r4 | 48;
        r5 = 0;
        r6 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r7 = 0;
        r8 = 0;
        r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3, r4, r5, r6, r7, r8);
        r0 = r22;
        r1 = r23;
        r0.addView(r1, r2);
    L_0x01d6:
        r0 = r24;
        r2 = r0.subtitle;
        if (r2 == 0) goto L_0x0241;
    L_0x01dc:
        r2 = new android.widget.TextView;
        r3 = r24.getContext();
        r2.<init>(r3);
        r0 = r24;
        r0.subtitleTextView = r2;
        r0 = r24;
        r2 = r0.subtitleTextView;
        r0 = r24;
        r3 = r0.subtitle;
        r2.setText(r3);
        r0 = r24;
        r2 = r0.subtitleTextView;
        r3 = "dialogIcon";
        r0 = r24;
        r3 = r0.getThemeColor(r3);
        r2.setTextColor(r3);
        r0 = r24;
        r2 = r0.subtitleTextView;
        r3 = 1;
        r4 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r2.setTextSize(r3, r4);
        r0 = r24;
        r3 = r0.subtitleTextView;
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 == 0) goto L_0x0474;
    L_0x0216:
        r2 = 5;
    L_0x0217:
        r2 = r2 | 48;
        r3.setGravity(r2);
        r0 = r24;
        r0 = r0.subtitleTextView;
        r22 = r0;
        r2 = -2;
        r3 = -2;
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 == 0) goto L_0x0477;
    L_0x0228:
        r4 = 5;
    L_0x0229:
        r4 = r4 | 48;
        r5 = 24;
        r6 = 0;
        r7 = 24;
        r0 = r24;
        r8 = r0.items;
        if (r8 == 0) goto L_0x047a;
    L_0x0236:
        r8 = 14;
    L_0x0238:
        r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r3, r4, r5, r6, r7, r8);
        r0 = r22;
        r12.addView(r0, r2);
    L_0x0241:
        r0 = r24;
        r2 = r0.progressViewStyle;
        if (r2 != 0) goto L_0x030c;
    L_0x0247:
        r0 = r24;
        r3 = r0.shadow;
        r4 = 0;
        r2 = r24.getContext();
        r2 = r2.getResources();
        r5 = NUM; // 0x7var_ float:1.7944886E38 double:1.052935578E-314;
        r2 = r2.getDrawable(r5);
        r2 = r2.mutate();
        r2 = (android.graphics.drawable.BitmapDrawable) r2;
        r3[r4] = r2;
        r0 = r24;
        r3 = r0.shadow;
        r4 = 1;
        r2 = r24.getContext();
        r2 = r2.getResources();
        r5 = NUM; // 0x7var_ float:1.7944888E38 double:1.0529355786E-314;
        r2 = r2.getDrawable(r5);
        r2 = r2.mutate();
        r2 = (android.graphics.drawable.BitmapDrawable) r2;
        r3[r4] = r2;
        r0 = r24;
        r2 = r0.shadow;
        r3 = 0;
        r2 = r2[r3];
        r3 = 0;
        r2.setAlpha(r3);
        r0 = r24;
        r2 = r0.shadow;
        r3 = 1;
        r2 = r2[r3];
        r3 = 0;
        r2.setAlpha(r3);
        r0 = r24;
        r2 = r0.shadow;
        r3 = 0;
        r2 = r2[r3];
        r0 = r24;
        r2.setCallback(r0);
        r0 = r24;
        r2 = r0.shadow;
        r3 = 1;
        r2 = r2[r3];
        r0 = r24;
        r2.setCallback(r0);
        r2 = new org.telegram.ui.ActionBar.AlertDialog$2;
        r3 = r24.getContext();
        r0 = r24;
        r2.<init>(r3);
        r0 = r24;
        r0.contentScrollView = r2;
        r0 = r24;
        r2 = r0.contentScrollView;
        r3 = 0;
        r2.setVerticalScrollBarEnabled(r3);
        r0 = r24;
        r2 = r0.contentScrollView;
        r3 = "dialogScrollGlow";
        r0 = r24;
        r3 = r0.getThemeColor(r3);
        org.telegram.messenger.AndroidUtilities.setScrollViewEdgeEffectColor(r2, r3);
        r0 = r24;
        r8 = r0.contentScrollView;
        r2 = -1;
        r3 = -2;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r7 = 0;
        r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r3, r4, r5, r6, r7);
        r12.addView(r8, r2);
        r2 = new android.widget.LinearLayout;
        r3 = r24.getContext();
        r2.<init>(r3);
        r0 = r24;
        r0.scrollContainer = r2;
        r0 = r24;
        r2 = r0.scrollContainer;
        r3 = 1;
        r2.setOrientation(r3);
        r0 = r24;
        r2 = r0.contentScrollView;
        r0 = r24;
        r3 = r0.scrollContainer;
        r4 = new android.widget.FrameLayout$LayoutParams;
        r5 = -1;
        r6 = -2;
        r4.<init>(r5, r6);
        r2.addView(r3, r4);
    L_0x030c:
        r2 = new android.widget.TextView;
        r3 = r24.getContext();
        r2.<init>(r3);
        r0 = r24;
        r0.messageTextView = r2;
        r0 = r24;
        r2 = r0.messageTextView;
        r3 = "dialogTextBlack";
        r0 = r24;
        r3 = r0.getThemeColor(r3);
        r2.setTextColor(r3);
        r0 = r24;
        r2 = r0.messageTextView;
        r3 = 1;
        r4 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r2.setTextSize(r3, r4);
        r0 = r24;
        r2 = r0.messageTextView;
        r3 = new org.telegram.messenger.AndroidUtilities$LinkMovementMethodMy;
        r3.<init>();
        r2.setMovementMethod(r3);
        r0 = r24;
        r2 = r0.messageTextView;
        r3 = "dialogTextLink";
        r0 = r24;
        r3 = r0.getThemeColor(r3);
        r2.setLinkTextColor(r3);
        r0 = r24;
        r3 = r0.messageTextView;
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 == 0) goto L_0x047e;
    L_0x0357:
        r2 = 5;
    L_0x0358:
        r2 = r2 | 48;
        r3.setGravity(r2);
        r0 = r24;
        r2 = r0.progressViewStyle;
        r3 = 1;
        if (r2 != r3) goto L_0x0491;
    L_0x0364:
        r2 = new android.widget.FrameLayout;
        r3 = r24.getContext();
        r2.<init>(r3);
        r0 = r24;
        r0.progressViewContainer = r2;
        r0 = r24;
        r0 = r0.progressViewContainer;
        r22 = r0;
        r2 = -1;
        r3 = 44;
        r4 = 51;
        r5 = 23;
        r0 = r24;
        r6 = r0.title;
        if (r6 != 0) goto L_0x0481;
    L_0x0384:
        r6 = 24;
    L_0x0386:
        r7 = 23;
        r8 = 24;
        r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r3, r4, r5, r6, r7, r8);
        r0 = r22;
        r12.addView(r0, r2);
        r17 = new org.telegram.ui.Components.RadialProgressView;
        r2 = r24.getContext();
        r0 = r17;
        r0.<init>(r2);
        r2 = "dialogProgressCircle";
        r0 = r24;
        r2 = r0.getThemeColor(r2);
        r0 = r17;
        r0.setProgressColor(r2);
        r0 = r24;
        r3 = r0.progressViewContainer;
        r4 = 44;
        r5 = 44;
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 == 0) goto L_0x0484;
    L_0x03b8:
        r2 = 5;
    L_0x03b9:
        r2 = r2 | 48;
        r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5, r2);
        r0 = r17;
        r3.addView(r0, r2);
        r0 = r24;
        r2 = r0.messageTextView;
        r3 = 1;
        r2.setLines(r3);
        r0 = r24;
        r2 = r0.messageTextView;
        r3 = android.text.TextUtils.TruncateAt.END;
        r2.setEllipsize(r3);
        r0 = r24;
        r0 = r0.progressViewContainer;
        r22 = r0;
        r0 = r24;
        r0 = r0.messageTextView;
        r23 = r0;
        r2 = -2;
        r3 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 == 0) goto L_0x0487;
    L_0x03e8:
        r4 = 5;
    L_0x03e9:
        r4 = r4 | 16;
        r5 = org.telegram.messenger.LocaleController.isRTL;
        if (r5 == 0) goto L_0x048a;
    L_0x03ef:
        r5 = 0;
    L_0x03f0:
        r5 = (float) r5;
        r6 = 0;
        r7 = org.telegram.messenger.LocaleController.isRTL;
        if (r7 == 0) goto L_0x048e;
    L_0x03f6:
        r7 = 62;
    L_0x03f8:
        r7 = (float) r7;
        r8 = 0;
        r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3, r4, r5, r6, r7, r8);
        r0 = r22;
        r1 = r23;
        r0.addView(r1, r2);
    L_0x0405:
        r0 = r24;
        r2 = r0.message;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0635;
    L_0x040f:
        r0 = r24;
        r2 = r0.messageTextView;
        r0 = r24;
        r3 = r0.message;
        r2.setText(r3);
        r0 = r24;
        r2 = r0.messageTextView;
        r3 = 0;
        r2.setVisibility(r3);
    L_0x0422:
        r0 = r24;
        r2 = r0.items;
        if (r2 == 0) goto L_0x0688;
    L_0x0428:
        r18 = 0;
        r14 = 0;
        r9 = 0;
    L_0x042c:
        r0 = r24;
        r2 = r0.items;
        r2 = r2.length;
        if (r9 >= r2) goto L_0x0688;
    L_0x0433:
        r0 = r24;
        r2 = r0.items;
        r2 = r2[r9];
        if (r2 != 0) goto L_0x0640;
    L_0x043b:
        r9 = r9 + 1;
        goto L_0x042c;
    L_0x043e:
        r0 = r24;
        r2 = r0.shadowDrawable;
        r12.setBackgroundDrawable(r2);
        goto L_0x001d;
    L_0x0447:
        r2 = 0;
        goto L_0x0024;
    L_0x044a:
        r13 = 0;
        goto L_0x003f;
    L_0x044d:
        r0 = r24;
        r2 = r0.topImageView;
        r0 = r24;
        r3 = r0.topResId;
        r2.setImageResource(r3);
        goto L_0x0069;
    L_0x045a:
        r2 = 3;
        goto L_0x0132;
    L_0x045d:
        r4 = 3;
        goto L_0x014b;
    L_0x0460:
        r0 = r24;
        r8 = r0.items;
        if (r8 == 0) goto L_0x046a;
    L_0x0466:
        r8 = 14;
        goto L_0x0158;
    L_0x046a:
        r8 = 10;
        goto L_0x0158;
    L_0x046e:
        r2 = 5;
        goto L_0x01ab;
    L_0x0471:
        r4 = 5;
        goto L_0x01c4;
    L_0x0474:
        r2 = 3;
        goto L_0x0217;
    L_0x0477:
        r4 = 3;
        goto L_0x0229;
    L_0x047a:
        r8 = 10;
        goto L_0x0238;
    L_0x047e:
        r2 = 3;
        goto L_0x0358;
    L_0x0481:
        r6 = 0;
        goto L_0x0386;
    L_0x0484:
        r2 = 3;
        goto L_0x03b9;
    L_0x0487:
        r4 = 3;
        goto L_0x03e9;
    L_0x048a:
        r5 = 62;
        goto L_0x03f0;
    L_0x048e:
        r7 = 0;
        goto L_0x03f8;
    L_0x0491:
        r0 = r24;
        r2 = r0.progressViewStyle;
        r3 = 2;
        if (r2 != r3) goto L_0x0585;
    L_0x0498:
        r0 = r24;
        r0 = r0.messageTextView;
        r22 = r0;
        r2 = -2;
        r3 = -2;
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 == 0) goto L_0x057b;
    L_0x04a4:
        r4 = 5;
    L_0x04a5:
        r4 = r4 | 48;
        r5 = 24;
        r0 = r24;
        r6 = r0.title;
        if (r6 != 0) goto L_0x057e;
    L_0x04af:
        r6 = 19;
    L_0x04b1:
        r7 = 24;
        r8 = 20;
        r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r3, r4, r5, r6, r7, r8);
        r0 = r22;
        r12.addView(r0, r2);
        r2 = new org.telegram.ui.Components.LineProgressView;
        r3 = r24.getContext();
        r2.<init>(r3);
        r0 = r24;
        r0.lineProgressView = r2;
        r0 = r24;
        r2 = r0.lineProgressView;
        r0 = r24;
        r3 = r0.currentProgress;
        r3 = (float) r3;
        r4 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r3 = r3 / r4;
        r4 = 0;
        r2.setProgress(r3, r4);
        r0 = r24;
        r2 = r0.lineProgressView;
        r3 = "dialogLineProgress";
        r0 = r24;
        r3 = r0.getThemeColor(r3);
        r2.setProgressColor(r3);
        r0 = r24;
        r2 = r0.lineProgressView;
        r3 = "dialogLineProgressBackground";
        r0 = r24;
        r3 = r0.getThemeColor(r3);
        r2.setBackColor(r3);
        r0 = r24;
        r0 = r0.lineProgressView;
        r22 = r0;
        r2 = -1;
        r3 = 4;
        r4 = 19;
        r5 = 24;
        r6 = 0;
        r7 = 24;
        r8 = 0;
        r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r3, r4, r5, r6, r7, r8);
        r0 = r22;
        r12.addView(r0, r2);
        r2 = new android.widget.TextView;
        r3 = r24.getContext();
        r2.<init>(r3);
        r0 = r24;
        r0.lineProgressViewPercent = r2;
        r0 = r24;
        r2 = r0.lineProgressViewPercent;
        r3 = "fonts/rmedium.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r2.setTypeface(r3);
        r0 = r24;
        r3 = r0.lineProgressViewPercent;
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 == 0) goto L_0x0581;
    L_0x0537:
        r2 = 5;
    L_0x0538:
        r2 = r2 | 48;
        r3.setGravity(r2);
        r0 = r24;
        r2 = r0.lineProgressViewPercent;
        r3 = "dialogTextGray2";
        r0 = r24;
        r3 = r0.getThemeColor(r3);
        r2.setTextColor(r3);
        r0 = r24;
        r2 = r0.lineProgressViewPercent;
        r3 = 1;
        r4 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r2.setTextSize(r3, r4);
        r0 = r24;
        r0 = r0.lineProgressViewPercent;
        r22 = r0;
        r2 = -2;
        r3 = -2;
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 == 0) goto L_0x0583;
    L_0x0563:
        r4 = 5;
    L_0x0564:
        r4 = r4 | 48;
        r5 = 23;
        r6 = 4;
        r7 = 23;
        r8 = 24;
        r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r3, r4, r5, r6, r7, r8);
        r0 = r22;
        r12.addView(r0, r2);
        r24.updateLineProgressTextView();
        goto L_0x0405;
    L_0x057b:
        r4 = 3;
        goto L_0x04a5;
    L_0x057e:
        r6 = 0;
        goto L_0x04b1;
    L_0x0581:
        r2 = 3;
        goto L_0x0538;
    L_0x0583:
        r4 = 3;
        goto L_0x0564;
    L_0x0585:
        r0 = r24;
        r2 = r0.progressViewStyle;
        r3 = 3;
        if (r2 != r3) goto L_0x05fa;
    L_0x058c:
        r2 = 0;
        r0 = r24;
        r0.setCanceledOnTouchOutside(r2);
        r2 = 0;
        r0 = r24;
        r0.setCancelable(r2);
        r2 = new android.widget.FrameLayout;
        r3 = r24.getContext();
        r2.<init>(r3);
        r0 = r24;
        r0.progressViewContainer = r2;
        r0 = r24;
        r2 = r0.progressViewContainer;
        r3 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = "dialog_inlineProgressBackground";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r3 = org.telegram.ui.ActionBar.Theme.createRoundRectDrawable(r3, r4);
        r2.setBackgroundDrawable(r3);
        r0 = r24;
        r2 = r0.progressViewContainer;
        r3 = 86;
        r4 = 86;
        r5 = 17;
        r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r4, r5);
        r12.addView(r2, r3);
        r17 = new org.telegram.ui.Components.RadialProgressView;
        r2 = r24.getContext();
        r0 = r17;
        r0.<init>(r2);
        r2 = "dialog_inlineProgress";
        r0 = r24;
        r2 = r0.getThemeColor(r2);
        r0 = r17;
        r0.setProgressColor(r2);
        r0 = r24;
        r2 = r0.progressViewContainer;
        r3 = 86;
        r4 = 86;
        r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r4);
        r0 = r17;
        r2.addView(r0, r3);
        goto L_0x0405;
    L_0x05fa:
        r0 = r24;
        r0 = r0.scrollContainer;
        r22 = r0;
        r0 = r24;
        r0 = r0.messageTextView;
        r23 = r0;
        r2 = -2;
        r3 = -2;
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 == 0) goto L_0x0631;
    L_0x060c:
        r4 = 5;
    L_0x060d:
        r4 = r4 | 48;
        r5 = 24;
        r6 = 0;
        r7 = 24;
        r0 = r24;
        r8 = r0.customView;
        if (r8 != 0) goto L_0x0620;
    L_0x061a:
        r0 = r24;
        r8 = r0.items;
        if (r8 == 0) goto L_0x0633;
    L_0x0620:
        r0 = r24;
        r8 = r0.customViewOffset;
    L_0x0624:
        r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r3, r4, r5, r6, r7, r8);
        r0 = r22;
        r1 = r23;
        r0.addView(r1, r2);
        goto L_0x0405;
    L_0x0631:
        r4 = 3;
        goto L_0x060d;
    L_0x0633:
        r8 = 0;
        goto L_0x0624;
    L_0x0635:
        r0 = r24;
        r2 = r0.messageTextView;
        r3 = 8;
        r2.setVisibility(r3);
        goto L_0x0422;
    L_0x0640:
        r11 = new org.telegram.ui.ActionBar.AlertDialog$AlertDialogCell;
        r2 = r24.getContext();
        r11.<init>(r2);
        r0 = r24;
        r2 = r0.items;
        r3 = r2[r9];
        r0 = r24;
        r2 = r0.itemIcons;
        if (r2 == 0) goto L_0x0686;
    L_0x0655:
        r0 = r24;
        r2 = r0.itemIcons;
        r2 = r2[r9];
    L_0x065b:
        r11.setTextAndIcon(r3, r2);
        r2 = java.lang.Integer.valueOf(r9);
        r11.setTag(r2);
        r0 = r24;
        r2 = r0.itemViews;
        r2.add(r11);
        r0 = r24;
        r2 = r0.scrollContainer;
        r3 = -1;
        r4 = 50;
        r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r4);
        r2.addView(r11, r3);
        r2 = new org.telegram.ui.ActionBar.AlertDialog$$Lambda$1;
        r0 = r24;
        r2.<init>(r0);
        r11.setOnClickListener(r2);
        goto L_0x043b;
    L_0x0686:
        r2 = 0;
        goto L_0x065b;
    L_0x0688:
        r0 = r24;
        r2 = r0.customView;
        if (r2 == 0) goto L_0x06bc;
    L_0x068e:
        r0 = r24;
        r2 = r0.customView;
        r2 = r2.getParent();
        if (r2 == 0) goto L_0x06ab;
    L_0x0698:
        r0 = r24;
        r2 = r0.customView;
        r20 = r2.getParent();
        r20 = (android.view.ViewGroup) r20;
        r0 = r24;
        r2 = r0.customView;
        r0 = r20;
        r0.removeView(r2);
    L_0x06ab:
        r0 = r24;
        r2 = r0.scrollContainer;
        r0 = r24;
        r3 = r0.customView;
        r4 = -1;
        r5 = -2;
        r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r5);
        r2.addView(r3, r4);
    L_0x06bc:
        if (r13 == 0) goto L_0x08e8;
    L_0x06be:
        r2 = new org.telegram.ui.ActionBar.AlertDialog$3;
        r3 = r24.getContext();
        r0 = r24;
        r2.<init>(r3);
        r0 = r24;
        r0.buttonsLayout = r2;
        r0 = r24;
        r2 = r0.buttonsLayout;
        r3 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r2.setPadding(r3, r4, r5, r6);
        r0 = r24;
        r2 = r0.buttonsLayout;
        r3 = -1;
        r4 = 52;
        r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r4);
        r12.addView(r2, r3);
        r0 = r24;
        r2 = r0.positiveButtonText;
        if (r2 == 0) goto L_0x0796;
    L_0x0700:
        r19 = new org.telegram.ui.ActionBar.AlertDialog$4;
        r2 = r24.getContext();
        r0 = r19;
        r1 = r24;
        r0.<init>(r2);
        r2 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r19;
        r0.setMinWidth(r2);
        r2 = -1;
        r2 = java.lang.Integer.valueOf(r2);
        r0 = r19;
        r0.setTag(r2);
        r2 = 1;
        r3 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r0 = r19;
        r0.setTextSize(r2, r3);
        r2 = "dialogButton";
        r0 = r24;
        r2 = r0.getThemeColor(r2);
        r0 = r19;
        r0.setTextColor(r2);
        r2 = 17;
        r0 = r19;
        r0.setGravity(r2);
        r2 = "fonts/rmedium.ttf";
        r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2);
        r0 = r19;
        r0.setTypeface(r2);
        r0 = r24;
        r2 = r0.positiveButtonText;
        r2 = r2.toString();
        r2 = r2.toUpperCase();
        r0 = r19;
        r0.setText(r2);
        r2 = org.telegram.ui.ActionBar.Theme.getRoundRectSelectorDrawable();
        r0 = r19;
        r0.setBackgroundDrawable(r2);
        r2 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = 0;
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = 0;
        r0 = r19;
        r0.setPadding(r2, r3, r4, r5);
        r0 = r24;
        r2 = r0.buttonsLayout;
        r3 = -2;
        r4 = 36;
        r5 = 53;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r4, r5);
        r0 = r19;
        r2.addView(r0, r3);
        r2 = new org.telegram.ui.ActionBar.AlertDialog$$Lambda$2;
        r0 = r24;
        r2.<init>(r0);
        r0 = r19;
        r0.setOnClickListener(r2);
    L_0x0796:
        r0 = r24;
        r2 = r0.negativeButtonText;
        if (r2 == 0) goto L_0x083f;
    L_0x079c:
        r19 = new org.telegram.ui.ActionBar.AlertDialog$5;
        r2 = r24.getContext();
        r0 = r19;
        r1 = r24;
        r0.<init>(r2);
        r2 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r19;
        r0.setMinWidth(r2);
        r2 = -2;
        r2 = java.lang.Integer.valueOf(r2);
        r0 = r19;
        r0.setTag(r2);
        r2 = 1;
        r3 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r0 = r19;
        r0.setTextSize(r2, r3);
        r2 = "dialogButton";
        r0 = r24;
        r2 = r0.getThemeColor(r2);
        r0 = r19;
        r0.setTextColor(r2);
        r2 = 17;
        r0 = r19;
        r0.setGravity(r2);
        r2 = "fonts/rmedium.ttf";
        r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2);
        r0 = r19;
        r0.setTypeface(r2);
        r2 = android.text.TextUtils.TruncateAt.END;
        r0 = r19;
        r0.setEllipsize(r2);
        r2 = 1;
        r0 = r19;
        r0.setSingleLine(r2);
        r0 = r24;
        r2 = r0.negativeButtonText;
        r2 = r2.toString();
        r2 = r2.toUpperCase();
        r0 = r19;
        r0.setText(r2);
        r2 = org.telegram.ui.ActionBar.Theme.getRoundRectSelectorDrawable();
        r0 = r19;
        r0.setBackgroundDrawable(r2);
        r2 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = 0;
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = 0;
        r0 = r19;
        r0.setPadding(r2, r3, r4, r5);
        r0 = r24;
        r2 = r0.buttonsLayout;
        r3 = -2;
        r4 = 36;
        r5 = 53;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r4, r5);
        r0 = r19;
        r2.addView(r0, r3);
        r2 = new org.telegram.ui.ActionBar.AlertDialog$$Lambda$3;
        r0 = r24;
        r2.<init>(r0);
        r0 = r19;
        r0.setOnClickListener(r2);
    L_0x083f:
        r0 = r24;
        r2 = r0.neutralButtonText;
        if (r2 == 0) goto L_0x08e8;
    L_0x0845:
        r19 = new org.telegram.ui.ActionBar.AlertDialog$6;
        r2 = r24.getContext();
        r0 = r19;
        r1 = r24;
        r0.<init>(r2);
        r2 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r19;
        r0.setMinWidth(r2);
        r2 = -3;
        r2 = java.lang.Integer.valueOf(r2);
        r0 = r19;
        r0.setTag(r2);
        r2 = 1;
        r3 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r0 = r19;
        r0.setTextSize(r2, r3);
        r2 = "dialogButton";
        r0 = r24;
        r2 = r0.getThemeColor(r2);
        r0 = r19;
        r0.setTextColor(r2);
        r2 = 17;
        r0 = r19;
        r0.setGravity(r2);
        r2 = "fonts/rmedium.ttf";
        r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2);
        r0 = r19;
        r0.setTypeface(r2);
        r2 = android.text.TextUtils.TruncateAt.END;
        r0 = r19;
        r0.setEllipsize(r2);
        r2 = 1;
        r0 = r19;
        r0.setSingleLine(r2);
        r0 = r24;
        r2 = r0.neutralButtonText;
        r2 = r2.toString();
        r2 = r2.toUpperCase();
        r0 = r19;
        r0.setText(r2);
        r2 = org.telegram.ui.ActionBar.Theme.getRoundRectSelectorDrawable();
        r0 = r19;
        r0.setBackgroundDrawable(r2);
        r2 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = 0;
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = 0;
        r0 = r19;
        r0.setPadding(r2, r3, r4, r5);
        r0 = r24;
        r2 = r0.buttonsLayout;
        r3 = -2;
        r4 = 36;
        r5 = 51;
        r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r4, r5);
        r0 = r19;
        r2.addView(r0, r3);
        r2 = new org.telegram.ui.ActionBar.AlertDialog$$Lambda$4;
        r0 = r24;
        r2.<init>(r0);
        r0 = r19;
        r0.setOnClickListener(r2);
    L_0x08e8:
        r21 = r24.getWindow();
        r16 = new android.view.WindowManager$LayoutParams;
        r16.<init>();
        r2 = r21.getAttributes();
        r0 = r16;
        r0.copyFrom(r2);
        r0 = r24;
        r2 = r0.progressViewStyle;
        r3 = 3;
        if (r2 != r3) goto L_0x0936;
    L_0x0901:
        r2 = -1;
        r0 = r16;
        r0.width = r2;
    L_0x0906:
        r0 = r24;
        r2 = r0.customView;
        if (r2 == 0) goto L_0x0918;
    L_0x090c:
        r0 = r24;
        r2 = r0.customView;
        r0 = r24;
        r2 = r0.canTextInput(r2);
        if (r2 != 0) goto L_0x0992;
    L_0x0918:
        r0 = r16;
        r2 = r0.flags;
        r3 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r2 = r2 | r3;
        r0 = r16;
        r0.flags = r2;
    L_0x0923:
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 28;
        if (r2 < r3) goto L_0x092e;
    L_0x0929:
        r2 = 0;
        r0 = r16;
        r0.layoutInDisplayCutoutMode = r2;
    L_0x092e:
        r0 = r21;
        r1 = r16;
        r0.setAttributes(r1);
        return;
    L_0x0936:
        r2 = NUM; // 0x3var_a float:0.6 double:5.230388065E-315;
        r0 = r16;
        r0.dimAmount = r2;
        r0 = r16;
        r2 = r0.flags;
        r2 = r2 | 2;
        r0 = r16;
        r0.flags = r2;
        r2 = org.telegram.messenger.AndroidUtilities.displaySize;
        r2 = r2.x;
        r0 = r24;
        r0.lastScreenWidth = r2;
        r2 = org.telegram.messenger.AndroidUtilities.displaySize;
        r2 = r2.x;
        r3 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r10 = r2 - r3;
        r2 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r2 == 0) goto L_0x098b;
    L_0x0961:
        r2 = org.telegram.messenger.AndroidUtilities.isSmallTablet();
        if (r2 == 0) goto L_0x0984;
    L_0x0967:
        r2 = NUM; // 0x43dvar_ float:446.0 double:5.62586622E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r2);
    L_0x096d:
        r2 = java.lang.Math.min(r15, r10);
        r0 = r24;
        r3 = r0.backgroundPaddings;
        r3 = r3.left;
        r2 = r2 + r3;
        r0 = r24;
        r3 = r0.backgroundPaddings;
        r3 = r3.right;
        r2 = r2 + r3;
        r0 = r16;
        r0.width = r2;
        goto L_0x0906;
    L_0x0984:
        r2 = NUM; // 0x43var_ float:496.0 double:5.633960993E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r2);
        goto L_0x096d;
    L_0x098b:
        r2 = NUM; // 0x43b20000 float:356.0 double:5.611295633E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r2);
        goto L_0x096d;
    L_0x0992:
        r2 = 4;
        r0 = r16;
        r0.softInputMode = r2;
        goto L_0x0923;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.AlertDialog.onCreate(android.os.Bundle):void");
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$onCreate$0$AlertDialog(View v) {
        if (this.onClickListener != null) {
            this.onClickListener.onClick(this, ((Integer) v.getTag()).intValue());
        }
        dismiss();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$onCreate$1$AlertDialog(View v) {
        if (this.positiveButtonListener != null) {
            this.positiveButtonListener.onClick(this, -1);
        }
        if (this.dismissDialogByButtons) {
            dismiss();
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$onCreate$2$AlertDialog(View v) {
        if (this.negativeButtonListener != null) {
            this.negativeButtonListener.onClick(this, -2);
        }
        if (this.dismissDialogByButtons) {
            cancel();
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$onCreate$3$AlertDialog(View v) {
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
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.getString("StopLoading", NUM));
            builder.setPositiveButton(LocaleController.getString("WaitMore", NUM), null);
            builder.setNegativeButton(LocaleController.getString("Stop", NUM), new AlertDialog$$Lambda$5(this));
            builder.setOnDismissListener(new AlertDialog$$Lambda$6(this));
            this.cancelDialog = builder.show();
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$showCancelAlert$4$AlertDialog(DialogInterface dialogInterface, int i) {
        if (this.onCancelListener != null) {
            this.onCancelListener.onCancel(this);
        }
        dismiss();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$showCancelAlert$5$AlertDialog(DialogInterface dialog) {
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
            } catch (Exception e) {
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

    /* Access modifiers changed, original: protected */
    public int getThemeColor(String key) {
        return Theme.getColor(key);
    }
}

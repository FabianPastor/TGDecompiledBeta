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
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AndroidUtilities.LinkMovementMethodMy;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
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
    private Runnable dismissRunnable = new -$$Lambda$H9iyBEO4Zihg11d8XSg-qvJnAGk(this);
    private int[] itemIcons;
    private ArrayList<AlertDialogCell> itemViews = new ArrayList();
    private CharSequence[] items;
    private int lastScreenWidth;
    private LineProgressView lineProgressView;
    private TextView lineProgressViewPercent;
    private CharSequence message;
    private TextView messageTextView;
    private boolean messageTextViewClickable = true;
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
            super(context);
            setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 2));
            setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
            this.imageView = new ImageView(context);
            this.imageView.setScaleType(ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogIcon"), Mode.MULTIPLY));
            int i = 5;
            addView(this.imageView, LayoutHelper.createFrame(-2, 40, (LocaleController.isRTL ? 5 : 3) | 16));
            this.textView = new TextView(context);
            this.textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TruncateAt.END);
            this.textView.setTextColor(Theme.getColor("dialogTextBlack"));
            this.textView.setTextSize(1, 16.0f);
            TextView textView = this.textView;
            if (!LocaleController.isRTL) {
                i = 3;
            }
            addView(textView, LayoutHelper.createFrame(-2, -2, i | 16));
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
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

        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            this.alertDialog.setOnCancelListener(onCancelListener);
            return this;
        }

        public Builder setCustomViewOffset(int i) {
            this.alertDialog.customViewOffset = i;
            return this;
        }

        public Builder setMessageTextViewClickable(boolean z) {
            this.alertDialog.messageTextViewClickable = z;
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

    public AlertDialog(Context context, int i) {
        super(context, NUM);
        if (i != 3) {
            this.shadowDrawable = context.getResources().getDrawable(NUM).mutate();
            this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(getThemeColor("dialogBackground"), Mode.MULTIPLY));
            this.shadowDrawable.getPadding(this.backgroundPaddings);
        }
        this.progressViewStyle = i;
    }

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        AnonymousClass1 anonymousClass1 = new LinearLayout(getContext()) {
            private boolean inLayout;

            public boolean hasOverlappingRendering() {
                return false;
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (AlertDialog.this.progressViewStyle != 3) {
                    return super.onTouchEvent(motionEvent);
                }
                AlertDialog.this.showCancelAlert();
                return false;
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (AlertDialog.this.progressViewStyle != 3) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                AlertDialog.this.showCancelAlert();
                return false;
            }

            /* Access modifiers changed, original: protected */
            /* JADX WARNING: Removed duplicated region for block: B:76:0x02db  */
            public void onMeasure(int r12, int r13) {
                /*
                r11 = this;
                r0 = org.telegram.ui.ActionBar.AlertDialog.this;
                r0 = r0.progressViewStyle;
                r1 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
                r2 = 3;
                if (r0 != r2) goto L_0x0033;
            L_0x000b:
                r0 = org.telegram.ui.ActionBar.AlertDialog.this;
                r0 = r0.progressViewContainer;
                r2 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
                r3 = org.telegram.messenger.AndroidUtilities.dp(r2);
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r1);
                r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r1);
                r0.measure(r3, r1);
                r12 = android.view.View.MeasureSpec.getSize(r12);
                r13 = android.view.View.MeasureSpec.getSize(r13);
                r11.setMeasuredDimension(r12, r13);
                goto L_0x0359;
            L_0x0033:
                r0 = 1;
                r11.inLayout = r0;
                r12 = android.view.View.MeasureSpec.getSize(r12);
                r0 = android.view.View.MeasureSpec.getSize(r13);
                r2 = r11.getPaddingTop();
                r0 = r0 - r2;
                r2 = r11.getPaddingBottom();
                r0 = r0 - r2;
                r2 = r11.getPaddingLeft();
                r2 = r12 - r2;
                r3 = r11.getPaddingRight();
                r2 = r2 - r3;
                r3 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
                r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                r3 = r2 - r3;
                r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r1);
                r4 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r1);
                r5 = org.telegram.ui.ActionBar.AlertDialog.this;
                r5 = r5.buttonsLayout;
                r6 = 0;
                if (r5 == 0) goto L_0x00b6;
            L_0x006a:
                r5 = r5.getChildCount();
                r7 = 0;
            L_0x006f:
                if (r7 >= r5) goto L_0x0094;
            L_0x0071:
                r8 = org.telegram.ui.ActionBar.AlertDialog.this;
                r8 = r8.buttonsLayout;
                r8 = r8.getChildAt(r7);
                r9 = r8 instanceof android.widget.TextView;
                if (r9 == 0) goto L_0x0091;
            L_0x007d:
                r8 = (android.widget.TextView) r8;
                r9 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
                r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
                r9 = r2 - r9;
                r9 = r9 / 2;
                r9 = (float) r9;
                r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
                r8.setMaxWidth(r9);
            L_0x0091:
                r7 = r7 + 1;
                goto L_0x006f;
            L_0x0094:
                r2 = org.telegram.ui.ActionBar.AlertDialog.this;
                r2 = r2.buttonsLayout;
                r2.measure(r4, r13);
                r2 = org.telegram.ui.ActionBar.AlertDialog.this;
                r2 = r2.buttonsLayout;
                r2 = r2.getLayoutParams();
                r2 = (android.widget.LinearLayout.LayoutParams) r2;
                r5 = org.telegram.ui.ActionBar.AlertDialog.this;
                r5 = r5.buttonsLayout;
                r5 = r5.getMeasuredHeight();
                r7 = r2.bottomMargin;
                r5 = r5 + r7;
                r2 = r2.topMargin;
                r5 = r5 + r2;
                r2 = r0 - r5;
                goto L_0x00b7;
            L_0x00b6:
                r2 = r0;
            L_0x00b7:
                r5 = org.telegram.ui.ActionBar.AlertDialog.this;
                r5 = r5.secondTitleTextView;
                r7 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
                if (r5 == 0) goto L_0x00d2;
            L_0x00c1:
                r5 = org.telegram.ui.ActionBar.AlertDialog.this;
                r5 = r5.secondTitleTextView;
                r8 = android.view.View.MeasureSpec.getSize(r3);
                r8 = android.view.View.MeasureSpec.makeMeasureSpec(r8, r7);
                r5.measure(r8, r13);
            L_0x00d2:
                r5 = org.telegram.ui.ActionBar.AlertDialog.this;
                r5 = r5.titleTextView;
                r8 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
                if (r5 == 0) goto L_0x010f;
            L_0x00dc:
                r5 = org.telegram.ui.ActionBar.AlertDialog.this;
                r5 = r5.secondTitleTextView;
                if (r5 == 0) goto L_0x0106;
            L_0x00e4:
                r5 = org.telegram.ui.ActionBar.AlertDialog.this;
                r5 = r5.titleTextView;
                r9 = android.view.View.MeasureSpec.getSize(r3);
                r10 = org.telegram.ui.ActionBar.AlertDialog.this;
                r10 = r10.secondTitleTextView;
                r10 = r10.getMeasuredWidth();
                r9 = r9 - r10;
                r10 = org.telegram.messenger.AndroidUtilities.dp(r8);
                r9 = r9 - r10;
                r9 = android.view.View.MeasureSpec.makeMeasureSpec(r9, r1);
                r5.measure(r9, r13);
                goto L_0x010f;
            L_0x0106:
                r5 = org.telegram.ui.ActionBar.AlertDialog.this;
                r5 = r5.titleTextView;
                r5.measure(r3, r13);
            L_0x010f:
                r5 = org.telegram.ui.ActionBar.AlertDialog.this;
                r5 = r5.titleContainer;
                if (r5 == 0) goto L_0x013d;
            L_0x0117:
                r5 = org.telegram.ui.ActionBar.AlertDialog.this;
                r5 = r5.titleContainer;
                r5.measure(r3, r13);
                r5 = org.telegram.ui.ActionBar.AlertDialog.this;
                r5 = r5.titleContainer;
                r5 = r5.getLayoutParams();
                r5 = (android.widget.LinearLayout.LayoutParams) r5;
                r9 = org.telegram.ui.ActionBar.AlertDialog.this;
                r9 = r9.titleContainer;
                r9 = r9.getMeasuredHeight();
                r10 = r5.bottomMargin;
                r9 = r9 + r10;
                r5 = r5.topMargin;
                r9 = r9 + r5;
                r2 = r2 - r9;
            L_0x013d:
                r5 = org.telegram.ui.ActionBar.AlertDialog.this;
                r5 = r5.subtitleTextView;
                if (r5 == 0) goto L_0x016b;
            L_0x0145:
                r5 = org.telegram.ui.ActionBar.AlertDialog.this;
                r5 = r5.subtitleTextView;
                r5.measure(r3, r13);
                r13 = org.telegram.ui.ActionBar.AlertDialog.this;
                r13 = r13.subtitleTextView;
                r13 = r13.getLayoutParams();
                r13 = (android.widget.LinearLayout.LayoutParams) r13;
                r5 = org.telegram.ui.ActionBar.AlertDialog.this;
                r5 = r5.subtitleTextView;
                r5 = r5.getMeasuredHeight();
                r9 = r13.bottomMargin;
                r5 = r5 + r9;
                r13 = r13.topMargin;
                r5 = r5 + r13;
                r2 = r2 - r5;
            L_0x016b:
                r13 = org.telegram.ui.ActionBar.AlertDialog.this;
                r13 = r13.topImageView;
                if (r13 == 0) goto L_0x019f;
            L_0x0173:
                r13 = org.telegram.ui.ActionBar.AlertDialog.this;
                r13 = r13.topImageView;
                r5 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r1);
                r9 = org.telegram.ui.ActionBar.AlertDialog.this;
                r9 = r9.topHeight;
                r9 = (float) r9;
                r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
                r9 = android.view.View.MeasureSpec.makeMeasureSpec(r9, r1);
                r13.measure(r5, r9);
                r13 = org.telegram.ui.ActionBar.AlertDialog.this;
                r13 = r13.topImageView;
                r13 = r13.getMeasuredHeight();
                r5 = org.telegram.messenger.AndroidUtilities.dp(r8);
                r13 = r13 - r5;
                r2 = r2 - r13;
            L_0x019f:
                r13 = org.telegram.ui.ActionBar.AlertDialog.this;
                r13 = r13.progressViewStyle;
                r5 = 8;
                if (r13 != 0) goto L_0x0263;
            L_0x01a9:
                r13 = org.telegram.ui.ActionBar.AlertDialog.this;
                r13 = r13.contentScrollView;
                r13 = r13.getLayoutParams();
                r13 = (android.widget.LinearLayout.LayoutParams) r13;
                r1 = org.telegram.ui.ActionBar.AlertDialog.this;
                r1 = r1.customView;
                if (r1 == 0) goto L_0x01f2;
            L_0x01bd:
                r1 = org.telegram.ui.ActionBar.AlertDialog.this;
                r1 = r1.titleTextView;
                if (r1 != 0) goto L_0x01e0;
            L_0x01c5:
                r1 = org.telegram.ui.ActionBar.AlertDialog.this;
                r1 = r1.messageTextView;
                r1 = r1.getVisibility();
                if (r1 != r5) goto L_0x01e0;
            L_0x01d1:
                r1 = org.telegram.ui.ActionBar.AlertDialog.this;
                r1 = r1.items;
                if (r1 != 0) goto L_0x01e0;
            L_0x01d9:
                r1 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
                r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                goto L_0x01e1;
            L_0x01e0:
                r1 = 0;
            L_0x01e1:
                r13.topMargin = r1;
                r1 = org.telegram.ui.ActionBar.AlertDialog.this;
                r1 = r1.buttonsLayout;
                if (r1 != 0) goto L_0x01ee;
            L_0x01e9:
                r1 = org.telegram.messenger.AndroidUtilities.dp(r8);
                goto L_0x01ef;
            L_0x01ee:
                r1 = 0;
            L_0x01ef:
                r13.bottomMargin = r1;
                goto L_0x0243;
            L_0x01f2:
                r1 = org.telegram.ui.ActionBar.AlertDialog.this;
                r1 = r1.items;
                if (r1 == 0) goto L_0x021d;
            L_0x01fa:
                r1 = org.telegram.ui.ActionBar.AlertDialog.this;
                r1 = r1.titleTextView;
                if (r1 != 0) goto L_0x0213;
            L_0x0202:
                r1 = org.telegram.ui.ActionBar.AlertDialog.this;
                r1 = r1.messageTextView;
                r1 = r1.getVisibility();
                if (r1 != r5) goto L_0x0213;
            L_0x020e:
                r1 = org.telegram.messenger.AndroidUtilities.dp(r8);
                goto L_0x0214;
            L_0x0213:
                r1 = 0;
            L_0x0214:
                r13.topMargin = r1;
                r1 = org.telegram.messenger.AndroidUtilities.dp(r8);
                r13.bottomMargin = r1;
                goto L_0x0243;
            L_0x021d:
                r1 = org.telegram.ui.ActionBar.AlertDialog.this;
                r1 = r1.messageTextView;
                r1 = r1.getVisibility();
                if (r1 != 0) goto L_0x0243;
            L_0x0229:
                r1 = org.telegram.ui.ActionBar.AlertDialog.this;
                r1 = r1.titleTextView;
                if (r1 != 0) goto L_0x0238;
            L_0x0231:
                r1 = NUM; // 0x41980000 float:19.0 double:5.43709615E-315;
                r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                goto L_0x0239;
            L_0x0238:
                r1 = 0;
            L_0x0239:
                r13.topMargin = r1;
                r1 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
                r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                r13.bottomMargin = r1;
            L_0x0243:
                r1 = r13.bottomMargin;
                r13 = r13.topMargin;
                r1 = r1 + r13;
                r2 = r2 - r1;
                r13 = org.telegram.ui.ActionBar.AlertDialog.this;
                r13 = r13.contentScrollView;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r7);
                r13.measure(r4, r1);
                r13 = org.telegram.ui.ActionBar.AlertDialog.this;
                r13 = r13.contentScrollView;
                r13 = r13.getMeasuredHeight();
                r2 = r2 - r13;
                goto L_0x0335;
            L_0x0263:
                r13 = org.telegram.ui.ActionBar.AlertDialog.this;
                r13 = r13.progressViewContainer;
                if (r13 == 0) goto L_0x0296;
            L_0x026b:
                r13 = org.telegram.ui.ActionBar.AlertDialog.this;
                r13 = r13.progressViewContainer;
                r4 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r7);
                r13.measure(r3, r4);
                r13 = org.telegram.ui.ActionBar.AlertDialog.this;
                r13 = r13.progressViewContainer;
                r13 = r13.getLayoutParams();
                r13 = (android.widget.LinearLayout.LayoutParams) r13;
                r4 = org.telegram.ui.ActionBar.AlertDialog.this;
                r4 = r4.progressViewContainer;
                r4 = r4.getMeasuredHeight();
                r5 = r13.bottomMargin;
                r4 = r4 + r5;
                r13 = r13.topMargin;
            L_0x0293:
                r4 = r4 + r13;
                r2 = r2 - r4;
                goto L_0x02d3;
            L_0x0296:
                r13 = org.telegram.ui.ActionBar.AlertDialog.this;
                r13 = r13.messageTextView;
                if (r13 == 0) goto L_0x02d3;
            L_0x029e:
                r13 = org.telegram.ui.ActionBar.AlertDialog.this;
                r13 = r13.messageTextView;
                r4 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r7);
                r13.measure(r3, r4);
                r13 = org.telegram.ui.ActionBar.AlertDialog.this;
                r13 = r13.messageTextView;
                r13 = r13.getVisibility();
                if (r13 == r5) goto L_0x02d3;
            L_0x02b7:
                r13 = org.telegram.ui.ActionBar.AlertDialog.this;
                r13 = r13.messageTextView;
                r13 = r13.getLayoutParams();
                r13 = (android.widget.LinearLayout.LayoutParams) r13;
                r4 = org.telegram.ui.ActionBar.AlertDialog.this;
                r4 = r4.messageTextView;
                r4 = r4.getMeasuredHeight();
                r5 = r13.bottomMargin;
                r4 = r4 + r5;
                r13 = r13.topMargin;
                goto L_0x0293;
            L_0x02d3:
                r13 = org.telegram.ui.ActionBar.AlertDialog.this;
                r13 = r13.lineProgressView;
                if (r13 == 0) goto L_0x0335;
            L_0x02db:
                r13 = org.telegram.ui.ActionBar.AlertDialog.this;
                r13 = r13.lineProgressView;
                r4 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
                r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r1);
                r13.measure(r3, r1);
                r13 = org.telegram.ui.ActionBar.AlertDialog.this;
                r13 = r13.lineProgressView;
                r13 = r13.getLayoutParams();
                r13 = (android.widget.LinearLayout.LayoutParams) r13;
                r1 = org.telegram.ui.ActionBar.AlertDialog.this;
                r1 = r1.lineProgressView;
                r1 = r1.getMeasuredHeight();
                r4 = r13.bottomMargin;
                r1 = r1 + r4;
                r13 = r13.topMargin;
                r1 = r1 + r13;
                r2 = r2 - r1;
                r13 = org.telegram.ui.ActionBar.AlertDialog.this;
                r13 = r13.lineProgressViewPercent;
                r1 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r7);
                r13.measure(r3, r1);
                r13 = org.telegram.ui.ActionBar.AlertDialog.this;
                r13 = r13.lineProgressViewPercent;
                r13 = r13.getLayoutParams();
                r13 = (android.widget.LinearLayout.LayoutParams) r13;
                r1 = org.telegram.ui.ActionBar.AlertDialog.this;
                r1 = r1.lineProgressViewPercent;
                r1 = r1.getMeasuredHeight();
                r3 = r13.bottomMargin;
                r1 = r1 + r3;
                r13 = r13.topMargin;
                r1 = r1 + r13;
                r2 = r2 - r1;
            L_0x0335:
                r0 = r0 - r2;
                r13 = r11.getPaddingTop();
                r0 = r0 + r13;
                r13 = r11.getPaddingBottom();
                r0 = r0 + r13;
                r11.setMeasuredDimension(r12, r0);
                r11.inLayout = r6;
                r12 = org.telegram.ui.ActionBar.AlertDialog.this;
                r12 = r12.lastScreenWidth;
                r13 = org.telegram.messenger.AndroidUtilities.displaySize;
                r13 = r13.x;
                if (r12 == r13) goto L_0x0359;
            L_0x0351:
                r12 = new org.telegram.ui.ActionBar.-$$Lambda$AlertDialog$1$2il1lPevBw8X_3FhfSjXOpGmbaM;
                r12.<init>(r11);
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r12);
            L_0x0359:
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.AlertDialog$AnonymousClass1.onMeasure(int, int):void");
            }

            public /* synthetic */ void lambda$onMeasure$0$AlertDialog$1() {
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

            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                if (AlertDialog.this.progressViewStyle == 3) {
                    i3 = ((i3 - i) - AlertDialog.this.progressViewContainer.getMeasuredWidth()) / 2;
                    i4 = ((i4 - i2) - AlertDialog.this.progressViewContainer.getMeasuredHeight()) / 2;
                    AlertDialog.this.progressViewContainer.layout(i3, i4, AlertDialog.this.progressViewContainer.getMeasuredWidth() + i3, AlertDialog.this.progressViewContainer.getMeasuredHeight() + i4);
                } else if (AlertDialog.this.contentScrollView != null) {
                    if (AlertDialog.this.onScrollChangedListener == null) {
                        AlertDialog.this.onScrollChangedListener = new -$$Lambda$AlertDialog$1$vvKcenyzvRwmFgV39QOFVkx4krI(this);
                        AlertDialog.this.contentScrollView.getViewTreeObserver().addOnScrollChangedListener(AlertDialog.this.onScrollChangedListener);
                    }
                    AlertDialog.this.onScrollChangedListener.onScrollChanged();
                }
            }

            public /* synthetic */ void lambda$onLayout$1$AlertDialog$1() {
                AlertDialog alertDialog = AlertDialog.this;
                boolean z = false;
                boolean z2 = alertDialog.titleTextView != null && AlertDialog.this.contentScrollView.getScrollY() > AlertDialog.this.scrollContainer.getTop();
                alertDialog.runShadowAnimation(0, z2);
                alertDialog = AlertDialog.this;
                if (alertDialog.buttonsLayout != null && alertDialog.contentScrollView.getScrollY() + AlertDialog.this.contentScrollView.getHeight() < AlertDialog.this.scrollContainer.getBottom()) {
                    z = true;
                }
                alertDialog.runShadowAnimation(1, z);
                AlertDialog.this.contentScrollView.invalidate();
            }

            public void requestLayout() {
                if (!this.inLayout) {
                    super.requestLayout();
                }
            }
        };
        anonymousClass1.setOrientation(1);
        if (this.progressViewStyle == 3) {
            anonymousClass1.setBackgroundDrawable(null);
        } else {
            anonymousClass1.setBackgroundDrawable(this.shadowDrawable);
        }
        anonymousClass1.setFitsSystemWindows(VERSION.SDK_INT >= 21);
        setContentView(anonymousClass1);
        Object obj = (this.positiveButtonText == null && this.negativeButtonText == null && this.neutralButtonText == null) ? null : 1;
        if (!(this.topResId == 0 && this.topDrawable == null)) {
            this.topImageView = new ImageView(getContext());
            Drawable drawable = this.topDrawable;
            if (drawable != null) {
                this.topImageView.setImageDrawable(drawable);
            } else {
                this.topImageView.setImageResource(this.topResId);
            }
            this.topImageView.setScaleType(ScaleType.CENTER);
            this.topImageView.setBackgroundDrawable(getContext().getResources().getDrawable(NUM));
            this.topImageView.getBackground().setColorFilter(new PorterDuffColorFilter(this.topBackgroundColor, Mode.MULTIPLY));
            this.topImageView.setPadding(0, 0, 0, 0);
            anonymousClass1.addView(this.topImageView, LayoutHelper.createLinear(-1, this.topHeight, 51, -8, -8, 0, 0));
        }
        String str = "fonts/rmedium.ttf";
        if (this.title != null) {
            this.titleContainer = new FrameLayout(getContext());
            anonymousClass1.addView(this.titleContainer, LayoutHelper.createLinear(-2, -2, 24.0f, 0.0f, 24.0f, 0.0f));
            this.titleTextView = new TextView(getContext());
            this.titleTextView.setText(this.title);
            this.titleTextView.setTextColor(getThemeColor("dialogTextBlack"));
            this.titleTextView.setTextSize(1, 20.0f);
            this.titleTextView.setTypeface(AndroidUtilities.getTypeface(str));
            this.titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            FrameLayout frameLayout = this.titleContainer;
            TextView textView = this.titleTextView;
            int i = (LocaleController.isRTL ? 5 : 3) | 48;
            int i2 = this.subtitle != null ? 2 : this.items != null ? 14 : 10;
            frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, i, 0.0f, 19.0f, 0.0f, (float) i2));
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
            anonymousClass1.addView(this.subtitleTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 24, 0, 24, this.items != null ? 14 : 10));
        }
        if (this.progressViewStyle == 0) {
            this.shadow[0] = (BitmapDrawable) getContext().getResources().getDrawable(NUM).mutate();
            this.shadow[1] = (BitmapDrawable) getContext().getResources().getDrawable(NUM).mutate();
            this.shadow[0].setAlpha(0);
            this.shadow[1].setAlpha(0);
            this.shadow[0].setCallback(this);
            this.shadow[1].setCallback(this);
            this.contentScrollView = new ScrollView(getContext()) {
                /* Access modifiers changed, original: protected */
                public boolean drawChild(Canvas canvas, View view, long j) {
                    boolean drawChild = super.drawChild(canvas, view, j);
                    if (AlertDialog.this.shadow[0].getPaint().getAlpha() != 0) {
                        AlertDialog.this.shadow[0].setBounds(0, getScrollY(), getMeasuredWidth(), getScrollY() + AndroidUtilities.dp(3.0f));
                        AlertDialog.this.shadow[0].draw(canvas);
                    }
                    if (AlertDialog.this.shadow[1].getPaint().getAlpha() != 0) {
                        AlertDialog.this.shadow[1].setBounds(0, (getScrollY() + getMeasuredHeight()) - AndroidUtilities.dp(3.0f), getMeasuredWidth(), getScrollY() + getMeasuredHeight());
                        AlertDialog.this.shadow[1].draw(canvas);
                    }
                    return drawChild;
                }
            };
            this.contentScrollView.setVerticalScrollBarEnabled(false);
            AndroidUtilities.setScrollViewEdgeEffectColor(this.contentScrollView, getThemeColor("dialogScrollGlow"));
            anonymousClass1.addView(this.contentScrollView, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 0.0f));
            this.scrollContainer = new LinearLayout(getContext());
            this.scrollContainer.setOrientation(1);
            this.contentScrollView.addView(this.scrollContainer, new FrameLayout.LayoutParams(-1, -2));
        }
        this.messageTextView = new TextView(getContext());
        this.messageTextView.setTextColor(getThemeColor("dialogTextBlack"));
        this.messageTextView.setTextSize(1, 16.0f);
        this.messageTextView.setMovementMethod(new LinkMovementMethodMy());
        this.messageTextView.setLinkTextColor(getThemeColor("dialogTextLink"));
        if (!this.messageTextViewClickable) {
            this.messageTextView.setClickable(false);
            this.messageTextView.setEnabled(false);
        }
        this.messageTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        int i3 = this.progressViewStyle;
        RadialProgressView radialProgressView;
        if (i3 == 1) {
            this.progressViewContainer = new FrameLayout(getContext());
            anonymousClass1.addView(this.progressViewContainer, LayoutHelper.createLinear(-1, 44, 51, 23, this.title == null ? 24 : 0, 23, 24));
            radialProgressView = new RadialProgressView(getContext());
            radialProgressView.setProgressColor(getThemeColor("dialogProgressCircle"));
            this.progressViewContainer.addView(radialProgressView, LayoutHelper.createFrame(44, 44, (LocaleController.isRTL ? 5 : 3) | 48));
            this.messageTextView.setLines(1);
            this.messageTextView.setEllipsize(TruncateAt.END);
            this.progressViewContainer.addView(this.messageTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, (float) (LocaleController.isRTL ? 0 : 62), 0.0f, (float) (LocaleController.isRTL ? 62 : 0), 0.0f));
        } else if (i3 == 2) {
            anonymousClass1.addView(this.messageTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 24, this.title == null ? 19 : 0, 24, 20));
            this.lineProgressView = new LineProgressView(getContext());
            this.lineProgressView.setProgress(((float) this.currentProgress) / 100.0f, false);
            this.lineProgressView.setProgressColor(getThemeColor("dialogLineProgress"));
            this.lineProgressView.setBackColor(getThemeColor("dialogLineProgressBackground"));
            anonymousClass1.addView(this.lineProgressView, LayoutHelper.createLinear(-1, 4, 19, 24, 0, 24, 0));
            this.lineProgressViewPercent = new TextView(getContext());
            this.lineProgressViewPercent.setTypeface(AndroidUtilities.getTypeface(str));
            this.lineProgressViewPercent.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.lineProgressViewPercent.setTextColor(getThemeColor("dialogTextGray2"));
            this.lineProgressViewPercent.setTextSize(1, 14.0f);
            anonymousClass1.addView(this.lineProgressViewPercent, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 23, 4, 23, 24));
            updateLineProgressTextView();
        } else if (i3 == 3) {
            setCanceledOnTouchOutside(false);
            setCancelable(false);
            this.progressViewContainer = new FrameLayout(getContext());
            this.progressViewContainer.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), Theme.getColor("dialog_inlineProgressBackground")));
            anonymousClass1.addView(this.progressViewContainer, LayoutHelper.createLinear(86, 86, 17));
            radialProgressView = new RadialProgressView(getContext());
            radialProgressView.setProgressColor(getThemeColor("dialog_inlineProgress"));
            this.progressViewContainer.addView(radialProgressView, LayoutHelper.createLinear(86, 86));
        } else {
            LinearLayout linearLayout = this.scrollContainer;
            TextView textView2 = this.messageTextView;
            int i4 = (LocaleController.isRTL ? 5 : 3) | 48;
            int i5 = (this.customView == null && this.items == null) ? 0 : this.customViewOffset;
            linearLayout.addView(textView2, LayoutHelper.createLinear(-2, -2, i4, 24, 0, 24, i5));
        }
        if (TextUtils.isEmpty(this.message)) {
            this.messageTextView.setVisibility(8);
        } else {
            this.messageTextView.setText(this.message);
            this.messageTextView.setVisibility(0);
        }
        if (this.items != null) {
            i3 = 0;
            while (true) {
                CharSequence[] charSequenceArr = this.items;
                if (i3 >= charSequenceArr.length) {
                    break;
                }
                if (charSequenceArr[i3] != null) {
                    AlertDialogCell alertDialogCell = new AlertDialogCell(getContext());
                    CharSequence charSequence = this.items[i3];
                    int[] iArr = this.itemIcons;
                    alertDialogCell.setTextAndIcon(charSequence, iArr != null ? iArr[i3] : 0);
                    alertDialogCell.setTag(Integer.valueOf(i3));
                    this.itemViews.add(alertDialogCell);
                    this.scrollContainer.addView(alertDialogCell, LayoutHelper.createLinear(-1, 50));
                    alertDialogCell.setOnClickListener(new -$$Lambda$AlertDialog$iCLASSNAMEx2guh9hO2NrF8CCJRy6v4_w(this));
                }
                i3++;
            }
        }
        View view = this.customView;
        if (view != null) {
            if (view.getParent() != null) {
                ((ViewGroup) this.customView.getParent()).removeView(this.customView);
            }
            this.scrollContainer.addView(this.customView, LayoutHelper.createLinear(-1, -2));
        }
        if (obj != null) {
            this.buttonsLayout = new FrameLayout(getContext()) {
                /* Access modifiers changed, original: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    int childCount = getChildCount();
                    i3 -= i;
                    View view = null;
                    for (i2 = 0; i2 < childCount; i2++) {
                        View childAt = getChildAt(i2);
                        Integer num = (Integer) childAt.getTag();
                        int measuredWidth;
                        if (num == null) {
                            int left;
                            int top;
                            measuredWidth = childAt.getMeasuredWidth();
                            int measuredHeight = childAt.getMeasuredHeight();
                            if (view != null) {
                                left = view.getLeft() + ((view.getMeasuredWidth() - measuredWidth) / 2);
                                top = view.getTop() + ((view.getMeasuredHeight() - measuredHeight) / 2);
                            } else {
                                left = 0;
                                top = 0;
                            }
                            childAt.layout(left, top, measuredWidth + left, measuredHeight + top);
                        } else if (num.intValue() == -1) {
                            if (LocaleController.isRTL) {
                                childAt.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + childAt.getMeasuredWidth(), getPaddingTop() + childAt.getMeasuredHeight());
                            } else {
                                childAt.layout((i3 - getPaddingRight()) - childAt.getMeasuredWidth(), getPaddingTop(), i3 - getPaddingRight(), getPaddingTop() + childAt.getMeasuredHeight());
                            }
                            view = childAt;
                        } else if (num.intValue() == -2) {
                            if (LocaleController.isRTL) {
                                measuredWidth = getPaddingLeft();
                                if (view != null) {
                                    measuredWidth += view.getMeasuredWidth() + AndroidUtilities.dp(8.0f);
                                }
                                childAt.layout(measuredWidth, getPaddingTop(), childAt.getMeasuredWidth() + measuredWidth, getPaddingTop() + childAt.getMeasuredHeight());
                            } else {
                                measuredWidth = (i3 - getPaddingRight()) - childAt.getMeasuredWidth();
                                if (view != null) {
                                    measuredWidth -= view.getMeasuredWidth() + AndroidUtilities.dp(8.0f);
                                }
                                childAt.layout(measuredWidth, getPaddingTop(), childAt.getMeasuredWidth() + measuredWidth, getPaddingTop() + childAt.getMeasuredHeight());
                            }
                        } else if (num.intValue() == -3) {
                            if (LocaleController.isRTL) {
                                childAt.layout((i3 - getPaddingRight()) - childAt.getMeasuredWidth(), getPaddingTop(), i3 - getPaddingRight(), getPaddingTop() + childAt.getMeasuredHeight());
                            } else {
                                childAt.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + childAt.getMeasuredWidth(), getPaddingTop() + childAt.getMeasuredHeight());
                            }
                        }
                    }
                }

                /* Access modifiers changed, original: protected */
                public void onMeasure(int i, int i2) {
                    super.onMeasure(i, i2);
                    i = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
                    i2 = getChildCount();
                    int i3 = 0;
                    for (int i4 = 0; i4 < i2; i4++) {
                        View childAt = getChildAt(i4);
                        if ((childAt instanceof TextView) && childAt.getTag() != null) {
                            i3 += childAt.getMeasuredWidth();
                        }
                    }
                    if (i3 > i) {
                        View findViewWithTag = findViewWithTag(Integer.valueOf(-2));
                        View findViewWithTag2 = findViewWithTag(Integer.valueOf(-3));
                        if (findViewWithTag != null && findViewWithTag2 != null) {
                            if (findViewWithTag.getMeasuredWidth() < findViewWithTag2.getMeasuredWidth()) {
                                findViewWithTag2.measure(MeasureSpec.makeMeasureSpec(findViewWithTag2.getMeasuredWidth() - (i3 - i), NUM), MeasureSpec.makeMeasureSpec(findViewWithTag2.getMeasuredHeight(), NUM));
                            } else {
                                findViewWithTag.measure(MeasureSpec.makeMeasureSpec(findViewWithTag.getMeasuredWidth() - (i3 - i), NUM), MeasureSpec.makeMeasureSpec(findViewWithTag.getMeasuredHeight(), NUM));
                            }
                        }
                    }
                }
            };
            this.buttonsLayout.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            anonymousClass1.addView(this.buttonsLayout, LayoutHelper.createLinear(-1, 52));
            String str2 = "dialogButton";
            if (this.positiveButtonText != null) {
                AnonymousClass4 anonymousClass4 = new TextView(getContext()) {
                    public void setEnabled(boolean z) {
                        super.setEnabled(z);
                        setAlpha(z ? 1.0f : 0.5f);
                    }

                    public void setTextColor(int i) {
                        super.setTextColor(i);
                        setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(i));
                    }
                };
                anonymousClass4.setMinWidth(AndroidUtilities.dp(64.0f));
                anonymousClass4.setTag(Integer.valueOf(-1));
                anonymousClass4.setTextSize(1, 14.0f);
                anonymousClass4.setTextColor(getThemeColor(str2));
                anonymousClass4.setGravity(17);
                anonymousClass4.setTypeface(AndroidUtilities.getTypeface(str));
                anonymousClass4.setText(this.positiveButtonText.toString().toUpperCase());
                anonymousClass4.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(getThemeColor(str2)));
                anonymousClass4.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                this.buttonsLayout.addView(anonymousClass4, LayoutHelper.createFrame(-2, 36, 53));
                anonymousClass4.setOnClickListener(new -$$Lambda$AlertDialog$rp49coWDdM6PFZnr9_LTptCU2Ag(this));
            }
            if (this.negativeButtonText != null) {
                AnonymousClass5 anonymousClass5 = new TextView(getContext()) {
                    public void setEnabled(boolean z) {
                        super.setEnabled(z);
                        setAlpha(z ? 1.0f : 0.5f);
                    }

                    public void setTextColor(int i) {
                        super.setTextColor(i);
                        setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(i));
                    }
                };
                anonymousClass5.setMinWidth(AndroidUtilities.dp(64.0f));
                anonymousClass5.setTag(Integer.valueOf(-2));
                anonymousClass5.setTextSize(1, 14.0f);
                anonymousClass5.setTextColor(getThemeColor(str2));
                anonymousClass5.setGravity(17);
                anonymousClass5.setTypeface(AndroidUtilities.getTypeface(str));
                anonymousClass5.setEllipsize(TruncateAt.END);
                anonymousClass5.setSingleLine(true);
                anonymousClass5.setText(this.negativeButtonText.toString().toUpperCase());
                anonymousClass5.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(getThemeColor(str2)));
                anonymousClass5.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                this.buttonsLayout.addView(anonymousClass5, LayoutHelper.createFrame(-2, 36, 53));
                anonymousClass5.setOnClickListener(new -$$Lambda$AlertDialog$35svlhUpH-M074FLkhJN8iyIwmw(this));
            }
            if (this.neutralButtonText != null) {
                AnonymousClass6 anonymousClass6 = new TextView(getContext()) {
                    public void setEnabled(boolean z) {
                        super.setEnabled(z);
                        setAlpha(z ? 1.0f : 0.5f);
                    }

                    public void setTextColor(int i) {
                        super.setTextColor(i);
                        setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(i));
                    }
                };
                anonymousClass6.setMinWidth(AndroidUtilities.dp(64.0f));
                anonymousClass6.setTag(Integer.valueOf(-3));
                anonymousClass6.setTextSize(1, 14.0f);
                anonymousClass6.setTextColor(getThemeColor(str2));
                anonymousClass6.setGravity(17);
                anonymousClass6.setTypeface(AndroidUtilities.getTypeface(str));
                anonymousClass6.setEllipsize(TruncateAt.END);
                anonymousClass6.setSingleLine(true);
                anonymousClass6.setText(this.neutralButtonText.toString().toUpperCase());
                anonymousClass6.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(getThemeColor(str2)));
                anonymousClass6.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                this.buttonsLayout.addView(anonymousClass6, LayoutHelper.createFrame(-2, 36, 51));
                anonymousClass6.setOnClickListener(new -$$Lambda$AlertDialog$hCRmQxFHC-EIDauULvRdmfnSEuE(this));
            }
        }
        Window window = getWindow();
        LayoutParams layoutParams = new LayoutParams();
        layoutParams.copyFrom(window.getAttributes());
        if (this.progressViewStyle == 3) {
            layoutParams.width = -1;
        } else {
            layoutParams.dimAmount = 0.6f;
            layoutParams.flags |= 2;
            int i6 = AndroidUtilities.displaySize.x;
            this.lastScreenWidth = i6;
            i6 -= AndroidUtilities.dp(48.0f);
            if (!AndroidUtilities.isTablet()) {
                i3 = AndroidUtilities.dp(356.0f);
            } else if (AndroidUtilities.isSmallTablet()) {
                i3 = AndroidUtilities.dp(446.0f);
            } else {
                i3 = AndroidUtilities.dp(496.0f);
            }
            i6 = Math.min(i3, i6);
            Rect rect = this.backgroundPaddings;
            layoutParams.width = (i6 + rect.left) + rect.right;
        }
        View view2 = this.customView;
        if (view2 == null || !canTextInput(view2)) {
            layoutParams.flags |= 131072;
        } else {
            layoutParams.softInputMode = 4;
        }
        if (VERSION.SDK_INT >= 28) {
            layoutParams.layoutInDisplayCutoutMode = 0;
        }
        window.setAttributes(layoutParams);
    }

    public /* synthetic */ void lambda$onCreate$0$AlertDialog(View view) {
        OnClickListener onClickListener = this.onClickListener;
        if (onClickListener != null) {
            onClickListener.onClick(this, ((Integer) view.getTag()).intValue());
        }
        dismiss();
    }

    public /* synthetic */ void lambda$onCreate$1$AlertDialog(View view) {
        OnClickListener onClickListener = this.positiveButtonListener;
        if (onClickListener != null) {
            onClickListener.onClick(this, -1);
        }
        if (this.dismissDialogByButtons) {
            dismiss();
        }
    }

    public /* synthetic */ void lambda$onCreate$2$AlertDialog(View view) {
        OnClickListener onClickListener = this.negativeButtonListener;
        if (onClickListener != null) {
            onClickListener.onClick(this, -2);
        }
        if (this.dismissDialogByButtons) {
            cancel();
        }
    }

    public /* synthetic */ void lambda$onCreate$3$AlertDialog(View view) {
        OnClickListener onClickListener = this.neutralButtonListener;
        if (onClickListener != null) {
            onClickListener.onClick(this, -2);
        }
        if (this.dismissDialogByButtons) {
            dismiss();
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        OnClickListener onClickListener = this.onBackButtonListener;
        if (onClickListener != null) {
            onClickListener.onClick(this, -2);
        }
    }

    private void showCancelAlert() {
        if (this.canCacnel && this.cancelDialog == null) {
            Builder builder = new Builder(getContext());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.getString("StopLoading", NUM));
            builder.setPositiveButton(LocaleController.getString("WaitMore", NUM), null);
            builder.setNegativeButton(LocaleController.getString("Stop", NUM), new -$$Lambda$AlertDialog$1zFp_sikyCYaQ1aMdMAPeCc-86g(this));
            builder.setOnDismissListener(new -$$Lambda$AlertDialog$_jIiJ2tOQUco_X_BniQD16XJ3QE(this));
            this.cancelDialog = builder.show();
        }
    }

    public /* synthetic */ void lambda$showCancelAlert$4$AlertDialog(DialogInterface dialogInterface, int i) {
        OnCancelListener onCancelListener = this.onCancelListener;
        if (onCancelListener != null) {
            onCancelListener.onCancel(this);
        }
        dismiss();
    }

    public /* synthetic */ void lambda$showCancelAlert$5$AlertDialog(DialogInterface dialogInterface) {
        this.cancelDialog = null;
    }

    private void runShadowAnimation(final int i, boolean z) {
        if ((z && !this.shadowVisibility[i]) || (!z && this.shadowVisibility[i])) {
            this.shadowVisibility[i] = z;
            AnimatorSet[] animatorSetArr = this.shadowAnimation;
            if (animatorSetArr[i] != null) {
                animatorSetArr[i].cancel();
            }
            this.shadowAnimation[i] = new AnimatorSet();
            BitmapDrawable[] bitmapDrawableArr = this.shadow;
            if (bitmapDrawableArr[i] != null) {
                AnimatorSet animatorSet = this.shadowAnimation[i];
                Animator[] animatorArr = new Animator[1];
                Object obj = bitmapDrawableArr[i];
                int[] iArr = new int[1];
                iArr[0] = z ? 255 : 0;
                animatorArr[0] = ObjectAnimator.ofInt(obj, "alpha", iArr);
                animatorSet.playTogether(animatorArr);
            }
            this.shadowAnimation[i].setDuration(150);
            this.shadowAnimation[i].addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (AlertDialog.this.shadowAnimation[i] != null && AlertDialog.this.shadowAnimation[i].equals(animator)) {
                        AlertDialog.this.shadowAnimation[i] = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (AlertDialog.this.shadowAnimation[i] != null && AlertDialog.this.shadowAnimation[i].equals(animator)) {
                        AlertDialog.this.shadowAnimation[i] = null;
                    }
                }
            });
            try {
                this.shadowAnimation[i].start();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public void setProgressStyle(int i) {
        this.progressViewStyle = i;
    }

    public void setDismissDialogByButtons(boolean z) {
        this.dismissDialogByButtons = z;
    }

    public void setProgress(int i) {
        this.currentProgress = i;
        LineProgressView lineProgressView = this.lineProgressView;
        if (lineProgressView != null) {
            lineProgressView.setProgress(((float) i) / 100.0f, true);
            updateLineProgressTextView();
        }
    }

    private void updateLineProgressTextView() {
        this.lineProgressViewPercent.setText(String.format("%d%%", new Object[]{Integer.valueOf(this.currentProgress)}));
    }

    public void setCanCacnel(boolean z) {
        this.canCacnel = z;
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
        AlertDialog alertDialog = this.cancelDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        try {
            super.dismiss();
        } catch (Throwable unused) {
        }
    }

    public void setCanceledOnTouchOutside(boolean z) {
        super.setCanceledOnTouchOutside(z);
    }

    public void setTopImage(int i, int i2) {
        this.topResId = i;
        this.topBackgroundColor = i2;
    }

    public void setTopHeight(int i) {
        this.topHeight = i;
    }

    public void setTopImage(Drawable drawable, int i) {
        this.topDrawable = drawable;
        this.topBackgroundColor = i;
    }

    public void setTitle(CharSequence charSequence) {
        this.title = charSequence;
        TextView textView = this.titleTextView;
        if (textView != null) {
            textView.setText(charSequence);
        }
    }

    public void setSecondTitle(CharSequence charSequence) {
        this.secondTitle = charSequence;
    }

    public void setPositiveButton(CharSequence charSequence, OnClickListener onClickListener) {
        this.positiveButtonText = charSequence;
        this.positiveButtonListener = onClickListener;
    }

    public void setNegativeButton(CharSequence charSequence, OnClickListener onClickListener) {
        this.negativeButtonText = charSequence;
        this.negativeButtonListener = onClickListener;
    }

    public void setNeutralButton(CharSequence charSequence, OnClickListener onClickListener) {
        this.neutralButtonText = charSequence;
        this.neutralButtonListener = onClickListener;
    }

    public void setItemColor(int i, int i2, int i3) {
        if (i >= 0 && i < this.itemViews.size()) {
            AlertDialogCell alertDialogCell = (AlertDialogCell) this.itemViews.get(i);
            alertDialogCell.textView.setTextColor(i2);
            alertDialogCell.imageView.setColorFilter(new PorterDuffColorFilter(i3, Mode.MULTIPLY));
        }
    }

    public int getItemsCount() {
        return this.itemViews.size();
    }

    public void setMessage(CharSequence charSequence) {
        this.message = charSequence;
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

    public void setMessageTextViewClickable(boolean z) {
        this.messageTextViewClickable = z;
    }

    public void setButton(int i, CharSequence charSequence, OnClickListener onClickListener) {
        if (i == -3) {
            this.neutralButtonText = charSequence;
            this.neutralButtonListener = onClickListener;
        } else if (i == -2) {
            this.negativeButtonText = charSequence;
            this.negativeButtonListener = onClickListener;
        } else if (i == -1) {
            this.positiveButtonText = charSequence;
            this.positiveButtonListener = onClickListener;
        }
    }

    public View getButton(int i) {
        FrameLayout frameLayout = this.buttonsLayout;
        return frameLayout != null ? frameLayout.findViewWithTag(Integer.valueOf(i)) : null;
    }

    public void invalidateDrawable(Drawable drawable) {
        this.contentScrollView.invalidate();
        this.scrollContainer.invalidate();
    }

    public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
        ScrollView scrollView = this.contentScrollView;
        if (scrollView != null) {
            scrollView.postDelayed(runnable, j);
        }
    }

    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        ScrollView scrollView = this.contentScrollView;
        if (scrollView != null) {
            scrollView.removeCallbacks(runnable);
        }
    }

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
        super.setOnCancelListener(onCancelListener);
    }

    public void setPositiveButtonListener(OnClickListener onClickListener) {
        this.positiveButtonListener = onClickListener;
    }

    /* Access modifiers changed, original: protected */
    public int getThemeColor(String str) {
        return Theme.getColor(str);
    }
}

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
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LineProgressView;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;

public class AlertDialog extends Dialog implements Drawable.Callback {
    /* access modifiers changed from: private */
    public Rect backgroundPaddings = new Rect();
    protected FrameLayout buttonsLayout;
    private boolean canCacnel = true;
    private AlertDialog cancelDialog;
    /* access modifiers changed from: private */
    public ScrollView contentScrollView;
    private int currentProgress;
    /* access modifiers changed from: private */
    public View customView;
    /* access modifiers changed from: private */
    public int customViewHeight = -2;
    /* access modifiers changed from: private */
    public int customViewOffset = 20;
    private boolean dismissDialogByButtons = true;
    /* access modifiers changed from: private */
    public Runnable dismissRunnable = new Runnable() {
        public final void run() {
            AlertDialog.this.dismiss();
        }
    };
    /* access modifiers changed from: private */
    public int[] itemIcons;
    private ArrayList<AlertDialogCell> itemViews = new ArrayList<>();
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
    public boolean messageTextViewClickable = true;
    /* access modifiers changed from: private */
    public DialogInterface.OnClickListener negativeButtonListener;
    /* access modifiers changed from: private */
    public CharSequence negativeButtonText;
    /* access modifiers changed from: private */
    public DialogInterface.OnClickListener neutralButtonListener;
    /* access modifiers changed from: private */
    public CharSequence neutralButtonText;
    /* access modifiers changed from: private */
    public DialogInterface.OnClickListener onBackButtonListener;
    private DialogInterface.OnCancelListener onCancelListener;
    /* access modifiers changed from: private */
    public DialogInterface.OnClickListener onClickListener;
    private DialogInterface.OnDismissListener onDismissListener;
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
    /* access modifiers changed from: private */
    public LinearLayout scrollContainer;
    private CharSequence secondTitle;
    /* access modifiers changed from: private */
    public TextView secondTitleTextView;
    /* access modifiers changed from: private */
    public BitmapDrawable[] shadow = new BitmapDrawable[2];
    /* access modifiers changed from: private */
    public AnimatorSet[] shadowAnimation = new AnimatorSet[2];
    private Drawable shadowDrawable;
    private boolean[] shadowVisibility = new boolean[2];
    private Runnable showRunnable = new Runnable() {
        public final void run() {
            AlertDialog.this.show();
        }
    };
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
    public int topAnimationId;
    /* access modifiers changed from: private */
    public int topBackgroundColor;
    /* access modifiers changed from: private */
    public Drawable topDrawable;
    /* access modifiers changed from: private */
    public int topHeight = 132;
    /* access modifiers changed from: private */
    public RLottieImageView topImageView;
    /* access modifiers changed from: private */
    public int topResId;

    public static class AlertDialogCell extends FrameLayout {
        /* access modifiers changed from: private */
        public ImageView imageView;
        /* access modifiers changed from: private */
        public TextView textView;

        public AlertDialogCell(Context context) {
            super(context);
            setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 2));
            setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
            this.imageView = new ImageView(context);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogIcon"), PorterDuff.Mode.MULTIPLY));
            int i = 5;
            addView(this.imageView, LayoutHelper.createFrame(-2, 40, (LocaleController.isRTL ? 5 : 3) | 16));
            this.textView = new TextView(context);
            this.textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setTextColor(Theme.getColor("dialogTextBlack"));
            this.textView.setTextSize(1, 16.0f);
            addView(this.textView, LayoutHelper.createFrame(-2, -2, (!LocaleController.isRTL ? 3 : i) | 16));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
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

    public AlertDialog(Context context, int i) {
        super(context, NUM);
        if (i != 3) {
            this.shadowDrawable = context.getResources().getDrawable(NUM).mutate();
            this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(getThemeColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
            this.shadowDrawable.getPadding(this.backgroundPaddings);
        }
        this.progressViewStyle = i;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        int i;
        super.onCreate(bundle);
        AnonymousClass1 r1 = new LinearLayout(getContext()) {
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

            /* access modifiers changed from: protected */
            /* JADX WARNING: Removed duplicated region for block: B:76:0x02db  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onMeasure(int r12, int r13) {
                /*
                    r11 = this;
                    org.telegram.ui.ActionBar.AlertDialog r0 = org.telegram.ui.ActionBar.AlertDialog.this
                    int r0 = r0.progressViewStyle
                    r1 = 1073741824(0x40000000, float:2.0)
                    r2 = 3
                    if (r0 != r2) goto L_0x0033
                    org.telegram.ui.ActionBar.AlertDialog r0 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.FrameLayout r0 = r0.progressViewContainer
                    r2 = 1118568448(0x42aCLASSNAME, float:86.0)
                    int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
                    int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r1)
                    int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                    int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r1)
                    r0.measure(r3, r1)
                    int r12 = android.view.View.MeasureSpec.getSize(r12)
                    int r13 = android.view.View.MeasureSpec.getSize(r13)
                    r11.setMeasuredDimension(r12, r13)
                    goto L_0x0359
                L_0x0033:
                    r0 = 1
                    r11.inLayout = r0
                    int r12 = android.view.View.MeasureSpec.getSize(r12)
                    int r0 = android.view.View.MeasureSpec.getSize(r13)
                    int r2 = r11.getPaddingTop()
                    int r0 = r0 - r2
                    int r2 = r11.getPaddingBottom()
                    int r0 = r0 - r2
                    int r2 = r11.getPaddingLeft()
                    int r2 = r12 - r2
                    int r3 = r11.getPaddingRight()
                    int r2 = r2 - r3
                    r3 = 1111490560(0x42400000, float:48.0)
                    int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                    int r3 = r2 - r3
                    int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r1)
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r1)
                    org.telegram.ui.ActionBar.AlertDialog r5 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.FrameLayout r5 = r5.buttonsLayout
                    r6 = 0
                    if (r5 == 0) goto L_0x00b6
                    int r5 = r5.getChildCount()
                    r7 = 0
                L_0x006f:
                    if (r7 >= r5) goto L_0x0094
                    org.telegram.ui.ActionBar.AlertDialog r8 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.FrameLayout r8 = r8.buttonsLayout
                    android.view.View r8 = r8.getChildAt(r7)
                    boolean r9 = r8 instanceof android.widget.TextView
                    if (r9 == 0) goto L_0x0091
                    android.widget.TextView r8 = (android.widget.TextView) r8
                    r9 = 1103101952(0x41CLASSNAME, float:24.0)
                    int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                    int r9 = r2 - r9
                    int r9 = r9 / 2
                    float r9 = (float) r9
                    int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                    r8.setMaxWidth(r9)
                L_0x0091:
                    int r7 = r7 + 1
                    goto L_0x006f
                L_0x0094:
                    org.telegram.ui.ActionBar.AlertDialog r2 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.FrameLayout r2 = r2.buttonsLayout
                    r2.measure(r4, r13)
                    org.telegram.ui.ActionBar.AlertDialog r2 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.FrameLayout r2 = r2.buttonsLayout
                    android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
                    android.widget.LinearLayout$LayoutParams r2 = (android.widget.LinearLayout.LayoutParams) r2
                    org.telegram.ui.ActionBar.AlertDialog r5 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.FrameLayout r5 = r5.buttonsLayout
                    int r5 = r5.getMeasuredHeight()
                    int r7 = r2.bottomMargin
                    int r5 = r5 + r7
                    int r2 = r2.topMargin
                    int r5 = r5 + r2
                    int r2 = r0 - r5
                    goto L_0x00b7
                L_0x00b6:
                    r2 = r0
                L_0x00b7:
                    org.telegram.ui.ActionBar.AlertDialog r5 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.TextView r5 = r5.secondTitleTextView
                    r7 = -2147483648(0xfffffffvar_, float:-0.0)
                    if (r5 == 0) goto L_0x00d2
                    org.telegram.ui.ActionBar.AlertDialog r5 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.TextView r5 = r5.secondTitleTextView
                    int r8 = android.view.View.MeasureSpec.getSize(r3)
                    int r8 = android.view.View.MeasureSpec.makeMeasureSpec(r8, r7)
                    r5.measure(r8, r13)
                L_0x00d2:
                    org.telegram.ui.ActionBar.AlertDialog r5 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.TextView r5 = r5.titleTextView
                    r8 = 1090519040(0x41000000, float:8.0)
                    if (r5 == 0) goto L_0x010f
                    org.telegram.ui.ActionBar.AlertDialog r5 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.TextView r5 = r5.secondTitleTextView
                    if (r5 == 0) goto L_0x0106
                    org.telegram.ui.ActionBar.AlertDialog r5 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.TextView r5 = r5.titleTextView
                    int r9 = android.view.View.MeasureSpec.getSize(r3)
                    org.telegram.ui.ActionBar.AlertDialog r10 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.TextView r10 = r10.secondTitleTextView
                    int r10 = r10.getMeasuredWidth()
                    int r9 = r9 - r10
                    int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
                    int r9 = r9 - r10
                    int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r9, r1)
                    r5.measure(r9, r13)
                    goto L_0x010f
                L_0x0106:
                    org.telegram.ui.ActionBar.AlertDialog r5 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.TextView r5 = r5.titleTextView
                    r5.measure(r3, r13)
                L_0x010f:
                    org.telegram.ui.ActionBar.AlertDialog r5 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.FrameLayout r5 = r5.titleContainer
                    if (r5 == 0) goto L_0x013d
                    org.telegram.ui.ActionBar.AlertDialog r5 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.FrameLayout r5 = r5.titleContainer
                    r5.measure(r3, r13)
                    org.telegram.ui.ActionBar.AlertDialog r5 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.FrameLayout r5 = r5.titleContainer
                    android.view.ViewGroup$LayoutParams r5 = r5.getLayoutParams()
                    android.widget.LinearLayout$LayoutParams r5 = (android.widget.LinearLayout.LayoutParams) r5
                    org.telegram.ui.ActionBar.AlertDialog r9 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.FrameLayout r9 = r9.titleContainer
                    int r9 = r9.getMeasuredHeight()
                    int r10 = r5.bottomMargin
                    int r9 = r9 + r10
                    int r5 = r5.topMargin
                    int r9 = r9 + r5
                    int r2 = r2 - r9
                L_0x013d:
                    org.telegram.ui.ActionBar.AlertDialog r5 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.TextView r5 = r5.subtitleTextView
                    if (r5 == 0) goto L_0x016b
                    org.telegram.ui.ActionBar.AlertDialog r5 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.TextView r5 = r5.subtitleTextView
                    r5.measure(r3, r13)
                    org.telegram.ui.ActionBar.AlertDialog r13 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.TextView r13 = r13.subtitleTextView
                    android.view.ViewGroup$LayoutParams r13 = r13.getLayoutParams()
                    android.widget.LinearLayout$LayoutParams r13 = (android.widget.LinearLayout.LayoutParams) r13
                    org.telegram.ui.ActionBar.AlertDialog r5 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.TextView r5 = r5.subtitleTextView
                    int r5 = r5.getMeasuredHeight()
                    int r9 = r13.bottomMargin
                    int r5 = r5 + r9
                    int r13 = r13.topMargin
                    int r5 = r5 + r13
                    int r2 = r2 - r5
                L_0x016b:
                    org.telegram.ui.ActionBar.AlertDialog r13 = org.telegram.ui.ActionBar.AlertDialog.this
                    org.telegram.ui.Components.RLottieImageView r13 = r13.topImageView
                    if (r13 == 0) goto L_0x019f
                    org.telegram.ui.ActionBar.AlertDialog r13 = org.telegram.ui.ActionBar.AlertDialog.this
                    org.telegram.ui.Components.RLottieImageView r13 = r13.topImageView
                    int r5 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r1)
                    org.telegram.ui.ActionBar.AlertDialog r9 = org.telegram.ui.ActionBar.AlertDialog.this
                    int r9 = r9.topHeight
                    float r9 = (float) r9
                    int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                    int r9 = android.view.View.MeasureSpec.makeMeasureSpec(r9, r1)
                    r13.measure(r5, r9)
                    org.telegram.ui.ActionBar.AlertDialog r13 = org.telegram.ui.ActionBar.AlertDialog.this
                    org.telegram.ui.Components.RLottieImageView r13 = r13.topImageView
                    int r13 = r13.getMeasuredHeight()
                    int r5 = org.telegram.messenger.AndroidUtilities.dp(r8)
                    int r13 = r13 - r5
                    int r2 = r2 - r13
                L_0x019f:
                    org.telegram.ui.ActionBar.AlertDialog r13 = org.telegram.ui.ActionBar.AlertDialog.this
                    int r13 = r13.progressViewStyle
                    r5 = 8
                    if (r13 != 0) goto L_0x0263
                    org.telegram.ui.ActionBar.AlertDialog r13 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.ScrollView r13 = r13.contentScrollView
                    android.view.ViewGroup$LayoutParams r13 = r13.getLayoutParams()
                    android.widget.LinearLayout$LayoutParams r13 = (android.widget.LinearLayout.LayoutParams) r13
                    org.telegram.ui.ActionBar.AlertDialog r1 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.view.View r1 = r1.customView
                    if (r1 == 0) goto L_0x01f2
                    org.telegram.ui.ActionBar.AlertDialog r1 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.TextView r1 = r1.titleTextView
                    if (r1 != 0) goto L_0x01e0
                    org.telegram.ui.ActionBar.AlertDialog r1 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.TextView r1 = r1.messageTextView
                    int r1 = r1.getVisibility()
                    if (r1 != r5) goto L_0x01e0
                    org.telegram.ui.ActionBar.AlertDialog r1 = org.telegram.ui.ActionBar.AlertDialog.this
                    java.lang.CharSequence[] r1 = r1.items
                    if (r1 != 0) goto L_0x01e0
                    r1 = 1098907648(0x41800000, float:16.0)
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    goto L_0x01e1
                L_0x01e0:
                    r1 = 0
                L_0x01e1:
                    r13.topMargin = r1
                    org.telegram.ui.ActionBar.AlertDialog r1 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.FrameLayout r1 = r1.buttonsLayout
                    if (r1 != 0) goto L_0x01ee
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r8)
                    goto L_0x01ef
                L_0x01ee:
                    r1 = 0
                L_0x01ef:
                    r13.bottomMargin = r1
                    goto L_0x0243
                L_0x01f2:
                    org.telegram.ui.ActionBar.AlertDialog r1 = org.telegram.ui.ActionBar.AlertDialog.this
                    java.lang.CharSequence[] r1 = r1.items
                    if (r1 == 0) goto L_0x021d
                    org.telegram.ui.ActionBar.AlertDialog r1 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.TextView r1 = r1.titleTextView
                    if (r1 != 0) goto L_0x0213
                    org.telegram.ui.ActionBar.AlertDialog r1 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.TextView r1 = r1.messageTextView
                    int r1 = r1.getVisibility()
                    if (r1 != r5) goto L_0x0213
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r8)
                    goto L_0x0214
                L_0x0213:
                    r1 = 0
                L_0x0214:
                    r13.topMargin = r1
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r8)
                    r13.bottomMargin = r1
                    goto L_0x0243
                L_0x021d:
                    org.telegram.ui.ActionBar.AlertDialog r1 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.TextView r1 = r1.messageTextView
                    int r1 = r1.getVisibility()
                    if (r1 != 0) goto L_0x0243
                    org.telegram.ui.ActionBar.AlertDialog r1 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.TextView r1 = r1.titleTextView
                    if (r1 != 0) goto L_0x0238
                    r1 = 1100480512(0x41980000, float:19.0)
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    goto L_0x0239
                L_0x0238:
                    r1 = 0
                L_0x0239:
                    r13.topMargin = r1
                    r1 = 1101004800(0x41a00000, float:20.0)
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    r13.bottomMargin = r1
                L_0x0243:
                    int r1 = r13.bottomMargin
                    int r13 = r13.topMargin
                    int r1 = r1 + r13
                    int r2 = r2 - r1
                    org.telegram.ui.ActionBar.AlertDialog r13 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.ScrollView r13 = r13.contentScrollView
                    int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r7)
                    r13.measure(r4, r1)
                    org.telegram.ui.ActionBar.AlertDialog r13 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.ScrollView r13 = r13.contentScrollView
                    int r13 = r13.getMeasuredHeight()
                    int r2 = r2 - r13
                    goto L_0x0335
                L_0x0263:
                    org.telegram.ui.ActionBar.AlertDialog r13 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.FrameLayout r13 = r13.progressViewContainer
                    if (r13 == 0) goto L_0x0296
                    org.telegram.ui.ActionBar.AlertDialog r13 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.FrameLayout r13 = r13.progressViewContainer
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r7)
                    r13.measure(r3, r4)
                    org.telegram.ui.ActionBar.AlertDialog r13 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.FrameLayout r13 = r13.progressViewContainer
                    android.view.ViewGroup$LayoutParams r13 = r13.getLayoutParams()
                    android.widget.LinearLayout$LayoutParams r13 = (android.widget.LinearLayout.LayoutParams) r13
                    org.telegram.ui.ActionBar.AlertDialog r4 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.FrameLayout r4 = r4.progressViewContainer
                    int r4 = r4.getMeasuredHeight()
                    int r5 = r13.bottomMargin
                    int r4 = r4 + r5
                    int r13 = r13.topMargin
                L_0x0293:
                    int r4 = r4 + r13
                    int r2 = r2 - r4
                    goto L_0x02d3
                L_0x0296:
                    org.telegram.ui.ActionBar.AlertDialog r13 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.TextView r13 = r13.messageTextView
                    if (r13 == 0) goto L_0x02d3
                    org.telegram.ui.ActionBar.AlertDialog r13 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.TextView r13 = r13.messageTextView
                    int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r7)
                    r13.measure(r3, r4)
                    org.telegram.ui.ActionBar.AlertDialog r13 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.TextView r13 = r13.messageTextView
                    int r13 = r13.getVisibility()
                    if (r13 == r5) goto L_0x02d3
                    org.telegram.ui.ActionBar.AlertDialog r13 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.TextView r13 = r13.messageTextView
                    android.view.ViewGroup$LayoutParams r13 = r13.getLayoutParams()
                    android.widget.LinearLayout$LayoutParams r13 = (android.widget.LinearLayout.LayoutParams) r13
                    org.telegram.ui.ActionBar.AlertDialog r4 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.TextView r4 = r4.messageTextView
                    int r4 = r4.getMeasuredHeight()
                    int r5 = r13.bottomMargin
                    int r4 = r4 + r5
                    int r13 = r13.topMargin
                    goto L_0x0293
                L_0x02d3:
                    org.telegram.ui.ActionBar.AlertDialog r13 = org.telegram.ui.ActionBar.AlertDialog.this
                    org.telegram.ui.Components.LineProgressView r13 = r13.lineProgressView
                    if (r13 == 0) goto L_0x0335
                    org.telegram.ui.ActionBar.AlertDialog r13 = org.telegram.ui.ActionBar.AlertDialog.this
                    org.telegram.ui.Components.LineProgressView r13 = r13.lineProgressView
                    r4 = 1082130432(0x40800000, float:4.0)
                    int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                    int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r1)
                    r13.measure(r3, r1)
                    org.telegram.ui.ActionBar.AlertDialog r13 = org.telegram.ui.ActionBar.AlertDialog.this
                    org.telegram.ui.Components.LineProgressView r13 = r13.lineProgressView
                    android.view.ViewGroup$LayoutParams r13 = r13.getLayoutParams()
                    android.widget.LinearLayout$LayoutParams r13 = (android.widget.LinearLayout.LayoutParams) r13
                    org.telegram.ui.ActionBar.AlertDialog r1 = org.telegram.ui.ActionBar.AlertDialog.this
                    org.telegram.ui.Components.LineProgressView r1 = r1.lineProgressView
                    int r1 = r1.getMeasuredHeight()
                    int r4 = r13.bottomMargin
                    int r1 = r1 + r4
                    int r13 = r13.topMargin
                    int r1 = r1 + r13
                    int r2 = r2 - r1
                    org.telegram.ui.ActionBar.AlertDialog r13 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.TextView r13 = r13.lineProgressViewPercent
                    int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r7)
                    r13.measure(r3, r1)
                    org.telegram.ui.ActionBar.AlertDialog r13 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.TextView r13 = r13.lineProgressViewPercent
                    android.view.ViewGroup$LayoutParams r13 = r13.getLayoutParams()
                    android.widget.LinearLayout$LayoutParams r13 = (android.widget.LinearLayout.LayoutParams) r13
                    org.telegram.ui.ActionBar.AlertDialog r1 = org.telegram.ui.ActionBar.AlertDialog.this
                    android.widget.TextView r1 = r1.lineProgressViewPercent
                    int r1 = r1.getMeasuredHeight()
                    int r3 = r13.bottomMargin
                    int r1 = r1 + r3
                    int r13 = r13.topMargin
                    int r1 = r1 + r13
                    int r2 = r2 - r1
                L_0x0335:
                    int r0 = r0 - r2
                    int r13 = r11.getPaddingTop()
                    int r0 = r0 + r13
                    int r13 = r11.getPaddingBottom()
                    int r0 = r0 + r13
                    r11.setMeasuredDimension(r12, r0)
                    r11.inLayout = r6
                    org.telegram.ui.ActionBar.AlertDialog r12 = org.telegram.ui.ActionBar.AlertDialog.this
                    int r12 = r12.lastScreenWidth
                    android.graphics.Point r13 = org.telegram.messenger.AndroidUtilities.displaySize
                    int r13 = r13.x
                    if (r12 == r13) goto L_0x0359
                    org.telegram.ui.ActionBar.-$$Lambda$AlertDialog$1$2il1lPevBw8X_3FhfSjXOpGmbaM r12 = new org.telegram.ui.ActionBar.-$$Lambda$AlertDialog$1$2il1lPevBw8X_3FhfSjXOpGmbaM
                    r12.<init>()
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r12)
                L_0x0359:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.AlertDialog.AnonymousClass1.onMeasure(int, int):void");
            }

            public /* synthetic */ void lambda$onMeasure$0$AlertDialog$1() {
                int i;
                int unused = AlertDialog.this.lastScreenWidth = AndroidUtilities.displaySize.x;
                int dp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(56.0f);
                if (!AndroidUtilities.isTablet()) {
                    i = AndroidUtilities.dp(356.0f);
                } else if (AndroidUtilities.isSmallTablet()) {
                    i = AndroidUtilities.dp(446.0f);
                } else {
                    i = AndroidUtilities.dp(496.0f);
                }
                Window window = AlertDialog.this.getWindow();
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(window.getAttributes());
                layoutParams.width = Math.min(i, dp) + AlertDialog.this.backgroundPaddings.left + AlertDialog.this.backgroundPaddings.right;
                window.setAttributes(layoutParams);
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                if (AlertDialog.this.progressViewStyle == 3) {
                    int measuredWidth = ((i3 - i) - AlertDialog.this.progressViewContainer.getMeasuredWidth()) / 2;
                    int measuredHeight = ((i4 - i2) - AlertDialog.this.progressViewContainer.getMeasuredHeight()) / 2;
                    AlertDialog.this.progressViewContainer.layout(measuredWidth, measuredHeight, AlertDialog.this.progressViewContainer.getMeasuredWidth() + measuredWidth, AlertDialog.this.progressViewContainer.getMeasuredHeight() + measuredHeight);
                } else if (AlertDialog.this.contentScrollView != null) {
                    if (AlertDialog.this.onScrollChangedListener == null) {
                        ViewTreeObserver.OnScrollChangedListener unused = AlertDialog.this.onScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
                            public final void onScrollChanged() {
                                AlertDialog.AnonymousClass1.this.lambda$onLayout$1$AlertDialog$1();
                            }
                        };
                        AlertDialog.this.contentScrollView.getViewTreeObserver().addOnScrollChangedListener(AlertDialog.this.onScrollChangedListener);
                    }
                    AlertDialog.this.onScrollChangedListener.onScrollChanged();
                }
            }

            public /* synthetic */ void lambda$onLayout$1$AlertDialog$1() {
                AlertDialog alertDialog = AlertDialog.this;
                boolean z = false;
                alertDialog.runShadowAnimation(0, alertDialog.titleTextView != null && AlertDialog.this.contentScrollView.getScrollY() > AlertDialog.this.scrollContainer.getTop());
                AlertDialog alertDialog2 = AlertDialog.this;
                if (alertDialog2.buttonsLayout != null && alertDialog2.contentScrollView.getScrollY() + AlertDialog.this.contentScrollView.getHeight() < AlertDialog.this.scrollContainer.getBottom()) {
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
        };
        r1.setOrientation(1);
        if (this.progressViewStyle == 3) {
            r1.setBackgroundDrawable((Drawable) null);
        } else {
            r1.setBackgroundDrawable(this.shadowDrawable);
        }
        r1.setFitsSystemWindows(Build.VERSION.SDK_INT >= 21);
        setContentView(r1);
        boolean z = (this.positiveButtonText == null && this.negativeButtonText == null && this.neutralButtonText == null) ? false : true;
        if (!(this.topResId == 0 && this.topAnimationId == 0 && this.topDrawable == null)) {
            this.topImageView = new RLottieImageView(getContext());
            Drawable drawable = this.topDrawable;
            if (drawable != null) {
                this.topImageView.setImageDrawable(drawable);
            } else {
                int i2 = this.topResId;
                if (i2 != 0) {
                    this.topImageView.setImageResource(i2);
                } else {
                    this.topImageView.setAutoRepeat(true);
                    this.topImageView.setAnimation(this.topAnimationId, 94, 94);
                    this.topImageView.playAnimation();
                }
            }
            this.topImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.topImageView.setBackgroundDrawable(getContext().getResources().getDrawable(NUM));
            this.topImageView.getBackground().setColorFilter(new PorterDuffColorFilter(this.topBackgroundColor, PorterDuff.Mode.MULTIPLY));
            this.topImageView.setPadding(0, 0, 0, 0);
            r1.addView(this.topImageView, LayoutHelper.createLinear(-1, this.topHeight, 51, -8, -8, 0, 0));
        }
        if (this.title != null) {
            this.titleContainer = new FrameLayout(getContext());
            r1.addView(this.titleContainer, LayoutHelper.createLinear(-2, -2, 24.0f, 0.0f, 24.0f, 0.0f));
            this.titleTextView = new TextView(getContext());
            this.titleTextView.setText(this.title);
            this.titleTextView.setTextColor(getThemeColor("dialogTextBlack"));
            this.titleTextView.setTextSize(1, 20.0f);
            this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.titleContainer.addView(this.titleTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 0.0f, 19.0f, 0.0f, (float) (this.subtitle != null ? 2 : this.items != null ? 14 : 10)));
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
            r1.addView(this.subtitleTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 24, 0, 24, this.items != null ? 14 : 10));
        }
        if (this.progressViewStyle == 0) {
            this.shadow[0] = (BitmapDrawable) getContext().getResources().getDrawable(NUM).mutate();
            this.shadow[1] = (BitmapDrawable) getContext().getResources().getDrawable(NUM).mutate();
            this.shadow[0].setAlpha(0);
            this.shadow[1].setAlpha(0);
            this.shadow[0].setCallback(this);
            this.shadow[1].setCallback(this);
            this.contentScrollView = new ScrollView(getContext()) {
                /* access modifiers changed from: protected */
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
            r1.addView(this.contentScrollView, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 0.0f));
            this.scrollContainer = new LinearLayout(getContext());
            this.scrollContainer.setOrientation(1);
            this.contentScrollView.addView(this.scrollContainer, new FrameLayout.LayoutParams(-1, -2));
        }
        this.messageTextView = new TextView(getContext());
        this.messageTextView.setTextColor(getThemeColor("dialogTextBlack"));
        this.messageTextView.setTextSize(1, 16.0f);
        this.messageTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        this.messageTextView.setLinkTextColor(getThemeColor("dialogTextLink"));
        if (!this.messageTextViewClickable) {
            this.messageTextView.setClickable(false);
            this.messageTextView.setEnabled(false);
        }
        this.messageTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        int i3 = this.progressViewStyle;
        if (i3 == 1) {
            this.progressViewContainer = new FrameLayout(getContext());
            r1.addView(this.progressViewContainer, LayoutHelper.createLinear(-1, 44, 51, 23, this.title == null ? 24 : 0, 23, 24));
            RadialProgressView radialProgressView = new RadialProgressView(getContext());
            radialProgressView.setProgressColor(getThemeColor("dialogProgressCircle"));
            this.progressViewContainer.addView(radialProgressView, LayoutHelper.createFrame(44, 44, (LocaleController.isRTL ? 5 : 3) | 48));
            this.messageTextView.setLines(1);
            this.messageTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.progressViewContainer.addView(this.messageTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, (float) (LocaleController.isRTL ? 0 : 62), 0.0f, (float) (LocaleController.isRTL ? 62 : 0), 0.0f));
        } else if (i3 == 2) {
            r1.addView(this.messageTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 24, this.title == null ? 19 : 0, 24, 20));
            this.lineProgressView = new LineProgressView(getContext());
            this.lineProgressView.setProgress(((float) this.currentProgress) / 100.0f, false);
            this.lineProgressView.setProgressColor(getThemeColor("dialogLineProgress"));
            this.lineProgressView.setBackColor(getThemeColor("dialogLineProgressBackground"));
            r1.addView(this.lineProgressView, LayoutHelper.createLinear(-1, 4, 19, 24, 0, 24, 0));
            this.lineProgressViewPercent = new TextView(getContext());
            this.lineProgressViewPercent.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.lineProgressViewPercent.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.lineProgressViewPercent.setTextColor(getThemeColor("dialogTextGray2"));
            this.lineProgressViewPercent.setTextSize(1, 14.0f);
            r1.addView(this.lineProgressViewPercent, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 23, 4, 23, 24));
            updateLineProgressTextView();
        } else if (i3 == 3) {
            setCanceledOnTouchOutside(false);
            setCancelable(false);
            this.progressViewContainer = new FrameLayout(getContext());
            this.progressViewContainer.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), Theme.getColor("dialog_inlineProgressBackground")));
            r1.addView(this.progressViewContainer, LayoutHelper.createLinear(86, 86, 17));
            RadialProgressView radialProgressView2 = new RadialProgressView(getContext());
            radialProgressView2.setProgressColor(getThemeColor("dialog_inlineProgress"));
            this.progressViewContainer.addView(radialProgressView2, LayoutHelper.createLinear(86, 86));
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
            int i4 = 0;
            while (true) {
                CharSequence[] charSequenceArr = this.items;
                if (i4 >= charSequenceArr.length) {
                    break;
                }
                if (charSequenceArr[i4] != null) {
                    AlertDialogCell alertDialogCell = new AlertDialogCell(getContext());
                    CharSequence charSequence = this.items[i4];
                    int[] iArr = this.itemIcons;
                    alertDialogCell.setTextAndIcon(charSequence, iArr != null ? iArr[i4] : 0);
                    alertDialogCell.setTag(Integer.valueOf(i4));
                    this.itemViews.add(alertDialogCell);
                    this.scrollContainer.addView(alertDialogCell, LayoutHelper.createLinear(-1, 50));
                    alertDialogCell.setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            AlertDialog.this.lambda$onCreate$0$AlertDialog(view);
                        }
                    });
                }
                i4++;
            }
        }
        View view = this.customView;
        if (view != null) {
            if (view.getParent() != null) {
                ((ViewGroup) this.customView.getParent()).removeView(this.customView);
            }
            this.scrollContainer.addView(this.customView, LayoutHelper.createLinear(-1, this.customViewHeight));
        }
        if (z) {
            this.buttonsLayout = new FrameLayout(getContext()) {
                /* access modifiers changed from: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    int i5;
                    int i6;
                    int childCount = getChildCount();
                    int i7 = i3 - i;
                    View view = null;
                    for (int i8 = 0; i8 < childCount; i8++) {
                        View childAt = getChildAt(i8);
                        Integer num = (Integer) childAt.getTag();
                        if (num == null) {
                            int measuredWidth = childAt.getMeasuredWidth();
                            int measuredHeight = childAt.getMeasuredHeight();
                            if (view != null) {
                                i6 = view.getLeft() + ((view.getMeasuredWidth() - measuredWidth) / 2);
                                i5 = view.getTop() + ((view.getMeasuredHeight() - measuredHeight) / 2);
                            } else {
                                i6 = 0;
                                i5 = 0;
                            }
                            childAt.layout(i6, i5, measuredWidth + i6, measuredHeight + i5);
                        } else if (num.intValue() == -1) {
                            if (LocaleController.isRTL) {
                                childAt.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + childAt.getMeasuredWidth(), getPaddingTop() + childAt.getMeasuredHeight());
                            } else {
                                childAt.layout((i7 - getPaddingRight()) - childAt.getMeasuredWidth(), getPaddingTop(), i7 - getPaddingRight(), getPaddingTop() + childAt.getMeasuredHeight());
                            }
                            view = childAt;
                        } else if (num.intValue() == -2) {
                            if (LocaleController.isRTL) {
                                int paddingLeft = getPaddingLeft();
                                if (view != null) {
                                    paddingLeft += view.getMeasuredWidth() + AndroidUtilities.dp(8.0f);
                                }
                                childAt.layout(paddingLeft, getPaddingTop(), childAt.getMeasuredWidth() + paddingLeft, getPaddingTop() + childAt.getMeasuredHeight());
                            } else {
                                int paddingRight = (i7 - getPaddingRight()) - childAt.getMeasuredWidth();
                                if (view != null) {
                                    paddingRight -= view.getMeasuredWidth() + AndroidUtilities.dp(8.0f);
                                }
                                childAt.layout(paddingRight, getPaddingTop(), childAt.getMeasuredWidth() + paddingRight, getPaddingTop() + childAt.getMeasuredHeight());
                            }
                        } else if (num.intValue() == -3) {
                            if (LocaleController.isRTL) {
                                childAt.layout((i7 - getPaddingRight()) - childAt.getMeasuredWidth(), getPaddingTop(), i7 - getPaddingRight(), getPaddingTop() + childAt.getMeasuredHeight());
                            } else {
                                childAt.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + childAt.getMeasuredWidth(), getPaddingTop() + childAt.getMeasuredHeight());
                            }
                        }
                    }
                }

                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    super.onMeasure(i, i2);
                    int measuredWidth = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
                    int childCount = getChildCount();
                    int i3 = 0;
                    for (int i4 = 0; i4 < childCount; i4++) {
                        View childAt = getChildAt(i4);
                        if ((childAt instanceof TextView) && childAt.getTag() != null) {
                            i3 += childAt.getMeasuredWidth();
                        }
                    }
                    if (i3 > measuredWidth) {
                        View findViewWithTag = findViewWithTag(-2);
                        View findViewWithTag2 = findViewWithTag(-3);
                        if (findViewWithTag != null && findViewWithTag2 != null) {
                            if (findViewWithTag.getMeasuredWidth() < findViewWithTag2.getMeasuredWidth()) {
                                findViewWithTag2.measure(View.MeasureSpec.makeMeasureSpec(findViewWithTag2.getMeasuredWidth() - (i3 - measuredWidth), NUM), View.MeasureSpec.makeMeasureSpec(findViewWithTag2.getMeasuredHeight(), NUM));
                            } else {
                                findViewWithTag.measure(View.MeasureSpec.makeMeasureSpec(findViewWithTag.getMeasuredWidth() - (i3 - measuredWidth), NUM), View.MeasureSpec.makeMeasureSpec(findViewWithTag.getMeasuredHeight(), NUM));
                            }
                        }
                    }
                }
            };
            this.buttonsLayout.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            r1.addView(this.buttonsLayout, LayoutHelper.createLinear(-1, 52));
            if (this.positiveButtonText != null) {
                AnonymousClass4 r12 = new TextView(getContext()) {
                    public void setEnabled(boolean z) {
                        super.setEnabled(z);
                        setAlpha(z ? 1.0f : 0.5f);
                    }

                    public void setTextColor(int i) {
                        super.setTextColor(i);
                        setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(i));
                    }
                };
                r12.setMinWidth(AndroidUtilities.dp(64.0f));
                r12.setTag(-1);
                r12.setTextSize(1, 14.0f);
                r12.setTextColor(getThemeColor("dialogButton"));
                r12.setGravity(17);
                r12.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                r12.setText(this.positiveButtonText.toString().toUpperCase());
                r12.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(getThemeColor("dialogButton")));
                r12.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                this.buttonsLayout.addView(r12, LayoutHelper.createFrame(-2, 36, 53));
                r12.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        AlertDialog.this.lambda$onCreate$1$AlertDialog(view);
                    }
                });
            }
            if (this.negativeButtonText != null) {
                AnonymousClass5 r13 = new TextView(getContext()) {
                    public void setEnabled(boolean z) {
                        super.setEnabled(z);
                        setAlpha(z ? 1.0f : 0.5f);
                    }

                    public void setTextColor(int i) {
                        super.setTextColor(i);
                        setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(i));
                    }
                };
                r13.setMinWidth(AndroidUtilities.dp(64.0f));
                r13.setTag(-2);
                r13.setTextSize(1, 14.0f);
                r13.setTextColor(getThemeColor("dialogButton"));
                r13.setGravity(17);
                r13.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                r13.setEllipsize(TextUtils.TruncateAt.END);
                r13.setSingleLine(true);
                r13.setText(this.negativeButtonText.toString().toUpperCase());
                r13.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(getThemeColor("dialogButton")));
                r13.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                this.buttonsLayout.addView(r13, LayoutHelper.createFrame(-2, 36, 53));
                r13.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        AlertDialog.this.lambda$onCreate$2$AlertDialog(view);
                    }
                });
            }
            if (this.neutralButtonText != null) {
                AnonymousClass6 r14 = new TextView(getContext()) {
                    public void setEnabled(boolean z) {
                        super.setEnabled(z);
                        setAlpha(z ? 1.0f : 0.5f);
                    }

                    public void setTextColor(int i) {
                        super.setTextColor(i);
                        setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(i));
                    }
                };
                r14.setMinWidth(AndroidUtilities.dp(64.0f));
                r14.setTag(-3);
                r14.setTextSize(1, 14.0f);
                r14.setTextColor(getThemeColor("dialogButton"));
                r14.setGravity(17);
                r14.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                r14.setEllipsize(TextUtils.TruncateAt.END);
                r14.setSingleLine(true);
                r14.setText(this.neutralButtonText.toString().toUpperCase());
                r14.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(getThemeColor("dialogButton")));
                r14.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                this.buttonsLayout.addView(r14, LayoutHelper.createFrame(-2, 36, 51));
                r14.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        AlertDialog.this.lambda$onCreate$3$AlertDialog(view);
                    }
                });
            }
        }
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(window.getAttributes());
        if (this.progressViewStyle == 3) {
            layoutParams.width = -1;
        } else {
            layoutParams.dimAmount = 0.6f;
            layoutParams.flags |= 2;
            int i5 = AndroidUtilities.displaySize.x;
            this.lastScreenWidth = i5;
            int dp = i5 - AndroidUtilities.dp(48.0f);
            if (!AndroidUtilities.isTablet()) {
                i = AndroidUtilities.dp(356.0f);
            } else if (AndroidUtilities.isSmallTablet()) {
                i = AndroidUtilities.dp(446.0f);
            } else {
                i = AndroidUtilities.dp(496.0f);
            }
            int min = Math.min(i, dp);
            Rect rect = this.backgroundPaddings;
            layoutParams.width = min + rect.left + rect.right;
        }
        View view2 = this.customView;
        if (view2 == null || !canTextInput(view2)) {
            layoutParams.flags |= 131072;
        } else {
            layoutParams.softInputMode = 4;
        }
        if (Build.VERSION.SDK_INT >= 28) {
            layoutParams.layoutInDisplayCutoutMode = 0;
        }
        window.setAttributes(layoutParams);
    }

    public /* synthetic */ void lambda$onCreate$0$AlertDialog(View view) {
        DialogInterface.OnClickListener onClickListener2 = this.onClickListener;
        if (onClickListener2 != null) {
            onClickListener2.onClick(this, ((Integer) view.getTag()).intValue());
        }
        dismiss();
    }

    public /* synthetic */ void lambda$onCreate$1$AlertDialog(View view) {
        DialogInterface.OnClickListener onClickListener2 = this.positiveButtonListener;
        if (onClickListener2 != null) {
            onClickListener2.onClick(this, -1);
        }
        if (this.dismissDialogByButtons) {
            dismiss();
        }
    }

    public /* synthetic */ void lambda$onCreate$2$AlertDialog(View view) {
        DialogInterface.OnClickListener onClickListener2 = this.negativeButtonListener;
        if (onClickListener2 != null) {
            onClickListener2.onClick(this, -2);
        }
        if (this.dismissDialogByButtons) {
            cancel();
        }
    }

    public /* synthetic */ void lambda$onCreate$3$AlertDialog(View view) {
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

    /* access modifiers changed from: private */
    public void showCancelAlert() {
        if (this.canCacnel && this.cancelDialog == null) {
            Builder builder = new Builder(getContext());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.getString("StopLoading", NUM));
            builder.setPositiveButton(LocaleController.getString("WaitMore", NUM), (DialogInterface.OnClickListener) null);
            builder.setNegativeButton(LocaleController.getString("Stop", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    AlertDialog.this.lambda$showCancelAlert$4$AlertDialog(dialogInterface, i);
                }
            });
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    AlertDialog.this.lambda$showCancelAlert$5$AlertDialog(dialogInterface);
                }
            });
            try {
                this.cancelDialog = builder.show();
            } catch (Exception unused) {
            }
        }
    }

    public /* synthetic */ void lambda$showCancelAlert$4$AlertDialog(DialogInterface dialogInterface, int i) {
        DialogInterface.OnCancelListener onCancelListener2 = this.onCancelListener;
        if (onCancelListener2 != null) {
            onCancelListener2.onCancel(this);
        }
        dismiss();
    }

    public /* synthetic */ void lambda$showCancelAlert$5$AlertDialog(DialogInterface dialogInterface) {
        this.cancelDialog = null;
    }

    /* access modifiers changed from: private */
    public void runShadowAnimation(final int i, boolean z) {
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
                BitmapDrawable bitmapDrawable = bitmapDrawableArr[i];
                int[] iArr = new int[1];
                iArr[0] = z ? 255 : 0;
                animatorArr[0] = ObjectAnimator.ofInt(bitmapDrawable, "alpha", iArr);
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
                FileLog.e((Throwable) e);
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
        LineProgressView lineProgressView2 = this.lineProgressView;
        if (lineProgressView2 != null) {
            lineProgressView2.setProgress(((float) i) / 100.0f, true);
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
        AndroidUtilities.cancelRunOnUIThread(this.showRunnable);
    }

    public void setCanceledOnTouchOutside(boolean z) {
        super.setCanceledOnTouchOutside(z);
    }

    public void setTopImage(int i, int i2) {
        this.topResId = i;
        this.topBackgroundColor = i2;
    }

    public void setTopAnimation(int i, int i2) {
        this.topAnimationId = i;
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

    public void setPositiveButton(CharSequence charSequence, DialogInterface.OnClickListener onClickListener2) {
        this.positiveButtonText = charSequence;
        this.positiveButtonListener = onClickListener2;
    }

    public void setNegativeButton(CharSequence charSequence, DialogInterface.OnClickListener onClickListener2) {
        this.negativeButtonText = charSequence;
        this.negativeButtonListener = onClickListener2;
    }

    public void setNeutralButton(CharSequence charSequence, DialogInterface.OnClickListener onClickListener2) {
        this.neutralButtonText = charSequence;
        this.neutralButtonListener = onClickListener2;
    }

    public void setItemColor(int i, int i2, int i3) {
        if (i >= 0 && i < this.itemViews.size()) {
            AlertDialogCell alertDialogCell = this.itemViews.get(i);
            alertDialogCell.textView.setTextColor(i2);
            alertDialogCell.imageView.setColorFilter(new PorterDuffColorFilter(i3, PorterDuff.Mode.MULTIPLY));
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
        if (!TextUtils.isEmpty(this.message)) {
            this.messageTextView.setText(this.message);
            this.messageTextView.setVisibility(0);
            return;
        }
        this.messageTextView.setVisibility(8);
    }

    public void setMessageTextViewClickable(boolean z) {
        this.messageTextViewClickable = z;
    }

    public void setButton(int i, CharSequence charSequence, DialogInterface.OnClickListener onClickListener2) {
        if (i == -3) {
            this.neutralButtonText = charSequence;
            this.neutralButtonListener = onClickListener2;
        } else if (i == -2) {
            this.negativeButtonText = charSequence;
            this.negativeButtonListener = onClickListener2;
        } else if (i == -1) {
            this.positiveButtonText = charSequence;
            this.positiveButtonListener = onClickListener2;
        }
    }

    public View getButton(int i) {
        FrameLayout frameLayout = this.buttonsLayout;
        if (frameLayout != null) {
            return frameLayout.findViewWithTag(Integer.valueOf(i));
        }
        return null;
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

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener2) {
        this.onCancelListener = onCancelListener2;
        super.setOnCancelListener(onCancelListener2);
    }

    public void setPositiveButtonListener(DialogInterface.OnClickListener onClickListener2) {
        this.positiveButtonListener = onClickListener2;
    }

    /* access modifiers changed from: protected */
    public int getThemeColor(String str) {
        return Theme.getColor(str);
    }

    public void showDelayed(long j) {
        AndroidUtilities.runOnUIThread(this.showRunnable, j);
    }

    public static class Builder {
        private AlertDialog alertDialog;

        protected Builder(AlertDialog alertDialog2) {
            this.alertDialog = alertDialog2;
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

        public Builder setItems(CharSequence[] charSequenceArr, DialogInterface.OnClickListener onClickListener) {
            CharSequence[] unused = this.alertDialog.items = charSequenceArr;
            DialogInterface.OnClickListener unused2 = this.alertDialog.onClickListener = onClickListener;
            return this;
        }

        public Builder setItems(CharSequence[] charSequenceArr, int[] iArr, DialogInterface.OnClickListener onClickListener) {
            CharSequence[] unused = this.alertDialog.items = charSequenceArr;
            int[] unused2 = this.alertDialog.itemIcons = iArr;
            DialogInterface.OnClickListener unused3 = this.alertDialog.onClickListener = onClickListener;
            return this;
        }

        public Builder setView(View view) {
            return setView(view, -2);
        }

        public Builder setView(View view, int i) {
            View unused = this.alertDialog.customView = view;
            int unused2 = this.alertDialog.customViewHeight = i;
            return this;
        }

        public Builder setTitle(CharSequence charSequence) {
            CharSequence unused = this.alertDialog.title = charSequence;
            return this;
        }

        public Builder setSubtitle(CharSequence charSequence) {
            CharSequence unused = this.alertDialog.subtitle = charSequence;
            return this;
        }

        public Builder setTopImage(int i, int i2) {
            int unused = this.alertDialog.topResId = i;
            int unused2 = this.alertDialog.topBackgroundColor = i2;
            return this;
        }

        public Builder setTopAnimation(int i, int i2) {
            int unused = this.alertDialog.topAnimationId = i;
            int unused2 = this.alertDialog.topBackgroundColor = i2;
            return this;
        }

        public Builder setTopImage(Drawable drawable, int i) {
            Drawable unused = this.alertDialog.topDrawable = drawable;
            int unused2 = this.alertDialog.topBackgroundColor = i;
            return this;
        }

        public Builder setMessage(CharSequence charSequence) {
            CharSequence unused = this.alertDialog.message = charSequence;
            return this;
        }

        public Builder setPositiveButton(CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
            CharSequence unused = this.alertDialog.positiveButtonText = charSequence;
            DialogInterface.OnClickListener unused2 = this.alertDialog.positiveButtonListener = onClickListener;
            return this;
        }

        public Builder setNegativeButton(CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
            CharSequence unused = this.alertDialog.negativeButtonText = charSequence;
            DialogInterface.OnClickListener unused2 = this.alertDialog.negativeButtonListener = onClickListener;
            return this;
        }

        public Builder setNeutralButton(CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
            CharSequence unused = this.alertDialog.neutralButtonText = charSequence;
            DialogInterface.OnClickListener unused2 = this.alertDialog.neutralButtonListener = onClickListener;
            return this;
        }

        public Builder setOnBackButtonListener(DialogInterface.OnClickListener onClickListener) {
            DialogInterface.OnClickListener unused = this.alertDialog.onBackButtonListener = onClickListener;
            return this;
        }

        public Builder setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
            this.alertDialog.setOnCancelListener(onCancelListener);
            return this;
        }

        public Builder setCustomViewOffset(int i) {
            int unused = this.alertDialog.customViewOffset = i;
            return this;
        }

        public Builder setMessageTextViewClickable(boolean z) {
            boolean unused = this.alertDialog.messageTextViewClickable = z;
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
    }
}

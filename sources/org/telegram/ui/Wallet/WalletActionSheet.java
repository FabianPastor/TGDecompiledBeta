package org.telegram.ui.Wallet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.widget.NestedScrollView;
import drinkless.org.ton.TonApi;
import java.net.URLEncoder;
import javax.crypto.Cipher;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.TonController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.PollEditTextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.BiometricPromtHelper;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.TypefaceSpan;

public class WalletActionSheet extends BottomSheet {
    private static final int MAX_COMMENT_LENGTH = 500;
    public static final int SEND_ACTIVITY_RESULT_CODE = 33;
    public static final int TYPE_INVOICE = 1;
    public static final int TYPE_SEND = 0;
    public static final int TYPE_TRANSACTION = 2;
    /* access modifiers changed from: private */
    public ActionBar actionBar;
    /* access modifiers changed from: private */
    public AnimatorSet actionBarAnimation;
    /* access modifiers changed from: private */
    public View actionBarShadow;
    /* access modifiers changed from: private */
    public int amountHeaderRow;
    /* access modifiers changed from: private */
    public int amountRow;
    /* access modifiers changed from: private */
    public long amountValue;
    /* access modifiers changed from: private */
    public Paint backgroundPaint = new Paint(1);
    /* access modifiers changed from: private */
    public int balanceRow;
    private BiometricPromtHelper biometricPromtHelper;
    /* access modifiers changed from: private */
    public int commentHeaderRow;
    /* access modifiers changed from: private */
    public int commentRow;
    /* access modifiers changed from: private */
    public String commentString = "";
    /* access modifiers changed from: private */
    public long currentBalance = -1;
    /* access modifiers changed from: private */
    public long currentDate;
    /* access modifiers changed from: private */
    public long currentStorageFee;
    /* access modifiers changed from: private */
    public long currentTransactionFee;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public int dateHeaderRow;
    /* access modifiers changed from: private */
    public int dateRow;
    /* access modifiers changed from: private */
    public WalletActionSheetDelegate delegate;
    /* access modifiers changed from: private */
    public Drawable gemDrawable;
    private boolean hasWalletInBack = true;
    /* access modifiers changed from: private */
    public boolean inLayout;
    /* access modifiers changed from: private */
    public int invoiceInfoRow;
    /* access modifiers changed from: private */
    public LinearLayout linearLayout;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    private BaseFragment parentFragment;
    /* access modifiers changed from: private */
    public int recipientHeaderRow;
    /* access modifiers changed from: private */
    public int recipientRow;
    /* access modifiers changed from: private */
    public String recipientString = "";
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    /* access modifiers changed from: private */
    public NestedScrollView scrollView;
    /* access modifiers changed from: private */
    public int sendBalanceRow;
    private View shadow;
    /* access modifiers changed from: private */
    public AnimatorSet shadowAnimation;
    /* access modifiers changed from: private */
    public int titleRow;
    private String walletAddress;
    /* access modifiers changed from: private */
    public boolean wasFirstAttach;

    public interface WalletActionSheetDelegate {

        /* renamed from: org.telegram.ui.Wallet.WalletActionSheet$WalletActionSheetDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$openInvoice(WalletActionSheetDelegate walletActionSheetDelegate, String str, long j) {
            }

            public static void $default$openQrReader(WalletActionSheetDelegate walletActionSheetDelegate) {
            }

            public static void $default$openSendToAddress(WalletActionSheetDelegate walletActionSheetDelegate, String str) {
            }
        }

        void openInvoice(String str, long j);

        void openQrReader();

        void openSendToAddress(String str);
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public void onResume() {
    }

    public static class ByteLengthFilter implements InputFilter {
        private final int mMax;

        public ByteLengthFilter(int i) {
            this.mMax = i;
        }

        public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
            int length = this.mMax - (spanned.toString().getBytes().length - (i4 - i3));
            if (length <= 0) {
                return "";
            }
            if (length >= i2 - i) {
                return null;
            }
            try {
                return new String(charSequence.toString().getBytes(), i, length + i, "UTF-8");
            } catch (Exception unused) {
                return "";
            }
        }

        public int getMax() {
            return this.mMax;
        }
    }

    private class TitleCell extends FrameLayout {
        private TextView titleView = new TextView(getContext());

        public TitleCell(Context context) {
            super(context);
            this.titleView.setLines(1);
            this.titleView.setSingleLine(true);
            this.titleView.setTextColor(Theme.getColor("dialogTextBlack"));
            this.titleView.setTextSize(1, 20.0f);
            this.titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.titleView.setPadding(AndroidUtilities.dp(22.0f), AndroidUtilities.dp(15.0f), AndroidUtilities.dp(22.0f), AndroidUtilities.dp(8.0f));
            this.titleView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            this.titleView.setGravity(16);
            addView(this.titleView, LayoutHelper.createFrame(-1, 60.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), NUM));
        }

        public void setText(String str) {
            this.titleView.setText(str);
        }
    }

    public class BalanceCell extends FrameLayout {
        private SimpleTextView valueTextView;
        private TextView yourBalanceTextView;

        public BalanceCell(Context context) {
            super(context);
            this.valueTextView = new SimpleTextView(context);
            this.valueTextView.setTextSize(30);
            this.valueTextView.setRightDrawable(NUM);
            this.valueTextView.setRightDrawableScale(0.8f);
            this.valueTextView.setDrawablePadding(AndroidUtilities.dp(7.0f));
            this.valueTextView.setGravity(1);
            this.valueTextView.setTextColor(Theme.getColor("dialogTextBlack"));
            this.valueTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            addView(this.valueTextView, LayoutHelper.createFrame(-1, -2.0f, 1, 0.0f, 18.0f, 0.0f, 0.0f));
            this.yourBalanceTextView = new TextView(context);
            this.yourBalanceTextView.setTextSize(1, 12.0f);
            this.yourBalanceTextView.setTextColor(Theme.getColor("dialogTextGray2"));
            this.yourBalanceTextView.setLineSpacing((float) AndroidUtilities.dp(4.0f), 1.0f);
            this.yourBalanceTextView.setGravity(1);
            addView(this.yourBalanceTextView, LayoutHelper.createFrame(-2, -2.0f, 1, 0.0f, 59.0f, 0.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM));
        }

        public void setBalance(long j, long j2, long j3) {
            StringBuilder sb = new StringBuilder();
            sb.append(j > 0 ? "+" : "");
            sb.append(TonController.formatCurrency(j));
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(sb.toString());
            int indexOf = TextUtils.indexOf(spannableStringBuilder, '.');
            if (indexOf >= 0) {
                spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), AndroidUtilities.dp(22.0f)), indexOf + 1, spannableStringBuilder.length(), 33);
            }
            this.valueTextView.setText(spannableStringBuilder);
            if (j2 == 0 && j3 == 0) {
                this.yourBalanceTextView.setVisibility(4);
                return;
            }
            StringBuilder sb2 = new StringBuilder();
            if (j3 != 0) {
                sb2.append(LocaleController.formatString("WalletTransactionFee", NUM, TonController.formatCurrency(j3)));
            }
            if (j2 != 0) {
                if (sb2.length() != 0) {
                    sb2.append(10);
                }
                sb2.append(LocaleController.formatString("WalletStorageFee", NUM, TonController.formatCurrency(j2)));
            }
            this.yourBalanceTextView.setText(sb2);
            this.yourBalanceTextView.setVisibility(0);
        }
    }

    private class SendAddressCell extends PollEditTextCell {
        private ImageView copyButton;
        private ImageView qrButton;
        final /* synthetic */ WalletActionSheet this$0;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public SendAddressCell(org.telegram.ui.Wallet.WalletActionSheet r19, android.content.Context r20) {
            /*
                r18 = this;
                r0 = r18
                r1 = r19
                r2 = r20
                r0.this$0 = r1
                r3 = 0
                r0.<init>(r2, r3)
                org.telegram.ui.Components.EditTextBoldCursor r3 = r18.getTextView()
                r4 = 0
                r3.setSingleLine(r4)
                r5 = 655505(0xa0091, float:9.18558E-40)
                r3.setInputType(r5)
                r5 = 2
                r3.setMinLines(r5)
                android.graphics.Typeface r5 = android.graphics.Typeface.DEFAULT
                r3.setTypeface(r5)
                boolean r5 = org.telegram.messenger.LocaleController.isRTL
                r6 = 5
                r7 = 3
                if (r5 == 0) goto L_0x002b
                r5 = 5
                goto L_0x002c
            L_0x002b:
                r5 = 3
            L_0x002c:
                r5 = r5 | 48
                r3.setGravity(r5)
                org.telegram.ui.Wallet.WalletActionSheet$SendAddressCell$1 r5 = new org.telegram.ui.Wallet.WalletActionSheet$SendAddressCell$1
                r5.<init>(r1)
                r0.addTextWatcher(r5)
                int r1 = r19.currentType
                r5 = 6
                java.lang.String r8 = "actionBarWhiteSelector"
                java.lang.String r9 = "windowBackgroundWhiteBlueHeader"
                r10 = 1095761920(0x41500000, float:13.0)
                if (r1 != 0) goto L_0x00ca
                r1 = 1
                android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r2, r1)
                r3.setBackground(r1)
                android.widget.ImageView r1 = new android.widget.ImageView
                r1.<init>(r2)
                r0.qrButton = r1
                android.widget.ImageView r1 = r0.qrButton
                r2 = 2131165942(0x7var_f6, float:1.7946115E38)
                r1.setImageResource(r2)
                android.widget.ImageView r1 = r0.qrButton
                android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
                r1.setScaleType(r2)
                android.widget.ImageView r1 = r0.qrButton
                android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
                android.graphics.PorterDuff$Mode r11 = android.graphics.PorterDuff.Mode.MULTIPLY
                r2.<init>(r9, r11)
                r1.setColorFilter(r2)
                android.widget.ImageView r1 = r0.qrButton
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2, r5)
                r1.setBackgroundDrawable(r2)
                android.widget.ImageView r1 = r0.qrButton
                r11 = 48
                r12 = 1111490560(0x42400000, float:48.0)
                boolean r2 = org.telegram.messenger.LocaleController.isRTL
                if (r2 == 0) goto L_0x008d
                r6 = 3
            L_0x008d:
                r13 = r6 | 48
                r14 = 1086324736(0x40CLASSNAME, float:6.0)
                r15 = 0
                r16 = 1086324736(0x40CLASSNAME, float:6.0)
                r17 = 0
                android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
                r0.addView(r1, r2)
                android.widget.ImageView r1 = r0.qrButton
                org.telegram.ui.Wallet.-$$Lambda$WalletActionSheet$SendAddressCell$14b9M9CVT7zAKhJBLIaOAlxl2xA r2 = new org.telegram.ui.Wallet.-$$Lambda$WalletActionSheet$SendAddressCell$14b9M9CVT7zAKhJBLIaOAlxl2xA
                r2.<init>()
                r1.setOnClickListener(r2)
                boolean r1 = org.telegram.messenger.LocaleController.isRTL
                r2 = 1114636288(0x42700000, float:60.0)
                if (r1 == 0) goto L_0x00b2
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
                goto L_0x00b3
            L_0x00b2:
                r1 = 0
            L_0x00b3:
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r10)
                boolean r6 = org.telegram.messenger.LocaleController.isRTL
                if (r6 == 0) goto L_0x00bc
                goto L_0x00c0
            L_0x00bc:
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)
            L_0x00c0:
                r2 = 1090519040(0x41000000, float:8.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                r3.setPadding(r1, r5, r4, r2)
                goto L_0x013a
            L_0x00ca:
                r3.setFocusable(r4)
                r3.setEnabled(r4)
                android.graphics.Typeface r1 = android.graphics.Typeface.MONOSPACE
                r3.setTypeface(r1)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r10 = 1092616192(0x41200000, float:10.0)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r3.setPadding(r4, r1, r4, r10)
                android.widget.ImageView r1 = new android.widget.ImageView
                r1.<init>(r2)
                r0.copyButton = r1
                android.widget.ImageView r1 = r0.copyButton
                r2 = 2131165645(0x7var_cd, float:1.7945513E38)
                r1.setImageResource(r2)
                android.widget.ImageView r1 = r0.copyButton
                android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
                r1.setScaleType(r2)
                android.widget.ImageView r1 = r0.copyButton
                android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r9)
                android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.MULTIPLY
                r2.<init>(r3, r4)
                r1.setColorFilter(r2)
                android.widget.ImageView r1 = r0.copyButton
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2, r5)
                r1.setBackgroundDrawable(r2)
                android.widget.ImageView r1 = r0.copyButton
                r8 = 48
                r9 = 1111490560(0x42400000, float:48.0)
                boolean r2 = org.telegram.messenger.LocaleController.isRTL
                if (r2 == 0) goto L_0x0120
                r6 = 3
            L_0x0120:
                r10 = r6 | 48
                r11 = 1086324736(0x40CLASSNAME, float:6.0)
                r12 = 1092616192(0x41200000, float:10.0)
                r13 = 1086324736(0x40CLASSNAME, float:6.0)
                r14 = 0
                android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
                r0.addView(r1, r2)
                android.widget.ImageView r1 = r0.copyButton
                org.telegram.ui.Wallet.-$$Lambda$WalletActionSheet$SendAddressCell$A3gvDCpvXUcB-rNjyoAe1A4uz14 r2 = new org.telegram.ui.Wallet.-$$Lambda$WalletActionSheet$SendAddressCell$A3gvDCpvXUcB-rNjyoAe1A4uz14
                r2.<init>()
                r1.setOnClickListener(r2)
            L_0x013a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Wallet.WalletActionSheet.SendAddressCell.<init>(org.telegram.ui.Wallet.WalletActionSheet, android.content.Context):void");
        }

        public /* synthetic */ void lambda$new$0$WalletActionSheet$SendAddressCell(View view) {
            AndroidUtilities.hideKeyboard(this.this$0.getCurrentFocus());
            this.this$0.delegate.openQrReader();
        }

        public /* synthetic */ void lambda$new$1$WalletActionSheet$SendAddressCell(View view) {
            AndroidUtilities.addToClipboard("ton://transfer/" + this.this$0.recipientString.replace("\n", ""));
            Toast.makeText(view.getContext(), LocaleController.getString("WalletTransactionAddressCopied", NUM), 0).show();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            ImageView imageView = this.qrButton;
            if (imageView != null) {
                measureChildWithMargins(imageView, i, 0, i2, 0);
            }
            ImageView imageView2 = this.copyButton;
            if (imageView2 != null) {
                measureChildWithMargins(imageView2, i, 0, i2, 0);
            }
        }
    }

    public WalletActionSheet(BaseFragment baseFragment, int i, String str) {
        super(baseFragment.getParentActivity(), true);
        this.walletAddress = str;
        if (this.walletAddress == null) {
            this.walletAddress = TonController.getInstance(this.currentAccount).getWalletAddress(UserConfig.getInstance(this.currentAccount).tonPublicKey);
        }
        this.currentType = i;
        this.parentFragment = baseFragment;
        init(baseFragment.getParentActivity());
    }

    public WalletActionSheet(BaseFragment baseFragment, String str, String str2, String str3, long j, long j2, long j3, long j4) {
        super(baseFragment.getParentActivity(), false);
        this.walletAddress = str;
        this.recipientString = str2;
        this.commentString = str3;
        this.amountValue = j;
        this.currentDate = j2;
        this.currentType = 2;
        this.currentStorageFee = j3;
        this.currentTransactionFee = j4;
        this.parentFragment = baseFragment;
        init(baseFragment.getParentActivity());
    }

    private void init(Context context) {
        final Context context2 = context;
        updateRows();
        this.gemDrawable = context.getResources().getDrawable(NUM);
        AnonymousClass1 r2 = new FrameLayout(context2) {
            private boolean ignoreLayout;
            private RectF rect = new RectF();

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || WalletActionSheet.this.scrollOffsetY == 0 || motionEvent.getY() >= ((float) WalletActionSheet.this.scrollOffsetY) || WalletActionSheet.this.actionBar.getAlpha() != 0.0f) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                WalletActionSheet.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                return !WalletActionSheet.this.isDismissed() && super.onTouchEvent(motionEvent);
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int i3;
                int size = View.MeasureSpec.getSize(i2);
                if (Build.VERSION.SDK_INT >= 21) {
                    this.ignoreLayout = true;
                    setPadding(WalletActionSheet.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, WalletActionSheet.this.backgroundPaddingLeft, 0);
                    this.ignoreLayout = false;
                }
                int paddingTop = size - getPaddingTop();
                View.MeasureSpec.getSize(i);
                int unused = WalletActionSheet.this.backgroundPaddingLeft;
                ((FrameLayout.LayoutParams) WalletActionSheet.this.actionBarShadow.getLayoutParams()).topMargin = ActionBar.getCurrentActionBarHeight();
                this.ignoreLayout = true;
                int dp = AndroidUtilities.dp(80.0f);
                int itemCount = WalletActionSheet.this.listAdapter.getItemCount();
                int i4 = dp;
                for (int i5 = 0; i5 < itemCount; i5++) {
                    View createView = WalletActionSheet.this.listAdapter.createView(context2, i5);
                    createView.measure(i, View.MeasureSpec.makeMeasureSpec(0, 0));
                    i4 += createView.getMeasuredHeight();
                }
                if (i4 < paddingTop) {
                    i3 = paddingTop - i4;
                } else {
                    i3 = WalletActionSheet.this.currentType == 2 ? paddingTop / 5 : 0;
                }
                if (WalletActionSheet.this.scrollView.getPaddingTop() != i3) {
                    WalletActionSheet.this.scrollView.getPaddingTop();
                    WalletActionSheet.this.scrollView.setPadding(0, i3, 0, 0);
                }
                this.ignoreLayout = false;
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(size, NUM));
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                boolean unused = WalletActionSheet.this.inLayout = true;
                super.onLayout(z, i, i2, i3, i4);
                boolean unused2 = WalletActionSheet.this.inLayout = false;
                WalletActionSheet.this.updateLayout(false);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                int access$300 = WalletActionSheet.this.scrollOffsetY - WalletActionSheet.this.backgroundPaddingTop;
                int measuredHeight = getMeasuredHeight() + AndroidUtilities.dp(30.0f) + WalletActionSheet.this.backgroundPaddingTop;
                float dp = (float) AndroidUtilities.dp(12.0f);
                float min = ((float) (WalletActionSheet.this.backgroundPaddingTop + access$300)) < dp ? 1.0f - Math.min(1.0f, ((dp - ((float) access$300)) - ((float) WalletActionSheet.this.backgroundPaddingTop)) / dp) : 1.0f;
                if (Build.VERSION.SDK_INT >= 21) {
                    int i = AndroidUtilities.statusBarHeight;
                    access$300 += i;
                    measuredHeight -= i;
                }
                WalletActionSheet.this.shadowDrawable.setBounds(0, access$300, getMeasuredWidth(), measuredHeight);
                WalletActionSheet.this.shadowDrawable.draw(canvas);
                if (min != 1.0f) {
                    WalletActionSheet.this.backgroundPaint.setColor(Theme.getColor("dialogBackground"));
                    this.rect.set((float) WalletActionSheet.this.backgroundPaddingLeft, (float) (WalletActionSheet.this.backgroundPaddingTop + access$300), (float) (getMeasuredWidth() - WalletActionSheet.this.backgroundPaddingLeft), (float) (WalletActionSheet.this.backgroundPaddingTop + access$300 + AndroidUtilities.dp(24.0f)));
                    float f = dp * min;
                    canvas.drawRoundRect(this.rect, f, f, WalletActionSheet.this.backgroundPaint);
                }
                int color = Theme.getColor("dialogBackground");
                WalletActionSheet.this.backgroundPaint.setColor(Color.argb((int) (WalletActionSheet.this.actionBar.getAlpha() * 255.0f), (int) (((float) Color.red(color)) * 0.8f), (int) (((float) Color.green(color)) * 0.8f), (int) (((float) Color.blue(color)) * 0.8f)));
                canvas.drawRect((float) WalletActionSheet.this.backgroundPaddingLeft, 0.0f, (float) (getMeasuredWidth() - WalletActionSheet.this.backgroundPaddingLeft), (float) AndroidUtilities.statusBarHeight, WalletActionSheet.this.backgroundPaint);
            }
        };
        r2.setWillNotDraw(false);
        this.containerView = r2;
        setApplyTopPadding(false);
        setApplyBottomPadding(false);
        if (this.currentType == 0) {
            this.biometricPromtHelper = new BiometricPromtHelper(this.parentFragment);
        }
        this.listAdapter = new ListAdapter();
        this.scrollView = new NestedScrollView(context2) {
            private View focusingView;

            public void requestChildFocus(View view, View view2) {
                this.focusingView = view2;
                super.requestChildFocus(view, view2);
            }

            /* access modifiers changed from: protected */
            public int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
                if (WalletActionSheet.this.linearLayout.getTop() != getPaddingTop()) {
                    return 0;
                }
                int computeScrollDeltaToGetChildRectOnScreen = super.computeScrollDeltaToGetChildRectOnScreen(rect);
                int currentActionBarHeight = ActionBar.getCurrentActionBarHeight() - (((this.focusingView.getTop() - getScrollY()) + rect.top) + computeScrollDeltaToGetChildRectOnScreen);
                return currentActionBarHeight > 0 ? computeScrollDeltaToGetChildRectOnScreen - (currentActionBarHeight + AndroidUtilities.dp(10.0f)) : computeScrollDeltaToGetChildRectOnScreen;
            }
        };
        this.scrollView.setClipToPadding(false);
        this.scrollView.setVerticalScrollBarEnabled(false);
        r2.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 80.0f));
        this.linearLayout = new LinearLayout(context2);
        this.linearLayout.setOrientation(1);
        this.scrollView.addView((View) this.linearLayout, (ViewGroup.LayoutParams) LayoutHelper.createScroll(-1, -1, 51));
        this.scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            public final void onScrollChange(NestedScrollView nestedScrollView, int i, int i2, int i3, int i4) {
                WalletActionSheet.this.lambda$init$0$WalletActionSheet(nestedScrollView, i, i2, i3, i4);
            }
        });
        int itemCount = this.listAdapter.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            View createView = this.listAdapter.createView(context2, i);
            this.linearLayout.addView(createView, LayoutHelper.createLinear(-1, -2));
            if (this.currentType == 2 && i == this.commentRow) {
                createView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                createView.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        WalletActionSheet.this.lambda$init$1$WalletActionSheet(view);
                    }
                });
                createView.setOnLongClickListener(new View.OnLongClickListener() {
                    public final boolean onLongClick(View view) {
                        return WalletActionSheet.this.lambda$init$2$WalletActionSheet(view);
                    }
                });
            }
        }
        this.actionBar = new ActionBar(context2) {
            public void setAlpha(float f) {
                super.setAlpha(f);
                WalletActionSheet.this.containerView.invalidate();
            }
        };
        this.actionBar.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setItemsColor(Theme.getColor("dialogTextBlack"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("dialogButtonSelector"), false);
        this.actionBar.setTitleColor(Theme.getColor("dialogTextBlack"));
        this.actionBar.setOccupyStatusBar(false);
        this.actionBar.setAlpha(0.0f);
        int i2 = this.currentType;
        if (i2 == 1) {
            this.actionBar.setTitle(LocaleController.getString("WalletCreateInvoiceTitle", NUM));
        } else if (i2 == 2) {
            this.actionBar.setTitle(LocaleController.getString("WalletTransaction", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("WalletSendGrams", NUM));
        }
        this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    WalletActionSheet.this.dismiss();
                }
            }
        });
        this.actionBarShadow = new View(context2);
        this.actionBarShadow.setAlpha(0.0f);
        this.actionBarShadow.setBackgroundColor(Theme.getColor("dialogShadowLine"));
        this.containerView.addView(this.actionBarShadow, LayoutHelper.createFrame(-1, 1.0f));
        this.shadow = new View(context2);
        this.shadow.setBackgroundColor(Theme.getColor("dialogShadowLine"));
        this.shadow.setAlpha(0.0f);
        this.containerView.addView(this.shadow, LayoutHelper.createFrame(-1, 1.0f, 83, 0.0f, 0.0f, 0.0f, 80.0f));
        TextView textView = new TextView(context2);
        textView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        textView.setGravity(17);
        textView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        textView.setTextSize(1, 14.0f);
        int i3 = this.currentType;
        if (i3 == 2) {
            textView.setText(LocaleController.getString("WalletTransactionSendGrams", NUM));
        } else if (i3 == 0) {
            textView.setText(LocaleController.getString("WalletSendGrams", NUM));
        } else {
            textView.setText(LocaleController.getString("WalletCreateInvoiceTitle", NUM));
        }
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        r2.addView(textView, LayoutHelper.createFrame(-1, 42.0f, 83, 16.0f, 16.0f, 16.0f, 16.0f));
        textView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                WalletActionSheet.this.lambda$init$4$WalletActionSheet(view);
            }
        });
    }

    public /* synthetic */ void lambda$init$0$WalletActionSheet(NestedScrollView nestedScrollView, int i, int i2, int i3, int i4) {
        updateLayout(!this.inLayout);
    }

    public /* synthetic */ void lambda$init$1$WalletActionSheet(View view) {
        AndroidUtilities.addToClipboard(this.commentString);
        Toast.makeText(view.getContext(), LocaleController.getString("TextCopied", NUM), 0).show();
    }

    public /* synthetic */ boolean lambda$init$2$WalletActionSheet(View view) {
        AndroidUtilities.addToClipboard(this.commentString);
        Toast.makeText(view.getContext(), LocaleController.getString("TextCopied", NUM), 0).show();
        return true;
    }

    public /* synthetic */ void lambda$init$4$WalletActionSheet(View view) {
        int i = this.currentType;
        if (i == 2) {
            this.delegate.openSendToAddress(this.recipientString.replace("\n", ""));
            dismiss();
        } else if (i == 0) {
            String str = this.recipientString;
            if (str.codePointCount(0, str.length()) != 48 || !TonController.getInstance(this.currentAccount).isValidWalletAddress(this.recipientString)) {
                onFieldError(this.recipientRow);
                return;
            }
            long j = this.amountValue;
            if (j <= 0 || j > this.currentBalance) {
                onFieldError(this.amountRow);
            } else if (this.walletAddress.replace("\n", "").equals(this.recipientString.replace("\n", ""))) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(LocaleController.getString("Wallet", NUM));
                builder.setMessage(LocaleController.getString("WalletSendSameWalletText", NUM));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                builder.setPositiveButton(LocaleController.getString("WalletSendSameWalletProceed", NUM), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        WalletActionSheet.this.lambda$null$3$WalletActionSheet(dialogInterface, i);
                    }
                });
                builder.show();
            } else {
                doSend();
            }
        } else if (i != 1) {
        } else {
            if (this.amountValue <= 0) {
                onFieldError(this.amountRow);
                return;
            }
            String str2 = "ton://transfer/" + this.walletAddress + "/?amount=" + this.amountValue;
            if (!TextUtils.isEmpty(this.commentString)) {
                try {
                    str2 = str2 + "&text=" + URLEncoder.encode(this.commentString, "UTF-8").replaceAll("\\+", "%20");
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            dismiss();
            this.delegate.openInvoice(str2, this.amountValue);
        }
    }

    public /* synthetic */ void lambda$null$3$WalletActionSheet(DialogInterface dialogInterface, int i) {
        doSend();
    }

    public void setRecipientString(String str, boolean z) {
        View childAt;
        this.recipientString = str;
        this.hasWalletInBack = z;
        if (this.scrollView != null && (childAt = this.linearLayout.getChildAt(this.recipientRow)) != null) {
            ListAdapter listAdapter2 = this.listAdapter;
            int i = this.recipientRow;
            listAdapter2.onBindViewHolder(childAt, i, listAdapter2.getItemViewType(i));
        }
    }

    public void parseTonUrl(Editable editable, String str) {
        View childAt;
        View childAt2;
        View childAt3;
        try {
            Uri parse = Uri.parse(str);
            String path = parse.getPath();
            String queryParameter = parse.getQueryParameter("text");
            String queryParameter2 = parse.getQueryParameter("amount");
            if (!TextUtils.isEmpty(path) && path.length() > 1) {
                this.recipientString = path.replace("/", "");
                if (!(editable != null || this.scrollView == null || (childAt3 = this.linearLayout.getChildAt(this.recipientRow)) == null)) {
                    this.listAdapter.onBindViewHolder(childAt3, this.recipientRow, this.listAdapter.getItemViewType(this.recipientRow));
                }
            }
            if (!TextUtils.isEmpty(queryParameter)) {
                this.commentString = queryParameter;
                if (!(this.scrollView == null || (childAt2 = this.linearLayout.getChildAt(this.commentRow)) == null)) {
                    this.listAdapter.onBindViewHolder(childAt2, this.commentRow, this.listAdapter.getItemViewType(this.commentRow));
                }
            }
            if (!TextUtils.isEmpty(queryParameter2)) {
                this.amountValue = Utilities.parseLong(queryParameter2).longValue();
            }
            if (!(this.scrollView == null || (childAt = this.linearLayout.getChildAt(this.amountRow)) == null)) {
                if (!TextUtils.isEmpty(queryParameter2)) {
                    this.listAdapter.onBindViewHolder(childAt, this.amountRow, this.listAdapter.getItemViewType(this.amountRow));
                }
                EditTextBoldCursor textView = ((PollEditTextCell) childAt).getTextView();
                textView.setSelection(textView.length());
                textView.requestFocus();
                AndroidUtilities.showKeyboard(textView);
            }
            if (editable != null) {
                editable.replace(0, editable.length(), this.recipientString);
            }
        } catch (Exception unused) {
        }
    }

    public void onPause() {
        BiometricPromtHelper biometricPromtHelper2 = this.biometricPromtHelper;
        if (biometricPromtHelper2 != null) {
            biometricPromtHelper2.onPause();
        }
    }

    public void setDelegate(WalletActionSheetDelegate walletActionSheetDelegate) {
        this.delegate = walletActionSheetDelegate;
    }

    /* access modifiers changed from: private */
    public void updateLayout(boolean z) {
        View childAt = this.scrollView.getChildAt(0);
        int top = childAt.getTop() - this.scrollView.getScrollY();
        if (top < 0) {
            top = 0;
        }
        boolean z2 = top <= 0;
        float f = 1.0f;
        if ((z2 && this.actionBar.getTag() == null) || (!z2 && this.actionBar.getTag() != null)) {
            this.actionBar.setTag(z2 ? 1 : null);
            AnimatorSet animatorSet = this.actionBarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.actionBarAnimation = null;
            }
            if (z) {
                this.actionBarAnimation = new AnimatorSet();
                this.actionBarAnimation.setDuration(180);
                AnimatorSet animatorSet2 = this.actionBarAnimation;
                Animator[] animatorArr = new Animator[2];
                ActionBar actionBar2 = this.actionBar;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = z2 ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(actionBar2, property, fArr);
                View view = this.actionBarShadow;
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                fArr2[0] = z2 ? 1.0f : 0.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(view, property2, fArr2);
                animatorSet2.playTogether(animatorArr);
                this.actionBarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        AnimatorSet unused = WalletActionSheet.this.actionBarAnimation = null;
                    }
                });
                this.actionBarAnimation.start();
            } else {
                this.actionBar.setAlpha(z2 ? 1.0f : 0.0f);
                this.actionBarShadow.setAlpha(z2 ? 1.0f : 0.0f);
            }
        }
        if (this.scrollOffsetY != top) {
            this.scrollOffsetY = top;
            this.containerView.invalidate();
        }
        childAt.getBottom();
        this.scrollView.getMeasuredHeight();
        boolean z3 = childAt.getBottom() - this.scrollView.getScrollY() > this.scrollView.getMeasuredHeight();
        if ((z3 && this.shadow.getTag() == null) || (!z3 && this.shadow.getTag() != null)) {
            this.shadow.setTag(z3 ? 1 : null);
            AnimatorSet animatorSet3 = this.shadowAnimation;
            if (animatorSet3 != null) {
                animatorSet3.cancel();
                this.shadowAnimation = null;
            }
            if (z) {
                this.shadowAnimation = new AnimatorSet();
                this.shadowAnimation.setDuration(180);
                AnimatorSet animatorSet4 = this.shadowAnimation;
                Animator[] animatorArr2 = new Animator[1];
                View view2 = this.shadow;
                Property property3 = View.ALPHA;
                float[] fArr3 = new float[1];
                if (!z3) {
                    f = 0.0f;
                }
                fArr3[0] = f;
                animatorArr2[0] = ObjectAnimator.ofFloat(view2, property3, fArr3);
                animatorSet4.playTogether(animatorArr2);
                this.shadowAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        AnimatorSet unused = WalletActionSheet.this.shadowAnimation = null;
                    }
                });
                this.shadowAnimation.start();
                return;
            }
            View view3 = this.shadow;
            if (!z3) {
                f = 0.0f;
            }
            view3.setAlpha(f);
        }
    }

    public void dismiss() {
        AndroidUtilities.hideKeyboard(getCurrentFocus());
        super.dismiss();
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (i == 33 && i2 == -1) {
            dismiss();
            this.parentFragment.presentFragment(new WalletPasscodeActivity(false, (Cipher) null, this.walletAddress, this.recipientString, this.amountValue, this.commentString, this.hasWalletInBack));
        }
    }

    private void doSend() {
        AlertDialog alertDialog = new AlertDialog(getContext(), 3);
        alertDialog.setCanCacnel(false);
        alertDialog.show();
        TonController.getInstance(this.currentAccount).getSendFee(this.walletAddress, this.recipientString, this.amountValue, this.commentString, new TonController.FeeCallback(alertDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(long j) {
                WalletActionSheet.this.lambda$doSend$7$WalletActionSheet(this.f$1, j);
            }
        });
    }

    public /* synthetic */ void lambda$doSend$7$WalletActionSheet(AlertDialog alertDialog, long j) {
        int i;
        alertDialog.dismiss();
        Context context = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("WalletConfirmation", NUM));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("WalletConfirmationText", NUM, TonController.formatCurrency(this.amountValue))));
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setClipToPadding(false);
        TextView textView = new TextView(context);
        textView.setTextSize(1, 15.0f);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmono.ttf"));
        textView.setTextColor(Theme.getColor("dialogTextBlack"));
        textView.setGravity(17);
        textView.setPadding(AndroidUtilities.dp(3.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(9.0f));
        textView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), Theme.getColor("wallet_addressConfirmBackground")));
        StringBuilder sb = new StringBuilder(this.recipientString);
        sb.insert(sb.length() / 2, " \n ");
        sb.insert(0, " ");
        sb.insert(sb.length(), " ");
        textView.setText(sb);
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2, 1));
        if (j > 0) {
            TextView textView2 = new TextView(context);
            textView2.setTextSize(1, 13.0f);
            textView2.setTextColor(Theme.getColor("dialogTextGray3"));
            textView2.setGravity(17);
            textView2.setText(LocaleController.formatString("WalletFee", NUM, TonController.formatCurrency(j)));
            frameLayout.addView(textView2, LayoutHelper.createFrame(-2, -2.0f, 1, 0.0f, 67.0f, 0.0f, 0.0f));
            i = 98;
        } else {
            i = 64;
        }
        builder.setView(frameLayout, i);
        builder.setPositiveButton(LocaleController.getString("WalletConfirm", NUM).toUpperCase(), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                WalletActionSheet.this.lambda$null$6$WalletActionSheet(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.show();
    }

    public /* synthetic */ void lambda$null$6$WalletActionSheet(DialogInterface dialogInterface, int i) {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            int keyProtectionType = TonController.getInstance(this.currentAccount).getKeyProtectionType();
            if (keyProtectionType == 0) {
                AndroidUtilities.hideKeyboard(getCurrentFocus());
                this.parentFragment.presentFragment(new WalletPasscodeActivity(true, (Cipher) null, this.walletAddress, this.recipientString, this.amountValue, this.commentString, this.hasWalletInBack));
                dismiss();
            } else if (keyProtectionType != 1) {
                if (keyProtectionType == 2) {
                    this.biometricPromtHelper.promtWithCipher(TonController.getInstance(this.currentAccount).getCipherForDecrypt(), LocaleController.getString("WalletSendConfirmCredentials", NUM), new BiometricPromtHelper.CipherCallback() {
                        public final void run(Cipher cipher) {
                            WalletActionSheet.this.lambda$null$5$WalletActionSheet(cipher);
                        }
                    });
                }
            } else if (Build.VERSION.SDK_INT >= 23) {
                this.parentFragment.getParentActivity().startActivityForResult(((KeyguardManager) ApplicationLoader.applicationContext.getSystemService("keyguard")).createConfirmDeviceCredentialIntent(LocaleController.getString("Wallet", NUM), LocaleController.getString("WalletSendConfirmCredentials", NUM)), 33);
            }
        }
    }

    public /* synthetic */ void lambda$null$5$WalletActionSheet(Cipher cipher) {
        dismiss();
        this.parentFragment.presentFragment(new WalletPasscodeActivity(false, cipher, this.walletAddress, this.recipientString, this.amountValue, this.commentString, this.hasWalletInBack));
    }

    private void updateRows() {
        this.rowCount = 0;
        this.recipientHeaderRow = -1;
        this.recipientRow = -1;
        this.amountHeaderRow = -1;
        this.amountRow = -1;
        this.commentRow = -1;
        this.commentHeaderRow = -1;
        this.balanceRow = -1;
        this.dateRow = -1;
        this.invoiceInfoRow = -1;
        this.dateHeaderRow = -1;
        this.sendBalanceRow = -1;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.titleRow = i;
        int i2 = this.currentType;
        if (i2 == 1) {
            int i3 = this.rowCount;
            this.rowCount = i3 + 1;
            this.invoiceInfoRow = i3;
            int i4 = this.rowCount;
            this.rowCount = i4 + 1;
            this.amountHeaderRow = i4;
            int i5 = this.rowCount;
            this.rowCount = i5 + 1;
            this.amountRow = i5;
            int i6 = this.rowCount;
            this.rowCount = i6 + 1;
            this.commentRow = i6;
        } else if (i2 == 2) {
            int i7 = this.rowCount;
            this.rowCount = i7 + 1;
            this.balanceRow = i7;
            int i8 = this.rowCount;
            this.rowCount = i8 + 1;
            this.recipientHeaderRow = i8;
            int i9 = this.rowCount;
            this.rowCount = i9 + 1;
            this.recipientRow = i9;
            int i10 = this.rowCount;
            this.rowCount = i10 + 1;
            this.dateHeaderRow = i10;
            int i11 = this.rowCount;
            this.rowCount = i11 + 1;
            this.dateRow = i11;
            if (!TextUtils.isEmpty(this.commentString)) {
                int i12 = this.rowCount;
                this.rowCount = i12 + 1;
                this.commentHeaderRow = i12;
                int i13 = this.rowCount;
                this.rowCount = i13 + 1;
                this.commentRow = i13;
            }
        } else {
            int i14 = this.rowCount;
            this.rowCount = i14 + 1;
            this.recipientHeaderRow = i14;
            int i15 = this.rowCount;
            this.rowCount = i15 + 1;
            this.recipientRow = i15;
            int i16 = this.rowCount;
            this.rowCount = i16 + 1;
            this.amountHeaderRow = i16;
            int i17 = this.rowCount;
            this.rowCount = i17 + 1;
            this.amountRow = i17;
            int i18 = this.rowCount;
            this.rowCount = i18 + 1;
            this.sendBalanceRow = i18;
            int i19 = this.rowCount;
            this.rowCount = i19 + 1;
            this.commentRow = i19;
        }
    }

    private void onFieldError(int i) {
        View childAt = this.linearLayout.getChildAt(i);
        if (childAt != null) {
            AndroidUtilities.shakeView(childAt, 2.0f, 0);
            try {
                Vibrator vibrator = (Vibrator) this.parentFragment.getParentActivity().getSystemService("vibrator");
                if (vibrator != null) {
                    vibrator.vibrate(200);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* access modifiers changed from: private */
    public void setTextLeft(View view) {
        int i = this.currentType;
        if (i != 2) {
            String str = "windowBackgroundWhiteRedText5";
            if (view instanceof PollEditTextCell) {
                PollEditTextCell pollEditTextCell = (PollEditTextCell) view;
                int length = 500 - this.commentString.getBytes().length;
                if (((float) length) <= 150.0f) {
                    pollEditTextCell.setText2(String.format("%d", new Object[]{Integer.valueOf(length)}));
                    SimpleTextView textView2 = pollEditTextCell.getTextView2();
                    if (length >= 0) {
                        str = "windowBackgroundWhiteGrayText3";
                    }
                    textView2.setTextColor(Theme.getColor(str));
                    textView2.setTag(str);
                    return;
                }
                pollEditTextCell.setText2("");
            } else if (view instanceof TextInfoPrivacyCell) {
                long j = this.currentBalance;
                if (j >= 0 && i == 0) {
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) view;
                    if (this.amountValue <= j) {
                        str = "windowBackgroundWhiteBlueHeader";
                    }
                    textInfoPrivacyCell.getTextView().setTag(str);
                    textInfoPrivacyCell.getTextView().setTextColor(Theme.getColor(str));
                }
            }
        }
    }

    private class ListAdapter {
        private ListAdapter() {
        }

        public int getItemCount() {
            return WalletActionSheet.this.rowCount;
        }

        public void onBindViewHolder(View view, int i, int i2) {
            TonApi.GenericAccountState cachedAccountState;
            String str = "";
            switch (i2) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) view;
                    if (i == WalletActionSheet.this.recipientHeaderRow) {
                        if (WalletActionSheet.this.currentType != 2) {
                            headerCell.setText(LocaleController.getString("WalletSendRecipient", NUM));
                            return;
                        } else if (WalletActionSheet.this.amountValue > 0) {
                            headerCell.setText(LocaleController.getString("WalletTransactionSender", NUM));
                            return;
                        } else {
                            headerCell.setText(LocaleController.getString("WalletTransactionRecipient", NUM));
                            return;
                        }
                    } else if (i == WalletActionSheet.this.commentHeaderRow) {
                        headerCell.setText(LocaleController.getString("WalletTransactionComment", NUM));
                        return;
                    } else if (i == WalletActionSheet.this.dateHeaderRow) {
                        headerCell.setText(LocaleController.getString("WalletDate", NUM));
                        return;
                    } else if (i == WalletActionSheet.this.amountHeaderRow) {
                        headerCell.setText(LocaleController.getString("WalletAmount", NUM));
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) view;
                    if (i == WalletActionSheet.this.invoiceInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("WalletInvoiceInfo", NUM));
                        return;
                    }
                    return;
                case 3:
                    PollEditTextCell pollEditTextCell = (PollEditTextCell) view;
                    if (i == WalletActionSheet.this.dateRow) {
                        pollEditTextCell.setTextAndHint(LocaleController.getInstance().formatterStats.format(WalletActionSheet.this.currentDate * 1000), str, false);
                        return;
                    } else if (i == WalletActionSheet.this.commentRow) {
                        pollEditTextCell.setTextAndHint(WalletActionSheet.this.commentString, LocaleController.getString("WalletComment", NUM), false);
                        return;
                    } else {
                        return;
                    }
                case 4:
                    PollEditTextCell pollEditTextCell2 = (PollEditTextCell) view;
                    CharSequence charSequence = str;
                    if (WalletActionSheet.this.amountValue != 0) {
                        charSequence = TonController.formatCurrency(WalletActionSheet.this.amountValue);
                    }
                    pollEditTextCell2.setText(charSequence, true);
                    return;
                case 6:
                    ((SendAddressCell) view).setTextAndHint(WalletActionSheet.this.recipientString, LocaleController.getString("WalletEnterWalletAddress", NUM), false);
                    return;
                case 7:
                    TextInfoPrivacyCell textInfoPrivacyCell2 = (TextInfoPrivacyCell) view;
                    if (i == WalletActionSheet.this.sendBalanceRow && (cachedAccountState = TonController.getInstance(WalletActionSheet.this.currentAccount).getCachedAccountState()) != null) {
                        textInfoPrivacyCell2.setText(LocaleController.formatString("WalletSendBalance", NUM, TonController.formatCurrency(WalletActionSheet.this.currentBalance = TonController.getBalance(cachedAccountState))));
                        return;
                    }
                    return;
                case 8:
                    ((BalanceCell) view).setBalance(WalletActionSheet.this.amountValue, WalletActionSheet.this.currentStorageFee, WalletActionSheet.this.currentTransactionFee);
                    return;
                case 9:
                    TitleCell titleCell = (TitleCell) view;
                    if (i != WalletActionSheet.this.titleRow) {
                        return;
                    }
                    if (WalletActionSheet.this.currentType == 1) {
                        titleCell.setText(LocaleController.getString("WalletCreateInvoiceTitle", NUM));
                        return;
                    } else if (WalletActionSheet.this.currentType == 2) {
                        titleCell.setText(LocaleController.getString("WalletTransaction", NUM));
                        return;
                    } else if (WalletActionSheet.this.currentType == 0) {
                        titleCell.setText(LocaleController.getString("WalletSendGrams", NUM));
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        /* JADX WARNING: type inference failed for: r1v3, types: [org.telegram.ui.Cells.TextInfoPrivacyCell] */
        /* JADX WARNING: type inference failed for: r3v2, types: [org.telegram.ui.Cells.PollEditTextCell, org.telegram.ui.Wallet.WalletActionSheet$ListAdapter$1] */
        /* JADX WARNING: type inference failed for: r4v6, types: [org.telegram.ui.Cells.PollEditTextCell, org.telegram.ui.Wallet.WalletActionSheet$ListAdapter$3] */
        /* JADX WARNING: type inference failed for: r1v9, types: [org.telegram.ui.Wallet.WalletActionSheet$SendAddressCell] */
        /* JADX WARNING: type inference failed for: r1v11, types: [org.telegram.ui.Wallet.WalletActionSheet$ListAdapter$5] */
        /* JADX WARNING: type inference failed for: r1v13, types: [org.telegram.ui.Wallet.WalletActionSheet$BalanceCell] */
        /* JADX WARNING: type inference failed for: r1v14, types: [org.telegram.ui.Wallet.WalletActionSheet$TitleCell] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Unknown variable types count: 2 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public android.view.View createView(android.content.Context r9, int r10) {
            /*
                r8 = this;
                int r0 = r8.getItemViewType(r10)
                if (r0 == 0) goto L_0x010c
                r1 = 1
                if (r0 == r1) goto L_0x0106
                r2 = 0
                r3 = 3
                if (r0 == r3) goto L_0x00b4
                r4 = 4
                if (r0 == r4) goto L_0x003c
                r1 = 6
                if (r0 == r1) goto L_0x0033
                r1 = 7
                if (r0 == r1) goto L_0x002c
                r1 = 8
                if (r0 == r1) goto L_0x0023
                org.telegram.ui.Wallet.WalletActionSheet$TitleCell r1 = new org.telegram.ui.Wallet.WalletActionSheet$TitleCell
                org.telegram.ui.Wallet.WalletActionSheet r2 = org.telegram.ui.Wallet.WalletActionSheet.this
                r1.<init>(r9)
                goto L_0x0119
            L_0x0023:
                org.telegram.ui.Wallet.WalletActionSheet$BalanceCell r1 = new org.telegram.ui.Wallet.WalletActionSheet$BalanceCell
                org.telegram.ui.Wallet.WalletActionSheet r2 = org.telegram.ui.Wallet.WalletActionSheet.this
                r1.<init>(r9)
                goto L_0x0119
            L_0x002c:
                org.telegram.ui.Wallet.WalletActionSheet$ListAdapter$5 r1 = new org.telegram.ui.Wallet.WalletActionSheet$ListAdapter$5
                r1.<init>(r9)
                goto L_0x0119
            L_0x0033:
                org.telegram.ui.Wallet.WalletActionSheet$SendAddressCell r1 = new org.telegram.ui.Wallet.WalletActionSheet$SendAddressCell
                org.telegram.ui.Wallet.WalletActionSheet r2 = org.telegram.ui.Wallet.WalletActionSheet.this
                r1.<init>(r2, r9)
                goto L_0x0119
            L_0x003c:
                org.telegram.ui.Wallet.WalletActionSheet$ListAdapter$3 r4 = new org.telegram.ui.Wallet.WalletActionSheet$ListAdapter$3
                r4.<init>(r9, r2)
                org.telegram.ui.Components.EditTextBoldCursor r2 = r4.getTextView()
                r4.setShowNextButton(r1)
                java.lang.String r5 = "dialogTextBlack"
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r2.setTextColor(r5)
                java.lang.String r5 = "dialogTextHint"
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r2.setHintColor(r5)
                r5 = 1106247680(0x41var_, float:30.0)
                r2.setTextSize(r1, r5)
                android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r9, r1)
                r2.setBackground(r9)
                int r9 = r2.getImeOptions()
                r9 = r9 | 5
                r2.setImeOptions(r9)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r5)
                r2.setCursorSize(r9)
                java.lang.String r9 = "fonts/rmedium.ttf"
                android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r9)
                r2.setTypeface(r9)
                android.text.SpannableStringBuilder r9 = new android.text.SpannableStringBuilder
                java.lang.String r5 = "0.0"
                r9.<init>(r5)
                android.text.style.RelativeSizeSpan r5 = new android.text.style.RelativeSizeSpan
                r6 = 1060823368(0x3f3ae148, float:0.73)
                r5.<init>(r6)
                int r6 = r9.length()
                int r6 = r6 - r1
                int r1 = r9.length()
                r7 = 33
                r9.setSpan(r5, r6, r1, r7)
                r2.setHintText(r9)
                r2.setInputType(r3)
                org.telegram.ui.Wallet.-$$Lambda$WalletActionSheet$ListAdapter$ZFDAbICnPCIUoc8-bv49sieBXSk r9 = new org.telegram.ui.Wallet.-$$Lambda$WalletActionSheet$ListAdapter$ZFDAbICnPCIUoc8-bv49sieBXSk
                r9.<init>()
                r2.setOnEditorActionListener(r9)
                org.telegram.ui.Wallet.WalletActionSheet$ListAdapter$4 r9 = new org.telegram.ui.Wallet.WalletActionSheet$ListAdapter$4
                r9.<init>(r2)
                r4.addTextWatcher(r9)
                r1 = r4
                goto L_0x0119
            L_0x00b4:
                org.telegram.ui.Wallet.WalletActionSheet$ListAdapter$1 r3 = new org.telegram.ui.Wallet.WalletActionSheet$ListAdapter$1
                r3.<init>(r9, r2)
                org.telegram.ui.Components.EditTextBoldCursor r2 = r3.getTextView()
                org.telegram.ui.Wallet.WalletActionSheet r4 = org.telegram.ui.Wallet.WalletActionSheet.this
                int r4 = r4.currentType
                r5 = 2
                r6 = 0
                if (r4 != r5) goto L_0x00d1
                r2.setEnabled(r6)
                r2.setFocusable(r6)
                r2.setClickable(r6)
                goto L_0x0104
            L_0x00d1:
                android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.createEditTextDrawable(r9, r1)
                r2.setBackground(r9)
                r9 = 1096810496(0x41600000, float:14.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r9)
                r5 = 1108606976(0x42140000, float:37.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                r2.setPadding(r6, r4, r5, r9)
                r3.createErrorTextView()
                android.text.InputFilter[] r9 = new android.text.InputFilter[r1]
                org.telegram.ui.Wallet.WalletActionSheet$ByteLengthFilter r1 = new org.telegram.ui.Wallet.WalletActionSheet$ByteLengthFilter
                r4 = 500(0x1f4, float:7.0E-43)
                r1.<init>(r4)
                r9[r6] = r1
                r2.setFilters(r9)
                org.telegram.ui.Wallet.WalletActionSheet$ListAdapter$2 r9 = new org.telegram.ui.Wallet.WalletActionSheet$ListAdapter$2
                r9.<init>(r3)
                r3.addTextWatcher(r9)
            L_0x0104:
                r1 = r3
                goto L_0x0119
            L_0x0106:
                org.telegram.ui.Cells.TextInfoPrivacyCell r1 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                r1.<init>(r9)
                goto L_0x0119
            L_0x010c:
                org.telegram.ui.Cells.HeaderCell r1 = new org.telegram.ui.Cells.HeaderCell
                r4 = 0
                r5 = 21
                r6 = 12
                r7 = 0
                r2 = r1
                r3 = r9
                r2.<init>(r3, r4, r5, r6, r7)
            L_0x0119:
                r8.onBindViewHolder(r1, r10, r0)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Wallet.WalletActionSheet.ListAdapter.createView(android.content.Context, int):android.view.View");
        }

        public /* synthetic */ boolean lambda$createView$0$WalletActionSheet$ListAdapter(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            View childAt = WalletActionSheet.this.linearLayout.getChildAt(WalletActionSheet.this.commentRow);
            if (childAt == null) {
                return true;
            }
            ((PollEditTextCell) childAt).getTextView().requestFocus();
            return true;
        }

        public int getItemViewType(int i) {
            if (i == WalletActionSheet.this.recipientHeaderRow || i == WalletActionSheet.this.commentHeaderRow || i == WalletActionSheet.this.dateHeaderRow || i == WalletActionSheet.this.amountHeaderRow) {
                return 0;
            }
            if (i == WalletActionSheet.this.invoiceInfoRow) {
                return 1;
            }
            if (i == WalletActionSheet.this.commentRow || i == WalletActionSheet.this.dateRow) {
                return 3;
            }
            if (i == WalletActionSheet.this.amountRow) {
                return 4;
            }
            if (i == WalletActionSheet.this.recipientRow) {
                return 6;
            }
            if (i == WalletActionSheet.this.sendBalanceRow) {
                return 7;
            }
            return i == WalletActionSheet.this.balanceRow ? 8 : 9;
        }
    }
}

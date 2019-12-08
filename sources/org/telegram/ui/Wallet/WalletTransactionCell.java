package org.telegram.ui.Wallet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import drinkless.org.ton.TonApi.RawMessage;
import drinkless.org.ton.TonApi.RawTransaction;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.TonController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.TypefaceSpan;

public class WalletTransactionCell extends LinearLayout {
    private TextView addressValueTextView;
    private ImageView clockImage;
    private TextView commentTextView;
    private long currentAmount;
    private long currentDate;
    private long currentStorageFee;
    private long currentTransactionFee;
    private TextView dateTextView;
    private Typeface defaultTypeFace = this.valueTextView.getTypeface();
    private boolean drawDivider;
    private TextView feeTextView;
    private TextView fromTextView;
    private ImageView gemImageView;
    private boolean isEmpty;
    private TextView valueTextView;

    public WalletTransactionCell(Context context) {
        super(context);
        setOrientation(1);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        addView(linearLayout, LayoutHelper.createLinear(-1, -2, 18.0f, 0.0f, 18.0f, 0.0f));
        this.valueTextView = new TextView(context);
        this.valueTextView.setTextSize(1, 16.0f);
        this.valueTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        linearLayout.addView(this.valueTextView, LayoutHelper.createLinear(-2, -2, 0.0f, 8.0f, 0.0f, 0.0f));
        this.gemImageView = new ImageView(context);
        this.gemImageView.setImageResource(NUM);
        linearLayout.addView(this.gemImageView, LayoutHelper.createLinear(-2, -2, 3.0f, 11.0f, 0.0f, 0.0f));
        this.fromTextView = new TextView(context);
        this.fromTextView.setTextSize(1, 15.0f);
        String str = "wallet_blackText";
        this.fromTextView.setTextColor(Theme.getColor(str));
        linearLayout.addView(this.fromTextView, LayoutHelper.createLinear(0, -2, 1.0f, 7, 8, 0, 0));
        this.clockImage = new ImageView(context);
        this.clockImage.setImageResource(NUM);
        String str2 = "wallet_dateText";
        this.clockImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str2), Mode.MULTIPLY));
        linearLayout.addView(this.clockImage, LayoutHelper.createLinear(-2, -2, 53, 0, 14, 4, 0));
        this.dateTextView = new TextView(context);
        this.dateTextView.setTextSize(1, 14.0f);
        this.dateTextView.setTextColor(Theme.getColor(str2));
        linearLayout.addView(this.dateTextView, LayoutHelper.createLinear(-2, -2, 53, 0, 8, 0, 0));
        this.addressValueTextView = new TextView(context);
        this.addressValueTextView.setTextSize(1, 15.0f);
        this.addressValueTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmono.ttf"));
        this.addressValueTextView.setTextColor(Theme.getColor(str));
        addView(this.addressValueTextView, LayoutHelper.createLinear(-2, -2, 51, 18, 6, 0, 10));
        this.commentTextView = new TextView(context);
        this.commentTextView.setTextSize(1, 14.0f);
        String str3 = "wallet_commentText";
        this.commentTextView.setTextColor(Theme.getColor(str3));
        addView(this.commentTextView, LayoutHelper.createLinear(-2, -2, 18.0f, 0.0f, 18.0f, 9.0f));
        this.feeTextView = new TextView(context);
        this.feeTextView.setTextSize(1, 14.0f);
        this.feeTextView.setTextColor(Theme.getColor(str3));
        addView(this.feeTextView, LayoutHelper.createLinear(-2, -2, 18.0f, 0.0f, 18.0f, 9.0f));
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(0, 0));
    }

    public void setTransaction(RawTransaction rawTransaction, boolean z) {
        long j;
        byte[] bArr;
        StringBuilder stringBuilder;
        CharSequence format;
        Object obj;
        int i;
        RawTransaction rawTransaction2 = rawTransaction;
        boolean z2 = z;
        RawMessage rawMessage = rawTransaction2.inMsg;
        if (rawMessage != null) {
            j = rawMessage.value + 0;
            bArr = rawMessage.message;
        } else {
            bArr = null;
            j = 0;
        }
        RawMessage[] rawMessageArr = rawTransaction2.outMsgs;
        if (rawMessageArr != null && rawMessageArr.length > 0) {
            byte[] bArr2 = bArr;
            int i2 = 0;
            while (true) {
                RawMessage[] rawMessageArr2 = rawTransaction2.outMsgs;
                if (i2 >= rawMessageArr2.length) {
                    break;
                }
                j -= rawMessageArr2[i2].value;
                if (bArr2 == null || bArr2.length == 0) {
                    bArr2 = rawTransaction2.outMsgs[i2].message;
                }
                i2++;
            }
            bArr = bArr2;
        }
        this.clockImage.setVisibility(8);
        this.isEmpty = false;
        if (j > 0) {
            stringBuilder = new StringBuilder(rawTransaction2.inMsg.source);
            this.valueTextView.setTextColor(Theme.getColor("wallet_greenText"));
            this.fromTextView.setText(LocaleController.getString("WalletFrom", NUM));
            format = String.format("+%s", new Object[]{TonController.formatCurrency(j)});
            obj = null;
        } else {
            String str = "WalletTo";
            if (rawTransaction2.transactionId.lt == 0) {
                stringBuilder = new StringBuilder(rawTransaction2.inMsg.destination);
                this.fromTextView.setText(LocaleController.getString(str, NUM));
                this.clockImage.setVisibility(0);
                obj = 1;
            } else {
                RawMessage[] rawMessageArr3 = rawTransaction2.outMsgs;
                if (rawMessageArr3 == null || rawMessageArr3.length <= 0) {
                    String str2 = "";
                    stringBuilder = new StringBuilder(str2);
                    this.isEmpty = true;
                    this.fromTextView.setText(str2);
                } else {
                    StringBuilder stringBuilder2 = new StringBuilder(rawMessageArr3[0].destination);
                    this.fromTextView.setText(LocaleController.getString(str, NUM));
                    stringBuilder = stringBuilder2;
                }
                obj = null;
            }
            this.valueTextView.setTextColor(Theme.getColor("wallet_redText"));
            format = String.format("%s", new Object[]{TonController.formatCurrency(j)});
        }
        this.currentAmount = j;
        this.currentDate = rawTransaction2.utime;
        int indexOf = TextUtils.indexOf(format, '.');
        if (indexOf >= 0) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(format);
            spannableStringBuilder.setSpan(new TypefaceSpan(this.defaultTypeFace, AndroidUtilities.dp(14.0f)), indexOf + 1, format.length(), 33);
            format = spannableStringBuilder;
        }
        this.valueTextView.setText(format);
        this.dateTextView.setText(LocaleController.getInstance().formatterDay.format(rawTransaction2.utime * 1000));
        if (!this.isEmpty) {
            stringBuilder.insert(stringBuilder.length() / 2, 10);
        }
        this.addressValueTextView.setText(stringBuilder);
        if (obj != null) {
            this.currentStorageFee = 0;
            this.currentTransactionFee = 0;
        } else {
            this.currentStorageFee = rawTransaction2.storageFee;
            this.currentTransactionFee = rawTransaction2.otherFee;
        }
        if (this.currentStorageFee == 0 && this.currentTransactionFee == 0) {
            this.feeTextView.setVisibility(8);
            i = 0;
        } else {
            TextView textView = this.feeTextView;
            Object[] objArr = new Object[1];
            i = 0;
            objArr[0] = TonController.formatCurrency((-this.currentStorageFee) - this.currentTransactionFee);
            textView.setText(LocaleController.formatString("WalletBlockchainFees", NUM, objArr));
            this.feeTextView.setVisibility(0);
        }
        if (bArr == null || bArr.length <= 0) {
            this.commentTextView.setVisibility(8);
        } else {
            this.commentTextView.setText(new String(bArr));
            this.commentTextView.setVisibility(i);
        }
        LayoutParams layoutParams = (LayoutParams) this.addressValueTextView.getLayoutParams();
        if (this.commentTextView.getVisibility() == 0 || this.feeTextView.getVisibility() == 0) {
            layoutParams.bottomMargin = AndroidUtilities.dp(1.0f);
        } else {
            layoutParams.bottomMargin = AndroidUtilities.dp(10.0f);
        }
        if (this.commentTextView.getVisibility() == 0) {
            layoutParams = (LayoutParams) this.commentTextView.getLayoutParams();
            if (this.feeTextView.getVisibility() == 0) {
                layoutParams.bottomMargin = AndroidUtilities.dp(3.0f);
            } else {
                layoutParams.bottomMargin = AndroidUtilities.dp(9.0f);
            }
        }
        this.drawDivider = z2;
        setWillNotDraw(z2 ^ 1);
    }

    public String getAddress() {
        return this.addressValueTextView.getText().toString();
    }

    public String getComment() {
        return this.commentTextView.getVisibility() == 0 ? this.commentTextView.getText().toString() : "";
    }

    public long getAmount() {
        return this.currentAmount;
    }

    public long getDate() {
        return this.currentDate;
    }

    public boolean isEmpty() {
        return this.isEmpty;
    }

    public long getStorageFee() {
        return this.currentStorageFee;
    }

    public long getTransactionFee() {
        return this.currentTransactionFee;
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (this.drawDivider) {
            canvas.drawLine((float) AndroidUtilities.dp(17.0f), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}

package org.telegram.ui.Wallet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import drinkless.org.ton.TonApi;
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
        this.fromTextView.setTextColor(Theme.getColor("wallet_blackText"));
        linearLayout.addView(this.fromTextView, LayoutHelper.createLinear(0, -2, 1.0f, 7, 8, 0, 0));
        this.clockImage = new ImageView(context);
        this.clockImage.setImageResource(NUM);
        this.clockImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("wallet_dateText"), PorterDuff.Mode.MULTIPLY));
        linearLayout.addView(this.clockImage, LayoutHelper.createLinear(-2, -2, 53, 0, 14, 4, 0));
        this.dateTextView = new TextView(context);
        this.dateTextView.setTextSize(1, 14.0f);
        this.dateTextView.setTextColor(Theme.getColor("wallet_dateText"));
        linearLayout.addView(this.dateTextView, LayoutHelper.createLinear(-2, -2, 53, 0, 8, 0, 0));
        this.addressValueTextView = new TextView(context);
        this.addressValueTextView.setTextSize(1, 15.0f);
        this.addressValueTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmono.ttf"));
        this.addressValueTextView.setTextColor(Theme.getColor("wallet_blackText"));
        addView(this.addressValueTextView, LayoutHelper.createLinear(-2, -2, 51, 18, 6, 0, 10));
        this.commentTextView = new TextView(context);
        this.commentTextView.setTextSize(1, 14.0f);
        this.commentTextView.setTextColor(Theme.getColor("wallet_commentText"));
        addView(this.commentTextView, LayoutHelper.createLinear(-2, -2, 18.0f, 0.0f, 18.0f, 9.0f));
        this.feeTextView = new TextView(context);
        this.feeTextView.setTextSize(1, 14.0f);
        this.feeTextView.setTextColor(Theme.getColor("wallet_commentText"));
        addView(this.feeTextView, LayoutHelper.createLinear(-2, -2, 18.0f, 0.0f, 18.0f, 9.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
    }

    public void setTransaction(TonApi.RawTransaction rawTransaction, boolean z) {
        long j;
        byte[] bArr;
        String str;
        boolean z2;
        StringBuilder sb;
        int i;
        TonApi.RawTransaction rawTransaction2 = rawTransaction;
        boolean z3 = z;
        TonApi.RawMessage rawMessage = rawTransaction2.inMsg;
        if (rawMessage != null) {
            j = rawMessage.value + 0;
            bArr = rawMessage.message;
        } else {
            bArr = null;
            j = 0;
        }
        TonApi.RawMessage[] rawMessageArr = rawTransaction2.outMsgs;
        if (rawMessageArr != null && rawMessageArr.length > 0) {
            byte[] bArr2 = bArr;
            int i2 = 0;
            while (true) {
                TonApi.RawMessage[] rawMessageArr2 = rawTransaction2.outMsgs;
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
            sb = new StringBuilder(rawTransaction2.inMsg.source);
            this.valueTextView.setTextColor(Theme.getColor("wallet_greenText"));
            this.fromTextView.setText(LocaleController.getString("WalletFrom", NUM));
            str = String.format("+%s", new Object[]{TonController.formatCurrency(j)});
            z2 = false;
        } else {
            if (rawTransaction2.transactionId.lt == 0) {
                sb = new StringBuilder(rawTransaction2.inMsg.destination);
                this.fromTextView.setText(LocaleController.getString("WalletTo", NUM));
                this.clockImage.setVisibility(0);
                z2 = true;
            } else {
                TonApi.RawMessage[] rawMessageArr3 = rawTransaction2.outMsgs;
                if (rawMessageArr3 == null || rawMessageArr3.length <= 0) {
                    sb = new StringBuilder("");
                    this.isEmpty = true;
                    this.fromTextView.setText("");
                } else {
                    StringBuilder sb2 = new StringBuilder(rawMessageArr3[0].destination);
                    this.fromTextView.setText(LocaleController.getString("WalletTo", NUM));
                    sb = sb2;
                }
                z2 = false;
            }
            this.valueTextView.setTextColor(Theme.getColor("wallet_redText"));
            str = String.format("%s", new Object[]{TonController.formatCurrency(j)});
        }
        this.currentAmount = j;
        this.currentDate = rawTransaction2.utime;
        int indexOf = TextUtils.indexOf(str, '.');
        if (indexOf >= 0) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
            spannableStringBuilder.setSpan(new TypefaceSpan(this.defaultTypeFace, AndroidUtilities.dp(14.0f)), indexOf + 1, str.length(), 33);
            str = spannableStringBuilder;
        }
        this.valueTextView.setText(str);
        this.dateTextView.setText(LocaleController.getInstance().formatterDay.format(rawTransaction2.utime * 1000));
        if (!this.isEmpty) {
            sb.insert(sb.length() / 2, 10);
        }
        this.addressValueTextView.setText(sb);
        if (z2) {
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
            i = 0;
            this.feeTextView.setText(LocaleController.formatString("WalletBlockchainFees", NUM, TonController.formatCurrency((-this.currentStorageFee) - this.currentTransactionFee)));
            this.feeTextView.setVisibility(0);
        }
        if (bArr == null || bArr.length <= 0) {
            this.commentTextView.setVisibility(8);
        } else {
            this.commentTextView.setText(new String(bArr));
            this.commentTextView.setVisibility(i);
        }
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.addressValueTextView.getLayoutParams();
        if (this.commentTextView.getVisibility() == 0 || this.feeTextView.getVisibility() == 0) {
            layoutParams.bottomMargin = AndroidUtilities.dp(1.0f);
        } else {
            layoutParams.bottomMargin = AndroidUtilities.dp(10.0f);
        }
        if (this.commentTextView.getVisibility() == 0) {
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.commentTextView.getLayoutParams();
            if (this.feeTextView.getVisibility() == 0) {
                layoutParams2.bottomMargin = AndroidUtilities.dp(3.0f);
            } else {
                layoutParams2.bottomMargin = AndroidUtilities.dp(9.0f);
            }
        }
        this.drawDivider = z3;
        setWillNotDraw(!z3);
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

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.drawDivider) {
            canvas.drawLine((float) AndroidUtilities.dp(17.0f), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}

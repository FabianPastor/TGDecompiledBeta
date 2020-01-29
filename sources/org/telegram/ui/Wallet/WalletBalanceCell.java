package org.telegram.ui.Wallet;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.TonController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.TypefaceSpan;

public class WalletBalanceCell extends FrameLayout {
    private Typeface defaultTypeFace;
    private RLottieDrawable gemDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f), false, (int[]) null);
    private FrameLayout receiveButton;
    private Drawable receiveDrawable;
    private SimpleTextView receiveTextView;
    private FrameLayout sendButton;
    private Drawable sendDrawable;
    private SimpleTextView sendTextView;
    private SimpleTextView valueTextView;
    private TextView yourBalanceTextView;

    /* access modifiers changed from: protected */
    public void onReceivePressed() {
    }

    /* access modifiers changed from: protected */
    public void onSendPressed() {
    }

    public WalletBalanceCell(Context context) {
        super(context);
        this.valueTextView = new SimpleTextView(context);
        this.valueTextView.setTextColor(Theme.getColor("wallet_whiteText"));
        this.valueTextView.setTextSize(41);
        this.valueTextView.setDrawablePadding(AndroidUtilities.dp(7.0f));
        this.valueTextView.setGravity(1);
        this.valueTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addView(this.valueTextView, LayoutHelper.createFrame(-1, -2.0f, 1, 0.0f, 35.0f, 0.0f, 0.0f));
        this.gemDrawable.setAutoRepeat(1);
        this.gemDrawable.setAllowDecodeSingleFrame(true);
        this.gemDrawable.addParentView(this.valueTextView);
        this.valueTextView.setRightDrawable((Drawable) this.gemDrawable);
        this.gemDrawable.start();
        this.yourBalanceTextView = new TextView(context);
        this.yourBalanceTextView.setTextSize(1, 14.0f);
        this.yourBalanceTextView.setTextColor(Theme.getColor("wallet_whiteText"));
        this.defaultTypeFace = this.yourBalanceTextView.getTypeface();
        this.yourBalanceTextView.setText(LocaleController.getString("WalletYourBalance", NUM));
        addView(this.yourBalanceTextView, LayoutHelper.createFrame(-2, -2.0f, 1, 0.0f, 90.0f, 0.0f, 0.0f));
        this.receiveDrawable = context.getResources().getDrawable(NUM).mutate();
        this.receiveDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("wallet_buttonText"), PorterDuff.Mode.MULTIPLY));
        this.sendDrawable = context.getResources().getDrawable(NUM).mutate();
        this.sendDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("wallet_buttonText"), PorterDuff.Mode.MULTIPLY));
        int i = 0;
        while (i < 2) {
            FrameLayout frameLayout = new FrameLayout(context);
            frameLayout.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("wallet_buttonBackground"), Theme.getColor("wallet_buttonPressedBackground")));
            addView(frameLayout, LayoutHelper.createFrame(-1, 42.0f, 51, i == 0 ? 32.0f : 16.0f, 168.0f, 0.0f, 0.0f));
            frameLayout.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    WalletBalanceCell.this.lambda$new$0$WalletBalanceCell(view);
                }
            });
            SimpleTextView simpleTextView = new SimpleTextView(context);
            simpleTextView.setTextColor(Theme.getColor("wallet_buttonText"));
            simpleTextView.setTextSize(14);
            simpleTextView.setDrawablePadding(AndroidUtilities.dp(6.0f));
            simpleTextView.setGravity(1);
            simpleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            if (i == 0) {
                simpleTextView.setText(LocaleController.getString("WalletReceive", NUM));
                simpleTextView.setLeftDrawable(this.receiveDrawable);
                this.receiveTextView = simpleTextView;
                this.receiveButton = frameLayout;
            } else {
                simpleTextView.setText(LocaleController.getString("WalletSend", NUM));
                simpleTextView.setLeftDrawable(this.sendDrawable);
                this.sendTextView = simpleTextView;
                this.sendButton = frameLayout;
            }
            frameLayout.addView(simpleTextView, LayoutHelper.createFrame(-1, -2, 17));
            i++;
        }
    }

    public /* synthetic */ void lambda$new$0$WalletBalanceCell(View view) {
        if (view == this.receiveButton) {
            onReceivePressed();
        } else {
            onSendPressed();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int i3;
        int size = View.MeasureSpec.getSize(i);
        if (this.sendButton.getVisibility() == 0) {
            i3 = (size - AndroidUtilities.dp(80.0f)) / 2;
        } else {
            i3 = size - AndroidUtilities.dp(64.0f);
        }
        ((FrameLayout.LayoutParams) this.receiveButton.getLayoutParams()).width = i3;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.sendButton.getLayoutParams();
        layoutParams.width = i3;
        layoutParams.leftMargin = AndroidUtilities.dp(48.0f) + i3;
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(242.0f), NUM));
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.gemDrawable.stop();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.gemDrawable.start();
    }

    public void setBalance(long j) {
        int i = 0;
        if (j >= 0) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(TonController.formatCurrency(j));
            int indexOf = TextUtils.indexOf(spannableStringBuilder, '.');
            if (indexOf >= 0) {
                spannableStringBuilder.setSpan(new TypefaceSpan(this.defaultTypeFace, AndroidUtilities.dp(27.0f)), indexOf + 1, spannableStringBuilder.length(), 33);
            }
            this.valueTextView.setText(spannableStringBuilder);
            this.valueTextView.setTranslationX(0.0f);
            this.yourBalanceTextView.setVisibility(0);
        } else {
            this.valueTextView.setText("");
            this.valueTextView.setTranslationX((float) (-AndroidUtilities.dp(4.0f)));
            this.yourBalanceTextView.setVisibility(8);
        }
        if (j <= 0) {
            i = 8;
        }
        if (this.sendButton.getVisibility() != i) {
            this.sendButton.setVisibility(i);
        }
    }
}

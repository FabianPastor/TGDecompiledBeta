package org.telegram.ui.Wallet;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
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
    private RLottieDrawable gemDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f), false);
    private FrameLayout receiveButton;
    private Drawable receiveDrawable;
    private SimpleTextView receiveTextView;
    private FrameLayout sendButton;
    private Drawable sendDrawable;
    private SimpleTextView sendTextView;
    private SimpleTextView valueTextView;
    private TextView yourBalanceTextView;

    /* Access modifiers changed, original: protected */
    public void onReceivePressed() {
    }

    /* Access modifiers changed, original: protected */
    public void onSendPressed() {
    }

    public WalletBalanceCell(Context context) {
        super(context);
        this.valueTextView = new SimpleTextView(context);
        String str = "wallet_whiteText";
        this.valueTextView.setTextColor(Theme.getColor(str));
        this.valueTextView.setTextSize(41);
        this.valueTextView.setDrawablePadding(AndroidUtilities.dp(7.0f));
        this.valueTextView.setGravity(1);
        String str2 = "fonts/rmedium.ttf";
        this.valueTextView.setTypeface(AndroidUtilities.getTypeface(str2));
        addView(this.valueTextView, LayoutHelper.createFrame(-1, -2.0f, 1, 0.0f, 35.0f, 0.0f, 0.0f));
        this.gemDrawable.setAutoRepeat(1);
        this.gemDrawable.setAllowDecodeSingleFrame(true);
        this.gemDrawable.addParentView(this.valueTextView);
        this.valueTextView.setRightDrawable(this.gemDrawable);
        this.gemDrawable.start();
        this.yourBalanceTextView = new TextView(context);
        this.yourBalanceTextView.setTextSize(1, 14.0f);
        this.yourBalanceTextView.setTextColor(Theme.getColor(str));
        this.defaultTypeFace = this.yourBalanceTextView.getTypeface();
        this.yourBalanceTextView.setText(LocaleController.getString("WalletYourBalance", NUM));
        addView(this.yourBalanceTextView, LayoutHelper.createFrame(-2, -2.0f, 1, 0.0f, 90.0f, 0.0f, 0.0f));
        this.receiveDrawable = context.getResources().getDrawable(NUM).mutate();
        String str3 = "wallet_buttonText";
        this.receiveDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str3), Mode.MULTIPLY));
        this.sendDrawable = context.getResources().getDrawable(NUM).mutate();
        this.sendDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str3), Mode.MULTIPLY));
        int i = 0;
        while (i < 2) {
            FrameLayout frameLayout = new FrameLayout(context);
            frameLayout.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("wallet_buttonBackground"), Theme.getColor("wallet_buttonPressedBackground")));
            addView(frameLayout, LayoutHelper.createFrame(-1, 42.0f, 51, i == 0 ? 32.0f : 16.0f, 168.0f, 0.0f, 0.0f));
            frameLayout.setOnClickListener(new -$$Lambda$WalletBalanceCell$RwNV3caeIC2AdPExp7uzz72hLXg(this));
            SimpleTextView simpleTextView = new SimpleTextView(context);
            simpleTextView.setTextColor(Theme.getColor(str3));
            simpleTextView.setTextSize(14);
            simpleTextView.setDrawablePadding(AndroidUtilities.dp(6.0f));
            simpleTextView.setGravity(1);
            simpleTextView.setTypeface(AndroidUtilities.getTypeface(str2));
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

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        i = MeasureSpec.getSize(i);
        if (this.sendButton.getVisibility() == 0) {
            i2 = (i - AndroidUtilities.dp(80.0f)) / 2;
        } else {
            i2 = i - AndroidUtilities.dp(64.0f);
        }
        ((LayoutParams) this.receiveButton.getLayoutParams()).width = i2;
        LayoutParams layoutParams = (LayoutParams) this.sendButton.getLayoutParams();
        layoutParams.width = i2;
        layoutParams.leftMargin = AndroidUtilities.dp(48.0f) + i2;
        super.onMeasure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(242.0f), NUM));
    }

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.gemDrawable.stop();
    }

    /* Access modifiers changed, original: protected */
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

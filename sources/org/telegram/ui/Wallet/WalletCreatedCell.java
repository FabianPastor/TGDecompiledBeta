package org.telegram.ui.Wallet;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;

public class WalletCreatedCell extends FrameLayout {
    private TextView addressTextView;
    private TextView addressValueTextView;
    private TextView createdTextView;
    private RLottieImageView imageView;

    public WalletCreatedCell(Context context) {
        super(context);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        addView(linearLayout, LayoutHelper.createFrame(-1, -2, 17));
        this.imageView = new RLottieImageView(context);
        this.imageView.setAnimation(NUM, 112, 112);
        this.imageView.setScaleType(ScaleType.CENTER);
        this.imageView.playAnimation();
        linearLayout.addView(this.imageView, LayoutHelper.createLinear(-2, -2, 1));
        this.createdTextView = new TextView(context);
        this.createdTextView.setTextSize(1, 24.0f);
        this.createdTextView.setText(LocaleController.getString("WalletCreated", NUM));
        String str = "wallet_blackText";
        this.createdTextView.setTextColor(Theme.getColor(str));
        linearLayout.addView(this.createdTextView, LayoutHelper.createLinear(-2, -2, 1, 0, 13, 0, 0));
        this.addressTextView = new TextView(context);
        this.addressTextView.setTextSize(1, 15.0f);
        this.addressTextView.setText(LocaleController.getString("WalletYourAddress", NUM));
        this.addressTextView.setTextColor(Theme.getColor("wallet_grayText"));
        linearLayout.addView(this.addressTextView, LayoutHelper.createLinear(-2, -2, 1, 0, 22, 0, 0));
        this.addressTextView.setOnLongClickListener(new -$$Lambda$WalletCreatedCell$VW7UnDVm2r3-O7MflYVhMCX1gRQ(this));
        this.addressValueTextView = new TextView(context);
        this.addressValueTextView.setTextSize(1, 15.0f);
        this.addressValueTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmono.ttf"));
        this.addressValueTextView.setText("t\nt");
        this.addressValueTextView.setAlpha(0.0f);
        this.addressValueTextView.setTextColor(Theme.getColor(str));
        linearLayout.addView(this.addressValueTextView, LayoutHelper.createLinear(-2, -2, 1, 0, 13, 0, 0));
    }

    public /* synthetic */ boolean lambda$new$0$WalletCreatedCell(View view) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ton://transfer/");
        stringBuilder.append(this.addressValueTextView.getText().toString().replace("\n", ""));
        AndroidUtilities.addToClipboard(stringBuilder.toString());
        Toast.makeText(view.getContext(), LocaleController.getString("WalletTransactionAddressCopied", NUM), 0).show();
        return true;
    }

    public void setAddress(String str) {
        if (!TextUtils.isEmpty(str)) {
            StringBuilder stringBuilder = new StringBuilder(str);
            stringBuilder.insert(stringBuilder.length() / 2, 10);
            this.addressValueTextView.setText(stringBuilder);
            this.addressValueTextView.setAlpha(1.0f);
        }
    }
}

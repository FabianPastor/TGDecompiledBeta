package org.telegram.ui.Cells;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.URLSpanNoUnderline;

public class SettingsSuggestionCell extends LinearLayout {
    private int currentAccount = UserConfig.selectedAccount;
    private int currentType;
    private TextView detailTextView;
    private TextView noButton;
    private Theme.ResourcesProvider resourcesProvider;
    private TextView textView;
    private TextView yesButton;

    /* access modifiers changed from: protected */
    public void onNoClick(int i) {
    }

    /* access modifiers changed from: protected */
    public void onYesClick(int i) {
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SettingsSuggestionCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        Context context2 = context;
        Theme.ResourcesProvider resourcesProvider3 = resourcesProvider2;
        this.resourcesProvider = resourcesProvider3;
        setOrientation(1);
        TextView textView2 = new TextView(context2);
        this.textView = textView2;
        textView2.setTextSize(1, 15.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueHeader", resourcesProvider3));
        addView(this.textView, LayoutHelper.createLinear(-1, -2, (LocaleController.isRTL ? 5 : 3) | 48, 21, 15, 21, 0));
        TextView textView3 = new TextView(context2);
        this.detailTextView = textView3;
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2", resourcesProvider3));
        this.detailTextView.setTextSize(1, 13.0f);
        this.detailTextView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText", resourcesProvider3));
        this.detailTextView.setHighlightColor(Theme.getColor("windowBackgroundWhiteLinkSelection", resourcesProvider3));
        this.detailTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        this.detailTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.detailTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 21, 8, 21, 0));
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        addView(linearLayout, LayoutHelper.createLinear(-1, 40, 21.0f, 17.0f, 21.0f, 20.0f));
        int i = 0;
        while (i < 2) {
            TextView textView4 = new TextView(context2);
            textView4.setBackground(Theme.AdaptiveRipple.filledRect("featuredStickers_addButton", 4.0f));
            textView4.setLines(1);
            textView4.setSingleLine(true);
            textView4.setGravity(1);
            textView4.setEllipsize(TextUtils.TruncateAt.END);
            textView4.setGravity(17);
            textView4.setTextColor(Theme.getColor("featuredStickers_buttonText", resourcesProvider3));
            textView4.setTextSize(1, 14.0f);
            textView4.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            linearLayout.addView(textView4, LayoutHelper.createLinear(0, 40, 0.5f, i == 0 ? 0 : 4, 0, i == 0 ? 4 : 0, 0));
            if (i == 0) {
                this.yesButton = textView4;
                textView4.setOnClickListener(new SettingsSuggestionCell$$ExternalSyntheticLambda1(this));
            } else {
                this.noButton = textView4;
                textView4.setOnClickListener(new SettingsSuggestionCell$$ExternalSyntheticLambda0(this));
            }
            i++;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        onYesClick(this.currentType);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view) {
        onNoClick(this.currentType);
    }

    public void setType(int i) {
        this.currentType = i;
        if (i == 0) {
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(UserConfig.getInstance(this.currentAccount).clientUserId));
            TextView textView2 = this.textView;
            int i2 = R.string.CheckPhoneNumber;
            PhoneFormat instance = PhoneFormat.getInstance();
            textView2.setText(LocaleController.formatString("CheckPhoneNumber", i2, instance.format("+" + user.phone)));
            String string = LocaleController.getString("CheckPhoneNumberInfo", R.string.CheckPhoneNumberInfo);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string);
            int indexOf = string.indexOf("**");
            int lastIndexOf = string.lastIndexOf("**");
            if (indexOf >= 0 && lastIndexOf >= 0 && indexOf != lastIndexOf) {
                spannableStringBuilder.replace(lastIndexOf, lastIndexOf + 2, "");
                spannableStringBuilder.replace(indexOf, indexOf + 2, "");
                try {
                    spannableStringBuilder.setSpan(new URLSpanNoUnderline(LocaleController.getString("CheckPhoneNumberLearnMoreUrl", R.string.CheckPhoneNumberLearnMoreUrl)), indexOf, lastIndexOf - 2, 33);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            this.detailTextView.setText(spannableStringBuilder);
            this.yesButton.setText(LocaleController.getString("CheckPhoneNumberYes", R.string.CheckPhoneNumberYes));
            this.noButton.setText(LocaleController.getString("CheckPhoneNumberNo", R.string.CheckPhoneNumberNo));
        } else if (i == 1) {
            this.textView.setText(LocaleController.getString("YourPasswordHeader", R.string.YourPasswordHeader));
            this.detailTextView.setText(LocaleController.getString("YourPasswordRemember", R.string.YourPasswordRemember));
            this.yesButton.setText(LocaleController.getString("YourPasswordRememberYes", R.string.YourPasswordRememberYes));
            this.noButton.setText(LocaleController.getString("YourPasswordRememberNo", R.string.YourPasswordRememberNo));
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), i2);
    }
}

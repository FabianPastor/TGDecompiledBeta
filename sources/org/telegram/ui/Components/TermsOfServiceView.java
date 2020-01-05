package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build.VERSION;
import android.text.SpannableStringBuilder;
import android.text.style.BulletSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AndroidUtilities.LinkMovementMethodMy;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_deleteAccount;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_help_acceptTermsOfService;
import org.telegram.tgnet.TLRPC.TL_help_termsOfService;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.Theme;

public class TermsOfServiceView extends FrameLayout {
    private int currentAccount;
    private TL_help_termsOfService currentTos;
    private TermsOfServiceViewDelegate delegate;
    private ScrollView scrollView;
    private TextView textView;
    private TextView titleTextView;

    public interface TermsOfServiceViewDelegate {
        void onAcceptTerms(int i);

        void onDeclineTerms(int i);
    }

    static /* synthetic */ void lambda$accept$7(TLObject tLObject, TL_error tL_error) {
    }

    public TermsOfServiceView(Context context) {
        Context context2 = context;
        super(context);
        setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        int i = VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
        if (i > 0) {
            View view = new View(context2);
            view.setBackgroundColor(-16777216);
            addView(view, new LayoutParams(-1, i));
        }
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        ImageView imageView = new ImageView(context2);
        imageView.setImageResource(NUM);
        linearLayout.addView(imageView, LayoutHelper.createLinear(-2, -2, 3, 0, 28, 0, 0));
        this.titleTextView = new TextView(context2);
        String str = "windowBackgroundWhiteBlackText";
        this.titleTextView.setTextColor(Theme.getColor(str));
        this.titleTextView.setTextSize(1, 17.0f);
        String str2 = "fonts/rmedium.ttf";
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface(str2));
        this.titleTextView.setText(LocaleController.getString("PrivacyPolicyAndTerms", NUM));
        linearLayout.addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 3, 0, 20, 0, 0));
        this.textView = new TextView(context2);
        this.textView.setTextColor(Theme.getColor(str));
        this.textView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
        this.textView.setTextSize(1, 15.0f);
        this.textView.setMovementMethod(new LinkMovementMethodMy());
        this.textView.setGravity(51);
        this.textView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        linearLayout.addView(this.textView, LayoutHelper.createLinear(-1, -2, 3, 0, 15, 0, 15));
        this.scrollView = new ScrollView(context2);
        this.scrollView.setVerticalScrollBarEnabled(false);
        this.scrollView.setOverScrollMode(2);
        this.scrollView.setPadding(AndroidUtilities.dp(24.0f), i, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(75.0f));
        this.scrollView.addView(linearLayout, new LayoutParams(-1, -2));
        addView(this.scrollView, LayoutHelper.createLinear(-1, -2));
        TextView textView = new TextView(context2);
        textView.setText(LocaleController.getString("Decline", NUM).toUpperCase());
        textView.setGravity(17);
        textView.setTypeface(AndroidUtilities.getTypeface(str2));
        String str3 = "windowBackgroundWhiteGrayText";
        textView.setTextColor(Theme.getColor(str3));
        textView.setTextSize(1, 14.0f);
        textView.setBackground(Theme.getRoundRectSelectorDrawable(Theme.getColor(str3)));
        textView.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f));
        addView(textView, LayoutHelper.createFrame(-2, -2.0f, 83, 16.0f, 0.0f, 16.0f, 16.0f));
        textView.setOnClickListener(new -$$Lambda$TermsOfServiceView$iC8ls3ZLGFSjERsGKqRFXaObEoI(this));
        textView = new TextView(context2);
        textView.setText(LocaleController.getString("Accept", NUM));
        textView.setGravity(17);
        textView.setTypeface(AndroidUtilities.getTypeface(str2));
        textView.setTextColor(-1);
        textView.setTextSize(1, 14.0f);
        textView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), -11491093, -12346402));
        textView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        addView(textView, LayoutHelper.createFrame(-2, 42.0f, 85, 16.0f, 0.0f, 16.0f, 16.0f));
        textView.setOnClickListener(new -$$Lambda$TermsOfServiceView$SxA1BjEDdG4D6RyZHS_wxrllcTk(this));
        View view2 = new View(context2);
        view2.setBackgroundColor(Theme.getColor("divider"));
        LayoutParams layoutParams = new LayoutParams(-1, 1);
        layoutParams.bottomMargin = AndroidUtilities.dp(75.0f);
        layoutParams.gravity = 80;
        addView(view2, layoutParams);
    }

    public /* synthetic */ void lambda$new$4$TermsOfServiceView(View view) {
        Builder builder = new Builder(view.getContext());
        builder.setTitle(LocaleController.getString("TermsOfService", NUM));
        builder.setPositiveButton(LocaleController.getString("DeclineDeactivate", NUM), new -$$Lambda$TermsOfServiceView$8TrTmo7wbIKz6qyAsVbWamu37pI(this));
        builder.setNegativeButton(LocaleController.getString("Back", NUM), null);
        builder.setMessage(LocaleController.getString("TosUpdateDecline", NUM));
        builder.show();
    }

    public /* synthetic */ void lambda$null$3$TermsOfServiceView(DialogInterface dialogInterface, int i) {
        Builder builder = new Builder(getContext());
        builder.setMessage(LocaleController.getString("TosDeclineDeleteAccount", NUM));
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setPositiveButton(LocaleController.getString("Deactivate", NUM), new -$$Lambda$TermsOfServiceView$hBaTjsArXh_sRa7tZwKAz5EvAJE(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        builder.show();
    }

    public /* synthetic */ void lambda$null$2$TermsOfServiceView(DialogInterface dialogInterface, int i) {
        AlertDialog alertDialog = new AlertDialog(getContext(), 3);
        alertDialog.setCanCacnel(false);
        TL_account_deleteAccount tL_account_deleteAccount = new TL_account_deleteAccount();
        tL_account_deleteAccount.reason = "Decline ToS update";
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_deleteAccount, new -$$Lambda$TermsOfServiceView$wpeq4s3tDOyR201M7CIeMmBtmDI(this, alertDialog));
        alertDialog.show();
    }

    public /* synthetic */ void lambda$null$1$TermsOfServiceView(AlertDialog alertDialog, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$TermsOfServiceView$WGt9_Qk4hrF5TQ_c-TZPa64SZbw(this, alertDialog, tLObject, tL_error));
    }

    public /* synthetic */ void lambda$null$0$TermsOfServiceView(AlertDialog alertDialog, TLObject tLObject, TL_error tL_error) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tLObject instanceof TL_boolTrue) {
            MessagesController.getInstance(this.currentAccount).performLogout(0);
        } else if (tL_error == null || tL_error.code != -1000) {
            CharSequence string = LocaleController.getString("ErrorOccurred", NUM);
            if (tL_error != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(string);
                stringBuilder.append("\n");
                stringBuilder.append(tL_error.text);
                string = stringBuilder.toString();
            }
            Builder builder = new Builder(getContext());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(string);
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            builder.show();
        }
    }

    public /* synthetic */ void lambda$new$6$TermsOfServiceView(View view) {
        if (this.currentTos.min_age_confirm != 0) {
            Builder builder = new Builder(view.getContext());
            builder.setTitle(LocaleController.getString("TosAgeTitle", NUM));
            builder.setPositiveButton(LocaleController.getString("Agree", NUM), new -$$Lambda$TermsOfServiceView$SeQmuFflbDIWWWYA0Y43_VRunls(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            Object[] objArr = new Object[1];
            objArr[0] = LocaleController.formatPluralString("Years", this.currentTos.min_age_confirm);
            builder.setMessage(LocaleController.formatString("TosAgeText", NUM, objArr));
            builder.show();
            return;
        }
        accept();
    }

    public /* synthetic */ void lambda$null$5$TermsOfServiceView(DialogInterface dialogInterface, int i) {
        accept();
    }

    private void accept() {
        this.delegate.onAcceptTerms(this.currentAccount);
        TL_help_acceptTermsOfService tL_help_acceptTermsOfService = new TL_help_acceptTermsOfService();
        tL_help_acceptTermsOfService.id = this.currentTos.id;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_help_acceptTermsOfService, -$$Lambda$TermsOfServiceView$YbrdSyJlv9WqhaCW47-DkBCPOAM.INSTANCE);
    }

    public void show(int i, TL_help_termsOfService tL_help_termsOfService) {
        if (getVisibility() != 0) {
            setVisibility(0);
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(tL_help_termsOfService.text);
        MessageObject.addEntitiesToText(spannableStringBuilder, tL_help_termsOfService.entities, false, 0, false, false, false);
        addBulletsToText(spannableStringBuilder, '-', AndroidUtilities.dp(10.0f), -11491093, AndroidUtilities.dp(4.0f));
        this.textView.setText(spannableStringBuilder);
        this.currentTos = tL_help_termsOfService;
        this.currentAccount = i;
    }

    public void setDelegate(TermsOfServiceViewDelegate termsOfServiceViewDelegate) {
        this.delegate = termsOfServiceViewDelegate;
    }

    private static void addBulletsToText(SpannableStringBuilder spannableStringBuilder, char c, int i, int i2, int i3) {
        int length = spannableStringBuilder.length() - 2;
        for (int i4 = 0; i4 < length; i4++) {
            if (spannableStringBuilder.charAt(i4) == 10) {
                int i5 = i4 + 1;
                if (spannableStringBuilder.charAt(i5) == c) {
                    int i6 = i4 + 2;
                    if (spannableStringBuilder.charAt(i6) == ' ') {
                        BulletSpan bulletSpan = new BulletSpan(i, i2, i3);
                        spannableStringBuilder.replace(i5, i4 + 3, "\u0000\u0000");
                        spannableStringBuilder.setSpan(bulletSpan, i5, i6, 33);
                    }
                }
            }
        }
    }
}

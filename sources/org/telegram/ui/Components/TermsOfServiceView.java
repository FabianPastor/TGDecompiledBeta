package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_account_deleteAccount;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_help_acceptTermsOfService;
import org.telegram.tgnet.TLRPC$TL_help_termsOfService;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;

public class TermsOfServiceView extends FrameLayout {
    private int currentAccount;
    private TLRPC$TL_help_termsOfService currentTos;
    private TermsOfServiceViewDelegate delegate;
    private ScrollView scrollView;
    private TextView textView;
    private TextView titleTextView;

    public interface TermsOfServiceViewDelegate {
        void onAcceptTerms(int i);
    }

    static /* synthetic */ void lambda$accept$7(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public TermsOfServiceView(Context context) {
        super(context);
        Context context2 = context;
        setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        int i = Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
        if (i > 0) {
            View view = new View(context2);
            view.setBackgroundColor(-16777216);
            addView(view, new FrameLayout.LayoutParams(-1, i));
        }
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        ImageView imageView = new ImageView(context2);
        imageView.setImageResource(NUM);
        linearLayout.addView(imageView, LayoutHelper.createLinear(-2, -2, 3, 0, 28, 0, 0));
        TextView textView2 = new TextView(context2);
        this.titleTextView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.titleTextView.setTextSize(1, 17.0f);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setText(LocaleController.getString("PrivacyPolicyAndTerms", NUM));
        linearLayout.addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 3, 0, 20, 0, 0));
        TextView textView3 = new TextView(context2);
        this.textView = textView3;
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
        this.textView.setTextSize(1, 15.0f);
        this.textView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        this.textView.setGravity(51);
        this.textView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        linearLayout.addView(this.textView, LayoutHelper.createLinear(-1, -2, 3, 0, 15, 0, 15));
        ScrollView scrollView2 = new ScrollView(context2);
        this.scrollView = scrollView2;
        scrollView2.setVerticalScrollBarEnabled(false);
        this.scrollView.setOverScrollMode(2);
        this.scrollView.setPadding(AndroidUtilities.dp(24.0f), i, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(75.0f));
        this.scrollView.addView(linearLayout, new FrameLayout.LayoutParams(-1, -2));
        addView(this.scrollView, LayoutHelper.createLinear(-1, -2));
        TextView textView4 = new TextView(context2);
        textView4.setText(LocaleController.getString("Decline", NUM).toUpperCase());
        textView4.setGravity(17);
        textView4.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView4.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        textView4.setTextSize(1, 14.0f);
        textView4.setBackground(Theme.getRoundRectSelectorDrawable(Theme.getColor("windowBackgroundWhiteGrayText")));
        textView4.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f));
        addView(textView4, LayoutHelper.createFrame(-2, -2.0f, 83, 16.0f, 0.0f, 16.0f, 16.0f));
        textView4.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TermsOfServiceView.this.lambda$new$4$TermsOfServiceView(view);
            }
        });
        TextView textView5 = new TextView(context2);
        textView5.setText(LocaleController.getString("Accept", NUM));
        textView5.setGravity(17);
        textView5.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView5.setTextColor(-1);
        textView5.setTextSize(1, 14.0f);
        textView5.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), -11491093, -12346402));
        textView5.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        addView(textView5, LayoutHelper.createFrame(-2, 42.0f, 85, 16.0f, 0.0f, 16.0f, 16.0f));
        textView5.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TermsOfServiceView.this.lambda$new$6$TermsOfServiceView(view);
            }
        });
        View view2 = new View(context2);
        view2.setBackgroundColor(Theme.getColor("divider"));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, 1);
        layoutParams.bottomMargin = AndroidUtilities.dp(75.0f);
        layoutParams.gravity = 80;
        addView(view2, layoutParams);
    }

    public /* synthetic */ void lambda$new$4$TermsOfServiceView(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle(LocaleController.getString("TermsOfService", NUM));
        builder.setPositiveButton(LocaleController.getString("DeclineDeactivate", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                TermsOfServiceView.this.lambda$null$3$TermsOfServiceView(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Back", NUM), (DialogInterface.OnClickListener) null);
        builder.setMessage(LocaleController.getString("TosUpdateDecline", NUM));
        builder.show();
    }

    public /* synthetic */ void lambda$null$3$TermsOfServiceView(DialogInterface dialogInterface, int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(LocaleController.getString("TosDeclineDeleteAccount", NUM));
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setPositiveButton(LocaleController.getString("Deactivate", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                TermsOfServiceView.this.lambda$null$2$TermsOfServiceView(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.show();
    }

    public /* synthetic */ void lambda$null$2$TermsOfServiceView(DialogInterface dialogInterface, int i) {
        AlertDialog alertDialog = new AlertDialog(getContext(), 3);
        alertDialog.setCanCacnel(false);
        TLRPC$TL_account_deleteAccount tLRPC$TL_account_deleteAccount = new TLRPC$TL_account_deleteAccount();
        tLRPC$TL_account_deleteAccount.reason = "Decline ToS update";
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_deleteAccount, new RequestDelegate(alertDialog) {
            public final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                TermsOfServiceView.this.lambda$null$1$TermsOfServiceView(this.f$1, tLObject, tLRPC$TL_error);
            }
        });
        alertDialog.show();
    }

    public /* synthetic */ void lambda$null$1$TermsOfServiceView(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tLObject, tLRPC$TL_error) {
            public final /* synthetic */ AlertDialog f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ TLRPC$TL_error f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                TermsOfServiceView.this.lambda$null$0$TermsOfServiceView(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$TermsOfServiceView(AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (tLObject instanceof TLRPC$TL_boolTrue) {
            MessagesController.getInstance(this.currentAccount).performLogout(0);
        } else if (tLRPC$TL_error == null || tLRPC$TL_error.code != -1000) {
            String string = LocaleController.getString("ErrorOccurred", NUM);
            if (tLRPC$TL_error != null) {
                string = string + "\n" + tLRPC$TL_error.text;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(string);
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            builder.show();
        }
    }

    public /* synthetic */ void lambda$new$6$TermsOfServiceView(View view) {
        if (this.currentTos.min_age_confirm != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle(LocaleController.getString("TosAgeTitle", NUM));
            builder.setPositiveButton(LocaleController.getString("Agree", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    TermsOfServiceView.this.lambda$null$5$TermsOfServiceView(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            builder.setMessage(LocaleController.formatString("TosAgeText", NUM, LocaleController.formatPluralString("Years", this.currentTos.min_age_confirm)));
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
        TLRPC$TL_help_acceptTermsOfService tLRPC$TL_help_acceptTermsOfService = new TLRPC$TL_help_acceptTermsOfService();
        tLRPC$TL_help_acceptTermsOfService.id = this.currentTos.id;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_help_acceptTermsOfService, $$Lambda$TermsOfServiceView$YbrdSyJlv9WqhaCW47DkBCPOAM.INSTANCE);
    }

    public void show(int i, TLRPC$TL_help_termsOfService tLRPC$TL_help_termsOfService) {
        if (getVisibility() != 0) {
            setVisibility(0);
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(tLRPC$TL_help_termsOfService.text);
        MessageObject.addEntitiesToText(spannableStringBuilder, tLRPC$TL_help_termsOfService.entities, false, false, false, false);
        addBulletsToText(spannableStringBuilder, '-', AndroidUtilities.dp(10.0f), -11491093, AndroidUtilities.dp(4.0f));
        this.textView.setText(spannableStringBuilder);
        this.currentTos = tLRPC$TL_help_termsOfService;
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

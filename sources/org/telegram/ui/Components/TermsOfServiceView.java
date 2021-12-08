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
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;

public class TermsOfServiceView extends FrameLayout {
    private int currentAccount;
    private TLRPC.TL_help_termsOfService currentTos;
    private TermsOfServiceViewDelegate delegate;
    private ScrollView scrollView;
    private TextView textView;
    private TextView titleTextView;

    public interface TermsOfServiceViewDelegate {
        void onAcceptTerms(int i);

        void onDeclineTerms(int i);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public TermsOfServiceView(Context context) {
        super(context);
        Context context2 = context;
        setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        int top = Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
        if (top > 0) {
            View view = new View(context2);
            view.setBackgroundColor(-16777216);
            addView(view, new FrameLayout.LayoutParams(-1, top));
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
        this.scrollView.setPadding(AndroidUtilities.dp(24.0f), top, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(75.0f));
        this.scrollView.addView(linearLayout, new FrameLayout.LayoutParams(-1, -2));
        addView(this.scrollView, LayoutHelper.createLinear(-1, -2));
        TextView declineTextView = new TextView(context2);
        declineTextView.setText(LocaleController.getString("Decline", NUM).toUpperCase());
        declineTextView.setGravity(17);
        declineTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        declineTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        declineTextView.setTextSize(1, 14.0f);
        declineTextView.setBackground(Theme.getRoundRectSelectorDrawable(Theme.getColor("windowBackgroundWhiteGrayText")));
        declineTextView.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f));
        addView(declineTextView, LayoutHelper.createFrame(-2, -2.0f, 83, 16.0f, 0.0f, 16.0f, 16.0f));
        declineTextView.setOnClickListener(new TermsOfServiceView$$ExternalSyntheticLambda3(this));
        TextView acceptTextView = new TextView(context2);
        acceptTextView.setText(LocaleController.getString("Accept", NUM));
        acceptTextView.setGravity(17);
        acceptTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        acceptTextView.setTextColor(-1);
        acceptTextView.setTextSize(1, 14.0f);
        acceptTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), -11491093, -12346402));
        acceptTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        addView(acceptTextView, LayoutHelper.createFrame(-2, 42.0f, 85, 16.0f, 0.0f, 16.0f, 16.0f));
        acceptTextView.setOnClickListener(new TermsOfServiceView$$ExternalSyntheticLambda4(this));
        View lineView = new View(context2);
        lineView.setBackgroundColor(Theme.getColor("divider"));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, 1);
        params.bottomMargin = AndroidUtilities.dp(75.0f);
        params.gravity = 80;
        addView(lineView, params);
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-TermsOfServiceView  reason: not valid java name */
    public /* synthetic */ void m2670lambda$new$4$orgtelegramuiComponentsTermsOfServiceView(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle(LocaleController.getString("TermsOfService", NUM));
        builder.setPositiveButton(LocaleController.getString("DeclineDeactivate", NUM), new TermsOfServiceView$$ExternalSyntheticLambda1(this));
        builder.setNegativeButton(LocaleController.getString("Back", NUM), (DialogInterface.OnClickListener) null);
        builder.setMessage(LocaleController.getString("TosUpdateDecline", NUM));
        builder.show();
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-TermsOfServiceView  reason: not valid java name */
    public /* synthetic */ void m2669lambda$new$3$orgtelegramuiComponentsTermsOfServiceView(DialogInterface dialog, int which) {
        AlertDialog.Builder builder12 = new AlertDialog.Builder(getContext());
        builder12.setMessage(LocaleController.getString("TosDeclineDeleteAccount", NUM));
        builder12.setTitle(LocaleController.getString("AppName", NUM));
        builder12.setPositiveButton(LocaleController.getString("Deactivate", NUM), new TermsOfServiceView$$ExternalSyntheticLambda0(this));
        builder12.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder12.show();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-TermsOfServiceView  reason: not valid java name */
    public /* synthetic */ void m2668lambda$new$2$orgtelegramuiComponentsTermsOfServiceView(DialogInterface dialogInterface, int i) {
        AlertDialog progressDialog = new AlertDialog(getContext(), 3);
        progressDialog.setCanCacnel(false);
        TLRPC.TL_account_deleteAccount req = new TLRPC.TL_account_deleteAccount();
        req.reason = "Decline ToS update";
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new TermsOfServiceView$$ExternalSyntheticLambda6(this, progressDialog));
        progressDialog.show();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-TermsOfServiceView  reason: not valid java name */
    public /* synthetic */ void m2667lambda$new$1$orgtelegramuiComponentsTermsOfServiceView(AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new TermsOfServiceView$$ExternalSyntheticLambda5(this, progressDialog, response, error));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-TermsOfServiceView  reason: not valid java name */
    public /* synthetic */ void m2666lambda$new$0$orgtelegramuiComponentsTermsOfServiceView(AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (response instanceof TLRPC.TL_boolTrue) {
            MessagesController.getInstance(this.currentAccount).performLogout(0);
        } else if (error == null || error.code != -1000) {
            String errorText = LocaleController.getString("ErrorOccurred", NUM);
            if (error != null) {
                errorText = errorText + "\n" + error.text;
            }
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
            builder1.setTitle(LocaleController.getString("AppName", NUM));
            builder1.setMessage(errorText);
            builder1.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            builder1.show();
        }
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-TermsOfServiceView  reason: not valid java name */
    public /* synthetic */ void m2672lambda$new$6$orgtelegramuiComponentsTermsOfServiceView(View view) {
        if (this.currentTos.min_age_confirm != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle(LocaleController.getString("TosAgeTitle", NUM));
            builder.setPositiveButton(LocaleController.getString("Agree", NUM), new TermsOfServiceView$$ExternalSyntheticLambda2(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            builder.setMessage(LocaleController.formatString("TosAgeText", NUM, LocaleController.formatPluralString("Years", this.currentTos.min_age_confirm)));
            builder.show();
            return;
        }
        accept();
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-TermsOfServiceView  reason: not valid java name */
    public /* synthetic */ void m2671lambda$new$5$orgtelegramuiComponentsTermsOfServiceView(DialogInterface dialog, int which) {
        accept();
    }

    private void accept() {
        this.delegate.onAcceptTerms(this.currentAccount);
        TLRPC.TL_help_acceptTermsOfService req = new TLRPC.TL_help_acceptTermsOfService();
        req.id = this.currentTos.id;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, TermsOfServiceView$$ExternalSyntheticLambda7.INSTANCE);
    }

    static /* synthetic */ void lambda$accept$7(TLObject response, TLRPC.TL_error error) {
    }

    public void show(int account, TLRPC.TL_help_termsOfService tos) {
        if (getVisibility() != 0) {
            setVisibility(0);
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(tos.text);
        MessageObject.addEntitiesToText(builder, tos.entities, false, false, false, false);
        addBulletsToText(builder, '-', AndroidUtilities.dp(10.0f), -11491093, AndroidUtilities.dp(4.0f));
        this.textView.setText(builder);
        this.currentTos = tos;
        this.currentAccount = account;
    }

    public void setDelegate(TermsOfServiceViewDelegate termsOfServiceViewDelegate) {
        this.delegate = termsOfServiceViewDelegate;
    }

    private static void addBulletsToText(SpannableStringBuilder builder, char bulletChar, int gapWidth, int color, int radius) {
        int until = builder.length() - 2;
        for (int i = 0; i < until; i++) {
            if (builder.charAt(i) == 10 && builder.charAt(i + 1) == bulletChar && builder.charAt(i + 2) == ' ') {
                BulletSpan span = new BulletSpan(gapWidth, color, radius);
                builder.replace(i + 1, i + 3, "\u0000\u0000");
                builder.setSpan(span, i + 1, i + 2, 33);
            }
        }
    }
}

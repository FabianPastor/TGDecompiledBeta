package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build.VERSION;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
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
        int i = VERSION.SDK_INT >= 21 ? (int) (((float) AndroidUtilities.statusBarHeight) / AndroidUtilities.density) : 0;
        if (VERSION.SDK_INT >= 21) {
            View view = new View(context2);
            view.setBackgroundColor(-16777216);
            addView(view, new LayoutParams(-1, AndroidUtilities.statusBarHeight));
        }
        ImageView imageView = new ImageView(context2);
        imageView.setImageResource(NUM);
        addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, (float) (i + 30), 0.0f, 0.0f));
        this.titleTextView = new TextView(context2);
        String str = "windowBackgroundWhiteBlackText";
        this.titleTextView.setTextColor(Theme.getColor(str));
        this.titleTextView.setTextSize(1, 17.0f);
        this.titleTextView.setGravity(51);
        String str2 = "fonts/rmedium.ttf";
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface(str2));
        this.titleTextView.setText(LocaleController.getString("PrivacyPolicyAndTerms", NUM));
        addView(this.titleTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 27.0f, (float) (i + 126), 27.0f, 75.0f));
        this.scrollView = new ScrollView(context2);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("actionBarDefault"));
        addView(this.scrollView, LayoutHelper.createFrame(-2, -1.0f, 51, 27.0f, (float) (i + 160), 27.0f, 75.0f));
        this.textView = new TextView(context2);
        this.textView.setTextColor(Theme.getColor(str));
        this.textView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
        this.textView.setTextSize(1, 15.0f);
        this.textView.setMovementMethod(new LinkMovementMethodMy());
        this.textView.setGravity(51);
        this.textView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        this.scrollView.addView(this.textView, new LayoutParams(-2, -2));
        TextView textView = new TextView(context2);
        textView.setText(LocaleController.getString("Decline", NUM).toUpperCase());
        textView.setGravity(17);
        textView.setTypeface(AndroidUtilities.getTypeface(str2));
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        textView.setTextSize(1, 16.0f);
        textView.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f));
        addView(textView, LayoutHelper.createFrame(-2, -2.0f, 83, 16.0f, 0.0f, 16.0f, 16.0f));
        textView.setOnClickListener(new -$$Lambda$TermsOfServiceView$iC8ls3ZLGFSjERsGKqRFXaObEoI(this));
        textView = new TextView(context2);
        textView.setText(LocaleController.getString("Accept", NUM).toUpperCase());
        textView.setGravity(17);
        textView.setTypeface(AndroidUtilities.getTypeface(str2));
        textView.setTextColor(-1);
        textView.setTextSize(1, 16.0f);
        textView.setBackgroundResource(NUM);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            String str3 = "translationZ";
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(textView, str3, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(textView, str3, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            textView.setStateListAnimator(stateListAnimator);
        }
        textView.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f));
        addView(textView, LayoutHelper.createFrame(-2, -2.0f, 85, 16.0f, 0.0f, 16.0f, 16.0f));
        textView.setOnClickListener(new -$$Lambda$TermsOfServiceView$SxA1BjEDdG4D6RyZHS_wxrllcTk(this));
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
        this.textView.setText(spannableStringBuilder);
        this.currentTos = tL_help_termsOfService;
        this.currentAccount = i;
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        measureChildWithMargins(this.titleTextView, i, 0, i2, 0);
        ((LayoutParams) this.scrollView.getLayoutParams()).topMargin = AndroidUtilities.dp(156.0f) + this.titleTextView.getMeasuredHeight();
        super.onMeasure(i, i2);
    }

    public void setDelegate(TermsOfServiceViewDelegate termsOfServiceViewDelegate) {
        this.delegate = termsOfServiceViewDelegate;
    }
}

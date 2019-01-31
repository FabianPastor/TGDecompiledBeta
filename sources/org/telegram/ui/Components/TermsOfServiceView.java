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
import org.telegram.messenger.beta.R;
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

    public TermsOfServiceView(Context context) {
        super(context);
        setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        int top = VERSION.SDK_INT >= 21 ? (int) (((float) AndroidUtilities.statusBarHeight) / AndroidUtilities.density) : 0;
        if (VERSION.SDK_INT >= 21) {
            View view = new View(context);
            view.setBackgroundColor(-16777216);
            addView(view, new LayoutParams(-1, AndroidUtilities.statusBarHeight));
        }
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.logo_middle);
        addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, (float) (top + 30), 0.0f, 0.0f));
        this.titleTextView = new TextView(context);
        this.titleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.titleTextView.setTextSize(1, 17.0f);
        this.titleTextView.setGravity(51);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setText(LocaleController.getString("PrivacyPolicyAndTerms", R.string.PrivacyPolicyAndTerms));
        addView(this.titleTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 27.0f, (float) (top + 126), 27.0f, 75.0f));
        this.scrollView = new ScrollView(context);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("actionBarDefault"));
        addView(this.scrollView, LayoutHelper.createFrame(-2, -1.0f, 51, 27.0f, (float) (top + 160), 27.0f, 75.0f));
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
        this.textView.setTextSize(1, 15.0f);
        this.textView.setMovementMethod(new LinkMovementMethodMy());
        this.textView.setGravity(51);
        this.textView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        this.scrollView.addView(this.textView, new LayoutParams(-2, -2));
        TextView declineTextView = new TextView(context);
        declineTextView.setText(LocaleController.getString("Decline", R.string.Decline).toUpperCase());
        declineTextView.setGravity(17);
        declineTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        declineTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        declineTextView.setTextSize(1, 16.0f);
        declineTextView.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f));
        addView(declineTextView, LayoutHelper.createFrame(-2, -2.0f, 83, 16.0f, 0.0f, 16.0f, 16.0f));
        declineTextView.setOnClickListener(new TermsOfServiceView$$Lambda$0(this));
        TextView acceptTextView = new TextView(context);
        acceptTextView.setText(LocaleController.getString("Accept", R.string.Accept).toUpperCase());
        acceptTextView.setGravity(17);
        acceptTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        acceptTextView.setTextColor(-1);
        acceptTextView.setTextSize(1, 16.0f);
        acceptTextView.setBackgroundResource(R.drawable.regbtn_states);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(acceptTextView, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(acceptTextView, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            acceptTextView.setStateListAnimator(animator);
        }
        acceptTextView.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f));
        addView(acceptTextView, LayoutHelper.createFrame(-2, -2.0f, 85, 16.0f, 0.0f, 16.0f, 16.0f));
        acceptTextView.setOnClickListener(new TermsOfServiceView$$Lambda$1(this));
    }

    final /* synthetic */ void lambda$new$4$TermsOfServiceView(View view) {
        Builder builder = new Builder(view.getContext());
        builder.setTitle(LocaleController.getString("TermsOfService", R.string.TermsOfService));
        builder.setPositiveButton(LocaleController.getString("DeclineDeactivate", R.string.DeclineDeactivate), new TermsOfServiceView$$Lambda$4(this));
        builder.setNegativeButton(LocaleController.getString("Back", R.string.Back), null);
        builder.setMessage(LocaleController.getString("TosUpdateDecline", R.string.TosUpdateDecline));
        builder.show();
    }

    final /* synthetic */ void lambda$null$3$TermsOfServiceView(DialogInterface dialog, int which) {
        Builder builder12 = new Builder(getContext());
        builder12.setMessage(LocaleController.getString("TosDeclineDeleteAccount", R.string.TosDeclineDeleteAccount));
        builder12.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder12.setPositiveButton(LocaleController.getString("Deactivate", R.string.Deactivate), new TermsOfServiceView$$Lambda$5(this));
        builder12.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder12.show();
    }

    final /* synthetic */ void lambda$null$2$TermsOfServiceView(DialogInterface dialogInterface, int i) {
        AlertDialog progressDialog = new AlertDialog(getContext(), 3);
        progressDialog.setCanCacnel(false);
        TL_account_deleteAccount req = new TL_account_deleteAccount();
        req.reason = "Decline ToS update";
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new TermsOfServiceView$$Lambda$6(this, progressDialog));
        progressDialog.show();
    }

    final /* synthetic */ void lambda$null$1$TermsOfServiceView(AlertDialog progressDialog, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new TermsOfServiceView$$Lambda$7(this, progressDialog, response, error));
    }

    final /* synthetic */ void lambda$null$0$TermsOfServiceView(AlertDialog progressDialog, TLObject response, TL_error error) {
        try {
            progressDialog.dismiss();
        } catch (Throwable e) {
            FileLog.e(e);
        }
        if (response instanceof TL_boolTrue) {
            MessagesController.getInstance(this.currentAccount).performLogout(0);
            return;
        }
        String errorText = LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred);
        if (error != null) {
            errorText = errorText + "\n" + error.text;
        }
        Builder builder1 = new Builder(getContext());
        builder1.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder1.setMessage(errorText);
        builder1.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        builder1.show();
    }

    final /* synthetic */ void lambda$new$6$TermsOfServiceView(View view) {
        if (this.currentTos.min_age_confirm != 0) {
            Builder builder = new Builder(view.getContext());
            builder.setTitle(LocaleController.getString("TosAgeTitle", R.string.TosAgeTitle));
            builder.setPositiveButton(LocaleController.getString("Agree", R.string.Agree), new TermsOfServiceView$$Lambda$3(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            builder.setMessage(LocaleController.formatString("TosAgeText", R.string.TosAgeText, LocaleController.formatPluralString("Years", this.currentTos.min_age_confirm)));
            builder.show();
            return;
        }
        accept();
    }

    private void accept() {
        this.delegate.onAcceptTerms(this.currentAccount);
        TL_help_acceptTermsOfService req = new TL_help_acceptTermsOfService();
        req.id = this.currentTos.id;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, TermsOfServiceView$$Lambda$2.$instance);
    }

    static final /* synthetic */ void lambda$accept$7$TermsOfServiceView(TLObject response, TL_error error) {
    }

    public void show(int account, TL_help_termsOfService tos) {
        if (getVisibility() != 0) {
            setVisibility(0);
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(tos.text);
        MessageObject.addEntitiesToText(builder, tos.entities, false, 0, false, false, false);
        this.textView.setText(builder);
        this.currentTos = tos;
        this.currentAccount = account;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildWithMargins(this.titleTextView, widthMeasureSpec, 0, heightMeasureSpec, 0);
        ((LayoutParams) this.scrollView.getLayoutParams()).topMargin = AndroidUtilities.dp(156.0f) + this.titleTextView.getMeasuredHeight();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setDelegate(TermsOfServiceViewDelegate termsOfServiceViewDelegate) {
        this.delegate = termsOfServiceViewDelegate;
    }
}

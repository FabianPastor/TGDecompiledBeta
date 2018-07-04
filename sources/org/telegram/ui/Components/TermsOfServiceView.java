package org.telegram.ui.Components;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build.VERSION;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AndroidUtilities.LinkMovementMethodMy;
import org.telegram.messenger.C0501R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
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
    private TextView textView;

    /* renamed from: org.telegram.ui.Components.TermsOfServiceView$1 */
    class C17391 implements OnClickListener {

        /* renamed from: org.telegram.ui.Components.TermsOfServiceView$1$1 */
        class C17381 implements DialogInterface.OnClickListener {

            /* renamed from: org.telegram.ui.Components.TermsOfServiceView$1$1$1 */
            class C17371 implements DialogInterface.OnClickListener {
                C17371() {
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    final AlertDialog progressDialog = new AlertDialog(TermsOfServiceView.this.getContext(), 1);
                    progressDialog.setMessage(LocaleController.getString("Loading", C0501R.string.Loading));
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);
                    TL_account_deleteAccount req = new TL_account_deleteAccount();
                    req.reason = "Decline ToS update";
                    ConnectionsManager.getInstance(TermsOfServiceView.this.currentAccount).sendRequest(req, new RequestDelegate() {
                        public void run(final TLObject response, final TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    try {
                                        progressDialog.dismiss();
                                    } catch (Throwable e) {
                                        FileLog.m3e(e);
                                    }
                                    if (response instanceof TL_boolTrue) {
                                        MessagesController.getInstance(TermsOfServiceView.this.currentAccount).performLogout(0);
                                        return;
                                    }
                                    String errorText = LocaleController.getString("ErrorOccurred", C0501R.string.ErrorOccurred);
                                    if (error != null) {
                                        errorText = errorText + "\n" + error.text;
                                    }
                                    Builder builder = new Builder(TermsOfServiceView.this.getContext());
                                    builder.setTitle(LocaleController.getString("AppName", C0501R.string.AppName));
                                    builder.setMessage(errorText);
                                    builder.setPositiveButton(LocaleController.getString("OK", C0501R.string.OK), null);
                                    builder.show();
                                }
                            });
                        }
                    });
                    progressDialog.show();
                }
            }

            C17381() {
            }

            public void onClick(DialogInterface dialog, int which) {
                Builder builder = new Builder(TermsOfServiceView.this.getContext());
                builder.setMessage(LocaleController.getString("TosDeclineDeleteAccount", C0501R.string.TosDeclineDeleteAccount));
                builder.setTitle(LocaleController.getString("AppName", C0501R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("Deactivate", C0501R.string.Deactivate), new C17371());
                builder.setNegativeButton(LocaleController.getString("Cancel", C0501R.string.Cancel), null);
                builder.show();
            }
        }

        C17391() {
        }

        public void onClick(View view) {
            Builder builder = new Builder(view.getContext());
            builder.setTitle(LocaleController.getString("TermsOfService", C0501R.string.TermsOfService));
            builder.setPositiveButton(LocaleController.getString("DeclineDeactivate", C0501R.string.DeclineDeactivate), new C17381());
            builder.setNegativeButton(LocaleController.getString("Back", C0501R.string.Back), null);
            builder.setMessage(LocaleController.getString("TosUpdateDecline", C0501R.string.TosUpdateDecline));
            builder.show();
        }
    }

    /* renamed from: org.telegram.ui.Components.TermsOfServiceView$2 */
    class C17412 implements OnClickListener {

        /* renamed from: org.telegram.ui.Components.TermsOfServiceView$2$1 */
        class C17401 implements DialogInterface.OnClickListener {
            C17401() {
            }

            public void onClick(DialogInterface dialog, int which) {
                TermsOfServiceView.this.accept();
            }
        }

        C17412() {
        }

        public void onClick(View view) {
            if (TermsOfServiceView.this.currentTos.min_age_confirm != 0) {
                Builder builder = new Builder(view.getContext());
                builder.setTitle(LocaleController.getString("TosAgeTitle", C0501R.string.TosAgeTitle));
                builder.setPositiveButton(LocaleController.getString("Agree", C0501R.string.Agree), new C17401());
                builder.setNegativeButton(LocaleController.getString("Cancel", C0501R.string.Cancel), null);
                builder.setMessage(LocaleController.formatString("TosAgeText", C0501R.string.TosAgeText, LocaleController.formatPluralString("Years", TermsOfServiceView.this.currentTos.min_age_confirm)));
                builder.show();
                return;
            }
            TermsOfServiceView.this.accept();
        }
    }

    /* renamed from: org.telegram.ui.Components.TermsOfServiceView$3 */
    class C17423 implements RequestDelegate {
        C17423() {
        }

        public void run(TLObject response, TL_error error) {
        }
    }

    public interface TermsOfServiceViewDelegate {
        void onAcceptTerms(int i);

        void onDeclineTerms(int i);
    }

    public TermsOfServiceView(Context context) {
        super(context);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        int top = VERSION.SDK_INT >= 21 ? (int) (((float) AndroidUtilities.statusBarHeight) / AndroidUtilities.density) : 0;
        if (VERSION.SDK_INT >= 21) {
            View view = new View(context);
            view.setBackgroundColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
            addView(view, new LayoutParams(-1, AndroidUtilities.statusBarHeight));
        }
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(C0501R.drawable.logo_middle);
        addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, (float) (top + 30), 0.0f, 0.0f));
        TextView titleTextView = new TextView(context);
        titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        titleTextView.setTextSize(1, 17.0f);
        titleTextView.setGravity(51);
        titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        titleTextView.setText(LocaleController.getString("PrivacyPolicyAndTerms", C0501R.string.PrivacyPolicyAndTerms));
        addView(titleTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 27.0f, (float) (top + 126), 27.0f, 75.0f));
        ScrollView scrollView = new ScrollView(context);
        AndroidUtilities.setScrollViewEdgeEffectColor(scrollView, Theme.getColor(Theme.key_actionBarDefault));
        addView(scrollView, LayoutHelper.createFrame(-2, -1.0f, 51, 27.0f, (float) (top + 160), 27.0f, 75.0f));
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
        this.textView.setTextSize(1, 15.0f);
        this.textView.setMovementMethod(new LinkMovementMethodMy());
        this.textView.setGravity(51);
        this.textView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        scrollView.addView(this.textView, new LayoutParams(-2, -2));
        TextView declineTextView = new TextView(context);
        declineTextView.setText(LocaleController.getString("Decline", C0501R.string.Decline).toUpperCase());
        declineTextView.setGravity(17);
        declineTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        declineTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        declineTextView.setTextSize(1, 16.0f);
        declineTextView.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f));
        addView(declineTextView, LayoutHelper.createFrame(-2, -2.0f, 83, 16.0f, 0.0f, 16.0f, 16.0f));
        declineTextView.setOnClickListener(new C17391());
        TextView acceptTextView = new TextView(context);
        acceptTextView.setText(LocaleController.getString("Accept", C0501R.string.Accept).toUpperCase());
        acceptTextView.setGravity(17);
        acceptTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        acceptTextView.setTextColor(-1);
        acceptTextView.setTextSize(1, 16.0f);
        acceptTextView.setBackgroundResource(C0501R.drawable.regbtn_states);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(acceptTextView, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(acceptTextView, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            acceptTextView.setStateListAnimator(animator);
        }
        acceptTextView.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(10.0f));
        addView(acceptTextView, LayoutHelper.createFrame(-2, -2.0f, 85, 16.0f, 0.0f, 16.0f, 16.0f));
        acceptTextView.setOnClickListener(new C17412());
    }

    private void accept() {
        this.delegate.onAcceptTerms(this.currentAccount);
        TL_help_acceptTermsOfService req = new TL_help_acceptTermsOfService();
        req.id = this.currentTos.id;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new C17423());
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

    public void setDelegate(TermsOfServiceViewDelegate termsOfServiceViewDelegate) {
        this.delegate = termsOfServiceViewDelegate;
    }
}

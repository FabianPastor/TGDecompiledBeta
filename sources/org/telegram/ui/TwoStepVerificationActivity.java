package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SRPHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EditTextSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.OutlineTextContainerView;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.TransformableLoginButtonView;
import org.telegram.ui.Components.VerticalPositionAutoAnimator;

public class TwoStepVerificationActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private SimpleTextView bottomButton;
    private TextView bottomTextView;
    private TextView cancelResetButton;
    /* access modifiers changed from: private */
    public int changePasswordRow;
    /* access modifiers changed from: private */
    public int changeRecoveryEmailRow;
    /* access modifiers changed from: private */
    public TLRPC.TL_account_password currentPassword;
    private byte[] currentPasswordHash = new byte[0];
    private byte[] currentSecret;
    private long currentSecretId;
    private TwoStepVerificationActivityDelegate delegate;
    private boolean destroyed;
    private String email;
    private boolean emailOnly;
    private EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public Runnable errorColorTimeout = new TwoStepVerificationActivity$$ExternalSyntheticLambda2(this);
    private String firstPassword;
    private FrameLayout floatingButtonContainer;
    private TransformableLoginButtonView floatingButtonIcon;
    private boolean forgotPasswordOnShow;
    private String hint;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public boolean loading;
    private RLottieImageView lockImageView;
    int otherwiseReloginDays = -1;
    private EditTextBoldCursor passwordEditText;
    /* access modifiers changed from: private */
    public int passwordEnabledDetailRow;
    private boolean passwordEntered = true;
    private OutlineTextContainerView passwordOutlineView;
    private boolean paused;
    /* access modifiers changed from: private */
    public boolean postedErrorColorTimeout;
    private AlertDialog progressDialog;
    private RadialProgressView radialProgressView;
    /* access modifiers changed from: private */
    public boolean resetPasswordOnShow;
    private TextView resetWaitView;
    /* access modifiers changed from: private */
    public int rowCount;
    private ScrollView scrollView;
    /* access modifiers changed from: private */
    public int setPasswordDetailRow;
    /* access modifiers changed from: private */
    public int setPasswordRow;
    /* access modifiers changed from: private */
    public int setRecoveryEmailRow;
    private TextView subtitleTextView;
    private TextView titleTextView;
    /* access modifiers changed from: private */
    public int turnPasswordOffRow;
    private Runnable updateTimeRunnable = new TwoStepVerificationActivity$$ExternalSyntheticLambda5(this);

    public interface TwoStepVerificationActivityDelegate {
        void didEnterPassword(TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3371lambda$new$0$orgtelegramuiTwoStepVerificationActivity() {
        this.postedErrorColorTimeout = false;
        this.passwordOutlineView.animateError(0.0f);
    }

    public TwoStepVerificationActivity() {
    }

    public TwoStepVerificationActivity(int account) {
        this.currentAccount = account;
    }

    public void setPassword(TLRPC.TL_account_password password) {
        this.currentPassword = password;
        this.passwordEntered = false;
    }

    public void setCurrentPasswordParams(TLRPC.TL_account_password password, byte[] passwordHash, long secretId, byte[] secret) {
        this.currentPassword = password;
        this.currentPasswordHash = passwordHash;
        this.currentSecret = secret;
        this.currentSecretId = secretId;
        this.passwordEntered = (passwordHash != null && passwordHash.length > 0) || !password.has_password;
    }

    public boolean onFragmentCreate() {
        byte[] bArr;
        super.onFragmentCreate();
        TLRPC.TL_account_password tL_account_password = this.currentPassword;
        if (tL_account_password == null || tL_account_password.current_algo == null || (bArr = this.currentPasswordHash) == null || bArr.length <= 0) {
            loadPasswordInfo(true, this.currentPassword != null);
        }
        updateRows();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.twoStepPasswordChanged);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        AndroidUtilities.cancelRunOnUIThread(this.updateTimeRunnable);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.twoStepPasswordChanged);
        this.destroyed = true;
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.progressDialog = null;
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(false);
        if (!this.passwordEntered) {
            this.actionBar.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.actionBar.setTitleColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteBlackText"), false);
            this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarWhiteSelector"), false);
            this.actionBar.setCastShadows(false);
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id != -1) {
                    return;
                }
                if (TwoStepVerificationActivity.this.otherwiseReloginDays >= 0) {
                    TwoStepVerificationActivity.this.showSetForcePasswordAlert();
                } else {
                    TwoStepVerificationActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context2);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        ScrollView scrollView2 = new ScrollView(context2);
        this.scrollView = scrollView2;
        scrollView2.setFillViewport(true);
        frameLayout.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f));
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        linearLayout.setGravity(1);
        this.scrollView.addView(linearLayout, LayoutHelper.createScroll(-1, -2, 51));
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.lockImageView = rLottieImageView;
        rLottieImageView.setAnimation(NUM, 120, 120);
        this.lockImageView.playAnimation();
        this.lockImageView.setVisibility((AndroidUtilities.isSmallScreen() || AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) ? 8 : 0);
        linearLayout.addView(this.lockImageView, LayoutHelper.createLinear(120, 120, 1));
        TextView textView = new TextView(context2);
        this.titleTextView = textView;
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.titleTextView.setTextSize(1, 18.0f);
        this.titleTextView.setGravity(1);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        linearLayout.addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 1, 24, 8, 24, 0));
        TextView textView2 = new TextView(context2);
        this.subtitleTextView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.subtitleTextView.setTextSize(1, 15.0f);
        this.subtitleTextView.setGravity(1);
        this.subtitleTextView.setVisibility(8);
        linearLayout.addView(this.subtitleTextView, LayoutHelper.createLinear(-2, -2, 1, 24, 8, 24, 0));
        OutlineTextContainerView outlineTextContainerView = new OutlineTextContainerView(context2);
        this.passwordOutlineView = outlineTextContainerView;
        outlineTextContainerView.setText(LocaleController.getString(NUM));
        this.passwordOutlineView.animateSelection(1.0f, false);
        linearLayout.addView(this.passwordOutlineView, LayoutHelper.createLinear(-1, -2, 1, 24, 24, 24, 0));
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
        this.passwordEditText = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 18.0f);
        this.passwordEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.passwordEditText.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.passwordEditText.setBackground((Drawable) null);
        this.passwordEditText.setSingleLine(true);
        this.passwordEditText.setInputType(129);
        this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.passwordEditText.setTypeface(Typeface.DEFAULT);
        this.passwordEditText.setCursorColor(Theme.getColor("windowBackgroundWhiteInputFieldActivated"));
        this.passwordEditText.setCursorWidth(1.5f);
        this.passwordEditText.setContentDescription(LocaleController.getString(NUM));
        int padding = AndroidUtilities.dp(16.0f);
        this.passwordEditText.setPadding(padding, padding, padding, padding);
        this.passwordOutlineView.addView(this.passwordEditText, LayoutHelper.createFrame(-1, -2.0f));
        this.passwordOutlineView.attachEditText(this.passwordEditText);
        this.passwordEditText.setOnFocusChangeListener(new TwoStepVerificationActivity$$ExternalSyntheticLambda38(this));
        this.passwordEditText.setOnEditorActionListener(new TwoStepVerificationActivity$$ExternalSyntheticLambda1(this));
        this.passwordEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (TwoStepVerificationActivity.this.postedErrorColorTimeout) {
                    AndroidUtilities.cancelRunOnUIThread(TwoStepVerificationActivity.this.errorColorTimeout);
                    TwoStepVerificationActivity.this.errorColorTimeout.run();
                }
            }
        });
        TextView textView3 = new TextView(context2);
        this.bottomTextView = textView3;
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.bottomTextView.setTextSize(1, 14.0f);
        this.bottomTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.bottomTextView.setText(LocaleController.getString("YourEmailInfo", NUM));
        linearLayout.addView(this.bottomTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 40, 30, 40, 0));
        TextView textView4 = new TextView(context2);
        this.resetWaitView = textView4;
        textView4.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.resetWaitView.setTextSize(1, 12.0f);
        this.resetWaitView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        linearLayout.addView(this.resetWaitView, LayoutHelper.createLinear(-1, -2, 40.0f, 8.0f, 40.0f, 0.0f));
        LinearLayout linearLayout2 = new LinearLayout(context2);
        linearLayout2.setOrientation(1);
        linearLayout2.setGravity(80);
        linearLayout2.setClipChildren(false);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, 0, 1.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.bottomButton = simpleTextView;
        simpleTextView.setTextSize(15);
        this.bottomButton.setGravity(19);
        this.bottomButton.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        frameLayout.addView(this.bottomButton, LayoutHelper.createFrame(-1, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 80, 0.0f, 0.0f, 0.0f, 16.0f));
        this.bottomButton.setOnClickListener(new TwoStepVerificationActivity$$ExternalSyntheticLambda35(this));
        VerticalPositionAutoAnimator.attach(this.bottomButton);
        TextView textView5 = new TextView(context2);
        this.cancelResetButton = textView5;
        textView5.setTextSize(1, 15.0f);
        this.cancelResetButton.setGravity(19);
        this.cancelResetButton.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.cancelResetButton.setText(LocaleController.getString("CancelReset", NUM));
        this.cancelResetButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.cancelResetButton.setVisibility(8);
        frameLayout.addView(this.cancelResetButton, LayoutHelper.createFrame(-1, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 80, 0.0f, 0.0f, 0.0f, 16.0f));
        this.cancelResetButton.setOnClickListener(new TwoStepVerificationActivity$$ExternalSyntheticLambda36(this));
        VerticalPositionAutoAnimator.attach(this.cancelResetButton);
        this.floatingButtonContainer = new FrameLayout(context2);
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButtonIcon, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButtonIcon, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.floatingButtonContainer.setStateListAnimator(animator);
            this.floatingButtonContainer.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        VerticalPositionAutoAnimator.attach(this.floatingButtonContainer);
        this.floatingButtonContainer.setOnClickListener(new TwoStepVerificationActivity$$ExternalSyntheticLambda37(this));
        TransformableLoginButtonView transformableLoginButtonView = new TransformableLoginButtonView(context2);
        this.floatingButtonIcon = transformableLoginButtonView;
        transformableLoginButtonView.setTransformType(1);
        this.floatingButtonIcon.setProgress(0.0f);
        this.floatingButtonIcon.setColor(Theme.getColor("chats_actionIcon"));
        this.floatingButtonIcon.setDrawBackground(false);
        this.floatingButtonContainer.setContentDescription(LocaleController.getString(NUM));
        this.floatingButtonContainer.addView(this.floatingButtonIcon, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f));
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(NUM).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        this.floatingButtonContainer.setBackground(drawable);
        frameLayout.addView(this.floatingButtonContainer, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 85, 0.0f, 0.0f, 24.0f, 16.0f));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context2);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
        this.listView.setEmptyView(this.emptyView);
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter2 = new ListAdapter(context2);
        this.listAdapter = listAdapter2;
        recyclerListView2.setAdapter(listAdapter2);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new TwoStepVerificationActivity$$ExternalSyntheticLambda31(this));
        AnonymousClass4 r7 = new RadialProgressView(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                ((ViewGroup.MarginLayoutParams) getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight / 2;
            }
        };
        this.radialProgressView = r7;
        r7.setSize(AndroidUtilities.dp(20.0f));
        this.radialProgressView.setAlpha(0.0f);
        this.radialProgressView.setScaleX(0.1f);
        this.radialProgressView.setScaleY(0.1f);
        this.radialProgressView.setProgressColor(Theme.getColor("windowBackgroundWhiteInputFieldActivated"));
        this.actionBar.addView(this.radialProgressView, LayoutHelper.createFrame(32, 32.0f, 21, 0.0f, 0.0f, 12.0f, 0.0f));
        updateRows();
        if (this.passwordEntered) {
            this.actionBar.setTitle(LocaleController.getString("TwoStepVerificationTitle", NUM));
        } else {
            this.actionBar.setTitle((CharSequence) null);
        }
        if (this.delegate != null) {
            this.titleTextView.setText(LocaleController.getString("PleaseEnterCurrentPasswordTransfer", NUM));
        } else {
            this.titleTextView.setText(LocaleController.getString(NUM));
            this.subtitleTextView.setVisibility(0);
            this.subtitleTextView.setText(LocaleController.getString(NUM));
        }
        if (this.passwordEntered) {
            this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
            this.fragmentView.setTag("windowBackgroundGray");
        } else {
            this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.fragmentView.setTag("windowBackgroundWhite");
        }
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3362lambda$createView$1$orgtelegramuiTwoStepVerificationActivity(View v, boolean hasFocus) {
        this.passwordOutlineView.animateSelection(hasFocus ? 1.0f : 0.0f);
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ boolean m3363lambda$createView$2$orgtelegramuiTwoStepVerificationActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 && i != 6) {
            return false;
        }
        processDone();
        return true;
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3364lambda$createView$3$orgtelegramuiTwoStepVerificationActivity(View v) {
        onPasswordForgot();
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3365lambda$createView$4$orgtelegramuiTwoStepVerificationActivity(View v) {
        cancelPasswordReset();
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3366lambda$createView$5$orgtelegramuiTwoStepVerificationActivity(View view) {
        processDone();
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3368lambda$createView$7$orgtelegramuiTwoStepVerificationActivity(View view, int position) {
        if (position == this.setPasswordRow || position == this.changePasswordRow) {
            TwoStepVerificationSetupActivity fragment = new TwoStepVerificationSetupActivity(this.currentAccount, 0, this.currentPassword);
            fragment.addFragmentToClose(this);
            fragment.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, false);
            presentFragment(fragment);
        } else if (position == this.setRecoveryEmailRow || position == this.changeRecoveryEmailRow) {
            TwoStepVerificationSetupActivity fragment2 = new TwoStepVerificationSetupActivity(this.currentAccount, 3, this.currentPassword);
            fragment2.addFragmentToClose(this);
            fragment2.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, true);
            presentFragment(fragment2);
        } else if (position == this.turnPasswordOffRow) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            String text = LocaleController.getString("TurnPasswordOffQuestion", NUM);
            if (this.currentPassword.has_secure_values) {
                text = text + "\n\n" + LocaleController.getString("TurnPasswordOffPassport", NUM);
            }
            String title = LocaleController.getString("TurnPasswordOffQuestionTitle", NUM);
            String buttonText = LocaleController.getString("Disable", NUM);
            builder.setMessage(text);
            builder.setTitle(title);
            builder.setPositiveButton(buttonText, new TwoStepVerificationActivity$$ExternalSyntheticLambda11(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            AlertDialog alertDialog = builder.create();
            showDialog(alertDialog);
            TextView button = (TextView) alertDialog.getButton(-1);
            if (button != null) {
                button.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        }
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3367lambda$createView$6$orgtelegramuiTwoStepVerificationActivity(DialogInterface dialogInterface, int i) {
        clearPassword();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.lockImageView.setVisibility((AndroidUtilities.isSmallScreen() || AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) ? 8 : 0);
    }

    private void cancelPasswordReset() {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setPositiveButton(LocaleController.getString("CancelPasswordResetYes", NUM), new TwoStepVerificationActivity$$ExternalSyntheticLambda0(this));
            builder.setNegativeButton(LocaleController.getString("CancelPasswordResetNo", NUM), (DialogInterface.OnClickListener) null);
            builder.setTitle(LocaleController.getString("CancelReset", NUM));
            builder.setMessage(LocaleController.getString("CancelPasswordReset", NUM));
            showDialog(builder.create());
        }
    }

    /* renamed from: lambda$cancelPasswordReset$10$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3352xec9f1fee(DialogInterface dialog, int which) {
        getConnectionsManager().sendRequest(new TLRPC.TL_account_declinePasswordReset(), new TwoStepVerificationActivity$$ExternalSyntheticLambda20(this));
    }

    /* renamed from: lambda$cancelPasswordReset$9$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3354x90dee49a(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda6(this, response));
    }

    /* renamed from: lambda$cancelPasswordReset$8$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3353xvar_e2219(TLObject response) {
        if (response instanceof TLRPC.TL_boolTrue) {
            this.currentPassword.pending_reset_date = 0;
            updateBottomButton();
        }
    }

    public void setForgotPasswordOnShow() {
        this.forgotPasswordOnShow = true;
    }

    private void resetPassword() {
        needShowProgress(true);
        getConnectionsManager().sendRequest(new TLRPC.TL_account_resetPassword(), new TwoStepVerificationActivity$$ExternalSyntheticLambda27(this));
    }

    /* renamed from: lambda$resetPassword$13$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3386x681CLASSNAME(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda7(this, response));
    }

    /* renamed from: lambda$resetPassword$12$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3385x6be0fec0(TLObject response) {
        String timeString;
        needHideProgress();
        if (response instanceof TLRPC.TL_account_resetPasswordOk) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            builder.setTitle(LocaleController.getString("ResetPassword", NUM));
            builder.setMessage(LocaleController.getString("RestorePasswordResetPasswordOk", NUM));
            showDialog(builder.create(), new TwoStepVerificationActivity$$ExternalSyntheticLambda34(this));
        } else if (response instanceof TLRPC.TL_account_resetPasswordRequestedWait) {
            this.currentPassword.pending_reset_date = ((TLRPC.TL_account_resetPasswordRequestedWait) response).until_date;
            updateBottomButton();
        } else if (response instanceof TLRPC.TL_account_resetPasswordFailedWait) {
            int time = ((TLRPC.TL_account_resetPasswordFailedWait) response).retry_date - getConnectionsManager().getCurrentTime();
            if (time > 86400) {
                timeString = LocaleController.formatPluralString("Days", time / 86400);
            } else if (time > 3600) {
                timeString = LocaleController.formatPluralString("Hours", time / 86400);
            } else if (time > 60) {
                timeString = LocaleController.formatPluralString("Minutes", time / 60);
            } else {
                timeString = LocaleController.formatPluralString("Seconds", Math.max(1, time));
            }
            showAlertWithText(LocaleController.getString("ResetPassword", NUM), LocaleController.formatString("ResetPasswordWait", NUM, timeString));
        }
    }

    /* renamed from: lambda$resetPassword$11$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3384xd1403c3f(DialogInterface dialog) {
        getNotificationCenter().postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, new Object[0]);
        finishFragment();
    }

    /* access modifiers changed from: private */
    public void updateBottomButton() {
        String time;
        if (!this.passwordEntered) {
            if (this.currentPassword.pending_reset_date == 0 || getConnectionsManager().getCurrentTime() > this.currentPassword.pending_reset_date) {
                if (this.resetWaitView.getVisibility() != 8) {
                    this.resetWaitView.setVisibility(8);
                }
                if (this.currentPassword.pending_reset_date == 0) {
                    this.bottomButton.setText(LocaleController.getString("ForgotPassword", NUM));
                    this.cancelResetButton.setVisibility(8);
                    this.bottomButton.setVisibility(0);
                } else {
                    this.bottomButton.setText(LocaleController.getString("ResetPassword", NUM));
                    this.cancelResetButton.setVisibility(0);
                    this.bottomButton.setVisibility(0);
                }
                this.bottomButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
                AndroidUtilities.cancelRunOnUIThread(this.updateTimeRunnable);
            } else {
                int t = Math.max(1, this.currentPassword.pending_reset_date - getConnectionsManager().getCurrentTime());
                if (t > 86400) {
                    time = LocaleController.formatPluralString("Days", t / 86400);
                } else if (t >= 3600) {
                    time = LocaleController.formatPluralString("Hours", t / 3600);
                } else {
                    time = String.format(Locale.US, "%02d:%02d", new Object[]{Integer.valueOf(t / 60), Integer.valueOf(t % 60)});
                }
                this.resetWaitView.setText(LocaleController.formatString("RestorePasswordResetIn", NUM, time));
                this.resetWaitView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
                if (this.bottomButton.getVisibility() != 8) {
                    this.bottomButton.setVisibility(8);
                }
                if (this.resetWaitView.getVisibility() != 0) {
                    this.resetWaitView.setVisibility(0);
                }
                this.cancelResetButton.setVisibility(0);
                AndroidUtilities.cancelRunOnUIThread(this.updateTimeRunnable);
                AndroidUtilities.runOnUIThread(this.updateTimeRunnable, 1000);
            }
            if (this.currentPassword == null || this.bottomButton == null || this.resetWaitView.getVisibility() != 0) {
                AndroidUtilities.cancelRunOnUIThread(this.updateTimeRunnable);
                TextView textView = this.cancelResetButton;
                if (textView != null) {
                    textView.setVisibility(8);
                }
            }
        }
    }

    private void onPasswordForgot() {
        if (this.currentPassword.pending_reset_date == 0 && this.currentPassword.has_recovery) {
            needShowProgress(true);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_auth_requestPasswordRecovery(), new TwoStepVerificationActivity$$ExternalSyntheticLambda25(this), 10);
        } else if (getParentActivity() != null) {
            if (this.currentPassword.pending_reset_date == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setPositiveButton(LocaleController.getString("Reset", NUM), new TwoStepVerificationActivity$$ExternalSyntheticLambda32(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                builder.setTitle(LocaleController.getString("ResetPassword", NUM));
                builder.setMessage(LocaleController.getString("RestorePasswordNoEmailText2", NUM));
                showDialog(builder.create());
            } else if (getConnectionsManager().getCurrentTime() > this.currentPassword.pending_reset_date) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                builder2.setPositiveButton(LocaleController.getString("Reset", NUM), new TwoStepVerificationActivity$$ExternalSyntheticLambda22(this));
                builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                builder2.setTitle(LocaleController.getString("ResetPassword", NUM));
                builder2.setMessage(LocaleController.getString("RestorePasswordResetPasswordText", NUM));
                AlertDialog dialog = builder2.create();
                showDialog(dialog);
                TextView button = (TextView) dialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            } else {
                cancelPasswordReset();
            }
        }
    }

    /* renamed from: lambda$onPasswordForgot$15$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3374xda2170f6(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda14(this, error, response));
    }

    /* renamed from: lambda$onPasswordForgot$14$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3373x3var_ae75(TLRPC.TL_error error, TLObject response) {
        String timeString;
        needHideProgress();
        if (error == null) {
            this.currentPassword.email_unconfirmed_pattern = ((TLRPC.TL_auth_passwordRecovery) response).email_pattern;
            AnonymousClass5 r1 = new TwoStepVerificationSetupActivity(this.currentAccount, 4, this.currentPassword) {
                /* access modifiers changed from: protected */
                public void onReset() {
                    boolean unused = TwoStepVerificationActivity.this.resetPasswordOnShow = true;
                }
            };
            r1.addFragmentToClose(this);
            r1.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, false);
            presentFragment(r1);
        } else if (error.text.startsWith("FLOOD_WAIT")) {
            int time = Utilities.parseInt(error.text).intValue();
            if (time < 60) {
                timeString = LocaleController.formatPluralString("Seconds", time);
            } else {
                timeString = LocaleController.formatPluralString("Minutes", time / 60);
            }
            showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
        } else {
            showAlertWithText(LocaleController.getString("AppName", NUM), error.text);
        }
    }

    /* renamed from: lambda$onPasswordForgot$16$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3375x74CLASSNAME(DialogInterface dialog, int which) {
        resetPassword();
    }

    /* renamed from: lambda$onPasswordForgot$17$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3376xvar_f5f8(DialogInterface dialog, int which) {
        resetPassword();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.twoStepPasswordChanged) {
            if (!(args == null || args.length <= 0 || args[0] == null)) {
                this.currentPasswordHash = args[0];
            }
            loadPasswordInfo(false, false);
            updateRows();
        }
    }

    public void onPause() {
        super.onPause();
        this.paused = true;
    }

    public void onResume() {
        super.onResume();
        this.paused = false;
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void setCurrentPasswordInfo(byte[] hash, TLRPC.TL_account_password password) {
        if (hash != null) {
            this.currentPasswordHash = hash;
        }
        this.currentPassword = password;
    }

    public void setDelegate(TwoStepVerificationActivityDelegate twoStepVerificationActivityDelegate) {
        this.delegate = twoStepVerificationActivityDelegate;
    }

    public static boolean canHandleCurrentPassword(TLRPC.TL_account_password password, boolean login) {
        if (login) {
            if (password.current_algo instanceof TLRPC.TL_passwordKdfAlgoUnknown) {
                return false;
            }
            return true;
        } else if ((password.new_algo instanceof TLRPC.TL_passwordKdfAlgoUnknown) || (password.current_algo instanceof TLRPC.TL_passwordKdfAlgoUnknown) || (password.new_secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoUnknown)) {
            return false;
        } else {
            return true;
        }
    }

    public static void initPasswordNewAlgo(TLRPC.TL_account_password password) {
        if (password.new_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo = (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) password.new_algo;
            byte[] salt = new byte[(algo.salt1.length + 32)];
            Utilities.random.nextBytes(salt);
            System.arraycopy(algo.salt1, 0, salt, 0, algo.salt1.length);
            algo.salt1 = salt;
        }
        if (password.new_secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
            TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 algo2 = (TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) password.new_secure_algo;
            byte[] salt2 = new byte[(algo2.salt.length + 32)];
            Utilities.random.nextBytes(salt2);
            System.arraycopy(algo2.salt, 0, salt2, 0, algo2.salt.length);
            algo2.salt = salt2;
        }
    }

    private void loadPasswordInfo(boolean first, boolean silent) {
        if (!silent) {
            this.loading = true;
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new TwoStepVerificationActivity$$ExternalSyntheticLambda28(this, silent, first), 10);
    }

    /* renamed from: lambda$loadPasswordInfo$19$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3370x983e2028(boolean silent, boolean first, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda16(this, error, response, silent, first));
    }

    /* renamed from: lambda$loadPasswordInfo$18$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3369xfd9d5da7(TLRPC.TL_error error, TLObject response, boolean silent, boolean first) {
        if (error == null) {
            this.loading = false;
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response;
            this.currentPassword = tL_account_password;
            if (!canHandleCurrentPassword(tL_account_password, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
                return;
            }
            if (!silent || first) {
                byte[] bArr = this.currentPasswordHash;
                this.passwordEntered = (bArr != null && bArr.length > 0) || !this.currentPassword.has_password;
            }
            initPasswordNewAlgo(this.currentPassword);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
        }
        updateRows();
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        super.onTransitionAnimationEnd(isOpen, backward);
        if (!isOpen) {
            return;
        }
        if (this.forgotPasswordOnShow) {
            onPasswordForgot();
            this.forgotPasswordOnShow = false;
        } else if (this.resetPasswordOnShow) {
            resetPassword();
            this.resetPasswordOnShow = false;
        }
    }

    private void updateRows() {
        TLRPC.TL_account_password tL_account_password;
        StringBuilder lastValue = new StringBuilder();
        lastValue.append(this.setPasswordRow);
        lastValue.append(this.setPasswordDetailRow);
        lastValue.append(this.changePasswordRow);
        lastValue.append(this.turnPasswordOffRow);
        lastValue.append(this.setRecoveryEmailRow);
        lastValue.append(this.changeRecoveryEmailRow);
        lastValue.append(this.passwordEnabledDetailRow);
        lastValue.append(this.rowCount);
        this.rowCount = 0;
        this.setPasswordRow = -1;
        this.setPasswordDetailRow = -1;
        this.changePasswordRow = -1;
        this.turnPasswordOffRow = -1;
        this.setRecoveryEmailRow = -1;
        this.changeRecoveryEmailRow = -1;
        this.passwordEnabledDetailRow = -1;
        if (!this.loading && (tL_account_password = this.currentPassword) != null && this.passwordEntered) {
            if (tL_account_password.has_password) {
                int i = this.rowCount;
                int i2 = i + 1;
                this.rowCount = i2;
                this.changePasswordRow = i;
                this.rowCount = i2 + 1;
                this.turnPasswordOffRow = i2;
                if (this.currentPassword.has_recovery) {
                    int i3 = this.rowCount;
                    this.rowCount = i3 + 1;
                    this.changeRecoveryEmailRow = i3;
                } else {
                    int i4 = this.rowCount;
                    this.rowCount = i4 + 1;
                    this.setRecoveryEmailRow = i4;
                }
                int i5 = this.rowCount;
                this.rowCount = i5 + 1;
                this.passwordEnabledDetailRow = i5;
            } else {
                int i6 = this.rowCount;
                int i7 = i6 + 1;
                this.rowCount = i7;
                this.setPasswordRow = i6;
                this.rowCount = i7 + 1;
                this.setPasswordDetailRow = i7;
            }
        }
        StringBuilder newValue = new StringBuilder();
        newValue.append(this.setPasswordRow);
        newValue.append(this.setPasswordDetailRow);
        newValue.append(this.changePasswordRow);
        newValue.append(this.turnPasswordOffRow);
        newValue.append(this.setRecoveryEmailRow);
        newValue.append(this.changeRecoveryEmailRow);
        newValue.append(this.passwordEnabledDetailRow);
        newValue.append(this.rowCount);
        if (this.listAdapter != null && !lastValue.toString().equals(newValue.toString())) {
            this.listAdapter.notifyDataSetChanged();
        }
        if (this.fragmentView == null) {
            return;
        }
        if (this.loading || this.passwordEntered) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                recyclerListView.setVisibility(0);
                this.scrollView.setVisibility(4);
                this.listView.setEmptyView(this.emptyView);
            }
            if (this.passwordEditText != null) {
                this.floatingButtonContainer.setVisibility(8);
                this.passwordEditText.setVisibility(4);
                this.titleTextView.setVisibility(4);
                this.bottomTextView.setVisibility(8);
                this.bottomButton.setVisibility(4);
                updateBottomButton();
            }
            this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
            this.fragmentView.setTag("windowBackgroundGray");
            return;
        }
        RecyclerListView recyclerListView2 = this.listView;
        if (recyclerListView2 != null) {
            recyclerListView2.setEmptyView((View) null);
            this.listView.setVisibility(4);
            this.scrollView.setVisibility(0);
            this.emptyView.setVisibility(4);
        }
        if (this.passwordEditText != null) {
            this.floatingButtonContainer.setVisibility(0);
            this.passwordEditText.setVisibility(0);
            this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.fragmentView.setTag("windowBackgroundWhite");
            this.titleTextView.setVisibility(0);
            this.bottomButton.setVisibility(0);
            updateBottomButton();
            this.bottomTextView.setVisibility(8);
            if (!TextUtils.isEmpty(this.currentPassword.hint)) {
                this.passwordEditText.setHint(this.currentPassword.hint);
            } else {
                this.passwordEditText.setHint((CharSequence) null);
            }
            AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda4(this), 200);
        }
    }

    /* renamed from: lambda$updateRows$20$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3388lambda$updateRows$20$orgtelegramuiTwoStepVerificationActivity() {
        EditTextBoldCursor editTextBoldCursor;
        if (!isFinishing() && !this.destroyed && (editTextBoldCursor = this.passwordEditText) != null) {
            editTextBoldCursor.requestFocus();
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    private void needShowProgress() {
        needShowProgress(false);
    }

    private void needShowProgress(boolean delay) {
        if (getParentActivity() != null && !getParentActivity().isFinishing() && this.progressDialog == null) {
            if (!this.passwordEntered) {
                AnimatorSet set = new AnimatorSet();
                set.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.radialProgressView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_Y, new float[]{1.0f})});
                set.setInterpolator(CubicBezierInterpolator.DEFAULT);
                set.start();
                return;
            }
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            this.progressDialog = alertDialog;
            alertDialog.setCanCancel(false);
            if (delay) {
                this.progressDialog.showDelayed(300);
            } else {
                this.progressDialog.show();
            }
        }
    }

    public void needHideProgress() {
        if (!this.passwordEntered) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.radialProgressView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_Y, new float[]{0.1f})});
            set.setInterpolator(CubicBezierInterpolator.DEFAULT);
            set.start();
            return;
        }
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.progressDialog = null;
        }
    }

    private void showAlertWithText(String title, String text) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            builder.setTitle(title);
            builder.setMessage(text);
            showDialog(builder.create());
        }
    }

    private void clearPassword() {
        String str = this.firstPassword;
        TLRPC.TL_account_updatePasswordSettings req = new TLRPC.TL_account_updatePasswordSettings();
        byte[] bArr = this.currentPasswordHash;
        if (bArr == null || bArr.length == 0) {
            req.password = new TLRPC.TL_inputCheckPasswordEmpty();
        }
        req.new_settings = new TLRPC.TL_account_passwordInputSettings();
        UserConfig.getInstance(this.currentAccount).resetSavedPassword();
        this.currentSecret = null;
        req.new_settings.flags = 3;
        req.new_settings.hint = "";
        req.new_settings.new_password_hash = new byte[0];
        req.new_settings.new_algo = new TLRPC.TL_passwordKdfAlgoUnknown();
        req.new_settings.email = "";
        needShowProgress();
        Utilities.globalQueue.postRunnable(new TwoStepVerificationActivity$$ExternalSyntheticLambda8(this, req));
    }

    /* renamed from: lambda$clearPassword$27$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3361xvar_fa26(TLRPC.TL_account_updatePasswordSettings req) {
        if (req.password == null) {
            if (this.currentPassword.current_algo == null) {
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new TwoStepVerificationActivity$$ExternalSyntheticLambda21(this), 8);
                return;
            }
            req.password = getNewSrpPassword();
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new TwoStepVerificationActivity$$ExternalSyntheticLambda24(this), 10);
    }

    /* renamed from: lambda$clearPassword$22$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3356xvar_da1(TLObject response2, TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda10(this, error2, response2));
    }

    /* renamed from: lambda$clearPassword$21$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3355x57CLASSNAMEb20(TLRPC.TL_error error2, TLObject response2) {
        if (error2 == null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response2;
            this.currentPassword = tL_account_password;
            initPasswordNewAlgo(tL_account_password);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            clearPassword();
        }
    }

    /* renamed from: lambda$clearPassword$26$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3360x5ce737a5(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda13(this, error, response));
    }

    /* renamed from: lambda$clearPassword$25$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3359xCLASSNAME(TLRPC.TL_error error, TLObject response) {
        String timeString;
        if (error == null || !"SRP_ID_INVALID".equals(error.text)) {
            needHideProgress();
            if (error == null && (response instanceof TLRPC.TL_boolTrue)) {
                this.currentPassword = null;
                this.currentPasswordHash = new byte[0];
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didRemoveTwoStepPassword, new Object[0]);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, new Object[0]);
                finishFragment();
            } else if (error == null) {
            } else {
                if (error.text.startsWith("FLOOD_WAIT")) {
                    int time = Utilities.parseInt(error.text).intValue();
                    if (time < 60) {
                        timeString = LocaleController.formatPluralString("Seconds", time);
                    } else {
                        timeString = LocaleController.formatPluralString("Minutes", time / 60);
                    }
                    showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
                    return;
                }
                showAlertWithText(LocaleController.getString("AppName", NUM), error.text);
            }
        } else {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new TwoStepVerificationActivity$$ExternalSyntheticLambda23(this), 8);
        }
    }

    /* renamed from: lambda$clearPassword$24$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3358x27a5b2a3(TLObject response2, TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda12(this, error2, response2));
    }

    /* renamed from: lambda$clearPassword$23$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3357x8d04var_(TLRPC.TL_error error2, TLObject response2) {
        if (error2 == null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response2;
            this.currentPassword = tL_account_password;
            initPasswordNewAlgo(tL_account_password);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            clearPassword();
        }
    }

    public TLRPC.TL_inputCheckPasswordSRP getNewSrpPassword() {
        if (!(this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow)) {
            return null;
        }
        return SRPHelper.startCheck(this.currentPasswordHash, this.currentPassword.srp_id, this.currentPassword.srp_B, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.current_algo);
    }

    private boolean checkSecretValues(byte[] passwordBytes, TLRPC.TL_account_passwordSettings passwordSettings) {
        byte[] passwordHash;
        byte[] bArr = passwordBytes;
        TLRPC.TL_account_passwordSettings tL_account_passwordSettings = passwordSettings;
        if (tL_account_passwordSettings.secure_settings != null) {
            this.currentSecret = tL_account_passwordSettings.secure_settings.secure_secret;
            if (tL_account_passwordSettings.secure_settings.secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
                passwordHash = Utilities.computePBKDF2(bArr, ((TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) tL_account_passwordSettings.secure_settings.secure_algo).salt);
            } else if (!(tL_account_passwordSettings.secure_settings.secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoSHA512)) {
                return false;
            } else {
                TLRPC.TL_securePasswordKdfAlgoSHA512 algo = (TLRPC.TL_securePasswordKdfAlgoSHA512) tL_account_passwordSettings.secure_settings.secure_algo;
                passwordHash = Utilities.computeSHA512(algo.salt, bArr, algo.salt);
            }
            this.currentSecretId = tL_account_passwordSettings.secure_settings.secure_secret_id;
            byte[] key = new byte[32];
            System.arraycopy(passwordHash, 0, key, 0, 32);
            byte[] iv = new byte[16];
            System.arraycopy(passwordHash, 32, iv, 0, 16);
            byte[] bArr2 = this.currentSecret;
            byte[] bArr3 = iv;
            byte[] bArr4 = key;
            Utilities.aesCbcEncryptionByteArraySafe(bArr2, key, iv, 0, bArr2.length, 0, 0);
            if (PassportActivity.checkSecret(tL_account_passwordSettings.secure_settings.secure_secret, Long.valueOf(tL_account_passwordSettings.secure_settings.secure_secret_id))) {
                return true;
            }
            TLRPC.TL_account_updatePasswordSettings req = new TLRPC.TL_account_updatePasswordSettings();
            req.password = getNewSrpPassword();
            req.new_settings = new TLRPC.TL_account_passwordInputSettings();
            req.new_settings.new_secure_settings = new TLRPC.TL_secureSecretSettings();
            req.new_settings.new_secure_settings.secure_secret = new byte[0];
            req.new_settings.new_secure_settings.secure_algo = new TLRPC.TL_securePasswordKdfAlgoUnknown();
            req.new_settings.new_secure_settings.secure_secret_id = 0;
            req.new_settings.flags |= 4;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, TwoStepVerificationActivity$$ExternalSyntheticLambda30.INSTANCE);
            this.currentSecret = null;
            this.currentSecretId = 0;
            return true;
        }
        this.currentSecret = null;
        this.currentSecretId = 0;
        return true;
    }

    static /* synthetic */ void lambda$checkSecretValues$28(TLObject response, TLRPC.TL_error error) {
    }

    private void processDone() {
        if (!this.passwordEntered) {
            String oldPassword = this.passwordEditText.getText().toString();
            if (oldPassword.length() == 0) {
                onFieldError(this.passwordOutlineView, this.passwordEditText, false);
                return;
            }
            byte[] oldPasswordBytes = AndroidUtilities.getStringBytes(oldPassword);
            needShowProgress();
            Utilities.globalQueue.postRunnable(new TwoStepVerificationActivity$$ExternalSyntheticLambda18(this, oldPasswordBytes));
        }
    }

    /* renamed from: lambda$processDone$35$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3383x8a83005a(byte[] oldPasswordBytes) {
        byte[] x_bytes;
        TLRPC.TL_account_getPasswordSettings req = new TLRPC.TL_account_getPasswordSettings();
        if (this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            x_bytes = SRPHelper.getX(oldPasswordBytes, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.current_algo);
        } else {
            x_bytes = null;
        }
        RequestDelegate requestDelegate = new TwoStepVerificationActivity$$ExternalSyntheticLambda29(this, oldPasswordBytes, x_bytes);
        if (this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            req.password = SRPHelper.startCheck(x_bytes, this.currentPassword.srp_id, this.currentPassword.srp_B, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.current_algo);
            if (req.password == null) {
                TLRPC.TL_error error = new TLRPC.TL_error();
                error.text = "ALGO_INVALID";
                requestDelegate.run((TLObject) null, error);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, requestDelegate, 10);
            return;
        }
        TLRPC.TL_error error2 = new TLRPC.TL_error();
        error2.text = "PASSWORD_HASH_INVALID";
        requestDelegate.run((TLObject) null, error2);
    }

    /* renamed from: lambda$processDone$34$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3382xefe23dd9(byte[] oldPasswordBytes, byte[] x_bytes, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            Utilities.globalQueue.postRunnable(new TwoStepVerificationActivity$$ExternalSyntheticLambda19(this, oldPasswordBytes, response, x_bytes));
        } else {
            AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda9(this, error));
        }
    }

    /* renamed from: lambda$processDone$30$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3378x855var_d5(byte[] oldPasswordBytes, TLObject response, byte[] x_bytes) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda17(this, checkSecretValues(oldPasswordBytes, (TLRPC.TL_account_passwordSettings) response), x_bytes));
    }

    /* renamed from: lambda$processDone$29$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3377x3b8e7cbf(boolean secretOk, byte[] x_bytes) {
        if (this.delegate == null || !secretOk) {
            needHideProgress();
        }
        if (secretOk) {
            this.currentPasswordHash = x_bytes;
            this.passwordEntered = true;
            if (this.delegate != null) {
                AndroidUtilities.hideKeyboard(this.passwordEditText);
                this.delegate.didEnterPassword(getNewSrpPassword());
            } else if (!TextUtils.isEmpty(this.currentPassword.email_unconfirmed_pattern)) {
                TwoStepVerificationSetupActivity fragment = new TwoStepVerificationSetupActivity(this.currentAccount, 5, this.currentPassword);
                fragment.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, true);
                presentFragment(fragment, true);
            } else {
                AndroidUtilities.hideKeyboard(this.passwordEditText);
                TwoStepVerificationActivity fragment2 = new TwoStepVerificationActivity();
                fragment2.passwordEntered = true;
                fragment2.currentPasswordHash = this.currentPasswordHash;
                fragment2.currentPassword = this.currentPassword;
                fragment2.currentSecret = this.currentSecret;
                fragment2.currentSecretId = this.currentSecretId;
                presentFragment(fragment2, true);
            }
        } else {
            AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
        }
    }

    /* renamed from: lambda$processDone$33$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3381x55417b58(TLRPC.TL_error error) {
        String timeString;
        if ("SRP_ID_INVALID".equals(error.text)) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new TwoStepVerificationActivity$$ExternalSyntheticLambda26(this), 8);
            return;
        }
        needHideProgress();
        if ("PASSWORD_HASH_INVALID".equals(error.text)) {
            onFieldError(this.passwordOutlineView, this.passwordEditText, true);
        } else if (error.text.startsWith("FLOOD_WAIT")) {
            int time = Utilities.parseInt(error.text).intValue();
            if (time < 60) {
                timeString = LocaleController.formatPluralString("Seconds", time);
            } else {
                timeString = LocaleController.formatPluralString("Minutes", time / 60);
            }
            showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
        } else {
            showAlertWithText(LocaleController.getString("AppName", NUM), error.text);
        }
    }

    /* renamed from: lambda$processDone$32$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3380xbaa0b8d7(TLObject response2, TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda15(this, error2, response2));
    }

    /* renamed from: lambda$processDone$31$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3379x1fffvar_(TLRPC.TL_error error2, TLObject response2) {
        if (error2 == null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response2;
            this.currentPassword = tL_account_password;
            initPasswordNewAlgo(tL_account_password);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            processDone();
        }
    }

    private void onFieldError(OutlineTextContainerView outlineView, TextView field, boolean clear) {
        if (getParentActivity() != null) {
            try {
                field.performHapticFeedback(3, 2);
            } catch (Exception e) {
            }
            if (clear) {
                field.setText("");
            }
            outlineView.animateError(1.0f);
            AndroidUtilities.shakeViewSpring(outlineView, 5.0f, new TwoStepVerificationActivity$$ExternalSyntheticLambda3(this));
        }
    }

    /* renamed from: lambda$onFieldError$36$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3372x551b7b25() {
        AndroidUtilities.cancelRunOnUIThread(this.errorColorTimeout);
        AndroidUtilities.runOnUIThread(this.errorColorTimeout, 1500);
        this.postedErrorColorTimeout = true;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public int getItemCount() {
            if (TwoStepVerificationActivity.this.loading || TwoStepVerificationActivity.this.currentPassword == null) {
                return 0;
            }
            return TwoStepVerificationActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                default:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    textCell.setTag("windowBackgroundWhiteBlackText");
                    textCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                    if (position == TwoStepVerificationActivity.this.changePasswordRow) {
                        textCell.setText(LocaleController.getString("ChangePassword", NUM), true);
                        return;
                    } else if (position == TwoStepVerificationActivity.this.setPasswordRow) {
                        textCell.setText(LocaleController.getString("SetAdditionalPassword", NUM), true);
                        return;
                    } else if (position == TwoStepVerificationActivity.this.turnPasswordOffRow) {
                        textCell.setText(LocaleController.getString("TurnPasswordOff", NUM), true);
                        return;
                    } else if (position == TwoStepVerificationActivity.this.changeRecoveryEmailRow) {
                        textCell.setText(LocaleController.getString("ChangeRecoveryEmail", NUM), false);
                        return;
                    } else if (position == TwoStepVerificationActivity.this.setRecoveryEmailRow) {
                        textCell.setText(LocaleController.getString("SetRecoveryEmail", NUM), false);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == TwoStepVerificationActivity.this.setPasswordDetailRow) {
                        privacyCell.setText(LocaleController.getString("SetAdditionalPasswordInfo", NUM));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == TwoStepVerificationActivity.this.passwordEnabledDetailRow) {
                        privacyCell.setText(LocaleController.getString("EnabledPasswordText", NUM));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == TwoStepVerificationActivity.this.setPasswordDetailRow || position == TwoStepVerificationActivity.this.passwordEnabledDetailRow) {
                return 1;
            }
            return 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, EditTextSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText3"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
        themeDescriptions.add(new ThemeDescription(this.bottomTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
        themeDescriptions.add(new ThemeDescription(this.bottomButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        themeDescriptions.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        themeDescriptions.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        themeDescriptions.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        return themeDescriptions;
    }

    public boolean onBackPressed() {
        if (this.otherwiseReloginDays < 0) {
            return super.onBackPressed();
        }
        showSetForcePasswordAlert();
        return false;
    }

    /* access modifiers changed from: private */
    public void showSetForcePasswordAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("Warning", NUM));
        builder.setMessage(LocaleController.formatPluralString("ForceSetPasswordAlertMessageShort", this.otherwiseReloginDays));
        builder.setPositiveButton(LocaleController.getString("TwoStepVerificationSetPassword", NUM), (DialogInterface.OnClickListener) null);
        builder.setNegativeButton(LocaleController.getString("ForceSetPasswordCancel", NUM), new TwoStepVerificationActivity$$ExternalSyntheticLambda33(this));
        ((TextView) builder.show().getButton(-2)).setTextColor(Theme.getColor("dialogTextRed2"));
    }

    /* renamed from: lambda$showSetForcePasswordAlert$37$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3387xa71edCLASSNAME(DialogInterface a1, int a2) {
        finishFragment();
    }

    public void setBlockingAlert(int otherwiseRelogin) {
        this.otherwiseReloginDays = otherwiseRelogin;
    }

    public void finishFragment() {
        if (this.otherwiseReloginDays >= 0) {
            Bundle args = new Bundle();
            args.putBoolean("afterSignup", true);
            presentFragment(new DialogsActivity(args), true);
            return;
        }
        super.finishFragment();
    }

    public boolean isLightStatusBar() {
        if (ColorUtils.calculateLuminance(Theme.getColor("windowBackgroundWhite", (boolean[]) null, true)) > 0.699999988079071d) {
            return true;
        }
        return false;
    }
}

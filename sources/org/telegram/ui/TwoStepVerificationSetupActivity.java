package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SRPHelper;
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
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.CustomPhoneKeyboardView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.OutlineTextContainerView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.TransformableLoginButtonView;
import org.telegram.ui.Components.VerticalPositionAutoAnimator;
import org.telegram.ui.Components.spoilers.SpoilersTextView;

public class TwoStepVerificationSetupActivity extends BaseFragment {
    public static final int TYPE_CREATE_PASSWORD_STEP_1 = 0;
    public static final int TYPE_CREATE_PASSWORD_STEP_2 = 1;
    public static final int TYPE_EMAIL_CONFIRM = 5;
    public static final int TYPE_EMAIL_RECOVERY = 4;
    public static final int TYPE_ENTER_EMAIL = 3;
    public static final int TYPE_ENTER_HINT = 2;
    public static final int TYPE_INTRO = 6;
    public static final int TYPE_PASSWORD_SET = 7;
    public static final int TYPE_VERIFY = 8;
    public static final int TYPE_VERIFY_OK = 9;
    private static final int item_abort = 1;
    /* access modifiers changed from: private */
    public AnimatorSet actionBarAnimator;
    /* access modifiers changed from: private */
    public View actionBarBackground;
    /* access modifiers changed from: private */
    public RLottieDrawable[] animationDrawables;
    private TextView bottomSkipButton;
    /* access modifiers changed from: private */
    public AnimatorSet buttonAnimation;
    /* access modifiers changed from: private */
    public TextView buttonTextView;
    private boolean closeAfterSet;
    private CodeFieldContainer codeFieldContainer;
    /* access modifiers changed from: private */
    public TLRPC.TL_account_password currentPassword;
    private byte[] currentPasswordHash = new byte[0];
    private byte[] currentSecret;
    private long currentSecretId;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public TextView descriptionText;
    /* access modifiers changed from: private */
    public TextView descriptionText2;
    private TextView descriptionText3;
    private boolean doneAfterPasswordLoad;
    /* access modifiers changed from: private */
    public EditTextBoldCursor editTextFirstRow;
    private EditTextBoldCursor editTextSecondRow;
    private String email;
    private String emailCode;
    private int emailCodeLength = 6;
    private boolean emailOnly;
    /* access modifiers changed from: private */
    public Runnable errorColorTimeout = new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda12(this);
    private Runnable finishCallback = new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda13(this);
    private String firstPassword;
    private VerticalPositionAutoAnimator floatingAutoAnimator;
    private FrameLayout floatingButtonContainer;
    private TransformableLoginButtonView floatingButtonIcon;
    private RadialProgressView floatingProgressView;
    private ArrayList<BaseFragment> fragmentsToClose = new ArrayList<>();
    private boolean fromRegistration;
    private String hint;
    /* access modifiers changed from: private */
    public boolean ignoreTextChange;
    /* access modifiers changed from: private */
    public RLottieImageView imageView;
    private boolean isPasswordVisible;
    /* access modifiers changed from: private */
    public CustomPhoneKeyboardView keyboardView;
    private Runnable monkeyAfterSwitchCallback;
    private Runnable monkeyEndCallback;
    /* access modifiers changed from: private */
    public boolean needPasswordButton = false;
    /* access modifiers changed from: private */
    public int otherwiseReloginDays = -1;
    private OutlineTextContainerView outlineTextFirstRow;
    private OutlineTextContainerView outlineTextSecondRow;
    private boolean paused;
    /* access modifiers changed from: private */
    public boolean postedErrorColorTimeout;
    /* access modifiers changed from: private */
    public RadialProgressView radialProgressView;
    private ScrollView scrollView;
    private Runnable setAnimationRunnable;
    /* access modifiers changed from: private */
    public ImageView showPasswordButton;
    /* access modifiers changed from: private */
    public TextView titleTextView;
    private boolean waitingForEmail;

    /* renamed from: lambda$new$0$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3409lambda$new$0$orgtelegramuiTwoStepVerificationSetupActivity() {
        this.postedErrorColorTimeout = false;
        for (CodeNumberField animateErrorProgress : this.codeFieldContainer.codeField) {
            animateErrorProgress.animateErrorProgress(0.0f);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3410lambda$new$1$orgtelegramuiTwoStepVerificationSetupActivity() {
        EditTextBoldCursor editTextBoldCursor = this.editTextFirstRow;
        if (editTextBoldCursor != null) {
            if (editTextBoldCursor.length() != 0) {
                this.animationDrawables[2].setCustomEndFrame(49);
                this.animationDrawables[2].setProgress(0.0f, false);
                this.imageView.playAnimation();
                return;
            }
            setRandomMonkeyIdleAnimation(true);
        }
    }

    public TwoStepVerificationSetupActivity(int type, TLRPC.TL_account_password password) {
        this.currentType = type;
        this.currentPassword = password;
        if (password == null && (type == 6 || type == 8)) {
            loadPasswordInfo();
        } else {
            this.waitingForEmail = !TextUtils.isEmpty(password.email_unconfirmed_pattern);
        }
    }

    public TwoStepVerificationSetupActivity(int account, int type, TLRPC.TL_account_password password) {
        this.currentAccount = account;
        this.currentType = type;
        this.currentPassword = password;
        this.waitingForEmail = !TextUtils.isEmpty(password.email_unconfirmed_pattern);
        if (this.currentPassword == null) {
            int i = this.currentType;
            if (i == 6 || i == 8) {
                loadPasswordInfo();
            }
        }
    }

    public void setCurrentPasswordParams(byte[] passwordHash, long secretId, byte[] secret, boolean email2) {
        this.currentPasswordHash = passwordHash;
        this.currentSecret = secret;
        this.currentSecretId = secretId;
        this.emailOnly = email2;
    }

    public void setCurrentEmailCode(String code) {
        this.emailCode = code;
    }

    public void addFragmentToClose(BaseFragment fragment) {
        this.fragmentsToClose.add(fragment);
    }

    public void setFromRegistration(boolean fromRegistration2) {
        this.fromRegistration = fromRegistration2;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        this.doneAfterPasswordLoad = false;
        Runnable runnable = this.setAnimationRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.setAnimationRunnable = null;
        }
        if (this.animationDrawables != null) {
            int a = 0;
            while (true) {
                RLottieDrawable[] rLottieDrawableArr = this.animationDrawables;
                if (a >= rLottieDrawableArr.length) {
                    break;
                }
                rLottieDrawableArr[a].recycle();
                a++;
            }
            this.animationDrawables = null;
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        if (isCustomKeyboardVisible()) {
            AndroidUtilities.removeAltFocusable(getParentActivity(), this.classGuid);
        }
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackgroundDrawable((Drawable) null);
        this.actionBar.setBackButtonImage(NUM);
        boolean z = false;
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setTitleColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteBlackText"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarWhiteSelector"), false);
        this.actionBar.setCastShadows(false);
        this.actionBar.setAddToContainer(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                String text;
                if (id == -1) {
                    if (TwoStepVerificationSetupActivity.this.otherwiseReloginDays < 0 || TwoStepVerificationSetupActivity.this.parentLayout.fragmentsStack.size() != 1) {
                        TwoStepVerificationSetupActivity.this.finishFragment();
                    } else {
                        TwoStepVerificationSetupActivity.this.showSetForcePasswordAlert();
                    }
                } else if (id == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) TwoStepVerificationSetupActivity.this.getParentActivity());
                    if (TwoStepVerificationSetupActivity.this.currentPassword == null || !TwoStepVerificationSetupActivity.this.currentPassword.has_password) {
                        text = LocaleController.getString("CancelPasswordQuestion", NUM);
                    } else {
                        text = LocaleController.getString("CancelEmailQuestion", NUM);
                    }
                    String title = LocaleController.getString("CancelEmailQuestionTitle", NUM);
                    String buttonText = LocaleController.getString("Abort", NUM);
                    builder.setMessage(text);
                    builder.setTitle(title);
                    builder.setPositiveButton(buttonText, new TwoStepVerificationSetupActivity$1$$ExternalSyntheticLambda0(this));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    AlertDialog alertDialog = builder.create();
                    TwoStepVerificationSetupActivity.this.showDialog(alertDialog);
                    TextView button = (TextView) alertDialog.getButton(-1);
                    if (button != null) {
                        button.setTextColor(Theme.getColor("dialogTextRed2"));
                    }
                }
            }

            /* renamed from: lambda$onItemClick$0$org-telegram-ui-TwoStepVerificationSetupActivity$1  reason: not valid java name */
            public /* synthetic */ void m3438xa0f7e77b(DialogInterface dialogInterface, int i) {
                TwoStepVerificationSetupActivity.this.setNewPassword(true);
            }
        });
        if (this.currentType == 5) {
            this.actionBar.createMenu().addItem(0, NUM).addSubItem(1, LocaleController.getString("AbortPasswordMenu", NUM));
        }
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
        this.floatingAutoAnimator = VerticalPositionAutoAnimator.attach(this.floatingButtonContainer);
        this.floatingButtonContainer.setOnClickListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda50(this));
        TransformableLoginButtonView transformableLoginButtonView = new TransformableLoginButtonView(context2);
        this.floatingButtonIcon = transformableLoginButtonView;
        transformableLoginButtonView.setTransformType(1);
        this.floatingButtonIcon.setProgress(0.0f);
        this.floatingButtonIcon.setColor(Theme.getColor("chats_actionIcon"));
        this.floatingButtonIcon.setDrawBackground(false);
        this.floatingButtonContainer.setContentDescription(LocaleController.getString(NUM));
        this.floatingButtonContainer.addView(this.floatingButtonIcon, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f));
        RadialProgressView radialProgressView2 = new RadialProgressView(context2);
        this.floatingProgressView = radialProgressView2;
        radialProgressView2.setSize(AndroidUtilities.dp(22.0f));
        this.floatingProgressView.setAlpha(0.0f);
        this.floatingProgressView.setScaleX(0.1f);
        this.floatingProgressView.setScaleY(0.1f);
        this.floatingButtonContainer.addView(this.floatingProgressView, LayoutHelper.createFrame(-1, -1.0f));
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(NUM).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        this.floatingButtonContainer.setBackground(drawable);
        TextView textView = new TextView(context2);
        this.bottomSkipButton = textView;
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText2"));
        this.bottomSkipButton.setTextSize(1, 14.0f);
        this.bottomSkipButton.setGravity(19);
        this.bottomSkipButton.setVisibility(8);
        VerticalPositionAutoAnimator.attach(this.bottomSkipButton);
        this.bottomSkipButton.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.bottomSkipButton.setOnClickListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda1(this));
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.imageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        if (this.currentType == 2 && AndroidUtilities.isSmallScreen()) {
            this.imageView.setVisibility(8);
        } else if (!isIntro()) {
            this.imageView.setVisibility(isLandscape() ? 8 : 0);
        }
        TextView textView2 = new TextView(context2);
        this.titleTextView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.titleTextView.setGravity(1);
        this.titleTextView.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.titleTextView.setTextSize(1, 24.0f);
        SpoilersTextView spoilersTextView = new SpoilersTextView(context2);
        this.descriptionText = spoilersTextView;
        spoilersTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.descriptionText.setGravity(1);
        this.descriptionText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        this.descriptionText.setTextSize(1, 15.0f);
        this.descriptionText.setVisibility(8);
        this.descriptionText.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        TextView textView3 = new TextView(context2);
        this.descriptionText2 = textView3;
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.descriptionText2.setGravity(1);
        this.descriptionText2.setTextSize(1, 14.0f);
        this.descriptionText2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        this.descriptionText2.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.descriptionText2.setVisibility(8);
        this.descriptionText2.setOnClickListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda2(this));
        TextView textView4 = new TextView(context2);
        this.buttonTextView = textView4;
        textView4.setMinWidth(AndroidUtilities.dp(220.0f));
        this.buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.buttonTextView.setTextSize(1, 15.0f);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.buttonTextView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        this.buttonTextView.setOnClickListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda3(this));
        switch (this.currentType) {
            case 6:
            case 7:
            case 9:
                this.titleTextView.setTypeface(Typeface.DEFAULT);
                this.titleTextView.setTextSize(1, 24.0f);
                break;
            default:
                this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.titleTextView.setTextSize(1, 18.0f);
                break;
        }
        switch (this.currentType) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 8:
                final FrameLayout frameLayout = new FrameLayout(context2) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                        ((ViewGroup.MarginLayoutParams) TwoStepVerificationSetupActivity.this.radialProgressView.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight + AndroidUtilities.dp(16.0f);
                    }
                };
                final SizeNotifierFrameLayout keyboardFrameLayout = new SizeNotifierFrameLayout(context2) {
                    /* access modifiers changed from: protected */
                    public void onLayout(boolean changed, int l, int t, int r, int b) {
                        int frameBottom;
                        if (TwoStepVerificationSetupActivity.this.keyboardView.getVisibility() == 8 || measureKeyboardHeight() < AndroidUtilities.dp(20.0f)) {
                            if (TwoStepVerificationSetupActivity.this.keyboardView.getVisibility() != 8) {
                                FrameLayout frameLayout = frameLayout;
                                int measuredWidth = getMeasuredWidth();
                                int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(230.0f);
                                frameBottom = measuredHeight;
                                frameLayout.layout(0, 0, measuredWidth, measuredHeight);
                            } else {
                                FrameLayout frameLayout2 = frameLayout;
                                int measuredWidth2 = getMeasuredWidth();
                                int measuredHeight2 = getMeasuredHeight();
                                frameBottom = measuredHeight2;
                                frameLayout2.layout(0, 0, measuredWidth2, measuredHeight2);
                            }
                        } else if (TwoStepVerificationSetupActivity.this.isCustomKeyboardVisible()) {
                            FrameLayout frameLayout3 = frameLayout;
                            int measuredWidth3 = getMeasuredWidth();
                            int measuredHeight3 = (getMeasuredHeight() - AndroidUtilities.dp(230.0f)) + measureKeyboardHeight();
                            frameBottom = measuredHeight3;
                            frameLayout3.layout(0, 0, measuredWidth3, measuredHeight3);
                        } else {
                            FrameLayout frameLayout4 = frameLayout;
                            int measuredWidth4 = getMeasuredWidth();
                            int measuredHeight4 = getMeasuredHeight();
                            frameBottom = measuredHeight4;
                            frameLayout4.layout(0, 0, measuredWidth4, measuredHeight4);
                        }
                        TwoStepVerificationSetupActivity.this.keyboardView.layout(0, frameBottom, getMeasuredWidth(), AndroidUtilities.dp(230.0f) + frameBottom);
                    }

                    /* access modifiers changed from: protected */
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        int width = View.MeasureSpec.getSize(widthMeasureSpec);
                        int height = View.MeasureSpec.getSize(heightMeasureSpec);
                        setMeasuredDimension(width, height);
                        int frameHeight = height;
                        if (TwoStepVerificationSetupActivity.this.keyboardView.getVisibility() != 8 && measureKeyboardHeight() < AndroidUtilities.dp(20.0f)) {
                            frameHeight -= AndroidUtilities.dp(230.0f);
                        }
                        frameLayout.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(frameHeight, NUM));
                        TwoStepVerificationSetupActivity.this.keyboardView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(230.0f), NUM));
                    }
                };
                keyboardFrameLayout.addView(frameLayout);
                ViewGroup container = new ViewGroup(context2) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        int width = View.MeasureSpec.getSize(widthMeasureSpec);
                        int height = View.MeasureSpec.getSize(heightMeasureSpec);
                        TwoStepVerificationSetupActivity.this.actionBar.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), heightMeasureSpec);
                        TwoStepVerificationSetupActivity.this.actionBarBackground.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(TwoStepVerificationSetupActivity.this.actionBar.getMeasuredHeight() + AndroidUtilities.dp(3.0f), NUM));
                        keyboardFrameLayout.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), heightMeasureSpec);
                        setMeasuredDimension(width, height);
                    }

                    /* access modifiers changed from: protected */
                    public void onLayout(boolean changed, int l, int t, int r, int b) {
                        TwoStepVerificationSetupActivity.this.actionBar.layout(0, 0, TwoStepVerificationSetupActivity.this.actionBar.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.actionBar.getMeasuredHeight());
                        TwoStepVerificationSetupActivity.this.actionBarBackground.layout(0, 0, TwoStepVerificationSetupActivity.this.actionBarBackground.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.actionBarBackground.getMeasuredHeight());
                        SizeNotifierFrameLayout sizeNotifierFrameLayout = keyboardFrameLayout;
                        sizeNotifierFrameLayout.layout(0, 0, sizeNotifierFrameLayout.getMeasuredWidth(), keyboardFrameLayout.getMeasuredHeight());
                    }
                };
                AnonymousClass7 r10 = new ScrollView(context2) {
                    private boolean isLayoutDirty = true;
                    private int[] location = new int[2];
                    private int scrollingUp;
                    private Rect tempRect = new Rect();

                    /* access modifiers changed from: protected */
                    public void onScrollChanged(int l, int t, int oldl, int oldt) {
                        super.onScrollChanged(l, t, oldl, oldt);
                        if (TwoStepVerificationSetupActivity.this.titleTextView != null) {
                            TwoStepVerificationSetupActivity.this.titleTextView.getLocationOnScreen(this.location);
                            boolean show = this.location[1] + TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredHeight() < TwoStepVerificationSetupActivity.this.actionBar.getBottom();
                            if (show != (TwoStepVerificationSetupActivity.this.titleTextView.getTag() == null)) {
                                TwoStepVerificationSetupActivity.this.titleTextView.setTag(show ? null : 1);
                                if (TwoStepVerificationSetupActivity.this.actionBarAnimator != null) {
                                    TwoStepVerificationSetupActivity.this.actionBarAnimator.cancel();
                                    AnimatorSet unused = TwoStepVerificationSetupActivity.this.actionBarAnimator = null;
                                }
                                AnimatorSet unused2 = TwoStepVerificationSetupActivity.this.actionBarAnimator = new AnimatorSet();
                                AnimatorSet access$2400 = TwoStepVerificationSetupActivity.this.actionBarAnimator;
                                Animator[] animatorArr = new Animator[2];
                                View access$1900 = TwoStepVerificationSetupActivity.this.actionBarBackground;
                                Property property = View.ALPHA;
                                float[] fArr = new float[1];
                                float f = 1.0f;
                                fArr[0] = show ? 1.0f : 0.0f;
                                animatorArr[0] = ObjectAnimator.ofFloat(access$1900, property, fArr);
                                SimpleTextView titleTextView = TwoStepVerificationSetupActivity.this.actionBar.getTitleTextView();
                                Property property2 = View.ALPHA;
                                float[] fArr2 = new float[1];
                                if (!show) {
                                    f = 0.0f;
                                }
                                fArr2[0] = f;
                                animatorArr[1] = ObjectAnimator.ofFloat(titleTextView, property2, fArr2);
                                access$2400.playTogether(animatorArr);
                                TwoStepVerificationSetupActivity.this.actionBarAnimator.setDuration(150);
                                TwoStepVerificationSetupActivity.this.actionBarAnimator.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animation) {
                                        if (animation.equals(TwoStepVerificationSetupActivity.this.actionBarAnimator)) {
                                            AnimatorSet unused = TwoStepVerificationSetupActivity.this.actionBarAnimator = null;
                                        }
                                    }
                                });
                                TwoStepVerificationSetupActivity.this.actionBarAnimator.start();
                            }
                        }
                    }

                    public void scrollToDescendant(View child) {
                        child.getDrawingRect(this.tempRect);
                        offsetDescendantRectToMyCoords(child, this.tempRect);
                        this.tempRect.bottom += AndroidUtilities.dp(120.0f);
                        int scrollDelta = computeScrollDeltaToGetChildRectOnScreen(this.tempRect);
                        if (scrollDelta < 0) {
                            int measuredHeight = (getMeasuredHeight() - child.getMeasuredHeight()) / 2;
                            this.scrollingUp = measuredHeight;
                            scrollDelta -= measuredHeight;
                        } else {
                            this.scrollingUp = 0;
                        }
                        if (scrollDelta != 0) {
                            smoothScrollBy(0, scrollDelta);
                        }
                    }

                    public void requestChildFocus(View child, View focused) {
                        if (Build.VERSION.SDK_INT < 29 && focused != null && !this.isLayoutDirty) {
                            scrollToDescendant(focused);
                        }
                        super.requestChildFocus(child, focused);
                    }

                    public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                        if (Build.VERSION.SDK_INT < 23) {
                            rectangle.bottom += AndroidUtilities.dp(120.0f);
                            if (this.scrollingUp != 0) {
                                rectangle.top -= this.scrollingUp;
                                rectangle.bottom -= this.scrollingUp;
                                this.scrollingUp = 0;
                            }
                        }
                        return super.requestChildRectangleOnScreen(child, rectangle, immediate);
                    }

                    public void requestLayout() {
                        this.isLayoutDirty = true;
                        super.requestLayout();
                    }

                    /* access modifiers changed from: protected */
                    public void onLayout(boolean changed, int l, int t, int r, int b) {
                        this.isLayoutDirty = false;
                        super.onLayout(changed, l, t, r, b);
                    }
                };
                this.scrollView = r10;
                r10.setVerticalScrollBarEnabled(false);
                frameLayout.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f));
                frameLayout.addView(this.bottomSkipButton, LayoutHelper.createFrame(-1, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 80, 0.0f, 0.0f, 0.0f, 16.0f));
                frameLayout.addView(this.floatingButtonContainer, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 85, 0.0f, 0.0f, 24.0f, 16.0f));
                container.addView(keyboardFrameLayout, LayoutHelper.createFrame(-1, -1.0f));
                LinearLayout scrollViewLinearLayout = new LinearLayout(context2) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) TwoStepVerificationSetupActivity.this.titleTextView.getLayoutParams();
                        int i = 0;
                        int dp = ((TwoStepVerificationSetupActivity.this.imageView.getVisibility() != 8 || Build.VERSION.SDK_INT < 21) ? 0 : AndroidUtilities.statusBarHeight) + AndroidUtilities.dp(8.0f);
                        if (TwoStepVerificationSetupActivity.this.currentType == 2 && AndroidUtilities.isSmallScreen() && !TwoStepVerificationSetupActivity.this.isLandscape()) {
                            i = AndroidUtilities.dp(32.0f);
                        }
                        params.topMargin = dp + i;
                    }
                };
                scrollViewLinearLayout.setOrientation(1);
                this.scrollView.addView(scrollViewLinearLayout, LayoutHelper.createScroll(-1, -1, 51));
                scrollViewLinearLayout.addView(this.imageView, LayoutHelper.createLinear(-2, -2, 49, 0, 69, 0, 0));
                scrollViewLinearLayout.addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 8, 0, 0));
                scrollViewLinearLayout.addView(this.descriptionText, LayoutHelper.createLinear(-2, -2, 49, 0, 9, 0, 0));
                OutlineTextContainerView outlineTextContainerView = new OutlineTextContainerView(context2);
                this.outlineTextFirstRow = outlineTextContainerView;
                outlineTextContainerView.animateSelection(1.0f, false);
                EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
                this.editTextFirstRow = editTextBoldCursor;
                editTextBoldCursor.setTextSize(1, 18.0f);
                int padding = AndroidUtilities.dp(16.0f);
                this.editTextFirstRow.setPadding(padding, padding, padding, padding);
                this.editTextFirstRow.setCursorColor(Theme.getColor("windowBackgroundWhiteInputFieldActivated"));
                this.editTextFirstRow.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                this.editTextFirstRow.setBackground((Drawable) null);
                this.editTextFirstRow.setMaxLines(1);
                this.editTextFirstRow.setLines(1);
                this.editTextFirstRow.setGravity(3);
                this.editTextFirstRow.setCursorSize(AndroidUtilities.dp(20.0f));
                this.editTextFirstRow.setSingleLine(true);
                this.editTextFirstRow.setCursorWidth(1.5f);
                this.editTextFirstRow.setOnEditorActionListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda8(this));
                this.outlineTextFirstRow.attachEditText(this.editTextFirstRow);
                this.editTextFirstRow.setOnFocusChangeListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda4(this));
                LinearLayout firstRowLinearLayout = new LinearLayout(context2);
                firstRowLinearLayout.setOrientation(0);
                firstRowLinearLayout.addView(this.editTextFirstRow, LayoutHelper.createLinear(0, -2, 1.0f));
                AnonymousClass9 r6 = new ImageView(context2) {
                    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                        super.onInitializeAccessibilityNodeInfo(info);
                        boolean z = true;
                        info.setCheckable(true);
                        if (TwoStepVerificationSetupActivity.this.editTextFirstRow.getTransformationMethod() != null) {
                            z = false;
                        }
                        info.setChecked(z);
                    }
                };
                this.showPasswordButton = r6;
                r6.setImageResource(NUM);
                this.showPasswordButton.setScaleType(ImageView.ScaleType.CENTER);
                this.showPasswordButton.setContentDescription(LocaleController.getString(NUM));
                if (Build.VERSION.SDK_INT >= 21) {
                    this.showPasswordButton.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
                }
                this.showPasswordButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
                AndroidUtilities.updateViewVisibilityAnimated(this.showPasswordButton, false, 0.1f, false);
                this.showPasswordButton.setOnClickListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda48(this));
                firstRowLinearLayout.addView(this.showPasswordButton, LayoutHelper.createLinear(24, 24, 16, 0, 0, 16, 0));
                this.editTextFirstRow.addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(Editable s) {
                        if (!TwoStepVerificationSetupActivity.this.needPasswordButton) {
                            return;
                        }
                        if (TwoStepVerificationSetupActivity.this.showPasswordButton.getVisibility() != 0 && !TextUtils.isEmpty(s)) {
                            AndroidUtilities.updateViewVisibilityAnimated(TwoStepVerificationSetupActivity.this.showPasswordButton, true, 0.1f, true);
                        } else if (TwoStepVerificationSetupActivity.this.showPasswordButton.getVisibility() != 8 && TextUtils.isEmpty(s)) {
                            AndroidUtilities.updateViewVisibilityAnimated(TwoStepVerificationSetupActivity.this.showPasswordButton, false, 0.1f, true);
                        }
                    }
                });
                this.outlineTextFirstRow.addView(firstRowLinearLayout, LayoutHelper.createFrame(-1, -2.0f));
                scrollViewLinearLayout.addView(this.outlineTextFirstRow, LayoutHelper.createFrame(-1, -2.0f, 49, 24.0f, 32.0f, 24.0f, 32.0f));
                this.outlineTextSecondRow = new OutlineTextContainerView(context2);
                EditTextBoldCursor editTextBoldCursor2 = new EditTextBoldCursor(context2);
                this.editTextSecondRow = editTextBoldCursor2;
                editTextBoldCursor2.setTextSize(1, 18.0f);
                int padding2 = AndroidUtilities.dp(16.0f);
                this.editTextSecondRow.setPadding(padding2, padding2, padding2, padding2);
                this.editTextSecondRow.setCursorColor(Theme.getColor("windowBackgroundWhiteInputFieldActivated"));
                this.editTextSecondRow.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                this.editTextSecondRow.setBackground((Drawable) null);
                this.editTextSecondRow.setMaxLines(1);
                this.editTextSecondRow.setLines(1);
                this.editTextSecondRow.setGravity(3);
                this.editTextSecondRow.setCursorSize(AndroidUtilities.dp(20.0f));
                this.editTextSecondRow.setSingleLine(true);
                this.editTextSecondRow.setCursorWidth(1.5f);
                this.editTextSecondRow.setOnEditorActionListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda9(this));
                this.outlineTextSecondRow.attachEditText(this.editTextSecondRow);
                this.editTextSecondRow.setOnFocusChangeListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda5(this));
                this.outlineTextSecondRow.addView(this.editTextSecondRow, LayoutHelper.createFrame(-1, -2.0f));
                scrollViewLinearLayout.addView(this.outlineTextSecondRow, LayoutHelper.createFrame(-1, -2.0f, 49, 24.0f, 16.0f, 24.0f, 0.0f));
                this.outlineTextSecondRow.setVisibility(8);
                CustomPhoneKeyboardView customPhoneKeyboardView = new CustomPhoneKeyboardView(context2);
                this.keyboardView = customPhoneKeyboardView;
                customPhoneKeyboardView.setVisibility(8);
                keyboardFrameLayout.addView(this.keyboardView);
                AnonymousClass11 r5 = new CodeFieldContainer(context2) {
                    /* access modifiers changed from: protected */
                    public void processNextPressed() {
                        TwoStepVerificationSetupActivity.this.processNext();
                    }
                };
                this.codeFieldContainer = r5;
                r5.setNumbersCount(6, 1);
                for (CodeNumberField f : this.codeFieldContainer.codeField) {
                    f.setShowSoftInputOnFocusCompat(!isCustomKeyboardVisible());
                    f.addTextChangedListener(new TextWatcher() {
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        public void afterTextChanged(Editable s) {
                            if (TwoStepVerificationSetupActivity.this.postedErrorColorTimeout) {
                                AndroidUtilities.cancelRunOnUIThread(TwoStepVerificationSetupActivity.this.errorColorTimeout);
                                TwoStepVerificationSetupActivity.this.errorColorTimeout.run();
                            }
                        }
                    });
                    f.setOnFocusChangeListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda6(this));
                }
                this.codeFieldContainer.setVisibility(8);
                scrollViewLinearLayout.addView(this.codeFieldContainer, LayoutHelper.createLinear(-2, -2, 1, 0, 32, 0, 0));
                FrameLayout frameLayout2 = new FrameLayout(context2);
                scrollViewLinearLayout.addView(frameLayout2, LayoutHelper.createLinear(-1, -2, 51, 0, 36, 0, 22));
                frameLayout2.addView(this.descriptionText2, LayoutHelper.createFrame(-2, -2, 49));
                if (this.currentType == 4) {
                    TextView textView5 = new TextView(context2);
                    this.descriptionText3 = textView5;
                    textView5.setTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
                    this.descriptionText3.setGravity(1);
                    this.descriptionText3.setTextSize(1, 14.0f);
                    this.descriptionText3.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
                    this.descriptionText3.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
                    this.descriptionText3.setText(LocaleController.getString("RestoreEmailTroubleNoEmail", NUM));
                    scrollViewLinearLayout.addView(this.descriptionText3, LayoutHelper.createLinear(-2, -2, 49, 0, 0, 0, 25));
                    this.descriptionText3.setOnClickListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda49(this));
                }
                this.fragmentView = container;
                AnonymousClass13 r8 = new View(context2) {
                    private Paint paint = new Paint();

                    /* access modifiers changed from: protected */
                    public void onDraw(Canvas canvas) {
                        this.paint.setColor(Theme.getColor("windowBackgroundWhite"));
                        int h = getMeasuredHeight() - AndroidUtilities.dp(3.0f);
                        canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) h, this.paint);
                        TwoStepVerificationSetupActivity.this.parentLayout.drawHeaderShadow(canvas, h);
                    }
                };
                this.actionBarBackground = r8;
                r8.setAlpha(0.0f);
                container.addView(this.actionBarBackground);
                container.addView(this.actionBar);
                RadialProgressView radialProgressView3 = new RadialProgressView(context2);
                this.radialProgressView = radialProgressView3;
                radialProgressView3.setSize(AndroidUtilities.dp(20.0f));
                this.radialProgressView.setAlpha(0.0f);
                this.radialProgressView.setScaleX(0.1f);
                this.radialProgressView.setScaleY(0.1f);
                this.radialProgressView.setProgressColor(Theme.getColor("windowBackgroundWhiteInputFieldActivated"));
                frameLayout.addView(this.radialProgressView, LayoutHelper.createFrame(32, 32.0f, 53, 0.0f, 16.0f, 16.0f, 0.0f));
                break;
            case 6:
            case 7:
            case 9:
                ViewGroup container2 = new ViewGroup(context2) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        int width = View.MeasureSpec.getSize(widthMeasureSpec);
                        int height = View.MeasureSpec.getSize(heightMeasureSpec);
                        TwoStepVerificationSetupActivity.this.actionBar.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), heightMeasureSpec);
                        if (width > height) {
                            TwoStepVerificationSetupActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec((int) (((float) width) * 0.45f), NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) height) * 0.68f), NUM));
                            TwoStepVerificationSetupActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec((int) (((float) width) * 0.6f), NUM), View.MeasureSpec.makeMeasureSpec(height, 0));
                            TwoStepVerificationSetupActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec((int) (((float) width) * 0.6f), NUM), View.MeasureSpec.makeMeasureSpec(height, 0));
                            TwoStepVerificationSetupActivity.this.descriptionText2.measure(View.MeasureSpec.makeMeasureSpec((int) (((float) width) * 0.6f), NUM), View.MeasureSpec.makeMeasureSpec(height, 0));
                            TwoStepVerificationSetupActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec((int) (((float) width) * 0.6f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                        } else {
                            int imageSize = TwoStepVerificationSetupActivity.this.currentType == 7 ? 160 : 140;
                            TwoStepVerificationSetupActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) imageSize), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) imageSize), NUM));
                            TwoStepVerificationSetupActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(height, 0));
                            TwoStepVerificationSetupActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(height, 0));
                            TwoStepVerificationSetupActivity.this.descriptionText2.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(height, 0));
                            TwoStepVerificationSetupActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp(48.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
                        }
                        setMeasuredDimension(width, height);
                    }

                    /* access modifiers changed from: protected */
                    public void onLayout(boolean changed, int l, int t, int r, int b) {
                        TwoStepVerificationSetupActivity.this.actionBar.layout(0, 0, r, TwoStepVerificationSetupActivity.this.actionBar.getMeasuredHeight());
                        int width = r - l;
                        int height = b - t;
                        if (r > b) {
                            int y = (height - TwoStepVerificationSetupActivity.this.imageView.getMeasuredHeight()) / 2;
                            TwoStepVerificationSetupActivity.this.imageView.layout(0, y, TwoStepVerificationSetupActivity.this.imageView.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.imageView.getMeasuredHeight() + y);
                            int x = (int) (((float) width) * 0.4f);
                            int y2 = (int) (((float) height) * 0.22f);
                            TwoStepVerificationSetupActivity.this.titleTextView.layout(x, y2, TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredWidth() + x, TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredHeight() + y2);
                            int x2 = (int) (((float) width) * 0.4f);
                            int y3 = (int) (((float) height) * 0.39f);
                            TwoStepVerificationSetupActivity.this.descriptionText.layout(x2, y3, TwoStepVerificationSetupActivity.this.descriptionText.getMeasuredWidth() + x2, TwoStepVerificationSetupActivity.this.descriptionText.getMeasuredHeight() + y3);
                            int x3 = (int) ((((float) width) * 0.4f) + (((((float) width) * 0.6f) - ((float) TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                            int y4 = (int) (((float) height) * 0.64f);
                            TwoStepVerificationSetupActivity.this.buttonTextView.layout(x3, y4, TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredWidth() + x3, TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredHeight() + y4);
                            return;
                        }
                        int y5 = (int) (((float) height) * 0.3f);
                        int x4 = (width - TwoStepVerificationSetupActivity.this.imageView.getMeasuredWidth()) / 2;
                        TwoStepVerificationSetupActivity.this.imageView.layout(x4, y5, TwoStepVerificationSetupActivity.this.imageView.getMeasuredWidth() + x4, TwoStepVerificationSetupActivity.this.imageView.getMeasuredHeight() + y5);
                        int y6 = y5 + TwoStepVerificationSetupActivity.this.imageView.getMeasuredHeight() + AndroidUtilities.dp(16.0f);
                        TwoStepVerificationSetupActivity.this.titleTextView.layout(0, y6, TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredHeight() + y6);
                        int y7 = y6 + TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredHeight() + AndroidUtilities.dp(12.0f);
                        TwoStepVerificationSetupActivity.this.descriptionText.layout(0, y7, TwoStepVerificationSetupActivity.this.descriptionText.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.descriptionText.getMeasuredHeight() + y7);
                        int x5 = (width - TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                        int y8 = (height - TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredHeight()) - AndroidUtilities.dp(48.0f);
                        TwoStepVerificationSetupActivity.this.buttonTextView.layout(x5, y8, TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredWidth() + x5, TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredHeight() + y8);
                    }
                };
                container2.setOnTouchListener(TwoStepVerificationSetupActivity$$ExternalSyntheticLambda7.INSTANCE);
                container2.addView(this.actionBar);
                container2.addView(this.imageView);
                container2.addView(this.titleTextView);
                container2.addView(this.descriptionText);
                container2.addView(this.buttonTextView);
                this.fragmentView = container2;
                break;
        }
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        switch (this.currentType) {
            case 0:
            case 1:
                if (this.currentPassword.has_password) {
                    this.actionBar.setTitle(LocaleController.getString("PleaseEnterNewFirstPassword", NUM));
                    this.titleTextView.setText(LocaleController.getString("PleaseEnterNewFirstPassword", NUM));
                } else {
                    CharSequence title = LocaleController.getString(this.currentType == 0 ? NUM : NUM);
                    this.actionBar.setTitle(title);
                    this.titleTextView.setText(title);
                }
                if (!TextUtils.isEmpty(this.emailCode)) {
                    this.bottomSkipButton.setVisibility(0);
                    this.bottomSkipButton.setText(LocaleController.getString("YourEmailSkip", NUM));
                }
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                this.outlineTextFirstRow.setText(LocaleController.getString(this.currentType == 0 ? NUM : NUM));
                this.editTextFirstRow.setContentDescription(LocaleController.getString(this.currentType == 0 ? NUM : NUM));
                this.editTextFirstRow.setImeOptions(NUM);
                this.editTextFirstRow.setInputType(129);
                this.editTextFirstRow.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.editTextFirstRow.setTypeface(Typeface.DEFAULT);
                this.needPasswordButton = this.currentType == 0;
                AndroidUtilities.updateViewVisibilityAnimated(this.showPasswordButton, false, 0.1f, false);
                RLottieDrawable[] rLottieDrawableArr = new RLottieDrawable[7];
                this.animationDrawables = rLottieDrawableArr;
                rLottieDrawableArr[0] = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, (int[]) null);
                this.animationDrawables[1] = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, (int[]) null);
                this.animationDrawables[2] = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, (int[]) null);
                this.animationDrawables[3] = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, (int[]) null);
                this.animationDrawables[4] = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, (int[]) null);
                this.animationDrawables[5] = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, (int[]) null);
                this.animationDrawables[6] = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, (int[]) null);
                this.animationDrawables[6].setPlayInDirectionOfCustomEndFrame(true);
                this.animationDrawables[6].setCustomEndFrame(19);
                this.animationDrawables[2].setOnFinishCallback(this.finishCallback, 97);
                setRandomMonkeyIdleAnimation(true);
                if (this.currentType == 1) {
                    z = true;
                }
                switchMonkeyAnimation(z);
                break;
            case 2:
                this.actionBar.setTitle(LocaleController.getString("PasswordHint", NUM));
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                this.bottomSkipButton.setVisibility(0);
                this.bottomSkipButton.setText(LocaleController.getString("YourEmailSkip", NUM));
                this.titleTextView.setText(LocaleController.getString("PasswordHint", NUM));
                this.descriptionText.setText(LocaleController.getString(NUM));
                this.descriptionText.setVisibility(0);
                this.outlineTextFirstRow.setText(LocaleController.getString(NUM));
                this.editTextFirstRow.setContentDescription(LocaleController.getString(NUM));
                this.editTextFirstRow.setImeOptions(NUM);
                this.outlineTextSecondRow.setVisibility(8);
                this.imageView.setAnimation(NUM, 120, 120);
                this.imageView.playAnimation();
                break;
            case 3:
                this.actionBar.setTitle(LocaleController.getString("RecoveryEmailTitle", NUM));
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                if (!this.emailOnly) {
                    this.bottomSkipButton.setVisibility(0);
                    this.bottomSkipButton.setText(LocaleController.getString("YourEmailSkip", NUM));
                }
                this.titleTextView.setText(LocaleController.getString("RecoveryEmailTitle", NUM));
                this.outlineTextFirstRow.setText(LocaleController.getString(NUM));
                this.editTextFirstRow.setContentDescription(LocaleController.getString(NUM));
                this.editTextFirstRow.setImeOptions(NUM);
                this.editTextFirstRow.setInputType(33);
                this.outlineTextSecondRow.setVisibility(8);
                this.imageView.setAnimation(NUM, 120, 120);
                this.imageView.playAnimation();
                break;
            case 4:
                this.actionBar.setTitle(LocaleController.getString("PasswordRecovery", NUM));
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                this.titleTextView.setText(LocaleController.getString("PasswordRecovery", NUM));
                this.keyboardView.setVisibility(0);
                this.outlineTextFirstRow.setVisibility(8);
                String rawPattern = this.currentPassword.email_unconfirmed_pattern != null ? this.currentPassword.email_unconfirmed_pattern : "";
                SpannableStringBuilder emailPattern = SpannableStringBuilder.valueOf(rawPattern);
                int startIndex = rawPattern.indexOf(42);
                int endIndex = rawPattern.lastIndexOf(42);
                if (!(startIndex == endIndex || startIndex == -1 || endIndex == -1)) {
                    TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
                    run.flags |= 256;
                    run.start = startIndex;
                    run.end = endIndex + 1;
                    emailPattern.setSpan(new TextStyleSpan(run), startIndex, endIndex + 1, 0);
                }
                this.descriptionText.setText(AndroidUtilities.formatSpannable(LocaleController.getString(NUM), emailPattern));
                this.descriptionText.setVisibility(0);
                this.floatingButtonContainer.setVisibility(8);
                this.codeFieldContainer.setVisibility(0);
                this.imageView.setAnimation(NUM, 120, 120);
                this.imageView.playAnimation();
                break;
            case 5:
                this.actionBar.setTitle(LocaleController.getString("VerificationCode", NUM));
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                this.titleTextView.setText(LocaleController.getString("VerificationCode", NUM));
                this.outlineTextFirstRow.setVisibility(8);
                this.keyboardView.setVisibility(0);
                TextView textView6 = this.descriptionText;
                Object[] objArr = new Object[1];
                objArr[0] = this.currentPassword.email_unconfirmed_pattern != null ? this.currentPassword.email_unconfirmed_pattern : "";
                textView6.setText(LocaleController.formatString("EmailPasswordConfirmText2", NUM, objArr));
                this.descriptionText.setVisibility(0);
                this.floatingButtonContainer.setVisibility(8);
                this.bottomSkipButton.setVisibility(0);
                this.bottomSkipButton.setGravity(17);
                ((ViewGroup.MarginLayoutParams) this.bottomSkipButton.getLayoutParams()).bottomMargin = 0;
                this.bottomSkipButton.setText(LocaleController.getString(NUM));
                this.bottomSkipButton.setOnClickListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda51(this));
                this.codeFieldContainer.setVisibility(0);
                this.imageView.setAnimation(NUM, 120, 120);
                this.imageView.playAnimation();
                break;
            case 6:
                this.titleTextView.setText(LocaleController.getString("TwoStepVerificationTitle", NUM));
                this.descriptionText.setText(LocaleController.getString("SetAdditionalPasswordInfo", NUM));
                this.buttonTextView.setText(LocaleController.getString("TwoStepVerificationSetPassword", NUM));
                this.descriptionText.setVisibility(0);
                this.imageView.setAnimation(NUM, 140, 140);
                this.imageView.playAnimation();
                break;
            case 7:
                this.titleTextView.setText(LocaleController.getString("TwoStepVerificationPasswordSet", NUM));
                this.descriptionText.setText(LocaleController.getString("TwoStepVerificationPasswordSetInfo", NUM));
                if (this.closeAfterSet) {
                    this.buttonTextView.setText(LocaleController.getString("TwoStepVerificationPasswordReturnPassport", NUM));
                } else if (this.fromRegistration) {
                    this.buttonTextView.setText(LocaleController.getString(NUM));
                } else {
                    this.buttonTextView.setText(LocaleController.getString("TwoStepVerificationPasswordReturnSettings", NUM));
                }
                this.descriptionText.setVisibility(0);
                this.imageView.setAnimation(NUM, 160, 160);
                this.imageView.playAnimation();
                break;
            case 8:
                this.actionBar.setTitle(LocaleController.getString("PleaseEnterCurrentPassword", NUM));
                this.titleTextView.setText(LocaleController.getString("PleaseEnterCurrentPassword", NUM));
                this.descriptionText.setText(LocaleController.getString("CheckPasswordInfo", NUM));
                this.descriptionText.setVisibility(0);
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                this.descriptionText2.setText(LocaleController.getString("ForgotPassword", NUM));
                this.descriptionText2.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText2"));
                this.outlineTextFirstRow.setText(LocaleController.getString(NUM));
                this.editTextFirstRow.setContentDescription(LocaleController.getString(NUM));
                this.editTextFirstRow.setImeOptions(NUM);
                this.editTextFirstRow.setInputType(129);
                this.editTextFirstRow.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.editTextFirstRow.setTypeface(Typeface.DEFAULT);
                this.imageView.setAnimation(NUM, 120, 120);
                this.imageView.playAnimation();
                break;
            case 9:
                this.titleTextView.setText(LocaleController.getString("CheckPasswordPerfect", NUM));
                this.descriptionText.setText(LocaleController.getString("CheckPasswordPerfectInfo", NUM));
                this.buttonTextView.setText(LocaleController.getString("CheckPasswordBackToSettings", NUM));
                this.descriptionText.setVisibility(0);
                this.imageView.setAnimation(NUM, 140, 140);
                this.imageView.playAnimation();
                break;
        }
        EditTextBoldCursor editTextBoldCursor3 = this.editTextFirstRow;
        if (editTextBoldCursor3 != null) {
            editTextBoldCursor3.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable s) {
                    if (!TwoStepVerificationSetupActivity.this.ignoreTextChange) {
                        if (TwoStepVerificationSetupActivity.this.currentType == 0) {
                            RLottieDrawable currentDrawable = TwoStepVerificationSetupActivity.this.imageView.getAnimatedDrawable();
                            if (TwoStepVerificationSetupActivity.this.editTextFirstRow.length() > 0) {
                                if (TwoStepVerificationSetupActivity.this.editTextFirstRow.getTransformationMethod() == null) {
                                    if (currentDrawable != TwoStepVerificationSetupActivity.this.animationDrawables[3] && currentDrawable != TwoStepVerificationSetupActivity.this.animationDrawables[5]) {
                                        TwoStepVerificationSetupActivity.this.imageView.setAnimation(TwoStepVerificationSetupActivity.this.animationDrawables[5]);
                                        TwoStepVerificationSetupActivity.this.animationDrawables[5].setProgress(0.0f, false);
                                        TwoStepVerificationSetupActivity.this.imageView.playAnimation();
                                    }
                                } else if (currentDrawable == TwoStepVerificationSetupActivity.this.animationDrawables[3]) {
                                } else {
                                    if (currentDrawable != TwoStepVerificationSetupActivity.this.animationDrawables[2]) {
                                        TwoStepVerificationSetupActivity.this.imageView.setAnimation(TwoStepVerificationSetupActivity.this.animationDrawables[2]);
                                        TwoStepVerificationSetupActivity.this.animationDrawables[2].setCustomEndFrame(49);
                                        TwoStepVerificationSetupActivity.this.animationDrawables[2].setProgress(0.0f, false);
                                        TwoStepVerificationSetupActivity.this.imageView.playAnimation();
                                    } else if (TwoStepVerificationSetupActivity.this.animationDrawables[2].getCurrentFrame() < 49) {
                                        TwoStepVerificationSetupActivity.this.animationDrawables[2].setCustomEndFrame(49);
                                    }
                                }
                            } else if ((currentDrawable == TwoStepVerificationSetupActivity.this.animationDrawables[3] && TwoStepVerificationSetupActivity.this.editTextFirstRow.getTransformationMethod() == null) || currentDrawable == TwoStepVerificationSetupActivity.this.animationDrawables[5]) {
                                TwoStepVerificationSetupActivity.this.imageView.setAnimation(TwoStepVerificationSetupActivity.this.animationDrawables[4]);
                                TwoStepVerificationSetupActivity.this.animationDrawables[4].setProgress(0.0f, false);
                                TwoStepVerificationSetupActivity.this.imageView.playAnimation();
                            } else {
                                TwoStepVerificationSetupActivity.this.animationDrawables[2].setCustomEndFrame(-1);
                                if (currentDrawable != TwoStepVerificationSetupActivity.this.animationDrawables[2]) {
                                    TwoStepVerificationSetupActivity.this.imageView.setAnimation(TwoStepVerificationSetupActivity.this.animationDrawables[2]);
                                    TwoStepVerificationSetupActivity.this.animationDrawables[2].setCurrentFrame(49, false);
                                }
                                TwoStepVerificationSetupActivity.this.imageView.playAnimation();
                            }
                        } else if (TwoStepVerificationSetupActivity.this.currentType == 1) {
                            try {
                                TwoStepVerificationSetupActivity.this.animationDrawables[6].setCustomEndFrame((int) ((142.0f * Math.min(1.0f, TwoStepVerificationSetupActivity.this.editTextFirstRow.getLayout().getLineWidth(0) / ((float) TwoStepVerificationSetupActivity.this.editTextFirstRow.getWidth()))) + 18.0f));
                                TwoStepVerificationSetupActivity.this.imageView.playAnimation();
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                            }
                        } else if (TwoStepVerificationSetupActivity.this.currentType == 8 && s.length() > 0) {
                            TwoStepVerificationSetupActivity.this.showDoneButton(true);
                        }
                    }
                }
            });
        }
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3398x9cCLASSNAME(View view) {
        processNext();
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3404xCLASSNAMECLASSNAME(View v) {
        int i = this.currentType;
        if (i == 0) {
            needShowProgress();
            TLRPC.TL_auth_recoverPassword req = new TLRPC.TL_auth_recoverPassword();
            req.code = this.emailCode;
            getConnectionsManager().sendRequest(req, new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda36(this));
        } else if (i == 3) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setMessage(LocaleController.getString("YourEmailSkipWarningText", NUM));
            builder.setTitle(LocaleController.getString("YourEmailSkipWarning", NUM));
            builder.setPositiveButton(LocaleController.getString("YourEmailSkip", NUM), new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda22(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            AlertDialog alertDialog = builder.create();
            showDialog(alertDialog);
            TextView button = (TextView) alertDialog.getButton(-1);
            if (button != null) {
                button.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        } else if (i == 2) {
            onHintDone();
        }
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3402xdeCLASSNAMEe4(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda25(this, error));
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3401xed1var_c5(TLRPC.TL_error error) {
        String timeString;
        needHideProgress();
        if (error == null) {
            getMessagesController().removeSuggestion(0, "VALIDATE_PASSWORD");
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda11(this));
            builder.setMessage(LocaleController.getString("PasswordReset", NUM));
            builder.setTitle(LocaleController.getString("TwoStepVerificationTitle", NUM));
            Dialog dialog = showDialog(builder.create());
            if (dialog != null) {
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
            }
        } else if (error.text.startsWith("FLOOD_WAIT")) {
            int time = Utilities.parseInt(error.text).intValue();
            if (time < 60) {
                timeString = LocaleController.formatPluralString("Seconds", time);
            } else {
                timeString = LocaleController.formatPluralString("Minutes", time / 60);
            }
            showAlertWithText(LocaleController.getString("TwoStepVerificationTitle", NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
        } else {
            showAlertWithText(LocaleController.getString("TwoStepVerificationTitle", NUM), error.text);
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3400xfb75dba6(DialogInterface dialogInterface, int i) {
        int N = this.fragmentsToClose.size();
        for (int a = 0; a < N; a++) {
            this.fragmentsToClose.get(a).removeSelfFromStack();
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.twoStepPasswordChanged, new Object[0]);
        finishFragment();
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3403xd072ce03(DialogInterface dialogInterface, int i) {
        this.email = "";
        setNewPassword(false);
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3405xb3CLASSNAMEa41(View v) {
        if (this.currentType == 8) {
            TwoStepVerificationActivity fragment = new TwoStepVerificationActivity();
            fragment.setForgotPasswordOnShow();
            fragment.setPassword(this.currentPassword);
            fragment.setBlockingAlert(this.otherwiseReloginDays);
            presentFragment(fragment, true);
        }
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3406xa56fCLASSNAME(View v) {
        processNext();
    }

    static /* synthetic */ boolean lambda$createView$10(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$11$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ boolean m3390x30CLASSNAMEecd(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 && i != 6) {
            return false;
        }
        if (this.outlineTextSecondRow.getVisibility() == 0) {
            this.editTextSecondRow.requestFocus();
            return true;
        }
        processNext();
        return true;
    }

    /* renamed from: lambda$createView$12$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3391x226d34ec(View v, boolean hasFocus) {
        this.outlineTextFirstRow.animateSelection(hasFocus ? 1.0f : 0.0f);
    }

    /* renamed from: lambda$createView$13$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3392x1416db0b(View v) {
        this.ignoreTextChange = true;
        if (this.editTextFirstRow.getTransformationMethod() == null) {
            this.isPasswordVisible = false;
            this.editTextFirstRow.setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.showPasswordButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
            if (this.currentType == 0 && this.editTextFirstRow.length() > 0 && this.editTextFirstRow.hasFocus() && this.monkeyEndCallback == null) {
                this.animationDrawables[3].setCustomEndFrame(-1);
                RLottieDrawable animatedDrawable = this.imageView.getAnimatedDrawable();
                RLottieDrawable[] rLottieDrawableArr = this.animationDrawables;
                if (animatedDrawable != rLottieDrawableArr[3]) {
                    this.imageView.setAnimation(rLottieDrawableArr[3]);
                    this.animationDrawables[3].setCurrentFrame(18, false);
                }
                this.imageView.playAnimation();
            }
        } else {
            this.isPasswordVisible = true;
            this.editTextFirstRow.setTransformationMethod((TransformationMethod) null);
            this.showPasswordButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelSend"), PorterDuff.Mode.MULTIPLY));
            if (this.currentType == 0 && this.editTextFirstRow.length() > 0 && this.editTextFirstRow.hasFocus() && this.monkeyEndCallback == null) {
                this.animationDrawables[3].setCustomEndFrame(18);
                RLottieDrawable animatedDrawable2 = this.imageView.getAnimatedDrawable();
                RLottieDrawable[] rLottieDrawableArr2 = this.animationDrawables;
                if (animatedDrawable2 != rLottieDrawableArr2[3]) {
                    this.imageView.setAnimation(rLottieDrawableArr2[3]);
                }
                this.animationDrawables[3].setProgress(0.0f, false);
                this.imageView.playAnimation();
            }
        }
        EditTextBoldCursor editTextBoldCursor = this.editTextFirstRow;
        editTextBoldCursor.setSelection(editTextBoldCursor.length());
        this.ignoreTextChange = false;
    }

    /* renamed from: lambda$createView$14$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ boolean m3393x5CLASSNAMEa(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 && i != 6) {
            return false;
        }
        processNext();
        return true;
    }

    /* renamed from: lambda$createView$15$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3394xvar_a2749(View v, boolean hasFocus) {
        this.outlineTextSecondRow.animateSelection(hasFocus ? 1.0f : 0.0f);
    }

    /* renamed from: lambda$createView$16$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3395xe913cd68(View v, boolean hasFocus) {
        if (hasFocus) {
            this.keyboardView.setEditText((EditText) v);
            this.keyboardView.setDispatchBackWhenEmpty(true);
        }
    }

    /* renamed from: lambda$createView$18$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3397xcCLASSNAMEa6(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("Reset", NUM), new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda0(this));
        builder.setTitle(LocaleController.getString("ResetPassword", NUM));
        builder.setMessage(LocaleController.getString("RestoreEmailTroubleText2", NUM));
        showDialog(builder.create());
    }

    /* renamed from: lambda$createView$17$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3396xdabd7387(DialogInterface dialog, int which) {
        onReset();
        finishFragment();
    }

    /* renamed from: lambda$createView$20$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3399x82a5066f(View v) {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_resendPasswordEmail(), TwoStepVerificationSetupActivity$$ExternalSyntheticLambda46.INSTANCE);
        showDialog(new AlertDialog.Builder((Context) getParentActivity()).setMessage(LocaleController.getString("ResendCodeInfo", NUM)).setTitle(LocaleController.getString("TwoStepVerificationTitle", NUM)).setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null).create());
    }

    static /* synthetic */ void lambda$createView$19(TLObject response, TLRPC.TL_error error) {
    }

    private boolean isIntro() {
        int i = this.currentType;
        return i == 6 || i == 9 || i == 7;
    }

    /* access modifiers changed from: private */
    public boolean isLandscape() {
        return AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int i = 0;
        if (this.currentType == 2 && AndroidUtilities.isSmallScreen()) {
            this.imageView.setVisibility(8);
        } else if (!isIntro()) {
            this.imageView.setVisibility(isLandscape() ? 8 : 0);
        }
        CustomPhoneKeyboardView customPhoneKeyboardView = this.keyboardView;
        if (!isCustomKeyboardVisible()) {
            i = 8;
        }
        customPhoneKeyboardView.setVisibility(i);
    }

    private void animateSuccess(Runnable callback) {
        for (int i = 0; i < this.codeFieldContainer.codeField.length; i++) {
            CodeNumberField field = this.codeFieldContainer.codeField[i];
            field.postDelayed(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda10(field), ((long) i) * 75);
        }
        this.codeFieldContainer.postDelayed(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda20(this, callback), (((long) this.codeFieldContainer.codeField.length) * 75) + 350);
    }

    /* renamed from: lambda$animateSuccess$22$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3389x559d11ce(Runnable callback) {
        for (CodeNumberField f : this.codeFieldContainer.codeField) {
            f.animateSuccessProgress(0.0f);
        }
        callback.run();
    }

    private void switchMonkeyAnimation(boolean tracking) {
        if (tracking) {
            Runnable runnable = this.setAnimationRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            this.imageView.setAnimation(this.animationDrawables[6]);
            this.imageView.playAnimation();
            return;
        }
        this.editTextFirstRow.dispatchTextWatchersTextChanged();
        setRandomMonkeyIdleAnimation(true);
    }

    public boolean hasForceLightStatusBar() {
        return true;
    }

    /* access modifiers changed from: private */
    public boolean isCustomKeyboardVisible() {
        int i = this.currentType;
        return (i == 5 || i == 4) && !AndroidUtilities.isTablet() && AndroidUtilities.displaySize.x < AndroidUtilities.displaySize.y && !AndroidUtilities.isAccessibilityTouchExplorationEnabled();
    }

    public void onPause() {
        super.onPause();
        this.paused = true;
    }

    public void onResume() {
        super.onResume();
        this.paused = false;
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        if (isCustomKeyboardVisible()) {
            AndroidUtilities.requestAltFocusable(getParentActivity(), this.classGuid);
            AndroidUtilities.hideKeyboard(this.fragmentView);
        }
    }

    /* access modifiers changed from: private */
    public void processNext() {
        if (getParentActivity() != null) {
            int i = 1;
            switch (this.currentType) {
                case 0:
                case 1:
                    if (this.editTextFirstRow.length() == 0) {
                        onFieldError(this.outlineTextFirstRow, this.editTextFirstRow, false);
                        return;
                    } else if (this.editTextFirstRow.getText().toString().equals(this.firstPassword) || this.currentType != 1) {
                        int i2 = this.currentAccount;
                        if (this.currentType != 0) {
                            i = 2;
                        }
                        TwoStepVerificationSetupActivity fragment = new TwoStepVerificationSetupActivity(i2, i, this.currentPassword);
                        fragment.fromRegistration = this.fromRegistration;
                        fragment.firstPassword = this.editTextFirstRow.getText().toString();
                        fragment.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, this.emailOnly);
                        fragment.setCurrentEmailCode(this.emailCode);
                        fragment.fragmentsToClose.addAll(this.fragmentsToClose);
                        fragment.fragmentsToClose.add(this);
                        fragment.closeAfterSet = this.closeAfterSet;
                        fragment.setBlockingAlert(this.otherwiseReloginDays);
                        presentFragment(fragment);
                        return;
                    } else {
                        AndroidUtilities.shakeViewSpring((View) this.outlineTextFirstRow, 5.0f);
                        try {
                            this.outlineTextFirstRow.performHapticFeedback(3, 2);
                        } catch (Exception e) {
                        }
                        try {
                            Toast.makeText(getParentActivity(), LocaleController.getString("PasswordDoNotMatch", NUM), 0).show();
                            return;
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                            return;
                        }
                    }
                case 2:
                    String obj = this.editTextFirstRow.getText().toString();
                    this.hint = obj;
                    if (obj.equalsIgnoreCase(this.firstPassword)) {
                        try {
                            Toast.makeText(getParentActivity(), LocaleController.getString("PasswordAsHintError", NUM), 0).show();
                        } catch (Exception e3) {
                            FileLog.e((Throwable) e3);
                        }
                        onFieldError(this.outlineTextFirstRow, this.editTextFirstRow, false);
                        return;
                    }
                    onHintDone();
                    return;
                case 3:
                    String obj2 = this.editTextFirstRow.getText().toString();
                    this.email = obj2;
                    if (!isValidEmail(obj2)) {
                        onFieldError(this.outlineTextFirstRow, this.editTextFirstRow, false);
                        return;
                    } else {
                        setNewPassword(false);
                        return;
                    }
                case 4:
                    String code = this.codeFieldContainer.getCode();
                    TLRPC.TL_auth_checkRecoveryPassword req = new TLRPC.TL_auth_checkRecoveryPassword();
                    req.code = code;
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda41(this, code), 10);
                    return;
                case 5:
                    TLRPC.TL_account_confirmPasswordEmail req2 = new TLRPC.TL_account_confirmPasswordEmail();
                    req2.code = this.codeFieldContainer.getCode();
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda39(this), 10);
                    needShowProgress();
                    return;
                case 6:
                    if (this.currentPassword == null) {
                        needShowProgress();
                        this.doneAfterPasswordLoad = true;
                        return;
                    }
                    TwoStepVerificationSetupActivity fragment2 = new TwoStepVerificationSetupActivity(this.currentAccount, 0, this.currentPassword);
                    fragment2.fromRegistration = this.fromRegistration;
                    fragment2.closeAfterSet = this.closeAfterSet;
                    fragment2.setBlockingAlert(this.otherwiseReloginDays);
                    presentFragment(fragment2, true);
                    return;
                case 7:
                    if (this.closeAfterSet) {
                        finishFragment();
                        return;
                    } else if (this.fromRegistration) {
                        Bundle args = new Bundle();
                        args.putBoolean("afterSignup", true);
                        presentFragment(new DialogsActivity(args), true);
                        return;
                    } else {
                        TwoStepVerificationActivity fragment3 = new TwoStepVerificationActivity();
                        fragment3.setCurrentPasswordParams(this.currentPassword, this.currentPasswordHash, this.currentSecretId, this.currentSecret);
                        fragment3.setBlockingAlert(this.otherwiseReloginDays);
                        presentFragment(fragment3, true);
                        return;
                    }
                case 8:
                    if (this.currentPassword == null) {
                        needShowProgress();
                        this.doneAfterPasswordLoad = true;
                        return;
                    }
                    String oldPassword = this.editTextFirstRow.getText().toString();
                    if (oldPassword.length() == 0) {
                        onFieldError(this.outlineTextFirstRow, this.editTextFirstRow, false);
                        return;
                    }
                    byte[] oldPasswordBytes = AndroidUtilities.getStringBytes(oldPassword);
                    needShowProgress();
                    Utilities.globalQueue.postRunnable(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda35(this, oldPasswordBytes));
                    return;
                case 9:
                    finishFragment();
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: lambda$processNext$28$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3420x822863ce(byte[] oldPasswordBytes) {
        byte[] x_bytes;
        TLRPC.TL_account_getPasswordSettings req = new TLRPC.TL_account_getPasswordSettings();
        if (this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            x_bytes = SRPHelper.getX(oldPasswordBytes, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.current_algo);
        } else {
            x_bytes = null;
        }
        RequestDelegate requestDelegate = new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda45(this, x_bytes);
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

    /* renamed from: lambda$processNext$27$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3419x907ebdaf(byte[] x_bytes, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda34(this, x_bytes));
        } else {
            AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda26(this, error));
        }
    }

    /* renamed from: lambda$processNext$23$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3415xc9d82533(byte[] x_bytes) {
        needHideProgress();
        this.currentPasswordHash = x_bytes;
        getMessagesController().removeSuggestion(0, "VALIDATE_PASSWORD");
        TwoStepVerificationSetupActivity fragment = new TwoStepVerificationSetupActivity(9, this.currentPassword);
        fragment.fromRegistration = this.fromRegistration;
        fragment.setBlockingAlert(this.otherwiseReloginDays);
        presentFragment(fragment, true);
    }

    /* renamed from: lambda$processNext$26$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3418x9ed51790(TLRPC.TL_error error) {
        String timeString;
        if ("SRP_ID_INVALID".equals(error.text)) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda38(this), 8);
            return;
        }
        needHideProgress();
        if ("PASSWORD_HASH_INVALID".equals(error.text)) {
            this.descriptionText.setText(LocaleController.getString("CheckPasswordWrong", NUM));
            this.descriptionText.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
            onFieldError(this.outlineTextFirstRow, this.editTextFirstRow, true);
            showDoneButton(false);
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

    /* renamed from: lambda$processNext$25$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3417xad2b7171(TLObject response2, TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda30(this, error2, response2));
    }

    /* renamed from: lambda$processNext$24$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3416xbb81cb52(TLRPC.TL_error error2, TLObject response2) {
        if (error2 == null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response2;
            this.currentPassword = tL_account_password;
            TwoStepVerificationActivity.initPasswordNewAlgo(tL_account_password);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            processNext();
        }
    }

    /* renamed from: lambda$processNext$31$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3423x2a0ff6b6(String code, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda23(this, response, code, error));
    }

    /* renamed from: lambda$processNext$30$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3422x38665097(TLObject response, String code, TLRPC.TL_error error) {
        String timeString;
        if (response instanceof TLRPC.TL_boolTrue) {
            animateSuccess(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda21(this, code));
        } else if (error == null || error.text.startsWith("CODE_INVALID")) {
            onCodeFieldError(true);
        } else if (error.text.startsWith("FLOOD_WAIT")) {
            int time = Utilities.parseInt(error.text).intValue();
            if (time < 60) {
                timeString = LocaleController.formatPluralString("Seconds", time);
            } else {
                timeString = LocaleController.formatPluralString("Minutes", time / 60);
            }
            showAlertWithText(LocaleController.getString("TwoStepVerificationTitle", NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
        } else {
            showAlertWithText(LocaleController.getString("TwoStepVerificationTitle", NUM), error.text);
        }
    }

    /* renamed from: lambda$processNext$29$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3421x73d209ed(String code) {
        TwoStepVerificationSetupActivity fragment = new TwoStepVerificationSetupActivity(this.currentAccount, 0, this.currentPassword);
        fragment.fromRegistration = this.fromRegistration;
        fragment.fragmentsToClose.addAll(this.fragmentsToClose);
        fragment.addFragmentToClose(this);
        fragment.setCurrentEmailCode(code);
        fragment.setBlockingAlert(this.otherwiseReloginDays);
        presentFragment(fragment, true);
    }

    /* renamed from: lambda$processNext$35$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3427xf0b68var_(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda27(this, error));
    }

    /* renamed from: lambda$processNext$34$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3426xff0ce913(TLRPC.TL_error error) {
        String timeString;
        needHideProgress();
        if (error == null) {
            if (getParentActivity() != null) {
                animateSuccess(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda18(this));
            }
        } else if (error.text.startsWith("CODE_INVALID")) {
            onCodeFieldError(true);
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

    /* renamed from: lambda$processNext$33$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3425xd6342f4() {
        if (this.currentPassword.has_password) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda33(this));
            if (this.currentPassword.has_recovery) {
                builder.setMessage(LocaleController.getString("YourEmailSuccessChangedText", NUM));
            } else {
                builder.setMessage(LocaleController.getString("YourEmailSuccessText", NUM));
            }
            builder.setTitle(LocaleController.getString("YourPasswordSuccess", NUM));
            Dialog dialog = showDialog(builder.create());
            if (dialog != null) {
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                return;
            }
            return;
        }
        int N = this.fragmentsToClose.size();
        for (int a = 0; a < N; a++) {
            this.fragmentsToClose.get(a).removeSelfFromStack();
        }
        this.currentPassword.has_password = true;
        this.currentPassword.has_recovery = true;
        this.currentPassword.email_unconfirmed_pattern = "";
        TwoStepVerificationSetupActivity fragment = new TwoStepVerificationSetupActivity(7, this.currentPassword);
        fragment.fromRegistration = this.fromRegistration;
        fragment.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, this.emailOnly);
        fragment.fragmentsToClose.addAll(this.fragmentsToClose);
        fragment.closeAfterSet = this.closeAfterSet;
        fragment.setBlockingAlert(this.otherwiseReloginDays);
        presentFragment(fragment, true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.twoStepPasswordChanged, this.currentPasswordHash, this.currentPassword.new_algo, this.currentPassword.new_secure_algo, this.currentPassword.secure_random, this.email, this.hint, null, this.firstPassword);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
    }

    /* renamed from: lambda$processNext$32$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3424x1bb99cd5(DialogInterface dialogInterface, int i) {
        int N = this.fragmentsToClose.size();
        for (int a = 0; a < N; a++) {
            this.fragmentsToClose.get(a).removeSelfFromStack();
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.twoStepPasswordChanged, this.currentPasswordHash, this.currentPassword.new_algo, this.currentPassword.new_secure_algo, this.currentPassword.secure_random, this.email, this.hint, null, this.firstPassword);
        TwoStepVerificationActivity fragment = new TwoStepVerificationActivity();
        this.currentPassword.has_password = true;
        this.currentPassword.has_recovery = true;
        this.currentPassword.email_unconfirmed_pattern = "";
        fragment.setCurrentPasswordParams(this.currentPassword, this.currentPasswordHash, this.currentSecretId, this.currentSecret);
        fragment.setBlockingAlert(this.otherwiseReloginDays);
        presentFragment(fragment, true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
    }

    private void onCodeFieldError(boolean clear) {
        for (CodeNumberField f : this.codeFieldContainer.codeField) {
            if (clear) {
                f.setText("");
            }
            f.animateErrorProgress(1.0f);
        }
        if (clear) {
            this.codeFieldContainer.codeField[0].requestFocus();
        }
        AndroidUtilities.shakeViewSpring(this.codeFieldContainer, 8.0f, new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda15(this));
    }

    /* renamed from: lambda$onCodeFieldError$37$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3412x2ddc0bc2() {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda14(this), 150);
    }

    /* renamed from: lambda$onCodeFieldError$36$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3411x3CLASSNAMEa3() {
        for (CodeNumberField f : this.codeFieldContainer.codeField) {
            f.animateErrorProgress(0.0f);
        }
    }

    /* access modifiers changed from: protected */
    public boolean hideKeyboardOnShow() {
        int i = this.currentType;
        return i == 7 || i == 9;
    }

    private void onHintDone() {
        if (!this.currentPassword.has_recovery) {
            TwoStepVerificationSetupActivity fragment = new TwoStepVerificationSetupActivity(this.currentAccount, 3, this.currentPassword);
            fragment.fromRegistration = this.fromRegistration;
            fragment.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, this.emailOnly);
            fragment.firstPassword = this.firstPassword;
            fragment.hint = this.hint;
            fragment.fragmentsToClose.addAll(this.fragmentsToClose);
            fragment.fragmentsToClose.add(this);
            fragment.closeAfterSet = this.closeAfterSet;
            fragment.setBlockingAlert(this.otherwiseReloginDays);
            presentFragment(fragment);
            return;
        }
        this.email = "";
        setNewPassword(false);
    }

    /* access modifiers changed from: private */
    public void showDoneButton(final boolean show) {
        if (show != (this.buttonTextView.getTag() != null)) {
            AnimatorSet animatorSet = this.buttonAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.buttonTextView.setTag(show ? 1 : null);
            this.buttonAnimation = new AnimatorSet();
            if (show) {
                this.buttonTextView.setVisibility(0);
                this.buttonAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.descriptionText2, View.SCALE_X, new float[]{0.9f}), ObjectAnimator.ofFloat(this.descriptionText2, View.SCALE_Y, new float[]{0.9f}), ObjectAnimator.ofFloat(this.descriptionText2, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.buttonTextView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.buttonTextView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.buttonTextView, View.ALPHA, new float[]{1.0f})});
            } else {
                this.descriptionText2.setVisibility(0);
                this.buttonAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.buttonTextView, View.SCALE_X, new float[]{0.9f}), ObjectAnimator.ofFloat(this.buttonTextView, View.SCALE_Y, new float[]{0.9f}), ObjectAnimator.ofFloat(this.buttonTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.descriptionText2, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.descriptionText2, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.descriptionText2, View.ALPHA, new float[]{1.0f})});
            }
            this.buttonAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (TwoStepVerificationSetupActivity.this.buttonAnimation != null && TwoStepVerificationSetupActivity.this.buttonAnimation.equals(animation)) {
                        if (show) {
                            TwoStepVerificationSetupActivity.this.descriptionText2.setVisibility(4);
                        } else {
                            TwoStepVerificationSetupActivity.this.buttonTextView.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (TwoStepVerificationSetupActivity.this.buttonAnimation != null && TwoStepVerificationSetupActivity.this.buttonAnimation.equals(animation)) {
                        AnimatorSet unused = TwoStepVerificationSetupActivity.this.buttonAnimation = null;
                    }
                }
            });
            this.buttonAnimation.setDuration(150);
            this.buttonAnimation.start();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x002e, code lost:
        if (r0.isRunning() != false) goto L_0x0063;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setRandomMonkeyIdleAnimation(boolean r6) {
        /*
            r5 = this;
            int r0 = r5.currentType
            if (r0 == 0) goto L_0x0005
            return
        L_0x0005:
            java.lang.Runnable r0 = r5.setAnimationRunnable
            if (r0 == 0) goto L_0x000c
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
        L_0x000c:
            org.telegram.ui.Components.RLottieImageView r0 = r5.imageView
            org.telegram.ui.Components.RLottieDrawable r0 = r0.getAnimatedDrawable()
            r1 = 1
            r2 = 0
            if (r6 != 0) goto L_0x0030
            org.telegram.ui.Components.RLottieDrawable[] r3 = r5.animationDrawables
            r4 = r3[r2]
            if (r0 == r4) goto L_0x0030
            r3 = r3[r1]
            if (r0 == r3) goto L_0x0030
            org.telegram.ui.Components.EditTextBoldCursor r3 = r5.editTextFirstRow
            int r3 = r3.length()
            if (r3 != 0) goto L_0x0063
            if (r0 == 0) goto L_0x0030
            boolean r3 = r0.isRunning()
            if (r3 != 0) goto L_0x0063
        L_0x0030:
            java.security.SecureRandom r3 = org.telegram.messenger.Utilities.random
            int r3 = r3.nextInt()
            int r3 = r3 % 2
            r4 = 0
            if (r3 != 0) goto L_0x004c
            org.telegram.ui.Components.RLottieImageView r1 = r5.imageView
            org.telegram.ui.Components.RLottieDrawable[] r3 = r5.animationDrawables
            r3 = r3[r2]
            r1.setAnimation(r3)
            org.telegram.ui.Components.RLottieDrawable[] r1 = r5.animationDrawables
            r1 = r1[r2]
            r1.setProgress(r4)
            goto L_0x005c
        L_0x004c:
            org.telegram.ui.Components.RLottieImageView r2 = r5.imageView
            org.telegram.ui.Components.RLottieDrawable[] r3 = r5.animationDrawables
            r3 = r3[r1]
            r2.setAnimation(r3)
            org.telegram.ui.Components.RLottieDrawable[] r2 = r5.animationDrawables
            r1 = r2[r1]
            r1.setProgress(r4)
        L_0x005c:
            if (r6 != 0) goto L_0x0063
            org.telegram.ui.Components.RLottieImageView r1 = r5.imageView
            r1.playAnimation()
        L_0x0063:
            org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda19 r1 = new org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda19
            r1.<init>(r5)
            r5.setAnimationRunnable = r1
            java.security.SecureRandom r2 = org.telegram.messenger.Utilities.random
            r3 = 2000(0x7d0, float:2.803E-42)
            int r2 = r2.nextInt(r3)
            int r2 = r2 + 5000
            long r2 = (long) r2
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TwoStepVerificationSetupActivity.setRandomMonkeyIdleAnimation(boolean):void");
    }

    /* renamed from: lambda$setRandomMonkeyIdleAnimation$38$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3436xc4dba19f() {
        if (this.setAnimationRunnable != null) {
            setRandomMonkeyIdleAnimation(false);
        }
    }

    public void setCloseAfterSet(boolean value) {
        this.closeAfterSet = value;
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            if (this.editTextFirstRow != null && !isCustomKeyboardVisible()) {
                AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda16(this), 200);
            }
            CodeFieldContainer codeFieldContainer2 = this.codeFieldContainer;
            if (codeFieldContainer2 != null && codeFieldContainer2.getVisibility() == 0) {
                AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda17(this), 200);
            }
        }
    }

    /* renamed from: lambda$onTransitionAnimationEnd$39$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3413xCLASSNAMECLASSNAME() {
        EditTextBoldCursor editTextBoldCursor = this.editTextFirstRow;
        if (editTextBoldCursor != null && editTextBoldCursor.getVisibility() == 0) {
            this.editTextFirstRow.requestFocus();
            AndroidUtilities.showKeyboard(this.editTextFirstRow);
        }
    }

    /* renamed from: lambda$onTransitionAnimationEnd$40$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3414x892909db() {
        CodeFieldContainer codeFieldContainer2 = this.codeFieldContainer;
        if (codeFieldContainer2 != null && codeFieldContainer2.getVisibility() == 0) {
            this.codeFieldContainer.codeField[0].requestFocus();
        }
    }

    private void loadPasswordInfo() {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda37(this), 10);
    }

    /* renamed from: lambda$loadPasswordInfo$42$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3408xfa43ad9d(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda29(this, error, response));
    }

    /* renamed from: lambda$loadPasswordInfo$41$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3407x89a077e(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response;
            this.currentPassword = tL_account_password;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(tL_account_password, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
                return;
            }
            this.waitingForEmail = !TextUtils.isEmpty(this.currentPassword.email_unconfirmed_pattern);
            TwoStepVerificationActivity.initPasswordNewAlgo(this.currentPassword);
            if (!this.paused && this.closeAfterSet && this.currentPassword.has_password) {
                TLRPC.PasswordKdfAlgo pendingCurrentAlgo = this.currentPassword.current_algo;
                TLRPC.SecurePasswordKdfAlgo pendingNewSecureAlgo = this.currentPassword.new_secure_algo;
                byte[] pendingSecureRandom = this.currentPassword.secure_random;
                String pendingEmail = this.currentPassword.has_recovery ? "1" : null;
                String pendingHint = this.currentPassword.hint != null ? this.currentPassword.hint : "";
                if (!this.waitingForEmail && pendingCurrentAlgo != null) {
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.twoStepPasswordChanged, null, pendingCurrentAlgo, pendingNewSecureAlgo, pendingSecureRandom, pendingEmail, pendingHint, null, null);
                    finishFragment();
                }
            }
            if (this.doneAfterPasswordLoad) {
                needHideProgress();
                processNext();
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
        }
    }

    private void needShowProgress() {
        if (getParentActivity() != null && !getParentActivity().isFinishing()) {
            AnimatorSet set = new AnimatorSet();
            if (this.floatingButtonContainer.getVisibility() == 0) {
                set.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingProgressView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingProgressView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingProgressView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.SCALE_Y, new float[]{0.1f})});
            } else {
                set.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.radialProgressView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_Y, new float[]{1.0f})});
            }
            set.setInterpolator(CubicBezierInterpolator.DEFAULT);
            set.start();
        }
    }

    /* access modifiers changed from: protected */
    public void needHideProgress() {
        AnimatorSet set = new AnimatorSet();
        if (this.floatingButtonContainer.getVisibility() == 0) {
            set.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingProgressView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.floatingProgressView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.floatingProgressView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.SCALE_Y, new float[]{1.0f})});
        } else {
            set.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.radialProgressView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_Y, new float[]{0.1f})});
        }
        set.setInterpolator(CubicBezierInterpolator.DEFAULT);
        set.start();
    }

    private boolean isValidEmail(String text) {
        if (text == null || text.length() < 3) {
            return false;
        }
        int dot = text.lastIndexOf(46);
        int dog = text.lastIndexOf(64);
        if (dog < 0 || dot < dog) {
            return false;
        }
        return true;
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: org.telegram.tgnet.TLRPC$TL_account_updatePasswordSettings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v4, resolved type: org.telegram.tgnet.TLRPC$TL_auth_recoverPassword} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v13, resolved type: org.telegram.tgnet.TLRPC$TL_account_updatePasswordSettings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v14, resolved type: org.telegram.tgnet.TLRPC$TL_account_updatePasswordSettings} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setNewPassword(boolean r12) {
        /*
            r11 = this;
            if (r12 == 0) goto L_0x0023
            boolean r0 = r11.waitingForEmail
            if (r0 == 0) goto L_0x0023
            org.telegram.tgnet.TLRPC$TL_account_password r0 = r11.currentPassword
            boolean r0 = r0.has_password
            if (r0 == 0) goto L_0x0023
            r11.needShowProgress()
            org.telegram.tgnet.TLRPC$TL_account_cancelPasswordEmail r0 = new org.telegram.tgnet.TLRPC$TL_account_cancelPasswordEmail
            r0.<init>()
            int r1 = r11.currentAccount
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda40 r2 = new org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda40
            r2.<init>(r11)
            r1.sendRequest(r0, r2)
            return
        L_0x0023:
            java.lang.String r0 = r11.firstPassword
            org.telegram.tgnet.TLRPC$TL_account_passwordInputSettings r1 = new org.telegram.tgnet.TLRPC$TL_account_passwordInputSettings
            r1.<init>()
            r2 = 2
            java.lang.String r3 = ""
            if (r12 == 0) goto L_0x0058
            int r4 = r11.currentAccount
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r4)
            r4.resetSavedPassword()
            r4 = 0
            r11.currentSecret = r4
            boolean r4 = r11.waitingForEmail
            if (r4 == 0) goto L_0x0044
            r1.flags = r2
            r1.email = r3
            goto L_0x0091
        L_0x0044:
            r2 = 3
            r1.flags = r2
            r1.hint = r3
            r2 = 0
            byte[] r2 = new byte[r2]
            r1.new_password_hash = r2
            org.telegram.tgnet.TLRPC$TL_passwordKdfAlgoUnknown r2 = new org.telegram.tgnet.TLRPC$TL_passwordKdfAlgoUnknown
            r2.<init>()
            r1.new_algo = r2
            r1.email = r3
            goto L_0x0091
        L_0x0058:
            java.lang.String r4 = r11.hint
            if (r4 != 0) goto L_0x0064
            org.telegram.tgnet.TLRPC$TL_account_password r4 = r11.currentPassword
            if (r4 == 0) goto L_0x0064
            java.lang.String r4 = r4.hint
            r11.hint = r4
        L_0x0064:
            java.lang.String r4 = r11.hint
            if (r4 != 0) goto L_0x006a
            r11.hint = r3
        L_0x006a:
            if (r0 == 0) goto L_0x007c
            int r3 = r1.flags
            r3 = r3 | 1
            r1.flags = r3
            java.lang.String r3 = r11.hint
            r1.hint = r3
            org.telegram.tgnet.TLRPC$TL_account_password r3 = r11.currentPassword
            org.telegram.tgnet.TLRPC$PasswordKdfAlgo r3 = r3.new_algo
            r1.new_algo = r3
        L_0x007c:
            java.lang.String r3 = r11.email
            int r3 = r3.length()
            if (r3 <= 0) goto L_0x0091
            int r3 = r1.flags
            r2 = r2 | r3
            r1.flags = r2
            java.lang.String r2 = r11.email
            java.lang.String r2 = r2.trim()
            r1.email = r2
        L_0x0091:
            java.lang.String r2 = r11.emailCode
            if (r2 == 0) goto L_0x00a8
            org.telegram.tgnet.TLRPC$TL_auth_recoverPassword r2 = new org.telegram.tgnet.TLRPC$TL_auth_recoverPassword
            r2.<init>()
            java.lang.String r3 = r11.emailCode
            r2.code = r3
            r2.new_settings = r1
            int r3 = r2.flags
            r3 = r3 | 1
            r2.flags = r3
            goto L_0x00c4
        L_0x00a8:
            org.telegram.tgnet.TLRPC$TL_account_updatePasswordSettings r2 = new org.telegram.tgnet.TLRPC$TL_account_updatePasswordSettings
            r2.<init>()
            byte[] r3 = r11.currentPasswordHash
            if (r3 == 0) goto L_0x00ba
            int r3 = r3.length
            if (r3 == 0) goto L_0x00ba
            if (r12 == 0) goto L_0x00c1
            boolean r3 = r11.waitingForEmail
            if (r3 == 0) goto L_0x00c1
        L_0x00ba:
            org.telegram.tgnet.TLRPC$TL_inputCheckPasswordEmpty r3 = new org.telegram.tgnet.TLRPC$TL_inputCheckPasswordEmpty
            r3.<init>()
            r2.password = r3
        L_0x00c1:
            r2.new_settings = r1
            r3 = r2
        L_0x00c4:
            r11.needShowProgress()
            org.telegram.messenger.DispatchQueue r9 = org.telegram.messenger.Utilities.globalQueue
            org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda24 r10 = new org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda24
            r3 = r10
            r4 = r11
            r5 = r2
            r6 = r12
            r7 = r0
            r8 = r1
            r3.<init>(r4, r5, r6, r7, r8)
            r9.postRunnable(r10)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TwoStepVerificationSetupActivity.setNewPassword(boolean):void");
    }

    /* renamed from: lambda$setNewPassword$44$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3429xebcd88c5(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda28(this, error));
    }

    /* renamed from: lambda$setNewPassword$43$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3428xfa23e2a6(TLRPC.TL_error error) {
        needHideProgress();
        if (error == null) {
            TwoStepVerificationActivity fragment = new TwoStepVerificationActivity();
            this.currentPassword.has_recovery = false;
            this.currentPassword.email_unconfirmed_pattern = "";
            fragment.setCurrentPasswordParams(this.currentPassword, this.currentPasswordHash, this.currentSecretId, this.currentSecret);
            fragment.setBlockingAlert(this.otherwiseReloginDays);
            presentFragment(fragment, true);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didRemoveTwoStepPassword, new Object[0]);
        }
    }

    /* renamed from: lambda$setNewPassword$50$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3435x68b20e0a(TLObject request, boolean clear, String password, TLRPC.TL_account_passwordInputSettings new_settings) {
        byte[] newPasswordHash;
        byte[] newPasswordBytes;
        byte[] bArr;
        TLObject tLObject = request;
        TLRPC.TL_account_passwordInputSettings tL_account_passwordInputSettings = new_settings;
        if (tLObject instanceof TLRPC.TL_account_updatePasswordSettings) {
            TLRPC.TL_account_updatePasswordSettings req = (TLRPC.TL_account_updatePasswordSettings) tLObject;
            if (req.password == null) {
                req.password = getNewSrpPassword();
            }
        }
        if (clear || password == null) {
            newPasswordBytes = null;
            newPasswordHash = null;
        } else {
            byte[] newPasswordBytes2 = AndroidUtilities.getStringBytes(password);
            if (this.currentPassword.new_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                newPasswordBytes = newPasswordBytes2;
                newPasswordHash = SRPHelper.getX(newPasswordBytes2, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.new_algo);
            } else {
                newPasswordBytes = newPasswordBytes2;
                newPasswordHash = null;
            }
        }
        RequestDelegate requestDelegate = new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda43(this, clear, newPasswordHash, password, new_settings);
        if (!clear) {
            if (password != null && (bArr = this.currentSecret) != null && bArr.length == 32 && (this.currentPassword.new_secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000)) {
                TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 newAlgo = (TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) this.currentPassword.new_secure_algo;
                byte[] passwordHash = Utilities.computePBKDF2(newPasswordBytes, newAlgo.salt);
                byte[] key = new byte[32];
                System.arraycopy(passwordHash, 0, key, 0, 32);
                byte[] iv = new byte[16];
                System.arraycopy(passwordHash, 32, iv, 0, 16);
                byte[] encryptedSecret = new byte[32];
                System.arraycopy(this.currentSecret, 0, encryptedSecret, 0, 32);
                byte[] bArr2 = iv;
                Utilities.aesCbcEncryptionByteArraySafe(encryptedSecret, key, iv, 0, encryptedSecret.length, 0, 1);
                tL_account_passwordInputSettings.new_secure_settings = new TLRPC.TL_secureSecretSettings();
                tL_account_passwordInputSettings.new_secure_settings.secure_algo = newAlgo;
                tL_account_passwordInputSettings.new_secure_settings.secure_secret = encryptedSecret;
                tL_account_passwordInputSettings.new_secure_settings.secure_secret_id = this.currentSecretId;
                tL_account_passwordInputSettings.flags |= 4;
            }
            if (this.currentPassword.new_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                if (password != null) {
                    tL_account_passwordInputSettings.new_password_hash = SRPHelper.getVBytes(newPasswordBytes, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.new_algo);
                    if (tL_account_passwordInputSettings.new_password_hash == null) {
                        TLRPC.TL_error error = new TLRPC.TL_error();
                        error.text = "ALGO_INVALID";
                        requestDelegate.run((TLObject) null, error);
                    }
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLObject, requestDelegate, 10);
                return;
            }
            TLRPC.TL_error error2 = new TLRPC.TL_error();
            error2.text = "PASSWORD_HASH_INVALID";
            requestDelegate.run((TLObject) null, error2);
            return;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLObject, requestDelegate, 10);
    }

    /* renamed from: lambda$setNewPassword$49$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3434xa41dCLASSNAME(boolean clear, byte[] newPasswordHash, String password, TLRPC.TL_account_passwordInputSettings new_settings, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda32(this, error, clear, response, newPasswordHash, password, new_settings));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: java.lang.Object[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* renamed from: lambda$setNewPassword$48$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m3433xb2742141(org.telegram.tgnet.TLRPC.TL_error r21, boolean r22, org.telegram.tgnet.TLObject r23, byte[] r24, java.lang.String r25, org.telegram.tgnet.TLRPC.TL_account_passwordInputSettings r26) {
        /*
            r20 = this;
            r0 = r20
            r1 = r21
            r2 = r22
            r3 = r23
            r4 = r24
            r5 = 8
            if (r1 == 0) goto L_0x002c
            java.lang.String r6 = r1.text
            java.lang.String r7 = "SRP_ID_INVALID"
            boolean r6 = r7.equals(r6)
            if (r6 == 0) goto L_0x002c
            org.telegram.tgnet.TLRPC$TL_account_getPassword r6 = new org.telegram.tgnet.TLRPC$TL_account_getPassword
            r6.<init>()
            int r7 = r0.currentAccount
            org.telegram.tgnet.ConnectionsManager r7 = org.telegram.tgnet.ConnectionsManager.getInstance(r7)
            org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda42 r8 = new org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda42
            r8.<init>(r0, r2)
            r7.sendRequest(r6, r8, r5)
            return
        L_0x002c:
            r20.needHideProgress()
            r6 = 7
            r7 = 0
            r8 = 1
            if (r1 != 0) goto L_0x0165
            boolean r9 = r3 instanceof org.telegram.tgnet.TLRPC.TL_boolTrue
            if (r9 != 0) goto L_0x003c
            boolean r9 = r3 instanceof org.telegram.tgnet.TLRPC.auth_Authorization
            if (r9 == 0) goto L_0x0165
        L_0x003c:
            org.telegram.messenger.MessagesController r5 = r20.getMessagesController()
            r9 = 0
            java.lang.String r11 = "VALIDATE_PASSWORD"
            r5.removeSuggestion(r9, r11)
            if (r2 == 0) goto L_0x0081
            r5 = 0
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r6 = r0.fragmentsToClose
            int r6 = r6.size()
        L_0x0050:
            if (r5 >= r6) goto L_0x0060
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r8 = r0.fragmentsToClose
            java.lang.Object r8 = r8.get(r5)
            org.telegram.ui.ActionBar.BaseFragment r8 = (org.telegram.ui.ActionBar.BaseFragment) r8
            r8.removeSelfFromStack()
            int r5 = r5 + 1
            goto L_0x0050
        L_0x0060:
            int r5 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getInstance(r5)
            int r6 = org.telegram.messenger.NotificationCenter.didRemoveTwoStepPassword
            java.lang.Object[] r8 = new java.lang.Object[r7]
            r5.postNotificationName(r6, r8)
            int r5 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getInstance(r5)
            int r6 = org.telegram.messenger.NotificationCenter.didSetOrRemoveTwoStepPassword
            java.lang.Object[] r7 = new java.lang.Object[r7]
            r5.postNotificationName(r6, r7)
            r20.finishFragment()
            r7 = r26
            goto L_0x027a
        L_0x0081:
            android.app.Activity r5 = r20.getParentActivity()
            if (r5 != 0) goto L_0x0088
            return
        L_0x0088:
            org.telegram.tgnet.TLRPC$TL_account_password r5 = r0.currentPassword
            boolean r5 = r5.has_password
            if (r5 == 0) goto L_0x00eb
            org.telegram.ui.ActionBar.AlertDialog$Builder r5 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r6 = r20.getParentActivity()
            r5.<init>((android.content.Context) r6)
            r6 = 2131626918(0x7f0e0ba6, float:1.8881086E38)
            java.lang.String r8 = "OK"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r6)
            org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda47 r8 = new org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda47
            r8.<init>(r0, r4)
            r5.setPositiveButton(r6, r8)
            if (r25 != 0) goto L_0x00bf
            org.telegram.tgnet.TLRPC$TL_account_password r6 = r0.currentPassword
            if (r6 == 0) goto L_0x00bf
            boolean r6 = r6.has_password
            if (r6 == 0) goto L_0x00bf
            r6 = 2131629054(0x7f0e13fe, float:1.8885418E38)
            java.lang.String r8 = "YourEmailSuccessText"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r6)
            r5.setMessage(r6)
            goto L_0x00cb
        L_0x00bf:
            r6 = 2131629060(0x7f0e1404, float:1.888543E38)
            java.lang.String r8 = "YourPasswordChangedSuccessText"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r6)
            r5.setMessage(r6)
        L_0x00cb:
            r6 = 2131629066(0x7f0e140a, float:1.8885442E38)
            java.lang.String r8 = "YourPasswordSuccess"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r6)
            r5.setTitle(r6)
            org.telegram.ui.ActionBar.AlertDialog r6 = r5.create()
            android.app.Dialog r6 = r0.showDialog(r6)
            if (r6 == 0) goto L_0x00e7
            r6.setCanceledOnTouchOutside(r7)
            r6.setCancelable(r7)
        L_0x00e7:
            r7 = r26
            goto L_0x027a
        L_0x00eb:
            r5 = 0
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r9 = r0.fragmentsToClose
            int r9 = r9.size()
        L_0x00f2:
            if (r5 >= r9) goto L_0x0102
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r10 = r0.fragmentsToClose
            java.lang.Object r10 = r10.get(r5)
            org.telegram.ui.ActionBar.BaseFragment r10 = (org.telegram.ui.ActionBar.BaseFragment) r10
            r10.removeSelfFromStack()
            int r5 = r5 + 1
            goto L_0x00f2
        L_0x0102:
            org.telegram.tgnet.TLRPC$TL_account_password r5 = r0.currentPassword
            r5.has_password = r8
            org.telegram.tgnet.TLRPC$TL_account_password r5 = r0.currentPassword
            boolean r5 = r5.has_recovery
            if (r5 != 0) goto L_0x0117
            org.telegram.tgnet.TLRPC$TL_account_password r5 = r0.currentPassword
            java.lang.String r9 = r5.email_unconfirmed_pattern
            boolean r9 = android.text.TextUtils.isEmpty(r9)
            r9 = r9 ^ r8
            r5.has_recovery = r9
        L_0x0117:
            boolean r5 = r0.closeAfterSet
            if (r5 == 0) goto L_0x0128
            int r5 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getInstance(r5)
            int r9 = org.telegram.messenger.NotificationCenter.twoStepPasswordChanged
            java.lang.Object[] r10 = new java.lang.Object[r7]
            r5.postNotificationName(r9, r10)
        L_0x0128:
            org.telegram.ui.TwoStepVerificationSetupActivity r5 = new org.telegram.ui.TwoStepVerificationSetupActivity
            org.telegram.tgnet.TLRPC$TL_account_password r9 = r0.currentPassword
            r5.<init>(r6, r9)
            boolean r6 = r0.fromRegistration
            r5.fromRegistration = r6
            if (r4 == 0) goto L_0x0137
            r11 = r4
            goto L_0x013a
        L_0x0137:
            byte[] r6 = r0.currentPasswordHash
            r11 = r6
        L_0x013a:
            long r12 = r0.currentSecretId
            byte[] r14 = r0.currentSecret
            boolean r15 = r0.emailOnly
            r10 = r5
            r10.setCurrentPasswordParams(r11, r12, r14, r15)
            boolean r6 = r0.closeAfterSet
            r5.closeAfterSet = r6
            int r6 = r0.otherwiseReloginDays
            r5.setBlockingAlert(r6)
            r0.presentFragment(r5, r8)
            int r6 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r6 = org.telegram.messenger.NotificationCenter.getInstance(r6)
            int r9 = org.telegram.messenger.NotificationCenter.didSetOrRemoveTwoStepPassword
            java.lang.Object[] r8 = new java.lang.Object[r8]
            org.telegram.tgnet.TLRPC$TL_account_password r10 = r0.currentPassword
            r8[r7] = r10
            r6.postNotificationName(r9, r8)
            r7 = r26
            goto L_0x027a
        L_0x0165:
            if (r1 == 0) goto L_0x0278
            java.lang.String r9 = r1.text
            java.lang.String r10 = "EMAIL_UNCONFIRMED"
            boolean r9 = r10.equals(r9)
            if (r9 != 0) goto L_0x01eb
            java.lang.String r9 = r1.text
            java.lang.String r10 = "EMAIL_UNCONFIRMED_"
            boolean r9 = r9.startsWith(r10)
            if (r9 == 0) goto L_0x017c
            goto L_0x01eb
        L_0x017c:
            java.lang.String r5 = r1.text
            java.lang.String r6 = "EMAIL_INVALID"
            boolean r5 = r6.equals(r5)
            r6 = 2131624316(0x7f0e017c, float:1.8875808E38)
            java.lang.String r9 = "AppName"
            if (r5 == 0) goto L_0x019f
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r6 = 2131627198(0x7f0e0cbe, float:1.8881654E38)
            java.lang.String r7 = "PasswordEmailInvalid"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r0.showAlertWithText(r5, r6)
            r7 = r26
            goto L_0x027a
        L_0x019f:
            java.lang.String r5 = r1.text
            java.lang.String r10 = "FLOOD_WAIT"
            boolean r5 = r5.startsWith(r10)
            if (r5 == 0) goto L_0x01de
            java.lang.String r5 = r1.text
            java.lang.Integer r5 = org.telegram.messenger.Utilities.parseInt(r5)
            int r5 = r5.intValue()
            r10 = 60
            if (r5 >= r10) goto L_0x01be
            java.lang.String r10 = "Seconds"
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatPluralString(r10, r5)
            goto L_0x01c6
        L_0x01be:
            int r10 = r5 / 60
            java.lang.String r11 = "Minutes"
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatPluralString(r11, r10)
        L_0x01c6:
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r9 = 2131625810(0x7f0e0752, float:1.8878838E38)
            java.lang.Object[] r8 = new java.lang.Object[r8]
            r8[r7] = r10
            java.lang.String r7 = "FloodWaitTime"
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r9, r8)
            r0.showAlertWithText(r6, r7)
            r7 = r26
            goto L_0x027a
        L_0x01de:
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r6)
            java.lang.String r6 = r1.text
            r0.showAlertWithText(r5, r6)
            r7 = r26
            goto L_0x027a
        L_0x01eb:
            int r9 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getInstance(r9)
            int r10 = org.telegram.messenger.NotificationCenter.twoStepPasswordChanged
            java.lang.Object[] r11 = new java.lang.Object[r7]
            r9.postNotificationName(r10, r11)
            r9 = 0
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r10 = r0.fragmentsToClose
            int r10 = r10.size()
        L_0x01ff:
            if (r9 >= r10) goto L_0x020f
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r11 = r0.fragmentsToClose
            java.lang.Object r11 = r11.get(r9)
            org.telegram.ui.ActionBar.BaseFragment r11 = (org.telegram.ui.ActionBar.BaseFragment) r11
            r11.removeSelfFromStack()
            int r9 = r9 + 1
            goto L_0x01ff
        L_0x020f:
            int r9 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getInstance(r9)
            int r10 = org.telegram.messenger.NotificationCenter.twoStepPasswordChanged
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r5[r7] = r4
            r7 = r26
            org.telegram.tgnet.TLRPC$PasswordKdfAlgo r11 = r7.new_algo
            r5[r8] = r11
            r11 = 2
            org.telegram.tgnet.TLRPC$TL_account_password r12 = r0.currentPassword
            org.telegram.tgnet.TLRPC$SecurePasswordKdfAlgo r12 = r12.new_secure_algo
            r5[r11] = r12
            r11 = 3
            org.telegram.tgnet.TLRPC$TL_account_password r12 = r0.currentPassword
            byte[] r12 = r12.secure_random
            r5[r11] = r12
            r11 = 4
            java.lang.String r12 = r0.email
            r5[r11] = r12
            java.lang.String r11 = r0.hint
            r13 = 5
            r5[r13] = r11
            r11 = 6
            r5[r11] = r12
            java.lang.String r11 = r0.firstPassword
            r5[r6] = r11
            r9.postNotificationName(r10, r5)
            org.telegram.tgnet.TLRPC$TL_account_password r5 = r0.currentPassword
            java.lang.String r6 = r0.email
            r5.email_unconfirmed_pattern = r6
            org.telegram.ui.TwoStepVerificationSetupActivity r5 = new org.telegram.ui.TwoStepVerificationSetupActivity
            org.telegram.tgnet.TLRPC$TL_account_password r6 = r0.currentPassword
            r5.<init>(r13, r6)
            boolean r6 = r0.fromRegistration
            r5.fromRegistration = r6
            if (r4 == 0) goto L_0x0258
            r15 = r4
            goto L_0x025b
        L_0x0258:
            byte[] r6 = r0.currentPasswordHash
            r15 = r6
        L_0x025b:
            long r9 = r0.currentSecretId
            byte[] r6 = r0.currentSecret
            boolean r11 = r0.emailOnly
            r14 = r5
            r16 = r9
            r18 = r6
            r19 = r11
            r14.setCurrentPasswordParams(r15, r16, r18, r19)
            boolean r6 = r0.closeAfterSet
            r5.closeAfterSet = r6
            int r6 = r0.otherwiseReloginDays
            r5.setBlockingAlert(r6)
            r0.presentFragment(r5, r8)
            goto L_0x027a
        L_0x0278:
            r7 = r26
        L_0x027a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TwoStepVerificationSetupActivity.m3433xb2742141(org.telegram.tgnet.TLRPC$TL_error, boolean, org.telegram.tgnet.TLObject, byte[], java.lang.String, org.telegram.tgnet.TLRPC$TL_account_passwordInputSettings):void");
    }

    /* renamed from: lambda$setNewPassword$46$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3431xcvar_d503(boolean clear, TLObject response2, TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda31(this, error2, response2, clear));
    }

    /* renamed from: lambda$setNewPassword$45$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3430xdd772ee4(TLRPC.TL_error error2, TLObject response2, boolean clear) {
        if (error2 == null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response2;
            this.currentPassword = tL_account_password;
            TwoStepVerificationActivity.initPasswordNewAlgo(tL_account_password);
            setNewPassword(clear);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
        }
    }

    /* renamed from: lambda$setNewPassword$47$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3432xc0ca7b22(byte[] newPasswordHash, DialogInterface dialogInterface, int i) {
        int N = this.fragmentsToClose.size();
        for (int a = 0; a < N; a++) {
            this.fragmentsToClose.get(a).removeSelfFromStack();
        }
        TwoStepVerificationActivity fragment = new TwoStepVerificationActivity();
        this.currentPassword.has_password = true;
        if (!this.currentPassword.has_recovery) {
            TLRPC.TL_account_password tL_account_password = this.currentPassword;
            tL_account_password.has_recovery = !TextUtils.isEmpty(tL_account_password.email_unconfirmed_pattern);
        }
        fragment.setCurrentPasswordParams(this.currentPassword, newPasswordHash != null ? newPasswordHash : this.currentPasswordHash, this.currentSecretId, this.currentSecret);
        fragment.setBlockingAlert(this.otherwiseReloginDays);
        presentFragment(fragment, true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
    }

    /* access modifiers changed from: protected */
    public TLRPC.TL_inputCheckPasswordSRP getNewSrpPassword() {
        if (!(this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow)) {
            return null;
        }
        return SRPHelper.startCheck(this.currentPasswordHash, this.currentPassword.srp_id, this.currentPassword.srp_B, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.current_algo);
    }

    /* access modifiers changed from: protected */
    public void onReset() {
    }

    private void onFieldError(View shakeView, TextView field, boolean clear) {
        if (getParentActivity() != null) {
            try {
                field.performHapticFeedback(3, 2);
            } catch (Exception e) {
            }
            if (clear) {
                field.setText("");
            }
            AndroidUtilities.shakeViewSpring(shakeView, 5.0f);
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
        themeDescriptions.add(new ThemeDescription(this.editTextFirstRow, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.editTextFirstRow, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        themeDescriptions.add(new ThemeDescription(this.editTextFirstRow, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        themeDescriptions.add(new ThemeDescription(this.editTextFirstRow, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        return themeDescriptions;
    }

    public boolean isSwipeBackEnabled(MotionEvent event) {
        if (this.otherwiseReloginDays < 0 || this.parentLayout.fragmentsStack.size() != 1) {
            return super.isSwipeBackEnabled(event);
        }
        return false;
    }

    public boolean onBackPressed() {
        if (this.otherwiseReloginDays < 0 || this.parentLayout.fragmentsStack.size() != 1) {
            finishFragment();
            return true;
        }
        showSetForcePasswordAlert();
        return false;
    }

    public void finishFragment(boolean animated) {
        Iterator<BaseFragment> it = getParentLayout().fragmentsStack.iterator();
        while (it.hasNext()) {
            BaseFragment fragment = it.next();
            if (fragment != this && (fragment instanceof TwoStepVerificationSetupActivity)) {
                ((TwoStepVerificationSetupActivity) fragment).floatingAutoAnimator.ignoreNextLayout();
            }
        }
        super.finishFragment(animated);
    }

    /* access modifiers changed from: private */
    public void showSetForcePasswordAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("Warning", NUM));
        builder.setMessage(LocaleController.formatPluralString("ForceSetPasswordAlertMessageShort", this.otherwiseReloginDays));
        builder.setPositiveButton(LocaleController.getString("TwoStepVerificationSetPassword", NUM), (DialogInterface.OnClickListener) null);
        builder.setNegativeButton(LocaleController.getString("ForceSetPasswordCancel", NUM), new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda44(this));
        ((TextView) builder.show().getButton(-2)).setTextColor(Theme.getColor("dialogTextRed2"));
    }

    /* renamed from: lambda$showSetForcePasswordAlert$51$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m3437x6978abd1(DialogInterface a1, int a2) {
        finishFragment();
    }

    public void setBlockingAlert(int otherwiseRelogin) {
        this.otherwiseReloginDays = otherwiseRelogin;
    }

    public void finishFragment() {
        if (this.otherwiseReloginDays < 0 || this.parentLayout.fragmentsStack.size() != 1) {
            super.finishFragment();
            return;
        }
        Bundle args = new Bundle();
        args.putBoolean("afterSignup", true);
        presentFragment(new DialogsActivity(args), true);
    }

    public boolean isLightStatusBar() {
        if (ColorUtils.calculateLuminance(Theme.getColor("windowBackgroundWhite", (boolean[]) null, true)) > 0.699999988079071d) {
            return true;
        }
        return false;
    }
}

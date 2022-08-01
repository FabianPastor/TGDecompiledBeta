package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
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
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$PasswordKdfAlgo;
import org.telegram.tgnet.TLRPC$SecurePasswordKdfAlgo;
import org.telegram.tgnet.TLRPC$TL_account_getPassword;
import org.telegram.tgnet.TLRPC$TL_account_getPasswordSettings;
import org.telegram.tgnet.TLRPC$TL_account_password;
import org.telegram.tgnet.TLRPC$TL_account_passwordInputSettings;
import org.telegram.tgnet.TLRPC$TL_account_resendPasswordEmail;
import org.telegram.tgnet.TLRPC$TL_account_updatePasswordSettings;
import org.telegram.tgnet.TLRPC$TL_auth_recoverPassword;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputCheckPasswordSRP;
import org.telegram.tgnet.TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow;
import org.telegram.tgnet.TLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000;
import org.telegram.tgnet.TLRPC$TL_secureSecretSettings;
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
    public TLRPC$TL_account_password currentPassword;
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
    private boolean emailOnly;
    /* access modifiers changed from: private */
    public Runnable errorColorTimeout = new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda26(this);
    private Runnable finishCallback = new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda21(this);
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
    /* access modifiers changed from: private */
    public CustomPhoneKeyboardView keyboardView;
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

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createView$10(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createView$19(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public boolean hasForceLightStatusBar() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onReset() {
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        int i = 0;
        this.postedErrorColorTimeout = false;
        while (true) {
            CodeNumberField[] codeNumberFieldArr = this.codeFieldContainer.codeField;
            if (i < codeNumberFieldArr.length) {
                codeNumberFieldArr[i].animateErrorProgress(0.0f);
                i++;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
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

    public TwoStepVerificationSetupActivity(int i, TLRPC$TL_account_password tLRPC$TL_account_password) {
        this.currentType = i;
        this.currentPassword = tLRPC$TL_account_password;
        if (tLRPC$TL_account_password == null && (i == 6 || i == 8)) {
            loadPasswordInfo();
        } else {
            this.waitingForEmail = !TextUtils.isEmpty(tLRPC$TL_account_password.email_unconfirmed_pattern);
        }
    }

    public TwoStepVerificationSetupActivity(int i, int i2, TLRPC$TL_account_password tLRPC$TL_account_password) {
        this.currentAccount = i;
        this.currentType = i2;
        this.currentPassword = tLRPC$TL_account_password;
        this.waitingForEmail = !TextUtils.isEmpty(tLRPC$TL_account_password.email_unconfirmed_pattern);
        if (this.currentPassword == null) {
            int i3 = this.currentType;
            if (i3 == 6 || i3 == 8) {
                loadPasswordInfo();
            }
        }
    }

    public void setCurrentPasswordParams(byte[] bArr, long j, byte[] bArr2, boolean z) {
        this.currentPasswordHash = bArr;
        this.currentSecret = bArr2;
        this.currentSecretId = j;
        this.emailOnly = z;
    }

    public void setCurrentEmailCode(String str) {
        this.emailCode = str;
    }

    public void addFragmentToClose(BaseFragment baseFragment) {
        this.fragmentsToClose.add(baseFragment);
    }

    public void setFromRegistration(boolean z) {
        this.fromRegistration = z;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        int i = 0;
        this.doneAfterPasswordLoad = false;
        Runnable runnable = this.setAnimationRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.setAnimationRunnable = null;
        }
        if (this.animationDrawables != null) {
            while (true) {
                RLottieDrawable[] rLottieDrawableArr = this.animationDrawables;
                if (i >= rLottieDrawableArr.length) {
                    break;
                }
                rLottieDrawableArr[i].recycle();
                i++;
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
            public void onItemClick(int i) {
                String str;
                if (i == -1) {
                    if (TwoStepVerificationSetupActivity.this.otherwiseReloginDays < 0 || TwoStepVerificationSetupActivity.this.parentLayout.fragmentsStack.size() != 1) {
                        TwoStepVerificationSetupActivity.this.finishFragment();
                    } else {
                        TwoStepVerificationSetupActivity.this.showSetForcePasswordAlert();
                    }
                } else if (i == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) TwoStepVerificationSetupActivity.this.getParentActivity());
                    if (TwoStepVerificationSetupActivity.this.currentPassword == null || !TwoStepVerificationSetupActivity.this.currentPassword.has_password) {
                        str = LocaleController.getString("CancelPasswordQuestion", NUM);
                    } else {
                        str = LocaleController.getString("CancelEmailQuestion", NUM);
                    }
                    String string = LocaleController.getString("CancelEmailQuestionTitle", NUM);
                    String string2 = LocaleController.getString("Abort", NUM);
                    builder.setMessage(str);
                    builder.setTitle(string);
                    builder.setPositiveButton(string2, new TwoStepVerificationSetupActivity$1$$ExternalSyntheticLambda0(this));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    AlertDialog create = builder.create();
                    TwoStepVerificationSetupActivity.this.showDialog(create);
                    TextView textView = (TextView) create.getButton(-1);
                    if (textView != null) {
                        textView.setTextColor(Theme.getColor("dialogTextRed2"));
                    }
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onItemClick$0(DialogInterface dialogInterface, int i) {
                TwoStepVerificationSetupActivity.this.setNewPassword(true);
            }
        });
        if (this.currentType == 5) {
            this.actionBar.createMenu().addItem(0, NUM).addSubItem(1, LocaleController.getString("AbortPasswordMenu", NUM));
        }
        this.floatingButtonContainer = new FrameLayout(context2);
        int i = Build.VERSION.SDK_INT;
        if (i >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButtonIcon, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButtonIcon, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.floatingButtonContainer.setStateListAnimator(stateListAnimator);
            this.floatingButtonContainer.setOutlineProvider(new ViewOutlineProvider(this) {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        this.floatingAutoAnimator = VerticalPositionAutoAnimator.attach(this.floatingButtonContainer);
        this.floatingButtonContainer.setOnClickListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda8(this));
        TransformableLoginButtonView transformableLoginButtonView = new TransformableLoginButtonView(context2);
        this.floatingButtonIcon = transformableLoginButtonView;
        transformableLoginButtonView.setTransformType(1);
        this.floatingButtonIcon.setProgress(0.0f);
        this.floatingButtonIcon.setColor(Theme.getColor("chats_actionIcon"));
        this.floatingButtonIcon.setDrawBackground(false);
        this.floatingButtonContainer.setContentDescription(LocaleController.getString(NUM));
        this.floatingButtonContainer.addView(this.floatingButtonIcon, LayoutHelper.createFrame(i >= 21 ? 56 : 60, i >= 21 ? 56.0f : 60.0f));
        RadialProgressView radialProgressView2 = new RadialProgressView(context2);
        this.floatingProgressView = radialProgressView2;
        radialProgressView2.setSize(AndroidUtilities.dp(22.0f));
        this.floatingProgressView.setAlpha(0.0f);
        this.floatingProgressView.setScaleX(0.1f);
        this.floatingProgressView.setScaleY(0.1f);
        this.floatingButtonContainer.addView(this.floatingProgressView, LayoutHelper.createFrame(-1, -1.0f));
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
        if (i < 21) {
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        this.floatingButtonContainer.setBackground(createSimpleSelectorCircleDrawable);
        TextView textView = new TextView(context2);
        this.bottomSkipButton = textView;
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText2"));
        this.bottomSkipButton.setTextSize(1, 14.0f);
        this.bottomSkipButton.setGravity(19);
        this.bottomSkipButton.setVisibility(8);
        VerticalPositionAutoAnimator.attach(this.bottomSkipButton);
        this.bottomSkipButton.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.bottomSkipButton.setOnClickListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda11(this));
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
        this.descriptionText2.setOnClickListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda10(this));
        TextView textView4 = new TextView(context2);
        this.buttonTextView = textView4;
        textView4.setMinWidth(AndroidUtilities.dp(220.0f));
        this.buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.buttonTextView.setTextSize(1, 15.0f);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.buttonTextView.setBackground(Theme.AdaptiveRipple.filledRect("featuredStickers_addButton", 6.0f));
        this.buttonTextView.setOnClickListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda7(this));
        int i2 = this.currentType;
        if (i2 == 6 || i2 == 7 || i2 == 9) {
            this.titleTextView.setTypeface(Typeface.DEFAULT);
            this.titleTextView.setTextSize(1, 24.0f);
        } else {
            this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.titleTextView.setTextSize(1, 18.0f);
        }
        switch (this.currentType) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 8:
                final AnonymousClass4 r3 = new FrameLayout(context2) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, i2);
                        ((ViewGroup.MarginLayoutParams) TwoStepVerificationSetupActivity.this.radialProgressView.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight + AndroidUtilities.dp(16.0f);
                    }
                };
                final AnonymousClass5 r12 = new SizeNotifierFrameLayout(context2) {
                    /* access modifiers changed from: protected */
                    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                        int i5;
                        if (TwoStepVerificationSetupActivity.this.keyboardView.getVisibility() == 8 || measureKeyboardHeight() < AndroidUtilities.dp(20.0f)) {
                            if (TwoStepVerificationSetupActivity.this.keyboardView.getVisibility() != 8) {
                                FrameLayout frameLayout = r3;
                                int measuredWidth = getMeasuredWidth();
                                i5 = getMeasuredHeight() - AndroidUtilities.dp(230.0f);
                                frameLayout.layout(0, 0, measuredWidth, i5);
                            } else {
                                FrameLayout frameLayout2 = r3;
                                int measuredWidth2 = getMeasuredWidth();
                                i5 = getMeasuredHeight();
                                frameLayout2.layout(0, 0, measuredWidth2, i5);
                            }
                        } else if (TwoStepVerificationSetupActivity.this.isCustomKeyboardVisible()) {
                            FrameLayout frameLayout3 = r3;
                            int measuredWidth3 = getMeasuredWidth();
                            i5 = (getMeasuredHeight() - AndroidUtilities.dp(230.0f)) + measureKeyboardHeight();
                            frameLayout3.layout(0, 0, measuredWidth3, i5);
                        } else {
                            FrameLayout frameLayout4 = r3;
                            int measuredWidth4 = getMeasuredWidth();
                            i5 = getMeasuredHeight();
                            frameLayout4.layout(0, 0, measuredWidth4, i5);
                        }
                        TwoStepVerificationSetupActivity.this.keyboardView.layout(0, i5, getMeasuredWidth(), AndroidUtilities.dp(230.0f) + i5);
                    }

                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        int size = View.MeasureSpec.getSize(i);
                        int size2 = View.MeasureSpec.getSize(i2);
                        setMeasuredDimension(size, size2);
                        if (TwoStepVerificationSetupActivity.this.keyboardView.getVisibility() != 8 && measureKeyboardHeight() < AndroidUtilities.dp(20.0f)) {
                            size2 -= AndroidUtilities.dp(230.0f);
                        }
                        r3.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, NUM));
                        TwoStepVerificationSetupActivity.this.keyboardView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(230.0f), NUM));
                    }
                };
                r12.addView(r3);
                AnonymousClass6 r7 = new ViewGroup(context2) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        int size = View.MeasureSpec.getSize(i);
                        int size2 = View.MeasureSpec.getSize(i2);
                        TwoStepVerificationSetupActivity.this.actionBar.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), i2);
                        TwoStepVerificationSetupActivity.this.actionBarBackground.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(TwoStepVerificationSetupActivity.this.actionBar.getMeasuredHeight() + AndroidUtilities.dp(3.0f), NUM));
                        r12.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), i2);
                        setMeasuredDimension(size, size2);
                    }

                    /* access modifiers changed from: protected */
                    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                        TwoStepVerificationSetupActivity.this.actionBar.layout(0, 0, TwoStepVerificationSetupActivity.this.actionBar.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.actionBar.getMeasuredHeight());
                        TwoStepVerificationSetupActivity.this.actionBarBackground.layout(0, 0, TwoStepVerificationSetupActivity.this.actionBarBackground.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.actionBarBackground.getMeasuredHeight());
                        SizeNotifierFrameLayout sizeNotifierFrameLayout = r12;
                        sizeNotifierFrameLayout.layout(0, 0, sizeNotifierFrameLayout.getMeasuredWidth(), r12.getMeasuredHeight());
                    }
                };
                AnonymousClass7 r9 = new ScrollView(context2) {
                    private boolean isLayoutDirty = true;
                    private int[] location = new int[2];
                    private int scrollingUp;
                    private Rect tempRect = new Rect();

                    /* access modifiers changed from: protected */
                    public void onScrollChanged(int i, int i2, int i3, int i4) {
                        super.onScrollChanged(i, i2, i3, i4);
                        if (TwoStepVerificationSetupActivity.this.titleTextView != null) {
                            TwoStepVerificationSetupActivity.this.titleTextView.getLocationOnScreen(this.location);
                            boolean z = this.location[1] + TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredHeight() < TwoStepVerificationSetupActivity.this.actionBar.getBottom();
                            if (z != (TwoStepVerificationSetupActivity.this.titleTextView.getTag() == null)) {
                                TwoStepVerificationSetupActivity.this.titleTextView.setTag(z ? null : 1);
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
                                fArr[0] = z ? 1.0f : 0.0f;
                                animatorArr[0] = ObjectAnimator.ofFloat(access$1900, property, fArr);
                                SimpleTextView titleTextView = TwoStepVerificationSetupActivity.this.actionBar.getTitleTextView();
                                Property property2 = View.ALPHA;
                                float[] fArr2 = new float[1];
                                if (!z) {
                                    f = 0.0f;
                                }
                                fArr2[0] = f;
                                animatorArr[1] = ObjectAnimator.ofFloat(titleTextView, property2, fArr2);
                                access$2400.playTogether(animatorArr);
                                TwoStepVerificationSetupActivity.this.actionBarAnimator.setDuration(150);
                                TwoStepVerificationSetupActivity.this.actionBarAnimator.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animator) {
                                        if (animator.equals(TwoStepVerificationSetupActivity.this.actionBarAnimator)) {
                                            AnimatorSet unused = TwoStepVerificationSetupActivity.this.actionBarAnimator = null;
                                        }
                                    }
                                });
                                TwoStepVerificationSetupActivity.this.actionBarAnimator.start();
                            }
                        }
                    }

                    public void scrollToDescendant(View view) {
                        view.getDrawingRect(this.tempRect);
                        offsetDescendantRectToMyCoords(view, this.tempRect);
                        this.tempRect.bottom += AndroidUtilities.dp(120.0f);
                        int computeScrollDeltaToGetChildRectOnScreen = computeScrollDeltaToGetChildRectOnScreen(this.tempRect);
                        if (computeScrollDeltaToGetChildRectOnScreen < 0) {
                            int measuredHeight = (getMeasuredHeight() - view.getMeasuredHeight()) / 2;
                            this.scrollingUp = measuredHeight;
                            computeScrollDeltaToGetChildRectOnScreen -= measuredHeight;
                        } else {
                            this.scrollingUp = 0;
                        }
                        if (computeScrollDeltaToGetChildRectOnScreen != 0) {
                            smoothScrollBy(0, computeScrollDeltaToGetChildRectOnScreen);
                        }
                    }

                    public void requestChildFocus(View view, View view2) {
                        if (Build.VERSION.SDK_INT < 29 && view2 != null && !this.isLayoutDirty) {
                            scrollToDescendant(view2);
                        }
                        super.requestChildFocus(view, view2);
                    }

                    public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
                        if (Build.VERSION.SDK_INT < 23) {
                            int dp = rect.bottom + AndroidUtilities.dp(120.0f);
                            rect.bottom = dp;
                            int i = this.scrollingUp;
                            if (i != 0) {
                                rect.top -= i;
                                rect.bottom = dp - i;
                                this.scrollingUp = 0;
                            }
                        }
                        return super.requestChildRectangleOnScreen(view, rect, z);
                    }

                    public void requestLayout() {
                        this.isLayoutDirty = true;
                        super.requestLayout();
                    }

                    /* access modifiers changed from: protected */
                    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                        this.isLayoutDirty = false;
                        super.onLayout(z, i, i2, i3, i4);
                    }
                };
                this.scrollView = r9;
                r9.setVerticalScrollBarEnabled(false);
                r3.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f));
                r3.addView(this.bottomSkipButton, LayoutHelper.createFrame(-1, i >= 21 ? 56.0f : 60.0f, 80, 0.0f, 0.0f, 0.0f, 16.0f));
                r3.addView(this.floatingButtonContainer, LayoutHelper.createFrame(i >= 21 ? 56 : 60, i >= 21 ? 56.0f : 60.0f, 85, 0.0f, 0.0f, 24.0f, 16.0f));
                r7.addView(r12, LayoutHelper.createFrame(-1, -1.0f));
                AnonymousClass8 r92 = new LinearLayout(context2) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, i2);
                        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) TwoStepVerificationSetupActivity.this.titleTextView.getLayoutParams();
                        int i3 = 0;
                        int dp = ((TwoStepVerificationSetupActivity.this.imageView.getVisibility() != 8 || Build.VERSION.SDK_INT < 21) ? 0 : AndroidUtilities.statusBarHeight) + AndroidUtilities.dp(8.0f);
                        if (TwoStepVerificationSetupActivity.this.currentType == 2 && AndroidUtilities.isSmallScreen() && !TwoStepVerificationSetupActivity.this.isLandscape()) {
                            i3 = AndroidUtilities.dp(32.0f);
                        }
                        marginLayoutParams.topMargin = dp + i3;
                    }
                };
                r92.setOrientation(1);
                this.scrollView.addView(r92, LayoutHelper.createScroll(-1, -1, 51));
                r92.addView(this.imageView, LayoutHelper.createLinear(-2, -2, 49, 0, 69, 0, 0));
                r92.addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 8, 0, 0));
                r92.addView(this.descriptionText, LayoutHelper.createLinear(-2, -2, 49, 0, 9, 0, 0));
                OutlineTextContainerView outlineTextContainerView = new OutlineTextContainerView(context2);
                this.outlineTextFirstRow = outlineTextContainerView;
                outlineTextContainerView.animateSelection(1.0f, false);
                EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
                this.editTextFirstRow = editTextBoldCursor;
                editTextBoldCursor.setTextSize(1, 18.0f);
                int dp = AndroidUtilities.dp(16.0f);
                this.editTextFirstRow.setPadding(dp, dp, dp, dp);
                this.editTextFirstRow.setCursorColor(Theme.getColor("windowBackgroundWhiteInputFieldActivated"));
                this.editTextFirstRow.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                this.editTextFirstRow.setBackground((Drawable) null);
                this.editTextFirstRow.setMaxLines(1);
                this.editTextFirstRow.setLines(1);
                this.editTextFirstRow.setGravity(3);
                this.editTextFirstRow.setCursorSize(AndroidUtilities.dp(20.0f));
                this.editTextFirstRow.setSingleLine(true);
                this.editTextFirstRow.setCursorWidth(1.5f);
                this.editTextFirstRow.setOnEditorActionListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda17(this));
                this.outlineTextFirstRow.attachEditText(this.editTextFirstRow);
                this.editTextFirstRow.setOnFocusChangeListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda13(this));
                LinearLayout linearLayout = new LinearLayout(context2);
                linearLayout.setOrientation(0);
                linearLayout.addView(this.editTextFirstRow, LayoutHelper.createLinear(0, -2, 1.0f));
                AnonymousClass9 r10 = new ImageView(context2) {
                    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                        boolean z = true;
                        accessibilityNodeInfo.setCheckable(true);
                        if (TwoStepVerificationSetupActivity.this.editTextFirstRow.getTransformationMethod() != null) {
                            z = false;
                        }
                        accessibilityNodeInfo.setChecked(z);
                    }
                };
                this.showPasswordButton = r10;
                r10.setImageResource(NUM);
                this.showPasswordButton.setScaleType(ImageView.ScaleType.CENTER);
                this.showPasswordButton.setContentDescription(LocaleController.getString(NUM));
                if (i >= 21) {
                    this.showPasswordButton.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
                }
                this.showPasswordButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
                AndroidUtilities.updateViewVisibilityAnimated(this.showPasswordButton, false, 0.1f, false);
                this.showPasswordButton.setOnClickListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda9(this));
                linearLayout.addView(this.showPasswordButton, LayoutHelper.createLinear(24, 24, 16, 0, 0, 16, 0));
                this.editTextFirstRow.addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void afterTextChanged(Editable editable) {
                        if (!TwoStepVerificationSetupActivity.this.needPasswordButton) {
                            return;
                        }
                        if (TwoStepVerificationSetupActivity.this.showPasswordButton.getVisibility() != 0 && !TextUtils.isEmpty(editable)) {
                            AndroidUtilities.updateViewVisibilityAnimated(TwoStepVerificationSetupActivity.this.showPasswordButton, true, 0.1f, true);
                        } else if (TwoStepVerificationSetupActivity.this.showPasswordButton.getVisibility() != 8 && TextUtils.isEmpty(editable)) {
                            AndroidUtilities.updateViewVisibilityAnimated(TwoStepVerificationSetupActivity.this.showPasswordButton, false, 0.1f, true);
                        }
                    }
                });
                this.outlineTextFirstRow.addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f));
                r92.addView(this.outlineTextFirstRow, LayoutHelper.createFrame(-1, -2.0f, 49, 24.0f, 32.0f, 24.0f, 32.0f));
                this.outlineTextSecondRow = new OutlineTextContainerView(context2);
                EditTextBoldCursor editTextBoldCursor2 = new EditTextBoldCursor(context2);
                this.editTextSecondRow = editTextBoldCursor2;
                editTextBoldCursor2.setTextSize(1, 18.0f);
                int dp2 = AndroidUtilities.dp(16.0f);
                this.editTextSecondRow.setPadding(dp2, dp2, dp2, dp2);
                this.editTextSecondRow.setCursorColor(Theme.getColor("windowBackgroundWhiteInputFieldActivated"));
                this.editTextSecondRow.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                this.editTextSecondRow.setBackground((Drawable) null);
                this.editTextSecondRow.setMaxLines(1);
                this.editTextSecondRow.setLines(1);
                this.editTextSecondRow.setGravity(3);
                this.editTextSecondRow.setCursorSize(AndroidUtilities.dp(20.0f));
                this.editTextSecondRow.setSingleLine(true);
                this.editTextSecondRow.setCursorWidth(1.5f);
                this.editTextSecondRow.setOnEditorActionListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda18(this));
                this.outlineTextSecondRow.attachEditText(this.editTextSecondRow);
                this.editTextSecondRow.setOnFocusChangeListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda14(this));
                this.outlineTextSecondRow.addView(this.editTextSecondRow, LayoutHelper.createFrame(-1, -2.0f));
                r92.addView(this.outlineTextSecondRow, LayoutHelper.createFrame(-1, -2.0f, 49, 24.0f, 16.0f, 24.0f, 0.0f));
                this.outlineTextSecondRow.setVisibility(8);
                CustomPhoneKeyboardView customPhoneKeyboardView = new CustomPhoneKeyboardView(context2);
                this.keyboardView = customPhoneKeyboardView;
                customPhoneKeyboardView.setVisibility(8);
                r12.addView(this.keyboardView);
                AnonymousClass11 r2 = new CodeFieldContainer(context2) {
                    /* access modifiers changed from: protected */
                    public void processNextPressed() {
                        TwoStepVerificationSetupActivity.this.processNext();
                    }
                };
                this.codeFieldContainer = r2;
                r2.setNumbersCount(6, 1);
                for (CodeNumberField codeNumberField : this.codeFieldContainer.codeField) {
                    codeNumberField.setShowSoftInputOnFocusCompat(!isCustomKeyboardVisible());
                    codeNumberField.addTextChangedListener(new TextWatcher() {
                        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        }

                        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        }

                        public void afterTextChanged(Editable editable) {
                            if (TwoStepVerificationSetupActivity.this.postedErrorColorTimeout) {
                                AndroidUtilities.cancelRunOnUIThread(TwoStepVerificationSetupActivity.this.errorColorTimeout);
                                TwoStepVerificationSetupActivity.this.errorColorTimeout.run();
                            }
                        }
                    });
                    codeNumberField.setOnFocusChangeListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda15(this));
                }
                this.codeFieldContainer.setVisibility(8);
                r92.addView(this.codeFieldContainer, LayoutHelper.createLinear(-2, -2, 1, 0, 32, 0, 0));
                FrameLayout frameLayout = new FrameLayout(context2);
                r92.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 51, 0, 36, 0, 22));
                frameLayout.addView(this.descriptionText2, LayoutHelper.createFrame(-2, -2, 49));
                if (this.currentType == 4) {
                    TextView textView5 = new TextView(context2);
                    this.descriptionText3 = textView5;
                    textView5.setTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
                    this.descriptionText3.setGravity(1);
                    this.descriptionText3.setTextSize(1, 14.0f);
                    this.descriptionText3.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
                    this.descriptionText3.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
                    this.descriptionText3.setText(LocaleController.getString("RestoreEmailTroubleNoEmail", NUM));
                    r92.addView(this.descriptionText3, LayoutHelper.createLinear(-2, -2, 49, 0, 0, 0, 25));
                    this.descriptionText3.setOnClickListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda12(this));
                }
                this.fragmentView = r7;
                AnonymousClass13 r22 = new View(context2) {
                    private Paint paint = new Paint();

                    /* access modifiers changed from: protected */
                    public void onDraw(Canvas canvas) {
                        this.paint.setColor(Theme.getColor("windowBackgroundWhite"));
                        int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(3.0f);
                        canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) measuredHeight, this.paint);
                        TwoStepVerificationSetupActivity.this.parentLayout.drawHeaderShadow(canvas, measuredHeight);
                    }
                };
                this.actionBarBackground = r22;
                r22.setAlpha(0.0f);
                r7.addView(this.actionBarBackground);
                r7.addView(this.actionBar);
                RadialProgressView radialProgressView3 = new RadialProgressView(context2);
                this.radialProgressView = radialProgressView3;
                radialProgressView3.setSize(AndroidUtilities.dp(20.0f));
                this.radialProgressView.setAlpha(0.0f);
                this.radialProgressView.setScaleX(0.1f);
                this.radialProgressView.setScaleY(0.1f);
                this.radialProgressView.setProgressColor(Theme.getColor("windowBackgroundWhiteInputFieldActivated"));
                r3.addView(this.radialProgressView, LayoutHelper.createFrame(32, 32.0f, 53, 0.0f, 16.0f, 16.0f, 0.0f));
                break;
            case 6:
            case 7:
            case 9:
                AnonymousClass3 r23 = new ViewGroup(context2) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        int size = View.MeasureSpec.getSize(i);
                        int size2 = View.MeasureSpec.getSize(i2);
                        TwoStepVerificationSetupActivity.this.actionBar.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), i2);
                        if (size > size2) {
                            float f = (float) size;
                            TwoStepVerificationSetupActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec((int) (0.45f * f), NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) size2) * 0.68f), NUM));
                            int i3 = (int) (f * 0.6f);
                            TwoStepVerificationSetupActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(i3, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            TwoStepVerificationSetupActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(i3, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            TwoStepVerificationSetupActivity.this.descriptionText2.measure(View.MeasureSpec.makeMeasureSpec(i3, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            TwoStepVerificationSetupActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(i3, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
                        } else {
                            float f2 = (float) (TwoStepVerificationSetupActivity.this.currentType == 7 ? 160 : 140);
                            TwoStepVerificationSetupActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(f2), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(f2), NUM));
                            TwoStepVerificationSetupActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            TwoStepVerificationSetupActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            TwoStepVerificationSetupActivity.this.descriptionText2.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            TwoStepVerificationSetupActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(48.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
                        }
                        setMeasuredDimension(size, size2);
                    }

                    /* access modifiers changed from: protected */
                    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                        TwoStepVerificationSetupActivity.this.actionBar.layout(0, 0, i3, TwoStepVerificationSetupActivity.this.actionBar.getMeasuredHeight());
                        int i5 = i3 - i;
                        int i6 = i4 - i2;
                        if (i3 > i4) {
                            int measuredHeight = (i6 - TwoStepVerificationSetupActivity.this.imageView.getMeasuredHeight()) / 2;
                            TwoStepVerificationSetupActivity.this.imageView.layout(0, measuredHeight, TwoStepVerificationSetupActivity.this.imageView.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.imageView.getMeasuredHeight() + measuredHeight);
                            float f = (float) i5;
                            float f2 = 0.4f * f;
                            int i7 = (int) f2;
                            float f3 = (float) i6;
                            int i8 = (int) (0.22f * f3);
                            TwoStepVerificationSetupActivity.this.titleTextView.layout(i7, i8, TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredWidth() + i7, TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredHeight() + i8);
                            int i9 = (int) (0.39f * f3);
                            TwoStepVerificationSetupActivity.this.descriptionText.layout(i7, i9, TwoStepVerificationSetupActivity.this.descriptionText.getMeasuredWidth() + i7, TwoStepVerificationSetupActivity.this.descriptionText.getMeasuredHeight() + i9);
                            int measuredWidth = (int) (f2 + (((f * 0.6f) - ((float) TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredWidth())) / 2.0f));
                            int i10 = (int) (f3 * 0.64f);
                            TwoStepVerificationSetupActivity.this.buttonTextView.layout(measuredWidth, i10, TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth, TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredHeight() + i10);
                            return;
                        }
                        int i11 = (int) (((float) i6) * 0.3f);
                        int measuredWidth2 = (i5 - TwoStepVerificationSetupActivity.this.imageView.getMeasuredWidth()) / 2;
                        TwoStepVerificationSetupActivity.this.imageView.layout(measuredWidth2, i11, TwoStepVerificationSetupActivity.this.imageView.getMeasuredWidth() + measuredWidth2, TwoStepVerificationSetupActivity.this.imageView.getMeasuredHeight() + i11);
                        int measuredHeight2 = i11 + TwoStepVerificationSetupActivity.this.imageView.getMeasuredHeight() + AndroidUtilities.dp(16.0f);
                        TwoStepVerificationSetupActivity.this.titleTextView.layout(0, measuredHeight2, TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredHeight() + measuredHeight2);
                        int measuredHeight3 = measuredHeight2 + TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredHeight() + AndroidUtilities.dp(12.0f);
                        TwoStepVerificationSetupActivity.this.descriptionText.layout(0, measuredHeight3, TwoStepVerificationSetupActivity.this.descriptionText.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.descriptionText.getMeasuredHeight() + measuredHeight3);
                        int measuredWidth3 = (i5 - TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                        int measuredHeight4 = (i6 - TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredHeight()) - AndroidUtilities.dp(48.0f);
                        TwoStepVerificationSetupActivity.this.buttonTextView.layout(measuredWidth3, measuredHeight4, TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth3, TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredHeight() + measuredHeight4);
                    }
                };
                r23.setOnTouchListener(TwoStepVerificationSetupActivity$$ExternalSyntheticLambda16.INSTANCE);
                r23.addView(this.actionBar);
                r23.addView(this.imageView);
                r23.addView(this.titleTextView);
                r23.addView(this.descriptionText);
                r23.addView(this.buttonTextView);
                this.fragmentView = r23;
                break;
        }
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        int i3 = this.currentType;
        switch (i3) {
            case 0:
            case 1:
                if (this.currentPassword.has_password) {
                    this.actionBar.setTitle(LocaleController.getString("PleaseEnterNewFirstPassword", NUM));
                    this.titleTextView.setText(LocaleController.getString("PleaseEnterNewFirstPassword", NUM));
                } else {
                    String string = LocaleController.getString(i3 == 0 ? NUM : NUM);
                    this.actionBar.setTitle(string);
                    this.titleTextView.setText(string);
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
                String str = this.currentPassword.email_unconfirmed_pattern;
                if (str == null) {
                    str = "";
                }
                SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(str);
                int indexOf = str.indexOf(42);
                int lastIndexOf = str.lastIndexOf(42);
                if (!(indexOf == lastIndexOf || indexOf == -1 || lastIndexOf == -1)) {
                    TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun();
                    textStyleRun.flags |= 256;
                    textStyleRun.start = indexOf;
                    int i4 = lastIndexOf + 1;
                    textStyleRun.end = i4;
                    valueOf.setSpan(new TextStyleSpan(textStyleRun), indexOf, i4, 0);
                }
                this.descriptionText.setText(AndroidUtilities.formatSpannable(LocaleController.getString(NUM), valueOf));
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
                String str2 = this.currentPassword.email_unconfirmed_pattern;
                if (str2 == null) {
                    str2 = "";
                }
                objArr[0] = str2;
                textView6.setText(LocaleController.formatString("EmailPasswordConfirmText2", NUM, objArr));
                this.descriptionText.setVisibility(0);
                this.floatingButtonContainer.setVisibility(8);
                this.bottomSkipButton.setVisibility(0);
                this.bottomSkipButton.setGravity(17);
                ((ViewGroup.MarginLayoutParams) this.bottomSkipButton.getLayoutParams()).bottomMargin = 0;
                this.bottomSkipButton.setText(LocaleController.getString(NUM));
                this.bottomSkipButton.setOnClickListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda6(this));
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
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    if (!TwoStepVerificationSetupActivity.this.ignoreTextChange) {
                        if (TwoStepVerificationSetupActivity.this.currentType == 0) {
                            RLottieDrawable animatedDrawable = TwoStepVerificationSetupActivity.this.imageView.getAnimatedDrawable();
                            if (TwoStepVerificationSetupActivity.this.editTextFirstRow.length() > 0) {
                                if (TwoStepVerificationSetupActivity.this.editTextFirstRow.getTransformationMethod() == null) {
                                    if (animatedDrawable != TwoStepVerificationSetupActivity.this.animationDrawables[3] && animatedDrawable != TwoStepVerificationSetupActivity.this.animationDrawables[5]) {
                                        TwoStepVerificationSetupActivity.this.imageView.setAnimation(TwoStepVerificationSetupActivity.this.animationDrawables[5]);
                                        TwoStepVerificationSetupActivity.this.animationDrawables[5].setProgress(0.0f, false);
                                        TwoStepVerificationSetupActivity.this.imageView.playAnimation();
                                    }
                                } else if (animatedDrawable == TwoStepVerificationSetupActivity.this.animationDrawables[3]) {
                                } else {
                                    if (animatedDrawable != TwoStepVerificationSetupActivity.this.animationDrawables[2]) {
                                        TwoStepVerificationSetupActivity.this.imageView.setAnimation(TwoStepVerificationSetupActivity.this.animationDrawables[2]);
                                        TwoStepVerificationSetupActivity.this.animationDrawables[2].setCustomEndFrame(49);
                                        TwoStepVerificationSetupActivity.this.animationDrawables[2].setProgress(0.0f, false);
                                        TwoStepVerificationSetupActivity.this.imageView.playAnimation();
                                    } else if (TwoStepVerificationSetupActivity.this.animationDrawables[2].getCurrentFrame() < 49) {
                                        TwoStepVerificationSetupActivity.this.animationDrawables[2].setCustomEndFrame(49);
                                    }
                                }
                            } else if ((animatedDrawable == TwoStepVerificationSetupActivity.this.animationDrawables[3] && TwoStepVerificationSetupActivity.this.editTextFirstRow.getTransformationMethod() == null) || animatedDrawable == TwoStepVerificationSetupActivity.this.animationDrawables[5]) {
                                TwoStepVerificationSetupActivity.this.imageView.setAnimation(TwoStepVerificationSetupActivity.this.animationDrawables[4]);
                                TwoStepVerificationSetupActivity.this.animationDrawables[4].setProgress(0.0f, false);
                                TwoStepVerificationSetupActivity.this.imageView.playAnimation();
                            } else {
                                TwoStepVerificationSetupActivity.this.animationDrawables[2].setCustomEndFrame(-1);
                                if (animatedDrawable != TwoStepVerificationSetupActivity.this.animationDrawables[2]) {
                                    TwoStepVerificationSetupActivity.this.imageView.setAnimation(TwoStepVerificationSetupActivity.this.animationDrawables[2]);
                                    TwoStepVerificationSetupActivity.this.animationDrawables[2].setCurrentFrame(49, false);
                                }
                                TwoStepVerificationSetupActivity.this.imageView.playAnimation();
                            }
                        } else if (TwoStepVerificationSetupActivity.this.currentType == 1) {
                            try {
                                TwoStepVerificationSetupActivity.this.animationDrawables[6].setCustomEndFrame((int) ((Math.min(1.0f, TwoStepVerificationSetupActivity.this.editTextFirstRow.getLayout().getLineWidth(0) / ((float) TwoStepVerificationSetupActivity.this.editTextFirstRow.getWidth())) * 142.0f) + 18.0f));
                                TwoStepVerificationSetupActivity.this.imageView.playAnimation();
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                            }
                        } else if (TwoStepVerificationSetupActivity.this.currentType == 8 && editable.length() > 0) {
                            TwoStepVerificationSetupActivity.this.showDoneButton(true);
                        }
                    }
                }
            });
        }
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(View view) {
        processNext();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$7(View view) {
        int i = this.currentType;
        if (i == 0) {
            needShowProgress();
            TLRPC$TL_auth_recoverPassword tLRPC$TL_auth_recoverPassword = new TLRPC$TL_auth_recoverPassword();
            tLRPC$TL_auth_recoverPassword.code = this.emailCode;
            getConnectionsManager().sendRequest(tLRPC$TL_auth_recoverPassword, new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda44(this));
        } else if (i == 3) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setMessage(LocaleController.getString("YourEmailSkipWarningText", NUM));
            builder.setTitle(LocaleController.getString("YourEmailSkipWarning", NUM));
            builder.setPositiveButton(LocaleController.getString("YourEmailSkip", NUM), new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda1(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            AlertDialog create = builder.create();
            showDialog(create);
            TextView textView = (TextView) create.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        } else if (i == 2) {
            onHintDone();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$5(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda35(this, tLRPC$TL_error));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(TLRPC$TL_error tLRPC$TL_error) {
        String str;
        needHideProgress();
        if (tLRPC$TL_error == null) {
            getMessagesController().removeSuggestion(0, "VALIDATE_PASSWORD");
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda3(this));
            builder.setMessage(LocaleController.getString("PasswordReset", NUM));
            builder.setTitle(LocaleController.getString("TwoStepVerificationTitle", NUM));
            Dialog showDialog = showDialog(builder.create());
            if (showDialog != null) {
                showDialog.setCanceledOnTouchOutside(false);
                showDialog.setCancelable(false);
            }
        } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
            int intValue = Utilities.parseInt((CharSequence) tLRPC$TL_error.text).intValue();
            if (intValue < 60) {
                str = LocaleController.formatPluralString("Seconds", intValue, new Object[0]);
            } else {
                str = LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0]);
            }
            showAlertWithText(LocaleController.getString("TwoStepVerificationTitle", NUM), LocaleController.formatString("FloodWaitTime", NUM, str));
        } else {
            showAlertWithText(LocaleController.getString("TwoStepVerificationTitle", NUM), tLRPC$TL_error.text);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(DialogInterface dialogInterface, int i) {
        int size = this.fragmentsToClose.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.fragmentsToClose.get(i2).removeSelfFromStack();
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.twoStepPasswordChanged, new Object[0]);
        finishFragment();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(DialogInterface dialogInterface, int i) {
        this.email = "";
        setNewPassword(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$8(View view) {
        if (this.currentType == 8) {
            TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
            twoStepVerificationActivity.setForgotPasswordOnShow();
            twoStepVerificationActivity.setPassword(this.currentPassword);
            twoStepVerificationActivity.setBlockingAlert(this.otherwiseReloginDays);
            presentFragment(twoStepVerificationActivity, true);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$9(View view) {
        processNext();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$11(TextView textView, int i, KeyEvent keyEvent) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$12(View view, boolean z) {
        this.outlineTextFirstRow.animateSelection(z ? 1.0f : 0.0f);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$13(View view) {
        this.ignoreTextChange = true;
        if (this.editTextFirstRow.getTransformationMethod() == null) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$14(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 && i != 6) {
            return false;
        }
        processNext();
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$15(View view, boolean z) {
        this.outlineTextSecondRow.animateSelection(z ? 1.0f : 0.0f);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$16(View view, boolean z) {
        if (z) {
            this.keyboardView.setEditText((EditText) view);
            this.keyboardView.setDispatchBackWhenEmpty(true);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$18(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("Reset", NUM), new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda2(this));
        builder.setTitle(LocaleController.getString("ResetPassword", NUM));
        builder.setMessage(LocaleController.getString("RestoreEmailTroubleText2", NUM));
        showDialog(builder.create());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$17(DialogInterface dialogInterface, int i) {
        onReset();
        finishFragment();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$20(View view) {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_resendPasswordEmail(), TwoStepVerificationSetupActivity$$ExternalSyntheticLambda51.INSTANCE);
        showDialog(new AlertDialog.Builder((Context) getParentActivity()).setMessage(LocaleController.getString("ResendCodeInfo", NUM)).setTitle(LocaleController.getString("TwoStepVerificationTitle", NUM)).setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null).create());
    }

    private boolean isIntro() {
        int i = this.currentType;
        return i == 6 || i == 9 || i == 7;
    }

    /* access modifiers changed from: private */
    public boolean isLandscape() {
        Point point = AndroidUtilities.displaySize;
        return point.x > point.y;
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        int i = 0;
        if (this.imageView != null) {
            if (this.currentType == 2 && AndroidUtilities.isSmallScreen()) {
                this.imageView.setVisibility(8);
            } else if (!isIntro()) {
                this.imageView.setVisibility(isLandscape() ? 8 : 0);
            }
        }
        CustomPhoneKeyboardView customPhoneKeyboardView = this.keyboardView;
        if (customPhoneKeyboardView != null) {
            if (!isCustomKeyboardVisible()) {
                i = 8;
            }
            customPhoneKeyboardView.setVisibility(i);
        }
    }

    private void animateSuccess(Runnable runnable) {
        int i = 0;
        while (true) {
            CodeFieldContainer codeFieldContainer2 = this.codeFieldContainer;
            CodeNumberField[] codeNumberFieldArr = codeFieldContainer2.codeField;
            if (i < codeNumberFieldArr.length) {
                CodeNumberField codeNumberField = codeNumberFieldArr[i];
                codeNumberField.postDelayed(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda19(codeNumberField), ((long) i) * 75);
                i++;
            } else {
                codeFieldContainer2.postDelayed(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda28(this, runnable), (((long) this.codeFieldContainer.codeField.length) * 75) + 350);
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateSuccess$22(Runnable runnable) {
        for (CodeNumberField animateSuccessProgress : this.codeFieldContainer.codeField) {
            animateSuccessProgress.animateSuccessProgress(0.0f);
        }
        runnable.run();
    }

    private void switchMonkeyAnimation(boolean z) {
        if (z) {
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

    /* access modifiers changed from: private */
    public boolean isCustomKeyboardVisible() {
        int i = this.currentType;
        if ((i == 5 || i == 4) && !AndroidUtilities.isTablet()) {
            Point point = AndroidUtilities.displaySize;
            return point.x < point.y && !AndroidUtilities.isAccessibilityTouchExplorationEnabled();
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
        if (isCustomKeyboardVisible()) {
            AndroidUtilities.requestAltFocusable(getParentActivity(), this.classGuid);
            AndroidUtilities.hideKeyboard(this.fragmentView);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't wrap try/catch for region: R(6:51|52|53|54|55|63) */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x018a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x018b, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:?, code lost:
        return;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:54:0x0175 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void processNext() {
        /*
            r9 = this;
            android.app.Activity r0 = r9.getParentActivity()
            if (r0 != 0) goto L_0x0007
            return
        L_0x0007:
            int r0 = r9.currentType
            r1 = 10
            r2 = 1
            r3 = 0
            switch(r0) {
                case 0: goto L_0x0141;
                case 1: goto L_0x0141;
                case 2: goto L_0x0107;
                case 3: goto L_0x00e8;
                case 4: goto L_0x00cb;
                case 5: goto L_0x00ab;
                case 6: goto L_0x0088;
                case 7: goto L_0x004c;
                case 8: goto L_0x0017;
                case 9: goto L_0x0012;
                default: goto L_0x0010;
            }
        L_0x0010:
            goto L_0x01d7
        L_0x0012:
            r9.finishFragment()
            goto L_0x01d7
        L_0x0017:
            org.telegram.tgnet.TLRPC$TL_account_password r0 = r9.currentPassword
            if (r0 != 0) goto L_0x0021
            r9.needShowProgress()
            r9.doneAfterPasswordLoad = r2
            return
        L_0x0021:
            org.telegram.ui.Components.EditTextBoldCursor r0 = r9.editTextFirstRow
            android.text.Editable r0 = r0.getText()
            java.lang.String r0 = r0.toString()
            int r1 = r0.length()
            if (r1 != 0) goto L_0x0039
            org.telegram.ui.Components.OutlineTextContainerView r0 = r9.outlineTextFirstRow
            org.telegram.ui.Components.EditTextBoldCursor r1 = r9.editTextFirstRow
            r9.onFieldError(r0, r1, r3)
            return
        L_0x0039:
            byte[] r0 = org.telegram.messenger.AndroidUtilities.getStringBytes(r0)
            r9.needShowProgress()
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.globalQueue
            org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda40 r2 = new org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda40
            r2.<init>(r9, r0)
            r1.postRunnable(r2)
            goto L_0x01d7
        L_0x004c:
            boolean r0 = r9.closeAfterSet
            if (r0 == 0) goto L_0x0055
            r9.finishFragment()
            goto L_0x01d7
        L_0x0055:
            boolean r0 = r9.fromRegistration
            if (r0 == 0) goto L_0x006d
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "afterSignup"
            r0.putBoolean(r1, r2)
            org.telegram.ui.DialogsActivity r1 = new org.telegram.ui.DialogsActivity
            r1.<init>(r0)
            r9.presentFragment(r1, r2)
            goto L_0x01d7
        L_0x006d:
            org.telegram.ui.TwoStepVerificationActivity r0 = new org.telegram.ui.TwoStepVerificationActivity
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_account_password r4 = r9.currentPassword
            byte[] r5 = r9.currentPasswordHash
            long r6 = r9.currentSecretId
            byte[] r8 = r9.currentSecret
            r3 = r0
            r3.setCurrentPasswordParams(r4, r5, r6, r8)
            int r1 = r9.otherwiseReloginDays
            r0.setBlockingAlert(r1)
            r9.presentFragment(r0, r2)
            goto L_0x01d7
        L_0x0088:
            org.telegram.tgnet.TLRPC$TL_account_password r0 = r9.currentPassword
            if (r0 != 0) goto L_0x0092
            r9.needShowProgress()
            r9.doneAfterPasswordLoad = r2
            return
        L_0x0092:
            org.telegram.ui.TwoStepVerificationSetupActivity r1 = new org.telegram.ui.TwoStepVerificationSetupActivity
            int r4 = r9.currentAccount
            r1.<init>(r4, r3, r0)
            boolean r0 = r9.fromRegistration
            r1.fromRegistration = r0
            boolean r0 = r9.closeAfterSet
            r1.closeAfterSet = r0
            int r0 = r9.otherwiseReloginDays
            r1.setBlockingAlert(r0)
            r9.presentFragment(r1, r2)
            goto L_0x01d7
        L_0x00ab:
            org.telegram.tgnet.TLRPC$TL_account_confirmPasswordEmail r0 = new org.telegram.tgnet.TLRPC$TL_account_confirmPasswordEmail
            r0.<init>()
            org.telegram.ui.CodeFieldContainer r2 = r9.codeFieldContainer
            java.lang.String r2 = r2.getCode()
            r0.code = r2
            int r2 = r9.currentAccount
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
            org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda46 r3 = new org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda46
            r3.<init>(r9)
            r2.sendRequest(r0, r3, r1)
            r9.needShowProgress()
            goto L_0x01d7
        L_0x00cb:
            org.telegram.ui.CodeFieldContainer r0 = r9.codeFieldContainer
            java.lang.String r0 = r0.getCode()
            org.telegram.tgnet.TLRPC$TL_auth_checkRecoveryPassword r2 = new org.telegram.tgnet.TLRPC$TL_auth_checkRecoveryPassword
            r2.<init>()
            r2.code = r0
            int r3 = r9.currentAccount
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3)
            org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda47 r4 = new org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda47
            r4.<init>(r9, r0)
            r3.sendRequest(r2, r4, r1)
            goto L_0x01d7
        L_0x00e8:
            org.telegram.ui.Components.EditTextBoldCursor r0 = r9.editTextFirstRow
            android.text.Editable r0 = r0.getText()
            java.lang.String r0 = r0.toString()
            r9.email = r0
            boolean r0 = r9.isValidEmail(r0)
            if (r0 != 0) goto L_0x0102
            org.telegram.ui.Components.OutlineTextContainerView r0 = r9.outlineTextFirstRow
            org.telegram.ui.Components.EditTextBoldCursor r1 = r9.editTextFirstRow
            r9.onFieldError(r0, r1, r3)
            return
        L_0x0102:
            r9.setNewPassword(r3)
            goto L_0x01d7
        L_0x0107:
            org.telegram.ui.Components.EditTextBoldCursor r0 = r9.editTextFirstRow
            android.text.Editable r0 = r0.getText()
            java.lang.String r0 = r0.toString()
            r9.hint = r0
            java.lang.String r1 = r9.firstPassword
            boolean r0 = r0.equalsIgnoreCase(r1)
            if (r0 == 0) goto L_0x013c
            android.app.Activity r0 = r9.getParentActivity()     // Catch:{ Exception -> 0x0130 }
            java.lang.String r1 = "PasswordAsHintError"
            r2 = 2131627417(0x7f0e0d99, float:1.8882098E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x0130 }
            android.widget.Toast r0 = android.widget.Toast.makeText(r0, r1, r3)     // Catch:{ Exception -> 0x0130 }
            r0.show()     // Catch:{ Exception -> 0x0130 }
            goto L_0x0134
        L_0x0130:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0134:
            org.telegram.ui.Components.OutlineTextContainerView r0 = r9.outlineTextFirstRow
            org.telegram.ui.Components.EditTextBoldCursor r1 = r9.editTextFirstRow
            r9.onFieldError(r0, r1, r3)
            return
        L_0x013c:
            r9.onHintDone()
            goto L_0x01d7
        L_0x0141:
            org.telegram.ui.Components.EditTextBoldCursor r0 = r9.editTextFirstRow
            int r0 = r0.length()
            if (r0 != 0) goto L_0x0151
            org.telegram.ui.Components.OutlineTextContainerView r0 = r9.outlineTextFirstRow
            org.telegram.ui.Components.EditTextBoldCursor r1 = r9.editTextFirstRow
            r9.onFieldError(r0, r1, r3)
            return
        L_0x0151:
            org.telegram.ui.Components.EditTextBoldCursor r0 = r9.editTextFirstRow
            android.text.Editable r0 = r0.getText()
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = r9.firstPassword
            boolean r0 = r0.equals(r1)
            r1 = 2
            if (r0 != 0) goto L_0x018f
            int r0 = r9.currentType
            if (r0 != r2) goto L_0x018f
            org.telegram.ui.Components.OutlineTextContainerView r0 = r9.outlineTextFirstRow
            r2 = 1084227584(0x40a00000, float:5.0)
            org.telegram.messenger.AndroidUtilities.shakeViewSpring((android.view.View) r0, (float) r2)
            org.telegram.ui.Components.OutlineTextContainerView r0 = r9.outlineTextFirstRow     // Catch:{ Exception -> 0x0175 }
            r2 = 3
            r0.performHapticFeedback(r2, r1)     // Catch:{ Exception -> 0x0175 }
        L_0x0175:
            android.app.Activity r0 = r9.getParentActivity()     // Catch:{ Exception -> 0x018a }
            java.lang.String r1 = "PasswordDoNotMatch"
            r2 = 2131627419(0x7f0e0d9b, float:1.8882102E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x018a }
            android.widget.Toast r0 = android.widget.Toast.makeText(r0, r1, r3)     // Catch:{ Exception -> 0x018a }
            r0.show()     // Catch:{ Exception -> 0x018a }
            goto L_0x018e
        L_0x018a:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x018e:
            return
        L_0x018f:
            org.telegram.ui.TwoStepVerificationSetupActivity r0 = new org.telegram.ui.TwoStepVerificationSetupActivity
            int r3 = r9.currentAccount
            int r4 = r9.currentType
            if (r4 != 0) goto L_0x0198
            goto L_0x0199
        L_0x0198:
            r2 = 2
        L_0x0199:
            org.telegram.tgnet.TLRPC$TL_account_password r1 = r9.currentPassword
            r0.<init>(r3, r2, r1)
            boolean r1 = r9.fromRegistration
            r0.fromRegistration = r1
            org.telegram.ui.Components.EditTextBoldCursor r1 = r9.editTextFirstRow
            android.text.Editable r1 = r1.getText()
            java.lang.String r1 = r1.toString()
            r0.firstPassword = r1
            byte[] r2 = r9.currentPasswordHash
            long r3 = r9.currentSecretId
            byte[] r5 = r9.currentSecret
            boolean r6 = r9.emailOnly
            r1 = r0
            r1.setCurrentPasswordParams(r2, r3, r5, r6)
            java.lang.String r1 = r9.emailCode
            r0.setCurrentEmailCode(r1)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r0.fragmentsToClose
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r9.fragmentsToClose
            r1.addAll(r2)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r0.fragmentsToClose
            r1.add(r9)
            boolean r1 = r9.closeAfterSet
            r0.closeAfterSet = r1
            int r1 = r9.otherwiseReloginDays
            r0.setBlockingAlert(r1)
            r9.presentFragment(r0)
        L_0x01d7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TwoStepVerificationSetupActivity.processNext():void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processNext$28(byte[] bArr) {
        TLRPC$TL_account_getPasswordSettings tLRPC$TL_account_getPasswordSettings = new TLRPC$TL_account_getPasswordSettings();
        TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo = this.currentPassword.current_algo;
        byte[] x = tLRPC$PasswordKdfAlgo instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow ? SRPHelper.getX(bArr, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo) : null;
        TwoStepVerificationSetupActivity$$ExternalSyntheticLambda50 twoStepVerificationSetupActivity$$ExternalSyntheticLambda50 = new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda50(this, x);
        TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
        TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo2 = tLRPC$TL_account_password.current_algo;
        if (tLRPC$PasswordKdfAlgo2 instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TLRPC$TL_inputCheckPasswordSRP startCheck = SRPHelper.startCheck(x, tLRPC$TL_account_password.srp_id, tLRPC$TL_account_password.srp_B, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo2);
            tLRPC$TL_account_getPasswordSettings.password = startCheck;
            if (startCheck == null) {
                TLRPC$TL_error tLRPC$TL_error = new TLRPC$TL_error();
                tLRPC$TL_error.text = "ALGO_INVALID";
                twoStepVerificationSetupActivity$$ExternalSyntheticLambda50.run((TLObject) null, tLRPC$TL_error);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_getPasswordSettings, twoStepVerificationSetupActivity$$ExternalSyntheticLambda50, 10);
            return;
        }
        TLRPC$TL_error tLRPC$TL_error2 = new TLRPC$TL_error();
        tLRPC$TL_error2.text = "PASSWORD_HASH_INVALID";
        twoStepVerificationSetupActivity$$ExternalSyntheticLambda50.run((TLObject) null, tLRPC$TL_error2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processNext$27(byte[] bArr, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda41(this, bArr));
        } else {
            AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda32(this, tLRPC$TL_error));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processNext$23(byte[] bArr) {
        needHideProgress();
        this.currentPasswordHash = bArr;
        getMessagesController().removeSuggestion(0, "VALIDATE_PASSWORD");
        TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(9, this.currentPassword);
        twoStepVerificationSetupActivity.fromRegistration = this.fromRegistration;
        twoStepVerificationSetupActivity.setBlockingAlert(this.otherwiseReloginDays);
        presentFragment(twoStepVerificationSetupActivity, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processNext$26(TLRPC$TL_error tLRPC$TL_error) {
        String str;
        if ("SRP_ID_INVALID".equals(tLRPC$TL_error.text)) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda42(this), 8);
            return;
        }
        needHideProgress();
        if ("PASSWORD_HASH_INVALID".equals(tLRPC$TL_error.text)) {
            this.descriptionText.setText(LocaleController.getString("CheckPasswordWrong", NUM));
            this.descriptionText.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
            onFieldError(this.outlineTextFirstRow, this.editTextFirstRow, true);
            showDoneButton(false);
        } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
            int intValue = Utilities.parseInt((CharSequence) tLRPC$TL_error.text).intValue();
            if (intValue < 60) {
                str = LocaleController.formatPluralString("Seconds", intValue, new Object[0]);
            } else {
                str = LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0]);
            }
            showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, str));
        } else {
            showAlertWithText(LocaleController.getString("AppName", NUM), tLRPC$TL_error.text);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processNext$25(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda37(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processNext$24(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            this.currentPassword = tLRPC$TL_account_password;
            TwoStepVerificationActivity.initPasswordNewAlgo(tLRPC$TL_account_password);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            processNext();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processNext$31(String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda30(this, tLObject, str, tLRPC$TL_error));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processNext$30(TLObject tLObject, String str, TLRPC$TL_error tLRPC$TL_error) {
        String str2;
        if (tLObject instanceof TLRPC$TL_boolTrue) {
            animateSuccess(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda29(this, str));
        } else if (tLRPC$TL_error == null || tLRPC$TL_error.text.startsWith("CODE_INVALID")) {
            onCodeFieldError(true);
        } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
            int intValue = Utilities.parseInt((CharSequence) tLRPC$TL_error.text).intValue();
            if (intValue < 60) {
                str2 = LocaleController.formatPluralString("Seconds", intValue, new Object[0]);
            } else {
                str2 = LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0]);
            }
            showAlertWithText(LocaleController.getString("TwoStepVerificationTitle", NUM), LocaleController.formatString("FloodWaitTime", NUM, str2));
        } else {
            showAlertWithText(LocaleController.getString("TwoStepVerificationTitle", NUM), tLRPC$TL_error.text);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processNext$29(String str) {
        TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(this.currentAccount, 0, this.currentPassword);
        twoStepVerificationSetupActivity.fromRegistration = this.fromRegistration;
        twoStepVerificationSetupActivity.fragmentsToClose.addAll(this.fragmentsToClose);
        twoStepVerificationSetupActivity.addFragmentToClose(this);
        twoStepVerificationSetupActivity.setCurrentEmailCode(str);
        twoStepVerificationSetupActivity.setBlockingAlert(this.otherwiseReloginDays);
        presentFragment(twoStepVerificationSetupActivity, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processNext$35(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda33(this, tLRPC$TL_error));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processNext$34(TLRPC$TL_error tLRPC$TL_error) {
        String str;
        needHideProgress();
        if (tLRPC$TL_error == null) {
            if (getParentActivity() != null) {
                animateSuccess(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda23(this));
            }
        } else if (tLRPC$TL_error.text.startsWith("CODE_INVALID")) {
            onCodeFieldError(true);
        } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
            int intValue = Utilities.parseInt((CharSequence) tLRPC$TL_error.text).intValue();
            if (intValue < 60) {
                str = LocaleController.formatPluralString("Seconds", intValue, new Object[0]);
            } else {
                str = LocaleController.formatPluralString("Minutes", intValue / 60, new Object[0]);
            }
            showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, str));
        } else {
            showAlertWithText(LocaleController.getString("AppName", NUM), tLRPC$TL_error.text);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processNext$33() {
        if (this.currentPassword.has_password) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda4(this));
            if (this.currentPassword.has_recovery) {
                builder.setMessage(LocaleController.getString("YourEmailSuccessChangedText", NUM));
            } else {
                builder.setMessage(LocaleController.getString("YourEmailSuccessText", NUM));
            }
            builder.setTitle(LocaleController.getString("YourPasswordSuccess", NUM));
            Dialog showDialog = showDialog(builder.create());
            if (showDialog != null) {
                showDialog.setCanceledOnTouchOutside(false);
                showDialog.setCancelable(false);
                return;
            }
            return;
        }
        int size = this.fragmentsToClose.size();
        for (int i = 0; i < size; i++) {
            this.fragmentsToClose.get(i).removeSelfFromStack();
        }
        TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
        tLRPC$TL_account_password.has_password = true;
        tLRPC$TL_account_password.has_recovery = true;
        tLRPC$TL_account_password.email_unconfirmed_pattern = "";
        TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(7, tLRPC$TL_account_password);
        twoStepVerificationSetupActivity.fromRegistration = this.fromRegistration;
        twoStepVerificationSetupActivity.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, this.emailOnly);
        twoStepVerificationSetupActivity.fragmentsToClose.addAll(this.fragmentsToClose);
        twoStepVerificationSetupActivity.closeAfterSet = this.closeAfterSet;
        twoStepVerificationSetupActivity.setBlockingAlert(this.otherwiseReloginDays);
        presentFragment(twoStepVerificationSetupActivity, true);
        NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
        int i2 = NotificationCenter.twoStepPasswordChanged;
        TLRPC$TL_account_password tLRPC$TL_account_password2 = this.currentPassword;
        instance.postNotificationName(i2, this.currentPasswordHash, tLRPC$TL_account_password2.new_algo, tLRPC$TL_account_password2.new_secure_algo, tLRPC$TL_account_password2.secure_random, this.email, this.hint, null, this.firstPassword);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processNext$32(DialogInterface dialogInterface, int i) {
        int size = this.fragmentsToClose.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.fragmentsToClose.get(i2).removeSelfFromStack();
        }
        NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
        int i3 = NotificationCenter.twoStepPasswordChanged;
        TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
        instance.postNotificationName(i3, this.currentPasswordHash, tLRPC$TL_account_password.new_algo, tLRPC$TL_account_password.new_secure_algo, tLRPC$TL_account_password.secure_random, this.email, this.hint, null, this.firstPassword);
        TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
        TLRPC$TL_account_password tLRPC$TL_account_password2 = this.currentPassword;
        tLRPC$TL_account_password2.has_password = true;
        tLRPC$TL_account_password2.has_recovery = true;
        tLRPC$TL_account_password2.email_unconfirmed_pattern = "";
        twoStepVerificationActivity.setCurrentPasswordParams(tLRPC$TL_account_password2, this.currentPasswordHash, this.currentSecretId, this.currentSecret);
        twoStepVerificationActivity.setBlockingAlert(this.otherwiseReloginDays);
        presentFragment(twoStepVerificationActivity, true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
    }

    private void onCodeFieldError(boolean z) {
        for (CodeNumberField codeNumberField : this.codeFieldContainer.codeField) {
            if (z) {
                codeNumberField.setText("");
            }
            codeNumberField.animateErrorProgress(1.0f);
        }
        if (z) {
            this.codeFieldContainer.codeField[0].requestFocus();
        }
        AndroidUtilities.shakeViewSpring(this.codeFieldContainer, 8.0f, new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda25(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCodeFieldError$37() {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda22(this), 150);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCodeFieldError$36() {
        for (CodeNumberField animateErrorProgress : this.codeFieldContainer.codeField) {
            animateErrorProgress.animateErrorProgress(0.0f);
        }
    }

    /* access modifiers changed from: protected */
    public boolean hideKeyboardOnShow() {
        int i = this.currentType;
        return i == 7 || i == 9;
    }

    private void onHintDone() {
        TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
        if (!tLRPC$TL_account_password.has_recovery) {
            TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(this.currentAccount, 3, tLRPC$TL_account_password);
            twoStepVerificationSetupActivity.fromRegistration = this.fromRegistration;
            twoStepVerificationSetupActivity.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, this.emailOnly);
            twoStepVerificationSetupActivity.firstPassword = this.firstPassword;
            twoStepVerificationSetupActivity.hint = this.hint;
            twoStepVerificationSetupActivity.fragmentsToClose.addAll(this.fragmentsToClose);
            twoStepVerificationSetupActivity.fragmentsToClose.add(this);
            twoStepVerificationSetupActivity.closeAfterSet = this.closeAfterSet;
            twoStepVerificationSetupActivity.setBlockingAlert(this.otherwiseReloginDays);
            presentFragment(twoStepVerificationSetupActivity);
            return;
        }
        this.email = "";
        setNewPassword(false);
    }

    /* access modifiers changed from: private */
    public void showDoneButton(final boolean z) {
        if (z != (this.buttonTextView.getTag() != null)) {
            AnimatorSet animatorSet = this.buttonAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.buttonTextView.setTag(z ? 1 : null);
            this.buttonAnimation = new AnimatorSet();
            if (z) {
                this.buttonTextView.setVisibility(0);
                this.buttonAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.descriptionText2, View.SCALE_X, new float[]{0.9f}), ObjectAnimator.ofFloat(this.descriptionText2, View.SCALE_Y, new float[]{0.9f}), ObjectAnimator.ofFloat(this.descriptionText2, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.buttonTextView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.buttonTextView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.buttonTextView, View.ALPHA, new float[]{1.0f})});
            } else {
                this.descriptionText2.setVisibility(0);
                this.buttonAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.buttonTextView, View.SCALE_X, new float[]{0.9f}), ObjectAnimator.ofFloat(this.buttonTextView, View.SCALE_Y, new float[]{0.9f}), ObjectAnimator.ofFloat(this.buttonTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.descriptionText2, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.descriptionText2, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.descriptionText2, View.ALPHA, new float[]{1.0f})});
            }
            this.buttonAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (TwoStepVerificationSetupActivity.this.buttonAnimation != null && TwoStepVerificationSetupActivity.this.buttonAnimation.equals(animator)) {
                        if (z) {
                            TwoStepVerificationSetupActivity.this.descriptionText2.setVisibility(4);
                        } else {
                            TwoStepVerificationSetupActivity.this.buttonTextView.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (TwoStepVerificationSetupActivity.this.buttonAnimation != null && TwoStepVerificationSetupActivity.this.buttonAnimation.equals(animator)) {
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
            boolean r0 = r0.isRunning()
            if (r0 != 0) goto L_0x0063
        L_0x0030:
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random
            int r0 = r0.nextInt()
            int r0 = r0 % 2
            r3 = 0
            if (r0 != 0) goto L_0x004c
            org.telegram.ui.Components.RLottieImageView r0 = r5.imageView
            org.telegram.ui.Components.RLottieDrawable[] r1 = r5.animationDrawables
            r1 = r1[r2]
            r0.setAnimation(r1)
            org.telegram.ui.Components.RLottieDrawable[] r0 = r5.animationDrawables
            r0 = r0[r2]
            r0.setProgress(r3)
            goto L_0x005c
        L_0x004c:
            org.telegram.ui.Components.RLottieImageView r0 = r5.imageView
            org.telegram.ui.Components.RLottieDrawable[] r2 = r5.animationDrawables
            r2 = r2[r1]
            r0.setAnimation(r2)
            org.telegram.ui.Components.RLottieDrawable[] r0 = r5.animationDrawables
            r0 = r0[r1]
            r0.setProgress(r3)
        L_0x005c:
            if (r6 != 0) goto L_0x0063
            org.telegram.ui.Components.RLottieImageView r6 = r5.imageView
            r6.playAnimation()
        L_0x0063:
            org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda27 r6 = new org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda27
            r6.<init>(r5)
            r5.setAnimationRunnable = r6
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random
            r1 = 2000(0x7d0, float:2.803E-42)
            int r0 = r0.nextInt(r1)
            int r0 = r0 + 5000
            long r0 = (long) r0
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r6, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TwoStepVerificationSetupActivity.setRandomMonkeyIdleAnimation(boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setRandomMonkeyIdleAnimation$38() {
        if (this.setAnimationRunnable != null) {
            setRandomMonkeyIdleAnimation(false);
        }
    }

    public void setCloseAfterSet(boolean z) {
        this.closeAfterSet = z;
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            if (this.editTextFirstRow != null && !isCustomKeyboardVisible()) {
                AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda24(this), 200);
            }
            CodeFieldContainer codeFieldContainer2 = this.codeFieldContainer;
            if (codeFieldContainer2 != null && codeFieldContainer2.getVisibility() == 0) {
                AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda20(this), 200);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onTransitionAnimationEnd$39() {
        EditTextBoldCursor editTextBoldCursor = this.editTextFirstRow;
        if (editTextBoldCursor != null && editTextBoldCursor.getVisibility() == 0) {
            this.editTextFirstRow.requestFocus();
            AndroidUtilities.showKeyboard(this.editTextFirstRow);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onTransitionAnimationEnd$40() {
        CodeFieldContainer codeFieldContainer2 = this.codeFieldContainer;
        if (codeFieldContainer2 != null && codeFieldContainer2.getVisibility() == 0) {
            this.codeFieldContainer.codeField[0].requestFocus();
        }
    }

    private void loadPasswordInfo() {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda45(this), 10);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPasswordInfo$42(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda36(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPasswordInfo$41(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            this.currentPassword = tLRPC$TL_account_password;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(tLRPC$TL_account_password, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
                return;
            }
            this.waitingForEmail = !TextUtils.isEmpty(this.currentPassword.email_unconfirmed_pattern);
            TwoStepVerificationActivity.initPasswordNewAlgo(this.currentPassword);
            if (!this.paused && this.closeAfterSet) {
                TLRPC$TL_account_password tLRPC$TL_account_password2 = this.currentPassword;
                if (tLRPC$TL_account_password2.has_password) {
                    Object obj = tLRPC$TL_account_password2.current_algo;
                    Object obj2 = tLRPC$TL_account_password2.new_secure_algo;
                    Object obj3 = tLRPC$TL_account_password2.secure_random;
                    String str = tLRPC$TL_account_password2.has_recovery ? "1" : null;
                    String str2 = tLRPC$TL_account_password2.hint;
                    if (str2 == null) {
                        str2 = "";
                    }
                    if (!this.waitingForEmail && obj != null) {
                        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.twoStepPasswordChanged, null, obj, obj2, obj3, str, str2, null, null);
                        finishFragment();
                    }
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
            AnimatorSet animatorSet = new AnimatorSet();
            if (this.floatingButtonContainer.getVisibility() == 0) {
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingProgressView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingProgressView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingProgressView, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.SCALE_Y, new float[]{0.1f})});
            } else {
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.radialProgressView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_Y, new float[]{1.0f})});
            }
            animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animatorSet.start();
        }
    }

    /* access modifiers changed from: protected */
    public void needHideProgress() {
        AnimatorSet animatorSet = new AnimatorSet();
        if (this.floatingButtonContainer.getVisibility() == 0) {
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingProgressView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.floatingProgressView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.floatingProgressView, View.SCALE_Y, new float[]{0.1f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.floatingButtonIcon, View.SCALE_Y, new float[]{1.0f})});
        } else {
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.radialProgressView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_X, new float[]{0.1f}), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_Y, new float[]{0.1f})});
        }
        animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
        animatorSet.start();
    }

    private boolean isValidEmail(String str) {
        if (str == null || str.length() < 3) {
            return false;
        }
        int lastIndexOf = str.lastIndexOf(46);
        int lastIndexOf2 = str.lastIndexOf(64);
        if (lastIndexOf2 < 0 || lastIndexOf < lastIndexOf2) {
            return false;
        }
        return true;
    }

    private void showAlertWithText(String str, String str2) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            builder.setTitle(str);
            builder.setMessage(str2);
            showDialog(builder.create());
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.telegram.tgnet.TLRPC$TL_account_updatePasswordSettings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: org.telegram.tgnet.TLRPC$TL_auth_recoverPassword} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v18, resolved type: org.telegram.tgnet.TLRPC$TL_account_updatePasswordSettings} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v19, resolved type: org.telegram.tgnet.TLRPC$TL_account_updatePasswordSettings} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setNewPassword(boolean r9) {
        /*
            r8 = this;
            if (r9 == 0) goto L_0x0023
            boolean r0 = r8.waitingForEmail
            if (r0 == 0) goto L_0x0023
            org.telegram.tgnet.TLRPC$TL_account_password r0 = r8.currentPassword
            boolean r0 = r0.has_password
            if (r0 == 0) goto L_0x0023
            r8.needShowProgress()
            org.telegram.tgnet.TLRPC$TL_account_cancelPasswordEmail r9 = new org.telegram.tgnet.TLRPC$TL_account_cancelPasswordEmail
            r9.<init>()
            int r0 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda43 r1 = new org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda43
            r1.<init>(r8)
            r0.sendRequest(r9, r1)
            return
        L_0x0023:
            java.lang.String r6 = r8.firstPassword
            org.telegram.tgnet.TLRPC$TL_account_passwordInputSettings r7 = new org.telegram.tgnet.TLRPC$TL_account_passwordInputSettings
            r7.<init>()
            r0 = 2
            java.lang.String r1 = ""
            if (r9 == 0) goto L_0x0058
            int r2 = r8.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            r2.resetSavedPassword()
            r2 = 0
            r8.currentSecret = r2
            boolean r2 = r8.waitingForEmail
            if (r2 == 0) goto L_0x0044
            r7.flags = r0
            r7.email = r1
            goto L_0x0091
        L_0x0044:
            r0 = 3
            r7.flags = r0
            r7.hint = r1
            r0 = 0
            byte[] r0 = new byte[r0]
            r7.new_password_hash = r0
            org.telegram.tgnet.TLRPC$TL_passwordKdfAlgoUnknown r0 = new org.telegram.tgnet.TLRPC$TL_passwordKdfAlgoUnknown
            r0.<init>()
            r7.new_algo = r0
            r7.email = r1
            goto L_0x0091
        L_0x0058:
            java.lang.String r2 = r8.hint
            if (r2 != 0) goto L_0x0064
            org.telegram.tgnet.TLRPC$TL_account_password r2 = r8.currentPassword
            if (r2 == 0) goto L_0x0064
            java.lang.String r2 = r2.hint
            r8.hint = r2
        L_0x0064:
            java.lang.String r2 = r8.hint
            if (r2 != 0) goto L_0x006a
            r8.hint = r1
        L_0x006a:
            if (r6 == 0) goto L_0x007c
            int r1 = r7.flags
            r1 = r1 | 1
            r7.flags = r1
            java.lang.String r1 = r8.hint
            r7.hint = r1
            org.telegram.tgnet.TLRPC$TL_account_password r1 = r8.currentPassword
            org.telegram.tgnet.TLRPC$PasswordKdfAlgo r1 = r1.new_algo
            r7.new_algo = r1
        L_0x007c:
            java.lang.String r1 = r8.email
            int r1 = r1.length()
            if (r1 <= 0) goto L_0x0091
            int r1 = r7.flags
            r0 = r0 | r1
            r7.flags = r0
            java.lang.String r0 = r8.email
            java.lang.String r0 = r0.trim()
            r7.email = r0
        L_0x0091:
            java.lang.String r0 = r8.emailCode
            if (r0 == 0) goto L_0x00a8
            org.telegram.tgnet.TLRPC$TL_auth_recoverPassword r0 = new org.telegram.tgnet.TLRPC$TL_auth_recoverPassword
            r0.<init>()
            java.lang.String r1 = r8.emailCode
            r0.code = r1
            r0.new_settings = r7
            int r1 = r0.flags
            r1 = r1 | 1
            r0.flags = r1
        L_0x00a6:
            r4 = r0
            goto L_0x00c4
        L_0x00a8:
            org.telegram.tgnet.TLRPC$TL_account_updatePasswordSettings r0 = new org.telegram.tgnet.TLRPC$TL_account_updatePasswordSettings
            r0.<init>()
            byte[] r1 = r8.currentPasswordHash
            if (r1 == 0) goto L_0x00ba
            int r1 = r1.length
            if (r1 == 0) goto L_0x00ba
            if (r9 == 0) goto L_0x00c1
            boolean r1 = r8.waitingForEmail
            if (r1 == 0) goto L_0x00c1
        L_0x00ba:
            org.telegram.tgnet.TLRPC$TL_inputCheckPasswordEmpty r1 = new org.telegram.tgnet.TLRPC$TL_inputCheckPasswordEmpty
            r1.<init>()
            r0.password = r1
        L_0x00c1:
            r0.new_settings = r7
            goto L_0x00a6
        L_0x00c4:
            r8.needShowProgress()
            org.telegram.messenger.DispatchQueue r0 = org.telegram.messenger.Utilities.globalQueue
            org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda31 r1 = new org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda31
            r2 = r1
            r3 = r8
            r5 = r9
            r2.<init>(r3, r4, r5, r6, r7)
            r0.postRunnable(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TwoStepVerificationSetupActivity.setNewPassword(boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setNewPassword$44(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda34(this, tLRPC$TL_error));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setNewPassword$43(TLRPC$TL_error tLRPC$TL_error) {
        needHideProgress();
        if (tLRPC$TL_error == null) {
            TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
            TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
            tLRPC$TL_account_password.has_recovery = false;
            tLRPC$TL_account_password.email_unconfirmed_pattern = "";
            twoStepVerificationActivity.setCurrentPasswordParams(tLRPC$TL_account_password, this.currentPasswordHash, this.currentSecretId, this.currentSecret);
            twoStepVerificationActivity.setBlockingAlert(this.otherwiseReloginDays);
            presentFragment(twoStepVerificationActivity, true);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didRemoveTwoStepPassword, new Object[0]);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setNewPassword$50(TLObject tLObject, boolean z, String str, TLRPC$TL_account_passwordInputSettings tLRPC$TL_account_passwordInputSettings) {
        byte[] bArr;
        byte[] bArr2;
        byte[] bArr3;
        TLObject tLObject2 = tLObject;
        TLRPC$TL_account_passwordInputSettings tLRPC$TL_account_passwordInputSettings2 = tLRPC$TL_account_passwordInputSettings;
        if (tLObject2 instanceof TLRPC$TL_account_updatePasswordSettings) {
            TLRPC$TL_account_updatePasswordSettings tLRPC$TL_account_updatePasswordSettings = (TLRPC$TL_account_updatePasswordSettings) tLObject2;
            if (tLRPC$TL_account_updatePasswordSettings.password == null) {
                tLRPC$TL_account_updatePasswordSettings.password = getNewSrpPassword();
            }
        }
        if (z || str == null) {
            bArr2 = null;
            bArr = null;
        } else {
            byte[] stringBytes = AndroidUtilities.getStringBytes(str);
            TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo = this.currentPassword.new_algo;
            if (tLRPC$PasswordKdfAlgo instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                bArr = stringBytes;
                bArr2 = SRPHelper.getX(stringBytes, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo);
            } else {
                bArr = stringBytes;
                bArr2 = null;
            }
        }
        TwoStepVerificationSetupActivity$$ExternalSyntheticLambda49 twoStepVerificationSetupActivity$$ExternalSyntheticLambda49 = new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda49(this, z, bArr2, str, tLRPC$TL_account_passwordInputSettings);
        if (!z) {
            if (!(str == null || (bArr3 = this.currentSecret) == null || bArr3.length != 32)) {
                TLRPC$SecurePasswordKdfAlgo tLRPC$SecurePasswordKdfAlgo = this.currentPassword.new_secure_algo;
                if (tLRPC$SecurePasswordKdfAlgo instanceof TLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
                    TLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 tLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 = (TLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) tLRPC$SecurePasswordKdfAlgo;
                    byte[] computePBKDF2 = Utilities.computePBKDF2(bArr, tLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000.salt);
                    byte[] bArr4 = new byte[32];
                    System.arraycopy(computePBKDF2, 0, bArr4, 0, 32);
                    byte[] bArr5 = new byte[16];
                    System.arraycopy(computePBKDF2, 32, bArr5, 0, 16);
                    byte[] bArr6 = new byte[32];
                    System.arraycopy(this.currentSecret, 0, bArr6, 0, 32);
                    Utilities.aesCbcEncryptionByteArraySafe(bArr6, bArr4, bArr5, 0, 32, 0, 1);
                    TLRPC$TL_secureSecretSettings tLRPC$TL_secureSecretSettings = new TLRPC$TL_secureSecretSettings();
                    tLRPC$TL_account_passwordInputSettings2.new_secure_settings = tLRPC$TL_secureSecretSettings;
                    tLRPC$TL_secureSecretSettings.secure_algo = tLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000;
                    tLRPC$TL_secureSecretSettings.secure_secret = bArr6;
                    tLRPC$TL_secureSecretSettings.secure_secret_id = this.currentSecretId;
                    tLRPC$TL_account_passwordInputSettings2.flags |= 4;
                }
            }
            TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo2 = this.currentPassword.new_algo;
            if (tLRPC$PasswordKdfAlgo2 instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                if (str != null) {
                    byte[] vBytes = SRPHelper.getVBytes(bArr, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo2);
                    tLRPC$TL_account_passwordInputSettings2.new_password_hash = vBytes;
                    if (vBytes == null) {
                        TLRPC$TL_error tLRPC$TL_error = new TLRPC$TL_error();
                        tLRPC$TL_error.text = "ALGO_INVALID";
                        twoStepVerificationSetupActivity$$ExternalSyntheticLambda49.run((TLObject) null, tLRPC$TL_error);
                    }
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLObject2, twoStepVerificationSetupActivity$$ExternalSyntheticLambda49, 10);
                return;
            }
            TLRPC$TL_error tLRPC$TL_error2 = new TLRPC$TL_error();
            tLRPC$TL_error2.text = "PASSWORD_HASH_INVALID";
            twoStepVerificationSetupActivity$$ExternalSyntheticLambda49.run((TLObject) null, tLRPC$TL_error2);
            return;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLObject2, twoStepVerificationSetupActivity$$ExternalSyntheticLambda49, 10);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setNewPassword$49(boolean z, byte[] bArr, String str, TLRPC$TL_account_passwordInputSettings tLRPC$TL_account_passwordInputSettings, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda39(this, tLRPC$TL_error, z, tLObject, bArr, str, tLRPC$TL_account_passwordInputSettings));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: java.lang.Object[]} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$setNewPassword$48(org.telegram.tgnet.TLRPC$TL_error r12, boolean r13, org.telegram.tgnet.TLObject r14, byte[] r15, java.lang.String r16, org.telegram.tgnet.TLRPC$TL_account_passwordInputSettings r17) {
        /*
            r11 = this;
            r0 = r11
            r1 = r12
            r2 = r13
            r3 = r14
            r4 = r15
            r5 = 8
            if (r1 == 0) goto L_0x0027
            java.lang.String r6 = r1.text
            java.lang.String r7 = "SRP_ID_INVALID"
            boolean r6 = r7.equals(r6)
            if (r6 == 0) goto L_0x0027
            org.telegram.tgnet.TLRPC$TL_account_getPassword r1 = new org.telegram.tgnet.TLRPC$TL_account_getPassword
            r1.<init>()
            int r3 = r0.currentAccount
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3)
            org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda48 r4 = new org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda48
            r4.<init>(r11, r13)
            r3.sendRequest(r1, r4, r5)
            return
        L_0x0027:
            r11.needHideProgress()
            r6 = 7
            r7 = 1
            r8 = 0
            if (r1 != 0) goto L_0x015b
            boolean r9 = r3 instanceof org.telegram.tgnet.TLRPC$TL_boolTrue
            if (r9 != 0) goto L_0x0037
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$auth_Authorization
            if (r3 == 0) goto L_0x015b
        L_0x0037:
            org.telegram.messenger.MessagesController r1 = r11.getMessagesController()
            r9 = 0
            java.lang.String r3 = "VALIDATE_PASSWORD"
            r1.removeSuggestion(r9, r3)
            if (r2 == 0) goto L_0x007a
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r0.fragmentsToClose
            int r1 = r1.size()
            r2 = 0
        L_0x004b:
            if (r2 >= r1) goto L_0x005b
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r0.fragmentsToClose
            java.lang.Object r3 = r3.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r3 = (org.telegram.ui.ActionBar.BaseFragment) r3
            r3.removeSelfFromStack()
            int r2 = r2 + 1
            goto L_0x004b
        L_0x005b:
            int r1 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.didRemoveTwoStepPassword
            java.lang.Object[] r3 = new java.lang.Object[r8]
            r1.postNotificationName(r2, r3)
            int r1 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.didSetOrRemoveTwoStepPassword
            java.lang.Object[] r3 = new java.lang.Object[r8]
            r1.postNotificationName(r2, r3)
            r11.finishFragment()
            goto L_0x0265
        L_0x007a:
            android.app.Activity r1 = r11.getParentActivity()
            if (r1 != 0) goto L_0x0081
            return
        L_0x0081:
            org.telegram.tgnet.TLRPC$TL_account_password r1 = r0.currentPassword
            boolean r1 = r1.has_password
            if (r1 == 0) goto L_0x00e2
            org.telegram.ui.ActionBar.AlertDialog$Builder r1 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r2 = r11.getParentActivity()
            r1.<init>((android.content.Context) r2)
            r2 = 2131627140(0x7f0e0CLASSNAME, float:1.8881536E38)
            java.lang.String r3 = "OK"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda5 r3 = new org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda5
            r3.<init>(r11, r15)
            r1.setPositiveButton(r2, r3)
            if (r16 != 0) goto L_0x00b8
            org.telegram.tgnet.TLRPC$TL_account_password r2 = r0.currentPassword
            if (r2 == 0) goto L_0x00b8
            boolean r2 = r2.has_password
            if (r2 == 0) goto L_0x00b8
            r2 = 2131629377(0x7f0e1541, float:1.8886073E38)
            java.lang.String r3 = "YourEmailSuccessText"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setMessage(r2)
            goto L_0x00c4
        L_0x00b8:
            r2 = 2131629383(0x7f0e1547, float:1.8886085E38)
            java.lang.String r3 = "YourPasswordChangedSuccessText"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setMessage(r2)
        L_0x00c4:
            r2 = 2131629389(0x7f0e154d, float:1.8886098E38)
            java.lang.String r3 = "YourPasswordSuccess"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setTitle(r2)
            org.telegram.ui.ActionBar.AlertDialog r1 = r1.create()
            android.app.Dialog r1 = r11.showDialog(r1)
            if (r1 == 0) goto L_0x0265
            r1.setCanceledOnTouchOutside(r8)
            r1.setCancelable(r8)
            goto L_0x0265
        L_0x00e2:
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r0.fragmentsToClose
            int r1 = r1.size()
            r2 = 0
        L_0x00e9:
            if (r2 >= r1) goto L_0x00f9
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r0.fragmentsToClose
            java.lang.Object r3 = r3.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r3 = (org.telegram.ui.ActionBar.BaseFragment) r3
            r3.removeSelfFromStack()
            int r2 = r2 + 1
            goto L_0x00e9
        L_0x00f9:
            org.telegram.tgnet.TLRPC$TL_account_password r1 = r0.currentPassword
            r1.has_password = r7
            boolean r2 = r1.has_recovery
            if (r2 != 0) goto L_0x010a
            java.lang.String r2 = r1.email_unconfirmed_pattern
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            r2 = r2 ^ r7
            r1.has_recovery = r2
        L_0x010a:
            boolean r1 = r0.closeAfterSet
            if (r1 == 0) goto L_0x011b
            int r1 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.twoStepPasswordChanged
            java.lang.Object[] r3 = new java.lang.Object[r8]
            r1.postNotificationName(r2, r3)
        L_0x011b:
            org.telegram.ui.TwoStepVerificationSetupActivity r1 = new org.telegram.ui.TwoStepVerificationSetupActivity
            org.telegram.tgnet.TLRPC$TL_account_password r2 = r0.currentPassword
            r1.<init>(r6, r2)
            boolean r2 = r0.fromRegistration
            r1.fromRegistration = r2
            if (r4 == 0) goto L_0x012a
            r2 = r4
            goto L_0x012c
        L_0x012a:
            byte[] r2 = r0.currentPasswordHash
        L_0x012c:
            long r3 = r0.currentSecretId
            byte[] r5 = r0.currentSecret
            boolean r6 = r0.emailOnly
            r12 = r1
            r13 = r2
            r14 = r3
            r16 = r5
            r17 = r6
            r12.setCurrentPasswordParams(r13, r14, r16, r17)
            boolean r2 = r0.closeAfterSet
            r1.closeAfterSet = r2
            int r2 = r0.otherwiseReloginDays
            r1.setBlockingAlert(r2)
            r11.presentFragment(r1, r7)
            int r1 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.didSetOrRemoveTwoStepPassword
            java.lang.Object[] r3 = new java.lang.Object[r7]
            org.telegram.tgnet.TLRPC$TL_account_password r4 = r0.currentPassword
            r3[r8] = r4
            r1.postNotificationName(r2, r3)
            goto L_0x0265
        L_0x015b:
            if (r1 == 0) goto L_0x0265
            java.lang.String r2 = r1.text
            java.lang.String r3 = "EMAIL_UNCONFIRMED"
            boolean r2 = r3.equals(r2)
            if (r2 != 0) goto L_0x01de
            java.lang.String r2 = r1.text
            java.lang.String r3 = "EMAIL_UNCONFIRMED_"
            boolean r2 = r2.startsWith(r3)
            if (r2 == 0) goto L_0x0172
            goto L_0x01de
        L_0x0172:
            java.lang.String r2 = r1.text
            java.lang.String r3 = "EMAIL_INVALID"
            boolean r2 = r3.equals(r2)
            r3 = 2131624390(0x7f0e01c6, float:1.8875958E38)
            java.lang.String r4 = "AppName"
            if (r2 == 0) goto L_0x0193
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2 = 2131627420(0x7f0e0d9c, float:1.8882104E38)
            java.lang.String r3 = "PasswordEmailInvalid"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r11.showAlertWithText(r1, r2)
            goto L_0x0265
        L_0x0193:
            java.lang.String r2 = r1.text
            java.lang.String r5 = "FLOOD_WAIT"
            boolean r2 = r2.startsWith(r5)
            if (r2 == 0) goto L_0x01d3
            java.lang.String r1 = r1.text
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r1)
            int r1 = r1.intValue()
            r2 = 60
            if (r1 >= r2) goto L_0x01b4
            java.lang.Object[] r2 = new java.lang.Object[r8]
            java.lang.String r5 = "Seconds"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r5, r1, r2)
            goto L_0x01bd
        L_0x01b4:
            int r1 = r1 / r2
            java.lang.Object[] r2 = new java.lang.Object[r8]
            java.lang.String r5 = "Minutes"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r5, r1, r2)
        L_0x01bd:
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r3 = 2131625963(0x7f0e07eb, float:1.8879149E38)
            java.lang.Object[] r4 = new java.lang.Object[r7]
            r4[r8] = r1
            java.lang.String r1 = "FloodWaitTime"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            r11.showAlertWithText(r2, r1)
            goto L_0x0265
        L_0x01d3:
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r3)
            java.lang.String r1 = r1.text
            r11.showAlertWithText(r2, r1)
            goto L_0x0265
        L_0x01de:
            int r1 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.twoStepPasswordChanged
            java.lang.Object[] r3 = new java.lang.Object[r8]
            r1.postNotificationName(r2, r3)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r0.fragmentsToClose
            int r1 = r1.size()
            r2 = 0
        L_0x01f2:
            if (r2 >= r1) goto L_0x0202
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r0.fragmentsToClose
            java.lang.Object r3 = r3.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r3 = (org.telegram.ui.ActionBar.BaseFragment) r3
            r3.removeSelfFromStack()
            int r2 = r2 + 1
            goto L_0x01f2
        L_0x0202:
            int r1 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.twoStepPasswordChanged
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r8] = r4
            r5 = r17
            org.telegram.tgnet.TLRPC$PasswordKdfAlgo r5 = r5.new_algo
            r3[r7] = r5
            r5 = 2
            org.telegram.tgnet.TLRPC$TL_account_password r8 = r0.currentPassword
            org.telegram.tgnet.TLRPC$SecurePasswordKdfAlgo r9 = r8.new_secure_algo
            r3[r5] = r9
            r5 = 3
            byte[] r8 = r8.secure_random
            r3[r5] = r8
            r5 = 4
            java.lang.String r8 = r0.email
            r3[r5] = r8
            java.lang.String r5 = r0.hint
            r9 = 5
            r3[r9] = r5
            r5 = 6
            r3[r5] = r8
            java.lang.String r5 = r0.firstPassword
            r3[r6] = r5
            r1.postNotificationName(r2, r3)
            org.telegram.tgnet.TLRPC$TL_account_password r1 = r0.currentPassword
            java.lang.String r2 = r0.email
            r1.email_unconfirmed_pattern = r2
            org.telegram.ui.TwoStepVerificationSetupActivity r2 = new org.telegram.ui.TwoStepVerificationSetupActivity
            r2.<init>(r9, r1)
            boolean r1 = r0.fromRegistration
            r2.fromRegistration = r1
            if (r4 == 0) goto L_0x0247
            r1 = r4
            goto L_0x0249
        L_0x0247:
            byte[] r1 = r0.currentPasswordHash
        L_0x0249:
            long r3 = r0.currentSecretId
            byte[] r5 = r0.currentSecret
            boolean r6 = r0.emailOnly
            r12 = r2
            r13 = r1
            r14 = r3
            r16 = r5
            r17 = r6
            r12.setCurrentPasswordParams(r13, r14, r16, r17)
            boolean r1 = r0.closeAfterSet
            r2.closeAfterSet = r1
            int r1 = r0.otherwiseReloginDays
            r2.setBlockingAlert(r1)
            r11.presentFragment(r2, r7)
        L_0x0265:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TwoStepVerificationSetupActivity.lambda$setNewPassword$48(org.telegram.tgnet.TLRPC$TL_error, boolean, org.telegram.tgnet.TLObject, byte[], java.lang.String, org.telegram.tgnet.TLRPC$TL_account_passwordInputSettings):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setNewPassword$46(boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda38(this, tLRPC$TL_error, tLObject, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setNewPassword$45(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, boolean z) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            this.currentPassword = tLRPC$TL_account_password;
            TwoStepVerificationActivity.initPasswordNewAlgo(tLRPC$TL_account_password);
            setNewPassword(z);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setNewPassword$47(byte[] bArr, DialogInterface dialogInterface, int i) {
        int size = this.fragmentsToClose.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.fragmentsToClose.get(i2).removeSelfFromStack();
        }
        TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
        TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
        tLRPC$TL_account_password.has_password = true;
        if (!tLRPC$TL_account_password.has_recovery) {
            tLRPC$TL_account_password.has_recovery = !TextUtils.isEmpty(tLRPC$TL_account_password.email_unconfirmed_pattern);
        }
        TLRPC$TL_account_password tLRPC$TL_account_password2 = this.currentPassword;
        if (bArr == null) {
            bArr = this.currentPasswordHash;
        }
        twoStepVerificationActivity.setCurrentPasswordParams(tLRPC$TL_account_password2, bArr, this.currentSecretId, this.currentSecret);
        twoStepVerificationActivity.setBlockingAlert(this.otherwiseReloginDays);
        presentFragment(twoStepVerificationActivity, true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
    }

    /* access modifiers changed from: protected */
    public TLRPC$TL_inputCheckPasswordSRP getNewSrpPassword() {
        TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
        TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo = tLRPC$TL_account_password.current_algo;
        if (!(tLRPC$PasswordKdfAlgo instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow)) {
            return null;
        }
        return SRPHelper.startCheck(this.currentPasswordHash, tLRPC$TL_account_password.srp_id, tLRPC$TL_account_password.srp_B, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo);
    }

    private void onFieldError(View view, TextView textView, boolean z) {
        if (getParentActivity() != null) {
            try {
                textView.performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            if (z) {
                textView.setText("");
            }
            AndroidUtilities.shakeViewSpring(view, 5.0f);
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(this.editTextFirstRow, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.editTextFirstRow, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.editTextFirstRow, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        arrayList.add(new ThemeDescription(this.editTextFirstRow, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        return arrayList;
    }

    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        if (this.otherwiseReloginDays < 0 || this.parentLayout.fragmentsStack.size() != 1) {
            return super.isSwipeBackEnabled(motionEvent);
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

    public void finishFragment(boolean z) {
        Iterator<BaseFragment> it = getParentLayout().fragmentsStack.iterator();
        while (it.hasNext()) {
            BaseFragment next = it.next();
            if (next != this && (next instanceof TwoStepVerificationSetupActivity)) {
                ((TwoStepVerificationSetupActivity) next).floatingAutoAnimator.ignoreNextLayout();
            }
        }
        super.finishFragment(z);
    }

    /* access modifiers changed from: private */
    public void showSetForcePasswordAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("Warning", NUM));
        builder.setMessage(LocaleController.formatPluralString("ForceSetPasswordAlertMessageShort", this.otherwiseReloginDays, new Object[0]));
        builder.setPositiveButton(LocaleController.getString("TwoStepVerificationSetPassword", NUM), (DialogInterface.OnClickListener) null);
        builder.setNegativeButton(LocaleController.getString("ForceSetPasswordCancel", NUM), new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda0(this));
        ((TextView) builder.show().getButton(-2)).setTextColor(Theme.getColor("dialogTextRed2"));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showSetForcePasswordAlert$51(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    public void setBlockingAlert(int i) {
        this.otherwiseReloginDays = i;
    }

    public void finishFragment() {
        if (this.otherwiseReloginDays < 0 || this.parentLayout.fragmentsStack.size() != 1) {
            super.finishFragment();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("afterSignup", true);
        presentFragment(new DialogsActivity(bundle), true);
    }

    public boolean isLightStatusBar() {
        if (ColorUtils.calculateLuminance(Theme.getColor("windowBackgroundWhite", (boolean[]) null, true)) > 0.699999988079071d) {
            return true;
        }
        return false;
    }
}

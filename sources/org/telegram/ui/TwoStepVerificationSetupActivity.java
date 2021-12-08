package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Property;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
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
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;

public class TwoStepVerificationSetupActivity extends BaseFragment {
    public static final int TYPE_EMAIL_CONFIRM = 5;
    public static final int TYPE_EMAIL_RECOVERY = 4;
    public static final int TYPE_ENTER_EMAIL = 3;
    public static final int TYPE_ENTER_FIRST = 0;
    public static final int TYPE_ENTER_HINT = 2;
    public static final int TYPE_ENTER_SECOND = 1;
    public static final int TYPE_INTRO = 6;
    public static final int TYPE_PASSWORD_SET = 7;
    public static final int TYPE_VERIFY = 8;
    public static final int TYPE_VERIFY_OK = 9;
    private static final int item_abort = 1;
    private static final int item_resend = 2;
    /* access modifiers changed from: private */
    public AnimatorSet actionBarAnimator;
    /* access modifiers changed from: private */
    public View actionBarBackground;
    /* access modifiers changed from: private */
    public RLottieDrawable[] animationDrawables;
    /* access modifiers changed from: private */
    public AnimatorSet buttonAnimation;
    /* access modifiers changed from: private */
    public TextView buttonTextView;
    private boolean closeAfterSet;
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
    private String email;
    private String emailCode;
    /* access modifiers changed from: private */
    public int emailCodeLength = 6;
    private boolean emailOnly;
    private Runnable finishCallback = new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda5(this);
    private String firstPassword;
    private ArrayList<BaseFragment> fragmentsToClose = new ArrayList<>();
    private String hint;
    /* access modifiers changed from: private */
    public boolean ignoreTextChange;
    /* access modifiers changed from: private */
    public RLottieImageView imageView;
    /* access modifiers changed from: private */
    public int otherwiseReloginDays = -1;
    /* access modifiers changed from: private */
    public EditTextBoldCursor passwordEditText;
    private boolean paused;
    private AlertDialog progressDialog;
    /* access modifiers changed from: private */
    public ScrollView scrollView;
    private Runnable setAnimationRunnable;
    private ImageView showPasswordButton;
    /* access modifiers changed from: private */
    public TextView titleTextView;
    /* access modifiers changed from: private */
    public TextView topButton;
    private boolean waitingForEmail;

    /* renamed from: lambda$new$0$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4030lambda$new$0$orgtelegramuiTwoStepVerificationSetupActivity() {
        EditTextBoldCursor editTextBoldCursor = this.passwordEditText;
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
        this.actionBar.setBackgroundDrawable((Drawable) null);
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setTitleColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteGrayText2"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarWhiteSelector"), false);
        this.actionBar.setCastShadows(false);
        this.actionBar.setAddToContainer(false);
        if (!AndroidUtilities.isTablet()) {
            this.actionBar.showActionModeTop();
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                String text;
                if (id == -1) {
                    if (TwoStepVerificationSetupActivity.this.otherwiseReloginDays < 0 || TwoStepVerificationSetupActivity.this.parentLayout.fragmentsStack.size() != 1) {
                        TwoStepVerificationSetupActivity.this.finishFragment();
                    } else {
                        TwoStepVerificationSetupActivity.this.showSetForcePasswordAlert();
                    }
                } else if (id == 2) {
                    ConnectionsManager.getInstance(TwoStepVerificationSetupActivity.this.currentAccount).sendRequest(new TLRPC.TL_account_resendPasswordEmail(), TwoStepVerificationSetupActivity$1$$ExternalSyntheticLambda1.INSTANCE);
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) TwoStepVerificationSetupActivity.this.getParentActivity());
                    builder.setMessage(LocaleController.getString("ResendCodeInfo", NUM));
                    builder.setTitle(LocaleController.getString("TwoStepVerificationTitle", NUM));
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                    TwoStepVerificationSetupActivity.this.showDialog(builder.create());
                } else if (id == 1) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) TwoStepVerificationSetupActivity.this.getParentActivity());
                    if (TwoStepVerificationSetupActivity.this.currentPassword == null || !TwoStepVerificationSetupActivity.this.currentPassword.has_password) {
                        text = LocaleController.getString("CancelPasswordQuestion", NUM);
                    } else {
                        text = LocaleController.getString("CancelEmailQuestion", NUM);
                    }
                    String title = LocaleController.getString("CancelEmailQuestionTitle", NUM);
                    String buttonText = LocaleController.getString("Abort", NUM);
                    builder2.setMessage(text);
                    builder2.setTitle(title);
                    builder2.setPositiveButton(buttonText, new TwoStepVerificationSetupActivity$1$$ExternalSyntheticLambda0(this));
                    builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    AlertDialog alertDialog = builder2.create();
                    TwoStepVerificationSetupActivity.this.showDialog(alertDialog);
                    TextView button = (TextView) alertDialog.getButton(-1);
                    if (button != null) {
                        button.setTextColor(Theme.getColor("dialogTextRed2"));
                    }
                }
            }

            static /* synthetic */ void lambda$onItemClick$0(TLObject response, TLRPC.TL_error error) {
            }

            /* renamed from: lambda$onItemClick$1$org-telegram-ui-TwoStepVerificationSetupActivity$1  reason: not valid java name */
            public /* synthetic */ void m4042xced081da(DialogInterface dialogInterface, int i) {
                TwoStepVerificationSetupActivity.this.setNewPassword(true);
            }
        });
        if (this.currentType == 5) {
            ActionBarMenuItem item = this.actionBar.createMenu().addItem(0, NUM);
            item.addSubItem(2, LocaleController.getString("ResendCode", NUM));
            item.addSubItem(1, LocaleController.getString("AbortPasswordMenu", NUM));
        }
        TextView textView = new TextView(context2);
        this.topButton = textView;
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText2"));
        this.topButton.setTextSize(1, 14.0f);
        this.topButton.setGravity(16);
        this.topButton.setVisibility(8);
        this.actionBar.addView(this.topButton, LayoutHelper.createFrame(-2, -1.0f, 53, 0.0f, 0.0f, 22.0f, 0.0f));
        this.topButton.setOnClickListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda1(this));
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.imageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        TextView textView2 = new TextView(context2);
        this.titleTextView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.titleTextView.setGravity(1);
        this.titleTextView.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.titleTextView.setTextSize(1, 24.0f);
        TextView textView3 = new TextView(context2);
        this.descriptionText = textView3;
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.descriptionText.setGravity(1);
        this.descriptionText.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        this.descriptionText.setTextSize(1, 15.0f);
        this.descriptionText.setVisibility(8);
        this.descriptionText.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        TextView textView4 = new TextView(context2);
        this.descriptionText2 = textView4;
        textView4.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.descriptionText2.setGravity(1);
        this.descriptionText2.setTextSize(1, 14.0f);
        this.descriptionText2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        this.descriptionText2.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
        this.descriptionText2.setVisibility(8);
        this.descriptionText2.setOnClickListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda2(this));
        TextView textView5 = new TextView(context2);
        this.buttonTextView = textView5;
        textView5.setMinWidth(AndroidUtilities.dp(220.0f));
        this.buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        this.buttonTextView.setOnClickListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda35(this));
        switch (this.currentType) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 8:
                ViewGroup container = new ViewGroup(context2) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        int width = View.MeasureSpec.getSize(widthMeasureSpec);
                        int height = View.MeasureSpec.getSize(heightMeasureSpec);
                        if (TwoStepVerificationSetupActivity.this.topButton != null) {
                            ((FrameLayout.LayoutParams) TwoStepVerificationSetupActivity.this.topButton.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
                        }
                        TwoStepVerificationSetupActivity.this.actionBar.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), heightMeasureSpec);
                        TwoStepVerificationSetupActivity.this.actionBarBackground.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(TwoStepVerificationSetupActivity.this.actionBar.getMeasuredHeight() + AndroidUtilities.dp(3.0f), NUM));
                        TwoStepVerificationSetupActivity.this.scrollView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), heightMeasureSpec);
                        setMeasuredDimension(width, height);
                    }

                    /* access modifiers changed from: protected */
                    public void onLayout(boolean changed, int l, int t, int r, int b) {
                        TwoStepVerificationSetupActivity.this.actionBar.layout(0, 0, TwoStepVerificationSetupActivity.this.actionBar.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.actionBar.getMeasuredHeight());
                        TwoStepVerificationSetupActivity.this.actionBarBackground.layout(0, 0, TwoStepVerificationSetupActivity.this.actionBarBackground.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.actionBarBackground.getMeasuredHeight());
                        TwoStepVerificationSetupActivity.this.scrollView.layout(0, 0, TwoStepVerificationSetupActivity.this.scrollView.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.scrollView.getMeasuredHeight());
                    }
                };
                AnonymousClass4 r15 = new ScrollView(context2) {
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
                                AnimatorSet access$2300 = TwoStepVerificationSetupActivity.this.actionBarAnimator;
                                Animator[] animatorArr = new Animator[2];
                                View access$1700 = TwoStepVerificationSetupActivity.this.actionBarBackground;
                                Property property = View.ALPHA;
                                float[] fArr = new float[1];
                                float f = 1.0f;
                                fArr[0] = show ? 1.0f : 0.0f;
                                animatorArr[0] = ObjectAnimator.ofFloat(access$1700, property, fArr);
                                SimpleTextView titleTextView = TwoStepVerificationSetupActivity.this.actionBar.getTitleTextView();
                                Property property2 = View.ALPHA;
                                float[] fArr2 = new float[1];
                                if (!show) {
                                    f = 0.0f;
                                }
                                fArr2[0] = f;
                                animatorArr[1] = ObjectAnimator.ofFloat(titleTextView, property2, fArr2);
                                access$2300.playTogether(animatorArr);
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
                this.scrollView = r15;
                r15.setVerticalScrollBarEnabled(false);
                container.addView(this.scrollView);
                LinearLayout scrollViewLinearLayout = new LinearLayout(context2);
                scrollViewLinearLayout.setOrientation(1);
                this.scrollView.addView(scrollViewLinearLayout, LayoutHelper.createScroll(-1, -1, 51));
                scrollViewLinearLayout.addView(this.imageView, LayoutHelper.createLinear(-2, -2, 49, 0, 69, 0, 0));
                scrollViewLinearLayout.addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 8, 0, 0));
                scrollViewLinearLayout.addView(this.descriptionText, LayoutHelper.createLinear(-2, -2, 49, 0, 9, 0, 0));
                FrameLayout frameLayout = new FrameLayout(context2);
                scrollViewLinearLayout.addView(frameLayout, LayoutHelper.createLinear(220, 36, 49, 40, 32, 40, 0));
                EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
                this.passwordEditText = editTextBoldCursor;
                editTextBoldCursor.setTextSize(1, 17.0f);
                this.passwordEditText.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
                this.passwordEditText.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
                this.passwordEditText.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                this.passwordEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                this.passwordEditText.setHintColor(Theme.getColor("windowBackgroundWhiteHintText"));
                this.passwordEditText.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
                this.passwordEditText.setMaxLines(1);
                this.passwordEditText.setLines(1);
                this.passwordEditText.setGravity(3);
                this.passwordEditText.setCursorSize(AndroidUtilities.dp(20.0f));
                this.passwordEditText.setSingleLine(true);
                this.passwordEditText.setCursorWidth(1.5f);
                frameLayout.addView(this.passwordEditText, LayoutHelper.createFrame(220, 36, 49));
                this.passwordEditText.setOnEditorActionListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda4(this));
                this.passwordEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    public void onDestroyActionMode(ActionMode mode) {
                    }

                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        return false;
                    }
                });
                AnonymousClass6 r4 = new ImageView(context2) {
                    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                        super.onInitializeAccessibilityNodeInfo(info);
                        boolean z = true;
                        info.setCheckable(true);
                        if (TwoStepVerificationSetupActivity.this.passwordEditText.getTransformationMethod() != null) {
                            z = false;
                        }
                        info.setChecked(z);
                    }
                };
                this.showPasswordButton = r4;
                r4.setImageResource(NUM);
                this.showPasswordButton.setScaleType(ImageView.ScaleType.CENTER);
                this.showPasswordButton.setContentDescription(LocaleController.getString("TwoStepVerificationShowPassword", NUM));
                if (Build.VERSION.SDK_INT >= 21) {
                    this.showPasswordButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
                }
                this.showPasswordButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
                this.showPasswordButton.setVisibility(8);
                frameLayout.addView(this.showPasswordButton, LayoutHelper.createFrame(36, 36.0f, 53, 0.0f, -5.0f, 0.0f, 0.0f));
                this.showPasswordButton.setOnClickListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda36(this));
                FrameLayout frameLayout2 = new FrameLayout(context2);
                scrollViewLinearLayout.addView(frameLayout2, LayoutHelper.createLinear(-1, -2, 51, 0, 36, 0, 22));
                frameLayout2.addView(this.buttonTextView, LayoutHelper.createFrame(-2, 42, 49));
                frameLayout2.addView(this.descriptionText2, LayoutHelper.createFrame(-2, -2, 49));
                if (this.currentType == 4) {
                    TextView textView6 = new TextView(context2);
                    this.descriptionText3 = textView6;
                    textView6.setTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
                    this.descriptionText3.setGravity(1);
                    this.descriptionText3.setTextSize(1, 14.0f);
                    this.descriptionText3.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
                    this.descriptionText3.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
                    this.descriptionText3.setText(LocaleController.getString("RestoreEmailTroubleNoEmail", NUM));
                    scrollViewLinearLayout.addView(this.descriptionText3, LayoutHelper.createLinear(-2, -2, 49, 0, 0, 0, 25));
                    this.descriptionText3.setOnClickListener(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda37(this));
                }
                this.fragmentView = container;
                AnonymousClass7 r5 = new View(context2) {
                    private Paint paint = new Paint();

                    /* access modifiers changed from: protected */
                    public void onDraw(Canvas canvas) {
                        this.paint.setColor(Theme.getColor("windowBackgroundWhite"));
                        int h = getMeasuredHeight() - AndroidUtilities.dp(3.0f);
                        canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) h, this.paint);
                        TwoStepVerificationSetupActivity.this.parentLayout.drawHeaderShadow(canvas, h);
                    }
                };
                this.actionBarBackground = r5;
                r5.setAlpha(0.0f);
                container.addView(this.actionBarBackground);
                container.addView(this.actionBar);
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
                            TwoStepVerificationSetupActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) height) * 0.399f), NUM));
                            TwoStepVerificationSetupActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(height, 0));
                            TwoStepVerificationSetupActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(height, 0));
                            TwoStepVerificationSetupActivity.this.descriptionText2.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(height, 0));
                            TwoStepVerificationSetupActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
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
                        int y5 = (int) (((float) height) * 0.148f);
                        TwoStepVerificationSetupActivity.this.imageView.layout(0, y5, TwoStepVerificationSetupActivity.this.imageView.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.imageView.getMeasuredHeight() + y5);
                        int y6 = (int) (((float) height) * 0.458f);
                        TwoStepVerificationSetupActivity.this.titleTextView.layout(0, y6, TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredHeight() + y6);
                        int y7 = y6 + TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredHeight() + AndroidUtilities.dp(12.0f);
                        TwoStepVerificationSetupActivity.this.descriptionText.layout(0, y7, TwoStepVerificationSetupActivity.this.descriptionText.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.descriptionText.getMeasuredHeight() + y7);
                        int x4 = (width - TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                        int y8 = (int) (((float) height) * 0.791f);
                        TwoStepVerificationSetupActivity.this.buttonTextView.layout(x4, y8, TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredWidth() + x4, TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredHeight() + y8);
                    }
                };
                container2.setOnTouchListener(TwoStepVerificationSetupActivity$$ExternalSyntheticLambda3.INSTANCE);
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
                if (this.currentPassword.has_password) {
                    this.actionBar.setTitle(LocaleController.getString("PleaseEnterNewFirstPassword", NUM));
                    this.titleTextView.setText(LocaleController.getString("PleaseEnterNewFirstPassword", NUM));
                } else {
                    this.actionBar.setTitle(LocaleController.getString("PleaseEnterFirstPassword", NUM));
                    this.titleTextView.setText(LocaleController.getString("PleaseEnterFirstPassword", NUM));
                }
                if (!TextUtils.isEmpty(this.emailCode)) {
                    this.topButton.setVisibility(0);
                    this.topButton.setText(LocaleController.getString("YourEmailSkip", NUM));
                }
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                this.buttonTextView.setText(LocaleController.getString("Continue", NUM));
                this.passwordEditText.setHint(LocaleController.getString("LoginPassword", NUM));
                this.passwordEditText.setImeOptions(NUM);
                this.passwordEditText.setInputType(129);
                this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.passwordEditText.setTypeface(Typeface.DEFAULT);
                this.showPasswordButton.setVisibility(0);
                this.passwordEditText.setPadding(0, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(36.0f), 0);
                RLottieDrawable[] rLottieDrawableArr = new RLottieDrawable[6];
                this.animationDrawables = rLottieDrawableArr;
                rLottieDrawableArr[0] = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, (int[]) null);
                this.animationDrawables[1] = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, (int[]) null);
                this.animationDrawables[2] = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, (int[]) null);
                this.animationDrawables[3] = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, (int[]) null);
                this.animationDrawables[4] = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, (int[]) null);
                this.animationDrawables[5] = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, (int[]) null);
                this.animationDrawables[2].setOnFinishCallback(this.finishCallback, 97);
                setRandomMonkeyIdleAnimation(true);
                break;
            case 1:
                this.actionBar.setTitle(LocaleController.getString("PleaseReEnterPassword", NUM));
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                this.titleTextView.setText(LocaleController.getString("PleaseReEnterPassword", NUM));
                this.buttonTextView.setText(LocaleController.getString("Continue", NUM));
                this.passwordEditText.setHint(LocaleController.getString("LoginPassword", NUM));
                this.passwordEditText.setImeOptions(NUM);
                this.passwordEditText.setInputType(129);
                this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.passwordEditText.setTypeface(Typeface.DEFAULT);
                this.showPasswordButton.setVisibility(0);
                this.passwordEditText.setPadding(0, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(36.0f), 0);
                RLottieDrawable[] rLottieDrawableArr2 = new RLottieDrawable[1];
                this.animationDrawables = rLottieDrawableArr2;
                rLottieDrawableArr2[0] = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), true, (int[]) null);
                this.animationDrawables[0].setPlayInDirectionOfCustomEndFrame(true);
                this.animationDrawables[0].setCustomEndFrame(19);
                this.imageView.setAnimation(this.animationDrawables[0]);
                this.imageView.playAnimation();
                break;
            case 2:
                this.actionBar.setTitle(LocaleController.getString("PasswordHint", NUM));
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                this.topButton.setVisibility(0);
                this.topButton.setText(LocaleController.getString("YourEmailSkip", NUM));
                this.titleTextView.setText(LocaleController.getString("PasswordHint", NUM));
                this.buttonTextView.setText(LocaleController.getString("Continue", NUM));
                this.passwordEditText.setHint(LocaleController.getString("PasswordHintPlaceholder", NUM));
                this.passwordEditText.setImeOptions(NUM);
                this.imageView.setAnimation(NUM, 120, 120);
                this.imageView.playAnimation();
                break;
            case 3:
                this.actionBar.setTitle(LocaleController.getString("RecoveryEmailTitle", NUM));
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                if (!this.emailOnly) {
                    this.topButton.setVisibility(0);
                    this.topButton.setText(LocaleController.getString("YourEmailSkip", NUM));
                }
                this.titleTextView.setText(LocaleController.getString("RecoveryEmailTitle", NUM));
                this.buttonTextView.setText(LocaleController.getString("Continue", NUM));
                this.passwordEditText.setHint(LocaleController.getString("PaymentShippingEmailPlaceholder", NUM));
                this.passwordEditText.setImeOptions(NUM);
                this.passwordEditText.setInputType(33);
                this.imageView.setAnimation(NUM, 120, 120);
                this.imageView.playAnimation();
                break;
            case 4:
                this.actionBar.setTitle(LocaleController.getString("PasswordRecovery", NUM));
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                this.titleTextView.setText(LocaleController.getString("PasswordRecovery", NUM));
                this.buttonTextView.setText(LocaleController.getString("Continue", NUM));
                this.passwordEditText.setHint(LocaleController.getString("EnterCode", NUM));
                this.passwordEditText.setInputType(3);
                this.passwordEditText.setImeOptions(NUM);
                TextView textView7 = this.descriptionText2;
                Object[] objArr = new Object[1];
                objArr[0] = this.currentPassword.email_unconfirmed_pattern != null ? this.currentPassword.email_unconfirmed_pattern : "";
                textView7.setText(LocaleController.formatString("RestoreEmailSent", NUM, objArr));
                this.descriptionText2.setVisibility(0);
                this.buttonTextView.setVisibility(4);
                this.buttonTextView.setAlpha(0.0f);
                this.buttonTextView.setScaleX(0.9f);
                this.buttonTextView.setScaleY(0.9f);
                this.imageView.setAnimation(NUM, 120, 120);
                this.imageView.playAnimation();
                break;
            case 5:
                this.actionBar.setTitle(LocaleController.getString("VerificationCode", NUM));
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                this.titleTextView.setText(LocaleController.getString("VerificationCode", NUM));
                this.buttonTextView.setText(LocaleController.getString("Continue", NUM));
                this.passwordEditText.setHint(LocaleController.getString("EnterCode", NUM));
                this.passwordEditText.setInputType(3);
                this.passwordEditText.setImeOptions(NUM);
                TextView textView8 = this.descriptionText2;
                Object[] objArr2 = new Object[1];
                objArr2[0] = this.currentPassword.email_unconfirmed_pattern != null ? this.currentPassword.email_unconfirmed_pattern : "";
                textView8.setText(LocaleController.formatString("EmailPasswordConfirmText2", NUM, objArr2));
                this.descriptionText2.setVisibility(0);
                this.buttonTextView.setVisibility(4);
                this.buttonTextView.setAlpha(0.0f);
                this.buttonTextView.setScaleX(0.9f);
                this.buttonTextView.setScaleY(0.9f);
                this.imageView.setAnimation(NUM, 120, 120);
                this.imageView.playAnimation();
                break;
            case 6:
                this.titleTextView.setText(LocaleController.getString("TwoStepVerificationTitle", NUM));
                this.descriptionText.setText(LocaleController.getString("SetAdditionalPasswordInfo", NUM));
                this.buttonTextView.setText(LocaleController.getString("TwoStepVerificationSetPassword", NUM));
                this.descriptionText.setVisibility(0);
                this.imageView.setAnimation(NUM, 120, 120);
                this.imageView.playAnimation();
                break;
            case 7:
                this.titleTextView.setText(LocaleController.getString("TwoStepVerificationPasswordSet", NUM));
                this.descriptionText.setText(LocaleController.getString("TwoStepVerificationPasswordSetInfo", NUM));
                if (this.closeAfterSet) {
                    this.buttonTextView.setText(LocaleController.getString("TwoStepVerificationPasswordReturnPassport", NUM));
                } else {
                    this.buttonTextView.setText(LocaleController.getString("TwoStepVerificationPasswordReturnSettings", NUM));
                }
                this.descriptionText.setVisibility(0);
                this.imageView.setAnimation(NUM, 120, 120);
                this.imageView.playAnimation();
                break;
            case 8:
                this.actionBar.setTitle(LocaleController.getString("PleaseEnterCurrentPassword", NUM));
                this.titleTextView.setText(LocaleController.getString("PleaseEnterCurrentPassword", NUM));
                this.descriptionText.setText(LocaleController.getString("CheckPasswordInfo", NUM));
                this.descriptionText.setVisibility(0);
                this.actionBar.getTitleTextView().setAlpha(0.0f);
                this.buttonTextView.setText(LocaleController.getString("CheckPassword", NUM));
                this.descriptionText2.setText(LocaleController.getString("ForgotPassword", NUM));
                this.descriptionText2.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText2"));
                this.passwordEditText.setHint(LocaleController.getString("LoginPassword", NUM));
                this.passwordEditText.setImeOptions(NUM);
                this.passwordEditText.setInputType(129);
                this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.passwordEditText.setTypeface(Typeface.DEFAULT);
                this.passwordEditText.setPadding(0, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(36.0f), 0);
                this.imageView.setAnimation(NUM, 120, 120);
                this.imageView.playAnimation();
                break;
            case 9:
                this.titleTextView.setText(LocaleController.getString("CheckPasswordPerfect", NUM));
                this.descriptionText.setText(LocaleController.getString("CheckPasswordPerfectInfo", NUM));
                this.buttonTextView.setText(LocaleController.getString("CheckPasswordBackToSettings", NUM));
                this.descriptionText.setVisibility(0);
                this.imageView.setAnimation(NUM, 120, 120);
                this.imageView.playAnimation();
                break;
        }
        EditTextBoldCursor editTextBoldCursor2 = this.passwordEditText;
        if (editTextBoldCursor2 != null) {
            editTextBoldCursor2.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable s) {
                    if (!TwoStepVerificationSetupActivity.this.ignoreTextChange) {
                        boolean z = false;
                        if (TwoStepVerificationSetupActivity.this.currentType == 0) {
                            RLottieDrawable currentDrawable = TwoStepVerificationSetupActivity.this.imageView.getAnimatedDrawable();
                            if (TwoStepVerificationSetupActivity.this.passwordEditText.length() > 0) {
                                if (TwoStepVerificationSetupActivity.this.passwordEditText.getTransformationMethod() == null) {
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
                            } else if ((currentDrawable == TwoStepVerificationSetupActivity.this.animationDrawables[3] && TwoStepVerificationSetupActivity.this.passwordEditText.getTransformationMethod() == null) || currentDrawable == TwoStepVerificationSetupActivity.this.animationDrawables[5]) {
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
                                TwoStepVerificationSetupActivity.this.animationDrawables[0].setCustomEndFrame((int) ((142.0f * Math.min(1.0f, TwoStepVerificationSetupActivity.this.passwordEditText.getLayout().getLineWidth(0) / ((float) TwoStepVerificationSetupActivity.this.passwordEditText.getWidth()))) + 18.0f));
                                TwoStepVerificationSetupActivity.this.imageView.playAnimation();
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                            }
                        } else if (TwoStepVerificationSetupActivity.this.currentType == 5 || TwoStepVerificationSetupActivity.this.currentType == 4) {
                            if (TwoStepVerificationSetupActivity.this.emailCodeLength != 0 && s.length() == TwoStepVerificationSetupActivity.this.emailCodeLength) {
                                TwoStepVerificationSetupActivity.this.buttonTextView.callOnClick();
                            }
                            TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = TwoStepVerificationSetupActivity.this;
                            if (s.length() > 0) {
                                z = true;
                            }
                            twoStepVerificationSetupActivity.showDoneButton(z);
                        } else if (TwoStepVerificationSetupActivity.this.currentType == 8 && s.length() > 0) {
                            TwoStepVerificationSetupActivity.this.showDoneButton(true);
                        }
                    }
                }
            });
        }
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4023xdeCLASSNAMEe4(View v) {
        int i = this.currentType;
        if (i == 0) {
            needShowProgress();
            TLRPC.TL_auth_recoverPassword req = new TLRPC.TL_auth_recoverPassword();
            req.code = this.emailCode;
            getConnectionsManager().sendRequest(req, new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda23(this));
        } else if (i == 3) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setMessage(LocaleController.getString("YourEmailSkipWarningText", NUM));
            builder.setTitle(LocaleController.getString("YourEmailSkipWarning", NUM));
            builder.setPositiveButton(LocaleController.getString("YourEmailSkip", NUM), new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda31(this));
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

    /* renamed from: lambda$createView$3$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4021xfb75dba6(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda13(this, error));
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4016x9cCLASSNAME(TLRPC.TL_error error) {
        String timeString;
        needHideProgress();
        if (error == null) {
            getMessagesController().removeSuggestion(0, "VALIDATE_PASSWORD");
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda0(this));
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

    /* renamed from: lambda$createView$1$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4006x18228var_(DialogInterface dialogInterface, int i) {
        int N = this.fragmentsToClose.size();
        for (int a = 0; a < N; a++) {
            this.fragmentsToClose.get(a).removeSelfFromStack();
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.twoStepPasswordChanged, new Object[0]);
        finishFragment();
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4022xed1var_c5(DialogInterface dialogInterface, int i) {
        this.email = "";
        setNewPassword(false);
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4024xd072ce03(View v) {
        if (this.currentType == 8) {
            TwoStepVerificationActivity fragment = new TwoStepVerificationActivity();
            fragment.setForgotPasswordOnShow();
            fragment.setPassword(this.currentPassword);
            fragment.setBlockingAlert(this.otherwiseReloginDays);
            presentFragment(fragment, true);
        }
    }

    /* renamed from: lambda$createView$18$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4015xcCLASSNAMEa6(View v) {
        if (getParentActivity() != null) {
            switch (this.currentType) {
                case 0:
                    if (this.passwordEditText.length() == 0) {
                        onFieldError(this.passwordEditText, false);
                        return;
                    }
                    TwoStepVerificationSetupActivity fragment = new TwoStepVerificationSetupActivity(this.currentAccount, 1, this.currentPassword);
                    fragment.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, this.emailOnly);
                    fragment.setCurrentEmailCode(this.emailCode);
                    fragment.firstPassword = this.passwordEditText.getText().toString();
                    fragment.fragmentsToClose.addAll(this.fragmentsToClose);
                    fragment.fragmentsToClose.add(this);
                    fragment.closeAfterSet = this.closeAfterSet;
                    fragment.setBlockingAlert(this.otherwiseReloginDays);
                    presentFragment(fragment);
                    return;
                case 1:
                    if (!this.firstPassword.equals(this.passwordEditText.getText().toString())) {
                        try {
                            Toast.makeText(getParentActivity(), LocaleController.getString("PasswordDoNotMatch", NUM), 0).show();
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                        onFieldError(this.passwordEditText, false);
                        return;
                    }
                    TwoStepVerificationSetupActivity fragment2 = new TwoStepVerificationSetupActivity(this.currentAccount, 2, this.currentPassword);
                    fragment2.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, this.emailOnly);
                    fragment2.setCurrentEmailCode(this.emailCode);
                    fragment2.firstPassword = this.firstPassword;
                    fragment2.fragmentsToClose.addAll(this.fragmentsToClose);
                    fragment2.fragmentsToClose.add(this);
                    fragment2.closeAfterSet = this.closeAfterSet;
                    fragment2.setBlockingAlert(this.otherwiseReloginDays);
                    presentFragment(fragment2);
                    return;
                case 2:
                    if (this.passwordEditText.getText().toString().toLowerCase().equals(this.firstPassword.toLowerCase())) {
                        try {
                            Toast.makeText(getParentActivity(), LocaleController.getString("PasswordAsHintError", NUM), 0).show();
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                        onFieldError(this.passwordEditText, false);
                        return;
                    }
                    onHintDone();
                    return;
                case 3:
                    String obj = this.passwordEditText.getText().toString();
                    this.email = obj;
                    if (!isValidEmail(obj)) {
                        onFieldError(this.passwordEditText, false);
                        return;
                    } else {
                        setNewPassword(false);
                        return;
                    }
                case 4:
                    String code = this.passwordEditText.getText().toString();
                    if (code.length() == 0) {
                        onFieldError(this.passwordEditText, false);
                        return;
                    }
                    TLRPC.TL_auth_checkRecoveryPassword req = new TLRPC.TL_auth_checkRecoveryPassword();
                    req.code = code;
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda27(this, code), 10);
                    return;
                case 5:
                    if (this.passwordEditText.length() == 0) {
                        onFieldError(this.passwordEditText, false);
                        return;
                    }
                    TLRPC.TL_account_confirmPasswordEmail req2 = new TLRPC.TL_account_confirmPasswordEmail();
                    req2.code = this.passwordEditText.getText().toString();
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda21(this), 10);
                    needShowProgress();
                    return;
                case 6:
                    if (this.currentPassword == null) {
                        needShowProgress();
                        this.doneAfterPasswordLoad = true;
                        return;
                    }
                    TwoStepVerificationSetupActivity fragment3 = new TwoStepVerificationSetupActivity(this.currentAccount, 0, this.currentPassword);
                    fragment3.closeAfterSet = this.closeAfterSet;
                    fragment3.setBlockingAlert(this.otherwiseReloginDays);
                    presentFragment(fragment3, true);
                    return;
                case 7:
                    if (this.closeAfterSet) {
                        finishFragment();
                        return;
                    }
                    TwoStepVerificationActivity fragment4 = new TwoStepVerificationActivity();
                    fragment4.setCurrentPasswordParams(this.currentPassword, this.currentPasswordHash, this.currentSecretId, this.currentSecret);
                    fragment4.setBlockingAlert(this.otherwiseReloginDays);
                    presentFragment(fragment4, true);
                    return;
                case 8:
                    if (this.currentPassword == null) {
                        needShowProgress();
                        this.doneAfterPasswordLoad = true;
                        return;
                    }
                    String oldPassword = this.passwordEditText.getText().toString();
                    if (oldPassword.length() == 0) {
                        onFieldError(this.passwordEditText, false);
                        return;
                    }
                    byte[] oldPasswordBytes = AndroidUtilities.getStringBytes(oldPassword);
                    needShowProgress();
                    Utilities.globalQueue.postRunnable(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda19(this, oldPasswordBytes));
                    return;
                case 9:
                    finishFragment();
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: lambda$createView$12$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4009x226d34ec(byte[] oldPasswordBytes) {
        byte[] x_bytes;
        TLRPC.TL_account_getPasswordSettings req = new TLRPC.TL_account_getPasswordSettings();
        if (this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            x_bytes = SRPHelper.getX(oldPasswordBytes, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.current_algo);
        } else {
            x_bytes = null;
        }
        RequestDelegate requestDelegate = new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda30(this, x_bytes);
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

    /* renamed from: lambda$createView$11$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4008x30CLASSNAMEecd(byte[] x_bytes, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda20(this, x_bytes));
        } else {
            AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda10(this, error));
        }
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4025xCLASSNAMECLASSNAME(byte[] x_bytes) {
        needHideProgress();
        this.currentPasswordHash = x_bytes;
        getMessagesController().removeSuggestion(0, "VALIDATE_PASSWORD");
        TwoStepVerificationSetupActivity fragment = new TwoStepVerificationSetupActivity(9, this.currentPassword);
        fragment.setBlockingAlert(this.otherwiseReloginDays);
        presentFragment(fragment, true);
    }

    /* renamed from: lambda$createView$10$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4007x3var_e8ae(TLRPC.TL_error error) {
        String timeString;
        if ("SRP_ID_INVALID".equals(error.text)) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda24(this), 8);
            return;
        }
        needHideProgress();
        if ("PASSWORD_HASH_INVALID".equals(error.text)) {
            this.descriptionText.setText(LocaleController.getString("CheckPasswordWrong", NUM));
            this.descriptionText.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
            onFieldError(this.passwordEditText, true);
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

    /* renamed from: lambda$createView$9$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4027xa56fCLASSNAME(TLObject response2, TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda15(this, error2, response2));
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4026xb3CLASSNAMEa41(TLRPC.TL_error error2, TLObject response2) {
        if (error2 == null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response2;
            this.currentPassword = tL_account_password;
            TwoStepVerificationActivity.initPasswordNewAlgo(tL_account_password);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            this.buttonTextView.callOnClick();
        }
    }

    /* renamed from: lambda$createView$14$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4011x5CLASSNAMEa(String code, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda8(this, response, code, error));
    }

    /* renamed from: lambda$createView$13$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4010x1416db0b(TLObject response, String code, TLRPC.TL_error error) {
        String timeString;
        if (response instanceof TLRPC.TL_boolTrue) {
            TwoStepVerificationSetupActivity fragment = new TwoStepVerificationSetupActivity(this.currentAccount, 0, this.currentPassword);
            fragment.fragmentsToClose.addAll(this.fragmentsToClose);
            fragment.addFragmentToClose(this);
            fragment.setCurrentEmailCode(code);
            fragment.setBlockingAlert(this.otherwiseReloginDays);
            presentFragment(fragment, true);
        } else if (error == null || error.text.startsWith("CODE_INVALID")) {
            onFieldError(this.passwordEditText, true);
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

    /* renamed from: lambda$createView$17$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4014xdabd7387(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda12(this, error));
    }

    /* renamed from: lambda$createView$16$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4013xe913cd68(TLRPC.TL_error error) {
        String timeString;
        needHideProgress();
        if (error == null) {
            if (getParentActivity() != null) {
                if (this.currentPassword.has_password) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda11(this));
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
                fragment.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, this.emailOnly);
                fragment.fragmentsToClose.addAll(this.fragmentsToClose);
                fragment.closeAfterSet = this.closeAfterSet;
                fragment.setBlockingAlert(this.otherwiseReloginDays);
                presentFragment(fragment, true);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.twoStepPasswordChanged, this.currentPasswordHash, this.currentPassword.new_algo, this.currentPassword.new_secure_algo, this.currentPassword.secure_random, this.email, this.hint, null, this.firstPassword);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            }
        } else if (error.text.startsWith("CODE_INVALID")) {
            onFieldError(this.passwordEditText, true);
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

    /* renamed from: lambda$createView$15$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4012xvar_a2749(DialogInterface dialogInterface, int i) {
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

    static /* synthetic */ boolean lambda$createView$19(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$20$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ boolean m4017x82a5066f(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 && i != 6) {
            return false;
        }
        this.buttonTextView.callOnClick();
        return true;
    }

    /* renamed from: lambda$createView$21$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4018x744eac8e(View v) {
        this.ignoreTextChange = true;
        if (this.passwordEditText.getTransformationMethod() == null) {
            this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.showPasswordButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
            if (this.currentType == 0 && this.passwordEditText.length() > 0) {
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
            this.passwordEditText.setTransformationMethod((TransformationMethod) null);
            this.showPasswordButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelSend"), PorterDuff.Mode.MULTIPLY));
            if (this.currentType == 0 && this.passwordEditText.length() > 0) {
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
        EditTextBoldCursor editTextBoldCursor = this.passwordEditText;
        editTextBoldCursor.setSelection(editTextBoldCursor.length());
        this.ignoreTextChange = false;
    }

    /* renamed from: lambda$createView$23$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4020x57a1f8cc(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("Reset", NUM), new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda22(this));
        builder.setTitle(LocaleController.getString("ResetPassword", NUM));
        builder.setMessage(LocaleController.getString("RestoreEmailTroubleText2", NUM));
        showDialog(builder.create());
    }

    /* renamed from: lambda$createView$22$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4019x65var_ad(DialogInterface dialog, int which) {
        onReset();
        finishFragment();
    }

    public void onPause() {
        super.onPause();
        this.paused = true;
    }

    public void onResume() {
        super.onResume();
        this.paused = false;
        EditTextBoldCursor editTextBoldCursor = this.passwordEditText;
        if (editTextBoldCursor != null && editTextBoldCursor.getVisibility() == 0) {
            AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda6(this), 200);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    /* renamed from: lambda$onResume$24$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4031x68aa17d6() {
        EditTextBoldCursor editTextBoldCursor = this.passwordEditText;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.requestFocus();
            AndroidUtilities.showKeyboard(this.passwordEditText);
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
            org.telegram.ui.Components.EditTextBoldCursor r3 = r5.passwordEditText
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
            org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda7 r1 = new org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda7
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

    /* renamed from: lambda$setRandomMonkeyIdleAnimation$25$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4040xaCLASSNAME() {
        if (this.setAnimationRunnable != null) {
            setRandomMonkeyIdleAnimation(false);
        }
    }

    public void setCloseAfterSet(boolean value) {
        this.closeAfterSet = value;
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        EditTextBoldCursor editTextBoldCursor;
        if (isOpen && (editTextBoldCursor = this.passwordEditText) != null && editTextBoldCursor.getVisibility() == 0) {
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    private void loadPasswordInfo() {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda25(this), 10);
    }

    /* renamed from: lambda$loadPasswordInfo$27$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4029x2b7db0b6(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda16(this, error, response));
    }

    /* renamed from: lambda$loadPasswordInfo$26$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4028x39d40a97(TLRPC.TL_error error, TLObject response) {
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
                this.buttonTextView.callOnClick();
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
        }
    }

    private void needShowProgress() {
        if (getParentActivity() != null && !getParentActivity().isFinishing() && this.progressDialog == null) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            this.progressDialog = alertDialog;
            alertDialog.setCanCacnel(false);
            this.progressDialog.show();
        }
    }

    /* access modifiers changed from: protected */
    public void needHideProgress() {
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
            org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda26 r2 = new org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda26
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
            org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda9 r10 = new org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda9
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

    /* renamed from: lambda$setNewPassword$29$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4033x1d078bde(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda14(this, error));
    }

    /* renamed from: lambda$setNewPassword$28$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4032x2b5de5bf(TLRPC.TL_error error) {
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

    /* renamed from: lambda$setNewPassword$35$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4039x99eCLASSNAME(TLObject request, boolean clear, String password, TLRPC.TL_account_passwordInputSettings new_settings) {
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
        RequestDelegate requestDelegate = new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda29(this, clear, newPasswordHash, password, new_settings);
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

    /* renamed from: lambda$setNewPassword$34$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4038xa8426b04(boolean clear, byte[] newPasswordHash, String password, TLRPC.TL_account_passwordInputSettings new_settings, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda18(this, error, clear, response, newPasswordHash, password, new_settings));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: java.lang.Object[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* renamed from: lambda$setNewPassword$33$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m4037xb698c4e5(org.telegram.tgnet.TLRPC.TL_error r21, boolean r22, org.telegram.tgnet.TLObject r23, byte[] r24, java.lang.String r25, org.telegram.tgnet.TLRPC.TL_account_passwordInputSettings r26) {
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
            org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda28 r8 = new org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda28
            r8.<init>(r0, r2)
            r7.sendRequest(r6, r8, r5)
            return
        L_0x002c:
            r20.needHideProgress()
            r6 = 7
            r7 = 0
            r8 = 1
            if (r1 != 0) goto L_0x0161
            boolean r9 = r3 instanceof org.telegram.tgnet.TLRPC.TL_boolTrue
            if (r9 != 0) goto L_0x003c
            boolean r9 = r3 instanceof org.telegram.tgnet.TLRPC.auth_Authorization
            if (r9 == 0) goto L_0x0161
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
            goto L_0x0272
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
            r6 = 2131626751(0x7f0e0aff, float:1.8880747E38)
            java.lang.String r8 = "OK"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r6)
            org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda33 r8 = new org.telegram.ui.TwoStepVerificationSetupActivity$$ExternalSyntheticLambda33
            r8.<init>(r0, r4)
            r5.setPositiveButton(r6, r8)
            if (r25 != 0) goto L_0x00bf
            org.telegram.tgnet.TLRPC$TL_account_password r6 = r0.currentPassword
            if (r6 == 0) goto L_0x00bf
            boolean r6 = r6.has_password
            if (r6 == 0) goto L_0x00bf
            r6 = 2131628738(0x7f0e12c2, float:1.8884777E38)
            java.lang.String r8 = "YourEmailSuccessText"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r6)
            r5.setMessage(r6)
            goto L_0x00cb
        L_0x00bf:
            r6 = 2131628743(0x7f0e12c7, float:1.8884787E38)
            java.lang.String r8 = "YourPasswordChangedSuccessText"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r6)
            r5.setMessage(r6)
        L_0x00cb:
            r6 = 2131628748(0x7f0e12cc, float:1.8884797E38)
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
            goto L_0x0272
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
            if (r4 == 0) goto L_0x0133
            r11 = r4
            goto L_0x0136
        L_0x0133:
            byte[] r6 = r0.currentPasswordHash
            r11 = r6
        L_0x0136:
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
            goto L_0x0272
        L_0x0161:
            if (r1 == 0) goto L_0x0270
            java.lang.String r9 = r1.text
            java.lang.String r10 = "EMAIL_UNCONFIRMED"
            boolean r9 = r10.equals(r9)
            if (r9 != 0) goto L_0x01e7
            java.lang.String r9 = r1.text
            java.lang.String r10 = "EMAIL_UNCONFIRMED_"
            boolean r9 = r9.startsWith(r10)
            if (r9 == 0) goto L_0x0178
            goto L_0x01e7
        L_0x0178:
            java.lang.String r5 = r1.text
            java.lang.String r6 = "EMAIL_INVALID"
            boolean r5 = r6.equals(r5)
            r6 = 2131624300(0x7f0e016c, float:1.8875776E38)
            java.lang.String r9 = "AppName"
            if (r5 == 0) goto L_0x019b
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r6 = 2131627025(0x7f0e0CLASSNAME, float:1.8881303E38)
            java.lang.String r7 = "PasswordEmailInvalid"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r0.showAlertWithText(r5, r6)
            r7 = r26
            goto L_0x0272
        L_0x019b:
            java.lang.String r5 = r1.text
            java.lang.String r10 = "FLOOD_WAIT"
            boolean r5 = r5.startsWith(r10)
            if (r5 == 0) goto L_0x01da
            java.lang.String r5 = r1.text
            java.lang.Integer r5 = org.telegram.messenger.Utilities.parseInt(r5)
            int r5 = r5.intValue()
            r10 = 60
            if (r5 >= r10) goto L_0x01ba
            java.lang.String r10 = "Seconds"
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatPluralString(r10, r5)
            goto L_0x01c2
        L_0x01ba:
            int r10 = r5 / 60
            java.lang.String r11 = "Minutes"
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatPluralString(r11, r10)
        L_0x01c2:
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r9 = 2131625681(0x7f0e06d1, float:1.8878577E38)
            java.lang.Object[] r8 = new java.lang.Object[r8]
            r8[r7] = r10
            java.lang.String r7 = "FloodWaitTime"
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r9, r8)
            r0.showAlertWithText(r6, r7)
            r7 = r26
            goto L_0x0272
        L_0x01da:
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r6)
            java.lang.String r6 = r1.text
            r0.showAlertWithText(r5, r6)
            r7 = r26
            goto L_0x0272
        L_0x01e7:
            int r9 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getInstance(r9)
            int r10 = org.telegram.messenger.NotificationCenter.twoStepPasswordChanged
            java.lang.Object[] r11 = new java.lang.Object[r7]
            r9.postNotificationName(r10, r11)
            r9 = 0
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r10 = r0.fragmentsToClose
            int r10 = r10.size()
        L_0x01fb:
            if (r9 >= r10) goto L_0x020b
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r11 = r0.fragmentsToClose
            java.lang.Object r11 = r11.get(r9)
            org.telegram.ui.ActionBar.BaseFragment r11 = (org.telegram.ui.ActionBar.BaseFragment) r11
            r11.removeSelfFromStack()
            int r9 = r9 + 1
            goto L_0x01fb
        L_0x020b:
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
            if (r4 == 0) goto L_0x0250
            r15 = r4
            goto L_0x0253
        L_0x0250:
            byte[] r6 = r0.currentPasswordHash
            r15 = r6
        L_0x0253:
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
            goto L_0x0272
        L_0x0270:
            r7 = r26
        L_0x0272:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TwoStepVerificationSetupActivity.m4037xb698c4e5(org.telegram.tgnet.TLRPC$TL_error, boolean, org.telegram.tgnet.TLObject, byte[], java.lang.String, org.telegram.tgnet.TLRPC$TL_account_passwordInputSettings):void");
    }

    /* renamed from: lambda$setNewPassword$31$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4035xd34578a7(boolean clear, TLObject response2, TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda17(this, error2, response2, clear));
    }

    /* renamed from: lambda$setNewPassword$30$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4034xe19bd288(TLRPC.TL_error error2, TLObject response2, boolean clear) {
        if (error2 == null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response2;
            this.currentPassword = tL_account_password;
            TwoStepVerificationActivity.initPasswordNewAlgo(tL_account_password);
            setNewPassword(clear);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
        }
    }

    /* renamed from: lambda$setNewPassword$32$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4036xc4ef1ec6(byte[] newPasswordHash, DialogInterface dialogInterface, int i) {
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

    private void onFieldError(TextView field, boolean clear) {
        if (getParentActivity() != null) {
            Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200);
            }
            if (clear) {
                field.setText("");
            }
            AndroidUtilities.shakeView(field, 2.0f, 0);
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
        themeDescriptions.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        themeDescriptions.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        themeDescriptions.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
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
            return super.onBackPressed();
        }
        showSetForcePasswordAlert();
        return false;
    }

    /* access modifiers changed from: private */
    public void showSetForcePasswordAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("Warning", NUM));
        builder.setMessage(LocaleController.formatPluralString("ForceSetPasswordAlertMessage", this.otherwiseReloginDays));
        builder.setPositiveButton(LocaleController.getString("ForceSetPasswordContinue", NUM), TwoStepVerificationSetupActivity$$ExternalSyntheticLambda34.INSTANCE);
        builder.setNegativeButton(LocaleController.getString("ForceSetPasswordCancel", NUM), new TwoStepVerificationSetupActivity$$ExternalSyntheticLambda32(this));
        ((TextView) builder.show().getButton(-2)).setTextColor(Theme.getColor("dialogTextRed2"));
    }

    static /* synthetic */ void lambda$showSetForcePasswordAlert$36(DialogInterface a1, int a2) {
    }

    /* renamed from: lambda$showSetForcePasswordAlert$37$org-telegram-ui-TwoStepVerificationSetupActivity  reason: not valid java name */
    public /* synthetic */ void m4041x8c5CLASSNAME(DialogInterface a1, int a2) {
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
}

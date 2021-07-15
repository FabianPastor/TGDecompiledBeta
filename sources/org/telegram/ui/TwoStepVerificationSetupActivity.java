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
import org.telegram.tgnet.TLRPC$PasswordKdfAlgo;
import org.telegram.tgnet.TLRPC$SecurePasswordKdfAlgo;
import org.telegram.tgnet.TLRPC$TL_account_confirmPasswordEmail;
import org.telegram.tgnet.TLRPC$TL_account_getPassword;
import org.telegram.tgnet.TLRPC$TL_account_getPasswordSettings;
import org.telegram.tgnet.TLRPC$TL_account_password;
import org.telegram.tgnet.TLRPC$TL_account_passwordInputSettings;
import org.telegram.tgnet.TLRPC$TL_account_resendPasswordEmail;
import org.telegram.tgnet.TLRPC$TL_account_updatePasswordSettings;
import org.telegram.tgnet.TLRPC$TL_auth_checkRecoveryPassword;
import org.telegram.tgnet.TLRPC$TL_auth_recoverPassword;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputCheckPasswordSRP;
import org.telegram.tgnet.TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow;
import org.telegram.tgnet.TLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000;
import org.telegram.tgnet.TLRPC$TL_secureSecretSettings;
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
import org.telegram.ui.TwoStepVerificationSetupActivity;

public class TwoStepVerificationSetupActivity extends BaseFragment {
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
    private String email;
    private String emailCode;
    /* access modifiers changed from: private */
    public int emailCodeLength = 6;
    private boolean emailOnly;
    private Runnable finishCallback = new Runnable() {
        public final void run() {
            TwoStepVerificationSetupActivity.this.lambda$new$0$TwoStepVerificationSetupActivity();
        }
    };
    private String firstPassword;
    private ArrayList<BaseFragment> fragmentsToClose = new ArrayList<>();
    private String hint;
    /* access modifiers changed from: private */
    public boolean ignoreTextChange;
    /* access modifiers changed from: private */
    public RLottieImageView imageView;
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

    static /* synthetic */ boolean lambda$createView$19(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onReset() {
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$TwoStepVerificationSetupActivity() {
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
            static /* synthetic */ void lambda$onItemClick$0(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            }

            public void onItemClick(int i) {
                String str;
                if (i == -1) {
                    TwoStepVerificationSetupActivity.this.finishFragment();
                } else if (i == 2) {
                    ConnectionsManager.getInstance(TwoStepVerificationSetupActivity.this.currentAccount).sendRequest(new TLRPC$TL_account_resendPasswordEmail(), $$Lambda$TwoStepVerificationSetupActivity$1$_oZufnja0jNGCLASSNAMERZfeSHbqyXOI.INSTANCE);
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) TwoStepVerificationSetupActivity.this.getParentActivity());
                    builder.setMessage(LocaleController.getString("ResendCodeInfo", NUM));
                    builder.setTitle(LocaleController.getString("TwoStepVerificationTitle", NUM));
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                    TwoStepVerificationSetupActivity.this.showDialog(builder.create());
                } else if (i == 1) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) TwoStepVerificationSetupActivity.this.getParentActivity());
                    if (TwoStepVerificationSetupActivity.this.currentPassword == null || !TwoStepVerificationSetupActivity.this.currentPassword.has_password) {
                        str = LocaleController.getString("CancelPasswordQuestion", NUM);
                    } else {
                        str = LocaleController.getString("CancelEmailQuestion", NUM);
                    }
                    String string = LocaleController.getString("CancelEmailQuestionTitle", NUM);
                    String string2 = LocaleController.getString("Abort", NUM);
                    builder2.setMessage(str);
                    builder2.setTitle(string);
                    builder2.setPositiveButton(string2, new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            TwoStepVerificationSetupActivity.AnonymousClass1.this.lambda$onItemClick$1$TwoStepVerificationSetupActivity$1(dialogInterface, i);
                        }
                    });
                    builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    AlertDialog create = builder2.create();
                    TwoStepVerificationSetupActivity.this.showDialog(create);
                    TextView textView = (TextView) create.getButton(-1);
                    if (textView != null) {
                        textView.setTextColor(Theme.getColor("dialogTextRed2"));
                    }
                }
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onItemClick$1 */
            public /* synthetic */ void lambda$onItemClick$1$TwoStepVerificationSetupActivity$1(DialogInterface dialogInterface, int i) {
                TwoStepVerificationSetupActivity.this.setNewPassword(true);
            }
        });
        if (this.currentType == 5) {
            ActionBarMenuItem addItem = this.actionBar.createMenu().addItem(0, NUM);
            addItem.addSubItem(2, LocaleController.getString("ResendCode", NUM));
            addItem.addSubItem(1, LocaleController.getString("AbortPasswordMenu", NUM));
        }
        TextView textView = new TextView(context2);
        this.topButton = textView;
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText2"));
        this.topButton.setTextSize(1, 14.0f);
        this.topButton.setGravity(16);
        this.topButton.setVisibility(8);
        this.actionBar.addView(this.topButton, LayoutHelper.createFrame(-2, -1.0f, 53, 0.0f, 0.0f, 22.0f, 0.0f));
        this.topButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TwoStepVerificationSetupActivity.this.lambda$createView$5$TwoStepVerificationSetupActivity(view);
            }
        });
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
        this.descriptionText2.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TwoStepVerificationSetupActivity.this.lambda$createView$6$TwoStepVerificationSetupActivity(view);
            }
        });
        TextView textView5 = new TextView(context2);
        this.buttonTextView = textView5;
        textView5.setMinWidth(AndroidUtilities.dp(220.0f));
        this.buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        this.buttonTextView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TwoStepVerificationSetupActivity.this.lambda$createView$18$TwoStepVerificationSetupActivity(view);
            }
        });
        switch (this.currentType) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 8:
                AnonymousClass3 r2 = new ViewGroup(context2) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        int size = View.MeasureSpec.getSize(i);
                        int size2 = View.MeasureSpec.getSize(i2);
                        if (TwoStepVerificationSetupActivity.this.topButton != null) {
                            ((FrameLayout.LayoutParams) TwoStepVerificationSetupActivity.this.topButton.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
                        }
                        TwoStepVerificationSetupActivity.this.actionBar.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), i2);
                        TwoStepVerificationSetupActivity.this.actionBarBackground.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(TwoStepVerificationSetupActivity.this.actionBar.getMeasuredHeight() + AndroidUtilities.dp(3.0f), NUM));
                        TwoStepVerificationSetupActivity.this.scrollView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), i2);
                        setMeasuredDimension(size, size2);
                    }

                    /* access modifiers changed from: protected */
                    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
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
                                AnimatorSet access$2000 = TwoStepVerificationSetupActivity.this.actionBarAnimator;
                                Animator[] animatorArr = new Animator[2];
                                View access$1400 = TwoStepVerificationSetupActivity.this.actionBarBackground;
                                Property property = View.ALPHA;
                                float[] fArr = new float[1];
                                float f = 1.0f;
                                fArr[0] = z ? 1.0f : 0.0f;
                                animatorArr[0] = ObjectAnimator.ofFloat(access$1400, property, fArr);
                                SimpleTextView titleTextView = TwoStepVerificationSetupActivity.this.actionBar.getTitleTextView();
                                Property property2 = View.ALPHA;
                                float[] fArr2 = new float[1];
                                if (!z) {
                                    f = 0.0f;
                                }
                                fArr2[0] = f;
                                animatorArr[1] = ObjectAnimator.ofFloat(titleTextView, property2, fArr2);
                                access$2000.playTogether(animatorArr);
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
                this.scrollView = r15;
                r15.setVerticalScrollBarEnabled(false);
                r2.addView(this.scrollView);
                LinearLayout linearLayout = new LinearLayout(context2);
                linearLayout.setOrientation(1);
                this.scrollView.addView(linearLayout, LayoutHelper.createScroll(-1, -1, 51));
                linearLayout.addView(this.imageView, LayoutHelper.createLinear(-2, -2, 49, 0, 69, 0, 0));
                linearLayout.addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 8, 0, 0));
                linearLayout.addView(this.descriptionText, LayoutHelper.createLinear(-2, -2, 49, 0, 9, 0, 0));
                FrameLayout frameLayout = new FrameLayout(context2);
                linearLayout.addView(frameLayout, LayoutHelper.createLinear(220, 36, 49, 40, 32, 40, 0));
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
                this.passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        return TwoStepVerificationSetupActivity.this.lambda$createView$20$TwoStepVerificationSetupActivity(textView, i, keyEvent);
                    }
                });
                this.passwordEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                        return false;
                    }

                    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                        return false;
                    }

                    public void onDestroyActionMode(ActionMode actionMode) {
                    }

                    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                        return false;
                    }
                });
                AnonymousClass6 r4 = new ImageView(context2) {
                    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                        boolean z = true;
                        accessibilityNodeInfo.setCheckable(true);
                        if (TwoStepVerificationSetupActivity.this.passwordEditText.getTransformationMethod() != null) {
                            z = false;
                        }
                        accessibilityNodeInfo.setChecked(z);
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
                this.showPasswordButton.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        TwoStepVerificationSetupActivity.this.lambda$createView$21$TwoStepVerificationSetupActivity(view);
                    }
                });
                FrameLayout frameLayout2 = new FrameLayout(context2);
                linearLayout.addView(frameLayout2, LayoutHelper.createLinear(-1, -2, 51, 0, 36, 0, 22));
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
                    linearLayout.addView(this.descriptionText3, LayoutHelper.createLinear(-2, -2, 49, 0, 0, 0, 25));
                    this.descriptionText3.setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            TwoStepVerificationSetupActivity.this.lambda$createView$23$TwoStepVerificationSetupActivity(view);
                        }
                    });
                }
                this.fragmentView = r2;
                AnonymousClass7 r42 = new View(context2) {
                    private Paint paint = new Paint();

                    /* access modifiers changed from: protected */
                    public void onDraw(Canvas canvas) {
                        this.paint.setColor(Theme.getColor("windowBackgroundWhite"));
                        int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(3.0f);
                        canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) measuredHeight, this.paint);
                        TwoStepVerificationSetupActivity.this.parentLayout.drawHeaderShadow(canvas, measuredHeight);
                    }
                };
                this.actionBarBackground = r42;
                r42.setAlpha(0.0f);
                r2.addView(this.actionBarBackground);
                r2.addView(this.actionBar);
                break;
            case 6:
            case 7:
            case 9:
                AnonymousClass2 r22 = new ViewGroup(context2) {
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
                            TwoStepVerificationSetupActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) size2) * 0.399f), NUM));
                            TwoStepVerificationSetupActivity.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            TwoStepVerificationSetupActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            TwoStepVerificationSetupActivity.this.descriptionText2.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(size2, 0));
                            TwoStepVerificationSetupActivity.this.buttonTextView.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
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
                        float f4 = (float) i6;
                        int i11 = (int) (0.148f * f4);
                        TwoStepVerificationSetupActivity.this.imageView.layout(0, i11, TwoStepVerificationSetupActivity.this.imageView.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.imageView.getMeasuredHeight() + i11);
                        int i12 = (int) (0.458f * f4);
                        TwoStepVerificationSetupActivity.this.titleTextView.layout(0, i12, TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredHeight() + i12);
                        int measuredHeight2 = i12 + TwoStepVerificationSetupActivity.this.titleTextView.getMeasuredHeight() + AndroidUtilities.dp(12.0f);
                        TwoStepVerificationSetupActivity.this.descriptionText.layout(0, measuredHeight2, TwoStepVerificationSetupActivity.this.descriptionText.getMeasuredWidth(), TwoStepVerificationSetupActivity.this.descriptionText.getMeasuredHeight() + measuredHeight2);
                        int measuredWidth2 = (i5 - TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredWidth()) / 2;
                        int i13 = (int) (f4 * 0.791f);
                        TwoStepVerificationSetupActivity.this.buttonTextView.layout(measuredWidth2, i13, TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredWidth() + measuredWidth2, TwoStepVerificationSetupActivity.this.buttonTextView.getMeasuredHeight() + i13);
                    }
                };
                r22.setOnTouchListener($$Lambda$TwoStepVerificationSetupActivity$O0xdrjZ8Kiqxmrx9HILh5HXUVzo.INSTANCE);
                r22.addView(this.actionBar);
                r22.addView(this.imageView);
                r22.addView(this.titleTextView);
                r22.addView(this.descriptionText);
                r22.addView(this.buttonTextView);
                this.fragmentView = r22;
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
                String str = this.currentPassword.email_unconfirmed_pattern;
                if (str == null) {
                    str = "";
                }
                objArr[0] = str;
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
                String str2 = this.currentPassword.email_unconfirmed_pattern;
                if (str2 == null) {
                    str2 = "";
                }
                objArr2[0] = str2;
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
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    if (!TwoStepVerificationSetupActivity.this.ignoreTextChange) {
                        boolean z = false;
                        if (TwoStepVerificationSetupActivity.this.currentType == 0) {
                            RLottieDrawable animatedDrawable = TwoStepVerificationSetupActivity.this.imageView.getAnimatedDrawable();
                            if (TwoStepVerificationSetupActivity.this.passwordEditText.length() > 0) {
                                if (TwoStepVerificationSetupActivity.this.passwordEditText.getTransformationMethod() == null) {
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
                            } else if ((animatedDrawable == TwoStepVerificationSetupActivity.this.animationDrawables[3] && TwoStepVerificationSetupActivity.this.passwordEditText.getTransformationMethod() == null) || animatedDrawable == TwoStepVerificationSetupActivity.this.animationDrawables[5]) {
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
                                TwoStepVerificationSetupActivity.this.animationDrawables[0].setCustomEndFrame((int) ((Math.min(1.0f, TwoStepVerificationSetupActivity.this.passwordEditText.getLayout().getLineWidth(0) / ((float) TwoStepVerificationSetupActivity.this.passwordEditText.getWidth())) * 142.0f) + 18.0f));
                                TwoStepVerificationSetupActivity.this.imageView.playAnimation();
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                            }
                        } else if (TwoStepVerificationSetupActivity.this.currentType == 5 || TwoStepVerificationSetupActivity.this.currentType == 4) {
                            if (TwoStepVerificationSetupActivity.this.emailCodeLength != 0 && editable.length() == TwoStepVerificationSetupActivity.this.emailCodeLength) {
                                TwoStepVerificationSetupActivity.this.buttonTextView.callOnClick();
                            }
                            TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = TwoStepVerificationSetupActivity.this;
                            if (editable.length() > 0) {
                                z = true;
                            }
                            twoStepVerificationSetupActivity.showDoneButton(z);
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
    /* renamed from: lambda$createView$5 */
    public /* synthetic */ void lambda$createView$5$TwoStepVerificationSetupActivity(View view) {
        int i = this.currentType;
        if (i == 0) {
            needShowProgress();
            TLRPC$TL_auth_recoverPassword tLRPC$TL_auth_recoverPassword = new TLRPC$TL_auth_recoverPassword();
            tLRPC$TL_auth_recoverPassword.code = this.emailCode;
            getConnectionsManager().sendRequest(tLRPC$TL_auth_recoverPassword, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    TwoStepVerificationSetupActivity.this.lambda$null$3$TwoStepVerificationSetupActivity(tLObject, tLRPC$TL_error);
                }
            });
        } else if (i == 3) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setMessage(LocaleController.getString("YourEmailSkipWarningText", NUM));
            builder.setTitle(LocaleController.getString("YourEmailSkipWarning", NUM));
            builder.setPositiveButton(LocaleController.getString("YourEmailSkip", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    TwoStepVerificationSetupActivity.this.lambda$null$4$TwoStepVerificationSetupActivity(dialogInterface, i);
                }
            });
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
    /* renamed from: lambda$null$3 */
    public /* synthetic */ void lambda$null$3$TwoStepVerificationSetupActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error) {
            public final /* synthetic */ TLRPC$TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                TwoStepVerificationSetupActivity.this.lambda$null$2$TwoStepVerificationSetupActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$2 */
    public /* synthetic */ void lambda$null$2$TwoStepVerificationSetupActivity(TLRPC$TL_error tLRPC$TL_error) {
        String str;
        needHideProgress();
        if (tLRPC$TL_error == null) {
            getMessagesController().removeSuggestion(0, "VALIDATE_PASSWORD");
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    TwoStepVerificationSetupActivity.this.lambda$null$1$TwoStepVerificationSetupActivity(dialogInterface, i);
                }
            });
            builder.setMessage(LocaleController.getString("PasswordReset", NUM));
            builder.setTitle(LocaleController.getString("TwoStepVerificationTitle", NUM));
            Dialog showDialog = showDialog(builder.create());
            if (showDialog != null) {
                showDialog.setCanceledOnTouchOutside(false);
                showDialog.setCancelable(false);
            }
        } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
            int intValue = Utilities.parseInt(tLRPC$TL_error.text).intValue();
            if (intValue < 60) {
                str = LocaleController.formatPluralString("Seconds", intValue);
            } else {
                str = LocaleController.formatPluralString("Minutes", intValue / 60);
            }
            showAlertWithText(LocaleController.getString("TwoStepVerificationTitle", NUM), LocaleController.formatString("FloodWaitTime", NUM, str));
        } else {
            showAlertWithText(LocaleController.getString("TwoStepVerificationTitle", NUM), tLRPC$TL_error.text);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$1 */
    public /* synthetic */ void lambda$null$1$TwoStepVerificationSetupActivity(DialogInterface dialogInterface, int i) {
        int size = this.fragmentsToClose.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.fragmentsToClose.get(i2).removeSelfFromStack();
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.twoStepPasswordChanged, new Object[0]);
        finishFragment();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$4 */
    public /* synthetic */ void lambda$null$4$TwoStepVerificationSetupActivity(DialogInterface dialogInterface, int i) {
        this.email = "";
        setNewPassword(false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$6 */
    public /* synthetic */ void lambda$createView$6$TwoStepVerificationSetupActivity(View view) {
        if (this.currentType == 8) {
            TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
            twoStepVerificationActivity.setForgotPasswordOnShow();
            twoStepVerificationActivity.setPassword(this.currentPassword);
            presentFragment(twoStepVerificationActivity, true);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$18 */
    public /* synthetic */ void lambda$createView$18$TwoStepVerificationSetupActivity(View view) {
        if (getParentActivity() != null) {
            switch (this.currentType) {
                case 0:
                    if (this.passwordEditText.length() == 0) {
                        onFieldError(this.passwordEditText, false);
                        return;
                    }
                    TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(this.currentAccount, 1, this.currentPassword);
                    twoStepVerificationSetupActivity.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, this.emailOnly);
                    twoStepVerificationSetupActivity.setCurrentEmailCode(this.emailCode);
                    twoStepVerificationSetupActivity.firstPassword = this.passwordEditText.getText().toString();
                    twoStepVerificationSetupActivity.fragmentsToClose.addAll(this.fragmentsToClose);
                    twoStepVerificationSetupActivity.fragmentsToClose.add(this);
                    twoStepVerificationSetupActivity.closeAfterSet = this.closeAfterSet;
                    presentFragment(twoStepVerificationSetupActivity);
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
                    TwoStepVerificationSetupActivity twoStepVerificationSetupActivity2 = new TwoStepVerificationSetupActivity(this.currentAccount, 2, this.currentPassword);
                    twoStepVerificationSetupActivity2.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, this.emailOnly);
                    twoStepVerificationSetupActivity2.setCurrentEmailCode(this.emailCode);
                    twoStepVerificationSetupActivity2.firstPassword = this.firstPassword;
                    twoStepVerificationSetupActivity2.fragmentsToClose.addAll(this.fragmentsToClose);
                    twoStepVerificationSetupActivity2.fragmentsToClose.add(this);
                    twoStepVerificationSetupActivity2.closeAfterSet = this.closeAfterSet;
                    presentFragment(twoStepVerificationSetupActivity2);
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
                    String obj2 = this.passwordEditText.getText().toString();
                    if (obj2.length() == 0) {
                        onFieldError(this.passwordEditText, false);
                        return;
                    }
                    TLRPC$TL_auth_checkRecoveryPassword tLRPC$TL_auth_checkRecoveryPassword = new TLRPC$TL_auth_checkRecoveryPassword();
                    tLRPC$TL_auth_checkRecoveryPassword.code = obj2;
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_auth_checkRecoveryPassword, new RequestDelegate(obj2) {
                        public final /* synthetic */ String f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            TwoStepVerificationSetupActivity.this.lambda$null$14$TwoStepVerificationSetupActivity(this.f$1, tLObject, tLRPC$TL_error);
                        }
                    }, 10);
                    return;
                case 5:
                    if (this.passwordEditText.length() == 0) {
                        onFieldError(this.passwordEditText, false);
                        return;
                    }
                    TLRPC$TL_account_confirmPasswordEmail tLRPC$TL_account_confirmPasswordEmail = new TLRPC$TL_account_confirmPasswordEmail();
                    tLRPC$TL_account_confirmPasswordEmail.code = this.passwordEditText.getText().toString();
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_confirmPasswordEmail, new RequestDelegate() {
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            TwoStepVerificationSetupActivity.this.lambda$null$17$TwoStepVerificationSetupActivity(tLObject, tLRPC$TL_error);
                        }
                    }, 10);
                    needShowProgress();
                    return;
                case 6:
                    TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
                    if (tLRPC$TL_account_password == null) {
                        needShowProgress();
                        this.doneAfterPasswordLoad = true;
                        return;
                    }
                    TwoStepVerificationSetupActivity twoStepVerificationSetupActivity3 = new TwoStepVerificationSetupActivity(this.currentAccount, 0, tLRPC$TL_account_password);
                    twoStepVerificationSetupActivity3.closeAfterSet = this.closeAfterSet;
                    presentFragment(twoStepVerificationSetupActivity3, true);
                    return;
                case 7:
                    if (this.closeAfterSet) {
                        finishFragment();
                        return;
                    }
                    TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
                    twoStepVerificationActivity.setCurrentPasswordParams(this.currentPassword, this.currentPasswordHash, this.currentSecretId, this.currentSecret);
                    presentFragment(twoStepVerificationActivity, true);
                    return;
                case 8:
                    if (this.currentPassword == null) {
                        needShowProgress();
                        this.doneAfterPasswordLoad = true;
                        return;
                    }
                    String obj3 = this.passwordEditText.getText().toString();
                    if (obj3.length() == 0) {
                        onFieldError(this.passwordEditText, false);
                        return;
                    }
                    byte[] stringBytes = AndroidUtilities.getStringBytes(obj3);
                    needShowProgress();
                    Utilities.globalQueue.postRunnable(new Runnable(stringBytes) {
                        public final /* synthetic */ byte[] f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            TwoStepVerificationSetupActivity.this.lambda$null$12$TwoStepVerificationSetupActivity(this.f$1);
                        }
                    });
                    return;
                case 9:
                    finishFragment();
                    return;
                default:
                    return;
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$12 */
    public /* synthetic */ void lambda$null$12$TwoStepVerificationSetupActivity(byte[] bArr) {
        TLRPC$TL_account_getPasswordSettings tLRPC$TL_account_getPasswordSettings = new TLRPC$TL_account_getPasswordSettings();
        TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo = this.currentPassword.current_algo;
        byte[] x = tLRPC$PasswordKdfAlgo instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow ? SRPHelper.getX(bArr, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo) : null;
        $$Lambda$TwoStepVerificationSetupActivity$BMUEjRNl9TXcaHNHBHJxA12HNM r1 = new RequestDelegate(x) {
            public final /* synthetic */ byte[] f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                TwoStepVerificationSetupActivity.this.lambda$null$11$TwoStepVerificationSetupActivity(this.f$1, tLObject, tLRPC$TL_error);
            }
        };
        TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
        TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo2 = tLRPC$TL_account_password.current_algo;
        if (tLRPC$PasswordKdfAlgo2 instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TLRPC$TL_inputCheckPasswordSRP startCheck = SRPHelper.startCheck(x, tLRPC$TL_account_password.srp_id, tLRPC$TL_account_password.srp_B, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo2);
            tLRPC$TL_account_getPasswordSettings.password = startCheck;
            if (startCheck == null) {
                TLRPC$TL_error tLRPC$TL_error = new TLRPC$TL_error();
                tLRPC$TL_error.text = "ALGO_INVALID";
                r1.run((TLObject) null, tLRPC$TL_error);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_getPasswordSettings, r1, 10);
            return;
        }
        TLRPC$TL_error tLRPC$TL_error2 = new TLRPC$TL_error();
        tLRPC$TL_error2.text = "PASSWORD_HASH_INVALID";
        r1.run((TLObject) null, tLRPC$TL_error2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$11 */
    public /* synthetic */ void lambda$null$11$TwoStepVerificationSetupActivity(byte[] bArr, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(bArr) {
                public final /* synthetic */ byte[] f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    TwoStepVerificationSetupActivity.this.lambda$null$7$TwoStepVerificationSetupActivity(this.f$1);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error) {
                public final /* synthetic */ TLRPC$TL_error f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    TwoStepVerificationSetupActivity.this.lambda$null$10$TwoStepVerificationSetupActivity(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$7 */
    public /* synthetic */ void lambda$null$7$TwoStepVerificationSetupActivity(byte[] bArr) {
        needHideProgress();
        this.currentPasswordHash = bArr;
        getMessagesController().removeSuggestion(0, "VALIDATE_PASSWORD");
        presentFragment(new TwoStepVerificationSetupActivity(9, this.currentPassword), true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$10 */
    public /* synthetic */ void lambda$null$10$TwoStepVerificationSetupActivity(TLRPC$TL_error tLRPC$TL_error) {
        String str;
        if ("SRP_ID_INVALID".equals(tLRPC$TL_error.text)) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    TwoStepVerificationSetupActivity.this.lambda$null$9$TwoStepVerificationSetupActivity(tLObject, tLRPC$TL_error);
                }
            }, 8);
            return;
        }
        needHideProgress();
        if ("PASSWORD_HASH_INVALID".equals(tLRPC$TL_error.text)) {
            this.descriptionText.setText(LocaleController.getString("CheckPasswordWrong", NUM));
            this.descriptionText.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
            onFieldError(this.passwordEditText, true);
            showDoneButton(false);
        } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
            int intValue = Utilities.parseInt(tLRPC$TL_error.text).intValue();
            if (intValue < 60) {
                str = LocaleController.formatPluralString("Seconds", intValue);
            } else {
                str = LocaleController.formatPluralString("Minutes", intValue / 60);
            }
            showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, str));
        } else {
            showAlertWithText(LocaleController.getString("AppName", NUM), tLRPC$TL_error.text);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$9 */
    public /* synthetic */ void lambda$null$9$TwoStepVerificationSetupActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                TwoStepVerificationSetupActivity.this.lambda$null$8$TwoStepVerificationSetupActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$8 */
    public /* synthetic */ void lambda$null$8$TwoStepVerificationSetupActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            this.currentPassword = tLRPC$TL_account_password;
            TwoStepVerificationActivity.initPasswordNewAlgo(tLRPC$TL_account_password);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            this.buttonTextView.callOnClick();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$14 */
    public /* synthetic */ void lambda$null$14$TwoStepVerificationSetupActivity(String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, str, tLRPC$TL_error) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ String f$2;
            public final /* synthetic */ TLRPC$TL_error f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                TwoStepVerificationSetupActivity.this.lambda$null$13$TwoStepVerificationSetupActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$13 */
    public /* synthetic */ void lambda$null$13$TwoStepVerificationSetupActivity(TLObject tLObject, String str, TLRPC$TL_error tLRPC$TL_error) {
        String str2;
        if (tLObject instanceof TLRPC$TL_boolTrue) {
            TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(this.currentAccount, 0, this.currentPassword);
            twoStepVerificationSetupActivity.fragmentsToClose.addAll(this.fragmentsToClose);
            twoStepVerificationSetupActivity.addFragmentToClose(this);
            twoStepVerificationSetupActivity.setCurrentEmailCode(str);
            presentFragment(twoStepVerificationSetupActivity, true);
        } else if (tLRPC$TL_error == null || tLRPC$TL_error.text.startsWith("CODE_INVALID")) {
            onFieldError(this.passwordEditText, true);
        } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
            int intValue = Utilities.parseInt(tLRPC$TL_error.text).intValue();
            if (intValue < 60) {
                str2 = LocaleController.formatPluralString("Seconds", intValue);
            } else {
                str2 = LocaleController.formatPluralString("Minutes", intValue / 60);
            }
            showAlertWithText(LocaleController.getString("TwoStepVerificationTitle", NUM), LocaleController.formatString("FloodWaitTime", NUM, str2));
        } else {
            showAlertWithText(LocaleController.getString("TwoStepVerificationTitle", NUM), tLRPC$TL_error.text);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$17 */
    public /* synthetic */ void lambda$null$17$TwoStepVerificationSetupActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error) {
            public final /* synthetic */ TLRPC$TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                TwoStepVerificationSetupActivity.this.lambda$null$16$TwoStepVerificationSetupActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$16 */
    public /* synthetic */ void lambda$null$16$TwoStepVerificationSetupActivity(TLRPC$TL_error tLRPC$TL_error) {
        String str;
        needHideProgress();
        if (tLRPC$TL_error == null) {
            if (getParentActivity() != null) {
                if (this.currentPassword.has_password) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            TwoStepVerificationSetupActivity.this.lambda$null$15$TwoStepVerificationSetupActivity(dialogInterface, i);
                        }
                    });
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
                twoStepVerificationSetupActivity.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, this.emailOnly);
                twoStepVerificationSetupActivity.fragmentsToClose.addAll(this.fragmentsToClose);
                twoStepVerificationSetupActivity.closeAfterSet = this.closeAfterSet;
                presentFragment(twoStepVerificationSetupActivity, true);
                NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
                int i2 = NotificationCenter.twoStepPasswordChanged;
                TLRPC$TL_account_password tLRPC$TL_account_password2 = this.currentPassword;
                instance.postNotificationName(i2, this.currentPasswordHash, tLRPC$TL_account_password2.new_algo, tLRPC$TL_account_password2.new_secure_algo, tLRPC$TL_account_password2.secure_random, this.email, this.hint, null, this.firstPassword);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            }
        } else if (tLRPC$TL_error.text.startsWith("CODE_INVALID")) {
            onFieldError(this.passwordEditText, true);
        } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
            int intValue = Utilities.parseInt(tLRPC$TL_error.text).intValue();
            if (intValue < 60) {
                str = LocaleController.formatPluralString("Seconds", intValue);
            } else {
                str = LocaleController.formatPluralString("Minutes", intValue / 60);
            }
            showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, str));
        } else {
            showAlertWithText(LocaleController.getString("AppName", NUM), tLRPC$TL_error.text);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$15 */
    public /* synthetic */ void lambda$null$15$TwoStepVerificationSetupActivity(DialogInterface dialogInterface, int i) {
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
        presentFragment(twoStepVerificationActivity, true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$20 */
    public /* synthetic */ boolean lambda$createView$20$TwoStepVerificationSetupActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 && i != 6) {
            return false;
        }
        this.buttonTextView.callOnClick();
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$21 */
    public /* synthetic */ void lambda$createView$21$TwoStepVerificationSetupActivity(View view) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$23 */
    public /* synthetic */ void lambda$createView$23$TwoStepVerificationSetupActivity(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("Reset", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                TwoStepVerificationSetupActivity.this.lambda$null$22$TwoStepVerificationSetupActivity(dialogInterface, i);
            }
        });
        builder.setTitle(LocaleController.getString("ResetPassword", NUM));
        builder.setMessage(LocaleController.getString("RestoreEmailTroubleText2", NUM));
        showDialog(builder.create());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$22 */
    public /* synthetic */ void lambda$null$22$TwoStepVerificationSetupActivity(DialogInterface dialogInterface, int i) {
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
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    TwoStepVerificationSetupActivity.this.lambda$onResume$24$TwoStepVerificationSetupActivity();
                }
            }, 200);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onResume$24 */
    public /* synthetic */ void lambda$onResume$24$TwoStepVerificationSetupActivity() {
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
        TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
        if (!tLRPC$TL_account_password.has_recovery) {
            TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(this.currentAccount, 3, tLRPC$TL_account_password);
            twoStepVerificationSetupActivity.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, this.emailOnly);
            twoStepVerificationSetupActivity.firstPassword = this.firstPassword;
            twoStepVerificationSetupActivity.hint = this.hint;
            twoStepVerificationSetupActivity.fragmentsToClose.addAll(this.fragmentsToClose);
            twoStepVerificationSetupActivity.fragmentsToClose.add(this);
            twoStepVerificationSetupActivity.closeAfterSet = this.closeAfterSet;
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
            org.telegram.ui.Components.EditTextBoldCursor r3 = r5.passwordEditText
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
            org.telegram.ui.-$$Lambda$TwoStepVerificationSetupActivity$26L328OcIuu_iKwb4OSDxLLaeW0 r6 = new org.telegram.ui.-$$Lambda$TwoStepVerificationSetupActivity$26L328OcIuu_iKwb4OSDxLLaeW0
            r6.<init>()
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
    /* renamed from: lambda$setRandomMonkeyIdleAnimation$25 */
    public /* synthetic */ void lambda$setRandomMonkeyIdleAnimation$25$TwoStepVerificationSetupActivity() {
        if (this.setAnimationRunnable != null) {
            setRandomMonkeyIdleAnimation(false);
        }
    }

    public void setCloseAfterSet(boolean z) {
        this.closeAfterSet = z;
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        EditTextBoldCursor editTextBoldCursor;
        if (z && (editTextBoldCursor = this.passwordEditText) != null && editTextBoldCursor.getVisibility() == 0) {
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    private void loadPasswordInfo() {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                TwoStepVerificationSetupActivity.this.lambda$loadPasswordInfo$27$TwoStepVerificationSetupActivity(tLObject, tLRPC$TL_error);
            }
        }, 10);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadPasswordInfo$27 */
    public /* synthetic */ void lambda$loadPasswordInfo$27$TwoStepVerificationSetupActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                TwoStepVerificationSetupActivity.this.lambda$null$26$TwoStepVerificationSetupActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$26 */
    public /* synthetic */ void lambda$null$26$TwoStepVerificationSetupActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
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
            org.telegram.ui.-$$Lambda$TwoStepVerificationSetupActivity$t3ohpsFngj7DsPKjmjjAI8OMiVc r1 = new org.telegram.ui.-$$Lambda$TwoStepVerificationSetupActivity$t3ohpsFngj7DsPKjmjjAI8OMiVc
            r1.<init>()
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
            org.telegram.ui.-$$Lambda$TwoStepVerificationSetupActivity$SYIphW3Ks0FrR_5FYvryT5M6TPg r1 = new org.telegram.ui.-$$Lambda$TwoStepVerificationSetupActivity$SYIphW3Ks0FrR_5FYvryT5M6TPg
            r2 = r1
            r3 = r8
            r5 = r9
            r2.<init>(r4, r5, r6, r7)
            r0.postRunnable(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TwoStepVerificationSetupActivity.setNewPassword(boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setNewPassword$29 */
    public /* synthetic */ void lambda$setNewPassword$29$TwoStepVerificationSetupActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error) {
            public final /* synthetic */ TLRPC$TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                TwoStepVerificationSetupActivity.this.lambda$null$28$TwoStepVerificationSetupActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$28 */
    public /* synthetic */ void lambda$null$28$TwoStepVerificationSetupActivity(TLRPC$TL_error tLRPC$TL_error) {
        needHideProgress();
        if (tLRPC$TL_error == null) {
            TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
            TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
            tLRPC$TL_account_password.has_recovery = false;
            tLRPC$TL_account_password.email_unconfirmed_pattern = "";
            twoStepVerificationActivity.setCurrentPasswordParams(tLRPC$TL_account_password, this.currentPasswordHash, this.currentSecretId, this.currentSecret);
            presentFragment(twoStepVerificationActivity, true);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didRemoveTwoStepPassword, new Object[0]);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setNewPassword$35 */
    public /* synthetic */ void lambda$setNewPassword$35$TwoStepVerificationSetupActivity(TLObject tLObject, boolean z, String str, TLRPC$TL_account_passwordInputSettings tLRPC$TL_account_passwordInputSettings) {
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
        $$Lambda$TwoStepVerificationSetupActivity$qRU_ALv4HAWS7wBpHwrHQ7V6ov8 r0 = new RequestDelegate(z, bArr2, str, tLRPC$TL_account_passwordInputSettings) {
            public final /* synthetic */ boolean f$1;
            public final /* synthetic */ byte[] f$2;
            public final /* synthetic */ String f$3;
            public final /* synthetic */ TLRPC$TL_account_passwordInputSettings f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                TwoStepVerificationSetupActivity.this.lambda$null$34$TwoStepVerificationSetupActivity(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
            }
        };
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
                        r0.run((TLObject) null, tLRPC$TL_error);
                    }
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLObject2, r0, 10);
                return;
            }
            TLRPC$TL_error tLRPC$TL_error2 = new TLRPC$TL_error();
            tLRPC$TL_error2.text = "PASSWORD_HASH_INVALID";
            r0.run((TLObject) null, tLRPC$TL_error2);
            return;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLObject2, r0, 10);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$34 */
    public /* synthetic */ void lambda$null$34$TwoStepVerificationSetupActivity(boolean z, byte[] bArr, String str, TLRPC$TL_account_passwordInputSettings tLRPC$TL_account_passwordInputSettings, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, z, tLObject, bArr, str, tLRPC$TL_account_passwordInputSettings) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ boolean f$2;
            public final /* synthetic */ TLObject f$3;
            public final /* synthetic */ byte[] f$4;
            public final /* synthetic */ String f$5;
            public final /* synthetic */ TLRPC$TL_account_passwordInputSettings f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
            }

            public final void run() {
                TwoStepVerificationSetupActivity.this.lambda$null$33$TwoStepVerificationSetupActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: java.lang.Object[]} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* renamed from: lambda$null$33 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$33$TwoStepVerificationSetupActivity(org.telegram.tgnet.TLRPC$TL_error r12, boolean r13, org.telegram.tgnet.TLObject r14, byte[] r15, java.lang.String r16, org.telegram.tgnet.TLRPC$TL_account_passwordInputSettings r17) {
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
            org.telegram.ui.-$$Lambda$TwoStepVerificationSetupActivity$cyz_6LsHXZbEpowbTHCwHax3nc0 r4 = new org.telegram.ui.-$$Lambda$TwoStepVerificationSetupActivity$cyz_6LsHXZbEpowbTHCwHax3nc0
            r4.<init>(r13)
            r3.sendRequest(r1, r4, r5)
            return
        L_0x0027:
            r11.needHideProgress()
            r6 = 7
            r7 = 1
            r8 = 0
            if (r1 != 0) goto L_0x0152
            boolean r9 = r3 instanceof org.telegram.tgnet.TLRPC$TL_boolTrue
            if (r9 != 0) goto L_0x0037
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$auth_Authorization
            if (r3 == 0) goto L_0x0152
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
            goto L_0x024f
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
            r2 = 2131626569(0x7f0e0a49, float:1.8880378E38)
            java.lang.String r3 = "OK"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.-$$Lambda$TwoStepVerificationSetupActivity$XQxenp1vKg-om2_GjO4-rL4nqDA r3 = new org.telegram.ui.-$$Lambda$TwoStepVerificationSetupActivity$XQxenp1vKg-om2_GjO4-rL4nqDA
            r3.<init>(r15)
            r1.setPositiveButton(r2, r3)
            if (r16 != 0) goto L_0x00b8
            org.telegram.tgnet.TLRPC$TL_account_password r2 = r0.currentPassword
            if (r2 == 0) goto L_0x00b8
            boolean r2 = r2.has_password
            if (r2 == 0) goto L_0x00b8
            r2 = 2131628360(0x7f0e1148, float:1.888401E38)
            java.lang.String r3 = "YourEmailSuccessText"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setMessage(r2)
            goto L_0x00c4
        L_0x00b8:
            r2 = 2131628365(0x7f0e114d, float:1.888402E38)
            java.lang.String r3 = "YourPasswordChangedSuccessText"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setMessage(r2)
        L_0x00c4:
            r2 = 2131628370(0x7f0e1152, float:1.888403E38)
            java.lang.String r3 = "YourPasswordSuccess"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setTitle(r2)
            org.telegram.ui.ActionBar.AlertDialog r1 = r1.create()
            android.app.Dialog r1 = r11.showDialog(r1)
            if (r1 == 0) goto L_0x024f
            r1.setCanceledOnTouchOutside(r8)
            r1.setCancelable(r8)
            goto L_0x024f
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
            if (r4 == 0) goto L_0x0126
            r2 = r4
            goto L_0x0128
        L_0x0126:
            byte[] r2 = r0.currentPasswordHash
        L_0x0128:
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
            r11.presentFragment(r1, r7)
            int r1 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.didSetOrRemoveTwoStepPassword
            java.lang.Object[] r3 = new java.lang.Object[r7]
            org.telegram.tgnet.TLRPC$TL_account_password r4 = r0.currentPassword
            r3[r8] = r4
            r1.postNotificationName(r2, r3)
            goto L_0x024f
        L_0x0152:
            if (r1 == 0) goto L_0x024f
            java.lang.String r2 = r1.text
            java.lang.String r3 = "EMAIL_UNCONFIRMED"
            boolean r2 = r3.equals(r2)
            if (r2 != 0) goto L_0x01d1
            java.lang.String r2 = r1.text
            java.lang.String r3 = "EMAIL_UNCONFIRMED_"
            boolean r2 = r2.startsWith(r3)
            if (r2 == 0) goto L_0x0169
            goto L_0x01d1
        L_0x0169:
            java.lang.String r2 = r1.text
            java.lang.String r3 = "EMAIL_INVALID"
            boolean r2 = r3.equals(r2)
            r3 = 2131624285(0x7f0e015d, float:1.8875745E38)
            java.lang.String r4 = "AppName"
            if (r2 == 0) goto L_0x018a
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2 = 2131626838(0x7f0e0b56, float:1.8880923E38)
            java.lang.String r3 = "PasswordEmailInvalid"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r11.showAlertWithText(r1, r2)
            goto L_0x024f
        L_0x018a:
            java.lang.String r2 = r1.text
            java.lang.String r5 = "FLOOD_WAIT"
            boolean r2 = r2.startsWith(r5)
            if (r2 == 0) goto L_0x01c6
            java.lang.String r1 = r1.text
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)
            int r1 = r1.intValue()
            r2 = 60
            if (r1 >= r2) goto L_0x01a9
            java.lang.String r2 = "Seconds"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r2, r1)
            goto L_0x01b0
        L_0x01a9:
            int r1 = r1 / r2
            java.lang.String r2 = "Minutes"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r2, r1)
        L_0x01b0:
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r3 = 2131625596(0x7f0e067c, float:1.8878404E38)
            java.lang.Object[] r4 = new java.lang.Object[r7]
            r4[r8] = r1
            java.lang.String r1 = "FloodWaitTime"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            r11.showAlertWithText(r2, r1)
            goto L_0x024f
        L_0x01c6:
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r3)
            java.lang.String r1 = r1.text
            r11.showAlertWithText(r2, r1)
            goto L_0x024f
        L_0x01d1:
            int r1 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.twoStepPasswordChanged
            java.lang.Object[] r3 = new java.lang.Object[r8]
            r1.postNotificationName(r2, r3)
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r0.fragmentsToClose
            int r1 = r1.size()
            r2 = 0
        L_0x01e5:
            if (r2 >= r1) goto L_0x01f5
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r0.fragmentsToClose
            java.lang.Object r3 = r3.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r3 = (org.telegram.ui.ActionBar.BaseFragment) r3
            r3.removeSelfFromStack()
            int r2 = r2 + 1
            goto L_0x01e5
        L_0x01f5:
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
            if (r4 == 0) goto L_0x0236
            r1 = r4
            goto L_0x0238
        L_0x0236:
            byte[] r1 = r0.currentPasswordHash
        L_0x0238:
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
            r11.presentFragment(r2, r7)
        L_0x024f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TwoStepVerificationSetupActivity.lambda$null$33$TwoStepVerificationSetupActivity(org.telegram.tgnet.TLRPC$TL_error, boolean, org.telegram.tgnet.TLObject, byte[], java.lang.String, org.telegram.tgnet.TLRPC$TL_account_passwordInputSettings):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$31 */
    public /* synthetic */ void lambda$null$31$TwoStepVerificationSetupActivity(boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, z) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ boolean f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                TwoStepVerificationSetupActivity.this.lambda$null$30$TwoStepVerificationSetupActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$30 */
    public /* synthetic */ void lambda$null$30$TwoStepVerificationSetupActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, boolean z) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            this.currentPassword = tLRPC$TL_account_password;
            TwoStepVerificationActivity.initPasswordNewAlgo(tLRPC$TL_account_password);
            setNewPassword(z);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$32 */
    public /* synthetic */ void lambda$null$32$TwoStepVerificationSetupActivity(byte[] bArr, DialogInterface dialogInterface, int i) {
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

    private void onFieldError(TextView textView, boolean z) {
        if (getParentActivity() != null) {
            Vibrator vibrator = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200);
            }
            if (z) {
                textView.setText("");
            }
            AndroidUtilities.shakeView(textView, 2.0f, 0);
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
        arrayList.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        arrayList.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        return arrayList;
    }
}

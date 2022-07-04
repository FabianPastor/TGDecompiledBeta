package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.fingerprint.FingerprintManagerCompat;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.CustomPhoneKeyboardView;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.OutlineTextContainerView;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.TextViewSwitcher;
import org.telegram.ui.Components.TransformableLoginButtonView;
import org.telegram.ui.Components.VerticalPositionAutoAnimator;

public class PasscodeActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int ID_SWITCH_TYPE = 1;
    public static final int TYPE_ENTER_CODE_TO_MANAGE_SETTINGS = 2;
    public static final int TYPE_MANAGE_CODE_SETTINGS = 0;
    public static final int TYPE_SETUP_CODE = 1;
    /* access modifiers changed from: private */
    public int autoLockDetailRow;
    /* access modifiers changed from: private */
    public int autoLockRow;
    /* access modifiers changed from: private */
    public int captureDetailRow;
    /* access modifiers changed from: private */
    public int captureHeaderRow;
    /* access modifiers changed from: private */
    public int captureRow;
    /* access modifiers changed from: private */
    public int changePasscodeRow;
    /* access modifiers changed from: private */
    public CodeFieldContainer codeFieldContainer;
    /* access modifiers changed from: private */
    public int currentPasswordType = 0;
    private TextViewSwitcher descriptionTextSwitcher;
    /* access modifiers changed from: private */
    public int disablePasscodeRow;
    /* access modifiers changed from: private */
    public int fingerprintRow;
    private String firstPassword;
    private VerticalPositionAutoAnimator floatingAutoAnimator;
    /* access modifiers changed from: private */
    public Animator floatingButtonAnimator;
    /* access modifiers changed from: private */
    public FrameLayout floatingButtonContainer;
    private TransformableLoginButtonView floatingButtonIcon;
    /* access modifiers changed from: private */
    public Runnable hidePasscodesDoNotMatch = new PasscodeActivity$$ExternalSyntheticLambda3(this);
    /* access modifiers changed from: private */
    public int hintRow;
    /* access modifiers changed from: private */
    public CustomPhoneKeyboardView keyboardView;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private RLottieImageView lockImageView;
    private Runnable onShowKeyboardCallback;
    private ActionBarMenuItem otherItem;
    private OutlineTextContainerView outlinePasswordView;
    /* access modifiers changed from: private */
    public int passcodeSetStep = 0;
    private TextView passcodesDoNotMatchTextView;
    /* access modifiers changed from: private */
    public ImageView passwordButton;
    /* access modifiers changed from: private */
    public EditTextBoldCursor passwordEditText;
    /* access modifiers changed from: private */
    public boolean postedHidePasscodesDoNotMatch;
    /* access modifiers changed from: private */
    public int rowCount;
    private TextView titleTextView;
    /* access modifiers changed from: private */
    public int type;
    /* access modifiers changed from: private */
    public int utyanRow;

    @Retention(RetentionPolicy.SOURCE)
    public @interface PasscodeActivityType {
    }

    /* renamed from: lambda$new$0$org-telegram-ui-PasscodeActivity  reason: not valid java name */
    public /* synthetic */ void m4011lambda$new$0$orgtelegramuiPasscodeActivity() {
        this.postedHidePasscodesDoNotMatch = false;
        AndroidUtilities.updateViewVisibilityAnimated(this.passcodesDoNotMatchTextView, false);
    }

    public PasscodeActivity(int type2) {
        this.type = type2;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        if (this.type != 0) {
            return true;
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetPasscode);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.type == 0) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public View createView(Context context) {
        final View scrollView;
        final ActionBarMenuSubItem switchItem;
        Context context2 = context;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    PasscodeActivity.this.finishFragment();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context2);
        if (this.type == 0) {
            scrollView = frameLayout;
        } else {
            ScrollView scrollView2 = new ScrollView(context2);
            scrollView2.addView(frameLayout, LayoutHelper.createFrame(-1, -2.0f));
            scrollView2.setFillViewport(true);
            ScrollView scrollView3 = scrollView2;
            scrollView = scrollView2;
        }
        SizeNotifierFrameLayout contentView = new SizeNotifierFrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                int frameBottom;
                if (PasscodeActivity.this.keyboardView.getVisibility() == 8 || measureKeyboardHeight() < AndroidUtilities.dp(20.0f)) {
                    if (PasscodeActivity.this.keyboardView.getVisibility() != 8) {
                        View view = scrollView;
                        int measuredWidth = getMeasuredWidth();
                        int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(230.0f);
                        frameBottom = measuredHeight;
                        view.layout(0, 0, measuredWidth, measuredHeight);
                    } else {
                        View view2 = scrollView;
                        int measuredWidth2 = getMeasuredWidth();
                        int measuredHeight2 = getMeasuredHeight();
                        frameBottom = measuredHeight2;
                        view2.layout(0, 0, measuredWidth2, measuredHeight2);
                    }
                } else if (PasscodeActivity.this.isCustomKeyboardVisible()) {
                    View view3 = scrollView;
                    int measuredWidth3 = getMeasuredWidth();
                    int measuredHeight3 = (getMeasuredHeight() - AndroidUtilities.dp(230.0f)) + measureKeyboardHeight();
                    frameBottom = measuredHeight3;
                    view3.layout(0, 0, measuredWidth3, measuredHeight3);
                } else {
                    View view4 = scrollView;
                    int measuredWidth4 = getMeasuredWidth();
                    int measuredHeight4 = getMeasuredHeight();
                    frameBottom = measuredHeight4;
                    view4.layout(0, 0, measuredWidth4, measuredHeight4);
                }
                PasscodeActivity.this.keyboardView.layout(0, frameBottom, getMeasuredWidth(), AndroidUtilities.dp(230.0f) + frameBottom);
                notifyHeightChanged();
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int width = View.MeasureSpec.getSize(widthMeasureSpec);
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(width, height);
                int frameHeight = height;
                if (PasscodeActivity.this.keyboardView.getVisibility() != 8 && measureKeyboardHeight() < AndroidUtilities.dp(20.0f)) {
                    frameHeight -= AndroidUtilities.dp(230.0f);
                }
                scrollView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(frameHeight, NUM));
                PasscodeActivity.this.keyboardView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(230.0f), NUM));
            }
        };
        contentView.setDelegate(new PasscodeActivity$$ExternalSyntheticLambda15(this));
        this.fragmentView = contentView;
        contentView.addView(scrollView, LayoutHelper.createLinear(-1, 0, 1.0f));
        CustomPhoneKeyboardView customPhoneKeyboardView = new CustomPhoneKeyboardView(context2);
        this.keyboardView = customPhoneKeyboardView;
        customPhoneKeyboardView.setVisibility(isCustomKeyboardVisible() ? 0 : 8);
        contentView.addView(this.keyboardView, LayoutHelper.createLinear(-1, 230));
        switch (this.type) {
            case 0:
                View fragmentContentView = scrollView;
                this.actionBar.setTitle(LocaleController.getString("Passcode", NUM));
                frameLayout.setTag("windowBackgroundGray");
                frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
                RecyclerListView recyclerListView = new RecyclerListView(context2);
                this.listView = recyclerListView;
                recyclerListView.setLayoutManager(new LinearLayoutManager(context2, 1, false) {
                    public boolean supportsPredictiveItemAnimations() {
                        return false;
                    }
                });
                this.listView.setVerticalScrollBarEnabled(false);
                this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
                this.listView.setLayoutAnimation((LayoutAnimationController) null);
                frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
                RecyclerListView recyclerListView2 = this.listView;
                ListAdapter listAdapter2 = new ListAdapter(context2);
                this.listAdapter = listAdapter2;
                recyclerListView2.setAdapter(listAdapter2);
                this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new PasscodeActivity$$ExternalSyntheticLambda14(this));
                break;
            case 1:
            case 2:
                if (this.actionBar != null) {
                    this.actionBar.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    this.actionBar.setBackButtonImage(NUM);
                    this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteBlackText"), false);
                    this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarWhiteSelector"), false);
                    this.actionBar.setCastShadows(false);
                    ActionBarMenu menu = this.actionBar.createMenu();
                    if (this.type == 1) {
                        ActionBarMenuItem addItem = menu.addItem(0, NUM);
                        this.otherItem = addItem;
                        switchItem = addItem.addSubItem(1, NUM, (CharSequence) LocaleController.getString(NUM));
                    } else {
                        switchItem = null;
                    }
                    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
                        public void onItemClick(int id) {
                            if (id == -1) {
                                PasscodeActivity.this.finishFragment();
                                return;
                            }
                            int i = 1;
                            if (id == 1) {
                                PasscodeActivity passcodeActivity = PasscodeActivity.this;
                                if (passcodeActivity.currentPasswordType != 0) {
                                    i = 0;
                                }
                                int unused = passcodeActivity.currentPasswordType = i;
                                AndroidUtilities.runOnUIThread(new PasscodeActivity$4$$ExternalSyntheticLambda0(this, switchItem), 150);
                                PasscodeActivity.this.passwordEditText.setText("");
                                for (CodeNumberField f : PasscodeActivity.this.codeFieldContainer.codeField) {
                                    f.setText("");
                                }
                                PasscodeActivity.this.updateFields();
                            }
                        }

                        /* renamed from: lambda$onItemClick$0$org-telegram-ui-PasscodeActivity$4  reason: not valid java name */
                        public /* synthetic */ void m4020lambda$onItemClick$0$orgtelegramuiPasscodeActivity$4(ActionBarMenuSubItem switchItem) {
                            switchItem.setText(LocaleController.getString(PasscodeActivity.this.currentPasswordType == 0 ? NUM : NUM));
                            switchItem.setIcon(PasscodeActivity.this.currentPasswordType == 0 ? NUM : NUM);
                            PasscodeActivity.this.showKeyboard();
                            if (PasscodeActivity.this.isPinCode()) {
                                PasscodeActivity.this.passwordEditText.setInputType(524417);
                                AndroidUtilities.updateViewVisibilityAnimated(PasscodeActivity.this.passwordButton, true, 0.1f, false);
                            }
                        }
                    });
                }
                FrameLayout codeContainer = new FrameLayout(context2);
                LinearLayout innerLinearLayout = new LinearLayout(context2);
                innerLinearLayout.setOrientation(1);
                innerLinearLayout.setGravity(1);
                frameLayout.addView(innerLinearLayout, LayoutHelper.createFrame(-1, -1.0f));
                RLottieImageView rLottieImageView = new RLottieImageView(context2);
                this.lockImageView = rLottieImageView;
                rLottieImageView.setFocusable(false);
                this.lockImageView.setAnimation(NUM, 120, 120);
                this.lockImageView.setAutoRepeat(false);
                this.lockImageView.playAnimation();
                this.lockImageView.setVisibility((AndroidUtilities.isSmallScreen() || AndroidUtilities.displaySize.x >= AndroidUtilities.displaySize.y) ? 8 : 0);
                innerLinearLayout.addView(this.lockImageView, LayoutHelper.createLinear(120, 120, 1));
                TextView textView = new TextView(context2);
                this.titleTextView = textView;
                textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                if (this.type != 1) {
                    this.titleTextView.setText(LocaleController.getString(NUM));
                } else if (SharedConfig.passcodeHash.length() != 0) {
                    this.titleTextView.setText(LocaleController.getString("EnterNewPasscode", NUM));
                } else {
                    this.titleTextView.setText(LocaleController.getString("CreatePasscode", NUM));
                }
                this.titleTextView.setTextSize(1, 18.0f);
                this.titleTextView.setGravity(1);
                innerLinearLayout.addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 0));
                TextViewSwitcher textViewSwitcher = new TextViewSwitcher(context2);
                this.descriptionTextSwitcher = textViewSwitcher;
                textViewSwitcher.setFactory(new PasscodeActivity$$ExternalSyntheticLambda1(context2));
                this.descriptionTextSwitcher.setInAnimation(context2, NUM);
                this.descriptionTextSwitcher.setOutAnimation(context2, NUM);
                innerLinearLayout.addView(this.descriptionTextSwitcher, LayoutHelper.createLinear(-2, -2, 1, 20, 8, 20, 0));
                TextView forgotPasswordButton = new TextView(context2);
                forgotPasswordButton.setTextSize(1, 14.0f);
                forgotPasswordButton.setTextColor(Theme.getColor("featuredStickers_addButton"));
                forgotPasswordButton.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
                forgotPasswordButton.setGravity((isPassword() ? 3 : 1) | 16);
                forgotPasswordButton.setOnClickListener(new PasscodeActivity$$ExternalSyntheticLambda18(context2));
                forgotPasswordButton.setVisibility(this.type == 2 ? 0 : 8);
                forgotPasswordButton.setText(LocaleController.getString(NUM));
                frameLayout.addView(forgotPasswordButton, LayoutHelper.createFrame(-1, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 81, 0.0f, 0.0f, 0.0f, 16.0f));
                VerticalPositionAutoAnimator.attach(forgotPasswordButton);
                TextView textView2 = new TextView(context2);
                this.passcodesDoNotMatchTextView = textView2;
                textView2.setTextSize(1, 14.0f);
                this.passcodesDoNotMatchTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
                this.passcodesDoNotMatchTextView.setText(LocaleController.getString(NUM));
                this.passcodesDoNotMatchTextView.setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f));
                AndroidUtilities.updateViewVisibilityAnimated(this.passcodesDoNotMatchTextView, false, 1.0f, false);
                frameLayout.addView(this.passcodesDoNotMatchTextView, LayoutHelper.createFrame(-2, -2.0f, 81, 0.0f, 0.0f, 0.0f, 16.0f));
                OutlineTextContainerView outlineTextContainerView = new OutlineTextContainerView(context2);
                this.outlinePasswordView = outlineTextContainerView;
                outlineTextContainerView.setText(LocaleController.getString(NUM));
                EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
                this.passwordEditText = editTextBoldCursor;
                editTextBoldCursor.setInputType(524417);
                this.passwordEditText.setTextSize(1, 18.0f);
                this.passwordEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                this.passwordEditText.setBackground((Drawable) null);
                this.passwordEditText.setMaxLines(1);
                this.passwordEditText.setLines(1);
                this.passwordEditText.setGravity(LocaleController.isRTL ? 5 : 3);
                this.passwordEditText.setSingleLine(true);
                if (this.type == 1) {
                    this.passcodeSetStep = 0;
                    this.passwordEditText.setImeOptions(5);
                } else {
                    this.passcodeSetStep = 1;
                    this.passwordEditText.setImeOptions(6);
                }
                this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.passwordEditText.setTypeface(Typeface.DEFAULT);
                this.passwordEditText.setCursorColor(Theme.getColor("windowBackgroundWhiteInputFieldActivated"));
                this.passwordEditText.setCursorSize(AndroidUtilities.dp(20.0f));
                this.passwordEditText.setCursorWidth(1.5f);
                int padding = AndroidUtilities.dp(16.0f);
                this.passwordEditText.setPadding(padding, padding, padding, padding);
                this.passwordEditText.setOnFocusChangeListener(new PasscodeActivity$$ExternalSyntheticLambda21(this));
                LinearLayout linearLayout = new LinearLayout(context2);
                linearLayout.setOrientation(0);
                linearLayout.setGravity(16);
                linearLayout.addView(this.passwordEditText, LayoutHelper.createLinear(0, -2, 1.0f));
                ImageView imageView = new ImageView(context2);
                this.passwordButton = imageView;
                imageView.setImageResource(NUM);
                this.passwordButton.setColorFilter(Theme.getColor("windowBackgroundWhiteHintText"));
                this.passwordButton.setBackground(Theme.createSelectorDrawable(getThemedColor("listSelectorSDK21"), 1));
                AndroidUtilities.updateViewVisibilityAnimated(this.passwordButton, this.type == 1 && this.passcodeSetStep == 0, 0.1f, false);
                final AtomicBoolean isPasswordShown = new AtomicBoolean(false);
                this.passwordEditText.addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(Editable s) {
                        if (PasscodeActivity.this.type != 1 || PasscodeActivity.this.passcodeSetStep != 0) {
                            return;
                        }
                        if (TextUtils.isEmpty(s) && PasscodeActivity.this.passwordButton.getVisibility() != 8) {
                            if (isPasswordShown.get()) {
                                PasscodeActivity.this.passwordButton.callOnClick();
                            }
                            AndroidUtilities.updateViewVisibilityAnimated(PasscodeActivity.this.passwordButton, false, 0.1f, true);
                        } else if (!TextUtils.isEmpty(s) && PasscodeActivity.this.passwordButton.getVisibility() != 0) {
                            AndroidUtilities.updateViewVisibilityAnimated(PasscodeActivity.this.passwordButton, true, 0.1f, true);
                        }
                    }
                });
                this.passwordButton.setOnClickListener(new PasscodeActivity$$ExternalSyntheticLambda20(this, isPasswordShown));
                linearLayout.addView(this.passwordButton, LayoutHelper.createLinearRelatively(24.0f, 24.0f, 0, 0.0f, 0.0f, 14.0f, 0.0f));
                this.outlinePasswordView.addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f));
                codeContainer.addView(this.outlinePasswordView, LayoutHelper.createLinear(-1, -2, 1, 32, 0, 32, 0));
                this.passwordEditText.setOnEditorActionListener(new PasscodeActivity$$ExternalSyntheticLambda23(this));
                this.passwordEditText.addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if (PasscodeActivity.this.postedHidePasscodesDoNotMatch) {
                            PasscodeActivity.this.codeFieldContainer.removeCallbacks(PasscodeActivity.this.hidePasscodesDoNotMatch);
                            PasscodeActivity.this.hidePasscodesDoNotMatch.run();
                        }
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(Editable s) {
                    }
                });
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
                AnonymousClass8 r4 = new CodeFieldContainer(context2) {
                    /* access modifiers changed from: protected */
                    public void processNextPressed() {
                        if (PasscodeActivity.this.passcodeSetStep == 0) {
                            postDelayed(new PasscodeActivity$8$$ExternalSyntheticLambda0(this), 260);
                        } else {
                            PasscodeActivity.this.processDone();
                        }
                    }

                    /* renamed from: lambda$processNextPressed$0$org-telegram-ui-PasscodeActivity$8  reason: not valid java name */
                    public /* synthetic */ void m4021lambda$processNextPressed$0$orgtelegramuiPasscodeActivity$8() {
                        PasscodeActivity.this.processNext();
                    }
                };
                this.codeFieldContainer = r4;
                r4.setNumbersCount(4, 10);
                CodeNumberField[] codeNumberFieldArr = this.codeFieldContainer.codeField;
                int length = codeNumberFieldArr.length;
                int i = 0;
                View fragmentContentView2 = scrollView;
                while (i < length) {
                    CodeNumberField f = codeNumberFieldArr[i];
                    f.setShowSoftInputOnFocusCompat(!isCustomKeyboardVisible());
                    f.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    f.setTextSize(1, 24.0f);
                    f.addTextChangedListener(new TextWatcher() {
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            if (PasscodeActivity.this.postedHidePasscodesDoNotMatch) {
                                PasscodeActivity.this.codeFieldContainer.removeCallbacks(PasscodeActivity.this.hidePasscodesDoNotMatch);
                                PasscodeActivity.this.hidePasscodesDoNotMatch.run();
                            }
                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        public void afterTextChanged(Editable s) {
                        }
                    });
                    f.setOnFocusChangeListener(new PasscodeActivity$$ExternalSyntheticLambda22(this, f));
                    i++;
                    codeNumberFieldArr = codeNumberFieldArr;
                    fragmentContentView2 = fragmentContentView2;
                }
                codeContainer.addView(this.codeFieldContainer, LayoutHelper.createFrame(-2, -2.0f, 1, 40.0f, 10.0f, 40.0f, 0.0f));
                innerLinearLayout.addView(codeContainer, LayoutHelper.createLinear(-1, -2, 1, 0, 32, 0, 72));
                if (this.type == 1) {
                    frameLayout.setTag("windowBackgroundWhite");
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
                frameLayout.addView(this.floatingButtonContainer, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 85, 0.0f, 0.0f, 24.0f, 16.0f));
                this.floatingButtonContainer.setOnClickListener(new PasscodeActivity$$ExternalSyntheticLambda19(this));
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
                updateFields();
                break;
            default:
                FrameLayout frameLayout2 = scrollView;
                break;
        }
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-PasscodeActivity  reason: not valid java name */
    public /* synthetic */ void m4002lambda$createView$1$orgtelegramuiPasscodeActivity(int keyboardHeight, boolean isWidthGreater) {
        Runnable runnable;
        if (keyboardHeight >= AndroidUtilities.dp(20.0f) && (runnable = this.onShowKeyboardCallback) != null) {
            runnable.run();
            this.onShowKeyboardCallback = null;
        }
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-PasscodeActivity  reason: not valid java name */
    public /* synthetic */ void m4008lambda$createView$5$orgtelegramuiPasscodeActivity(View view, int position) {
        if (view.isEnabled()) {
            if (position == this.disablePasscodeRow) {
                AlertDialog alertDialog = new AlertDialog.Builder((Context) getParentActivity()).setTitle(LocaleController.getString(NUM)).setMessage(LocaleController.getString(NUM)).setNegativeButton(LocaleController.getString(NUM), (DialogInterface.OnClickListener) null).setPositiveButton(LocaleController.getString(NUM), new PasscodeActivity$$ExternalSyntheticLambda16(this)).create();
                alertDialog.show();
                ((TextView) alertDialog.getButton(-1)).setTextColor(Theme.getColor("dialogTextRed"));
            } else if (position == this.changePasscodeRow) {
                presentFragment(new PasscodeActivity(1));
            } else if (position == this.autoLockRow) {
                if (getParentActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setTitle(LocaleController.getString("AutoLock", NUM));
                    NumberPicker numberPicker = new NumberPicker(getParentActivity());
                    numberPicker.setMinValue(0);
                    numberPicker.setMaxValue(4);
                    if (SharedConfig.autoLockIn == 0) {
                        numberPicker.setValue(0);
                    } else if (SharedConfig.autoLockIn == 60) {
                        numberPicker.setValue(1);
                    } else if (SharedConfig.autoLockIn == 300) {
                        numberPicker.setValue(2);
                    } else if (SharedConfig.autoLockIn == 3600) {
                        numberPicker.setValue(3);
                    } else if (SharedConfig.autoLockIn == 18000) {
                        numberPicker.setValue(4);
                    }
                    numberPicker.setFormatter(PasscodeActivity$$ExternalSyntheticLambda13.INSTANCE);
                    builder.setView(numberPicker);
                    builder.setNegativeButton(LocaleController.getString("Done", NUM), new PasscodeActivity$$ExternalSyntheticLambda17(this, numberPicker, position));
                    showDialog(builder.create());
                }
            } else if (position == this.fingerprintRow) {
                SharedConfig.useFingerprint = !SharedConfig.useFingerprint;
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                ((TextCheckCell) view).setChecked(SharedConfig.useFingerprint);
            } else if (position == this.captureRow) {
                SharedConfig.allowScreenCapture = !SharedConfig.allowScreenCapture;
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                ((TextCheckCell) view).setChecked(SharedConfig.allowScreenCapture);
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, false);
                if (!SharedConfig.allowScreenCapture) {
                    AlertsCreator.showSimpleAlert(this, LocaleController.getString("ScreenCaptureAlert", NUM));
                }
            }
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-PasscodeActivity  reason: not valid java name */
    public /* synthetic */ void m4006lambda$createView$2$orgtelegramuiPasscodeActivity(DialogInterface dialog, int which) {
        SharedConfig.passcodeHash = "";
        SharedConfig.appLocked = false;
        SharedConfig.saveConfig();
        getMediaDataController().buildShortcuts();
        int count = this.listView.getChildCount();
        int a = 0;
        while (true) {
            if (a >= count) {
                break;
            }
            View child = this.listView.getChildAt(a);
            if (child instanceof TextSettingsCell) {
                ((TextSettingsCell) child).setTextColor(Theme.getColor("windowBackgroundWhiteGrayText7"));
                break;
            }
            a++;
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
        finishFragment();
    }

    static /* synthetic */ String lambda$createView$3(int value) {
        if (value == 0) {
            return LocaleController.getString("AutoLockDisabled", NUM);
        }
        if (value == 1) {
            return LocaleController.formatString("AutoLockInTime", NUM, LocaleController.formatPluralString("Minutes", 1, new Object[0]));
        } else if (value == 2) {
            return LocaleController.formatString("AutoLockInTime", NUM, LocaleController.formatPluralString("Minutes", 5, new Object[0]));
        } else if (value == 3) {
            return LocaleController.formatString("AutoLockInTime", NUM, LocaleController.formatPluralString("Hours", 1, new Object[0]));
        } else if (value != 4) {
            return "";
        } else {
            return LocaleController.formatString("AutoLockInTime", NUM, LocaleController.formatPluralString("Hours", 5, new Object[0]));
        }
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-PasscodeActivity  reason: not valid java name */
    public /* synthetic */ void m4007lambda$createView$4$orgtelegramuiPasscodeActivity(NumberPicker numberPicker, int position, DialogInterface dialog, int which) {
        int which2 = numberPicker.getValue();
        if (which2 == 0) {
            SharedConfig.autoLockIn = 0;
        } else if (which2 == 1) {
            SharedConfig.autoLockIn = 60;
        } else if (which2 == 2) {
            SharedConfig.autoLockIn = 300;
        } else if (which2 == 3) {
            SharedConfig.autoLockIn = 3600;
        } else if (which2 == 4) {
            SharedConfig.autoLockIn = 18000;
        }
        this.listAdapter.notifyItemChanged(position);
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
    }

    static /* synthetic */ View lambda$createView$6(Context context) {
        TextView tv = new TextView(context);
        tv.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        tv.setGravity(1);
        tv.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        tv.setTextSize(1, 15.0f);
        return tv;
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-PasscodeActivity  reason: not valid java name */
    public /* synthetic */ void m4009lambda$createView$8$orgtelegramuiPasscodeActivity(View v, boolean hasFocus) {
        this.outlinePasswordView.animateSelection(hasFocus ? 1.0f : 0.0f);
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-PasscodeActivity  reason: not valid java name */
    public /* synthetic */ void m4010lambda$createView$9$orgtelegramuiPasscodeActivity(AtomicBoolean isPasswordShown, View v) {
        isPasswordShown.set(!isPasswordShown.get());
        int selectionStart = this.passwordEditText.getSelectionStart();
        int selectionEnd = this.passwordEditText.getSelectionEnd();
        this.passwordEditText.setInputType((isPasswordShown.get() ? 144 : 128) | 1);
        this.passwordEditText.setSelection(selectionStart, selectionEnd);
        this.passwordButton.setColorFilter(Theme.getColor(isPasswordShown.get() ? "windowBackgroundWhiteInputFieldActivated" : "windowBackgroundWhiteHintText"));
    }

    /* renamed from: lambda$createView$10$org-telegram-ui-PasscodeActivity  reason: not valid java name */
    public /* synthetic */ boolean m4003lambda$createView$10$orgtelegramuiPasscodeActivity(TextView textView, int i, KeyEvent keyEvent) {
        int i2 = this.passcodeSetStep;
        if (i2 == 0) {
            processNext();
            return true;
        } else if (i2 != 1) {
            return false;
        } else {
            processDone();
            return true;
        }
    }

    /* renamed from: lambda$createView$11$org-telegram-ui-PasscodeActivity  reason: not valid java name */
    public /* synthetic */ void m4004lambda$createView$11$orgtelegramuiPasscodeActivity(CodeNumberField f, View v, boolean hasFocus) {
        this.keyboardView.setEditText(f);
        this.keyboardView.setDispatchBackWhenEmpty(true);
    }

    /* renamed from: lambda$createView$12$org-telegram-ui-PasscodeActivity  reason: not valid java name */
    public /* synthetic */ void m4005lambda$createView$12$orgtelegramuiPasscodeActivity(View view) {
        int i = this.type;
        if (i == 1) {
            if (this.passcodeSetStep == 0) {
                processNext();
            } else {
                processDone();
            }
        } else if (i == 2) {
            processDone();
        }
    }

    public boolean hasForceLightStatusBar() {
        return this.type != 0;
    }

    private void setCustomKeyboardVisible(final boolean visible, boolean animate) {
        if (visible) {
            AndroidUtilities.hideKeyboard(this.fragmentView);
            AndroidUtilities.requestAltFocusable(getParentActivity(), this.classGuid);
        } else {
            AndroidUtilities.removeAltFocusable(getParentActivity(), this.classGuid);
        }
        int i = 0;
        float f = 1.0f;
        float f2 = 0.0f;
        if (!animate) {
            CustomPhoneKeyboardView customPhoneKeyboardView = this.keyboardView;
            if (!visible) {
                i = 8;
            }
            customPhoneKeyboardView.setVisibility(i);
            CustomPhoneKeyboardView customPhoneKeyboardView2 = this.keyboardView;
            if (!visible) {
                f = 0.0f;
            }
            customPhoneKeyboardView2.setAlpha(f);
            CustomPhoneKeyboardView customPhoneKeyboardView3 = this.keyboardView;
            if (!visible) {
                f2 = (float) AndroidUtilities.dp(230.0f);
            }
            customPhoneKeyboardView3.setTranslationY(f2);
            this.fragmentView.requestLayout();
            return;
        }
        float[] fArr = new float[2];
        fArr[0] = visible ? 0.0f : 1.0f;
        if (!visible) {
            f = 0.0f;
        }
        fArr[1] = f;
        ValueAnimator animator = ValueAnimator.ofFloat(fArr).setDuration(150);
        animator.setInterpolator(visible ? CubicBezierInterpolator.DEFAULT : Easings.easeInOutQuad);
        animator.addUpdateListener(new PasscodeActivity$$ExternalSyntheticLambda0(this));
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
                if (visible) {
                    PasscodeActivity.this.keyboardView.setVisibility(0);
                }
            }

            public void onAnimationEnd(Animator animation) {
                if (!visible) {
                    PasscodeActivity.this.keyboardView.setVisibility(8);
                }
            }
        });
        animator.start();
    }

    /* renamed from: lambda$setCustomKeyboardVisible$13$org-telegram-ui-PasscodeActivity  reason: not valid java name */
    public /* synthetic */ void m4017x97ad0356(ValueAnimator animation) {
        float val = ((Float) animation.getAnimatedValue()).floatValue();
        this.keyboardView.setAlpha(val);
        this.keyboardView.setTranslationY((1.0f - val) * ((float) AndroidUtilities.dp(230.0f)) * 0.75f);
        this.fragmentView.requestLayout();
    }

    private void setFloatingButtonVisible(final boolean visible, boolean animate) {
        Animator animator = this.floatingButtonAnimator;
        if (animator != null) {
            animator.cancel();
            this.floatingButtonAnimator = null;
        }
        int i = 0;
        float f = 1.0f;
        if (!animate) {
            this.floatingAutoAnimator.setOffsetY(visible ? 0.0f : (float) AndroidUtilities.dp(70.0f));
            FrameLayout frameLayout = this.floatingButtonContainer;
            if (!visible) {
                f = 0.0f;
            }
            frameLayout.setAlpha(f);
            FrameLayout frameLayout2 = this.floatingButtonContainer;
            if (!visible) {
                i = 8;
            }
            frameLayout2.setVisibility(i);
            return;
        }
        float[] fArr = new float[2];
        fArr[0] = visible ? 0.0f : 1.0f;
        if (!visible) {
            f = 0.0f;
        }
        fArr[1] = f;
        ValueAnimator animator2 = ValueAnimator.ofFloat(fArr).setDuration(150);
        animator2.setInterpolator(visible ? AndroidUtilities.decelerateInterpolator : AndroidUtilities.accelerateInterpolator);
        animator2.addUpdateListener(new PasscodeActivity$$ExternalSyntheticLambda11(this));
        animator2.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
                if (visible) {
                    PasscodeActivity.this.floatingButtonContainer.setVisibility(0);
                }
            }

            public void onAnimationEnd(Animator animation) {
                if (!visible) {
                    PasscodeActivity.this.floatingButtonContainer.setVisibility(8);
                }
                if (PasscodeActivity.this.floatingButtonAnimator == animation) {
                    Animator unused = PasscodeActivity.this.floatingButtonAnimator = null;
                }
            }
        });
        animator2.start();
        this.floatingButtonAnimator = animator2;
    }

    /* renamed from: lambda$setFloatingButtonVisible$14$org-telegram-ui-PasscodeActivity  reason: not valid java name */
    public /* synthetic */ void m4018xvar_avar_(ValueAnimator animation) {
        float val = ((Float) animation.getAnimatedValue()).floatValue();
        this.floatingAutoAnimator.setOffsetY(((float) AndroidUtilities.dp(70.0f)) * (1.0f - val));
        this.floatingButtonContainer.setAlpha(val);
    }

    public static BaseFragment determineOpenFragment() {
        if (SharedConfig.passcodeHash.length() != 0) {
            return new PasscodeActivity(2);
        }
        return new ActionIntroActivity(6);
    }

    private void animateSuccessAnimation(Runnable callback) {
        if (!isPinCode()) {
            callback.run();
            return;
        }
        for (int i = 0; i < this.codeFieldContainer.codeField.length; i++) {
            CodeNumberField field = this.codeFieldContainer.codeField[i];
            field.postDelayed(new PasscodeActivity$$ExternalSyntheticLambda2(field), ((long) i) * 75);
        }
        this.codeFieldContainer.postDelayed(new PasscodeActivity$$ExternalSyntheticLambda9(this, callback), (((long) this.codeFieldContainer.codeField.length) * 75) + 350);
    }

    /* renamed from: lambda$animateSuccessAnimation$16$org-telegram-ui-PasscodeActivity  reason: not valid java name */
    public /* synthetic */ void m4001x5ef7bfc3(Runnable callback) {
        for (CodeNumberField f : this.codeFieldContainer.codeField) {
            f.animateSuccessProgress(0.0f);
        }
        callback.run();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setCustomKeyboardVisible(isCustomKeyboardVisible(), false);
        RLottieImageView rLottieImageView = this.lockImageView;
        if (rLottieImageView != null) {
            rLottieImageView.setVisibility((AndroidUtilities.isSmallScreen() || AndroidUtilities.displaySize.x >= AndroidUtilities.displaySize.y) ? 8 : 0);
        }
        CodeNumberField[] codeNumberFieldArr = this.codeFieldContainer.codeField;
        int length = codeNumberFieldArr.length;
        for (int i = 0; i < length; i++) {
            codeNumberFieldArr[i].setShowSoftInputOnFocusCompat(!isCustomKeyboardVisible());
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        if (this.type != 0 && !isCustomKeyboardVisible()) {
            AndroidUtilities.runOnUIThread(new PasscodeActivity$$ExternalSyntheticLambda8(this), 200);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        if (isCustomKeyboardVisible()) {
            AndroidUtilities.hideKeyboard(this.fragmentView);
            AndroidUtilities.requestAltFocusable(getParentActivity(), this.classGuid);
        }
    }

    public void onPause() {
        super.onPause();
        AndroidUtilities.removeAltFocusable(getParentActivity(), this.classGuid);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id != NotificationCenter.didSetPasscode) {
            return;
        }
        if ((args.length == 0 || args[0].booleanValue()) && this.type == 0) {
            updateRows();
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.utyanRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.hintRow = i;
        this.rowCount = i2 + 1;
        this.changePasscodeRow = i2;
        try {
            if (Build.VERSION.SDK_INT < 23) {
                this.fingerprintRow = -1;
            } else if (!FingerprintManagerCompat.from(ApplicationLoader.applicationContext).isHardwareDetected() || !AndroidUtilities.isKeyguardSecure()) {
                this.fingerprintRow = -1;
            } else {
                int i3 = this.rowCount;
                this.rowCount = i3 + 1;
                this.fingerprintRow = i3;
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
        int i4 = this.rowCount;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.autoLockRow = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.autoLockDetailRow = i5;
        int i7 = i6 + 1;
        this.rowCount = i7;
        this.captureHeaderRow = i6;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.captureRow = i7;
        int i9 = i8 + 1;
        this.rowCount = i9;
        this.captureDetailRow = i8;
        this.rowCount = i9 + 1;
        this.disablePasscodeRow = i9;
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && this.type != 0) {
            showKeyboard();
        }
    }

    /* access modifiers changed from: private */
    public void showKeyboard() {
        if (isPinCode()) {
            this.codeFieldContainer.codeField[0].requestFocus();
            if (!isCustomKeyboardVisible()) {
                AndroidUtilities.showKeyboard(this.codeFieldContainer.codeField[0]);
            }
        } else if (isPassword()) {
            this.passwordEditText.requestFocus();
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    /* access modifiers changed from: private */
    public void updateFields() {
        String text;
        int i = NUM;
        if (this.type == 2) {
            text = LocaleController.getString(NUM);
        } else if (this.passcodeSetStep == 0) {
            text = LocaleController.getString(this.currentPasswordType == 0 ? NUM : NUM);
        } else {
            text = this.descriptionTextSwitcher.getCurrentView().getText().toString();
        }
        boolean animate = !this.descriptionTextSwitcher.getCurrentView().getText().equals(text) && !TextUtils.isEmpty(this.descriptionTextSwitcher.getCurrentView().getText());
        if (this.type == 2) {
            this.descriptionTextSwitcher.setText(LocaleController.getString(NUM), animate);
        } else if (this.passcodeSetStep == 0) {
            TextViewSwitcher textViewSwitcher = this.descriptionTextSwitcher;
            if (this.currentPasswordType != 0) {
                i = NUM;
            }
            textViewSwitcher.setText(LocaleController.getString(i), animate);
        }
        if (isPinCode()) {
            AndroidUtilities.updateViewVisibilityAnimated(this.codeFieldContainer, true, 1.0f, animate);
            AndroidUtilities.updateViewVisibilityAnimated(this.outlinePasswordView, false, 1.0f, animate);
        } else if (isPassword()) {
            AndroidUtilities.updateViewVisibilityAnimated(this.codeFieldContainer, false, 1.0f, animate);
            AndroidUtilities.updateViewVisibilityAnimated(this.outlinePasswordView, true, 1.0f, animate);
        }
        boolean show = isPassword();
        if (show) {
            PasscodeActivity$$ExternalSyntheticLambda12 passcodeActivity$$ExternalSyntheticLambda12 = new PasscodeActivity$$ExternalSyntheticLambda12(this, show, animate);
            this.onShowKeyboardCallback = passcodeActivity$$ExternalSyntheticLambda12;
            AndroidUtilities.runOnUIThread(passcodeActivity$$ExternalSyntheticLambda12, 3000);
        } else {
            setFloatingButtonVisible(show, animate);
        }
        setCustomKeyboardVisible(isCustomKeyboardVisible(), animate);
        showKeyboard();
    }

    /* renamed from: lambda$updateFields$17$org-telegram-ui-PasscodeActivity  reason: not valid java name */
    public /* synthetic */ void m4019lambda$updateFields$17$orgtelegramuiPasscodeActivity(boolean show, boolean animate) {
        setFloatingButtonVisible(show, animate);
        AndroidUtilities.cancelRunOnUIThread(this.onShowKeyboardCallback);
    }

    /* access modifiers changed from: private */
    public boolean isCustomKeyboardVisible() {
        return isPinCode() && this.type != 0 && !AndroidUtilities.isTablet() && AndroidUtilities.displaySize.x < AndroidUtilities.displaySize.y && !AndroidUtilities.isAccessibilityTouchExplorationEnabled();
    }

    /* access modifiers changed from: private */
    public void processNext() {
        if (!(this.currentPasswordType == 1 && this.passwordEditText.getText().length() == 0) && (this.currentPasswordType != 0 || this.codeFieldContainer.getCode().length() == 4)) {
            ActionBarMenuItem actionBarMenuItem = this.otherItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setVisibility(8);
            }
            this.titleTextView.setText(LocaleController.getString("ConfirmCreatePasscode", NUM));
            this.descriptionTextSwitcher.setText(AndroidUtilities.replaceTags(LocaleController.getString("PasscodeReinstallNotice", NUM)));
            this.firstPassword = isPinCode() ? this.codeFieldContainer.getCode() : this.passwordEditText.getText().toString();
            this.passwordEditText.setText("");
            this.passwordEditText.setInputType(524417);
            for (CodeNumberField f : this.codeFieldContainer.codeField) {
                f.setText("");
            }
            showKeyboard();
            this.passcodeSetStep = 1;
            return;
        }
        onPasscodeError();
    }

    /* access modifiers changed from: private */
    public boolean isPinCode() {
        int i = this.type;
        if (i == 1 && this.currentPasswordType == 0) {
            return true;
        }
        return i == 2 && SharedConfig.passcodeType == 0;
    }

    private boolean isPassword() {
        int i = this.type;
        if (i == 1 && this.currentPasswordType == 1) {
            return true;
        }
        return i == 2 && SharedConfig.passcodeType == 1;
    }

    /* access modifiers changed from: private */
    public void processDone() {
        if (!isPassword() || this.passwordEditText.getText().length() != 0) {
            String password = isPinCode() ? this.codeFieldContainer.getCode() : this.passwordEditText.getText().toString();
            int i = this.type;
            int i2 = 0;
            if (i == 1) {
                if (!this.firstPassword.equals(password)) {
                    AndroidUtilities.updateViewVisibilityAnimated(this.passcodesDoNotMatchTextView, true);
                    for (CodeNumberField f : this.codeFieldContainer.codeField) {
                        f.setText("");
                    }
                    if (isPinCode()) {
                        this.codeFieldContainer.codeField[0].requestFocus();
                    }
                    this.passwordEditText.setText("");
                    onPasscodeError();
                    this.codeFieldContainer.removeCallbacks(this.hidePasscodesDoNotMatch);
                    this.codeFieldContainer.post(new PasscodeActivity$$ExternalSyntheticLambda6(this));
                    return;
                }
                boolean isFirst = SharedConfig.passcodeHash.length() == 0;
                try {
                    SharedConfig.passcodeSalt = new byte[16];
                    Utilities.random.nextBytes(SharedConfig.passcodeSalt);
                    byte[] passcodeBytes = this.firstPassword.getBytes("UTF-8");
                    byte[] bytes = new byte[(passcodeBytes.length + 32)];
                    System.arraycopy(SharedConfig.passcodeSalt, 0, bytes, 0, 16);
                    System.arraycopy(passcodeBytes, 0, bytes, 16, passcodeBytes.length);
                    System.arraycopy(SharedConfig.passcodeSalt, 0, bytes, passcodeBytes.length + 16, 16);
                    SharedConfig.passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(bytes, 0, (long) bytes.length));
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                SharedConfig.allowScreenCapture = true;
                SharedConfig.passcodeType = this.currentPasswordType;
                SharedConfig.saveConfig();
                this.passwordEditText.clearFocus();
                AndroidUtilities.hideKeyboard(this.passwordEditText);
                CodeNumberField[] codeNumberFieldArr = this.codeFieldContainer.codeField;
                int length = codeNumberFieldArr.length;
                while (i2 < length) {
                    CodeNumberField f2 = codeNumberFieldArr[i2];
                    f2.clearFocus();
                    AndroidUtilities.hideKeyboard(f2);
                    i2++;
                }
                this.keyboardView.setEditText((EditText) null);
                animateSuccessAnimation(new PasscodeActivity$$ExternalSyntheticLambda10(this, isFirst));
            } else if (i != 2) {
            } else {
                if (SharedConfig.passcodeRetryInMs > 0) {
                    double d = (double) SharedConfig.passcodeRetryInMs;
                    Double.isNaN(d);
                    Toast.makeText(getParentActivity(), LocaleController.formatString("TooManyTries", NUM, LocaleController.formatPluralString("Seconds", Math.max(1, (int) Math.ceil(d / 1000.0d)), new Object[0])), 0).show();
                    for (CodeNumberField f3 : this.codeFieldContainer.codeField) {
                        f3.setText("");
                    }
                    this.passwordEditText.setText("");
                    if (isPinCode()) {
                        this.codeFieldContainer.codeField[0].requestFocus();
                    }
                    onPasscodeError();
                } else if (SharedConfig.checkPasscode(password) == 0) {
                    SharedConfig.increaseBadPasscodeTries();
                    this.passwordEditText.setText("");
                    for (CodeNumberField f4 : this.codeFieldContainer.codeField) {
                        f4.setText("");
                    }
                    if (isPinCode()) {
                        this.codeFieldContainer.codeField[0].requestFocus();
                    }
                    onPasscodeError();
                } else {
                    SharedConfig.badPasscodeTries = 0;
                    SharedConfig.saveConfig();
                    this.passwordEditText.clearFocus();
                    AndroidUtilities.hideKeyboard(this.passwordEditText);
                    CodeNumberField[] codeNumberFieldArr2 = this.codeFieldContainer.codeField;
                    int length2 = codeNumberFieldArr2.length;
                    while (i2 < length2) {
                        CodeNumberField f5 = codeNumberFieldArr2[i2];
                        f5.clearFocus();
                        AndroidUtilities.hideKeyboard(f5);
                        i2++;
                    }
                    this.keyboardView.setEditText((EditText) null);
                    animateSuccessAnimation(new PasscodeActivity$$ExternalSyntheticLambda7(this));
                }
            }
        } else {
            onPasscodeError();
        }
    }

    /* renamed from: lambda$processDone$18$org-telegram-ui-PasscodeActivity  reason: not valid java name */
    public /* synthetic */ void m4014lambda$processDone$18$orgtelegramuiPasscodeActivity() {
        this.codeFieldContainer.postDelayed(this.hidePasscodesDoNotMatch, 3000);
        this.postedHidePasscodesDoNotMatch = true;
    }

    /* renamed from: lambda$processDone$19$org-telegram-ui-PasscodeActivity  reason: not valid java name */
    public /* synthetic */ void m4015lambda$processDone$19$orgtelegramuiPasscodeActivity(boolean isFirst) {
        getMediaDataController().buildShortcuts();
        if (isFirst) {
            presentFragment(new PasscodeActivity(0), true);
        } else {
            finishFragment();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
    }

    /* renamed from: lambda$processDone$20$org-telegram-ui-PasscodeActivity  reason: not valid java name */
    public /* synthetic */ void m4016lambda$processDone$20$orgtelegramuiPasscodeActivity() {
        presentFragment(new PasscodeActivity(0), true);
    }

    private void onPasscodeError() {
        if (getParentActivity() != null) {
            try {
                this.fragmentView.performHapticFeedback(3, 2);
            } catch (Exception e) {
            }
            if (isPinCode()) {
                for (CodeNumberField f : this.codeFieldContainer.codeField) {
                    f.animateErrorProgress(1.0f);
                }
            } else {
                this.outlinePasswordView.animateError(1.0f);
            }
            AndroidUtilities.shakeViewSpring(isPinCode() ? this.codeFieldContainer : this.outlinePasswordView, isPinCode() ? 10.0f : 4.0f, new PasscodeActivity$$ExternalSyntheticLambda5(this));
        }
    }

    /* renamed from: lambda$onPasscodeError$22$org-telegram-ui-PasscodeActivity  reason: not valid java name */
    public /* synthetic */ void m4013lambda$onPasscodeError$22$orgtelegramuiPasscodeActivity() {
        AndroidUtilities.runOnUIThread(new PasscodeActivity$$ExternalSyntheticLambda4(this), isPinCode() ? 150 : 1000);
    }

    /* renamed from: lambda$onPasscodeError$21$org-telegram-ui-PasscodeActivity  reason: not valid java name */
    public /* synthetic */ void m4012lambda$onPasscodeError$21$orgtelegramuiPasscodeActivity() {
        if (isPinCode()) {
            for (CodeNumberField f : this.codeFieldContainer.codeField) {
                f.animateErrorProgress(0.0f);
            }
            return;
        }
        this.outlinePasswordView.animateError(0.0f);
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private static final int VIEW_TYPE_CHECK = 0;
        private static final int VIEW_TYPE_HEADER = 3;
        private static final int VIEW_TYPE_INFO = 2;
        private static final int VIEW_TYPE_SETTING = 1;
        private static final int VIEW_TYPE_UTYAN = 4;
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == PasscodeActivity.this.fingerprintRow || position == PasscodeActivity.this.autoLockRow || position == PasscodeActivity.this.captureRow || position == PasscodeActivity.this.changePasscodeRow || position == PasscodeActivity.this.disablePasscodeRow;
        }

        public int getItemCount() {
            return PasscodeActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    View view2 = new TextCheckCell(this.mContext);
                    view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view2;
                    break;
                case 1:
                    View view3 = new TextSettingsCell(this.mContext);
                    view3.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view3;
                    break;
                case 3:
                    View view4 = new HeaderCell(this.mContext);
                    view4.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view4;
                    break;
                case 4:
                    view = new RLottieImageHolderView(this.mContext);
                    break;
                default:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String val;
            switch (holder.getItemViewType()) {
                case 0:
                    TextCheckCell textCell = (TextCheckCell) holder.itemView;
                    if (position == PasscodeActivity.this.fingerprintRow) {
                        textCell.setTextAndCheck(LocaleController.getString("UnlockFingerprint", NUM), SharedConfig.useFingerprint, true);
                        return;
                    } else if (position == PasscodeActivity.this.captureRow) {
                        textCell.setTextAndCheck(LocaleController.getString(NUM), SharedConfig.allowScreenCapture, false);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextSettingsCell textCell2 = (TextSettingsCell) holder.itemView;
                    if (position == PasscodeActivity.this.changePasscodeRow) {
                        textCell2.setText(LocaleController.getString("ChangePasscode", NUM), true);
                        if (SharedConfig.passcodeHash.length() == 0) {
                            textCell2.setTag("windowBackgroundWhiteGrayText7");
                            textCell2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText7"));
                            return;
                        }
                        textCell2.setTag("windowBackgroundWhiteBlackText");
                        textCell2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                        return;
                    } else if (position == PasscodeActivity.this.autoLockRow) {
                        if (SharedConfig.autoLockIn == 0) {
                            val = LocaleController.formatString("AutoLockDisabled", NUM, new Object[0]);
                        } else if (SharedConfig.autoLockIn < 3600) {
                            val = LocaleController.formatString("AutoLockInTime", NUM, LocaleController.formatPluralString("Minutes", SharedConfig.autoLockIn / 60, new Object[0]));
                        } else if (SharedConfig.autoLockIn < 86400) {
                            val = LocaleController.formatString("AutoLockInTime", NUM, LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) SharedConfig.autoLockIn) / 60.0f) / 60.0f)), new Object[0]));
                        } else {
                            val = LocaleController.formatString("AutoLockInTime", NUM, LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) SharedConfig.autoLockIn) / 60.0f) / 60.0f) / 24.0f)), new Object[0]));
                        }
                        textCell2.setTextAndValue(LocaleController.getString("AutoLock", NUM), val, true);
                        textCell2.setTag("windowBackgroundWhiteBlackText");
                        textCell2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                        return;
                    } else if (position == PasscodeActivity.this.disablePasscodeRow) {
                        textCell2.setText(LocaleController.getString(NUM), false);
                        textCell2.setTag("dialogTextRed");
                        textCell2.setTextColor(Theme.getColor("dialogTextRed"));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == PasscodeActivity.this.hintRow) {
                        cell.setText(LocaleController.getString(NUM));
                        cell.setBackground((Drawable) null);
                        cell.getTextView().setGravity(1);
                        return;
                    }
                    int i = 5;
                    if (position == PasscodeActivity.this.autoLockDetailRow) {
                        cell.setText(LocaleController.getString(NUM));
                        cell.setBackground(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        TextView textView = cell.getTextView();
                        if (!LocaleController.isRTL) {
                            i = 3;
                        }
                        textView.setGravity(i);
                        return;
                    } else if (position == PasscodeActivity.this.captureDetailRow) {
                        cell.setText(LocaleController.getString(NUM));
                        cell.setBackground(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        TextView textView2 = cell.getTextView();
                        if (!LocaleController.isRTL) {
                            i = 3;
                        }
                        textView2.setGravity(i);
                        return;
                    } else {
                        return;
                    }
                case 3:
                    HeaderCell cell2 = (HeaderCell) holder.itemView;
                    cell2.setHeight(46);
                    if (position == PasscodeActivity.this.captureHeaderRow) {
                        cell2.setText(LocaleController.getString(NUM));
                        return;
                    }
                    return;
                case 4:
                    RLottieImageHolderView holderView = (RLottieImageHolderView) holder.itemView;
                    holderView.imageView.setAnimation(NUM, 100, 100);
                    holderView.imageView.playAnimation();
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == PasscodeActivity.this.fingerprintRow || position == PasscodeActivity.this.captureRow) {
                return 0;
            }
            if (position == PasscodeActivity.this.changePasscodeRow || position == PasscodeActivity.this.autoLockRow || position == PasscodeActivity.this.disablePasscodeRow) {
                return 1;
            }
            if (position == PasscodeActivity.this.autoLockDetailRow || position == PasscodeActivity.this.captureDetailRow || position == PasscodeActivity.this.hintRow) {
                return 2;
            }
            if (position == PasscodeActivity.this.captureHeaderRow) {
                return 3;
            }
            if (position == PasscodeActivity.this.utyanRow) {
                return 4;
            }
            return 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextCheckCell.class, TextSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuBackground"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItem"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItemIcon"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
        themeDescriptions.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        themeDescriptions.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText7"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        return themeDescriptions;
    }

    private static final class RLottieImageHolderView extends FrameLayout {
        /* access modifiers changed from: private */
        public RLottieImageView imageView;

        private RLottieImageHolderView(Context context) {
            super(context);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setOnClickListener(new PasscodeActivity$RLottieImageHolderView$$ExternalSyntheticLambda0(this));
            int size = AndroidUtilities.dp(120.0f);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
            params.gravity = 1;
            addView(this.imageView, params);
            setPadding(0, AndroidUtilities.dp(32.0f), 0, 0);
            setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-PasscodeActivity$RLottieImageHolderView  reason: not valid java name */
        public /* synthetic */ void m4022x8045aa27(View v) {
            if (!this.imageView.getAnimatedDrawable().isRunning()) {
                this.imageView.getAnimatedDrawable().setCurrentFrame(0, false);
                this.imageView.playAnimation();
            }
        }
    }
}

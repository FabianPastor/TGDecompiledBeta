package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
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
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.RecyclerListView;

public class PasscodeActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int done_button = 1;
    private static final int password_item = 3;
    private static final int pin_item = 2;
    /* access modifiers changed from: private */
    public int autoLockDetailRow;
    /* access modifiers changed from: private */
    public int autoLockRow;
    private int badPasscodeTries;
    /* access modifiers changed from: private */
    public int captureDetailRow;
    /* access modifiers changed from: private */
    public int captureRow;
    /* access modifiers changed from: private */
    public int changePasscodeRow;
    /* access modifiers changed from: private */
    public int currentPasswordType = 0;
    private TextView dropDown;
    private ActionBarMenuItem dropDownContainer;
    private Drawable dropDownDrawable;
    /* access modifiers changed from: private */
    public int fingerprintRow;
    private String firstPassword;
    private long lastPasscodeTry;
    private ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public int passcodeDetailRow;
    /* access modifiers changed from: private */
    public int passcodeRow;
    /* access modifiers changed from: private */
    public int passcodeSetStep = 0;
    /* access modifiers changed from: private */
    public EditTextBoldCursor passwordEditText;
    /* access modifiers changed from: private */
    public int rowCount;
    private TextView titleTextView;
    /* access modifiers changed from: private */
    public int type;

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
    }

    public View createView(Context context) {
        Context context2 = context;
        if (this.type != 3) {
            this.actionBar.setBackButtonImage(NUM);
        }
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    PasscodeActivity.this.finishFragment();
                } else if (id == 1) {
                    if (PasscodeActivity.this.passcodeSetStep == 0) {
                        PasscodeActivity.this.processNext();
                    } else if (PasscodeActivity.this.passcodeSetStep == 1) {
                        PasscodeActivity.this.processDone();
                    }
                } else if (id == 2) {
                    int unused = PasscodeActivity.this.currentPasswordType = 0;
                    PasscodeActivity.this.updateDropDownTextView();
                } else if (id == 3) {
                    int unused2 = PasscodeActivity.this.currentPasswordType = 1;
                    PasscodeActivity.this.updateDropDownTextView();
                }
            }
        });
        this.fragmentView = new FrameLayout(context2);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        if (this.type != 0) {
            ActionBarMenu menu = this.actionBar.createMenu();
            menu.addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", NUM));
            TextView textView = new TextView(context2);
            this.titleTextView = textView;
            textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            if (this.type != 1) {
                this.titleTextView.setText(LocaleController.getString("EnterCurrentPasscode", NUM));
            } else if (SharedConfig.passcodeHash.length() != 0) {
                this.titleTextView.setText(LocaleController.getString("EnterNewPasscode", NUM));
            } else {
                this.titleTextView.setText(LocaleController.getString("EnterNewFirstPasscode", NUM));
            }
            this.titleTextView.setTextSize(1, 18.0f);
            this.titleTextView.setGravity(1);
            frameLayout.addView(this.titleTextView, LayoutHelper.createFrame(-2, -2.0f, 1, 0.0f, 38.0f, 0.0f, 0.0f));
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
            this.passwordEditText = editTextBoldCursor;
            editTextBoldCursor.setTextSize(1, 20.0f);
            this.passwordEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.passwordEditText.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            this.passwordEditText.setMaxLines(1);
            this.passwordEditText.setLines(1);
            this.passwordEditText.setGravity(1);
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
            this.passwordEditText.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.passwordEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.passwordEditText.setCursorWidth(1.5f);
            frameLayout.addView(this.passwordEditText, LayoutHelper.createFrame(-1, 36.0f, 51, 40.0f, 90.0f, 40.0f, 0.0f));
            this.passwordEditText.setOnEditorActionListener(new PasscodeActivity$$ExternalSyntheticLambda2(this));
            this.passwordEditText.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable s) {
                    if (PasscodeActivity.this.passwordEditText.length() != 4) {
                        return;
                    }
                    if (PasscodeActivity.this.type == 2 && SharedConfig.passcodeType == 0) {
                        PasscodeActivity.this.processDone();
                    } else if (PasscodeActivity.this.type != 1 || PasscodeActivity.this.currentPasswordType != 0) {
                    } else {
                        if (PasscodeActivity.this.passcodeSetStep == 0) {
                            PasscodeActivity.this.processNext();
                        } else if (PasscodeActivity.this.passcodeSetStep == 1) {
                            PasscodeActivity.this.processDone();
                        }
                    }
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
            if (this.type == 1) {
                frameLayout.setTag("windowBackgroundWhite");
                ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context2, menu, 0, 0);
                this.dropDownContainer = actionBarMenuItem;
                actionBarMenuItem.setSubMenuOpenSide(1);
                this.dropDownContainer.addSubItem(2, LocaleController.getString("PasscodePIN", NUM));
                this.dropDownContainer.addSubItem(3, LocaleController.getString("PasscodePassword", NUM));
                this.actionBar.addView(this.dropDownContainer, LayoutHelper.createFrame(-2, -1.0f, 51, AndroidUtilities.isTablet() ? 64.0f : 56.0f, 0.0f, 40.0f, 0.0f));
                this.dropDownContainer.setOnClickListener(new PasscodeActivity$$ExternalSyntheticLambda1(this));
                TextView textView2 = new TextView(context2);
                this.dropDown = textView2;
                textView2.setGravity(3);
                this.dropDown.setSingleLine(true);
                this.dropDown.setLines(1);
                this.dropDown.setMaxLines(1);
                this.dropDown.setEllipsize(TextUtils.TruncateAt.END);
                this.dropDown.setTextColor(Theme.getColor("actionBarDefaultTitle"));
                this.dropDown.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                Drawable mutate = context.getResources().getDrawable(NUM).mutate();
                this.dropDownDrawable = mutate;
                mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("actionBarDefaultTitle"), PorterDuff.Mode.MULTIPLY));
                this.dropDown.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, this.dropDownDrawable, (Drawable) null);
                this.dropDown.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                this.dropDown.setPadding(0, 0, AndroidUtilities.dp(10.0f), 0);
                this.dropDownContainer.addView(this.dropDown, LayoutHelper.createFrame(-2, -2.0f, 16, 16.0f, 0.0f, 0.0f, 1.0f));
            } else {
                this.actionBar.setTitle(LocaleController.getString("Passcode", NUM));
            }
            updateDropDownTextView();
        } else {
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
            this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new PasscodeActivity$$ExternalSyntheticLambda5(this));
        }
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-PasscodeActivity  reason: not valid java name */
    public /* synthetic */ boolean m3350lambda$createView$0$orgtelegramuiPasscodeActivity(TextView textView, int i, KeyEvent keyEvent) {
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

    /* renamed from: lambda$createView$1$org-telegram-ui-PasscodeActivity  reason: not valid java name */
    public /* synthetic */ void m3351lambda$createView$1$orgtelegramuiPasscodeActivity(View view) {
        this.dropDownContainer.toggleSubMenu();
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-PasscodeActivity  reason: not valid java name */
    public /* synthetic */ void m3353lambda$createView$4$orgtelegramuiPasscodeActivity(View view, int position) {
        if (view.isEnabled()) {
            boolean z = true;
            if (position == this.changePasscodeRow) {
                presentFragment(new PasscodeActivity(1));
            } else if (position == this.passcodeRow) {
                TextCheckCell cell = (TextCheckCell) view;
                if (SharedConfig.passcodeHash.length() != 0) {
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
                    if (SharedConfig.passcodeHash.length() == 0) {
                        z = false;
                    }
                    cell.setChecked(z);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
                    return;
                }
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
                    numberPicker.setFormatter(PasscodeActivity$$ExternalSyntheticLambda4.INSTANCE);
                    builder.setView(numberPicker);
                    builder.setNegativeButton(LocaleController.getString("Done", NUM), new PasscodeActivity$$ExternalSyntheticLambda0(this, numberPicker, position));
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
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
                if (!SharedConfig.allowScreenCapture) {
                    AlertsCreator.showSimpleAlert(this, LocaleController.getString("ScreenCaptureAlert", NUM));
                }
            }
        }
    }

    static /* synthetic */ String lambda$createView$2(int value) {
        if (value == 0) {
            return LocaleController.getString("AutoLockDisabled", NUM);
        }
        if (value == 1) {
            return LocaleController.formatString("AutoLockInTime", NUM, LocaleController.formatPluralString("Minutes", 1));
        } else if (value == 2) {
            return LocaleController.formatString("AutoLockInTime", NUM, LocaleController.formatPluralString("Minutes", 5));
        } else if (value == 3) {
            return LocaleController.formatString("AutoLockInTime", NUM, LocaleController.formatPluralString("Hours", 1));
        } else if (value != 4) {
            return "";
        } else {
            return LocaleController.formatString("AutoLockInTime", NUM, LocaleController.formatPluralString("Hours", 5));
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-PasscodeActivity  reason: not valid java name */
    public /* synthetic */ void m3352lambda$createView$3$orgtelegramuiPasscodeActivity(NumberPicker numberPicker, int position, DialogInterface dialog, int which) {
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

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        if (this.type != 0) {
            AndroidUtilities.runOnUIThread(new PasscodeActivity$$ExternalSyntheticLambda3(this), 200);
        }
        fixLayoutInternal();
    }

    /* renamed from: lambda$onResume$5$org-telegram-ui-PasscodeActivity  reason: not valid java name */
    public /* synthetic */ void m3354lambda$onResume$5$orgtelegramuiPasscodeActivity() {
        EditTextBoldCursor editTextBoldCursor = this.passwordEditText;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.requestFocus();
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.didSetPasscode && this.type == 0) {
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
        this.passcodeRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.changePasscodeRow = i;
        this.rowCount = i2 + 1;
        this.passcodeDetailRow = i2;
        if (SharedConfig.passcodeHash.length() > 0) {
            try {
                if (Build.VERSION.SDK_INT >= 23 && FingerprintManagerCompat.from(ApplicationLoader.applicationContext).isHardwareDetected()) {
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
            this.captureRow = i6;
            this.rowCount = i7 + 1;
            this.captureDetailRow = i7;
            return;
        }
        this.captureRow = -1;
        this.captureDetailRow = -1;
        this.fingerprintRow = -1;
        this.autoLockRow = -1;
        this.autoLockDetailRow = -1;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    PasscodeActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    PasscodeActivity.this.fixLayoutInternal();
                    return true;
                }
            });
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && this.type != 0) {
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    /* access modifiers changed from: private */
    public void updateDropDownTextView() {
        TextView textView = this.dropDown;
        if (textView != null) {
            int i = this.currentPasswordType;
            if (i == 0) {
                textView.setText(LocaleController.getString("PasscodePIN", NUM));
            } else if (i == 1) {
                textView.setText(LocaleController.getString("PasscodePassword", NUM));
            }
        }
        int i2 = this.type;
        if ((i2 == 1 && this.currentPasswordType == 0) || (i2 == 2 && SharedConfig.passcodeType == 0)) {
            this.passwordEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
            this.passwordEditText.setInputType(3);
            this.passwordEditText.setKeyListener(DigitsKeyListener.getInstance("NUM"));
        } else {
            int i3 = this.type;
            if ((i3 == 1 && this.currentPasswordType == 1) || (i3 == 2 && SharedConfig.passcodeType == 1)) {
                this.passwordEditText.setFilters(new InputFilter[0]);
                this.passwordEditText.setKeyListener((KeyListener) null);
                this.passwordEditText.setInputType(129);
            }
        }
        this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    /* access modifiers changed from: private */
    public void processNext() {
        if (this.passwordEditText.getText().length() == 0 || (this.currentPasswordType == 0 && this.passwordEditText.getText().length() != 4)) {
            onPasscodeError();
            return;
        }
        if (this.currentPasswordType == 0) {
            this.actionBar.setTitle(LocaleController.getString("PasscodePIN", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("PasscodePassword", NUM));
        }
        this.dropDownContainer.setVisibility(8);
        this.titleTextView.setText(LocaleController.getString("ReEnterYourPasscode", NUM));
        this.firstPassword = this.passwordEditText.getText().toString();
        this.passwordEditText.setText("");
        this.passcodeSetStep = 1;
    }

    /* access modifiers changed from: private */
    public void processDone() {
        if (this.passwordEditText.getText().length() == 0) {
            onPasscodeError();
            return;
        }
        int i = this.type;
        if (i == 1) {
            if (!this.firstPassword.equals(this.passwordEditText.getText().toString())) {
                try {
                    Toast.makeText(getParentActivity(), LocaleController.getString("PasscodeDoNotMatch", NUM), 0).show();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                AndroidUtilities.shakeView(this.titleTextView, 2.0f, 0);
                this.passwordEditText.setText("");
                return;
            }
            try {
                SharedConfig.passcodeSalt = new byte[16];
                Utilities.random.nextBytes(SharedConfig.passcodeSalt);
                byte[] passcodeBytes = this.firstPassword.getBytes("UTF-8");
                byte[] bytes = new byte[(passcodeBytes.length + 32)];
                System.arraycopy(SharedConfig.passcodeSalt, 0, bytes, 0, 16);
                System.arraycopy(passcodeBytes, 0, bytes, 16, passcodeBytes.length);
                System.arraycopy(SharedConfig.passcodeSalt, 0, bytes, passcodeBytes.length + 16, 16);
                SharedConfig.passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(bytes, 0, bytes.length));
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            SharedConfig.allowScreenCapture = true;
            SharedConfig.passcodeType = this.currentPasswordType;
            SharedConfig.saveConfig();
            getMediaDataController().buildShortcuts();
            finishFragment();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
            this.passwordEditText.clearFocus();
            AndroidUtilities.hideKeyboard(this.passwordEditText);
        } else if (i != 2) {
        } else {
            if (SharedConfig.passcodeRetryInMs > 0) {
                double d = (double) SharedConfig.passcodeRetryInMs;
                Double.isNaN(d);
                int value = Math.max(1, (int) Math.ceil(d / 1000.0d));
                Toast.makeText(getParentActivity(), LocaleController.formatString("TooManyTries", NUM, LocaleController.formatPluralString("Seconds", value)), 0).show();
                this.passwordEditText.setText("");
                onPasscodeError();
            } else if (!SharedConfig.checkPasscode(this.passwordEditText.getText().toString())) {
                SharedConfig.increaseBadPasscodeTries();
                this.passwordEditText.setText("");
                onPasscodeError();
            } else {
                SharedConfig.badPasscodeTries = 0;
                SharedConfig.saveConfig();
                this.passwordEditText.clearFocus();
                AndroidUtilities.hideKeyboard(this.passwordEditText);
                presentFragment(new PasscodeActivity(0), true);
            }
        }
    }

    private void onPasscodeError() {
        if (getParentActivity() != null) {
            Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200);
            }
            AndroidUtilities.shakeView(this.titleTextView, 2.0f, 0);
        }
    }

    /* access modifiers changed from: private */
    public void fixLayoutInternal() {
        if (this.dropDownContainer != null) {
            if (!AndroidUtilities.isTablet()) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.dropDownContainer.getLayoutParams();
                layoutParams.topMargin = Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
                this.dropDownContainer.setLayoutParams(layoutParams);
            }
            if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
                this.dropDown.setTextSize(1, 20.0f);
            } else {
                this.dropDown.setTextSize(1, 18.0f);
            }
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Boolean hasWidgets;
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == PasscodeActivity.this.passcodeRow || position == PasscodeActivity.this.fingerprintRow || position == PasscodeActivity.this.autoLockRow || position == PasscodeActivity.this.captureRow || (SharedConfig.passcodeHash.length() != 0 && position == PasscodeActivity.this.changePasscodeRow);
        }

        public int getItemCount() {
            return PasscodeActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new TextCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 1:
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
            String val;
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    TextCheckCell textCell = (TextCheckCell) holder.itemView;
                    if (position == PasscodeActivity.this.passcodeRow) {
                        String string = LocaleController.getString("Passcode", NUM);
                        if (SharedConfig.passcodeHash.length() > 0) {
                            z = true;
                        }
                        textCell.setTextAndCheck(string, z, true);
                        return;
                    } else if (position == PasscodeActivity.this.fingerprintRow) {
                        textCell.setTextAndCheck(LocaleController.getString("UnlockFingerprint", NUM), SharedConfig.useFingerprint, true);
                        return;
                    } else if (position == PasscodeActivity.this.captureRow) {
                        textCell.setTextAndCheck(LocaleController.getString("ScreenCapture", NUM), SharedConfig.allowScreenCapture, false);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextSettingsCell textCell2 = (TextSettingsCell) holder.itemView;
                    if (position == PasscodeActivity.this.changePasscodeRow) {
                        textCell2.setText(LocaleController.getString("ChangePasscode", NUM), false);
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
                            val = LocaleController.formatString("AutoLockInTime", NUM, LocaleController.formatPluralString("Minutes", SharedConfig.autoLockIn / 60));
                        } else if (SharedConfig.autoLockIn < 86400) {
                            val = LocaleController.formatString("AutoLockInTime", NUM, LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) SharedConfig.autoLockIn) / 60.0f) / 60.0f))));
                        } else {
                            val = LocaleController.formatString("AutoLockInTime", NUM, LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) SharedConfig.autoLockIn) / 60.0f) / 60.0f) / 24.0f))));
                        }
                        textCell2.setTextAndValue(LocaleController.getString("AutoLock", NUM), val, true);
                        textCell2.setTag("windowBackgroundWhiteBlackText");
                        textCell2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == PasscodeActivity.this.passcodeDetailRow) {
                        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(LocaleController.getString("ChangePasscodeInfo", NUM));
                        if (this.hasWidgets == null) {
                            this.hasWidgets = Boolean.valueOf(true ^ this.mContext.getSharedPreferences("shortcut_widget", 0).getAll().isEmpty());
                        }
                        if (this.hasWidgets.booleanValue()) {
                            stringBuilder.append("\n\n").append(AndroidUtilities.replaceTags(LocaleController.getString("WidgetPasscodeEnable2", NUM)));
                        }
                        cell.setText(stringBuilder);
                        if (PasscodeActivity.this.autoLockDetailRow != -1) {
                            cell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                            return;
                        } else {
                            cell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                            return;
                        }
                    } else if (position == PasscodeActivity.this.autoLockDetailRow) {
                        cell.setText(LocaleController.getString("AutoLockInfo", NUM));
                        cell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == PasscodeActivity.this.captureDetailRow) {
                        cell.setText(LocaleController.getString("ScreenCaptureInfo", NUM));
                        cell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == PasscodeActivity.this.passcodeRow || position == PasscodeActivity.this.fingerprintRow || position == PasscodeActivity.this.captureRow) {
                return 0;
            }
            if (position == PasscodeActivity.this.changePasscodeRow || position == PasscodeActivity.this.autoLockRow) {
                return 1;
            }
            if (position == PasscodeActivity.this.passcodeDetailRow || position == PasscodeActivity.this.autoLockDetailRow || position == PasscodeActivity.this.captureDetailRow) {
                return 2;
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
        themeDescriptions.add(new ThemeDescription(this.dropDown, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.dropDown, 0, (Class[]) null, (Paint) null, new Drawable[]{this.dropDownDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
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
}

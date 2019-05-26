package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.fingerprint.FingerprintManagerCompat;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
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
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class PasscodeActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private static final int password_item = 3;
    private static final int pin_item = 2;
    private int autoLockDetailRow;
    private int autoLockRow;
    private int badPasscodeTries;
    private int captureDetailRow;
    private int captureRow;
    private int changePasscodeRow;
    private int currentPasswordType = 0;
    private TextView dropDown;
    private ActionBarMenuItem dropDownContainer;
    private Drawable dropDownDrawable;
    private int fingerprintRow;
    private String firstPassword;
    private long lastPasscodeTry;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int passcodeDetailRow;
    private int passcodeRow;
    private int passcodeSetStep = 0;
    private EditTextBoldCursor passwordEditText;
    private int rowCount;
    private TextView titleTextView;
    private int type;

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == PasscodeActivity.this.passcodeRow || adapterPosition == PasscodeActivity.this.fingerprintRow || adapterPosition == PasscodeActivity.this.autoLockRow || adapterPosition == PasscodeActivity.this.captureRow || (SharedConfig.passcodeHash.length() != 0 && adapterPosition == PasscodeActivity.this.changePasscodeRow);
        }

        public int getItemCount() {
            return PasscodeActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View textCheckCell;
            String str = "windowBackgroundWhite";
            if (i == 0) {
                textCheckCell = new TextCheckCell(this.mContext);
                textCheckCell.setBackgroundColor(Theme.getColor(str));
            } else if (i != 1) {
                textCheckCell = new TextInfoPrivacyCell(this.mContext);
            } else {
                textCheckCell = new TextSettingsCell(this.mContext);
                textCheckCell.setBackgroundColor(Theme.getColor(str));
            }
            return new Holder(textCheckCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            String string;
            String str;
            if (itemViewType == 0) {
                TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                if (i == PasscodeActivity.this.passcodeRow) {
                    string = LocaleController.getString("Passcode", NUM);
                    if (SharedConfig.passcodeHash.length() > 0) {
                        z = true;
                    }
                    textCheckCell.setTextAndCheck(string, z, true);
                } else if (i == PasscodeActivity.this.fingerprintRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("UnlockFingerprint", NUM), SharedConfig.useFingerprint, true);
                } else if (i == PasscodeActivity.this.captureRow) {
                    textCheckCell.setTextAndCheck(LocaleController.getString("ScreenCapture", NUM), SharedConfig.allowScreenCapture, false);
                }
            } else if (itemViewType == 1) {
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                str = "windowBackgroundWhiteBlackText";
                if (i == PasscodeActivity.this.changePasscodeRow) {
                    textSettingsCell.setText(LocaleController.getString("ChangePasscode", NUM), false);
                    if (SharedConfig.passcodeHash.length() == 0) {
                        string = "windowBackgroundWhiteGrayText7";
                        textSettingsCell.setTag(string);
                        textSettingsCell.setTextColor(Theme.getColor(string));
                        return;
                    }
                    textSettingsCell.setTag(str);
                    textSettingsCell.setTextColor(Theme.getColor(str));
                } else if (i == PasscodeActivity.this.autoLockRow) {
                    i = SharedConfig.autoLockIn;
                    if (i == 0) {
                        string = LocaleController.formatString("AutoLockDisabled", NUM, new Object[0]);
                    } else {
                        String str2 = "AutoLockInTime";
                        Object[] objArr;
                        if (i < 3600) {
                            objArr = new Object[1];
                            objArr[0] = LocaleController.formatPluralString("Minutes", i / 60);
                            string = LocaleController.formatString(str2, NUM, objArr);
                        } else if (i < 86400) {
                            objArr = new Object[1];
                            objArr[0] = LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) i) / 60.0f) / 60.0f)));
                            string = LocaleController.formatString(str2, NUM, objArr);
                        } else {
                            objArr = new Object[1];
                            objArr[0] = LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) i) / 60.0f) / 60.0f) / 24.0f)));
                            string = LocaleController.formatString(str2, NUM, objArr);
                        }
                    }
                    textSettingsCell.setTextAndValue(LocaleController.getString("AutoLock", NUM), string, true);
                    textSettingsCell.setTag(str);
                    textSettingsCell.setTextColor(Theme.getColor(str));
                }
            } else if (itemViewType == 2) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                str = "windowBackgroundGrayShadow";
                if (i == PasscodeActivity.this.passcodeDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("ChangePasscodeInfo", NUM));
                    if (PasscodeActivity.this.autoLockDetailRow != -1) {
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                    } else {
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                    }
                } else if (i == PasscodeActivity.this.autoLockDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("AutoLockInfo", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                } else if (i == PasscodeActivity.this.captureDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("ScreenCaptureInfo", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                }
            }
        }

        public int getItemViewType(int i) {
            if (i == PasscodeActivity.this.passcodeRow || i == PasscodeActivity.this.fingerprintRow || i == PasscodeActivity.this.captureRow) {
                return 0;
            }
            if (i == PasscodeActivity.this.changePasscodeRow || i == PasscodeActivity.this.autoLockRow) {
                return 1;
            }
            if (i == PasscodeActivity.this.passcodeDetailRow || i == PasscodeActivity.this.autoLockDetailRow || i == PasscodeActivity.this.captureDetailRow) {
                return 2;
            }
            return 0;
        }
    }

    public PasscodeActivity(int i) {
        this.type = i;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        if (this.type == 0) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetPasscode);
        }
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
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    PasscodeActivity.this.finishFragment();
                } else if (i == 1) {
                    if (PasscodeActivity.this.passcodeSetStep == 0) {
                        PasscodeActivity.this.processNext();
                    } else if (PasscodeActivity.this.passcodeSetStep == 1) {
                        PasscodeActivity.this.processDone();
                    }
                } else if (i == 2) {
                    PasscodeActivity.this.currentPasswordType = 0;
                    PasscodeActivity.this.updateDropDownTextView();
                } else if (i == 3) {
                    PasscodeActivity.this.currentPasswordType = 1;
                    PasscodeActivity.this.updateDropDownTextView();
                }
            }
        });
        this.fragmentView = new FrameLayout(context2);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        String str = "Passcode";
        String str2;
        if (this.type != 0) {
            ActionBarMenu createMenu = this.actionBar.createMenu();
            createMenu.addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
            this.titleTextView = new TextView(context2);
            this.titleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
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
            this.passwordEditText = new EditTextBoldCursor(context2);
            this.passwordEditText.setTextSize(1, 20.0f);
            String str3 = "windowBackgroundWhiteBlackText";
            this.passwordEditText.setTextColor(Theme.getColor(str3));
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
            this.passwordEditText.setCursorColor(Theme.getColor(str3));
            this.passwordEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.passwordEditText.setCursorWidth(1.5f);
            frameLayout.addView(this.passwordEditText, LayoutHelper.createFrame(-1, 36.0f, 51, 40.0f, 90.0f, 40.0f, 0.0f));
            this.passwordEditText.setOnEditorActionListener(new -$$Lambda$PasscodeActivity$Dw5wi6axlaDg9f9aOPWpPJRaBU0(this));
            this.passwordEditText.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
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
            this.passwordEditText.setCustomSelectionActionModeCallback(new Callback() {
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
            if (this.type == 1) {
                frameLayout.setTag("windowBackgroundWhite");
                this.dropDownContainer = new ActionBarMenuItem(context2, createMenu, 0, 0);
                this.dropDownContainer.setSubMenuOpenSide(1);
                this.dropDownContainer.addSubItem(2, LocaleController.getString("PasscodePIN", NUM));
                this.dropDownContainer.addSubItem(3, LocaleController.getString("PasscodePassword", NUM));
                this.actionBar.addView(this.dropDownContainer, LayoutHelper.createFrame(-2, -1.0f, 51, AndroidUtilities.isTablet() ? 64.0f : 56.0f, 0.0f, 40.0f, 0.0f));
                this.dropDownContainer.setOnClickListener(new -$$Lambda$PasscodeActivity$nthmgeFTBNbMbbgybaEUi4bvQ7I(this));
                this.dropDown = new TextView(context2);
                this.dropDown.setGravity(3);
                this.dropDown.setSingleLine(true);
                this.dropDown.setLines(1);
                this.dropDown.setMaxLines(1);
                this.dropDown.setEllipsize(TruncateAt.END);
                str2 = "actionBarDefaultTitle";
                this.dropDown.setTextColor(Theme.getColor(str2));
                this.dropDown.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.dropDownDrawable = context.getResources().getDrawable(NUM).mutate();
                this.dropDownDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str2), Mode.MULTIPLY));
                this.dropDown.setCompoundDrawablesWithIntrinsicBounds(null, null, this.dropDownDrawable, null);
                this.dropDown.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                this.dropDown.setPadding(0, 0, AndroidUtilities.dp(10.0f), 0);
                this.dropDownContainer.addView(this.dropDown, LayoutHelper.createFrame(-2, -2.0f, 16, 16.0f, 0.0f, 0.0f, 1.0f));
            } else {
                this.actionBar.setTitle(LocaleController.getString(str, NUM));
            }
            updateDropDownTextView();
        } else {
            this.actionBar.setTitle(LocaleController.getString(str, NUM));
            str2 = "windowBackgroundGray";
            frameLayout.setTag(str2);
            frameLayout.setBackgroundColor(Theme.getColor(str2));
            this.listView = new RecyclerListView(context2);
            this.listView.setLayoutManager(new LinearLayoutManager(context2, 1, false) {
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            });
            this.listView.setVerticalScrollBarEnabled(false);
            this.listView.setItemAnimator(null);
            this.listView.setLayoutAnimation(null);
            frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
            RecyclerListView recyclerListView = this.listView;
            ListAdapter listAdapter = new ListAdapter(context2);
            this.listAdapter = listAdapter;
            recyclerListView.setAdapter(listAdapter);
            this.listView.setOnItemClickListener(new -$$Lambda$PasscodeActivity$mrP17AePE_jxJGc4Lp8zfeRkzb0(this));
        }
        return this.fragmentView;
    }

    public /* synthetic */ boolean lambda$createView$0$PasscodeActivity(TextView textView, int i, KeyEvent keyEvent) {
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

    public /* synthetic */ void lambda$createView$1$PasscodeActivity(View view) {
        this.dropDownContainer.toggleSubMenu();
    }

    public /* synthetic */ void lambda$createView$4$PasscodeActivity(View view, int i) {
        if (view.isEnabled()) {
            boolean z = true;
            if (i == this.changePasscodeRow) {
                presentFragment(new PasscodeActivity(1));
            } else if (i == this.passcodeRow) {
                TextCheckCell textCheckCell = (TextCheckCell) view;
                if (SharedConfig.passcodeHash.length() != 0) {
                    SharedConfig.passcodeHash = "";
                    SharedConfig.appLocked = false;
                    SharedConfig.saveConfig();
                    i = this.listView.getChildCount();
                    for (int i2 = 0; i2 < i; i2++) {
                        View childAt = this.listView.getChildAt(i2);
                        if (childAt instanceof TextSettingsCell) {
                            ((TextSettingsCell) childAt).setTextColor(Theme.getColor("windowBackgroundWhiteGrayText7"));
                            break;
                        }
                    }
                    if (SharedConfig.passcodeHash.length() == 0) {
                        z = false;
                    }
                    textCheckCell.setChecked(z);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
                } else {
                    presentFragment(new PasscodeActivity(1));
                }
            } else if (i == this.autoLockRow) {
                if (getParentActivity() != null) {
                    Builder builder = new Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString("AutoLock", NUM));
                    NumberPicker numberPicker = new NumberPicker(getParentActivity());
                    numberPicker.setMinValue(0);
                    numberPicker.setMaxValue(4);
                    int i3 = SharedConfig.autoLockIn;
                    if (i3 == 0) {
                        numberPicker.setValue(0);
                    } else if (i3 == 60) {
                        numberPicker.setValue(1);
                    } else if (i3 == 300) {
                        numberPicker.setValue(2);
                    } else if (i3 == 3600) {
                        numberPicker.setValue(3);
                    } else if (i3 == 18000) {
                        numberPicker.setValue(4);
                    }
                    numberPicker.setFormatter(-$$Lambda$PasscodeActivity$e1eIIUXJcHN6dWhbpJbTrxba_60.INSTANCE);
                    builder.setView(numberPicker);
                    builder.setNegativeButton(LocaleController.getString("Done", NUM), new -$$Lambda$PasscodeActivity$gpdG6ADQUdojRTN0OUtZLDO-4yI(this, numberPicker, i));
                    showDialog(builder.create());
                }
            } else if (i == this.fingerprintRow) {
                SharedConfig.useFingerprint ^= 1;
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                ((TextCheckCell) view).setChecked(SharedConfig.useFingerprint);
            } else if (i == this.captureRow) {
                SharedConfig.allowScreenCapture ^= 1;
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                ((TextCheckCell) view).setChecked(SharedConfig.allowScreenCapture);
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
                if (!SharedConfig.allowScreenCapture) {
                    AlertsCreator.showSimpleAlert(this, LocaleController.getString("ScreenCaptureAlert", NUM));
                }
            }
        }
    }

    static /* synthetic */ String lambda$null$2(int i) {
        if (i == 0) {
            return LocaleController.getString("AutoLockDisabled", NUM);
        }
        String str = "Minutes";
        String str2 = "AutoLockInTime";
        if (i == 1) {
            return LocaleController.formatString(str2, NUM, LocaleController.formatPluralString(str, 1));
        } else if (i == 2) {
            return LocaleController.formatString(str2, NUM, LocaleController.formatPluralString(str, 5));
        } else {
            String str3 = "Hours";
            if (i == 3) {
                return LocaleController.formatString(str2, NUM, LocaleController.formatPluralString(str3, 1));
            } else if (i != 4) {
                return "";
            } else {
                return LocaleController.formatString(str2, NUM, LocaleController.formatPluralString(str3, 5));
            }
        }
    }

    public /* synthetic */ void lambda$null$3$PasscodeActivity(NumberPicker numberPicker, int i, DialogInterface dialogInterface, int i2) {
        int value = numberPicker.getValue();
        if (value == 0) {
            SharedConfig.autoLockIn = 0;
        } else if (value == 1) {
            SharedConfig.autoLockIn = 60;
        } else if (value == 2) {
            SharedConfig.autoLockIn = 300;
        } else if (value == 3) {
            SharedConfig.autoLockIn = 3600;
        } else if (value == 4) {
            SharedConfig.autoLockIn = 18000;
        }
        this.listAdapter.notifyItemChanged(i);
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        if (this.type != 0) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$PasscodeActivity$uOu47HjuX9AWXmApKYN2F1Z-SDQ(this), 200);
        }
        fixLayoutInternal();
    }

    public /* synthetic */ void lambda$onResume$5$PasscodeActivity() {
        EditTextBoldCursor editTextBoldCursor = this.passwordEditText;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.requestFocus();
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.didSetPasscode && this.type == 0) {
            updateRows();
            ListAdapter listAdapter = this.listAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.passcodeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.changePasscodeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.passcodeDetailRow = i;
        if (SharedConfig.passcodeHash.length() > 0) {
            try {
                if (VERSION.SDK_INT >= 23 && FingerprintManagerCompat.from(ApplicationLoader.applicationContext).isHardwareDetected()) {
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.fingerprintRow = i;
                }
            } catch (Throwable th) {
                FileLog.e(th);
            }
            i = this.rowCount;
            this.rowCount = i + 1;
            this.autoLockRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.autoLockDetailRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.captureRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.captureDetailRow = i;
            return;
        }
        this.captureRow = -1;
        this.captureDetailRow = -1;
        this.fingerprintRow = -1;
        this.autoLockRow = -1;
        this.autoLockDetailRow = -1;
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    PasscodeActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    PasscodeActivity.this.fixLayoutInternal();
                    return true;
                }
            });
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && this.type != 0) {
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    private void updateDropDownTextView() {
        TextView textView = this.dropDown;
        if (textView != null) {
            int i = this.currentPasswordType;
            if (i == 0) {
                textView.setText(LocaleController.getString("PasscodePIN", NUM));
            } else if (i == 1) {
                textView.setText(LocaleController.getString("PasscodePassword", NUM));
            }
        }
        if ((this.type == 1 && this.currentPasswordType == 0) || (this.type == 2 && SharedConfig.passcodeType == 0)) {
            this.passwordEditText.setFilters(new InputFilter[]{new LengthFilter(4)});
            this.passwordEditText.setInputType(3);
            this.passwordEditText.setKeyListener(DigitsKeyListener.getInstance("NUM"));
        } else if ((this.type == 1 && this.currentPasswordType == 1) || (this.type == 2 && SharedConfig.passcodeType == 1)) {
            this.passwordEditText.setFilters(new InputFilter[0]);
            this.passwordEditText.setKeyListener(null);
            this.passwordEditText.setInputType(129);
        }
        this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    private void processNext() {
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

    private void processDone() {
        if (this.passwordEditText.getText().length() == 0) {
            onPasscodeError();
            return;
        }
        int i = this.type;
        String str = "";
        if (i == 1) {
            if (this.firstPassword.equals(this.passwordEditText.getText().toString())) {
                try {
                    SharedConfig.passcodeSalt = new byte[16];
                    Utilities.random.nextBytes(SharedConfig.passcodeSalt);
                    byte[] bytes = this.firstPassword.getBytes("UTF-8");
                    byte[] bArr = new byte[(bytes.length + 32)];
                    System.arraycopy(SharedConfig.passcodeSalt, 0, bArr, 0, 16);
                    System.arraycopy(bytes, 0, bArr, 16, bytes.length);
                    System.arraycopy(SharedConfig.passcodeSalt, 0, bArr, bytes.length + 16, 16);
                    SharedConfig.passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(bArr, 0, bArr.length));
                } catch (Exception e) {
                    FileLog.e(e);
                }
                SharedConfig.allowScreenCapture = true;
                SharedConfig.passcodeType = this.currentPasswordType;
                SharedConfig.saveConfig();
                finishFragment();
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
                this.passwordEditText.clearFocus();
                AndroidUtilities.hideKeyboard(this.passwordEditText);
            } else {
                try {
                    Toast.makeText(getParentActivity(), LocaleController.getString("PasscodeDoNotMatch", NUM), 0).show();
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
                AndroidUtilities.shakeView(this.titleTextView, 2.0f, 0);
                this.passwordEditText.setText(str);
            }
        } else if (i == 2) {
            long j = SharedConfig.passcodeRetryInMs;
            if (j > 0) {
                double d = (double) j;
                Double.isNaN(d);
                i = Math.max(1, (int) Math.ceil(d / 1000.0d));
                Toast.makeText(getParentActivity(), LocaleController.formatString("TooManyTries", NUM, LocaleController.formatPluralString("Seconds", i)), 0).show();
                this.passwordEditText.setText(str);
                onPasscodeError();
            } else if (SharedConfig.checkPasscode(this.passwordEditText.getText().toString())) {
                SharedConfig.badPasscodeTries = 0;
                SharedConfig.saveConfig();
                this.passwordEditText.clearFocus();
                AndroidUtilities.hideKeyboard(this.passwordEditText);
                presentFragment(new PasscodeActivity(0), true);
            } else {
                SharedConfig.increaseBadPasscodeTries();
                this.passwordEditText.setText(str);
                onPasscodeError();
            }
        }
    }

    private void onPasscodeError() {
        if (getParentActivity() != null) {
            Vibrator vibrator = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200);
            }
            AndroidUtilities.shakeView(this.titleTextView, 2.0f, 0);
        }
    }

    private void fixLayoutInternal() {
        if (this.dropDownContainer != null) {
            if (!AndroidUtilities.isTablet()) {
                LayoutParams layoutParams = (LayoutParams) this.dropDownContainer.getLayoutParams();
                layoutParams.topMargin = VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
                this.dropDownContainer.setLayoutParams(layoutParams);
            }
            if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
                this.dropDown.setTextSize(20.0f);
            } else {
                this.dropDown.setTextSize(18.0f);
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        r1 = new ThemeDescription[27];
        r1[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextCheckCell.class, TextSettingsCell.class}, null, null, null, "windowBackgroundWhite");
        r1[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhite");
        r1[2] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        r1[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        r1[4] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        r1[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        r1[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        r1[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        r1[8] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground");
        r1[9] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem");
        r1[10] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "actionBarDefaultSubmenuItemIcon");
        r1[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r1[12] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r1[13] = new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6");
        r1[14] = new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        r1[15] = new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField");
        r1[16] = new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated");
        r1[17] = new ThemeDescription(this.dropDown, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "actionBarDefaultTitle");
        r1[18] = new ThemeDescription(this.dropDown, 0, null, null, new Drawable[]{this.dropDownDrawable}, null, "actionBarDefaultTitle");
        View view = this.listView;
        Class[] clsArr = new Class[]{TextCheckCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        r1[19] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        clsArr = new Class[]{TextCheckCell.class};
        strArr = new String[1];
        strArr[0] = "checkBox";
        r1[20] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "switchTrack");
        r1[21] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        r1[22] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r1[23] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText7");
        r1[24] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteValueText");
        r1[25] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r1[26] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        return r1;
    }
}

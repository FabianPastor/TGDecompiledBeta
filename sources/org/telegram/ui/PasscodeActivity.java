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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.messenger.support.fingerprint.FingerprintManagerCompat;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar;
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
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.NumberPicker.Formatter;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class PasscodeActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private static final int password_item = 3;
    private static final int pin_item = 2;
    private int autoLockDetailRow;
    private int autoLockRow;
    private int captureDetailRow;
    private int captureRow;
    private int changePasscodeRow;
    private int currentPasswordType = 0;
    private TextView dropDown;
    private ActionBarMenuItem dropDownContainer;
    private Drawable dropDownDrawable;
    private int fingerprintRow;
    private String firstPassword;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int passcodeDetailRow;
    private int passcodeRow;
    private int passcodeSetStep = 0;
    private EditTextBoldCursor passwordEditText;
    private int rowCount;
    private TextView titleTextView;
    private int type;

    /* renamed from: org.telegram.ui.PasscodeActivity$2 */
    class C15492 implements OnEditorActionListener {
        C15492() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (PasscodeActivity.this.passcodeSetStep == null) {
                PasscodeActivity.this.processNext();
                return true;
            } else if (PasscodeActivity.this.passcodeSetStep != 1) {
                return null;
            } else {
                PasscodeActivity.this.processDone();
                return true;
            }
        }
    }

    /* renamed from: org.telegram.ui.PasscodeActivity$3 */
    class C15503 implements TextWatcher {
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        C15503() {
        }

        public void afterTextChanged(Editable editable) {
            if (PasscodeActivity.this.passwordEditText.length() != 4) {
                return;
            }
            if (PasscodeActivity.this.type == 2 && SharedConfig.passcodeType == null) {
                PasscodeActivity.this.processDone();
            } else if (PasscodeActivity.this.type != 1 || PasscodeActivity.this.currentPasswordType != null) {
            } else {
                if (PasscodeActivity.this.passcodeSetStep == null) {
                    PasscodeActivity.this.processNext();
                } else if (PasscodeActivity.this.passcodeSetStep == 1) {
                    PasscodeActivity.this.processDone();
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.PasscodeActivity$4 */
    class C15514 implements Callback {
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

        C15514() {
        }
    }

    /* renamed from: org.telegram.ui.PasscodeActivity$5 */
    class C15525 implements OnClickListener {
        C15525() {
        }

        public void onClick(View view) {
            PasscodeActivity.this.dropDownContainer.toggleSubMenu();
        }
    }

    /* renamed from: org.telegram.ui.PasscodeActivity$8 */
    class C15548 implements Runnable {
        C15548() {
        }

        public void run() {
            if (PasscodeActivity.this.passwordEditText != null) {
                PasscodeActivity.this.passwordEditText.requestFocus();
                AndroidUtilities.showKeyboard(PasscodeActivity.this.passwordEditText);
            }
        }
    }

    /* renamed from: org.telegram.ui.PasscodeActivity$9 */
    class C15559 implements OnPreDrawListener {
        C15559() {
        }

        public boolean onPreDraw() {
            PasscodeActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
            PasscodeActivity.this.fixLayoutInternal();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.PasscodeActivity$1 */
    class C22121 extends ActionBarMenuOnItemClick {
        C22121() {
        }

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
    }

    /* renamed from: org.telegram.ui.PasscodeActivity$7 */
    class C22147 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.PasscodeActivity$7$1 */
        class C22131 implements Formatter {
            C22131() {
            }

            public String format(int i) {
                if (i == 0) {
                    return LocaleController.getString("AutoLockDisabled", C0446R.string.AutoLockDisabled);
                }
                if (i == 1) {
                    return LocaleController.formatString("AutoLockInTime", C0446R.string.AutoLockInTime, LocaleController.formatPluralString("Minutes", 1));
                } else if (i == 2) {
                    return LocaleController.formatString("AutoLockInTime", C0446R.string.AutoLockInTime, LocaleController.formatPluralString("Minutes", 5));
                } else if (i == 3) {
                    return LocaleController.formatString("AutoLockInTime", C0446R.string.AutoLockInTime, LocaleController.formatPluralString("Hours", 1));
                } else if (i != 4) {
                    return TtmlNode.ANONYMOUS_REGION_ID;
                } else {
                    return LocaleController.formatString("AutoLockInTime", C0446R.string.AutoLockInTime, LocaleController.formatPluralString("Hours", 5));
                }
            }
        }

        C22147() {
        }

        public void onItemClick(View view, final int i) {
            if (view.isEnabled()) {
                boolean z = true;
                if (i == PasscodeActivity.this.changePasscodeRow) {
                    PasscodeActivity.this.presentFragment(new PasscodeActivity(1));
                } else if (i == PasscodeActivity.this.passcodeRow) {
                    TextCheckCell textCheckCell = (TextCheckCell) view;
                    if (SharedConfig.passcodeHash.length() != 0) {
                        SharedConfig.passcodeHash = TtmlNode.ANONYMOUS_REGION_ID;
                        SharedConfig.appLocked = false;
                        SharedConfig.saveConfig();
                        i = PasscodeActivity.this.listView.getChildCount();
                        for (int i2 = 0; i2 < i; i2++) {
                            View childAt = PasscodeActivity.this.listView.getChildAt(i2);
                            if (childAt instanceof TextSettingsCell) {
                                ((TextSettingsCell) childAt).setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7));
                                break;
                            }
                        }
                        if (SharedConfig.passcodeHash.length() == 0) {
                            z = false;
                        }
                        textCheckCell.setChecked(z);
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
                    } else {
                        PasscodeActivity.this.presentFragment(new PasscodeActivity(1));
                    }
                } else if (i == PasscodeActivity.this.autoLockRow) {
                    if (PasscodeActivity.this.getParentActivity() != null) {
                        view = new Builder(PasscodeActivity.this.getParentActivity());
                        view.setTitle(LocaleController.getString("AutoLock", C0446R.string.AutoLock));
                        final View numberPicker = new NumberPicker(PasscodeActivity.this.getParentActivity());
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
                        numberPicker.setFormatter(new C22131());
                        view.setView(numberPicker);
                        view.setNegativeButton(LocaleController.getString("Done", C0446R.string.Done), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface = numberPicker.getValue();
                                if (dialogInterface == null) {
                                    SharedConfig.autoLockIn = 0;
                                } else if (dialogInterface == 1) {
                                    SharedConfig.autoLockIn = 60;
                                } else if (dialogInterface == 2) {
                                    SharedConfig.autoLockIn = 300;
                                } else if (dialogInterface == 3) {
                                    SharedConfig.autoLockIn = 3600;
                                } else if (dialogInterface == 4) {
                                    SharedConfig.autoLockIn = 18000;
                                }
                                PasscodeActivity.this.listAdapter.notifyItemChanged(i);
                                UserConfig.getInstance(PasscodeActivity.this.currentAccount).saveConfig(false);
                            }
                        });
                        PasscodeActivity.this.showDialog(view.create());
                    }
                } else if (i == PasscodeActivity.this.fingerprintRow) {
                    SharedConfig.useFingerprint ^= 1;
                    UserConfig.getInstance(PasscodeActivity.this.currentAccount).saveConfig(false);
                    ((TextCheckCell) view).setChecked(SharedConfig.useFingerprint);
                } else if (i == PasscodeActivity.this.captureRow) {
                    SharedConfig.allowScreenCapture ^= 1;
                    UserConfig.getInstance(PasscodeActivity.this.currentAccount).saveConfig(false);
                    ((TextCheckCell) view).setChecked(SharedConfig.allowScreenCapture);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
                }
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            viewHolder = viewHolder.getAdapterPosition();
            if (!(viewHolder == PasscodeActivity.this.passcodeRow || viewHolder == PasscodeActivity.this.fingerprintRow || viewHolder == PasscodeActivity.this.autoLockRow || viewHolder == PasscodeActivity.this.captureRow)) {
                if (SharedConfig.passcodeHash.length() == 0 || viewHolder != PasscodeActivity.this.changePasscodeRow) {
                    return null;
                }
            }
            return true;
        }

        public int getItemCount() {
            return PasscodeActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    viewGroup = new TextCheckCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    viewGroup = new TextSettingsCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    viewGroup = new TextInfoPrivacyCell(this.mContext);
                    break;
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = false;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                    if (i == PasscodeActivity.this.passcodeRow) {
                        i = LocaleController.getString("Passcode", C0446R.string.Passcode);
                        if (SharedConfig.passcodeHash.length() > 0) {
                            z = true;
                        }
                        textCheckCell.setTextAndCheck(i, z, true);
                        return;
                    } else if (i == PasscodeActivity.this.fingerprintRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("UnlockFingerprint", C0446R.string.UnlockFingerprint), SharedConfig.useFingerprint, true);
                        return;
                    } else if (i == PasscodeActivity.this.captureRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("ScreenCapture", C0446R.string.ScreenCapture), SharedConfig.allowScreenCapture, false);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    if (i == PasscodeActivity.this.changePasscodeRow) {
                        textSettingsCell.setText(LocaleController.getString("ChangePasscode", C0446R.string.ChangePasscode), false);
                        if (SharedConfig.passcodeHash.length() == 0) {
                            textSettingsCell.setTag(Theme.key_windowBackgroundWhiteGrayText7);
                            textSettingsCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7));
                            return;
                        }
                        textSettingsCell.setTag(Theme.key_windowBackgroundWhiteBlackText);
                        textSettingsCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                        return;
                    } else if (i == PasscodeActivity.this.autoLockRow) {
                        if (SharedConfig.autoLockIn == 0) {
                            i = LocaleController.formatString("AutoLockDisabled", C0446R.string.AutoLockDisabled, new Object[0]);
                        } else if (SharedConfig.autoLockIn < 3600) {
                            i = LocaleController.formatString("AutoLockInTime", C0446R.string.AutoLockInTime, LocaleController.formatPluralString("Minutes", SharedConfig.autoLockIn / 60));
                        } else if (SharedConfig.autoLockIn < 86400) {
                            i = LocaleController.formatString("AutoLockInTime", C0446R.string.AutoLockInTime, LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) SharedConfig.autoLockIn) / 60.0f) / 60.0f))));
                        } else {
                            i = LocaleController.formatString("AutoLockInTime", C0446R.string.AutoLockInTime, LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) SharedConfig.autoLockIn) / 60.0f) / 60.0f) / 24.0f))));
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("AutoLock", C0446R.string.AutoLock), i, true);
                        textSettingsCell.setTag(Theme.key_windowBackgroundWhiteBlackText);
                        textSettingsCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == PasscodeActivity.this.passcodeDetailRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("ChangePasscodeInfo", C0446R.string.ChangePasscodeInfo));
                        if (PasscodeActivity.this.autoLockDetailRow != -1) {
                            textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                            return;
                        } else {
                            textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                            return;
                        }
                    } else if (i == PasscodeActivity.this.autoLockDetailRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("AutoLockInfo", C0446R.string.AutoLockInfo));
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == PasscodeActivity.this.captureDetailRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("ScreenCaptureInfo", C0446R.string.ScreenCaptureInfo));
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int i) {
            if (!(i == PasscodeActivity.this.passcodeRow || i == PasscodeActivity.this.fingerprintRow)) {
                if (i != PasscodeActivity.this.captureRow) {
                    if (i != PasscodeActivity.this.changePasscodeRow) {
                        if (i != PasscodeActivity.this.autoLockRow) {
                            if (!(i == PasscodeActivity.this.passcodeDetailRow || i == PasscodeActivity.this.autoLockDetailRow)) {
                                if (i != PasscodeActivity.this.captureDetailRow) {
                                    return 0;
                                }
                            }
                            return 2;
                        }
                    }
                    return 1;
                }
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
            r0.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        }
        r0.actionBar.setAllowOverlayTitle(false);
        r0.actionBar.setActionBarMenuOnItemClick(new C22121());
        r0.fragmentView = new FrameLayout(context2);
        FrameLayout frameLayout = (FrameLayout) r0.fragmentView;
        if (r0.type != 0) {
            ActionBarMenu createMenu = r0.actionBar.createMenu();
            float f = 56.0f;
            createMenu.addItemWithWidth(1, C0446R.drawable.ic_done, AndroidUtilities.dp(56.0f));
            r0.titleTextView = new TextView(context2);
            r0.titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            if (r0.type != 1) {
                r0.titleTextView.setText(LocaleController.getString("EnterCurrentPasscode", C0446R.string.EnterCurrentPasscode));
            } else if (SharedConfig.passcodeHash.length() != 0) {
                r0.titleTextView.setText(LocaleController.getString("EnterNewPasscode", C0446R.string.EnterNewPasscode));
            } else {
                r0.titleTextView.setText(LocaleController.getString("EnterNewFirstPasscode", C0446R.string.EnterNewFirstPasscode));
            }
            r0.titleTextView.setTextSize(1, 18.0f);
            r0.titleTextView.setGravity(1);
            frameLayout.addView(r0.titleTextView, LayoutHelper.createFrame(-2, -2.0f, 1, 0.0f, 38.0f, 0.0f, 0.0f));
            r0.passwordEditText = new EditTextBoldCursor(context2);
            r0.passwordEditText.setTextSize(1, 20.0f);
            r0.passwordEditText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.passwordEditText.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
            r0.passwordEditText.setMaxLines(1);
            r0.passwordEditText.setLines(1);
            r0.passwordEditText.setGravity(1);
            r0.passwordEditText.setSingleLine(true);
            if (r0.type == 1) {
                r0.passcodeSetStep = 0;
                r0.passwordEditText.setImeOptions(5);
            } else {
                r0.passcodeSetStep = 1;
                r0.passwordEditText.setImeOptions(6);
            }
            r0.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            r0.passwordEditText.setTypeface(Typeface.DEFAULT);
            r0.passwordEditText.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.passwordEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            r0.passwordEditText.setCursorWidth(1.5f);
            frameLayout.addView(r0.passwordEditText, LayoutHelper.createFrame(-1, 36.0f, 51, 40.0f, 90.0f, 40.0f, 0.0f));
            r0.passwordEditText.setOnEditorActionListener(new C15492());
            r0.passwordEditText.addTextChangedListener(new C15503());
            r0.passwordEditText.setCustomSelectionActionModeCallback(new C15514());
            if (r0.type == 1) {
                frameLayout.setTag(Theme.key_windowBackgroundWhite);
                r0.dropDownContainer = new ActionBarMenuItem(context2, createMenu, 0, 0);
                r0.dropDownContainer.setSubMenuOpenSide(1);
                r0.dropDownContainer.addSubItem(2, LocaleController.getString("PasscodePIN", C0446R.string.PasscodePIN));
                r0.dropDownContainer.addSubItem(3, LocaleController.getString("PasscodePassword", C0446R.string.PasscodePassword));
                ActionBar actionBar = r0.actionBar;
                View view = r0.dropDownContainer;
                if (AndroidUtilities.isTablet()) {
                    f = 64.0f;
                }
                actionBar.addView(view, LayoutHelper.createFrame(-2, -1.0f, 51, f, 0.0f, 40.0f, 0.0f));
                r0.dropDownContainer.setOnClickListener(new C15525());
                r0.dropDown = new TextView(context2);
                r0.dropDown.setGravity(3);
                r0.dropDown.setSingleLine(true);
                r0.dropDown.setLines(1);
                r0.dropDown.setMaxLines(1);
                r0.dropDown.setEllipsize(TruncateAt.END);
                r0.dropDown.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
                r0.dropDown.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                r0.dropDownDrawable = context.getResources().getDrawable(C0446R.drawable.ic_arrow_drop_down).mutate();
                r0.dropDownDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultTitle), Mode.MULTIPLY));
                r0.dropDown.setCompoundDrawablesWithIntrinsicBounds(null, null, r0.dropDownDrawable, null);
                r0.dropDown.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                r0.dropDown.setPadding(0, 0, AndroidUtilities.dp(10.0f), 0);
                r0.dropDownContainer.addView(r0.dropDown, LayoutHelper.createFrame(-2, -2.0f, 16, 16.0f, 0.0f, 0.0f, 1.0f));
            } else {
                r0.actionBar.setTitle(LocaleController.getString("Passcode", C0446R.string.Passcode));
            }
            updateDropDownTextView();
        } else {
            r0.actionBar.setTitle(LocaleController.getString("Passcode", C0446R.string.Passcode));
            frameLayout.setTag(Theme.key_windowBackgroundGray);
            frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            r0.listView = new RecyclerListView(context2);
            r0.listView.setLayoutManager(new LinearLayoutManager(context2, 1, false) {
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            });
            r0.listView.setVerticalScrollBarEnabled(false);
            r0.listView.setItemAnimator(null);
            r0.listView.setLayoutAnimation(null);
            frameLayout.addView(r0.listView, LayoutHelper.createFrame(-1, -1.0f));
            RecyclerListView recyclerListView = r0.listView;
            Adapter listAdapter = new ListAdapter(context2);
            r0.listAdapter = listAdapter;
            recyclerListView.setAdapter(listAdapter);
            r0.listView.setOnItemClickListener(new C22147());
        }
        return r0.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        if (this.type != 0) {
            AndroidUtilities.runOnUIThread(new C15548(), 200);
        }
        fixLayoutInternal();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.didSetPasscode && this.type == 0) {
            updateRows();
            if (this.listAdapter != 0) {
                this.listAdapter.notifyDataSetChanged();
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
                FileLog.m3e(th);
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
        if (this.listView != null) {
            this.listView.getViewTreeObserver().addOnPreDrawListener(new C15559());
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && this.type) {
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    private void updateDropDownTextView() {
        if (this.dropDown != null) {
            if (this.currentPasswordType == 0) {
                this.dropDown.setText(LocaleController.getString("PasscodePIN", C0446R.string.PasscodePIN));
            } else if (this.currentPasswordType == 1) {
                this.dropDown.setText(LocaleController.getString("PasscodePassword", C0446R.string.PasscodePassword));
            }
        }
        if ((this.type == 1 && this.currentPasswordType == 0) || (this.type == 2 && SharedConfig.passcodeType == 0)) {
            this.passwordEditText.setFilters(new InputFilter[]{new LengthFilter(4)});
            this.passwordEditText.setInputType(3);
            this.passwordEditText.setKeyListener(DigitsKeyListener.getInstance("1234567890"));
        } else if ((this.type == 1 && this.currentPasswordType == 1) || (this.type == 2 && SharedConfig.passcodeType == 1)) {
            this.passwordEditText.setFilters(new InputFilter[0]);
            this.passwordEditText.setKeyListener(null);
            this.passwordEditText.setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
        }
        this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    private void processNext() {
        if (this.passwordEditText.getText().length() != 0) {
            if (this.currentPasswordType != 0 || this.passwordEditText.getText().length() == 4) {
                if (this.currentPasswordType == 0) {
                    this.actionBar.setTitle(LocaleController.getString("PasscodePIN", C0446R.string.PasscodePIN));
                } else {
                    this.actionBar.setTitle(LocaleController.getString("PasscodePassword", C0446R.string.PasscodePassword));
                }
                this.dropDownContainer.setVisibility(8);
                this.titleTextView.setText(LocaleController.getString("ReEnterYourPasscode", C0446R.string.ReEnterYourPasscode));
                this.firstPassword = this.passwordEditText.getText().toString();
                this.passwordEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
                this.passcodeSetStep = 1;
                return;
            }
        }
        onPasscodeError();
    }

    private void processDone() {
        if (this.passwordEditText.getText().length() == 0) {
            onPasscodeError();
            return;
        }
        if (this.type == 1) {
            if (this.firstPassword.equals(this.passwordEditText.getText().toString())) {
                try {
                    SharedConfig.passcodeSalt = new byte[16];
                    Utilities.random.nextBytes(SharedConfig.passcodeSalt);
                    Object bytes = this.firstPassword.getBytes(C0542C.UTF8_NAME);
                    Object obj = new byte[(32 + bytes.length)];
                    System.arraycopy(SharedConfig.passcodeSalt, 0, obj, 0, 16);
                    System.arraycopy(bytes, 0, obj, 16, bytes.length);
                    System.arraycopy(SharedConfig.passcodeSalt, 0, obj, bytes.length + 16, 16);
                    SharedConfig.passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(obj, 0, obj.length));
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                SharedConfig.passcodeType = this.currentPasswordType;
                SharedConfig.saveConfig();
                finishFragment();
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
                this.passwordEditText.clearFocus();
                AndroidUtilities.hideKeyboard(this.passwordEditText);
            } else {
                try {
                    Toast.makeText(getParentActivity(), LocaleController.getString("PasscodeDoNotMatch", C0446R.string.PasscodeDoNotMatch), 0).show();
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
                AndroidUtilities.shakeView(this.titleTextView, 2.0f, 0);
                this.passwordEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
            }
        } else if (this.type == 2) {
            if (SharedConfig.checkPasscode(this.passwordEditText.getText().toString())) {
                this.passwordEditText.clearFocus();
                AndroidUtilities.hideKeyboard(this.passwordEditText);
                presentFragment(new PasscodeActivity(0), true);
            } else {
                this.passwordEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
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
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[28];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextCheckCell.class, TextSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[2] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[4] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[8] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground);
        themeDescriptionArr[9] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[12] = new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[13] = new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[14] = new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[15] = new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        themeDescriptionArr[16] = new ThemeDescription(this.dropDown, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[17] = new ThemeDescription(this.dropDown, 0, null, null, new Drawable[]{this.dropDownDrawable}, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[19] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumb);
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        themeDescriptionArr[21] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumbChecked);
        themeDescriptionArr[22] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        themeDescriptionArr[23] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[24] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText7);
        themeDescriptionArr[25] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        themeDescriptionArr[26] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[27] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        return themeDescriptionArr;
    }
}

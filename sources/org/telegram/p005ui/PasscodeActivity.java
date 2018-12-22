package org.telegram.p005ui;

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
import com.google.android.exoplayer2.CLASSNAMEC;
import com.google.android.exoplayer2.extractor.p003ts.TsExtractor;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.CLASSNAMER;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.fingerprint.FingerprintManagerCompat;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.ActionBar.ActionBarMenu;
import org.telegram.p005ui.ActionBar.ActionBarMenuItem;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.Cells.TextCheckCell;
import org.telegram.p005ui.Cells.TextInfoPrivacyCell;
import org.telegram.p005ui.Cells.TextSettingsCell;
import org.telegram.p005ui.Components.EditTextBoldCursor;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.NumberPicker;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;

/* renamed from: org.telegram.ui.PasscodeActivity */
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

    /* renamed from: org.telegram.ui.PasscodeActivity$1 */
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                PasscodeActivity.this.lambda$createView$1$PhotoAlbumPickerActivity();
            } else if (id == 1) {
                if (PasscodeActivity.this.passcodeSetStep == 0) {
                    PasscodeActivity.this.processNext();
                } else if (PasscodeActivity.this.passcodeSetStep == 1) {
                    PasscodeActivity.this.processDone();
                }
            } else if (id == 2) {
                PasscodeActivity.this.currentPasswordType = 0;
                PasscodeActivity.this.updateDropDownTextView();
            } else if (id == 3) {
                PasscodeActivity.this.currentPasswordType = 1;
                PasscodeActivity.this.updateDropDownTextView();
            }
        }
    }

    /* renamed from: org.telegram.ui.PasscodeActivity$2 */
    class CLASSNAME implements TextWatcher {
        CLASSNAME() {
        }

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
    }

    /* renamed from: org.telegram.ui.PasscodeActivity$3 */
    class CLASSNAME implements Callback {
        CLASSNAME() {
        }

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
    }

    /* renamed from: org.telegram.ui.PasscodeActivity$5 */
    class CLASSNAME implements OnPreDrawListener {
        CLASSNAME() {
        }

        public boolean onPreDraw() {
            PasscodeActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
            PasscodeActivity.this.fixLayoutInternal();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.PasscodeActivity$ListAdapter */
    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == PasscodeActivity.this.passcodeRow || position == PasscodeActivity.this.fingerprintRow || position == PasscodeActivity.this.autoLockRow || position == PasscodeActivity.this.captureRow || (SharedConfig.passcodeHash.length() != 0 && position == PasscodeActivity.this.changePasscodeRow);
        }

        public int getItemCount() {
            return PasscodeActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new TextCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    TextCheckCell textCell = holder.itemView;
                    if (position == PasscodeActivity.this.passcodeRow) {
                        String string = LocaleController.getString("Passcode", CLASSNAMER.string.Passcode);
                        if (SharedConfig.passcodeHash.length() > 0) {
                            z = true;
                        }
                        textCell.setTextAndCheck(string, z, true);
                        return;
                    } else if (position == PasscodeActivity.this.fingerprintRow) {
                        textCell.setTextAndCheck(LocaleController.getString("UnlockFingerprint", CLASSNAMER.string.UnlockFingerprint), SharedConfig.useFingerprint, true);
                        return;
                    } else if (position == PasscodeActivity.this.captureRow) {
                        textCell.setTextAndCheck(LocaleController.getString("ScreenCapture", CLASSNAMER.string.ScreenCapture), SharedConfig.allowScreenCapture, false);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextSettingsCell textCell2 = holder.itemView;
                    if (position == PasscodeActivity.this.changePasscodeRow) {
                        textCell2.setText(LocaleController.getString("ChangePasscode", CLASSNAMER.string.ChangePasscode), false);
                        if (SharedConfig.passcodeHash.length() == 0) {
                            textCell2.setTag(Theme.key_windowBackgroundWhiteGrayText7);
                            textCell2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7));
                            return;
                        }
                        textCell2.setTag(Theme.key_windowBackgroundWhiteBlackText);
                        textCell2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                        return;
                    } else if (position == PasscodeActivity.this.autoLockRow) {
                        String val;
                        if (SharedConfig.autoLockIn == 0) {
                            val = LocaleController.formatString("AutoLockDisabled", CLASSNAMER.string.AutoLockDisabled, new Object[0]);
                        } else if (SharedConfig.autoLockIn < 3600) {
                            val = LocaleController.formatString("AutoLockInTime", CLASSNAMER.string.AutoLockInTime, LocaleController.formatPluralString("Minutes", SharedConfig.autoLockIn / 60));
                        } else if (SharedConfig.autoLockIn < 86400) {
                            val = LocaleController.formatString("AutoLockInTime", CLASSNAMER.string.AutoLockInTime, LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) SharedConfig.autoLockIn) / 60.0f) / 60.0f))));
                        } else {
                            val = LocaleController.formatString("AutoLockInTime", CLASSNAMER.string.AutoLockInTime, LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) SharedConfig.autoLockIn) / 60.0f) / 60.0f) / 24.0f))));
                        }
                        textCell2.setTextAndValue(LocaleController.getString("AutoLock", CLASSNAMER.string.AutoLock), val, true);
                        textCell2.setTag(Theme.key_windowBackgroundWhiteBlackText);
                        textCell2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextInfoPrivacyCell cell = holder.itemView;
                    if (position == PasscodeActivity.this.passcodeDetailRow) {
                        cell.setText(LocaleController.getString("ChangePasscodeInfo", CLASSNAMER.string.ChangePasscodeInfo));
                        if (PasscodeActivity.this.autoLockDetailRow != -1) {
                            cell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) CLASSNAMER.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                            return;
                        } else {
                            cell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) CLASSNAMER.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                            return;
                        }
                    } else if (position == PasscodeActivity.this.autoLockDetailRow) {
                        cell.setText(LocaleController.getString("AutoLockInfo", CLASSNAMER.string.AutoLockInfo));
                        cell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) CLASSNAMER.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (position == PasscodeActivity.this.captureDetailRow) {
                        cell.setText(LocaleController.getString("ScreenCaptureInfo", CLASSNAMER.string.ScreenCaptureInfo));
                        cell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) CLASSNAMER.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
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

    public PasscodeActivity(int type) {
        this.type = type;
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
        if (this.type != 3) {
            this.actionBar.setBackButtonImage(CLASSNAMER.drawable.ic_ab_back);
        }
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        if (this.type != 0) {
            ActionBarMenu menu = this.actionBar.createMenu();
            menu.addItemWithWidth(1, CLASSNAMER.drawable.ic_done, AndroidUtilities.m9dp(56.0f));
            this.titleTextView = new TextView(context);
            this.titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            if (this.type != 1) {
                this.titleTextView.setText(LocaleController.getString("EnterCurrentPasscode", CLASSNAMER.string.EnterCurrentPasscode));
            } else if (SharedConfig.passcodeHash.length() != 0) {
                this.titleTextView.setText(LocaleController.getString("EnterNewPasscode", CLASSNAMER.string.EnterNewPasscode));
            } else {
                this.titleTextView.setText(LocaleController.getString("EnterNewFirstPasscode", CLASSNAMER.string.EnterNewFirstPasscode));
            }
            this.titleTextView.setTextSize(1, 18.0f);
            this.titleTextView.setGravity(1);
            frameLayout.addView(this.titleTextView, LayoutHelper.createFrame(-2, -2.0f, 1, 0.0f, 38.0f, 0.0f, 0.0f));
            this.passwordEditText = new EditTextBoldCursor(context);
            this.passwordEditText.setTextSize(1, 20.0f);
            this.passwordEditText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.passwordEditText.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
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
            this.passwordEditText.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.passwordEditText.setCursorSize(AndroidUtilities.m9dp(20.0f));
            this.passwordEditText.setCursorWidth(1.5f);
            frameLayout.addView(this.passwordEditText, LayoutHelper.createFrame(-1, 36.0f, 51, 40.0f, 90.0f, 40.0f, 0.0f));
            this.passwordEditText.setOnEditorActionListener(new PasscodeActivity$$Lambda$0(this));
            this.passwordEditText.addTextChangedListener(new CLASSNAME());
            this.passwordEditText.setCustomSelectionActionModeCallback(new CLASSNAME());
            if (this.type == 1) {
                frameLayout.setTag(Theme.key_windowBackgroundWhite);
                this.dropDownContainer = new ActionBarMenuItem(context, menu, 0, 0);
                this.dropDownContainer.setSubMenuOpenSide(1);
                this.dropDownContainer.addSubItem(2, LocaleController.getString("PasscodePIN", CLASSNAMER.string.PasscodePIN));
                this.dropDownContainer.addSubItem(3, LocaleController.getString("PasscodePassword", CLASSNAMER.string.PasscodePassword));
                this.actionBar.addView(this.dropDownContainer, LayoutHelper.createFrame(-2, -1.0f, 51, AndroidUtilities.isTablet() ? 64.0f : 56.0f, 0.0f, 40.0f, 0.0f));
                this.dropDownContainer.setOnClickListener(new PasscodeActivity$$Lambda$1(this));
                this.dropDown = new TextView(context);
                this.dropDown.setGravity(3);
                this.dropDown.setSingleLine(true);
                this.dropDown.setLines(1);
                this.dropDown.setMaxLines(1);
                this.dropDown.setEllipsize(TruncateAt.END);
                this.dropDown.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
                this.dropDown.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.dropDownDrawable = context.getResources().getDrawable(CLASSNAMER.drawable.ic_arrow_drop_down).mutate();
                this.dropDownDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultTitle), Mode.MULTIPLY));
                this.dropDown.setCompoundDrawablesWithIntrinsicBounds(null, null, this.dropDownDrawable, null);
                this.dropDown.setCompoundDrawablePadding(AndroidUtilities.m9dp(4.0f));
                this.dropDown.setPadding(0, 0, AndroidUtilities.m9dp(10.0f), 0);
                this.dropDownContainer.addView(this.dropDown, LayoutHelper.createFrame(-2, -2.0f, 16, 16.0f, 0.0f, 0.0f, 1.0f));
            } else {
                this.actionBar.setTitle(LocaleController.getString("Passcode", CLASSNAMER.string.Passcode));
            }
            updateDropDownTextView();
        } else {
            this.actionBar.setTitle(LocaleController.getString("Passcode", CLASSNAMER.string.Passcode));
            frameLayout.setTag(Theme.key_windowBackgroundGray);
            frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            this.listView = new RecyclerListView(context);
            this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false) {
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            });
            this.listView.setVerticalScrollBarEnabled(false);
            this.listView.setItemAnimator(null);
            this.listView.setLayoutAnimation(null);
            frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
            RecyclerListView recyclerListView = this.listView;
            Adapter listAdapter = new ListAdapter(context);
            this.listAdapter = listAdapter;
            recyclerListView.setAdapter(listAdapter);
            this.listView.setOnItemClickListener(new PasscodeActivity$$Lambda$2(this));
        }
        return this.fragmentView;
    }

    final /* synthetic */ boolean lambda$createView$0$PasscodeActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (this.passcodeSetStep == 0) {
            processNext();
            return true;
        } else if (this.passcodeSetStep != 1) {
            return false;
        } else {
            processDone();
            return true;
        }
    }

    final /* synthetic */ void lambda$createView$1$PasscodeActivity(View view) {
        this.dropDownContainer.toggleSubMenu();
    }

    final /* synthetic */ void lambda$createView$4$PasscodeActivity(View view, int position) {
        boolean z = true;
        if (!view.isEnabled()) {
            return;
        }
        if (position == this.changePasscodeRow) {
            presentFragment(new PasscodeActivity(1));
        } else if (position == this.passcodeRow) {
            TextCheckCell cell = (TextCheckCell) view;
            if (SharedConfig.passcodeHash.length() != 0) {
                SharedConfig.passcodeHash = TtmlNode.ANONYMOUS_REGION_ID;
                SharedConfig.appLocked = false;
                SharedConfig.saveConfig();
                int count = this.listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = this.listView.getChildAt(a);
                    if (child instanceof TextSettingsCell) {
                        ((TextSettingsCell) child).setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7));
                        break;
                    }
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
                Builder builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AutoLock", CLASSNAMER.string.AutoLock));
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
                numberPicker.setFormatter(PasscodeActivity$$Lambda$4.$instance);
                builder.setView(numberPicker);
                builder.setNegativeButton(LocaleController.getString("Done", CLASSNAMER.string.Done), new PasscodeActivity$$Lambda$5(this, numberPicker, position));
                showDialog(builder.create());
            }
        } else if (position == this.fingerprintRow) {
            if (SharedConfig.useFingerprint) {
                z = false;
            }
            SharedConfig.useFingerprint = z;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
            ((TextCheckCell) view).setChecked(SharedConfig.useFingerprint);
        } else if (position == this.captureRow) {
            if (SharedConfig.allowScreenCapture) {
                z = false;
            }
            SharedConfig.allowScreenCapture = z;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
            ((TextCheckCell) view).setChecked(SharedConfig.allowScreenCapture);
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
        }
    }

    static final /* synthetic */ String lambda$null$2$PasscodeActivity(int value) {
        if (value == 0) {
            return LocaleController.getString("AutoLockDisabled", CLASSNAMER.string.AutoLockDisabled);
        }
        if (value == 1) {
            return LocaleController.formatString("AutoLockInTime", CLASSNAMER.string.AutoLockInTime, LocaleController.formatPluralString("Minutes", 1));
        } else if (value == 2) {
            return LocaleController.formatString("AutoLockInTime", CLASSNAMER.string.AutoLockInTime, LocaleController.formatPluralString("Minutes", 5));
        } else if (value == 3) {
            return LocaleController.formatString("AutoLockInTime", CLASSNAMER.string.AutoLockInTime, LocaleController.formatPluralString("Hours", 1));
        } else if (value != 4) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        } else {
            return LocaleController.formatString("AutoLockInTime", CLASSNAMER.string.AutoLockInTime, LocaleController.formatPluralString("Hours", 5));
        }
    }

    final /* synthetic */ void lambda$null$3$PasscodeActivity(NumberPicker numberPicker, int position, DialogInterface dialog, int which) {
        which = numberPicker.getValue();
        if (which == 0) {
            SharedConfig.autoLockIn = 0;
        } else if (which == 1) {
            SharedConfig.autoLockIn = 60;
        } else if (which == 2) {
            SharedConfig.autoLockIn = 300;
        } else if (which == 3) {
            SharedConfig.autoLockIn = 3600;
        } else if (which == 4) {
            SharedConfig.autoLockIn = 18000;
        }
        this.listAdapter.notifyItemChanged(position);
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        if (this.type != 0) {
            AndroidUtilities.runOnUIThread(new PasscodeActivity$$Lambda$3(this), 200);
        }
        fixLayoutInternal();
    }

    final /* synthetic */ void lambda$onResume$5$PasscodeActivity() {
        if (this.passwordEditText != null) {
            this.passwordEditText.requestFocus();
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.didSetPasscode && this.type == 0) {
            updateRows();
            if (this.listAdapter != null) {
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
            } catch (Throwable e) {
                FileLog.m13e(e);
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

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.listView != null) {
            this.listView.getViewTreeObserver().addOnPreDrawListener(new CLASSNAME());
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && this.type != 0) {
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    private void updateDropDownTextView() {
        if (this.dropDown != null) {
            if (this.currentPasswordType == 0) {
                this.dropDown.setText(LocaleController.getString("PasscodePIN", CLASSNAMER.string.PasscodePIN));
            } else if (this.currentPasswordType == 1) {
                this.dropDown.setText(LocaleController.getString("PasscodePassword", CLASSNAMER.string.PasscodePassword));
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
        if (this.passwordEditText.getText().length() == 0 || (this.currentPasswordType == 0 && this.passwordEditText.getText().length() != 4)) {
            onPasscodeError();
            return;
        }
        if (this.currentPasswordType == 0) {
            this.actionBar.setTitle(LocaleController.getString("PasscodePIN", CLASSNAMER.string.PasscodePIN));
        } else {
            this.actionBar.setTitle(LocaleController.getString("PasscodePassword", CLASSNAMER.string.PasscodePassword));
        }
        this.dropDownContainer.setVisibility(8);
        this.titleTextView.setText(LocaleController.getString("ReEnterYourPasscode", CLASSNAMER.string.ReEnterYourPasscode));
        this.firstPassword = this.passwordEditText.getText().toString();
        this.passwordEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
        this.passcodeSetStep = 1;
    }

    private void processDone() {
        if (this.passwordEditText.getText().length() == 0) {
            onPasscodeError();
        } else if (this.type == 1) {
            if (this.firstPassword.equals(this.passwordEditText.getText().toString())) {
                try {
                    SharedConfig.passcodeSalt = new byte[16];
                    Utilities.random.nextBytes(SharedConfig.passcodeSalt);
                    byte[] passcodeBytes = this.firstPassword.getBytes(CLASSNAMEC.UTF8_NAME);
                    byte[] bytes = new byte[(passcodeBytes.length + 32)];
                    System.arraycopy(SharedConfig.passcodeSalt, 0, bytes, 0, 16);
                    System.arraycopy(passcodeBytes, 0, bytes, 16, passcodeBytes.length);
                    System.arraycopy(SharedConfig.passcodeSalt, 0, bytes, passcodeBytes.length + 16, 16);
                    SharedConfig.passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(bytes, 0, bytes.length));
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
                SharedConfig.passcodeType = this.currentPasswordType;
                SharedConfig.saveConfig();
                lambda$createView$1$PhotoAlbumPickerActivity();
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
                this.passwordEditText.clearFocus();
                AndroidUtilities.hideKeyboard(this.passwordEditText);
                return;
            }
            try {
                Toast.makeText(getParentActivity(), LocaleController.getString("PasscodeDoNotMatch", CLASSNAMER.string.PasscodeDoNotMatch), 0).show();
            } catch (Throwable e2) {
                FileLog.m13e(e2);
            }
            AndroidUtilities.shakeView(this.titleTextView, 2.0f, 0);
            this.passwordEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
        } else if (this.type != 2) {
        } else {
            if (SharedConfig.passcodeRetryInMs > 0) {
                int value = Math.max(1, (int) Math.ceil(((double) SharedConfig.passcodeRetryInMs) / 1000.0d));
                Toast.makeText(getParentActivity(), LocaleController.formatString("TooManyTries", CLASSNAMER.string.TooManyTries, LocaleController.formatPluralString("Seconds", value)), 0).show();
                this.passwordEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
                onPasscodeError();
            } else if (SharedConfig.checkPasscode(this.passwordEditText.getText().toString())) {
                SharedConfig.badPasscodeTries = 0;
                SharedConfig.saveConfig();
                this.passwordEditText.clearFocus();
                AndroidUtilities.hideKeyboard(this.passwordEditText);
                presentFragment(new PasscodeActivity(0), true);
            } else {
                SharedConfig.increaseBadPasscodeTries();
                this.passwordEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
                onPasscodeError();
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
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[26];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextCheckCell.class, TextSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[2] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundGray);
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
        themeDescriptionArr[19] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        themeDescriptionArr[21] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[22] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText7);
        themeDescriptionArr[23] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        themeDescriptionArr[24] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[25] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        return themeDescriptionArr;
    }
}

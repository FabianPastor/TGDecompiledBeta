package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Paint;
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
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import java.security.SecureRandom;
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
import org.telegram.messenger.support.widget.LinearLayoutManager;
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

public class PasscodeActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
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
  
  public PasscodeActivity(int paramInt)
  {
    this.type = paramInt;
  }
  
  private void fixLayoutInternal()
  {
    int i;
    if (this.dropDownContainer != null)
    {
      if (!AndroidUtilities.isTablet())
      {
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.dropDownContainer.getLayoutParams();
        if (Build.VERSION.SDK_INT < 21) {
          break label81;
        }
        i = AndroidUtilities.statusBarHeight;
        localLayoutParams.topMargin = i;
        this.dropDownContainer.setLayoutParams(localLayoutParams);
      }
      if ((AndroidUtilities.isTablet()) || (ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2)) {
        break label86;
      }
      this.dropDown.setTextSize(18.0F);
    }
    for (;;)
    {
      return;
      label81:
      i = 0;
      break;
      label86:
      this.dropDown.setTextSize(20.0F);
    }
  }
  
  private void onPasscodeError()
  {
    if (getParentActivity() == null) {}
    for (;;)
    {
      return;
      Vibrator localVibrator = (Vibrator)getParentActivity().getSystemService("vibrator");
      if (localVibrator != null) {
        localVibrator.vibrate(200L);
      }
      AndroidUtilities.shakeView(this.titleTextView, 2.0F, 0);
    }
  }
  
  private void processDone()
  {
    if (this.passwordEditText.getText().length() == 0) {
      onPasscodeError();
    }
    for (;;)
    {
      return;
      if (this.type == 1)
      {
        if (!this.firstPassword.equals(this.passwordEditText.getText().toString())) {
          try
          {
            Toast.makeText(getParentActivity(), LocaleController.getString("PasscodeDoNotMatch", NUM), 0).show();
            AndroidUtilities.shakeView(this.titleTextView, 2.0F, 0);
            this.passwordEditText.setText("");
          }
          catch (Exception localException1)
          {
            for (;;)
            {
              FileLog.e(localException1);
            }
          }
        } else {
          try
          {
            SharedConfig.passcodeSalt = new byte[16];
            Utilities.random.nextBytes(SharedConfig.passcodeSalt);
            byte[] arrayOfByte2 = this.firstPassword.getBytes("UTF-8");
            byte[] arrayOfByte1 = new byte[arrayOfByte2.length + 32];
            System.arraycopy(SharedConfig.passcodeSalt, 0, arrayOfByte1, 0, 16);
            System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 16, arrayOfByte2.length);
            System.arraycopy(SharedConfig.passcodeSalt, 0, arrayOfByte1, arrayOfByte2.length + 16, 16);
            SharedConfig.passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(arrayOfByte1, 0, arrayOfByte1.length));
            SharedConfig.passcodeType = this.currentPasswordType;
            SharedConfig.saveConfig();
            finishFragment();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
            this.passwordEditText.clearFocus();
            AndroidUtilities.hideKeyboard(this.passwordEditText);
          }
          catch (Exception localException2)
          {
            for (;;)
            {
              FileLog.e(localException2);
            }
          }
        }
      }
      else if (this.type == 2) {
        if (!SharedConfig.checkPasscode(this.passwordEditText.getText().toString()))
        {
          this.passwordEditText.setText("");
          onPasscodeError();
        }
        else
        {
          this.passwordEditText.clearFocus();
          AndroidUtilities.hideKeyboard(this.passwordEditText);
          presentFragment(new PasscodeActivity(0), true);
        }
      }
    }
  }
  
  private void processNext()
  {
    if ((this.passwordEditText.getText().length() == 0) || ((this.currentPasswordType == 0) && (this.passwordEditText.getText().length() != 4)))
    {
      onPasscodeError();
      return;
    }
    if (this.currentPasswordType == 0) {
      this.actionBar.setTitle(LocaleController.getString("PasscodePIN", NUM));
    }
    for (;;)
    {
      this.dropDownContainer.setVisibility(8);
      this.titleTextView.setText(LocaleController.getString("ReEnterYourPasscode", NUM));
      this.firstPassword = this.passwordEditText.getText().toString();
      this.passwordEditText.setText("");
      this.passcodeSetStep = 1;
      break;
      this.actionBar.setTitle(LocaleController.getString("PasscodePassword", NUM));
    }
  }
  
  private void updateDropDownTextView()
  {
    if (this.dropDown != null)
    {
      if (this.currentPasswordType == 0) {
        this.dropDown.setText(LocaleController.getString("PasscodePIN", NUM));
      }
    }
    else
    {
      if (((this.type != 1) || (this.currentPasswordType != 0)) && ((this.type != 2) || (SharedConfig.passcodeType != 0))) {
        break label142;
      }
      InputFilter.LengthFilter localLengthFilter = new InputFilter.LengthFilter(4);
      this.passwordEditText.setFilters(new InputFilter[] { localLengthFilter });
      this.passwordEditText.setInputType(3);
      this.passwordEditText.setKeyListener(DigitsKeyListener.getInstance("1234567890"));
    }
    for (;;)
    {
      this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
      return;
      if (this.currentPasswordType != 1) {
        break;
      }
      this.dropDown.setText(LocaleController.getString("PasscodePassword", NUM));
      break;
      label142:
      if (((this.type == 1) && (this.currentPasswordType == 1)) || ((this.type == 2) && (SharedConfig.passcodeType == 1)))
      {
        this.passwordEditText.setFilters(new InputFilter[0]);
        this.passwordEditText.setKeyListener(null);
        this.passwordEditText.setInputType(129);
      }
    }
  }
  
  private void updateRows()
  {
    this.rowCount = 0;
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.passcodeRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.changePasscodeRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.passcodeDetailRow = i;
    if (SharedConfig.passcodeHash.length() > 0) {}
    for (;;)
    {
      try
      {
        if ((Build.VERSION.SDK_INT >= 23) && (FingerprintManagerCompat.from(ApplicationLoader.applicationContext).isHardwareDetected()))
        {
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.fingerprintRow = i;
        }
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.autoLockRow = i;
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.autoLockDetailRow = i;
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.captureRow = i;
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.captureDetailRow = i;
        return;
      }
      catch (Throwable localThrowable)
      {
        FileLog.e(localThrowable);
        continue;
      }
      this.captureRow = -1;
      this.captureDetailRow = -1;
      this.fingerprintRow = -1;
      this.autoLockRow = -1;
      this.autoLockDetailRow = -1;
    }
  }
  
  public View createView(Context paramContext)
  {
    if (this.type != 3) {
      this.actionBar.setBackButtonImage(NUM);
    }
    this.actionBar.setAllowOverlayTitle(false);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          PasscodeActivity.this.finishFragment();
        }
        for (;;)
        {
          return;
          if (paramAnonymousInt == 1)
          {
            if (PasscodeActivity.this.passcodeSetStep == 0) {
              PasscodeActivity.this.processNext();
            } else if (PasscodeActivity.this.passcodeSetStep == 1) {
              PasscodeActivity.this.processDone();
            }
          }
          else if (paramAnonymousInt == 2)
          {
            PasscodeActivity.access$302(PasscodeActivity.this, 0);
            PasscodeActivity.this.updateDropDownTextView();
          }
          else if (paramAnonymousInt == 3)
          {
            PasscodeActivity.access$302(PasscodeActivity.this, 1);
            PasscodeActivity.this.updateDropDownTextView();
          }
        }
      }
    });
    this.fragmentView = new FrameLayout(paramContext);
    Object localObject1 = (FrameLayout)this.fragmentView;
    label290:
    float f;
    if (this.type != 0)
    {
      Object localObject2 = this.actionBar.createMenu();
      ((ActionBarMenu)localObject2).addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0F));
      this.titleTextView = new TextView(paramContext);
      this.titleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
      if (this.type == 1) {
        if (SharedConfig.passcodeHash.length() != 0)
        {
          this.titleTextView.setText(LocaleController.getString("EnterNewPasscode", NUM));
          this.titleTextView.setTextSize(1, 18.0F);
          this.titleTextView.setGravity(1);
          ((FrameLayout)localObject1).addView(this.titleTextView, LayoutHelper.createFrame(-2, -2.0F, 1, 0.0F, 38.0F, 0.0F, 0.0F));
          this.passwordEditText = new EditTextBoldCursor(paramContext);
          this.passwordEditText.setTextSize(1, 20.0F);
          this.passwordEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
          this.passwordEditText.setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, false));
          this.passwordEditText.setMaxLines(1);
          this.passwordEditText.setLines(1);
          this.passwordEditText.setGravity(1);
          this.passwordEditText.setSingleLine(true);
          if (this.type != 1) {
            break label786;
          }
          this.passcodeSetStep = 0;
          this.passwordEditText.setImeOptions(5);
          this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
          this.passwordEditText.setTypeface(Typeface.DEFAULT);
          this.passwordEditText.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
          this.passwordEditText.setCursorSize(AndroidUtilities.dp(20.0F));
          this.passwordEditText.setCursorWidth(1.5F);
          ((FrameLayout)localObject1).addView(this.passwordEditText, LayoutHelper.createFrame(-1, 36.0F, 51, 40.0F, 90.0F, 40.0F, 0.0F));
          this.passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
          {
            public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
            {
              boolean bool = true;
              if (PasscodeActivity.this.passcodeSetStep == 0) {
                PasscodeActivity.this.processNext();
              }
              for (;;)
              {
                return bool;
                if (PasscodeActivity.this.passcodeSetStep == 1) {
                  PasscodeActivity.this.processDone();
                } else {
                  bool = false;
                }
              }
            }
          });
          this.passwordEditText.addTextChangedListener(new TextWatcher()
          {
            public void afterTextChanged(Editable paramAnonymousEditable)
            {
              if (PasscodeActivity.this.passwordEditText.length() == 4)
              {
                if ((PasscodeActivity.this.type != 2) || (SharedConfig.passcodeType != 0)) {
                  break label39;
                }
                PasscodeActivity.this.processDone();
              }
              for (;;)
              {
                return;
                label39:
                if ((PasscodeActivity.this.type == 1) && (PasscodeActivity.this.currentPasswordType == 0)) {
                  if (PasscodeActivity.this.passcodeSetStep == 0) {
                    PasscodeActivity.this.processNext();
                  } else if (PasscodeActivity.this.passcodeSetStep == 1) {
                    PasscodeActivity.this.processDone();
                  }
                }
              }
            }
            
            public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
            
            public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
          });
          this.passwordEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback()
          {
            public boolean onActionItemClicked(ActionMode paramAnonymousActionMode, MenuItem paramAnonymousMenuItem)
            {
              return false;
            }
            
            public boolean onCreateActionMode(ActionMode paramAnonymousActionMode, Menu paramAnonymousMenu)
            {
              return false;
            }
            
            public void onDestroyActionMode(ActionMode paramAnonymousActionMode) {}
            
            public boolean onPrepareActionMode(ActionMode paramAnonymousActionMode, Menu paramAnonymousMenu)
            {
              return false;
            }
          });
          if (this.type != 1) {
            break label811;
          }
          ((FrameLayout)localObject1).setTag("windowBackgroundWhite");
          this.dropDownContainer = new ActionBarMenuItem(paramContext, (ActionBarMenu)localObject2, 0, 0);
          this.dropDownContainer.setSubMenuOpenSide(1);
          this.dropDownContainer.addSubItem(2, LocaleController.getString("PasscodePIN", NUM));
          this.dropDownContainer.addSubItem(3, LocaleController.getString("PasscodePassword", NUM));
          localObject2 = this.actionBar;
          localObject1 = this.dropDownContainer;
          if (!AndroidUtilities.isTablet()) {
            break label803;
          }
          f = 64.0F;
          label512:
          ((ActionBar)localObject2).addView((View)localObject1, LayoutHelper.createFrame(-2, -1.0F, 51, f, 0.0F, 40.0F, 0.0F));
          this.dropDownContainer.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              PasscodeActivity.this.dropDownContainer.toggleSubMenu();
            }
          });
          this.dropDown = new TextView(paramContext);
          this.dropDown.setGravity(3);
          this.dropDown.setSingleLine(true);
          this.dropDown.setLines(1);
          this.dropDown.setMaxLines(1);
          this.dropDown.setEllipsize(TextUtils.TruncateAt.END);
          this.dropDown.setTextColor(Theme.getColor("actionBarDefaultTitle"));
          this.dropDown.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
          this.dropDownDrawable = paramContext.getResources().getDrawable(NUM).mutate();
          this.dropDownDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("actionBarDefaultTitle"), PorterDuff.Mode.MULTIPLY));
          this.dropDown.setCompoundDrawablesWithIntrinsicBounds(null, null, this.dropDownDrawable, null);
          this.dropDown.setCompoundDrawablePadding(AndroidUtilities.dp(4.0F));
          this.dropDown.setPadding(0, 0, AndroidUtilities.dp(10.0F), 0);
          this.dropDownContainer.addView(this.dropDown, LayoutHelper.createFrame(-2, -2.0F, 16, 16.0F, 0.0F, 0.0F, 1.0F));
          label739:
          updateDropDownTextView();
        }
      }
    }
    for (;;)
    {
      return this.fragmentView;
      this.titleTextView.setText(LocaleController.getString("EnterNewFirstPasscode", NUM));
      break;
      this.titleTextView.setText(LocaleController.getString("EnterCurrentPasscode", NUM));
      break;
      label786:
      this.passcodeSetStep = 1;
      this.passwordEditText.setImeOptions(6);
      break label290;
      label803:
      f = 56.0F;
      break label512;
      label811:
      this.actionBar.setTitle(LocaleController.getString("Passcode", NUM));
      break label739;
      this.actionBar.setTitle(LocaleController.getString("Passcode", NUM));
      ((FrameLayout)localObject1).setTag("windowBackgroundGray");
      ((FrameLayout)localObject1).setBackgroundColor(Theme.getColor("windowBackgroundGray"));
      this.listView = new RecyclerListView(paramContext);
      this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false)
      {
        public boolean supportsPredictiveItemAnimations()
        {
          return false;
        }
      });
      this.listView.setVerticalScrollBarEnabled(false);
      this.listView.setItemAnimator(null);
      this.listView.setLayoutAnimation(null);
      ((FrameLayout)localObject1).addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      localObject1 = this.listView;
      paramContext = new ListAdapter(paramContext);
      this.listAdapter = paramContext;
      ((RecyclerListView)localObject1).setAdapter(paramContext);
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(final View paramAnonymousView, final int paramAnonymousInt)
        {
          boolean bool1 = true;
          boolean bool2 = true;
          boolean bool3 = true;
          if (!paramAnonymousView.isEnabled()) {}
          label97:
          label174:
          do
          {
            do
            {
              for (;;)
              {
                return;
                if (paramAnonymousInt == PasscodeActivity.this.changePasscodeRow)
                {
                  PasscodeActivity.this.presentFragment(new PasscodeActivity(1));
                }
                else
                {
                  if (paramAnonymousInt != PasscodeActivity.this.passcodeRow) {
                    break;
                  }
                  paramAnonymousView = (TextCheckCell)paramAnonymousView;
                  if (SharedConfig.passcodeHash.length() != 0)
                  {
                    SharedConfig.passcodeHash = "";
                    SharedConfig.appLocked = false;
                    SharedConfig.saveConfig();
                    int i = PasscodeActivity.this.listView.getChildCount();
                    paramAnonymousInt = 0;
                    if (paramAnonymousInt < i)
                    {
                      localObject = PasscodeActivity.this.listView.getChildAt(paramAnonymousInt);
                      if ((localObject instanceof TextSettingsCell)) {
                        ((TextSettingsCell)localObject).setTextColor(Theme.getColor("windowBackgroundWhiteGrayText7"));
                      }
                    }
                    else
                    {
                      if (SharedConfig.passcodeHash.length() == 0) {
                        break label174;
                      }
                    }
                    for (;;)
                    {
                      paramAnonymousView.setChecked(bool3);
                      NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
                      break;
                      paramAnonymousInt++;
                      break label97;
                      bool3 = false;
                    }
                  }
                  PasscodeActivity.this.presentFragment(new PasscodeActivity(1));
                }
              }
              if (paramAnonymousInt != PasscodeActivity.this.autoLockRow) {
                break;
              }
            } while (PasscodeActivity.this.getParentActivity() == null);
            Object localObject = new AlertDialog.Builder(PasscodeActivity.this.getParentActivity());
            ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AutoLock", NUM));
            paramAnonymousView = new NumberPicker(PasscodeActivity.this.getParentActivity());
            paramAnonymousView.setMinValue(0);
            paramAnonymousView.setMaxValue(4);
            if (SharedConfig.autoLockIn == 0) {
              paramAnonymousView.setValue(0);
            }
            for (;;)
            {
              paramAnonymousView.setFormatter(new NumberPicker.Formatter()
              {
                public String format(int paramAnonymous2Int)
                {
                  String str;
                  if (paramAnonymous2Int == 0) {
                    str = LocaleController.getString("AutoLockDisabled", NUM);
                  }
                  for (;;)
                  {
                    return str;
                    if (paramAnonymous2Int == 1) {
                      str = LocaleController.formatString("AutoLockInTime", NUM, new Object[] { LocaleController.formatPluralString("Minutes", 1) });
                    } else if (paramAnonymous2Int == 2) {
                      str = LocaleController.formatString("AutoLockInTime", NUM, new Object[] { LocaleController.formatPluralString("Minutes", 5) });
                    } else if (paramAnonymous2Int == 3) {
                      str = LocaleController.formatString("AutoLockInTime", NUM, new Object[] { LocaleController.formatPluralString("Hours", 1) });
                    } else if (paramAnonymous2Int == 4) {
                      str = LocaleController.formatString("AutoLockInTime", NUM, new Object[] { LocaleController.formatPluralString("Hours", 5) });
                    } else {
                      str = "";
                    }
                  }
                }
              });
              ((AlertDialog.Builder)localObject).setView(paramAnonymousView);
              ((AlertDialog.Builder)localObject).setNegativeButton(LocaleController.getString("Done", NUM), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                {
                  paramAnonymous2Int = paramAnonymousView.getValue();
                  if (paramAnonymous2Int == 0) {
                    SharedConfig.autoLockIn = 0;
                  }
                  for (;;)
                  {
                    PasscodeActivity.this.listAdapter.notifyItemChanged(paramAnonymousInt);
                    UserConfig.getInstance(PasscodeActivity.this.currentAccount).saveConfig(false);
                    return;
                    if (paramAnonymous2Int == 1) {
                      SharedConfig.autoLockIn = 60;
                    } else if (paramAnonymous2Int == 2) {
                      SharedConfig.autoLockIn = 300;
                    } else if (paramAnonymous2Int == 3) {
                      SharedConfig.autoLockIn = 3600;
                    } else if (paramAnonymous2Int == 4) {
                      SharedConfig.autoLockIn = 18000;
                    }
                  }
                }
              });
              PasscodeActivity.this.showDialog(((AlertDialog.Builder)localObject).create());
              break;
              if (SharedConfig.autoLockIn == 60) {
                paramAnonymousView.setValue(1);
              } else if (SharedConfig.autoLockIn == 300) {
                paramAnonymousView.setValue(2);
              } else if (SharedConfig.autoLockIn == 3600) {
                paramAnonymousView.setValue(3);
              } else if (SharedConfig.autoLockIn == 18000) {
                paramAnonymousView.setValue(4);
              }
            }
            if (paramAnonymousInt == PasscodeActivity.this.fingerprintRow)
            {
              if (!SharedConfig.useFingerprint) {}
              for (bool3 = bool1;; bool3 = false)
              {
                SharedConfig.useFingerprint = bool3;
                UserConfig.getInstance(PasscodeActivity.this.currentAccount).saveConfig(false);
                ((TextCheckCell)paramAnonymousView).setChecked(SharedConfig.useFingerprint);
                break;
              }
            }
          } while (paramAnonymousInt != PasscodeActivity.this.captureRow);
          if (!SharedConfig.allowScreenCapture) {}
          for (bool3 = bool2;; bool3 = false)
          {
            SharedConfig.allowScreenCapture = bool3;
            UserConfig.getInstance(PasscodeActivity.this.currentAccount).saveConfig(false);
            ((TextCheckCell)paramAnonymousView).setChecked(SharedConfig.allowScreenCapture);
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
            break;
          }
        }
      });
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if ((paramInt1 == NotificationCenter.didSetPasscode) && (this.type == 0))
    {
      updateRows();
      if (this.listAdapter != null) {
        this.listAdapter.notifyDataSetChanged();
      }
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextCheckCell.class, TextSettingsCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localThemeDescription10, localThemeDescription11, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(this.dropDown, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.dropDown, 0, null, null, new Drawable[] { this.dropDownDrawable }, null, "actionBarDefaultTitle"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumb"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrack"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumbChecked"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrackChecked"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText7"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4") };
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    if (this.listView != null) {
      this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          PasscodeActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
          PasscodeActivity.this.fixLayoutInternal();
          return true;
        }
      });
    }
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    updateRows();
    if (this.type == 0) {
      NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetPasscode);
    }
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    if (this.type == 0) {
      NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
    }
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
    }
    if (this.type != 0) {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          if (PasscodeActivity.this.passwordEditText != null)
          {
            PasscodeActivity.this.passwordEditText.requestFocus();
            AndroidUtilities.showKeyboard(PasscodeActivity.this.passwordEditText);
          }
        }
      }, 200L);
    }
    fixLayoutInternal();
  }
  
  public void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) && (this.type != 0)) {
      AndroidUtilities.showKeyboard(this.passwordEditText);
    }
  }
  
  private class ListAdapter
    extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public int getItemCount()
    {
      return PasscodeActivity.this.rowCount;
    }
    
    public int getItemViewType(int paramInt)
    {
      int i = 0;
      int j = i;
      if (paramInt != PasscodeActivity.this.passcodeRow)
      {
        j = i;
        if (paramInt != PasscodeActivity.this.fingerprintRow)
        {
          if (paramInt != PasscodeActivity.this.captureRow) {
            break label43;
          }
          j = i;
        }
      }
      for (;;)
      {
        return j;
        label43:
        if ((paramInt == PasscodeActivity.this.changePasscodeRow) || (paramInt == PasscodeActivity.this.autoLockRow))
        {
          j = 1;
        }
        else if ((paramInt != PasscodeActivity.this.passcodeDetailRow) && (paramInt != PasscodeActivity.this.autoLockDetailRow))
        {
          j = i;
          if (paramInt != PasscodeActivity.this.captureDetailRow) {}
        }
        else
        {
          j = 2;
        }
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getAdapterPosition();
      if ((i == PasscodeActivity.this.passcodeRow) || (i == PasscodeActivity.this.fingerprintRow) || (i == PasscodeActivity.this.autoLockRow) || (i == PasscodeActivity.this.captureRow) || ((SharedConfig.passcodeHash.length() != 0) && (i == PasscodeActivity.this.changePasscodeRow))) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool = false;
      switch (paramViewHolder.getItemViewType())
      {
      }
      for (;;)
      {
        return;
        paramViewHolder = (TextCheckCell)paramViewHolder.itemView;
        Object localObject;
        if (paramInt == PasscodeActivity.this.passcodeRow)
        {
          localObject = LocaleController.getString("Passcode", NUM);
          if (SharedConfig.passcodeHash.length() > 0) {
            bool = true;
          }
          paramViewHolder.setTextAndCheck((String)localObject, bool, true);
        }
        else if (paramInt == PasscodeActivity.this.fingerprintRow)
        {
          paramViewHolder.setTextAndCheck(LocaleController.getString("UnlockFingerprint", NUM), SharedConfig.useFingerprint, true);
        }
        else if (paramInt == PasscodeActivity.this.captureRow)
        {
          paramViewHolder.setTextAndCheck(LocaleController.getString("ScreenCapture", NUM), SharedConfig.allowScreenCapture, false);
          continue;
          localObject = (TextSettingsCell)paramViewHolder.itemView;
          if (paramInt == PasscodeActivity.this.changePasscodeRow)
          {
            ((TextSettingsCell)localObject).setText(LocaleController.getString("ChangePasscode", NUM), false);
            if (SharedConfig.passcodeHash.length() == 0)
            {
              ((TextSettingsCell)localObject).setTag("windowBackgroundWhiteGrayText7");
              ((TextSettingsCell)localObject).setTextColor(Theme.getColor("windowBackgroundWhiteGrayText7"));
            }
            else
            {
              ((TextSettingsCell)localObject).setTag("windowBackgroundWhiteBlackText");
              ((TextSettingsCell)localObject).setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            }
          }
          else if (paramInt == PasscodeActivity.this.autoLockRow)
          {
            if (SharedConfig.autoLockIn == 0) {
              paramViewHolder = LocaleController.formatString("AutoLockDisabled", NUM, new Object[0]);
            }
            for (;;)
            {
              ((TextSettingsCell)localObject).setTextAndValue(LocaleController.getString("AutoLock", NUM), paramViewHolder, true);
              ((TextSettingsCell)localObject).setTag("windowBackgroundWhiteBlackText");
              ((TextSettingsCell)localObject).setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
              break;
              if (SharedConfig.autoLockIn < 3600) {
                paramViewHolder = LocaleController.formatString("AutoLockInTime", NUM, new Object[] { LocaleController.formatPluralString("Minutes", SharedConfig.autoLockIn / 60) });
              } else if (SharedConfig.autoLockIn < 86400) {
                paramViewHolder = LocaleController.formatString("AutoLockInTime", NUM, new Object[] { LocaleController.formatPluralString("Hours", (int)Math.ceil(SharedConfig.autoLockIn / 60.0F / 60.0F)) });
              } else {
                paramViewHolder = LocaleController.formatString("AutoLockInTime", NUM, new Object[] { LocaleController.formatPluralString("Days", (int)Math.ceil(SharedConfig.autoLockIn / 60.0F / 60.0F / 24.0F)) });
              }
            }
            paramViewHolder = (TextInfoPrivacyCell)paramViewHolder.itemView;
            if (paramInt == PasscodeActivity.this.passcodeDetailRow)
            {
              paramViewHolder.setText(LocaleController.getString("ChangePasscodeInfo", NUM));
              if (PasscodeActivity.this.autoLockDetailRow != -1) {
                paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
              } else {
                paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
              }
            }
            else if (paramInt == PasscodeActivity.this.autoLockDetailRow)
            {
              paramViewHolder.setText(LocaleController.getString("AutoLockInfo", NUM));
              paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
            }
            else if (paramInt == PasscodeActivity.this.captureDetailRow)
            {
              paramViewHolder.setText(LocaleController.getString("ScreenCaptureInfo", NUM));
              paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
            }
          }
        }
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default: 
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
      }
      for (;;)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new TextCheckCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/PasscodeActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
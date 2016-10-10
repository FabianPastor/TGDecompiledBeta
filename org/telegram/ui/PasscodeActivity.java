package org.telegram.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.os.Vibrator;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
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
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
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
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.NumberPicker.Formatter;

public class PasscodeActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int done_button = 1;
  private static final int password_item = 3;
  private static final int pin_item = 2;
  private int autoLockDetailRow;
  private int autoLockRow;
  private int changePasscodeRow;
  private int currentPasswordType = 0;
  private TextView dropDown;
  private ActionBarMenuItem dropDownContainer;
  private int fingerprintRow;
  private String firstPassword;
  private ListAdapter listAdapter;
  private ListView listView;
  private int passcodeDetailRow;
  private int passcodeRow;
  private int passcodeSetStep = 0;
  private EditText passwordEditText;
  private int rowCount;
  private TextView titleTextView;
  private int type;
  
  public PasscodeActivity(int paramInt)
  {
    this.type = paramInt;
  }
  
  private void fixLayoutInternal()
  {
    FrameLayout.LayoutParams localLayoutParams;
    if (this.dropDownContainer != null) {
      if (!AndroidUtilities.isTablet())
      {
        localLayoutParams = (FrameLayout.LayoutParams)this.dropDownContainer.getLayoutParams();
        if (Build.VERSION.SDK_INT < 21) {
          break label81;
        }
      }
    }
    label81:
    for (int i = AndroidUtilities.statusBarHeight;; i = 0)
    {
      localLayoutParams.topMargin = i;
      this.dropDownContainer.setLayoutParams(localLayoutParams);
      if ((AndroidUtilities.isTablet()) || (ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2)) {
        break;
      }
      this.dropDown.setTextSize(18.0F);
      return;
    }
    this.dropDown.setTextSize(20.0F);
  }
  
  private void onPasscodeError()
  {
    if (getParentActivity() == null) {
      return;
    }
    Vibrator localVibrator = (Vibrator)getParentActivity().getSystemService("vibrator");
    if (localVibrator != null) {
      localVibrator.vibrate(200L);
    }
    AndroidUtilities.shakeView(this.titleTextView, 2.0F, 0);
  }
  
  private void processDone()
  {
    if (this.passwordEditText.getText().length() == 0) {
      onPasscodeError();
    }
    do
    {
      return;
      if (this.type == 1)
      {
        if (!this.firstPassword.equals(this.passwordEditText.getText().toString())) {
          try
          {
            Toast.makeText(getParentActivity(), LocaleController.getString("PasscodeDoNotMatch", 2131166084), 0).show();
            AndroidUtilities.shakeView(this.titleTextView, 2.0F, 0);
            this.passwordEditText.setText("");
            return;
          }
          catch (Exception localException1)
          {
            for (;;)
            {
              FileLog.e("tmessages", localException1);
            }
          }
        }
        try
        {
          UserConfig.passcodeSalt = new byte[16];
          Utilities.random.nextBytes(UserConfig.passcodeSalt);
          byte[] arrayOfByte1 = this.firstPassword.getBytes("UTF-8");
          byte[] arrayOfByte2 = new byte[arrayOfByte1.length + 32];
          System.arraycopy(UserConfig.passcodeSalt, 0, arrayOfByte2, 0, 16);
          System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 16, arrayOfByte1.length);
          System.arraycopy(UserConfig.passcodeSalt, 0, arrayOfByte2, arrayOfByte1.length + 16, 16);
          UserConfig.passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(arrayOfByte2, 0, arrayOfByte2.length));
          UserConfig.passcodeType = this.currentPasswordType;
          UserConfig.saveConfig(false);
          finishFragment();
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
          this.passwordEditText.clearFocus();
          AndroidUtilities.hideKeyboard(this.passwordEditText);
          return;
        }
        catch (Exception localException2)
        {
          for (;;)
          {
            FileLog.e("tmessages", localException2);
          }
        }
      }
    } while (this.type != 2);
    if (!UserConfig.checkPasscode(this.passwordEditText.getText().toString()))
    {
      this.passwordEditText.setText("");
      onPasscodeError();
      return;
    }
    this.passwordEditText.clearFocus();
    AndroidUtilities.hideKeyboard(this.passwordEditText);
    presentFragment(new PasscodeActivity(0), true);
  }
  
  private void processNext()
  {
    if ((this.passwordEditText.getText().length() == 0) || ((this.currentPasswordType == 0) && (this.passwordEditText.getText().length() != 4)))
    {
      onPasscodeError();
      return;
    }
    if (this.currentPasswordType == 0) {
      this.actionBar.setTitle(LocaleController.getString("PasscodePIN", 2131166085));
    }
    for (;;)
    {
      this.dropDownContainer.setVisibility(8);
      this.titleTextView.setText(LocaleController.getString("ReEnterYourPasscode", 2131166147));
      this.firstPassword = this.passwordEditText.getText().toString();
      this.passwordEditText.setText("");
      this.passcodeSetStep = 1;
      return;
      this.actionBar.setTitle(LocaleController.getString("PasscodePassword", 2131166086));
    }
  }
  
  private void updateDropDownTextView()
  {
    if (this.dropDown != null)
    {
      if (this.currentPasswordType == 0) {
        this.dropDown.setText(LocaleController.getString("PasscodePIN", 2131166085));
      }
    }
    else
    {
      if (((this.type != 1) || (this.currentPasswordType != 0)) && ((this.type != 2) || (UserConfig.passcodeType != 0))) {
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
      this.dropDown.setText(LocaleController.getString("PasscodePassword", 2131166086));
      break;
      label142:
      if (((this.type == 1) && (this.currentPasswordType == 1)) || ((this.type == 2) && (UserConfig.passcodeType == 1)))
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
    if (UserConfig.passcodeHash.length() > 0) {
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
        return;
      }
      catch (Throwable localThrowable)
      {
        for (;;)
        {
          FileLog.e("tmessages", localThrowable);
        }
      }
    }
    this.fingerprintRow = -1;
    this.autoLockRow = -1;
    this.autoLockDetailRow = -1;
  }
  
  public View createView(Context paramContext)
  {
    if (this.type != 3) {
      this.actionBar.setBackButtonImage(2130837700);
    }
    this.actionBar.setAllowOverlayTitle(false);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          PasscodeActivity.this.finishFragment();
        }
        do
        {
          do
          {
            return;
            if (paramAnonymousInt != 1) {
              break;
            }
            if (PasscodeActivity.this.passcodeSetStep == 0)
            {
              PasscodeActivity.this.processNext();
              return;
            }
          } while (PasscodeActivity.this.passcodeSetStep != 1);
          PasscodeActivity.this.processDone();
          return;
          if (paramAnonymousInt == 2)
          {
            PasscodeActivity.access$302(PasscodeActivity.this, 0);
            PasscodeActivity.this.updateDropDownTextView();
            return;
          }
        } while (paramAnonymousInt != 3);
        PasscodeActivity.access$302(PasscodeActivity.this, 1);
        PasscodeActivity.this.updateDropDownTextView();
      }
    });
    this.fragmentView = new FrameLayout(paramContext);
    Object localObject2 = (FrameLayout)this.fragmentView;
    Object localObject1;
    label311:
    int i;
    if (this.type != 0)
    {
      localObject1 = this.actionBar.createMenu();
      ((ActionBarMenu)localObject1).addItemWithWidth(1, 2130837720, AndroidUtilities.dp(56.0F));
      this.titleTextView = new TextView(paramContext);
      this.titleTextView.setTextColor(-9079435);
      if (this.type == 1) {
        if (UserConfig.passcodeHash.length() != 0)
        {
          this.titleTextView.setText(LocaleController.getString("EnterNewPasscode", 2131165624));
          this.titleTextView.setTextSize(1, 18.0F);
          this.titleTextView.setGravity(1);
          ((FrameLayout)localObject2).addView(this.titleTextView);
          FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.titleTextView.getLayoutParams();
          localLayoutParams.width = -2;
          localLayoutParams.height = -2;
          localLayoutParams.gravity = 1;
          localLayoutParams.topMargin = AndroidUtilities.dp(38.0F);
          this.titleTextView.setLayoutParams(localLayoutParams);
          this.passwordEditText = new EditText(paramContext);
          this.passwordEditText.setTextSize(1, 20.0F);
          this.passwordEditText.setTextColor(-16777216);
          this.passwordEditText.setMaxLines(1);
          this.passwordEditText.setLines(1);
          this.passwordEditText.setGravity(1);
          this.passwordEditText.setSingleLine(true);
          if (this.type != 1) {
            break label858;
          }
          this.passcodeSetStep = 0;
          this.passwordEditText.setImeOptions(5);
          this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
          this.passwordEditText.setTypeface(Typeface.DEFAULT);
          AndroidUtilities.clearCursorDrawable(this.passwordEditText);
          ((FrameLayout)localObject2).addView(this.passwordEditText);
          localObject2 = (FrameLayout.LayoutParams)this.passwordEditText.getLayoutParams();
          ((FrameLayout.LayoutParams)localObject2).topMargin = AndroidUtilities.dp(90.0F);
          ((FrameLayout.LayoutParams)localObject2).height = AndroidUtilities.dp(36.0F);
          ((FrameLayout.LayoutParams)localObject2).leftMargin = AndroidUtilities.dp(40.0F);
          ((FrameLayout.LayoutParams)localObject2).gravity = 51;
          ((FrameLayout.LayoutParams)localObject2).rightMargin = AndroidUtilities.dp(40.0F);
          ((FrameLayout.LayoutParams)localObject2).width = -1;
          this.passwordEditText.setLayoutParams((ViewGroup.LayoutParams)localObject2);
          this.passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
          {
            public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
            {
              if (PasscodeActivity.this.passcodeSetStep == 0)
              {
                PasscodeActivity.this.processNext();
                return true;
              }
              if (PasscodeActivity.this.passcodeSetStep == 1)
              {
                PasscodeActivity.this.processDone();
                return true;
              }
              return false;
            }
          });
          this.passwordEditText.addTextChangedListener(new TextWatcher()
          {
            public void afterTextChanged(Editable paramAnonymousEditable)
            {
              if (PasscodeActivity.this.passwordEditText.length() == 4)
              {
                if ((PasscodeActivity.this.type != 2) || (UserConfig.passcodeType != 0)) {
                  break label39;
                }
                PasscodeActivity.this.processDone();
              }
              label39:
              do
              {
                do
                {
                  return;
                } while ((PasscodeActivity.this.type != 1) || (PasscodeActivity.this.currentPasswordType != 0));
                if (PasscodeActivity.this.passcodeSetStep == 0)
                {
                  PasscodeActivity.this.processNext();
                  return;
                }
              } while (PasscodeActivity.this.passcodeSetStep != 1);
              PasscodeActivity.this.processDone();
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
            break label885;
          }
          this.dropDownContainer = new ActionBarMenuItem(paramContext, (ActionBarMenu)localObject1, 0);
          this.dropDownContainer.setSubMenuOpenSide(1);
          this.dropDownContainer.addSubItem(2, LocaleController.getString("PasscodePIN", 2131166085), 0);
          this.dropDownContainer.addSubItem(3, LocaleController.getString("PasscodePassword", 2131166086), 0);
          this.actionBar.addView(this.dropDownContainer);
          localObject1 = (FrameLayout.LayoutParams)this.dropDownContainer.getLayoutParams();
          ((FrameLayout.LayoutParams)localObject1).height = -1;
          ((FrameLayout.LayoutParams)localObject1).width = -2;
          ((FrameLayout.LayoutParams)localObject1).rightMargin = AndroidUtilities.dp(40.0F);
          if (!AndroidUtilities.isTablet()) {
            break label875;
          }
          i = AndroidUtilities.dp(64.0F);
          label594:
          ((FrameLayout.LayoutParams)localObject1).leftMargin = i;
          ((FrameLayout.LayoutParams)localObject1).gravity = 51;
          this.dropDownContainer.setLayoutParams((ViewGroup.LayoutParams)localObject1);
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
          this.dropDown.setTextColor(-1);
          this.dropDown.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
          this.dropDown.setCompoundDrawablesWithIntrinsicBounds(0, 0, 2130837716, 0);
          this.dropDown.setCompoundDrawablePadding(AndroidUtilities.dp(4.0F));
          this.dropDown.setPadding(0, 0, AndroidUtilities.dp(10.0F), 0);
          this.dropDownContainer.addView(this.dropDown);
          paramContext = (FrameLayout.LayoutParams)this.dropDown.getLayoutParams();
          paramContext.width = -2;
          paramContext.height = -2;
          paramContext.leftMargin = AndroidUtilities.dp(16.0F);
          paramContext.gravity = 16;
          paramContext.bottomMargin = AndroidUtilities.dp(1.0F);
          this.dropDown.setLayoutParams(paramContext);
          label811:
          updateDropDownTextView();
        }
      }
    }
    for (;;)
    {
      return this.fragmentView;
      this.titleTextView.setText(LocaleController.getString("EnterNewFirstPasscode", 2131165623));
      break;
      this.titleTextView.setText(LocaleController.getString("EnterCurrentPasscode", 2131165620));
      break;
      label858:
      this.passcodeSetStep = 1;
      this.passwordEditText.setImeOptions(6);
      break label311;
      label875:
      i = AndroidUtilities.dp(56.0F);
      break label594;
      label885:
      this.actionBar.setTitle(LocaleController.getString("Passcode", 2131166083));
      break label811;
      this.actionBar.setTitle(LocaleController.getString("Passcode", 2131166083));
      ((FrameLayout)localObject2).setBackgroundColor(-986896);
      this.listView = new ListView(paramContext);
      this.listView.setDivider(null);
      this.listView.setDividerHeight(0);
      this.listView.setVerticalScrollBarEnabled(false);
      this.listView.setDrawSelectorOnTop(true);
      ((FrameLayout)localObject2).addView(this.listView);
      localObject1 = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject1).width = -1;
      ((FrameLayout.LayoutParams)localObject1).height = -1;
      ((FrameLayout.LayoutParams)localObject1).gravity = 48;
      this.listView.setLayoutParams((ViewGroup.LayoutParams)localObject1);
      localObject1 = this.listView;
      paramContext = new ListAdapter(paramContext);
      this.listAdapter = paramContext;
      ((ListView)localObject1).setAdapter(paramContext);
      this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, final View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          if (paramAnonymousInt == PasscodeActivity.this.changePasscodeRow) {
            PasscodeActivity.this.presentFragment(new PasscodeActivity(1));
          }
          label153:
          do
          {
            do
            {
              return;
              if (paramAnonymousInt == PasscodeActivity.this.passcodeRow)
              {
                paramAnonymousAdapterView = (TextCheckCell)paramAnonymousView;
                if (UserConfig.passcodeHash.length() != 0)
                {
                  UserConfig.passcodeHash = "";
                  UserConfig.appLocked = false;
                  UserConfig.saveConfig(false);
                  int i = PasscodeActivity.this.listView.getChildCount();
                  paramAnonymousInt = 0;
                  if (paramAnonymousInt < i)
                  {
                    paramAnonymousView = PasscodeActivity.this.listView.getChildAt(paramAnonymousInt);
                    if ((paramAnonymousView instanceof TextSettingsCell)) {
                      ((TextSettingsCell)paramAnonymousView).setTextColor(-3750202);
                    }
                  }
                  else
                  {
                    if (UserConfig.passcodeHash.length() == 0) {
                      break label153;
                    }
                  }
                  for (bool = true;; bool = false)
                  {
                    paramAnonymousAdapterView.setChecked(bool);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
                    return;
                    paramAnonymousInt += 1;
                    break;
                  }
                }
                PasscodeActivity.this.presentFragment(new PasscodeActivity(1));
                return;
              }
              if (paramAnonymousInt != PasscodeActivity.this.autoLockRow) {
                break;
              }
            } while (PasscodeActivity.this.getParentActivity() == null);
            paramAnonymousAdapterView = new AlertDialog.Builder(PasscodeActivity.this.getParentActivity());
            paramAnonymousAdapterView.setTitle(LocaleController.getString("AutoLock", 2131165349));
            paramAnonymousView = new NumberPicker(PasscodeActivity.this.getParentActivity());
            paramAnonymousView.setMinValue(0);
            paramAnonymousView.setMaxValue(4);
            if (UserConfig.autoLockIn == 0) {
              paramAnonymousView.setValue(0);
            }
            for (;;)
            {
              paramAnonymousView.setFormatter(new NumberPicker.Formatter()
              {
                public String format(int paramAnonymous2Int)
                {
                  if (paramAnonymous2Int == 0) {
                    return LocaleController.getString("AutoLockDisabled", 2131165350);
                  }
                  if (paramAnonymous2Int == 1) {
                    return LocaleController.formatString("AutoLockInTime", 2131165351, new Object[] { LocaleController.formatPluralString("Minutes", 1) });
                  }
                  if (paramAnonymous2Int == 2) {
                    return LocaleController.formatString("AutoLockInTime", 2131165351, new Object[] { LocaleController.formatPluralString("Minutes", 5) });
                  }
                  if (paramAnonymous2Int == 3) {
                    return LocaleController.formatString("AutoLockInTime", 2131165351, new Object[] { LocaleController.formatPluralString("Hours", 1) });
                  }
                  if (paramAnonymous2Int == 4) {
                    return LocaleController.formatString("AutoLockInTime", 2131165351, new Object[] { LocaleController.formatPluralString("Hours", 5) });
                  }
                  return "";
                }
              });
              paramAnonymousAdapterView.setView(paramAnonymousView);
              paramAnonymousAdapterView.setNegativeButton(LocaleController.getString("Done", 2131165590), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                {
                  paramAnonymous2Int = paramAnonymousView.getValue();
                  if (paramAnonymous2Int == 0) {
                    UserConfig.autoLockIn = 0;
                  }
                  for (;;)
                  {
                    PasscodeActivity.this.listView.invalidateViews();
                    UserConfig.saveConfig(false);
                    return;
                    if (paramAnonymous2Int == 1) {
                      UserConfig.autoLockIn = 60;
                    } else if (paramAnonymous2Int == 2) {
                      UserConfig.autoLockIn = 300;
                    } else if (paramAnonymous2Int == 3) {
                      UserConfig.autoLockIn = 3600;
                    } else if (paramAnonymous2Int == 4) {
                      UserConfig.autoLockIn = 18000;
                    }
                  }
                }
              });
              PasscodeActivity.this.showDialog(paramAnonymousAdapterView.create());
              return;
              if (UserConfig.autoLockIn == 60) {
                paramAnonymousView.setValue(1);
              } else if (UserConfig.autoLockIn == 300) {
                paramAnonymousView.setValue(2);
              } else if (UserConfig.autoLockIn == 3600) {
                paramAnonymousView.setValue(3);
              } else if (UserConfig.autoLockIn == 18000) {
                paramAnonymousView.setValue(4);
              }
            }
          } while (paramAnonymousInt != PasscodeActivity.this.fingerprintRow);
          if (!UserConfig.useFingerprint) {}
          for (boolean bool = true;; bool = false)
          {
            UserConfig.useFingerprint = bool;
            UserConfig.saveConfig(false);
            ((TextCheckCell)paramAnonymousView).setChecked(UserConfig.useFingerprint);
            return;
          }
        }
      });
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if ((paramInt == NotificationCenter.didSetPasscode) && (this.type == 0))
    {
      updateRows();
      if (this.listAdapter != null) {
        this.listAdapter.notifyDataSetChanged();
      }
    }
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
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSetPasscode);
    }
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    if (this.type == 0) {
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetPasscode);
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
    extends BaseFragmentAdapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public boolean areAllItemsEnabled()
    {
      return false;
    }
    
    public int getCount()
    {
      return PasscodeActivity.this.rowCount;
    }
    
    public Object getItem(int paramInt)
    {
      return null;
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public int getItemViewType(int paramInt)
    {
      if ((paramInt == PasscodeActivity.this.passcodeRow) || (paramInt == PasscodeActivity.this.fingerprintRow)) {}
      do
      {
        return 0;
        if ((paramInt == PasscodeActivity.this.changePasscodeRow) || (paramInt == PasscodeActivity.this.autoLockRow)) {
          return 1;
        }
      } while ((paramInt != PasscodeActivity.this.passcodeDetailRow) && (paramInt != PasscodeActivity.this.autoLockDetailRow));
      return 2;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      boolean bool = false;
      int i = getItemViewType(paramInt);
      Object localObject;
      if (i == 0)
      {
        localObject = paramView;
        if (paramView == null)
        {
          localObject = new TextCheckCell(this.mContext);
          ((View)localObject).setBackgroundColor(-1);
        }
        paramView = (TextCheckCell)localObject;
        if (paramInt == PasscodeActivity.this.passcodeRow)
        {
          paramViewGroup = LocaleController.getString("Passcode", 2131166083);
          if (UserConfig.passcodeHash.length() > 0) {
            bool = true;
          }
          paramView.setTextAndCheck(paramViewGroup, bool, true);
          paramViewGroup = (ViewGroup)localObject;
        }
      }
      do
      {
        do
        {
          TextSettingsCell localTextSettingsCell;
          do
          {
            do
            {
              return paramViewGroup;
              paramViewGroup = (ViewGroup)localObject;
            } while (paramInt != PasscodeActivity.this.fingerprintRow);
            paramView.setTextAndCheck(LocaleController.getString("UnlockFingerprint", 2131166353), UserConfig.useFingerprint, true);
            return (View)localObject;
            if (i != 1) {
              break;
            }
            localObject = paramView;
            if (paramView == null)
            {
              localObject = new TextSettingsCell(this.mContext);
              ((View)localObject).setBackgroundColor(-1);
            }
            localTextSettingsCell = (TextSettingsCell)localObject;
            if (paramInt == PasscodeActivity.this.changePasscodeRow)
            {
              localTextSettingsCell.setText(LocaleController.getString("ChangePasscode", 2131165393), false);
              if (UserConfig.passcodeHash.length() == 0) {}
              for (paramInt = -3750202;; paramInt = -16777216)
              {
                localTextSettingsCell.setTextColor(paramInt);
                return (View)localObject;
              }
            }
            paramViewGroup = (ViewGroup)localObject;
          } while (paramInt != PasscodeActivity.this.autoLockRow);
          if (UserConfig.autoLockIn == 0) {
            paramView = LocaleController.formatString("AutoLockDisabled", 2131165350, new Object[0]);
          }
          for (;;)
          {
            localTextSettingsCell.setTextAndValue(LocaleController.getString("AutoLock", 2131165349), paramView, true);
            localTextSettingsCell.setTextColor(-16777216);
            return (View)localObject;
            if (UserConfig.autoLockIn < 3600) {
              paramView = LocaleController.formatString("AutoLockInTime", 2131165351, new Object[] { LocaleController.formatPluralString("Minutes", UserConfig.autoLockIn / 60) });
            } else if (UserConfig.autoLockIn < 86400) {
              paramView = LocaleController.formatString("AutoLockInTime", 2131165351, new Object[] { LocaleController.formatPluralString("Hours", (int)Math.ceil(UserConfig.autoLockIn / 60.0F / 60.0F)) });
            } else {
              paramView = LocaleController.formatString("AutoLockInTime", 2131165351, new Object[] { LocaleController.formatPluralString("Days", (int)Math.ceil(UserConfig.autoLockIn / 60.0F / 60.0F / 24.0F)) });
            }
          }
          paramViewGroup = paramView;
        } while (i != 2);
        localObject = paramView;
        if (paramView == null) {
          localObject = new TextInfoPrivacyCell(this.mContext);
        }
        if (paramInt == PasscodeActivity.this.passcodeDetailRow)
        {
          ((TextInfoPrivacyCell)localObject).setText(LocaleController.getString("ChangePasscodeInfo", 2131165395));
          if (PasscodeActivity.this.autoLockDetailRow != -1)
          {
            ((View)localObject).setBackgroundResource(2130837688);
            return (View)localObject;
          }
          ((View)localObject).setBackgroundResource(2130837689);
          return (View)localObject;
        }
        paramViewGroup = (ViewGroup)localObject;
      } while (paramInt != PasscodeActivity.this.autoLockDetailRow);
      ((TextInfoPrivacyCell)localObject).setText(LocaleController.getString("AutoLockInfo", 2131165352));
      ((View)localObject).setBackgroundResource(2130837689);
      return (View)localObject;
    }
    
    public int getViewTypeCount()
    {
      return 3;
    }
    
    public boolean hasStableIds()
    {
      return false;
    }
    
    public boolean isEmpty()
    {
      return false;
    }
    
    public boolean isEnabled(int paramInt)
    {
      return (paramInt == PasscodeActivity.this.passcodeRow) || (paramInt == PasscodeActivity.this.fingerprintRow) || (paramInt == PasscodeActivity.this.autoLockRow) || ((UserConfig.passcodeHash.length() != 0) && (paramInt == PasscodeActivity.this.changePasscodeRow));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/PasscodeActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
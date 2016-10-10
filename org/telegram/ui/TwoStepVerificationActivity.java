package org.telegram.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.text.Editable;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import java.security.SecureRandom;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_account_getPasswordSettings;
import org.telegram.tgnet.TLRPC.TL_account_noPassword;
import org.telegram.tgnet.TLRPC.TL_account_password;
import org.telegram.tgnet.TLRPC.TL_account_passwordInputSettings;
import org.telegram.tgnet.TLRPC.TL_account_updatePasswordSettings;
import org.telegram.tgnet.TLRPC.TL_auth_passwordRecovery;
import org.telegram.tgnet.TLRPC.TL_auth_recoverPassword;
import org.telegram.tgnet.TLRPC.TL_auth_requestPasswordRecovery;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.account_Password;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;

public class TwoStepVerificationActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int done_button = 1;
  private int abortPasswordRow;
  private TextView bottomButton;
  private TextView bottomTextView;
  private int changePasswordRow;
  private int changeRecoveryEmailRow;
  private TLRPC.account_Password currentPassword;
  private byte[] currentPasswordHash = new byte[0];
  private boolean destroyed;
  private ActionBarMenuItem doneItem;
  private String email;
  private boolean emailOnly;
  private String firstPassword;
  private String hint;
  private ListAdapter listAdapter;
  private ListView listView;
  private boolean loading;
  private EditText passwordEditText;
  private int passwordEmailVerifyDetailRow;
  private int passwordEnabledDetailRow;
  private boolean passwordEntered = true;
  private int passwordSetState;
  private int passwordSetupDetailRow;
  private ProgressDialog progressDialog;
  private FrameLayout progressView;
  private int rowCount;
  private ScrollView scrollView;
  private int setPasswordDetailRow;
  private int setPasswordRow;
  private int setRecoveryEmailRow;
  private int shadowRow;
  private Runnable shortPollRunnable;
  private TextView titleTextView;
  private int turnPasswordOffRow;
  private int type;
  private boolean waitingForEmail;
  
  public TwoStepVerificationActivity(int paramInt)
  {
    this.type = paramInt;
    if (paramInt == 0) {
      loadPasswordInfo(false);
    }
  }
  
  private boolean isValidEmail(String paramString)
  {
    if ((paramString == null) || (paramString.length() < 3)) {}
    int i;
    int j;
    do
    {
      return false;
      i = paramString.lastIndexOf('.');
      j = paramString.lastIndexOf('@');
    } while ((i < 0) || (j < 0) || (i < j));
    return true;
  }
  
  private void loadPasswordInfo(final boolean paramBoolean)
  {
    if (!paramBoolean) {
      this.loading = true;
    }
    TLRPC.TL_account_getPassword localTL_account_getPassword = new TLRPC.TL_account_getPassword();
    ConnectionsManager.getInstance().sendRequest(localTL_account_getPassword, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            boolean bool2 = true;
            TwoStepVerificationActivity.access$1802(TwoStepVerificationActivity.this, false);
            Object localObject;
            if (paramAnonymousTL_error == null)
            {
              if (!TwoStepVerificationActivity.8.this.val$silent)
              {
                localObject = TwoStepVerificationActivity.this;
                if ((TwoStepVerificationActivity.this.currentPassword == null) && (!(paramAnonymousTLObject instanceof TLRPC.TL_account_noPassword))) {
                  break label282;
                }
                bool1 = true;
                TwoStepVerificationActivity.access$1902((TwoStepVerificationActivity)localObject, bool1);
              }
              TwoStepVerificationActivity.access$202(TwoStepVerificationActivity.this, (TLRPC.account_Password)paramAnonymousTLObject);
              localObject = TwoStepVerificationActivity.this;
              if (TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern.length() <= 0) {
                break label287;
              }
            }
            label282:
            label287:
            for (boolean bool1 = bool2;; bool1 = false)
            {
              TwoStepVerificationActivity.access$2002((TwoStepVerificationActivity)localObject, bool1);
              localObject = new byte[TwoStepVerificationActivity.this.currentPassword.new_salt.length + 8];
              Utilities.random.nextBytes((byte[])localObject);
              System.arraycopy(TwoStepVerificationActivity.this.currentPassword.new_salt, 0, localObject, 0, TwoStepVerificationActivity.this.currentPassword.new_salt.length);
              TwoStepVerificationActivity.this.currentPassword.new_salt = ((byte[])localObject);
              if ((TwoStepVerificationActivity.this.type == 0) && (!TwoStepVerificationActivity.this.destroyed) && (TwoStepVerificationActivity.this.shortPollRunnable == null))
              {
                TwoStepVerificationActivity.access$2202(TwoStepVerificationActivity.this, new Runnable()
                {
                  public void run()
                  {
                    if (TwoStepVerificationActivity.this.shortPollRunnable == null) {
                      return;
                    }
                    TwoStepVerificationActivity.this.loadPasswordInfo(true);
                    TwoStepVerificationActivity.access$2202(TwoStepVerificationActivity.this, null);
                  }
                });
                AndroidUtilities.runOnUIThread(TwoStepVerificationActivity.this.shortPollRunnable, 5000L);
              }
              TwoStepVerificationActivity.this.updateRows();
              return;
              bool1 = false;
              break;
            }
          }
        });
      }
    }, 10);
  }
  
  private void needHideProgress()
  {
    if (this.progressDialog == null) {
      return;
    }
    try
    {
      this.progressDialog.dismiss();
      this.progressDialog = null;
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  private void needShowProgress()
  {
    if ((getParentActivity() == null) || (getParentActivity().isFinishing()) || (this.progressDialog != null)) {
      return;
    }
    this.progressDialog = new ProgressDialog(getParentActivity());
    this.progressDialog.setMessage(LocaleController.getString("Loading", 2131165834));
    this.progressDialog.setCanceledOnTouchOutside(false);
    this.progressDialog.setCancelable(false);
    this.progressDialog.show();
  }
  
  private void onPasscodeError(boolean paramBoolean)
  {
    if (getParentActivity() == null) {
      return;
    }
    Vibrator localVibrator = (Vibrator)getParentActivity().getSystemService("vibrator");
    if (localVibrator != null) {
      localVibrator.vibrate(200L);
    }
    if (paramBoolean) {
      this.passwordEditText.setText("");
    }
    AndroidUtilities.shakeView(this.titleTextView, 2.0F, 0);
  }
  
  private void processDone()
  {
    Object localObject2;
    if (this.type == 0) {
      if (!this.passwordEntered)
      {
        localObject2 = this.passwordEditText.getText().toString();
        if (((String)localObject2).length() != 0) {
          break label38;
        }
        onPasscodeError(false);
      }
    }
    label38:
    do
    {
      do
      {
        return;
        final Object localObject1 = null;
        try
        {
          localObject2 = ((String)localObject2).getBytes("UTF-8");
          localObject1 = localObject2;
        }
        catch (Exception localException3)
        {
          for (;;)
          {
            FileLog.e("tmessages", localException3);
          }
        }
        needShowProgress();
        localObject2 = new byte[this.currentPassword.current_salt.length * 2 + localObject1.length];
        System.arraycopy(this.currentPassword.current_salt, 0, localObject2, 0, this.currentPassword.current_salt.length);
        System.arraycopy(localObject1, 0, localObject2, this.currentPassword.current_salt.length, localObject1.length);
        System.arraycopy(this.currentPassword.current_salt, 0, localObject2, localObject2.length - this.currentPassword.current_salt.length, this.currentPassword.current_salt.length);
        localObject1 = new TLRPC.TL_account_getPasswordSettings();
        ((TLRPC.TL_account_getPasswordSettings)localObject1).current_password_hash = Utilities.computeSHA256((byte[])localObject2, 0, localObject2.length);
        ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                TwoStepVerificationActivity.this.needHideProgress();
                if (paramAnonymousTL_error == null)
                {
                  TwoStepVerificationActivity.access$1102(TwoStepVerificationActivity.this, TwoStepVerificationActivity.11.this.val$req.current_password_hash);
                  TwoStepVerificationActivity.access$1902(TwoStepVerificationActivity.this, true);
                  AndroidUtilities.hideKeyboard(TwoStepVerificationActivity.this.passwordEditText);
                  TwoStepVerificationActivity.this.updateRows();
                  return;
                }
                if (paramAnonymousTL_error.text.equals("PASSWORD_HASH_INVALID"))
                {
                  TwoStepVerificationActivity.this.onPasscodeError(true);
                  return;
                }
                if (paramAnonymousTL_error.text.startsWith("FLOOD_WAIT"))
                {
                  int i = Utilities.parseInt(paramAnonymousTL_error.text).intValue();
                  if (i < 60) {}
                  for (String str = LocaleController.formatPluralString("Seconds", i);; str = LocaleController.formatPluralString("Minutes", i / 60))
                  {
                    TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", 2131165299), LocaleController.formatString("FloodWaitTime", 2131165640, new Object[] { str }));
                    return;
                  }
                }
                TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", 2131165299), paramAnonymousTL_error.text);
              }
            });
          }
        }, 10);
        return;
      } while (this.type != 1);
      if (this.passwordSetState == 0)
      {
        if (this.passwordEditText.getText().length() == 0)
        {
          onPasscodeError(false);
          return;
        }
        this.titleTextView.setText(LocaleController.getString("ReEnterYourPasscode", 2131166147));
        this.firstPassword = this.passwordEditText.getText().toString();
        setPasswordSetState(1);
        return;
      }
      if (this.passwordSetState == 1)
      {
        if (!this.firstPassword.equals(this.passwordEditText.getText().toString())) {
          try
          {
            Toast.makeText(getParentActivity(), LocaleController.getString("PasswordDoNotMatch", 2131166089), 0).show();
            onPasscodeError(true);
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
        setPasswordSetState(2);
        return;
      }
      if (this.passwordSetState == 2)
      {
        this.hint = this.passwordEditText.getText().toString();
        if (this.hint.toLowerCase().equals(this.firstPassword.toLowerCase())) {
          try
          {
            Toast.makeText(getParentActivity(), LocaleController.getString("PasswordAsHintError", 2131166087), 0).show();
            onPasscodeError(false);
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
        if (!this.currentPassword.has_recovery)
        {
          setPasswordSetState(3);
          return;
        }
        this.email = "";
        setNewPassword(false);
        return;
      }
      if (this.passwordSetState == 3)
      {
        this.email = this.passwordEditText.getText().toString();
        if (!isValidEmail(this.email))
        {
          onPasscodeError(false);
          return;
        }
        setNewPassword(false);
        return;
      }
    } while (this.passwordSetState != 4);
    String str = this.passwordEditText.getText().toString();
    if (str.length() == 0)
    {
      onPasscodeError(false);
      return;
    }
    TLRPC.TL_auth_recoverPassword localTL_auth_recoverPassword = new TLRPC.TL_auth_recoverPassword();
    localTL_auth_recoverPassword.code = str;
    ConnectionsManager.getInstance().sendRequest(localTL_auth_recoverPassword, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            Object localObject;
            if (paramAnonymousTL_error == null)
            {
              localObject = new AlertDialog.Builder(TwoStepVerificationActivity.this.getParentActivity());
              ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymous3DialogInterface, int paramAnonymous3Int)
                {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetTwoStepPassword, new Object[0]);
                  TwoStepVerificationActivity.this.finishFragment();
                }
              });
              ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("PasswordReset", 2131166094));
              ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", 2131165299));
              localObject = TwoStepVerificationActivity.this.showDialog(((AlertDialog.Builder)localObject).create());
              if (localObject != null)
              {
                ((Dialog)localObject).setCanceledOnTouchOutside(false);
                ((Dialog)localObject).setCancelable(false);
              }
              return;
            }
            if (paramAnonymousTL_error.text.startsWith("CODE_INVALID"))
            {
              TwoStepVerificationActivity.this.onPasscodeError(true);
              return;
            }
            if (paramAnonymousTL_error.text.startsWith("FLOOD_WAIT"))
            {
              int i = Utilities.parseInt(paramAnonymousTL_error.text).intValue();
              if (i < 60) {}
              for (localObject = LocaleController.formatPluralString("Seconds", i);; localObject = LocaleController.formatPluralString("Minutes", i / 60))
              {
                TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", 2131165299), LocaleController.formatString("FloodWaitTime", 2131165640, new Object[] { localObject }));
                return;
              }
            }
            TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", 2131165299), paramAnonymousTL_error.text);
          }
        });
      }
    }, 10);
  }
  
  private void setNewPassword(final boolean paramBoolean)
  {
    final TLRPC.TL_account_updatePasswordSettings localTL_account_updatePasswordSettings = new TLRPC.TL_account_updatePasswordSettings();
    localTL_account_updatePasswordSettings.current_password_hash = this.currentPasswordHash;
    localTL_account_updatePasswordSettings.new_settings = new TLRPC.TL_account_passwordInputSettings();
    if (paramBoolean) {
      if ((this.waitingForEmail) && ((this.currentPassword instanceof TLRPC.TL_account_noPassword)))
      {
        localTL_account_updatePasswordSettings.new_settings.flags = 2;
        localTL_account_updatePasswordSettings.new_settings.email = "";
        localTL_account_updatePasswordSettings.current_password_hash = new byte[0];
      }
    }
    for (;;)
    {
      needShowProgress();
      ConnectionsManager.getInstance().sendRequest(localTL_account_updatePasswordSettings, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              TwoStepVerificationActivity.this.needHideProgress();
              if ((paramAnonymousTL_error == null) && ((paramAnonymousTLObject instanceof TLRPC.TL_boolTrue))) {
                if (TwoStepVerificationActivity.10.this.val$clear)
                {
                  TwoStepVerificationActivity.access$202(TwoStepVerificationActivity.this, null);
                  TwoStepVerificationActivity.access$1102(TwoStepVerificationActivity.this, new byte[0]);
                  TwoStepVerificationActivity.this.loadPasswordInfo(false);
                  TwoStepVerificationActivity.this.updateRows();
                }
              }
              label84:
              Object localObject;
              do
              {
                do
                {
                  do
                  {
                    break label84;
                    do
                    {
                      return;
                    } while (TwoStepVerificationActivity.this.getParentActivity() == null);
                    localObject = new AlertDialog.Builder(TwoStepVerificationActivity.this.getParentActivity());
                    ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
                    {
                      public void onClick(DialogInterface paramAnonymous3DialogInterface, int paramAnonymous3Int)
                      {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetTwoStepPassword, new Object[] { TwoStepVerificationActivity.10.this.val$req.new_settings.new_password_hash });
                        TwoStepVerificationActivity.this.finishFragment();
                      }
                    });
                    ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("YourPasswordSuccessText", 2131166431));
                    ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("YourPasswordSuccess", 2131166430));
                    localObject = TwoStepVerificationActivity.this.showDialog(((AlertDialog.Builder)localObject).create());
                  } while (localObject == null);
                  ((Dialog)localObject).setCanceledOnTouchOutside(false);
                  ((Dialog)localObject).setCancelable(false);
                  return;
                } while (paramAnonymousTL_error == null);
                if (!paramAnonymousTL_error.text.equals("EMAIL_UNCONFIRMED")) {
                  break;
                }
                localObject = new AlertDialog.Builder(TwoStepVerificationActivity.this.getParentActivity());
                ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
                {
                  public void onClick(DialogInterface paramAnonymous3DialogInterface, int paramAnonymous3Int)
                  {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetTwoStepPassword, new Object[] { TwoStepVerificationActivity.10.this.val$req.new_settings.new_password_hash });
                    TwoStepVerificationActivity.this.finishFragment();
                  }
                });
                ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("YourEmailAlmostThereText", 2131166423));
                ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("YourEmailAlmostThere", 2131166422));
                localObject = TwoStepVerificationActivity.this.showDialog(((AlertDialog.Builder)localObject).create());
              } while (localObject == null);
              ((Dialog)localObject).setCanceledOnTouchOutside(false);
              ((Dialog)localObject).setCancelable(false);
              return;
              if (paramAnonymousTL_error.text.equals("EMAIL_INVALID"))
              {
                TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", 2131165299), LocaleController.getString("PasswordEmailInvalid", 2131166090));
                return;
              }
              if (paramAnonymousTL_error.text.startsWith("FLOOD_WAIT"))
              {
                int i = Utilities.parseInt(paramAnonymousTL_error.text).intValue();
                if (i < 60) {}
                for (localObject = LocaleController.formatPluralString("Seconds", i);; localObject = LocaleController.formatPluralString("Minutes", i / 60))
                {
                  TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", 2131165299), LocaleController.formatString("FloodWaitTime", 2131165640, new Object[] { localObject }));
                  return;
                }
              }
              TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", 2131165299), paramAnonymousTL_error.text);
            }
          });
        }
      }, 10);
      return;
      localTL_account_updatePasswordSettings.new_settings.flags = 3;
      localTL_account_updatePasswordSettings.new_settings.hint = "";
      localTL_account_updatePasswordSettings.new_settings.new_password_hash = new byte[0];
      localTL_account_updatePasswordSettings.new_settings.new_salt = new byte[0];
      localTL_account_updatePasswordSettings.new_settings.email = "";
      continue;
      if ((this.firstPassword != null) && (this.firstPassword.length() > 0)) {
        localObject = null;
      }
      try
      {
        arrayOfByte1 = this.firstPassword.getBytes("UTF-8");
        localObject = arrayOfByte1;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          byte[] arrayOfByte1;
          byte[] arrayOfByte2;
          FileLog.e("tmessages", localException);
        }
      }
      arrayOfByte1 = this.currentPassword.new_salt;
      arrayOfByte2 = new byte[arrayOfByte1.length * 2 + localObject.length];
      System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, arrayOfByte1.length);
      System.arraycopy(localObject, 0, arrayOfByte2, arrayOfByte1.length, localObject.length);
      System.arraycopy(arrayOfByte1, 0, arrayOfByte2, arrayOfByte2.length - arrayOfByte1.length, arrayOfByte1.length);
      Object localObject = localTL_account_updatePasswordSettings.new_settings;
      ((TLRPC.TL_account_passwordInputSettings)localObject).flags |= 0x1;
      localTL_account_updatePasswordSettings.new_settings.hint = this.hint;
      localTL_account_updatePasswordSettings.new_settings.new_password_hash = Utilities.computeSHA256(arrayOfByte2, 0, arrayOfByte2.length);
      localTL_account_updatePasswordSettings.new_settings.new_salt = arrayOfByte1;
      if (this.email.length() > 0)
      {
        localObject = localTL_account_updatePasswordSettings.new_settings;
        ((TLRPC.TL_account_passwordInputSettings)localObject).flags |= 0x2;
        localTL_account_updatePasswordSettings.new_settings.email = this.email;
      }
    }
  }
  
  private void setPasswordSetState(int paramInt)
  {
    int i = 4;
    if (this.passwordEditText == null) {
      return;
    }
    this.passwordSetState = paramInt;
    if (this.passwordSetState == 0)
    {
      this.actionBar.setTitle(LocaleController.getString("YourPassword", 2131166429));
      if ((this.currentPassword instanceof TLRPC.TL_account_noPassword))
      {
        this.titleTextView.setText(LocaleController.getString("PleaseEnterFirstPassword", 2131166129));
        this.passwordEditText.setImeOptions(5);
        this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.bottomTextView.setVisibility(4);
        this.bottomButton.setVisibility(4);
      }
    }
    for (;;)
    {
      this.passwordEditText.setText("");
      return;
      this.titleTextView.setText(LocaleController.getString("PleaseEnterPassword", 2131166130));
      break;
      if (this.passwordSetState == 1)
      {
        this.actionBar.setTitle(LocaleController.getString("YourPassword", 2131166429));
        this.titleTextView.setText(LocaleController.getString("PleaseReEnterPassword", 2131166131));
        this.passwordEditText.setImeOptions(5);
        this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.bottomTextView.setVisibility(4);
        this.bottomButton.setVisibility(4);
      }
      else if (this.passwordSetState == 2)
      {
        this.actionBar.setTitle(LocaleController.getString("PasswordHint", 2131166091));
        this.titleTextView.setText(LocaleController.getString("PasswordHintText", 2131166092));
        this.passwordEditText.setImeOptions(5);
        this.passwordEditText.setTransformationMethod(null);
        this.bottomTextView.setVisibility(4);
        this.bottomButton.setVisibility(4);
      }
      else
      {
        if (this.passwordSetState == 3)
        {
          this.actionBar.setTitle(LocaleController.getString("RecoveryEmail", 2131166150));
          this.titleTextView.setText(LocaleController.getString("YourEmail", 2131166421));
          this.passwordEditText.setImeOptions(6);
          this.passwordEditText.setTransformationMethod(null);
          this.passwordEditText.setInputType(33);
          this.bottomTextView.setVisibility(0);
          TextView localTextView = this.bottomButton;
          if (this.emailOnly) {}
          for (paramInt = i;; paramInt = 0)
          {
            localTextView.setVisibility(paramInt);
            break;
          }
        }
        if (this.passwordSetState == 4)
        {
          this.actionBar.setTitle(LocaleController.getString("PasswordRecovery", 2131166093));
          this.titleTextView.setText(LocaleController.getString("PasswordCode", 2131166088));
          this.bottomTextView.setText(LocaleController.getString("RestoreEmailSentInfo", 2131166184));
          this.bottomButton.setText(LocaleController.formatString("RestoreEmailTrouble", 2131166185, new Object[] { this.currentPassword.email_unconfirmed_pattern }));
          this.passwordEditText.setImeOptions(6);
          this.passwordEditText.setTransformationMethod(null);
          this.passwordEditText.setInputType(3);
          this.bottomTextView.setVisibility(0);
          this.bottomButton.setVisibility(0);
        }
      }
    }
  }
  
  private void showAlertWithText(String paramString1, String paramString2)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), null);
    localBuilder.setTitle(paramString1);
    localBuilder.setMessage(paramString2);
    showDialog(localBuilder.create());
  }
  
  private void updateRows()
  {
    this.rowCount = 0;
    this.setPasswordRow = -1;
    this.setPasswordDetailRow = -1;
    this.changePasswordRow = -1;
    this.turnPasswordOffRow = -1;
    this.setRecoveryEmailRow = -1;
    this.changeRecoveryEmailRow = -1;
    this.abortPasswordRow = -1;
    this.passwordSetupDetailRow = -1;
    this.passwordEnabledDetailRow = -1;
    this.passwordEmailVerifyDetailRow = -1;
    this.shadowRow = -1;
    int i;
    if ((!this.loading) && (this.currentPassword != null))
    {
      if (!(this.currentPassword instanceof TLRPC.TL_account_noPassword)) {
        break label291;
      }
      if (this.waitingForEmail)
      {
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.passwordSetupDetailRow = i;
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.abortPasswordRow = i;
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.shadowRow = i;
      }
    }
    else
    {
      if (this.listAdapter != null) {
        this.listAdapter.notifyDataSetChanged();
      }
      if (!this.passwordEntered) {
        break label429;
      }
      if (this.listView != null)
      {
        this.listView.setVisibility(0);
        this.scrollView.setVisibility(4);
        this.progressView.setVisibility(0);
        this.listView.setEmptyView(this.progressView);
      }
      if (this.passwordEditText != null)
      {
        this.doneItem.setVisibility(8);
        this.passwordEditText.setVisibility(4);
        this.titleTextView.setVisibility(4);
        this.bottomTextView.setVisibility(4);
        this.bottomButton.setVisibility(4);
      }
    }
    label291:
    label409:
    label429:
    do
    {
      return;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.setPasswordRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.setPasswordDetailRow = i;
      break;
      if (!(this.currentPassword instanceof TLRPC.TL_account_password)) {
        break;
      }
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.changePasswordRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.turnPasswordOffRow = i;
      if (this.currentPassword.has_recovery)
      {
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.changeRecoveryEmailRow = i;
      }
      for (;;)
      {
        if (!this.waitingForEmail) {
          break label409;
        }
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.passwordEmailVerifyDetailRow = i;
        break;
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.setRecoveryEmailRow = i;
      }
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.passwordEnabledDetailRow = i;
      break;
      if (this.listView != null)
      {
        this.listView.setEmptyView(null);
        this.listView.setVisibility(4);
        this.scrollView.setVisibility(0);
        this.progressView.setVisibility(4);
      }
    } while (this.passwordEditText == null);
    this.doneItem.setVisibility(0);
    this.passwordEditText.setVisibility(0);
    this.titleTextView.setVisibility(0);
    this.bottomButton.setVisibility(0);
    this.bottomTextView.setVisibility(4);
    this.bottomButton.setText(LocaleController.getString("ForgotPassword", 2131165642));
    if ((this.currentPassword.hint != null) && (this.currentPassword.hint.length() > 0)) {
      this.passwordEditText.setHint(this.currentPassword.hint);
    }
    for (;;)
    {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          if (TwoStepVerificationActivity.this.passwordEditText != null)
          {
            TwoStepVerificationActivity.this.passwordEditText.requestFocus();
            AndroidUtilities.showKeyboard(TwoStepVerificationActivity.this.passwordEditText);
          }
        }
      }, 200L);
      return;
      this.passwordEditText.setHint("");
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(false);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          TwoStepVerificationActivity.this.finishFragment();
        }
        while (paramAnonymousInt != 1) {
          return;
        }
        TwoStepVerificationActivity.this.processDone();
      }
    });
    this.fragmentView = new FrameLayout(paramContext);
    Object localObject1 = (FrameLayout)this.fragmentView;
    ((FrameLayout)localObject1).setBackgroundColor(-986896);
    this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, 2130837720, AndroidUtilities.dp(56.0F));
    this.scrollView = new ScrollView(paramContext);
    this.scrollView.setFillViewport(true);
    ((FrameLayout)localObject1).addView(this.scrollView);
    Object localObject2 = (FrameLayout.LayoutParams)this.scrollView.getLayoutParams();
    ((FrameLayout.LayoutParams)localObject2).width = -1;
    ((FrameLayout.LayoutParams)localObject2).height = -1;
    this.scrollView.setLayoutParams((ViewGroup.LayoutParams)localObject2);
    localObject2 = new LinearLayout(paramContext);
    ((LinearLayout)localObject2).setOrientation(1);
    this.scrollView.addView((View)localObject2);
    Object localObject3 = (FrameLayout.LayoutParams)((LinearLayout)localObject2).getLayoutParams();
    ((FrameLayout.LayoutParams)localObject3).width = -1;
    ((FrameLayout.LayoutParams)localObject3).height = -2;
    ((LinearLayout)localObject2).setLayoutParams((ViewGroup.LayoutParams)localObject3);
    this.titleTextView = new TextView(paramContext);
    this.titleTextView.setTextColor(-9079435);
    this.titleTextView.setTextSize(1, 18.0F);
    this.titleTextView.setGravity(1);
    ((LinearLayout)localObject2).addView(this.titleTextView);
    localObject3 = (LinearLayout.LayoutParams)this.titleTextView.getLayoutParams();
    ((LinearLayout.LayoutParams)localObject3).width = -2;
    ((LinearLayout.LayoutParams)localObject3).height = -2;
    ((LinearLayout.LayoutParams)localObject3).gravity = 1;
    ((LinearLayout.LayoutParams)localObject3).topMargin = AndroidUtilities.dp(38.0F);
    this.titleTextView.setLayoutParams((ViewGroup.LayoutParams)localObject3);
    this.passwordEditText = new EditText(paramContext);
    this.passwordEditText.setTextSize(1, 20.0F);
    this.passwordEditText.setTextColor(-16777216);
    this.passwordEditText.setMaxLines(1);
    this.passwordEditText.setLines(1);
    this.passwordEditText.setGravity(1);
    this.passwordEditText.setSingleLine(true);
    this.passwordEditText.setInputType(129);
    this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
    this.passwordEditText.setTypeface(Typeface.DEFAULT);
    AndroidUtilities.clearCursorDrawable(this.passwordEditText);
    ((LinearLayout)localObject2).addView(this.passwordEditText);
    localObject3 = (LinearLayout.LayoutParams)this.passwordEditText.getLayoutParams();
    ((LinearLayout.LayoutParams)localObject3).topMargin = AndroidUtilities.dp(32.0F);
    ((LinearLayout.LayoutParams)localObject3).height = AndroidUtilities.dp(36.0F);
    ((LinearLayout.LayoutParams)localObject3).leftMargin = AndroidUtilities.dp(40.0F);
    ((LinearLayout.LayoutParams)localObject3).rightMargin = AndroidUtilities.dp(40.0F);
    ((LinearLayout.LayoutParams)localObject3).gravity = 51;
    ((LinearLayout.LayoutParams)localObject3).width = -1;
    this.passwordEditText.setLayoutParams((ViewGroup.LayoutParams)localObject3);
    this.passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
    {
      public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
      {
        if ((paramAnonymousInt == 5) || (paramAnonymousInt == 6))
        {
          TwoStepVerificationActivity.this.processDone();
          return true;
        }
        return false;
      }
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
    this.bottomTextView = new TextView(paramContext);
    this.bottomTextView.setTextColor(-9079435);
    this.bottomTextView.setTextSize(1, 14.0F);
    localObject3 = this.bottomTextView;
    int i;
    if (LocaleController.isRTL)
    {
      i = 5;
      ((TextView)localObject3).setGravity(i | 0x30);
      this.bottomTextView.setText(LocaleController.getString("YourEmailInfo", 2131166424));
      ((LinearLayout)localObject2).addView(this.bottomTextView);
      localObject3 = (LinearLayout.LayoutParams)this.bottomTextView.getLayoutParams();
      ((LinearLayout.LayoutParams)localObject3).width = -2;
      ((LinearLayout.LayoutParams)localObject3).height = -2;
      if (!LocaleController.isRTL) {
        break label1257;
      }
      i = 5;
      label636:
      ((LinearLayout.LayoutParams)localObject3).gravity = (i | 0x30);
      ((LinearLayout.LayoutParams)localObject3).topMargin = AndroidUtilities.dp(30.0F);
      ((LinearLayout.LayoutParams)localObject3).leftMargin = AndroidUtilities.dp(40.0F);
      ((LinearLayout.LayoutParams)localObject3).rightMargin = AndroidUtilities.dp(40.0F);
      this.bottomTextView.setLayoutParams((ViewGroup.LayoutParams)localObject3);
      localObject3 = new LinearLayout(paramContext);
      ((LinearLayout)localObject3).setGravity(80);
      ((LinearLayout)localObject2).addView((View)localObject3);
      localObject2 = (LinearLayout.LayoutParams)((LinearLayout)localObject3).getLayoutParams();
      ((LinearLayout.LayoutParams)localObject2).width = -1;
      ((LinearLayout.LayoutParams)localObject2).height = -1;
      ((LinearLayout)localObject3).setLayoutParams((ViewGroup.LayoutParams)localObject2);
      this.bottomButton = new TextView(paramContext);
      this.bottomButton.setTextColor(-11697229);
      this.bottomButton.setTextSize(1, 14.0F);
      localObject2 = this.bottomButton;
      if (!LocaleController.isRTL) {
        break label1262;
      }
      i = 5;
      label787:
      ((TextView)localObject2).setGravity(i | 0x50);
      this.bottomButton.setText(LocaleController.getString("YourEmailSkip", 2131166425));
      this.bottomButton.setPadding(0, AndroidUtilities.dp(10.0F), 0, 0);
      ((LinearLayout)localObject3).addView(this.bottomButton);
      localObject2 = (LinearLayout.LayoutParams)this.bottomButton.getLayoutParams();
      ((LinearLayout.LayoutParams)localObject2).width = -2;
      ((LinearLayout.LayoutParams)localObject2).height = -2;
      if (!LocaleController.isRTL) {
        break label1267;
      }
      i = 5;
      label871:
      ((LinearLayout.LayoutParams)localObject2).gravity = (i | 0x50);
      ((LinearLayout.LayoutParams)localObject2).bottomMargin = AndroidUtilities.dp(14.0F);
      ((LinearLayout.LayoutParams)localObject2).leftMargin = AndroidUtilities.dp(40.0F);
      ((LinearLayout.LayoutParams)localObject2).rightMargin = AndroidUtilities.dp(40.0F);
      this.bottomButton.setLayoutParams((ViewGroup.LayoutParams)localObject2);
      this.bottomButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (TwoStepVerificationActivity.this.type == 0)
          {
            if (TwoStepVerificationActivity.this.currentPassword.has_recovery)
            {
              TwoStepVerificationActivity.this.needShowProgress();
              paramAnonymousView = new TLRPC.TL_auth_requestPasswordRecovery();
              ConnectionsManager.getInstance().sendRequest(paramAnonymousView, new RequestDelegate()
              {
                public void run(final TLObject paramAnonymous2TLObject, final TLRPC.TL_error paramAnonymous2TL_error)
                {
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      TwoStepVerificationActivity.this.needHideProgress();
                      final Object localObject;
                      if (paramAnonymous2TL_error == null)
                      {
                        localObject = (TLRPC.TL_auth_passwordRecovery)paramAnonymous2TLObject;
                        AlertDialog.Builder localBuilder = new AlertDialog.Builder(TwoStepVerificationActivity.this.getParentActivity());
                        localBuilder.setMessage(LocaleController.formatString("RestoreEmailSent", 2131166183, new Object[] { ((TLRPC.TL_auth_passwordRecovery)localObject).email_pattern }));
                        localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
                        localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
                        {
                          public void onClick(DialogInterface paramAnonymous4DialogInterface, int paramAnonymous4Int)
                          {
                            paramAnonymous4DialogInterface = new TwoStepVerificationActivity(1);
                            TwoStepVerificationActivity.access$202(paramAnonymous4DialogInterface, TwoStepVerificationActivity.this.currentPassword);
                            paramAnonymous4DialogInterface.currentPassword.email_unconfirmed_pattern = localObject.email_pattern;
                            TwoStepVerificationActivity.access$502(paramAnonymous4DialogInterface, 4);
                            TwoStepVerificationActivity.this.presentFragment(paramAnonymous4DialogInterface);
                          }
                        });
                        localObject = TwoStepVerificationActivity.this.showDialog(localBuilder.create());
                        if (localObject != null)
                        {
                          ((Dialog)localObject).setCanceledOnTouchOutside(false);
                          ((Dialog)localObject).setCancelable(false);
                        }
                        return;
                      }
                      if (paramAnonymous2TL_error.text.startsWith("FLOOD_WAIT"))
                      {
                        int i = Utilities.parseInt(paramAnonymous2TL_error.text).intValue();
                        if (i < 60) {}
                        for (localObject = LocaleController.formatPluralString("Seconds", i);; localObject = LocaleController.formatPluralString("Minutes", i / 60))
                        {
                          TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", 2131165299), LocaleController.formatString("FloodWaitTime", 2131165640, new Object[] { localObject }));
                          return;
                        }
                      }
                      TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", 2131165299), paramAnonymous2TL_error.text);
                    }
                  });
                }
              }, 10);
              return;
            }
            TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("RestorePasswordNoEmailTitle", 2131166188), LocaleController.getString("RestorePasswordNoEmailText", 2131166187));
            return;
          }
          if (TwoStepVerificationActivity.this.passwordSetState == 4)
          {
            TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("RestorePasswordNoEmailTitle", 2131166188), LocaleController.getString("RestoreEmailTroubleText", 2131166186));
            return;
          }
          paramAnonymousView = new AlertDialog.Builder(TwoStepVerificationActivity.this.getParentActivity());
          paramAnonymousView.setMessage(LocaleController.getString("YourEmailSkipWarningText", 2131166427));
          paramAnonymousView.setTitle(LocaleController.getString("YourEmailSkipWarning", 2131166426));
          paramAnonymousView.setPositiveButton(LocaleController.getString("YourEmailSkip", 2131166425), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              TwoStepVerificationActivity.access$702(TwoStepVerificationActivity.this, "");
              TwoStepVerificationActivity.this.setNewPassword(false);
            }
          });
          paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
          TwoStepVerificationActivity.this.showDialog(paramAnonymousView.create());
        }
      });
      if (this.type != 0) {
        break label1272;
      }
      this.progressView = new FrameLayout(paramContext);
      ((FrameLayout)localObject1).addView(this.progressView);
      localObject2 = (FrameLayout.LayoutParams)this.progressView.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject2).width = -1;
      ((FrameLayout.LayoutParams)localObject2).height = -1;
      this.progressView.setLayoutParams((ViewGroup.LayoutParams)localObject2);
      this.progressView.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return true;
        }
      });
      localObject2 = new ProgressBar(paramContext);
      this.progressView.addView((View)localObject2);
      localObject2 = (FrameLayout.LayoutParams)this.progressView.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject2).width = -2;
      ((FrameLayout.LayoutParams)localObject2).height = -2;
      ((FrameLayout.LayoutParams)localObject2).gravity = 17;
      this.progressView.setLayoutParams((ViewGroup.LayoutParams)localObject2);
      this.listView = new ListView(paramContext);
      this.listView.setDivider(null);
      this.listView.setEmptyView(this.progressView);
      this.listView.setDividerHeight(0);
      this.listView.setVerticalScrollBarEnabled(false);
      this.listView.setDrawSelectorOnTop(true);
      ((FrameLayout)localObject1).addView(this.listView);
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
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          if ((paramAnonymousInt == TwoStepVerificationActivity.this.setPasswordRow) || (paramAnonymousInt == TwoStepVerificationActivity.this.changePasswordRow))
          {
            paramAnonymousAdapterView = new TwoStepVerificationActivity(1);
            TwoStepVerificationActivity.access$1102(paramAnonymousAdapterView, TwoStepVerificationActivity.this.currentPasswordHash);
            TwoStepVerificationActivity.access$202(paramAnonymousAdapterView, TwoStepVerificationActivity.this.currentPassword);
            TwoStepVerificationActivity.this.presentFragment(paramAnonymousAdapterView);
          }
          do
          {
            return;
            if ((paramAnonymousInt == TwoStepVerificationActivity.this.setRecoveryEmailRow) || (paramAnonymousInt == TwoStepVerificationActivity.this.changeRecoveryEmailRow))
            {
              paramAnonymousAdapterView = new TwoStepVerificationActivity(1);
              TwoStepVerificationActivity.access$1102(paramAnonymousAdapterView, TwoStepVerificationActivity.this.currentPasswordHash);
              TwoStepVerificationActivity.access$202(paramAnonymousAdapterView, TwoStepVerificationActivity.this.currentPassword);
              TwoStepVerificationActivity.access$1402(paramAnonymousAdapterView, true);
              TwoStepVerificationActivity.access$502(paramAnonymousAdapterView, 3);
              TwoStepVerificationActivity.this.presentFragment(paramAnonymousAdapterView);
              return;
            }
          } while ((paramAnonymousInt != TwoStepVerificationActivity.this.turnPasswordOffRow) && (paramAnonymousInt != TwoStepVerificationActivity.this.abortPasswordRow));
          paramAnonymousAdapterView = new AlertDialog.Builder(TwoStepVerificationActivity.this.getParentActivity());
          paramAnonymousAdapterView.setMessage(LocaleController.getString("TurnPasswordOffQuestion", 2131166345));
          paramAnonymousAdapterView.setTitle(LocaleController.getString("AppName", 2131165299));
          paramAnonymousAdapterView.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              TwoStepVerificationActivity.this.setNewPassword(true);
            }
          });
          paramAnonymousAdapterView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
          TwoStepVerificationActivity.this.showDialog(paramAnonymousAdapterView.create());
        }
      });
      updateRows();
      this.actionBar.setTitle(LocaleController.getString("TwoStepVerification", 2131166346));
      this.titleTextView.setText(LocaleController.getString("PleaseEnterCurrentPassword", 2131166128));
    }
    for (;;)
    {
      return this.fragmentView;
      i = 3;
      break;
      label1257:
      i = 3;
      break label636;
      label1262:
      i = 3;
      break label787;
      label1267:
      i = 3;
      break label871;
      label1272:
      if (this.type == 1) {
        setPasswordSetState(this.passwordSetState);
      }
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.didSetTwoStepPassword)
    {
      if ((paramVarArgs != null) && (paramVarArgs.length > 0) && (paramVarArgs[0] != null)) {
        this.currentPasswordHash = ((byte[])paramVarArgs[0]);
      }
      loadPasswordInfo(false);
      updateRows();
    }
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    updateRows();
    if (this.type == 0) {
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSetTwoStepPassword);
    }
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    if (this.type == 0)
    {
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetTwoStepPassword);
      if (this.shortPollRunnable != null)
      {
        AndroidUtilities.cancelRunOnUIThread(this.shortPollRunnable);
        this.shortPollRunnable = null;
      }
      this.destroyed = true;
    }
    if (this.progressDialog != null) {}
    try
    {
      this.progressDialog.dismiss();
      this.progressDialog = null;
      AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.type == 1) {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          if (TwoStepVerificationActivity.this.passwordEditText != null)
          {
            TwoStepVerificationActivity.this.passwordEditText.requestFocus();
            AndroidUtilities.showKeyboard(TwoStepVerificationActivity.this.passwordEditText);
          }
        }
      }, 200L);
    }
    AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
  }
  
  public void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) && (this.type == 1)) {
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
      if ((TwoStepVerificationActivity.this.loading) || (TwoStepVerificationActivity.this.currentPassword == null)) {
        return 0;
      }
      return TwoStepVerificationActivity.this.rowCount;
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
      if ((paramInt == TwoStepVerificationActivity.this.setPasswordDetailRow) || (paramInt == TwoStepVerificationActivity.this.shadowRow) || (paramInt == TwoStepVerificationActivity.this.passwordSetupDetailRow) || (paramInt == TwoStepVerificationActivity.this.passwordEnabledDetailRow) || (paramInt == TwoStepVerificationActivity.this.passwordEmailVerifyDetailRow)) {
        return 1;
      }
      return 0;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      int i = getItemViewType(paramInt);
      Object localObject;
      if (i == 0)
      {
        paramViewGroup = paramView;
        if (paramView == null)
        {
          paramViewGroup = new TextSettingsCell(this.mContext);
          paramViewGroup.setBackgroundColor(-1);
        }
        paramView = (TextSettingsCell)paramViewGroup;
        paramView.setTextColor(-14606047);
        if (paramInt == TwoStepVerificationActivity.this.changePasswordRow)
        {
          paramView.setText(LocaleController.getString("ChangePassword", 2131165396), true);
          localObject = paramViewGroup;
        }
      }
      do
      {
        do
        {
          do
          {
            return (View)localObject;
            if (paramInt == TwoStepVerificationActivity.this.setPasswordRow)
            {
              paramView.setText(LocaleController.getString("SetAdditionalPassword", 2131166260), true);
              return paramViewGroup;
            }
            if (paramInt == TwoStepVerificationActivity.this.turnPasswordOffRow)
            {
              paramView.setText(LocaleController.getString("TurnPasswordOff", 2131166344), true);
              return paramViewGroup;
            }
            if (paramInt == TwoStepVerificationActivity.this.changeRecoveryEmailRow)
            {
              localObject = LocaleController.getString("ChangeRecoveryEmail", 2131165401);
              if (TwoStepVerificationActivity.this.abortPasswordRow != -1) {}
              for (boolean bool = true;; bool = false)
              {
                paramView.setText((String)localObject, bool);
                return paramViewGroup;
              }
            }
            if (paramInt == TwoStepVerificationActivity.this.setRecoveryEmailRow)
            {
              paramView.setText(LocaleController.getString("SetRecoveryEmail", 2131166268), false);
              return paramViewGroup;
            }
            localObject = paramViewGroup;
          } while (paramInt != TwoStepVerificationActivity.this.abortPasswordRow);
          paramView.setTextColor(-2995895);
          paramView.setText(LocaleController.getString("AbortPassword", 2131165205), false);
          return paramViewGroup;
          localObject = paramView;
        } while (i != 1);
        paramViewGroup = paramView;
        if (paramView == null) {
          paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        }
        if (paramInt == TwoStepVerificationActivity.this.setPasswordDetailRow)
        {
          ((TextInfoPrivacyCell)paramViewGroup).setText(LocaleController.getString("SetAdditionalPasswordInfo", 2131166261));
          paramViewGroup.setBackgroundResource(2130837689);
          return paramViewGroup;
        }
        if (paramInt == TwoStepVerificationActivity.this.shadowRow)
        {
          ((TextInfoPrivacyCell)paramViewGroup).setText("");
          paramViewGroup.setBackgroundResource(2130837689);
          return paramViewGroup;
        }
        if (paramInt == TwoStepVerificationActivity.this.passwordSetupDetailRow)
        {
          ((TextInfoPrivacyCell)paramViewGroup).setText(LocaleController.formatString("EmailPasswordConfirmText", 2131165599, new Object[] { TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern }));
          paramViewGroup.setBackgroundResource(2130837690);
          return paramViewGroup;
        }
        if (paramInt == TwoStepVerificationActivity.this.passwordEnabledDetailRow)
        {
          ((TextInfoPrivacyCell)paramViewGroup).setText(LocaleController.getString("EnabledPasswordText", 2131165603));
          paramViewGroup.setBackgroundResource(2130837689);
          return paramViewGroup;
        }
        localObject = paramViewGroup;
      } while (paramInt != TwoStepVerificationActivity.this.passwordEmailVerifyDetailRow);
      ((TextInfoPrivacyCell)paramViewGroup).setText(LocaleController.formatString("PendingEmailText", 2131166095, new Object[] { TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern }));
      paramViewGroup.setBackgroundResource(2130837689);
      return paramViewGroup;
    }
    
    public int getViewTypeCount()
    {
      return 2;
    }
    
    public boolean hasStableIds()
    {
      return false;
    }
    
    public boolean isEmpty()
    {
      return (TwoStepVerificationActivity.this.loading) || (TwoStepVerificationActivity.this.currentPassword == null);
    }
    
    public boolean isEnabled(int paramInt)
    {
      return (paramInt != TwoStepVerificationActivity.this.setPasswordDetailRow) && (paramInt != TwoStepVerificationActivity.this.shadowRow) && (paramInt != TwoStepVerificationActivity.this.passwordSetupDetailRow) && (paramInt != TwoStepVerificationActivity.this.passwordEmailVerifyDetailRow) && (paramInt != TwoStepVerificationActivity.this.passwordEnabledDetailRow);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/TwoStepVerificationActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
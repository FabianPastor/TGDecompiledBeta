package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_checkUsername;
import org.telegram.tgnet.TLRPC.TL_account_updateUsername;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.LayoutHelper;

public class ChangeUsernameActivity
  extends BaseFragment
{
  private static final int done_button = 1;
  private int checkReqId = 0;
  private Runnable checkRunnable = null;
  private TextView checkTextView;
  private View doneButton;
  private EditText firstNameField;
  private String lastCheckName = null;
  private boolean lastNameAvailable = false;
  
  private boolean checkUserName(final String paramString, boolean paramBoolean)
  {
    if ((paramString != null) && (paramString.length() > 0))
    {
      this.checkTextView.setVisibility(0);
      if ((!paramBoolean) || (paramString.length() != 0)) {
        break label44;
      }
    }
    label44:
    do
    {
      return true;
      this.checkTextView.setVisibility(8);
      break;
      if (this.checkRunnable != null)
      {
        AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
        this.checkRunnable = null;
        this.lastCheckName = null;
        if (this.checkReqId != 0) {
          ConnectionsManager.getInstance().cancelRequest(this.checkReqId, true);
        }
      }
      this.lastNameAvailable = false;
      if (paramString != null)
      {
        if ((paramString.startsWith("_")) || (paramString.endsWith("_")))
        {
          this.checkTextView.setText(LocaleController.getString("UsernameInvalid", 2131166371));
          this.checkTextView.setTextColor(-3198928);
          return false;
        }
        int i = 0;
        while (i < paramString.length())
        {
          int j = paramString.charAt(i);
          if ((i == 0) && (j >= 48) && (j <= 57))
          {
            if (paramBoolean) {
              showErrorAlert(LocaleController.getString("UsernameInvalidStartNumber", 2131166374));
            }
            for (;;)
            {
              return false;
              this.checkTextView.setText(LocaleController.getString("UsernameInvalidStartNumber", 2131166374));
              this.checkTextView.setTextColor(-3198928);
            }
          }
          if (((j < 48) || (j > 57)) && ((j < 97) || (j > 122)) && ((j < 65) || (j > 90)) && (j != 95))
          {
            if (paramBoolean) {
              showErrorAlert(LocaleController.getString("UsernameInvalid", 2131166371));
            }
            for (;;)
            {
              return false;
              this.checkTextView.setText(LocaleController.getString("UsernameInvalid", 2131166371));
              this.checkTextView.setTextColor(-3198928);
            }
          }
          i += 1;
        }
      }
      if ((paramString == null) || (paramString.length() < 5))
      {
        if (paramBoolean) {
          showErrorAlert(LocaleController.getString("UsernameInvalidShort", 2131166373));
        }
        for (;;)
        {
          return false;
          this.checkTextView.setText(LocaleController.getString("UsernameInvalidShort", 2131166373));
          this.checkTextView.setTextColor(-3198928);
        }
      }
      if (paramString.length() > 32)
      {
        if (paramBoolean) {
          showErrorAlert(LocaleController.getString("UsernameInvalidLong", 2131166372));
        }
        for (;;)
        {
          return false;
          this.checkTextView.setText(LocaleController.getString("UsernameInvalidLong", 2131166372));
          this.checkTextView.setTextColor(-3198928);
        }
      }
    } while (paramBoolean);
    String str2 = UserConfig.getCurrentUser().username;
    String str1 = str2;
    if (str2 == null) {
      str1 = "";
    }
    if (paramString.equals(str1))
    {
      this.checkTextView.setText(LocaleController.formatString("UsernameAvailable", 2131166366, new Object[] { paramString }));
      this.checkTextView.setTextColor(-14248148);
      return true;
    }
    this.checkTextView.setText(LocaleController.getString("UsernameChecking", 2131166367));
    this.checkTextView.setTextColor(-9605774);
    this.lastCheckName = paramString;
    this.checkRunnable = new Runnable()
    {
      public void run()
      {
        TLRPC.TL_account_checkUsername localTL_account_checkUsername = new TLRPC.TL_account_checkUsername();
        localTL_account_checkUsername.username = paramString;
        ChangeUsernameActivity.access$402(ChangeUsernameActivity.this, ConnectionsManager.getInstance().sendRequest(localTL_account_checkUsername, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymous2TLObject, final TLRPC.TL_error paramAnonymous2TL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                ChangeUsernameActivity.access$402(ChangeUsernameActivity.this, 0);
                if ((ChangeUsernameActivity.this.lastCheckName != null) && (ChangeUsernameActivity.this.lastCheckName.equals(ChangeUsernameActivity.5.this.val$name)))
                {
                  if ((paramAnonymous2TL_error == null) && ((paramAnonymous2TLObject instanceof TLRPC.TL_boolTrue)))
                  {
                    ChangeUsernameActivity.this.checkTextView.setText(LocaleController.formatString("UsernameAvailable", 2131166366, new Object[] { ChangeUsernameActivity.5.this.val$name }));
                    ChangeUsernameActivity.this.checkTextView.setTextColor(-14248148);
                    ChangeUsernameActivity.access$702(ChangeUsernameActivity.this, true);
                  }
                }
                else {
                  return;
                }
                ChangeUsernameActivity.this.checkTextView.setText(LocaleController.getString("UsernameInUse", 2131166370));
                ChangeUsernameActivity.this.checkTextView.setTextColor(-3198928);
                ChangeUsernameActivity.access$702(ChangeUsernameActivity.this, false);
              }
            });
          }
        }, 2));
      }
    };
    AndroidUtilities.runOnUIThread(this.checkRunnable, 300L);
    return true;
  }
  
  private void saveName()
  {
    if (!checkUserName(this.firstNameField.getText().toString(), true)) {}
    do
    {
      return;
      localObject = UserConfig.getCurrentUser();
    } while ((getParentActivity() == null) || (localObject == null));
    String str = ((TLRPC.User)localObject).username;
    final Object localObject = str;
    if (str == null) {
      localObject = "";
    }
    str = this.firstNameField.getText().toString();
    if (((String)localObject).equals(str))
    {
      finishFragment();
      return;
    }
    localObject = new ProgressDialog(getParentActivity());
    ((ProgressDialog)localObject).setMessage(LocaleController.getString("Loading", 2131165834));
    ((ProgressDialog)localObject).setCanceledOnTouchOutside(false);
    ((ProgressDialog)localObject).setCancelable(false);
    TLRPC.TL_account_updateUsername localTL_account_updateUsername = new TLRPC.TL_account_updateUsername();
    localTL_account_updateUsername.username = str;
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(1) });
    final int i = ConnectionsManager.getInstance().sendRequest(localTL_account_updateUsername, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTL_error == null)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              try
              {
                ChangeUsernameActivity.6.this.val$progressDialog.dismiss();
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(paramAnonymousTLObject);
                MessagesController.getInstance().putUsers(localArrayList, false);
                MessagesStorage.getInstance().putUsersAndChats(localArrayList, null, false, true);
                UserConfig.saveConfig(true);
                ChangeUsernameActivity.this.finishFragment();
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
          });
          return;
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            try
            {
              ChangeUsernameActivity.6.this.val$progressDialog.dismiss();
              ChangeUsernameActivity.this.showErrorAlert(paramAnonymousTL_error.text);
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
        });
      }
    }, 2);
    ConnectionsManager.getInstance().bindRequestToGuid(i, this.classGuid);
    ((ProgressDialog)localObject).setButton(-2, LocaleController.getString("Cancel", 2131165386), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        ConnectionsManager.getInstance().cancelRequest(i, true);
        try
        {
          paramAnonymousDialogInterface.dismiss();
          return;
        }
        catch (Exception paramAnonymousDialogInterface)
        {
          FileLog.e("tmessages", paramAnonymousDialogInterface);
        }
      }
    });
    ((ProgressDialog)localObject).show();
  }
  
  private void showErrorAlert(String paramString)
  {
    if (getParentActivity() == null) {
      return;
    }
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
    int i = -1;
    switch (paramString.hashCode())
    {
    default: 
      switch (i)
      {
      default: 
        localBuilder.setMessage(LocaleController.getString("ErrorOccurred", 2131165626));
      }
      break;
    }
    for (;;)
    {
      localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), null);
      showDialog(localBuilder.create());
      return;
      if (!paramString.equals("USERNAME_INVALID")) {
        break;
      }
      i = 0;
      break;
      if (!paramString.equals("USERNAME_OCCUPIED")) {
        break;
      }
      i = 1;
      break;
      if (!paramString.equals("USERNAMES_UNAVAILABLE")) {
        break;
      }
      i = 2;
      break;
      localBuilder.setMessage(LocaleController.getString("UsernameInvalid", 2131166371));
      continue;
      localBuilder.setMessage(LocaleController.getString("UsernameInUse", 2131166370));
      continue;
      localBuilder.setMessage(LocaleController.getString("FeatureUnavailable", 2131165631));
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("Username", 2131166365));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          ChangeUsernameActivity.this.finishFragment();
        }
        while (paramAnonymousInt != 1) {
          return;
        }
        ChangeUsernameActivity.this.saveName();
      }
    });
    this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, 2130837720, AndroidUtilities.dp(56.0F));
    Object localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = UserConfig.getCurrentUser();
    }
    this.fragmentView = new LinearLayout(paramContext);
    ((LinearLayout)this.fragmentView).setOrientation(1);
    this.fragmentView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    this.firstNameField = new EditText(paramContext);
    this.firstNameField.setTextSize(1, 18.0F);
    this.firstNameField.setHintTextColor(-6842473);
    this.firstNameField.setTextColor(-14606047);
    this.firstNameField.setMaxLines(1);
    this.firstNameField.setLines(1);
    this.firstNameField.setPadding(0, 0, 0, 0);
    this.firstNameField.setSingleLine(true);
    localObject2 = this.firstNameField;
    if (LocaleController.isRTL)
    {
      i = 5;
      ((EditText)localObject2).setGravity(i);
      this.firstNameField.setInputType(180224);
      this.firstNameField.setImeOptions(6);
      this.firstNameField.setHint(LocaleController.getString("UsernamePlaceholder", 2131166375));
      AndroidUtilities.clearCursorDrawable(this.firstNameField);
      this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          if ((paramAnonymousInt == 6) && (ChangeUsernameActivity.this.doneButton != null))
          {
            ChangeUsernameActivity.this.doneButton.performClick();
            return true;
          }
          return false;
        }
      });
      ((LinearLayout)this.fragmentView).addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0F, 24.0F, 24.0F, 0.0F));
      if ((localObject1 != null) && (((TLRPC.User)localObject1).username != null) && (((TLRPC.User)localObject1).username.length() > 0))
      {
        this.firstNameField.setText(((TLRPC.User)localObject1).username);
        this.firstNameField.setSelection(this.firstNameField.length());
      }
      this.checkTextView = new TextView(paramContext);
      this.checkTextView.setTextSize(1, 15.0F);
      localObject1 = this.checkTextView;
      if (!LocaleController.isRTL) {
        break label574;
      }
      i = 5;
      label404:
      ((TextView)localObject1).setGravity(i);
      localObject1 = (LinearLayout)this.fragmentView;
      localObject2 = this.checkTextView;
      if (!LocaleController.isRTL) {
        break label579;
      }
      i = 5;
      label431:
      ((LinearLayout)localObject1).addView((View)localObject2, LayoutHelper.createLinear(-2, -2, i, 24, 12, 24, 0));
      paramContext = new TextView(paramContext);
      paramContext.setTextSize(1, 15.0F);
      paramContext.setTextColor(-9605774);
      if (!LocaleController.isRTL) {
        break label584;
      }
      i = 5;
      label483:
      paramContext.setGravity(i);
      paramContext.setText(AndroidUtilities.replaceTags(LocaleController.getString("UsernameHelp", 2131166369)));
      localObject1 = (LinearLayout)this.fragmentView;
      if (!LocaleController.isRTL) {
        break label589;
      }
    }
    label574:
    label579:
    label584:
    label589:
    for (int i = 5;; i = 3)
    {
      ((LinearLayout)localObject1).addView(paramContext, LayoutHelper.createLinear(-2, -2, i, 24, 10, 24, 0));
      this.firstNameField.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramAnonymousEditable) {}
        
        public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        
        public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
        {
          ChangeUsernameActivity.this.checkUserName(ChangeUsernameActivity.this.firstNameField.getText().toString(), false);
        }
      });
      this.checkTextView.setVisibility(8);
      return this.fragmentView;
      i = 3;
      break;
      i = 3;
      break label404;
      i = 3;
      break label431;
      i = 3;
      break label483;
    }
  }
  
  public void onResume()
  {
    super.onResume();
    if (!ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("view_animations", true))
    {
      this.firstNameField.requestFocus();
      AndroidUtilities.showKeyboard(this.firstNameField);
    }
  }
  
  public void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1)
    {
      this.firstNameField.requestFocus();
      AndroidUtilities.showKeyboard(this.firstNameField);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ChangeUsernameActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
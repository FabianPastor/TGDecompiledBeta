package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_updateProfile;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.LayoutHelper;

public class ChangeNameActivity
  extends BaseFragment
{
  private static final int done_button = 1;
  private View doneButton;
  private EditText firstNameField;
  private View headerLabelView;
  private EditText lastNameField;
  
  private void saveName()
  {
    TLRPC.User localUser = UserConfig.getCurrentUser();
    if ((localUser == null) || (this.lastNameField.getText() == null) || (this.firstNameField.getText() == null)) {}
    String str1;
    String str2;
    do
    {
      return;
      str1 = this.firstNameField.getText().toString();
      str2 = this.lastNameField.getText().toString();
    } while ((localUser.first_name != null) && (localUser.first_name.equals(str1)) && (localUser.last_name != null) && (localUser.last_name.equals(str2)));
    TLRPC.TL_account_updateProfile localTL_account_updateProfile = new TLRPC.TL_account_updateProfile();
    localTL_account_updateProfile.flags = 3;
    localTL_account_updateProfile.first_name = str1;
    localUser.first_name = str1;
    localTL_account_updateProfile.last_name = str2;
    localUser.last_name = str2;
    localUser = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
    if (localUser != null)
    {
      localUser.first_name = localTL_account_updateProfile.first_name;
      localUser.last_name = localTL_account_updateProfile.last_name;
    }
    UserConfig.saveConfig(true);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(1) });
    ConnectionsManager.getInstance().sendRequest(localTL_account_updateProfile, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
    });
  }
  
  public View createView(Context paramContext)
  {
    int j = 5;
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("EditName", 2131165596));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          ChangeNameActivity.this.finishFragment();
        }
        while ((paramAnonymousInt != 1) || (ChangeNameActivity.this.firstNameField.getText().length() == 0)) {
          return;
        }
        ChangeNameActivity.this.saveName();
        ChangeNameActivity.this.finishFragment();
      }
    });
    this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, 2130837720, AndroidUtilities.dp(56.0F));
    Object localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = UserConfig.getCurrentUser();
    }
    localObject2 = new LinearLayout(paramContext);
    this.fragmentView = ((View)localObject2);
    this.fragmentView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
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
    this.firstNameField.setSingleLine(true);
    EditText localEditText = this.firstNameField;
    if (LocaleController.isRTL)
    {
      i = 5;
      localEditText.setGravity(i);
      this.firstNameField.setInputType(49152);
      this.firstNameField.setImeOptions(5);
      this.firstNameField.setHint(LocaleController.getString("FirstName", 2131165638));
      AndroidUtilities.clearCursorDrawable(this.firstNameField);
      ((LinearLayout)localObject2).addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0F, 24.0F, 24.0F, 0.0F));
      this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          if (paramAnonymousInt == 5)
          {
            ChangeNameActivity.this.lastNameField.requestFocus();
            ChangeNameActivity.this.lastNameField.setSelection(ChangeNameActivity.this.lastNameField.length());
            return true;
          }
          return false;
        }
      });
      this.lastNameField = new EditText(paramContext);
      this.lastNameField.setTextSize(1, 18.0F);
      this.lastNameField.setHintTextColor(-6842473);
      this.lastNameField.setTextColor(-14606047);
      this.lastNameField.setMaxLines(1);
      this.lastNameField.setLines(1);
      this.lastNameField.setSingleLine(true);
      paramContext = this.lastNameField;
      if (!LocaleController.isRTL) {
        break label535;
      }
    }
    label535:
    for (int i = j;; i = 3)
    {
      paramContext.setGravity(i);
      this.lastNameField.setInputType(49152);
      this.lastNameField.setImeOptions(6);
      this.lastNameField.setHint(LocaleController.getString("LastName", 2131165788));
      AndroidUtilities.clearCursorDrawable(this.lastNameField);
      ((LinearLayout)localObject2).addView(this.lastNameField, LayoutHelper.createLinear(-1, 36, 24.0F, 16.0F, 24.0F, 0.0F));
      this.lastNameField.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          if (paramAnonymousInt == 6)
          {
            ChangeNameActivity.this.doneButton.performClick();
            return true;
          }
          return false;
        }
      });
      if (localObject1 != null)
      {
        this.firstNameField.setText(((TLRPC.User)localObject1).first_name);
        this.firstNameField.setSelection(this.firstNameField.length());
        this.lastNameField.setText(((TLRPC.User)localObject1).last_name);
      }
      return this.fragmentView;
      i = 3;
      break;
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
    if (paramBoolean1) {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          if (ChangeNameActivity.this.firstNameField != null)
          {
            ChangeNameActivity.this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(ChangeNameActivity.this.firstNameField);
          }
        }
      }, 100L);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ChangeNameActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
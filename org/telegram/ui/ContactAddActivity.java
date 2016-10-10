package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils.TruncateAt;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;

public class ContactAddActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int done_button = 1;
  private boolean addContact;
  private BackupImageView avatarImage;
  private View doneButton;
  private EditText firstNameField;
  private EditText lastNameField;
  private TextView nameTextView;
  private TextView onlineTextView;
  private String phone = null;
  private int user_id;
  
  public ContactAddActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }
  
  private void updateAvatarLayout()
  {
    if (this.nameTextView == null) {}
    TLRPC.User localUser;
    do
    {
      return;
      localUser = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
    } while (localUser == null);
    this.nameTextView.setText(PhoneFormat.getInstance().format("+" + localUser.phone));
    this.onlineTextView.setText(LocaleController.formatUserStatus(localUser));
    TLRPC.FileLocation localFileLocation = null;
    if (localUser.photo != null) {
      localFileLocation = localUser.photo.photo_small;
    }
    this.avatarImage.setImage(localFileLocation, "50_50", new AvatarDrawable(localUser));
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    LinearLayout localLinearLayout;
    label288:
    label409:
    float f;
    if (this.addContact)
    {
      this.actionBar.setTitle(LocaleController.getString("AddContactTitle", 2131165258));
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramAnonymousInt)
        {
          if (paramAnonymousInt == -1) {
            ContactAddActivity.this.finishFragment();
          }
          while ((paramAnonymousInt != 1) || (ContactAddActivity.this.firstNameField.getText().length() == 0)) {
            return;
          }
          TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(ContactAddActivity.this.user_id));
          localUser.first_name = ContactAddActivity.this.firstNameField.getText().toString();
          localUser.last_name = ContactAddActivity.this.lastNameField.getText().toString();
          ContactsController.getInstance().addContact(localUser);
          ContactAddActivity.this.finishFragment();
          ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("spam3_" + ContactAddActivity.this.user_id, 1).commit();
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(1) });
        }
      });
      this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, 2130837720, AndroidUtilities.dp(56.0F));
      this.fragmentView = new ScrollView(paramContext);
      localLinearLayout = new LinearLayout(paramContext);
      localLinearLayout.setOrientation(1);
      ((ScrollView)this.fragmentView).addView(localLinearLayout);
      Object localObject1 = (FrameLayout.LayoutParams)localLinearLayout.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject1).width = -1;
      ((FrameLayout.LayoutParams)localObject1).height = -2;
      localLinearLayout.setLayoutParams((ViewGroup.LayoutParams)localObject1);
      localLinearLayout.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return true;
        }
      });
      localObject1 = new FrameLayout(paramContext);
      localLinearLayout.addView((View)localObject1);
      Object localObject2 = (LinearLayout.LayoutParams)((FrameLayout)localObject1).getLayoutParams();
      ((LinearLayout.LayoutParams)localObject2).topMargin = AndroidUtilities.dp(24.0F);
      ((LinearLayout.LayoutParams)localObject2).leftMargin = AndroidUtilities.dp(24.0F);
      ((LinearLayout.LayoutParams)localObject2).rightMargin = AndroidUtilities.dp(24.0F);
      ((LinearLayout.LayoutParams)localObject2).width = -1;
      ((LinearLayout.LayoutParams)localObject2).height = -2;
      ((FrameLayout)localObject1).setLayoutParams((ViewGroup.LayoutParams)localObject2);
      this.avatarImage = new BackupImageView(paramContext);
      this.avatarImage.setRoundRadius(AndroidUtilities.dp(30.0F));
      ((FrameLayout)localObject1).addView(this.avatarImage);
      localObject2 = (FrameLayout.LayoutParams)this.avatarImage.getLayoutParams();
      if (!LocaleController.isRTL) {
        break label1267;
      }
      i = 5;
      ((FrameLayout.LayoutParams)localObject2).gravity = (i | 0x30);
      ((FrameLayout.LayoutParams)localObject2).width = AndroidUtilities.dp(60.0F);
      ((FrameLayout.LayoutParams)localObject2).height = AndroidUtilities.dp(60.0F);
      this.avatarImage.setLayoutParams((ViewGroup.LayoutParams)localObject2);
      this.nameTextView = new TextView(paramContext);
      this.nameTextView.setTextColor(-14606047);
      this.nameTextView.setTextSize(1, 20.0F);
      this.nameTextView.setLines(1);
      this.nameTextView.setMaxLines(1);
      this.nameTextView.setSingleLine(true);
      this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject2 = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label1272;
      }
      i = 5;
      ((TextView)localObject2).setGravity(i);
      this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      ((FrameLayout)localObject1).addView(this.nameTextView);
      localObject2 = (FrameLayout.LayoutParams)this.nameTextView.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject2).width = -2;
      ((FrameLayout.LayoutParams)localObject2).height = -2;
      if (!LocaleController.isRTL) {
        break label1277;
      }
      f = 0.0F;
      label471:
      ((FrameLayout.LayoutParams)localObject2).leftMargin = AndroidUtilities.dp(f);
      if (!LocaleController.isRTL) {
        break label1284;
      }
      f = 80.0F;
      label490:
      ((FrameLayout.LayoutParams)localObject2).rightMargin = AndroidUtilities.dp(f);
      ((FrameLayout.LayoutParams)localObject2).topMargin = AndroidUtilities.dp(3.0F);
      if (!LocaleController.isRTL) {
        break label1289;
      }
      i = 5;
      label518:
      ((FrameLayout.LayoutParams)localObject2).gravity = (i | 0x30);
      this.nameTextView.setLayoutParams((ViewGroup.LayoutParams)localObject2);
      this.onlineTextView = new TextView(paramContext);
      this.onlineTextView.setTextColor(-6710887);
      this.onlineTextView.setTextSize(1, 14.0F);
      this.onlineTextView.setLines(1);
      this.onlineTextView.setMaxLines(1);
      this.onlineTextView.setSingleLine(true);
      this.onlineTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject2 = this.onlineTextView;
      if (!LocaleController.isRTL) {
        break label1294;
      }
      i = 5;
      label617:
      ((TextView)localObject2).setGravity(i);
      ((FrameLayout)localObject1).addView(this.onlineTextView);
      localObject1 = (FrameLayout.LayoutParams)this.onlineTextView.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject1).width = -2;
      ((FrameLayout.LayoutParams)localObject1).height = -2;
      if (!LocaleController.isRTL) {
        break label1299;
      }
      f = 0.0F;
      label666:
      ((FrameLayout.LayoutParams)localObject1).leftMargin = AndroidUtilities.dp(f);
      if (!LocaleController.isRTL) {
        break label1306;
      }
      f = 80.0F;
      label685:
      ((FrameLayout.LayoutParams)localObject1).rightMargin = AndroidUtilities.dp(f);
      ((FrameLayout.LayoutParams)localObject1).topMargin = AndroidUtilities.dp(32.0F);
      if (!LocaleController.isRTL) {
        break label1311;
      }
      i = 5;
      label713:
      ((FrameLayout.LayoutParams)localObject1).gravity = (i | 0x30);
      this.onlineTextView.setLayoutParams((ViewGroup.LayoutParams)localObject1);
      this.firstNameField = new EditText(paramContext);
      this.firstNameField.setTextSize(1, 18.0F);
      this.firstNameField.setHintTextColor(-6842473);
      this.firstNameField.setTextColor(-14606047);
      this.firstNameField.setMaxLines(1);
      this.firstNameField.setLines(1);
      this.firstNameField.setSingleLine(true);
      localObject1 = this.firstNameField;
      if (!LocaleController.isRTL) {
        break label1316;
      }
      i = 5;
      label812:
      ((EditText)localObject1).setGravity(i);
      this.firstNameField.setInputType(49152);
      this.firstNameField.setImeOptions(5);
      this.firstNameField.setHint(LocaleController.getString("FirstName", 2131165638));
      AndroidUtilities.clearCursorDrawable(this.firstNameField);
      localLinearLayout.addView(this.firstNameField);
      localObject1 = (LinearLayout.LayoutParams)this.firstNameField.getLayoutParams();
      ((LinearLayout.LayoutParams)localObject1).topMargin = AndroidUtilities.dp(24.0F);
      ((LinearLayout.LayoutParams)localObject1).height = AndroidUtilities.dp(36.0F);
      ((LinearLayout.LayoutParams)localObject1).leftMargin = AndroidUtilities.dp(24.0F);
      ((LinearLayout.LayoutParams)localObject1).rightMargin = AndroidUtilities.dp(24.0F);
      ((LinearLayout.LayoutParams)localObject1).width = -1;
      this.firstNameField.setLayoutParams((ViewGroup.LayoutParams)localObject1);
      this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          if (paramAnonymousInt == 5)
          {
            ContactAddActivity.this.lastNameField.requestFocus();
            ContactAddActivity.this.lastNameField.setSelection(ContactAddActivity.this.lastNameField.length());
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
        break label1321;
      }
    }
    label1267:
    label1272:
    label1277:
    label1284:
    label1289:
    label1294:
    label1299:
    label1306:
    label1311:
    label1316:
    label1321:
    for (int i = 5;; i = 3)
    {
      paramContext.setGravity(i);
      this.lastNameField.setInputType(49152);
      this.lastNameField.setImeOptions(6);
      this.lastNameField.setHint(LocaleController.getString("LastName", 2131165788));
      AndroidUtilities.clearCursorDrawable(this.lastNameField);
      localLinearLayout.addView(this.lastNameField);
      paramContext = (LinearLayout.LayoutParams)this.lastNameField.getLayoutParams();
      paramContext.topMargin = AndroidUtilities.dp(16.0F);
      paramContext.height = AndroidUtilities.dp(36.0F);
      paramContext.leftMargin = AndroidUtilities.dp(24.0F);
      paramContext.rightMargin = AndroidUtilities.dp(24.0F);
      paramContext.width = -1;
      this.lastNameField.setLayoutParams(paramContext);
      this.lastNameField.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          if (paramAnonymousInt == 6)
          {
            ContactAddActivity.this.doneButton.performClick();
            return true;
          }
          return false;
        }
      });
      paramContext = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
      if (paramContext != null)
      {
        if ((paramContext.phone == null) && (this.phone != null)) {
          paramContext.phone = PhoneFormat.stripExceptNumbers(this.phone);
        }
        this.firstNameField.setText(paramContext.first_name);
        this.firstNameField.setSelection(this.firstNameField.length());
        this.lastNameField.setText(paramContext.last_name);
      }
      return this.fragmentView;
      this.actionBar.setTitle(LocaleController.getString("EditName", 2131165596));
      break;
      i = 3;
      break label288;
      i = 3;
      break label409;
      f = 80.0F;
      break label471;
      f = 0.0F;
      break label490;
      i = 3;
      break label518;
      i = 3;
      break label617;
      f = 80.0F;
      break label666;
      f = 0.0F;
      break label685;
      i = 3;
      break label713;
      i = 3;
      break label812;
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.updateInterfaces)
    {
      paramInt = ((Integer)paramVarArgs[0]).intValue();
      if (((paramInt & 0x2) != 0) || ((paramInt & 0x4) != 0)) {
        updateAvatarLayout();
      }
    }
  }
  
  public boolean onFragmentCreate()
  {
    boolean bool2 = false;
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    this.user_id = getArguments().getInt("user_id", 0);
    this.phone = getArguments().getString("phone");
    this.addContact = getArguments().getBoolean("addContact", false);
    boolean bool1 = bool2;
    if (MessagesController.getInstance().getUser(Integer.valueOf(this.user_id)) != null)
    {
      bool1 = bool2;
      if (super.onFragmentCreate()) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
  }
  
  public void onResume()
  {
    super.onResume();
    updateAvatarLayout();
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ContactAddActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
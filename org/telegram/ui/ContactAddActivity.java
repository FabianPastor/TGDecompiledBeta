package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils.TruncateAt;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
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
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ContactAddActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int done_button = 1;
  private boolean addContact;
  private AvatarDrawable avatarDrawable;
  private BackupImageView avatarImage;
  private View doneButton;
  private EditTextBoldCursor firstNameField;
  private EditTextBoldCursor lastNameField;
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
    for (;;)
    {
      return;
      Object localObject = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
      if (localObject != null)
      {
        this.nameTextView.setText(PhoneFormat.getInstance().format("+" + ((TLRPC.User)localObject).phone));
        this.onlineTextView.setText(LocaleController.formatUserStatus(this.currentAccount, (TLRPC.User)localObject));
        TLRPC.FileLocation localFileLocation = null;
        if (((TLRPC.User)localObject).photo != null) {
          localFileLocation = ((TLRPC.User)localObject).photo.photo_small;
        }
        BackupImageView localBackupImageView = this.avatarImage;
        localObject = new AvatarDrawable((TLRPC.User)localObject);
        this.avatarDrawable = ((AvatarDrawable)localObject);
        localBackupImageView.setImage(localFileLocation, "50_50", (Drawable)localObject);
      }
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    LinearLayout localLinearLayout;
    label198:
    label301:
    label336:
    float f1;
    label345:
    float f2;
    if (this.addContact)
    {
      this.actionBar.setTitle(LocaleController.getString("AddContactTitle", NUM));
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramAnonymousInt)
        {
          if (paramAnonymousInt == -1) {
            ContactAddActivity.this.finishFragment();
          }
          for (;;)
          {
            return;
            if ((paramAnonymousInt == 1) && (ContactAddActivity.this.firstNameField.getText().length() != 0))
            {
              TLRPC.User localUser = MessagesController.getInstance(ContactAddActivity.this.currentAccount).getUser(Integer.valueOf(ContactAddActivity.this.user_id));
              localUser.first_name = ContactAddActivity.this.firstNameField.getText().toString();
              localUser.last_name = ContactAddActivity.this.lastNameField.getText().toString();
              ContactsController.getInstance(ContactAddActivity.this.currentAccount).addContact(localUser);
              ContactAddActivity.this.finishFragment();
              MessagesController.getNotificationsSettings(ContactAddActivity.this.currentAccount).edit().putInt("spam3_" + ContactAddActivity.this.user_id, 1).commit();
              NotificationCenter.getInstance(ContactAddActivity.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(1) });
              NotificationCenter.getInstance(ContactAddActivity.this.currentAccount).postNotificationName(NotificationCenter.peerSettingsDidLoaded, new Object[] { Long.valueOf(ContactAddActivity.this.user_id) });
            }
          }
        }
      });
      this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0F));
      this.fragmentView = new ScrollView(paramContext);
      localLinearLayout = new LinearLayout(paramContext);
      localLinearLayout.setOrientation(1);
      ((ScrollView)this.fragmentView).addView(localLinearLayout, LayoutHelper.createScroll(-1, -2, 51));
      localLinearLayout.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return true;
        }
      });
      Object localObject1 = new FrameLayout(paramContext);
      localLinearLayout.addView((View)localObject1, LayoutHelper.createLinear(-1, -2, 24.0F, 24.0F, 24.0F, 0.0F));
      this.avatarImage = new BackupImageView(paramContext);
      this.avatarImage.setRoundRadius(AndroidUtilities.dp(30.0F));
      Object localObject2 = this.avatarImage;
      if (!LocaleController.isRTL) {
        break label1068;
      }
      i = 5;
      ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(60, 60, i | 0x30));
      this.nameTextView = new TextView(paramContext);
      this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.nameTextView.setTextSize(1, 20.0F);
      this.nameTextView.setLines(1);
      this.nameTextView.setMaxLines(1);
      this.nameTextView.setSingleLine(true);
      this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject2 = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label1074;
      }
      i = 5;
      ((TextView)localObject2).setGravity(i);
      this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      localObject2 = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label1080;
      }
      i = 5;
      if (!LocaleController.isRTL) {
        break label1086;
      }
      f1 = 0.0F;
      if (!LocaleController.isRTL) {
        break label1094;
      }
      f2 = 80.0F;
      label356:
      ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f1, 3.0F, f2, 0.0F));
      this.onlineTextView = new TextView(paramContext);
      this.onlineTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
      this.onlineTextView.setTextSize(1, 14.0F);
      this.onlineTextView.setLines(1);
      this.onlineTextView.setMaxLines(1);
      this.onlineTextView.setSingleLine(true);
      this.onlineTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject2 = this.onlineTextView;
      if (!LocaleController.isRTL) {
        break label1100;
      }
      i = 5;
      label468:
      ((TextView)localObject2).setGravity(i);
      localObject2 = this.onlineTextView;
      if (!LocaleController.isRTL) {
        break label1106;
      }
      i = 5;
      label490:
      if (!LocaleController.isRTL) {
        break label1112;
      }
      f1 = 0.0F;
      label499:
      if (!LocaleController.isRTL) {
        break label1120;
      }
      f2 = 80.0F;
      label510:
      ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f1, 32.0F, f2, 0.0F));
      this.firstNameField = new EditTextBoldCursor(paramContext);
      this.firstNameField.setTextSize(1, 18.0F);
      this.firstNameField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
      this.firstNameField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, false));
      this.firstNameField.setMaxLines(1);
      this.firstNameField.setLines(1);
      this.firstNameField.setSingleLine(true);
      localObject1 = this.firstNameField;
      if (!LocaleController.isRTL) {
        break label1126;
      }
      i = 5;
      label636:
      ((EditTextBoldCursor)localObject1).setGravity(i);
      this.firstNameField.setInputType(49152);
      this.firstNameField.setImeOptions(5);
      this.firstNameField.setHint(LocaleController.getString("FirstName", NUM));
      this.firstNameField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.firstNameField.setCursorSize(AndroidUtilities.dp(20.0F));
      this.firstNameField.setCursorWidth(1.5F);
      localLinearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0F, 24.0F, 24.0F, 0.0F));
      this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          if (paramAnonymousInt == 5)
          {
            ContactAddActivity.this.lastNameField.requestFocus();
            ContactAddActivity.this.lastNameField.setSelection(ContactAddActivity.this.lastNameField.length());
          }
          for (boolean bool = true;; bool = false) {
            return bool;
          }
        }
      });
      this.lastNameField = new EditTextBoldCursor(paramContext);
      this.lastNameField.setTextSize(1, 18.0F);
      this.lastNameField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
      this.lastNameField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.lastNameField.setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, false));
      this.lastNameField.setMaxLines(1);
      this.lastNameField.setLines(1);
      this.lastNameField.setSingleLine(true);
      paramContext = this.lastNameField;
      if (!LocaleController.isRTL) {
        break label1132;
      }
    }
    label1068:
    label1074:
    label1080:
    label1086:
    label1094:
    label1100:
    label1106:
    label1112:
    label1120:
    label1126:
    label1132:
    for (int i = 5;; i = 3)
    {
      paramContext.setGravity(i);
      this.lastNameField.setInputType(49152);
      this.lastNameField.setImeOptions(6);
      this.lastNameField.setHint(LocaleController.getString("LastName", NUM));
      this.lastNameField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.lastNameField.setCursorSize(AndroidUtilities.dp(20.0F));
      this.lastNameField.setCursorWidth(1.5F);
      localLinearLayout.addView(this.lastNameField, LayoutHelper.createLinear(-1, 36, 24.0F, 16.0F, 24.0F, 0.0F));
      this.lastNameField.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          if (paramAnonymousInt == 6) {
            ContactAddActivity.this.doneButton.performClick();
          }
          for (boolean bool = true;; bool = false) {
            return bool;
          }
        }
      });
      paramContext = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
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
      this.actionBar.setTitle(LocaleController.getString("EditName", NUM));
      break;
      i = 3;
      break label198;
      i = 3;
      break label301;
      i = 3;
      break label336;
      f1 = 80.0F;
      break label345;
      f2 = 0.0F;
      break label356;
      i = 3;
      break label468;
      i = 3;
      break label490;
      f1 = 80.0F;
      break label499;
      f2 = 0.0F;
      break label510;
      i = 3;
      break label636;
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.updateInterfaces)
    {
      paramInt1 = ((Integer)paramVarArgs[0]).intValue();
      if (((paramInt1 & 0x2) != 0) || ((paramInt1 & 0x4) != 0)) {
        updateAvatarLayout();
      }
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription.ThemeDescriptionDelegate local5 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor()
      {
        TLRPC.User localUser;
        if (ContactAddActivity.this.avatarImage != null)
        {
          localUser = MessagesController.getInstance(ContactAddActivity.this.currentAccount).getUser(Integer.valueOf(ContactAddActivity.this.user_id));
          if (localUser != null) {
            break label39;
          }
        }
        for (;;)
        {
          return;
          label39:
          ContactAddActivity.this.avatarDrawable.setInfo(localUser);
          ContactAddActivity.this.avatarImage.invalidate();
        }
      }
    };
    return new ThemeDescription[] { new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.onlineTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(null, 0, null, null, new Drawable[] { Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable }, local5, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundRed"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundOrange"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundViolet"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundGreen"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundCyan"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundBlue"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundPink") };
  }
  
  public boolean onFragmentCreate()
  {
    boolean bool1 = false;
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
    this.user_id = getArguments().getInt("user_id", 0);
    this.phone = getArguments().getString("phone");
    this.addContact = getArguments().getBoolean("addContact", false);
    boolean bool2 = bool1;
    if (MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id)) != null)
    {
      bool2 = bool1;
      if (super.onFragmentCreate()) {
        bool2 = true;
      }
    }
    return bool2;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
  }
  
  public void onResume()
  {
    super.onResume();
    updateAvatarLayout();
    if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true))
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
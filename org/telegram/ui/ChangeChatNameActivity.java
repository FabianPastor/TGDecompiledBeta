package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_chatPhoto;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.AvatarUpdater;
import org.telegram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ChangeChatNameActivity
  extends BaseFragment
  implements AvatarUpdater.AvatarUpdaterDelegate
{
  private static final int done_button = 1;
  private TLRPC.FileLocation avatar;
  private AvatarDrawable avatarDrawable;
  private BackupImageView avatarImage;
  private AvatarUpdater avatarUpdater;
  private int chatId;
  private boolean createAfterUpload;
  private TLRPC.Chat currentChat;
  private View doneButton;
  private boolean donePressed;
  private View headerLabelView;
  private EditTextBoldCursor nameTextView;
  private AlertDialog progressDialog;
  private TLRPC.InputFile uploadedAvatar;
  
  public ChangeChatNameActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }
  
  private void saveName()
  {
    MessagesController.getInstance(this.currentAccount).changeChatTitle(this.chatId, this.nameTextView.getText().toString());
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("ChannelEdit", NUM));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          ChangeChatNameActivity.this.finishFragment();
        }
        for (;;)
        {
          return;
          if ((paramAnonymousInt != 1) || (ChangeChatNameActivity.this.donePressed)) {
            break label293;
          }
          if (ChangeChatNameActivity.this.nameTextView.length() == 0)
          {
            Vibrator localVibrator = (Vibrator)ChangeChatNameActivity.this.getParentActivity().getSystemService("vibrator");
            if (localVibrator != null) {
              localVibrator.vibrate(200L);
            }
            AndroidUtilities.shakeView(ChangeChatNameActivity.this.nameTextView, 2.0F, 0);
          }
          else
          {
            ChangeChatNameActivity.access$002(ChangeChatNameActivity.this, true);
            if (ChangeChatNameActivity.this.avatarUpdater.uploadingAvatar == null) {
              break;
            }
            ChangeChatNameActivity.access$302(ChangeChatNameActivity.this, true);
            ChangeChatNameActivity.access$402(ChangeChatNameActivity.this, new AlertDialog(ChangeChatNameActivity.this.getParentActivity(), 1));
            ChangeChatNameActivity.this.progressDialog.setMessage(LocaleController.getString("Loading", NUM));
            ChangeChatNameActivity.this.progressDialog.setCanceledOnTouchOutside(false);
            ChangeChatNameActivity.this.progressDialog.setCancelable(false);
            ChangeChatNameActivity.this.progressDialog.setButton(-2, LocaleController.getString("Cancel", NUM), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                ChangeChatNameActivity.access$302(ChangeChatNameActivity.this, false);
                ChangeChatNameActivity.access$402(ChangeChatNameActivity.this, null);
                ChangeChatNameActivity.access$002(ChangeChatNameActivity.this, false);
                try
                {
                  paramAnonymous2DialogInterface.dismiss();
                  return;
                }
                catch (Exception paramAnonymous2DialogInterface)
                {
                  for (;;)
                  {
                    FileLog.e(paramAnonymous2DialogInterface);
                  }
                }
              }
            });
            ChangeChatNameActivity.this.progressDialog.show();
          }
        }
        if (ChangeChatNameActivity.this.uploadedAvatar != null) {
          MessagesController.getInstance(ChangeChatNameActivity.this.currentAccount).changeChatAvatar(ChangeChatNameActivity.this.chatId, ChangeChatNameActivity.this.uploadedAvatar);
        }
        for (;;)
        {
          ChangeChatNameActivity.this.finishFragment();
          if (ChangeChatNameActivity.this.nameTextView.getText().length() == 0) {
            break;
          }
          ChangeChatNameActivity.this.saveName();
          ChangeChatNameActivity.this.finishFragment();
          break;
          label293:
          break;
          if ((ChangeChatNameActivity.this.avatar == null) && ((ChangeChatNameActivity.this.currentChat.photo instanceof TLRPC.TL_chatPhoto))) {
            MessagesController.getInstance(ChangeChatNameActivity.this.currentAccount).changeChatAvatar(ChangeChatNameActivity.this.chatId, null);
          }
        }
      }
    });
    this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0F));
    this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
    LinearLayout localLinearLayout = new LinearLayout(paramContext);
    this.fragmentView = localLinearLayout;
    this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    this.fragmentView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
    ((LinearLayout)this.fragmentView).setOrientation(1);
    this.fragmentView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    Object localObject1 = new LinearLayout(paramContext);
    ((LinearLayout)localObject1).setOrientation(1);
    ((LinearLayout)localObject1).setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
    localLinearLayout.addView((View)localObject1, LayoutHelper.createLinear(-1, -2));
    Object localObject2 = new FrameLayout(paramContext);
    ((LinearLayout)localObject1).addView((View)localObject2, LayoutHelper.createLinear(-1, -2));
    this.avatarImage = new BackupImageView(paramContext);
    this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0F));
    this.avatarDrawable.setInfo(5, null, null, false);
    this.avatarDrawable.setDrawPhoto(true);
    localObject1 = this.avatarImage;
    int i;
    float f1;
    label281:
    float f2;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL) {
        break label914;
      }
      f1 = 0.0F;
      if (!LocaleController.isRTL) {
        break label922;
      }
      f2 = 16.0F;
      label292:
      ((FrameLayout)localObject2).addView((View)localObject1, LayoutHelper.createFrame(64, 64.0F, i | 0x30, f1, 12.0F, f2, 12.0F));
      this.avatarImage.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (ChangeChatNameActivity.this.getParentActivity() == null) {
            return;
          }
          AlertDialog.Builder localBuilder = new AlertDialog.Builder(ChangeChatNameActivity.this.getParentActivity());
          if (ChangeChatNameActivity.this.avatar != null)
          {
            paramAnonymousView = new CharSequence[3];
            paramAnonymousView[0] = LocaleController.getString("FromCamera", NUM);
            paramAnonymousView[1] = LocaleController.getString("FromGalley", NUM);
            paramAnonymousView[2] = LocaleController.getString("DeletePhoto", NUM);
          }
          for (;;)
          {
            localBuilder.setItems(paramAnonymousView, new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                if (paramAnonymous2Int == 0) {
                  ChangeChatNameActivity.this.avatarUpdater.openCamera();
                }
                for (;;)
                {
                  return;
                  if (paramAnonymous2Int == 1)
                  {
                    ChangeChatNameActivity.this.avatarUpdater.openGallery();
                  }
                  else if (paramAnonymous2Int == 2)
                  {
                    ChangeChatNameActivity.access$802(ChangeChatNameActivity.this, null);
                    ChangeChatNameActivity.access$502(ChangeChatNameActivity.this, null);
                    ChangeChatNameActivity.this.avatarImage.setImage(ChangeChatNameActivity.this.avatar, "50_50", ChangeChatNameActivity.this.avatarDrawable);
                  }
                }
              }
            });
            ChangeChatNameActivity.this.showDialog(localBuilder.create());
            break;
            paramAnonymousView = new CharSequence[2];
            paramAnonymousView[0] = LocaleController.getString("FromCamera", NUM);
            paramAnonymousView[1] = LocaleController.getString("FromGalley", NUM);
          }
        }
      });
      this.nameTextView = new EditTextBoldCursor(paramContext);
      if (!this.currentChat.megagroup) {
        break label928;
      }
      this.nameTextView.setHint(LocaleController.getString("GroupName", NUM));
      label374:
      this.nameTextView.setMaxLines(4);
      this.nameTextView.setText(this.currentChat.title);
      localObject1 = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label947;
      }
      i = 5;
      label410:
      ((EditTextBoldCursor)localObject1).setGravity(i | 0x10);
      this.nameTextView.setTextSize(1, 16.0F);
      this.nameTextView.setHint(LocaleController.getString("GroupName", NUM));
      this.nameTextView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
      this.nameTextView.setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, false));
      this.nameTextView.setImeOptions(268435456);
      this.nameTextView.setInputType(16385);
      this.nameTextView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0F));
      this.nameTextView.setFocusable(this.nameTextView.isEnabled());
      localObject1 = new InputFilter.LengthFilter(100);
      this.nameTextView.setFilters(new InputFilter[] { localObject1 });
      this.nameTextView.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.nameTextView.setCursorSize(AndroidUtilities.dp(20.0F));
      this.nameTextView.setCursorWidth(1.5F);
      this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      localObject1 = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label953;
      }
      f1 = 16.0F;
      label611:
      if (!LocaleController.isRTL) {
        break label961;
      }
      f2 = 96.0F;
      label622:
      ((FrameLayout)localObject2).addView((View)localObject1, LayoutHelper.createFrame(-1, -2.0F, 16, f1, 0.0F, f2, 0.0F));
      this.nameTextView.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramAnonymousEditable)
        {
          AvatarDrawable localAvatarDrawable = ChangeChatNameActivity.this.avatarDrawable;
          if (ChangeChatNameActivity.this.nameTextView.length() > 0) {}
          for (paramAnonymousEditable = ChangeChatNameActivity.this.nameTextView.getText().toString();; paramAnonymousEditable = null)
          {
            localAvatarDrawable.setInfo(5, paramAnonymousEditable, null, false);
            ChangeChatNameActivity.this.avatarImage.invalidate();
            return;
          }
        }
        
        public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        
        public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      });
      localObject2 = new ShadowSectionCell(paramContext);
      ((ShadowSectionCell)localObject2).setSize(20);
      localLinearLayout.addView((View)localObject2, LayoutHelper.createLinear(-1, -2));
      if (!this.currentChat.creator) {
        break label969;
      }
      localObject2 = new FrameLayout(paramContext);
      ((FrameLayout)localObject2).setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      localLinearLayout.addView((View)localObject2, LayoutHelper.createLinear(-1, -2));
      localObject1 = new TextSettingsCell(paramContext);
      ((TextSettingsCell)localObject1).setTextColor(Theme.getColor("windowBackgroundWhiteRedText5"));
      ((TextSettingsCell)localObject1).setBackgroundDrawable(Theme.getSelectorDrawable(false));
      ((TextSettingsCell)localObject1).setText(LocaleController.getString("DeleteMega", NUM), false);
      ((FrameLayout)localObject2).addView((View)localObject1, LayoutHelper.createFrame(-1, -2.0F));
      ((TextSettingsCell)localObject1).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = new AlertDialog.Builder(ChangeChatNameActivity.this.getParentActivity());
          paramAnonymousView.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", NUM));
          paramAnonymousView.setTitle(LocaleController.getString("AppName", NUM));
          paramAnonymousView.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              NotificationCenter.getInstance(ChangeChatNameActivity.this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
              if (AndroidUtilities.isTablet()) {
                NotificationCenter.getInstance(ChangeChatNameActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[] { Long.valueOf(-ChangeChatNameActivity.this.chatId) });
              }
              for (;;)
              {
                MessagesController.getInstance(ChangeChatNameActivity.this.currentAccount).deleteUserFromChat(ChangeChatNameActivity.this.chatId, MessagesController.getInstance(ChangeChatNameActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(ChangeChatNameActivity.this.currentAccount).getClientUserId())), null, true);
                ChangeChatNameActivity.this.finishFragment();
                return;
                NotificationCenter.getInstance(ChangeChatNameActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
              }
            }
          });
          paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
          ChangeChatNameActivity.this.showDialog(paramAnonymousView.create());
        }
      });
      localObject2 = new TextInfoPrivacyCell(paramContext);
      ((TextInfoPrivacyCell)localObject2).setBackgroundDrawable(Theme.getThemedDrawable(paramContext, NUM, "windowBackgroundGrayShadow"));
      ((TextInfoPrivacyCell)localObject2).setText(LocaleController.getString("MegaDeleteInfo", NUM));
      localLinearLayout.addView((View)localObject2, LayoutHelper.createLinear(-1, -2));
      label847:
      this.nameTextView.setSelection(this.nameTextView.length());
      if (this.currentChat.photo == null) {
        break label987;
      }
      this.avatar = this.currentChat.photo.photo_small;
      this.avatarImage.setImage(this.avatar, "50_50", this.avatarDrawable);
    }
    for (;;)
    {
      return this.fragmentView;
      i = 3;
      break;
      label914:
      f1 = 16.0F;
      break label281;
      label922:
      f2 = 0.0F;
      break label292;
      label928:
      this.nameTextView.setHint(LocaleController.getString("EnterChannelName", NUM));
      break label374;
      label947:
      i = 3;
      break label410;
      label953:
      f1 = 96.0F;
      break label611;
      label961:
      f2 = 16.0F;
      break label622;
      label969:
      ((ShadowSectionCell)localObject2).setBackgroundDrawable(Theme.getThemedDrawable(paramContext, NUM, "windowBackgroundGrayShadow"));
      break label847;
      label987:
      this.avatarImage.setImageDrawable(this.avatarDrawable);
    }
  }
  
  public void didUploadedPhoto(final TLRPC.InputFile paramInputFile, final TLRPC.PhotoSize paramPhotoSize1, TLRPC.PhotoSize paramPhotoSize2)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        ChangeChatNameActivity.access$502(ChangeChatNameActivity.this, paramInputFile);
        ChangeChatNameActivity.access$802(ChangeChatNameActivity.this, paramPhotoSize1.location);
        ChangeChatNameActivity.this.avatarImage.setImage(ChangeChatNameActivity.this.avatar, "50_50", ChangeChatNameActivity.this.avatarDrawable);
        if (ChangeChatNameActivity.this.createAfterUpload) {
          ChangeChatNameActivity.access$002(ChangeChatNameActivity.this, false);
        }
        try
        {
          if ((ChangeChatNameActivity.this.progressDialog != null) && (ChangeChatNameActivity.this.progressDialog.isShowing()))
          {
            ChangeChatNameActivity.this.progressDialog.dismiss();
            ChangeChatNameActivity.access$402(ChangeChatNameActivity.this, null);
          }
          ChangeChatNameActivity.this.doneButton.performClick();
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  public void onActivityResultFragment(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.avatarUpdater.onActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    this.avatarDrawable = new AvatarDrawable();
    this.chatId = getArguments().getInt("chat_id", 0);
    this.avatarUpdater = new AvatarUpdater();
    this.avatarUpdater.parentFragment = this;
    this.avatarUpdater.delegate = this;
    return true;
  }
  
  public void onResume()
  {
    super.onResume();
    if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true))
    {
      this.nameTextView.requestFocus();
      AndroidUtilities.showKeyboard(this.nameTextView);
    }
  }
  
  public void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1) {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          if (ChangeChatNameActivity.this.nameTextView != null)
          {
            ChangeChatNameActivity.this.nameTextView.requestFocus();
            AndroidUtilities.showKeyboard(ChangeChatNameActivity.this.nameTextView);
          }
        }
      }, 100L);
    }
  }
  
  public void restoreSelfArgs(Bundle paramBundle)
  {
    if (this.avatarUpdater != null) {
      this.avatarUpdater.currentPicturePath = paramBundle.getString("path");
    }
  }
  
  public void saveSelfArgs(Bundle paramBundle)
  {
    if ((this.avatarUpdater != null) && (this.avatarUpdater.currentPicturePath != null)) {
      paramBundle.putString("path", this.avatarUpdater.currentPicturePath);
    }
    if (this.nameTextView != null)
    {
      String str = this.nameTextView.getText().toString();
      if ((str != null) && (str.length() != 0)) {
        paramBundle.putString("nameTextView", str);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ChangeChatNameActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
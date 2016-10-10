package org.telegram.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.util.concurrent.Semaphore;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_chatPhoto;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.AvatarUpdater;
import org.telegram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class ChannelEditActivity
  extends BaseFragment
  implements AvatarUpdater.AvatarUpdaterDelegate, NotificationCenter.NotificationCenterDelegate
{
  private static final int done_button = 1;
  private TextSettingsCell adminCell;
  private TLRPC.FileLocation avatar;
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private BackupImageView avatarImage;
  private AvatarUpdater avatarUpdater = new AvatarUpdater();
  private int chatId;
  private boolean createAfterUpload;
  private TLRPC.Chat currentChat;
  private EditText descriptionTextView;
  private View doneButton;
  private boolean donePressed;
  private TLRPC.ChatFull info;
  private EditText nameTextView;
  private ProgressDialog progressDialog;
  private boolean signMessages;
  private TextSettingsCell typeCell;
  private TLRPC.InputFile uploadedAvatar;
  
  public ChannelEditActivity(Bundle paramBundle)
  {
    super(paramBundle);
    this.chatId = paramBundle.getInt("chat_id", 0);
  }
  
  private void updateAdminCell()
  {
    if (this.adminCell == null) {
      return;
    }
    if (this.info != null)
    {
      this.adminCell.setTextAndValue(LocaleController.getString("ChannelAdministrators", 2131165409), String.format("%d", new Object[] { Integer.valueOf(this.info.admins_count) }), false);
      return;
    }
    this.adminCell.setText(LocaleController.getString("ChannelAdministrators", 2131165409), false);
  }
  
  private void updateTypeCell()
  {
    String str;
    if ((this.currentChat.username == null) || (this.currentChat.username.length() == 0))
    {
      str = LocaleController.getString("ChannelTypePrivate", 2131165478);
      if (!this.currentChat.megagroup) {
        break label129;
      }
      this.typeCell.setTextAndValue(LocaleController.getString("GroupType", 2131165720), str, false);
    }
    for (;;)
    {
      if ((!this.currentChat.creator) || ((this.info != null) && (!this.info.can_set_username))) {
        break label148;
      }
      this.typeCell.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = new Bundle();
          paramAnonymousView.putInt("chat_id", ChannelEditActivity.this.chatId);
          paramAnonymousView = new ChannelEditTypeActivity(paramAnonymousView);
          paramAnonymousView.setInfo(ChannelEditActivity.this.info);
          ChannelEditActivity.this.presentFragment(paramAnonymousView);
        }
      });
      this.typeCell.setTextColor(-14606047);
      this.typeCell.setTextValueColor(-13660983);
      return;
      str = LocaleController.getString("ChannelTypePublic", 2131165479);
      break;
      label129:
      this.typeCell.setTextAndValue(LocaleController.getString("ChannelType", 2131165477), str, false);
    }
    label148:
    this.typeCell.setOnClickListener(null);
    this.typeCell.setTextColor(-5723992);
    this.typeCell.setTextValueColor(-5723992);
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          ChannelEditActivity.this.finishFragment();
        }
        while ((paramAnonymousInt != 1) || (ChannelEditActivity.this.donePressed)) {
          return;
        }
        if (ChannelEditActivity.this.nameTextView.length() == 0)
        {
          Vibrator localVibrator = (Vibrator)ChannelEditActivity.this.getParentActivity().getSystemService("vibrator");
          if (localVibrator != null) {
            localVibrator.vibrate(200L);
          }
          AndroidUtilities.shakeView(ChannelEditActivity.this.nameTextView, 2.0F, 0);
          return;
        }
        ChannelEditActivity.access$202(ChannelEditActivity.this, true);
        if (ChannelEditActivity.this.avatarUpdater.uploadingAvatar != null)
        {
          ChannelEditActivity.access$502(ChannelEditActivity.this, true);
          ChannelEditActivity.access$602(ChannelEditActivity.this, new ProgressDialog(ChannelEditActivity.this.getParentActivity()));
          ChannelEditActivity.this.progressDialog.setMessage(LocaleController.getString("Loading", 2131165834));
          ChannelEditActivity.this.progressDialog.setCanceledOnTouchOutside(false);
          ChannelEditActivity.this.progressDialog.setCancelable(false);
          ChannelEditActivity.this.progressDialog.setButton(-2, LocaleController.getString("Cancel", 2131165386), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              ChannelEditActivity.access$502(ChannelEditActivity.this, false);
              ChannelEditActivity.access$602(ChannelEditActivity.this, null);
              ChannelEditActivity.access$202(ChannelEditActivity.this, false);
              try
              {
                paramAnonymous2DialogInterface.dismiss();
                return;
              }
              catch (Exception paramAnonymous2DialogInterface)
              {
                FileLog.e("tmessages", paramAnonymous2DialogInterface);
              }
            }
          });
          ChannelEditActivity.this.progressDialog.show();
          return;
        }
        if (!ChannelEditActivity.this.currentChat.title.equals(ChannelEditActivity.this.nameTextView.getText().toString())) {
          MessagesController.getInstance().changeChatTitle(ChannelEditActivity.this.chatId, ChannelEditActivity.this.nameTextView.getText().toString());
        }
        if ((ChannelEditActivity.this.info != null) && (!ChannelEditActivity.this.info.about.equals(ChannelEditActivity.this.descriptionTextView.getText().toString()))) {
          MessagesController.getInstance().updateChannelAbout(ChannelEditActivity.this.chatId, ChannelEditActivity.this.descriptionTextView.getText().toString(), ChannelEditActivity.this.info);
        }
        if (ChannelEditActivity.this.signMessages != ChannelEditActivity.this.currentChat.signatures)
        {
          ChannelEditActivity.this.currentChat.signatures = true;
          MessagesController.getInstance().toogleChannelSignatures(ChannelEditActivity.this.chatId, ChannelEditActivity.this.signMessages);
        }
        if (ChannelEditActivity.this.uploadedAvatar != null) {
          MessagesController.getInstance().changeChatAvatar(ChannelEditActivity.this.chatId, ChannelEditActivity.this.uploadedAvatar);
        }
        for (;;)
        {
          ChannelEditActivity.this.finishFragment();
          return;
          if ((ChannelEditActivity.this.avatar == null) && ((ChannelEditActivity.this.currentChat.photo instanceof TLRPC.TL_chatPhoto))) {
            MessagesController.getInstance().changeChatAvatar(ChannelEditActivity.this.chatId, null);
          }
        }
      }
    });
    this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, 2130837720, AndroidUtilities.dp(56.0F));
    this.fragmentView = new ScrollView(paramContext);
    this.fragmentView.setBackgroundColor(-986896);
    Object localObject1 = (ScrollView)this.fragmentView;
    ((ScrollView)localObject1).setFillViewport(true);
    LinearLayout localLinearLayout = new LinearLayout(paramContext);
    ((ScrollView)localObject1).addView(localLinearLayout, new FrameLayout.LayoutParams(-1, -2));
    localLinearLayout.setOrientation(1);
    this.actionBar.setTitle(LocaleController.getString("ChannelEdit", 2131165425));
    Object localObject2 = new LinearLayout(paramContext);
    ((LinearLayout)localObject2).setOrientation(1);
    ((LinearLayout)localObject2).setBackgroundColor(-1);
    localLinearLayout.addView((View)localObject2, LayoutHelper.createLinear(-1, -2));
    localObject1 = new FrameLayout(paramContext);
    ((LinearLayout)localObject2).addView((View)localObject1, LayoutHelper.createLinear(-1, -2));
    this.avatarImage = new BackupImageView(paramContext);
    this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0F));
    this.avatarDrawable.setInfo(5, null, null, false);
    this.avatarDrawable.setDrawPhoto(true);
    localObject2 = this.avatarImage;
    int i;
    float f1;
    label268:
    float f2;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL) {
        break label1410;
      }
      f1 = 0.0F;
      if (!LocaleController.isRTL) {
        break label1417;
      }
      f2 = 16.0F;
      label278:
      ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(64, 64.0F, i | 0x30, f1, 12.0F, f2, 12.0F));
      this.avatarImage.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (ChannelEditActivity.this.getParentActivity() == null) {
            return;
          }
          AlertDialog.Builder localBuilder = new AlertDialog.Builder(ChannelEditActivity.this.getParentActivity());
          if (ChannelEditActivity.this.avatar != null)
          {
            paramAnonymousView = new CharSequence[3];
            paramAnonymousView[0] = LocaleController.getString("FromCamera", 2131165703);
            paramAnonymousView[1] = LocaleController.getString("FromGalley", 2131165710);
            paramAnonymousView[2] = LocaleController.getString("DeletePhoto", 2131165576);
          }
          for (;;)
          {
            localBuilder.setItems(paramAnonymousView, new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                if (paramAnonymous2Int == 0) {
                  ChannelEditActivity.this.avatarUpdater.openCamera();
                }
                do
                {
                  return;
                  if (paramAnonymous2Int == 1)
                  {
                    ChannelEditActivity.this.avatarUpdater.openGallery();
                    return;
                  }
                } while (paramAnonymous2Int != 2);
                ChannelEditActivity.access$1102(ChannelEditActivity.this, null);
                ChannelEditActivity.access$1002(ChannelEditActivity.this, null);
                ChannelEditActivity.this.avatarImage.setImage(ChannelEditActivity.this.avatar, "50_50", ChannelEditActivity.this.avatarDrawable);
              }
            });
            ChannelEditActivity.this.showDialog(localBuilder.create());
            return;
            paramAnonymousView = new CharSequence[2];
            paramAnonymousView[0] = LocaleController.getString("FromCamera", 2131165703);
            paramAnonymousView[1] = LocaleController.getString("FromGalley", 2131165710);
          }
        }
      });
      this.nameTextView = new EditText(paramContext);
      if (!this.currentChat.megagroup) {
        break label1422;
      }
      this.nameTextView.setHint(LocaleController.getString("GroupName", 2131165718));
      label359:
      this.nameTextView.setMaxLines(4);
      localObject2 = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label1441;
      }
      i = 5;
      label382:
      ((EditText)localObject2).setGravity(i | 0x10);
      this.nameTextView.setTextSize(1, 16.0F);
      this.nameTextView.setHintTextColor(-6842473);
      this.nameTextView.setImeOptions(268435456);
      this.nameTextView.setInputType(16385);
      this.nameTextView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0F));
      localObject2 = new InputFilter.LengthFilter(100);
      this.nameTextView.setFilters(new InputFilter[] { localObject2 });
      AndroidUtilities.clearCursorDrawable(this.nameTextView);
      this.nameTextView.setTextColor(-14606047);
      localObject2 = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label1447;
      }
      f1 = 16.0F;
      label508:
      if (!LocaleController.isRTL) {
        break label1454;
      }
      f2 = 96.0F;
      label518:
      ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-1, -2.0F, 16, f1, 0.0F, f2, 0.0F));
      this.nameTextView.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramAnonymousEditable)
        {
          AvatarDrawable localAvatarDrawable = ChannelEditActivity.this.avatarDrawable;
          if (ChannelEditActivity.this.nameTextView.length() > 0) {}
          for (paramAnonymousEditable = ChannelEditActivity.this.nameTextView.getText().toString();; paramAnonymousEditable = null)
          {
            localAvatarDrawable.setInfo(5, paramAnonymousEditable, null, false);
            ChannelEditActivity.this.avatarImage.invalidate();
            return;
          }
        }
        
        public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        
        public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      });
      localObject1 = new View(paramContext);
      ((View)localObject1).setBackgroundColor(-3158065);
      localLinearLayout.addView((View)localObject1, new LinearLayout.LayoutParams(-1, 1));
      localObject1 = new LinearLayout(paramContext);
      ((LinearLayout)localObject1).setOrientation(1);
      ((LinearLayout)localObject1).setBackgroundColor(-1);
      localLinearLayout.addView((View)localObject1, LayoutHelper.createLinear(-1, -2));
      this.descriptionTextView = new EditText(paramContext);
      this.descriptionTextView.setTextSize(1, 16.0F);
      this.descriptionTextView.setHintTextColor(-6842473);
      this.descriptionTextView.setTextColor(-14606047);
      this.descriptionTextView.setPadding(0, 0, 0, AndroidUtilities.dp(6.0F));
      this.descriptionTextView.setBackgroundDrawable(null);
      localObject2 = this.descriptionTextView;
      if (!LocaleController.isRTL) {
        break label1461;
      }
      i = 5;
      label703:
      ((EditText)localObject2).setGravity(i);
      this.descriptionTextView.setInputType(180225);
      this.descriptionTextView.setImeOptions(6);
      localObject2 = new InputFilter.LengthFilter(255);
      this.descriptionTextView.setFilters(new InputFilter[] { localObject2 });
      this.descriptionTextView.setHint(LocaleController.getString("DescriptionOptionalPlaceholder", 2131165582));
      AndroidUtilities.clearCursorDrawable(this.descriptionTextView);
      ((LinearLayout)localObject1).addView(this.descriptionTextView, LayoutHelper.createLinear(-1, -2, 17.0F, 12.0F, 17.0F, 6.0F));
      this.descriptionTextView.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          if ((paramAnonymousInt == 6) && (ChannelEditActivity.this.doneButton != null))
          {
            ChannelEditActivity.this.doneButton.performClick();
            return true;
          }
          return false;
        }
      });
      this.descriptionTextView.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramAnonymousEditable) {}
        
        public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        
        public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      });
      localObject1 = new ShadowSectionCell(paramContext);
      ((ShadowSectionCell)localObject1).setSize(20);
      localLinearLayout.addView((View)localObject1, LayoutHelper.createLinear(-1, -2));
      if ((this.currentChat.megagroup) || (!this.currentChat.megagroup))
      {
        localObject1 = new FrameLayout(paramContext);
        ((FrameLayout)localObject1).setBackgroundColor(-1);
        localLinearLayout.addView((View)localObject1, LayoutHelper.createLinear(-1, -2));
        this.typeCell = new TextSettingsCell(paramContext);
        updateTypeCell();
        this.typeCell.setBackgroundResource(2130837796);
        ((FrameLayout)localObject1).addView(this.typeCell, LayoutHelper.createFrame(-1, -2.0F));
        localObject1 = new View(paramContext);
        ((View)localObject1).setBackgroundColor(-3158065);
        localLinearLayout.addView((View)localObject1, new LinearLayout.LayoutParams(-1, 1));
        localObject1 = new FrameLayout(paramContext);
        ((FrameLayout)localObject1).setBackgroundColor(-1);
        localLinearLayout.addView((View)localObject1, LayoutHelper.createLinear(-1, -2));
        if (this.currentChat.megagroup) {
          break label1467;
        }
        localObject2 = new TextCheckCell(paramContext);
        ((TextCheckCell)localObject2).setBackgroundResource(2130837796);
        ((TextCheckCell)localObject2).setTextAndCheck(LocaleController.getString("ChannelSignMessages", 2131165474), this.signMessages, false);
        ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-1, -2.0F));
        ((TextCheckCell)localObject2).setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            ChannelEditActivity localChannelEditActivity = ChannelEditActivity.this;
            if (!ChannelEditActivity.this.signMessages) {}
            for (boolean bool = true;; bool = false)
            {
              ChannelEditActivity.access$902(localChannelEditActivity, bool);
              ((TextCheckCell)paramAnonymousView).setChecked(ChannelEditActivity.this.signMessages);
              return;
            }
          }
        });
        localObject1 = new TextInfoPrivacyCell(paramContext);
        ((TextInfoPrivacyCell)localObject1).setBackgroundResource(2130837688);
        ((TextInfoPrivacyCell)localObject1).setText(LocaleController.getString("ChannelSignMessagesInfo", 2131165475));
        localLinearLayout.addView((View)localObject1, LayoutHelper.createLinear(-1, -2));
      }
      label1140:
      if (this.currentChat.creator)
      {
        localObject1 = new FrameLayout(paramContext);
        ((FrameLayout)localObject1).setBackgroundColor(-1);
        localLinearLayout.addView((View)localObject1, LayoutHelper.createLinear(-1, -2));
        localObject2 = new TextSettingsCell(paramContext);
        ((TextSettingsCell)localObject2).setTextColor(-1229511);
        ((TextSettingsCell)localObject2).setBackgroundResource(2130837796);
        if (!this.currentChat.megagroup) {
          break label1575;
        }
        ((TextSettingsCell)localObject2).setText(LocaleController.getString("DeleteMega", 2131165574), false);
        label1230:
        ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-1, -2.0F));
        ((TextSettingsCell)localObject2).setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            paramAnonymousView = new AlertDialog.Builder(ChannelEditActivity.this.getParentActivity());
            if (ChannelEditActivity.this.currentChat.megagroup) {
              paramAnonymousView.setMessage(LocaleController.getString("MegaDeleteAlert", 2131165854));
            }
            for (;;)
            {
              paramAnonymousView.setTitle(LocaleController.getString("AppName", 2131165299));
              paramAnonymousView.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                {
                  NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
                  if (AndroidUtilities.isTablet()) {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[] { Long.valueOf(-ChannelEditActivity.this.chatId) });
                  }
                  for (;;)
                  {
                    MessagesController.getInstance().deleteUserFromChat(ChannelEditActivity.this.chatId, MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), ChannelEditActivity.this.info);
                    ChannelEditActivity.this.finishFragment();
                    return;
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                  }
                }
              });
              paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
              ChannelEditActivity.this.showDialog(paramAnonymousView.create());
              return;
              paramAnonymousView.setMessage(LocaleController.getString("ChannelDeleteAlert", 2131165422));
            }
          }
        });
        paramContext = new TextInfoPrivacyCell(paramContext);
        paramContext.setBackgroundResource(2130837689);
        if (!this.currentChat.megagroup) {
          break label1593;
        }
        paramContext.setText(LocaleController.getString("MegaDeleteInfo", 2131165855));
        label1296:
        localLinearLayout.addView(paramContext, LayoutHelper.createLinear(-1, -2));
      }
      this.nameTextView.setText(this.currentChat.title);
      this.nameTextView.setSelection(this.nameTextView.length());
      if (this.info != null) {
        this.descriptionTextView.setText(this.info.about);
      }
      if (this.currentChat.photo == null) {
        break label1609;
      }
      this.avatar = this.currentChat.photo.photo_small;
      this.avatarImage.setImage(this.avatar, "50_50", this.avatarDrawable);
    }
    for (;;)
    {
      return this.fragmentView;
      i = 3;
      break;
      label1410:
      f1 = 16.0F;
      break label268;
      label1417:
      f2 = 0.0F;
      break label278;
      label1422:
      this.nameTextView.setHint(LocaleController.getString("EnterChannelName", 2131165619));
      break label359;
      label1441:
      i = 3;
      break label382;
      label1447:
      f1 = 96.0F;
      break label508;
      label1454:
      f2 = 16.0F;
      break label518;
      label1461:
      i = 3;
      break label703;
      label1467:
      this.adminCell = new TextSettingsCell(paramContext);
      updateAdminCell();
      this.adminCell.setBackgroundResource(2130837796);
      ((FrameLayout)localObject1).addView(this.adminCell, LayoutHelper.createFrame(-1, -2.0F));
      this.adminCell.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = new Bundle();
          paramAnonymousView.putInt("chat_id", ChannelEditActivity.this.chatId);
          paramAnonymousView.putInt("type", 1);
          ChannelEditActivity.this.presentFragment(new ChannelUsersActivity(paramAnonymousView));
        }
      });
      localObject1 = new ShadowSectionCell(paramContext);
      ((ShadowSectionCell)localObject1).setSize(20);
      localLinearLayout.addView((View)localObject1, LayoutHelper.createLinear(-1, -2));
      if (this.currentChat.creator) {
        break label1140;
      }
      ((ShadowSectionCell)localObject1).setBackgroundResource(2130837689);
      break label1140;
      label1575:
      ((TextSettingsCell)localObject2).setText(LocaleController.getString("ChannelDelete", 2131165421), false);
      break label1230;
      label1593:
      paramContext.setText(LocaleController.getString("ChannelDeleteInfo", 2131165423));
      break label1296;
      label1609:
      this.avatarImage.setImageDrawable(this.avatarDrawable);
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.chatInfoDidLoaded)
    {
      paramVarArgs = (TLRPC.ChatFull)paramVarArgs[0];
      if (paramVarArgs.id == this.chatId)
      {
        if (this.info == null) {
          this.descriptionTextView.setText(paramVarArgs.about);
        }
        this.info = paramVarArgs;
        updateAdminCell();
        updateTypeCell();
      }
    }
    while ((paramInt != NotificationCenter.updateInterfaces) || ((((Integer)paramVarArgs[0]).intValue() & 0x2000) == 0)) {
      return;
    }
    updateTypeCell();
  }
  
  public void didUploadedPhoto(final TLRPC.InputFile paramInputFile, final TLRPC.PhotoSize paramPhotoSize1, TLRPC.PhotoSize paramPhotoSize2)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        ChannelEditActivity.access$1002(ChannelEditActivity.this, paramInputFile);
        ChannelEditActivity.access$1102(ChannelEditActivity.this, paramPhotoSize1.location);
        ChannelEditActivity.this.avatarImage.setImage(ChannelEditActivity.this.avatar, "50_50", ChannelEditActivity.this.avatarDrawable);
        if (ChannelEditActivity.this.createAfterUpload) {}
        try
        {
          if ((ChannelEditActivity.this.progressDialog != null) && (ChannelEditActivity.this.progressDialog.isShowing()))
          {
            ChannelEditActivity.this.progressDialog.dismiss();
            ChannelEditActivity.access$602(ChannelEditActivity.this, null);
          }
          ChannelEditActivity.this.doneButton.performClick();
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
  
  public void onActivityResultFragment(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.avatarUpdater.onActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  public boolean onFragmentCreate()
  {
    this.currentChat = MessagesController.getInstance().getChat(Integer.valueOf(this.chatId));
    if (this.currentChat == null)
    {
      final Semaphore localSemaphore = new Semaphore(0);
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          ChannelEditActivity.access$002(ChannelEditActivity.this, MessagesStorage.getInstance().getChat(ChannelEditActivity.this.chatId));
          localSemaphore.release();
        }
      });
      try
      {
        localSemaphore.acquire();
        if (this.currentChat != null)
        {
          MessagesController.getInstance().putChat(this.currentChat, true);
          if (this.info != null) {
            break label128;
          }
          MessagesStorage.getInstance().loadChatInfo(this.chatId, localSemaphore, false, false);
        }
      }
      catch (Exception localException2)
      {
        try
        {
          localSemaphore.acquire();
          if (this.info == null)
          {
            return false;
            localException2 = localException2;
            FileLog.e("tmessages", localException2);
          }
        }
        catch (Exception localException1)
        {
          for (;;)
          {
            FileLog.e("tmessages", localException1);
          }
        }
      }
    }
    label128:
    this.avatarUpdater.parentFragment = this;
    this.avatarUpdater.delegate = this;
    this.signMessages = this.currentChat.signatures;
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    return super.onFragmentCreate();
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    if (this.avatarUpdater != null) {
      this.avatarUpdater.clear();
    }
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
  }
  
  public void onResume()
  {
    super.onResume();
    AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
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
  
  public void setInfo(TLRPC.ChatFull paramChatFull)
  {
    this.info = paramChatFull;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ChannelEditActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
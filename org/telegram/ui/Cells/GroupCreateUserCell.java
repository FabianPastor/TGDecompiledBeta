package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.tgnet.TLRPC.UserStatus;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.GroupCreateCheckBox;
import org.telegram.ui.Components.LayoutHelper;

public class GroupCreateUserCell
  extends FrameLayout
{
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private BackupImageView avatarImageView;
  private GroupCreateCheckBox checkBox;
  private int currentAccount = UserConfig.selectedAccount;
  private CharSequence currentName;
  private CharSequence currentStatus;
  private TLRPC.User currentUser;
  private TLRPC.FileLocation lastAvatar;
  private String lastName;
  private int lastStatus;
  private SimpleTextView nameTextView;
  private SimpleTextView statusTextView;
  
  public GroupCreateUserCell(Context paramContext, boolean paramBoolean)
  {
    super(paramContext);
    this.avatarImageView = new BackupImageView(paramContext);
    this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0F));
    Object localObject = this.avatarImageView;
    int j;
    float f1;
    if (LocaleController.isRTL)
    {
      j = 5;
      if (!LocaleController.isRTL) {
        break label430;
      }
      f1 = 0.0F;
      label73:
      if (!LocaleController.isRTL) {
        break label437;
      }
      f2 = 11.0F;
      label83:
      addView((View)localObject, LayoutHelper.createFrame(50, 50.0F, j | 0x30, f1, 11.0F, f2, 0.0F));
      this.nameTextView = new SimpleTextView(paramContext);
      this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.nameTextView.setTextSize(17);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label443;
      }
      j = 5;
      label168:
      ((SimpleTextView)localObject).setGravity(j | 0x30);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label449;
      }
      j = 5;
      label193:
      if (!LocaleController.isRTL) {
        break label455;
      }
      f1 = 28.0F;
      label203:
      if (!LocaleController.isRTL) {
        break label462;
      }
      f2 = 72.0F;
      label213:
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, j | 0x30, f1, 14.0F, f2, 0.0F));
      this.statusTextView = new SimpleTextView(paramContext);
      this.statusTextView.setTextSize(16);
      localObject = this.statusTextView;
      if (!LocaleController.isRTL) {
        break label469;
      }
      j = 5;
      label273:
      ((SimpleTextView)localObject).setGravity(j | 0x30);
      localObject = this.statusTextView;
      if (!LocaleController.isRTL) {
        break label475;
      }
      j = 5;
      label298:
      if (!LocaleController.isRTL) {
        break label481;
      }
      f1 = 28.0F;
      label308:
      if (!LocaleController.isRTL) {
        break label488;
      }
      f2 = 72.0F;
      label318:
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, j | 0x30, f1, 39.0F, f2, 0.0F));
      if (paramBoolean)
      {
        this.checkBox = new GroupCreateCheckBox(paramContext);
        this.checkBox.setVisibility(0);
        paramContext = this.checkBox;
        if (!LocaleController.isRTL) {
          break label495;
        }
        j = i;
        label380:
        if (!LocaleController.isRTL) {
          break label501;
        }
        f1 = 0.0F;
        label389:
        if (!LocaleController.isRTL) {
          break label508;
        }
      }
    }
    label430:
    label437:
    label443:
    label449:
    label455:
    label462:
    label469:
    label475:
    label481:
    label488:
    label495:
    label501:
    label508:
    for (float f2 = 41.0F;; f2 = 0.0F)
    {
      addView(paramContext, LayoutHelper.createFrame(24, 24.0F, j | 0x30, f1, 41.0F, f2, 0.0F));
      return;
      j = 3;
      break;
      f1 = 11.0F;
      break label73;
      f2 = 0.0F;
      break label83;
      j = 3;
      break label168;
      j = 3;
      break label193;
      f1 = 72.0F;
      break label203;
      f2 = 28.0F;
      break label213;
      j = 3;
      break label273;
      j = 3;
      break label298;
      f1 = 72.0F;
      break label308;
      f2 = 28.0F;
      break label318;
      j = 3;
      break label380;
      f1 = 41.0F;
      break label389;
    }
  }
  
  public TLRPC.User getUser()
  {
    return this.currentUser;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(72.0F), NUM));
  }
  
  public void recycle()
  {
    this.avatarImageView.getImageReceiver().cancelLoadImage();
  }
  
  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.checkBox.setChecked(paramBoolean1, paramBoolean2);
  }
  
  public void setUser(TLRPC.User paramUser, CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    this.currentUser = paramUser;
    this.currentStatus = paramCharSequence2;
    this.currentName = paramCharSequence1;
    update(0);
  }
  
  public void update(int paramInt)
  {
    if (this.currentUser == null) {}
    TLRPC.FileLocation localFileLocation;
    Object localObject1;
    Object localObject2;
    int j;
    label138:
    do
    {
      return;
      localFileLocation = null;
      localObject1 = null;
      localObject2 = null;
      if (this.currentUser.photo != null) {
        localFileLocation = this.currentUser.photo.photo_small;
      }
      if (paramInt == 0) {
        break;
      }
      int i = 0;
      j = i;
      if ((paramInt & 0x2) != 0)
      {
        if ((this.lastAvatar == null) || (localFileLocation != null))
        {
          j = i;
          if (this.lastAvatar != null) {
            break label138;
          }
          j = i;
          if (localFileLocation == null) {
            break label138;
          }
          j = i;
          if (this.lastAvatar == null) {
            break label138;
          }
          j = i;
          if (localFileLocation == null) {
            break label138;
          }
          if (this.lastAvatar.volume_id == localFileLocation.volume_id)
          {
            j = i;
            if (this.lastAvatar.local_id == localFileLocation.local_id) {
              break label138;
            }
          }
        }
        j = 1;
      }
      i = j;
      if (this.currentUser != null)
      {
        i = j;
        if (this.currentStatus == null)
        {
          i = j;
          if (j == 0)
          {
            i = j;
            if ((paramInt & 0x4) != 0)
            {
              int k = 0;
              if (this.currentUser.status != null) {
                k = this.currentUser.status.expires;
              }
              i = j;
              if (k != this.lastStatus) {
                i = 1;
              }
            }
          }
        }
      }
      j = i;
      localObject1 = localObject2;
      if (i == 0)
      {
        j = i;
        localObject1 = localObject2;
        if (this.currentName == null)
        {
          j = i;
          localObject1 = localObject2;
          if (this.lastName != null)
          {
            j = i;
            localObject1 = localObject2;
            if ((paramInt & 0x1) != 0)
            {
              localObject2 = UserObject.getUserName(this.currentUser);
              j = i;
              localObject1 = localObject2;
              if (!((String)localObject2).equals(this.lastName))
              {
                j = 1;
                localObject1 = localObject2;
              }
            }
          }
        }
      }
    } while (j == 0);
    this.avatarDrawable.setInfo(this.currentUser);
    if (this.currentUser.status != null)
    {
      paramInt = this.currentUser.status.expires;
      label344:
      this.lastStatus = paramInt;
      if (this.currentName == null) {
        break label435;
      }
      this.lastName = null;
      this.nameTextView.setText(this.currentName, true);
      label373:
      if (this.currentStatus == null) {
        break label471;
      }
      this.statusTextView.setText(this.currentStatus, true);
      this.statusTextView.setTag("groupcreate_offlineText");
      this.statusTextView.setTextColor(Theme.getColor("groupcreate_offlineText"));
    }
    for (;;)
    {
      this.avatarImageView.setImage(localFileLocation, "50_50", this.avatarDrawable);
      break;
      paramInt = 0;
      break label344;
      label435:
      localObject2 = localObject1;
      if (localObject1 == null) {
        localObject2 = UserObject.getUserName(this.currentUser);
      }
      this.lastName = ((String)localObject2);
      this.nameTextView.setText(this.lastName);
      break label373;
      label471:
      if (this.currentUser.bot)
      {
        this.statusTextView.setTag("groupcreate_offlineText");
        this.statusTextView.setTextColor(Theme.getColor("groupcreate_offlineText"));
        this.statusTextView.setText(LocaleController.getString("Bot", NUM));
      }
      else if ((this.currentUser.id == UserConfig.getInstance(this.currentAccount).getClientUserId()) || ((this.currentUser.status != null) && (this.currentUser.status.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime())) || (MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(this.currentUser.id))))
      {
        this.statusTextView.setTag("groupcreate_offlineText");
        this.statusTextView.setTextColor(Theme.getColor("groupcreate_onlineText"));
        this.statusTextView.setText(LocaleController.getString("Online", NUM));
      }
      else
      {
        this.statusTextView.setTag("groupcreate_offlineText");
        this.statusTextView.setTextColor(Theme.getColor("groupcreate_offlineText"));
        this.statusTextView.setText(LocaleController.formatUserStatus(this.currentAccount, this.currentUser));
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/GroupCreateUserCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
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
import org.telegram.ui.Components.LayoutHelper;

public class ManageChatUserCell
  extends FrameLayout
{
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private BackupImageView avatarImageView;
  private int currentAccount = UserConfig.selectedAccount;
  private CharSequence currentName;
  private TLRPC.User currentUser;
  private CharSequence currrntStatus;
  private ManageChatUserCellDelegate delegate;
  private boolean isAdmin;
  private TLRPC.FileLocation lastAvatar;
  private String lastName;
  private int lastStatus;
  private SimpleTextView nameTextView;
  private ImageView optionsButton;
  private int statusColor = Theme.getColor("windowBackgroundWhiteGrayText");
  private int statusOnlineColor = Theme.getColor("windowBackgroundWhiteBlueText");
  private SimpleTextView statusTextView;
  
  public ManageChatUserCell(Context paramContext, int paramInt, boolean paramBoolean)
  {
    super(paramContext);
    this.avatarImageView = new BackupImageView(paramContext);
    this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0F));
    Object localObject = this.avatarImageView;
    int j;
    float f1;
    label92:
    float f2;
    if (LocaleController.isRTL)
    {
      j = 5;
      if (!LocaleController.isRTL) {
        break label502;
      }
      f1 = 0.0F;
      if (!LocaleController.isRTL) {
        break label512;
      }
      f2 = paramInt + 7;
      label105:
      addView((View)localObject, LayoutHelper.createFrame(48, 48.0F, j | 0x30, f1, 8.0F, f2, 0.0F));
      this.nameTextView = new SimpleTextView(paramContext);
      this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.nameTextView.setTextSize(17);
      this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label518;
      }
      j = 5;
      label190:
      ((SimpleTextView)localObject).setGravity(j | 0x30);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label524;
      }
      j = 5;
      label215:
      if (!LocaleController.isRTL) {
        break label530;
      }
      f1 = 46.0F;
      label225:
      if (!LocaleController.isRTL) {
        break label540;
      }
      f2 = paramInt + 68;
      label238:
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, j | 0x30, f1, 11.5F, f2, 0.0F));
      this.statusTextView = new SimpleTextView(paramContext);
      this.statusTextView.setTextSize(14);
      localObject = this.statusTextView;
      if (!LocaleController.isRTL) {
        break label547;
      }
      j = 5;
      label298:
      ((SimpleTextView)localObject).setGravity(j | 0x30);
      localObject = this.statusTextView;
      if (!LocaleController.isRTL) {
        break label553;
      }
      j = 5;
      label323:
      if (!LocaleController.isRTL) {
        break label559;
      }
      f1 = 28.0F;
      label333:
      if (!LocaleController.isRTL) {
        break label569;
      }
      f2 = paramInt + 68;
      label346:
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, j | 0x30, f1, 34.5F, f2, 0.0F));
      if (paramBoolean)
      {
        this.optionsButton = new ImageView(paramContext);
        this.optionsButton.setFocusable(false);
        this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
        this.optionsButton.setImageResource(NUM);
        this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), PorterDuff.Mode.MULTIPLY));
        this.optionsButton.setScaleType(ImageView.ScaleType.CENTER);
        paramContext = this.optionsButton;
        if (!LocaleController.isRTL) {
          break label576;
        }
      }
    }
    label502:
    label512:
    label518:
    label524:
    label530:
    label540:
    label547:
    label553:
    label559:
    label569:
    label576:
    for (paramInt = i;; paramInt = 5)
    {
      addView(paramContext, LayoutHelper.createFrame(48, 64, paramInt | 0x30));
      this.optionsButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          ManageChatUserCell.this.delegate.onOptionsButtonCheck(ManageChatUserCell.this, true);
        }
      });
      return;
      j = 3;
      break;
      f1 = paramInt + 7;
      break label92;
      f2 = 0.0F;
      break label105;
      j = 3;
      break label190;
      j = 3;
      break label215;
      f1 = paramInt + 68;
      break label225;
      f2 = 46.0F;
      break label238;
      j = 3;
      break label298;
      j = 3;
      break label323;
      f1 = paramInt + 68;
      break label333;
      f2 = 28.0F;
      break label346;
    }
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0F), NUM));
  }
  
  public void recycle()
  {
    this.avatarImageView.getImageReceiver().cancelLoadImage();
  }
  
  public void setData(TLRPC.User paramUser, CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    if (paramUser == null)
    {
      this.currrntStatus = null;
      this.currentName = null;
      this.currentUser = null;
      this.nameTextView.setText("");
      this.statusTextView.setText("");
      this.avatarImageView.setImageDrawable(null);
      return;
    }
    this.currrntStatus = paramCharSequence2;
    this.currentName = paramCharSequence1;
    this.currentUser = paramUser;
    if (this.optionsButton != null)
    {
      paramUser = this.optionsButton;
      if (!this.delegate.onOptionsButtonCheck(this, false)) {
        break label104;
      }
    }
    label104:
    for (int i = 0;; i = 4)
    {
      paramUser.setVisibility(i);
      update(0);
      break;
    }
  }
  
  public void setDelegate(ManageChatUserCellDelegate paramManageChatUserCellDelegate)
  {
    this.delegate = paramManageChatUserCellDelegate;
  }
  
  public void setIsAdmin(boolean paramBoolean)
  {
    this.isAdmin = paramBoolean;
  }
  
  public void setStatusColors(int paramInt1, int paramInt2)
  {
    this.statusColor = paramInt1;
    this.statusOnlineColor = paramInt2;
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
      this.lastStatus = this.currentUser.status.expires;
      label336:
      if (this.currentName == null) {
        break label414;
      }
      this.lastName = null;
      this.nameTextView.setText(this.currentName);
      label359:
      if (this.currrntStatus == null) {
        break label450;
      }
      this.statusTextView.setTextColor(this.statusColor);
      this.statusTextView.setText(this.currrntStatus);
    }
    for (;;)
    {
      this.avatarImageView.setImage(localFileLocation, "50_50", this.avatarDrawable);
      break;
      this.lastStatus = 0;
      break label336;
      label414:
      localObject2 = localObject1;
      if (localObject1 == null) {
        localObject2 = UserObject.getUserName(this.currentUser);
      }
      this.lastName = ((String)localObject2);
      this.nameTextView.setText(this.lastName);
      break label359;
      label450:
      if (this.currentUser != null) {
        if (this.currentUser.bot)
        {
          this.statusTextView.setTextColor(this.statusColor);
          if ((this.currentUser.bot_chat_history) || (this.isAdmin)) {
            this.statusTextView.setText(LocaleController.getString("BotStatusRead", NUM));
          } else {
            this.statusTextView.setText(LocaleController.getString("BotStatusCantRead", NUM));
          }
        }
        else if ((this.currentUser.id == UserConfig.getInstance(this.currentAccount).getClientUserId()) || ((this.currentUser.status != null) && (this.currentUser.status.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime())) || (MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(this.currentUser.id))))
        {
          this.statusTextView.setTextColor(this.statusOnlineColor);
          this.statusTextView.setText(LocaleController.getString("Online", NUM));
        }
        else
        {
          this.statusTextView.setTextColor(this.statusColor);
          this.statusTextView.setText(LocaleController.formatUserStatus(this.currentAccount, this.currentUser));
        }
      }
    }
  }
  
  public static abstract interface ManageChatUserCellDelegate
  {
    public abstract boolean onOptionsButtonCheck(ManageChatUserCell paramManageChatUserCell, boolean paramBoolean);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/ManageChatUserCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
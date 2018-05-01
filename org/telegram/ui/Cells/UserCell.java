package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.tgnet.TLRPC.UserStatus;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;

public class UserCell
  extends FrameLayout
{
  private ImageView adminImage;
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private BackupImageView avatarImageView;
  private CheckBox checkBox;
  private CheckBoxSquare checkBoxBig;
  private int currentAccount = UserConfig.selectedAccount;
  private int currentDrawable;
  private CharSequence currentName;
  private TLObject currentObject;
  private CharSequence currrntStatus;
  private ImageView imageView;
  private TLRPC.FileLocation lastAvatar;
  private String lastName;
  private int lastStatus;
  private SimpleTextView nameTextView;
  private int statusColor = Theme.getColor("windowBackgroundWhiteGrayText");
  private int statusOnlineColor = Theme.getColor("windowBackgroundWhiteBlueText");
  private SimpleTextView statusTextView;
  
  public UserCell(Context paramContext, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    super(paramContext);
    this.avatarImageView = new BackupImageView(paramContext);
    this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0F));
    Object localObject = this.avatarImageView;
    int i;
    float f1;
    label89:
    label102:
    label175:
    label200:
    int j;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL) {
        break label619;
      }
      f1 = 0.0F;
      if (!LocaleController.isRTL) {
        break label629;
      }
      f2 = paramInt1 + 7;
      addView((View)localObject, LayoutHelper.createFrame(48, 48.0F, i | 0x30, f1, 8.0F, f2, 0.0F));
      this.nameTextView = new SimpleTextView(paramContext);
      this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.nameTextView.setTextSize(17);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label635;
      }
      i = 5;
      ((SimpleTextView)localObject).setGravity(i | 0x30);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label641;
      }
      i = 5;
      if (!LocaleController.isRTL) {
        break label653;
      }
      if (paramInt2 != 2) {
        break label647;
      }
      j = 18;
      label215:
      f1 = j + 28;
      label223:
      if (!LocaleController.isRTL) {
        break label663;
      }
      f2 = paramInt1 + 68;
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 11.5F, f2, 0.0F));
      this.statusTextView = new SimpleTextView(paramContext);
      this.statusTextView.setTextSize(14);
      localObject = this.statusTextView;
      if (!LocaleController.isRTL) {
        break label689;
      }
      i = 5;
      label296:
      ((SimpleTextView)localObject).setGravity(i | 0x30);
      localObject = this.statusTextView;
      if (!LocaleController.isRTL) {
        break label695;
      }
      i = 5;
      label321:
      if (!LocaleController.isRTL) {
        break label701;
      }
      f1 = 28.0F;
      label331:
      if (!LocaleController.isRTL) {
        break label711;
      }
      f2 = paramInt1 + 68;
      label344:
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 34.5F, f2, 0.0F));
      this.imageView = new ImageView(paramContext);
      this.imageView.setScaleType(ImageView.ScaleType.CENTER);
      this.imageView.setVisibility(8);
      localObject = this.imageView;
      if (!LocaleController.isRTL) {
        break label718;
      }
      i = 5;
      label414:
      if (!LocaleController.isRTL) {
        break label724;
      }
      f1 = 0.0F;
      label423:
      if (!LocaleController.isRTL) {
        break label731;
      }
      f2 = 16.0F;
      label433:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i | 0x10, f1, 0.0F, f2, 0.0F));
      if (paramInt2 != 2) {
        break label755;
      }
      this.checkBoxBig = new CheckBoxSquare(paramContext, false);
      localObject = this.checkBoxBig;
      if (!LocaleController.isRTL) {
        break label737;
      }
      paramInt1 = 3;
      label489:
      if (!LocaleController.isRTL) {
        break label742;
      }
      f1 = 19.0F;
      label499:
      if (!LocaleController.isRTL) {
        break label748;
      }
      f2 = 0.0F;
      label508:
      addView((View)localObject, LayoutHelper.createFrame(18, 18.0F, paramInt1 | 0x10, f1, 0.0F, f2, 0.0F));
      label531:
      if (paramBoolean)
      {
        this.adminImage = new ImageView(paramContext);
        this.adminImage.setImageResource(NUM);
        paramContext = this.adminImage;
        if (!LocaleController.isRTL) {
          break label883;
        }
        paramInt1 = 3;
        label570:
        if (!LocaleController.isRTL) {
          break label888;
        }
        f1 = 24.0F;
        label580:
        if (!LocaleController.isRTL) {
          break label894;
        }
      }
    }
    label619:
    label629:
    label635:
    label641:
    label647:
    label653:
    label663:
    label689:
    label695:
    label701:
    label711:
    label718:
    label724:
    label731:
    label737:
    label742:
    label748:
    label755:
    label813:
    label822:
    label867:
    label877:
    label883:
    label888:
    label894:
    for (float f2 = 0.0F;; f2 = 24.0F)
    {
      addView(paramContext, LayoutHelper.createFrame(16, 16.0F, paramInt1 | 0x30, f1, 13.5F, f2, 0.0F));
      return;
      i = 3;
      break;
      f1 = paramInt1 + 7;
      break label89;
      f2 = 0.0F;
      break label102;
      i = 3;
      break label175;
      i = 3;
      break label200;
      j = 0;
      break label215;
      f1 = paramInt1 + 68;
      break label223;
      if (paramInt2 == 2) {}
      for (j = 18;; j = 0)
      {
        f2 = j + 28;
        break;
      }
      i = 3;
      break label296;
      i = 3;
      break label321;
      f1 = paramInt1 + 68;
      break label331;
      f2 = 28.0F;
      break label344;
      i = 3;
      break label414;
      f1 = 16.0F;
      break label423;
      f2 = 0.0F;
      break label433;
      paramInt1 = 5;
      break label489;
      f1 = 0.0F;
      break label499;
      f2 = 19.0F;
      break label508;
      if (paramInt2 != 1) {
        break label531;
      }
      this.checkBox = new CheckBox(paramContext, NUM);
      this.checkBox.setVisibility(4);
      this.checkBox.setColor(Theme.getColor("checkbox"), Theme.getColor("checkboxCheck"));
      localObject = this.checkBox;
      if (LocaleController.isRTL)
      {
        paramInt2 = 5;
        if (!LocaleController.isRTL) {
          break label867;
        }
        f1 = 0.0F;
        if (!LocaleController.isRTL) {
          break label877;
        }
      }
      for (f2 = paramInt1 + 37;; f2 = 0.0F)
      {
        addView((View)localObject, LayoutHelper.createFrame(22, 22.0F, paramInt2 | 0x30, f1, 38.0F, f2, 0.0F));
        break;
        paramInt2 = 3;
        break label813;
        f1 = paramInt1 + 37;
        break label822;
      }
      paramInt1 = 5;
      break label570;
      f1 = 0.0F;
      break label580;
    }
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  public void invalidate()
  {
    super.invalidate();
    if (this.checkBoxBig != null) {
      this.checkBoxBig.invalidate();
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0F), NUM));
  }
  
  public void setCheckDisabled(boolean paramBoolean)
  {
    if (this.checkBoxBig != null) {
      this.checkBoxBig.setDisabled(paramBoolean);
    }
  }
  
  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.checkBox != null)
    {
      if (this.checkBox.getVisibility() != 0) {
        this.checkBox.setVisibility(0);
      }
      this.checkBox.setChecked(paramBoolean1, paramBoolean2);
    }
    for (;;)
    {
      return;
      if (this.checkBoxBig != null)
      {
        if (this.checkBoxBig.getVisibility() != 0) {
          this.checkBoxBig.setVisibility(0);
        }
        this.checkBoxBig.setChecked(paramBoolean1, paramBoolean2);
      }
    }
  }
  
  public void setData(TLObject paramTLObject, CharSequence paramCharSequence1, CharSequence paramCharSequence2, int paramInt)
  {
    if (paramTLObject == null)
    {
      this.currrntStatus = null;
      this.currentName = null;
      this.currentObject = null;
      this.nameTextView.setText("");
      this.statusTextView.setText("");
      this.avatarImageView.setImageDrawable(null);
    }
    for (;;)
    {
      return;
      this.currrntStatus = paramCharSequence2;
      this.currentName = paramCharSequence1;
      this.currentObject = paramTLObject;
      this.currentDrawable = paramInt;
      update(0);
    }
  }
  
  public void setIsAdmin(int paramInt)
  {
    if (this.adminImage == null) {}
    for (;;)
    {
      return;
      Object localObject = this.adminImage;
      int i;
      if (paramInt != 0)
      {
        i = 0;
        label19:
        ((ImageView)localObject).setVisibility(i);
        localObject = this.nameTextView;
        if ((!LocaleController.isRTL) || (paramInt == 0)) {
          break label113;
        }
        i = AndroidUtilities.dp(16.0F);
        label45:
        if ((LocaleController.isRTL) || (paramInt == 0)) {
          break label118;
        }
      }
      label113:
      label118:
      for (int j = AndroidUtilities.dp(16.0F);; j = 0)
      {
        ((SimpleTextView)localObject).setPadding(i, 0, j, 0);
        if (paramInt != 1) {
          break label124;
        }
        setTag("profile_creatorIcon");
        this.adminImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("profile_creatorIcon"), PorterDuff.Mode.MULTIPLY));
        break;
        i = 8;
        break label19;
        i = 0;
        break label45;
      }
      label124:
      if (paramInt == 2)
      {
        setTag("profile_adminIcon");
        this.adminImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("profile_adminIcon"), PorterDuff.Mode.MULTIPLY));
      }
    }
  }
  
  public void setStatusColors(int paramInt1, int paramInt2)
  {
    this.statusColor = paramInt1;
    this.statusOnlineColor = paramInt2;
  }
  
  public void update(int paramInt)
  {
    int i = 8;
    if (this.currentObject == null) {}
    TLRPC.FileLocation localFileLocation;
    Object localObject1;
    Object localObject2;
    TLRPC.User localUser1;
    TLRPC.Chat localChat1;
    TLRPC.Chat localChat2;
    TLRPC.User localUser2;
    label77:
    int k;
    label179:
    label313:
    do
    {
      return;
      localFileLocation = null;
      localObject1 = null;
      localObject2 = null;
      localUser1 = null;
      localChat1 = null;
      if (!(this.currentObject instanceof TLRPC.User)) {
        break label517;
      }
      localUser1 = (TLRPC.User)this.currentObject;
      localChat2 = localChat1;
      localUser2 = localUser1;
      if (localUser1.photo != null)
      {
        localFileLocation = localUser1.photo.photo_small;
        localUser2 = localUser1;
        localChat2 = localChat1;
      }
      if (paramInt == 0) {
        break;
      }
      int j = 0;
      k = j;
      if ((paramInt & 0x2) != 0)
      {
        if ((this.lastAvatar == null) || (localFileLocation != null))
        {
          k = j;
          if (this.lastAvatar != null) {
            break label179;
          }
          k = j;
          if (localFileLocation == null) {
            break label179;
          }
          k = j;
          if (this.lastAvatar == null) {
            break label179;
          }
          k = j;
          if (localFileLocation == null) {
            break label179;
          }
          if (this.lastAvatar.volume_id == localFileLocation.volume_id)
          {
            k = j;
            if (this.lastAvatar.local_id == localFileLocation.local_id) {
              break label179;
            }
          }
        }
        k = 1;
      }
      j = k;
      if (localUser2 != null)
      {
        j = k;
        if (k == 0)
        {
          j = k;
          if ((paramInt & 0x4) != 0)
          {
            int m = 0;
            if (localUser2.status != null) {
              m = localUser2.status.expires;
            }
            j = k;
            if (m != this.lastStatus) {
              j = 1;
            }
          }
        }
      }
      k = j;
      localObject1 = localObject2;
      if (j == 0)
      {
        k = j;
        localObject1 = localObject2;
        if (this.currentName == null)
        {
          k = j;
          localObject1 = localObject2;
          if (this.lastName != null)
          {
            k = j;
            localObject1 = localObject2;
            if ((paramInt & 0x1) != 0)
            {
              if (localUser2 == null) {
                break label562;
              }
              localObject2 = UserObject.getUserName(localUser2);
              k = j;
              localObject1 = localObject2;
              if (!((String)localObject2).equals(this.lastName))
              {
                k = 1;
                localObject1 = localObject2;
              }
            }
          }
        }
      }
    } while (k == 0);
    if (localUser2 != null)
    {
      this.avatarDrawable.setInfo(localUser2);
      if (localUser2.status != null)
      {
        this.lastStatus = localUser2.status.expires;
        label379:
        if (this.currentName == null) {
          break label592;
        }
        this.lastName = null;
        this.nameTextView.setText(this.currentName);
        if (this.currrntStatus == null) {
          break label658;
        }
        this.statusTextView.setTextColor(this.statusColor);
        this.statusTextView.setText(this.currrntStatus);
        label431:
        if (((this.imageView.getVisibility() == 0) && (this.currentDrawable == 0)) || ((this.imageView.getVisibility() == 8) && (this.currentDrawable != 0)))
        {
          localObject1 = this.imageView;
          if (this.currentDrawable != 0) {
            break label876;
          }
        }
      }
    }
    label517:
    label562:
    label592:
    label658:
    label876:
    for (paramInt = i;; paramInt = 0)
    {
      ((ImageView)localObject1).setVisibility(paramInt);
      this.imageView.setImageResource(this.currentDrawable);
      this.avatarImageView.setImage(localFileLocation, "50_50", this.avatarDrawable);
      break;
      localChat1 = (TLRPC.Chat)this.currentObject;
      localChat2 = localChat1;
      localUser2 = localUser1;
      if (localChat1.photo == null) {
        break label77;
      }
      localFileLocation = localChat1.photo.photo_small;
      localChat2 = localChat1;
      localUser2 = localUser1;
      break label77;
      localObject2 = localChat2.title;
      break label313;
      this.lastStatus = 0;
      break label379;
      this.avatarDrawable.setInfo(localChat2);
      break label379;
      if (localUser2 != null)
      {
        localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = UserObject.getUserName(localUser2);
        }
      }
      for (this.lastName = ((String)localObject2);; this.lastName = ((String)localObject2))
      {
        this.nameTextView.setText(this.lastName);
        break;
        localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = localChat2.title;
        }
      }
      if (localUser2 == null) {
        break label431;
      }
      if (localUser2.bot)
      {
        this.statusTextView.setTextColor(this.statusColor);
        if ((localUser2.bot_chat_history) || ((this.adminImage != null) && (this.adminImage.getVisibility() == 0)))
        {
          this.statusTextView.setText(LocaleController.getString("BotStatusRead", NUM));
          break label431;
        }
        this.statusTextView.setText(LocaleController.getString("BotStatusCantRead", NUM));
        break label431;
      }
      if ((localUser2.id == UserConfig.getInstance(this.currentAccount).getClientUserId()) || ((localUser2.status != null) && (localUser2.status.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime())) || (MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(localUser2.id))))
      {
        this.statusTextView.setTextColor(this.statusOnlineColor);
        this.statusTextView.setText(LocaleController.getString("Online", NUM));
        break label431;
      }
      this.statusTextView.setTextColor(this.statusColor);
      this.statusTextView.setText(LocaleController.formatUserStatus(this.currentAccount, localUser2));
      break label431;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/UserCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
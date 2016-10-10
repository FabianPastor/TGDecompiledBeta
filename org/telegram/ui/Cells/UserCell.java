package org.telegram.ui.Cells;

import android.content.Context;
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
  private int currentDrawable;
  private CharSequence currentName;
  private TLObject currentObject;
  private CharSequence currrntStatus;
  private ImageView imageView;
  private TLRPC.FileLocation lastAvatar;
  private String lastName;
  private int lastStatus;
  private SimpleTextView nameTextView;
  private int statusColor = -5723992;
  private int statusOnlineColor = -12876608;
  private SimpleTextView statusTextView;
  
  public UserCell(Context paramContext, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    super(paramContext);
    this.avatarImageView = new BackupImageView(paramContext);
    this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0F));
    Object localObject = this.avatarImageView;
    int i;
    float f1;
    label76:
    label89:
    label159:
    label184:
    int j;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL) {
        break label593;
      }
      f1 = 0.0F;
      if (!LocaleController.isRTL) {
        break label603;
      }
      f2 = paramInt1 + 7;
      addView((View)localObject, LayoutHelper.createFrame(48, 48.0F, i | 0x30, f1, 8.0F, f2, 0.0F));
      this.nameTextView = new SimpleTextView(paramContext);
      this.nameTextView.setTextColor(-14606047);
      this.nameTextView.setTextSize(17);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label609;
      }
      i = 5;
      ((SimpleTextView)localObject).setGravity(i | 0x30);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label615;
      }
      i = 5;
      if (!LocaleController.isRTL) {
        break label627;
      }
      if (paramInt2 != 2) {
        break label621;
      }
      j = 18;
      label199:
      f1 = j + 28;
      label207:
      if (!LocaleController.isRTL) {
        break label637;
      }
      f2 = paramInt1 + 68;
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 11.5F, f2, 0.0F));
      this.statusTextView = new SimpleTextView(paramContext);
      this.statusTextView.setTextSize(14);
      localObject = this.statusTextView;
      if (!LocaleController.isRTL) {
        break label663;
      }
      i = 5;
      label280:
      ((SimpleTextView)localObject).setGravity(i | 0x30);
      localObject = this.statusTextView;
      if (!LocaleController.isRTL) {
        break label669;
      }
      i = 5;
      label305:
      if (!LocaleController.isRTL) {
        break label675;
      }
      f1 = 28.0F;
      label315:
      if (!LocaleController.isRTL) {
        break label685;
      }
      f2 = paramInt1 + 68;
      label328:
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 34.5F, f2, 0.0F));
      this.imageView = new ImageView(paramContext);
      this.imageView.setScaleType(ImageView.ScaleType.CENTER);
      this.imageView.setVisibility(8);
      localObject = this.imageView;
      if (!LocaleController.isRTL) {
        break label692;
      }
      i = 5;
      label398:
      if (!LocaleController.isRTL) {
        break label698;
      }
      f1 = 0.0F;
      label407:
      if (!LocaleController.isRTL) {
        break label705;
      }
      f2 = 16.0F;
      label417:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i | 0x10, f1, 0.0F, f2, 0.0F));
      if (paramInt2 != 2) {
        break label729;
      }
      this.checkBoxBig = new CheckBoxSquare(paramContext);
      localObject = this.checkBoxBig;
      if (!LocaleController.isRTL) {
        break label711;
      }
      paramInt1 = 3;
      label472:
      if (!LocaleController.isRTL) {
        break label716;
      }
      f1 = 19.0F;
      label482:
      if (!LocaleController.isRTL) {
        break label722;
      }
      f2 = 0.0F;
      label491:
      addView((View)localObject, LayoutHelper.createFrame(18, 18.0F, paramInt1 | 0x10, f1, 0.0F, f2, 0.0F));
      label514:
      if (paramBoolean)
      {
        this.adminImage = new ImageView(paramContext);
        paramContext = this.adminImage;
        if (!LocaleController.isRTL) {
          break label840;
        }
        paramInt1 = 3;
        label544:
        if (!LocaleController.isRTL) {
          break label845;
        }
        f1 = 24.0F;
        label554:
        if (!LocaleController.isRTL) {
          break label851;
        }
      }
    }
    label593:
    label603:
    label609:
    label615:
    label621:
    label627:
    label637:
    label663:
    label669:
    label675:
    label685:
    label692:
    label698:
    label705:
    label711:
    label716:
    label722:
    label729:
    label770:
    label779:
    label824:
    label834:
    label840:
    label845:
    label851:
    for (float f2 = 0.0F;; f2 = 24.0F)
    {
      addView(paramContext, LayoutHelper.createFrame(16, 16.0F, paramInt1 | 0x30, f1, 13.5F, f2, 0.0F));
      return;
      i = 3;
      break;
      f1 = paramInt1 + 7;
      break label76;
      f2 = 0.0F;
      break label89;
      i = 3;
      break label159;
      i = 3;
      break label184;
      j = 0;
      break label199;
      f1 = paramInt1 + 68;
      break label207;
      if (paramInt2 == 2) {}
      for (j = 18;; j = 0)
      {
        f2 = j + 28;
        break;
      }
      i = 3;
      break label280;
      i = 3;
      break label305;
      f1 = paramInt1 + 68;
      break label315;
      f2 = 28.0F;
      break label328;
      i = 3;
      break label398;
      f1 = 16.0F;
      break label407;
      f2 = 0.0F;
      break label417;
      paramInt1 = 5;
      break label472;
      f1 = 0.0F;
      break label482;
      f2 = 19.0F;
      break label491;
      if (paramInt2 != 1) {
        break label514;
      }
      this.checkBox = new CheckBox(paramContext, 2130837959);
      this.checkBox.setVisibility(4);
      localObject = this.checkBox;
      if (LocaleController.isRTL)
      {
        paramInt2 = 5;
        if (!LocaleController.isRTL) {
          break label824;
        }
        f1 = 0.0F;
        if (!LocaleController.isRTL) {
          break label834;
        }
      }
      for (f2 = paramInt1 + 37;; f2 = 0.0F)
      {
        addView((View)localObject, LayoutHelper.createFrame(22, 22.0F, paramInt2 | 0x30, f1, 38.0F, f2, 0.0F));
        break;
        paramInt2 = 3;
        break label770;
        f1 = paramInt1 + 37;
        break label779;
      }
      paramInt1 = 5;
      break label544;
      f1 = 0.0F;
      break label554;
    }
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0F), 1073741824));
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
    while (this.checkBoxBig == null) {
      return;
    }
    if (this.checkBoxBig.getVisibility() != 0) {
      this.checkBoxBig.setVisibility(0);
    }
    this.checkBoxBig.setChecked(paramBoolean1, paramBoolean2);
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
      return;
    }
    this.currrntStatus = paramCharSequence2;
    this.currentName = paramCharSequence1;
    this.currentObject = paramTLObject;
    this.currentDrawable = paramInt;
    update(0);
  }
  
  public void setIsAdmin(int paramInt)
  {
    if (this.adminImage == null) {}
    label48:
    label94:
    label99:
    label104:
    do
    {
      return;
      Object localObject = this.adminImage;
      int i;
      if (paramInt != 0)
      {
        i = 0;
        ((ImageView)localObject).setVisibility(i);
        localObject = this.nameTextView;
        if ((!LocaleController.isRTL) || (paramInt == 0)) {
          break label94;
        }
        i = AndroidUtilities.dp(16.0F);
        if ((LocaleController.isRTL) || (paramInt == 0)) {
          break label99;
        }
      }
      for (int j = AndroidUtilities.dp(16.0F);; j = 0)
      {
        ((SimpleTextView)localObject).setPadding(i, 0, j, 0);
        if (paramInt != 1) {
          break label104;
        }
        this.adminImage.setImageResource(2130837511);
        return;
        i = 8;
        break;
        i = 0;
        break label48;
      }
    } while (paramInt != 2);
    this.adminImage.setImageResource(2130837512);
  }
  
  public void setStatusColors(int paramInt1, int paramInt2)
  {
    this.statusColor = paramInt1;
    this.statusOnlineColor = paramInt2;
  }
  
  public void update(int paramInt)
  {
    int m = 8;
    if (this.currentObject == null) {}
    TLRPC.FileLocation localFileLocation;
    Object localObject1;
    Object localObject2;
    TLRPC.User localUser2;
    TLRPC.Chat localChat2;
    TLRPC.Chat localChat1;
    TLRPC.User localUser1;
    int i;
    label173:
    label288:
    do
    {
      return;
      localFileLocation = null;
      localObject1 = null;
      localObject2 = null;
      localUser2 = null;
      localChat2 = null;
      if (!(this.currentObject instanceof TLRPC.User)) {
        break label488;
      }
      localUser2 = (TLRPC.User)this.currentObject;
      localChat1 = localChat2;
      localUser1 = localUser2;
      if (localUser2.photo != null)
      {
        localFileLocation = localUser2.photo.photo_small;
        localUser1 = localUser2;
        localChat1 = localChat2;
      }
      if (paramInt == 0) {
        break;
      }
      int j = 0;
      i = j;
      if ((paramInt & 0x2) != 0)
      {
        if ((this.lastAvatar == null) || (localFileLocation != null))
        {
          i = j;
          if (this.lastAvatar != null) {
            break label173;
          }
          i = j;
          if (localFileLocation == null) {
            break label173;
          }
          i = j;
          if (this.lastAvatar == null) {
            break label173;
          }
          i = j;
          if (localFileLocation == null) {
            break label173;
          }
          if (this.lastAvatar.volume_id == localFileLocation.volume_id)
          {
            i = j;
            if (this.lastAvatar.local_id == localFileLocation.local_id) {
              break label173;
            }
          }
        }
        i = 1;
      }
      j = i;
      if (localUser1 != null)
      {
        j = i;
        if (i == 0)
        {
          j = i;
          if ((paramInt & 0x4) != 0)
          {
            int k = 0;
            if (localUser1.status != null) {
              k = localUser1.status.expires;
            }
            j = i;
            if (k != this.lastStatus) {
              j = 1;
            }
          }
        }
      }
      i = j;
      localObject1 = localObject2;
      if (j == 0)
      {
        i = j;
        localObject1 = localObject2;
        if (this.currentName == null)
        {
          i = j;
          localObject1 = localObject2;
          if (this.lastName != null)
          {
            i = j;
            localObject1 = localObject2;
            if ((paramInt & 0x1) != 0)
            {
              if (localUser1 == null) {
                break label534;
              }
              localObject2 = UserObject.getUserName(localUser1);
              i = j;
              localObject1 = localObject2;
              if (!((String)localObject2).equals(this.lastName))
              {
                i = 1;
                localObject1 = localObject2;
              }
            }
          }
        }
      }
    } while (i == 0);
    if (localUser1 != null)
    {
      this.avatarDrawable.setInfo(localUser1);
      if (localUser1.status != null)
      {
        this.lastStatus = localUser1.status.expires;
        label350:
        if (this.currentName == null) {
          break label564;
        }
        this.lastName = null;
        this.nameTextView.setText(this.currentName);
        if (this.currrntStatus == null) {
          break label630;
        }
        this.statusTextView.setTextColor(this.statusColor);
        this.statusTextView.setText(this.currrntStatus);
        label402:
        if (((this.imageView.getVisibility() == 0) && (this.currentDrawable == 0)) || ((this.imageView.getVisibility() == 8) && (this.currentDrawable != 0)))
        {
          localObject1 = this.imageView;
          if (this.currentDrawable != 0) {
            break label829;
          }
        }
      }
    }
    label488:
    label534:
    label564:
    label630:
    label829:
    for (paramInt = m;; paramInt = 0)
    {
      ((ImageView)localObject1).setVisibility(paramInt);
      this.imageView.setImageResource(this.currentDrawable);
      this.avatarImageView.setImage(localFileLocation, "50_50", this.avatarDrawable);
      return;
      localChat2 = (TLRPC.Chat)this.currentObject;
      localChat1 = localChat2;
      localUser1 = localUser2;
      if (localChat2.photo == null) {
        break;
      }
      localFileLocation = localChat2.photo.photo_small;
      localChat1 = localChat2;
      localUser1 = localUser2;
      break;
      localObject2 = localChat1.title;
      break label288;
      this.lastStatus = 0;
      break label350;
      this.avatarDrawable.setInfo(localChat1);
      break label350;
      if (localUser1 != null)
      {
        localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = UserObject.getUserName(localUser1);
        }
      }
      for (this.lastName = ((String)localObject2);; this.lastName = ((String)localObject2))
      {
        this.nameTextView.setText(this.lastName);
        break;
        localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = localChat1.title;
        }
      }
      if (localUser1 == null) {
        break label402;
      }
      if (localUser1.bot)
      {
        this.statusTextView.setTextColor(this.statusColor);
        if ((localUser1.bot_chat_history) || ((this.adminImage != null) && (this.adminImage.getVisibility() == 0)))
        {
          this.statusTextView.setText(LocaleController.getString("BotStatusRead", 2131165376));
          break label402;
        }
        this.statusTextView.setText(LocaleController.getString("BotStatusCantRead", 2131165375));
        break label402;
      }
      if ((localUser1.id == UserConfig.getClientUserId()) || ((localUser1.status != null) && (localUser1.status.expires > ConnectionsManager.getInstance().getCurrentTime())) || (MessagesController.getInstance().onlinePrivacy.containsKey(Integer.valueOf(localUser1.id))))
      {
        this.statusTextView.setTextColor(this.statusOnlineColor);
        this.statusTextView.setText(LocaleController.getString("Online", 2131166046));
        break label402;
      }
      this.statusTextView.setTextColor(this.statusColor);
      this.statusTextView.setText(LocaleController.formatUserStatus(localUser1));
      break label402;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/UserCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
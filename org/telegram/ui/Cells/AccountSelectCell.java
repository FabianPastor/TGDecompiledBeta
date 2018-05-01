package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class AccountSelectCell
  extends FrameLayout
{
  private int accountNumber;
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private ImageView checkImageView;
  private BackupImageView imageView;
  private TextView textView;
  
  public AccountSelectCell(Context paramContext)
  {
    super(paramContext);
    this.avatarDrawable.setTextSize(AndroidUtilities.dp(12.0F));
    this.imageView = new BackupImageView(paramContext);
    this.imageView.setRoundRadius(AndroidUtilities.dp(18.0F));
    addView(this.imageView, LayoutHelper.createFrame(36, 36.0F, 51, 10.0F, 10.0F, 0.0F, 0.0F));
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
    this.textView.setTextSize(1, 15.0F);
    this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    this.textView.setGravity(19);
    this.textView.setEllipsize(TextUtils.TruncateAt.END);
    addView(this.textView, LayoutHelper.createFrame(-1, -1.0F, 51, 61.0F, 0.0F, 56.0F, 0.0F));
    this.checkImageView = new ImageView(paramContext);
    this.checkImageView.setImageResource(NUM);
    this.checkImageView.setScaleType(ImageView.ScaleType.CENTER);
    this.checkImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_menuItemCheck"), PorterDuff.Mode.MULTIPLY));
    addView(this.checkImageView, LayoutHelper.createFrame(40, -1.0F, 53, 0.0F, 0.0F, 6.0F, 0.0F));
  }
  
  public int getAccountNumber()
  {
    return this.accountNumber;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.textView.setTextColor(Theme.getColor("chats_menuItemText"));
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0F), NUM));
  }
  
  public void setAccount(int paramInt)
  {
    this.accountNumber = paramInt;
    Object localObject = UserConfig.getInstance(this.accountNumber).getCurrentUser();
    this.avatarDrawable.setInfo((TLRPC.User)localObject);
    this.textView.setText(ContactsController.formatName(((TLRPC.User)localObject).first_name, ((TLRPC.User)localObject).last_name));
    if ((((TLRPC.User)localObject).photo != null) && (((TLRPC.User)localObject).photo.photo_small != null) && (((TLRPC.User)localObject).photo.photo_small.volume_id != 0L) && (((TLRPC.User)localObject).photo.photo_small.local_id != 0))
    {
      localObject = ((TLRPC.User)localObject).photo.photo_small;
      this.imageView.getImageReceiver().setCurrentAccount(paramInt);
      this.imageView.setImage((TLObject)localObject, "50_50", this.avatarDrawable);
      localObject = this.checkImageView;
      if (paramInt != UserConfig.selectedAccount) {
        break label145;
      }
    }
    label145:
    for (paramInt = 0;; paramInt = 4)
    {
      ((ImageView)localObject).setVisibility(paramInt);
      return;
      localObject = null;
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/AccountSelectCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
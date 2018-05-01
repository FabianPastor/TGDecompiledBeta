package org.telegram.ui.Cells;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class JoinSheetUserCell
  extends FrameLayout
{
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private BackupImageView imageView;
  private TextView nameTextView;
  private int[] result = new int[1];
  
  public JoinSheetUserCell(Context paramContext)
  {
    super(paramContext);
    this.imageView = new BackupImageView(paramContext);
    this.imageView.setRoundRadius(AndroidUtilities.dp(27.0F));
    addView(this.imageView, LayoutHelper.createFrame(54, 54.0F, 49, 0.0F, 7.0F, 0.0F, 0.0F));
    this.nameTextView = new TextView(paramContext);
    this.nameTextView.setTextColor(Theme.getColor("dialogTextBlack"));
    this.nameTextView.setTextSize(1, 12.0F);
    this.nameTextView.setMaxLines(1);
    this.nameTextView.setGravity(49);
    this.nameTextView.setLines(1);
    this.nameTextView.setSingleLine(true);
    this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
    addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0F, 51, 6.0F, 64.0F, 6.0F, 0.0F));
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0F), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(90.0F), NUM));
  }
  
  public void setCount(int paramInt)
  {
    this.nameTextView.setText("");
    this.avatarDrawable.setInfo(0, null, null, false, "+" + LocaleController.formatShortNumber(paramInt, this.result));
    this.imageView.setImage((TLRPC.FileLocation)null, "50_50", this.avatarDrawable);
  }
  
  public void setUser(TLRPC.User paramUser)
  {
    this.nameTextView.setText(ContactsController.formatName(paramUser.first_name, paramUser.last_name));
    this.avatarDrawable.setInfo(paramUser);
    Object localObject1 = null;
    Object localObject2 = localObject1;
    if (paramUser != null)
    {
      localObject2 = localObject1;
      if (paramUser.photo != null) {
        localObject2 = paramUser.photo.photo_small;
      }
    }
    this.imageView.setImage((TLObject)localObject2, "50_50", this.avatarDrawable);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/JoinSheetUserCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
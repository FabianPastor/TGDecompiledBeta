package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.GroupCreateCheckBox;
import org.telegram.ui.Components.LayoutHelper;

public class DrawerUserCell
  extends FrameLayout
{
  private int accountNumber;
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private GroupCreateCheckBox checkBox;
  private BackupImageView imageView;
  private RectF rect = new RectF();
  private TextView textView;
  
  public DrawerUserCell(Context paramContext)
  {
    super(paramContext);
    this.avatarDrawable.setTextSize(AndroidUtilities.dp(12.0F));
    this.imageView = new BackupImageView(paramContext);
    this.imageView.setRoundRadius(AndroidUtilities.dp(18.0F));
    addView(this.imageView, LayoutHelper.createFrame(36, 36.0F, 51, 14.0F, 6.0F, 0.0F, 0.0F));
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(Theme.getColor("chats_menuItemText"));
    this.textView.setTextSize(1, 15.0F);
    this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    this.textView.setGravity(19);
    this.textView.setEllipsize(TextUtils.TruncateAt.END);
    addView(this.textView, LayoutHelper.createFrame(-1, -1.0F, 51, 72.0F, 0.0F, 60.0F, 0.0F));
    this.checkBox = new GroupCreateCheckBox(paramContext);
    this.checkBox.setChecked(true, false);
    this.checkBox.setCheckScale(0.9F);
    this.checkBox.setInnerRadDiff(AndroidUtilities.dp(1.5F));
    this.checkBox.setColorKeysOverrides("chats_unreadCounterText", "chats_unreadCounter", "chats_menuBackground");
    addView(this.checkBox, LayoutHelper.createFrame(18, 18.0F, 51, 37.0F, 27.0F, 0.0F, 0.0F));
    setWillNotDraw(false);
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
  
  protected void onDraw(Canvas paramCanvas)
  {
    if ((UserConfig.getActivatedAccountsCount() <= 1) || (!NotificationsController.getInstance(this.accountNumber).showBadgeNumber)) {}
    for (;;)
    {
      return;
      int i = NotificationsController.getInstance(this.accountNumber).getTotalUnreadCount();
      if (i > 0)
      {
        String str = String.format("%d", new Object[] { Integer.valueOf(i) });
        int j = AndroidUtilities.dp(12.5F);
        int k = (int)Math.ceil(Theme.chat_livePaint.measureText(str));
        int m = Math.max(AndroidUtilities.dp(12.0F), k);
        i = getMeasuredWidth() - m - AndroidUtilities.dp(25.0F) - AndroidUtilities.dp(5.5F);
        this.rect.set(i, j, i + m + AndroidUtilities.dp(11.0F), AndroidUtilities.dp(23.0F) + j);
        paramCanvas.drawRoundRect(this.rect, AndroidUtilities.density * 11.5F, AndroidUtilities.density * 11.5F, Theme.dialogs_countPaint);
        paramCanvas.drawText(str, this.rect.left + (this.rect.width() - k) / 2.0F - AndroidUtilities.dp(0.5F), AndroidUtilities.dp(16.0F) + j, Theme.dialogs_countTextPaint);
      }
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0F), NUM));
  }
  
  public void setAccount(int paramInt)
  {
    this.accountNumber = paramInt;
    Object localObject = UserConfig.getInstance(this.accountNumber).getCurrentUser();
    if (localObject == null) {
      return;
    }
    this.avatarDrawable.setInfo((TLRPC.User)localObject);
    this.textView.setText(ContactsController.formatName(((TLRPC.User)localObject).first_name, ((TLRPC.User)localObject).last_name));
    if ((((TLRPC.User)localObject).photo != null) && (((TLRPC.User)localObject).photo.photo_small != null) && (((TLRPC.User)localObject).photo.photo_small.volume_id != 0L) && (((TLRPC.User)localObject).photo.photo_small.local_id != 0))
    {
      localObject = ((TLRPC.User)localObject).photo.photo_small;
      label100:
      this.imageView.getImageReceiver().setCurrentAccount(paramInt);
      this.imageView.setImage((TLObject)localObject, "50_50", this.avatarDrawable);
      localObject = this.checkBox;
      if (paramInt != UserConfig.selectedAccount) {
        break label153;
      }
    }
    label153:
    for (paramInt = 0;; paramInt = 4)
    {
      ((GroupCreateCheckBox)localObject).setVisibility(paramInt);
      break;
      localObject = null;
      break label100;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/DrawerUserCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
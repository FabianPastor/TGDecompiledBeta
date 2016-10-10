package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;

public class ShareDialogCell
  extends FrameLayout
{
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private CheckBox checkBox;
  private BackupImageView imageView;
  private TextView nameTextView;
  
  public ShareDialogCell(Context paramContext)
  {
    super(paramContext);
    setBackgroundResource(2130837796);
    this.imageView = new BackupImageView(paramContext);
    this.imageView.setRoundRadius(AndroidUtilities.dp(27.0F));
    addView(this.imageView, LayoutHelper.createFrame(54, 54.0F, 49, 0.0F, 7.0F, 0.0F, 0.0F));
    this.nameTextView = new TextView(paramContext);
    this.nameTextView.setTextColor(-14606047);
    this.nameTextView.setTextSize(1, 12.0F);
    this.nameTextView.setMaxLines(2);
    this.nameTextView.setGravity(49);
    this.nameTextView.setLines(2);
    this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
    addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0F, 51, 6.0F, 64.0F, 6.0F, 0.0F));
    this.checkBox = new CheckBox(paramContext, 2130837959);
    this.checkBox.setSize(24);
    this.checkBox.setCheckOffset(AndroidUtilities.dp(1.0F));
    this.checkBox.setVisibility(0);
    this.checkBox.setColor(-12664327);
    addView(this.checkBox, LayoutHelper.createFrame(24, 24.0F, 49, 17.0F, 39.0F, 0.0F, 0.0F));
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0F), 1073741824));
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((Build.VERSION.SDK_INT >= 21) && (getBackground() != null) && ((paramMotionEvent.getAction() == 0) || (paramMotionEvent.getAction() == 2))) {
      getBackground().setHotspot(paramMotionEvent.getX(), paramMotionEvent.getY());
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.checkBox.setChecked(paramBoolean1, paramBoolean2);
  }
  
  public void setDialog(int paramInt, boolean paramBoolean, CharSequence paramCharSequence)
  {
    Object localObject1 = null;
    if (paramInt > 0)
    {
      localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(paramInt));
      if (paramCharSequence != null) {
        this.nameTextView.setText(paramCharSequence);
      }
      for (;;)
      {
        this.avatarDrawable.setInfo((TLRPC.User)localObject2);
        paramCharSequence = (CharSequence)localObject1;
        if (localObject2 != null)
        {
          paramCharSequence = (CharSequence)localObject1;
          if (((TLRPC.User)localObject2).photo != null) {
            paramCharSequence = ((TLRPC.User)localObject2).photo.photo_small;
          }
        }
        this.imageView.setImage(paramCharSequence, "50_50", this.avatarDrawable);
        this.checkBox.setChecked(paramBoolean, false);
        return;
        if (localObject2 != null) {
          this.nameTextView.setText(ContactsController.formatName(((TLRPC.User)localObject2).first_name, ((TLRPC.User)localObject2).last_name));
        } else {
          this.nameTextView.setText("");
        }
      }
    }
    Object localObject2 = MessagesController.getInstance().getChat(Integer.valueOf(-paramInt));
    if (paramCharSequence != null) {
      this.nameTextView.setText(paramCharSequence);
    }
    for (;;)
    {
      this.avatarDrawable.setInfo((TLRPC.Chat)localObject2);
      paramCharSequence = (CharSequence)localObject1;
      if (localObject2 == null) {
        break;
      }
      paramCharSequence = (CharSequence)localObject1;
      if (((TLRPC.Chat)localObject2).photo == null) {
        break;
      }
      paramCharSequence = ((TLRPC.Chat)localObject2).photo.photo_small;
      break;
      if (localObject2 != null) {
        this.nameTextView.setText(((TLRPC.Chat)localObject2).title);
      } else {
        this.nameTextView.setText("");
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/ShareDialogCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
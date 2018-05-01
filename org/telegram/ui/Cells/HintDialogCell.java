package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.util.LongSparseArray;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class HintDialogCell
  extends FrameLayout
{
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private StaticLayout countLayout;
  private int countWidth;
  private int currentAccount = UserConfig.selectedAccount;
  private long dialog_id;
  private BackupImageView imageView;
  private int lastUnreadCount;
  private TextView nameTextView;
  private RectF rect = new RectF();
  
  public HintDialogCell(Context paramContext)
  {
    super(paramContext);
    this.imageView = new BackupImageView(paramContext);
    this.imageView.setRoundRadius(AndroidUtilities.dp(27.0F));
    addView(this.imageView, LayoutHelper.createFrame(54, 54.0F, 49, 0.0F, 7.0F, 0.0F, 0.0F));
    this.nameTextView = new TextView(paramContext);
    this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.nameTextView.setTextSize(1, 12.0F);
    this.nameTextView.setMaxLines(2);
    this.nameTextView.setGravity(49);
    this.nameTextView.setLines(2);
    this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
    addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0F, 51, 6.0F, 64.0F, 6.0F, 0.0F));
  }
  
  public void checkUnreadCounter(int paramInt)
  {
    if ((paramInt != 0) && ((paramInt & 0x100) == 0) && ((paramInt & 0x800) == 0)) {}
    for (;;)
    {
      return;
      Object localObject = (TLRPC.TL_dialog)MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.dialog_id);
      if ((localObject != null) && (((TLRPC.TL_dialog)localObject).unread_count != 0))
      {
        if (this.lastUnreadCount != ((TLRPC.TL_dialog)localObject).unread_count)
        {
          this.lastUnreadCount = ((TLRPC.TL_dialog)localObject).unread_count;
          localObject = String.format("%d", new Object[] { Integer.valueOf(((TLRPC.TL_dialog)localObject).unread_count) });
          this.countWidth = Math.max(AndroidUtilities.dp(12.0F), (int)Math.ceil(Theme.dialogs_countTextPaint.measureText((String)localObject)));
          this.countLayout = new StaticLayout((CharSequence)localObject, Theme.dialogs_countTextPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, false);
          if (paramInt != 0) {
            invalidate();
          }
        }
      }
      else if (this.countLayout != null)
      {
        if (paramInt != 0) {
          invalidate();
        }
        this.lastUnreadCount = 0;
        this.countLayout = null;
      }
    }
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    boolean bool = super.drawChild(paramCanvas, paramView, paramLong);
    int i;
    int j;
    RectF localRectF;
    float f1;
    float f2;
    if ((paramView == this.imageView) && (this.countLayout != null))
    {
      i = AndroidUtilities.dp(6.0F);
      j = AndroidUtilities.dp(54.0F);
      int k = j - AndroidUtilities.dp(5.5F);
      this.rect.set(k, i, this.countWidth + k + AndroidUtilities.dp(11.0F), AndroidUtilities.dp(23.0F) + i);
      localRectF = this.rect;
      f1 = AndroidUtilities.density;
      f2 = AndroidUtilities.density;
      if (!MessagesController.getInstance(this.currentAccount).isDialogMuted(this.dialog_id)) {
        break label174;
      }
    }
    label174:
    for (paramView = Theme.dialogs_countGrayPaint;; paramView = Theme.dialogs_countPaint)
    {
      paramCanvas.drawRoundRect(localRectF, 11.5F * f1, 11.5F * f2, paramView);
      paramCanvas.save();
      paramCanvas.translate(j, AndroidUtilities.dp(4.0F) + i);
      this.countLayout.draw(paramCanvas);
      paramCanvas.restore();
      return bool;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0F), NUM));
  }
  
  public void setDialog(int paramInt, boolean paramBoolean, CharSequence paramCharSequence)
  {
    this.dialog_id = paramInt;
    Object localObject1 = null;
    Object localObject2;
    if (paramInt > 0)
    {
      localObject2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(paramInt));
      if (paramCharSequence != null)
      {
        this.nameTextView.setText(paramCharSequence);
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
        if (!paramBoolean) {
          break label246;
        }
        checkUnreadCounter(0);
      }
    }
    for (;;)
    {
      return;
      if (localObject2 != null)
      {
        this.nameTextView.setText(ContactsController.formatName(((TLRPC.User)localObject2).first_name, ((TLRPC.User)localObject2).last_name));
        break;
      }
      this.nameTextView.setText("");
      break;
      localObject2 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-paramInt));
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
      label246:
      this.countLayout = null;
    }
  }
  
  public void update()
  {
    int i = (int)this.dialog_id;
    Object localObject;
    if (i > 0)
    {
      localObject = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
      this.avatarDrawable.setInfo((TLRPC.User)localObject);
    }
    for (;;)
    {
      return;
      localObject = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
      this.avatarDrawable.setInfo((TLRPC.Chat)localObject);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/HintDialogCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
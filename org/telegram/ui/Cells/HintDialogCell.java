package org.telegram.ui.Cells;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class HintDialogCell
  extends FrameLayout
{
  private static Drawable countDrawable;
  private static Drawable countDrawableGrey;
  private static TextPaint countPaint;
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private StaticLayout countLayout;
  private int countWidth;
  private long dialog_id;
  private BackupImageView imageView;
  private int lastUnreadCount;
  private TextView nameTextView;
  
  public HintDialogCell(Context paramContext)
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
    if (countDrawable == null)
    {
      countDrawable = getResources().getDrawable(2130837639);
      countDrawableGrey = getResources().getDrawable(2130837640);
      countPaint = new TextPaint(1);
      countPaint.setColor(-1);
      countPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    }
    countPaint.setTextSize(AndroidUtilities.dp(13.0F));
  }
  
  public void checkUnreadCounter(int paramInt)
  {
    if ((paramInt != 0) && ((paramInt & 0x100) == 0) && ((paramInt & 0x800) == 0)) {}
    do
    {
      do
      {
        do
        {
          return;
          localObject = (TLRPC.TL_dialog)MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.dialog_id));
          if ((localObject == null) || (((TLRPC.TL_dialog)localObject).unread_count == 0)) {
            break;
          }
        } while (this.lastUnreadCount == ((TLRPC.TL_dialog)localObject).unread_count);
        this.lastUnreadCount = ((TLRPC.TL_dialog)localObject).unread_count;
        Object localObject = String.format("%d", new Object[] { Integer.valueOf(((TLRPC.TL_dialog)localObject).unread_count) });
        this.countWidth = Math.max(AndroidUtilities.dp(12.0F), (int)Math.ceil(countPaint.measureText((String)localObject)));
        this.countLayout = new StaticLayout((CharSequence)localObject, countPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, false);
      } while (paramInt == 0);
      invalidate();
      return;
    } while (this.countLayout == null);
    if (paramInt != 0) {
      invalidate();
    }
    this.lastUnreadCount = 0;
    this.countLayout = null;
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    boolean bool = super.drawChild(paramCanvas, paramView, paramLong);
    int i;
    int j;
    int k;
    if ((paramView == this.imageView) && (this.countLayout != null))
    {
      i = AndroidUtilities.dp(6.0F);
      j = AndroidUtilities.dp(54.0F);
      k = j - AndroidUtilities.dp(5.5F);
      if (!MessagesController.getInstance().isDialogMuted(this.dialog_id)) {
        break label136;
      }
      countDrawableGrey.setBounds(k, i, this.countWidth + k + AndroidUtilities.dp(11.0F), countDrawableGrey.getIntrinsicHeight() + i);
      countDrawableGrey.draw(paramCanvas);
    }
    for (;;)
    {
      paramCanvas.save();
      paramCanvas.translate(j, AndroidUtilities.dp(4.0F) + i);
      this.countLayout.draw(paramCanvas);
      paramCanvas.restore();
      return bool;
      label136:
      countDrawable.setBounds(k, i, this.countWidth + k + AndroidUtilities.dp(11.0F), countDrawable.getIntrinsicHeight() + i);
      countDrawable.draw(paramCanvas);
    }
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
  
  public void setDialog(int paramInt, boolean paramBoolean, CharSequence paramCharSequence)
  {
    this.dialog_id = paramInt;
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
        if (!paramBoolean) {
          break;
        }
        checkUnreadCounter(0);
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
    this.countLayout = null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/HintDialogCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
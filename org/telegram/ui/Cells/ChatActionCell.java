package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.PhotoViewer;

public class ChatActionCell
  extends BaseCell
{
  private static Paint backPaint;
  private static TextPaint textPaint;
  private AvatarDrawable avatarDrawable;
  private MessageObject currentMessageObject;
  private ChatActionCellDelegate delegate;
  private boolean hasReplyMessage;
  private boolean imagePressed = false;
  private ImageReceiver imageReceiver;
  private URLSpan pressedLink;
  private int previousWidth = 0;
  private int textHeight = 0;
  private StaticLayout textLayout;
  private int textWidth = 0;
  private int textX = 0;
  private int textXLeft = 0;
  private int textY = 0;
  
  public ChatActionCell(Context paramContext)
  {
    super(paramContext);
    if (textPaint == null)
    {
      textPaint = new TextPaint(1);
      textPaint.setColor(-1);
      textPaint.linkColor = -1;
      textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      backPaint = new Paint(1);
    }
    backPaint.setColor(ApplicationLoader.getServiceMessageColor());
    this.imageReceiver = new ImageReceiver(this);
    this.imageReceiver.setRoundRadius(AndroidUtilities.dp(32.0F));
    this.avatarDrawable = new AvatarDrawable();
    textPaint.setTextSize(AndroidUtilities.dp(MessagesController.getInstance().fontSize - 2));
  }
  
  private int findMaxWidthAroundLine(int paramInt)
  {
    int i = (int)Math.ceil(this.textLayout.getLineWidth(paramInt));
    int k = this.textLayout.getLineCount();
    int j = paramInt + 1;
    while (j < k)
    {
      int m = (int)Math.ceil(this.textLayout.getLineWidth(j));
      if (Math.abs(m - i) >= AndroidUtilities.dp(12.0F)) {
        break;
      }
      i = Math.max(m, i);
      j += 1;
    }
    paramInt -= 1;
    while (paramInt >= 0)
    {
      j = (int)Math.ceil(this.textLayout.getLineWidth(paramInt));
      if (Math.abs(j - i) >= AndroidUtilities.dp(12.0F)) {
        break;
      }
      i = Math.max(j, i);
      paramInt -= 1;
    }
    return i;
  }
  
  public MessageObject getMessageObject()
  {
    return this.currentMessageObject;
  }
  
  public ImageReceiver getPhotoImage()
  {
    return this.imageReceiver;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.currentMessageObject == null) {}
    do
    {
      return;
      if (this.currentMessageObject.type == 11) {
        this.imageReceiver.draw(paramCanvas);
      }
    } while (this.textLayout == null);
    int i6 = this.textLayout.getLineCount();
    int i7 = AndroidUtilities.dp(6.0F);
    int i = AndroidUtilities.dp(7.0F);
    int i3 = 0;
    int i2 = 0;
    if (i2 < i6)
    {
      int j = findMaxWidthAroundLine(i2);
      int i8 = (getMeasuredWidth() - j) / 2 - AndroidUtilities.dp(3.0F);
      int i9 = j + AndroidUtilities.dp(6.0F);
      j = this.textLayout.getLineBottom(i2);
      int i1 = j - i3;
      int i5 = 0;
      i3 = j;
      label136:
      int m;
      label144:
      int k;
      int n;
      int i4;
      label356:
      int i10;
      if (i2 == i6 - 1)
      {
        j = 1;
        if (i2 != 0) {
          break label762;
        }
        m = 1;
        k = i1;
        n = i;
        if (m != 0)
        {
          n = i - AndroidUtilities.dp(3.0F);
          k = i1 + AndroidUtilities.dp(3.0F);
        }
        i1 = k;
        if (j != 0) {
          i1 = k + AndroidUtilities.dp(3.0F);
        }
        paramCanvas.drawRect(i8, n, i8 + i9, n + i1, backPaint);
        i = i5;
        i4 = j;
        if (j == 0)
        {
          i = i5;
          i4 = j;
          if (i2 + 1 < i6)
          {
            k = findMaxWidthAroundLine(i2 + 1) + AndroidUtilities.dp(6.0F);
            if (i7 * 2 + k >= i9) {
              break label768;
            }
            j = (getMeasuredWidth() - k) / 2;
            i4 = 1;
            i = AndroidUtilities.dp(3.0F);
            paramCanvas.drawRect(i8, n + i1, j, n + i1 + AndroidUtilities.dp(3.0F), backPaint);
            paramCanvas.drawRect(j + k, n + i1, i8 + i9, n + i1 + AndroidUtilities.dp(3.0F), backPaint);
          }
        }
        i5 = m;
        j = i1;
        k = n;
        if (m == 0)
        {
          i5 = m;
          j = i1;
          k = n;
          if (i2 > 0)
          {
            i10 = findMaxWidthAroundLine(i2 - 1) + AndroidUtilities.dp(6.0F);
            if (i7 * 2 + i10 >= i9) {
              break label898;
            }
            m = (getMeasuredWidth() - i10) / 2;
            i5 = 1;
            k = n - AndroidUtilities.dp(3.0F);
            j = i1 + AndroidUtilities.dp(3.0F);
            paramCanvas.drawRect(i8, k, m, AndroidUtilities.dp(3.0F) + k, backPaint);
            paramCanvas.drawRect(m + i10, k, i8 + i9, AndroidUtilities.dp(3.0F) + k, backPaint);
          }
        }
      }
      for (;;)
      {
        paramCanvas.drawRect(i8 - i7, k + i7, i8, k + j + i - i7, backPaint);
        paramCanvas.drawRect(i8 + i9, k + i7, i8 + i9 + i7, k + j + i - i7, backPaint);
        if (i5 != 0)
        {
          m = i8 - i7;
          org.telegram.ui.ActionBar.Theme.cornerOuter[0].setBounds(m, k, m + i7, k + i7);
          org.telegram.ui.ActionBar.Theme.cornerOuter[0].draw(paramCanvas);
          m = i8 + i9;
          org.telegram.ui.ActionBar.Theme.cornerOuter[1].setBounds(m, k, m + i7, k + i7);
          org.telegram.ui.ActionBar.Theme.cornerOuter[1].draw(paramCanvas);
        }
        if (i4 != 0)
        {
          i = k + j + i - i7;
          m = i8 + i9;
          org.telegram.ui.ActionBar.Theme.cornerOuter[2].setBounds(m, i, m + i7, i + i7);
          org.telegram.ui.ActionBar.Theme.cornerOuter[2].draw(paramCanvas);
          m = i8 - i7;
          org.telegram.ui.ActionBar.Theme.cornerOuter[3].setBounds(m, i, m + i7, i + i7);
          org.telegram.ui.ActionBar.Theme.cornerOuter[3].draw(paramCanvas);
        }
        i = k + j;
        i2 += 1;
        break;
        j = 0;
        break label136;
        label762:
        m = 0;
        break label144;
        label768:
        if (i7 * 2 + i9 < k)
        {
          i = AndroidUtilities.dp(3.0F);
          k = n + i1 - AndroidUtilities.dp(9.0F);
          i4 = i8 - i7 * 2;
          org.telegram.ui.ActionBar.Theme.cornerInner[2].setBounds(i4, k, i4 + i7, k + i7);
          org.telegram.ui.ActionBar.Theme.cornerInner[2].draw(paramCanvas);
          i4 = i8 + i9 + i7;
          org.telegram.ui.ActionBar.Theme.cornerInner[3].setBounds(i4, k, i4 + i7, k + i7);
          org.telegram.ui.ActionBar.Theme.cornerInner[3].draw(paramCanvas);
          i4 = j;
          break label356;
        }
        i = AndroidUtilities.dp(6.0F);
        i4 = j;
        break label356;
        label898:
        if (i7 * 2 + i9 < i10)
        {
          k = n - AndroidUtilities.dp(3.0F);
          j = i1 + AndroidUtilities.dp(3.0F);
          n = k + i7;
          i1 = i8 - i7 * 2;
          org.telegram.ui.ActionBar.Theme.cornerInner[0].setBounds(i1, n, i1 + i7, n + i7);
          org.telegram.ui.ActionBar.Theme.cornerInner[0].draw(paramCanvas);
          i1 = i8 + i9 + i7;
          org.telegram.ui.ActionBar.Theme.cornerInner[1].setBounds(i1, n, i1 + i7, n + i7);
          org.telegram.ui.ActionBar.Theme.cornerInner[1].draw(paramCanvas);
          i5 = m;
        }
        else
        {
          k = n - AndroidUtilities.dp(6.0F);
          j = i1 + AndroidUtilities.dp(6.0F);
          i5 = m;
        }
      }
    }
    paramCanvas.save();
    paramCanvas.translate(this.textXLeft, this.textY);
    this.textLayout.draw(paramCanvas);
    paramCanvas.restore();
  }
  
  protected void onLongPress()
  {
    if (this.delegate != null) {
      this.delegate.didLongPressed(this);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (this.currentMessageObject == null)
    {
      setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), this.textHeight + AndroidUtilities.dp(14.0F));
      return;
    }
    paramInt2 = Math.max(AndroidUtilities.dp(30.0F), View.MeasureSpec.getSize(paramInt1));
    int i;
    if (paramInt2 != this.previousWidth)
    {
      this.previousWidth = paramInt2;
      i = paramInt2 - AndroidUtilities.dp(30.0F);
      this.textLayout = new StaticLayout(this.currentMessageObject.messageText, textPaint, i, Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, false);
      this.textHeight = 0;
      this.textWidth = 0;
      try
      {
        int j = this.textLayout.getLineCount();
        paramInt1 = 0;
        for (;;)
        {
          if (paramInt1 < j) {
            try
            {
              float f2 = this.textLayout.getLineWidth(paramInt1);
              float f1 = f2;
              if (f2 > i) {
                f1 = i;
              }
              this.textHeight = ((int)Math.max(this.textHeight, Math.ceil(this.textLayout.getLineBottom(paramInt1))));
              this.textWidth = ((int)Math.max(this.textWidth, Math.ceil(f1)));
              paramInt1 += 1;
            }
            catch (Exception localException1)
            {
              FileLog.e("tmessages", localException1);
              return;
            }
          }
        }
        i = this.textHeight;
      }
      catch (Exception localException2)
      {
        FileLog.e("tmessages", localException2);
        this.textX = ((paramInt2 - this.textWidth) / 2);
        this.textY = AndroidUtilities.dp(7.0F);
        this.textXLeft = ((paramInt2 - this.textLayout.getWidth()) / 2);
        if (this.currentMessageObject.type == 11) {
          this.imageReceiver.setImageCoords((paramInt2 - AndroidUtilities.dp(64.0F)) / 2, this.textHeight + AndroidUtilities.dp(15.0F), AndroidUtilities.dp(64.0F), AndroidUtilities.dp(64.0F));
        }
      }
    }
    if (this.currentMessageObject.type == 11) {}
    for (paramInt1 = 70;; paramInt1 = 0)
    {
      setMeasuredDimension(paramInt2, AndroidUtilities.dp(paramInt1 + 14) + i);
      return;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    float f3 = paramMotionEvent.getX();
    float f1 = paramMotionEvent.getY();
    boolean bool1 = false;
    boolean bool3 = false;
    boolean bool2;
    Object localObject;
    if (paramMotionEvent.getAction() == 0)
    {
      bool2 = bool1;
      if (this.delegate != null)
      {
        bool1 = bool3;
        if (this.currentMessageObject.type == 11)
        {
          bool1 = bool3;
          if (this.imageReceiver.isInsideImage(f3, f1))
          {
            this.imagePressed = true;
            bool1 = true;
          }
        }
        bool2 = bool1;
        if (bool1)
        {
          startCheckLongPress();
          bool2 = bool1;
        }
      }
      bool1 = bool2;
      if (!bool2) {
        if (paramMotionEvent.getAction() != 0)
        {
          bool1 = bool2;
          if (this.pressedLink != null)
          {
            bool1 = bool2;
            if (paramMotionEvent.getAction() != 1) {}
          }
        }
        else
        {
          if ((f3 < this.textX) || (f1 < this.textY) || (f3 > this.textX + this.textWidth) || (f1 > this.textY + this.textHeight)) {
            break label582;
          }
          float f2 = this.textY;
          f3 -= this.textXLeft;
          int i = this.textLayout.getLineForVertical((int)(f1 - f2));
          int j = this.textLayout.getOffsetForHorizontal(i, f3);
          f1 = this.textLayout.getLineLeft(i);
          if ((f1 > f3) || (this.textLayout.getLineWidth(i) + f1 < f3) || (!(this.currentMessageObject.messageText instanceof Spannable))) {
            break label570;
          }
          localObject = (URLSpan[])((Spannable)this.currentMessageObject.messageText).getSpans(j, j, URLSpan.class);
          if (localObject.length == 0) {
            break label558;
          }
          if (paramMotionEvent.getAction() != 0) {
            break label473;
          }
          this.pressedLink = localObject[0];
          bool1 = true;
        }
      }
    }
    for (;;)
    {
      bool2 = bool1;
      if (!bool1) {
        bool2 = super.onTouchEvent(paramMotionEvent);
      }
      return bool2;
      if (paramMotionEvent.getAction() != 2) {
        cancelCheckLongPress();
      }
      bool2 = bool1;
      if (!this.imagePressed) {
        break;
      }
      if (paramMotionEvent.getAction() == 1)
      {
        this.imagePressed = false;
        bool2 = bool1;
        if (this.delegate == null) {
          break;
        }
        this.delegate.didClickedImage(this);
        playSoundEffect(0);
        bool2 = bool1;
        break;
      }
      if (paramMotionEvent.getAction() == 3)
      {
        this.imagePressed = false;
        bool2 = bool1;
        break;
      }
      bool2 = bool1;
      if (paramMotionEvent.getAction() != 2) {
        break;
      }
      bool2 = bool1;
      if (this.imageReceiver.isInsideImage(f3, f1)) {
        break;
      }
      this.imagePressed = false;
      bool2 = bool1;
      break;
      label473:
      bool1 = bool2;
      if (localObject[0] == this.pressedLink)
      {
        if (this.delegate != null)
        {
          localObject = localObject[0].getURL();
          if (!((String)localObject).startsWith("game")) {
            break label541;
          }
          this.delegate.didPressedReplyMessage(this, this.currentMessageObject.messageOwner.reply_to_msg_id);
        }
        for (;;)
        {
          bool1 = true;
          break;
          label541:
          this.delegate.needOpenUserProfile(Integer.parseInt((String)localObject));
        }
        label558:
        this.pressedLink = null;
        bool1 = bool2;
        continue;
        label570:
        this.pressedLink = null;
        bool1 = bool2;
        continue;
        label582:
        this.pressedLink = null;
        bool1 = bool2;
      }
    }
  }
  
  public void setDelegate(ChatActionCellDelegate paramChatActionCellDelegate)
  {
    this.delegate = paramChatActionCellDelegate;
  }
  
  public void setMessageObject(MessageObject paramMessageObject)
  {
    boolean bool2 = true;
    if ((this.currentMessageObject == paramMessageObject) && ((this.hasReplyMessage) || (paramMessageObject.replyMessageObject == null))) {
      return;
    }
    this.currentMessageObject = paramMessageObject;
    boolean bool1;
    int i;
    if (paramMessageObject.replyMessageObject != null)
    {
      bool1 = true;
      this.hasReplyMessage = bool1;
      this.previousWidth = 0;
      if (this.currentMessageObject.type != 11) {
        break label321;
      }
      i = 0;
      if (paramMessageObject.messageOwner.to_id != null)
      {
        if (paramMessageObject.messageOwner.to_id.chat_id == 0) {
          break label199;
        }
        i = paramMessageObject.messageOwner.to_id.chat_id;
      }
      label100:
      this.avatarDrawable.setInfo(i, null, null, false);
      if (!(this.currentMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto)) {
        break label257;
      }
      this.imageReceiver.setImage(this.currentMessageObject.messageOwner.action.newUserPhoto.photo_small, "50_50", this.avatarDrawable, null, false);
      label159:
      paramMessageObject = this.imageReceiver;
      if (PhotoViewer.getInstance().isShowingImage(this.currentMessageObject)) {
        break label315;
      }
      bool1 = bool2;
      label181:
      paramMessageObject.setVisible(bool1, false);
    }
    for (;;)
    {
      requestLayout();
      return;
      bool1 = false;
      break;
      label199:
      if (paramMessageObject.messageOwner.to_id.channel_id != 0)
      {
        i = paramMessageObject.messageOwner.to_id.channel_id;
        break label100;
      }
      int j = paramMessageObject.messageOwner.to_id.user_id;
      i = j;
      if (j != UserConfig.getClientUserId()) {
        break label100;
      }
      i = paramMessageObject.messageOwner.from_id;
      break label100;
      label257:
      paramMessageObject = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.photoThumbs, AndroidUtilities.dp(64.0F));
      if (paramMessageObject != null)
      {
        this.imageReceiver.setImage(paramMessageObject.location, "50_50", this.avatarDrawable, null, false);
        break label159;
      }
      this.imageReceiver.setImageBitmap(this.avatarDrawable);
      break label159;
      label315:
      bool1 = false;
      break label181;
      label321:
      this.imageReceiver.setImageBitmap((Bitmap)null);
    }
  }
  
  public static abstract interface ChatActionCellDelegate
  {
    public abstract void didClickedImage(ChatActionCell paramChatActionCell);
    
    public abstract void didLongPressed(ChatActionCell paramChatActionCell);
    
    public abstract void didPressedBotButton(MessageObject paramMessageObject, TLRPC.KeyboardButton paramKeyboardButton);
    
    public abstract void didPressedReplyMessage(ChatActionCell paramChatActionCell, int paramInt);
    
    public abstract void needOpenUserProfile(int paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/ChatActionCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
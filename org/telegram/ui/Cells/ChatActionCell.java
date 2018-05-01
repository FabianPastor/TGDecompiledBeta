package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentEmpty;
import org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.PhotoViewer;

public class ChatActionCell
  extends BaseCell
{
  private AvatarDrawable avatarDrawable;
  private int currentAccount = UserConfig.selectedAccount;
  private MessageObject currentMessageObject;
  private int customDate;
  private CharSequence customText;
  private ChatActionCellDelegate delegate;
  private boolean hasReplyMessage;
  private boolean imagePressed = false;
  private ImageReceiver imageReceiver = new ImageReceiver(this);
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
    this.imageReceiver.setRoundRadius(AndroidUtilities.dp(32.0F));
    this.avatarDrawable = new AvatarDrawable();
  }
  
  private void createLayout(CharSequence paramCharSequence, int paramInt)
  {
    int i = paramInt - AndroidUtilities.dp(30.0F);
    this.textLayout = new StaticLayout(paramCharSequence, Theme.chat_actionTextPaint, i, Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, false);
    this.textHeight = 0;
    this.textWidth = 0;
    try
    {
      int j = this.textLayout.getLineCount();
      int k = 0;
      for (;;)
      {
        if (k >= j) {
          break label148;
        }
        try
        {
          float f1 = this.textLayout.getLineWidth(k);
          float f2 = f1;
          if (f1 > i) {
            f2 = i;
          }
          this.textHeight = ((int)Math.max(this.textHeight, Math.ceil(this.textLayout.getLineBottom(k))));
          this.textWidth = ((int)Math.max(this.textWidth, Math.ceil(f2)));
          k++;
        }
        catch (Exception paramCharSequence)
        {
          FileLog.e(paramCharSequence);
        }
      }
      return;
    }
    catch (Exception paramCharSequence)
    {
      for (;;)
      {
        FileLog.e(paramCharSequence);
        label148:
        this.textX = ((paramInt - this.textWidth) / 2);
        this.textY = AndroidUtilities.dp(7.0F);
        this.textXLeft = ((paramInt - this.textLayout.getWidth()) / 2);
      }
    }
  }
  
  private int findMaxWidthAroundLine(int paramInt)
  {
    int i = (int)Math.ceil(this.textLayout.getLineWidth(paramInt));
    int j = this.textLayout.getLineCount();
    for (int k = paramInt + 1; k < j; k++)
    {
      int m = (int)Math.ceil(this.textLayout.getLineWidth(k));
      if (Math.abs(m - i) >= AndroidUtilities.dp(10.0F)) {
        break;
      }
      i = Math.max(m, i);
    }
    paramInt--;
    while (paramInt >= 0)
    {
      k = (int)Math.ceil(this.textLayout.getLineWidth(paramInt));
      if (Math.abs(k - i) >= AndroidUtilities.dp(10.0F)) {
        break;
      }
      i = Math.max(k, i);
      paramInt--;
    }
    return i;
  }
  
  private boolean isLineBottom(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    if ((paramInt3 == paramInt4 - 1) || ((paramInt3 >= 0) && (paramInt3 <= paramInt4 - 1) && (findMaxWidthAroundLine(paramInt3 + 1) + paramInt5 * 3 < paramInt1))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private boolean isLineTop(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    if ((paramInt3 == 0) || ((paramInt3 >= 0) && (paramInt3 < paramInt4) && (findMaxWidthAroundLine(paramInt3 - 1) + paramInt5 * 3 < paramInt1))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public int getCustomDate()
  {
    return this.customDate;
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
    if ((this.currentMessageObject != null) && (this.currentMessageObject.type == 11)) {
      this.imageReceiver.draw(paramCanvas);
    }
    if (this.textLayout != null)
    {
      int i = this.textLayout.getLineCount();
      int j = AndroidUtilities.dp(11.0F);
      int k = AndroidUtilities.dp(6.0F);
      int m = j - k;
      int n = AndroidUtilities.dp(8.0F);
      int i1 = AndroidUtilities.dp(7.0F);
      int i2 = 0;
      int i3 = 0;
      if (i3 < i)
      {
        int i4 = findMaxWidthAroundLine(i3);
        int i5 = (getMeasuredWidth() - i4 - m) / 2;
        int i6 = i4 + m;
        i4 = this.textLayout.getLineBottom(i3);
        int i7 = i4 - i2;
        int i8 = 0;
        i2 = i4;
        int i9;
        label153:
        label161:
        int i10;
        int i11;
        int i12;
        int i13;
        int i15;
        int i16;
        label293:
        int i17;
        if (i3 == i - 1)
        {
          i9 = 1;
          if (i3 != 0) {
            break label922;
          }
          i4 = 1;
          i10 = i7;
          i11 = i1;
          if (i4 != 0)
          {
            i11 = i1 - AndroidUtilities.dp(3.0F);
            i10 = i7 + AndroidUtilities.dp(3.0F);
          }
          i7 = i10;
          if (i9 != 0) {
            i7 = i10 + AndroidUtilities.dp(3.0F);
          }
          i12 = 0;
          i13 = 0;
          i10 = 0;
          int i14 = 0;
          i15 = i10;
          i16 = i9;
          i1 = i12;
          if (i9 == 0)
          {
            i15 = i10;
            i16 = i9;
            i1 = i12;
            if (i3 + 1 < i)
            {
              i15 = findMaxWidthAroundLine(i3 + 1) + m;
              if (m * 2 + i15 >= i6) {
                break label928;
              }
              i1 = 1;
              i16 = 1;
            }
          }
          i10 = i13;
          i12 = i4;
          i17 = i14;
          if (i4 == 0)
          {
            i10 = i13;
            i12 = i4;
            i17 = i14;
            if (i3 > 0)
            {
              i17 = findMaxWidthAroundLine(i3 - 1) + m;
              if (m * 2 + i17 >= i6) {
                break label960;
              }
              i10 = 1;
              i12 = 1;
            }
          }
          label358:
          if (i1 != 0)
          {
            if (i1 != 1) {
              break label1069;
            }
            i4 = (getMeasuredWidth() - i15) / 2;
            i8 = AndroidUtilities.dp(3.0F);
            if (!isLineBottom(i15, i6, i3 + 1, i, m)) {
              break label992;
            }
            paramCanvas.drawRect(i5 + k, i11 + i7, i4 - m, i11 + i7 + AndroidUtilities.dp(3.0F), Theme.chat_actionBackgroundPaint);
            paramCanvas.drawRect(i4 + i15 + m, i11 + i7, i5 + i6 - k, i11 + i7 + AndroidUtilities.dp(3.0F), Theme.chat_actionBackgroundPaint);
          }
          label485:
          i4 = i7;
          i9 = i11;
          if (i10 != 0)
          {
            if (i10 != 1) {
              break label1387;
            }
            i15 = (getMeasuredWidth() - i17) / 2;
            i9 = i11 - AndroidUtilities.dp(3.0F);
            i4 = i7 + AndroidUtilities.dp(3.0F);
            if (!isLineTop(i17, i6, i3 - 1, i, m)) {
              break label1322;
            }
            paramCanvas.drawRect(i5 + k, i9, i15 - m, AndroidUtilities.dp(3.0F) + i9, Theme.chat_actionBackgroundPaint);
            paramCanvas.drawRect(i15 + i17 + m, i9, i5 + i6 - k, AndroidUtilities.dp(3.0F) + i9, Theme.chat_actionBackgroundPaint);
          }
          label621:
          if ((i12 == 0) && (i16 == 0)) {
            break label1675;
          }
          paramCanvas.drawRect(i5 + k, i11, i5 + i6 - k, i11 + i7, Theme.chat_actionBackgroundPaint);
          label662:
          i11 = i5 - m;
          i7 = i5 + i6 - k;
          if ((i12 == 0) || (i16 != 0) || (i1 == 2)) {
            break label1703;
          }
          paramCanvas.drawRect(i11, i9 + j, i11 + j, i9 + i4 + i8 - AndroidUtilities.dp(6.0F), Theme.chat_actionBackgroundPaint);
          paramCanvas.drawRect(i7, i9 + j, i7 + j, i9 + i4 + i8 - AndroidUtilities.dp(6.0F), Theme.chat_actionBackgroundPaint);
        }
        for (;;)
        {
          if (i12 != 0)
          {
            Theme.chat_cornerOuter[0].setBounds(i11, i9, i11 + j, i9 + j);
            Theme.chat_cornerOuter[0].draw(paramCanvas);
            Theme.chat_cornerOuter[1].setBounds(i7, i9, i7 + j, i9 + j);
            Theme.chat_cornerOuter[1].draw(paramCanvas);
          }
          if (i16 != 0)
          {
            i1 = i9 + i4 + i8 - j;
            Theme.chat_cornerOuter[2].setBounds(i7, i1, i7 + j, i1 + j);
            Theme.chat_cornerOuter[2].draw(paramCanvas);
            Theme.chat_cornerOuter[3].setBounds(i11, i1, i11 + j, i1 + j);
            Theme.chat_cornerOuter[3].draw(paramCanvas);
          }
          i1 = i9 + i4;
          i3++;
          break;
          i9 = 0;
          break label153;
          label922:
          i4 = 0;
          break label161;
          label928:
          if (m * 2 + i6 < i15)
          {
            i1 = 2;
            i16 = i9;
            break label293;
          }
          i1 = 3;
          i16 = i9;
          break label293;
          label960:
          if (m * 2 + i6 < i17)
          {
            i10 = 2;
            i12 = i4;
            break label358;
          }
          i10 = 3;
          i12 = i4;
          break label358;
          label992:
          paramCanvas.drawRect(i5 + k, i11 + i7, i4, i11 + i7 + AndroidUtilities.dp(3.0F), Theme.chat_actionBackgroundPaint);
          paramCanvas.drawRect(i4 + i15, i11 + i7, i5 + i6 - k, i11 + i7 + AndroidUtilities.dp(3.0F), Theme.chat_actionBackgroundPaint);
          break label485;
          label1069:
          if (i1 == 2)
          {
            i8 = AndroidUtilities.dp(3.0F);
            i15 = i11 + i7 - AndroidUtilities.dp(11.0F);
            i9 = i5 - n;
            i4 = i9;
            if (i10 != 2)
            {
              i4 = i9;
              if (i10 != 3) {
                i4 = i9 - m;
              }
            }
            if ((i12 != 0) || (i16 != 0)) {
              paramCanvas.drawRect(i4 + n, AndroidUtilities.dp(3.0F) + i15, i4 + n + j, i15 + j, Theme.chat_actionBackgroundPaint);
            }
            Theme.chat_cornerInner[2].setBounds(i4, i15, i4 + n, i15 + n);
            Theme.chat_cornerInner[2].draw(paramCanvas);
            i9 = i5 + i6;
            i4 = i9;
            if (i10 != 2)
            {
              i4 = i9;
              if (i10 != 3) {
                i4 = i9 + m;
              }
            }
            if ((i12 != 0) || (i16 != 0)) {
              paramCanvas.drawRect(i4 - j, AndroidUtilities.dp(3.0F) + i15, i4, i15 + j, Theme.chat_actionBackgroundPaint);
            }
            Theme.chat_cornerInner[3].setBounds(i4, i15, i4 + n, i15 + n);
            Theme.chat_cornerInner[3].draw(paramCanvas);
            break label485;
          }
          i8 = AndroidUtilities.dp(6.0F);
          break label485;
          label1322:
          paramCanvas.drawRect(i5 + k, i9, i15, AndroidUtilities.dp(3.0F) + i9, Theme.chat_actionBackgroundPaint);
          paramCanvas.drawRect(i15 + i17, i9, i5 + i6 - k, AndroidUtilities.dp(3.0F) + i9, Theme.chat_actionBackgroundPaint);
          break label621;
          label1387:
          if (i10 == 2)
          {
            i9 = i11 - AndroidUtilities.dp(3.0F);
            i17 = i7 + AndroidUtilities.dp(3.0F);
            i13 = i9 + AndroidUtilities.dp(6.2F);
            i15 = i5 - n;
            i4 = i15;
            if (i1 != 2)
            {
              i4 = i15;
              if (i1 != 3) {
                i4 = i15 - m;
              }
            }
            if ((i12 != 0) || (i16 != 0)) {
              paramCanvas.drawRect(i4 + n, AndroidUtilities.dp(3.0F) + i9, i4 + n + j, AndroidUtilities.dp(11.0F) + i9, Theme.chat_actionBackgroundPaint);
            }
            Theme.chat_cornerInner[0].setBounds(i4, i13, i4 + n, i13 + n);
            Theme.chat_cornerInner[0].draw(paramCanvas);
            i15 = i5 + i6;
            i4 = i15;
            if (i1 != 2)
            {
              i4 = i15;
              if (i1 != 3) {
                i4 = i15 + m;
              }
            }
            if ((i12 != 0) || (i16 != 0)) {
              paramCanvas.drawRect(i4 - j, AndroidUtilities.dp(3.0F) + i9, i4, AndroidUtilities.dp(11.0F) + i9, Theme.chat_actionBackgroundPaint);
            }
            Theme.chat_cornerInner[1].setBounds(i4, i13, i4 + n, i13 + n);
            Theme.chat_cornerInner[1].draw(paramCanvas);
            i4 = i17;
            break label621;
          }
          i9 = i11 - AndroidUtilities.dp(6.0F);
          i4 = i7 + AndroidUtilities.dp(6.0F);
          break label621;
          label1675:
          paramCanvas.drawRect(i5, i11, i5 + i6, i11 + i7, Theme.chat_actionBackgroundPaint);
          break label662;
          label1703:
          if ((i16 != 0) && (i12 == 0) && (i10 != 2))
          {
            paramCanvas.drawRect(i11, i9 + j - AndroidUtilities.dp(5.0F), i11 + j, i9 + i4 + i8 - j, Theme.chat_actionBackgroundPaint);
            paramCanvas.drawRect(i7, i9 + j - AndroidUtilities.dp(5.0F), i7 + j, i9 + i4 + i8 - j, Theme.chat_actionBackgroundPaint);
          }
          else if ((i12 != 0) || (i16 != 0))
          {
            paramCanvas.drawRect(i11, i9 + j, i11 + j, i9 + i4 + i8 - j, Theme.chat_actionBackgroundPaint);
            paramCanvas.drawRect(i7, i9 + j, i7 + j, i9 + i4 + i8 - j, Theme.chat_actionBackgroundPaint);
          }
        }
      }
      paramCanvas.save();
      paramCanvas.translate(this.textXLeft, this.textY);
      this.textLayout.draw(paramCanvas);
      paramCanvas.restore();
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  protected void onLongPress()
  {
    if (this.delegate != null) {
      this.delegate.didLongPressed(this);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if ((this.currentMessageObject == null) && (this.customText == null))
    {
      setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), this.textHeight + AndroidUtilities.dp(14.0F));
      return;
    }
    paramInt2 = Math.max(AndroidUtilities.dp(30.0F), View.MeasureSpec.getSize(paramInt1));
    Object localObject;
    if (paramInt2 != this.previousWidth)
    {
      if (this.currentMessageObject == null) {
        break label300;
      }
      if ((this.currentMessageObject.messageOwner == null) || (this.currentMessageObject.messageOwner.media == null) || (this.currentMessageObject.messageOwner.media.ttl_seconds == 0)) {
        break label289;
      }
      if (!(this.currentMessageObject.messageOwner.media.photo instanceof TLRPC.TL_photoEmpty)) {
        break label246;
      }
      localObject = LocaleController.getString("AttachPhotoExpired", NUM);
      label129:
      this.previousWidth = paramInt2;
      createLayout((CharSequence)localObject, paramInt2);
      if ((this.currentMessageObject != null) && (this.currentMessageObject.type == 11)) {
        this.imageReceiver.setImageCoords((paramInt2 - AndroidUtilities.dp(64.0F)) / 2, this.textHeight + AndroidUtilities.dp(15.0F), AndroidUtilities.dp(64.0F), AndroidUtilities.dp(64.0F));
      }
    }
    int i = this.textHeight;
    if ((this.currentMessageObject != null) && (this.currentMessageObject.type == 11)) {}
    for (paramInt1 = 70;; paramInt1 = 0)
    {
      setMeasuredDimension(paramInt2, AndroidUtilities.dp(paramInt1 + 14) + i);
      break;
      label246:
      if ((this.currentMessageObject.messageOwner.media.document instanceof TLRPC.TL_documentEmpty))
      {
        localObject = LocaleController.getString("AttachVideoExpired", NUM);
        break label129;
      }
      localObject = this.currentMessageObject.messageText;
      break label129;
      label289:
      localObject = this.currentMessageObject.messageText;
      break label129;
      label300:
      localObject = this.customText;
      break label129;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool1;
    if (this.currentMessageObject == null)
    {
      bool1 = super.onTouchEvent(paramMotionEvent);
      return bool1;
    }
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    boolean bool2 = false;
    boolean bool3 = false;
    label105:
    Object localObject;
    if (paramMotionEvent.getAction() == 0)
    {
      bool1 = bool2;
      if (this.delegate != null)
      {
        bool2 = bool3;
        if (this.currentMessageObject.type == 11)
        {
          bool2 = bool3;
          if (this.imageReceiver.isInsideImage(f1, f2))
          {
            this.imagePressed = true;
            bool2 = true;
          }
        }
        bool1 = bool2;
        if (bool2)
        {
          startCheckLongPress();
          bool1 = bool2;
        }
      }
      bool2 = bool1;
      if (!bool1) {
        if (paramMotionEvent.getAction() != 0)
        {
          bool2 = bool1;
          if (this.pressedLink != null)
          {
            bool2 = bool1;
            if (paramMotionEvent.getAction() != 1) {}
          }
        }
        else
        {
          if ((f1 < this.textX) || (f2 < this.textY) || (f1 > this.textX + this.textWidth) || (f2 > this.textY + this.textHeight)) {
            break label602;
          }
          float f3 = this.textY;
          f1 -= this.textXLeft;
          int i = this.textLayout.getLineForVertical((int)(f2 - f3));
          int j = this.textLayout.getOffsetForHorizontal(i, f1);
          f2 = this.textLayout.getLineLeft(i);
          if ((f2 > f1) || (this.textLayout.getLineWidth(i) + f2 < f1) || (!(this.currentMessageObject.messageText instanceof Spannable))) {
            break label591;
          }
          localObject = (URLSpan[])((Spannable)this.currentMessageObject.messageText).getSpans(j, j, URLSpan.class);
          if (localObject.length == 0) {
            break label580;
          }
          if (paramMotionEvent.getAction() != 0) {
            break label473;
          }
          this.pressedLink = localObject[0];
          bool2 = true;
        }
      }
    }
    for (;;)
    {
      bool1 = bool2;
      if (bool2) {
        break;
      }
      bool1 = super.onTouchEvent(paramMotionEvent);
      break;
      if (paramMotionEvent.getAction() != 2) {
        cancelCheckLongPress();
      }
      bool1 = bool2;
      if (!this.imagePressed) {
        break label105;
      }
      if (paramMotionEvent.getAction() == 1)
      {
        this.imagePressed = false;
        bool1 = bool2;
        if (this.delegate == null) {
          break label105;
        }
        this.delegate.didClickedImage(this);
        playSoundEffect(0);
        bool1 = bool2;
        break label105;
      }
      if (paramMotionEvent.getAction() == 3)
      {
        this.imagePressed = false;
        bool1 = bool2;
        break label105;
      }
      bool1 = bool2;
      if (paramMotionEvent.getAction() != 2) {
        break label105;
      }
      bool1 = bool2;
      if (this.imageReceiver.isInsideImage(f1, f2)) {
        break label105;
      }
      this.imagePressed = false;
      bool1 = bool2;
      break label105;
      label473:
      bool2 = bool1;
      if (localObject[0] == this.pressedLink)
      {
        if (this.delegate != null)
        {
          localObject = localObject[0].getURL();
          if (!((String)localObject).startsWith("game")) {
            break label540;
          }
          this.delegate.didPressedReplyMessage(this, this.currentMessageObject.messageOwner.reply_to_msg_id);
        }
        for (;;)
        {
          bool2 = true;
          break;
          label540:
          if (((String)localObject).startsWith("http")) {
            Browser.openUrl(getContext(), (String)localObject);
          } else {
            this.delegate.needOpenUserProfile(Integer.parseInt((String)localObject));
          }
        }
        label580:
        this.pressedLink = null;
        bool2 = bool1;
        continue;
        label591:
        this.pressedLink = null;
        bool2 = bool1;
        continue;
        label602:
        this.pressedLink = null;
        bool2 = bool1;
      }
    }
  }
  
  public void setCustomDate(int paramInt)
  {
    if (this.customDate == paramInt) {}
    for (;;)
    {
      return;
      String str = LocaleController.formatDateChat(paramInt);
      if ((this.customText == null) || (!TextUtils.equals(str, this.customText)))
      {
        this.previousWidth = 0;
        this.customDate = paramInt;
        this.customText = str;
        if (getMeasuredWidth() != 0)
        {
          createLayout(this.customText, getMeasuredWidth());
          invalidate();
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            ChatActionCell.this.requestLayout();
          }
        });
      }
    }
  }
  
  public void setDelegate(ChatActionCellDelegate paramChatActionCellDelegate)
  {
    this.delegate = paramChatActionCellDelegate;
  }
  
  public void setMessageObject(MessageObject paramMessageObject)
  {
    boolean bool1 = true;
    if ((this.currentMessageObject == paramMessageObject) && ((this.hasReplyMessage) || (paramMessageObject.replyMessageObject == null))) {
      return;
    }
    this.currentMessageObject = paramMessageObject;
    boolean bool2;
    label39:
    int i;
    if (paramMessageObject.replyMessageObject != null)
    {
      bool2 = true;
      this.hasReplyMessage = bool2;
      this.previousWidth = 0;
      if (this.currentMessageObject.type != 11) {
        break label328;
      }
      i = 0;
      if (paramMessageObject.messageOwner.to_id != null)
      {
        if (paramMessageObject.messageOwner.to_id.chat_id == 0) {
          break label194;
        }
        i = paramMessageObject.messageOwner.to_id.chat_id;
      }
      label99:
      this.avatarDrawable.setInfo(i, null, null, false);
      if (!(this.currentMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto)) {
        break label265;
      }
      this.imageReceiver.setImage(this.currentMessageObject.messageOwner.action.newUserPhoto.photo_small, "50_50", this.avatarDrawable, null, 0);
      label159:
      paramMessageObject = this.imageReceiver;
      if (PhotoViewer.isShowingImage(this.currentMessageObject)) {
        break label323;
      }
      bool2 = bool1;
      label176:
      paramMessageObject.setVisible(bool2, false);
    }
    for (;;)
    {
      requestLayout();
      break;
      bool2 = false;
      break label39;
      label194:
      if (paramMessageObject.messageOwner.to_id.channel_id != 0)
      {
        i = paramMessageObject.messageOwner.to_id.channel_id;
        break label99;
      }
      int j = paramMessageObject.messageOwner.to_id.user_id;
      i = j;
      if (j != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
        break label99;
      }
      i = paramMessageObject.messageOwner.from_id;
      break label99;
      label265:
      paramMessageObject = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.photoThumbs, AndroidUtilities.dp(64.0F));
      if (paramMessageObject != null)
      {
        this.imageReceiver.setImage(paramMessageObject.location, "50_50", this.avatarDrawable, null, 0);
        break label159;
      }
      this.imageReceiver.setImageBitmap(this.avatarDrawable);
      break label159;
      label323:
      bool2 = false;
      break label176;
      label328:
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
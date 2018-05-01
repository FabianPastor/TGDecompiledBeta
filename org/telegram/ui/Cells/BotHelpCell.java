package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanNoUnderline;

public class BotHelpCell
  extends View
{
  private BotHelpCellDelegate delegate;
  private int height;
  private String oldText;
  private ClickableSpan pressedLink;
  private StaticLayout textLayout;
  private int textX;
  private int textY;
  private LinkPath urlPath = new LinkPath();
  private int width;
  
  public BotHelpCell(Context paramContext)
  {
    super(paramContext);
  }
  
  private void resetPressedLink()
  {
    if (this.pressedLink != null) {
      this.pressedLink = null;
    }
    invalidate();
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    int i = (paramCanvas.getWidth() - this.width) / 2;
    int j = AndroidUtilities.dp(4.0F);
    Theme.chat_msgInMediaShadowDrawable.setBounds(i, j, this.width + i, this.height + j);
    Theme.chat_msgInMediaShadowDrawable.draw(paramCanvas);
    Theme.chat_msgInMediaDrawable.setBounds(i, j, this.width + i, this.height + j);
    Theme.chat_msgInMediaDrawable.draw(paramCanvas);
    Theme.chat_msgTextPaint.setColor(Theme.getColor("chat_messageTextIn"));
    Theme.chat_msgTextPaint.linkColor = Theme.getColor("chat_messageLinkIn");
    paramCanvas.save();
    i = AndroidUtilities.dp(11.0F) + i;
    this.textX = i;
    float f = i;
    j = AndroidUtilities.dp(11.0F) + j;
    this.textY = j;
    paramCanvas.translate(f, j);
    if (this.pressedLink != null) {
      paramCanvas.drawPath(this.urlPath, Theme.chat_urlPaint);
    }
    if (this.textLayout != null) {
      this.textLayout.draw(paramCanvas);
    }
    paramCanvas.restore();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), this.height + AndroidUtilities.dp(8.0F));
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    int i = 0;
    int j = 0;
    int k = j;
    if (this.textLayout != null)
    {
      if ((paramMotionEvent.getAction() != 0) && ((this.pressedLink == null) || (paramMotionEvent.getAction() != 1))) {
        break label513;
      }
      if (paramMotionEvent.getAction() == 0)
      {
        resetPressedLink();
        k = i;
      }
    }
    else
    {
      for (;;)
      {
        try
        {
          int m = (int)(f1 - this.textX);
          k = i;
          int n = (int)(f2 - this.textY);
          k = i;
          int i1 = this.textLayout.getLineForVertical(n);
          k = i;
          n = this.textLayout.getOffsetForHorizontal(i1, m);
          k = i;
          f1 = this.textLayout.getLineLeft(i1);
          if (f1 > m) {
            continue;
          }
          k = i;
          if (this.textLayout.getLineWidth(i1) + f1 < m) {
            continue;
          }
          k = i;
          localSpannable = (Spannable)this.textLayout.getText();
          k = i;
          ClickableSpan[] arrayOfClickableSpan = (ClickableSpan[])localSpannable.getSpans(n, n, ClickableSpan.class);
          k = i;
          if (arrayOfClickableSpan.length == 0) {
            continue;
          }
          k = i;
          resetPressedLink();
          k = i;
          this.pressedLink = arrayOfClickableSpan[0];
          k = 1;
          j = 1;
        }
        catch (Exception localException2)
        {
          Spannable localSpannable;
          resetPressedLink();
          FileLog.e(localException2);
          continue;
          k = i;
          resetPressedLink();
          k = j;
          continue;
          k = i;
          resetPressedLink();
          k = j;
          continue;
        }
        try
        {
          i = localSpannable.getSpanStart(this.pressedLink);
          this.urlPath.setCurrentLayout(this.textLayout, i, 0.0F);
          this.textLayout.getSelectionPath(i, localSpannable.getSpanEnd(this.pressedLink), this.urlPath);
          k = j;
        }
        catch (Exception localException1)
        {
          FileLog.e(localException1);
          k = j;
        }
      }
      if ((k == 0) && (!super.onTouchEvent(paramMotionEvent))) {
        break label536;
      }
    }
    label513:
    label536:
    for (boolean bool = true;; bool = false)
    {
      return bool;
      k = j;
      if (this.pressedLink == null) {
        break;
      }
      for (;;)
      {
        try
        {
          if (!(this.pressedLink instanceof URLSpanNoUnderline)) {
            continue;
          }
          String str = ((URLSpanNoUnderline)this.pressedLink).getURL();
          if (((str.startsWith("@")) || (str.startsWith("#")) || (str.startsWith("/"))) && (this.delegate != null)) {
            this.delegate.didPressUrl(str);
          }
        }
        catch (Exception localException3)
        {
          FileLog.e(localException3);
          continue;
          this.pressedLink.onClick(this);
          continue;
        }
        resetPressedLink();
        k = 1;
        break;
        if (!(this.pressedLink instanceof URLSpan)) {
          continue;
        }
        Browser.openUrl(getContext(), ((URLSpan)this.pressedLink).getURL());
      }
      k = j;
      if (paramMotionEvent.getAction() != 3) {
        break;
      }
      resetPressedLink();
      k = j;
      break;
    }
  }
  
  public void setDelegate(BotHelpCellDelegate paramBotHelpCellDelegate)
  {
    this.delegate = paramBotHelpCellDelegate;
  }
  
  public void setText(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      setVisibility(8);
    }
    for (;;)
    {
      return;
      int i;
      Object localObject;
      int j;
      if ((paramString == null) || (this.oldText == null) || (!paramString.equals(this.oldText)))
      {
        this.oldText = paramString;
        setVisibility(0);
        if (AndroidUtilities.isTablet()) {}
        String str;
        for (i = (int)(AndroidUtilities.getMinTabletSide() * 0.7F);; i = (int)(Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.7F))
        {
          localObject = paramString.split("\n");
          paramString = new SpannableStringBuilder();
          str = LocaleController.getString("BotInfoTitle", NUM);
          paramString.append(str);
          paramString.append("\n\n");
          for (j = 0; j < localObject.length; j++)
          {
            paramString.append(localObject[j].trim());
            if (j != localObject.length - 1) {
              paramString.append("\n");
            }
          }
        }
        MessageObject.addLinks(false, paramString);
        paramString.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0, str.length(), 33);
        Emoji.replaceEmoji(paramString, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
      }
      try
      {
        localObject = new android/text/StaticLayout;
        ((StaticLayout)localObject).<init>(paramString, Theme.chat_msgTextPaint, i, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        this.textLayout = ((StaticLayout)localObject);
        this.width = 0;
        this.height = (this.textLayout.getHeight() + AndroidUtilities.dp(22.0F));
        int k = this.textLayout.getLineCount();
        for (j = 0; j < k; j++) {
          this.width = ((int)Math.ceil(Math.max(this.width, this.textLayout.getLineWidth(j) + this.textLayout.getLineLeft(j))));
        }
        if (this.width > i) {
          this.width = i;
        }
      }
      catch (Exception paramString)
      {
        for (;;)
        {
          FileLog.e(paramString);
        }
      }
      this.width += AndroidUtilities.dp(22.0F);
    }
  }
  
  public static abstract interface BotHelpCellDelegate
  {
    public abstract void didPressUrl(String paramString);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/BotHelpCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
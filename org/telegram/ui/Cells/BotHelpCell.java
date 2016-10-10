package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
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
  private TextPaint textPaint = new TextPaint(1);
  private int textX;
  private int textY;
  private Paint urlPaint;
  private LinkPath urlPath = new LinkPath();
  private int width;
  
  public BotHelpCell(Context paramContext)
  {
    super(paramContext);
    this.textPaint.setTextSize(AndroidUtilities.dp(16.0F));
    this.textPaint.setColor(-16777216);
    this.textPaint.linkColor = -14255946;
    this.urlPaint = new Paint();
    this.urlPaint.setColor(862104035);
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
    int j = (paramCanvas.getWidth() - this.width) / 2;
    int i = AndroidUtilities.dp(4.0F);
    Theme.backgroundMediaDrawableIn.setBounds(j, i, this.width + j, this.height + i);
    Theme.backgroundMediaDrawableIn.draw(paramCanvas);
    paramCanvas.save();
    j = AndroidUtilities.dp(11.0F) + j;
    this.textX = j;
    float f = j;
    i = AndroidUtilities.dp(11.0F) + i;
    this.textY = i;
    paramCanvas.translate(f, i);
    if (this.pressedLink != null) {
      paramCanvas.drawPath(this.urlPath, this.urlPaint);
    }
    if (this.textLayout != null) {
      this.textLayout.draw(paramCanvas);
    }
    paramCanvas.restore();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), this.height + AndroidUtilities.dp(8.0F));
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    int k = 0;
    int j = 0;
    int i = j;
    if (this.textLayout != null)
    {
      if ((paramMotionEvent.getAction() != 0) && ((this.pressedLink == null) || (paramMotionEvent.getAction() != 1))) {
        break label515;
      }
      if (paramMotionEvent.getAction() != 0) {
        break label371;
      }
      resetPressedLink();
      i = k;
    }
    for (;;)
    {
      try
      {
        int m = (int)(f1 - this.textX);
        i = k;
        int n = (int)(f2 - this.textY);
        i = k;
        n = this.textLayout.getLineForVertical(n);
        i = k;
        int i1 = this.textLayout.getOffsetForHorizontal(n, m);
        i = k;
        f1 = this.textLayout.getLineLeft(n);
        if (f1 > m) {
          continue;
        }
        i = k;
        if (this.textLayout.getLineWidth(n) + f1 < m) {
          continue;
        }
        i = k;
        localSpannable = (Spannable)this.textLayout.getText();
        i = k;
        ClickableSpan[] arrayOfClickableSpan = (ClickableSpan[])localSpannable.getSpans(i1, i1, ClickableSpan.class);
        i = k;
        if (arrayOfClickableSpan.length == 0) {
          continue;
        }
        i = k;
        resetPressedLink();
        i = k;
        this.pressedLink = arrayOfClickableSpan[0];
        i = 1;
        j = 1;
      }
      catch (Exception localException2)
      {
        Spannable localSpannable;
        resetPressedLink();
        FileLog.e("tmessages", localException2);
        continue;
        i = k;
        resetPressedLink();
        i = j;
        continue;
        i = k;
        resetPressedLink();
        i = j;
        continue;
      }
      try
      {
        k = localSpannable.getSpanStart(this.pressedLink);
        this.urlPath.setCurrentLayout(this.textLayout, k, 0.0F);
        this.textLayout.getSelectionPath(k, localSpannable.getSpanEnd(this.pressedLink), this.urlPath);
        i = j;
      }
      catch (Exception localException1)
      {
        FileLog.e("tmessages", localException1);
        i = j;
        continue;
      }
      if ((i == 0) && (!super.onTouchEvent(paramMotionEvent))) {
        break;
      }
      return true;
      label371:
      i = j;
      if (this.pressedLink != null)
      {
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
            FileLog.e("tmessages", localException3);
            continue;
            this.pressedLink.onClick(this);
            continue;
          }
          resetPressedLink();
          i = 1;
          break;
          if (!(this.pressedLink instanceof URLSpan)) {
            continue;
          }
          Browser.openUrl(getContext(), ((URLSpan)this.pressedLink).getURL());
        }
        label515:
        i = j;
        if (paramMotionEvent.getAction() == 3)
        {
          resetPressedLink();
          i = j;
        }
      }
    }
    return false;
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
    while ((paramString != null) && (this.oldText != null) && (paramString.equals(this.oldText))) {
      return;
    }
    this.oldText = paramString;
    setVisibility(0);
    if (AndroidUtilities.isTablet()) {
      this.width = ((int)(AndroidUtilities.getMinTabletSide() * 0.7F));
    }
    for (;;)
    {
      SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder();
      String str = LocaleController.getString("BotInfoTitle", 2131165368);
      localSpannableStringBuilder.append(str);
      localSpannableStringBuilder.append("\n\n");
      localSpannableStringBuilder.append(paramString);
      MessageObject.addLinks(localSpannableStringBuilder);
      localSpannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0, str.length(), 33);
      Emoji.replaceEmoji(localSpannableStringBuilder, this.textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
      try
      {
        this.textLayout = new StaticLayout(localSpannableStringBuilder, this.textPaint, this.width, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        this.width = 0;
        this.height = (this.textLayout.getHeight() + AndroidUtilities.dp(22.0F));
        int j = this.textLayout.getLineCount();
        int i = 0;
        while (i < j)
        {
          this.width = ((int)Math.ceil(Math.max(this.width, this.textLayout.getLineWidth(i) + this.textLayout.getLineLeft(i))));
          i += 1;
          continue;
          this.width = ((int)(Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.7F));
        }
      }
      catch (Exception paramString)
      {
        FileLog.e("tmessage", paramString);
        this.width += AndroidUtilities.dp(22.0F);
      }
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
package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.URLSpanNoUnderline;

public class AboutLinkCell
  extends FrameLayout
{
  private AboutLinkCellDelegate delegate;
  private ImageView imageView;
  private String oldText;
  private ClickableSpan pressedLink;
  private SpannableStringBuilder stringBuilder;
  private StaticLayout textLayout;
  private TextPaint textPaint = new TextPaint(1);
  private int textX;
  private int textY;
  private Paint urlPaint;
  private LinkPath urlPath = new LinkPath();
  
  public AboutLinkCell(Context paramContext)
  {
    super(paramContext);
    this.textPaint.setTextSize(AndroidUtilities.dp(16.0F));
    this.textPaint.setColor(-16777216);
    this.textPaint.linkColor = -14255946;
    this.urlPaint = new Paint();
    this.urlPaint.setColor(862104035);
    this.imageView = new ImageView(paramContext);
    this.imageView.setScaleType(ImageView.ScaleType.CENTER);
    paramContext = this.imageView;
    int i;
    float f1;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL) {
        break label166;
      }
      f1 = 0.0F;
      label126:
      if (!LocaleController.isRTL) {
        break label172;
      }
    }
    for (;;)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f1, 5.0F, f2, 0.0F));
      setWillNotDraw(false);
      return;
      i = 3;
      break;
      label166:
      f1 = 16.0F;
      break label126;
      label172:
      f2 = 0.0F;
    }
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
    paramCanvas.save();
    float f;
    if (LocaleController.isRTL) {
      f = 16.0F;
    }
    for (;;)
    {
      int i = AndroidUtilities.dp(f);
      this.textX = i;
      f = i;
      i = AndroidUtilities.dp(8.0F);
      this.textY = i;
      paramCanvas.translate(f, i);
      if (this.pressedLink != null) {
        paramCanvas.drawPath(this.urlPath, this.urlPaint);
      }
      try
      {
        if (this.textLayout != null) {
          this.textLayout.draw(paramCanvas);
        }
        paramCanvas.restore();
        return;
        f = 71.0F;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException);
        }
      }
    }
  }
  
  @SuppressLint({"DrawAllocation"})
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    this.textLayout = new StaticLayout(this.stringBuilder, this.textPaint, View.MeasureSpec.getSize(paramInt1) - AndroidUtilities.dp(87.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(this.textLayout.getHeight() + AndroidUtilities.dp(16.0F), 1073741824));
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    if ((Build.VERSION.SDK_INT >= 21) && (getBackground() != null) && ((paramMotionEvent.getAction() == 0) || (paramMotionEvent.getAction() == 2))) {
      getBackground().setHotspot(f1, f2);
    }
    int k = 0;
    int j = 0;
    int i = j;
    if (this.textLayout != null)
    {
      if ((paramMotionEvent.getAction() != 0) && ((this.pressedLink == null) || (paramMotionEvent.getAction() != 1))) {
        break label557;
      }
      if (paramMotionEvent.getAction() != 0) {
        break label410;
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
      label410:
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
        label557:
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
  
  public void setDelegate(AboutLinkCellDelegate paramAboutLinkCellDelegate)
  {
    this.delegate = paramAboutLinkCellDelegate;
  }
  
  public void setTextAndIcon(String paramString, int paramInt)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      setVisibility(8);
    }
    while ((paramString != null) && (this.oldText != null) && (paramString.equals(this.oldText))) {
      return;
    }
    this.oldText = paramString;
    this.stringBuilder = new SpannableStringBuilder(this.oldText);
    MessageObject.addLinks(this.stringBuilder, false);
    Emoji.replaceEmoji(this.stringBuilder, this.textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
    requestLayout();
    if (paramInt == 0)
    {
      this.imageView.setImageDrawable(null);
      return;
    }
    this.imageView.setImageResource(paramInt);
  }
  
  public static abstract interface AboutLinkCellDelegate
  {
    public abstract void didPressUrl(String paramString);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/AboutLinkCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;

public class ManageChatTextCell
  extends FrameLayout
{
  private boolean divider;
  private ImageView imageView;
  private SimpleTextView textView;
  private SimpleTextView valueTextView;
  
  public ManageChatTextCell(Context paramContext)
  {
    super(paramContext);
    this.textView = new SimpleTextView(paramContext);
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.textView.setTextSize(16);
    SimpleTextView localSimpleTextView = this.textView;
    if (LocaleController.isRTL)
    {
      j = 5;
      localSimpleTextView.setGravity(j);
      addView(this.textView);
      this.valueTextView = new SimpleTextView(paramContext);
      this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteValueText"));
      this.valueTextView.setTextSize(16);
      localSimpleTextView = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label188;
      }
    }
    label188:
    for (int j = i;; j = 5)
    {
      localSimpleTextView.setGravity(j);
      addView(this.valueTextView);
      this.imageView = new ImageView(paramContext);
      this.imageView.setScaleType(ImageView.ScaleType.CENTER);
      this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
      addView(this.imageView);
      return;
      j = 3;
      break;
    }
  }
  
  public SimpleTextView getTextView()
  {
    return this.textView;
  }
  
  public SimpleTextView getValueTextView()
  {
    return this.valueTextView;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.divider) {
      paramCanvas.drawLine(AndroidUtilities.dp(71.0F), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = paramInt4 - paramInt2;
    paramInt4 = (i - this.valueTextView.getTextHeight()) / 2;
    if (LocaleController.isRTL)
    {
      paramInt2 = AndroidUtilities.dp(24.0F);
      this.valueTextView.layout(paramInt2, paramInt4, this.valueTextView.getMeasuredWidth() + paramInt2, this.valueTextView.getMeasuredHeight() + paramInt4);
      paramInt4 = (i - this.textView.getTextHeight()) / 2;
      if (LocaleController.isRTL) {
        break label167;
      }
      paramInt2 = AndroidUtilities.dp(71.0F);
      label87:
      this.textView.layout(paramInt2, paramInt4, this.textView.getMeasuredWidth() + paramInt2, this.textView.getMeasuredHeight() + paramInt4);
      paramInt2 = AndroidUtilities.dp(9.0F);
      if (LocaleController.isRTL) {
        break label176;
      }
    }
    label167:
    label176:
    for (paramInt1 = AndroidUtilities.dp(16.0F);; paramInt1 = paramInt3 - paramInt1 - this.imageView.getMeasuredWidth() - AndroidUtilities.dp(16.0F))
    {
      this.imageView.layout(paramInt1, paramInt2, this.imageView.getMeasuredWidth() + paramInt1, this.imageView.getMeasuredHeight() + paramInt2);
      return;
      paramInt2 = 0;
      break;
      paramInt2 = AndroidUtilities.dp(24.0F);
      break label87;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = View.MeasureSpec.getSize(paramInt1);
    paramInt1 = AndroidUtilities.dp(48.0F);
    this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(paramInt2 - AndroidUtilities.dp(24.0F), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0F), NUM));
    this.textView.measure(View.MeasureSpec.makeMeasureSpec(paramInt2 - AndroidUtilities.dp(95.0F), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0F), NUM));
    this.imageView.measure(View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(paramInt1, Integer.MIN_VALUE));
    int i = AndroidUtilities.dp(56.0F);
    if (this.divider) {}
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      setMeasuredDimension(paramInt2, paramInt1 + i);
      return;
    }
  }
  
  public void setText(String paramString1, String paramString2, int paramInt, boolean paramBoolean)
  {
    boolean bool = false;
    this.textView.setText(paramString1);
    if (paramString2 != null)
    {
      this.valueTextView.setText(paramString2);
      this.valueTextView.setVisibility(0);
    }
    for (;;)
    {
      this.imageView.setPadding(0, AndroidUtilities.dp(7.0F), 0, 0);
      this.imageView.setImageResource(paramInt);
      this.divider = paramBoolean;
      paramBoolean = bool;
      if (!this.divider) {
        paramBoolean = true;
      }
      setWillNotDraw(paramBoolean);
      return;
      this.valueTextView.setVisibility(4);
    }
  }
  
  public void setTextColor(int paramInt)
  {
    this.textView.setTextColor(paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/ManageChatTextCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
package org.telegram.ui.Cells;

import android.content.Context;
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

public class InviteTextCell
  extends FrameLayout
{
  private ImageView imageView;
  private SimpleTextView textView;
  
  public InviteTextCell(Context paramContext)
  {
    super(paramContext);
    this.textView = new SimpleTextView(paramContext);
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.textView.setTextSize(17);
    SimpleTextView localSimpleTextView = this.textView;
    if (LocaleController.isRTL) {}
    for (int i = 5;; i = 3)
    {
      localSimpleTextView.setGravity(i);
      addView(this.textView);
      this.imageView = new ImageView(paramContext);
      this.imageView.setScaleType(ImageView.ScaleType.CENTER);
      this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
      addView(this.imageView);
      return;
    }
  }
  
  public SimpleTextView getTextView()
  {
    return this.textView;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt4 -= paramInt2;
    int i = (paramInt4 - this.textView.getTextHeight()) / 2;
    if (!LocaleController.isRTL)
    {
      paramInt2 = AndroidUtilities.dp(71.0F);
      this.textView.layout(paramInt2, i, this.textView.getMeasuredWidth() + paramInt2, this.textView.getMeasuredHeight() + i);
      paramInt2 = (paramInt4 - this.imageView.getMeasuredHeight()) / 2;
      if (LocaleController.isRTL) {
        break label123;
      }
    }
    label123:
    for (paramInt1 = AndroidUtilities.dp(20.0F);; paramInt1 = paramInt3 - paramInt1 - this.imageView.getMeasuredWidth() - AndroidUtilities.dp(20.0F))
    {
      this.imageView.layout(paramInt1, paramInt2, this.imageView.getMeasuredWidth() + paramInt1, this.imageView.getMeasuredHeight() + paramInt2);
      return;
      paramInt2 = AndroidUtilities.dp(24.0F);
      break;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = View.MeasureSpec.getSize(paramInt1);
    paramInt1 = AndroidUtilities.dp(72.0F);
    this.textView.measure(View.MeasureSpec.makeMeasureSpec(paramInt2 - AndroidUtilities.dp(95.0F), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0F), NUM));
    this.imageView.measure(View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(paramInt1, Integer.MIN_VALUE));
    setMeasuredDimension(paramInt2, AndroidUtilities.dp(72.0F));
  }
  
  public void setTextAndIcon(String paramString, int paramInt)
  {
    this.textView.setText(paramString);
    this.imageView.setImageResource(paramInt);
  }
  
  public void setTextColor(int paramInt)
  {
    this.textView.setTextColor(paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/InviteTextCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
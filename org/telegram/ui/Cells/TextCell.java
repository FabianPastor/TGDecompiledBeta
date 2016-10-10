package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;

public class TextCell
  extends FrameLayout
{
  private ImageView imageView;
  private SimpleTextView textView;
  private ImageView valueImageView;
  private SimpleTextView valueTextView;
  
  public TextCell(Context paramContext)
  {
    super(paramContext);
    this.textView = new SimpleTextView(paramContext);
    this.textView.setTextColor(-14606047);
    this.textView.setTextSize(16);
    SimpleTextView localSimpleTextView = this.textView;
    if (LocaleController.isRTL)
    {
      i = 5;
      localSimpleTextView.setGravity(i);
      addView(this.textView);
      this.valueTextView = new SimpleTextView(paramContext);
      this.valueTextView.setTextColor(-13660983);
      this.valueTextView.setTextSize(16);
      localSimpleTextView = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label189;
      }
    }
    label189:
    for (int i = j;; i = 5)
    {
      localSimpleTextView.setGravity(i);
      addView(this.valueTextView);
      this.imageView = new ImageView(paramContext);
      this.imageView.setScaleType(ImageView.ScaleType.CENTER);
      addView(this.imageView);
      this.valueImageView = new ImageView(paramContext);
      this.valueImageView.setScaleType(ImageView.ScaleType.CENTER);
      addView(this.valueImageView);
      return;
      i = 3;
      break;
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt2 = paramInt4 - paramInt2;
    paramInt3 -= paramInt1;
    paramInt4 = (paramInt2 - this.valueTextView.getTextHeight()) / 2;
    if (LocaleController.isRTL)
    {
      paramInt1 = AndroidUtilities.dp(24.0F);
      this.valueTextView.layout(paramInt1, paramInt4, this.valueTextView.getMeasuredWidth() + paramInt1, this.valueTextView.getMeasuredHeight() + paramInt4);
      paramInt4 = (paramInt2 - this.textView.getTextHeight()) / 2;
      if (LocaleController.isRTL) {
        break label224;
      }
      paramInt1 = AndroidUtilities.dp(71.0F);
      label90:
      this.textView.layout(paramInt1, paramInt4, this.textView.getMeasuredWidth() + paramInt1, this.textView.getMeasuredHeight() + paramInt4);
      paramInt4 = AndroidUtilities.dp(5.0F);
      if (LocaleController.isRTL) {
        break label233;
      }
      paramInt1 = AndroidUtilities.dp(16.0F);
      label138:
      this.imageView.layout(paramInt1, paramInt4, this.imageView.getMeasuredWidth() + paramInt1, this.imageView.getMeasuredHeight() + paramInt4);
      paramInt2 = (paramInt2 - this.valueImageView.getMeasuredHeight()) / 2;
      if (!LocaleController.isRTL) {
        break label253;
      }
    }
    label224:
    label233:
    label253:
    for (paramInt1 = AndroidUtilities.dp(24.0F);; paramInt1 = paramInt3 - this.valueImageView.getMeasuredWidth() - AndroidUtilities.dp(24.0F))
    {
      this.valueImageView.layout(paramInt1, paramInt2, this.valueImageView.getMeasuredWidth() + paramInt1, this.valueImageView.getMeasuredHeight() + paramInt2);
      return;
      paramInt1 = 0;
      break;
      paramInt1 = AndroidUtilities.dp(24.0F);
      break label90;
      paramInt1 = paramInt3 - this.imageView.getMeasuredWidth() - AndroidUtilities.dp(16.0F);
      break label138;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt1 = View.MeasureSpec.getSize(paramInt1);
    paramInt2 = AndroidUtilities.dp(48.0F);
    this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1 - AndroidUtilities.dp(24.0F), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0F), 1073741824));
    this.textView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1 - AndroidUtilities.dp(95.0F), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0F), 1073741824));
    this.imageView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE));
    this.valueImageView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE));
    setMeasuredDimension(paramInt1, AndroidUtilities.dp(48.0F));
  }
  
  public void setText(String paramString)
  {
    this.textView.setText(paramString);
    this.valueTextView.setText(null);
    this.imageView.setVisibility(4);
    this.valueTextView.setVisibility(4);
    this.valueImageView.setVisibility(4);
  }
  
  public void setTextAndIcon(String paramString, int paramInt)
  {
    this.textView.setText(paramString);
    this.valueTextView.setText(null);
    this.imageView.setImageResource(paramInt);
    this.imageView.setVisibility(0);
    this.valueTextView.setVisibility(4);
    this.valueImageView.setVisibility(4);
    this.imageView.setPadding(0, AndroidUtilities.dp(7.0F), 0, 0);
  }
  
  public void setTextAndValue(String paramString1, String paramString2)
  {
    this.textView.setText(paramString1);
    this.valueTextView.setText(paramString2);
    this.valueTextView.setVisibility(0);
    this.imageView.setVisibility(4);
    this.valueImageView.setVisibility(4);
  }
  
  public void setTextAndValueAndIcon(String paramString1, String paramString2, int paramInt)
  {
    this.textView.setText(paramString1);
    this.valueTextView.setText(paramString2);
    this.valueTextView.setVisibility(0);
    this.valueImageView.setVisibility(4);
    this.imageView.setVisibility(0);
    this.imageView.setPadding(0, AndroidUtilities.dp(7.0F), 0, 0);
    this.imageView.setImageResource(paramInt);
  }
  
  public void setTextAndValueDrawable(String paramString, Drawable paramDrawable)
  {
    this.textView.setText(paramString);
    this.valueTextView.setText(null);
    this.valueImageView.setVisibility(0);
    this.valueImageView.setImageDrawable(paramDrawable);
    this.valueTextView.setVisibility(4);
    this.imageView.setVisibility(4);
    this.imageView.setPadding(0, AndroidUtilities.dp(7.0F), 0, 0);
  }
  
  public void setTextColor(int paramInt)
  {
    this.textView.setTextColor(paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/TextCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
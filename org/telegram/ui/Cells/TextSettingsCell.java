package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class TextSettingsCell
  extends FrameLayout
{
  private static Paint paint;
  private boolean needDivider;
  private TextView textView;
  private ImageView valueImageView;
  private TextView valueTextView;
  
  public TextSettingsCell(Context paramContext)
  {
    super(paramContext);
    if (paint == null)
    {
      paint = new Paint();
      paint.setColor(-2500135);
      paint.setStrokeWidth(1.0F);
    }
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(-14606047);
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    this.textView.setEllipsize(TextUtils.TruncateAt.END);
    TextView localTextView = this.textView;
    if (LocaleController.isRTL)
    {
      i = 5;
      localTextView.setGravity(i | 0x10);
      localTextView = this.textView;
      if (!LocaleController.isRTL) {
        break label358;
      }
      i = 5;
      label140:
      addView(localTextView, LayoutHelper.createFrame(-1, -1.0F, i | 0x30, 17.0F, 0.0F, 17.0F, 0.0F));
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextColor(-13660983);
      this.valueTextView.setTextSize(1, 16.0F);
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
      localTextView = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label363;
      }
      i = 3;
      label241:
      localTextView.setGravity(i | 0x10);
      localTextView = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label368;
      }
      i = 3;
      label264:
      addView(localTextView, LayoutHelper.createFrame(-2, -1.0F, i | 0x30, 17.0F, 0.0F, 17.0F, 0.0F));
      this.valueImageView = new ImageView(paramContext);
      this.valueImageView.setScaleType(ImageView.ScaleType.CENTER);
      this.valueImageView.setVisibility(4);
      paramContext = this.valueImageView;
      if (!LocaleController.isRTL) {
        break label373;
      }
    }
    label358:
    label363:
    label368:
    label373:
    for (int i = j;; i = 5)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, i | 0x10, 17.0F, 0.0F, 17.0F, 0.0F));
      return;
      i = 3;
      break;
      i = 3;
      break label140;
      i = 5;
      break label241;
      i = 5;
      break label264;
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider) {
      paramCanvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = View.MeasureSpec.getSize(paramInt1);
    int i = AndroidUtilities.dp(48.0F);
    if (this.needDivider)
    {
      paramInt1 = 1;
      setMeasuredDimension(paramInt2, paramInt1 + i);
      paramInt1 = getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - AndroidUtilities.dp(34.0F);
      paramInt2 = paramInt1 / 2;
      if (this.valueImageView.getVisibility() == 0) {
        this.valueImageView.measure(View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
      }
      if (this.valueTextView.getVisibility() != 0) {
        break label161;
      }
      this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
      paramInt1 = paramInt1 - this.valueTextView.getMeasuredWidth() - AndroidUtilities.dp(8.0F);
    }
    label161:
    for (;;)
    {
      this.textView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
      return;
      paramInt1 = 0;
      break;
    }
  }
  
  public void setText(String paramString, boolean paramBoolean)
  {
    this.textView.setText(paramString);
    this.valueTextView.setVisibility(4);
    this.valueImageView.setVisibility(4);
    this.needDivider = paramBoolean;
    if (!paramBoolean) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      setWillNotDraw(paramBoolean);
      return;
    }
  }
  
  public void setTextAndIcon(String paramString, int paramInt, boolean paramBoolean)
  {
    boolean bool = false;
    this.textView.setText(paramString);
    this.valueTextView.setVisibility(4);
    if (paramInt != 0)
    {
      this.valueImageView.setVisibility(0);
      this.valueImageView.setImageResource(paramInt);
    }
    for (;;)
    {
      this.needDivider = paramBoolean;
      if (!paramBoolean) {
        bool = true;
      }
      setWillNotDraw(bool);
      return;
      this.valueImageView.setVisibility(4);
    }
  }
  
  public void setTextAndValue(String paramString1, String paramString2, boolean paramBoolean)
  {
    boolean bool = false;
    this.textView.setText(paramString1);
    this.valueImageView.setVisibility(4);
    if (paramString2 != null)
    {
      this.valueTextView.setText(paramString2);
      this.valueTextView.setVisibility(0);
    }
    for (;;)
    {
      this.needDivider = paramBoolean;
      if (!paramBoolean) {
        bool = true;
      }
      setWillNotDraw(bool);
      requestLayout();
      return;
      this.valueTextView.setVisibility(4);
    }
  }
  
  public void setTextColor(int paramInt)
  {
    this.textView.setTextColor(paramInt);
  }
  
  public void setTextValueColor(int paramInt)
  {
    this.valueTextView.setTextColor(paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/TextSettingsCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;

public class CheckBoxCell
  extends FrameLayout
{
  private static Paint paint;
  private CheckBoxSquare checkBox;
  private boolean needDivider;
  private TextView textView;
  private TextView valueTextView;
  
  public CheckBoxCell(Context paramContext)
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
    int i;
    label145:
    label155:
    float f;
    if (LocaleController.isRTL)
    {
      i = 5;
      localTextView.setGravity(i | 0x10);
      localTextView = this.textView;
      if (!LocaleController.isRTL) {
        break label394;
      }
      i = 5;
      if (!LocaleController.isRTL) {
        break label399;
      }
      j = 17;
      f = j;
      if (!LocaleController.isRTL) {
        break label406;
      }
      j = 46;
      label169:
      addView(localTextView, LayoutHelper.createFrame(-1, -1.0F, i | 0x30, f, 0.0F, j, 0.0F));
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextColor(-13660983);
      this.valueTextView.setTextSize(1, 16.0F);
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
      localTextView = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label413;
      }
      i = 3;
      label270:
      localTextView.setGravity(i | 0x10);
      localTextView = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label418;
      }
      i = 3;
      label293:
      addView(localTextView, LayoutHelper.createFrame(-2, -1.0F, i | 0x30, 17.0F, 0.0F, 17.0F, 0.0F));
      this.checkBox = new CheckBoxSquare(paramContext);
      paramContext = this.checkBox;
      if (!LocaleController.isRTL) {
        break label423;
      }
      i = m;
      label342:
      if (!LocaleController.isRTL) {
        break label428;
      }
      j = 0;
      label351:
      f = j;
      if (!LocaleController.isRTL) {
        break label435;
      }
    }
    label394:
    label399:
    label406:
    label413:
    label418:
    label423:
    label428:
    label435:
    for (int j = k;; j = 0)
    {
      addView(paramContext, LayoutHelper.createFrame(18, 18.0F, i | 0x30, f, 15.0F, j, 0.0F));
      return;
      i = 3;
      break;
      i = 3;
      break label145;
      j = 46;
      break label155;
      j = 17;
      break label169;
      i = 5;
      break label270;
      i = 5;
      break label293;
      i = 3;
      break label342;
      j = 17;
      break label351;
    }
  }
  
  public boolean isChecked()
  {
    return this.checkBox.isChecked();
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
    if (this.needDivider) {}
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      setMeasuredDimension(paramInt2, paramInt1 + i);
      paramInt1 = getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - AndroidUtilities.dp(34.0F);
      this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1 / 2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
      this.textView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1 - this.valueTextView.getMeasuredWidth() - AndroidUtilities.dp(8.0F), 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
      this.checkBox.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(18.0F), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(18.0F), 1073741824));
      return;
    }
  }
  
  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.checkBox.setChecked(paramBoolean1, paramBoolean2);
  }
  
  public void setText(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = false;
    this.textView.setText(paramString1);
    this.checkBox.setChecked(paramBoolean1, false);
    this.valueTextView.setText(paramString2);
    this.needDivider = paramBoolean2;
    paramBoolean1 = bool;
    if (!paramBoolean2) {
      paramBoolean1 = true;
    }
    setWillNotDraw(paramBoolean1);
  }
  
  public void setTextColor(int paramInt)
  {
    this.textView.setTextColor(paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/CheckBoxCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
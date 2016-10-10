package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Switch;

public class TextCheckCell
  extends FrameLayout
{
  private static Paint paint;
  private Switch checkBox;
  private boolean isMultiline;
  private boolean needDivider;
  private TextView textView;
  private TextView valueTextView;
  
  public TextCheckCell(Context paramContext)
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
    TextView localTextView = this.textView;
    label141:
    float f1;
    label150:
    float f2;
    if (LocaleController.isRTL)
    {
      i = 5;
      localTextView.setGravity(i | 0x10);
      this.textView.setEllipsize(TextUtils.TruncateAt.END);
      localTextView = this.textView;
      if (!LocaleController.isRTL) {
        break label422;
      }
      i = 5;
      if (!LocaleController.isRTL) {
        break label428;
      }
      f1 = 64.0F;
      if (!LocaleController.isRTL) {
        break label434;
      }
      f2 = 17.0F;
      label159:
      addView(localTextView, LayoutHelper.createFrame(-1, -1.0F, i | 0x30, f1, 0.0F, f2, 0.0F));
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextColor(-7697782);
      this.valueTextView.setTextSize(1, 13.0F);
      localTextView = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label440;
      }
      i = 5;
      label226:
      localTextView.setGravity(i);
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      this.valueTextView.setPadding(0, 0, 0, 0);
      this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
      localTextView = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label446;
      }
      i = 5;
      label293:
      if (!LocaleController.isRTL) {
        break label452;
      }
      f1 = 64.0F;
      label302:
      if (!LocaleController.isRTL) {
        break label458;
      }
      f2 = 17.0F;
      label311:
      addView(localTextView, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f1, 35.0F, f2, 0.0F));
      this.checkBox = new Switch(paramContext);
      this.checkBox.setDuplicateParentStateEnabled(false);
      this.checkBox.setFocusable(false);
      this.checkBox.setFocusableInTouchMode(false);
      this.checkBox.setClickable(false);
      paramContext = this.checkBox;
      if (!LocaleController.isRTL) {
        break label464;
      }
    }
    label422:
    label428:
    label434:
    label440:
    label446:
    label452:
    label458:
    label464:
    for (int i = 3;; i = 5)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, i | 0x10, 14.0F, 0.0F, 14.0F, 0.0F));
      return;
      i = 3;
      break;
      i = 3;
      break label141;
      f1 = 17.0F;
      break label150;
      f2 = 64.0F;
      break label159;
      i = 3;
      break label226;
      i = 3;
      break label293;
      f1 = 17.0F;
      break label302;
      f2 = 64.0F;
      break label311;
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
    if (this.isMultiline)
    {
      super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(0, 0));
      return;
    }
    float f;
    int i;
    if (this.valueTextView.getVisibility() == 0)
    {
      f = 64.0F;
      i = AndroidUtilities.dp(f);
      if (!this.needDivider) {
        break label67;
      }
    }
    label67:
    for (paramInt2 = 1;; paramInt2 = 0)
    {
      super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(paramInt2 + i, 1073741824));
      return;
      f = 48.0F;
      break;
    }
  }
  
  public void setChecked(boolean paramBoolean)
  {
    this.checkBox.setChecked(paramBoolean);
  }
  
  public void setTextAndCheck(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = false;
    this.textView.setText(paramString);
    this.isMultiline = false;
    this.checkBox.setChecked(paramBoolean1);
    this.needDivider = paramBoolean2;
    this.valueTextView.setVisibility(8);
    paramString = (FrameLayout.LayoutParams)this.textView.getLayoutParams();
    paramString.height = -1;
    paramString.topMargin = 0;
    this.textView.setLayoutParams(paramString);
    paramBoolean1 = bool;
    if (!paramBoolean2) {
      paramBoolean1 = true;
    }
    setWillNotDraw(paramBoolean1);
  }
  
  public void setTextAndValueAndCheck(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    boolean bool = true;
    this.textView.setText(paramString1);
    this.valueTextView.setText(paramString2);
    this.checkBox.setChecked(paramBoolean1);
    this.needDivider = paramBoolean3;
    this.valueTextView.setVisibility(0);
    this.isMultiline = paramBoolean2;
    if (paramBoolean2)
    {
      this.valueTextView.setLines(0);
      this.valueTextView.setMaxLines(0);
      this.valueTextView.setSingleLine(false);
      this.valueTextView.setEllipsize(null);
      this.valueTextView.setPadding(0, 0, 0, AndroidUtilities.dp(11.0F));
      paramString1 = (FrameLayout.LayoutParams)this.textView.getLayoutParams();
      paramString1.height = -2;
      paramString1.topMargin = AndroidUtilities.dp(10.0F);
      this.textView.setLayoutParams(paramString1);
      if (paramBoolean3) {
        break label195;
      }
    }
    label195:
    for (paramBoolean1 = bool;; paramBoolean1 = false)
    {
      setWillNotDraw(paramBoolean1);
      return;
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
      this.valueTextView.setPadding(0, 0, 0, 0);
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/TextCheckCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
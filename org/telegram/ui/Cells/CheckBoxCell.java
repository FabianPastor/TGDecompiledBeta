package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;

public class CheckBoxCell
  extends FrameLayout
{
  private CheckBoxSquare checkBox;
  private boolean needDivider;
  private TextView textView;
  private TextView valueTextView;
  
  public CheckBoxCell(Context paramContext, int paramInt)
  {
    super(paramContext);
    this.textView = new TextView(paramContext);
    TextView localTextView = this.textView;
    Object localObject;
    int k;
    label106:
    label136:
    int m;
    label145:
    float f;
    label160:
    label211:
    label280:
    label305:
    boolean bool;
    if (paramInt == 1)
    {
      localObject = "dialogTextBlack";
      localTextView.setTextColor(Theme.getColor((String)localObject));
      this.textView.setTextSize(1, 16.0F);
      this.textView.setLines(1);
      this.textView.setMaxLines(1);
      this.textView.setSingleLine(true);
      this.textView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.textView;
      if (!LocaleController.isRTL) {
        break label397;
      }
      k = 5;
      ((TextView)localObject).setGravity(k | 0x10);
      if (paramInt != 2) {
        break label422;
      }
      localObject = this.textView;
      if (!LocaleController.isRTL) {
        break label403;
      }
      k = 5;
      if (!LocaleController.isRTL) {
        break label409;
      }
      m = 0;
      f = m;
      if (!LocaleController.isRTL) {
        break label416;
      }
      m = 29;
      addView((View)localObject, LayoutHelper.createFrame(-1, -1.0F, k | 0x30, f, 0.0F, m, 0.0F));
      this.valueTextView = new TextView(paramContext);
      localTextView = this.valueTextView;
      if (paramInt != 1) {
        break label509;
      }
      localObject = "dialogTextBlue";
      localTextView.setTextColor(Theme.getColor((String)localObject));
      this.valueTextView.setTextSize(1, 16.0F);
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label516;
      }
      k = 3;
      ((TextView)localObject).setGravity(k | 0x10);
      localObject = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label522;
      }
      k = 3;
      addView((View)localObject, LayoutHelper.createFrame(-2, -1.0F, k | 0x30, 17.0F, 0.0F, 17.0F, 0.0F));
      if (paramInt != 1) {
        break label528;
      }
      bool = true;
      label337:
      this.checkBox = new CheckBoxSquare(paramContext, bool);
      if (paramInt != 2) {
        break label540;
      }
      paramContext = this.checkBox;
      if (!LocaleController.isRTL) {
        break label534;
      }
    }
    for (;;)
    {
      addView(paramContext, LayoutHelper.createFrame(18, 18.0F, j | 0x30, 0.0F, 15.0F, 0.0F, 0.0F));
      return;
      localObject = "windowBackgroundWhiteBlackText";
      break;
      label397:
      k = 3;
      break label106;
      label403:
      k = 3;
      break label136;
      label409:
      m = 29;
      break label145;
      label416:
      m = 0;
      break label160;
      label422:
      localObject = this.textView;
      if (LocaleController.isRTL)
      {
        k = 5;
        label437:
        if (!LocaleController.isRTL) {
          break label495;
        }
        m = 17;
        label447:
        f = m;
        if (!LocaleController.isRTL) {
          break label502;
        }
      }
      label495:
      label502:
      for (m = 46;; m = 17)
      {
        addView((View)localObject, LayoutHelper.createFrame(-1, -1.0F, k | 0x30, f, 0.0F, m, 0.0F));
        break;
        k = 3;
        break label437;
        m = 46;
        break label447;
      }
      label509:
      localObject = "windowBackgroundWhiteValueText";
      break label211;
      label516:
      k = 5;
      break label280;
      label522:
      k = 5;
      break label305;
      label528:
      bool = false;
      break label337;
      label534:
      j = 3;
    }
    label540:
    paramContext = this.checkBox;
    if (LocaleController.isRTL) {
      label551:
      if (!LocaleController.isRTL) {
        break label607;
      }
    }
    label607:
    for (paramInt = 0;; paramInt = 17)
    {
      f = paramInt;
      paramInt = i;
      if (LocaleController.isRTL) {
        paramInt = 17;
      }
      addView(paramContext, LayoutHelper.createFrame(18, 18.0F, j | 0x30, f, 15.0F, paramInt, 0.0F));
      break;
      j = 3;
      break label551;
    }
  }
  
  public CheckBoxSquare getCheckBox()
  {
    return this.checkBox;
  }
  
  public TextView getTextView()
  {
    return this.textView;
  }
  
  public TextView getValueTextView()
  {
    return this.valueTextView;
  }
  
  public boolean isChecked()
  {
    return this.checkBox.isChecked();
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider) {
      paramCanvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.dividerPaint);
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
      this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1 / 2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
      this.textView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1 - this.valueTextView.getMeasuredWidth() - AndroidUtilities.dp(8.0F), NUM), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
      this.checkBox.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(18.0F), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(18.0F), NUM));
      return;
    }
  }
  
  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.checkBox.setChecked(paramBoolean1, paramBoolean2);
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    float f1 = 1.0F;
    super.setEnabled(paramBoolean);
    Object localObject = this.textView;
    if (paramBoolean)
    {
      f2 = 1.0F;
      ((TextView)localObject).setAlpha(f2);
      localObject = this.valueTextView;
      if (!paramBoolean) {
        break label69;
      }
      f2 = 1.0F;
      label37:
      ((TextView)localObject).setAlpha(f2);
      localObject = this.checkBox;
      if (!paramBoolean) {
        break label76;
      }
    }
    label69:
    label76:
    for (float f2 = f1;; f2 = 0.5F)
    {
      ((CheckBoxSquare)localObject).setAlpha(f2);
      return;
      f2 = 0.5F;
      break;
      f2 = 0.5F;
      break label37;
    }
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
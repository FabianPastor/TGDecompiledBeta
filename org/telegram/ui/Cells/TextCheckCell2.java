package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Switch2;

public class TextCheckCell2
  extends FrameLayout
{
  private Switch2 checkBox;
  private boolean isMultiline;
  private boolean needDivider;
  private TextView textView;
  private TextView valueTextView;
  
  public TextCheckCell2(Context paramContext)
  {
    super(paramContext);
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    TextView localTextView = this.textView;
    label118:
    float f2;
    label128:
    float f3;
    if (LocaleController.isRTL)
    {
      j = 5;
      localTextView.setGravity(j | 0x10);
      this.textView.setEllipsize(TextUtils.TruncateAt.END);
      localTextView = this.textView;
      if (!LocaleController.isRTL) {
        break label377;
      }
      j = 5;
      if (!LocaleController.isRTL) {
        break label383;
      }
      f2 = 64.0F;
      if (!LocaleController.isRTL) {
        break label390;
      }
      f3 = 17.0F;
      label138:
      addView(localTextView, LayoutHelper.createFrame(-1, -1.0F, j | 0x30, f2, 0.0F, f3, 0.0F));
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
      this.valueTextView.setTextSize(1, 13.0F);
      localTextView = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label397;
      }
      j = 5;
      label210:
      localTextView.setGravity(j);
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      this.valueTextView.setPadding(0, 0, 0, 0);
      this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
      localTextView = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label403;
      }
      j = 5;
      label277:
      if (!LocaleController.isRTL) {
        break label409;
      }
      f2 = 64.0F;
      label287:
      if (!LocaleController.isRTL) {
        break label416;
      }
      f3 = f1;
      label296:
      addView(localTextView, LayoutHelper.createFrame(-2, -2.0F, j | 0x30, f2, 35.0F, f3, 0.0F));
      this.checkBox = new Switch2(paramContext);
      paramContext = this.checkBox;
      if (!LocaleController.isRTL) {
        break label423;
      }
    }
    label377:
    label383:
    label390:
    label397:
    label403:
    label409:
    label416:
    label423:
    for (int j = i;; j = 5)
    {
      addView(paramContext, LayoutHelper.createFrame(44, 40.0F, j | 0x10, 14.0F, 0.0F, 14.0F, 0.0F));
      return;
      j = 3;
      break;
      j = 3;
      break label118;
      f2 = 17.0F;
      break label128;
      f3 = 64.0F;
      break label138;
      j = 3;
      break label210;
      j = 3;
      break label277;
      f2 = 17.0F;
      break label287;
      f3 = 64.0F;
      break label296;
    }
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
    if (this.isMultiline)
    {
      super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
      return;
    }
    paramInt2 = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM);
    float f;
    label49:
    int i;
    if (this.valueTextView.getVisibility() == 0)
    {
      f = 64.0F;
      i = AndroidUtilities.dp(f);
      if (!this.needDivider) {
        break label87;
      }
    }
    label87:
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      super.onMeasure(paramInt2, View.MeasureSpec.makeMeasureSpec(paramInt1 + i, NUM));
      break;
      f = 48.0F;
      break label49;
    }
  }
  
  public void setChecked(boolean paramBoolean)
  {
    this.checkBox.setChecked(paramBoolean, true);
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    super.setEnabled(paramBoolean);
    if (paramBoolean)
    {
      this.textView.setAlpha(1.0F);
      this.valueTextView.setAlpha(1.0F);
      this.checkBox.setAlpha(1.0F);
    }
    for (;;)
    {
      return;
      this.checkBox.setAlpha(0.5F);
      this.textView.setAlpha(0.5F);
      this.valueTextView.setAlpha(0.5F);
    }
  }
  
  public void setTextAndCheck(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = false;
    this.textView.setText(paramString);
    this.isMultiline = false;
    this.checkBox.setChecked(paramBoolean1, false);
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
    this.checkBox.setChecked(paramBoolean1, false);
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
        break label196;
      }
    }
    label196:
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/TextCheckCell2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
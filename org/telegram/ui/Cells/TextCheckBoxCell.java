package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;

public class TextCheckBoxCell
  extends FrameLayout
{
  private CheckBoxSquare checkBox;
  private boolean needDivider;
  private TextView textView;
  
  public TextCheckBoxCell(Context paramContext)
  {
    super(paramContext);
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    TextView localTextView = this.textView;
    label112:
    float f1;
    label122:
    float f2;
    if (LocaleController.isRTL)
    {
      j = 5;
      localTextView.setGravity(j | 0x10);
      this.textView.setEllipsize(TextUtils.TruncateAt.END);
      localTextView = this.textView;
      if (!LocaleController.isRTL) {
        break label243;
      }
      j = 5;
      if (!LocaleController.isRTL) {
        break label249;
      }
      f1 = 64.0F;
      if (!LocaleController.isRTL) {
        break label256;
      }
      f2 = 17.0F;
      label132:
      addView(localTextView, LayoutHelper.createFrame(-1, -1.0F, j | 0x30, f1, 0.0F, f2, 0.0F));
      this.checkBox = new CheckBoxSquare(paramContext, false);
      this.checkBox.setDuplicateParentStateEnabled(false);
      this.checkBox.setFocusable(false);
      this.checkBox.setFocusableInTouchMode(false);
      this.checkBox.setClickable(false);
      paramContext = this.checkBox;
      if (!LocaleController.isRTL) {
        break label263;
      }
    }
    label243:
    label249:
    label256:
    label263:
    for (int j = i;; j = 5)
    {
      addView(paramContext, LayoutHelper.createFrame(18, 18.0F, j | 0x10, 19.0F, 0.0F, 19.0F, 0.0F));
      return;
      j = 3;
      break;
      j = 3;
      break label112;
      f1 = 17.0F;
      break label122;
      f2 = 64.0F;
      break label132;
    }
  }
  
  public void invalidate()
  {
    super.invalidate();
    this.checkBox.invalidate();
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
    paramInt2 = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM);
    int i = AndroidUtilities.dp(48.0F);
    if (this.needDivider) {}
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      super.onMeasure(paramInt2, View.MeasureSpec.makeMeasureSpec(paramInt1 + i, NUM));
      return;
    }
  }
  
  public void setChecked(boolean paramBoolean)
  {
    this.checkBox.setChecked(paramBoolean, true);
  }
  
  public void setTextAndCheck(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = false;
    this.textView.setText(paramString);
    this.checkBox.setChecked(paramBoolean1, false);
    this.needDivider = paramBoolean2;
    paramBoolean1 = bool;
    if (!paramBoolean2) {
      paramBoolean1 = true;
    }
    setWillNotDraw(paramBoolean1);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/TextCheckBoxCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
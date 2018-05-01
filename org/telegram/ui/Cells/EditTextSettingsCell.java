package org.telegram.ui.Cells;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class EditTextSettingsCell
  extends FrameLayout
{
  private boolean needDivider;
  private EditText textView;
  
  public EditTextSettingsCell(Context paramContext)
  {
    super(paramContext);
    this.textView = new EditText(paramContext);
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.textView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    this.textView.setEllipsize(TextUtils.TruncateAt.END);
    paramContext = this.textView;
    if (LocaleController.isRTL) {}
    for (int j = 5;; j = 3)
    {
      paramContext.setGravity(j | 0x10);
      this.textView.setBackgroundDrawable(null);
      this.textView.setPadding(0, 0, 0, 0);
      this.textView.setInputType(this.textView.getInputType() | 0x4000);
      paramContext = this.textView;
      j = i;
      if (LocaleController.isRTL) {
        j = 5;
      }
      addView(paramContext, LayoutHelper.createFrame(-1, -1.0F, j | 0x30, 17.0F, 0.0F, 17.0F, 0.0F));
      return;
    }
  }
  
  public TextView getTextView()
  {
    return this.textView;
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
      paramInt2 = getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - AndroidUtilities.dp(34.0F);
      paramInt1 = paramInt2 / 2;
      this.textView.measure(View.MeasureSpec.makeMeasureSpec(paramInt2, NUM), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
      return;
    }
  }
  
  public void setEnabled(boolean paramBoolean, ArrayList<Animator> paramArrayList)
  {
    setEnabled(paramBoolean);
  }
  
  public void setText(String paramString, boolean paramBoolean)
  {
    this.textView.setText(paramString);
    this.needDivider = paramBoolean;
    if (!paramBoolean) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      setWillNotDraw(paramBoolean);
      return;
    }
  }
  
  public void setTextAndHint(String paramString1, String paramString2, boolean paramBoolean)
  {
    this.textView.setText(paramString1);
    this.textView.setHint(paramString2);
    this.needDivider = paramBoolean;
    if (!paramBoolean) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      setWillNotDraw(paramBoolean);
      return;
    }
  }
  
  public void setTextColor(int paramInt)
  {
    this.textView.setTextColor(paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/EditTextSettingsCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
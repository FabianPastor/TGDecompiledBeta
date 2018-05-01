package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class ThemeTypeCell
  extends FrameLayout
{
  private ImageView checkImage;
  private boolean needDivider;
  private TextView textView;
  
  public ThemeTypeCell(Context paramContext)
  {
    super(paramContext);
    setWillNotDraw(false);
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    this.textView.setEllipsize(TextUtils.TruncateAt.END);
    TextView localTextView = this.textView;
    label117:
    float f1;
    label127:
    float f2;
    if (LocaleController.isRTL)
    {
      j = 5;
      localTextView.setGravity(j | 0x10);
      localTextView = this.textView;
      if (!LocaleController.isRTL) {
        break label246;
      }
      j = 5;
      if (!LocaleController.isRTL) {
        break label252;
      }
      f1 = 71.0F;
      if (!LocaleController.isRTL) {
        break label259;
      }
      f2 = 17.0F;
      label137:
      addView(localTextView, LayoutHelper.createFrame(-1, -1.0F, j | 0x30, f1, 0.0F, f2, 0.0F));
      this.checkImage = new ImageView(paramContext);
      this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), PorterDuff.Mode.MULTIPLY));
      this.checkImage.setImageResource(NUM);
      paramContext = this.checkImage;
      if (!LocaleController.isRTL) {
        break label266;
      }
    }
    label246:
    label252:
    label259:
    label266:
    for (int j = i;; j = 5)
    {
      addView(paramContext, LayoutHelper.createFrame(19, 14.0F, j | 0x10, 23.0F, 0.0F, 23.0F, 0.0F));
      return;
      j = 3;
      break;
      j = 3;
      break label117;
      f1 = 17.0F;
      break label127;
      f2 = 23.0F;
      break label137;
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider) {
      paramCanvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.dividerPaint);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM);
    paramInt2 = AndroidUtilities.dp(48.0F);
    if (this.needDivider) {}
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(paramInt1 + paramInt2, NUM));
      return;
    }
  }
  
  public void setTypeChecked(boolean paramBoolean)
  {
    ImageView localImageView = this.checkImage;
    if (paramBoolean) {}
    for (int i = 0;; i = 4)
    {
      localImageView.setVisibility(i);
      return;
    }
  }
  
  public void setValue(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.textView.setText(paramString);
    paramString = this.checkImage;
    if (paramBoolean1) {}
    for (int i = 0;; i = 4)
    {
      paramString.setVisibility(i);
      this.needDivider = paramBoolean2;
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/ThemeTypeCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
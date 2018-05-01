package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class TextDetailCell
  extends FrameLayout
{
  private ImageView imageView;
  private boolean multiline;
  private TextView textView;
  private TextView valueTextView;
  
  public TextDetailCell(Context paramContext)
  {
    super(paramContext);
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.textView.setTextSize(1, 16.0F);
    TextView localTextView = this.textView;
    int j;
    label75:
    float f1;
    if (LocaleController.isRTL)
    {
      j = 5;
      localTextView.setGravity(j);
      localTextView = this.textView;
      if (!LocaleController.isRTL) {
        break label357;
      }
      j = 5;
      if (!LocaleController.isRTL) {
        break label363;
      }
      f1 = 16.0F;
      label85:
      if (!LocaleController.isRTL) {
        break label370;
      }
      f2 = 71.0F;
      label95:
      addView(localTextView, LayoutHelper.createFrame(-2, -2.0F, j, f1, 10.0F, f2, 0.0F));
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
      this.valueTextView.setTextSize(1, 13.0F);
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      localTextView = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label377;
      }
      j = 5;
      label188:
      localTextView.setGravity(j);
      localTextView = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label383;
      }
      j = 5;
      label208:
      if (!LocaleController.isRTL) {
        break label389;
      }
      f1 = 16.0F;
      label218:
      if (!LocaleController.isRTL) {
        break label396;
      }
      f2 = 71.0F;
      label228:
      addView(localTextView, LayoutHelper.createFrame(-2, -2.0F, j, f1, 35.0F, f2, 0.0F));
      this.imageView = new ImageView(paramContext);
      this.imageView.setScaleType(ImageView.ScaleType.CENTER);
      this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
      paramContext = this.imageView;
      if (!LocaleController.isRTL) {
        break label403;
      }
      j = i;
      label307:
      if (!LocaleController.isRTL) {
        break label409;
      }
      f1 = 0.0F;
      label316:
      if (!LocaleController.isRTL) {
        break label416;
      }
    }
    label357:
    label363:
    label370:
    label377:
    label383:
    label389:
    label396:
    label403:
    label409:
    label416:
    for (float f2 = 16.0F;; f2 = 0.0F)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, j | 0x30, f1, 11.0F, f2, 0.0F));
      return;
      j = 3;
      break;
      j = 3;
      break label75;
      f1 = 71.0F;
      break label85;
      f2 = 16.0F;
      break label95;
      j = 3;
      break label188;
      j = 3;
      break label208;
      f1 = 71.0F;
      break label218;
      f2 = 16.0F;
      break label228;
      j = 3;
      break label307;
      f1 = 16.0F;
      break label316;
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    if (this.multiline)
    {
      paramInt1 = this.textView.getMeasuredHeight() + AndroidUtilities.dp(13.0F);
      this.valueTextView.layout(this.valueTextView.getLeft(), paramInt1, this.valueTextView.getRight(), this.valueTextView.getMeasuredHeight() + paramInt1);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (!this.multiline) {
      super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0F), NUM));
    }
    for (;;)
    {
      return;
      measureChildWithMargins(this.textView, paramInt1, 0, paramInt2, 0);
      measureChildWithMargins(this.valueTextView, paramInt1, 0, paramInt2, 0);
      measureChildWithMargins(this.imageView, paramInt1, 0, paramInt2, 0);
      setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), Math.max(AndroidUtilities.dp(64.0F), this.textView.getMeasuredHeight() + this.valueTextView.getMeasuredHeight() + AndroidUtilities.dp(20.0F)));
    }
  }
  
  public void setMultiline(boolean paramBoolean)
  {
    this.multiline = paramBoolean;
    if (this.multiline) {
      this.textView.setSingleLine(false);
    }
    for (;;)
    {
      return;
      this.textView.setLines(1);
      this.textView.setMaxLines(1);
      this.textView.setSingleLine(true);
    }
  }
  
  public void setTextAndValue(String paramString1, String paramString2)
  {
    this.textView.setText(paramString1);
    this.valueTextView.setText(paramString2);
    this.imageView.setVisibility(4);
  }
  
  public void setTextAndValueAndIcon(String paramString1, String paramString2, int paramInt1, int paramInt2)
  {
    this.textView.setText(paramString1);
    this.valueTextView.setText(paramString2);
    this.imageView.setVisibility(0);
    this.imageView.setImageResource(paramInt1);
    float f1;
    if (paramInt2 == 0)
    {
      paramString1 = this.imageView;
      if (LocaleController.isRTL)
      {
        paramInt1 = 5;
        if (!LocaleController.isRTL) {
          break label96;
        }
        f1 = 0.0F;
        label59:
        if (!LocaleController.isRTL) {
          break label103;
        }
      }
      label96:
      label103:
      for (f2 = 16.0F;; f2 = 0.0F)
      {
        paramString1.setLayoutParams(LayoutHelper.createFrame(-2, -2.0F, paramInt1 | 0x10, f1, 0.0F, f2, 0.0F));
        return;
        paramInt1 = 3;
        break;
        f1 = 16.0F;
        break label59;
      }
    }
    paramString1 = this.imageView;
    label122:
    label131:
    float f3;
    if (LocaleController.isRTL)
    {
      paramInt1 = 5;
      if (!LocaleController.isRTL) {
        break label176;
      }
      f1 = 0.0F;
      f3 = paramInt2;
      if (!LocaleController.isRTL) {
        break label183;
      }
    }
    label176:
    label183:
    for (float f2 = 16.0F;; f2 = 0.0F)
    {
      paramString1.setLayoutParams(LayoutHelper.createFrame(-2, -2.0F, paramInt1 | 0x30, f1, f3, f2, 0.0F));
      break;
      paramInt1 = 3;
      break label122;
      f1 = 16.0F;
      break label131;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/TextDetailCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
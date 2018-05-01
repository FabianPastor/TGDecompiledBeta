package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class TextSettingsCell
  extends FrameLayout
{
  private boolean needDivider;
  private TextView textView;
  private ImageView valueImageView;
  private TextView valueTextView;
  
  public TextSettingsCell(Context paramContext)
  {
    super(paramContext);
    this.textView = new TextView(paramContext);
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    this.textView.setEllipsize(TextUtils.TruncateAt.END);
    TextView localTextView = this.textView;
    if (LocaleController.isRTL)
    {
      j = 5;
      localTextView.setGravity(j | 0x10);
      localTextView = this.textView;
      if (!LocaleController.isRTL) {
        break label334;
      }
      j = 5;
      label100:
      addView(localTextView, LayoutHelper.createFrame(-1, -1.0F, j | 0x30, 17.0F, 0.0F, 17.0F, 0.0F));
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextSize(1, 16.0F);
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
      localTextView = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label340;
      }
      j = 3;
      label192:
      localTextView.setGravity(j | 0x10);
      localTextView = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label346;
      }
      j = 3;
      label215:
      addView(localTextView, LayoutHelper.createFrame(-2, -1.0F, j | 0x30, 17.0F, 0.0F, 17.0F, 0.0F));
      this.valueImageView = new ImageView(paramContext);
      this.valueImageView.setScaleType(ImageView.ScaleType.CENTER);
      this.valueImageView.setVisibility(4);
      this.valueImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
      paramContext = this.valueImageView;
      if (!LocaleController.isRTL) {
        break label352;
      }
    }
    label334:
    label340:
    label346:
    label352:
    for (int j = i;; j = 5)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, j | 0x10, 17.0F, 0.0F, 17.0F, 0.0F));
      return;
      j = 3;
      break;
      j = 3;
      break label100;
      j = 5;
      break label192;
      j = 5;
      break label215;
    }
  }
  
  public TextView getTextView()
  {
    return this.textView;
  }
  
  public TextView getValueTextView()
  {
    return this.valueTextView;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteValueText"));
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
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
    if (this.needDivider)
    {
      paramInt1 = 1;
      setMeasuredDimension(paramInt2, paramInt1 + i);
      paramInt1 = getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - AndroidUtilities.dp(34.0F);
      paramInt2 = paramInt1 / 2;
      if (this.valueImageView.getVisibility() == 0) {
        this.valueImageView.measure(View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
      }
      if (this.valueTextView.getVisibility() != 0) {
        break label161;
      }
      this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
      paramInt1 = paramInt1 - this.valueTextView.getMeasuredWidth() - AndroidUtilities.dp(8.0F);
    }
    label161:
    for (;;)
    {
      this.textView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1, NUM), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
      return;
      paramInt1 = 0;
      break;
    }
  }
  
  public void setEnabled(boolean paramBoolean, ArrayList<Animator> paramArrayList)
  {
    float f1 = 1.0F;
    setEnabled(paramBoolean);
    float f2;
    if (paramArrayList != null)
    {
      Object localObject = this.textView;
      if (paramBoolean)
      {
        f2 = 1.0F;
        paramArrayList.add(ObjectAnimator.ofFloat(localObject, "alpha", new float[] { f2 }));
        if (this.valueTextView.getVisibility() == 0)
        {
          localObject = this.valueTextView;
          if (!paramBoolean) {
            break label134;
          }
          f2 = 1.0F;
          label67:
          paramArrayList.add(ObjectAnimator.ofFloat(localObject, "alpha", new float[] { f2 }));
        }
        if (this.valueImageView.getVisibility() == 0)
        {
          localObject = this.valueImageView;
          if (!paramBoolean) {
            break label141;
          }
          label107:
          paramArrayList.add(ObjectAnimator.ofFloat(localObject, "alpha", new float[] { f1 }));
        }
      }
    }
    label134:
    label141:
    label159:
    label187:
    do
    {
      return;
      f2 = 0.5F;
      break;
      f2 = 0.5F;
      break label67;
      f1 = 0.5F;
      break label107;
      paramArrayList = this.textView;
      if (!paramBoolean) {
        break label220;
      }
      f2 = 1.0F;
      paramArrayList.setAlpha(f2);
      if (this.valueTextView.getVisibility() == 0)
      {
        paramArrayList = this.valueTextView;
        if (!paramBoolean) {
          break label227;
        }
        f2 = 1.0F;
        paramArrayList.setAlpha(f2);
      }
    } while (this.valueImageView.getVisibility() != 0);
    paramArrayList = this.valueImageView;
    if (paramBoolean) {}
    for (;;)
    {
      paramArrayList.setAlpha(f1);
      break;
      label220:
      f2 = 0.5F;
      break label159;
      label227:
      f2 = 0.5F;
      break label187;
      f1 = 0.5F;
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
package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class TextPriceCell
  extends FrameLayout
{
  private int dotLength;
  private String dotstring;
  private TextView textView;
  private TextView valueTextView;
  
  public TextPriceCell(Context paramContext)
  {
    super(paramContext);
    Object localObject;
    if (LocaleController.isRTL)
    {
      localObject = " .";
      this.dotstring = ((String)localObject);
      setWillNotDraw(false);
      this.textView = new TextView(paramContext);
      this.textView.setTextSize(1, 16.0F);
      this.textView.setLines(1);
      this.textView.setMaxLines(1);
      this.textView.setSingleLine(true);
      this.textView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.textView;
      if (!LocaleController.isRTL) {
        break label277;
      }
      j = 5;
      label96:
      ((TextView)localObject).setGravity(j | 0x10);
      localObject = this.textView;
      if (!LocaleController.isRTL) {
        break label283;
      }
      j = 5;
      label119:
      addView((View)localObject, LayoutHelper.createFrame(-2, -1.0F, j | 0x30, 17.0F, 0.0F, 17.0F, 0.0F));
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextSize(1, 16.0F);
      this.valueTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
      paramContext = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label289;
      }
      j = 3;
      label224:
      paramContext.setGravity(j | 0x10);
      paramContext = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label295;
      }
    }
    label277:
    label283:
    label289:
    label295:
    for (int j = i;; j = 5)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -1.0F, j | 0x30, 17.0F, 0.0F, 17.0F, 0.0F));
      return;
      localObject = ". ";
      break;
      j = 3;
      break label96;
      j = 3;
      break label119;
      j = 5;
      break label224;
    }
  }
  
  protected void onDraw(Canvas paramCanvas) {}
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), AndroidUtilities.dp(40.0F));
    paramInt1 = getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - AndroidUtilities.dp(34.0F);
    paramInt2 = paramInt1 / 2;
    this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
    int i = this.valueTextView.getMeasuredWidth();
    paramInt2 = AndroidUtilities.dp(8.0F);
    this.textView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1 - i - paramInt2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
    this.dotLength = ((int)Math.ceil(this.textView.getPaint().measureText(this.dotstring)));
  }
  
  public void setTextAndValue(String paramString1, String paramString2, boolean paramBoolean)
  {
    this.textView.setText(paramString1);
    if (paramString2 != null)
    {
      this.valueTextView.setText(paramString2);
      this.valueTextView.setVisibility(0);
      if (!paramBoolean) {
        break label102;
      }
      setTag("windowBackgroundWhiteBlackText");
      this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.valueTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    }
    for (;;)
    {
      requestLayout();
      return;
      this.valueTextView.setVisibility(4);
      break;
      label102:
      setTag("windowBackgroundWhiteGrayText2");
      this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
      this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
      this.textView.setTypeface(Typeface.DEFAULT);
      this.valueTextView.setTypeface(Typeface.DEFAULT);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/TextPriceCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
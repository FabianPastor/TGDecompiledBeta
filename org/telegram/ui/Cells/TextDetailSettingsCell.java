package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class TextDetailSettingsCell
  extends FrameLayout
{
  private boolean multiline;
  private boolean needDivider;
  private TextView textView;
  private TextView valueTextView;
  
  public TextDetailSettingsCell(Context paramContext)
  {
    super(paramContext);
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
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
        break label270;
      }
      j = 5;
      label112:
      addView(localTextView, LayoutHelper.createFrame(-2, -2.0F, j | 0x30, 17.0F, 10.0F, 17.0F, 0.0F));
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
      this.valueTextView.setTextSize(1, 13.0F);
      paramContext = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label276;
      }
      j = 5;
      label184:
      paramContext.setGravity(j);
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      this.valueTextView.setPadding(0, 0, 0, 0);
      paramContext = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label282;
      }
    }
    label270:
    label276:
    label282:
    for (int j = i;; j = 3)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, j | 0x30, 17.0F, 35.0F, 17.0F, 0.0F));
      return;
      j = 3;
      break;
      j = 3;
      break label112;
      j = 3;
      break label184;
    }
  }
  
  public void invalidate()
  {
    super.invalidate();
    this.textView.invalidate();
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider) {
      paramCanvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.dividerPaint);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = 0;
    if (!this.multiline)
    {
      int i = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM);
      int j = AndroidUtilities.dp(64.0F);
      paramInt1 = paramInt2;
      if (this.needDivider) {
        paramInt1 = 1;
      }
      super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(paramInt1 + j, NUM));
    }
    for (;;)
    {
      return;
      super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
    }
  }
  
  public void setMultilineDetail(boolean paramBoolean)
  {
    this.multiline = paramBoolean;
    if (paramBoolean)
    {
      this.valueTextView.setLines(0);
      this.valueTextView.setMaxLines(0);
      this.valueTextView.setSingleLine(false);
      this.valueTextView.setPadding(0, 0, 0, AndroidUtilities.dp(12.0F));
    }
    for (;;)
    {
      return;
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      this.valueTextView.setPadding(0, 0, 0, 0);
    }
  }
  
  public void setTextAndValue(String paramString, CharSequence paramCharSequence, boolean paramBoolean)
  {
    this.textView.setText(paramString);
    this.valueTextView.setText(paramCharSequence);
    this.needDivider = paramBoolean;
    if (!paramBoolean) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      setWillNotDraw(paramBoolean);
      return;
    }
  }
  
  public void setTextWithEmojiAndValue(String paramString, CharSequence paramCharSequence, boolean paramBoolean)
  {
    boolean bool = false;
    this.textView.setText(Emoji.replaceEmoji(paramString, this.textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0F), false));
    this.valueTextView.setText(paramCharSequence);
    this.needDivider = paramBoolean;
    if (!paramBoolean) {
      bool = true;
    }
    setWillNotDraw(bool);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/TextDetailSettingsCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
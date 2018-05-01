package org.telegram.ui.Cells;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate;

public class MaxFileSizeCell
  extends FrameLayout
{
  private long maxSize;
  private SeekBarView seekBarView;
  private SimpleTextView sizeTextView;
  private TextView textView;
  
  public MaxFileSizeCell(Context paramContext)
  {
    super(paramContext);
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    this.textView.setText(LocaleController.getString("AutodownloadSizeLimit", NUM));
    Object localObject = this.textView;
    int j;
    label126:
    float f1;
    if (LocaleController.isRTL)
    {
      j = 5;
      ((TextView)localObject).setGravity(j | 0x30);
      this.textView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.textView;
      if (!LocaleController.isRTL) {
        break label348;
      }
      j = 5;
      if (!LocaleController.isRTL) {
        break label354;
      }
      f1 = 64.0F;
      label136:
      if (!LocaleController.isRTL) {
        break label361;
      }
      f2 = 17.0F;
      label146:
      addView((View)localObject, LayoutHelper.createFrame(-1, -1.0F, j | 0x30, f1, 13.0F, f2, 0.0F));
      this.sizeTextView = new SimpleTextView(paramContext);
      this.sizeTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText6"));
      this.sizeTextView.setTextSize(16);
      localObject = this.sizeTextView;
      if (!LocaleController.isRTL) {
        break label368;
      }
      j = 3;
      label216:
      ((SimpleTextView)localObject).setGravity(j | 0x30);
      localObject = this.sizeTextView;
      if (!LocaleController.isRTL) {
        break label374;
      }
      j = i;
      label239:
      if (!LocaleController.isRTL) {
        break label380;
      }
      f1 = 17.0F;
      label249:
      if (!LocaleController.isRTL) {
        break label387;
      }
    }
    label348:
    label354:
    label361:
    label368:
    label374:
    label380:
    label387:
    for (float f2 = 64.0F;; f2 = 17.0F)
    {
      addView((View)localObject, LayoutHelper.createFrame(-1, -1.0F, j | 0x30, f1, 13.0F, f2, 0.0F));
      this.seekBarView = new SeekBarView(paramContext)
      {
        public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          if (paramAnonymousMotionEvent.getAction() == 0) {
            getParent().requestDisallowInterceptTouchEvent(true);
          }
          return super.onTouchEvent(paramAnonymousMotionEvent);
        }
      };
      this.seekBarView.setReportChanges(true);
      this.seekBarView.setDelegate(new SeekBarView.SeekBarViewDelegate()
      {
        public void onSeekBarDrag(float paramAnonymousFloat)
        {
          int i;
          if (MaxFileSizeCell.this.maxSize > 10485760L) {
            if (paramAnonymousFloat <= 0.8F) {
              i = (int)(104857600 * (paramAnonymousFloat / 0.8F));
            }
          }
          for (;;)
          {
            MaxFileSizeCell.this.sizeTextView.setText(LocaleController.formatString("AutodownloadSizeLimitUpTo", NUM, new Object[] { AndroidUtilities.formatFileSize(i) }));
            MaxFileSizeCell.this.didChangedSizeValue(i);
            return;
            i = (int)(104857600 + (float)(MaxFileSizeCell.this.maxSize - 104857600) * (paramAnonymousFloat - 0.8F) / 0.2F);
            continue;
            i = (int)((float)MaxFileSizeCell.this.maxSize * paramAnonymousFloat);
          }
        }
      });
      addView(this.seekBarView, LayoutHelper.createFrame(-1, 30.0F, 51, 4.0F, 40.0F, 4.0F, 0.0F));
      return;
      j = 3;
      break;
      j = 3;
      break label126;
      f1 = 17.0F;
      break label136;
      f2 = 64.0F;
      break label146;
      j = 5;
      break label216;
      j = 3;
      break label239;
      f1 = 64.0F;
      break label249;
    }
  }
  
  protected void didChangedSizeValue(int paramInt) {}
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0F), NUM));
  }
  
  public void setSize(long paramLong1, long paramLong2)
  {
    this.maxSize = paramLong2;
    float f;
    if (this.maxSize > 10485760L) {
      if (paramLong1 <= 104857600) {
        f = (float)paramLong1 / 104857600 * 0.8F;
      }
    }
    for (;;)
    {
      this.seekBarView.setProgress(f);
      this.sizeTextView.setText(LocaleController.formatString("AutodownloadSizeLimitUpTo", NUM, new Object[] { AndroidUtilities.formatFileSize(paramLong1) }));
      return;
      f = 0.8F + (float)(paramLong1 - 104857600) / (float)(this.maxSize - 104857600) * 0.2F;
      continue;
      f = (float)paramLong1 / (float)this.maxSize;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/MaxFileSizeCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class StickerCell
  extends FrameLayout
{
  private static AccelerateInterpolator interpolator = new AccelerateInterpolator(0.5F);
  private BackupImageView imageView;
  private long lastUpdateTime;
  private float scale;
  private boolean scaled;
  private TLRPC.Document sticker;
  private long time = 0L;
  
  public StickerCell(Context paramContext)
  {
    super(paramContext);
    this.imageView = new BackupImageView(paramContext);
    this.imageView.setAspectFit(true);
    addView(this.imageView, LayoutHelper.createFrame(66, 66.0F, 1, 0.0F, 5.0F, 0.0F, 0.0F));
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    boolean bool = super.drawChild(paramCanvas, paramView, paramLong);
    long l;
    if ((paramView == this.imageView) && (((this.scaled) && (this.scale != 0.8F)) || ((!this.scaled) && (this.scale != 1.0F))))
    {
      paramLong = System.currentTimeMillis();
      l = paramLong - this.lastUpdateTime;
      this.lastUpdateTime = paramLong;
      if ((!this.scaled) || (this.scale == 0.8F)) {
        break label151;
      }
      this.scale -= (float)l / 400.0F;
      if (this.scale < 0.8F) {
        this.scale = 0.8F;
      }
    }
    for (;;)
    {
      this.imageView.setScaleX(this.scale);
      this.imageView.setScaleY(this.scale);
      this.imageView.invalidate();
      invalidate();
      return bool;
      label151:
      this.scale += (float)l / 400.0F;
      if (this.scale > 1.0F) {
        this.scale = 1.0F;
      }
    }
  }
  
  public TLRPC.Document getSticker()
  {
    return this.sticker;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(76.0F) + getPaddingLeft() + getPaddingRight(), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(78.0F), NUM));
  }
  
  public void setPressed(boolean paramBoolean)
  {
    ImageReceiver localImageReceiver;
    if (this.imageView.getImageReceiver().getPressed() != paramBoolean)
    {
      localImageReceiver = this.imageView.getImageReceiver();
      if (!paramBoolean) {
        break label46;
      }
    }
    label46:
    for (int i = 1;; i = 0)
    {
      localImageReceiver.setPressed(i);
      this.imageView.invalidate();
      super.setPressed(paramBoolean);
      return;
    }
  }
  
  public void setScaled(boolean paramBoolean)
  {
    this.scaled = paramBoolean;
    this.lastUpdateTime = System.currentTimeMillis();
    invalidate();
  }
  
  public void setSticker(TLRPC.Document paramDocument, int paramInt)
  {
    if ((paramDocument != null) && (paramDocument.thumb != null)) {
      this.imageView.setImage(paramDocument.thumb.location, null, "webp", null);
    }
    this.sticker = paramDocument;
    if (paramInt == -1)
    {
      setBackgroundResource(NUM);
      setPadding(AndroidUtilities.dp(7.0F), 0, 0, 0);
    }
    for (;;)
    {
      paramDocument = getBackground();
      if (paramDocument != null)
      {
        paramDocument.setAlpha(230);
        paramDocument.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_stickersHintPanel"), PorterDuff.Mode.MULTIPLY));
      }
      return;
      if (paramInt == 0)
      {
        setBackgroundResource(NUM);
        setPadding(0, 0, 0, 0);
      }
      else if (paramInt == 1)
      {
        setBackgroundResource(NUM);
        setPadding(0, 0, AndroidUtilities.dp(7.0F), 0);
      }
      else if (paramInt == 2)
      {
        setBackgroundResource(NUM);
        setPadding(AndroidUtilities.dp(3.0F), 0, AndroidUtilities.dp(3.0F), 0);
      }
    }
  }
  
  public boolean showingBitmap()
  {
    if (this.imageView.getImageReceiver().getBitmap() != null) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/StickerCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
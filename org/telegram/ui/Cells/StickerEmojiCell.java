package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class StickerEmojiCell
  extends FrameLayout
{
  private static AccelerateInterpolator interpolator = new AccelerateInterpolator(0.5F);
  private float alpha = 1.0F;
  private boolean changingAlpha;
  private TextView emojiTextView;
  private BackupImageView imageView;
  private long lastUpdateTime;
  private float scale;
  private boolean scaled;
  private TLRPC.Document sticker;
  private long time = 0L;
  
  public StickerEmojiCell(Context paramContext)
  {
    super(paramContext);
    this.imageView = new BackupImageView(paramContext);
    this.imageView.setAspectFit(true);
    addView(this.imageView, LayoutHelper.createFrame(66, 66, 17));
    this.emojiTextView = new TextView(paramContext);
    this.emojiTextView.setTextSize(1, 16.0F);
    addView(this.emojiTextView, LayoutHelper.createFrame(28, 28, 85));
  }
  
  public void disable()
  {
    this.changingAlpha = true;
    this.alpha = 0.5F;
    this.time = 0L;
    this.imageView.getImageReceiver().setAlpha(this.alpha);
    this.imageView.invalidate();
    this.lastUpdateTime = System.currentTimeMillis();
    invalidate();
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    boolean bool = super.drawChild(paramCanvas, paramView, paramLong);
    long l;
    if ((paramView == this.imageView) && ((this.changingAlpha) || ((this.scaled) && (this.scale != 0.8F)) || ((!this.scaled) && (this.scale != 1.0F))))
    {
      paramLong = System.currentTimeMillis();
      l = paramLong - this.lastUpdateTime;
      this.lastUpdateTime = paramLong;
      if (!this.changingAlpha) {
        break label203;
      }
      this.time += l;
      if (this.time > 1050L) {
        this.time = 1050L;
      }
      this.alpha = (0.5F + interpolator.getInterpolation((float)this.time / 1050.0F) * 0.5F);
      if (this.alpha >= 1.0F)
      {
        this.changingAlpha = false;
        this.alpha = 1.0F;
      }
      this.imageView.getImageReceiver().setAlpha(this.alpha);
    }
    for (;;)
    {
      this.imageView.setScaleX(this.scale);
      this.imageView.setScaleY(this.scale);
      this.imageView.invalidate();
      invalidate();
      return bool;
      label203:
      if ((this.scaled) && (this.scale != 0.8F))
      {
        this.scale -= (float)l / 400.0F;
        if (this.scale < 0.8F) {
          this.scale = 0.8F;
        }
      }
      else
      {
        this.scale += (float)l / 400.0F;
        if (this.scale > 1.0F) {
          this.scale = 1.0F;
        }
      }
    }
  }
  
  public TLRPC.Document getSticker()
  {
    return this.sticker;
  }
  
  public void invalidate()
  {
    this.emojiTextView.invalidate();
    super.invalidate();
  }
  
  public boolean isDisabled()
  {
    return this.changingAlpha;
  }
  
  public void setScaled(boolean paramBoolean)
  {
    this.scaled = paramBoolean;
    this.lastUpdateTime = System.currentTimeMillis();
    invalidate();
  }
  
  public void setSticker(TLRPC.Document paramDocument, boolean paramBoolean)
  {
    int k;
    int i;
    if (paramDocument != null)
    {
      this.sticker = paramDocument;
      if (paramDocument.thumb != null) {
        this.imageView.setImage(paramDocument.thumb.location, null, "webp", null);
      }
      if (paramBoolean)
      {
        k = 0;
        i = 0;
      }
    }
    else
    {
      for (;;)
      {
        int j = k;
        if (i < paramDocument.attributes.size())
        {
          TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
          if (!(localDocumentAttribute instanceof TLRPC.TL_documentAttributeSticker)) {
            break label190;
          }
          j = k;
          if (localDocumentAttribute.alt != null)
          {
            j = k;
            if (localDocumentAttribute.alt.length() > 0)
            {
              this.emojiTextView.setText(Emoji.replaceEmoji(localDocumentAttribute.alt, this.emojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0F), false));
              j = 1;
            }
          }
        }
        if (j == 0) {
          this.emojiTextView.setText(Emoji.replaceEmoji(StickersQuery.getEmojiForSticker(this.sticker.id), this.emojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0F), false));
        }
        this.emojiTextView.setVisibility(0);
        return;
        label190:
        i += 1;
      }
    }
    this.emojiTextView.setVisibility(4);
  }
  
  public boolean showingBitmap()
  {
    return this.imageView.getImageReceiver().getBitmap() != null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/StickerEmojiCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
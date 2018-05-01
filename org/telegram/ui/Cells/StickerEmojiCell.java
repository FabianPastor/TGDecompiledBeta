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
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.UserConfig;
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
  private int currentAccount = UserConfig.selectedAccount;
  private TextView emojiTextView;
  private BackupImageView imageView;
  private long lastUpdateTime;
  private boolean recent;
  private float scale;
  private boolean scaled;
  private TLRPC.Document sticker;
  private long time;
  
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
    if ((paramView == this.imageView) && ((this.changingAlpha) || ((this.scaled) && (this.scale != 0.8F)) || ((!this.scaled) && (this.scale != 1.0F))))
    {
      long l = System.currentTimeMillis();
      paramLong = l - this.lastUpdateTime;
      this.lastUpdateTime = l;
      if (!this.changingAlpha) {
        break label204;
      }
      this.time += paramLong;
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
      label204:
      if ((this.scaled) && (this.scale != 0.8F))
      {
        this.scale -= (float)paramLong / 400.0F;
        if (this.scale < 0.8F) {
          this.scale = 0.8F;
        }
      }
      else
      {
        this.scale += (float)paramLong / 400.0F;
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
  
  public boolean isRecent()
  {
    return this.recent;
  }
  
  public void setRecent(boolean paramBoolean)
  {
    this.recent = paramBoolean;
  }
  
  public void setScaled(boolean paramBoolean)
  {
    this.scaled = paramBoolean;
    this.lastUpdateTime = System.currentTimeMillis();
    invalidate();
  }
  
  public void setSticker(TLRPC.Document paramDocument, boolean paramBoolean)
  {
    int j;
    if (paramDocument != null)
    {
      this.sticker = paramDocument;
      if (paramDocument.thumb != null) {
        this.imageView.setImage(paramDocument.thumb.location, null, "webp", null);
      }
      if (!paramBoolean) {
        break label202;
      }
      int i = 0;
      j = 0;
      int k = i;
      if (j < paramDocument.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(j);
        if (!(localDocumentAttribute instanceof TLRPC.TL_documentAttributeSticker)) {
          break label196;
        }
        k = i;
        if (localDocumentAttribute.alt != null)
        {
          k = i;
          if (localDocumentAttribute.alt.length() > 0)
          {
            this.emojiTextView.setText(Emoji.replaceEmoji(localDocumentAttribute.alt, this.emojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0F), false));
            k = 1;
          }
        }
      }
      if (k == 0) {
        this.emojiTextView.setText(Emoji.replaceEmoji(DataQuery.getInstance(this.currentAccount).getEmojiForSticker(this.sticker.id), this.emojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0F), false));
      }
      this.emojiTextView.setVisibility(0);
    }
    for (;;)
    {
      return;
      label196:
      j++;
      break;
      label202:
      this.emojiTextView.setVisibility(4);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/StickerEmojiCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
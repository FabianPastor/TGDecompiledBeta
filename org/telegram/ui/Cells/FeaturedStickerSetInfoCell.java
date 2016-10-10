package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.ui.Components.LayoutHelper;

public class FeaturedStickerSetInfoCell
  extends FrameLayout
{
  private static Paint botProgressPaint;
  private TextView addButton;
  private int angle;
  private boolean drawProgress;
  Drawable drawable = new Drawable()
  {
    Paint paint = new Paint(1);
    
    public void draw(Canvas paramAnonymousCanvas)
    {
      this.paint.setColor(-11688214);
      paramAnonymousCanvas.drawCircle(AndroidUtilities.dp(8.0F), 0.0F, AndroidUtilities.dp(4.0F), this.paint);
    }
    
    public int getIntrinsicHeight()
    {
      return AndroidUtilities.dp(26.0F);
    }
    
    public int getIntrinsicWidth()
    {
      return AndroidUtilities.dp(12.0F);
    }
    
    public int getOpacity()
    {
      return 0;
    }
    
    public void setAlpha(int paramAnonymousInt) {}
    
    public void setColorFilter(ColorFilter paramAnonymousColorFilter) {}
  };
  private boolean hasOnClick;
  private TextView infoTextView;
  private boolean isInstalled;
  private long lastUpdateTime;
  private TextView nameTextView;
  private float progressAlpha;
  private RectF rect = new RectF();
  private TLRPC.StickerSetCovered set;
  
  public FeaturedStickerSetInfoCell(Context paramContext, int paramInt)
  {
    super(paramContext);
    if (botProgressPaint == null)
    {
      botProgressPaint = new Paint(1);
      botProgressPaint.setColor(-1);
      botProgressPaint.setStrokeCap(Paint.Cap.ROUND);
      botProgressPaint.setStyle(Paint.Style.STROKE);
    }
    botProgressPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
    this.nameTextView = new TextView(paramContext);
    this.nameTextView.setTextColor(-13421773);
    this.nameTextView.setTextSize(1, 17.0F);
    this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
    this.nameTextView.setSingleLine(true);
    addView(this.nameTextView, LayoutHelper.createFrame(-2, -1.0F, 51, paramInt, 8.0F, 100.0F, 0.0F));
    this.infoTextView = new TextView(paramContext);
    this.infoTextView.setTextColor(-7697782);
    this.infoTextView.setTextSize(1, 13.0F);
    this.infoTextView.setEllipsize(TextUtils.TruncateAt.END);
    this.infoTextView.setSingleLine(true);
    addView(this.infoTextView, LayoutHelper.createFrame(-2, -1.0F, 51, paramInt, 30.0F, 100.0F, 0.0F));
    this.addButton = new TextView(paramContext)
    {
      protected void onDraw(Canvas paramAnonymousCanvas)
      {
        super.onDraw(paramAnonymousCanvas);
        long l1;
        long l2;
        if ((FeaturedStickerSetInfoCell.this.drawProgress) || ((!FeaturedStickerSetInfoCell.this.drawProgress) && (FeaturedStickerSetInfoCell.this.progressAlpha != 0.0F)))
        {
          FeaturedStickerSetInfoCell.botProgressPaint.setAlpha(Math.min(255, (int)(FeaturedStickerSetInfoCell.this.progressAlpha * 255.0F)));
          int i = getMeasuredWidth() - AndroidUtilities.dp(11.0F);
          FeaturedStickerSetInfoCell.this.rect.set(i, AndroidUtilities.dp(3.0F), AndroidUtilities.dp(8.0F) + i, AndroidUtilities.dp(11.0F));
          paramAnonymousCanvas.drawArc(FeaturedStickerSetInfoCell.this.rect, FeaturedStickerSetInfoCell.this.angle, 220.0F, false, FeaturedStickerSetInfoCell.botProgressPaint);
          invalidate((int)FeaturedStickerSetInfoCell.this.rect.left - AndroidUtilities.dp(2.0F), (int)FeaturedStickerSetInfoCell.this.rect.top - AndroidUtilities.dp(2.0F), (int)FeaturedStickerSetInfoCell.this.rect.right + AndroidUtilities.dp(2.0F), (int)FeaturedStickerSetInfoCell.this.rect.bottom + AndroidUtilities.dp(2.0F));
          l1 = System.currentTimeMillis();
          if (Math.abs(FeaturedStickerSetInfoCell.this.lastUpdateTime - System.currentTimeMillis()) < 1000L)
          {
            l2 = l1 - FeaturedStickerSetInfoCell.this.lastUpdateTime;
            float f = (float)(360L * l2) / 2000.0F;
            FeaturedStickerSetInfoCell.access$402(FeaturedStickerSetInfoCell.this, (int)(FeaturedStickerSetInfoCell.this.angle + f));
            FeaturedStickerSetInfoCell.access$402(FeaturedStickerSetInfoCell.this, FeaturedStickerSetInfoCell.this.angle - FeaturedStickerSetInfoCell.this.angle / 360 * 360);
            if (!FeaturedStickerSetInfoCell.this.drawProgress) {
              break label375;
            }
            if (FeaturedStickerSetInfoCell.this.progressAlpha < 1.0F)
            {
              FeaturedStickerSetInfoCell.access$102(FeaturedStickerSetInfoCell.this, FeaturedStickerSetInfoCell.this.progressAlpha + (float)l2 / 200.0F);
              if (FeaturedStickerSetInfoCell.this.progressAlpha > 1.0F) {
                FeaturedStickerSetInfoCell.access$102(FeaturedStickerSetInfoCell.this, 1.0F);
              }
            }
          }
        }
        for (;;)
        {
          FeaturedStickerSetInfoCell.access$502(FeaturedStickerSetInfoCell.this, l1);
          invalidate();
          return;
          label375:
          if (FeaturedStickerSetInfoCell.this.progressAlpha > 0.0F)
          {
            FeaturedStickerSetInfoCell.access$102(FeaturedStickerSetInfoCell.this, FeaturedStickerSetInfoCell.this.progressAlpha - (float)l2 / 200.0F);
            if (FeaturedStickerSetInfoCell.this.progressAlpha < 0.0F) {
              FeaturedStickerSetInfoCell.access$102(FeaturedStickerSetInfoCell.this, 0.0F);
            }
          }
        }
      }
    };
    this.addButton.setPadding(AndroidUtilities.dp(17.0F), 0, AndroidUtilities.dp(17.0F), 0);
    this.addButton.setGravity(17);
    this.addButton.setTextColor(-1);
    this.addButton.setTextSize(1, 14.0F);
    this.addButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    addView(this.addButton, LayoutHelper.createFrame(-2, 28.0F, 53, 0.0F, 16.0F, 14.0F, 0.0F));
  }
  
  public TLRPC.StickerSetCovered getStickerSet()
  {
    return this.set;
  }
  
  public boolean isInstalled()
  {
    return this.isInstalled;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0F), 1073741824));
  }
  
  public void setAddOnClickListener(View.OnClickListener paramOnClickListener)
  {
    this.hasOnClick = true;
    this.addButton.setOnClickListener(paramOnClickListener);
  }
  
  public void setDrawProgress(boolean paramBoolean)
  {
    this.drawProgress = paramBoolean;
    this.lastUpdateTime = System.currentTimeMillis();
    this.addButton.invalidate();
  }
  
  public void setStickerSet(TLRPC.StickerSetCovered paramStickerSetCovered, boolean paramBoolean)
  {
    this.lastUpdateTime = System.currentTimeMillis();
    this.nameTextView.setText(paramStickerSetCovered.set.title);
    this.infoTextView.setText(LocaleController.formatPluralString("Stickers", paramStickerSetCovered.set.count));
    if (paramBoolean)
    {
      this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, this.drawable, null);
      if (!this.hasOnClick) {
        break label174;
      }
      this.addButton.setVisibility(0);
      paramBoolean = StickersQuery.isStickerPackInstalled(paramStickerSetCovered.set.id);
      this.isInstalled = paramBoolean;
      if (!paramBoolean) {
        break label142;
      }
      this.addButton.setBackgroundResource(2130837636);
      this.addButton.setText(LocaleController.getString("StickersRemove", 2131166313).toUpperCase());
    }
    for (;;)
    {
      this.set = paramStickerSetCovered;
      return;
      this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
      break;
      label142:
      this.addButton.setBackgroundResource(2130837509);
      this.addButton.setText(LocaleController.getString("Add", 2131165254).toUpperCase());
      continue;
      label174:
      this.addButton.setVisibility(8);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/FeaturedStickerSetInfoCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
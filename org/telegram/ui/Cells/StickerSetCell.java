package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class StickerSetCell
  extends FrameLayout
{
  private static Paint paint;
  private BackupImageView imageView;
  private boolean needDivider;
  private ImageView optionsButton;
  private Rect rect = new Rect();
  private TLRPC.TL_messages_stickerSet stickersSet;
  private TextView textView;
  private TextView valueTextView;
  
  public StickerSetCell(Context paramContext)
  {
    super(paramContext);
    if (paint == null)
    {
      paint = new Paint();
      paint.setColor(-2500135);
    }
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(-14606047);
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    this.textView.setEllipsize(TextUtils.TruncateAt.END);
    Object localObject = this.textView;
    label145:
    float f1;
    label154:
    float f2;
    if (LocaleController.isRTL)
    {
      i = 5;
      ((TextView)localObject).setGravity(i);
      localObject = this.textView;
      if (!LocaleController.isRTL) {
        break label478;
      }
      i = 5;
      if (!LocaleController.isRTL) {
        break label484;
      }
      f1 = 40.0F;
      if (!LocaleController.isRTL) {
        break label490;
      }
      f2 = 71.0F;
      label163:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i, f1, 10.0F, f2, 0.0F));
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextColor(-7697782);
      this.valueTextView.setTextSize(1, 13.0F);
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      localObject = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label496;
      }
      i = 5;
      label253:
      ((TextView)localObject).setGravity(i);
      localObject = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label502;
      }
      i = 5;
      label275:
      if (!LocaleController.isRTL) {
        break label508;
      }
      f1 = 40.0F;
      label284:
      if (!LocaleController.isRTL) {
        break label514;
      }
      f2 = 71.0F;
      label293:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i, f1, 35.0F, f2, 0.0F));
      this.imageView = new BackupImageView(paramContext);
      this.imageView.setAspectFit(true);
      localObject = this.imageView;
      if (!LocaleController.isRTL) {
        break label520;
      }
      i = 5;
      label348:
      if (!LocaleController.isRTL) {
        break label526;
      }
      f1 = 0.0F;
      label356:
      if (!LocaleController.isRTL) {
        break label532;
      }
      f2 = 12.0F;
      label365:
      addView((View)localObject, LayoutHelper.createFrame(48, 48.0F, i | 0x30, f1, 8.0F, f2, 0.0F));
      this.optionsButton = new ImageView(paramContext);
      this.optionsButton.setFocusable(false);
      this.optionsButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(788529152));
      this.optionsButton.setImageResource(2130837644);
      this.optionsButton.setScaleType(ImageView.ScaleType.CENTER);
      paramContext = this.optionsButton;
      if (!LocaleController.isRTL) {
        break label537;
      }
    }
    label478:
    label484:
    label490:
    label496:
    label502:
    label508:
    label514:
    label520:
    label526:
    label532:
    label537:
    for (int i = j;; i = 5)
    {
      addView(paramContext, LayoutHelper.createFrame(40, 40, i | 0x30));
      return;
      i = 3;
      break;
      i = 3;
      break label145;
      f1 = 71.0F;
      break label154;
      f2 = 40.0F;
      break label163;
      i = 3;
      break label253;
      i = 3;
      break label275;
      f1 = 71.0F;
      break label284;
      f2 = 40.0F;
      break label293;
      i = 3;
      break label348;
      f1 = 12.0F;
      break label356;
      f2 = 0.0F;
      break label365;
    }
  }
  
  public TLRPC.TL_messages_stickerSet getStickersSet()
  {
    return this.stickersSet;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider) {
      paramCanvas.drawLine(0.0F, getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824);
    int i = AndroidUtilities.dp(64.0F);
    if (this.needDivider) {}
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      super.onMeasure(paramInt2, View.MeasureSpec.makeMeasureSpec(paramInt1 + i, 1073741824));
      return;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((Build.VERSION.SDK_INT >= 21) && (getBackground() != null))
    {
      this.optionsButton.getHitRect(this.rect);
      if (this.rect.contains((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY())) {
        return true;
      }
      if ((paramMotionEvent.getAction() == 0) || (paramMotionEvent.getAction() == 2)) {
        getBackground().setHotspot(paramMotionEvent.getX(), paramMotionEvent.getY());
      }
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  public void setOnOptionsClick(View.OnClickListener paramOnClickListener)
  {
    this.optionsButton.setOnClickListener(paramOnClickListener);
  }
  
  public void setStickersSet(TLRPC.TL_messages_stickerSet paramTL_messages_stickerSet, boolean paramBoolean)
  {
    this.needDivider = paramBoolean;
    this.stickersSet = paramTL_messages_stickerSet;
    this.textView.setText(this.stickersSet.set.title);
    if (this.stickersSet.set.archived)
    {
      this.textView.setAlpha(0.5F);
      this.valueTextView.setAlpha(0.5F);
      this.imageView.setAlpha(0.5F);
    }
    for (;;)
    {
      paramTL_messages_stickerSet = paramTL_messages_stickerSet.documents;
      if ((paramTL_messages_stickerSet == null) || (paramTL_messages_stickerSet.isEmpty())) {
        break;
      }
      this.valueTextView.setText(LocaleController.formatPluralString("Stickers", paramTL_messages_stickerSet.size()));
      paramTL_messages_stickerSet = (TLRPC.Document)paramTL_messages_stickerSet.get(0);
      if ((paramTL_messages_stickerSet.thumb != null) && (paramTL_messages_stickerSet.thumb.location != null)) {
        this.imageView.setImage(paramTL_messages_stickerSet.thumb.location, null, "webp", null);
      }
      return;
      this.textView.setAlpha(1.0F);
      this.valueTextView.setAlpha(1.0F);
      this.imageView.setAlpha(1.0F);
    }
    this.valueTextView.setText(LocaleController.formatPluralString("Stickers", 0));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/StickerSetCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
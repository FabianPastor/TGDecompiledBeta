package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.text.TextUtils;
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
import org.telegram.ui.Components.RadialProgressView;

public class StickerSetCell
  extends FrameLayout
{
  private BackupImageView imageView;
  private boolean needDivider;
  private ImageView optionsButton;
  private RadialProgressView progressView;
  private Rect rect = new Rect();
  private TLRPC.TL_messages_stickerSet stickersSet;
  private TextView textView;
  private TextView valueTextView;
  
  public StickerSetCell(Context paramContext, int paramInt)
  {
    super(paramContext);
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    this.textView.setEllipsize(TextUtils.TruncateAt.END);
    Object localObject = this.textView;
    int k;
    label126:
    float f1;
    label136:
    float f2;
    if (LocaleController.isRTL)
    {
      k = 5;
      ((TextView)localObject).setGravity(k);
      localObject = this.textView;
      if (!LocaleController.isRTL) {
        break label490;
      }
      k = 5;
      if (!LocaleController.isRTL) {
        break label496;
      }
      f1 = 40.0F;
      if (!LocaleController.isRTL) {
        break label503;
      }
      f2 = 71.0F;
      label146:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, k, f1, 10.0F, f2, 0.0F));
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
      this.valueTextView.setTextSize(1, 13.0F);
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      localObject = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label510;
      }
      k = 5;
      label241:
      ((TextView)localObject).setGravity(k);
      localObject = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label516;
      }
      k = 5;
      label263:
      if (!LocaleController.isRTL) {
        break label522;
      }
      f1 = 40.0F;
      label273:
      if (!LocaleController.isRTL) {
        break label529;
      }
      f2 = 71.0F;
      label283:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, k, f1, 35.0F, f2, 0.0F));
      this.imageView = new BackupImageView(paramContext);
      this.imageView.setAspectFit(true);
      localObject = this.imageView;
      if (!LocaleController.isRTL) {
        break label536;
      }
      k = 5;
      label340:
      if (!LocaleController.isRTL) {
        break label542;
      }
      f1 = 0.0F;
      label349:
      if (!LocaleController.isRTL) {
        break label549;
      }
      f2 = 12.0F;
      label359:
      addView((View)localObject, LayoutHelper.createFrame(48, 48.0F, k | 0x30, f1, 8.0F, f2, 0.0F));
      if (paramInt != 2) {
        break label573;
      }
      this.progressView = new RadialProgressView(getContext());
      this.progressView.setProgressColor(Theme.getColor("dialogProgressCircle"));
      this.progressView.setSize(AndroidUtilities.dp(30.0F));
      paramContext = this.progressView;
      if (!LocaleController.isRTL) {
        break label555;
      }
      paramInt = i;
      label441:
      if (!LocaleController.isRTL) {
        break label560;
      }
      f1 = 0.0F;
      label450:
      if (!LocaleController.isRTL) {
        break label567;
      }
      f2 = 12.0F;
      label460:
      addView(paramContext, LayoutHelper.createFrame(48, 48.0F, paramInt | 0x30, f1, 8.0F, f2, 0.0F));
    }
    label490:
    label496:
    label503:
    label510:
    label516:
    label522:
    label529:
    label536:
    label542:
    label549:
    label555:
    label560:
    label567:
    label573:
    do
    {
      do
      {
        return;
        k = 3;
        break;
        k = 3;
        break label126;
        f1 = 71.0F;
        break label136;
        f2 = 40.0F;
        break label146;
        k = 3;
        break label241;
        k = 3;
        break label263;
        f1 = 71.0F;
        break label273;
        f2 = 40.0F;
        break label283;
        k = 3;
        break label340;
        f1 = 12.0F;
        break label349;
        f2 = 0.0F;
        break label359;
        paramInt = 3;
        break label441;
        f1 = 12.0F;
        break label450;
        f2 = 0.0F;
        break label460;
      } while (paramInt == 0);
      this.optionsButton = new ImageView(paramContext);
      this.optionsButton.setFocusable(false);
      this.optionsButton.setScaleType(ImageView.ScaleType.CENTER);
      this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
      if (paramInt == 1)
      {
        this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), PorterDuff.Mode.MULTIPLY));
        this.optionsButton.setImageResource(NUM);
        paramContext = this.optionsButton;
        if (LocaleController.isRTL) {}
        for (;;)
        {
          addView(paramContext, LayoutHelper.createFrame(40, 40, j | 0x30));
          break;
          j = 5;
        }
      }
    } while (paramInt != 3);
    this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), PorterDuff.Mode.MULTIPLY));
    this.optionsButton.setImageResource(NUM);
    paramContext = this.optionsButton;
    if (LocaleController.isRTL)
    {
      label742:
      if (!LocaleController.isRTL) {
        break label796;
      }
      paramInt = 10;
      label751:
      f1 = paramInt;
      if (!LocaleController.isRTL) {
        break label801;
      }
    }
    label796:
    label801:
    for (paramInt = 0;; paramInt = 10)
    {
      addView(paramContext, LayoutHelper.createFrame(40, 40.0F, j | 0x30, f1, 12.0F, paramInt, 0.0F));
      break;
      j = 5;
      break label742;
      paramInt = 0;
      break label751;
    }
  }
  
  public TLRPC.TL_messages_stickerSet getStickersSet()
  {
    return this.stickersSet;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider) {
      paramCanvas.drawLine(0.0F, getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.dividerPaint);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM);
    paramInt2 = AndroidUtilities.dp(64.0F);
    if (this.needDivider) {}
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(paramInt1 + paramInt2, NUM));
      return;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((Build.VERSION.SDK_INT >= 21) && (getBackground() != null) && (this.optionsButton != null))
    {
      this.optionsButton.getHitRect(this.rect);
      if (!this.rect.contains((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY())) {}
    }
    for (boolean bool = true;; bool = super.onTouchEvent(paramMotionEvent)) {
      return bool;
    }
  }
  
  public void setChecked(boolean paramBoolean)
  {
    if (this.optionsButton == null) {
      return;
    }
    ImageView localImageView = this.optionsButton;
    if (paramBoolean) {}
    for (int i = 0;; i = 4)
    {
      localImageView.setVisibility(i);
      break;
    }
  }
  
  public void setOnOptionsClick(View.OnClickListener paramOnClickListener)
  {
    if (this.optionsButton == null) {}
    for (;;)
    {
      return;
      this.optionsButton.setOnClickListener(paramOnClickListener);
    }
  }
  
  public void setStickersSet(TLRPC.TL_messages_stickerSet paramTL_messages_stickerSet, boolean paramBoolean)
  {
    this.needDivider = paramBoolean;
    this.stickersSet = paramTL_messages_stickerSet;
    this.imageView.setVisibility(0);
    if (this.progressView != null) {
      this.progressView.setVisibility(4);
    }
    this.textView.setTranslationY(0.0F);
    this.textView.setText(this.stickersSet.set.title);
    if (this.stickersSet.set.archived)
    {
      this.textView.setAlpha(0.5F);
      this.valueTextView.setAlpha(0.5F);
      this.imageView.setAlpha(0.5F);
      paramTL_messages_stickerSet = paramTL_messages_stickerSet.documents;
      if ((paramTL_messages_stickerSet == null) || (paramTL_messages_stickerSet.isEmpty())) {
        break label207;
      }
      this.valueTextView.setText(LocaleController.formatPluralString("Stickers", paramTL_messages_stickerSet.size()));
      paramTL_messages_stickerSet = (TLRPC.Document)paramTL_messages_stickerSet.get(0);
      if ((paramTL_messages_stickerSet.thumb != null) && (paramTL_messages_stickerSet.thumb.location != null)) {
        this.imageView.setImage(paramTL_messages_stickerSet.thumb.location, null, "webp", null);
      }
    }
    for (;;)
    {
      return;
      this.textView.setAlpha(1.0F);
      this.valueTextView.setAlpha(1.0F);
      this.imageView.setAlpha(1.0F);
      break;
      label207:
      this.valueTextView.setText(LocaleController.formatPluralString("Stickers", 0));
    }
  }
  
  public void setText(String paramString1, String paramString2, int paramInt, boolean paramBoolean)
  {
    this.needDivider = paramBoolean;
    this.stickersSet = null;
    this.textView.setText(paramString1);
    this.valueTextView.setText(paramString2);
    if (TextUtils.isEmpty(paramString2))
    {
      this.textView.setTranslationY(AndroidUtilities.dp(10.0F));
      if (paramInt == 0) {
        break label100;
      }
      this.imageView.setImageResource(paramInt, Theme.getColor("windowBackgroundWhiteGrayIcon"));
      this.imageView.setVisibility(0);
      if (this.progressView != null) {
        this.progressView.setVisibility(4);
      }
    }
    for (;;)
    {
      return;
      this.textView.setTranslationY(0.0F);
      break;
      label100:
      this.imageView.setVisibility(4);
      if (this.progressView != null) {
        this.progressView.setVisibility(0);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/StickerSetCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
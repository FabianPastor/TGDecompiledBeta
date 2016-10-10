package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Switch;

public class ArchivedStickerSetCell
  extends FrameLayout
{
  private static Paint paint;
  private Switch checkBox;
  private BackupImageView imageView;
  private boolean needDivider;
  private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;
  private Rect rect = new Rect();
  private TLRPC.StickerSetCovered stickersSet;
  private TextView textView;
  private TextView valueTextView;
  
  public ArchivedStickerSetCell(Context paramContext, boolean paramBoolean)
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
        break label479;
      }
      i = 5;
      if (!LocaleController.isRTL) {
        break label485;
      }
      f1 = 40.0F;
      if (!LocaleController.isRTL) {
        break label491;
      }
      f2 = 71.0F;
      label164:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i, f1, 10.0F, f2, 0.0F));
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextColor(-7697782);
      this.valueTextView.setTextSize(1, 13.0F);
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      localObject = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label498;
      }
      i = 5;
      label255:
      ((TextView)localObject).setGravity(i);
      localObject = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label504;
      }
      i = 5;
      label277:
      if (!LocaleController.isRTL) {
        break label510;
      }
      f1 = 40.0F;
      label286:
      if (!LocaleController.isRTL) {
        break label516;
      }
      f2 = 71.0F;
      label296:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i, f1, 35.0F, f2, 0.0F));
      this.imageView = new BackupImageView(paramContext);
      this.imageView.setAspectFit(true);
      localObject = this.imageView;
      if (!LocaleController.isRTL) {
        break label523;
      }
      i = 5;
      label352:
      if (!LocaleController.isRTL) {
        break label529;
      }
      f1 = 0.0F;
      label360:
      if (!LocaleController.isRTL) {
        break label535;
      }
      f2 = 12.0F;
      label370:
      addView((View)localObject, LayoutHelper.createFrame(48, 48.0F, i | 0x30, f1, 8.0F, f2, 0.0F));
      if (paramBoolean)
      {
        this.checkBox = new Switch(paramContext);
        this.checkBox.setDuplicateParentStateEnabled(false);
        this.checkBox.setFocusable(false);
        this.checkBox.setFocusableInTouchMode(false);
        paramContext = this.checkBox;
        if (!LocaleController.isRTL) {
          break label541;
        }
      }
    }
    label479:
    label485:
    label491:
    label498:
    label504:
    label510:
    label516:
    label523:
    label529:
    label535:
    label541:
    for (int i = j;; i = 5)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, i | 0x10, 14.0F, 0.0F, 14.0F, 0.0F));
      return;
      i = 3;
      break;
      i = 3;
      break label145;
      f1 = 71.0F;
      break label154;
      f2 = 40.0F;
      break label164;
      i = 3;
      break label255;
      i = 3;
      break label277;
      f1 = 71.0F;
      break label286;
      f2 = 40.0F;
      break label296;
      i = 3;
      break label352;
      f1 = 12.0F;
      break label360;
      f2 = 0.0F;
      break label370;
    }
  }
  
  public TLRPC.StickerSetCovered getStickersSet()
  {
    return this.stickersSet;
  }
  
  public boolean isChecked()
  {
    return (this.checkBox != null) && (this.checkBox.isChecked());
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
    if (this.checkBox != null)
    {
      this.checkBox.getHitRect(this.rect);
      if (this.rect.contains((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY()))
      {
        paramMotionEvent.offsetLocation(-this.checkBox.getX(), -this.checkBox.getY());
        return this.checkBox.onTouchEvent(paramMotionEvent);
      }
    }
    if ((Build.VERSION.SDK_INT >= 21) && (getBackground() != null) && ((paramMotionEvent.getAction() == 0) || (paramMotionEvent.getAction() == 2))) {
      getBackground().setHotspot(paramMotionEvent.getX(), paramMotionEvent.getY());
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  public void setChecked(boolean paramBoolean)
  {
    this.checkBox.setOnCheckedChangeListener(null);
    this.checkBox.setChecked(paramBoolean);
    this.checkBox.setOnCheckedChangeListener(this.onCheckedChangeListener);
  }
  
  public void setOnCheckClick(CompoundButton.OnCheckedChangeListener paramOnCheckedChangeListener)
  {
    Switch localSwitch = this.checkBox;
    this.onCheckedChangeListener = paramOnCheckedChangeListener;
    localSwitch.setOnCheckedChangeListener(paramOnCheckedChangeListener);
    this.checkBox.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView) {}
    });
  }
  
  public void setStickersSet(TLRPC.StickerSetCovered paramStickerSetCovered, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.needDivider = paramBoolean1;
    this.stickersSet = paramStickerSetCovered;
    Drawable local1;
    Object localObject;
    if (!this.needDivider)
    {
      paramBoolean1 = true;
      setWillNotDraw(paramBoolean1);
      this.textView.setText(this.stickersSet.set.title);
      if (!paramBoolean2) {
        break label178;
      }
      local1 = new Drawable()
      {
        Paint paint = new Paint(1);
        
        public void draw(Canvas paramAnonymousCanvas)
        {
          this.paint.setColor(-12277526);
          paramAnonymousCanvas.drawCircle(AndroidUtilities.dp(4.0F), AndroidUtilities.dp(5.0F), AndroidUtilities.dp(3.0F), this.paint);
        }
        
        public int getIntrinsicHeight()
        {
          return AndroidUtilities.dp(8.0F);
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
      TextView localTextView = this.textView;
      if (!LocaleController.isRTL) {
        break label165;
      }
      localObject = null;
      label70:
      if (!LocaleController.isRTL) {
        break label172;
      }
      label76:
      localTextView.setCompoundDrawablesWithIntrinsicBounds((Drawable)localObject, null, local1, null);
      label87:
      this.valueTextView.setText(LocaleController.formatPluralString("Stickers", paramStickerSetCovered.set.count));
      if ((paramStickerSetCovered.cover == null) || (paramStickerSetCovered.cover.thumb == null) || (paramStickerSetCovered.cover.thumb.location == null)) {
        break label192;
      }
      this.imageView.setImage(paramStickerSetCovered.cover.thumb.location, null, "webp", null);
    }
    label165:
    label172:
    label178:
    label192:
    while (paramStickerSetCovered.covers.isEmpty())
    {
      return;
      paramBoolean1 = false;
      break;
      localObject = local1;
      break label70;
      local1 = null;
      break label76;
      this.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
      break label87;
    }
    this.imageView.setImage(((TLRPC.Document)paramStickerSetCovered.covers.get(0)).thumb.location, null, "webp", null);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/ArchivedStickerSetCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
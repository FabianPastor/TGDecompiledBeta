package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Switch;

public class FeaturedStickerSetCell
  extends FrameLayout
{
  private static Paint botProgressPaint;
  private static Paint paint;
  private TextView addButton;
  private int angle;
  private Switch checkBox;
  private ImageView checkImage;
  private AnimatorSet currentAnimation;
  private boolean drawProgress;
  private BackupImageView imageView;
  private boolean isInstalled;
  private long lastUpdateTime;
  private boolean needDivider;
  private float progressAlpha;
  private RectF progressRect = new RectF();
  private Rect rect = new Rect();
  private TLRPC.StickerSetCovered stickersSet;
  private TextView textView;
  private TextView valueTextView;
  private boolean wasLayout;
  
  public FeaturedStickerSetCell(Context paramContext)
  {
    super(paramContext);
    if (paint == null)
    {
      paint = new Paint();
      paint.setColor(-2500135);
    }
    if (botProgressPaint == null)
    {
      botProgressPaint = new Paint(1);
      botProgressPaint.setColor(-1);
      botProgressPaint.setStrokeCap(Paint.Cap.ROUND);
      botProgressPaint.setStyle(Paint.Style.STROKE);
    }
    botProgressPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(-14606047);
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    this.textView.setEllipsize(TextUtils.TruncateAt.END);
    Object localObject = this.textView;
    int i;
    label209:
    float f1;
    if (LocaleController.isRTL)
    {
      i = 5;
      ((TextView)localObject).setGravity(i);
      localObject = this.textView;
      if (!LocaleController.isRTL) {
        break label658;
      }
      i = 5;
      if (!LocaleController.isRTL) {
        break label664;
      }
      f1 = 100.0F;
      label218:
      if (!LocaleController.isRTL) {
        break label670;
      }
      f2 = 71.0F;
      label227:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i, f1, 10.0F, f2, 0.0F));
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextColor(-7697782);
      this.valueTextView.setTextSize(1, 13.0F);
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label676;
      }
      i = 5;
      label327:
      ((TextView)localObject).setGravity(i);
      localObject = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label682;
      }
      i = 5;
      label349:
      if (!LocaleController.isRTL) {
        break label688;
      }
      f1 = 100.0F;
      label358:
      if (!LocaleController.isRTL) {
        break label694;
      }
      f2 = 71.0F;
      label367:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i, f1, 35.0F, f2, 0.0F));
      this.imageView = new BackupImageView(paramContext);
      this.imageView.setAspectFit(true);
      localObject = this.imageView;
      if (!LocaleController.isRTL) {
        break label700;
      }
      i = 5;
      label422:
      if (!LocaleController.isRTL) {
        break label706;
      }
      f1 = 0.0F;
      label430:
      if (!LocaleController.isRTL) {
        break label712;
      }
      f2 = 12.0F;
      label439:
      addView((View)localObject, LayoutHelper.createFrame(48, 48.0F, i | 0x30, f1, 8.0F, f2, 0.0F));
      this.addButton = new TextView(paramContext)
      {
        protected void onDraw(Canvas paramAnonymousCanvas)
        {
          super.onDraw(paramAnonymousCanvas);
          long l1;
          long l2;
          if ((FeaturedStickerSetCell.this.drawProgress) || ((!FeaturedStickerSetCell.this.drawProgress) && (FeaturedStickerSetCell.this.progressAlpha != 0.0F)))
          {
            FeaturedStickerSetCell.botProgressPaint.setAlpha(Math.min(255, (int)(FeaturedStickerSetCell.this.progressAlpha * 255.0F)));
            int i = getMeasuredWidth() - AndroidUtilities.dp(11.0F);
            FeaturedStickerSetCell.this.progressRect.set(i, AndroidUtilities.dp(3.0F), AndroidUtilities.dp(8.0F) + i, AndroidUtilities.dp(11.0F));
            paramAnonymousCanvas.drawArc(FeaturedStickerSetCell.this.progressRect, FeaturedStickerSetCell.this.angle, 220.0F, false, FeaturedStickerSetCell.botProgressPaint);
            invalidate((int)FeaturedStickerSetCell.this.progressRect.left - AndroidUtilities.dp(2.0F), (int)FeaturedStickerSetCell.this.progressRect.top - AndroidUtilities.dp(2.0F), (int)FeaturedStickerSetCell.this.progressRect.right + AndroidUtilities.dp(2.0F), (int)FeaturedStickerSetCell.this.progressRect.bottom + AndroidUtilities.dp(2.0F));
            l1 = System.currentTimeMillis();
            if (Math.abs(FeaturedStickerSetCell.this.lastUpdateTime - System.currentTimeMillis()) < 1000L)
            {
              l2 = l1 - FeaturedStickerSetCell.this.lastUpdateTime;
              float f = (float)(360L * l2) / 2000.0F;
              FeaturedStickerSetCell.access$402(FeaturedStickerSetCell.this, (int)(FeaturedStickerSetCell.this.angle + f));
              FeaturedStickerSetCell.access$402(FeaturedStickerSetCell.this, FeaturedStickerSetCell.this.angle - FeaturedStickerSetCell.this.angle / 360 * 360);
              if (!FeaturedStickerSetCell.this.drawProgress) {
                break label375;
              }
              if (FeaturedStickerSetCell.this.progressAlpha < 1.0F)
              {
                FeaturedStickerSetCell.access$102(FeaturedStickerSetCell.this, FeaturedStickerSetCell.this.progressAlpha + (float)l2 / 200.0F);
                if (FeaturedStickerSetCell.this.progressAlpha > 1.0F) {
                  FeaturedStickerSetCell.access$102(FeaturedStickerSetCell.this, 1.0F);
                }
              }
            }
          }
          for (;;)
          {
            FeaturedStickerSetCell.access$502(FeaturedStickerSetCell.this, l1);
            invalidate();
            return;
            label375:
            if (FeaturedStickerSetCell.this.progressAlpha > 0.0F)
            {
              FeaturedStickerSetCell.access$102(FeaturedStickerSetCell.this, FeaturedStickerSetCell.this.progressAlpha - (float)l2 / 200.0F);
              if (FeaturedStickerSetCell.this.progressAlpha < 0.0F) {
                FeaturedStickerSetCell.access$102(FeaturedStickerSetCell.this, 0.0F);
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
      this.addButton.setBackgroundResource(2130837509);
      this.addButton.setText(LocaleController.getString("Add", 2131165254).toUpperCase());
      localObject = this.addButton;
      if (!LocaleController.isRTL) {
        break label717;
      }
      i = j;
      label575:
      if (!LocaleController.isRTL) {
        break label723;
      }
      f1 = 14.0F;
      label584:
      if (!LocaleController.isRTL) {
        break label728;
      }
    }
    label658:
    label664:
    label670:
    label676:
    label682:
    label688:
    label694:
    label700:
    label706:
    label712:
    label717:
    label723:
    label728:
    for (float f2 = 0.0F;; f2 = 14.0F)
    {
      addView((View)localObject, LayoutHelper.createFrame(-2, 28.0F, i | 0x30, f1, 18.0F, f2, 0.0F));
      this.checkImage = new ImageView(paramContext);
      this.checkImage.setImageResource(2130837988);
      addView(this.checkImage, LayoutHelper.createFrame(19, 14.0F));
      return;
      i = 3;
      break;
      i = 3;
      break label209;
      f1 = 71.0F;
      break label218;
      f2 = 100.0F;
      break label227;
      i = 3;
      break label327;
      i = 3;
      break label349;
      f1 = 71.0F;
      break label358;
      f2 = 100.0F;
      break label367;
      i = 3;
      break label422;
      f1 = 12.0F;
      break label430;
      f2 = 0.0F;
      break label439;
      i = 5;
      break label575;
      f1 = 0.0F;
      break label584;
    }
  }
  
  public TLRPC.StickerSetCovered getStickerSet()
  {
    return this.stickersSet;
  }
  
  public boolean isInstalled()
  {
    return this.isInstalled;
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.wasLayout = false;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider) {
      paramCanvas.drawLine(0.0F, getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    paramInt1 = this.addButton.getLeft() + this.addButton.getMeasuredWidth() / 2 - this.checkImage.getMeasuredWidth() / 2;
    paramInt2 = this.addButton.getTop() + this.addButton.getMeasuredHeight() / 2 - this.checkImage.getMeasuredHeight() / 2;
    this.checkImage.layout(paramInt1, paramInt2, this.checkImage.getMeasuredWidth() + paramInt1, this.checkImage.getMeasuredHeight() + paramInt2);
    this.wasLayout = true;
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
    if ((Build.VERSION.SDK_INT >= 21) && (getBackground() != null) && ((paramMotionEvent.getAction() == 0) || (paramMotionEvent.getAction() == 2))) {
      getBackground().setHotspot(paramMotionEvent.getX(), paramMotionEvent.getY());
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  public void setAddOnClickListener(View.OnClickListener paramOnClickListener)
  {
    this.addButton.setOnClickListener(paramOnClickListener);
  }
  
  public void setDrawProgress(boolean paramBoolean)
  {
    this.drawProgress = paramBoolean;
    this.lastUpdateTime = System.currentTimeMillis();
    this.addButton.invalidate();
  }
  
  public void setStickersSet(TLRPC.StickerSetCovered paramStickerSetCovered, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i;
    label44:
    Drawable local2;
    Object localObject;
    if ((paramStickerSetCovered == this.stickersSet) && (this.wasLayout))
    {
      i = 1;
      this.needDivider = paramBoolean1;
      this.stickersSet = paramStickerSetCovered;
      this.lastUpdateTime = System.currentTimeMillis();
      if (this.needDivider) {
        break label468;
      }
      paramBoolean1 = true;
      setWillNotDraw(paramBoolean1);
      if (this.currentAnimation != null)
      {
        this.currentAnimation.cancel();
        this.currentAnimation = null;
      }
      this.textView.setText(this.stickersSet.set.title);
      if (!paramBoolean2) {
        break label486;
      }
      local2 = new Drawable()
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
        break label473;
      }
      localObject = null;
      label114:
      if (!LocaleController.isRTL) {
        break label480;
      }
      label120:
      localTextView.setCompoundDrawablesWithIntrinsicBounds((Drawable)localObject, null, local2, null);
      label131:
      this.valueTextView.setText(LocaleController.formatPluralString("Stickers", paramStickerSetCovered.set.count));
      if ((paramStickerSetCovered.cover == null) || (paramStickerSetCovered.cover.thumb == null) || (paramStickerSetCovered.cover.thumb.location == null)) {
        break label500;
      }
      this.imageView.setImage(paramStickerSetCovered.cover.thumb.location, null, "webp", null);
      label203:
      if (i == 0) {
        break label771;
      }
      paramBoolean1 = this.isInstalled;
      paramBoolean2 = StickersQuery.isStickerPackInstalled(paramStickerSetCovered.set.id);
      this.isInstalled = paramBoolean2;
      if (!paramBoolean2) {
        break label542;
      }
      if (!paramBoolean1)
      {
        this.checkImage.setVisibility(0);
        this.addButton.setClickable(false);
        this.currentAnimation = new AnimatorSet();
        this.currentAnimation.setDuration(200L);
        this.currentAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.addButton, "alpha", new float[] { 1.0F, 0.0F }), ObjectAnimator.ofFloat(this.addButton, "scaleX", new float[] { 1.0F, 0.01F }), ObjectAnimator.ofFloat(this.addButton, "scaleY", new float[] { 1.0F, 0.01F }), ObjectAnimator.ofFloat(this.checkImage, "alpha", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.checkImage, "scaleX", new float[] { 0.01F, 1.0F }), ObjectAnimator.ofFloat(this.checkImage, "scaleY", new float[] { 0.01F, 1.0F }) });
        this.currentAnimation.addListener(new AnimatorListenerAdapterProxy()
        {
          public void onAnimationCancel(Animator paramAnonymousAnimator)
          {
            if ((FeaturedStickerSetCell.this.currentAnimation != null) && (FeaturedStickerSetCell.this.currentAnimation.equals(paramAnonymousAnimator))) {
              FeaturedStickerSetCell.access$602(FeaturedStickerSetCell.this, null);
            }
          }
          
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            if ((FeaturedStickerSetCell.this.currentAnimation != null) && (FeaturedStickerSetCell.this.currentAnimation.equals(paramAnonymousAnimator))) {
              FeaturedStickerSetCell.this.addButton.setVisibility(4);
            }
          }
        });
        this.currentAnimation.start();
      }
    }
    label468:
    label473:
    label480:
    label486:
    label500:
    label542:
    while (!paramBoolean1)
    {
      return;
      i = 0;
      break;
      paramBoolean1 = false;
      break label44;
      localObject = local2;
      break label114;
      local2 = null;
      break label120;
      this.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
      break label131;
      if (paramStickerSetCovered.covers.isEmpty()) {
        break label203;
      }
      this.imageView.setImage(((TLRPC.Document)paramStickerSetCovered.covers.get(0)).thumb.location, null, "webp", null);
      break label203;
    }
    this.addButton.setVisibility(0);
    this.addButton.setClickable(true);
    this.currentAnimation = new AnimatorSet();
    this.currentAnimation.setDuration(200L);
    this.currentAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.checkImage, "alpha", new float[] { 1.0F, 0.0F }), ObjectAnimator.ofFloat(this.checkImage, "scaleX", new float[] { 1.0F, 0.01F }), ObjectAnimator.ofFloat(this.checkImage, "scaleY", new float[] { 1.0F, 0.01F }), ObjectAnimator.ofFloat(this.addButton, "alpha", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.addButton, "scaleX", new float[] { 0.01F, 1.0F }), ObjectAnimator.ofFloat(this.addButton, "scaleY", new float[] { 0.01F, 1.0F }) });
    this.currentAnimation.addListener(new AnimatorListenerAdapterProxy()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        if ((FeaturedStickerSetCell.this.currentAnimation != null) && (FeaturedStickerSetCell.this.currentAnimation.equals(paramAnonymousAnimator))) {
          FeaturedStickerSetCell.access$602(FeaturedStickerSetCell.this, null);
        }
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        if ((FeaturedStickerSetCell.this.currentAnimation != null) && (FeaturedStickerSetCell.this.currentAnimation.equals(paramAnonymousAnimator))) {
          FeaturedStickerSetCell.this.checkImage.setVisibility(4);
        }
      }
    });
    this.currentAnimation.start();
    return;
    label771:
    paramBoolean1 = StickersQuery.isStickerPackInstalled(paramStickerSetCovered.set.id);
    this.isInstalled = paramBoolean1;
    if (paramBoolean1)
    {
      this.addButton.setVisibility(4);
      this.addButton.setClickable(false);
      this.checkImage.setVisibility(0);
      this.checkImage.setScaleX(1.0F);
      this.checkImage.setScaleY(1.0F);
      this.checkImage.setAlpha(1.0F);
      return;
    }
    this.addButton.setVisibility(0);
    this.addButton.setClickable(true);
    this.checkImage.setVisibility(4);
    this.addButton.setScaleX(1.0F);
    this.addButton.setScaleY(1.0F);
    this.addButton.setAlpha(1.0F);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/FeaturedStickerSetCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
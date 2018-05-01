package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class FeaturedStickerSetCell
  extends FrameLayout
{
  private TextView addButton;
  private int angle;
  private ImageView checkImage;
  private int currentAccount = UserConfig.selectedAccount;
  private AnimatorSet currentAnimation;
  private boolean drawProgress;
  private BackupImageView imageView;
  private boolean isInstalled;
  private long lastUpdateTime;
  private boolean needDivider;
  private float progressAlpha;
  private Paint progressPaint = new Paint(1);
  private RectF progressRect = new RectF();
  private Rect rect = new Rect();
  private TLRPC.StickerSetCovered stickersSet;
  private TextView textView;
  private TextView valueTextView;
  private boolean wasLayout;
  
  public FeaturedStickerSetCell(Context paramContext)
  {
    super(paramContext);
    this.progressPaint.setColor(Theme.getColor("featuredStickers_buttonProgress"));
    this.progressPaint.setStrokeCap(Paint.Cap.ROUND);
    this.progressPaint.setStyle(Paint.Style.STROKE);
    this.progressPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    this.textView.setEllipsize(TextUtils.TruncateAt.END);
    Object localObject = this.textView;
    int j;
    label194:
    float f1;
    if (LocaleController.isRTL)
    {
      j = 5;
      ((TextView)localObject).setGravity(j);
      localObject = this.textView;
      if (!LocaleController.isRTL) {
        break label695;
      }
      j = 5;
      if (!LocaleController.isRTL) {
        break label701;
      }
      f1 = 100.0F;
      label204:
      if (!LocaleController.isRTL) {
        break label708;
      }
      f2 = 71.0F;
      label214:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, j, f1, 10.0F, f2, 0.0F));
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
      this.valueTextView.setTextSize(1, 13.0F);
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label715;
      }
      j = 5;
      label317:
      ((TextView)localObject).setGravity(j);
      localObject = this.valueTextView;
      if (!LocaleController.isRTL) {
        break label721;
      }
      j = 5;
      label337:
      if (!LocaleController.isRTL) {
        break label727;
      }
      f1 = 100.0F;
      label347:
      if (!LocaleController.isRTL) {
        break label734;
      }
      f2 = 71.0F;
      label357:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, j, f1, 35.0F, f2, 0.0F));
      this.imageView = new BackupImageView(paramContext);
      this.imageView.setAspectFit(true);
      localObject = this.imageView;
      if (!LocaleController.isRTL) {
        break label741;
      }
      j = 5;
      label412:
      if (!LocaleController.isRTL) {
        break label747;
      }
      f1 = 0.0F;
      label421:
      if (!LocaleController.isRTL) {
        break label754;
      }
      f2 = 12.0F;
      label431:
      addView((View)localObject, LayoutHelper.createFrame(48, 48.0F, j | 0x30, f1, 8.0F, f2, 0.0F));
      this.addButton = new TextView(paramContext)
      {
        protected void onDraw(Canvas paramAnonymousCanvas)
        {
          super.onDraw(paramAnonymousCanvas);
          long l1;
          long l2;
          if ((FeaturedStickerSetCell.this.drawProgress) || ((!FeaturedStickerSetCell.this.drawProgress) && (FeaturedStickerSetCell.this.progressAlpha != 0.0F)))
          {
            FeaturedStickerSetCell.this.progressPaint.setAlpha(Math.min(255, (int)(FeaturedStickerSetCell.this.progressAlpha * 255.0F)));
            int i = getMeasuredWidth() - AndroidUtilities.dp(11.0F);
            FeaturedStickerSetCell.this.progressRect.set(i, AndroidUtilities.dp(3.0F), AndroidUtilities.dp(8.0F) + i, AndroidUtilities.dp(11.0F));
            paramAnonymousCanvas.drawArc(FeaturedStickerSetCell.this.progressRect, FeaturedStickerSetCell.this.angle, 220.0F, false, FeaturedStickerSetCell.this.progressPaint);
            invalidate((int)FeaturedStickerSetCell.this.progressRect.left - AndroidUtilities.dp(2.0F), (int)FeaturedStickerSetCell.this.progressRect.top - AndroidUtilities.dp(2.0F), (int)FeaturedStickerSetCell.this.progressRect.right + AndroidUtilities.dp(2.0F), (int)FeaturedStickerSetCell.this.progressRect.bottom + AndroidUtilities.dp(2.0F));
            l1 = System.currentTimeMillis();
            if (Math.abs(FeaturedStickerSetCell.this.lastUpdateTime - System.currentTimeMillis()) < 1000L)
            {
              l2 = l1 - FeaturedStickerSetCell.this.lastUpdateTime;
              float f = (float)(360L * l2) / 2000.0F;
              FeaturedStickerSetCell.access$402(FeaturedStickerSetCell.this, (int)(FeaturedStickerSetCell.this.angle + f));
              FeaturedStickerSetCell.access$402(FeaturedStickerSetCell.this, FeaturedStickerSetCell.this.angle - FeaturedStickerSetCell.this.angle / 360 * 360);
              if (!FeaturedStickerSetCell.this.drawProgress) {
                break label382;
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
            label382:
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
      this.addButton.setGravity(17);
      this.addButton.setTextColor(Theme.getColor("featuredStickers_buttonText"));
      this.addButton.setTextSize(1, 14.0F);
      this.addButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.addButton.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0F), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
      this.addButton.setText(LocaleController.getString("Add", NUM).toUpperCase());
      this.addButton.setPadding(AndroidUtilities.dp(17.0F), 0, AndroidUtilities.dp(17.0F), 0);
      localObject = this.addButton;
      if (!LocaleController.isRTL) {
        break label760;
      }
      j = i;
      label586:
      if (!LocaleController.isRTL) {
        break label766;
      }
      f1 = 14.0F;
      label596:
      if (!LocaleController.isRTL) {
        break label772;
      }
    }
    label695:
    label701:
    label708:
    label715:
    label721:
    label727:
    label734:
    label741:
    label747:
    label754:
    label760:
    label766:
    label772:
    for (float f2 = 0.0F;; f2 = 14.0F)
    {
      addView((View)localObject, LayoutHelper.createFrame(-2, 28.0F, j | 0x30, f1, 18.0F, f2, 0.0F));
      this.checkImage = new ImageView(paramContext);
      this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), PorterDuff.Mode.MULTIPLY));
      this.checkImage.setImageResource(NUM);
      addView(this.checkImage, LayoutHelper.createFrame(19, 14.0F));
      return;
      j = 3;
      break;
      j = 3;
      break label194;
      f1 = 71.0F;
      break label204;
      f2 = 100.0F;
      break label214;
      j = 3;
      break label317;
      j = 3;
      break label337;
      f1 = 71.0F;
      break label347;
      f2 = 100.0F;
      break label357;
      j = 3;
      break label412;
      f1 = 12.0F;
      break label421;
      f2 = 0.0F;
      break label431;
      j = 5;
      break label586;
      f1 = 0.0F;
      break label596;
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
      paramCanvas.drawLine(0.0F, getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.dividerPaint);
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
    paramInt2 = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM);
    int i = AndroidUtilities.dp(64.0F);
    if (this.needDivider) {}
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      super.onMeasure(paramInt2, View.MeasureSpec.makeMeasureSpec(paramInt1 + i, NUM));
      return;
    }
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
        break label475;
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
        break label493;
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
          return -2;
        }
        
        public void setAlpha(int paramAnonymousInt) {}
        
        public void setColorFilter(ColorFilter paramAnonymousColorFilter) {}
      };
      TextView localTextView = this.textView;
      if (!LocaleController.isRTL) {
        break label480;
      }
      localObject = null;
      label114:
      if (!LocaleController.isRTL) {
        break label487;
      }
      label120:
      localTextView.setCompoundDrawablesWithIntrinsicBounds((Drawable)localObject, null, local2, null);
      label131:
      this.valueTextView.setText(LocaleController.formatPluralString("Stickers", paramStickerSetCovered.set.count));
      if ((paramStickerSetCovered.cover == null) || (paramStickerSetCovered.cover.thumb == null) || (paramStickerSetCovered.cover.thumb.location == null)) {
        break label507;
      }
      this.imageView.setImage(paramStickerSetCovered.cover.thumb.location, null, "webp", null);
      label203:
      if (i == 0) {
        break label797;
      }
      paramBoolean2 = this.isInstalled;
      paramBoolean1 = DataQuery.getInstance(this.currentAccount).isStickerPackInstalled(paramStickerSetCovered.set.id);
      this.isInstalled = paramBoolean1;
      if (!paramBoolean1) {
        break label566;
      }
      if (!paramBoolean2)
      {
        this.checkImage.setVisibility(0);
        this.addButton.setClickable(false);
        this.currentAnimation = new AnimatorSet();
        this.currentAnimation.setDuration(200L);
        this.currentAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.addButton, "alpha", new float[] { 1.0F, 0.0F }), ObjectAnimator.ofFloat(this.addButton, "scaleX", new float[] { 1.0F, 0.01F }), ObjectAnimator.ofFloat(this.addButton, "scaleY", new float[] { 1.0F, 0.01F }), ObjectAnimator.ofFloat(this.checkImage, "alpha", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.checkImage, "scaleX", new float[] { 0.01F, 1.0F }), ObjectAnimator.ofFloat(this.checkImage, "scaleY", new float[] { 0.01F, 1.0F }) });
        this.currentAnimation.addListener(new AnimatorListenerAdapter()
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
    for (;;)
    {
      return;
      i = 0;
      break;
      label475:
      paramBoolean1 = false;
      break label44;
      label480:
      localObject = local2;
      break label114;
      label487:
      local2 = null;
      break label120;
      label493:
      this.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
      break label131;
      label507:
      if ((paramStickerSetCovered.covers.isEmpty()) || (((TLRPC.Document)paramStickerSetCovered.covers.get(0)).thumb == null)) {
        break label203;
      }
      this.imageView.setImage(((TLRPC.Document)paramStickerSetCovered.covers.get(0)).thumb.location, null, "webp", null);
      break label203;
      label566:
      if (paramBoolean2)
      {
        this.addButton.setVisibility(0);
        this.addButton.setClickable(true);
        this.currentAnimation = new AnimatorSet();
        this.currentAnimation.setDuration(200L);
        this.currentAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.checkImage, "alpha", new float[] { 1.0F, 0.0F }), ObjectAnimator.ofFloat(this.checkImage, "scaleX", new float[] { 1.0F, 0.01F }), ObjectAnimator.ofFloat(this.checkImage, "scaleY", new float[] { 1.0F, 0.01F }), ObjectAnimator.ofFloat(this.addButton, "alpha", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.addButton, "scaleX", new float[] { 0.01F, 1.0F }), ObjectAnimator.ofFloat(this.addButton, "scaleY", new float[] { 0.01F, 1.0F }) });
        this.currentAnimation.addListener(new AnimatorListenerAdapter()
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
        continue;
        label797:
        paramBoolean1 = DataQuery.getInstance(this.currentAccount).isStickerPackInstalled(paramStickerSetCovered.set.id);
        this.isInstalled = paramBoolean1;
        if (paramBoolean1)
        {
          this.addButton.setVisibility(4);
          this.addButton.setClickable(false);
          this.checkImage.setVisibility(0);
          this.checkImage.setScaleX(1.0F);
          this.checkImage.setScaleY(1.0F);
          this.checkImage.setAlpha(1.0F);
        }
        else
        {
          this.addButton.setVisibility(0);
          this.addButton.setClickable(true);
          this.checkImage.setVisibility(4);
          this.addButton.setScaleX(1.0F);
          this.addButton.setScaleY(1.0F);
          this.addButton.setAlpha(1.0F);
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/FeaturedStickerSetCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
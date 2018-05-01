package org.telegram.messenger.exoplayer2.ui;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import org.telegram.messenger.exoplayer2.text.CaptionStyleCompat;
import org.telegram.messenger.exoplayer2.text.Cue;
import org.telegram.messenger.exoplayer2.util.Util;

final class SubtitlePainter
{
  private static final float INNER_PADDING_RATIO = 0.125F;
  private static final String TAG = "SubtitlePainter";
  private boolean applyEmbeddedFontSizes;
  private boolean applyEmbeddedStyles;
  private int backgroundColor;
  private Rect bitmapRect;
  private float bottomPaddingFraction;
  private final float cornerRadius;
  private Bitmap cueBitmap;
  private float cueBitmapHeight;
  private float cueLine;
  private int cueLineAnchor;
  private int cueLineType;
  private float cuePosition;
  private int cuePositionAnchor;
  private float cueSize;
  private CharSequence cueText;
  private Layout.Alignment cueTextAlignment;
  private int edgeColor;
  private int edgeType;
  private int foregroundColor;
  private final RectF lineBounds = new RectF();
  private final float outlineWidth;
  private final Paint paint;
  private int parentBottom;
  private int parentLeft;
  private int parentRight;
  private int parentTop;
  private final float shadowOffset;
  private final float shadowRadius;
  private final float spacingAdd;
  private final float spacingMult;
  private StaticLayout textLayout;
  private int textLeft;
  private int textPaddingX;
  private final TextPaint textPaint;
  private float textSizePx;
  private int textTop;
  private int windowColor;
  
  public SubtitlePainter(Context paramContext)
  {
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(null, new int[] { 16843287, 16843288 }, 0, 0);
    this.spacingAdd = localTypedArray.getDimensionPixelSize(0, 0);
    this.spacingMult = localTypedArray.getFloat(1, 1.0F);
    localTypedArray.recycle();
    int i = Math.round(2.0F * paramContext.getResources().getDisplayMetrics().densityDpi / 160.0F);
    this.cornerRadius = i;
    this.outlineWidth = i;
    this.shadowRadius = i;
    this.shadowOffset = i;
    this.textPaint = new TextPaint();
    this.textPaint.setAntiAlias(true);
    this.textPaint.setSubpixelText(true);
    this.paint = new Paint();
    this.paint.setAntiAlias(true);
    this.paint.setStyle(Paint.Style.FILL);
  }
  
  private static boolean areCharSequencesEqual(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    return (paramCharSequence1 == paramCharSequence2) || ((paramCharSequence1 != null) && (paramCharSequence1.equals(paramCharSequence2)));
  }
  
  private void drawBitmapLayout(Canvas paramCanvas)
  {
    paramCanvas.drawBitmap(this.cueBitmap, null, this.bitmapRect, null);
  }
  
  private void drawLayout(Canvas paramCanvas, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      drawTextLayout(paramCanvas);
      return;
    }
    drawBitmapLayout(paramCanvas);
  }
  
  private void drawTextLayout(Canvas paramCanvas)
  {
    StaticLayout localStaticLayout = this.textLayout;
    if (localStaticLayout == null) {
      return;
    }
    int k = paramCanvas.save();
    paramCanvas.translate(this.textLeft, this.textTop);
    if (Color.alpha(this.windowColor) > 0)
    {
      this.paint.setColor(this.windowColor);
      paramCanvas.drawRect(-this.textPaddingX, 0.0F, localStaticLayout.getWidth() + this.textPaddingX, localStaticLayout.getHeight(), this.paint);
    }
    float f;
    int i;
    if (Color.alpha(this.backgroundColor) > 0)
    {
      this.paint.setColor(this.backgroundColor);
      f = localStaticLayout.getLineTop(0);
      j = localStaticLayout.getLineCount();
      i = 0;
      while (i < j)
      {
        this.lineBounds.left = (localStaticLayout.getLineLeft(i) - this.textPaddingX);
        this.lineBounds.right = (localStaticLayout.getLineRight(i) + this.textPaddingX);
        this.lineBounds.top = f;
        this.lineBounds.bottom = localStaticLayout.getLineBottom(i);
        f = this.lineBounds.bottom;
        paramCanvas.drawRoundRect(this.lineBounds, this.cornerRadius, this.cornerRadius, this.paint);
        i += 1;
      }
    }
    if (this.edgeType == 1)
    {
      this.textPaint.setStrokeJoin(Paint.Join.ROUND);
      this.textPaint.setStrokeWidth(this.outlineWidth);
      this.textPaint.setColor(this.edgeColor);
      this.textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
      localStaticLayout.draw(paramCanvas);
    }
    do
    {
      for (;;)
      {
        this.textPaint.setColor(this.foregroundColor);
        this.textPaint.setStyle(Paint.Style.FILL);
        localStaticLayout.draw(paramCanvas);
        this.textPaint.setShadowLayer(0.0F, 0.0F, 0.0F, 0);
        paramCanvas.restoreToCount(k);
        return;
        if (this.edgeType != 2) {
          break;
        }
        this.textPaint.setShadowLayer(this.shadowRadius, this.shadowOffset, this.shadowOffset, this.edgeColor);
      }
    } while ((this.edgeType != 3) && (this.edgeType != 4));
    if (this.edgeType == 3)
    {
      j = 1;
      label386:
      if (j == 0) {
        break label478;
      }
      i = -1;
      label393:
      if (j == 0) {
        break label486;
      }
    }
    label478:
    label486:
    for (int j = this.edgeColor;; j = -1)
    {
      f = this.shadowRadius / 2.0F;
      this.textPaint.setColor(this.foregroundColor);
      this.textPaint.setStyle(Paint.Style.FILL);
      this.textPaint.setShadowLayer(this.shadowRadius, -f, -f, i);
      localStaticLayout.draw(paramCanvas);
      this.textPaint.setShadowLayer(this.shadowRadius, f, f, j);
      break;
      j = 0;
      break label386;
      i = this.edgeColor;
      break label393;
    }
  }
  
  private void setupBitmapLayout()
  {
    int j = this.parentRight - this.parentLeft;
    int i = this.parentBottom - this.parentTop;
    float f3 = this.parentLeft + j * this.cuePosition;
    float f2 = this.parentTop + i * this.cueLine;
    j = Math.round(j * this.cueSize);
    float f1;
    label103:
    int k;
    if (this.cueBitmapHeight != Float.MIN_VALUE)
    {
      i = Math.round(i * this.cueBitmapHeight);
      if (this.cueLineAnchor != 2) {
        break label184;
      }
      f1 = f3 - j;
      k = Math.round(f1);
      if (this.cuePositionAnchor != 2) {
        break label205;
      }
      f1 = f2 - i;
    }
    for (;;)
    {
      int m = Math.round(f1);
      this.bitmapRect = new Rect(k, m, k + j, m + i);
      return;
      i = Math.round(j * (this.cueBitmap.getHeight() / this.cueBitmap.getWidth()));
      break;
      label184:
      f1 = f3;
      if (this.cueLineAnchor != 1) {
        break label103;
      }
      f1 = f3 - j / 2;
      break label103;
      label205:
      f1 = f2;
      if (this.cuePositionAnchor == 1) {
        f1 = f2 - i / 2;
      }
    }
  }
  
  private void setupTextLayout()
  {
    int i2 = this.parentRight - this.parentLeft;
    int n = this.parentBottom - this.parentTop;
    this.textPaint.setTextSize(this.textSizePx);
    int m = (int)(this.textSizePx * 0.125F + 0.5F);
    int j = i2 - m * 2;
    int i = j;
    if (this.cueSize != Float.MIN_VALUE) {
      i = (int)(j * this.cueSize);
    }
    if (i <= 0)
    {
      Log.w("SubtitlePainter", "Skipped drawing subtitle cue (insufficient space)");
      return;
    }
    Object localObject1;
    if ((this.applyEmbeddedFontSizes) && (this.applyEmbeddedStyles))
    {
      localObject1 = this.cueText;
      if (this.cueTextAlignment != null) {
        break label332;
      }
    }
    int i1;
    label332:
    for (Object localObject2 = Layout.Alignment.ALIGN_CENTER;; localObject2 = this.cueTextAlignment)
    {
      this.textLayout = new StaticLayout((CharSequence)localObject1, this.textPaint, i, (Layout.Alignment)localObject2, this.spacingMult, this.spacingAdd, true);
      i1 = this.textLayout.getHeight();
      j = 0;
      int i3 = this.textLayout.getLineCount();
      k = 0;
      while (k < i3)
      {
        j = Math.max((int)Math.ceil(this.textLayout.getLineWidth(k)), j);
        k += 1;
      }
      if (!this.applyEmbeddedStyles)
      {
        localObject1 = this.cueText.toString();
        break;
      }
      localObject1 = new SpannableStringBuilder(this.cueText);
      j = ((SpannableStringBuilder)localObject1).length();
      localObject2 = (AbsoluteSizeSpan[])((SpannableStringBuilder)localObject1).getSpans(0, j, AbsoluteSizeSpan.class);
      RelativeSizeSpan[] arrayOfRelativeSizeSpan = (RelativeSizeSpan[])((SpannableStringBuilder)localObject1).getSpans(0, j, RelativeSizeSpan.class);
      k = localObject2.length;
      j = 0;
      while (j < k)
      {
        ((SpannableStringBuilder)localObject1).removeSpan(localObject2[j]);
        j += 1;
      }
      k = arrayOfRelativeSizeSpan.length;
      j = 0;
      while (j < k)
      {
        ((SpannableStringBuilder)localObject1).removeSpan(arrayOfRelativeSizeSpan[j]);
        j += 1;
      }
      break;
    }
    int k = j;
    if (this.cueSize != Float.MIN_VALUE)
    {
      k = j;
      if (j < i) {
        k = i;
      }
    }
    j = k + m * 2;
    if (this.cuePosition != Float.MIN_VALUE)
    {
      i = Math.round(i2 * this.cuePosition) + this.parentLeft;
      if (this.cuePositionAnchor == 2)
      {
        i -= j;
        k = Math.max(i, this.parentLeft);
      }
    }
    for (i = Math.min(k + j, this.parentRight);; i = k + j)
    {
      i2 = i - k;
      if (i2 > 0) {
        break label486;
      }
      Log.w("SubtitlePainter", "Skipped drawing subtitle cue (invalid horizontal positioning)");
      return;
      if (this.cuePositionAnchor == 1)
      {
        i = (i * 2 - j) / 2;
        break;
      }
      break;
      k = (i2 - j) / 2;
    }
    label486:
    if (this.cueLine != Float.MIN_VALUE) {
      if (this.cueLineType == 0)
      {
        i = Math.round(n * this.cueLine) + this.parentTop;
        if (this.cueLineAnchor != 2) {
          break label667;
        }
        i -= i1;
        label534:
        if (i + i1 <= this.parentBottom) {
          break label690;
        }
        j = this.parentBottom - i1;
      }
    }
    for (;;)
    {
      this.textLayout = new StaticLayout((CharSequence)localObject1, this.textPaint, i2, (Layout.Alignment)localObject2, this.spacingMult, this.spacingAdd, true);
      this.textLeft = k;
      this.textTop = j;
      this.textPaddingX = m;
      return;
      i = this.textLayout.getLineBottom(0) - this.textLayout.getLineTop(0);
      if (this.cueLine >= 0.0F)
      {
        i = Math.round(this.cueLine * i) + this.parentTop;
        break;
      }
      i = Math.round((this.cueLine + 1.0F) * i) + this.parentBottom;
      break;
      label667:
      if (this.cueLineAnchor == 1)
      {
        i = (i * 2 - i1) / 2;
        break label534;
      }
      break label534;
      label690:
      j = i;
      if (i < this.parentTop)
      {
        j = this.parentTop;
        continue;
        j = this.parentBottom - i1 - (int)(n * this.bottomPaddingFraction);
      }
    }
  }
  
  public void draw(Cue paramCue, boolean paramBoolean1, boolean paramBoolean2, CaptionStyleCompat paramCaptionStyleCompat, float paramFloat1, float paramFloat2, Canvas paramCanvas, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramCue.bitmap == null) {}
    for (boolean bool = true;; bool = false)
    {
      i = -16777216;
      if (!bool) {
        break label54;
      }
      if (!TextUtils.isEmpty(paramCue.text)) {
        break;
      }
      return;
    }
    if ((paramCue.windowColorSet) && (paramBoolean1)) {}
    for (int i = paramCue.windowColor; (areCharSequencesEqual(this.cueText, paramCue.text)) && (Util.areEqual(this.cueTextAlignment, paramCue.textAlignment)) && (this.cueBitmap == paramCue.bitmap) && (this.cueLine == paramCue.line) && (this.cueLineType == paramCue.lineType) && (Util.areEqual(Integer.valueOf(this.cueLineAnchor), Integer.valueOf(paramCue.lineAnchor))) && (this.cuePosition == paramCue.position) && (Util.areEqual(Integer.valueOf(this.cuePositionAnchor), Integer.valueOf(paramCue.positionAnchor))) && (this.cueSize == paramCue.size) && (this.cueBitmapHeight == paramCue.bitmapHeight) && (this.applyEmbeddedStyles == paramBoolean1) && (this.applyEmbeddedFontSizes == paramBoolean2) && (this.foregroundColor == paramCaptionStyleCompat.foregroundColor) && (this.backgroundColor == paramCaptionStyleCompat.backgroundColor) && (this.windowColor == i) && (this.edgeType == paramCaptionStyleCompat.edgeType) && (this.edgeColor == paramCaptionStyleCompat.edgeColor) && (Util.areEqual(this.textPaint.getTypeface(), paramCaptionStyleCompat.typeface)) && (this.textSizePx == paramFloat1) && (this.bottomPaddingFraction == paramFloat2) && (this.parentLeft == paramInt1) && (this.parentTop == paramInt2) && (this.parentRight == paramInt3) && (this.parentBottom == paramInt4); i = paramCaptionStyleCompat.windowColor)
    {
      label54:
      drawLayout(paramCanvas, bool);
      return;
    }
    this.cueText = paramCue.text;
    this.cueTextAlignment = paramCue.textAlignment;
    this.cueBitmap = paramCue.bitmap;
    this.cueLine = paramCue.line;
    this.cueLineType = paramCue.lineType;
    this.cueLineAnchor = paramCue.lineAnchor;
    this.cuePosition = paramCue.position;
    this.cuePositionAnchor = paramCue.positionAnchor;
    this.cueSize = paramCue.size;
    this.cueBitmapHeight = paramCue.bitmapHeight;
    this.applyEmbeddedStyles = paramBoolean1;
    this.applyEmbeddedFontSizes = paramBoolean2;
    this.foregroundColor = paramCaptionStyleCompat.foregroundColor;
    this.backgroundColor = paramCaptionStyleCompat.backgroundColor;
    this.windowColor = i;
    this.edgeType = paramCaptionStyleCompat.edgeType;
    this.edgeColor = paramCaptionStyleCompat.edgeColor;
    this.textPaint.setTypeface(paramCaptionStyleCompat.typeface);
    this.textSizePx = paramFloat1;
    this.bottomPaddingFraction = paramFloat2;
    this.parentLeft = paramInt1;
    this.parentTop = paramInt2;
    this.parentRight = paramInt3;
    this.parentBottom = paramInt4;
    if (bool) {
      setupTextLayout();
    }
    for (;;)
    {
      drawLayout(paramCanvas, bool);
      return;
      setupBitmapLayout();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/ui/SubtitlePainter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
package org.telegram.messenger.exoplayer2.ui;

import android.content.Context;
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
import android.util.Log;
import org.telegram.messenger.exoplayer2.text.CaptionStyleCompat;
import org.telegram.messenger.exoplayer2.text.Cue;
import org.telegram.messenger.exoplayer2.util.Util;

final class SubtitlePainter {
    private static final float INNER_PADDING_RATIO = 0.125f;
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
    private Alignment cueTextAlignment;
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

    public SubtitlePainter(Context context) {
        TypedArray styledAttributes = context.obtainStyledAttributes(null, new int[]{16843287, 16843288}, 0, 0);
        this.spacingAdd = (float) styledAttributes.getDimensionPixelSize(0, 0);
        this.spacingMult = styledAttributes.getFloat(1, 1.0f);
        styledAttributes.recycle();
        int twoDpInPx = Math.round((2.0f * ((float) context.getResources().getDisplayMetrics().densityDpi)) / 160.0f);
        this.cornerRadius = (float) twoDpInPx;
        this.outlineWidth = (float) twoDpInPx;
        this.shadowRadius = (float) twoDpInPx;
        this.shadowOffset = (float) twoDpInPx;
        this.textPaint = new TextPaint();
        this.textPaint.setAntiAlias(true);
        this.textPaint.setSubpixelText(true);
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Style.FILL);
    }

    public void draw(Cue cue, boolean applyEmbeddedStyles, boolean applyEmbeddedFontSizes, CaptionStyleCompat style, float textSizePx, float bottomPaddingFraction, Canvas canvas, int cueBoxLeft, int cueBoxTop, int cueBoxRight, int cueBoxBottom) {
        boolean isTextCue = cue.bitmap == null;
        int windowColor = -16777216;
        if (isTextCue) {
            if (!TextUtils.isEmpty(cue.text)) {
                windowColor = (cue.windowColorSet && applyEmbeddedStyles) ? cue.windowColor : style.windowColor;
            } else {
                return;
            }
        }
        if (areCharSequencesEqual(this.cueText, cue.text) && Util.areEqual(this.cueTextAlignment, cue.textAlignment) && this.cueBitmap == cue.bitmap && this.cueLine == cue.line && this.cueLineType == cue.lineType && Util.areEqual(Integer.valueOf(this.cueLineAnchor), Integer.valueOf(cue.lineAnchor)) && this.cuePosition == cue.position && Util.areEqual(Integer.valueOf(this.cuePositionAnchor), Integer.valueOf(cue.positionAnchor)) && this.cueSize == cue.size && this.cueBitmapHeight == cue.bitmapHeight && this.applyEmbeddedStyles == applyEmbeddedStyles && this.applyEmbeddedFontSizes == applyEmbeddedFontSizes && this.foregroundColor == style.foregroundColor && this.backgroundColor == style.backgroundColor && this.windowColor == windowColor && this.edgeType == style.edgeType && this.edgeColor == style.edgeColor && Util.areEqual(this.textPaint.getTypeface(), style.typeface) && this.textSizePx == textSizePx && this.bottomPaddingFraction == bottomPaddingFraction && this.parentLeft == cueBoxLeft && this.parentTop == cueBoxTop && this.parentRight == cueBoxRight && this.parentBottom == cueBoxBottom) {
            drawLayout(canvas, isTextCue);
            return;
        }
        this.cueText = cue.text;
        this.cueTextAlignment = cue.textAlignment;
        this.cueBitmap = cue.bitmap;
        this.cueLine = cue.line;
        this.cueLineType = cue.lineType;
        this.cueLineAnchor = cue.lineAnchor;
        this.cuePosition = cue.position;
        this.cuePositionAnchor = cue.positionAnchor;
        this.cueSize = cue.size;
        this.cueBitmapHeight = cue.bitmapHeight;
        this.applyEmbeddedStyles = applyEmbeddedStyles;
        this.applyEmbeddedFontSizes = applyEmbeddedFontSizes;
        this.foregroundColor = style.foregroundColor;
        this.backgroundColor = style.backgroundColor;
        this.windowColor = windowColor;
        this.edgeType = style.edgeType;
        this.edgeColor = style.edgeColor;
        this.textPaint.setTypeface(style.typeface);
        this.textSizePx = textSizePx;
        this.bottomPaddingFraction = bottomPaddingFraction;
        this.parentLeft = cueBoxLeft;
        this.parentTop = cueBoxTop;
        this.parentRight = cueBoxRight;
        this.parentBottom = cueBoxBottom;
        if (isTextCue) {
            setupTextLayout();
        } else {
            setupBitmapLayout();
        }
        drawLayout(canvas, isTextCue);
    }

    private void setupTextLayout() {
        int parentWidth = this.parentRight - this.parentLeft;
        int parentHeight = this.parentBottom - this.parentTop;
        this.textPaint.setTextSize(this.textSizePx);
        int textPaddingX = (int) ((this.textSizePx * INNER_PADDING_RATIO) + 0.5f);
        int availableWidth = parentWidth - (textPaddingX * 2);
        if (this.cueSize != Cue.DIMEN_UNSET) {
            availableWidth = (int) (((float) availableWidth) * this.cueSize);
        }
        if (availableWidth <= 0) {
            Log.w(TAG, "Skipped drawing subtitle cue (insufficient space)");
            return;
        }
        CharSequence cueText;
        int textLeft;
        int textRight;
        if (this.applyEmbeddedFontSizes && this.applyEmbeddedStyles) {
            cueText = this.cueText;
        } else if (this.applyEmbeddedStyles) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.cueText);
            int cueLength = spannableStringBuilder.length();
            RelativeSizeSpan[] relSpans = (RelativeSizeSpan[]) spannableStringBuilder.getSpans(0, cueLength, RelativeSizeSpan.class);
            for (AbsoluteSizeSpan absSpan : (AbsoluteSizeSpan[]) spannableStringBuilder.getSpans(0, cueLength, AbsoluteSizeSpan.class)) {
                spannableStringBuilder.removeSpan(absSpan);
            }
            for (RelativeSizeSpan relSpan : relSpans) {
                spannableStringBuilder.removeSpan(relSpan);
            }
            Object cueText2 = spannableStringBuilder;
        } else {
            cueText = this.cueText.toString();
        }
        Alignment textAlignment = this.cueTextAlignment == null ? Alignment.ALIGN_CENTER : this.cueTextAlignment;
        this.textLayout = new StaticLayout(cueText, this.textPaint, availableWidth, textAlignment, this.spacingMult, this.spacingAdd, true);
        int textHeight = this.textLayout.getHeight();
        int textWidth = 0;
        int lineCount = this.textLayout.getLineCount();
        for (int i = 0; i < lineCount; i++) {
            textWidth = Math.max((int) Math.ceil((double) this.textLayout.getLineWidth(i)), textWidth);
        }
        if (this.cueSize != Cue.DIMEN_UNSET && textWidth < availableWidth) {
            textWidth = availableWidth;
        }
        textWidth += textPaddingX * 2;
        if (this.cuePosition != Cue.DIMEN_UNSET) {
            int anchorPosition = Math.round(((float) parentWidth) * this.cuePosition) + this.parentLeft;
            textLeft = this.cuePositionAnchor == 2 ? anchorPosition - textWidth : this.cuePositionAnchor == 1 ? ((anchorPosition * 2) - textWidth) / 2 : anchorPosition;
            textLeft = Math.max(textLeft, this.parentLeft);
            textRight = Math.min(textLeft + textWidth, this.parentRight);
        } else {
            textLeft = (parentWidth - textWidth) / 2;
            textRight = textLeft + textWidth;
        }
        textWidth = textRight - textLeft;
        if (textWidth <= 0) {
            Log.w(TAG, "Skipped drawing subtitle cue (invalid horizontal positioning)");
            return;
        }
        int textTop;
        if (this.cueLine != Cue.DIMEN_UNSET) {
            if (this.cueLineType == 0) {
                anchorPosition = Math.round(((float) parentHeight) * this.cueLine) + this.parentTop;
            } else {
                int firstLineHeight = this.textLayout.getLineBottom(0) - this.textLayout.getLineTop(0);
                if (this.cueLine >= 0.0f) {
                    anchorPosition = Math.round(this.cueLine * ((float) firstLineHeight)) + this.parentTop;
                } else {
                    anchorPosition = Math.round((this.cueLine + 1.0f) * ((float) firstLineHeight)) + this.parentBottom;
                }
            }
            textTop = this.cueLineAnchor == 2 ? anchorPosition - textHeight : this.cueLineAnchor == 1 ? ((anchorPosition * 2) - textHeight) / 2 : anchorPosition;
            if (textTop + textHeight > this.parentBottom) {
                textTop = this.parentBottom - textHeight;
            } else if (textTop < this.parentTop) {
                textTop = this.parentTop;
            }
        } else {
            textTop = (this.parentBottom - textHeight) - ((int) (((float) parentHeight) * this.bottomPaddingFraction));
        }
        this.textLayout = new StaticLayout(cueText, this.textPaint, textWidth, textAlignment, this.spacingMult, this.spacingAdd, true);
        this.textLeft = textLeft;
        this.textTop = textTop;
        this.textPaddingX = textPaddingX;
    }

    private void setupBitmapLayout() {
        int height;
        int parentWidth = this.parentRight - this.parentLeft;
        int parentHeight = this.parentBottom - this.parentTop;
        float anchorX = ((float) this.parentLeft) + (((float) parentWidth) * this.cuePosition);
        float anchorY = ((float) this.parentTop) + (((float) parentHeight) * this.cueLine);
        int width = Math.round(((float) parentWidth) * this.cueSize);
        if (this.cueBitmapHeight != Cue.DIMEN_UNSET) {
            height = Math.round(((float) parentHeight) * this.cueBitmapHeight);
        } else {
            height = Math.round(((float) width) * (((float) this.cueBitmap.getHeight()) / ((float) this.cueBitmap.getWidth())));
        }
        if (this.cueLineAnchor == 2) {
            anchorX -= (float) width;
        } else if (this.cueLineAnchor == 1) {
            anchorX -= (float) (width / 2);
        }
        int x = Math.round(anchorX);
        if (this.cuePositionAnchor == 2) {
            anchorY -= (float) height;
        } else if (this.cuePositionAnchor == 1) {
            anchorY -= (float) (height / 2);
        }
        int y = Math.round(anchorY);
        this.bitmapRect = new Rect(x, y, x + width, y + height);
    }

    private void drawLayout(Canvas canvas, boolean isTextCue) {
        if (isTextCue) {
            drawTextLayout(canvas);
        } else {
            drawBitmapLayout(canvas);
        }
    }

    private void drawTextLayout(Canvas canvas) {
        StaticLayout layout = this.textLayout;
        if (layout != null) {
            int saveCount = canvas.save();
            canvas.translate((float) this.textLeft, (float) this.textTop);
            if (Color.alpha(this.windowColor) > 0) {
                this.paint.setColor(this.windowColor);
                canvas.drawRect((float) (-this.textPaddingX), 0.0f, (float) (layout.getWidth() + this.textPaddingX), (float) layout.getHeight(), this.paint);
            }
            if (Color.alpha(this.backgroundColor) > 0) {
                this.paint.setColor(this.backgroundColor);
                float previousBottom = (float) layout.getLineTop(0);
                int lineCount = layout.getLineCount();
                for (int i = 0; i < lineCount; i++) {
                    this.lineBounds.left = layout.getLineLeft(i) - ((float) this.textPaddingX);
                    this.lineBounds.right = layout.getLineRight(i) + ((float) this.textPaddingX);
                    this.lineBounds.top = previousBottom;
                    this.lineBounds.bottom = (float) layout.getLineBottom(i);
                    previousBottom = this.lineBounds.bottom;
                    canvas.drawRoundRect(this.lineBounds, this.cornerRadius, this.cornerRadius, this.paint);
                }
            }
            if (this.edgeType == 1) {
                this.textPaint.setStrokeJoin(Join.ROUND);
                this.textPaint.setStrokeWidth(this.outlineWidth);
                this.textPaint.setColor(this.edgeColor);
                this.textPaint.setStyle(Style.FILL_AND_STROKE);
                layout.draw(canvas);
            } else if (this.edgeType == 2) {
                this.textPaint.setShadowLayer(this.shadowRadius, this.shadowOffset, this.shadowOffset, this.edgeColor);
            } else if (this.edgeType == 3 || this.edgeType == 4) {
                boolean raised = this.edgeType == 3;
                int colorUp = raised ? -1 : this.edgeColor;
                int colorDown = raised ? this.edgeColor : -1;
                float offset = this.shadowRadius / 2.0f;
                this.textPaint.setColor(this.foregroundColor);
                this.textPaint.setStyle(Style.FILL);
                this.textPaint.setShadowLayer(this.shadowRadius, -offset, -offset, colorUp);
                layout.draw(canvas);
                this.textPaint.setShadowLayer(this.shadowRadius, offset, offset, colorDown);
            }
            this.textPaint.setColor(this.foregroundColor);
            this.textPaint.setStyle(Style.FILL);
            layout.draw(canvas);
            this.textPaint.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
            canvas.restoreToCount(saveCount);
        }
    }

    private void drawBitmapLayout(Canvas canvas) {
        canvas.drawBitmap(this.cueBitmap, null, this.bitmapRect, null);
    }

    private static boolean areCharSequencesEqual(CharSequence first, CharSequence second) {
        return first == second || (first != null && first.equals(second));
    }
}

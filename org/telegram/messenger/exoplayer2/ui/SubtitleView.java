package org.telegram.messenger.exoplayer2.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.accessibility.CaptioningManager;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer2.text.CaptionStyleCompat;
import org.telegram.messenger.exoplayer2.text.Cue;
import org.telegram.messenger.exoplayer2.text.TextRenderer.Output;
import org.telegram.messenger.exoplayer2.util.Util;

public final class SubtitleView extends View implements Output {
    private static final int ABSOLUTE = 2;
    public static final float DEFAULT_BOTTOM_PADDING_FRACTION = 0.08f;
    public static final float DEFAULT_TEXT_SIZE_FRACTION = 0.0533f;
    private static final int FRACTIONAL = 0;
    private static final int FRACTIONAL_IGNORE_PADDING = 1;
    private boolean applyEmbeddedStyles;
    private float bottomPaddingFraction;
    private List<Cue> cues;
    private final List<SubtitlePainter> painters;
    private CaptionStyleCompat style;
    private float textSize;
    private int textSizeType;

    public SubtitleView(Context context) {
        this(context, null);
    }

    public SubtitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.painters = new ArrayList();
        this.textSizeType = 0;
        this.textSize = DEFAULT_TEXT_SIZE_FRACTION;
        this.applyEmbeddedStyles = true;
        this.style = CaptionStyleCompat.DEFAULT;
        this.bottomPaddingFraction = DEFAULT_BOTTOM_PADDING_FRACTION;
    }

    public void onCues(List<Cue> cues) {
        setCues(cues);
    }

    public void setCues(List<Cue> cues) {
        if (this.cues != cues) {
            this.cues = cues;
            int cueCount = cues == null ? 0 : cues.size();
            while (this.painters.size() < cueCount) {
                this.painters.add(new SubtitlePainter(getContext()));
            }
            invalidate();
        }
    }

    public void setFixedTextSize(int unit, float size) {
        Resources resources;
        Context context = getContext();
        if (context == null) {
            resources = Resources.getSystem();
        } else {
            resources = context.getResources();
        }
        setTextSize(2, TypedValue.applyDimension(unit, size, resources.getDisplayMetrics()));
    }

    public void setUserDefaultTextSize() {
        float fontScale = (Util.SDK_INT < 19 || isInEditMode()) ? 1.0f : getUserCaptionFontScaleV19();
        setFractionalTextSize(DEFAULT_TEXT_SIZE_FRACTION * fontScale);
    }

    public void setFractionalTextSize(float fractionOfHeight) {
        setFractionalTextSize(fractionOfHeight, false);
    }

    public void setFractionalTextSize(float fractionOfHeight, boolean ignorePadding) {
        setTextSize(ignorePadding ? 1 : 0, fractionOfHeight);
    }

    private void setTextSize(int textSizeType, float textSize) {
        if (this.textSizeType != textSizeType || this.textSize != textSize) {
            this.textSizeType = textSizeType;
            this.textSize = textSize;
            invalidate();
        }
    }

    public void setApplyEmbeddedStyles(boolean applyEmbeddedStyles) {
        if (this.applyEmbeddedStyles != applyEmbeddedStyles) {
            this.applyEmbeddedStyles = applyEmbeddedStyles;
            invalidate();
        }
    }

    public void setUserDefaultStyle() {
        CaptionStyleCompat userCaptionStyleV19 = (Util.SDK_INT < 19 || isInEditMode()) ? CaptionStyleCompat.DEFAULT : getUserCaptionStyleV19();
        setStyle(userCaptionStyleV19);
    }

    public void setStyle(CaptionStyleCompat style) {
        if (this.style != style) {
            this.style = style;
            invalidate();
        }
    }

    public void setBottomPaddingFraction(float bottomPaddingFraction) {
        if (this.bottomPaddingFraction != bottomPaddingFraction) {
            this.bottomPaddingFraction = bottomPaddingFraction;
            invalidate();
        }
    }

    public void dispatchDraw(Canvas canvas) {
        int cueCount;
        if (this.cues == null) {
            cueCount = 0;
        } else {
            cueCount = this.cues.size();
        }
        int rawTop = getTop();
        int rawBottom = getBottom();
        int left = getLeft() + getPaddingLeft();
        int top = rawTop + getPaddingTop();
        int right = getRight() + getPaddingRight();
        int bottom = rawBottom - getPaddingBottom();
        if (bottom > top && right > left) {
            float textSizePx;
            if (this.textSizeType == 2) {
                textSizePx = this.textSize;
            } else {
                textSizePx = this.textSize * ((float) (this.textSizeType == 0 ? bottom - top : rawBottom - rawTop));
            }
            if (textSizePx > 0.0f) {
                for (int i = 0; i < cueCount; i++) {
                    ((SubtitlePainter) this.painters.get(i)).draw((Cue) this.cues.get(i), this.applyEmbeddedStyles, this.style, textSizePx, this.bottomPaddingFraction, canvas, left, top, right, bottom);
                }
            }
        }
    }

    @TargetApi(19)
    private float getUserCaptionFontScaleV19() {
        return ((CaptioningManager) getContext().getSystemService("captioning")).getFontScale();
    }

    @TargetApi(19)
    private CaptionStyleCompat getUserCaptionStyleV19() {
        return CaptionStyleCompat.createFromCaptionStyle(((CaptioningManager) getContext().getSystemService("captioning")).getUserStyle());
    }
}

package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class SeekBarView extends FrameLayout {
    private float bufferedProgress;
    boolean captured;
    private float currentRadius;
    public SeekBarViewDelegate delegate;
    private Drawable hoverDrawable;
    private Paint innerPaint1;
    private long lastUpdateTime;
    private Paint outerPaint1;
    /* access modifiers changed from: private */
    public boolean pressed;
    private int[] pressedState;
    private float progressToSet;
    private boolean reportChanges;
    private final Theme.ResourcesProvider resourcesProvider;
    private final SeekBarAccessibilityDelegate seekBarAccessibilityDelegate;
    private int selectorWidth;
    float sx;
    float sy;
    private int thumbDX;
    private int thumbSize;
    private int thumbX;
    private float transitionProgress;
    private int transitionThumbX;
    private boolean twoSided;

    public interface SeekBarViewDelegate {
        CharSequence getContentDescription();

        int getStepsCount();

        void onSeekBarDrag(boolean z, float f);

        void onSeekBarPressed(boolean z);

        /* renamed from: org.telegram.ui.Components.SeekBarView$SeekBarViewDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static CharSequence $default$getContentDescription(SeekBarViewDelegate _this) {
                return null;
            }

            public static int $default$getStepsCount(SeekBarViewDelegate _this) {
                return 0;
            }
        }
    }

    public SeekBarView(Context context) {
        this(context, (Theme.ResourcesProvider) null);
    }

    public SeekBarView(Context context, Theme.ResourcesProvider resourcesProvider2) {
        this(context, false, resourcesProvider2);
    }

    public SeekBarView(Context context, boolean inPercents, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.progressToSet = -100.0f;
        this.pressedState = new int[]{16842910, 16842919};
        this.transitionProgress = 1.0f;
        this.resourcesProvider = resourcesProvider2;
        setWillNotDraw(false);
        this.innerPaint1 = new Paint(1);
        Paint paint = new Paint(1);
        this.outerPaint1 = paint;
        paint.setColor(getThemedColor("player_progress"));
        this.selectorWidth = AndroidUtilities.dp(32.0f);
        this.thumbSize = AndroidUtilities.dp(24.0f);
        this.currentRadius = (float) AndroidUtilities.dp(6.0f);
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable createSelectorDrawable = Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(getThemedColor("player_progress"), 40), 1, AndroidUtilities.dp(16.0f));
            this.hoverDrawable = createSelectorDrawable;
            createSelectorDrawable.setCallback(this);
            this.hoverDrawable.setVisible(true, false);
        }
        setImportantForAccessibility(1);
        AnonymousClass1 r0 = new FloatSeekBarAccessibilityDelegate(inPercents) {
            public float getProgress() {
                return SeekBarView.this.getProgress();
            }

            public void setProgress(float progress) {
                boolean unused = SeekBarView.this.pressed = true;
                SeekBarView.this.setProgress(progress);
                if (SeekBarView.this.delegate != null) {
                    SeekBarView.this.delegate.onSeekBarDrag(true, progress);
                }
                boolean unused2 = SeekBarView.this.pressed = false;
            }

            /* access modifiers changed from: protected */
            public float getDelta() {
                int stepsCount = SeekBarView.this.delegate.getStepsCount();
                if (stepsCount > 0) {
                    return 1.0f / ((float) stepsCount);
                }
                return super.getDelta();
            }

            public CharSequence getContentDescription(View host) {
                if (SeekBarView.this.delegate != null) {
                    return SeekBarView.this.delegate.getContentDescription();
                }
                return null;
            }
        };
        this.seekBarAccessibilityDelegate = r0;
        setAccessibilityDelegate(r0);
    }

    public void setColors(int inner, int outer) {
        this.innerPaint1.setColor(inner);
        this.outerPaint1.setColor(outer);
        Drawable drawable = this.hoverDrawable;
        if (drawable != null) {
            Theme.setSelectorDrawableColor(drawable, ColorUtils.setAlphaComponent(outer, 40), true);
        }
    }

    public void setTwoSided(boolean value) {
        this.twoSided = value;
    }

    public boolean isTwoSided() {
        return this.twoSided;
    }

    public void setInnerColor(int color) {
        this.innerPaint1.setColor(color);
    }

    public void setOuterColor(int color) {
        this.outerPaint1.setColor(color);
        Drawable drawable = this.hoverDrawable;
        if (drawable != null) {
            Theme.setSelectorDrawableColor(drawable, ColorUtils.setAlphaComponent(color, 40), true);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return onTouch(ev);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return onTouch(event);
    }

    public void setReportChanges(boolean value) {
        this.reportChanges = value;
    }

    public void setDelegate(SeekBarViewDelegate seekBarViewDelegate) {
        this.delegate = seekBarViewDelegate;
    }

    /* access modifiers changed from: package-private */
    public boolean onTouch(MotionEvent ev) {
        Drawable drawable;
        Drawable drawable2;
        Drawable drawable3;
        if (ev.getAction() == 0) {
            this.sx = ev.getX();
            this.sy = ev.getY();
            return true;
        }
        if (ev.getAction() == 1 || ev.getAction() == 3) {
            this.captured = false;
            if (ev.getAction() == 1) {
                if (Math.abs(ev.getY() - this.sy) < ((float) ViewConfiguration.get(getContext()).getScaledTouchSlop())) {
                    int additionWidth = (getMeasuredHeight() - this.thumbSize) / 2;
                    if (((float) (this.thumbX - additionWidth)) > ev.getX() || ev.getX() > ((float) (this.thumbX + this.thumbSize + additionWidth))) {
                        int x = ((int) ev.getX()) - (this.thumbSize / 2);
                        this.thumbX = x;
                        if (x < 0) {
                            this.thumbX = 0;
                        } else if (x > getMeasuredWidth() - this.selectorWidth) {
                            this.thumbX = getMeasuredWidth() - this.selectorWidth;
                        }
                    }
                    this.thumbDX = (int) (ev.getX() - ((float) this.thumbX));
                    this.pressed = true;
                }
            }
            if (this.pressed) {
                if (ev.getAction() == 1) {
                    if (this.twoSided) {
                        float w = (float) ((getMeasuredWidth() - this.selectorWidth) / 2);
                        int i = this.thumbX;
                        if (((float) i) >= w) {
                            this.delegate.onSeekBarDrag(false, (((float) i) - w) / w);
                        } else {
                            this.delegate.onSeekBarDrag(false, -Math.max(0.01f, 1.0f - ((w - ((float) i)) / w)));
                        }
                    } else {
                        this.delegate.onSeekBarDrag(true, ((float) this.thumbX) / ((float) (getMeasuredWidth() - this.selectorWidth)));
                    }
                }
                if (Build.VERSION.SDK_INT >= 21 && (drawable = this.hoverDrawable) != null) {
                    drawable.setState(StateSet.NOTHING);
                }
                this.delegate.onSeekBarPressed(false);
                this.pressed = false;
                invalidate();
                return true;
            }
        } else if (ev.getAction() == 2) {
            if (!this.captured) {
                ViewConfiguration vc = ViewConfiguration.get(getContext());
                if (Math.abs(ev.getY() - this.sy) <= ((float) vc.getScaledTouchSlop()) && Math.abs(ev.getX() - this.sx) > ((float) vc.getScaledTouchSlop())) {
                    this.captured = true;
                    getParent().requestDisallowInterceptTouchEvent(true);
                    int additionWidth2 = (getMeasuredHeight() - this.thumbSize) / 2;
                    if (ev.getY() >= 0.0f && ev.getY() <= ((float) getMeasuredHeight())) {
                        if (((float) (this.thumbX - additionWidth2)) > ev.getX() || ev.getX() > ((float) (this.thumbX + this.thumbSize + additionWidth2))) {
                            int x2 = ((int) ev.getX()) - (this.thumbSize / 2);
                            this.thumbX = x2;
                            if (x2 < 0) {
                                this.thumbX = 0;
                            } else if (x2 > getMeasuredWidth() - this.selectorWidth) {
                                this.thumbX = getMeasuredWidth() - this.selectorWidth;
                            }
                        }
                        this.thumbDX = (int) (ev.getX() - ((float) this.thumbX));
                        this.pressed = true;
                        this.delegate.onSeekBarPressed(true);
                        if (Build.VERSION.SDK_INT >= 21 && (drawable3 = this.hoverDrawable) != null) {
                            drawable3.setState(this.pressedState);
                            this.hoverDrawable.setHotspot(ev.getX(), ev.getY());
                        }
                        invalidate();
                        return true;
                    }
                }
            } else if (this.pressed) {
                int x3 = (int) (ev.getX() - ((float) this.thumbDX));
                this.thumbX = x3;
                if (x3 < 0) {
                    this.thumbX = 0;
                } else if (x3 > getMeasuredWidth() - this.selectorWidth) {
                    this.thumbX = getMeasuredWidth() - this.selectorWidth;
                }
                if (this.reportChanges) {
                    if (this.twoSided) {
                        float w2 = (float) ((getMeasuredWidth() - this.selectorWidth) / 2);
                        int i2 = this.thumbX;
                        if (((float) i2) >= w2) {
                            this.delegate.onSeekBarDrag(false, (((float) i2) - w2) / w2);
                        } else {
                            this.delegate.onSeekBarDrag(false, -Math.max(0.01f, 1.0f - ((w2 - ((float) i2)) / w2)));
                        }
                    } else {
                        this.delegate.onSeekBarDrag(false, ((float) this.thumbX) / ((float) (getMeasuredWidth() - this.selectorWidth)));
                    }
                }
                if (Build.VERSION.SDK_INT >= 21 && (drawable2 = this.hoverDrawable) != null) {
                    drawable2.setHotspot(ev.getX(), ev.getY());
                }
                invalidate();
                return true;
            }
        }
        return false;
    }

    public float getProgress() {
        if (getMeasuredWidth() == 0) {
            return this.progressToSet;
        }
        return ((float) this.thumbX) / ((float) (getMeasuredWidth() - this.selectorWidth));
    }

    public void setProgress(float progress) {
        setProgress(progress, false);
    }

    public void setProgress(float progress, boolean animated) {
        int newThumbX;
        if (getMeasuredWidth() == 0) {
            this.progressToSet = progress;
            return;
        }
        this.progressToSet = -100.0f;
        if (this.twoSided) {
            int w = getMeasuredWidth() - this.selectorWidth;
            float cx = (float) (w / 2);
            if (progress < 0.0f) {
                newThumbX = (int) Math.ceil((double) ((((float) (w / 2)) * (-(1.0f + progress))) + cx));
            } else {
                newThumbX = (int) Math.ceil((double) ((((float) (w / 2)) * progress) + cx));
            }
        } else {
            newThumbX = (int) Math.ceil((double) (((float) (getMeasuredWidth() - this.selectorWidth)) * progress));
        }
        int i = this.thumbX;
        if (i != newThumbX) {
            if (animated) {
                this.transitionThumbX = i;
                this.transitionProgress = 0.0f;
            }
            this.thumbX = newThumbX;
            if (newThumbX < 0) {
                this.thumbX = 0;
            } else if (newThumbX > getMeasuredWidth() - this.selectorWidth) {
                this.thumbX = getMeasuredWidth() - this.selectorWidth;
            }
            invalidate();
        }
    }

    public void setBufferedProgress(float progress) {
        this.bufferedProgress = progress;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.progressToSet != -100.0f && getMeasuredWidth() > 0) {
            setProgress(this.progressToSet);
            this.progressToSet = -100.0f;
        }
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == this.hoverDrawable;
    }

    public boolean isDragging() {
        return this.pressed;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        int y = (getMeasuredHeight() - this.thumbSize) / 2;
        this.innerPaint1.setColor(getThemedColor("player_progressBackground"));
        canvas.drawRect((float) (this.selectorWidth / 2), (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f)), (float) (getMeasuredWidth() - (this.selectorWidth / 2)), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f)), this.innerPaint1);
        if (this.bufferedProgress > 0.0f) {
            this.innerPaint1.setColor(getThemedColor("key_player_progressCachedBackground"));
            Canvas canvas3 = canvas;
            canvas3.drawRect((float) (this.selectorWidth / 2), (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f)), (this.bufferedProgress * ((float) (getMeasuredWidth() - this.selectorWidth))) + ((float) (this.selectorWidth / 2)), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f)), this.innerPaint1);
        }
        float f = 6.0f;
        if (this.twoSided) {
            canvas.drawRect((float) ((getMeasuredWidth() / 2) - AndroidUtilities.dp(1.0f)), (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(6.0f)), (float) ((getMeasuredWidth() / 2) + AndroidUtilities.dp(1.0f)), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(6.0f)), this.outerPaint1);
            int i = this.thumbX;
            int measuredWidth = getMeasuredWidth();
            int i2 = this.selectorWidth;
            if (i > (measuredWidth - i2) / 2) {
                canvas.drawRect((float) (getMeasuredWidth() / 2), (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f)), (float) ((this.selectorWidth / 2) + this.thumbX), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f)), this.outerPaint1);
            } else {
                canvas.drawRect((float) (this.thumbX + (i2 / 2)), (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f)), (float) (getMeasuredWidth() / 2), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f)), this.outerPaint1);
            }
        } else {
            canvas.drawRect((float) (this.selectorWidth / 2), (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f)), (float) ((this.selectorWidth / 2) + this.thumbX), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f)), this.outerPaint1);
        }
        if (this.hoverDrawable != null) {
            int dx = (this.thumbX + (this.selectorWidth / 2)) - AndroidUtilities.dp(16.0f);
            int dy = ((this.thumbSize / 2) + y) - AndroidUtilities.dp(16.0f);
            this.hoverDrawable.setBounds(dx, dy, AndroidUtilities.dp(32.0f) + dx, AndroidUtilities.dp(32.0f) + dy);
            this.hoverDrawable.draw(canvas2);
        }
        boolean needInvalidate = false;
        if (this.pressed) {
            f = 8.0f;
        }
        int newRad = AndroidUtilities.dp(f);
        long dt = SystemClock.elapsedRealtime() - this.lastUpdateTime;
        if (dt > 18) {
            dt = 16;
        }
        float f2 = this.currentRadius;
        if (f2 != ((float) newRad)) {
            if (f2 < ((float) newRad)) {
                float dp = f2 + (((float) AndroidUtilities.dp(1.0f)) * (((float) dt) / 60.0f));
                this.currentRadius = dp;
                if (dp > ((float) newRad)) {
                    this.currentRadius = (float) newRad;
                }
            } else {
                float dp2 = f2 - (((float) AndroidUtilities.dp(1.0f)) * (((float) dt) / 60.0f));
                this.currentRadius = dp2;
                if (dp2 < ((float) newRad)) {
                    this.currentRadius = (float) newRad;
                }
            }
            needInvalidate = true;
        }
        float f3 = this.transitionProgress;
        if (f3 < 1.0f) {
            float f4 = f3 + (((float) dt) / 225.0f);
            this.transitionProgress = f4;
            if (f4 < 1.0f) {
                needInvalidate = true;
            } else {
                this.transitionProgress = 1.0f;
            }
        }
        if (this.transitionProgress < 1.0f) {
            float oldCircleProgress = 1.0f - Easings.easeInQuad.getInterpolation(Math.min(1.0f, this.transitionProgress * 3.0f));
            float newCircleProgress = Easings.easeOutQuad.getInterpolation(this.transitionProgress);
            if (oldCircleProgress > 0.0f) {
                canvas2.drawCircle((float) (this.transitionThumbX + (this.selectorWidth / 2)), (float) ((this.thumbSize / 2) + y), this.currentRadius * oldCircleProgress, this.outerPaint1);
            }
            canvas2.drawCircle((float) (this.thumbX + (this.selectorWidth / 2)), (float) ((this.thumbSize / 2) + y), this.currentRadius * newCircleProgress, this.outerPaint1);
        } else {
            canvas2.drawCircle((float) (this.thumbX + (this.selectorWidth / 2)), (float) ((this.thumbSize / 2) + y), this.currentRadius, this.outerPaint1);
        }
        if (needInvalidate) {
            postInvalidateOnAnimation();
        }
    }

    public SeekBarAccessibilityDelegate getSeekBarAccessibilityDelegate() {
        return this.seekBarAccessibilityDelegate;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}

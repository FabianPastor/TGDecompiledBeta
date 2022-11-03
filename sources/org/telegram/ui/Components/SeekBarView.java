package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes3.dex */
public class SeekBarView extends FrameLayout {
    private AnimatedFloat animatedThumbX;
    private float bufferedProgress;
    boolean captured;
    private float currentRadius;
    public SeekBarViewDelegate delegate;
    private Drawable hoverDrawable;
    private Paint innerPaint1;
    private long lastUpdateTime;
    int lastValue;
    private Paint outerPaint1;
    private boolean pressed;
    private int[] pressedState;
    private float progressToSet;
    private boolean reportChanges;
    private final Theme.ResourcesProvider resourcesProvider;
    private final SeekBarAccessibilityDelegate seekBarAccessibilityDelegate;
    private int selectorWidth;
    private int separatorsCount;
    float sx;
    float sy;
    private int thumbDX;
    private int thumbSize;
    private int thumbX;
    private float transitionProgress;
    private int transitionThumbX;
    private boolean twoSided;

    /* loaded from: classes3.dex */
    public interface SeekBarViewDelegate {

        /* renamed from: org.telegram.ui.Components.SeekBarView$SeekBarViewDelegate$-CC */
        /* loaded from: classes3.dex */
        public final /* synthetic */ class CC {
            public static CharSequence $default$getContentDescription(SeekBarViewDelegate seekBarViewDelegate) {
                return null;
            }

            public static int $default$getStepsCount(SeekBarViewDelegate seekBarViewDelegate) {
                return 0;
            }
        }

        CharSequence getContentDescription();

        int getStepsCount();

        void onSeekBarDrag(boolean z, float f);

        void onSeekBarPressed(boolean z);
    }

    public SeekBarView(Context context) {
        this(context, null);
    }

    public SeekBarView(Context context, Theme.ResourcesProvider resourcesProvider) {
        this(context, false, resourcesProvider);
    }

    public SeekBarView(Context context, boolean z, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.animatedThumbX = new AnimatedFloat(this, 0L, 80L, CubicBezierInterpolator.EASE_OUT);
        this.progressToSet = -100.0f;
        this.pressedState = new int[]{16842910, 16842919};
        this.transitionProgress = 1.0f;
        this.resourcesProvider = resourcesProvider;
        setWillNotDraw(false);
        this.innerPaint1 = new Paint(1);
        Paint paint = new Paint(1);
        this.outerPaint1 = paint;
        paint.setColor(getThemedColor("player_progress"));
        this.selectorWidth = AndroidUtilities.dp(32.0f);
        this.thumbSize = AndroidUtilities.dp(24.0f);
        this.currentRadius = AndroidUtilities.dp(6.0f);
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable createSelectorDrawable = Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(getThemedColor("player_progress"), 40), 1, AndroidUtilities.dp(16.0f));
            this.hoverDrawable = createSelectorDrawable;
            createSelectorDrawable.setCallback(this);
            this.hoverDrawable.setVisible(true, false);
        }
        setImportantForAccessibility(1);
        FloatSeekBarAccessibilityDelegate floatSeekBarAccessibilityDelegate = new FloatSeekBarAccessibilityDelegate(z) { // from class: org.telegram.ui.Components.SeekBarView.1
            {
                SeekBarView.this = this;
            }

            @Override // org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate
            public float getProgress() {
                return SeekBarView.this.getProgress();
            }

            @Override // org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate
            public void setProgress(float f) {
                SeekBarView.this.pressed = true;
                SeekBarView.this.setProgress(f);
                SeekBarView.this.setSeekBarDrag(true, f);
                SeekBarView.this.pressed = false;
            }

            @Override // org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate
            public float getDelta() {
                int stepsCount = SeekBarView.this.delegate.getStepsCount();
                return stepsCount > 0 ? 1.0f / stepsCount : super.getDelta();
            }

            @Override // org.telegram.ui.Components.SeekBarAccessibilityDelegate
            /* renamed from: getContentDescription */
            public CharSequence mo2220getContentDescription(View view) {
                SeekBarViewDelegate seekBarViewDelegate = SeekBarView.this.delegate;
                if (seekBarViewDelegate != null) {
                    return seekBarViewDelegate.getContentDescription();
                }
                return null;
            }
        };
        this.seekBarAccessibilityDelegate = floatSeekBarAccessibilityDelegate;
        setAccessibilityDelegate(floatSeekBarAccessibilityDelegate);
    }

    public void setSeparatorsCount(int i) {
        this.separatorsCount = i;
    }

    public void setTwoSided(boolean z) {
        this.twoSided = z;
    }

    public boolean isTwoSided() {
        return this.twoSided;
    }

    public void setInnerColor(int i) {
        this.innerPaint1.setColor(i);
    }

    public void setOuterColor(int i) {
        this.outerPaint1.setColor(i);
        Drawable drawable = this.hoverDrawable;
        if (drawable != null) {
            Theme.setSelectorDrawableColor(drawable, ColorUtils.setAlphaComponent(i, 40), true);
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return onTouch(motionEvent);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return onTouch(motionEvent);
    }

    public void setReportChanges(boolean z) {
        this.reportChanges = z;
    }

    public void setDelegate(SeekBarViewDelegate seekBarViewDelegate) {
        this.delegate = seekBarViewDelegate;
    }

    public boolean onTouch(MotionEvent motionEvent) {
        Drawable drawable;
        Drawable drawable2;
        Drawable drawable3;
        if (motionEvent.getAction() == 0) {
            this.sx = motionEvent.getX();
            this.sy = motionEvent.getY();
            return true;
        }
        if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            this.captured = false;
            if (motionEvent.getAction() == 1) {
                if (Math.abs(motionEvent.getY() - this.sy) < ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                    int measuredHeight = (getMeasuredHeight() - this.thumbSize) / 2;
                    if (this.thumbX - measuredHeight > motionEvent.getX() || motionEvent.getX() > this.thumbX + this.thumbSize + measuredHeight) {
                        int x = ((int) motionEvent.getX()) - (this.thumbSize / 2);
                        this.thumbX = x;
                        if (x < 0) {
                            this.thumbX = 0;
                        } else if (x > getMeasuredWidth() - this.selectorWidth) {
                            this.thumbX = getMeasuredWidth() - this.selectorWidth;
                        }
                    }
                    this.thumbDX = (int) (motionEvent.getX() - this.thumbX);
                    this.pressed = true;
                }
            }
            if (this.pressed) {
                if (motionEvent.getAction() == 1) {
                    if (this.twoSided) {
                        float measuredWidth = (getMeasuredWidth() - this.selectorWidth) / 2;
                        int i = this.thumbX;
                        if (i >= measuredWidth) {
                            setSeekBarDrag(false, (i - measuredWidth) / measuredWidth);
                        } else {
                            setSeekBarDrag(false, -Math.max(0.01f, 1.0f - ((measuredWidth - i) / measuredWidth)));
                        }
                    } else {
                        setSeekBarDrag(true, this.thumbX / (getMeasuredWidth() - this.selectorWidth));
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
        } else if (motionEvent.getAction() == 2) {
            if (!this.captured) {
                ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
                if (Math.abs(motionEvent.getY() - this.sy) <= viewConfiguration.getScaledTouchSlop() && Math.abs(motionEvent.getX() - this.sx) > viewConfiguration.getScaledTouchSlop()) {
                    this.captured = true;
                    getParent().requestDisallowInterceptTouchEvent(true);
                    int measuredHeight2 = (getMeasuredHeight() - this.thumbSize) / 2;
                    if (motionEvent.getY() >= 0.0f && motionEvent.getY() <= getMeasuredHeight()) {
                        if (this.thumbX - measuredHeight2 > motionEvent.getX() || motionEvent.getX() > this.thumbX + this.thumbSize + measuredHeight2) {
                            int x2 = ((int) motionEvent.getX()) - (this.thumbSize / 2);
                            this.thumbX = x2;
                            if (x2 < 0) {
                                this.thumbX = 0;
                            } else if (x2 > getMeasuredWidth() - this.selectorWidth) {
                                this.thumbX = getMeasuredWidth() - this.selectorWidth;
                            }
                        }
                        this.thumbDX = (int) (motionEvent.getX() - this.thumbX);
                        this.pressed = true;
                        this.delegate.onSeekBarPressed(true);
                        if (Build.VERSION.SDK_INT >= 21 && (drawable3 = this.hoverDrawable) != null) {
                            drawable3.setState(this.pressedState);
                            this.hoverDrawable.setHotspot(motionEvent.getX(), motionEvent.getY());
                        }
                        invalidate();
                        return true;
                    }
                }
            } else if (this.pressed) {
                int x3 = (int) (motionEvent.getX() - this.thumbDX);
                this.thumbX = x3;
                if (x3 < 0) {
                    this.thumbX = 0;
                } else if (x3 > getMeasuredWidth() - this.selectorWidth) {
                    this.thumbX = getMeasuredWidth() - this.selectorWidth;
                }
                if (this.reportChanges) {
                    if (this.twoSided) {
                        float measuredWidth2 = (getMeasuredWidth() - this.selectorWidth) / 2;
                        int i2 = this.thumbX;
                        if (i2 >= measuredWidth2) {
                            setSeekBarDrag(false, (i2 - measuredWidth2) / measuredWidth2);
                        } else {
                            setSeekBarDrag(false, -Math.max(0.01f, 1.0f - ((measuredWidth2 - i2) / measuredWidth2)));
                        }
                    } else {
                        setSeekBarDrag(false, this.thumbX / (getMeasuredWidth() - this.selectorWidth));
                    }
                }
                if (Build.VERSION.SDK_INT >= 21 && (drawable2 = this.hoverDrawable) != null) {
                    drawable2.setHotspot(motionEvent.getX(), motionEvent.getY());
                }
                invalidate();
                return true;
            }
        }
        return false;
    }

    public void setSeekBarDrag(boolean z, float f) {
        SeekBarViewDelegate seekBarViewDelegate = this.delegate;
        if (seekBarViewDelegate != null) {
            seekBarViewDelegate.onSeekBarDrag(z, f);
        }
        int i = this.separatorsCount;
        if (i > 1) {
            int round = Math.round((i - 1) * f);
            if (!z && round != this.lastValue) {
                try {
                    performHapticFeedback(9, 1);
                } catch (Exception unused) {
                }
            }
            this.lastValue = round;
        }
    }

    public float getProgress() {
        if (getMeasuredWidth() == 0) {
            return this.progressToSet;
        }
        return this.thumbX / (getMeasuredWidth() - this.selectorWidth);
    }

    public void setProgress(float f) {
        setProgress(f, false);
    }

    public void setProgress(float f, boolean z) {
        double ceil;
        if (getMeasuredWidth() == 0) {
            this.progressToSet = f;
            return;
        }
        this.progressToSet = -100.0f;
        if (this.twoSided) {
            float measuredWidth = (getMeasuredWidth() - this.selectorWidth) / 2;
            if (f < 0.0f) {
                ceil = Math.ceil(measuredWidth + ((-(f + 1.0f)) * measuredWidth));
            } else {
                ceil = Math.ceil(measuredWidth + (f * measuredWidth));
            }
        } else {
            ceil = Math.ceil((getMeasuredWidth() - this.selectorWidth) * f);
        }
        int i = (int) ceil;
        int i2 = this.thumbX;
        if (i2 == i) {
            return;
        }
        if (z) {
            this.transitionThumbX = i2;
            this.transitionProgress = 0.0f;
        }
        this.thumbX = i;
        if (i < 0) {
            this.thumbX = 0;
        } else if (i > getMeasuredWidth() - this.selectorWidth) {
            this.thumbX = getMeasuredWidth() - this.selectorWidth;
        }
        invalidate();
    }

    public void setBufferedProgress(float f) {
        this.bufferedProgress = f;
        invalidate();
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.progressToSet == -100.0f || getMeasuredWidth() <= 0) {
            return;
        }
        setProgress(this.progressToSet);
        this.progressToSet = -100.0f;
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.hoverDrawable;
    }

    public boolean isDragging() {
        return this.pressed;
    }

    /* JADX WARN: Removed duplicated region for block: B:101:0x0211  */
    /* JADX WARN: Removed duplicated region for block: B:105:0x0259  */
    /* JADX WARN: Removed duplicated region for block: B:107:0x026e  */
    /* JADX WARN: Removed duplicated region for block: B:109:? A[RETURN, SYNTHETIC] */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void onDraw(android.graphics.Canvas r15) {
        /*
            Method dump skipped, instructions count: 626
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SeekBarView.onDraw(android.graphics.Canvas):void");
    }

    public SeekBarAccessibilityDelegate getSeekBarAccessibilityDelegate() {
        return this.seekBarAccessibilityDelegate;
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}

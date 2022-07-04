package org.telegram.ui.Components;

import android.animation.TimeInterpolator;
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

public class SeekBarView extends FrameLayout {
    private AnimatedFloat animatedThumbX;
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
    private int separatorsCount;
    float sx;
    float sy;
    private int thumbDX;
    private int thumbSize;
    private int thumbX;
    private float transitionProgress;
    private int transitionThumbX;
    private boolean twoSided;

    public interface SeekBarViewDelegate {

        /* renamed from: org.telegram.ui.Components.SeekBarView$SeekBarViewDelegate$-CC  reason: invalid class name */
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
        this(context, (Theme.ResourcesProvider) null);
    }

    public SeekBarView(Context context, Theme.ResourcesProvider resourcesProvider2) {
        this(context, false, resourcesProvider2);
    }

    public SeekBarView(Context context, boolean z, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.animatedThumbX = new AnimatedFloat((View) this, 150, (TimeInterpolator) CubicBezierInterpolator.DEFAULT);
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
        AnonymousClass1 r4 = new FloatSeekBarAccessibilityDelegate(z) {
            public float getProgress() {
                return SeekBarView.this.getProgress();
            }

            public void setProgress(float f) {
                boolean unused = SeekBarView.this.pressed = true;
                SeekBarView.this.setProgress(f);
                SeekBarViewDelegate seekBarViewDelegate = SeekBarView.this.delegate;
                if (seekBarViewDelegate != null) {
                    seekBarViewDelegate.onSeekBarDrag(true, f);
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

            public CharSequence getContentDescription(View view) {
                SeekBarViewDelegate seekBarViewDelegate = SeekBarView.this.delegate;
                if (seekBarViewDelegate != null) {
                    return seekBarViewDelegate.getContentDescription();
                }
                return null;
            }
        };
        this.seekBarAccessibilityDelegate = r4;
        setAccessibilityDelegate(r4);
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

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return onTouch(motionEvent);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return onTouch(motionEvent);
    }

    public void setReportChanges(boolean z) {
        this.reportChanges = z;
    }

    public void setDelegate(SeekBarViewDelegate seekBarViewDelegate) {
        this.delegate = seekBarViewDelegate;
    }

    /* access modifiers changed from: package-private */
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
                if (Math.abs(motionEvent.getY() - this.sy) < ((float) ViewConfiguration.get(getContext()).getScaledTouchSlop())) {
                    int measuredHeight = (getMeasuredHeight() - this.thumbSize) / 2;
                    if (((float) (this.thumbX - measuredHeight)) > motionEvent.getX() || motionEvent.getX() > ((float) (this.thumbX + this.thumbSize + measuredHeight))) {
                        int x = ((int) motionEvent.getX()) - (this.thumbSize / 2);
                        this.thumbX = x;
                        if (x < 0) {
                            this.thumbX = 0;
                        } else if (x > getMeasuredWidth() - this.selectorWidth) {
                            this.thumbX = getMeasuredWidth() - this.selectorWidth;
                        }
                    }
                    this.thumbDX = (int) (motionEvent.getX() - ((float) this.thumbX));
                    this.pressed = true;
                }
            }
            if (this.pressed) {
                if (motionEvent.getAction() == 1) {
                    if (this.twoSided) {
                        float measuredWidth = (float) ((getMeasuredWidth() - this.selectorWidth) / 2);
                        int i = this.thumbX;
                        if (((float) i) >= measuredWidth) {
                            this.delegate.onSeekBarDrag(false, (((float) i) - measuredWidth) / measuredWidth);
                        } else {
                            this.delegate.onSeekBarDrag(false, -Math.max(0.01f, 1.0f - ((measuredWidth - ((float) i)) / measuredWidth)));
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
        } else if (motionEvent.getAction() == 2) {
            if (!this.captured) {
                ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
                if (Math.abs(motionEvent.getY() - this.sy) <= ((float) viewConfiguration.getScaledTouchSlop()) && Math.abs(motionEvent.getX() - this.sx) > ((float) viewConfiguration.getScaledTouchSlop())) {
                    this.captured = true;
                    getParent().requestDisallowInterceptTouchEvent(true);
                    int measuredHeight2 = (getMeasuredHeight() - this.thumbSize) / 2;
                    if (motionEvent.getY() >= 0.0f && motionEvent.getY() <= ((float) getMeasuredHeight())) {
                        if (((float) (this.thumbX - measuredHeight2)) > motionEvent.getX() || motionEvent.getX() > ((float) (this.thumbX + this.thumbSize + measuredHeight2))) {
                            int x2 = ((int) motionEvent.getX()) - (this.thumbSize / 2);
                            this.thumbX = x2;
                            if (x2 < 0) {
                                this.thumbX = 0;
                            } else if (x2 > getMeasuredWidth() - this.selectorWidth) {
                                this.thumbX = getMeasuredWidth() - this.selectorWidth;
                            }
                        }
                        this.thumbDX = (int) (motionEvent.getX() - ((float) this.thumbX));
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
                int x3 = (int) (motionEvent.getX() - ((float) this.thumbDX));
                this.thumbX = x3;
                if (x3 < 0) {
                    this.thumbX = 0;
                } else if (x3 > getMeasuredWidth() - this.selectorWidth) {
                    this.thumbX = getMeasuredWidth() - this.selectorWidth;
                }
                if (this.reportChanges) {
                    if (this.twoSided) {
                        float measuredWidth2 = (float) ((getMeasuredWidth() - this.selectorWidth) / 2);
                        int i2 = this.thumbX;
                        if (((float) i2) >= measuredWidth2) {
                            this.delegate.onSeekBarDrag(false, (((float) i2) - measuredWidth2) / measuredWidth2);
                        } else {
                            this.delegate.onSeekBarDrag(false, -Math.max(0.01f, 1.0f - ((measuredWidth2 - ((float) i2)) / measuredWidth2)));
                        }
                    } else {
                        this.delegate.onSeekBarDrag(false, ((float) this.thumbX) / ((float) (getMeasuredWidth() - this.selectorWidth)));
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

    public float getProgress() {
        if (getMeasuredWidth() == 0) {
            return this.progressToSet;
        }
        return ((float) this.thumbX) / ((float) (getMeasuredWidth() - this.selectorWidth));
    }

    public void setProgress(float f) {
        setProgress(f, false);
    }

    public void setProgress(float f, boolean z) {
        double d;
        if (getMeasuredWidth() == 0) {
            this.progressToSet = f;
            return;
        }
        this.progressToSet = -100.0f;
        if (this.twoSided) {
            float measuredWidth = (float) ((getMeasuredWidth() - this.selectorWidth) / 2);
            if (f < 0.0f) {
                d = Math.ceil((double) (measuredWidth + ((-(f + 1.0f)) * measuredWidth)));
            } else {
                d = Math.ceil((double) (measuredWidth + (f * measuredWidth)));
            }
        } else {
            d = Math.ceil((double) (((float) (getMeasuredWidth() - this.selectorWidth)) * f));
        }
        int i = (int) d;
        int i2 = this.thumbX;
        if (i2 != i) {
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
    }

    public void setBufferedProgress(float f) {
        this.bufferedProgress = f;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.progressToSet != -100.0f && getMeasuredWidth() > 0) {
            setProgress(this.progressToSet);
            this.progressToSet = -100.0f;
        }
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.hoverDrawable;
    }

    public boolean isDragging() {
        return this.pressed;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x029a  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x02e2  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x02f7  */
    /* JADX WARNING: Removed duplicated region for block: B:69:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r16) {
        /*
            r15 = this;
            r0 = r15
            r7 = r16
            int r1 = r0.thumbX
            boolean r2 = r0.twoSided
            r8 = 1
            r9 = 1065353216(0x3var_, float:1.0)
            if (r2 != 0) goto L_0x002d
            int r2 = r0.separatorsCount
            if (r2 <= r8) goto L_0x002d
            int r2 = r15.getMeasuredWidth()
            int r3 = r0.selectorWidth
            int r2 = r2 - r3
            float r2 = (float) r2
            int r3 = r0.separatorsCount
            float r3 = (float) r3
            float r3 = r3 - r9
            float r2 = r2 / r3
            org.telegram.ui.Components.AnimatedFloat r3 = r0.animatedThumbX
            float r1 = (float) r1
            float r1 = r1 / r2
            int r1 = java.lang.Math.round(r1)
            float r1 = (float) r1
            float r1 = r1 * r2
            float r1 = r3.set(r1)
            int r1 = (int) r1
        L_0x002d:
            r10 = r1
            int r1 = r15.getMeasuredHeight()
            int r2 = r0.thumbSize
            int r1 = r1 - r2
            int r11 = r1 / 2
            android.graphics.Paint r1 = r0.innerPaint1
            java.lang.String r2 = "player_progressBackground"
            int r2 = r15.getThemedColor(r2)
            r1.setColor(r2)
            int r1 = r0.selectorWidth
            int r1 = r1 / 2
            float r2 = (float) r1
            int r1 = r15.getMeasuredHeight()
            int r1 = r1 / 2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r1 = r1 - r3
            float r3 = (float) r1
            int r1 = r15.getMeasuredWidth()
            int r4 = r0.selectorWidth
            int r4 = r4 / 2
            int r1 = r1 - r4
            float r4 = (float) r1
            int r1 = r15.getMeasuredHeight()
            int r1 = r1 / 2
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r1 = r1 + r5
            float r5 = (float) r1
            android.graphics.Paint r6 = r0.innerPaint1
            r1 = r16
            r1.drawRect(r2, r3, r4, r5, r6)
            boolean r1 = r0.twoSided
            r12 = 0
            if (r1 != 0) goto L_0x00ad
            int r1 = r0.separatorsCount
            if (r1 <= r8) goto L_0x00ad
            r1 = 0
        L_0x007a:
            int r2 = r0.separatorsCount
            if (r1 >= r2) goto L_0x00ad
            int r2 = r0.selectorWidth
            int r2 = r2 / 2
            int r3 = r15.getMeasuredWidth()
            int r4 = r0.selectorWidth
            int r4 = r4 / 2
            int r3 = r3 - r4
            float r4 = (float) r1
            int r5 = r0.separatorsCount
            float r5 = (float) r5
            float r5 = r5 - r9
            float r4 = r4 / r5
            int r2 = org.telegram.messenger.AndroidUtilities.lerp((int) r2, (int) r3, (float) r4)
            float r2 = (float) r2
            int r3 = r15.getMeasuredHeight()
            int r3 = r3 / 2
            float r3 = (float) r3
            r4 = 1070386381(0x3fcccccd, float:1.6)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            android.graphics.Paint r5 = r0.innerPaint1
            r7.drawCircle(r2, r3, r4, r5)
            int r1 = r1 + 1
            goto L_0x007a
        L_0x00ad:
            float r1 = r0.bufferedProgress
            r13 = 0
            int r1 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r1 <= 0) goto L_0x00f5
            android.graphics.Paint r1 = r0.innerPaint1
            java.lang.String r2 = "key_player_progressCachedBackground"
            int r2 = r15.getThemedColor(r2)
            r1.setColor(r2)
            int r1 = r0.selectorWidth
            int r1 = r1 / 2
            float r2 = (float) r1
            int r1 = r15.getMeasuredHeight()
            int r1 = r1 / 2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r1 = r1 - r3
            float r3 = (float) r1
            int r1 = r0.selectorWidth
            int r1 = r1 / 2
            float r1 = (float) r1
            float r4 = r0.bufferedProgress
            int r5 = r15.getMeasuredWidth()
            int r6 = r0.selectorWidth
            int r5 = r5 - r6
            float r5 = (float) r5
            float r4 = r4 * r5
            float r4 = r4 + r1
            int r1 = r15.getMeasuredHeight()
            int r1 = r1 / 2
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r1 = r1 + r5
            float r5 = (float) r1
            android.graphics.Paint r6 = r0.innerPaint1
            r1 = r16
            r1.drawRect(r2, r3, r4, r5, r6)
        L_0x00f5:
            boolean r1 = r0.twoSided
            r14 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r1 == 0) goto L_0x0196
            int r1 = r15.getMeasuredWidth()
            int r1 = r1 / 2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r1 = r1 - r2
            float r2 = (float) r1
            int r1 = r15.getMeasuredHeight()
            int r1 = r1 / 2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r1 = r1 - r3
            float r3 = (float) r1
            int r1 = r15.getMeasuredWidth()
            int r1 = r1 / 2
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r1 = r1 + r4
            float r4 = (float) r1
            int r1 = r15.getMeasuredHeight()
            int r1 = r1 / 2
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r1 = r1 + r5
            float r5 = (float) r1
            android.graphics.Paint r6 = r0.outerPaint1
            r1 = r16
            r1.drawRect(r2, r3, r4, r5, r6)
            int r1 = r15.getMeasuredWidth()
            int r2 = r0.selectorWidth
            int r1 = r1 - r2
            int r1 = r1 / 2
            if (r10 <= r1) goto L_0x016b
            int r1 = r15.getMeasuredWidth()
            int r1 = r1 / 2
            float r2 = (float) r1
            int r1 = r15.getMeasuredHeight()
            int r1 = r1 / 2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r1 = r1 - r3
            float r3 = (float) r1
            int r1 = r0.selectorWidth
            int r1 = r1 / 2
            int r1 = r1 + r10
            float r4 = (float) r1
            int r1 = r15.getMeasuredHeight()
            int r1 = r1 / 2
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r1 = r1 + r5
            float r5 = (float) r1
            android.graphics.Paint r6 = r0.outerPaint1
            r1 = r16
            r1.drawRect(r2, r3, r4, r5, r6)
            goto L_0x0203
        L_0x016b:
            int r2 = r2 / 2
            int r2 = r2 + r10
            float r2 = (float) r2
            int r1 = r15.getMeasuredHeight()
            int r1 = r1 / 2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r1 = r1 - r3
            float r3 = (float) r1
            int r1 = r15.getMeasuredWidth()
            int r1 = r1 / 2
            float r4 = (float) r1
            int r1 = r15.getMeasuredHeight()
            int r1 = r1 / 2
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r1 = r1 + r5
            float r5 = (float) r1
            android.graphics.Paint r6 = r0.outerPaint1
            r1 = r16
            r1.drawRect(r2, r3, r4, r5, r6)
            goto L_0x0203
        L_0x0196:
            int r1 = r0.selectorWidth
            int r1 = r1 / 2
            float r2 = (float) r1
            int r1 = r15.getMeasuredHeight()
            int r1 = r1 / 2
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r1 = r1 - r3
            float r3 = (float) r1
            int r1 = r0.selectorWidth
            int r1 = r1 / 2
            int r1 = r1 + r10
            float r4 = (float) r1
            int r1 = r15.getMeasuredHeight()
            int r1 = r1 / 2
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r1 = r1 + r5
            float r5 = (float) r1
            android.graphics.Paint r6 = r0.outerPaint1
            r1 = r16
            r1.drawRect(r2, r3, r4, r5, r6)
            int r1 = r0.separatorsCount
            if (r1 <= r8) goto L_0x0203
            r1 = 0
        L_0x01c5:
            int r2 = r0.separatorsCount
            if (r1 >= r2) goto L_0x0203
            int r2 = r0.selectorWidth
            int r2 = r2 / 2
            int r3 = r15.getMeasuredWidth()
            int r4 = r0.selectorWidth
            int r4 = r4 / 2
            int r3 = r3 - r4
            float r4 = (float) r1
            int r5 = r0.separatorsCount
            float r5 = (float) r5
            float r5 = r5 - r9
            float r4 = r4 / r5
            int r2 = org.telegram.messenger.AndroidUtilities.lerp((int) r2, (int) r3, (float) r4)
            float r2 = (float) r2
            int r3 = r0.selectorWidth
            int r3 = r3 / 2
            int r3 = r3 + r10
            float r3 = (float) r3
            int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r3 <= 0) goto L_0x01ec
            goto L_0x0203
        L_0x01ec:
            int r3 = r15.getMeasuredHeight()
            int r3 = r3 / 2
            float r3 = (float) r3
            r4 = 1068708659(0x3fb33333, float:1.4)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            android.graphics.Paint r5 = r0.outerPaint1
            r7.drawCircle(r2, r3, r4, r5)
            int r1 = r1 + 1
            goto L_0x01c5
        L_0x0203:
            android.graphics.drawable.Drawable r1 = r0.hoverDrawable
            if (r1 == 0) goto L_0x0233
            int r1 = r0.selectorWidth
            int r1 = r1 / 2
            int r1 = r1 + r10
            r2 = 1098907648(0x41800000, float:16.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 - r3
            int r3 = r0.thumbSize
            int r3 = r3 / 2
            int r3 = r3 + r11
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r3 = r3 - r2
            android.graphics.drawable.Drawable r2 = r0.hoverDrawable
            r4 = 1107296256(0x42000000, float:32.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r5 = r5 + r1
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = r4 + r3
            r2.setBounds(r1, r3, r5, r4)
            android.graphics.drawable.Drawable r1 = r0.hoverDrawable
            r1.draw(r7)
        L_0x0233:
            boolean r1 = r0.pressed
            if (r1 == 0) goto L_0x0239
            r14 = 1090519040(0x41000000, float:8.0)
        L_0x0239:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r14)
            long r2 = android.os.SystemClock.elapsedRealtime()
            long r4 = r0.lastUpdateTime
            long r2 = r2 - r4
            r4 = 18
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 <= 0) goto L_0x024c
            r2 = 16
        L_0x024c:
            float r4 = r0.currentRadius
            float r1 = (float) r1
            int r5 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r5 == 0) goto L_0x027f
            r5 = 1114636288(0x42700000, float:60.0)
            int r6 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r6 >= 0) goto L_0x026c
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r6 = (float) r6
            float r12 = (float) r2
            float r12 = r12 / r5
            float r6 = r6 * r12
            float r4 = r4 + r6
            r0.currentRadius = r4
            int r4 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x027e
            r0.currentRadius = r1
            goto L_0x027e
        L_0x026c:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r6 = (float) r6
            float r12 = (float) r2
            float r12 = r12 / r5
            float r6 = r6 * r12
            float r4 = r4 - r6
            r0.currentRadius = r4
            int r4 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r4 >= 0) goto L_0x027e
            r0.currentRadius = r1
        L_0x027e:
            r12 = 1
        L_0x027f:
            float r1 = r0.transitionProgress
            int r4 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
            if (r4 >= 0) goto L_0x0293
            float r2 = (float) r2
            r3 = 1130430464(0x43610000, float:225.0)
            float r2 = r2 / r3
            float r1 = r1 + r2
            r0.transitionProgress = r1
            int r1 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
            if (r1 >= 0) goto L_0x0291
            goto L_0x0294
        L_0x0291:
            r0.transitionProgress = r9
        L_0x0293:
            r8 = r12
        L_0x0294:
            float r1 = r0.transitionProgress
            int r2 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
            if (r2 >= 0) goto L_0x02e2
            android.view.animation.Interpolator r2 = org.telegram.ui.Components.Easings.easeInQuad
            r3 = 1077936128(0x40400000, float:3.0)
            float r1 = r1 * r3
            float r1 = java.lang.Math.min(r9, r1)
            float r1 = r2.getInterpolation(r1)
            float r9 = r9 - r1
            android.view.animation.Interpolator r1 = org.telegram.ui.Components.Easings.easeOutQuad
            float r2 = r0.transitionProgress
            float r1 = r1.getInterpolation(r2)
            int r2 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1))
            if (r2 <= 0) goto L_0x02cc
            int r2 = r0.transitionThumbX
            int r3 = r0.selectorWidth
            int r3 = r3 / 2
            int r2 = r2 + r3
            float r2 = (float) r2
            int r3 = r0.thumbSize
            int r3 = r3 / 2
            int r3 = r3 + r11
            float r3 = (float) r3
            float r4 = r0.currentRadius
            float r4 = r4 * r9
            android.graphics.Paint r5 = r0.outerPaint1
            r7.drawCircle(r2, r3, r4, r5)
        L_0x02cc:
            int r2 = r0.selectorWidth
            int r2 = r2 / 2
            int r10 = r10 + r2
            float r2 = (float) r10
            int r3 = r0.thumbSize
            int r3 = r3 / 2
            int r11 = r11 + r3
            float r3 = (float) r11
            float r4 = r0.currentRadius
            float r4 = r4 * r1
            android.graphics.Paint r1 = r0.outerPaint1
            r7.drawCircle(r2, r3, r4, r1)
            goto L_0x02f5
        L_0x02e2:
            int r1 = r0.selectorWidth
            int r1 = r1 / 2
            int r10 = r10 + r1
            float r1 = (float) r10
            int r2 = r0.thumbSize
            int r2 = r2 / 2
            int r11 = r11 + r2
            float r2 = (float) r11
            float r3 = r0.currentRadius
            android.graphics.Paint r4 = r0.outerPaint1
            r7.drawCircle(r1, r2, r3, r4)
        L_0x02f5:
            if (r8 == 0) goto L_0x02fa
            r15.postInvalidateOnAnimation()
        L_0x02fa:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SeekBarView.onDraw(android.graphics.Canvas):void");
    }

    public SeekBarAccessibilityDelegate getSeekBarAccessibilityDelegate() {
        return this.seekBarAccessibilityDelegate;
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}

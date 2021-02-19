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
    private final SeekBarAccessibilityDelegate seekBarAccessibilityDelegate;
    private int selectorWidth;
    float sx;
    float sy;
    private int thumbDX;
    private int thumbSize;
    private int thumbX;
    private float transitionProgress;
    private int transitionThumbX;

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
        this(context, false);
    }

    public SeekBarView(Context context, boolean z) {
        super(context);
        this.pressedState = new int[]{16842910, 16842919};
        this.transitionProgress = 1.0f;
        setWillNotDraw(false);
        this.innerPaint1 = new Paint(1);
        Paint paint = new Paint(1);
        this.outerPaint1 = paint;
        paint.setColor(Theme.getColor("player_progress"));
        this.selectorWidth = AndroidUtilities.dp(32.0f);
        this.thumbSize = AndroidUtilities.dp(24.0f);
        this.currentRadius = (float) AndroidUtilities.dp(6.0f);
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable createSelectorDrawable = Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(Theme.getColor("player_progress"), 40), 1, AndroidUtilities.dp(16.0f));
            this.hoverDrawable = createSelectorDrawable;
            createSelectorDrawable.setCallback(this);
            this.hoverDrawable.setVisible(true, false);
        }
        setImportantForAccessibility(1);
        AnonymousClass1 r5 = new FloatSeekBarAccessibilityDelegate(z) {
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
        this.seekBarAccessibilityDelegate = r5;
        setAccessibilityDelegate(r5);
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
                    this.delegate.onSeekBarDrag(true, ((float) this.thumbX) / ((float) (getMeasuredWidth() - this.selectorWidth)));
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
                    this.delegate.onSeekBarDrag(false, ((float) this.thumbX) / ((float) (getMeasuredWidth() - this.selectorWidth)));
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
        if (getMeasuredWidth() == 0) {
            this.progressToSet = f;
            return;
        }
        this.progressToSet = -1.0f;
        int ceil = (int) Math.ceil((double) (((float) (getMeasuredWidth() - this.selectorWidth)) * f));
        int i = this.thumbX;
        if (i != ceil) {
            if (z) {
                this.transitionThumbX = i;
                this.transitionProgress = 0.0f;
            }
            this.thumbX = ceil;
            if (ceil < 0) {
                this.thumbX = 0;
            } else if (ceil > getMeasuredWidth() - this.selectorWidth) {
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
        if (this.progressToSet >= 0.0f && getMeasuredWidth() > 0) {
            setProgress(this.progressToSet);
            this.progressToSet = -1.0f;
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
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0153  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x019d  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x01b4  */
    /* JADX WARNING: Removed duplicated region for block: B:40:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r12) {
        /*
            r11 = this;
            int r0 = r11.getMeasuredHeight()
            int r1 = r11.thumbSize
            int r0 = r0 - r1
            int r6 = r0 / 2
            android.graphics.Paint r0 = r11.innerPaint1
            java.lang.String r1 = "player_progressBackground"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setColor(r1)
            int r0 = r11.selectorWidth
            int r0 = r0 / 2
            float r1 = (float) r0
            int r0 = r11.getMeasuredHeight()
            int r0 = r0 / 2
            r7 = 1065353216(0x3var_, float:1.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 - r2
            float r2 = (float) r0
            int r0 = r11.getMeasuredWidth()
            int r3 = r11.selectorWidth
            int r3 = r3 / 2
            int r0 = r0 - r3
            float r3 = (float) r0
            int r0 = r11.getMeasuredHeight()
            int r0 = r0 / 2
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 + r4
            float r4 = (float) r0
            android.graphics.Paint r5 = r11.innerPaint1
            r0 = r12
            r0.drawRect(r1, r2, r3, r4, r5)
            float r0 = r11.bufferedProgress
            r8 = 0
            int r0 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
            if (r0 <= 0) goto L_0x008a
            android.graphics.Paint r0 = r11.innerPaint1
            java.lang.String r1 = "key_player_progressCachedBackground"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setColor(r1)
            int r0 = r11.selectorWidth
            int r0 = r0 / 2
            float r1 = (float) r0
            int r0 = r11.getMeasuredHeight()
            int r0 = r0 / 2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 - r2
            float r2 = (float) r0
            int r0 = r11.selectorWidth
            int r0 = r0 / 2
            float r0 = (float) r0
            float r3 = r11.bufferedProgress
            int r4 = r11.getMeasuredWidth()
            int r5 = r11.selectorWidth
            int r4 = r4 - r5
            float r4 = (float) r4
            float r3 = r3 * r4
            float r3 = r3 + r0
            int r0 = r11.getMeasuredHeight()
            int r0 = r0 / 2
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 + r4
            float r4 = (float) r0
            android.graphics.Paint r5 = r11.innerPaint1
            r0 = r12
            r0.drawRect(r1, r2, r3, r4, r5)
        L_0x008a:
            int r0 = r11.selectorWidth
            int r0 = r0 / 2
            float r1 = (float) r0
            int r0 = r11.getMeasuredHeight()
            int r0 = r0 / 2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 - r2
            float r2 = (float) r0
            int r0 = r11.selectorWidth
            int r0 = r0 / 2
            int r3 = r11.thumbX
            int r0 = r0 + r3
            float r3 = (float) r0
            int r0 = r11.getMeasuredHeight()
            int r0 = r0 / 2
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r0 = r0 + r4
            float r4 = (float) r0
            android.graphics.Paint r5 = r11.outerPaint1
            r0 = r12
            r0.drawRect(r1, r2, r3, r4, r5)
            android.graphics.drawable.Drawable r0 = r11.hoverDrawable
            if (r0 == 0) goto L_0x00e7
            int r0 = r11.thumbX
            int r1 = r11.selectorWidth
            int r1 = r1 / 2
            int r0 = r0 + r1
            r1 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 - r2
            int r2 = r11.thumbSize
            int r2 = r2 / 2
            int r2 = r2 + r6
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r2 = r2 - r1
            android.graphics.drawable.Drawable r1 = r11.hoverDrawable
            r3 = 1107296256(0x42000000, float:32.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r4 = r4 + r0
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = r3 + r2
            r1.setBounds(r0, r2, r4, r3)
            android.graphics.drawable.Drawable r0 = r11.hoverDrawable
            r0.draw(r12)
        L_0x00e7:
            r0 = 0
            boolean r1 = r11.pressed
            if (r1 == 0) goto L_0x00ef
            r1 = 1090519040(0x41000000, float:8.0)
            goto L_0x00f1
        L_0x00ef:
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
        L_0x00f1:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            long r2 = android.os.SystemClock.elapsedRealtime()
            long r4 = r11.lastUpdateTime
            long r2 = r2 - r4
            r4 = 18
            int r9 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r9 <= 0) goto L_0x0104
            r2 = 16
        L_0x0104:
            float r4 = r11.currentRadius
            float r1 = (float) r1
            r5 = 1
            int r9 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r9 == 0) goto L_0x0138
            r0 = 1114636288(0x42700000, float:60.0)
            int r9 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r9 >= 0) goto L_0x0125
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r9 = (float) r9
            float r10 = (float) r2
            float r10 = r10 / r0
            float r9 = r9 * r10
            float r4 = r4 + r9
            r11.currentRadius = r4
            int r0 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x0137
            r11.currentRadius = r1
            goto L_0x0137
        L_0x0125:
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r9 = (float) r9
            float r10 = (float) r2
            float r10 = r10 / r0
            float r9 = r9 * r10
            float r4 = r4 - r9
            r11.currentRadius = r4
            int r0 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x0137
            r11.currentRadius = r1
        L_0x0137:
            r0 = 1
        L_0x0138:
            float r1 = r11.transitionProgress
            int r4 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
            if (r4 >= 0) goto L_0x014c
            float r2 = (float) r2
            r3 = 1130430464(0x43610000, float:225.0)
            float r2 = r2 / r3
            float r1 = r1 + r2
            r11.transitionProgress = r1
            int r1 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
            if (r1 >= 0) goto L_0x014a
            goto L_0x014d
        L_0x014a:
            r11.transitionProgress = r7
        L_0x014c:
            r5 = r0
        L_0x014d:
            float r0 = r11.transitionProgress
            int r1 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r1 >= 0) goto L_0x019d
            android.view.animation.Interpolator r1 = org.telegram.ui.Components.Easings.easeInQuad
            r2 = 1077936128(0x40400000, float:3.0)
            float r0 = r0 * r2
            float r0 = java.lang.Math.min(r7, r0)
            float r0 = r1.getInterpolation(r0)
            float r7 = r7 - r0
            android.view.animation.Interpolator r0 = org.telegram.ui.Components.Easings.easeOutQuad
            float r1 = r11.transitionProgress
            float r0 = r0.getInterpolation(r1)
            int r1 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r1 <= 0) goto L_0x0185
            int r1 = r11.transitionThumbX
            int r2 = r11.selectorWidth
            int r2 = r2 / 2
            int r1 = r1 + r2
            float r1 = (float) r1
            int r2 = r11.thumbSize
            int r2 = r2 / 2
            int r2 = r2 + r6
            float r2 = (float) r2
            float r3 = r11.currentRadius
            float r3 = r3 * r7
            android.graphics.Paint r4 = r11.outerPaint1
            r12.drawCircle(r1, r2, r3, r4)
        L_0x0185:
            int r1 = r11.thumbX
            int r2 = r11.selectorWidth
            int r2 = r2 / 2
            int r1 = r1 + r2
            float r1 = (float) r1
            int r2 = r11.thumbSize
            int r2 = r2 / 2
            int r6 = r6 + r2
            float r2 = (float) r6
            float r3 = r11.currentRadius
            float r3 = r3 * r0
            android.graphics.Paint r0 = r11.outerPaint1
            r12.drawCircle(r1, r2, r3, r0)
            goto L_0x01b2
        L_0x019d:
            int r0 = r11.thumbX
            int r1 = r11.selectorWidth
            int r1 = r1 / 2
            int r0 = r0 + r1
            float r0 = (float) r0
            int r1 = r11.thumbSize
            int r1 = r1 / 2
            int r6 = r6 + r1
            float r1 = (float) r6
            float r2 = r11.currentRadius
            android.graphics.Paint r3 = r11.outerPaint1
            r12.drawCircle(r0, r1, r2, r3)
        L_0x01b2:
            if (r5 == 0) goto L_0x01b7
            r11.postInvalidateOnAnimation()
        L_0x01b7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SeekBarView.onDraw(android.graphics.Canvas):void");
    }

    public SeekBarAccessibilityDelegate getSeekBarAccessibilityDelegate() {
        return this.seekBarAccessibilityDelegate;
    }
}

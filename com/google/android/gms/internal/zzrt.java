package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.SystemClock;
import com.google.android.gms.common.util.zzs;
import org.telegram.messenger.volley.DefaultRetryPolicy;

public final class zzrt extends Drawable implements Callback {
    private int AD;
    private int AE;
    private int AF;
    private int AG;
    private int AH;
    private boolean AI;
    private zzb AJ;
    private Drawable AK;
    private Drawable AL;
    private boolean AM;
    private boolean AN;
    private boolean AO;
    private int AP;
    private boolean Ax;
    private long bZ;
    private int mFrom;

    private static final class zza extends Drawable {
        private static final zza AQ = new zza();
        private static final zza AR = new zza();

        private static final class zza extends ConstantState {
            private zza() {
            }

            public int getChangingConfigurations() {
                return 0;
            }

            public Drawable newDrawable() {
                return zza.AQ;
            }
        }

        private zza() {
        }

        public void draw(Canvas canvas) {
        }

        public ConstantState getConstantState() {
            return AR;
        }

        public int getOpacity() {
            return -2;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }
    }

    static final class zzb extends ConstantState {
        int AS;
        int mChangingConfigurations;

        zzb(zzb com_google_android_gms_internal_zzrt_zzb) {
            if (com_google_android_gms_internal_zzrt_zzb != null) {
                this.mChangingConfigurations = com_google_android_gms_internal_zzrt_zzb.mChangingConfigurations;
                this.AS = com_google_android_gms_internal_zzrt_zzb.AS;
            }
        }

        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }

        public Drawable newDrawable() {
            return new zzrt(this);
        }
    }

    public zzrt(Drawable drawable, Drawable drawable2) {
        this(null);
        if (drawable == null) {
            drawable = zza.AQ;
        }
        this.AK = drawable;
        drawable.setCallback(this);
        zzb com_google_android_gms_internal_zzrt_zzb = this.AJ;
        com_google_android_gms_internal_zzrt_zzb.AS |= drawable.getChangingConfigurations();
        if (drawable2 == null) {
            drawable2 = zza.AQ;
        }
        this.AL = drawable2;
        drawable2.setCallback(this);
        com_google_android_gms_internal_zzrt_zzb = this.AJ;
        com_google_android_gms_internal_zzrt_zzb.AS |= drawable2.getChangingConfigurations();
    }

    zzrt(zzb com_google_android_gms_internal_zzrt_zzb) {
        this.AD = 0;
        this.AF = 255;
        this.AH = 0;
        this.Ax = true;
        this.AJ = new zzb(com_google_android_gms_internal_zzrt_zzb);
    }

    public boolean canConstantState() {
        if (!this.AM) {
            boolean z = (this.AK.getConstantState() == null || this.AL.getConstantState() == null) ? false : true;
            this.AN = z;
            this.AM = true;
        }
        return this.AN;
    }

    public void draw(Canvas canvas) {
        int i = 1;
        int i2 = 0;
        switch (this.AD) {
            case 1:
                this.bZ = SystemClock.uptimeMillis();
                this.AD = 2;
                break;
            case 2:
                if (this.bZ >= 0) {
                    float uptimeMillis = ((float) (SystemClock.uptimeMillis() - this.bZ)) / ((float) this.AG);
                    if (uptimeMillis < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                        i = 0;
                    }
                    if (i != 0) {
                        this.AD = 0;
                    }
                    this.AH = (int) ((Math.min(uptimeMillis, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) * ((float) (this.AE + 0))) + 0.0f);
                    break;
                }
                break;
        }
        i2 = i;
        i = this.AH;
        boolean z = this.Ax;
        Drawable drawable = this.AK;
        Drawable drawable2 = this.AL;
        if (i2 != 0) {
            if (!z || i == 0) {
                drawable.draw(canvas);
            }
            if (i == this.AF) {
                drawable2.setAlpha(this.AF);
                drawable2.draw(canvas);
                return;
            }
            return;
        }
        if (z) {
            drawable.setAlpha(this.AF - i);
        }
        drawable.draw(canvas);
        if (z) {
            drawable.setAlpha(this.AF);
        }
        if (i > 0) {
            drawable2.setAlpha(i);
            drawable2.draw(canvas);
            drawable2.setAlpha(this.AF);
        }
        invalidateSelf();
    }

    public int getChangingConfigurations() {
        return (super.getChangingConfigurations() | this.AJ.mChangingConfigurations) | this.AJ.AS;
    }

    public ConstantState getConstantState() {
        if (!canConstantState()) {
            return null;
        }
        this.AJ.mChangingConfigurations = getChangingConfigurations();
        return this.AJ;
    }

    public int getIntrinsicHeight() {
        return Math.max(this.AK.getIntrinsicHeight(), this.AL.getIntrinsicHeight());
    }

    public int getIntrinsicWidth() {
        return Math.max(this.AK.getIntrinsicWidth(), this.AL.getIntrinsicWidth());
    }

    public int getOpacity() {
        if (!this.AO) {
            this.AP = Drawable.resolveOpacity(this.AK.getOpacity(), this.AL.getOpacity());
            this.AO = true;
        }
        return this.AP;
    }

    @TargetApi(11)
    public void invalidateDrawable(Drawable drawable) {
        if (zzs.zzaxk()) {
            Callback callback = getCallback();
            if (callback != null) {
                callback.invalidateDrawable(this);
            }
        }
    }

    public Drawable mutate() {
        if (!this.AI && super.mutate() == this) {
            if (canConstantState()) {
                this.AK.mutate();
                this.AL.mutate();
                this.AI = true;
            } else {
                throw new IllegalStateException("One or more children of this LayerDrawable does not have constant state; this drawable cannot be mutated.");
            }
        }
        return this;
    }

    protected void onBoundsChange(Rect rect) {
        this.AK.setBounds(rect);
        this.AL.setBounds(rect);
    }

    @TargetApi(11)
    public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
        if (zzs.zzaxk()) {
            Callback callback = getCallback();
            if (callback != null) {
                callback.scheduleDrawable(this, runnable, j);
            }
        }
    }

    public void setAlpha(int i) {
        if (this.AH == this.AF) {
            this.AH = i;
        }
        this.AF = i;
        invalidateSelf();
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.AK.setColorFilter(colorFilter);
        this.AL.setColorFilter(colorFilter);
    }

    public void startTransition(int i) {
        this.mFrom = 0;
        this.AE = this.AF;
        this.AH = 0;
        this.AG = i;
        this.AD = 1;
        invalidateSelf();
    }

    @TargetApi(11)
    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        if (zzs.zzaxk()) {
            Callback callback = getCallback();
            if (callback != null) {
                callback.unscheduleDrawable(this, runnable);
            }
        }
    }

    public Drawable zzatn() {
        return this.AL;
    }
}

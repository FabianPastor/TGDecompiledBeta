package com.google.android.gms.internal;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.SystemClock;

public final class zzacb extends Drawable implements Callback {
    private int mFrom;
    private int zzaED;
    private int zzaEE;
    private int zzaEF;
    private int zzaEG;
    private int zzaEH;
    private boolean zzaEI;
    private zzb zzaEJ;
    private Drawable zzaEK;
    private Drawable zzaEL;
    private boolean zzaEM;
    private boolean zzaEN;
    private boolean zzaEO;
    private int zzaEP;
    private boolean zzaEy;
    private long zzafe;

    private static final class zza extends Drawable {
        private static final zza zzaEQ = new zza();
        private static final zza zzaER = new zza();

        private static final class zza extends ConstantState {
            private zza() {
            }

            public int getChangingConfigurations() {
                return 0;
            }

            public Drawable newDrawable() {
                return zza.zzaEQ;
            }
        }

        private zza() {
        }

        public void draw(Canvas canvas) {
        }

        public ConstantState getConstantState() {
            return zzaER;
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
        int mChangingConfigurations;
        int zzaES;

        zzb(zzb com_google_android_gms_internal_zzacb_zzb) {
            if (com_google_android_gms_internal_zzacb_zzb != null) {
                this.mChangingConfigurations = com_google_android_gms_internal_zzacb_zzb.mChangingConfigurations;
                this.zzaES = com_google_android_gms_internal_zzacb_zzb.zzaES;
            }
        }

        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }

        public Drawable newDrawable() {
            return new zzacb(this);
        }
    }

    public zzacb(Drawable drawable, Drawable drawable2) {
        this(null);
        if (drawable == null) {
            drawable = zza.zzaEQ;
        }
        this.zzaEK = drawable;
        drawable.setCallback(this);
        zzb com_google_android_gms_internal_zzacb_zzb = this.zzaEJ;
        com_google_android_gms_internal_zzacb_zzb.zzaES |= drawable.getChangingConfigurations();
        if (drawable2 == null) {
            drawable2 = zza.zzaEQ;
        }
        this.zzaEL = drawable2;
        drawable2.setCallback(this);
        com_google_android_gms_internal_zzacb_zzb = this.zzaEJ;
        com_google_android_gms_internal_zzacb_zzb.zzaES |= drawable2.getChangingConfigurations();
    }

    zzacb(zzb com_google_android_gms_internal_zzacb_zzb) {
        this.zzaED = 0;
        this.zzaEF = 255;
        this.zzaEH = 0;
        this.zzaEy = true;
        this.zzaEJ = new zzb(com_google_android_gms_internal_zzacb_zzb);
    }

    public boolean canConstantState() {
        if (!this.zzaEM) {
            boolean z = (this.zzaEK.getConstantState() == null || this.zzaEL.getConstantState() == null) ? false : true;
            this.zzaEN = z;
            this.zzaEM = true;
        }
        return this.zzaEN;
    }

    public void draw(Canvas canvas) {
        int i = 1;
        int i2 = 0;
        switch (this.zzaED) {
            case 1:
                this.zzafe = SystemClock.uptimeMillis();
                this.zzaED = 2;
                break;
            case 2:
                if (this.zzafe >= 0) {
                    float uptimeMillis = ((float) (SystemClock.uptimeMillis() - this.zzafe)) / ((float) this.zzaEG);
                    if (uptimeMillis < 1.0f) {
                        i = 0;
                    }
                    if (i != 0) {
                        this.zzaED = 0;
                    }
                    this.zzaEH = (int) ((Math.min(uptimeMillis, 1.0f) * ((float) (this.zzaEE + 0))) + 0.0f);
                    break;
                }
                break;
        }
        i2 = i;
        i = this.zzaEH;
        boolean z = this.zzaEy;
        Drawable drawable = this.zzaEK;
        Drawable drawable2 = this.zzaEL;
        if (i2 != 0) {
            if (!z || i == 0) {
                drawable.draw(canvas);
            }
            if (i == this.zzaEF) {
                drawable2.setAlpha(this.zzaEF);
                drawable2.draw(canvas);
                return;
            }
            return;
        }
        if (z) {
            drawable.setAlpha(this.zzaEF - i);
        }
        drawable.draw(canvas);
        if (z) {
            drawable.setAlpha(this.zzaEF);
        }
        if (i > 0) {
            drawable2.setAlpha(i);
            drawable2.draw(canvas);
            drawable2.setAlpha(this.zzaEF);
        }
        invalidateSelf();
    }

    public int getChangingConfigurations() {
        return (super.getChangingConfigurations() | this.zzaEJ.mChangingConfigurations) | this.zzaEJ.zzaES;
    }

    public ConstantState getConstantState() {
        if (!canConstantState()) {
            return null;
        }
        this.zzaEJ.mChangingConfigurations = getChangingConfigurations();
        return this.zzaEJ;
    }

    public int getIntrinsicHeight() {
        return Math.max(this.zzaEK.getIntrinsicHeight(), this.zzaEL.getIntrinsicHeight());
    }

    public int getIntrinsicWidth() {
        return Math.max(this.zzaEK.getIntrinsicWidth(), this.zzaEL.getIntrinsicWidth());
    }

    public int getOpacity() {
        if (!this.zzaEO) {
            this.zzaEP = Drawable.resolveOpacity(this.zzaEK.getOpacity(), this.zzaEL.getOpacity());
            this.zzaEO = true;
        }
        return this.zzaEP;
    }

    public void invalidateDrawable(Drawable drawable) {
        Callback callback = getCallback();
        if (callback != null) {
            callback.invalidateDrawable(this);
        }
    }

    public Drawable mutate() {
        if (!this.zzaEI && super.mutate() == this) {
            if (canConstantState()) {
                this.zzaEK.mutate();
                this.zzaEL.mutate();
                this.zzaEI = true;
            } else {
                throw new IllegalStateException("One or more children of this LayerDrawable does not have constant state; this drawable cannot be mutated.");
            }
        }
        return this;
    }

    protected void onBoundsChange(Rect rect) {
        this.zzaEK.setBounds(rect);
        this.zzaEL.setBounds(rect);
    }

    public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
        Callback callback = getCallback();
        if (callback != null) {
            callback.scheduleDrawable(this, runnable, j);
        }
    }

    public void setAlpha(int i) {
        if (this.zzaEH == this.zzaEF) {
            this.zzaEH = i;
        }
        this.zzaEF = i;
        invalidateSelf();
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.zzaEK.setColorFilter(colorFilter);
        this.zzaEL.setColorFilter(colorFilter);
    }

    public void startTransition(int i) {
        this.mFrom = 0;
        this.zzaEE = this.zzaEF;
        this.zzaEH = 0;
        this.zzaEG = i;
        this.zzaED = 1;
        invalidateSelf();
    }

    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        Callback callback = getCallback();
        if (callback != null) {
            callback.unscheduleDrawable(this, runnable);
        }
    }

    public Drawable zzxs() {
        return this.zzaEL;
    }
}

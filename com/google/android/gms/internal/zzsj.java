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

public final class zzsj extends Drawable implements Callback {
    private boolean CH;
    private int CM;
    private int CN;
    private int CO;
    private int CP;
    private int CQ;
    private boolean CR;
    private zzb CS;
    private Drawable CT;
    private Drawable CU;
    private boolean CV;
    private boolean CW;
    private boolean CX;
    private int CY;
    private long eg;
    private int mFrom;

    private static final class zza extends Drawable {
        private static final zza CZ = new zza();
        private static final zza Da = new zza();

        private static final class zza extends ConstantState {
            private zza() {
            }

            public int getChangingConfigurations() {
                return 0;
            }

            public Drawable newDrawable() {
                return zza.CZ;
            }
        }

        private zza() {
        }

        public void draw(Canvas canvas) {
        }

        public ConstantState getConstantState() {
            return Da;
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
        int Db;
        int mChangingConfigurations;

        zzb(zzb com_google_android_gms_internal_zzsj_zzb) {
            if (com_google_android_gms_internal_zzsj_zzb != null) {
                this.mChangingConfigurations = com_google_android_gms_internal_zzsj_zzb.mChangingConfigurations;
                this.Db = com_google_android_gms_internal_zzsj_zzb.Db;
            }
        }

        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }

        public Drawable newDrawable() {
            return new zzsj(this);
        }
    }

    public zzsj(Drawable drawable, Drawable drawable2) {
        this(null);
        if (drawable == null) {
            drawable = zza.CZ;
        }
        this.CT = drawable;
        drawable.setCallback(this);
        zzb com_google_android_gms_internal_zzsj_zzb = this.CS;
        com_google_android_gms_internal_zzsj_zzb.Db |= drawable.getChangingConfigurations();
        if (drawable2 == null) {
            drawable2 = zza.CZ;
        }
        this.CU = drawable2;
        drawable2.setCallback(this);
        com_google_android_gms_internal_zzsj_zzb = this.CS;
        com_google_android_gms_internal_zzsj_zzb.Db |= drawable2.getChangingConfigurations();
    }

    zzsj(zzb com_google_android_gms_internal_zzsj_zzb) {
        this.CM = 0;
        this.CO = 255;
        this.CQ = 0;
        this.CH = true;
        this.CS = new zzb(com_google_android_gms_internal_zzsj_zzb);
    }

    public boolean canConstantState() {
        if (!this.CV) {
            boolean z = (this.CT.getConstantState() == null || this.CU.getConstantState() == null) ? false : true;
            this.CW = z;
            this.CV = true;
        }
        return this.CW;
    }

    public void draw(Canvas canvas) {
        int i = 1;
        int i2 = 0;
        switch (this.CM) {
            case 1:
                this.eg = SystemClock.uptimeMillis();
                this.CM = 2;
                break;
            case 2:
                if (this.eg >= 0) {
                    float uptimeMillis = ((float) (SystemClock.uptimeMillis() - this.eg)) / ((float) this.CP);
                    if (uptimeMillis < DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                        i = 0;
                    }
                    if (i != 0) {
                        this.CM = 0;
                    }
                    this.CQ = (int) ((Math.min(uptimeMillis, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) * ((float) (this.CN + 0))) + 0.0f);
                    break;
                }
                break;
        }
        i2 = i;
        i = this.CQ;
        boolean z = this.CH;
        Drawable drawable = this.CT;
        Drawable drawable2 = this.CU;
        if (i2 != 0) {
            if (!z || i == 0) {
                drawable.draw(canvas);
            }
            if (i == this.CO) {
                drawable2.setAlpha(this.CO);
                drawable2.draw(canvas);
                return;
            }
            return;
        }
        if (z) {
            drawable.setAlpha(this.CO - i);
        }
        drawable.draw(canvas);
        if (z) {
            drawable.setAlpha(this.CO);
        }
        if (i > 0) {
            drawable2.setAlpha(i);
            drawable2.draw(canvas);
            drawable2.setAlpha(this.CO);
        }
        invalidateSelf();
    }

    public int getChangingConfigurations() {
        return (super.getChangingConfigurations() | this.CS.mChangingConfigurations) | this.CS.Db;
    }

    public ConstantState getConstantState() {
        if (!canConstantState()) {
            return null;
        }
        this.CS.mChangingConfigurations = getChangingConfigurations();
        return this.CS;
    }

    public int getIntrinsicHeight() {
        return Math.max(this.CT.getIntrinsicHeight(), this.CU.getIntrinsicHeight());
    }

    public int getIntrinsicWidth() {
        return Math.max(this.CT.getIntrinsicWidth(), this.CU.getIntrinsicWidth());
    }

    public int getOpacity() {
        if (!this.CX) {
            this.CY = Drawable.resolveOpacity(this.CT.getOpacity(), this.CU.getOpacity());
            this.CX = true;
        }
        return this.CY;
    }

    @TargetApi(11)
    public void invalidateDrawable(Drawable drawable) {
        if (zzs.zzayn()) {
            Callback callback = getCallback();
            if (callback != null) {
                callback.invalidateDrawable(this);
            }
        }
    }

    public Drawable mutate() {
        if (!this.CR && super.mutate() == this) {
            if (canConstantState()) {
                this.CT.mutate();
                this.CU.mutate();
                this.CR = true;
            } else {
                throw new IllegalStateException("One or more children of this LayerDrawable does not have constant state; this drawable cannot be mutated.");
            }
        }
        return this;
    }

    protected void onBoundsChange(Rect rect) {
        this.CT.setBounds(rect);
        this.CU.setBounds(rect);
    }

    @TargetApi(11)
    public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
        if (zzs.zzayn()) {
            Callback callback = getCallback();
            if (callback != null) {
                callback.scheduleDrawable(this, runnable, j);
            }
        }
    }

    public void setAlpha(int i) {
        if (this.CQ == this.CO) {
            this.CQ = i;
        }
        this.CO = i;
        invalidateSelf();
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.CT.setColorFilter(colorFilter);
        this.CU.setColorFilter(colorFilter);
    }

    public void startTransition(int i) {
        this.mFrom = 0;
        this.CN = this.CO;
        this.CQ = 0;
        this.CP = i;
        this.CM = 1;
        invalidateSelf();
    }

    @TargetApi(11)
    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        if (zzs.zzayn()) {
            Callback callback = getCallback();
            if (callback != null) {
                callback.unscheduleDrawable(this, runnable);
            }
        }
    }

    public Drawable zzauw() {
        return this.CU;
    }
}

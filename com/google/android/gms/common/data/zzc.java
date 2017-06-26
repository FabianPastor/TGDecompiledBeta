package com.google.android.gms.common.data;

import android.database.CharArrayBuffer;
import android.net.Uri;
import com.google.android.gms.common.internal.zzbe;
import com.google.android.gms.common.internal.zzbo;
import java.util.Arrays;

public class zzc {
    protected final DataHolder zzaCX;
    protected int zzaFx;
    private int zzaFy;

    public zzc(DataHolder dataHolder, int i) {
        this.zzaCX = (DataHolder) zzbo.zzu(dataHolder);
        zzar(i);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof zzc)) {
            return false;
        }
        zzc com_google_android_gms_common_data_zzc = (zzc) obj;
        return zzbe.equal(Integer.valueOf(com_google_android_gms_common_data_zzc.zzaFx), Integer.valueOf(this.zzaFx)) && zzbe.equal(Integer.valueOf(com_google_android_gms_common_data_zzc.zzaFy), Integer.valueOf(this.zzaFy)) && com_google_android_gms_common_data_zzc.zzaCX == this.zzaCX;
    }

    protected final boolean getBoolean(String str) {
        return this.zzaCX.zze(str, this.zzaFx, this.zzaFy);
    }

    protected final byte[] getByteArray(String str) {
        return this.zzaCX.zzg(str, this.zzaFx, this.zzaFy);
    }

    protected final float getFloat(String str) {
        return this.zzaCX.zzf(str, this.zzaFx, this.zzaFy);
    }

    protected final int getInteger(String str) {
        return this.zzaCX.zzc(str, this.zzaFx, this.zzaFy);
    }

    protected final long getLong(String str) {
        return this.zzaCX.zzb(str, this.zzaFx, this.zzaFy);
    }

    protected final String getString(String str) {
        return this.zzaCX.zzd(str, this.zzaFx, this.zzaFy);
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[]{Integer.valueOf(this.zzaFx), Integer.valueOf(this.zzaFy), this.zzaCX});
    }

    public boolean isDataValid() {
        return !this.zzaCX.isClosed();
    }

    protected final void zza(String str, CharArrayBuffer charArrayBuffer) {
        this.zzaCX.zza(str, this.zzaFx, this.zzaFy, charArrayBuffer);
    }

    protected final void zzar(int i) {
        boolean z = i >= 0 && i < this.zzaCX.zzaFG;
        zzbo.zzae(z);
        this.zzaFx = i;
        this.zzaFy = this.zzaCX.zzat(this.zzaFx);
    }

    public final boolean zzcv(String str) {
        return this.zzaCX.zzcv(str);
    }

    protected final Uri zzcw(String str) {
        String zzd = this.zzaCX.zzd(str, this.zzaFx, this.zzaFy);
        return zzd == null ? null : Uri.parse(zzd);
    }

    protected final boolean zzcx(String str) {
        return this.zzaCX.zzh(str, this.zzaFx, this.zzaFy);
    }
}

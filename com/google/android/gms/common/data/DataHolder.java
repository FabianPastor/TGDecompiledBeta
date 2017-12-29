package com.google.android.gms.common.data;

import android.database.CursorIndexOutOfBoundsException;
import android.database.CursorWindow;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.Log;
import com.google.android.gms.common.annotation.KeepName;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;

@KeepName
public final class DataHolder extends zzbfm implements Closeable {
    public static final Creator<DataHolder> CREATOR = new zzf();
    private static final zza zzfwi = new zze(new String[0], null);
    private boolean mClosed = false;
    private final int zzcd;
    private int zzeck;
    private final String[] zzfwb;
    private Bundle zzfwc;
    private final CursorWindow[] zzfwd;
    private final Bundle zzfwe;
    private int[] zzfwf;
    int zzfwg;
    private boolean zzfwh = true;

    public static class zza {
        private final String[] zzfwb;
        private final ArrayList<HashMap<String, Object>> zzfwj;
        private final String zzfwk;
        private final HashMap<Object, Integer> zzfwl;
        private boolean zzfwm;
        private String zzfwn;

        private zza(String[] strArr, String str) {
            this.zzfwb = (String[]) zzbq.checkNotNull(strArr);
            this.zzfwj = new ArrayList();
            this.zzfwk = str;
            this.zzfwl = new HashMap();
            this.zzfwm = false;
            this.zzfwn = null;
        }
    }

    DataHolder(int i, String[] strArr, CursorWindow[] cursorWindowArr, int i2, Bundle bundle) {
        this.zzeck = i;
        this.zzfwb = strArr;
        this.zzfwd = cursorWindowArr;
        this.zzcd = i2;
        this.zzfwe = bundle;
    }

    private final void zzh(String str, int i) {
        if (this.zzfwc == null || !this.zzfwc.containsKey(str)) {
            String str2 = "No such column: ";
            String valueOf = String.valueOf(str);
            throw new IllegalArgumentException(valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
        } else if (isClosed()) {
            throw new IllegalArgumentException("Buffer is closed.");
        } else if (i < 0 || i >= this.zzfwg) {
            throw new CursorIndexOutOfBoundsException(i, this.zzfwg);
        }
    }

    public final void close() {
        synchronized (this) {
            if (!this.mClosed) {
                this.mClosed = true;
                for (CursorWindow close : this.zzfwd) {
                    close.close();
                }
            }
        }
    }

    protected final void finalize() throws Throwable {
        try {
            if (this.zzfwh && this.zzfwd.length > 0 && !isClosed()) {
                close();
                String obj = toString();
                Log.e("DataBuffer", new StringBuilder(String.valueOf(obj).length() + 178).append("Internal data leak within a DataBuffer object detected!  Be sure to explicitly call release() on all DataBuffer extending objects when you are done with them. (internal object: ").append(obj).append(")").toString());
            }
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
        }
    }

    public final int getCount() {
        return this.zzfwg;
    }

    public final int getStatusCode() {
        return this.zzcd;
    }

    public final boolean isClosed() {
        boolean z;
        synchronized (this) {
            z = this.mClosed;
        }
        return z;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 1, this.zzfwb, false);
        zzbfp.zza(parcel, 2, this.zzfwd, i, false);
        zzbfp.zzc(parcel, 3, this.zzcd);
        zzbfp.zza(parcel, 4, this.zzfwe, false);
        zzbfp.zzc(parcel, 1000, this.zzeck);
        zzbfp.zzai(parcel, zze);
        if ((i & 1) != 0) {
            close();
        }
    }

    public final void zzajz() {
        int i;
        int i2 = 0;
        this.zzfwc = new Bundle();
        for (i = 0; i < this.zzfwb.length; i++) {
            this.zzfwc.putInt(this.zzfwb[i], i);
        }
        this.zzfwf = new int[this.zzfwd.length];
        i = 0;
        while (i2 < this.zzfwd.length) {
            this.zzfwf[i2] = i;
            i += this.zzfwd[i2].getNumRows() - (i - this.zzfwd[i2].getStartPosition());
            i2++;
        }
        this.zzfwg = i;
    }

    public final int zzbz(int i) {
        int i2 = 0;
        boolean z = i >= 0 && i < this.zzfwg;
        zzbq.checkState(z);
        while (i2 < this.zzfwf.length) {
            if (i < this.zzfwf[i2]) {
                i2--;
                break;
            }
            i2++;
        }
        return i2 == this.zzfwf.length ? i2 - 1 : i2;
    }

    public final int zzc(String str, int i, int i2) {
        zzh(str, i);
        return this.zzfwd[i2].getInt(i, this.zzfwc.getInt(str));
    }

    public final String zzd(String str, int i, int i2) {
        zzh(str, i);
        return this.zzfwd[i2].getString(i, this.zzfwc.getInt(str));
    }

    public final byte[] zzg(String str, int i, int i2) {
        zzh(str, i);
        return this.zzfwd[i2].getBlob(i, this.zzfwc.getInt(str));
    }
}

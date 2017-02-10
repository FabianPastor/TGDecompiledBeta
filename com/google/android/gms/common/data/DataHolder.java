package com.google.android.gms.common.data;

import android.content.ContentValues;
import android.database.CharArrayBuffer;
import android.database.CursorIndexOutOfBoundsException;
import android.database.CursorWindow;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.Log;
import com.google.android.gms.common.annotation.KeepName;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzc;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@KeepName
public final class DataHolder extends com.google.android.gms.common.internal.safeparcel.zza implements Closeable {
    public static final Creator<DataHolder> CREATOR = new zze();
    private static final zza zzaCx = new AnonymousClass1(new String[0], null);
    boolean mClosed;
    final int mVersionCode;
    private final String[] zzaCq;
    Bundle zzaCr;
    private final CursorWindow[] zzaCs;
    private final Bundle zzaCt;
    int[] zzaCu;
    int zzaCv;
    private boolean zzaCw;
    private final int zzauz;

    public static class zza {
        private final HashMap<Object, Integer> zzaCA;
        private boolean zzaCB;
        private String zzaCC;
        private final String[] zzaCq;
        private final ArrayList<HashMap<String, Object>> zzaCy;
        private final String zzaCz;

        private zza(String[] strArr, String str) {
            this.zzaCq = (String[]) zzac.zzw(strArr);
            this.zzaCy = new ArrayList();
            this.zzaCz = str;
            this.zzaCA = new HashMap();
            this.zzaCB = false;
            this.zzaCC = null;
        }

        private int zzc(HashMap<String, Object> hashMap) {
            if (this.zzaCz == null) {
                return -1;
            }
            Object obj = hashMap.get(this.zzaCz);
            if (obj == null) {
                return -1;
            }
            Integer num = (Integer) this.zzaCA.get(obj);
            if (num != null) {
                return num.intValue();
            }
            this.zzaCA.put(obj, Integer.valueOf(this.zzaCy.size()));
            return -1;
        }

        public zza zza(ContentValues contentValues) {
            zzc.zzt(contentValues);
            HashMap hashMap = new HashMap(contentValues.size());
            for (Entry entry : contentValues.valueSet()) {
                hashMap.put((String) entry.getKey(), entry.getValue());
            }
            return zzb(hashMap);
        }

        public zza zzb(HashMap<String, Object> hashMap) {
            zzc.zzt(hashMap);
            int zzc = zzc(hashMap);
            if (zzc == -1) {
                this.zzaCy.add(hashMap);
            } else {
                this.zzaCy.remove(zzc);
                this.zzaCy.add(zzc, hashMap);
            }
            this.zzaCB = false;
            return this;
        }

        public DataHolder zzcE(int i) {
            return new DataHolder(this, i, null);
        }
    }

    public static class zzb extends RuntimeException {
        public zzb(String str) {
            super(str);
        }
    }

    class AnonymousClass1 extends zza {
        AnonymousClass1(String[] strArr, String str) {
            super(strArr, str);
        }

        public zza zza(ContentValues contentValues) {
            throw new UnsupportedOperationException("Cannot add data to empty builder");
        }

        public zza zzb(HashMap<String, Object> hashMap) {
            throw new UnsupportedOperationException("Cannot add data to empty builder");
        }
    }

    DataHolder(int i, String[] strArr, CursorWindow[] cursorWindowArr, int i2, Bundle bundle) {
        this.mClosed = false;
        this.zzaCw = true;
        this.mVersionCode = i;
        this.zzaCq = strArr;
        this.zzaCs = cursorWindowArr;
        this.zzauz = i2;
        this.zzaCt = bundle;
    }

    private DataHolder(zza com_google_android_gms_common_data_DataHolder_zza, int i, Bundle bundle) {
        this(com_google_android_gms_common_data_DataHolder_zza.zzaCq, zza(com_google_android_gms_common_data_DataHolder_zza, -1), i, bundle);
    }

    public DataHolder(String[] strArr, CursorWindow[] cursorWindowArr, int i, Bundle bundle) {
        this.mClosed = false;
        this.zzaCw = true;
        this.mVersionCode = 1;
        this.zzaCq = (String[]) zzac.zzw(strArr);
        this.zzaCs = (CursorWindow[]) zzac.zzw(cursorWindowArr);
        this.zzauz = i;
        this.zzaCt = bundle;
        zzwD();
    }

    public static DataHolder zza(int i, Bundle bundle) {
        return new DataHolder(zzaCx, i, bundle);
    }

    private static CursorWindow[] zza(zza com_google_android_gms_common_data_DataHolder_zza, int i) {
        int size;
        int i2 = 0;
        if (com_google_android_gms_common_data_DataHolder_zza.zzaCq.length == 0) {
            return new CursorWindow[0];
        }
        List zzb = (i < 0 || i >= com_google_android_gms_common_data_DataHolder_zza.zzaCy.size()) ? com_google_android_gms_common_data_DataHolder_zza.zzaCy : com_google_android_gms_common_data_DataHolder_zza.zzaCy.subList(0, i);
        int size2 = zzb.size();
        CursorWindow cursorWindow = new CursorWindow(false);
        ArrayList arrayList = new ArrayList();
        arrayList.add(cursorWindow);
        cursorWindow.setNumColumns(com_google_android_gms_common_data_DataHolder_zza.zzaCq.length);
        int i3 = 0;
        int i4 = 0;
        while (i3 < size2) {
            if (!cursorWindow.allocRow()) {
                Log.d("DataHolder", "Allocating additional cursor window for large data set (row " + i3 + ")");
                cursorWindow = new CursorWindow(false);
                cursorWindow.setStartPosition(i3);
                cursorWindow.setNumColumns(com_google_android_gms_common_data_DataHolder_zza.zzaCq.length);
                arrayList.add(cursorWindow);
                if (!cursorWindow.allocRow()) {
                    Log.e("DataHolder", "Unable to allocate row to hold data.");
                    arrayList.remove(cursorWindow);
                    return (CursorWindow[]) arrayList.toArray(new CursorWindow[arrayList.size()]);
                }
            }
            try {
                int i5;
                int i6;
                CursorWindow cursorWindow2;
                Map map = (Map) zzb.get(i3);
                boolean z = true;
                for (int i7 = 0; i7 < com_google_android_gms_common_data_DataHolder_zza.zzaCq.length && z; i7++) {
                    String str = com_google_android_gms_common_data_DataHolder_zza.zzaCq[i7];
                    Object obj = map.get(str);
                    if (obj == null) {
                        z = cursorWindow.putNull(i3, i7);
                    } else if (obj instanceof String) {
                        z = cursorWindow.putString((String) obj, i3, i7);
                    } else if (obj instanceof Long) {
                        z = cursorWindow.putLong(((Long) obj).longValue(), i3, i7);
                    } else if (obj instanceof Integer) {
                        z = cursorWindow.putLong((long) ((Integer) obj).intValue(), i3, i7);
                    } else if (obj instanceof Boolean) {
                        z = cursorWindow.putLong(((Boolean) obj).booleanValue() ? 1 : 0, i3, i7);
                    } else if (obj instanceof byte[]) {
                        z = cursorWindow.putBlob((byte[]) obj, i3, i7);
                    } else if (obj instanceof Double) {
                        z = cursorWindow.putDouble(((Double) obj).doubleValue(), i3, i7);
                    } else if (obj instanceof Float) {
                        z = cursorWindow.putDouble((double) ((Float) obj).floatValue(), i3, i7);
                    } else {
                        String valueOf = String.valueOf(obj);
                        throw new IllegalArgumentException(new StringBuilder((String.valueOf(str).length() + 32) + String.valueOf(valueOf).length()).append("Unsupported object for column ").append(str).append(": ").append(valueOf).toString());
                    }
                }
                if (z) {
                    i5 = i3;
                    i6 = 0;
                    cursorWindow2 = cursorWindow;
                } else if (i4 != 0) {
                    throw new zzb("Could not add the value to a new CursorWindow. The size of value may be larger than what a CursorWindow can handle.");
                } else {
                    Log.d("DataHolder", "Couldn't populate window data for row " + i3 + " - allocating new window.");
                    cursorWindow.freeLastRow();
                    CursorWindow cursorWindow3 = new CursorWindow(false);
                    cursorWindow3.setStartPosition(i3);
                    cursorWindow3.setNumColumns(com_google_android_gms_common_data_DataHolder_zza.zzaCq.length);
                    arrayList.add(cursorWindow3);
                    i5 = i3 - 1;
                    cursorWindow2 = cursorWindow3;
                    i6 = 1;
                }
                i4 = i6;
                cursorWindow = cursorWindow2;
                i3 = i5 + 1;
            } catch (RuntimeException e) {
                RuntimeException runtimeException = e;
                size = arrayList.size();
                while (i2 < size) {
                    ((CursorWindow) arrayList.get(i2)).close();
                    i2++;
                }
                throw runtimeException;
            }
        }
        return (CursorWindow[]) arrayList.toArray(new CursorWindow[arrayList.size()]);
    }

    public static zza zzc(String[] strArr) {
        return new zza(strArr, null);
    }

    public static DataHolder zzcD(int i) {
        return zza(i, null);
    }

    private void zzi(String str, int i) {
        if (this.zzaCr == null || !this.zzaCr.containsKey(str)) {
            String str2 = "No such column: ";
            String valueOf = String.valueOf(str);
            throw new IllegalArgumentException(valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
        } else if (isClosed()) {
            throw new IllegalArgumentException("Buffer is closed.");
        } else if (i < 0 || i >= this.zzaCv) {
            throw new CursorIndexOutOfBoundsException(i, this.zzaCv);
        }
    }

    public void close() {
        synchronized (this) {
            if (!this.mClosed) {
                this.mClosed = true;
                for (CursorWindow close : this.zzaCs) {
                    close.close();
                }
            }
        }
    }

    protected void finalize() throws Throwable {
        try {
            if (this.zzaCw && this.zzaCs.length > 0 && !isClosed()) {
                close();
                String valueOf = String.valueOf(toString());
                Log.e("DataBuffer", new StringBuilder(String.valueOf(valueOf).length() + 178).append("Internal data leak within a DataBuffer object detected!  Be sure to explicitly call release() on all DataBuffer extending objects when you are done with them. (internal object: ").append(valueOf).append(")").toString());
            }
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
        }
    }

    public int getCount() {
        return this.zzaCv;
    }

    public int getStatusCode() {
        return this.zzauz;
    }

    public boolean isClosed() {
        boolean z;
        synchronized (this) {
            z = this.mClosed;
        }
        return z;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zze.zza(this, parcel, i);
    }

    public void zza(String str, int i, int i2, CharArrayBuffer charArrayBuffer) {
        zzi(str, i);
        this.zzaCs[i2].copyStringToBuffer(i, this.zzaCr.getInt(str), charArrayBuffer);
    }

    public long zzb(String str, int i, int i2) {
        zzi(str, i);
        return this.zzaCs[i2].getLong(i, this.zzaCr.getInt(str));
    }

    public int zzc(String str, int i, int i2) {
        zzi(str, i);
        return this.zzaCs[i2].getInt(i, this.zzaCr.getInt(str));
    }

    public int zzcC(int i) {
        int i2 = 0;
        boolean z = i >= 0 && i < this.zzaCv;
        zzac.zzar(z);
        while (i2 < this.zzaCu.length) {
            if (i < this.zzaCu[i2]) {
                i2--;
                break;
            }
            i2++;
        }
        return i2 == this.zzaCu.length ? i2 - 1 : i2;
    }

    public String zzd(String str, int i, int i2) {
        zzi(str, i);
        return this.zzaCs[i2].getString(i, this.zzaCr.getInt(str));
    }

    public boolean zzdj(String str) {
        return this.zzaCr.containsKey(str);
    }

    public boolean zze(String str, int i, int i2) {
        zzi(str, i);
        return Long.valueOf(this.zzaCs[i2].getLong(i, this.zzaCr.getInt(str))).longValue() == 1;
    }

    public float zzf(String str, int i, int i2) {
        zzi(str, i);
        return this.zzaCs[i2].getFloat(i, this.zzaCr.getInt(str));
    }

    public byte[] zzg(String str, int i, int i2) {
        zzi(str, i);
        return this.zzaCs[i2].getBlob(i, this.zzaCr.getInt(str));
    }

    public Uri zzh(String str, int i, int i2) {
        String zzd = zzd(str, i, i2);
        return zzd == null ? null : Uri.parse(zzd);
    }

    public boolean zzi(String str, int i, int i2) {
        zzi(str, i);
        return this.zzaCs[i2].isNull(i, this.zzaCr.getInt(str));
    }

    public void zzwD() {
        int i;
        int i2 = 0;
        this.zzaCr = new Bundle();
        for (i = 0; i < this.zzaCq.length; i++) {
            this.zzaCr.putInt(this.zzaCq[i], i);
        }
        this.zzaCu = new int[this.zzaCs.length];
        i = 0;
        while (i2 < this.zzaCs.length) {
            this.zzaCu[i2] = i;
            i += this.zzaCs[i2].getNumRows() - (i - this.zzaCs[i2].getStartPosition());
            i2++;
        }
        this.zzaCv = i;
    }

    String[] zzwE() {
        return this.zzaCq;
    }

    CursorWindow[] zzwF() {
        return this.zzaCs;
    }

    public Bundle zzwy() {
        return this.zzaCt;
    }
}

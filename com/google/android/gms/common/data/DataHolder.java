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
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzc;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@KeepName
public final class DataHolder extends AbstractSafeParcelable implements Closeable {
    public static final Creator<DataHolder> CREATOR = new zze();
    private static final zza zV = new AnonymousClass1(new String[0], null);
    boolean mClosed;
    private final int mVersionCode;
    private final int rR;
    private final String[] zO;
    Bundle zP;
    private final CursorWindow[] zQ;
    private final Bundle zR;
    int[] zS;
    int zT;
    private boolean zU;

    public static class zza {
        private String Aa;
        private final String[] zO;
        private final ArrayList<HashMap<String, Object>> zW;
        private final String zX;
        private final HashMap<Object, Integer> zY;
        private boolean zZ;

        private zza(String[] strArr, String str) {
            this.zO = (String[]) zzac.zzy(strArr);
            this.zW = new ArrayList();
            this.zX = str;
            this.zY = new HashMap();
            this.zZ = false;
            this.Aa = null;
        }

        private int zzc(HashMap<String, Object> hashMap) {
            if (this.zX == null) {
                return -1;
            }
            Object obj = hashMap.get(this.zX);
            if (obj == null) {
                return -1;
            }
            Integer num = (Integer) this.zY.get(obj);
            if (num != null) {
                return num.intValue();
            }
            this.zY.put(obj, Integer.valueOf(this.zW.size()));
            return -1;
        }

        public zza zza(ContentValues contentValues) {
            zzc.zzu(contentValues);
            HashMap hashMap = new HashMap(contentValues.size());
            for (Entry entry : contentValues.valueSet()) {
                hashMap.put((String) entry.getKey(), entry.getValue());
            }
            return zzb(hashMap);
        }

        public zza zzb(HashMap<String, Object> hashMap) {
            zzc.zzu(hashMap);
            int zzc = zzc(hashMap);
            if (zzc == -1) {
                this.zW.add(hashMap);
            } else {
                this.zW.remove(zzc);
                this.zW.add(zzc, hashMap);
            }
            this.zZ = false;
            return this;
        }

        public DataHolder zzgd(int i) {
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
        this.zU = true;
        this.mVersionCode = i;
        this.zO = strArr;
        this.zQ = cursorWindowArr;
        this.rR = i2;
        this.zR = bundle;
    }

    private DataHolder(zza com_google_android_gms_common_data_DataHolder_zza, int i, Bundle bundle) {
        this(com_google_android_gms_common_data_DataHolder_zza.zO, zza(com_google_android_gms_common_data_DataHolder_zza, -1), i, bundle);
    }

    public DataHolder(String[] strArr, CursorWindow[] cursorWindowArr, int i, Bundle bundle) {
        this.mClosed = false;
        this.zU = true;
        this.mVersionCode = 1;
        this.zO = (String[]) zzac.zzy(strArr);
        this.zQ = (CursorWindow[]) zzac.zzy(cursorWindowArr);
        this.rR = i;
        this.zR = bundle;
        zzate();
    }

    public static DataHolder zza(int i, Bundle bundle) {
        return new DataHolder(zV, i, bundle);
    }

    private static CursorWindow[] zza(zza com_google_android_gms_common_data_DataHolder_zza, int i) {
        int i2 = 0;
        if (com_google_android_gms_common_data_DataHolder_zza.zO.length == 0) {
            return new CursorWindow[0];
        }
        List zzb = (i < 0 || i >= com_google_android_gms_common_data_DataHolder_zza.zW.size()) ? com_google_android_gms_common_data_DataHolder_zza.zW : com_google_android_gms_common_data_DataHolder_zza.zW.subList(0, i);
        int size = zzb.size();
        CursorWindow cursorWindow = new CursorWindow(false);
        ArrayList arrayList = new ArrayList();
        arrayList.add(cursorWindow);
        cursorWindow.setNumColumns(com_google_android_gms_common_data_DataHolder_zza.zO.length);
        int i3 = 0;
        int i4 = 0;
        while (i3 < size) {
            try {
                int i5;
                int i6;
                CursorWindow cursorWindow2;
                if (!cursorWindow.allocRow()) {
                    Log.d("DataHolder", "Allocating additional cursor window for large data set (row " + i3 + ")");
                    cursorWindow = new CursorWindow(false);
                    cursorWindow.setStartPosition(i3);
                    cursorWindow.setNumColumns(com_google_android_gms_common_data_DataHolder_zza.zO.length);
                    arrayList.add(cursorWindow);
                    if (!cursorWindow.allocRow()) {
                        Log.e("DataHolder", "Unable to allocate row to hold data.");
                        arrayList.remove(cursorWindow);
                        return (CursorWindow[]) arrayList.toArray(new CursorWindow[arrayList.size()]);
                    }
                }
                Map map = (Map) zzb.get(i3);
                boolean z = true;
                for (int i7 = 0; i7 < com_google_android_gms_common_data_DataHolder_zza.zO.length && z; i7++) {
                    String str = com_google_android_gms_common_data_DataHolder_zza.zO[i7];
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
                    cursorWindow3.setNumColumns(com_google_android_gms_common_data_DataHolder_zza.zO.length);
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
                int size2 = arrayList.size();
                while (i2 < size2) {
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

    public static DataHolder zzgc(int i) {
        return zza(i, null);
    }

    private void zzi(String str, int i) {
        if (this.zP == null || !this.zP.containsKey(str)) {
            String str2 = "No such column: ";
            String valueOf = String.valueOf(str);
            throw new IllegalArgumentException(valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
        } else if (isClosed()) {
            throw new IllegalArgumentException("Buffer is closed.");
        } else if (i < 0 || i >= this.zT) {
            throw new CursorIndexOutOfBoundsException(i, this.zT);
        }
    }

    public void close() {
        synchronized (this) {
            if (!this.mClosed) {
                this.mClosed = true;
                for (CursorWindow close : this.zQ) {
                    close.close();
                }
            }
        }
    }

    protected void finalize() throws Throwable {
        try {
            if (this.zU && this.zQ.length > 0 && !isClosed()) {
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
        return this.zT;
    }

    public int getStatusCode() {
        return this.rR;
    }

    int getVersionCode() {
        return this.mVersionCode;
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
        this.zQ[i2].copyStringToBuffer(i, this.zP.getInt(str), charArrayBuffer);
    }

    public Bundle zzasz() {
        return this.zR;
    }

    public void zzate() {
        int i;
        int i2 = 0;
        this.zP = new Bundle();
        for (i = 0; i < this.zO.length; i++) {
            this.zP.putInt(this.zO[i], i);
        }
        this.zS = new int[this.zQ.length];
        i = 0;
        while (i2 < this.zQ.length) {
            this.zS[i2] = i;
            i += this.zQ[i2].getNumRows() - (i - this.zQ[i2].getStartPosition());
            i2++;
        }
        this.zT = i;
    }

    String[] zzatf() {
        return this.zO;
    }

    CursorWindow[] zzatg() {
        return this.zQ;
    }

    public long zzb(String str, int i, int i2) {
        zzi(str, i);
        return this.zQ[i2].getLong(i, this.zP.getInt(str));
    }

    public int zzc(String str, int i, int i2) {
        zzi(str, i);
        return this.zQ[i2].getInt(i, this.zP.getInt(str));
    }

    public String zzd(String str, int i, int i2) {
        zzi(str, i);
        return this.zQ[i2].getString(i, this.zP.getInt(str));
    }

    public boolean zze(String str, int i, int i2) {
        zzi(str, i);
        return Long.valueOf(this.zQ[i2].getLong(i, this.zP.getInt(str))).longValue() == 1;
    }

    public float zzf(String str, int i, int i2) {
        zzi(str, i);
        return this.zQ[i2].getFloat(i, this.zP.getInt(str));
    }

    public byte[] zzg(String str, int i, int i2) {
        zzi(str, i);
        return this.zQ[i2].getBlob(i, this.zP.getInt(str));
    }

    public int zzgb(int i) {
        int i2 = 0;
        boolean z = i >= 0 && i < this.zT;
        zzac.zzbr(z);
        while (i2 < this.zS.length) {
            if (i < this.zS[i2]) {
                i2--;
                break;
            }
            i2++;
        }
        return i2 == this.zS.length ? i2 - 1 : i2;
    }

    public Uri zzh(String str, int i, int i2) {
        String zzd = zzd(str, i, i2);
        return zzd == null ? null : Uri.parse(zzd);
    }

    public boolean zzhm(String str) {
        return this.zP.containsKey(str);
    }

    public boolean zzi(String str, int i, int i2) {
        zzi(str, i);
        return this.zQ[i2].isNull(i, this.zP.getInt(str));
    }
}

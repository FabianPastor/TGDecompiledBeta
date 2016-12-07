package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zzc;
import com.google.android.gms.common.util.zzp;
import com.google.android.gms.common.util.zzq;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class zzack {

    public interface zzb<I, O> {
        I convertBack(O o);
    }

    public static class zza<I, O> extends com.google.android.gms.common.internal.safeparcel.zza {
        public static final zzacm CREATOR = new zzacm();
        private final int mVersionCode;
        protected final int zzaFA;
        protected final boolean zzaFB;
        protected final int zzaFC;
        protected final boolean zzaFD;
        protected final String zzaFE;
        protected final int zzaFF;
        protected final Class<? extends zzack> zzaFG;
        protected final String zzaFH;
        private zzaco zzaFI;
        private zzb<I, O> zzaFJ;

        zza(int i, int i2, boolean z, int i3, boolean z2, String str, int i4, String str2, zzacf com_google_android_gms_internal_zzacf) {
            this.mVersionCode = i;
            this.zzaFA = i2;
            this.zzaFB = z;
            this.zzaFC = i3;
            this.zzaFD = z2;
            this.zzaFE = str;
            this.zzaFF = i4;
            if (str2 == null) {
                this.zzaFG = null;
                this.zzaFH = null;
            } else {
                this.zzaFG = zzacr.class;
                this.zzaFH = str2;
            }
            if (com_google_android_gms_internal_zzacf == null) {
                this.zzaFJ = null;
            } else {
                this.zzaFJ = com_google_android_gms_internal_zzacf.zzxI();
            }
        }

        protected zza(int i, boolean z, int i2, boolean z2, String str, int i3, Class<? extends zzack> cls, zzb<I, O> com_google_android_gms_internal_zzack_zzb_I__O) {
            this.mVersionCode = 1;
            this.zzaFA = i;
            this.zzaFB = z;
            this.zzaFC = i2;
            this.zzaFD = z2;
            this.zzaFE = str;
            this.zzaFF = i3;
            this.zzaFG = cls;
            if (cls == null) {
                this.zzaFH = null;
            } else {
                this.zzaFH = cls.getCanonicalName();
            }
            this.zzaFJ = com_google_android_gms_internal_zzack_zzb_I__O;
        }

        public static zza zza(String str, int i, zzb<?, ?> com_google_android_gms_internal_zzack_zzb___, boolean z) {
            return new zza(7, z, 0, false, str, i, null, com_google_android_gms_internal_zzack_zzb___);
        }

        public static <T extends zzack> zza<T, T> zza(String str, int i, Class<T> cls) {
            return new zza(11, false, 11, false, str, i, cls, null);
        }

        public static <T extends zzack> zza<ArrayList<T>, ArrayList<T>> zzb(String str, int i, Class<T> cls) {
            return new zza(11, true, 11, true, str, i, cls, null);
        }

        public static zza<Integer, Integer> zzk(String str, int i) {
            return new zza(0, false, 0, false, str, i, null, null);
        }

        public static zza<Boolean, Boolean> zzl(String str, int i) {
            return new zza(6, false, 6, false, str, i, null, null);
        }

        public static zza<String, String> zzm(String str, int i) {
            return new zza(7, false, 7, false, str, i, null, null);
        }

        public I convertBack(O o) {
            return this.zzaFJ.convertBack(o);
        }

        public int getVersionCode() {
            return this.mVersionCode;
        }

        public String toString() {
            com.google.android.gms.common.internal.zzaa.zza zzg = zzaa.zzv(this).zzg("versionCode", Integer.valueOf(this.mVersionCode)).zzg("typeIn", Integer.valueOf(this.zzaFA)).zzg("typeInArray", Boolean.valueOf(this.zzaFB)).zzg("typeOut", Integer.valueOf(this.zzaFC)).zzg("typeOutArray", Boolean.valueOf(this.zzaFD)).zzg("outputFieldName", this.zzaFE).zzg("safeParcelFieldId", Integer.valueOf(this.zzaFF)).zzg("concreteTypeName", zzxS());
            Class zzxR = zzxR();
            if (zzxR != null) {
                zzg.zzg("concreteType.class", zzxR.getCanonicalName());
            }
            if (this.zzaFJ != null) {
                zzg.zzg("converterName", this.zzaFJ.getClass().getCanonicalName());
            }
            return zzg.toString();
        }

        public void writeToParcel(Parcel parcel, int i) {
            zzacm.zza(this, parcel, i);
        }

        public void zza(zzaco com_google_android_gms_internal_zzaco) {
            this.zzaFI = com_google_android_gms_internal_zzaco;
        }

        public int zzxL() {
            return this.zzaFA;
        }

        public boolean zzxM() {
            return this.zzaFB;
        }

        public int zzxN() {
            return this.zzaFC;
        }

        public boolean zzxO() {
            return this.zzaFD;
        }

        public String zzxP() {
            return this.zzaFE;
        }

        public int zzxQ() {
            return this.zzaFF;
        }

        public Class<? extends zzack> zzxR() {
            return this.zzaFG;
        }

        String zzxS() {
            return this.zzaFH == null ? null : this.zzaFH;
        }

        public boolean zzxT() {
            return this.zzaFJ != null;
        }

        zzacf zzxU() {
            return this.zzaFJ == null ? null : zzacf.zza(this.zzaFJ);
        }

        public Map<String, zza<?, ?>> zzxV() {
            zzac.zzw(this.zzaFH);
            zzac.zzw(this.zzaFI);
            return this.zzaFI.zzdA(this.zzaFH);
        }
    }

    private void zza(StringBuilder stringBuilder, zza com_google_android_gms_internal_zzack_zza, Object obj) {
        if (com_google_android_gms_internal_zzack_zza.zzxL() == 11) {
            stringBuilder.append(((zzack) com_google_android_gms_internal_zzack_zza.zzxR().cast(obj)).toString());
        } else if (com_google_android_gms_internal_zzack_zza.zzxL() == 7) {
            stringBuilder.append("\"");
            stringBuilder.append(zzp.zzdC((String) obj));
            stringBuilder.append("\"");
        } else {
            stringBuilder.append(obj);
        }
    }

    private void zza(StringBuilder stringBuilder, zza com_google_android_gms_internal_zzack_zza, ArrayList<Object> arrayList) {
        stringBuilder.append("[");
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                stringBuilder.append(",");
            }
            Object obj = arrayList.get(i);
            if (obj != null) {
                zza(stringBuilder, com_google_android_gms_internal_zzack_zza, obj);
            }
        }
        stringBuilder.append("]");
    }

    public String toString() {
        Map zzxK = zzxK();
        StringBuilder stringBuilder = new StringBuilder(100);
        for (String str : zzxK.keySet()) {
            zza com_google_android_gms_internal_zzack_zza = (zza) zzxK.get(str);
            if (zza(com_google_android_gms_internal_zzack_zza)) {
                Object zza = zza(com_google_android_gms_internal_zzack_zza, zzb(com_google_android_gms_internal_zzack_zza));
                if (stringBuilder.length() == 0) {
                    stringBuilder.append("{");
                } else {
                    stringBuilder.append(",");
                }
                stringBuilder.append("\"").append(str).append("\":");
                if (zza != null) {
                    switch (com_google_android_gms_internal_zzack_zza.zzxN()) {
                        case 8:
                            stringBuilder.append("\"").append(zzc.zzq((byte[]) zza)).append("\"");
                            break;
                        case 9:
                            stringBuilder.append("\"").append(zzc.zzr((byte[]) zza)).append("\"");
                            break;
                        case 10:
                            zzq.zza(stringBuilder, (HashMap) zza);
                            break;
                        default:
                            if (!com_google_android_gms_internal_zzack_zza.zzxM()) {
                                zza(stringBuilder, com_google_android_gms_internal_zzack_zza, zza);
                                break;
                            }
                            zza(stringBuilder, com_google_android_gms_internal_zzack_zza, (ArrayList) zza);
                            break;
                    }
                }
                stringBuilder.append("null");
            }
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.append("}");
        } else {
            stringBuilder.append("{}");
        }
        return stringBuilder.toString();
    }

    protected <O, I> I zza(zza<I, O> com_google_android_gms_internal_zzack_zza_I__O, Object obj) {
        return com_google_android_gms_internal_zzack_zza_I__O.zzaFJ != null ? com_google_android_gms_internal_zzack_zza_I__O.convertBack(obj) : obj;
    }

    protected boolean zza(zza com_google_android_gms_internal_zzack_zza) {
        return com_google_android_gms_internal_zzack_zza.zzxN() == 11 ? com_google_android_gms_internal_zzack_zza.zzxO() ? zzdz(com_google_android_gms_internal_zzack_zza.zzxP()) : zzdy(com_google_android_gms_internal_zzack_zza.zzxP()) : zzdx(com_google_android_gms_internal_zzack_zza.zzxP());
    }

    protected Object zzb(zza com_google_android_gms_internal_zzack_zza) {
        String zzxP = com_google_android_gms_internal_zzack_zza.zzxP();
        if (com_google_android_gms_internal_zzack_zza.zzxR() == null) {
            return zzdw(com_google_android_gms_internal_zzack_zza.zzxP());
        }
        zzdw(com_google_android_gms_internal_zzack_zza.zzxP());
        zzac.zza(true, "Concrete field shouldn't be value object: %s", com_google_android_gms_internal_zzack_zza.zzxP());
        com_google_android_gms_internal_zzack_zza.zzxO();
        try {
            char toUpperCase = Character.toUpperCase(zzxP.charAt(0));
            zzxP = String.valueOf(zzxP.substring(1));
            return getClass().getMethod(new StringBuilder(String.valueOf(zzxP).length() + 4).append("get").append(toUpperCase).append(zzxP).toString(), new Class[0]).invoke(this, new Object[0]);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract Object zzdw(String str);

    protected abstract boolean zzdx(String str);

    protected boolean zzdy(String str) {
        throw new UnsupportedOperationException("Concrete types not supported");
    }

    protected boolean zzdz(String str) {
        throw new UnsupportedOperationException("Concrete type arrays not supported");
    }

    public abstract Map<String, zza<?, ?>> zzxK();
}

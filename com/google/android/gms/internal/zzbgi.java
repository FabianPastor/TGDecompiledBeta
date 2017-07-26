package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zzc;
import com.google.android.gms.common.util.zzo;
import com.google.android.gms.common.util.zzp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class zzbgi {
    protected static <O, I> I zza(zzbgj<I, O> com_google_android_gms_internal_zzbgj_I__O, Object obj) {
        return com_google_android_gms_internal_zzbgj_I__O.zzaIQ != null ? com_google_android_gms_internal_zzbgj_I__O.convertBack(obj) : obj;
    }

    private static void zza(StringBuilder stringBuilder, zzbgj com_google_android_gms_internal_zzbgj, Object obj) {
        if (com_google_android_gms_internal_zzbgj.zzaIH == 11) {
            stringBuilder.append(((zzbgi) com_google_android_gms_internal_zzbgj.zzaIN.cast(obj)).toString());
        } else if (com_google_android_gms_internal_zzbgj.zzaIH == 7) {
            stringBuilder.append("\"");
            stringBuilder.append(zzo.zzcK((String) obj));
            stringBuilder.append("\"");
        } else {
            stringBuilder.append(obj);
        }
    }

    private static void zza(StringBuilder stringBuilder, zzbgj com_google_android_gms_internal_zzbgj, ArrayList<Object> arrayList) {
        stringBuilder.append("[");
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                stringBuilder.append(",");
            }
            Object obj = arrayList.get(i);
            if (obj != null) {
                zza(stringBuilder, com_google_android_gms_internal_zzbgj, obj);
            }
        }
        stringBuilder.append("]");
    }

    public String toString() {
        Map zzrL = zzrL();
        StringBuilder stringBuilder = new StringBuilder(100);
        for (String str : zzrL.keySet()) {
            zzbgj com_google_android_gms_internal_zzbgj = (zzbgj) zzrL.get(str);
            if (zza(com_google_android_gms_internal_zzbgj)) {
                Object zza = zza(com_google_android_gms_internal_zzbgj, zzb(com_google_android_gms_internal_zzbgj));
                if (stringBuilder.length() == 0) {
                    stringBuilder.append("{");
                } else {
                    stringBuilder.append(",");
                }
                stringBuilder.append("\"").append(str).append("\":");
                if (zza != null) {
                    switch (com_google_android_gms_internal_zzbgj.zzaIJ) {
                        case 8:
                            stringBuilder.append("\"").append(zzc.encode((byte[]) zza)).append("\"");
                            break;
                        case 9:
                            stringBuilder.append("\"").append(zzc.zzg((byte[]) zza)).append("\"");
                            break;
                        case 10:
                            zzp.zza(stringBuilder, (HashMap) zza);
                            break;
                        default:
                            if (!com_google_android_gms_internal_zzbgj.zzaII) {
                                zza(stringBuilder, com_google_android_gms_internal_zzbgj, zza);
                                break;
                            }
                            zza(stringBuilder, com_google_android_gms_internal_zzbgj, (ArrayList) zza);
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

    protected boolean zza(zzbgj com_google_android_gms_internal_zzbgj) {
        if (com_google_android_gms_internal_zzbgj.zzaIJ != 11) {
            return zzcI(com_google_android_gms_internal_zzbgj.zzaIL);
        }
        if (com_google_android_gms_internal_zzbgj.zzaIK) {
            String str = com_google_android_gms_internal_zzbgj.zzaIL;
            throw new UnsupportedOperationException("Concrete type arrays not supported");
        }
        str = com_google_android_gms_internal_zzbgj.zzaIL;
        throw new UnsupportedOperationException("Concrete types not supported");
    }

    protected Object zzb(zzbgj com_google_android_gms_internal_zzbgj) {
        String str = com_google_android_gms_internal_zzbgj.zzaIL;
        if (com_google_android_gms_internal_zzbgj.zzaIN == null) {
            return zzcH(com_google_android_gms_internal_zzbgj.zzaIL);
        }
        zzcH(com_google_android_gms_internal_zzbgj.zzaIL);
        zzbo.zza(true, "Concrete field shouldn't be value object: %s", com_google_android_gms_internal_zzbgj.zzaIL);
        boolean z = com_google_android_gms_internal_zzbgj.zzaIK;
        try {
            char toUpperCase = Character.toUpperCase(str.charAt(0));
            str = String.valueOf(str.substring(1));
            return getClass().getMethod(new StringBuilder(String.valueOf(str).length() + 4).append("get").append(toUpperCase).append(str).toString(), new Class[0]).invoke(this, new Object[0]);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract Object zzcH(String str);

    protected abstract boolean zzcI(String str);

    public abstract Map<String, zzbgj<?, ?>> zzrL();
}

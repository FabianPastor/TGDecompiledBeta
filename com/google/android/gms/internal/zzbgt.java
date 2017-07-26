package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.internal.view.SupportMenu;
import android.util.SparseArray;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zzc;
import com.google.android.gms.common.util.zzo;
import com.google.android.gms.common.util.zzp;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class zzbgt extends zzbgl {
    public static final Creator<zzbgt> CREATOR = new zzbgu();
    private final String mClassName;
    private final zzbgo zzaIP;
    private final Parcel zzaIW;
    private final int zzaIX = 2;
    private int zzaIY;
    private int zzaIZ;
    private final int zzaku;

    zzbgt(int i, Parcel parcel, zzbgo com_google_android_gms_internal_zzbgo) {
        this.zzaku = i;
        this.zzaIW = (Parcel) zzbo.zzu(parcel);
        this.zzaIP = com_google_android_gms_internal_zzbgo;
        if (this.zzaIP == null) {
            this.mClassName = null;
        } else {
            this.mClassName = this.zzaIP.zzrR();
        }
        this.zzaIY = 2;
    }

    private static void zza(StringBuilder stringBuilder, int i, Object obj) {
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                stringBuilder.append(obj);
                return;
            case 7:
                stringBuilder.append("\"").append(zzo.zzcK(obj.toString())).append("\"");
                return;
            case 8:
                stringBuilder.append("\"").append(zzc.encode((byte[]) obj)).append("\"");
                return;
            case 9:
                stringBuilder.append("\"").append(zzc.zzg((byte[]) obj));
                stringBuilder.append("\"");
                return;
            case 10:
                zzp.zza(stringBuilder, (HashMap) obj);
                return;
            case 11:
                throw new IllegalArgumentException("Method does not accept concrete type.");
            default:
                throw new IllegalArgumentException("Unknown type = " + i);
        }
    }

    private final void zza(StringBuilder stringBuilder, zzbgj<?, ?> com_google_android_gms_internal_zzbgj___, Parcel parcel, int i) {
        double[] dArr = null;
        int i2 = 0;
        int length;
        if (com_google_android_gms_internal_zzbgj___.zzaIK) {
            stringBuilder.append("[");
            int dataPosition;
            switch (com_google_android_gms_internal_zzbgj___.zzaIJ) {
                case 0:
                    int[] zzw = zzb.zzw(parcel, i);
                    length = zzw.length;
                    while (i2 < length) {
                        if (i2 != 0) {
                            stringBuilder.append(",");
                        }
                        stringBuilder.append(Integer.toString(zzw[i2]));
                        i2++;
                    }
                    break;
                case 1:
                    Object[] objArr;
                    length = zzb.zza(parcel, i);
                    dataPosition = parcel.dataPosition();
                    if (length != 0) {
                        int readInt = parcel.readInt();
                        objArr = new BigInteger[readInt];
                        while (i2 < readInt) {
                            objArr[i2] = new BigInteger(parcel.createByteArray());
                            i2++;
                        }
                        parcel.setDataPosition(length + dataPosition);
                    }
                    com.google.android.gms.common.util.zzb.zza(stringBuilder, objArr);
                    break;
                case 2:
                    com.google.android.gms.common.util.zzb.zza(stringBuilder, zzb.zzx(parcel, i));
                    break;
                case 3:
                    com.google.android.gms.common.util.zzb.zza(stringBuilder, zzb.zzy(parcel, i));
                    break;
                case 4:
                    length = zzb.zza(parcel, i);
                    i2 = parcel.dataPosition();
                    if (length != 0) {
                        dArr = parcel.createDoubleArray();
                        parcel.setDataPosition(length + i2);
                    }
                    com.google.android.gms.common.util.zzb.zza(stringBuilder, dArr);
                    break;
                case 5:
                    com.google.android.gms.common.util.zzb.zza(stringBuilder, zzb.zzz(parcel, i));
                    break;
                case 6:
                    com.google.android.gms.common.util.zzb.zza(stringBuilder, zzb.zzv(parcel, i));
                    break;
                case 7:
                    com.google.android.gms.common.util.zzb.zza(stringBuilder, zzb.zzA(parcel, i));
                    break;
                case 8:
                case 9:
                case 10:
                    throw new UnsupportedOperationException("List of type BASE64, BASE64_URL_SAFE, or STRING_MAP is not supported");
                case 11:
                    Parcel[] zzE = zzb.zzE(parcel, i);
                    dataPosition = zzE.length;
                    for (int i3 = 0; i3 < dataPosition; i3++) {
                        if (i3 > 0) {
                            stringBuilder.append(",");
                        }
                        zzE[i3].setDataPosition(0);
                        zza(stringBuilder, com_google_android_gms_internal_zzbgj___.zzrP(), zzE[i3]);
                    }
                    break;
                default:
                    throw new IllegalStateException("Unknown field type out.");
            }
            stringBuilder.append("]");
            return;
        }
        switch (com_google_android_gms_internal_zzbgj___.zzaIJ) {
            case 0:
                stringBuilder.append(zzb.zzg(parcel, i));
                return;
            case 1:
                stringBuilder.append(zzb.zzk(parcel, i));
                return;
            case 2:
                stringBuilder.append(zzb.zzi(parcel, i));
                return;
            case 3:
                stringBuilder.append(zzb.zzl(parcel, i));
                return;
            case 4:
                stringBuilder.append(zzb.zzn(parcel, i));
                return;
            case 5:
                stringBuilder.append(zzb.zzp(parcel, i));
                return;
            case 6:
                stringBuilder.append(zzb.zzc(parcel, i));
                return;
            case 7:
                stringBuilder.append("\"").append(zzo.zzcK(zzb.zzq(parcel, i))).append("\"");
                return;
            case 8:
                stringBuilder.append("\"").append(zzc.encode(zzb.zzt(parcel, i))).append("\"");
                return;
            case 9:
                stringBuilder.append("\"").append(zzc.zzg(zzb.zzt(parcel, i)));
                stringBuilder.append("\"");
                return;
            case 10:
                Bundle zzs = zzb.zzs(parcel, i);
                Set<String> keySet = zzs.keySet();
                keySet.size();
                stringBuilder.append("{");
                length = 1;
                for (String str : keySet) {
                    if (length == 0) {
                        stringBuilder.append(",");
                    }
                    stringBuilder.append("\"").append(str).append("\"");
                    stringBuilder.append(":");
                    stringBuilder.append("\"").append(zzo.zzcK(zzs.getString(str))).append("\"");
                    length = 0;
                }
                stringBuilder.append("}");
                return;
            case 11:
                Parcel zzD = zzb.zzD(parcel, i);
                zzD.setDataPosition(0);
                zza(stringBuilder, com_google_android_gms_internal_zzbgj___.zzrP(), zzD);
                return;
            default:
                throw new IllegalStateException("Unknown field type out");
        }
    }

    private final void zza(StringBuilder stringBuilder, Map<String, zzbgj<?, ?>> map, Parcel parcel) {
        Entry entry;
        SparseArray sparseArray = new SparseArray();
        for (Entry entry2 : map.entrySet()) {
            sparseArray.put(((zzbgj) entry2.getValue()).zzaIM, entry2);
        }
        stringBuilder.append('{');
        int zzd = zzb.zzd(parcel);
        Object obj = null;
        while (parcel.dataPosition() < zzd) {
            int readInt = parcel.readInt();
            entry2 = (Entry) sparseArray.get(SupportMenu.USER_MASK & readInt);
            if (entry2 != null) {
                if (obj != null) {
                    stringBuilder.append(",");
                }
                String str = (String) entry2.getKey();
                zzbgj com_google_android_gms_internal_zzbgj = (zzbgj) entry2.getValue();
                stringBuilder.append("\"").append(str).append("\":");
                if (com_google_android_gms_internal_zzbgj.zzrO()) {
                    switch (com_google_android_gms_internal_zzbgj.zzaIJ) {
                        case 0:
                            zzb(stringBuilder, com_google_android_gms_internal_zzbgj, zzbgi.zza(com_google_android_gms_internal_zzbgj, Integer.valueOf(zzb.zzg(parcel, readInt))));
                            break;
                        case 1:
                            zzb(stringBuilder, com_google_android_gms_internal_zzbgj, zzbgi.zza(com_google_android_gms_internal_zzbgj, zzb.zzk(parcel, readInt)));
                            break;
                        case 2:
                            zzb(stringBuilder, com_google_android_gms_internal_zzbgj, zzbgi.zza(com_google_android_gms_internal_zzbgj, Long.valueOf(zzb.zzi(parcel, readInt))));
                            break;
                        case 3:
                            zzb(stringBuilder, com_google_android_gms_internal_zzbgj, zzbgi.zza(com_google_android_gms_internal_zzbgj, Float.valueOf(zzb.zzl(parcel, readInt))));
                            break;
                        case 4:
                            zzb(stringBuilder, com_google_android_gms_internal_zzbgj, zzbgi.zza(com_google_android_gms_internal_zzbgj, Double.valueOf(zzb.zzn(parcel, readInt))));
                            break;
                        case 5:
                            zzb(stringBuilder, com_google_android_gms_internal_zzbgj, zzbgi.zza(com_google_android_gms_internal_zzbgj, zzb.zzp(parcel, readInt)));
                            break;
                        case 6:
                            zzb(stringBuilder, com_google_android_gms_internal_zzbgj, zzbgi.zza(com_google_android_gms_internal_zzbgj, Boolean.valueOf(zzb.zzc(parcel, readInt))));
                            break;
                        case 7:
                            zzb(stringBuilder, com_google_android_gms_internal_zzbgj, zzbgi.zza(com_google_android_gms_internal_zzbgj, zzb.zzq(parcel, readInt)));
                            break;
                        case 8:
                        case 9:
                            zzb(stringBuilder, com_google_android_gms_internal_zzbgj, zzbgi.zza(com_google_android_gms_internal_zzbgj, zzb.zzt(parcel, readInt)));
                            break;
                        case 10:
                            zzb(stringBuilder, com_google_android_gms_internal_zzbgj, zzbgi.zza(com_google_android_gms_internal_zzbgj, zzo(zzb.zzs(parcel, readInt))));
                            break;
                        case 11:
                            throw new IllegalArgumentException("Method does not accept concrete type.");
                        default:
                            throw new IllegalArgumentException("Unknown field out type = " + com_google_android_gms_internal_zzbgj.zzaIJ);
                    }
                }
                zza(stringBuilder, com_google_android_gms_internal_zzbgj, parcel, readInt);
                obj = 1;
            }
        }
        if (parcel.dataPosition() != zzd) {
            throw new com.google.android.gms.common.internal.safeparcel.zzc("Overread allowed size end=" + zzd, parcel);
        }
        stringBuilder.append('}');
    }

    private final void zzb(StringBuilder stringBuilder, zzbgj<?, ?> com_google_android_gms_internal_zzbgj___, Object obj) {
        if (com_google_android_gms_internal_zzbgj___.zzaII) {
            ArrayList arrayList = (ArrayList) obj;
            stringBuilder.append("[");
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                if (i != 0) {
                    stringBuilder.append(",");
                }
                zza(stringBuilder, com_google_android_gms_internal_zzbgj___.zzaIH, arrayList.get(i));
            }
            stringBuilder.append("]");
            return;
        }
        zza(stringBuilder, com_google_android_gms_internal_zzbgj___.zzaIH, obj);
    }

    private static HashMap<String, String> zzo(Bundle bundle) {
        HashMap<String, String> hashMap = new HashMap();
        for (String str : bundle.keySet()) {
            hashMap.put(str, bundle.getString(str));
        }
        return hashMap;
    }

    private Parcel zzrT() {
        switch (this.zzaIY) {
            case 0:
                this.zzaIZ = zzd.zze(this.zzaIW);
                break;
            case 1:
                break;
        }
        zzd.zzI(this.zzaIW, this.zzaIZ);
        this.zzaIY = 2;
        return this.zzaIW;
    }

    public String toString() {
        zzbo.zzb(this.zzaIP, (Object) "Cannot convert to JSON on client side.");
        Parcel zzrT = zzrT();
        zzrT.setDataPosition(0);
        StringBuilder stringBuilder = new StringBuilder(100);
        zza(stringBuilder, this.zzaIP.zzcJ(this.mClassName), zzrT);
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        Parcelable parcelable;
        int zze = zzd.zze(parcel);
        zzd.zzc(parcel, 1, this.zzaku);
        zzd.zza(parcel, 2, zzrT(), false);
        switch (this.zzaIX) {
            case 0:
                parcelable = null;
                break;
            case 1:
                parcelable = this.zzaIP;
                break;
            case 2:
                parcelable = this.zzaIP;
                break;
            default:
                throw new IllegalStateException("Invalid creation type: " + this.zzaIX);
        }
        zzd.zza(parcel, 3, parcelable, i, false);
        zzd.zzI(parcel, zze);
    }

    public final Object zzcH(String str) {
        throw new UnsupportedOperationException("Converting to JSON does not require this method.");
    }

    public final boolean zzcI(String str) {
        throw new UnsupportedOperationException("Converting to JSON does not require this method.");
    }

    public final Map<String, zzbgj<?, ?>> zzrL() {
        return this.zzaIP == null ? null : this.zzaIP.zzcJ(this.mClassName);
    }
}

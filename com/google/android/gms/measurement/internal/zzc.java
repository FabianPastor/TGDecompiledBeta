package com.google.android.gms.measurement.internal;

import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.internal.zzwa.zza;
import com.google.android.gms.internal.zzwa.zzb;
import com.google.android.gms.internal.zzwa.zzd;
import com.google.android.gms.internal.zzwa.zze;
import com.google.android.gms.internal.zzwa.zzf;
import com.google.android.gms.internal.zzwc;
import com.google.android.gms.internal.zzwc.zzg;
import com.google.android.gms.measurement.AppMeasurement;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

class zzc extends zzaa {
    zzc(zzx com_google_android_gms_measurement_internal_zzx) {
        super(com_google_android_gms_measurement_internal_zzx);
    }

    private Boolean zza(zzb com_google_android_gms_internal_zzwa_zzb, zzwc.zzb com_google_android_gms_internal_zzwc_zzb, long j) {
        Boolean zza;
        if (com_google_android_gms_internal_zzwa_zzb.awh != null) {
            zza = zza(j, com_google_android_gms_internal_zzwa_zzb.awh);
            if (zza == null) {
                return null;
            }
            if (!zza.booleanValue()) {
                return Boolean.valueOf(false);
            }
        }
        Set hashSet = new HashSet();
        for (com.google.android.gms.internal.zzwa.zzc com_google_android_gms_internal_zzwa_zzc : com_google_android_gms_internal_zzwa_zzb.awf) {
            if (TextUtils.isEmpty(com_google_android_gms_internal_zzwa_zzc.awm)) {
                zzbwb().zzbxa().zzj("null or empty param name in filter. event", com_google_android_gms_internal_zzwc_zzb.name);
                return null;
            }
            hashSet.add(com_google_android_gms_internal_zzwa_zzc.awm);
        }
        Map arrayMap = new ArrayMap();
        for (com.google.android.gms.internal.zzwc.zzc com_google_android_gms_internal_zzwc_zzc : com_google_android_gms_internal_zzwc_zzb.awN) {
            if (hashSet.contains(com_google_android_gms_internal_zzwc_zzc.name)) {
                if (com_google_android_gms_internal_zzwc_zzc.awR != null) {
                    arrayMap.put(com_google_android_gms_internal_zzwc_zzc.name, com_google_android_gms_internal_zzwc_zzc.awR);
                } else if (com_google_android_gms_internal_zzwc_zzc.avW != null) {
                    arrayMap.put(com_google_android_gms_internal_zzwc_zzc.name, com_google_android_gms_internal_zzwc_zzc.avW);
                } else if (com_google_android_gms_internal_zzwc_zzc.Fe != null) {
                    arrayMap.put(com_google_android_gms_internal_zzwc_zzc.name, com_google_android_gms_internal_zzwc_zzc.Fe);
                } else {
                    zzbwb().zzbxa().zze("Unknown value for param. event, param", com_google_android_gms_internal_zzwc_zzb.name, com_google_android_gms_internal_zzwc_zzc.name);
                    return null;
                }
            }
        }
        for (com.google.android.gms.internal.zzwa.zzc com_google_android_gms_internal_zzwa_zzc2 : com_google_android_gms_internal_zzwa_zzb.awf) {
            boolean equals = Boolean.TRUE.equals(com_google_android_gms_internal_zzwa_zzc2.awl);
            CharSequence charSequence = com_google_android_gms_internal_zzwa_zzc2.awm;
            if (TextUtils.isEmpty(charSequence)) {
                zzbwb().zzbxa().zzj("Event has empty param name. event", com_google_android_gms_internal_zzwc_zzb.name);
                return null;
            }
            Object obj = arrayMap.get(charSequence);
            if (obj instanceof Long) {
                if (com_google_android_gms_internal_zzwa_zzc2.awk == null) {
                    zzbwb().zzbxa().zze("No number filter for long param. event, param", com_google_android_gms_internal_zzwc_zzb.name, charSequence);
                    return null;
                }
                zza = zza(((Long) obj).longValue(), com_google_android_gms_internal_zzwa_zzc2.awk);
                if (zza == null) {
                    return null;
                }
                if (((!zza.booleanValue() ? 1 : 0) ^ equals) != 0) {
                    return Boolean.valueOf(false);
                }
            } else if (obj instanceof Double) {
                if (com_google_android_gms_internal_zzwa_zzc2.awk == null) {
                    zzbwb().zzbxa().zze("No number filter for double param. event, param", com_google_android_gms_internal_zzwc_zzb.name, charSequence);
                    return null;
                }
                zza = zza(((Double) obj).doubleValue(), com_google_android_gms_internal_zzwa_zzc2.awk);
                if (zza == null) {
                    return null;
                }
                if (((!zza.booleanValue() ? 1 : 0) ^ equals) != 0) {
                    return Boolean.valueOf(false);
                }
            } else if (obj instanceof String) {
                if (com_google_android_gms_internal_zzwa_zzc2.awj == null) {
                    zzbwb().zzbxa().zze("No string filter for String param. event, param", com_google_android_gms_internal_zzwc_zzb.name, charSequence);
                    return null;
                }
                zza = zza((String) obj, com_google_android_gms_internal_zzwa_zzc2.awj);
                if (zza == null) {
                    return null;
                }
                if (((!zza.booleanValue() ? 1 : 0) ^ equals) != 0) {
                    return Boolean.valueOf(false);
                }
            } else if (obj == null) {
                zzbwb().zzbxe().zze("Missing param for filter. event, param", com_google_android_gms_internal_zzwc_zzb.name, charSequence);
                return Boolean.valueOf(false);
            } else {
                zzbwb().zzbxa().zze("Unknown param type. event, param", com_google_android_gms_internal_zzwc_zzb.name, charSequence);
                return null;
            }
        }
        return Boolean.valueOf(true);
    }

    private Boolean zza(zze com_google_android_gms_internal_zzwa_zze, zzg com_google_android_gms_internal_zzwc_zzg) {
        com.google.android.gms.internal.zzwa.zzc com_google_android_gms_internal_zzwa_zzc = com_google_android_gms_internal_zzwa_zze.awu;
        if (com_google_android_gms_internal_zzwa_zzc == null) {
            zzbwb().zzbxa().zzj("Missing property filter. property", com_google_android_gms_internal_zzwc_zzg.name);
            return null;
        }
        boolean equals = Boolean.TRUE.equals(com_google_android_gms_internal_zzwa_zzc.awl);
        if (com_google_android_gms_internal_zzwc_zzg.awR != null) {
            if (com_google_android_gms_internal_zzwa_zzc.awk != null) {
                return zza(zza(com_google_android_gms_internal_zzwc_zzg.awR.longValue(), com_google_android_gms_internal_zzwa_zzc.awk), equals);
            }
            zzbwb().zzbxa().zzj("No number filter for long property. property", com_google_android_gms_internal_zzwc_zzg.name);
            return null;
        } else if (com_google_android_gms_internal_zzwc_zzg.avW != null) {
            if (com_google_android_gms_internal_zzwa_zzc.awk != null) {
                return zza(zza(com_google_android_gms_internal_zzwc_zzg.avW.doubleValue(), com_google_android_gms_internal_zzwa_zzc.awk), equals);
            }
            zzbwb().zzbxa().zzj("No number filter for double property. property", com_google_android_gms_internal_zzwc_zzg.name);
            return null;
        } else if (com_google_android_gms_internal_zzwc_zzg.Fe == null) {
            zzbwb().zzbxa().zzj("User property has no value, property", com_google_android_gms_internal_zzwc_zzg.name);
            return null;
        } else if (com_google_android_gms_internal_zzwa_zzc.awj != null) {
            return zza(zza(com_google_android_gms_internal_zzwc_zzg.Fe, com_google_android_gms_internal_zzwa_zzc.awj), equals);
        } else {
            if (com_google_android_gms_internal_zzwa_zzc.awk == null) {
                zzbwb().zzbxa().zzj("No string or number filter defined. property", com_google_android_gms_internal_zzwc_zzg.name);
                return null;
            } else if (zzal.zzng(com_google_android_gms_internal_zzwc_zzg.Fe)) {
                return zza(zza(com_google_android_gms_internal_zzwc_zzg.Fe, com_google_android_gms_internal_zzwa_zzc.awk), equals);
            } else {
                zzbwb().zzbxa().zze("Invalid user property value for Numeric number filter. property, value", com_google_android_gms_internal_zzwc_zzg.name, com_google_android_gms_internal_zzwc_zzg.Fe);
                return null;
            }
        }
    }

    static Boolean zza(Boolean bool, boolean z) {
        return bool == null ? null : Boolean.valueOf(bool.booleanValue() ^ z);
    }

    private Boolean zza(String str, int i, boolean z, String str2, List<String> list, String str3) {
        if (str == null) {
            return null;
        }
        if (i == 6) {
            if (list == null || list.size() == 0) {
                return null;
            }
        } else if (str2 == null) {
            return null;
        }
        if (!(z || i == 1)) {
            str = str.toUpperCase(Locale.ENGLISH);
        }
        switch (i) {
            case 1:
                return Boolean.valueOf(Pattern.compile(str3, z ? 0 : 66).matcher(str).matches());
            case 2:
                return Boolean.valueOf(str.startsWith(str2));
            case 3:
                return Boolean.valueOf(str.endsWith(str2));
            case 4:
                return Boolean.valueOf(str.contains(str2));
            case 5:
                return Boolean.valueOf(str.equals(str2));
            case 6:
                return Boolean.valueOf(list.contains(str));
            default:
                return null;
        }
    }

    private Boolean zza(BigDecimal bigDecimal, int i, BigDecimal bigDecimal2, BigDecimal bigDecimal3, BigDecimal bigDecimal4, double d) {
        boolean z = true;
        if (bigDecimal == null) {
            return null;
        }
        if (i == 4) {
            if (bigDecimal3 == null || bigDecimal4 == null) {
                return null;
            }
        } else if (bigDecimal2 == null) {
            return null;
        }
        switch (i) {
            case 1:
                if (bigDecimal.compareTo(bigDecimal2) != -1) {
                    z = false;
                }
                return Boolean.valueOf(z);
            case 2:
                if (bigDecimal.compareTo(bigDecimal2) != 1) {
                    z = false;
                }
                return Boolean.valueOf(z);
            case 3:
                if (d != 0.0d) {
                    if (!(bigDecimal.compareTo(bigDecimal2.subtract(new BigDecimal(d).multiply(new BigDecimal(2)))) == 1 && bigDecimal.compareTo(bigDecimal2.add(new BigDecimal(d).multiply(new BigDecimal(2)))) == -1)) {
                        z = false;
                    }
                    return Boolean.valueOf(z);
                }
                if (bigDecimal.compareTo(bigDecimal2) != 0) {
                    z = false;
                }
                return Boolean.valueOf(z);
            case 4:
                if (bigDecimal.compareTo(bigDecimal3) == -1 || bigDecimal.compareTo(bigDecimal4) == 1) {
                    z = false;
                }
                return Boolean.valueOf(z);
            default:
                return null;
        }
    }

    private List<String> zza(String[] strArr, boolean z) {
        if (z) {
            return Arrays.asList(strArr);
        }
        List<String> arrayList = new ArrayList();
        for (String toUpperCase : strArr) {
            arrayList.add(toUpperCase.toUpperCase(Locale.ENGLISH));
        }
        return arrayList;
    }

    public Boolean zza(double d, zzd com_google_android_gms_internal_zzwa_zzd) {
        try {
            return zza(new BigDecimal(d), com_google_android_gms_internal_zzwa_zzd, Math.ulp(d));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Boolean zza(long j, zzd com_google_android_gms_internal_zzwa_zzd) {
        try {
            return zza(new BigDecimal(j), com_google_android_gms_internal_zzwa_zzd, 0.0d);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Boolean zza(String str, zzd com_google_android_gms_internal_zzwa_zzd) {
        Boolean bool = null;
        if (zzal.zzng(str)) {
            try {
                bool = zza(new BigDecimal(str), com_google_android_gms_internal_zzwa_zzd, 0.0d);
            } catch (NumberFormatException e) {
            }
        }
        return bool;
    }

    Boolean zza(String str, zzf com_google_android_gms_internal_zzwa_zzf) {
        String str2 = null;
        zzaa.zzy(com_google_android_gms_internal_zzwa_zzf);
        if (str == null || com_google_android_gms_internal_zzwa_zzf.awv == null || com_google_android_gms_internal_zzwa_zzf.awv.intValue() == 0) {
            return null;
        }
        if (com_google_android_gms_internal_zzwa_zzf.awv.intValue() == 6) {
            if (com_google_android_gms_internal_zzwa_zzf.awy == null || com_google_android_gms_internal_zzwa_zzf.awy.length == 0) {
                return null;
            }
        } else if (com_google_android_gms_internal_zzwa_zzf.aww == null) {
            return null;
        }
        int intValue = com_google_android_gms_internal_zzwa_zzf.awv.intValue();
        boolean z = com_google_android_gms_internal_zzwa_zzf.awx != null && com_google_android_gms_internal_zzwa_zzf.awx.booleanValue();
        String toUpperCase = (z || intValue == 1 || intValue == 6) ? com_google_android_gms_internal_zzwa_zzf.aww : com_google_android_gms_internal_zzwa_zzf.aww.toUpperCase(Locale.ENGLISH);
        List zza = com_google_android_gms_internal_zzwa_zzf.awy == null ? null : zza(com_google_android_gms_internal_zzwa_zzf.awy, z);
        if (intValue == 1) {
            str2 = toUpperCase;
        }
        return zza(str, intValue, z, toUpperCase, zza, str2);
    }

    Boolean zza(BigDecimal bigDecimal, zzd com_google_android_gms_internal_zzwa_zzd, double d) {
        zzaa.zzy(com_google_android_gms_internal_zzwa_zzd);
        if (com_google_android_gms_internal_zzwa_zzd.awn == null || com_google_android_gms_internal_zzwa_zzd.awn.intValue() == 0) {
            return null;
        }
        BigDecimal bigDecimal2;
        BigDecimal bigDecimal3;
        BigDecimal bigDecimal4;
        if (com_google_android_gms_internal_zzwa_zzd.awn.intValue() == 4) {
            if (com_google_android_gms_internal_zzwa_zzd.awq == null || com_google_android_gms_internal_zzwa_zzd.awr == null) {
                return null;
            }
        } else if (com_google_android_gms_internal_zzwa_zzd.awp == null) {
            return null;
        }
        int intValue = com_google_android_gms_internal_zzwa_zzd.awn.intValue();
        if (com_google_android_gms_internal_zzwa_zzd.awn.intValue() == 4) {
            if (!zzal.zzng(com_google_android_gms_internal_zzwa_zzd.awq) || !zzal.zzng(com_google_android_gms_internal_zzwa_zzd.awr)) {
                return null;
            }
            try {
                bigDecimal2 = new BigDecimal(com_google_android_gms_internal_zzwa_zzd.awq);
                bigDecimal3 = new BigDecimal(com_google_android_gms_internal_zzwa_zzd.awr);
                bigDecimal4 = null;
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (!zzal.zzng(com_google_android_gms_internal_zzwa_zzd.awp)) {
            return null;
        } else {
            try {
                bigDecimal4 = new BigDecimal(com_google_android_gms_internal_zzwa_zzd.awp);
                bigDecimal3 = null;
                bigDecimal2 = null;
            } catch (NumberFormatException e2) {
                return null;
            }
        }
        return zza(bigDecimal, intValue, bigDecimal4, bigDecimal2, bigDecimal3, d);
    }

    @WorkerThread
    void zza(String str, zza[] com_google_android_gms_internal_zzwa_zzaArr) {
        zzaa.zzy(com_google_android_gms_internal_zzwa_zzaArr);
        for (zza com_google_android_gms_internal_zzwa_zza : com_google_android_gms_internal_zzwa_zzaArr) {
            for (zzb com_google_android_gms_internal_zzwa_zzb : com_google_android_gms_internal_zzwa_zza.awb) {
                String str2 = (String) AppMeasurement.zza.aqx.get(com_google_android_gms_internal_zzwa_zzb.awe);
                if (str2 != null) {
                    com_google_android_gms_internal_zzwa_zzb.awe = str2;
                }
                for (com.google.android.gms.internal.zzwa.zzc com_google_android_gms_internal_zzwa_zzc : com_google_android_gms_internal_zzwa_zzb.awf) {
                    str2 = (String) AppMeasurement.zze.aqy.get(com_google_android_gms_internal_zzwa_zzc.awm);
                    if (str2 != null) {
                        com_google_android_gms_internal_zzwa_zzc.awm = str2;
                    }
                }
            }
            for (zze com_google_android_gms_internal_zzwa_zze : com_google_android_gms_internal_zzwa_zza.awa) {
                str2 = (String) AppMeasurement.zzg.aqC.get(com_google_android_gms_internal_zzwa_zze.awt);
                if (str2 != null) {
                    com_google_android_gms_internal_zzwa_zze.awt = str2;
                }
            }
        }
        zzbvw().zzb(str, com_google_android_gms_internal_zzwa_zzaArr);
    }

    @WorkerThread
    zzwc.zza[] zza(String str, zzwc.zzb[] com_google_android_gms_internal_zzwc_zzbArr, zzg[] com_google_android_gms_internal_zzwc_zzgArr) {
        int intValue;
        BitSet bitSet;
        BitSet bitSet2;
        Map map;
        Map map2;
        Boolean zza;
        Object obj;
        zzaa.zzib(str);
        Set hashSet = new HashSet();
        ArrayMap arrayMap = new ArrayMap();
        Map arrayMap2 = new ArrayMap();
        ArrayMap arrayMap3 = new ArrayMap();
        Map zzmd = zzbvw().zzmd(str);
        if (zzmd != null) {
            for (Integer intValue2 : zzmd.keySet()) {
                intValue = intValue2.intValue();
                zzwc.zzf com_google_android_gms_internal_zzwc_zzf = (zzwc.zzf) zzmd.get(Integer.valueOf(intValue));
                bitSet = (BitSet) arrayMap2.get(Integer.valueOf(intValue));
                bitSet2 = (BitSet) arrayMap3.get(Integer.valueOf(intValue));
                if (bitSet == null) {
                    bitSet = new BitSet();
                    arrayMap2.put(Integer.valueOf(intValue), bitSet);
                    bitSet2 = new BitSet();
                    arrayMap3.put(Integer.valueOf(intValue), bitSet2);
                }
                for (int i = 0; i < com_google_android_gms_internal_zzwc_zzf.axu.length * 64; i++) {
                    if (zzal.zza(com_google_android_gms_internal_zzwc_zzf.axu, i)) {
                        zzbwb().zzbxe().zze("Filter already evaluated. audience ID, filter ID", Integer.valueOf(intValue), Integer.valueOf(i));
                        bitSet2.set(i);
                        if (zzal.zza(com_google_android_gms_internal_zzwc_zzf.axv, i)) {
                            bitSet.set(i);
                        }
                    }
                }
                zzwc.zza com_google_android_gms_internal_zzwc_zza = new zzwc.zza();
                arrayMap.put(Integer.valueOf(intValue), com_google_android_gms_internal_zzwc_zza);
                com_google_android_gms_internal_zzwc_zza.awL = Boolean.valueOf(false);
                com_google_android_gms_internal_zzwc_zza.awK = com_google_android_gms_internal_zzwc_zzf;
                com_google_android_gms_internal_zzwc_zza.awJ = new zzwc.zzf();
                com_google_android_gms_internal_zzwc_zza.awJ.axv = zzal.zza(bitSet);
                com_google_android_gms_internal_zzwc_zza.awJ.axu = zzal.zza(bitSet2);
            }
        }
        if (com_google_android_gms_internal_zzwc_zzbArr != null) {
            ArrayMap arrayMap4 = new ArrayMap();
            for (zzwc.zzb com_google_android_gms_internal_zzwc_zzb : com_google_android_gms_internal_zzwc_zzbArr) {
                zzi com_google_android_gms_measurement_internal_zzi;
                zzi zzap = zzbvw().zzap(str, com_google_android_gms_internal_zzwc_zzb.name);
                if (zzap == null) {
                    zzbwb().zzbxa().zzj("Event aggregate wasn't created during raw event logging. event", com_google_android_gms_internal_zzwc_zzb.name);
                    com_google_android_gms_measurement_internal_zzi = new zzi(str, com_google_android_gms_internal_zzwc_zzb.name, 1, 1, com_google_android_gms_internal_zzwc_zzb.awO.longValue());
                } else {
                    com_google_android_gms_measurement_internal_zzi = zzap.zzbwv();
                }
                zzbvw().zza(com_google_android_gms_measurement_internal_zzi);
                long j = com_google_android_gms_measurement_internal_zzi.arD;
                map = (Map) arrayMap4.get(com_google_android_gms_internal_zzwc_zzb.name);
                if (map == null) {
                    map = zzbvw().zzas(str, com_google_android_gms_internal_zzwc_zzb.name);
                    if (map == null) {
                        map = new ArrayMap();
                    }
                    arrayMap4.put(com_google_android_gms_internal_zzwc_zzb.name, map);
                    map2 = map;
                } else {
                    map2 = map;
                }
                for (Integer intValue22 : r7.keySet()) {
                    int intValue3 = intValue22.intValue();
                    if (hashSet.contains(Integer.valueOf(intValue3))) {
                        zzbwb().zzbxe().zzj("Skipping failed audience ID", Integer.valueOf(intValue3));
                    } else {
                        bitSet = (BitSet) arrayMap2.get(Integer.valueOf(intValue3));
                        bitSet2 = (BitSet) arrayMap3.get(Integer.valueOf(intValue3));
                        if (((zzwc.zza) arrayMap.get(Integer.valueOf(intValue3))) == null) {
                            zzwc.zza com_google_android_gms_internal_zzwc_zza2 = new zzwc.zza();
                            arrayMap.put(Integer.valueOf(intValue3), com_google_android_gms_internal_zzwc_zza2);
                            com_google_android_gms_internal_zzwc_zza2.awL = Boolean.valueOf(true);
                            bitSet = new BitSet();
                            arrayMap2.put(Integer.valueOf(intValue3), bitSet);
                            bitSet2 = new BitSet();
                            arrayMap3.put(Integer.valueOf(intValue3), bitSet2);
                        }
                        for (zzb com_google_android_gms_internal_zzwa_zzb : (List) r7.get(Integer.valueOf(intValue3))) {
                            if (zzbwb().zzbi(2)) {
                                zzbwb().zzbxe().zzd("Evaluating filter. audience, filter, event", Integer.valueOf(intValue3), com_google_android_gms_internal_zzwa_zzb.awd, com_google_android_gms_internal_zzwa_zzb.awe);
                                zzbwb().zzbxe().zzj("Filter definition", zzal.zza(com_google_android_gms_internal_zzwa_zzb));
                            }
                            if (com_google_android_gms_internal_zzwa_zzb.awd == null || com_google_android_gms_internal_zzwa_zzb.awd.intValue() > 256) {
                                zzbwb().zzbxa().zzj("Invalid event filter ID. id", String.valueOf(com_google_android_gms_internal_zzwa_zzb.awd));
                            } else if (bitSet.get(com_google_android_gms_internal_zzwa_zzb.awd.intValue())) {
                                zzbwb().zzbxe().zze("Event filter already evaluated true. audience ID, filter ID", Integer.valueOf(intValue3), com_google_android_gms_internal_zzwa_zzb.awd);
                            } else {
                                zza = zza(com_google_android_gms_internal_zzwa_zzb, com_google_android_gms_internal_zzwc_zzb, j);
                                zzq.zza zzbxe = zzbwb().zzbxe();
                                String str2 = "Event filter result";
                                if (zza == null) {
                                    obj = "null";
                                } else {
                                    Boolean bool = zza;
                                }
                                zzbxe.zzj(str2, obj);
                                if (zza == null) {
                                    hashSet.add(Integer.valueOf(intValue3));
                                } else {
                                    bitSet2.set(com_google_android_gms_internal_zzwa_zzb.awd.intValue());
                                    if (zza.booleanValue()) {
                                        bitSet.set(com_google_android_gms_internal_zzwa_zzb.awd.intValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (com_google_android_gms_internal_zzwc_zzgArr != null) {
            Map arrayMap5 = new ArrayMap();
            for (zzg com_google_android_gms_internal_zzwc_zzg : com_google_android_gms_internal_zzwc_zzgArr) {
                map = (Map) arrayMap5.get(com_google_android_gms_internal_zzwc_zzg.name);
                if (map == null) {
                    map = zzbvw().zzat(str, com_google_android_gms_internal_zzwc_zzg.name);
                    if (map == null) {
                        map = new ArrayMap();
                    }
                    arrayMap5.put(com_google_android_gms_internal_zzwc_zzg.name, map);
                    map2 = map;
                } else {
                    map2 = map;
                }
                for (Integer intValue222 : r7.keySet()) {
                    int intValue4 = intValue222.intValue();
                    if (hashSet.contains(Integer.valueOf(intValue4))) {
                        zzbwb().zzbxe().zzj("Skipping failed audience ID", Integer.valueOf(intValue4));
                    } else {
                        bitSet = (BitSet) arrayMap2.get(Integer.valueOf(intValue4));
                        bitSet2 = (BitSet) arrayMap3.get(Integer.valueOf(intValue4));
                        if (((zzwc.zza) arrayMap.get(Integer.valueOf(intValue4))) == null) {
                            com_google_android_gms_internal_zzwc_zza2 = new zzwc.zza();
                            arrayMap.put(Integer.valueOf(intValue4), com_google_android_gms_internal_zzwc_zza2);
                            com_google_android_gms_internal_zzwc_zza2.awL = Boolean.valueOf(true);
                            bitSet = new BitSet();
                            arrayMap2.put(Integer.valueOf(intValue4), bitSet);
                            bitSet2 = new BitSet();
                            arrayMap3.put(Integer.valueOf(intValue4), bitSet2);
                        }
                        for (zze com_google_android_gms_internal_zzwa_zze : (List) r7.get(Integer.valueOf(intValue4))) {
                            if (zzbwb().zzbi(2)) {
                                zzbwb().zzbxe().zzd("Evaluating filter. audience, filter, property", Integer.valueOf(intValue4), com_google_android_gms_internal_zzwa_zze.awd, com_google_android_gms_internal_zzwa_zze.awt);
                                zzbwb().zzbxe().zzj("Filter definition", zzal.zza(com_google_android_gms_internal_zzwa_zze));
                            }
                            if (com_google_android_gms_internal_zzwa_zze.awd == null || com_google_android_gms_internal_zzwa_zze.awd.intValue() > 256) {
                                zzbwb().zzbxa().zzj("Invalid property filter ID. id", String.valueOf(com_google_android_gms_internal_zzwa_zze.awd));
                                hashSet.add(Integer.valueOf(intValue4));
                                break;
                            } else if (bitSet.get(com_google_android_gms_internal_zzwa_zze.awd.intValue())) {
                                zzbwb().zzbxe().zze("Property filter already evaluated true. audience ID, filter ID", Integer.valueOf(intValue4), com_google_android_gms_internal_zzwa_zze.awd);
                            } else {
                                zza = zza(com_google_android_gms_internal_zzwa_zze, com_google_android_gms_internal_zzwc_zzg);
                                zzq.zza zzbxe2 = zzbwb().zzbxe();
                                String str3 = "Property filter result";
                                if (zza == null) {
                                    obj = "null";
                                } else {
                                    bool = zza;
                                }
                                zzbxe2.zzj(str3, obj);
                                if (zza == null) {
                                    hashSet.add(Integer.valueOf(intValue4));
                                } else {
                                    bitSet2.set(com_google_android_gms_internal_zzwa_zze.awd.intValue());
                                    if (zza.booleanValue()) {
                                        bitSet.set(com_google_android_gms_internal_zzwa_zze.awd.intValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        zzwc.zza[] com_google_android_gms_internal_zzwc_zzaArr = new zzwc.zza[arrayMap2.size()];
        int i2 = 0;
        for (Integer intValue2222 : arrayMap2.keySet()) {
            intValue = intValue2222.intValue();
            if (!hashSet.contains(Integer.valueOf(intValue))) {
                com_google_android_gms_internal_zzwc_zza2 = (zzwc.zza) arrayMap.get(Integer.valueOf(intValue));
                com_google_android_gms_internal_zzwc_zza = com_google_android_gms_internal_zzwc_zza2 == null ? new zzwc.zza() : com_google_android_gms_internal_zzwc_zza2;
                int i3 = i2 + 1;
                com_google_android_gms_internal_zzwc_zzaArr[i2] = com_google_android_gms_internal_zzwc_zza;
                com_google_android_gms_internal_zzwc_zza.avZ = Integer.valueOf(intValue);
                com_google_android_gms_internal_zzwc_zza.awJ = new zzwc.zzf();
                com_google_android_gms_internal_zzwc_zza.awJ.axv = zzal.zza((BitSet) arrayMap2.get(Integer.valueOf(intValue)));
                com_google_android_gms_internal_zzwc_zza.awJ.axu = zzal.zza((BitSet) arrayMap3.get(Integer.valueOf(intValue)));
                zzbvw().zza(str, intValue, com_google_android_gms_internal_zzwc_zza.awJ);
                i2 = i3;
            }
        }
        return (zzwc.zza[]) Arrays.copyOf(com_google_android_gms_internal_zzwc_zzaArr, i2);
    }

    protected void zzzy() {
    }
}

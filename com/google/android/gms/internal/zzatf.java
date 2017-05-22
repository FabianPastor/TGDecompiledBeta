package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzauu.zza;
import com.google.android.gms.internal.zzauu.zzb;
import com.google.android.gms.internal.zzauu.zzc;
import com.google.android.gms.internal.zzauu.zzd;
import com.google.android.gms.internal.zzauu.zze;
import com.google.android.gms.internal.zzauu.zzf;
import com.google.android.gms.internal.zzauw.zzg;
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

class zzatf extends zzauh {
    zzatf(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
    }

    private Boolean zza(zzb com_google_android_gms_internal_zzauu_zzb, zzauw.zzb com_google_android_gms_internal_zzauw_zzb, long j) {
        Boolean zza;
        if (com_google_android_gms_internal_zzauu_zzb.zzbwv != null) {
            zza = zza(j, com_google_android_gms_internal_zzauu_zzb.zzbwv);
            if (zza == null) {
                return null;
            }
            if (!zza.booleanValue()) {
                return Boolean.valueOf(false);
            }
        }
        Set hashSet = new HashSet();
        for (zzc com_google_android_gms_internal_zzauu_zzc : com_google_android_gms_internal_zzauu_zzb.zzbwt) {
            if (TextUtils.isEmpty(com_google_android_gms_internal_zzauu_zzc.zzbwA)) {
                zzKl().zzMb().zzj("null or empty param name in filter. event", com_google_android_gms_internal_zzauw_zzb.name);
                return null;
            }
            hashSet.add(com_google_android_gms_internal_zzauu_zzc.zzbwA);
        }
        Map arrayMap = new ArrayMap();
        for (zzauw.zzc com_google_android_gms_internal_zzauw_zzc : com_google_android_gms_internal_zzauw_zzb.zzbxb) {
            if (hashSet.contains(com_google_android_gms_internal_zzauw_zzc.name)) {
                if (com_google_android_gms_internal_zzauw_zzc.zzbxf != null) {
                    arrayMap.put(com_google_android_gms_internal_zzauw_zzc.name, com_google_android_gms_internal_zzauw_zzc.zzbxf);
                } else if (com_google_android_gms_internal_zzauw_zzc.zzbwi != null) {
                    arrayMap.put(com_google_android_gms_internal_zzauw_zzc.name, com_google_android_gms_internal_zzauw_zzc.zzbwi);
                } else if (com_google_android_gms_internal_zzauw_zzc.zzaGV != null) {
                    arrayMap.put(com_google_android_gms_internal_zzauw_zzc.name, com_google_android_gms_internal_zzauw_zzc.zzaGV);
                } else {
                    zzKl().zzMb().zze("Unknown value for param. event, param", com_google_android_gms_internal_zzauw_zzb.name, com_google_android_gms_internal_zzauw_zzc.name);
                    return null;
                }
            }
        }
        for (zzc com_google_android_gms_internal_zzauu_zzc2 : com_google_android_gms_internal_zzauu_zzb.zzbwt) {
            boolean equals = Boolean.TRUE.equals(com_google_android_gms_internal_zzauu_zzc2.zzbwz);
            CharSequence charSequence = com_google_android_gms_internal_zzauu_zzc2.zzbwA;
            if (TextUtils.isEmpty(charSequence)) {
                zzKl().zzMb().zzj("Event has empty param name. event", com_google_android_gms_internal_zzauw_zzb.name);
                return null;
            }
            Object obj = arrayMap.get(charSequence);
            if (obj instanceof Long) {
                if (com_google_android_gms_internal_zzauu_zzc2.zzbwy == null) {
                    zzKl().zzMb().zze("No number filter for long param. event, param", com_google_android_gms_internal_zzauw_zzb.name, charSequence);
                    return null;
                }
                zza = zza(((Long) obj).longValue(), com_google_android_gms_internal_zzauu_zzc2.zzbwy);
                if (zza == null) {
                    return null;
                }
                if (((!zza.booleanValue() ? 1 : 0) ^ equals) != 0) {
                    return Boolean.valueOf(false);
                }
            } else if (obj instanceof Double) {
                if (com_google_android_gms_internal_zzauu_zzc2.zzbwy == null) {
                    zzKl().zzMb().zze("No number filter for double param. event, param", com_google_android_gms_internal_zzauw_zzb.name, charSequence);
                    return null;
                }
                zza = zza(((Double) obj).doubleValue(), com_google_android_gms_internal_zzauu_zzc2.zzbwy);
                if (zza == null) {
                    return null;
                }
                if (((!zza.booleanValue() ? 1 : 0) ^ equals) != 0) {
                    return Boolean.valueOf(false);
                }
            } else if (obj instanceof String) {
                if (com_google_android_gms_internal_zzauu_zzc2.zzbwx != null) {
                    zza = zza((String) obj, com_google_android_gms_internal_zzauu_zzc2.zzbwx);
                } else if (com_google_android_gms_internal_zzauu_zzc2.zzbwy == null) {
                    zzKl().zzMb().zze("No filter for String param. event, param", com_google_android_gms_internal_zzauw_zzb.name, charSequence);
                    return null;
                } else if (zzaut.zzgf((String) obj)) {
                    zza = zza((String) obj, com_google_android_gms_internal_zzauu_zzc2.zzbwy);
                } else {
                    zzKl().zzMb().zze("Invalid param value for number filter. event, param", com_google_android_gms_internal_zzauw_zzb.name, charSequence);
                    return null;
                }
                if (zza == null) {
                    return null;
                }
                if (((!zza.booleanValue() ? 1 : 0) ^ equals) != 0) {
                    return Boolean.valueOf(false);
                }
            } else if (obj == null) {
                zzKl().zzMf().zze("Missing param for filter. event, param", com_google_android_gms_internal_zzauw_zzb.name, charSequence);
                return Boolean.valueOf(false);
            } else {
                zzKl().zzMb().zze("Unknown param type. event, param", com_google_android_gms_internal_zzauw_zzb.name, charSequence);
                return null;
            }
        }
        return Boolean.valueOf(true);
    }

    private Boolean zza(zze com_google_android_gms_internal_zzauu_zze, zzg com_google_android_gms_internal_zzauw_zzg) {
        zzc com_google_android_gms_internal_zzauu_zzc = com_google_android_gms_internal_zzauu_zze.zzbwI;
        if (com_google_android_gms_internal_zzauu_zzc == null) {
            zzKl().zzMb().zzj("Missing property filter. property", com_google_android_gms_internal_zzauw_zzg.name);
            return null;
        }
        boolean equals = Boolean.TRUE.equals(com_google_android_gms_internal_zzauu_zzc.zzbwz);
        if (com_google_android_gms_internal_zzauw_zzg.zzbxf != null) {
            if (com_google_android_gms_internal_zzauu_zzc.zzbwy != null) {
                return zza(zza(com_google_android_gms_internal_zzauw_zzg.zzbxf.longValue(), com_google_android_gms_internal_zzauu_zzc.zzbwy), equals);
            }
            zzKl().zzMb().zzj("No number filter for long property. property", com_google_android_gms_internal_zzauw_zzg.name);
            return null;
        } else if (com_google_android_gms_internal_zzauw_zzg.zzbwi != null) {
            if (com_google_android_gms_internal_zzauu_zzc.zzbwy != null) {
                return zza(zza(com_google_android_gms_internal_zzauw_zzg.zzbwi.doubleValue(), com_google_android_gms_internal_zzauu_zzc.zzbwy), equals);
            }
            zzKl().zzMb().zzj("No number filter for double property. property", com_google_android_gms_internal_zzauw_zzg.name);
            return null;
        } else if (com_google_android_gms_internal_zzauw_zzg.zzaGV == null) {
            zzKl().zzMb().zzj("User property has no value, property", com_google_android_gms_internal_zzauw_zzg.name);
            return null;
        } else if (com_google_android_gms_internal_zzauu_zzc.zzbwx != null) {
            return zza(zza(com_google_android_gms_internal_zzauw_zzg.zzaGV, com_google_android_gms_internal_zzauu_zzc.zzbwx), equals);
        } else {
            if (com_google_android_gms_internal_zzauu_zzc.zzbwy == null) {
                zzKl().zzMb().zzj("No string or number filter defined. property", com_google_android_gms_internal_zzauw_zzg.name);
                return null;
            } else if (zzaut.zzgf(com_google_android_gms_internal_zzauw_zzg.zzaGV)) {
                return zza(zza(com_google_android_gms_internal_zzauw_zzg.zzaGV, com_google_android_gms_internal_zzauu_zzc.zzbwy), equals);
            } else {
                zzKl().zzMb().zze("Invalid user property value for Numeric number filter. property, value", com_google_android_gms_internal_zzauw_zzg.name, com_google_android_gms_internal_zzauw_zzg.zzaGV);
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

    public Boolean zza(double d, zzd com_google_android_gms_internal_zzauu_zzd) {
        try {
            return zza(new BigDecimal(d), com_google_android_gms_internal_zzauu_zzd, Math.ulp(d));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Boolean zza(long j, zzd com_google_android_gms_internal_zzauu_zzd) {
        try {
            return zza(new BigDecimal(j), com_google_android_gms_internal_zzauu_zzd, 0.0d);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Boolean zza(String str, zzd com_google_android_gms_internal_zzauu_zzd) {
        Boolean bool = null;
        if (zzaut.zzgf(str)) {
            try {
                bool = zza(new BigDecimal(str), com_google_android_gms_internal_zzauu_zzd, 0.0d);
            } catch (NumberFormatException e) {
            }
        }
        return bool;
    }

    Boolean zza(String str, zzf com_google_android_gms_internal_zzauu_zzf) {
        String str2 = null;
        zzac.zzw(com_google_android_gms_internal_zzauu_zzf);
        if (str == null || com_google_android_gms_internal_zzauu_zzf.zzbwJ == null || com_google_android_gms_internal_zzauu_zzf.zzbwJ.intValue() == 0) {
            return null;
        }
        if (com_google_android_gms_internal_zzauu_zzf.zzbwJ.intValue() == 6) {
            if (com_google_android_gms_internal_zzauu_zzf.zzbwM == null || com_google_android_gms_internal_zzauu_zzf.zzbwM.length == 0) {
                return null;
            }
        } else if (com_google_android_gms_internal_zzauu_zzf.zzbwK == null) {
            return null;
        }
        int intValue = com_google_android_gms_internal_zzauu_zzf.zzbwJ.intValue();
        boolean z = com_google_android_gms_internal_zzauu_zzf.zzbwL != null && com_google_android_gms_internal_zzauu_zzf.zzbwL.booleanValue();
        String toUpperCase = (z || intValue == 1 || intValue == 6) ? com_google_android_gms_internal_zzauu_zzf.zzbwK : com_google_android_gms_internal_zzauu_zzf.zzbwK.toUpperCase(Locale.ENGLISH);
        List zza = com_google_android_gms_internal_zzauu_zzf.zzbwM == null ? null : zza(com_google_android_gms_internal_zzauu_zzf.zzbwM, z);
        if (intValue == 1) {
            str2 = toUpperCase;
        }
        return zza(str, intValue, z, toUpperCase, zza, str2);
    }

    Boolean zza(BigDecimal bigDecimal, zzd com_google_android_gms_internal_zzauu_zzd, double d) {
        zzac.zzw(com_google_android_gms_internal_zzauu_zzd);
        if (com_google_android_gms_internal_zzauu_zzd.zzbwB == null || com_google_android_gms_internal_zzauu_zzd.zzbwB.intValue() == 0) {
            return null;
        }
        BigDecimal bigDecimal2;
        BigDecimal bigDecimal3;
        BigDecimal bigDecimal4;
        if (com_google_android_gms_internal_zzauu_zzd.zzbwB.intValue() == 4) {
            if (com_google_android_gms_internal_zzauu_zzd.zzbwE == null || com_google_android_gms_internal_zzauu_zzd.zzbwF == null) {
                return null;
            }
        } else if (com_google_android_gms_internal_zzauu_zzd.zzbwD == null) {
            return null;
        }
        int intValue = com_google_android_gms_internal_zzauu_zzd.zzbwB.intValue();
        if (com_google_android_gms_internal_zzauu_zzd.zzbwB.intValue() == 4) {
            if (!zzaut.zzgf(com_google_android_gms_internal_zzauu_zzd.zzbwE) || !zzaut.zzgf(com_google_android_gms_internal_zzauu_zzd.zzbwF)) {
                return null;
            }
            try {
                bigDecimal2 = new BigDecimal(com_google_android_gms_internal_zzauu_zzd.zzbwE);
                bigDecimal3 = new BigDecimal(com_google_android_gms_internal_zzauu_zzd.zzbwF);
                bigDecimal4 = null;
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (!zzaut.zzgf(com_google_android_gms_internal_zzauu_zzd.zzbwD)) {
            return null;
        } else {
            try {
                bigDecimal4 = new BigDecimal(com_google_android_gms_internal_zzauu_zzd.zzbwD);
                bigDecimal3 = null;
                bigDecimal2 = null;
            } catch (NumberFormatException e2) {
                return null;
            }
        }
        return zza(bigDecimal, intValue, bigDecimal4, bigDecimal2, bigDecimal3, d);
    }

    @WorkerThread
    void zza(String str, zza[] com_google_android_gms_internal_zzauu_zzaArr) {
        zzac.zzw(com_google_android_gms_internal_zzauu_zzaArr);
        for (zza com_google_android_gms_internal_zzauu_zza : com_google_android_gms_internal_zzauu_zzaArr) {
            for (zzb com_google_android_gms_internal_zzauu_zzb : com_google_android_gms_internal_zzauu_zza.zzbwp) {
                String str2 = (String) AppMeasurement.zza.zzbqc.get(com_google_android_gms_internal_zzauu_zzb.zzbws);
                if (str2 != null) {
                    com_google_android_gms_internal_zzauu_zzb.zzbws = str2;
                }
                for (zzc com_google_android_gms_internal_zzauu_zzc : com_google_android_gms_internal_zzauu_zzb.zzbwt) {
                    str2 = (String) AppMeasurement.zze.zzbqd.get(com_google_android_gms_internal_zzauu_zzc.zzbwA);
                    if (str2 != null) {
                        com_google_android_gms_internal_zzauu_zzc.zzbwA = str2;
                    }
                }
            }
            for (zze com_google_android_gms_internal_zzauu_zze : com_google_android_gms_internal_zzauu_zza.zzbwo) {
                str2 = (String) AppMeasurement.zzg.zzbqh.get(com_google_android_gms_internal_zzauu_zze.zzbwH);
                if (str2 != null) {
                    com_google_android_gms_internal_zzauu_zze.zzbwH = str2;
                }
            }
        }
        zzKg().zzb(str, com_google_android_gms_internal_zzauu_zzaArr);
    }

    @WorkerThread
    zzauw.zza[] zza(String str, zzauw.zzb[] com_google_android_gms_internal_zzauw_zzbArr, zzg[] com_google_android_gms_internal_zzauw_zzgArr) {
        int intValue;
        BitSet bitSet;
        BitSet bitSet2;
        Map map;
        Map map2;
        Boolean zza;
        Object obj;
        zzac.zzdr(str);
        Set hashSet = new HashSet();
        ArrayMap arrayMap = new ArrayMap();
        Map arrayMap2 = new ArrayMap();
        ArrayMap arrayMap3 = new ArrayMap();
        Map zzfy = zzKg().zzfy(str);
        if (zzfy != null) {
            for (Integer intValue2 : zzfy.keySet()) {
                intValue = intValue2.intValue();
                zzauw.zzf com_google_android_gms_internal_zzauw_zzf = (zzauw.zzf) zzfy.get(Integer.valueOf(intValue));
                bitSet = (BitSet) arrayMap2.get(Integer.valueOf(intValue));
                bitSet2 = (BitSet) arrayMap3.get(Integer.valueOf(intValue));
                if (bitSet == null) {
                    bitSet = new BitSet();
                    arrayMap2.put(Integer.valueOf(intValue), bitSet);
                    bitSet2 = new BitSet();
                    arrayMap3.put(Integer.valueOf(intValue), bitSet2);
                }
                for (int i = 0; i < com_google_android_gms_internal_zzauw_zzf.zzbxJ.length * 64; i++) {
                    if (zzaut.zza(com_google_android_gms_internal_zzauw_zzf.zzbxJ, i)) {
                        zzKl().zzMf().zze("Filter already evaluated. audience ID, filter ID", Integer.valueOf(intValue), Integer.valueOf(i));
                        bitSet2.set(i);
                        if (zzaut.zza(com_google_android_gms_internal_zzauw_zzf.zzbxK, i)) {
                            bitSet.set(i);
                        }
                    }
                }
                zzauw.zza com_google_android_gms_internal_zzauw_zza = new zzauw.zza();
                arrayMap.put(Integer.valueOf(intValue), com_google_android_gms_internal_zzauw_zza);
                com_google_android_gms_internal_zzauw_zza.zzbwZ = Boolean.valueOf(false);
                com_google_android_gms_internal_zzauw_zza.zzbwY = com_google_android_gms_internal_zzauw_zzf;
                com_google_android_gms_internal_zzauw_zza.zzbwX = new zzauw.zzf();
                com_google_android_gms_internal_zzauw_zza.zzbwX.zzbxK = zzaut.zza(bitSet);
                com_google_android_gms_internal_zzauw_zza.zzbwX.zzbxJ = zzaut.zza(bitSet2);
            }
        }
        if (com_google_android_gms_internal_zzauw_zzbArr != null) {
            ArrayMap arrayMap4 = new ArrayMap();
            for (zzauw.zzb com_google_android_gms_internal_zzauw_zzb : com_google_android_gms_internal_zzauw_zzbArr) {
                zzatn com_google_android_gms_internal_zzatn;
                zzatn zzQ = zzKg().zzQ(str, com_google_android_gms_internal_zzauw_zzb.name);
                if (zzQ == null) {
                    zzKl().zzMb().zze("Event aggregate wasn't created during raw event logging. appId, event", zzatx.zzfE(str), com_google_android_gms_internal_zzauw_zzb.name);
                    com_google_android_gms_internal_zzatn = new zzatn(str, com_google_android_gms_internal_zzauw_zzb.name, 1, 1, com_google_android_gms_internal_zzauw_zzb.zzbxc.longValue());
                } else {
                    com_google_android_gms_internal_zzatn = zzQ.zzLV();
                }
                zzKg().zza(com_google_android_gms_internal_zzatn);
                long j = com_google_android_gms_internal_zzatn.zzbrB;
                map = (Map) arrayMap4.get(com_google_android_gms_internal_zzauw_zzb.name);
                if (map == null) {
                    map = zzKg().zzV(str, com_google_android_gms_internal_zzauw_zzb.name);
                    if (map == null) {
                        map = new ArrayMap();
                    }
                    arrayMap4.put(com_google_android_gms_internal_zzauw_zzb.name, map);
                    map2 = map;
                } else {
                    map2 = map;
                }
                for (Integer intValue22 : r7.keySet()) {
                    int intValue3 = intValue22.intValue();
                    if (hashSet.contains(Integer.valueOf(intValue3))) {
                        zzKl().zzMf().zzj("Skipping failed audience ID", Integer.valueOf(intValue3));
                    } else {
                        bitSet = (BitSet) arrayMap2.get(Integer.valueOf(intValue3));
                        bitSet2 = (BitSet) arrayMap3.get(Integer.valueOf(intValue3));
                        if (((zzauw.zza) arrayMap.get(Integer.valueOf(intValue3))) == null) {
                            zzauw.zza com_google_android_gms_internal_zzauw_zza2 = new zzauw.zza();
                            arrayMap.put(Integer.valueOf(intValue3), com_google_android_gms_internal_zzauw_zza2);
                            com_google_android_gms_internal_zzauw_zza2.zzbwZ = Boolean.valueOf(true);
                            bitSet = new BitSet();
                            arrayMap2.put(Integer.valueOf(intValue3), bitSet);
                            bitSet2 = new BitSet();
                            arrayMap3.put(Integer.valueOf(intValue3), bitSet2);
                        }
                        for (zzb com_google_android_gms_internal_zzauu_zzb : (List) r7.get(Integer.valueOf(intValue3))) {
                            if (zzKl().zzak(2)) {
                                zzKl().zzMf().zzd("Evaluating filter. audience, filter, event", Integer.valueOf(intValue3), com_google_android_gms_internal_zzauu_zzb.zzbwr, com_google_android_gms_internal_zzauu_zzb.zzbws);
                                zzKl().zzMf().zzj("Filter definition", zzaut.zza(com_google_android_gms_internal_zzauu_zzb));
                            }
                            if (com_google_android_gms_internal_zzauu_zzb.zzbwr == null || com_google_android_gms_internal_zzauu_zzb.zzbwr.intValue() > 256) {
                                zzKl().zzMb().zze("Invalid event filter ID. appId, id", zzatx.zzfE(str), String.valueOf(com_google_android_gms_internal_zzauu_zzb.zzbwr));
                            } else if (bitSet.get(com_google_android_gms_internal_zzauu_zzb.zzbwr.intValue())) {
                                zzKl().zzMf().zze("Event filter already evaluated true. audience ID, filter ID", Integer.valueOf(intValue3), com_google_android_gms_internal_zzauu_zzb.zzbwr);
                            } else {
                                zza = zza(com_google_android_gms_internal_zzauu_zzb, com_google_android_gms_internal_zzauw_zzb, j);
                                zzatx.zza zzMf = zzKl().zzMf();
                                String str2 = "Event filter result";
                                if (zza == null) {
                                    obj = "null";
                                } else {
                                    Boolean bool = zza;
                                }
                                zzMf.zzj(str2, obj);
                                if (zza == null) {
                                    hashSet.add(Integer.valueOf(intValue3));
                                } else {
                                    bitSet2.set(com_google_android_gms_internal_zzauu_zzb.zzbwr.intValue());
                                    if (zza.booleanValue()) {
                                        bitSet.set(com_google_android_gms_internal_zzauu_zzb.zzbwr.intValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (com_google_android_gms_internal_zzauw_zzgArr != null) {
            Map arrayMap5 = new ArrayMap();
            for (zzg com_google_android_gms_internal_zzauw_zzg : com_google_android_gms_internal_zzauw_zzgArr) {
                map = (Map) arrayMap5.get(com_google_android_gms_internal_zzauw_zzg.name);
                if (map == null) {
                    map = zzKg().zzW(str, com_google_android_gms_internal_zzauw_zzg.name);
                    if (map == null) {
                        map = new ArrayMap();
                    }
                    arrayMap5.put(com_google_android_gms_internal_zzauw_zzg.name, map);
                    map2 = map;
                } else {
                    map2 = map;
                }
                for (Integer intValue222 : r7.keySet()) {
                    int intValue4 = intValue222.intValue();
                    if (hashSet.contains(Integer.valueOf(intValue4))) {
                        zzKl().zzMf().zzj("Skipping failed audience ID", Integer.valueOf(intValue4));
                    } else {
                        bitSet = (BitSet) arrayMap2.get(Integer.valueOf(intValue4));
                        bitSet2 = (BitSet) arrayMap3.get(Integer.valueOf(intValue4));
                        if (((zzauw.zza) arrayMap.get(Integer.valueOf(intValue4))) == null) {
                            com_google_android_gms_internal_zzauw_zza2 = new zzauw.zza();
                            arrayMap.put(Integer.valueOf(intValue4), com_google_android_gms_internal_zzauw_zza2);
                            com_google_android_gms_internal_zzauw_zza2.zzbwZ = Boolean.valueOf(true);
                            bitSet = new BitSet();
                            arrayMap2.put(Integer.valueOf(intValue4), bitSet);
                            bitSet2 = new BitSet();
                            arrayMap3.put(Integer.valueOf(intValue4), bitSet2);
                        }
                        for (zze com_google_android_gms_internal_zzauu_zze : (List) r7.get(Integer.valueOf(intValue4))) {
                            if (zzKl().zzak(2)) {
                                zzKl().zzMf().zzd("Evaluating filter. audience, filter, property", Integer.valueOf(intValue4), com_google_android_gms_internal_zzauu_zze.zzbwr, com_google_android_gms_internal_zzauu_zze.zzbwH);
                                zzKl().zzMf().zzj("Filter definition", zzaut.zza(com_google_android_gms_internal_zzauu_zze));
                            }
                            if (com_google_android_gms_internal_zzauu_zze.zzbwr == null || com_google_android_gms_internal_zzauu_zze.zzbwr.intValue() > 256) {
                                zzKl().zzMb().zze("Invalid property filter ID. appId, id", zzatx.zzfE(str), String.valueOf(com_google_android_gms_internal_zzauu_zze.zzbwr));
                                hashSet.add(Integer.valueOf(intValue4));
                                break;
                            } else if (bitSet.get(com_google_android_gms_internal_zzauu_zze.zzbwr.intValue())) {
                                zzKl().zzMf().zze("Property filter already evaluated true. audience ID, filter ID", Integer.valueOf(intValue4), com_google_android_gms_internal_zzauu_zze.zzbwr);
                            } else {
                                zza = zza(com_google_android_gms_internal_zzauu_zze, com_google_android_gms_internal_zzauw_zzg);
                                zzatx.zza zzMf2 = zzKl().zzMf();
                                String str3 = "Property filter result";
                                if (zza == null) {
                                    obj = "null";
                                } else {
                                    bool = zza;
                                }
                                zzMf2.zzj(str3, obj);
                                if (zza == null) {
                                    hashSet.add(Integer.valueOf(intValue4));
                                } else {
                                    bitSet2.set(com_google_android_gms_internal_zzauu_zze.zzbwr.intValue());
                                    if (zza.booleanValue()) {
                                        bitSet.set(com_google_android_gms_internal_zzauu_zze.zzbwr.intValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        zzauw.zza[] com_google_android_gms_internal_zzauw_zzaArr = new zzauw.zza[arrayMap2.size()];
        int i2 = 0;
        for (Integer intValue2222 : arrayMap2.keySet()) {
            intValue = intValue2222.intValue();
            if (!hashSet.contains(Integer.valueOf(intValue))) {
                com_google_android_gms_internal_zzauw_zza2 = (zzauw.zza) arrayMap.get(Integer.valueOf(intValue));
                com_google_android_gms_internal_zzauw_zza = com_google_android_gms_internal_zzauw_zza2 == null ? new zzauw.zza() : com_google_android_gms_internal_zzauw_zza2;
                int i3 = i2 + 1;
                com_google_android_gms_internal_zzauw_zzaArr[i2] = com_google_android_gms_internal_zzauw_zza;
                com_google_android_gms_internal_zzauw_zza.zzbwn = Integer.valueOf(intValue);
                com_google_android_gms_internal_zzauw_zza.zzbwX = new zzauw.zzf();
                com_google_android_gms_internal_zzauw_zza.zzbwX.zzbxK = zzaut.zza((BitSet) arrayMap2.get(Integer.valueOf(intValue)));
                com_google_android_gms_internal_zzauw_zza.zzbwX.zzbxJ = zzaut.zza((BitSet) arrayMap3.get(Integer.valueOf(intValue)));
                zzKg().zza(str, intValue, com_google_android_gms_internal_zzauw_zza.zzbwX);
                i2 = i3;
            }
        }
        return (zzauw.zza[]) Arrays.copyOf(com_google_android_gms_internal_zzauw_zzaArr, i2);
    }

    protected void zzmS() {
    }
}

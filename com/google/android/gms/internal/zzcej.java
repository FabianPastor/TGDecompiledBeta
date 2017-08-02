package com.google.android.gms.internal;

import android.content.ContentValues;
import android.database.sqlite.SQLiteException;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbo;
import java.io.IOException;
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
import java.util.regex.PatternSyntaxException;

final class zzcej extends zzchj {
    zzcej(zzcgl com_google_android_gms_internal_zzcgl) {
        super(com_google_android_gms_internal_zzcgl);
    }

    private final Boolean zza(double d, zzcjp com_google_android_gms_internal_zzcjp) {
        try {
            return zza(new BigDecimal(d), com_google_android_gms_internal_zzcjp, Math.ulp(d));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private final Boolean zza(long j, zzcjp com_google_android_gms_internal_zzcjp) {
        try {
            return zza(new BigDecimal(j), com_google_android_gms_internal_zzcjp, 0.0d);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private final Boolean zza(zzcjn com_google_android_gms_internal_zzcjn, zzcjw com_google_android_gms_internal_zzcjw, long j) {
        Boolean zza;
        if (com_google_android_gms_internal_zzcjn.zzbuQ != null) {
            zza = zza(j, com_google_android_gms_internal_zzcjn.zzbuQ);
            if (zza == null) {
                return null;
            }
            if (!zza.booleanValue()) {
                return Boolean.valueOf(false);
            }
        }
        Set hashSet = new HashSet();
        for (zzcjo com_google_android_gms_internal_zzcjo : com_google_android_gms_internal_zzcjn.zzbuO) {
            if (TextUtils.isEmpty(com_google_android_gms_internal_zzcjo.zzbuV)) {
                zzwF().zzyz().zzj("null or empty param name in filter. event", zzwA().zzdW(com_google_android_gms_internal_zzcjw.name));
                return null;
            }
            hashSet.add(com_google_android_gms_internal_zzcjo.zzbuV);
        }
        Map arrayMap = new ArrayMap();
        for (zzcjx com_google_android_gms_internal_zzcjx : com_google_android_gms_internal_zzcjw.zzbvw) {
            if (hashSet.contains(com_google_android_gms_internal_zzcjx.name)) {
                if (com_google_android_gms_internal_zzcjx.zzbvA != null) {
                    arrayMap.put(com_google_android_gms_internal_zzcjx.name, com_google_android_gms_internal_zzcjx.zzbvA);
                } else if (com_google_android_gms_internal_zzcjx.zzbuB != null) {
                    arrayMap.put(com_google_android_gms_internal_zzcjx.name, com_google_android_gms_internal_zzcjx.zzbuB);
                } else if (com_google_android_gms_internal_zzcjx.zzaIF != null) {
                    arrayMap.put(com_google_android_gms_internal_zzcjx.name, com_google_android_gms_internal_zzcjx.zzaIF);
                } else {
                    zzwF().zzyz().zze("Unknown value for param. event, param", zzwA().zzdW(com_google_android_gms_internal_zzcjw.name), zzwA().zzdX(com_google_android_gms_internal_zzcjx.name));
                    return null;
                }
            }
        }
        for (zzcjo com_google_android_gms_internal_zzcjo2 : com_google_android_gms_internal_zzcjn.zzbuO) {
            boolean equals = Boolean.TRUE.equals(com_google_android_gms_internal_zzcjo2.zzbuU);
            String str = com_google_android_gms_internal_zzcjo2.zzbuV;
            if (TextUtils.isEmpty(str)) {
                zzwF().zzyz().zzj("Event has empty param name. event", zzwA().zzdW(com_google_android_gms_internal_zzcjw.name));
                return null;
            }
            Object obj = arrayMap.get(str);
            if (obj instanceof Long) {
                if (com_google_android_gms_internal_zzcjo2.zzbuT == null) {
                    zzwF().zzyz().zze("No number filter for long param. event, param", zzwA().zzdW(com_google_android_gms_internal_zzcjw.name), zzwA().zzdX(str));
                    return null;
                }
                zza = zza(((Long) obj).longValue(), com_google_android_gms_internal_zzcjo2.zzbuT);
                if (zza == null) {
                    return null;
                }
                if (((!zza.booleanValue() ? 1 : 0) ^ equals) != 0) {
                    return Boolean.valueOf(false);
                }
            } else if (obj instanceof Double) {
                if (com_google_android_gms_internal_zzcjo2.zzbuT == null) {
                    zzwF().zzyz().zze("No number filter for double param. event, param", zzwA().zzdW(com_google_android_gms_internal_zzcjw.name), zzwA().zzdX(str));
                    return null;
                }
                zza = zza(((Double) obj).doubleValue(), com_google_android_gms_internal_zzcjo2.zzbuT);
                if (zza == null) {
                    return null;
                }
                if (((!zza.booleanValue() ? 1 : 0) ^ equals) != 0) {
                    return Boolean.valueOf(false);
                }
            } else if (obj instanceof String) {
                if (com_google_android_gms_internal_zzcjo2.zzbuS != null) {
                    zza = zza((String) obj, com_google_android_gms_internal_zzcjo2.zzbuS);
                } else if (com_google_android_gms_internal_zzcjo2.zzbuT == null) {
                    zzwF().zzyz().zze("No filter for String param. event, param", zzwA().zzdW(com_google_android_gms_internal_zzcjw.name), zzwA().zzdX(str));
                    return null;
                } else if (zzcjl.zzez((String) obj)) {
                    zza = zza((String) obj, com_google_android_gms_internal_zzcjo2.zzbuT);
                } else {
                    zzwF().zzyz().zze("Invalid param value for number filter. event, param", zzwA().zzdW(com_google_android_gms_internal_zzcjw.name), zzwA().zzdX(str));
                    return null;
                }
                if (zza == null) {
                    return null;
                }
                if (((!zza.booleanValue() ? 1 : 0) ^ equals) != 0) {
                    return Boolean.valueOf(false);
                }
            } else if (obj == null) {
                zzwF().zzyD().zze("Missing param for filter. event, param", zzwA().zzdW(com_google_android_gms_internal_zzcjw.name), zzwA().zzdX(str));
                return Boolean.valueOf(false);
            } else {
                zzwF().zzyz().zze("Unknown param type. event, param", zzwA().zzdW(com_google_android_gms_internal_zzcjw.name), zzwA().zzdX(str));
                return null;
            }
        }
        return Boolean.valueOf(true);
    }

    private static Boolean zza(Boolean bool, boolean z) {
        return bool == null ? null : Boolean.valueOf(bool.booleanValue() ^ z);
    }

    private final Boolean zza(String str, int i, boolean z, String str2, List<String> list, String str3) {
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
                try {
                    return Boolean.valueOf(Pattern.compile(str3, z ? 0 : 66).matcher(str).matches());
                } catch (PatternSyntaxException e) {
                    zzwF().zzyz().zzj("Invalid regular expression in REGEXP audience filter. expression", str3);
                    return null;
                }
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

    private final Boolean zza(String str, zzcjp com_google_android_gms_internal_zzcjp) {
        Boolean bool = null;
        if (zzcjl.zzez(str)) {
            try {
                bool = zza(new BigDecimal(str), com_google_android_gms_internal_zzcjp, 0.0d);
            } catch (NumberFormatException e) {
            }
        }
        return bool;
    }

    private final Boolean zza(String str, zzcjr com_google_android_gms_internal_zzcjr) {
        int i = 0;
        String str2 = null;
        zzbo.zzu(com_google_android_gms_internal_zzcjr);
        if (str == null || com_google_android_gms_internal_zzcjr.zzbve == null || com_google_android_gms_internal_zzcjr.zzbve.intValue() == 0) {
            return null;
        }
        List list;
        if (com_google_android_gms_internal_zzcjr.zzbve.intValue() == 6) {
            if (com_google_android_gms_internal_zzcjr.zzbvh == null || com_google_android_gms_internal_zzcjr.zzbvh.length == 0) {
                return null;
            }
        } else if (com_google_android_gms_internal_zzcjr.zzbvf == null) {
            return null;
        }
        int intValue = com_google_android_gms_internal_zzcjr.zzbve.intValue();
        boolean z = com_google_android_gms_internal_zzcjr.zzbvg != null && com_google_android_gms_internal_zzcjr.zzbvg.booleanValue();
        String toUpperCase = (z || intValue == 1 || intValue == 6) ? com_google_android_gms_internal_zzcjr.zzbvf : com_google_android_gms_internal_zzcjr.zzbvf.toUpperCase(Locale.ENGLISH);
        if (com_google_android_gms_internal_zzcjr.zzbvh == null) {
            list = null;
        } else {
            String[] strArr = com_google_android_gms_internal_zzcjr.zzbvh;
            if (z) {
                list = Arrays.asList(strArr);
            } else {
                list = new ArrayList();
                int length = strArr.length;
                while (i < length) {
                    list.add(strArr[i].toUpperCase(Locale.ENGLISH));
                    i++;
                }
            }
        }
        if (intValue == 1) {
            str2 = toUpperCase;
        }
        return zza(str, intValue, z, toUpperCase, list, str2);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static Boolean zza(BigDecimal bigDecimal, zzcjp com_google_android_gms_internal_zzcjp, double d) {
        boolean z = true;
        zzbo.zzu(com_google_android_gms_internal_zzcjp);
        if (com_google_android_gms_internal_zzcjp.zzbuW == null || com_google_android_gms_internal_zzcjp.zzbuW.intValue() == 0) {
            return null;
        }
        BigDecimal bigDecimal2;
        BigDecimal bigDecimal3;
        if (com_google_android_gms_internal_zzcjp.zzbuW.intValue() == 4) {
            if (com_google_android_gms_internal_zzcjp.zzbuZ == null || com_google_android_gms_internal_zzcjp.zzbva == null) {
                return null;
            }
        } else if (com_google_android_gms_internal_zzcjp.zzbuY == null) {
            return null;
        }
        int intValue = com_google_android_gms_internal_zzcjp.zzbuW.intValue();
        BigDecimal bigDecimal4;
        if (com_google_android_gms_internal_zzcjp.zzbuW.intValue() == 4) {
            if (!zzcjl.zzez(com_google_android_gms_internal_zzcjp.zzbuZ) || !zzcjl.zzez(com_google_android_gms_internal_zzcjp.zzbva)) {
                return null;
            }
            try {
                bigDecimal2 = new BigDecimal(com_google_android_gms_internal_zzcjp.zzbuZ);
                bigDecimal4 = new BigDecimal(com_google_android_gms_internal_zzcjp.zzbva);
                bigDecimal3 = null;
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (!zzcjl.zzez(com_google_android_gms_internal_zzcjp.zzbuY)) {
            return null;
        } else {
            try {
                bigDecimal2 = null;
                bigDecimal3 = new BigDecimal(com_google_android_gms_internal_zzcjp.zzbuY);
                bigDecimal4 = null;
            } catch (NumberFormatException e2) {
                return null;
            }
        }
        if (intValue == 4) {
            if (bigDecimal2 == null) {
                return null;
            }
        }
        switch (intValue) {
            case 1:
                if (bigDecimal.compareTo(bigDecimal3) != -1) {
                    z = false;
                }
                return Boolean.valueOf(z);
            case 2:
                if (bigDecimal.compareTo(bigDecimal3) != 1) {
                    z = false;
                }
                return Boolean.valueOf(z);
            case 3:
                if (d != 0.0d) {
                    if (!(bigDecimal.compareTo(bigDecimal3.subtract(new BigDecimal(d).multiply(new BigDecimal(2)))) == 1 && bigDecimal.compareTo(bigDecimal3.add(new BigDecimal(d).multiply(new BigDecimal(2)))) == -1)) {
                        z = false;
                    }
                    return Boolean.valueOf(z);
                }
                if (bigDecimal.compareTo(bigDecimal3) != 0) {
                    z = false;
                }
                return Boolean.valueOf(z);
            case 4:
                if (bigDecimal.compareTo(bigDecimal2) == -1 || bigDecimal.compareTo(r3) == 1) {
                    z = false;
                }
                return Boolean.valueOf(z);
        }
        return null;
    }

    @WorkerThread
    final zzcjv[] zza(String str, zzcjw[] com_google_android_gms_internal_zzcjwArr, zzckb[] com_google_android_gms_internal_zzckbArr) {
        int intValue;
        zzcka com_google_android_gms_internal_zzcka;
        BitSet bitSet;
        BitSet bitSet2;
        Map map;
        Map map2;
        zzbo.zzcF(str);
        Set hashSet = new HashSet();
        ArrayMap arrayMap = new ArrayMap();
        Map arrayMap2 = new ArrayMap();
        ArrayMap arrayMap3 = new ArrayMap();
        Map zzdT = zzwz().zzdT(str);
        if (zzdT != null) {
            for (Integer intValue2 : zzdT.keySet()) {
                intValue = intValue2.intValue();
                com_google_android_gms_internal_zzcka = (zzcka) zzdT.get(Integer.valueOf(intValue));
                bitSet = (BitSet) arrayMap2.get(Integer.valueOf(intValue));
                bitSet2 = (BitSet) arrayMap3.get(Integer.valueOf(intValue));
                if (bitSet == null) {
                    bitSet = new BitSet();
                    arrayMap2.put(Integer.valueOf(intValue), bitSet);
                    bitSet2 = new BitSet();
                    arrayMap3.put(Integer.valueOf(intValue), bitSet2);
                }
                for (int i = 0; i < (com_google_android_gms_internal_zzcka.zzbwe.length << 6); i++) {
                    if (zzcjl.zza(com_google_android_gms_internal_zzcka.zzbwe, i)) {
                        zzwF().zzyD().zze("Filter already evaluated. audience ID, filter ID", Integer.valueOf(intValue), Integer.valueOf(i));
                        bitSet2.set(i);
                        if (zzcjl.zza(com_google_android_gms_internal_zzcka.zzbwf, i)) {
                            bitSet.set(i);
                        }
                    }
                }
                zzcjv com_google_android_gms_internal_zzcjv = new zzcjv();
                arrayMap.put(Integer.valueOf(intValue), com_google_android_gms_internal_zzcjv);
                com_google_android_gms_internal_zzcjv.zzbvu = Boolean.valueOf(false);
                com_google_android_gms_internal_zzcjv.zzbvt = com_google_android_gms_internal_zzcka;
                com_google_android_gms_internal_zzcjv.zzbvs = new zzcka();
                com_google_android_gms_internal_zzcjv.zzbvs.zzbwf = zzcjl.zza(bitSet);
                com_google_android_gms_internal_zzcjv.zzbvs.zzbwe = zzcjl.zza(bitSet2);
            }
        }
        if (com_google_android_gms_internal_zzcjwArr != null) {
            ArrayMap arrayMap4 = new ArrayMap();
            for (zzcjw com_google_android_gms_internal_zzcjw : com_google_android_gms_internal_zzcjwArr) {
                zzcev com_google_android_gms_internal_zzcev;
                zzcev zzE = zzwz().zzE(str, com_google_android_gms_internal_zzcjw.name);
                if (zzE == null) {
                    zzwF().zzyz().zze("Event aggregate wasn't created during raw event logging. appId, event", zzcfl.zzdZ(str), zzwA().zzdW(com_google_android_gms_internal_zzcjw.name));
                    com_google_android_gms_internal_zzcev = new zzcev(str, com_google_android_gms_internal_zzcjw.name, 1, 1, com_google_android_gms_internal_zzcjw.zzbvx.longValue());
                } else {
                    com_google_android_gms_internal_zzcev = zzE.zzys();
                }
                zzwz().zza(com_google_android_gms_internal_zzcev);
                long j = com_google_android_gms_internal_zzcev.zzbpG;
                map = (Map) arrayMap4.get(com_google_android_gms_internal_zzcjw.name);
                if (map == null) {
                    map = zzwz().zzJ(str, com_google_android_gms_internal_zzcjw.name);
                    if (map == null) {
                        map = new ArrayMap();
                    }
                    arrayMap4.put(com_google_android_gms_internal_zzcjw.name, map);
                    map2 = map;
                } else {
                    map2 = map;
                }
                for (Integer intValue22 : r7.keySet()) {
                    int intValue3 = intValue22.intValue();
                    if (hashSet.contains(Integer.valueOf(intValue3))) {
                        zzwF().zzyD().zzj("Skipping failed audience ID", Integer.valueOf(intValue3));
                    } else {
                        bitSet = (BitSet) arrayMap2.get(Integer.valueOf(intValue3));
                        bitSet2 = (BitSet) arrayMap3.get(Integer.valueOf(intValue3));
                        if (((zzcjv) arrayMap.get(Integer.valueOf(intValue3))) == null) {
                            zzcjv com_google_android_gms_internal_zzcjv2 = new zzcjv();
                            arrayMap.put(Integer.valueOf(intValue3), com_google_android_gms_internal_zzcjv2);
                            com_google_android_gms_internal_zzcjv2.zzbvu = Boolean.valueOf(true);
                            bitSet = new BitSet();
                            arrayMap2.put(Integer.valueOf(intValue3), bitSet);
                            bitSet2 = new BitSet();
                            arrayMap3.put(Integer.valueOf(intValue3), bitSet2);
                        }
                        for (zzcjn com_google_android_gms_internal_zzcjn : (List) r7.get(Integer.valueOf(intValue3))) {
                            if (zzwF().zzz(2)) {
                                zzwF().zzyD().zzd("Evaluating filter. audience, filter, event", Integer.valueOf(intValue3), com_google_android_gms_internal_zzcjn.zzbuM, zzwA().zzdW(com_google_android_gms_internal_zzcjn.zzbuN));
                                zzwF().zzyD().zzj("Filter definition", zzwA().zza(com_google_android_gms_internal_zzcjn));
                            }
                            if (com_google_android_gms_internal_zzcjn.zzbuM == null || com_google_android_gms_internal_zzcjn.zzbuM.intValue() > 256) {
                                zzwF().zzyz().zze("Invalid event filter ID. appId, id", zzcfl.zzdZ(str), String.valueOf(com_google_android_gms_internal_zzcjn.zzbuM));
                            } else if (bitSet.get(com_google_android_gms_internal_zzcjn.zzbuM.intValue())) {
                                zzwF().zzyD().zze("Event filter already evaluated true. audience ID, filter ID", Integer.valueOf(intValue3), com_google_android_gms_internal_zzcjn.zzbuM);
                            } else {
                                Object obj;
                                Boolean zza = zza(com_google_android_gms_internal_zzcjn, com_google_android_gms_internal_zzcjw, j);
                                zzcfn zzyD = zzwF().zzyD();
                                String str2 = "Event filter result";
                                if (zza == null) {
                                    obj = "null";
                                } else {
                                    Boolean bool = zza;
                                }
                                zzyD.zzj(str2, obj);
                                if (zza == null) {
                                    hashSet.add(Integer.valueOf(intValue3));
                                } else {
                                    bitSet2.set(com_google_android_gms_internal_zzcjn.zzbuM.intValue());
                                    if (zza.booleanValue()) {
                                        bitSet.set(com_google_android_gms_internal_zzcjn.zzbuM.intValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (com_google_android_gms_internal_zzckbArr != null) {
            Map arrayMap5 = new ArrayMap();
            for (zzckb com_google_android_gms_internal_zzckb : com_google_android_gms_internal_zzckbArr) {
                map = (Map) arrayMap5.get(com_google_android_gms_internal_zzckb.name);
                if (map == null) {
                    map = zzwz().zzK(str, com_google_android_gms_internal_zzckb.name);
                    if (map == null) {
                        map = new ArrayMap();
                    }
                    arrayMap5.put(com_google_android_gms_internal_zzckb.name, map);
                    map2 = map;
                } else {
                    map2 = map;
                }
                for (Integer intValue222 : r7.keySet()) {
                    int intValue4 = intValue222.intValue();
                    if (hashSet.contains(Integer.valueOf(intValue4))) {
                        zzwF().zzyD().zzj("Skipping failed audience ID", Integer.valueOf(intValue4));
                    } else {
                        bitSet = (BitSet) arrayMap2.get(Integer.valueOf(intValue4));
                        bitSet2 = (BitSet) arrayMap3.get(Integer.valueOf(intValue4));
                        if (((zzcjv) arrayMap.get(Integer.valueOf(intValue4))) == null) {
                            com_google_android_gms_internal_zzcjv2 = new zzcjv();
                            arrayMap.put(Integer.valueOf(intValue4), com_google_android_gms_internal_zzcjv2);
                            com_google_android_gms_internal_zzcjv2.zzbvu = Boolean.valueOf(true);
                            bitSet = new BitSet();
                            arrayMap2.put(Integer.valueOf(intValue4), bitSet);
                            bitSet2 = new BitSet();
                            arrayMap3.put(Integer.valueOf(intValue4), bitSet2);
                        }
                        for (zzcjq com_google_android_gms_internal_zzcjq : (List) r7.get(Integer.valueOf(intValue4))) {
                            if (zzwF().zzz(2)) {
                                zzwF().zzyD().zzd("Evaluating filter. audience, filter, property", Integer.valueOf(intValue4), com_google_android_gms_internal_zzcjq.zzbuM, zzwA().zzdY(com_google_android_gms_internal_zzcjq.zzbvc));
                                zzwF().zzyD().zzj("Filter definition", zzwA().zza(com_google_android_gms_internal_zzcjq));
                            }
                            if (com_google_android_gms_internal_zzcjq.zzbuM == null || com_google_android_gms_internal_zzcjq.zzbuM.intValue() > 256) {
                                zzwF().zzyz().zze("Invalid property filter ID. appId, id", zzcfl.zzdZ(str), String.valueOf(com_google_android_gms_internal_zzcjq.zzbuM));
                                hashSet.add(Integer.valueOf(intValue4));
                                break;
                            } else if (bitSet.get(com_google_android_gms_internal_zzcjq.zzbuM.intValue())) {
                                zzwF().zzyD().zze("Property filter already evaluated true. audience ID, filter ID", Integer.valueOf(intValue4), com_google_android_gms_internal_zzcjq.zzbuM);
                            } else {
                                Object obj2;
                                zzcjo com_google_android_gms_internal_zzcjo = com_google_android_gms_internal_zzcjq.zzbvd;
                                if (com_google_android_gms_internal_zzcjo == null) {
                                    zzwF().zzyz().zzj("Missing property filter. property", zzwA().zzdY(com_google_android_gms_internal_zzckb.name));
                                    bool = null;
                                } else {
                                    boolean equals = Boolean.TRUE.equals(com_google_android_gms_internal_zzcjo.zzbuU);
                                    if (com_google_android_gms_internal_zzckb.zzbvA != null) {
                                        if (com_google_android_gms_internal_zzcjo.zzbuT == null) {
                                            zzwF().zzyz().zzj("No number filter for long property. property", zzwA().zzdY(com_google_android_gms_internal_zzckb.name));
                                            bool = null;
                                        } else {
                                            bool = zza(zza(com_google_android_gms_internal_zzckb.zzbvA.longValue(), com_google_android_gms_internal_zzcjo.zzbuT), equals);
                                        }
                                    } else if (com_google_android_gms_internal_zzckb.zzbuB != null) {
                                        if (com_google_android_gms_internal_zzcjo.zzbuT == null) {
                                            zzwF().zzyz().zzj("No number filter for double property. property", zzwA().zzdY(com_google_android_gms_internal_zzckb.name));
                                            bool = null;
                                        } else {
                                            bool = zza(zza(com_google_android_gms_internal_zzckb.zzbuB.doubleValue(), com_google_android_gms_internal_zzcjo.zzbuT), equals);
                                        }
                                    } else if (com_google_android_gms_internal_zzckb.zzaIF == null) {
                                        zzwF().zzyz().zzj("User property has no value, property", zzwA().zzdY(com_google_android_gms_internal_zzckb.name));
                                        bool = null;
                                    } else if (com_google_android_gms_internal_zzcjo.zzbuS == null) {
                                        if (com_google_android_gms_internal_zzcjo.zzbuT == null) {
                                            zzwF().zzyz().zzj("No string or number filter defined. property", zzwA().zzdY(com_google_android_gms_internal_zzckb.name));
                                        } else if (zzcjl.zzez(com_google_android_gms_internal_zzckb.zzaIF)) {
                                            bool = zza(zza(com_google_android_gms_internal_zzckb.zzaIF, com_google_android_gms_internal_zzcjo.zzbuT), equals);
                                        } else {
                                            zzwF().zzyz().zze("Invalid user property value for Numeric number filter. property, value", zzwA().zzdY(com_google_android_gms_internal_zzckb.name), com_google_android_gms_internal_zzckb.zzaIF);
                                        }
                                        bool = null;
                                    } else {
                                        bool = zza(zza(com_google_android_gms_internal_zzckb.zzaIF, com_google_android_gms_internal_zzcjo.zzbuS), equals);
                                    }
                                }
                                zzcfn zzyD2 = zzwF().zzyD();
                                String str3 = "Property filter result";
                                if (bool == null) {
                                    obj2 = "null";
                                } else {
                                    zza = bool;
                                }
                                zzyD2.zzj(str3, obj2);
                                if (bool == null) {
                                    hashSet.add(Integer.valueOf(intValue4));
                                } else {
                                    bitSet2.set(com_google_android_gms_internal_zzcjq.zzbuM.intValue());
                                    if (bool.booleanValue()) {
                                        bitSet.set(com_google_android_gms_internal_zzcjq.zzbuM.intValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        zzcjv[] com_google_android_gms_internal_zzcjvArr = new zzcjv[arrayMap2.size()];
        int i2 = 0;
        for (Integer intValue2222 : arrayMap2.keySet()) {
            intValue = intValue2222.intValue();
            if (!hashSet.contains(Integer.valueOf(intValue))) {
                com_google_android_gms_internal_zzcjv2 = (zzcjv) arrayMap.get(Integer.valueOf(intValue));
                com_google_android_gms_internal_zzcjv = com_google_android_gms_internal_zzcjv2 == null ? new zzcjv() : com_google_android_gms_internal_zzcjv2;
                int i3 = i2 + 1;
                com_google_android_gms_internal_zzcjvArr[i2] = com_google_android_gms_internal_zzcjv;
                com_google_android_gms_internal_zzcjv.zzbuI = Integer.valueOf(intValue);
                com_google_android_gms_internal_zzcjv.zzbvs = new zzcka();
                com_google_android_gms_internal_zzcjv.zzbvs.zzbwf = zzcjl.zza((BitSet) arrayMap2.get(Integer.valueOf(intValue)));
                com_google_android_gms_internal_zzcjv.zzbvs.zzbwe = zzcjl.zza((BitSet) arrayMap3.get(Integer.valueOf(intValue)));
                zzcen zzwz = zzwz();
                com_google_android_gms_internal_zzcka = com_google_android_gms_internal_zzcjv.zzbvs;
                zzwz.zzkD();
                zzwz.zzjC();
                zzbo.zzcF(str);
                zzbo.zzu(com_google_android_gms_internal_zzcka);
                try {
                    byte[] bArr = new byte[com_google_android_gms_internal_zzcka.zzLV()];
                    adh zzc = adh.zzc(bArr, 0, bArr.length);
                    com_google_android_gms_internal_zzcka.zza(zzc);
                    zzc.zzLM();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("app_id", str);
                    contentValues.put("audience_id", Integer.valueOf(intValue));
                    contentValues.put("current_results", bArr);
                    try {
                        if (zzwz.getWritableDatabase().insertWithOnConflict("audience_filter_values", null, contentValues, 5) == -1) {
                            zzwz.zzwF().zzyx().zzj("Failed to insert filter results (got -1). appId", zzcfl.zzdZ(str));
                        }
                        i2 = i3;
                    } catch (SQLiteException e) {
                        zzwz.zzwF().zzyx().zze("Error storing filter results. appId", zzcfl.zzdZ(str), e);
                        i2 = i3;
                    }
                } catch (IOException e2) {
                    zzwz.zzwF().zzyx().zze("Configuration loss. Failed to serialize filter results. appId", zzcfl.zzdZ(str), e2);
                    i2 = i3;
                }
            }
        }
        return (zzcjv[]) Arrays.copyOf(com_google_android_gms_internal_zzcjvArr, i2);
    }

    protected final void zzjD() {
    }
}

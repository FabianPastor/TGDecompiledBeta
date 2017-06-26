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

final class zzcei extends zzchi {
    zzcei(zzcgk com_google_android_gms_internal_zzcgk) {
        super(com_google_android_gms_internal_zzcgk);
    }

    private final Boolean zza(double d, zzcjo com_google_android_gms_internal_zzcjo) {
        try {
            return zza(new BigDecimal(d), com_google_android_gms_internal_zzcjo, Math.ulp(d));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private final Boolean zza(long j, zzcjo com_google_android_gms_internal_zzcjo) {
        try {
            return zza(new BigDecimal(j), com_google_android_gms_internal_zzcjo, 0.0d);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private final Boolean zza(zzcjm com_google_android_gms_internal_zzcjm, zzcjv com_google_android_gms_internal_zzcjv, long j) {
        Boolean zza;
        if (com_google_android_gms_internal_zzcjm.zzbuQ != null) {
            zza = zza(j, com_google_android_gms_internal_zzcjm.zzbuQ);
            if (zza == null) {
                return null;
            }
            if (!zza.booleanValue()) {
                return Boolean.valueOf(false);
            }
        }
        Set hashSet = new HashSet();
        for (zzcjn com_google_android_gms_internal_zzcjn : com_google_android_gms_internal_zzcjm.zzbuO) {
            if (TextUtils.isEmpty(com_google_android_gms_internal_zzcjn.zzbuV)) {
                zzwF().zzyz().zzj("null or empty param name in filter. event", zzwA().zzdW(com_google_android_gms_internal_zzcjv.name));
                return null;
            }
            hashSet.add(com_google_android_gms_internal_zzcjn.zzbuV);
        }
        Map arrayMap = new ArrayMap();
        for (zzcjw com_google_android_gms_internal_zzcjw : com_google_android_gms_internal_zzcjv.zzbvw) {
            if (hashSet.contains(com_google_android_gms_internal_zzcjw.name)) {
                if (com_google_android_gms_internal_zzcjw.zzbvA != null) {
                    arrayMap.put(com_google_android_gms_internal_zzcjw.name, com_google_android_gms_internal_zzcjw.zzbvA);
                } else if (com_google_android_gms_internal_zzcjw.zzbuB != null) {
                    arrayMap.put(com_google_android_gms_internal_zzcjw.name, com_google_android_gms_internal_zzcjw.zzbuB);
                } else if (com_google_android_gms_internal_zzcjw.zzaIF != null) {
                    arrayMap.put(com_google_android_gms_internal_zzcjw.name, com_google_android_gms_internal_zzcjw.zzaIF);
                } else {
                    zzwF().zzyz().zze("Unknown value for param. event, param", zzwA().zzdW(com_google_android_gms_internal_zzcjv.name), zzwA().zzdX(com_google_android_gms_internal_zzcjw.name));
                    return null;
                }
            }
        }
        for (zzcjn com_google_android_gms_internal_zzcjn2 : com_google_android_gms_internal_zzcjm.zzbuO) {
            boolean equals = Boolean.TRUE.equals(com_google_android_gms_internal_zzcjn2.zzbuU);
            String str = com_google_android_gms_internal_zzcjn2.zzbuV;
            if (TextUtils.isEmpty(str)) {
                zzwF().zzyz().zzj("Event has empty param name. event", zzwA().zzdW(com_google_android_gms_internal_zzcjv.name));
                return null;
            }
            Object obj = arrayMap.get(str);
            if (obj instanceof Long) {
                if (com_google_android_gms_internal_zzcjn2.zzbuT == null) {
                    zzwF().zzyz().zze("No number filter for long param. event, param", zzwA().zzdW(com_google_android_gms_internal_zzcjv.name), zzwA().zzdX(str));
                    return null;
                }
                zza = zza(((Long) obj).longValue(), com_google_android_gms_internal_zzcjn2.zzbuT);
                if (zza == null) {
                    return null;
                }
                if (((!zza.booleanValue() ? 1 : 0) ^ equals) != 0) {
                    return Boolean.valueOf(false);
                }
            } else if (obj instanceof Double) {
                if (com_google_android_gms_internal_zzcjn2.zzbuT == null) {
                    zzwF().zzyz().zze("No number filter for double param. event, param", zzwA().zzdW(com_google_android_gms_internal_zzcjv.name), zzwA().zzdX(str));
                    return null;
                }
                zza = zza(((Double) obj).doubleValue(), com_google_android_gms_internal_zzcjn2.zzbuT);
                if (zza == null) {
                    return null;
                }
                if (((!zza.booleanValue() ? 1 : 0) ^ equals) != 0) {
                    return Boolean.valueOf(false);
                }
            } else if (obj instanceof String) {
                if (com_google_android_gms_internal_zzcjn2.zzbuS != null) {
                    zza = zza((String) obj, com_google_android_gms_internal_zzcjn2.zzbuS);
                } else if (com_google_android_gms_internal_zzcjn2.zzbuT == null) {
                    zzwF().zzyz().zze("No filter for String param. event, param", zzwA().zzdW(com_google_android_gms_internal_zzcjv.name), zzwA().zzdX(str));
                    return null;
                } else if (zzcjk.zzez((String) obj)) {
                    zza = zza((String) obj, com_google_android_gms_internal_zzcjn2.zzbuT);
                } else {
                    zzwF().zzyz().zze("Invalid param value for number filter. event, param", zzwA().zzdW(com_google_android_gms_internal_zzcjv.name), zzwA().zzdX(str));
                    return null;
                }
                if (zza == null) {
                    return null;
                }
                if (((!zza.booleanValue() ? 1 : 0) ^ equals) != 0) {
                    return Boolean.valueOf(false);
                }
            } else if (obj == null) {
                zzwF().zzyD().zze("Missing param for filter. event, param", zzwA().zzdW(com_google_android_gms_internal_zzcjv.name), zzwA().zzdX(str));
                return Boolean.valueOf(false);
            } else {
                zzwF().zzyz().zze("Unknown param type. event, param", zzwA().zzdW(com_google_android_gms_internal_zzcjv.name), zzwA().zzdX(str));
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

    private final Boolean zza(String str, zzcjo com_google_android_gms_internal_zzcjo) {
        Boolean bool = null;
        if (zzcjk.zzez(str)) {
            try {
                bool = zza(new BigDecimal(str), com_google_android_gms_internal_zzcjo, 0.0d);
            } catch (NumberFormatException e) {
            }
        }
        return bool;
    }

    private final Boolean zza(String str, zzcjq com_google_android_gms_internal_zzcjq) {
        int i = 0;
        String str2 = null;
        zzbo.zzu(com_google_android_gms_internal_zzcjq);
        if (str == null || com_google_android_gms_internal_zzcjq.zzbve == null || com_google_android_gms_internal_zzcjq.zzbve.intValue() == 0) {
            return null;
        }
        List list;
        if (com_google_android_gms_internal_zzcjq.zzbve.intValue() == 6) {
            if (com_google_android_gms_internal_zzcjq.zzbvh == null || com_google_android_gms_internal_zzcjq.zzbvh.length == 0) {
                return null;
            }
        } else if (com_google_android_gms_internal_zzcjq.zzbvf == null) {
            return null;
        }
        int intValue = com_google_android_gms_internal_zzcjq.zzbve.intValue();
        boolean z = com_google_android_gms_internal_zzcjq.zzbvg != null && com_google_android_gms_internal_zzcjq.zzbvg.booleanValue();
        String toUpperCase = (z || intValue == 1 || intValue == 6) ? com_google_android_gms_internal_zzcjq.zzbvf : com_google_android_gms_internal_zzcjq.zzbvf.toUpperCase(Locale.ENGLISH);
        if (com_google_android_gms_internal_zzcjq.zzbvh == null) {
            list = null;
        } else {
            String[] strArr = com_google_android_gms_internal_zzcjq.zzbvh;
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
    private static Boolean zza(BigDecimal bigDecimal, zzcjo com_google_android_gms_internal_zzcjo, double d) {
        boolean z = true;
        zzbo.zzu(com_google_android_gms_internal_zzcjo);
        if (com_google_android_gms_internal_zzcjo.zzbuW == null || com_google_android_gms_internal_zzcjo.zzbuW.intValue() == 0) {
            return null;
        }
        BigDecimal bigDecimal2;
        BigDecimal bigDecimal3;
        if (com_google_android_gms_internal_zzcjo.zzbuW.intValue() == 4) {
            if (com_google_android_gms_internal_zzcjo.zzbuZ == null || com_google_android_gms_internal_zzcjo.zzbva == null) {
                return null;
            }
        } else if (com_google_android_gms_internal_zzcjo.zzbuY == null) {
            return null;
        }
        int intValue = com_google_android_gms_internal_zzcjo.zzbuW.intValue();
        BigDecimal bigDecimal4;
        if (com_google_android_gms_internal_zzcjo.zzbuW.intValue() == 4) {
            if (!zzcjk.zzez(com_google_android_gms_internal_zzcjo.zzbuZ) || !zzcjk.zzez(com_google_android_gms_internal_zzcjo.zzbva)) {
                return null;
            }
            try {
                bigDecimal2 = new BigDecimal(com_google_android_gms_internal_zzcjo.zzbuZ);
                bigDecimal4 = new BigDecimal(com_google_android_gms_internal_zzcjo.zzbva);
                bigDecimal3 = null;
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (!zzcjk.zzez(com_google_android_gms_internal_zzcjo.zzbuY)) {
            return null;
        } else {
            try {
                bigDecimal2 = null;
                bigDecimal3 = new BigDecimal(com_google_android_gms_internal_zzcjo.zzbuY);
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
    final zzcju[] zza(String str, zzcjv[] com_google_android_gms_internal_zzcjvArr, zzcka[] com_google_android_gms_internal_zzckaArr) {
        int intValue;
        zzcjz com_google_android_gms_internal_zzcjz;
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
                com_google_android_gms_internal_zzcjz = (zzcjz) zzdT.get(Integer.valueOf(intValue));
                bitSet = (BitSet) arrayMap2.get(Integer.valueOf(intValue));
                bitSet2 = (BitSet) arrayMap3.get(Integer.valueOf(intValue));
                if (bitSet == null) {
                    bitSet = new BitSet();
                    arrayMap2.put(Integer.valueOf(intValue), bitSet);
                    bitSet2 = new BitSet();
                    arrayMap3.put(Integer.valueOf(intValue), bitSet2);
                }
                for (int i = 0; i < (com_google_android_gms_internal_zzcjz.zzbwe.length << 6); i++) {
                    if (zzcjk.zza(com_google_android_gms_internal_zzcjz.zzbwe, i)) {
                        zzwF().zzyD().zze("Filter already evaluated. audience ID, filter ID", Integer.valueOf(intValue), Integer.valueOf(i));
                        bitSet2.set(i);
                        if (zzcjk.zza(com_google_android_gms_internal_zzcjz.zzbwf, i)) {
                            bitSet.set(i);
                        }
                    }
                }
                zzcju com_google_android_gms_internal_zzcju = new zzcju();
                arrayMap.put(Integer.valueOf(intValue), com_google_android_gms_internal_zzcju);
                com_google_android_gms_internal_zzcju.zzbvu = Boolean.valueOf(false);
                com_google_android_gms_internal_zzcju.zzbvt = com_google_android_gms_internal_zzcjz;
                com_google_android_gms_internal_zzcju.zzbvs = new zzcjz();
                com_google_android_gms_internal_zzcju.zzbvs.zzbwf = zzcjk.zza(bitSet);
                com_google_android_gms_internal_zzcju.zzbvs.zzbwe = zzcjk.zza(bitSet2);
            }
        }
        if (com_google_android_gms_internal_zzcjvArr != null) {
            ArrayMap arrayMap4 = new ArrayMap();
            for (zzcjv com_google_android_gms_internal_zzcjv : com_google_android_gms_internal_zzcjvArr) {
                zzceu com_google_android_gms_internal_zzceu;
                zzceu zzE = zzwz().zzE(str, com_google_android_gms_internal_zzcjv.name);
                if (zzE == null) {
                    zzwF().zzyz().zze("Event aggregate wasn't created during raw event logging. appId, event", zzcfk.zzdZ(str), zzwA().zzdW(com_google_android_gms_internal_zzcjv.name));
                    com_google_android_gms_internal_zzceu = new zzceu(str, com_google_android_gms_internal_zzcjv.name, 1, 1, com_google_android_gms_internal_zzcjv.zzbvx.longValue());
                } else {
                    com_google_android_gms_internal_zzceu = zzE.zzys();
                }
                zzwz().zza(com_google_android_gms_internal_zzceu);
                long j = com_google_android_gms_internal_zzceu.zzbpG;
                map = (Map) arrayMap4.get(com_google_android_gms_internal_zzcjv.name);
                if (map == null) {
                    map = zzwz().zzJ(str, com_google_android_gms_internal_zzcjv.name);
                    if (map == null) {
                        map = new ArrayMap();
                    }
                    arrayMap4.put(com_google_android_gms_internal_zzcjv.name, map);
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
                        if (((zzcju) arrayMap.get(Integer.valueOf(intValue3))) == null) {
                            zzcju com_google_android_gms_internal_zzcju2 = new zzcju();
                            arrayMap.put(Integer.valueOf(intValue3), com_google_android_gms_internal_zzcju2);
                            com_google_android_gms_internal_zzcju2.zzbvu = Boolean.valueOf(true);
                            bitSet = new BitSet();
                            arrayMap2.put(Integer.valueOf(intValue3), bitSet);
                            bitSet2 = new BitSet();
                            arrayMap3.put(Integer.valueOf(intValue3), bitSet2);
                        }
                        for (zzcjm com_google_android_gms_internal_zzcjm : (List) r7.get(Integer.valueOf(intValue3))) {
                            if (zzwF().zzz(2)) {
                                zzwF().zzyD().zzd("Evaluating filter. audience, filter, event", Integer.valueOf(intValue3), com_google_android_gms_internal_zzcjm.zzbuM, zzwA().zzdW(com_google_android_gms_internal_zzcjm.zzbuN));
                                zzwF().zzyD().zzj("Filter definition", zzwA().zza(com_google_android_gms_internal_zzcjm));
                            }
                            if (com_google_android_gms_internal_zzcjm.zzbuM == null || com_google_android_gms_internal_zzcjm.zzbuM.intValue() > 256) {
                                zzwF().zzyz().zze("Invalid event filter ID. appId, id", zzcfk.zzdZ(str), String.valueOf(com_google_android_gms_internal_zzcjm.zzbuM));
                            } else if (bitSet.get(com_google_android_gms_internal_zzcjm.zzbuM.intValue())) {
                                zzwF().zzyD().zze("Event filter already evaluated true. audience ID, filter ID", Integer.valueOf(intValue3), com_google_android_gms_internal_zzcjm.zzbuM);
                            } else {
                                Object obj;
                                Boolean zza = zza(com_google_android_gms_internal_zzcjm, com_google_android_gms_internal_zzcjv, j);
                                zzcfm zzyD = zzwF().zzyD();
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
                                    bitSet2.set(com_google_android_gms_internal_zzcjm.zzbuM.intValue());
                                    if (zza.booleanValue()) {
                                        bitSet.set(com_google_android_gms_internal_zzcjm.zzbuM.intValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (com_google_android_gms_internal_zzckaArr != null) {
            Map arrayMap5 = new ArrayMap();
            for (zzcka com_google_android_gms_internal_zzcka : com_google_android_gms_internal_zzckaArr) {
                map = (Map) arrayMap5.get(com_google_android_gms_internal_zzcka.name);
                if (map == null) {
                    map = zzwz().zzK(str, com_google_android_gms_internal_zzcka.name);
                    if (map == null) {
                        map = new ArrayMap();
                    }
                    arrayMap5.put(com_google_android_gms_internal_zzcka.name, map);
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
                        if (((zzcju) arrayMap.get(Integer.valueOf(intValue4))) == null) {
                            com_google_android_gms_internal_zzcju2 = new zzcju();
                            arrayMap.put(Integer.valueOf(intValue4), com_google_android_gms_internal_zzcju2);
                            com_google_android_gms_internal_zzcju2.zzbvu = Boolean.valueOf(true);
                            bitSet = new BitSet();
                            arrayMap2.put(Integer.valueOf(intValue4), bitSet);
                            bitSet2 = new BitSet();
                            arrayMap3.put(Integer.valueOf(intValue4), bitSet2);
                        }
                        for (zzcjp com_google_android_gms_internal_zzcjp : (List) r7.get(Integer.valueOf(intValue4))) {
                            if (zzwF().zzz(2)) {
                                zzwF().zzyD().zzd("Evaluating filter. audience, filter, property", Integer.valueOf(intValue4), com_google_android_gms_internal_zzcjp.zzbuM, zzwA().zzdY(com_google_android_gms_internal_zzcjp.zzbvc));
                                zzwF().zzyD().zzj("Filter definition", zzwA().zza(com_google_android_gms_internal_zzcjp));
                            }
                            if (com_google_android_gms_internal_zzcjp.zzbuM == null || com_google_android_gms_internal_zzcjp.zzbuM.intValue() > 256) {
                                zzwF().zzyz().zze("Invalid property filter ID. appId, id", zzcfk.zzdZ(str), String.valueOf(com_google_android_gms_internal_zzcjp.zzbuM));
                                hashSet.add(Integer.valueOf(intValue4));
                                break;
                            } else if (bitSet.get(com_google_android_gms_internal_zzcjp.zzbuM.intValue())) {
                                zzwF().zzyD().zze("Property filter already evaluated true. audience ID, filter ID", Integer.valueOf(intValue4), com_google_android_gms_internal_zzcjp.zzbuM);
                            } else {
                                Object obj2;
                                zzcjn com_google_android_gms_internal_zzcjn = com_google_android_gms_internal_zzcjp.zzbvd;
                                if (com_google_android_gms_internal_zzcjn == null) {
                                    zzwF().zzyz().zzj("Missing property filter. property", zzwA().zzdY(com_google_android_gms_internal_zzcka.name));
                                    bool = null;
                                } else {
                                    boolean equals = Boolean.TRUE.equals(com_google_android_gms_internal_zzcjn.zzbuU);
                                    if (com_google_android_gms_internal_zzcka.zzbvA != null) {
                                        if (com_google_android_gms_internal_zzcjn.zzbuT == null) {
                                            zzwF().zzyz().zzj("No number filter for long property. property", zzwA().zzdY(com_google_android_gms_internal_zzcka.name));
                                            bool = null;
                                        } else {
                                            bool = zza(zza(com_google_android_gms_internal_zzcka.zzbvA.longValue(), com_google_android_gms_internal_zzcjn.zzbuT), equals);
                                        }
                                    } else if (com_google_android_gms_internal_zzcka.zzbuB != null) {
                                        if (com_google_android_gms_internal_zzcjn.zzbuT == null) {
                                            zzwF().zzyz().zzj("No number filter for double property. property", zzwA().zzdY(com_google_android_gms_internal_zzcka.name));
                                            bool = null;
                                        } else {
                                            bool = zza(zza(com_google_android_gms_internal_zzcka.zzbuB.doubleValue(), com_google_android_gms_internal_zzcjn.zzbuT), equals);
                                        }
                                    } else if (com_google_android_gms_internal_zzcka.zzaIF == null) {
                                        zzwF().zzyz().zzj("User property has no value, property", zzwA().zzdY(com_google_android_gms_internal_zzcka.name));
                                        bool = null;
                                    } else if (com_google_android_gms_internal_zzcjn.zzbuS == null) {
                                        if (com_google_android_gms_internal_zzcjn.zzbuT == null) {
                                            zzwF().zzyz().zzj("No string or number filter defined. property", zzwA().zzdY(com_google_android_gms_internal_zzcka.name));
                                        } else if (zzcjk.zzez(com_google_android_gms_internal_zzcka.zzaIF)) {
                                            bool = zza(zza(com_google_android_gms_internal_zzcka.zzaIF, com_google_android_gms_internal_zzcjn.zzbuT), equals);
                                        } else {
                                            zzwF().zzyz().zze("Invalid user property value for Numeric number filter. property, value", zzwA().zzdY(com_google_android_gms_internal_zzcka.name), com_google_android_gms_internal_zzcka.zzaIF);
                                        }
                                        bool = null;
                                    } else {
                                        bool = zza(zza(com_google_android_gms_internal_zzcka.zzaIF, com_google_android_gms_internal_zzcjn.zzbuS), equals);
                                    }
                                }
                                zzcfm zzyD2 = zzwF().zzyD();
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
                                    bitSet2.set(com_google_android_gms_internal_zzcjp.zzbuM.intValue());
                                    if (bool.booleanValue()) {
                                        bitSet.set(com_google_android_gms_internal_zzcjp.zzbuM.intValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        zzcju[] com_google_android_gms_internal_zzcjuArr = new zzcju[arrayMap2.size()];
        int i2 = 0;
        for (Integer intValue2222 : arrayMap2.keySet()) {
            intValue = intValue2222.intValue();
            if (!hashSet.contains(Integer.valueOf(intValue))) {
                com_google_android_gms_internal_zzcju2 = (zzcju) arrayMap.get(Integer.valueOf(intValue));
                com_google_android_gms_internal_zzcju = com_google_android_gms_internal_zzcju2 == null ? new zzcju() : com_google_android_gms_internal_zzcju2;
                int i3 = i2 + 1;
                com_google_android_gms_internal_zzcjuArr[i2] = com_google_android_gms_internal_zzcju;
                com_google_android_gms_internal_zzcju.zzbuI = Integer.valueOf(intValue);
                com_google_android_gms_internal_zzcju.zzbvs = new zzcjz();
                com_google_android_gms_internal_zzcju.zzbvs.zzbwf = zzcjk.zza((BitSet) arrayMap2.get(Integer.valueOf(intValue)));
                com_google_android_gms_internal_zzcju.zzbvs.zzbwe = zzcjk.zza((BitSet) arrayMap3.get(Integer.valueOf(intValue)));
                zzcem zzwz = zzwz();
                com_google_android_gms_internal_zzcjz = com_google_android_gms_internal_zzcju.zzbvs;
                zzwz.zzkD();
                zzwz.zzjC();
                zzbo.zzcF(str);
                zzbo.zzu(com_google_android_gms_internal_zzcjz);
                try {
                    byte[] bArr = new byte[com_google_android_gms_internal_zzcjz.zzLT()];
                    acy zzc = acy.zzc(bArr, 0, bArr.length);
                    com_google_android_gms_internal_zzcjz.zza(zzc);
                    zzc.zzLK();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("app_id", str);
                    contentValues.put("audience_id", Integer.valueOf(intValue));
                    contentValues.put("current_results", bArr);
                    try {
                        if (zzwz.getWritableDatabase().insertWithOnConflict("audience_filter_values", null, contentValues, 5) == -1) {
                            zzwz.zzwF().zzyx().zzj("Failed to insert filter results (got -1). appId", zzcfk.zzdZ(str));
                        }
                        i2 = i3;
                    } catch (SQLiteException e) {
                        zzwz.zzwF().zzyx().zze("Error storing filter results. appId", zzcfk.zzdZ(str), e);
                        i2 = i3;
                    }
                } catch (IOException e2) {
                    zzwz.zzwF().zzyx().zze("Configuration loss. Failed to serialize filter results. appId", zzcfk.zzdZ(str), e2);
                    i2 = i3;
                }
            }
        }
        return (zzcju[]) Arrays.copyOf(com_google_android_gms_internal_zzcjuArr, i2);
    }

    protected final void zzjD() {
    }
}

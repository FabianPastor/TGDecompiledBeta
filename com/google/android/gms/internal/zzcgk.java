package com.google.android.gms.internal;

import android.content.ContentValues;
import android.database.sqlite.SQLiteException;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbq;
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

final class zzcgk extends zzcjl {
    zzcgk(zzcim com_google_android_gms_internal_zzcim) {
        super(com_google_android_gms_internal_zzcim);
    }

    private final Boolean zza(double d, zzclu com_google_android_gms_internal_zzclu) {
        try {
            return zza(new BigDecimal(d), com_google_android_gms_internal_zzclu, Math.ulp(d));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private final Boolean zza(long j, zzclu com_google_android_gms_internal_zzclu) {
        try {
            return zza(new BigDecimal(j), com_google_android_gms_internal_zzclu, 0.0d);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private final Boolean zza(zzcls com_google_android_gms_internal_zzcls, zzcmb com_google_android_gms_internal_zzcmb, long j) {
        Boolean zza;
        if (com_google_android_gms_internal_zzcls.zzjka != null) {
            zza = zza(j, com_google_android_gms_internal_zzcls.zzjka);
            if (zza == null) {
                return null;
            }
            if (!zza.booleanValue()) {
                return Boolean.valueOf(false);
            }
        }
        Set hashSet = new HashSet();
        for (zzclt com_google_android_gms_internal_zzclt : com_google_android_gms_internal_zzcls.zzjjy) {
            if (TextUtils.isEmpty(com_google_android_gms_internal_zzclt.zzjkf)) {
                zzawy().zzazf().zzj("null or empty param name in filter. event", zzawt().zzjh(com_google_android_gms_internal_zzcmb.name));
                return null;
            }
            hashSet.add(com_google_android_gms_internal_zzclt.zzjkf);
        }
        Map arrayMap = new ArrayMap();
        for (zzcmc com_google_android_gms_internal_zzcmc : com_google_android_gms_internal_zzcmb.zzjlh) {
            if (hashSet.contains(com_google_android_gms_internal_zzcmc.name)) {
                if (com_google_android_gms_internal_zzcmc.zzjll != null) {
                    arrayMap.put(com_google_android_gms_internal_zzcmc.name, com_google_android_gms_internal_zzcmc.zzjll);
                } else if (com_google_android_gms_internal_zzcmc.zzjjl != null) {
                    arrayMap.put(com_google_android_gms_internal_zzcmc.name, com_google_android_gms_internal_zzcmc.zzjjl);
                } else if (com_google_android_gms_internal_zzcmc.zzgcc != null) {
                    arrayMap.put(com_google_android_gms_internal_zzcmc.name, com_google_android_gms_internal_zzcmc.zzgcc);
                } else {
                    zzawy().zzazf().zze("Unknown value for param. event, param", zzawt().zzjh(com_google_android_gms_internal_zzcmb.name), zzawt().zzji(com_google_android_gms_internal_zzcmc.name));
                    return null;
                }
            }
        }
        for (zzclt com_google_android_gms_internal_zzclt2 : com_google_android_gms_internal_zzcls.zzjjy) {
            boolean equals = Boolean.TRUE.equals(com_google_android_gms_internal_zzclt2.zzjke);
            String str = com_google_android_gms_internal_zzclt2.zzjkf;
            if (TextUtils.isEmpty(str)) {
                zzawy().zzazf().zzj("Event has empty param name. event", zzawt().zzjh(com_google_android_gms_internal_zzcmb.name));
                return null;
            }
            Object obj = arrayMap.get(str);
            if (obj instanceof Long) {
                if (com_google_android_gms_internal_zzclt2.zzjkd == null) {
                    zzawy().zzazf().zze("No number filter for long param. event, param", zzawt().zzjh(com_google_android_gms_internal_zzcmb.name), zzawt().zzji(str));
                    return null;
                }
                zza = zza(((Long) obj).longValue(), com_google_android_gms_internal_zzclt2.zzjkd);
                if (zza == null) {
                    return null;
                }
                if (((!zza.booleanValue() ? 1 : 0) ^ equals) != 0) {
                    return Boolean.valueOf(false);
                }
            } else if (obj instanceof Double) {
                if (com_google_android_gms_internal_zzclt2.zzjkd == null) {
                    zzawy().zzazf().zze("No number filter for double param. event, param", zzawt().zzjh(com_google_android_gms_internal_zzcmb.name), zzawt().zzji(str));
                    return null;
                }
                zza = zza(((Double) obj).doubleValue(), com_google_android_gms_internal_zzclt2.zzjkd);
                if (zza == null) {
                    return null;
                }
                if (((!zza.booleanValue() ? 1 : 0) ^ equals) != 0) {
                    return Boolean.valueOf(false);
                }
            } else if (obj instanceof String) {
                if (com_google_android_gms_internal_zzclt2.zzjkc != null) {
                    zza = zza((String) obj, com_google_android_gms_internal_zzclt2.zzjkc);
                } else if (com_google_android_gms_internal_zzclt2.zzjkd == null) {
                    zzawy().zzazf().zze("No filter for String param. event, param", zzawt().zzjh(com_google_android_gms_internal_zzcmb.name), zzawt().zzji(str));
                    return null;
                } else if (zzclq.zzkk((String) obj)) {
                    zza = zza((String) obj, com_google_android_gms_internal_zzclt2.zzjkd);
                } else {
                    zzawy().zzazf().zze("Invalid param value for number filter. event, param", zzawt().zzjh(com_google_android_gms_internal_zzcmb.name), zzawt().zzji(str));
                    return null;
                }
                if (zza == null) {
                    return null;
                }
                if (((!zza.booleanValue() ? 1 : 0) ^ equals) != 0) {
                    return Boolean.valueOf(false);
                }
            } else if (obj == null) {
                zzawy().zzazj().zze("Missing param for filter. event, param", zzawt().zzjh(com_google_android_gms_internal_zzcmb.name), zzawt().zzji(str));
                return Boolean.valueOf(false);
            } else {
                zzawy().zzazf().zze("Unknown param type. event, param", zzawt().zzjh(com_google_android_gms_internal_zzcmb.name), zzawt().zzji(str));
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
                    zzawy().zzazf().zzj("Invalid regular expression in REGEXP audience filter. expression", str3);
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

    private final Boolean zza(String str, zzclu com_google_android_gms_internal_zzclu) {
        Boolean bool = null;
        if (zzclq.zzkk(str)) {
            try {
                bool = zza(new BigDecimal(str), com_google_android_gms_internal_zzclu, 0.0d);
            } catch (NumberFormatException e) {
            }
        }
        return bool;
    }

    private final Boolean zza(String str, zzclw com_google_android_gms_internal_zzclw) {
        int i = 0;
        String str2 = null;
        zzbq.checkNotNull(com_google_android_gms_internal_zzclw);
        if (str == null || com_google_android_gms_internal_zzclw.zzjko == null || com_google_android_gms_internal_zzclw.zzjko.intValue() == 0) {
            return null;
        }
        List list;
        if (com_google_android_gms_internal_zzclw.zzjko.intValue() == 6) {
            if (com_google_android_gms_internal_zzclw.zzjkr == null || com_google_android_gms_internal_zzclw.zzjkr.length == 0) {
                return null;
            }
        } else if (com_google_android_gms_internal_zzclw.zzjkp == null) {
            return null;
        }
        int intValue = com_google_android_gms_internal_zzclw.zzjko.intValue();
        boolean z = com_google_android_gms_internal_zzclw.zzjkq != null && com_google_android_gms_internal_zzclw.zzjkq.booleanValue();
        String toUpperCase = (z || intValue == 1 || intValue == 6) ? com_google_android_gms_internal_zzclw.zzjkp : com_google_android_gms_internal_zzclw.zzjkp.toUpperCase(Locale.ENGLISH);
        if (com_google_android_gms_internal_zzclw.zzjkr == null) {
            list = null;
        } else {
            String[] strArr = com_google_android_gms_internal_zzclw.zzjkr;
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
    private static Boolean zza(BigDecimal bigDecimal, zzclu com_google_android_gms_internal_zzclu, double d) {
        boolean z = true;
        zzbq.checkNotNull(com_google_android_gms_internal_zzclu);
        if (com_google_android_gms_internal_zzclu.zzjkg == null || com_google_android_gms_internal_zzclu.zzjkg.intValue() == 0) {
            return null;
        }
        BigDecimal bigDecimal2;
        BigDecimal bigDecimal3;
        if (com_google_android_gms_internal_zzclu.zzjkg.intValue() == 4) {
            if (com_google_android_gms_internal_zzclu.zzjkj == null || com_google_android_gms_internal_zzclu.zzjkk == null) {
                return null;
            }
        } else if (com_google_android_gms_internal_zzclu.zzjki == null) {
            return null;
        }
        int intValue = com_google_android_gms_internal_zzclu.zzjkg.intValue();
        BigDecimal bigDecimal4;
        if (com_google_android_gms_internal_zzclu.zzjkg.intValue() == 4) {
            if (!zzclq.zzkk(com_google_android_gms_internal_zzclu.zzjkj) || !zzclq.zzkk(com_google_android_gms_internal_zzclu.zzjkk)) {
                return null;
            }
            try {
                bigDecimal2 = new BigDecimal(com_google_android_gms_internal_zzclu.zzjkj);
                bigDecimal4 = new BigDecimal(com_google_android_gms_internal_zzclu.zzjkk);
                bigDecimal3 = null;
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (!zzclq.zzkk(com_google_android_gms_internal_zzclu.zzjki)) {
            return null;
        } else {
            try {
                bigDecimal2 = null;
                bigDecimal3 = new BigDecimal(com_google_android_gms_internal_zzclu.zzjki);
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

    final zzcma[] zza(String str, zzcmb[] com_google_android_gms_internal_zzcmbArr, zzcmg[] com_google_android_gms_internal_zzcmgArr) {
        int intValue;
        BitSet bitSet;
        BitSet bitSet2;
        Map map;
        Map map2;
        zzbq.zzgm(str);
        HashSet hashSet = new HashSet();
        ArrayMap arrayMap = new ArrayMap();
        Map arrayMap2 = new ArrayMap();
        ArrayMap arrayMap3 = new ArrayMap();
        Map zzje = zzaws().zzje(str);
        if (zzje != null) {
            for (Integer intValue2 : zzje.keySet()) {
                intValue = intValue2.intValue();
                zzcmf com_google_android_gms_internal_zzcmf = (zzcmf) zzje.get(Integer.valueOf(intValue));
                bitSet = (BitSet) arrayMap2.get(Integer.valueOf(intValue));
                bitSet2 = (BitSet) arrayMap3.get(Integer.valueOf(intValue));
                if (bitSet == null) {
                    bitSet = new BitSet();
                    arrayMap2.put(Integer.valueOf(intValue), bitSet);
                    bitSet2 = new BitSet();
                    arrayMap3.put(Integer.valueOf(intValue), bitSet2);
                }
                for (int i = 0; i < (com_google_android_gms_internal_zzcmf.zzjmp.length << 6); i++) {
                    if (zzclq.zza(com_google_android_gms_internal_zzcmf.zzjmp, i)) {
                        zzawy().zzazj().zze("Filter already evaluated. audience ID, filter ID", Integer.valueOf(intValue), Integer.valueOf(i));
                        bitSet2.set(i);
                        if (zzclq.zza(com_google_android_gms_internal_zzcmf.zzjmq, i)) {
                            bitSet.set(i);
                        }
                    }
                }
                zzcma com_google_android_gms_internal_zzcma = new zzcma();
                arrayMap.put(Integer.valueOf(intValue), com_google_android_gms_internal_zzcma);
                com_google_android_gms_internal_zzcma.zzjlf = Boolean.valueOf(false);
                com_google_android_gms_internal_zzcma.zzjle = com_google_android_gms_internal_zzcmf;
                com_google_android_gms_internal_zzcma.zzjld = new zzcmf();
                com_google_android_gms_internal_zzcma.zzjld.zzjmq = zzclq.zza(bitSet);
                com_google_android_gms_internal_zzcma.zzjld.zzjmp = zzclq.zza(bitSet2);
            }
        }
        if (com_google_android_gms_internal_zzcmbArr != null) {
            ArrayMap arrayMap4 = new ArrayMap();
            for (zzcmb com_google_android_gms_internal_zzcmb : com_google_android_gms_internal_zzcmbArr) {
                zzcgw com_google_android_gms_internal_zzcgw;
                zzcgw zzae = zzaws().zzae(str, com_google_android_gms_internal_zzcmb.name);
                if (zzae == null) {
                    zzawy().zzazf().zze("Event aggregate wasn't created during raw event logging. appId, event", zzchm.zzjk(str), zzawt().zzjh(com_google_android_gms_internal_zzcmb.name));
                    com_google_android_gms_internal_zzcgw = new zzcgw(str, com_google_android_gms_internal_zzcmb.name, 1, 1, com_google_android_gms_internal_zzcmb.zzjli.longValue(), 0, null, null, null);
                } else {
                    com_google_android_gms_internal_zzcgw = zzae.zzayw();
                }
                zzaws().zza(com_google_android_gms_internal_zzcgw);
                long j = com_google_android_gms_internal_zzcgw.zzizk;
                map = (Map) arrayMap4.get(com_google_android_gms_internal_zzcmb.name);
                if (map == null) {
                    map = zzaws().zzaj(str, com_google_android_gms_internal_zzcmb.name);
                    if (map == null) {
                        map = new ArrayMap();
                    }
                    arrayMap4.put(com_google_android_gms_internal_zzcmb.name, map);
                    map2 = map;
                } else {
                    map2 = map;
                }
                for (Integer intValue22 : r7.keySet()) {
                    int intValue3 = intValue22.intValue();
                    if (hashSet.contains(Integer.valueOf(intValue3))) {
                        zzawy().zzazj().zzj("Skipping failed audience ID", Integer.valueOf(intValue3));
                    } else {
                        bitSet = (BitSet) arrayMap2.get(Integer.valueOf(intValue3));
                        bitSet2 = (BitSet) arrayMap3.get(Integer.valueOf(intValue3));
                        if (((zzcma) arrayMap.get(Integer.valueOf(intValue3))) == null) {
                            zzcma com_google_android_gms_internal_zzcma2 = new zzcma();
                            arrayMap.put(Integer.valueOf(intValue3), com_google_android_gms_internal_zzcma2);
                            com_google_android_gms_internal_zzcma2.zzjlf = Boolean.valueOf(true);
                            bitSet = new BitSet();
                            arrayMap2.put(Integer.valueOf(intValue3), bitSet);
                            bitSet2 = new BitSet();
                            arrayMap3.put(Integer.valueOf(intValue3), bitSet2);
                        }
                        for (zzcls com_google_android_gms_internal_zzcls : (List) r7.get(Integer.valueOf(intValue3))) {
                            if (zzawy().zzae(2)) {
                                zzawy().zzazj().zzd("Evaluating filter. audience, filter, event", Integer.valueOf(intValue3), com_google_android_gms_internal_zzcls.zzjjw, zzawt().zzjh(com_google_android_gms_internal_zzcls.zzjjx));
                                zzawy().zzazj().zzj("Filter definition", zzawt().zza(com_google_android_gms_internal_zzcls));
                            }
                            if (com_google_android_gms_internal_zzcls.zzjjw == null || com_google_android_gms_internal_zzcls.zzjjw.intValue() > 256) {
                                zzawy().zzazf().zze("Invalid event filter ID. appId, id", zzchm.zzjk(str), String.valueOf(com_google_android_gms_internal_zzcls.zzjjw));
                            } else if (bitSet.get(com_google_android_gms_internal_zzcls.zzjjw.intValue())) {
                                zzawy().zzazj().zze("Event filter already evaluated true. audience ID, filter ID", Integer.valueOf(intValue3), com_google_android_gms_internal_zzcls.zzjjw);
                            } else {
                                Object obj;
                                Boolean zza = zza(com_google_android_gms_internal_zzcls, com_google_android_gms_internal_zzcmb, j);
                                zzcho zzazj = zzawy().zzazj();
                                String str2 = "Event filter result";
                                if (zza == null) {
                                    obj = "null";
                                } else {
                                    Boolean bool = zza;
                                }
                                zzazj.zzj(str2, obj);
                                if (zza == null) {
                                    hashSet.add(Integer.valueOf(intValue3));
                                } else {
                                    bitSet2.set(com_google_android_gms_internal_zzcls.zzjjw.intValue());
                                    if (zza.booleanValue()) {
                                        bitSet.set(com_google_android_gms_internal_zzcls.zzjjw.intValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (com_google_android_gms_internal_zzcmgArr != null) {
            Map arrayMap5 = new ArrayMap();
            for (zzcmg com_google_android_gms_internal_zzcmg : com_google_android_gms_internal_zzcmgArr) {
                map = (Map) arrayMap5.get(com_google_android_gms_internal_zzcmg.name);
                if (map == null) {
                    map = zzaws().zzak(str, com_google_android_gms_internal_zzcmg.name);
                    if (map == null) {
                        map = new ArrayMap();
                    }
                    arrayMap5.put(com_google_android_gms_internal_zzcmg.name, map);
                    map2 = map;
                } else {
                    map2 = map;
                }
                for (Integer intValue222 : r7.keySet()) {
                    int intValue4 = intValue222.intValue();
                    if (hashSet.contains(Integer.valueOf(intValue4))) {
                        zzawy().zzazj().zzj("Skipping failed audience ID", Integer.valueOf(intValue4));
                    } else {
                        bitSet = (BitSet) arrayMap2.get(Integer.valueOf(intValue4));
                        bitSet2 = (BitSet) arrayMap3.get(Integer.valueOf(intValue4));
                        if (((zzcma) arrayMap.get(Integer.valueOf(intValue4))) == null) {
                            com_google_android_gms_internal_zzcma2 = new zzcma();
                            arrayMap.put(Integer.valueOf(intValue4), com_google_android_gms_internal_zzcma2);
                            com_google_android_gms_internal_zzcma2.zzjlf = Boolean.valueOf(true);
                            bitSet = new BitSet();
                            arrayMap2.put(Integer.valueOf(intValue4), bitSet);
                            bitSet2 = new BitSet();
                            arrayMap3.put(Integer.valueOf(intValue4), bitSet2);
                        }
                        for (zzclv com_google_android_gms_internal_zzclv : (List) r7.get(Integer.valueOf(intValue4))) {
                            if (zzawy().zzae(2)) {
                                zzawy().zzazj().zzd("Evaluating filter. audience, filter, property", Integer.valueOf(intValue4), com_google_android_gms_internal_zzclv.zzjjw, zzawt().zzjj(com_google_android_gms_internal_zzclv.zzjkm));
                                zzawy().zzazj().zzj("Filter definition", zzawt().zza(com_google_android_gms_internal_zzclv));
                            }
                            if (com_google_android_gms_internal_zzclv.zzjjw == null || com_google_android_gms_internal_zzclv.zzjjw.intValue() > 256) {
                                zzawy().zzazf().zze("Invalid property filter ID. appId, id", zzchm.zzjk(str), String.valueOf(com_google_android_gms_internal_zzclv.zzjjw));
                                hashSet.add(Integer.valueOf(intValue4));
                                break;
                            } else if (bitSet.get(com_google_android_gms_internal_zzclv.zzjjw.intValue())) {
                                zzawy().zzazj().zze("Property filter already evaluated true. audience ID, filter ID", Integer.valueOf(intValue4), com_google_android_gms_internal_zzclv.zzjjw);
                            } else {
                                Object obj2;
                                zzclt com_google_android_gms_internal_zzclt = com_google_android_gms_internal_zzclv.zzjkn;
                                if (com_google_android_gms_internal_zzclt == null) {
                                    zzawy().zzazf().zzj("Missing property filter. property", zzawt().zzjj(com_google_android_gms_internal_zzcmg.name));
                                    bool = null;
                                } else {
                                    boolean equals = Boolean.TRUE.equals(com_google_android_gms_internal_zzclt.zzjke);
                                    if (com_google_android_gms_internal_zzcmg.zzjll != null) {
                                        if (com_google_android_gms_internal_zzclt.zzjkd == null) {
                                            zzawy().zzazf().zzj("No number filter for long property. property", zzawt().zzjj(com_google_android_gms_internal_zzcmg.name));
                                            bool = null;
                                        } else {
                                            bool = zza(zza(com_google_android_gms_internal_zzcmg.zzjll.longValue(), com_google_android_gms_internal_zzclt.zzjkd), equals);
                                        }
                                    } else if (com_google_android_gms_internal_zzcmg.zzjjl != null) {
                                        if (com_google_android_gms_internal_zzclt.zzjkd == null) {
                                            zzawy().zzazf().zzj("No number filter for double property. property", zzawt().zzjj(com_google_android_gms_internal_zzcmg.name));
                                            bool = null;
                                        } else {
                                            bool = zza(zza(com_google_android_gms_internal_zzcmg.zzjjl.doubleValue(), com_google_android_gms_internal_zzclt.zzjkd), equals);
                                        }
                                    } else if (com_google_android_gms_internal_zzcmg.zzgcc == null) {
                                        zzawy().zzazf().zzj("User property has no value, property", zzawt().zzjj(com_google_android_gms_internal_zzcmg.name));
                                        bool = null;
                                    } else if (com_google_android_gms_internal_zzclt.zzjkc == null) {
                                        if (com_google_android_gms_internal_zzclt.zzjkd == null) {
                                            zzawy().zzazf().zzj("No string or number filter defined. property", zzawt().zzjj(com_google_android_gms_internal_zzcmg.name));
                                        } else if (zzclq.zzkk(com_google_android_gms_internal_zzcmg.zzgcc)) {
                                            bool = zza(zza(com_google_android_gms_internal_zzcmg.zzgcc, com_google_android_gms_internal_zzclt.zzjkd), equals);
                                        } else {
                                            zzawy().zzazf().zze("Invalid user property value for Numeric number filter. property, value", zzawt().zzjj(com_google_android_gms_internal_zzcmg.name), com_google_android_gms_internal_zzcmg.zzgcc);
                                        }
                                        bool = null;
                                    } else {
                                        bool = zza(zza(com_google_android_gms_internal_zzcmg.zzgcc, com_google_android_gms_internal_zzclt.zzjkc), equals);
                                    }
                                }
                                zzcho zzazj2 = zzawy().zzazj();
                                String str3 = "Property filter result";
                                if (bool == null) {
                                    obj2 = "null";
                                } else {
                                    zza = bool;
                                }
                                zzazj2.zzj(str3, obj2);
                                if (bool == null) {
                                    hashSet.add(Integer.valueOf(intValue4));
                                } else {
                                    bitSet2.set(com_google_android_gms_internal_zzclv.zzjjw.intValue());
                                    if (bool.booleanValue()) {
                                        bitSet.set(com_google_android_gms_internal_zzclv.zzjjw.intValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        zzcma[] com_google_android_gms_internal_zzcmaArr = new zzcma[arrayMap2.size()];
        int i2 = 0;
        for (Integer intValue2222 : arrayMap2.keySet()) {
            intValue = intValue2222.intValue();
            if (!hashSet.contains(Integer.valueOf(intValue))) {
                com_google_android_gms_internal_zzcma2 = (zzcma) arrayMap.get(Integer.valueOf(intValue));
                com_google_android_gms_internal_zzcma = com_google_android_gms_internal_zzcma2 == null ? new zzcma() : com_google_android_gms_internal_zzcma2;
                int i3 = i2 + 1;
                com_google_android_gms_internal_zzcmaArr[i2] = com_google_android_gms_internal_zzcma;
                com_google_android_gms_internal_zzcma.zzjjs = Integer.valueOf(intValue);
                com_google_android_gms_internal_zzcma.zzjld = new zzcmf();
                com_google_android_gms_internal_zzcma.zzjld.zzjmq = zzclq.zza((BitSet) arrayMap2.get(Integer.valueOf(intValue)));
                com_google_android_gms_internal_zzcma.zzjld.zzjmp = zzclq.zza((BitSet) arrayMap3.get(Integer.valueOf(intValue)));
                zzcjk zzaws = zzaws();
                zzfjs com_google_android_gms_internal_zzfjs = com_google_android_gms_internal_zzcma.zzjld;
                zzaws.zzxf();
                zzaws.zzve();
                zzbq.zzgm(str);
                zzbq.checkNotNull(com_google_android_gms_internal_zzfjs);
                try {
                    byte[] bArr = new byte[com_google_android_gms_internal_zzfjs.zzho()];
                    zzfjk zzo = zzfjk.zzo(bArr, 0, bArr.length);
                    com_google_android_gms_internal_zzfjs.zza(zzo);
                    zzo.zzcwt();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("app_id", str);
                    contentValues.put("audience_id", Integer.valueOf(intValue));
                    contentValues.put("current_results", bArr);
                    try {
                        if (zzaws.getWritableDatabase().insertWithOnConflict("audience_filter_values", null, contentValues, 5) == -1) {
                            zzaws.zzawy().zzazd().zzj("Failed to insert filter results (got -1). appId", zzchm.zzjk(str));
                        }
                        i2 = i3;
                    } catch (SQLiteException e) {
                        zzaws.zzawy().zzazd().zze("Error storing filter results. appId", zzchm.zzjk(str), e);
                        i2 = i3;
                    }
                } catch (IOException e2) {
                    zzaws.zzawy().zzazd().zze("Configuration loss. Failed to serialize filter results. appId", zzchm.zzjk(str), e2);
                    i2 = i3;
                }
            }
        }
        return (zzcma[]) Arrays.copyOf(com_google_android_gms_internal_zzcmaArr, i2);
    }

    protected final boolean zzaxz() {
        return false;
    }
}

package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzauf.zza;
import com.google.android.gms.internal.zzauf.zzb;
import com.google.android.gms.internal.zzauf.zzc;
import com.google.android.gms.internal.zzauf.zzd;
import com.google.android.gms.internal.zzauf.zze;
import com.google.android.gms.internal.zzauf.zzf;
import com.google.android.gms.internal.zzauh.zzg;
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

class zzass extends zzats {
    zzass(zzatp com_google_android_gms_internal_zzatp) {
        super(com_google_android_gms_internal_zzatp);
    }

    private Boolean zza(zzb com_google_android_gms_internal_zzauf_zzb, zzauh.zzb com_google_android_gms_internal_zzauh_zzb, long j) {
        Boolean zza;
        if (com_google_android_gms_internal_zzauf_zzb.zzbvp != null) {
            zza = zza(j, com_google_android_gms_internal_zzauf_zzb.zzbvp);
            if (zza == null) {
                return null;
            }
            if (!zza.booleanValue()) {
                return Boolean.valueOf(false);
            }
        }
        Set hashSet = new HashSet();
        for (zzc com_google_android_gms_internal_zzauf_zzc : com_google_android_gms_internal_zzauf_zzb.zzbvn) {
            if (TextUtils.isEmpty(com_google_android_gms_internal_zzauf_zzc.zzbvu)) {
                zzJt().zzLc().zzj("null or empty param name in filter. event", com_google_android_gms_internal_zzauh_zzb.name);
                return null;
            }
            hashSet.add(com_google_android_gms_internal_zzauf_zzc.zzbvu);
        }
        Map arrayMap = new ArrayMap();
        for (zzauh.zzc com_google_android_gms_internal_zzauh_zzc : com_google_android_gms_internal_zzauh_zzb.zzbvV) {
            if (hashSet.contains(com_google_android_gms_internal_zzauh_zzc.name)) {
                if (com_google_android_gms_internal_zzauh_zzc.zzbvZ != null) {
                    arrayMap.put(com_google_android_gms_internal_zzauh_zzc.name, com_google_android_gms_internal_zzauh_zzc.zzbvZ);
                } else if (com_google_android_gms_internal_zzauh_zzc.zzbvc != null) {
                    arrayMap.put(com_google_android_gms_internal_zzauh_zzc.name, com_google_android_gms_internal_zzauh_zzc.zzbvc);
                } else if (com_google_android_gms_internal_zzauh_zzc.zzaFy != null) {
                    arrayMap.put(com_google_android_gms_internal_zzauh_zzc.name, com_google_android_gms_internal_zzauh_zzc.zzaFy);
                } else {
                    zzJt().zzLc().zze("Unknown value for param. event, param", com_google_android_gms_internal_zzauh_zzb.name, com_google_android_gms_internal_zzauh_zzc.name);
                    return null;
                }
            }
        }
        for (zzc com_google_android_gms_internal_zzauf_zzc2 : com_google_android_gms_internal_zzauf_zzb.zzbvn) {
            boolean equals = Boolean.TRUE.equals(com_google_android_gms_internal_zzauf_zzc2.zzbvt);
            CharSequence charSequence = com_google_android_gms_internal_zzauf_zzc2.zzbvu;
            if (TextUtils.isEmpty(charSequence)) {
                zzJt().zzLc().zzj("Event has empty param name. event", com_google_android_gms_internal_zzauh_zzb.name);
                return null;
            }
            Object obj = arrayMap.get(charSequence);
            if (obj instanceof Long) {
                if (com_google_android_gms_internal_zzauf_zzc2.zzbvs == null) {
                    zzJt().zzLc().zze("No number filter for long param. event, param", com_google_android_gms_internal_zzauh_zzb.name, charSequence);
                    return null;
                }
                zza = zza(((Long) obj).longValue(), com_google_android_gms_internal_zzauf_zzc2.zzbvs);
                if (zza == null) {
                    return null;
                }
                if (((!zza.booleanValue() ? 1 : 0) ^ equals) != 0) {
                    return Boolean.valueOf(false);
                }
            } else if (obj instanceof Double) {
                if (com_google_android_gms_internal_zzauf_zzc2.zzbvs == null) {
                    zzJt().zzLc().zze("No number filter for double param. event, param", com_google_android_gms_internal_zzauh_zzb.name, charSequence);
                    return null;
                }
                zza = zza(((Double) obj).doubleValue(), com_google_android_gms_internal_zzauf_zzc2.zzbvs);
                if (zza == null) {
                    return null;
                }
                if (((!zza.booleanValue() ? 1 : 0) ^ equals) != 0) {
                    return Boolean.valueOf(false);
                }
            } else if (obj instanceof String) {
                if (com_google_android_gms_internal_zzauf_zzc2.zzbvr == null) {
                    zzJt().zzLc().zze("No string filter for String param. event, param", com_google_android_gms_internal_zzauh_zzb.name, charSequence);
                    return null;
                }
                zza = zza((String) obj, com_google_android_gms_internal_zzauf_zzc2.zzbvr);
                if (zza == null) {
                    return null;
                }
                if (((!zza.booleanValue() ? 1 : 0) ^ equals) != 0) {
                    return Boolean.valueOf(false);
                }
            } else if (obj == null) {
                zzJt().zzLg().zze("Missing param for filter. event, param", com_google_android_gms_internal_zzauh_zzb.name, charSequence);
                return Boolean.valueOf(false);
            } else {
                zzJt().zzLc().zze("Unknown param type. event, param", com_google_android_gms_internal_zzauh_zzb.name, charSequence);
                return null;
            }
        }
        return Boolean.valueOf(true);
    }

    private Boolean zza(zze com_google_android_gms_internal_zzauf_zze, zzg com_google_android_gms_internal_zzauh_zzg) {
        zzc com_google_android_gms_internal_zzauf_zzc = com_google_android_gms_internal_zzauf_zze.zzbvC;
        if (com_google_android_gms_internal_zzauf_zzc == null) {
            zzJt().zzLc().zzj("Missing property filter. property", com_google_android_gms_internal_zzauh_zzg.name);
            return null;
        }
        boolean equals = Boolean.TRUE.equals(com_google_android_gms_internal_zzauf_zzc.zzbvt);
        if (com_google_android_gms_internal_zzauh_zzg.zzbvZ != null) {
            if (com_google_android_gms_internal_zzauf_zzc.zzbvs != null) {
                return zza(zza(com_google_android_gms_internal_zzauh_zzg.zzbvZ.longValue(), com_google_android_gms_internal_zzauf_zzc.zzbvs), equals);
            }
            zzJt().zzLc().zzj("No number filter for long property. property", com_google_android_gms_internal_zzauh_zzg.name);
            return null;
        } else if (com_google_android_gms_internal_zzauh_zzg.zzbvc != null) {
            if (com_google_android_gms_internal_zzauf_zzc.zzbvs != null) {
                return zza(zza(com_google_android_gms_internal_zzauh_zzg.zzbvc.doubleValue(), com_google_android_gms_internal_zzauf_zzc.zzbvs), equals);
            }
            zzJt().zzLc().zzj("No number filter for double property. property", com_google_android_gms_internal_zzauh_zzg.name);
            return null;
        } else if (com_google_android_gms_internal_zzauh_zzg.zzaFy == null) {
            zzJt().zzLc().zzj("User property has no value, property", com_google_android_gms_internal_zzauh_zzg.name);
            return null;
        } else if (com_google_android_gms_internal_zzauf_zzc.zzbvr != null) {
            return zza(zza(com_google_android_gms_internal_zzauh_zzg.zzaFy, com_google_android_gms_internal_zzauf_zzc.zzbvr), equals);
        } else {
            if (com_google_android_gms_internal_zzauf_zzc.zzbvs == null) {
                zzJt().zzLc().zzj("No string or number filter defined. property", com_google_android_gms_internal_zzauh_zzg.name);
                return null;
            } else if (zzaue.zzgi(com_google_android_gms_internal_zzauh_zzg.zzaFy)) {
                return zza(zza(com_google_android_gms_internal_zzauh_zzg.zzaFy, com_google_android_gms_internal_zzauf_zzc.zzbvs), equals);
            } else {
                zzJt().zzLc().zze("Invalid user property value for Numeric number filter. property, value", com_google_android_gms_internal_zzauh_zzg.name, com_google_android_gms_internal_zzauh_zzg.zzaFy);
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

    public Boolean zza(double d, zzd com_google_android_gms_internal_zzauf_zzd) {
        try {
            return zza(new BigDecimal(d), com_google_android_gms_internal_zzauf_zzd, Math.ulp(d));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Boolean zza(long j, zzd com_google_android_gms_internal_zzauf_zzd) {
        try {
            return zza(new BigDecimal(j), com_google_android_gms_internal_zzauf_zzd, 0.0d);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Boolean zza(String str, zzd com_google_android_gms_internal_zzauf_zzd) {
        Boolean bool = null;
        if (zzaue.zzgi(str)) {
            try {
                bool = zza(new BigDecimal(str), com_google_android_gms_internal_zzauf_zzd, 0.0d);
            } catch (NumberFormatException e) {
            }
        }
        return bool;
    }

    Boolean zza(String str, zzf com_google_android_gms_internal_zzauf_zzf) {
        String str2 = null;
        zzac.zzw(com_google_android_gms_internal_zzauf_zzf);
        if (str == null || com_google_android_gms_internal_zzauf_zzf.zzbvD == null || com_google_android_gms_internal_zzauf_zzf.zzbvD.intValue() == 0) {
            return null;
        }
        if (com_google_android_gms_internal_zzauf_zzf.zzbvD.intValue() == 6) {
            if (com_google_android_gms_internal_zzauf_zzf.zzbvG == null || com_google_android_gms_internal_zzauf_zzf.zzbvG.length == 0) {
                return null;
            }
        } else if (com_google_android_gms_internal_zzauf_zzf.zzbvE == null) {
            return null;
        }
        int intValue = com_google_android_gms_internal_zzauf_zzf.zzbvD.intValue();
        boolean z = com_google_android_gms_internal_zzauf_zzf.zzbvF != null && com_google_android_gms_internal_zzauf_zzf.zzbvF.booleanValue();
        String toUpperCase = (z || intValue == 1 || intValue == 6) ? com_google_android_gms_internal_zzauf_zzf.zzbvE : com_google_android_gms_internal_zzauf_zzf.zzbvE.toUpperCase(Locale.ENGLISH);
        List zza = com_google_android_gms_internal_zzauf_zzf.zzbvG == null ? null : zza(com_google_android_gms_internal_zzauf_zzf.zzbvG, z);
        if (intValue == 1) {
            str2 = toUpperCase;
        }
        return zza(str, intValue, z, toUpperCase, zza, str2);
    }

    Boolean zza(BigDecimal bigDecimal, zzd com_google_android_gms_internal_zzauf_zzd, double d) {
        zzac.zzw(com_google_android_gms_internal_zzauf_zzd);
        if (com_google_android_gms_internal_zzauf_zzd.zzbvv == null || com_google_android_gms_internal_zzauf_zzd.zzbvv.intValue() == 0) {
            return null;
        }
        BigDecimal bigDecimal2;
        BigDecimal bigDecimal3;
        BigDecimal bigDecimal4;
        if (com_google_android_gms_internal_zzauf_zzd.zzbvv.intValue() == 4) {
            if (com_google_android_gms_internal_zzauf_zzd.zzbvy == null || com_google_android_gms_internal_zzauf_zzd.zzbvz == null) {
                return null;
            }
        } else if (com_google_android_gms_internal_zzauf_zzd.zzbvx == null) {
            return null;
        }
        int intValue = com_google_android_gms_internal_zzauf_zzd.zzbvv.intValue();
        if (com_google_android_gms_internal_zzauf_zzd.zzbvv.intValue() == 4) {
            if (!zzaue.zzgi(com_google_android_gms_internal_zzauf_zzd.zzbvy) || !zzaue.zzgi(com_google_android_gms_internal_zzauf_zzd.zzbvz)) {
                return null;
            }
            try {
                bigDecimal2 = new BigDecimal(com_google_android_gms_internal_zzauf_zzd.zzbvy);
                bigDecimal3 = new BigDecimal(com_google_android_gms_internal_zzauf_zzd.zzbvz);
                bigDecimal4 = null;
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (!zzaue.zzgi(com_google_android_gms_internal_zzauf_zzd.zzbvx)) {
            return null;
        } else {
            try {
                bigDecimal4 = new BigDecimal(com_google_android_gms_internal_zzauf_zzd.zzbvx);
                bigDecimal3 = null;
                bigDecimal2 = null;
            } catch (NumberFormatException e2) {
                return null;
            }
        }
        return zza(bigDecimal, intValue, bigDecimal4, bigDecimal2, bigDecimal3, d);
    }

    @WorkerThread
    void zza(String str, zza[] com_google_android_gms_internal_zzauf_zzaArr) {
        zzac.zzw(com_google_android_gms_internal_zzauf_zzaArr);
        for (zza com_google_android_gms_internal_zzauf_zza : com_google_android_gms_internal_zzauf_zzaArr) {
            for (zzb com_google_android_gms_internal_zzauf_zzb : com_google_android_gms_internal_zzauf_zza.zzbvj) {
                String str2 = (String) AppMeasurement.zza.zzbpx.get(com_google_android_gms_internal_zzauf_zzb.zzbvm);
                if (str2 != null) {
                    com_google_android_gms_internal_zzauf_zzb.zzbvm = str2;
                }
                for (zzc com_google_android_gms_internal_zzauf_zzc : com_google_android_gms_internal_zzauf_zzb.zzbvn) {
                    str2 = (String) AppMeasurement.zze.zzbpy.get(com_google_android_gms_internal_zzauf_zzc.zzbvu);
                    if (str2 != null) {
                        com_google_android_gms_internal_zzauf_zzc.zzbvu = str2;
                    }
                }
            }
            for (zze com_google_android_gms_internal_zzauf_zze : com_google_android_gms_internal_zzauf_zza.zzbvi) {
                str2 = (String) AppMeasurement.zzg.zzbpC.get(com_google_android_gms_internal_zzauf_zze.zzbvB);
                if (str2 != null) {
                    com_google_android_gms_internal_zzauf_zze.zzbvB = str2;
                }
            }
        }
        zzJo().zzb(str, com_google_android_gms_internal_zzauf_zzaArr);
    }

    @WorkerThread
    zzauh.zza[] zza(String str, zzauh.zzb[] com_google_android_gms_internal_zzauh_zzbArr, zzg[] com_google_android_gms_internal_zzauh_zzgArr) {
        int intValue;
        BitSet bitSet;
        BitSet bitSet2;
        Map map;
        Map map2;
        Boolean zza;
        Object obj;
        zzac.zzdv(str);
        Set hashSet = new HashSet();
        ArrayMap arrayMap = new ArrayMap();
        Map arrayMap2 = new ArrayMap();
        ArrayMap arrayMap3 = new ArrayMap();
        Map zzfC = zzJo().zzfC(str);
        if (zzfC != null) {
            for (Integer intValue2 : zzfC.keySet()) {
                intValue = intValue2.intValue();
                zzauh.zzf com_google_android_gms_internal_zzauh_zzf = (zzauh.zzf) zzfC.get(Integer.valueOf(intValue));
                bitSet = (BitSet) arrayMap2.get(Integer.valueOf(intValue));
                bitSet2 = (BitSet) arrayMap3.get(Integer.valueOf(intValue));
                if (bitSet == null) {
                    bitSet = new BitSet();
                    arrayMap2.put(Integer.valueOf(intValue), bitSet);
                    bitSet2 = new BitSet();
                    arrayMap3.put(Integer.valueOf(intValue), bitSet2);
                }
                for (int i = 0; i < com_google_android_gms_internal_zzauh_zzf.zzbwC.length * 64; i++) {
                    if (zzaue.zza(com_google_android_gms_internal_zzauh_zzf.zzbwC, i)) {
                        zzJt().zzLg().zze("Filter already evaluated. audience ID, filter ID", Integer.valueOf(intValue), Integer.valueOf(i));
                        bitSet2.set(i);
                        if (zzaue.zza(com_google_android_gms_internal_zzauh_zzf.zzbwD, i)) {
                            bitSet.set(i);
                        }
                    }
                }
                zzauh.zza com_google_android_gms_internal_zzauh_zza = new zzauh.zza();
                arrayMap.put(Integer.valueOf(intValue), com_google_android_gms_internal_zzauh_zza);
                com_google_android_gms_internal_zzauh_zza.zzbvT = Boolean.valueOf(false);
                com_google_android_gms_internal_zzauh_zza.zzbvS = com_google_android_gms_internal_zzauh_zzf;
                com_google_android_gms_internal_zzauh_zza.zzbvR = new zzauh.zzf();
                com_google_android_gms_internal_zzauh_zza.zzbvR.zzbwD = zzaue.zza(bitSet);
                com_google_android_gms_internal_zzauh_zza.zzbvR.zzbwC = zzaue.zza(bitSet2);
            }
        }
        if (com_google_android_gms_internal_zzauh_zzbArr != null) {
            ArrayMap arrayMap4 = new ArrayMap();
            for (zzauh.zzb com_google_android_gms_internal_zzauh_zzb : com_google_android_gms_internal_zzauh_zzbArr) {
                zzasy com_google_android_gms_internal_zzasy;
                zzasy zzP = zzJo().zzP(str, com_google_android_gms_internal_zzauh_zzb.name);
                if (zzP == null) {
                    zzJt().zzLc().zze("Event aggregate wasn't created during raw event logging. appId, event", zzati.zzfI(str), com_google_android_gms_internal_zzauh_zzb.name);
                    com_google_android_gms_internal_zzasy = new zzasy(str, com_google_android_gms_internal_zzauh_zzb.name, 1, 1, com_google_android_gms_internal_zzauh_zzb.zzbvW.longValue());
                } else {
                    com_google_android_gms_internal_zzasy = zzP.zzKX();
                }
                zzJo().zza(com_google_android_gms_internal_zzasy);
                long j = com_google_android_gms_internal_zzasy.zzbqJ;
                map = (Map) arrayMap4.get(com_google_android_gms_internal_zzauh_zzb.name);
                if (map == null) {
                    map = zzJo().zzS(str, com_google_android_gms_internal_zzauh_zzb.name);
                    if (map == null) {
                        map = new ArrayMap();
                    }
                    arrayMap4.put(com_google_android_gms_internal_zzauh_zzb.name, map);
                    map2 = map;
                } else {
                    map2 = map;
                }
                for (Integer intValue22 : r7.keySet()) {
                    int intValue3 = intValue22.intValue();
                    if (hashSet.contains(Integer.valueOf(intValue3))) {
                        zzJt().zzLg().zzj("Skipping failed audience ID", Integer.valueOf(intValue3));
                    } else {
                        bitSet = (BitSet) arrayMap2.get(Integer.valueOf(intValue3));
                        bitSet2 = (BitSet) arrayMap3.get(Integer.valueOf(intValue3));
                        if (((zzauh.zza) arrayMap.get(Integer.valueOf(intValue3))) == null) {
                            zzauh.zza com_google_android_gms_internal_zzauh_zza2 = new zzauh.zza();
                            arrayMap.put(Integer.valueOf(intValue3), com_google_android_gms_internal_zzauh_zza2);
                            com_google_android_gms_internal_zzauh_zza2.zzbvT = Boolean.valueOf(true);
                            bitSet = new BitSet();
                            arrayMap2.put(Integer.valueOf(intValue3), bitSet);
                            bitSet2 = new BitSet();
                            arrayMap3.put(Integer.valueOf(intValue3), bitSet2);
                        }
                        for (zzb com_google_android_gms_internal_zzauf_zzb : (List) r7.get(Integer.valueOf(intValue3))) {
                            if (zzJt().zzai(2)) {
                                zzJt().zzLg().zzd("Evaluating filter. audience, filter, event", Integer.valueOf(intValue3), com_google_android_gms_internal_zzauf_zzb.zzbvl, com_google_android_gms_internal_zzauf_zzb.zzbvm);
                                zzJt().zzLg().zzj("Filter definition", zzaue.zza(com_google_android_gms_internal_zzauf_zzb));
                            }
                            if (com_google_android_gms_internal_zzauf_zzb.zzbvl == null || com_google_android_gms_internal_zzauf_zzb.zzbvl.intValue() > 256) {
                                zzJt().zzLc().zze("Invalid event filter ID. appId, id", zzati.zzfI(str), String.valueOf(com_google_android_gms_internal_zzauf_zzb.zzbvl));
                            } else if (bitSet.get(com_google_android_gms_internal_zzauf_zzb.zzbvl.intValue())) {
                                zzJt().zzLg().zze("Event filter already evaluated true. audience ID, filter ID", Integer.valueOf(intValue3), com_google_android_gms_internal_zzauf_zzb.zzbvl);
                            } else {
                                zza = zza(com_google_android_gms_internal_zzauf_zzb, com_google_android_gms_internal_zzauh_zzb, j);
                                zzati.zza zzLg = zzJt().zzLg();
                                String str2 = "Event filter result";
                                if (zza == null) {
                                    obj = "null";
                                } else {
                                    Boolean bool = zza;
                                }
                                zzLg.zzj(str2, obj);
                                if (zza == null) {
                                    hashSet.add(Integer.valueOf(intValue3));
                                } else {
                                    bitSet2.set(com_google_android_gms_internal_zzauf_zzb.zzbvl.intValue());
                                    if (zza.booleanValue()) {
                                        bitSet.set(com_google_android_gms_internal_zzauf_zzb.zzbvl.intValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (com_google_android_gms_internal_zzauh_zzgArr != null) {
            Map arrayMap5 = new ArrayMap();
            for (zzg com_google_android_gms_internal_zzauh_zzg : com_google_android_gms_internal_zzauh_zzgArr) {
                map = (Map) arrayMap5.get(com_google_android_gms_internal_zzauh_zzg.name);
                if (map == null) {
                    map = zzJo().zzT(str, com_google_android_gms_internal_zzauh_zzg.name);
                    if (map == null) {
                        map = new ArrayMap();
                    }
                    arrayMap5.put(com_google_android_gms_internal_zzauh_zzg.name, map);
                    map2 = map;
                } else {
                    map2 = map;
                }
                for (Integer intValue222 : r7.keySet()) {
                    int intValue4 = intValue222.intValue();
                    if (hashSet.contains(Integer.valueOf(intValue4))) {
                        zzJt().zzLg().zzj("Skipping failed audience ID", Integer.valueOf(intValue4));
                    } else {
                        bitSet = (BitSet) arrayMap2.get(Integer.valueOf(intValue4));
                        bitSet2 = (BitSet) arrayMap3.get(Integer.valueOf(intValue4));
                        if (((zzauh.zza) arrayMap.get(Integer.valueOf(intValue4))) == null) {
                            com_google_android_gms_internal_zzauh_zza2 = new zzauh.zza();
                            arrayMap.put(Integer.valueOf(intValue4), com_google_android_gms_internal_zzauh_zza2);
                            com_google_android_gms_internal_zzauh_zza2.zzbvT = Boolean.valueOf(true);
                            bitSet = new BitSet();
                            arrayMap2.put(Integer.valueOf(intValue4), bitSet);
                            bitSet2 = new BitSet();
                            arrayMap3.put(Integer.valueOf(intValue4), bitSet2);
                        }
                        for (zze com_google_android_gms_internal_zzauf_zze : (List) r7.get(Integer.valueOf(intValue4))) {
                            if (zzJt().zzai(2)) {
                                zzJt().zzLg().zzd("Evaluating filter. audience, filter, property", Integer.valueOf(intValue4), com_google_android_gms_internal_zzauf_zze.zzbvl, com_google_android_gms_internal_zzauf_zze.zzbvB);
                                zzJt().zzLg().zzj("Filter definition", zzaue.zza(com_google_android_gms_internal_zzauf_zze));
                            }
                            if (com_google_android_gms_internal_zzauf_zze.zzbvl == null || com_google_android_gms_internal_zzauf_zze.zzbvl.intValue() > 256) {
                                zzJt().zzLc().zze("Invalid property filter ID. appId, id", zzati.zzfI(str), String.valueOf(com_google_android_gms_internal_zzauf_zze.zzbvl));
                                hashSet.add(Integer.valueOf(intValue4));
                                break;
                            } else if (bitSet.get(com_google_android_gms_internal_zzauf_zze.zzbvl.intValue())) {
                                zzJt().zzLg().zze("Property filter already evaluated true. audience ID, filter ID", Integer.valueOf(intValue4), com_google_android_gms_internal_zzauf_zze.zzbvl);
                            } else {
                                zza = zza(com_google_android_gms_internal_zzauf_zze, com_google_android_gms_internal_zzauh_zzg);
                                zzati.zza zzLg2 = zzJt().zzLg();
                                String str3 = "Property filter result";
                                if (zza == null) {
                                    obj = "null";
                                } else {
                                    bool = zza;
                                }
                                zzLg2.zzj(str3, obj);
                                if (zza == null) {
                                    hashSet.add(Integer.valueOf(intValue4));
                                } else {
                                    bitSet2.set(com_google_android_gms_internal_zzauf_zze.zzbvl.intValue());
                                    if (zza.booleanValue()) {
                                        bitSet.set(com_google_android_gms_internal_zzauf_zze.zzbvl.intValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        zzauh.zza[] com_google_android_gms_internal_zzauh_zzaArr = new zzauh.zza[arrayMap2.size()];
        int i2 = 0;
        for (Integer intValue2222 : arrayMap2.keySet()) {
            intValue = intValue2222.intValue();
            if (!hashSet.contains(Integer.valueOf(intValue))) {
                com_google_android_gms_internal_zzauh_zza2 = (zzauh.zza) arrayMap.get(Integer.valueOf(intValue));
                com_google_android_gms_internal_zzauh_zza = com_google_android_gms_internal_zzauh_zza2 == null ? new zzauh.zza() : com_google_android_gms_internal_zzauh_zza2;
                int i3 = i2 + 1;
                com_google_android_gms_internal_zzauh_zzaArr[i2] = com_google_android_gms_internal_zzauh_zza;
                com_google_android_gms_internal_zzauh_zza.zzbvh = Integer.valueOf(intValue);
                com_google_android_gms_internal_zzauh_zza.zzbvR = new zzauh.zzf();
                com_google_android_gms_internal_zzauh_zza.zzbvR.zzbwD = zzaue.zza((BitSet) arrayMap2.get(Integer.valueOf(intValue)));
                com_google_android_gms_internal_zzauh_zza.zzbvR.zzbwC = zzaue.zza((BitSet) arrayMap3.get(Integer.valueOf(intValue)));
                zzJo().zza(str, intValue, com_google_android_gms_internal_zzauh_zza.zzbvR);
                i2 = i3;
            }
        }
        return (zzauh.zza[]) Arrays.copyOf(com_google_android_gms_internal_zzauh_zzaArr, i2);
    }

    protected void zzmr() {
    }
}

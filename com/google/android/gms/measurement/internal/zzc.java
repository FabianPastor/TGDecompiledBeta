package com.google.android.gms.measurement.internal;

import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzvk.zza;
import com.google.android.gms.internal.zzvk.zzb;
import com.google.android.gms.internal.zzvk.zze;
import com.google.android.gms.internal.zzvm;
import com.google.android.gms.internal.zzvm.zzf;
import com.google.android.gms.internal.zzvm.zzg;
import com.google.android.gms.measurement.AppMeasurement;
import com.google.android.gms.measurement.AppMeasurement.zzd;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class zzc extends zzaa {
    zzc(zzx com_google_android_gms_measurement_internal_zzx) {
        super(com_google_android_gms_measurement_internal_zzx);
    }

    private Boolean zza(zzb com_google_android_gms_internal_zzvk_zzb, zzvm.zzb com_google_android_gms_internal_zzvm_zzb, long j) {
        Boolean zzbn;
        if (com_google_android_gms_internal_zzvk_zzb.asI != null) {
            zzbn = new zzs(com_google_android_gms_internal_zzvk_zzb.asI).zzbn(j);
            if (zzbn == null) {
                return null;
            }
            if (!zzbn.booleanValue()) {
                return Boolean.valueOf(false);
            }
        }
        Set hashSet = new HashSet();
        for (com.google.android.gms.internal.zzvk.zzc com_google_android_gms_internal_zzvk_zzc : com_google_android_gms_internal_zzvk_zzb.asG) {
            if (TextUtils.isEmpty(com_google_android_gms_internal_zzvk_zzc.asN)) {
                zzbvg().zzbwe().zzj("null or empty param name in filter. event", com_google_android_gms_internal_zzvm_zzb.name);
                return null;
            }
            hashSet.add(com_google_android_gms_internal_zzvk_zzc.asN);
        }
        Map arrayMap = new ArrayMap();
        for (com.google.android.gms.internal.zzvm.zzc com_google_android_gms_internal_zzvm_zzc : com_google_android_gms_internal_zzvm_zzb.ato) {
            if (hashSet.contains(com_google_android_gms_internal_zzvm_zzc.name)) {
                if (com_google_android_gms_internal_zzvm_zzc.ats != null) {
                    arrayMap.put(com_google_android_gms_internal_zzvm_zzc.name, com_google_android_gms_internal_zzvm_zzc.ats);
                } else if (com_google_android_gms_internal_zzvm_zzc.asx != null) {
                    arrayMap.put(com_google_android_gms_internal_zzvm_zzc.name, com_google_android_gms_internal_zzvm_zzc.asx);
                } else if (com_google_android_gms_internal_zzvm_zzc.Dr != null) {
                    arrayMap.put(com_google_android_gms_internal_zzvm_zzc.name, com_google_android_gms_internal_zzvm_zzc.Dr);
                } else {
                    zzbvg().zzbwe().zze("Unknown value for param. event, param", com_google_android_gms_internal_zzvm_zzb.name, com_google_android_gms_internal_zzvm_zzc.name);
                    return null;
                }
            }
        }
        for (com.google.android.gms.internal.zzvk.zzc com_google_android_gms_internal_zzvk_zzc2 : com_google_android_gms_internal_zzvk_zzb.asG) {
            boolean equals = Boolean.TRUE.equals(com_google_android_gms_internal_zzvk_zzc2.asM);
            CharSequence charSequence = com_google_android_gms_internal_zzvk_zzc2.asN;
            if (TextUtils.isEmpty(charSequence)) {
                zzbvg().zzbwe().zzj("Event has empty param name. event", com_google_android_gms_internal_zzvm_zzb.name);
                return null;
            }
            Object obj = arrayMap.get(charSequence);
            if (obj instanceof Long) {
                if (com_google_android_gms_internal_zzvk_zzc2.asL == null) {
                    zzbvg().zzbwe().zze("No number filter for long param. event, param", com_google_android_gms_internal_zzvm_zzb.name, charSequence);
                    return null;
                }
                zzbn = new zzs(com_google_android_gms_internal_zzvk_zzc2.asL).zzbn(((Long) obj).longValue());
                if (zzbn == null) {
                    return null;
                }
                if (((!zzbn.booleanValue() ? 1 : 0) ^ equals) != 0) {
                    return Boolean.valueOf(false);
                }
            } else if (obj instanceof Double) {
                if (com_google_android_gms_internal_zzvk_zzc2.asL == null) {
                    zzbvg().zzbwe().zze("No number filter for double param. event, param", com_google_android_gms_internal_zzvm_zzb.name, charSequence);
                    return null;
                }
                zzbn = new zzs(com_google_android_gms_internal_zzvk_zzc2.asL).zzj(((Double) obj).doubleValue());
                if (zzbn == null) {
                    return null;
                }
                if (((!zzbn.booleanValue() ? 1 : 0) ^ equals) != 0) {
                    return Boolean.valueOf(false);
                }
            } else if (obj instanceof String) {
                if (com_google_android_gms_internal_zzvk_zzc2.asK == null) {
                    zzbvg().zzbwe().zze("No string filter for String param. event, param", com_google_android_gms_internal_zzvm_zzb.name, charSequence);
                    return null;
                }
                zzbn = new zzag(com_google_android_gms_internal_zzvk_zzc2.asK).zzmw((String) obj);
                if (zzbn == null) {
                    return null;
                }
                if (((!zzbn.booleanValue() ? 1 : 0) ^ equals) != 0) {
                    return Boolean.valueOf(false);
                }
            } else if (obj == null) {
                zzbvg().zzbwj().zze("Missing param for filter. event, param", com_google_android_gms_internal_zzvm_zzb.name, charSequence);
                return Boolean.valueOf(false);
            } else {
                zzbvg().zzbwe().zze("Unknown param type. event, param", com_google_android_gms_internal_zzvm_zzb.name, charSequence);
                return null;
            }
        }
        return Boolean.valueOf(true);
    }

    private Boolean zza(zze com_google_android_gms_internal_zzvk_zze, zzg com_google_android_gms_internal_zzvm_zzg) {
        com.google.android.gms.internal.zzvk.zzc com_google_android_gms_internal_zzvk_zzc = com_google_android_gms_internal_zzvk_zze.asV;
        if (com_google_android_gms_internal_zzvk_zzc == null) {
            zzbvg().zzbwe().zzj("Missing property filter. property", com_google_android_gms_internal_zzvm_zzg.name);
            return null;
        }
        boolean equals = Boolean.TRUE.equals(com_google_android_gms_internal_zzvk_zzc.asM);
        if (com_google_android_gms_internal_zzvm_zzg.ats != null) {
            if (com_google_android_gms_internal_zzvk_zzc.asL != null) {
                return zza(new zzs(com_google_android_gms_internal_zzvk_zzc.asL).zzbn(com_google_android_gms_internal_zzvm_zzg.ats.longValue()), equals);
            }
            zzbvg().zzbwe().zzj("No number filter for long property. property", com_google_android_gms_internal_zzvm_zzg.name);
            return null;
        } else if (com_google_android_gms_internal_zzvm_zzg.asx != null) {
            if (com_google_android_gms_internal_zzvk_zzc.asL != null) {
                return zza(new zzs(com_google_android_gms_internal_zzvk_zzc.asL).zzj(com_google_android_gms_internal_zzvm_zzg.asx.doubleValue()), equals);
            }
            zzbvg().zzbwe().zzj("No number filter for double property. property", com_google_android_gms_internal_zzvm_zzg.name);
            return null;
        } else if (com_google_android_gms_internal_zzvm_zzg.Dr == null) {
            zzbvg().zzbwe().zzj("User property has no value, property", com_google_android_gms_internal_zzvm_zzg.name);
            return null;
        } else if (com_google_android_gms_internal_zzvk_zzc.asK != null) {
            return zza(new zzag(com_google_android_gms_internal_zzvk_zzc.asK).zzmw(com_google_android_gms_internal_zzvm_zzg.Dr), equals);
        } else {
            if (com_google_android_gms_internal_zzvk_zzc.asL == null) {
                zzbvg().zzbwe().zzj("No string or number filter defined. property", com_google_android_gms_internal_zzvm_zzg.name);
                return null;
            }
            zzs com_google_android_gms_measurement_internal_zzs = new zzs(com_google_android_gms_internal_zzvk_zzc.asL);
            if (zzal.zznj(com_google_android_gms_internal_zzvm_zzg.Dr)) {
                return zza(com_google_android_gms_measurement_internal_zzs.zzmk(com_google_android_gms_internal_zzvm_zzg.Dr), equals);
            }
            zzbvg().zzbwe().zze("Invalid user property value for Numeric number filter. property, value", com_google_android_gms_internal_zzvm_zzg.name, com_google_android_gms_internal_zzvm_zzg.Dr);
            return null;
        }
    }

    static Boolean zza(Boolean bool, boolean z) {
        return bool == null ? null : Boolean.valueOf(bool.booleanValue() ^ z);
    }

    @WorkerThread
    void zza(String str, zza[] com_google_android_gms_internal_zzvk_zzaArr) {
        zzac.zzy(com_google_android_gms_internal_zzvk_zzaArr);
        for (zza com_google_android_gms_internal_zzvk_zza : com_google_android_gms_internal_zzvk_zzaArr) {
            for (zzb com_google_android_gms_internal_zzvk_zzb : com_google_android_gms_internal_zzvk_zza.asC) {
                String str2 = (String) AppMeasurement.zza.anr.get(com_google_android_gms_internal_zzvk_zzb.asF);
                if (str2 != null) {
                    com_google_android_gms_internal_zzvk_zzb.asF = str2;
                }
                for (com.google.android.gms.internal.zzvk.zzc com_google_android_gms_internal_zzvk_zzc : com_google_android_gms_internal_zzvk_zzb.asG) {
                    str2 = (String) zzd.ans.get(com_google_android_gms_internal_zzvk_zzc.asN);
                    if (str2 != null) {
                        com_google_android_gms_internal_zzvk_zzc.asN = str2;
                    }
                }
            }
            for (zze com_google_android_gms_internal_zzvk_zze : com_google_android_gms_internal_zzvk_zza.asB) {
                str2 = (String) AppMeasurement.zze.ant.get(com_google_android_gms_internal_zzvk_zze.asU);
                if (str2 != null) {
                    com_google_android_gms_internal_zzvk_zze.asU = str2;
                }
            }
        }
        zzbvb().zzb(str, com_google_android_gms_internal_zzvk_zzaArr);
    }

    @WorkerThread
    zzvm.zza[] zza(String str, zzvm.zzb[] com_google_android_gms_internal_zzvm_zzbArr, zzg[] com_google_android_gms_internal_zzvm_zzgArr) {
        int intValue;
        BitSet bitSet;
        BitSet bitSet2;
        Map map;
        Map map2;
        Boolean zza;
        Object obj;
        zzac.zzhz(str);
        Set hashSet = new HashSet();
        ArrayMap arrayMap = new ArrayMap();
        Map arrayMap2 = new ArrayMap();
        ArrayMap arrayMap3 = new ArrayMap();
        Map zzmd = zzbvb().zzmd(str);
        if (zzmd != null) {
            for (Integer intValue2 : zzmd.keySet()) {
                intValue = intValue2.intValue();
                zzf com_google_android_gms_internal_zzvm_zzf = (zzf) zzmd.get(Integer.valueOf(intValue));
                bitSet = (BitSet) arrayMap2.get(Integer.valueOf(intValue));
                bitSet2 = (BitSet) arrayMap3.get(Integer.valueOf(intValue));
                if (bitSet == null) {
                    bitSet = new BitSet();
                    arrayMap2.put(Integer.valueOf(intValue), bitSet);
                    bitSet2 = new BitSet();
                    arrayMap3.put(Integer.valueOf(intValue), bitSet2);
                }
                for (int i = 0; i < com_google_android_gms_internal_zzvm_zzf.atU.length * 64; i++) {
                    if (zzal.zza(com_google_android_gms_internal_zzvm_zzf.atU, i)) {
                        zzbvg().zzbwj().zze("Filter already evaluated. audience ID, filter ID", Integer.valueOf(intValue), Integer.valueOf(i));
                        bitSet2.set(i);
                        if (zzal.zza(com_google_android_gms_internal_zzvm_zzf.atV, i)) {
                            bitSet.set(i);
                        }
                    }
                }
                zzvm.zza com_google_android_gms_internal_zzvm_zza = new zzvm.zza();
                arrayMap.put(Integer.valueOf(intValue), com_google_android_gms_internal_zzvm_zza);
                com_google_android_gms_internal_zzvm_zza.atm = Boolean.valueOf(false);
                com_google_android_gms_internal_zzvm_zza.atl = com_google_android_gms_internal_zzvm_zzf;
                com_google_android_gms_internal_zzvm_zza.atk = new zzf();
                com_google_android_gms_internal_zzvm_zza.atk.atV = zzal.zza(bitSet);
                com_google_android_gms_internal_zzvm_zza.atk.atU = zzal.zza(bitSet2);
            }
        }
        if (com_google_android_gms_internal_zzvm_zzbArr != null) {
            ArrayMap arrayMap4 = new ArrayMap();
            for (zzvm.zzb com_google_android_gms_internal_zzvm_zzb : com_google_android_gms_internal_zzvm_zzbArr) {
                zzi com_google_android_gms_measurement_internal_zzi;
                zzi zzaq = zzbvb().zzaq(str, com_google_android_gms_internal_zzvm_zzb.name);
                if (zzaq == null) {
                    zzbvg().zzbwe().zzj("Event aggregate wasn't created during raw event logging. event", com_google_android_gms_internal_zzvm_zzb.name);
                    com_google_android_gms_measurement_internal_zzi = new zzi(str, com_google_android_gms_internal_zzvm_zzb.name, 1, 1, com_google_android_gms_internal_zzvm_zzb.atp.longValue());
                } else {
                    com_google_android_gms_measurement_internal_zzi = zzaq.zzbvy();
                }
                zzbvb().zza(com_google_android_gms_measurement_internal_zzi);
                long j = com_google_android_gms_measurement_internal_zzi.aot;
                map = (Map) arrayMap4.get(com_google_android_gms_internal_zzvm_zzb.name);
                if (map == null) {
                    map = zzbvb().zzat(str, com_google_android_gms_internal_zzvm_zzb.name);
                    if (map == null) {
                        map = new ArrayMap();
                    }
                    arrayMap4.put(com_google_android_gms_internal_zzvm_zzb.name, map);
                    map2 = map;
                } else {
                    map2 = map;
                }
                for (Integer intValue22 : r7.keySet()) {
                    int intValue3 = intValue22.intValue();
                    if (hashSet.contains(Integer.valueOf(intValue3))) {
                        zzbvg().zzbwj().zzj("Skipping failed audience ID", Integer.valueOf(intValue3));
                    } else {
                        bitSet = (BitSet) arrayMap2.get(Integer.valueOf(intValue3));
                        bitSet2 = (BitSet) arrayMap3.get(Integer.valueOf(intValue3));
                        if (((zzvm.zza) arrayMap.get(Integer.valueOf(intValue3))) == null) {
                            zzvm.zza com_google_android_gms_internal_zzvm_zza2 = new zzvm.zza();
                            arrayMap.put(Integer.valueOf(intValue3), com_google_android_gms_internal_zzvm_zza2);
                            com_google_android_gms_internal_zzvm_zza2.atm = Boolean.valueOf(true);
                            bitSet = new BitSet();
                            arrayMap2.put(Integer.valueOf(intValue3), bitSet);
                            bitSet2 = new BitSet();
                            arrayMap3.put(Integer.valueOf(intValue3), bitSet2);
                        }
                        for (zzb com_google_android_gms_internal_zzvk_zzb : (List) r7.get(Integer.valueOf(intValue3))) {
                            if (zzbvg().zzbf(2)) {
                                zzbvg().zzbwj().zzd("Evaluating filter. audience, filter, event", Integer.valueOf(intValue3), com_google_android_gms_internal_zzvk_zzb.asE, com_google_android_gms_internal_zzvk_zzb.asF);
                                zzbvg().zzbwj().zzj("Filter definition", zzal.zza(com_google_android_gms_internal_zzvk_zzb));
                            }
                            if (com_google_android_gms_internal_zzvk_zzb.asE == null || com_google_android_gms_internal_zzvk_zzb.asE.intValue() > 256) {
                                zzbvg().zzbwe().zzj("Invalid event filter ID. id", String.valueOf(com_google_android_gms_internal_zzvk_zzb.asE));
                            } else if (bitSet.get(com_google_android_gms_internal_zzvk_zzb.asE.intValue())) {
                                zzbvg().zzbwj().zze("Event filter already evaluated true. audience ID, filter ID", Integer.valueOf(intValue3), com_google_android_gms_internal_zzvk_zzb.asE);
                            } else {
                                zza = zza(com_google_android_gms_internal_zzvk_zzb, com_google_android_gms_internal_zzvm_zzb, j);
                                zzp.zza zzbwj = zzbvg().zzbwj();
                                String str2 = "Event filter result";
                                if (zza == null) {
                                    obj = "null";
                                } else {
                                    Boolean bool = zza;
                                }
                                zzbwj.zzj(str2, obj);
                                if (zza == null) {
                                    hashSet.add(Integer.valueOf(intValue3));
                                } else {
                                    bitSet2.set(com_google_android_gms_internal_zzvk_zzb.asE.intValue());
                                    if (zza.booleanValue()) {
                                        bitSet.set(com_google_android_gms_internal_zzvk_zzb.asE.intValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (com_google_android_gms_internal_zzvm_zzgArr != null) {
            Map arrayMap5 = new ArrayMap();
            for (zzg com_google_android_gms_internal_zzvm_zzg : com_google_android_gms_internal_zzvm_zzgArr) {
                map = (Map) arrayMap5.get(com_google_android_gms_internal_zzvm_zzg.name);
                if (map == null) {
                    map = zzbvb().zzau(str, com_google_android_gms_internal_zzvm_zzg.name);
                    if (map == null) {
                        map = new ArrayMap();
                    }
                    arrayMap5.put(com_google_android_gms_internal_zzvm_zzg.name, map);
                    map2 = map;
                } else {
                    map2 = map;
                }
                for (Integer intValue222 : r7.keySet()) {
                    int intValue4 = intValue222.intValue();
                    if (hashSet.contains(Integer.valueOf(intValue4))) {
                        zzbvg().zzbwj().zzj("Skipping failed audience ID", Integer.valueOf(intValue4));
                    } else {
                        bitSet = (BitSet) arrayMap2.get(Integer.valueOf(intValue4));
                        bitSet2 = (BitSet) arrayMap3.get(Integer.valueOf(intValue4));
                        if (((zzvm.zza) arrayMap.get(Integer.valueOf(intValue4))) == null) {
                            com_google_android_gms_internal_zzvm_zza2 = new zzvm.zza();
                            arrayMap.put(Integer.valueOf(intValue4), com_google_android_gms_internal_zzvm_zza2);
                            com_google_android_gms_internal_zzvm_zza2.atm = Boolean.valueOf(true);
                            bitSet = new BitSet();
                            arrayMap2.put(Integer.valueOf(intValue4), bitSet);
                            bitSet2 = new BitSet();
                            arrayMap3.put(Integer.valueOf(intValue4), bitSet2);
                        }
                        for (zze com_google_android_gms_internal_zzvk_zze : (List) r7.get(Integer.valueOf(intValue4))) {
                            if (zzbvg().zzbf(2)) {
                                zzbvg().zzbwj().zzd("Evaluating filter. audience, filter, property", Integer.valueOf(intValue4), com_google_android_gms_internal_zzvk_zze.asE, com_google_android_gms_internal_zzvk_zze.asU);
                                zzbvg().zzbwj().zzj("Filter definition", zzal.zza(com_google_android_gms_internal_zzvk_zze));
                            }
                            if (com_google_android_gms_internal_zzvk_zze.asE == null || com_google_android_gms_internal_zzvk_zze.asE.intValue() > 256) {
                                zzbvg().zzbwe().zzj("Invalid property filter ID. id", String.valueOf(com_google_android_gms_internal_zzvk_zze.asE));
                                hashSet.add(Integer.valueOf(intValue4));
                                break;
                            } else if (bitSet.get(com_google_android_gms_internal_zzvk_zze.asE.intValue())) {
                                zzbvg().zzbwj().zze("Property filter already evaluated true. audience ID, filter ID", Integer.valueOf(intValue4), com_google_android_gms_internal_zzvk_zze.asE);
                            } else {
                                zza = zza(com_google_android_gms_internal_zzvk_zze, com_google_android_gms_internal_zzvm_zzg);
                                zzp.zza zzbwj2 = zzbvg().zzbwj();
                                String str3 = "Property filter result";
                                if (zza == null) {
                                    obj = "null";
                                } else {
                                    bool = zza;
                                }
                                zzbwj2.zzj(str3, obj);
                                if (zza == null) {
                                    hashSet.add(Integer.valueOf(intValue4));
                                } else {
                                    bitSet2.set(com_google_android_gms_internal_zzvk_zze.asE.intValue());
                                    if (zza.booleanValue()) {
                                        bitSet.set(com_google_android_gms_internal_zzvk_zze.asE.intValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        zzvm.zza[] com_google_android_gms_internal_zzvm_zzaArr = new zzvm.zza[arrayMap2.size()];
        int i2 = 0;
        for (Integer intValue2222 : arrayMap2.keySet()) {
            intValue = intValue2222.intValue();
            if (!hashSet.contains(Integer.valueOf(intValue))) {
                com_google_android_gms_internal_zzvm_zza2 = (zzvm.zza) arrayMap.get(Integer.valueOf(intValue));
                com_google_android_gms_internal_zzvm_zza = com_google_android_gms_internal_zzvm_zza2 == null ? new zzvm.zza() : com_google_android_gms_internal_zzvm_zza2;
                int i3 = i2 + 1;
                com_google_android_gms_internal_zzvm_zzaArr[i2] = com_google_android_gms_internal_zzvm_zza;
                com_google_android_gms_internal_zzvm_zza.asA = Integer.valueOf(intValue);
                com_google_android_gms_internal_zzvm_zza.atk = new zzf();
                com_google_android_gms_internal_zzvm_zza.atk.atV = zzal.zza((BitSet) arrayMap2.get(Integer.valueOf(intValue)));
                com_google_android_gms_internal_zzvm_zza.atk.atU = zzal.zza((BitSet) arrayMap3.get(Integer.valueOf(intValue)));
                zzbvb().zza(str, intValue, com_google_android_gms_internal_zzvm_zza.atk);
                i2 = i3;
            }
        }
        return (zzvm.zza[]) Arrays.copyOf(com_google_android_gms_internal_zzvm_zzaArr, i2);
    }

    protected void zzym() {
    }
}

package com.google.android.gms.measurement.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzard;
import com.google.android.gms.internal.zzvk.zzb;
import com.google.android.gms.internal.zzvk.zzc;
import com.google.android.gms.internal.zzvk.zzd;
import com.google.android.gms.internal.zzvk.zze;
import com.google.android.gms.internal.zzvk.zzf;
import com.google.android.gms.internal.zzvm;
import com.google.android.gms.internal.zzvm.zza;
import com.google.android.gms.internal.zzvm.zzg;
import com.google.android.gms.measurement.AppMeasurement;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class zzal extends zzz {
    zzal(zzx com_google_android_gms_measurement_internal_zzx) {
        super(com_google_android_gms_measurement_internal_zzx);
    }

    private Object zza(int i, Object obj, boolean z) {
        if (obj == null) {
            return null;
        }
        if ((obj instanceof Long) || (obj instanceof Double)) {
            return obj;
        }
        if (obj instanceof Integer) {
            return Long.valueOf((long) ((Integer) obj).intValue());
        }
        if (obj instanceof Byte) {
            return Long.valueOf((long) ((Byte) obj).byteValue());
        }
        if (obj instanceof Short) {
            return Long.valueOf((long) ((Short) obj).shortValue());
        }
        if (!(obj instanceof Boolean)) {
            return obj instanceof Float ? Double.valueOf(((Float) obj).doubleValue()) : ((obj instanceof String) || (obj instanceof Character) || (obj instanceof CharSequence)) ? zza(String.valueOf(obj), i, z) : null;
        } else {
            return Long.valueOf(((Boolean) obj).booleanValue() ? 1 : 0);
        }
    }

    public static String zza(zzb com_google_android_gms_internal_zzvk_zzb) {
        int i = 0;
        if (com_google_android_gms_internal_zzvk_zzb == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nevent_filter {\n");
        zza(stringBuilder, 0, "filter_id", com_google_android_gms_internal_zzvk_zzb.asE);
        zza(stringBuilder, 0, "event_name", com_google_android_gms_internal_zzvk_zzb.asF);
        zza(stringBuilder, 1, "event_count_filter", com_google_android_gms_internal_zzvk_zzb.asI);
        stringBuilder.append("  filters {\n");
        zzc[] com_google_android_gms_internal_zzvk_zzcArr = com_google_android_gms_internal_zzvk_zzb.asG;
        int length = com_google_android_gms_internal_zzvk_zzcArr.length;
        while (i < length) {
            zza(stringBuilder, 2, com_google_android_gms_internal_zzvk_zzcArr[i]);
            i++;
        }
        zza(stringBuilder, 1);
        stringBuilder.append("}\n}\n");
        return stringBuilder.toString();
    }

    public static String zza(zze com_google_android_gms_internal_zzvk_zze) {
        if (com_google_android_gms_internal_zzvk_zze == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nproperty_filter {\n");
        zza(stringBuilder, 0, "filter_id", com_google_android_gms_internal_zzvk_zze.asE);
        zza(stringBuilder, 0, "property_name", com_google_android_gms_internal_zzvk_zze.asU);
        zza(stringBuilder, 1, com_google_android_gms_internal_zzvk_zze.asV);
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    private static void zza(StringBuilder stringBuilder, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            stringBuilder.append("  ");
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, zzc com_google_android_gms_internal_zzvk_zzc) {
        if (com_google_android_gms_internal_zzvk_zzc != null) {
            zza(stringBuilder, i);
            stringBuilder.append("filter {\n");
            zza(stringBuilder, i, "complement", com_google_android_gms_internal_zzvk_zzc.asM);
            zza(stringBuilder, i, "param_name", com_google_android_gms_internal_zzvk_zzc.asN);
            zza(stringBuilder, i + 1, "string_filter", com_google_android_gms_internal_zzvk_zzc.asK);
            zza(stringBuilder, i + 1, "number_filter", com_google_android_gms_internal_zzvk_zzc.asL);
            zza(stringBuilder, i);
            stringBuilder.append("}\n");
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, zzvm.zze com_google_android_gms_internal_zzvm_zze) {
        if (com_google_android_gms_internal_zzvm_zze != null) {
            zza(stringBuilder, i);
            stringBuilder.append("bundle {\n");
            zza(stringBuilder, i, "protocol_version", com_google_android_gms_internal_zzvm_zze.atv);
            zza(stringBuilder, i, "platform", com_google_android_gms_internal_zzvm_zze.atD);
            zza(stringBuilder, i, "gmp_version", com_google_android_gms_internal_zzvm_zze.atH);
            zza(stringBuilder, i, "uploading_gmp_version", com_google_android_gms_internal_zzvm_zze.atI);
            zza(stringBuilder, i, "gmp_app_id", com_google_android_gms_internal_zzvm_zze.anQ);
            zza(stringBuilder, i, "app_id", com_google_android_gms_internal_zzvm_zze.zzck);
            zza(stringBuilder, i, "app_version", com_google_android_gms_internal_zzvm_zze.afY);
            zza(stringBuilder, i, "app_version_major", com_google_android_gms_internal_zzvm_zze.atQ);
            zza(stringBuilder, i, "firebase_instance_id", com_google_android_gms_internal_zzvm_zze.anY);
            zza(stringBuilder, i, "dev_cert_hash", com_google_android_gms_internal_zzvm_zze.atM);
            zza(stringBuilder, i, "app_store", com_google_android_gms_internal_zzvm_zze.anR);
            zza(stringBuilder, i, "upload_timestamp_millis", com_google_android_gms_internal_zzvm_zze.aty);
            zza(stringBuilder, i, "start_timestamp_millis", com_google_android_gms_internal_zzvm_zze.atz);
            zza(stringBuilder, i, "end_timestamp_millis", com_google_android_gms_internal_zzvm_zze.atA);
            zza(stringBuilder, i, "previous_bundle_start_timestamp_millis", com_google_android_gms_internal_zzvm_zze.atB);
            zza(stringBuilder, i, "previous_bundle_end_timestamp_millis", com_google_android_gms_internal_zzvm_zze.atC);
            zza(stringBuilder, i, "app_instance_id", com_google_android_gms_internal_zzvm_zze.atL);
            zza(stringBuilder, i, "resettable_device_id", com_google_android_gms_internal_zzvm_zze.atJ);
            zza(stringBuilder, i, "device_id", com_google_android_gms_internal_zzvm_zze.atT);
            zza(stringBuilder, i, "limited_ad_tracking", com_google_android_gms_internal_zzvm_zze.atK);
            zza(stringBuilder, i, "os_version", com_google_android_gms_internal_zzvm_zze.zzct);
            zza(stringBuilder, i, "device_model", com_google_android_gms_internal_zzvm_zze.atE);
            zza(stringBuilder, i, "user_default_language", com_google_android_gms_internal_zzvm_zze.atF);
            zza(stringBuilder, i, "time_zone_offset_minutes", com_google_android_gms_internal_zzvm_zze.atG);
            zza(stringBuilder, i, "bundle_sequential_index", com_google_android_gms_internal_zzvm_zze.atN);
            zza(stringBuilder, i, "service_upload", com_google_android_gms_internal_zzvm_zze.atO);
            zza(stringBuilder, i, "health_monitor", com_google_android_gms_internal_zzvm_zze.anU);
            zza(stringBuilder, i, com_google_android_gms_internal_zzvm_zze.atx);
            zza(stringBuilder, i, com_google_android_gms_internal_zzvm_zze.atP);
            zza(stringBuilder, i, com_google_android_gms_internal_zzvm_zze.atw);
            zza(stringBuilder, i);
            stringBuilder.append("}\n");
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, String str, zzd com_google_android_gms_internal_zzvk_zzd) {
        if (com_google_android_gms_internal_zzvk_zzd != null) {
            zza(stringBuilder, i);
            stringBuilder.append(str);
            stringBuilder.append(" {\n");
            if (com_google_android_gms_internal_zzvk_zzd.asO != null) {
                Object obj = "UNKNOWN_COMPARISON_TYPE";
                switch (com_google_android_gms_internal_zzvk_zzd.asO.intValue()) {
                    case 1:
                        obj = "LESS_THAN";
                        break;
                    case 2:
                        obj = "GREATER_THAN";
                        break;
                    case 3:
                        obj = "EQUAL";
                        break;
                    case 4:
                        obj = "BETWEEN";
                        break;
                }
                zza(stringBuilder, i, "comparison_type", obj);
            }
            zza(stringBuilder, i, "match_as_float", com_google_android_gms_internal_zzvk_zzd.asP);
            zza(stringBuilder, i, "comparison_value", com_google_android_gms_internal_zzvk_zzd.asQ);
            zza(stringBuilder, i, "min_comparison_value", com_google_android_gms_internal_zzvk_zzd.asR);
            zza(stringBuilder, i, "max_comparison_value", com_google_android_gms_internal_zzvk_zzd.asS);
            zza(stringBuilder, i);
            stringBuilder.append("}\n");
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, String str, zzf com_google_android_gms_internal_zzvk_zzf) {
        if (com_google_android_gms_internal_zzvk_zzf != null) {
            zza(stringBuilder, i);
            stringBuilder.append(str);
            stringBuilder.append(" {\n");
            if (com_google_android_gms_internal_zzvk_zzf.asW != null) {
                Object obj = "UNKNOWN_MATCH_TYPE";
                switch (com_google_android_gms_internal_zzvk_zzf.asW.intValue()) {
                    case 1:
                        obj = "REGEXP";
                        break;
                    case 2:
                        obj = "BEGINS_WITH";
                        break;
                    case 3:
                        obj = "ENDS_WITH";
                        break;
                    case 4:
                        obj = "PARTIAL";
                        break;
                    case 5:
                        obj = "EXACT";
                        break;
                    case 6:
                        obj = "IN_LIST";
                        break;
                }
                zza(stringBuilder, i, "match_type", obj);
            }
            zza(stringBuilder, i, "expression", com_google_android_gms_internal_zzvk_zzf.asX);
            zza(stringBuilder, i, "case_sensitive", com_google_android_gms_internal_zzvk_zzf.asY);
            if (com_google_android_gms_internal_zzvk_zzf.asZ.length > 0) {
                zza(stringBuilder, i + 1);
                stringBuilder.append("expression_list {\n");
                for (String str2 : com_google_android_gms_internal_zzvk_zzf.asZ) {
                    zza(stringBuilder, i + 2);
                    stringBuilder.append(str2);
                    stringBuilder.append("\n");
                }
                stringBuilder.append("}\n");
            }
            zza(stringBuilder, i);
            stringBuilder.append("}\n");
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, String str, zzvm.zzf com_google_android_gms_internal_zzvm_zzf) {
        int i2 = 0;
        if (com_google_android_gms_internal_zzvm_zzf != null) {
            int i3;
            int i4;
            int i5 = i + 1;
            zza(stringBuilder, i5);
            stringBuilder.append(str);
            stringBuilder.append(" {\n");
            if (com_google_android_gms_internal_zzvm_zzf.atV != null) {
                zza(stringBuilder, i5 + 1);
                stringBuilder.append("results: ");
                long[] jArr = com_google_android_gms_internal_zzvm_zzf.atV;
                int length = jArr.length;
                i3 = 0;
                i4 = 0;
                while (i3 < length) {
                    Long valueOf = Long.valueOf(jArr[i3]);
                    int i6 = i4 + 1;
                    if (i4 != 0) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append(valueOf);
                    i3++;
                    i4 = i6;
                }
                stringBuilder.append('\n');
            }
            if (com_google_android_gms_internal_zzvm_zzf.atU != null) {
                zza(stringBuilder, i5 + 1);
                stringBuilder.append("status: ");
                long[] jArr2 = com_google_android_gms_internal_zzvm_zzf.atU;
                int length2 = jArr2.length;
                i3 = 0;
                while (i2 < length2) {
                    Long valueOf2 = Long.valueOf(jArr2[i2]);
                    i4 = i3 + 1;
                    if (i3 != 0) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append(valueOf2);
                    i2++;
                    i3 = i4;
                }
                stringBuilder.append('\n');
            }
            zza(stringBuilder, i5);
            stringBuilder.append("}\n");
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, String str, Object obj) {
        if (obj != null) {
            zza(stringBuilder, i + 1);
            stringBuilder.append(str);
            stringBuilder.append(": ");
            stringBuilder.append(obj);
            stringBuilder.append('\n');
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, zza[] com_google_android_gms_internal_zzvm_zzaArr) {
        if (com_google_android_gms_internal_zzvm_zzaArr != null) {
            int i2 = i + 1;
            for (zza com_google_android_gms_internal_zzvm_zza : com_google_android_gms_internal_zzvm_zzaArr) {
                if (com_google_android_gms_internal_zzvm_zza != null) {
                    zza(stringBuilder, i2);
                    stringBuilder.append("audience_membership {\n");
                    zza(stringBuilder, i2, "audience_id", com_google_android_gms_internal_zzvm_zza.asA);
                    zza(stringBuilder, i2, "new_audience", com_google_android_gms_internal_zzvm_zza.atm);
                    zza(stringBuilder, i2, "current_data", com_google_android_gms_internal_zzvm_zza.atk);
                    zza(stringBuilder, i2, "previous_data", com_google_android_gms_internal_zzvm_zza.atl);
                    zza(stringBuilder, i2);
                    stringBuilder.append("}\n");
                }
            }
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, zzvm.zzb[] com_google_android_gms_internal_zzvm_zzbArr) {
        if (com_google_android_gms_internal_zzvm_zzbArr != null) {
            int i2 = i + 1;
            for (zzvm.zzb com_google_android_gms_internal_zzvm_zzb : com_google_android_gms_internal_zzvm_zzbArr) {
                if (com_google_android_gms_internal_zzvm_zzb != null) {
                    zza(stringBuilder, i2);
                    stringBuilder.append("event {\n");
                    zza(stringBuilder, i2, "name", com_google_android_gms_internal_zzvm_zzb.name);
                    zza(stringBuilder, i2, "timestamp_millis", com_google_android_gms_internal_zzvm_zzb.atp);
                    zza(stringBuilder, i2, "previous_timestamp_millis", com_google_android_gms_internal_zzvm_zzb.atq);
                    zza(stringBuilder, i2, "count", com_google_android_gms_internal_zzvm_zzb.count);
                    zza(stringBuilder, i2, com_google_android_gms_internal_zzvm_zzb.ato);
                    zza(stringBuilder, i2);
                    stringBuilder.append("}\n");
                }
            }
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, zzvm.zzc[] com_google_android_gms_internal_zzvm_zzcArr) {
        if (com_google_android_gms_internal_zzvm_zzcArr != null) {
            int i2 = i + 1;
            for (zzvm.zzc com_google_android_gms_internal_zzvm_zzc : com_google_android_gms_internal_zzvm_zzcArr) {
                if (com_google_android_gms_internal_zzvm_zzc != null) {
                    zza(stringBuilder, i2);
                    stringBuilder.append("param {\n");
                    zza(stringBuilder, i2, "name", com_google_android_gms_internal_zzvm_zzc.name);
                    zza(stringBuilder, i2, "string_value", com_google_android_gms_internal_zzvm_zzc.Dr);
                    zza(stringBuilder, i2, "int_value", com_google_android_gms_internal_zzvm_zzc.ats);
                    zza(stringBuilder, i2, "double_value", com_google_android_gms_internal_zzvm_zzc.asx);
                    zza(stringBuilder, i2);
                    stringBuilder.append("}\n");
                }
            }
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, zzg[] com_google_android_gms_internal_zzvm_zzgArr) {
        if (com_google_android_gms_internal_zzvm_zzgArr != null) {
            int i2 = i + 1;
            for (zzg com_google_android_gms_internal_zzvm_zzg : com_google_android_gms_internal_zzvm_zzgArr) {
                if (com_google_android_gms_internal_zzvm_zzg != null) {
                    zza(stringBuilder, i2);
                    stringBuilder.append("user_property {\n");
                    zza(stringBuilder, i2, "set_timestamp_millis", com_google_android_gms_internal_zzvm_zzg.atX);
                    zza(stringBuilder, i2, "name", com_google_android_gms_internal_zzvm_zzg.name);
                    zza(stringBuilder, i2, "string_value", com_google_android_gms_internal_zzvm_zzg.Dr);
                    zza(stringBuilder, i2, "int_value", com_google_android_gms_internal_zzvm_zzg.ats);
                    zza(stringBuilder, i2, "double_value", com_google_android_gms_internal_zzvm_zzg.asx);
                    zza(stringBuilder, i2);
                    stringBuilder.append("}\n");
                }
            }
        }
    }

    public static boolean zza(Context context, String str, boolean z) {
        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager == null) {
                return false;
            }
            ActivityInfo receiverInfo = packageManager.getReceiverInfo(new ComponentName(context, str), 2);
            return (receiverInfo == null || !receiverInfo.enabled) ? false : !z || receiverInfo.exported;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static boolean zza(long[] jArr, int i) {
        return i < jArr.length * 64 && (jArr[i / 64] & (1 << (i % 64))) != 0;
    }

    public static long[] zza(BitSet bitSet) {
        int length = (bitSet.length() + 63) / 64;
        long[] jArr = new long[length];
        int i = 0;
        while (i < length) {
            jArr[i] = 0;
            int i2 = 0;
            while (i2 < 64 && (i * 64) + i2 < bitSet.length()) {
                if (bitSet.get((i * 64) + i2)) {
                    jArr[i] = jArr[i] | (1 << i2);
                }
                i2++;
            }
            i++;
        }
        return jArr;
    }

    public static String zzb(zzvm.zzd com_google_android_gms_internal_zzvm_zzd) {
        if (com_google_android_gms_internal_zzvm_zzd == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nbatch {\n");
        if (com_google_android_gms_internal_zzvm_zzd.att != null) {
            for (zzvm.zze com_google_android_gms_internal_zzvm_zze : com_google_android_gms_internal_zzvm_zzd.att) {
                if (com_google_android_gms_internal_zzvm_zze != null) {
                    zza(stringBuilder, 1, com_google_android_gms_internal_zzvm_zze);
                }
            }
        }
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    public static boolean zzbb(String str, String str2) {
        return (str == null && str2 == null) ? true : str == null ? false : str.equals(str2);
    }

    static MessageDigest zzfi(String str) {
        int i = 0;
        while (i < 2) {
            try {
                MessageDigest instance = MessageDigest.getInstance(str);
                if (instance != null) {
                    return instance;
                }
                i++;
            } catch (NoSuchAlgorithmException e) {
            }
        }
        return null;
    }

    static boolean zzmx(String str) {
        zzac.zzhz(str);
        return str.charAt(0) != '_';
    }

    private int zzng(String str) {
        return "_ldl".equals(str) ? zzbvi().zzbtt() : zzbvi().zzbts();
    }

    public static boolean zznh(String str) {
        return !TextUtils.isEmpty(str) && str.startsWith("_");
    }

    static boolean zznj(String str) {
        return str != null && str.matches("(\\+|-)?([0-9]+\\.?[0-9]*|[0-9]*\\.?[0-9]+)") && str.length() <= 310;
    }

    public static boolean zzq(Context context, String str) {
        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager == null) {
                return false;
            }
            ServiceInfo serviceInfo = packageManager.getServiceInfo(new ComponentName(context, str), 4);
            return serviceInfo != null && serviceInfo.enabled;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    static long zzx(byte[] bArr) {
        long j = null;
        zzac.zzy(bArr);
        zzac.zzbr(bArr.length > 0);
        long j2 = 0;
        int length = bArr.length - 1;
        while (length >= 0 && length >= bArr.length - 8) {
            j2 += (((long) bArr[length]) & 255) << j;
            j += 8;
            length--;
        }
        return j2;
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public Bundle zza(String str, Bundle bundle, @Nullable List<String> list, boolean z) {
        if (bundle == null) {
            return null;
        }
        Bundle bundle2 = new Bundle(bundle);
        int zzbtm = zzbvi().zzbtm();
        int i = 0;
        for (String str2 : bundle.keySet()) {
            int zznc;
            if (list == null || !list.contains(str2)) {
                zznc = z ? zznc(str2) : 0;
                if (zznc == 0) {
                    zznc = zznd(str2);
                }
            } else {
                zznc = 0;
            }
            if (zznc != 0) {
                if (zzd(bundle2, zznc)) {
                    bundle2.putString("_ev", zza(str2, zzbvi().zzbtp(), true));
                    if (zznc == 3) {
                        zzb(bundle2, str2);
                    }
                }
                bundle2.remove(str2);
            } else if (zzk(str2, bundle.get(str2)) || "_ev".equals(str2)) {
                if (zzmx(str2)) {
                    i++;
                    if (i > zzbtm) {
                        zzbvg().zzbwc().zze("Event can't contain more then " + zzbtm + " params", str, bundle);
                        zzd(bundle2, 5);
                        bundle2.remove(str2);
                    }
                }
                i = i;
            } else {
                if (zzd(bundle2, 4)) {
                    bundle2.putString("_ev", zza(str2, zzbvi().zzbtp(), true));
                    zzb(bundle2, bundle.get(str2));
                }
                bundle2.remove(str2);
            }
        }
        return bundle2;
    }

    public String zza(String str, int i, boolean z) {
        return str.length() > i ? z ? String.valueOf(str.substring(0, i)).concat("...") : null : str;
    }

    public void zza(int i, String str, String str2, int i2) {
        Bundle bundle = new Bundle();
        zzd(bundle, i);
        if (!TextUtils.isEmpty(str)) {
            bundle.putString(str, str2);
        }
        if (i == 6 || i == 7 || i == 2) {
            bundle.putLong("_el", (long) i2);
        }
        this.anq.zzbux().zzf("auto", "_err", bundle);
    }

    public void zza(Bundle bundle, String str, Object obj) {
        if (bundle != null) {
            if (obj instanceof Long) {
                bundle.putLong(str, ((Long) obj).longValue());
            } else if (obj instanceof String) {
                bundle.putString(str, String.valueOf(obj));
            } else if (obj instanceof Double) {
                bundle.putDouble(str, ((Double) obj).doubleValue());
            } else if (str != null) {
                zzbvg().zzbwg().zze("Not putting event parameter. Invalid value type. name, type", str, obj != null ? obj.getClass().getSimpleName() : null);
            }
        }
    }

    public void zza(zzvm.zzc com_google_android_gms_internal_zzvm_zzc, Object obj) {
        zzac.zzy(obj);
        com_google_android_gms_internal_zzvm_zzc.Dr = null;
        com_google_android_gms_internal_zzvm_zzc.ats = null;
        com_google_android_gms_internal_zzvm_zzc.asx = null;
        if (obj instanceof String) {
            com_google_android_gms_internal_zzvm_zzc.Dr = (String) obj;
        } else if (obj instanceof Long) {
            com_google_android_gms_internal_zzvm_zzc.ats = (Long) obj;
        } else if (obj instanceof Double) {
            com_google_android_gms_internal_zzvm_zzc.asx = (Double) obj;
        } else {
            zzbvg().zzbwc().zzj("Ignoring invalid (type) event param value", obj);
        }
    }

    public void zza(zzg com_google_android_gms_internal_zzvm_zzg, Object obj) {
        zzac.zzy(obj);
        com_google_android_gms_internal_zzvm_zzg.Dr = null;
        com_google_android_gms_internal_zzvm_zzg.ats = null;
        com_google_android_gms_internal_zzvm_zzg.asx = null;
        if (obj instanceof String) {
            com_google_android_gms_internal_zzvm_zzg.Dr = (String) obj;
        } else if (obj instanceof Long) {
            com_google_android_gms_internal_zzvm_zzg.ats = (Long) obj;
        } else if (obj instanceof Double) {
            com_google_android_gms_internal_zzvm_zzg.asx = (Double) obj;
        } else {
            zzbvg().zzbwc().zzj("Ignoring invalid (type) user attribute value", obj);
        }
    }

    boolean zza(String str, String str2, int i, Object obj) {
        if (obj == null || (obj instanceof Long) || (obj instanceof Float) || (obj instanceof Integer) || (obj instanceof Byte) || (obj instanceof Short) || (obj instanceof Boolean) || (obj instanceof Double)) {
            return true;
        }
        if (!(obj instanceof String) && !(obj instanceof Character) && !(obj instanceof CharSequence)) {
            return false;
        }
        String valueOf = String.valueOf(obj);
        if (valueOf.length() <= i) {
            return true;
        }
        zzbvg().zzbwe().zzd("Value is too long; discarded. Value kind, name, value length", str, str2, Integer.valueOf(valueOf.length()));
        return false;
    }

    public byte[] zza(zzvm.zzd com_google_android_gms_internal_zzvm_zzd) {
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzvm_zzd.db()];
            zzard zzbe = zzard.zzbe(bArr);
            com_google_android_gms_internal_zzvm_zzd.zza(zzbe);
            zzbe.cO();
            return bArr;
        } catch (IOException e) {
            zzbvg().zzbwc().zzj("Data loss. Failed to serialize batch", e);
            return null;
        }
    }

    public /* bridge */ /* synthetic */ void zzaam() {
        super.zzaam();
    }

    public /* bridge */ /* synthetic */ com.google.android.gms.common.util.zze zzaan() {
        return super.zzaan();
    }

    boolean zzaz(String str, String str2) {
        if (str2 == null) {
            zzbvg().zzbwc().zzj("Name is required and can't be null. Type", str);
            return false;
        } else if (str2.length() == 0) {
            zzbvg().zzbwc().zzj("Name is required and can't be empty. Type", str);
            return false;
        } else if (Character.isLetter(str2.charAt(0))) {
            int i = 1;
            while (i < str2.length()) {
                char charAt = str2.charAt(i);
                if (charAt == '_' || Character.isLetterOrDigit(charAt)) {
                    i++;
                } else {
                    zzbvg().zzbwc().zze("Name must consist of letters, digits or _ (underscores). Type, name", str, str2);
                    return false;
                }
            }
            return true;
        } else {
            zzbvg().zzbwc().zze("Name must start with a letter. Type, name", str, str2);
            return false;
        }
    }

    public void zzb(Bundle bundle, Object obj) {
        zzac.zzy(bundle);
        if (obj == null) {
            return;
        }
        if ((obj instanceof String) || (obj instanceof CharSequence)) {
            bundle.putLong("_el", (long) String.valueOf(obj).length());
        }
    }

    boolean zzba(String str, String str2) {
        if (str2 == null) {
            zzbvg().zzbwc().zzj("Name is required and can't be null. Type", str);
            return false;
        } else if (str2.length() == 0) {
            zzbvg().zzbwc().zzj("Name is required and can't be empty. Type", str);
            return false;
        } else {
            char charAt = str2.charAt(0);
            if (Character.isLetter(charAt) || charAt == '_') {
                int i = 1;
                while (i < str2.length()) {
                    char charAt2 = str2.charAt(i);
                    if (charAt2 == '_' || Character.isLetterOrDigit(charAt2)) {
                        i++;
                    } else {
                        zzbvg().zzbwc().zze("Name must consist of letters, digits or _ (underscores). Type, name", str, str2);
                        return false;
                    }
                }
                return true;
            }
            zzbvg().zzbwc().zze("Name must start with a letter or _ (underscores). Type, name", str, str2);
            return false;
        }
    }

    public /* bridge */ /* synthetic */ void zzbuv() {
        super.zzbuv();
    }

    public /* bridge */ /* synthetic */ zzc zzbuw() {
        return super.zzbuw();
    }

    public /* bridge */ /* synthetic */ zzac zzbux() {
        return super.zzbux();
    }

    public /* bridge */ /* synthetic */ zzn zzbuy() {
        return super.zzbuy();
    }

    public /* bridge */ /* synthetic */ zzg zzbuz() {
        return super.zzbuz();
    }

    public /* bridge */ /* synthetic */ zzad zzbva() {
        return super.zzbva();
    }

    public /* bridge */ /* synthetic */ zze zzbvb() {
        return super.zzbvb();
    }

    public /* bridge */ /* synthetic */ zzal zzbvc() {
        return super.zzbvc();
    }

    public /* bridge */ /* synthetic */ zzv zzbvd() {
        return super.zzbvd();
    }

    public /* bridge */ /* synthetic */ zzaf zzbve() {
        return super.zzbve();
    }

    public /* bridge */ /* synthetic */ zzw zzbvf() {
        return super.zzbvf();
    }

    public /* bridge */ /* synthetic */ zzp zzbvg() {
        return super.zzbvg();
    }

    public /* bridge */ /* synthetic */ zzt zzbvh() {
        return super.zzbvh();
    }

    public /* bridge */ /* synthetic */ zzd zzbvi() {
        return super.zzbvi();
    }

    boolean zzc(String str, int i, String str2) {
        if (str2 == null) {
            zzbvg().zzbwc().zzj("Name is required and can't be null. Type", str);
            return false;
        } else if (str2.length() <= i) {
            return true;
        } else {
            zzbvg().zzbwc().zzd("Name is too long. Type, maximum supported length, name", str, Integer.valueOf(i), str2);
            return false;
        }
    }

    boolean zzc(String str, Map<String, String> map, String str2) {
        if (str2 == null) {
            zzbvg().zzbwc().zzj("Name is required and can't be null. Type", str);
            return false;
        } else if (str2.startsWith("firebase_")) {
            zzbvg().zzbwc().zze("Name starts with reserved prefix. Type, name", str, str2);
            return false;
        } else if (map == null || !map.containsKey(str2)) {
            return true;
        } else {
            zzbvg().zzbwc().zze("Name is reserved. Type, name", str, str2);
            return false;
        }
    }

    public boolean zzd(Bundle bundle, int i) {
        if (bundle == null || bundle.getLong("_err") != 0) {
            return false;
        }
        bundle.putLong("_err", (long) i);
        return true;
    }

    @WorkerThread
    public boolean zzew(String str) {
        zzyl();
        if (getContext().checkCallingOrSelfPermission(str) == 0) {
            return true;
        }
        zzbvg().zzbwi().zzj("Permission not granted", str);
        return false;
    }

    public boolean zzg(long j, long j2) {
        return j == 0 || j2 <= 0 || Math.abs(zzaan().currentTimeMillis() - j) > j2;
    }

    public byte[] zzj(byte[] bArr) throws IOException {
        try {
            OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gZIPOutputStream.write(bArr);
            gZIPOutputStream.close();
            byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            zzbvg().zzbwc().zzj("Failed to gzip content", e);
            throw e;
        }
    }

    public boolean zzk(String str, Object obj) {
        return zznh(str) ? zza("param", str, zzbvi().zzbtr(), obj) : zza("param", str, zzbvi().zzbtq(), obj);
    }

    public Object zzl(String str, Object obj) {
        if ("_ev".equals(str)) {
            return zza(zzbvi().zzbtr(), obj, true);
        }
        return zza(zznh(str) ? zzbvi().zzbtr() : zzbvi().zzbtq(), obj, false);
    }

    public int zzm(String str, Object obj) {
        return "_ldl".equals(str) ? zza("user property referrer", str, zzng(str), obj) : zza("user property", str, zzng(str), obj) ? 0 : 7;
    }

    public int zzmy(String str) {
        return !zzaz("event", str) ? 2 : !zzc("event", AppMeasurement.zza.anr, str) ? 13 : zzc("event", zzbvi().zzbtn(), str) ? 0 : 2;
    }

    public int zzmz(String str) {
        return !zzba("event", str) ? 2 : !zzc("event", AppMeasurement.zza.anr, str) ? 13 : zzc("event", zzbvi().zzbtn(), str) ? 0 : 2;
    }

    public Object zzn(String str, Object obj) {
        return "_ldl".equals(str) ? zza(zzng(str), obj, true) : zza(zzng(str), obj, false);
    }

    public int zzna(String str) {
        return !zzaz("user property", str) ? 6 : !zzc("user property", AppMeasurement.zze.ant, str) ? 15 : zzc("user property", zzbvi().zzbto(), str) ? 0 : 6;
    }

    public int zznb(String str) {
        return !zzba("user property", str) ? 6 : !zzc("user property", AppMeasurement.zze.ant, str) ? 15 : zzc("user property", zzbvi().zzbto(), str) ? 0 : 6;
    }

    public int zznc(String str) {
        return !zzaz("event param", str) ? 3 : !zzc("event param", null, str) ? 14 : zzc("event param", zzbvi().zzbtp(), str) ? 0 : 3;
    }

    public int zznd(String str) {
        return !zzba("event param", str) ? 3 : !zzc("event param", null, str) ? 14 : zzc("event param", zzbvi().zzbtp(), str) ? 0 : 3;
    }

    public boolean zzne(String str) {
        if (TextUtils.isEmpty(str)) {
            zzbvg().zzbwc().log("Measurement Service called without google_app_id");
            return false;
        } else if (!str.startsWith("1:")) {
            zzbvg().zzbwe().zzj("Measurement Service called with unknown id version", str);
            return true;
        } else if (zznf(str)) {
            return true;
        } else {
            zzbvg().zzbwc().zzj("Invalid google_app_id. Firebase Analytics disabled. See", "https://goo.gl/FZRIUV");
            return false;
        }
    }

    boolean zznf(String str) {
        zzac.zzy(str);
        return str.matches("^1:\\d+:android:[a-f0-9]+$");
    }

    public boolean zzni(String str) {
        return TextUtils.isEmpty(str) ? false : zzbvi().zzbuu().equals(str);
    }

    public Bundle zzt(@NonNull Uri uri) {
        Bundle bundle = null;
        if (uri != null) {
            try {
                Object queryParameter;
                Object queryParameter2;
                Object queryParameter3;
                Object queryParameter4;
                if (uri.isHierarchical()) {
                    queryParameter = uri.getQueryParameter("utm_campaign");
                    queryParameter2 = uri.getQueryParameter("utm_source");
                    queryParameter3 = uri.getQueryParameter("utm_medium");
                    queryParameter4 = uri.getQueryParameter("gclid");
                } else {
                    queryParameter4 = null;
                    queryParameter3 = null;
                    queryParameter2 = null;
                    queryParameter = null;
                }
                if (!(TextUtils.isEmpty(queryParameter) && TextUtils.isEmpty(queryParameter2) && TextUtils.isEmpty(queryParameter3) && TextUtils.isEmpty(queryParameter4))) {
                    bundle = new Bundle();
                    if (!TextUtils.isEmpty(queryParameter)) {
                        bundle.putString("campaign", queryParameter);
                    }
                    if (!TextUtils.isEmpty(queryParameter2)) {
                        bundle.putString("source", queryParameter2);
                    }
                    if (!TextUtils.isEmpty(queryParameter3)) {
                        bundle.putString("medium", queryParameter3);
                    }
                    if (!TextUtils.isEmpty(queryParameter4)) {
                        bundle.putString("gclid", queryParameter4);
                    }
                    queryParameter4 = uri.getQueryParameter("utm_term");
                    if (!TextUtils.isEmpty(queryParameter4)) {
                        bundle.putString("term", queryParameter4);
                    }
                    queryParameter4 = uri.getQueryParameter("utm_content");
                    if (!TextUtils.isEmpty(queryParameter4)) {
                        bundle.putString("content", queryParameter4);
                    }
                    queryParameter4 = uri.getQueryParameter("aclid");
                    if (!TextUtils.isEmpty(queryParameter4)) {
                        bundle.putString("aclid", queryParameter4);
                    }
                    queryParameter4 = uri.getQueryParameter("cp1");
                    if (!TextUtils.isEmpty(queryParameter4)) {
                        bundle.putString("cp1", queryParameter4);
                    }
                    queryParameter4 = uri.getQueryParameter("anid");
                    if (!TextUtils.isEmpty(queryParameter4)) {
                        bundle.putString("anid", queryParameter4);
                    }
                }
            } catch (UnsupportedOperationException e) {
                zzbvg().zzbwe().zzj("Install referrer url isn't a hierarchical URI", e);
            }
        }
        return bundle;
    }

    public byte[] zzw(byte[] bArr) throws IOException {
        try {
            InputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
            GZIPInputStream gZIPInputStream = new GZIPInputStream(byteArrayInputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bArr2 = new byte[1024];
            while (true) {
                int read = gZIPInputStream.read(bArr2);
                if (read <= 0) {
                    gZIPInputStream.close();
                    byteArrayInputStream.close();
                    return byteArrayOutputStream.toByteArray();
                }
                byteArrayOutputStream.write(bArr2, 0, read);
            }
        } catch (IOException e) {
            zzbvg().zzbwc().zzj("Failed to ungzip content", e);
            throw e;
        }
    }

    public long zzy(byte[] bArr) {
        zzac.zzy(bArr);
        MessageDigest zzfi = zzfi("MD5");
        if (zzfi != null) {
            return zzx(zzfi.digest(bArr));
        }
        zzbvg().zzbwc().log("Failed to get MD5");
        return 0;
    }

    public /* bridge */ /* synthetic */ void zzyl() {
        super.zzyl();
    }
}

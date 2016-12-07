package com.google.android.gms.measurement.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.internal.zzart;
import com.google.android.gms.internal.zzwa.zzb;
import com.google.android.gms.internal.zzwa.zzc;
import com.google.android.gms.internal.zzwa.zzd;
import com.google.android.gms.internal.zzwa.zze;
import com.google.android.gms.internal.zzwa.zzf;
import com.google.android.gms.internal.zzwc;
import com.google.android.gms.internal.zzwc.zza;
import com.google.android.gms.internal.zzwc.zzg;
import com.google.android.gms.measurement.AppMeasurement;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.security.auth.x500.X500Principal;

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

    public static String zza(zzb com_google_android_gms_internal_zzwa_zzb) {
        int i = 0;
        if (com_google_android_gms_internal_zzwa_zzb == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nevent_filter {\n");
        zza(stringBuilder, 0, "filter_id", com_google_android_gms_internal_zzwa_zzb.awd);
        zza(stringBuilder, 0, "event_name", com_google_android_gms_internal_zzwa_zzb.awe);
        zza(stringBuilder, 1, "event_count_filter", com_google_android_gms_internal_zzwa_zzb.awh);
        stringBuilder.append("  filters {\n");
        zzc[] com_google_android_gms_internal_zzwa_zzcArr = com_google_android_gms_internal_zzwa_zzb.awf;
        int length = com_google_android_gms_internal_zzwa_zzcArr.length;
        while (i < length) {
            zza(stringBuilder, 2, com_google_android_gms_internal_zzwa_zzcArr[i]);
            i++;
        }
        zza(stringBuilder, 1);
        stringBuilder.append("}\n}\n");
        return stringBuilder.toString();
    }

    public static String zza(zze com_google_android_gms_internal_zzwa_zze) {
        if (com_google_android_gms_internal_zzwa_zze == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nproperty_filter {\n");
        zza(stringBuilder, 0, "filter_id", com_google_android_gms_internal_zzwa_zze.awd);
        zza(stringBuilder, 0, "property_name", com_google_android_gms_internal_zzwa_zze.awt);
        zza(stringBuilder, 1, com_google_android_gms_internal_zzwa_zze.awu);
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    private static void zza(StringBuilder stringBuilder, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            stringBuilder.append("  ");
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, zzc com_google_android_gms_internal_zzwa_zzc) {
        if (com_google_android_gms_internal_zzwa_zzc != null) {
            zza(stringBuilder, i);
            stringBuilder.append("filter {\n");
            zza(stringBuilder, i, "complement", com_google_android_gms_internal_zzwa_zzc.awl);
            zza(stringBuilder, i, "param_name", com_google_android_gms_internal_zzwa_zzc.awm);
            zza(stringBuilder, i + 1, "string_filter", com_google_android_gms_internal_zzwa_zzc.awj);
            zza(stringBuilder, i + 1, "number_filter", com_google_android_gms_internal_zzwa_zzc.awk);
            zza(stringBuilder, i);
            stringBuilder.append("}\n");
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, zzwc.zze com_google_android_gms_internal_zzwc_zze) {
        if (com_google_android_gms_internal_zzwc_zze != null) {
            zza(stringBuilder, i);
            stringBuilder.append("bundle {\n");
            zza(stringBuilder, i, "protocol_version", com_google_android_gms_internal_zzwc_zze.awU);
            zza(stringBuilder, i, "platform", com_google_android_gms_internal_zzwc_zze.axc);
            zza(stringBuilder, i, "gmp_version", com_google_android_gms_internal_zzwc_zze.axg);
            zza(stringBuilder, i, "uploading_gmp_version", com_google_android_gms_internal_zzwc_zze.axh);
            zza(stringBuilder, i, "config_version", com_google_android_gms_internal_zzwc_zze.axt);
            zza(stringBuilder, i, "gmp_app_id", com_google_android_gms_internal_zzwc_zze.aqZ);
            zza(stringBuilder, i, "app_id", com_google_android_gms_internal_zzwc_zze.zzcs);
            zza(stringBuilder, i, "app_version", com_google_android_gms_internal_zzwc_zze.aii);
            zza(stringBuilder, i, "app_version_major", com_google_android_gms_internal_zzwc_zze.axp);
            zza(stringBuilder, i, "firebase_instance_id", com_google_android_gms_internal_zzwc_zze.arh);
            zza(stringBuilder, i, "dev_cert_hash", com_google_android_gms_internal_zzwc_zze.axl);
            zza(stringBuilder, i, "app_store", com_google_android_gms_internal_zzwc_zze.ara);
            zza(stringBuilder, i, "upload_timestamp_millis", com_google_android_gms_internal_zzwc_zze.awX);
            zza(stringBuilder, i, "start_timestamp_millis", com_google_android_gms_internal_zzwc_zze.awY);
            zza(stringBuilder, i, "end_timestamp_millis", com_google_android_gms_internal_zzwc_zze.awZ);
            zza(stringBuilder, i, "previous_bundle_start_timestamp_millis", com_google_android_gms_internal_zzwc_zze.axa);
            zza(stringBuilder, i, "previous_bundle_end_timestamp_millis", com_google_android_gms_internal_zzwc_zze.axb);
            zza(stringBuilder, i, "app_instance_id", com_google_android_gms_internal_zzwc_zze.axk);
            zza(stringBuilder, i, "resettable_device_id", com_google_android_gms_internal_zzwc_zze.axi);
            zza(stringBuilder, i, "device_id", com_google_android_gms_internal_zzwc_zze.axs);
            zza(stringBuilder, i, "limited_ad_tracking", com_google_android_gms_internal_zzwc_zze.axj);
            zza(stringBuilder, i, "os_version", com_google_android_gms_internal_zzwc_zze.zzdb);
            zza(stringBuilder, i, "device_model", com_google_android_gms_internal_zzwc_zze.axd);
            zza(stringBuilder, i, "user_default_language", com_google_android_gms_internal_zzwc_zze.axe);
            zza(stringBuilder, i, "time_zone_offset_minutes", com_google_android_gms_internal_zzwc_zze.axf);
            zza(stringBuilder, i, "bundle_sequential_index", com_google_android_gms_internal_zzwc_zze.axm);
            zza(stringBuilder, i, "service_upload", com_google_android_gms_internal_zzwc_zze.axn);
            zza(stringBuilder, i, "health_monitor", com_google_android_gms_internal_zzwc_zze.ard);
            zza(stringBuilder, i, com_google_android_gms_internal_zzwc_zze.awW);
            zza(stringBuilder, i, com_google_android_gms_internal_zzwc_zze.axo);
            zza(stringBuilder, i, com_google_android_gms_internal_zzwc_zze.awV);
            zza(stringBuilder, i);
            stringBuilder.append("}\n");
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, String str, zzd com_google_android_gms_internal_zzwa_zzd) {
        if (com_google_android_gms_internal_zzwa_zzd != null) {
            zza(stringBuilder, i);
            stringBuilder.append(str);
            stringBuilder.append(" {\n");
            if (com_google_android_gms_internal_zzwa_zzd.awn != null) {
                Object obj = "UNKNOWN_COMPARISON_TYPE";
                switch (com_google_android_gms_internal_zzwa_zzd.awn.intValue()) {
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
            zza(stringBuilder, i, "match_as_float", com_google_android_gms_internal_zzwa_zzd.awo);
            zza(stringBuilder, i, "comparison_value", com_google_android_gms_internal_zzwa_zzd.awp);
            zza(stringBuilder, i, "min_comparison_value", com_google_android_gms_internal_zzwa_zzd.awq);
            zza(stringBuilder, i, "max_comparison_value", com_google_android_gms_internal_zzwa_zzd.awr);
            zza(stringBuilder, i);
            stringBuilder.append("}\n");
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, String str, zzf com_google_android_gms_internal_zzwa_zzf) {
        if (com_google_android_gms_internal_zzwa_zzf != null) {
            zza(stringBuilder, i);
            stringBuilder.append(str);
            stringBuilder.append(" {\n");
            if (com_google_android_gms_internal_zzwa_zzf.awv != null) {
                Object obj = "UNKNOWN_MATCH_TYPE";
                switch (com_google_android_gms_internal_zzwa_zzf.awv.intValue()) {
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
            zza(stringBuilder, i, "expression", com_google_android_gms_internal_zzwa_zzf.aww);
            zza(stringBuilder, i, "case_sensitive", com_google_android_gms_internal_zzwa_zzf.awx);
            if (com_google_android_gms_internal_zzwa_zzf.awy.length > 0) {
                zza(stringBuilder, i + 1);
                stringBuilder.append("expression_list {\n");
                for (String str2 : com_google_android_gms_internal_zzwa_zzf.awy) {
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

    private static void zza(StringBuilder stringBuilder, int i, String str, zzwc.zzf com_google_android_gms_internal_zzwc_zzf) {
        int i2 = 0;
        if (com_google_android_gms_internal_zzwc_zzf != null) {
            int i3;
            int i4;
            int i5 = i + 1;
            zza(stringBuilder, i5);
            stringBuilder.append(str);
            stringBuilder.append(" {\n");
            if (com_google_android_gms_internal_zzwc_zzf.axv != null) {
                zza(stringBuilder, i5 + 1);
                stringBuilder.append("results: ");
                long[] jArr = com_google_android_gms_internal_zzwc_zzf.axv;
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
            if (com_google_android_gms_internal_zzwc_zzf.axu != null) {
                zza(stringBuilder, i5 + 1);
                stringBuilder.append("status: ");
                long[] jArr2 = com_google_android_gms_internal_zzwc_zzf.axu;
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

    private static void zza(StringBuilder stringBuilder, int i, zza[] com_google_android_gms_internal_zzwc_zzaArr) {
        if (com_google_android_gms_internal_zzwc_zzaArr != null) {
            int i2 = i + 1;
            for (zza com_google_android_gms_internal_zzwc_zza : com_google_android_gms_internal_zzwc_zzaArr) {
                if (com_google_android_gms_internal_zzwc_zza != null) {
                    zza(stringBuilder, i2);
                    stringBuilder.append("audience_membership {\n");
                    zza(stringBuilder, i2, "audience_id", com_google_android_gms_internal_zzwc_zza.avZ);
                    zza(stringBuilder, i2, "new_audience", com_google_android_gms_internal_zzwc_zza.awL);
                    zza(stringBuilder, i2, "current_data", com_google_android_gms_internal_zzwc_zza.awJ);
                    zza(stringBuilder, i2, "previous_data", com_google_android_gms_internal_zzwc_zza.awK);
                    zza(stringBuilder, i2);
                    stringBuilder.append("}\n");
                }
            }
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, zzwc.zzb[] com_google_android_gms_internal_zzwc_zzbArr) {
        if (com_google_android_gms_internal_zzwc_zzbArr != null) {
            int i2 = i + 1;
            for (zzwc.zzb com_google_android_gms_internal_zzwc_zzb : com_google_android_gms_internal_zzwc_zzbArr) {
                if (com_google_android_gms_internal_zzwc_zzb != null) {
                    zza(stringBuilder, i2);
                    stringBuilder.append("event {\n");
                    zza(stringBuilder, i2, "name", com_google_android_gms_internal_zzwc_zzb.name);
                    zza(stringBuilder, i2, "timestamp_millis", com_google_android_gms_internal_zzwc_zzb.awO);
                    zza(stringBuilder, i2, "previous_timestamp_millis", com_google_android_gms_internal_zzwc_zzb.awP);
                    zza(stringBuilder, i2, "count", com_google_android_gms_internal_zzwc_zzb.count);
                    zza(stringBuilder, i2, com_google_android_gms_internal_zzwc_zzb.awN);
                    zza(stringBuilder, i2);
                    stringBuilder.append("}\n");
                }
            }
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, zzwc.zzc[] com_google_android_gms_internal_zzwc_zzcArr) {
        if (com_google_android_gms_internal_zzwc_zzcArr != null) {
            int i2 = i + 1;
            for (zzwc.zzc com_google_android_gms_internal_zzwc_zzc : com_google_android_gms_internal_zzwc_zzcArr) {
                if (com_google_android_gms_internal_zzwc_zzc != null) {
                    zza(stringBuilder, i2);
                    stringBuilder.append("param {\n");
                    zza(stringBuilder, i2, "name", com_google_android_gms_internal_zzwc_zzc.name);
                    zza(stringBuilder, i2, "string_value", com_google_android_gms_internal_zzwc_zzc.Fe);
                    zza(stringBuilder, i2, "int_value", com_google_android_gms_internal_zzwc_zzc.awR);
                    zza(stringBuilder, i2, "double_value", com_google_android_gms_internal_zzwc_zzc.avW);
                    zza(stringBuilder, i2);
                    stringBuilder.append("}\n");
                }
            }
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, zzg[] com_google_android_gms_internal_zzwc_zzgArr) {
        if (com_google_android_gms_internal_zzwc_zzgArr != null) {
            int i2 = i + 1;
            for (zzg com_google_android_gms_internal_zzwc_zzg : com_google_android_gms_internal_zzwc_zzgArr) {
                if (com_google_android_gms_internal_zzwc_zzg != null) {
                    zza(stringBuilder, i2);
                    stringBuilder.append("user_property {\n");
                    zza(stringBuilder, i2, "set_timestamp_millis", com_google_android_gms_internal_zzwc_zzg.axx);
                    zza(stringBuilder, i2, "name", com_google_android_gms_internal_zzwc_zzg.name);
                    zza(stringBuilder, i2, "string_value", com_google_android_gms_internal_zzwc_zzg.Fe);
                    zza(stringBuilder, i2, "int_value", com_google_android_gms_internal_zzwc_zzg.awR);
                    zza(stringBuilder, i2, "double_value", com_google_android_gms_internal_zzwc_zzg.avW);
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

    public static String zzb(zzwc.zzd com_google_android_gms_internal_zzwc_zzd) {
        if (com_google_android_gms_internal_zzwc_zzd == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nbatch {\n");
        if (com_google_android_gms_internal_zzwc_zzd.awS != null) {
            for (zzwc.zze com_google_android_gms_internal_zzwc_zze : com_google_android_gms_internal_zzwc_zzd.awS) {
                if (com_google_android_gms_internal_zzwc_zze != null) {
                    zza(stringBuilder, 1, com_google_android_gms_internal_zzwc_zze);
                }
            }
        }
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    public static boolean zzbb(String str, String str2) {
        return (str == null && str2 == null) ? true : str == null ? false : str.equals(str2);
    }

    @WorkerThread
    static boolean zzc(EventParcel eventParcel, AppMetadata appMetadata) {
        zzaa.zzy(eventParcel);
        zzaa.zzy(appMetadata);
        return !TextUtils.isEmpty(appMetadata.aqZ) || "_in".equals(eventParcel.name);
    }

    static MessageDigest zzfl(String str) {
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

    static boolean zzmu(String str) {
        zzaa.zzib(str);
        return str.charAt(0) != '_';
    }

    private int zznd(String str) {
        return "_ldl".equals(str) ? zzbwd().zzbuj() : zzbwd().zzbui();
    }

    public static boolean zzne(String str) {
        return !TextUtils.isEmpty(str) && str.startsWith("_");
    }

    static boolean zzng(String str) {
        return str != null && str.matches("(\\+|-)?([0-9]+\\.?[0-9]*|[0-9]*\\.?[0-9]+)") && str.length() <= 310;
    }

    public static boolean zzr(Context context, String str) {
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

    static long zzy(byte[] bArr) {
        long j = null;
        zzaa.zzy(bArr);
        zzaa.zzbs(bArr.length > 0);
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
        zzbwd().zzbuc();
        int i = 0;
        for (String str2 : bundle.keySet()) {
            int zzmz;
            if (list == null || !list.contains(str2)) {
                zzmz = z ? zzmz(str2) : 0;
                if (zzmz == 0) {
                    zzmz = zzna(str2);
                }
            } else {
                zzmz = 0;
            }
            if (zzmz != 0) {
                if (zzd(bundle2, zzmz)) {
                    bundle2.putString("_ev", zza(str2, zzbwd().zzbuf(), true));
                    if (zzmz == 3) {
                        zzb(bundle2, str2);
                    }
                }
                bundle2.remove(str2);
            } else if (zzk(str2, bundle.get(str2)) || "_ev".equals(str2)) {
                if (zzmu(str2)) {
                    i++;
                    if (i > 25) {
                        zzbwb().zzbwy().zze("Event can't contain more then " + 25 + " params", str, bundle);
                        zzd(bundle2, 5);
                        bundle2.remove(str2);
                    }
                }
                i = i;
            } else {
                if (zzd(bundle2, 4)) {
                    bundle2.putString("_ev", zza(str2, zzbwd().zzbuf(), true));
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
        this.aqw.zzbvq().zzf("auto", "_err", bundle);
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
                zzbwb().zzbxb().zze("Not putting event parameter. Invalid value type. name, type", str, obj != null ? obj.getClass().getSimpleName() : null);
            }
        }
    }

    public void zza(zzwc.zzc com_google_android_gms_internal_zzwc_zzc, Object obj) {
        zzaa.zzy(obj);
        com_google_android_gms_internal_zzwc_zzc.Fe = null;
        com_google_android_gms_internal_zzwc_zzc.awR = null;
        com_google_android_gms_internal_zzwc_zzc.avW = null;
        if (obj instanceof String) {
            com_google_android_gms_internal_zzwc_zzc.Fe = (String) obj;
        } else if (obj instanceof Long) {
            com_google_android_gms_internal_zzwc_zzc.awR = (Long) obj;
        } else if (obj instanceof Double) {
            com_google_android_gms_internal_zzwc_zzc.avW = (Double) obj;
        } else {
            zzbwb().zzbwy().zzj("Ignoring invalid (type) event param value", obj);
        }
    }

    public void zza(zzg com_google_android_gms_internal_zzwc_zzg, Object obj) {
        zzaa.zzy(obj);
        com_google_android_gms_internal_zzwc_zzg.Fe = null;
        com_google_android_gms_internal_zzwc_zzg.awR = null;
        com_google_android_gms_internal_zzwc_zzg.avW = null;
        if (obj instanceof String) {
            com_google_android_gms_internal_zzwc_zzg.Fe = (String) obj;
        } else if (obj instanceof Long) {
            com_google_android_gms_internal_zzwc_zzg.awR = (Long) obj;
        } else if (obj instanceof Double) {
            com_google_android_gms_internal_zzwc_zzg.avW = (Double) obj;
        } else {
            zzbwb().zzbwy().zzj("Ignoring invalid (type) user attribute value", obj);
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
        zzbwb().zzbxa().zzd("Value is too long; discarded. Value kind, name, value length", str, str2, Integer.valueOf(valueOf.length()));
        return false;
    }

    public byte[] zza(zzwc.zzd com_google_android_gms_internal_zzwc_zzd) {
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzwc_zzd.cz()];
            zzart zzbe = zzart.zzbe(bArr);
            com_google_android_gms_internal_zzwc_zzd.zza(zzbe);
            zzbe.cm();
            return bArr;
        } catch (IOException e) {
            zzbwb().zzbwy().zzj("Data loss. Failed to serialize batch", e);
            return null;
        }
    }

    public /* bridge */ /* synthetic */ void zzaby() {
        super.zzaby();
    }

    public /* bridge */ /* synthetic */ com.google.android.gms.common.util.zze zzabz() {
        return super.zzabz();
    }

    @WorkerThread
    long zzad(Context context, String str) {
        zzzx();
        zzaa.zzy(context);
        zzaa.zzib(str);
        PackageManager packageManager = context.getPackageManager();
        MessageDigest zzfl = zzfl("MD5");
        if (zzfl == null) {
            zzbwb().zzbwy().log("Could not get MD5 instance");
            return -1;
        }
        if (packageManager != null) {
            try {
                if (!zzae(context, str)) {
                    PackageInfo packageInfo = packageManager.getPackageInfo(getContext().getPackageName(), 64);
                    if (packageInfo.signatures != null && packageInfo.signatures.length > 0) {
                        return zzy(zzfl.digest(packageInfo.signatures[0].toByteArray()));
                    }
                    zzbwb().zzbxa().log("Could not get signatures");
                    return -1;
                }
            } catch (NameNotFoundException e) {
                zzbwb().zzbwy().zzj("Package name not found", e);
            }
        }
        return 0;
    }

    boolean zzae(Context context, String str) {
        X500Principal x500Principal = new X500Principal("CN=Android Debug,O=Android,C=US");
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(str, 64);
            if (!(packageInfo == null || packageInfo.signatures == null || packageInfo.signatures.length <= 0)) {
                return ((X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(packageInfo.signatures[0].toByteArray()))).getSubjectX500Principal().equals(x500Principal);
            }
        } catch (CertificateException e) {
            zzbwb().zzbwy().zzj("Error obtaining certificate", e);
        } catch (NameNotFoundException e2) {
            zzbwb().zzbwy().zzj("Package name not found", e2);
        }
        return true;
    }

    boolean zzaz(String str, String str2) {
        if (str2 == null) {
            zzbwb().zzbwy().zzj("Name is required and can't be null. Type", str);
            return false;
        } else if (str2.length() == 0) {
            zzbwb().zzbwy().zzj("Name is required and can't be empty. Type", str);
            return false;
        } else if (Character.isLetter(str2.charAt(0))) {
            int i = 1;
            while (i < str2.length()) {
                char charAt = str2.charAt(i);
                if (charAt == '_' || Character.isLetterOrDigit(charAt)) {
                    i++;
                } else {
                    zzbwb().zzbwy().zze("Name must consist of letters, digits or _ (underscores). Type, name", str, str2);
                    return false;
                }
            }
            return true;
        } else {
            zzbwb().zzbwy().zze("Name must start with a letter. Type, name", str, str2);
            return false;
        }
    }

    public void zzb(Bundle bundle, Object obj) {
        zzaa.zzy(bundle);
        if (obj == null) {
            return;
        }
        if ((obj instanceof String) || (obj instanceof CharSequence)) {
            bundle.putLong("_el", (long) String.valueOf(obj).length());
        }
    }

    boolean zzba(String str, String str2) {
        if (str2 == null) {
            zzbwb().zzbwy().zzj("Name is required and can't be null. Type", str);
            return false;
        } else if (str2.length() == 0) {
            zzbwb().zzbwy().zzj("Name is required and can't be empty. Type", str);
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
                        zzbwb().zzbwy().zze("Name must consist of letters, digits or _ (underscores). Type, name", str, str2);
                        return false;
                    }
                }
                return true;
            }
            zzbwb().zzbwy().zze("Name must start with a letter or _ (underscores). Type, name", str, str2);
            return false;
        }
    }

    public /* bridge */ /* synthetic */ void zzbvo() {
        super.zzbvo();
    }

    public /* bridge */ /* synthetic */ zzc zzbvp() {
        return super.zzbvp();
    }

    public /* bridge */ /* synthetic */ zzac zzbvq() {
        return super.zzbvq();
    }

    public /* bridge */ /* synthetic */ zzn zzbvr() {
        return super.zzbvr();
    }

    public /* bridge */ /* synthetic */ zzg zzbvs() {
        return super.zzbvs();
    }

    public /* bridge */ /* synthetic */ zzae zzbvt() {
        return super.zzbvt();
    }

    public /* bridge */ /* synthetic */ zzad zzbvu() {
        return super.zzbvu();
    }

    public /* bridge */ /* synthetic */ zzo zzbvv() {
        return super.zzbvv();
    }

    public /* bridge */ /* synthetic */ zze zzbvw() {
        return super.zzbvw();
    }

    public /* bridge */ /* synthetic */ zzal zzbvx() {
        return super.zzbvx();
    }

    public /* bridge */ /* synthetic */ zzv zzbvy() {
        return super.zzbvy();
    }

    public /* bridge */ /* synthetic */ zzag zzbvz() {
        return super.zzbvz();
    }

    public /* bridge */ /* synthetic */ zzw zzbwa() {
        return super.zzbwa();
    }

    public /* bridge */ /* synthetic */ zzq zzbwb() {
        return super.zzbwb();
    }

    public /* bridge */ /* synthetic */ zzt zzbwc() {
        return super.zzbwc();
    }

    public /* bridge */ /* synthetic */ zzd zzbwd() {
        return super.zzbwd();
    }

    boolean zzc(String str, int i, String str2) {
        if (str2 == null) {
            zzbwb().zzbwy().zzj("Name is required and can't be null. Type", str);
            return false;
        } else if (str2.length() <= i) {
            return true;
        } else {
            zzbwb().zzbwy().zzd("Name is too long. Type, maximum supported length, name", str, Integer.valueOf(i), str2);
            return false;
        }
    }

    boolean zzc(String str, Map<String, String> map, String str2) {
        if (str2 == null) {
            zzbwb().zzbwy().zzj("Name is required and can't be null. Type", str);
            return false;
        } else if (str2.startsWith("firebase_")) {
            zzbwb().zzbwy().zze("Name starts with reserved prefix. Type, name", str, str2);
            return false;
        } else if (map == null || !map.containsKey(str2)) {
            return true;
        } else {
            zzbwb().zzbwy().zze("Name is reserved. Type, name", str, str2);
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
    public boolean zzez(String str) {
        zzzx();
        if (getContext().checkCallingOrSelfPermission(str) == 0) {
            return true;
        }
        zzbwb().zzbxd().zzj("Permission not granted", str);
        return false;
    }

    public boolean zzf(long j, long j2) {
        return j == 0 || j2 <= 0 || Math.abs(zzabz().currentTimeMillis() - j) > j2;
    }

    public boolean zzk(String str, Object obj) {
        return zzne(str) ? zza("param", str, zzbwd().zzbuh(), obj) : zza("param", str, zzbwd().zzbug(), obj);
    }

    public byte[] zzk(byte[] bArr) throws IOException {
        try {
            OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gZIPOutputStream.write(bArr);
            gZIPOutputStream.close();
            byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            zzbwb().zzbwy().zzj("Failed to gzip content", e);
            throw e;
        }
    }

    public Object zzl(String str, Object obj) {
        if ("_ev".equals(str)) {
            return zza(zzbwd().zzbuh(), obj, true);
        }
        return zza(zzne(str) ? zzbwd().zzbuh() : zzbwd().zzbug(), obj, false);
    }

    public int zzm(String str, Object obj) {
        return "_ldl".equals(str) ? zza("user property referrer", str, zznd(str), obj) : zza("user property", str, zznd(str), obj) ? 0 : 7;
    }

    public int zzmv(String str) {
        return !zzaz("event", str) ? 2 : !zzc("event", AppMeasurement.zza.aqx, str) ? 13 : zzc("event", zzbwd().zzbud(), str) ? 0 : 2;
    }

    public int zzmw(String str) {
        return !zzba("event", str) ? 2 : !zzc("event", AppMeasurement.zza.aqx, str) ? 13 : zzc("event", zzbwd().zzbud(), str) ? 0 : 2;
    }

    public int zzmx(String str) {
        return !zzaz("user property", str) ? 6 : !zzc("user property", AppMeasurement.zzg.aqC, str) ? 15 : zzc("user property", zzbwd().zzbue(), str) ? 0 : 6;
    }

    public int zzmy(String str) {
        return !zzba("user property", str) ? 6 : !zzc("user property", AppMeasurement.zzg.aqC, str) ? 15 : zzc("user property", zzbwd().zzbue(), str) ? 0 : 6;
    }

    public int zzmz(String str) {
        return !zzaz("event param", str) ? 3 : !zzc("event param", null, str) ? 14 : zzc("event param", zzbwd().zzbuf(), str) ? 0 : 3;
    }

    public Object zzn(String str, Object obj) {
        return "_ldl".equals(str) ? zza(zznd(str), obj, true) : zza(zznd(str), obj, false);
    }

    public int zzna(String str) {
        return !zzba("event param", str) ? 3 : !zzc("event param", null, str) ? 14 : zzc("event param", zzbwd().zzbuf(), str) ? 0 : 3;
    }

    public boolean zznb(String str) {
        if (TextUtils.isEmpty(str)) {
            zzbwb().zzbwy().log("Measurement Service called without google_app_id");
            return false;
        } else if (!str.matches("^\\d+:.*")) {
            zzbwb().zzbxa().zzj("Measurement Service called with invalid id version", str);
            return false;
        } else if (!str.startsWith("1:")) {
            zzbwb().zzbxa().zzj("Measurement Service called with unknown id version", str);
            return true;
        } else if (zznc(str)) {
            return true;
        } else {
            zzbwb().zzbwy().zzj("Invalid google_app_id. Firebase Analytics disabled. See", "https://goo.gl/FZRIUV");
            return false;
        }
    }

    boolean zznc(String str) {
        zzaa.zzy(str);
        return str.matches("^1:\\d+:android:[a-f0-9]+$");
    }

    public boolean zznf(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        String zzbvn = zzbwd().zzbvn();
        zzbwd().zzayi();
        return zzbvn.equals(str);
    }

    boolean zznh(String str) {
        return "1".equals(zzbvy().zzaw(str, "measurement.upload.blacklist_internal"));
    }

    boolean zzni(String str) {
        return "1".equals(zzbvy().zzaw(str, "measurement.upload.blacklist_public"));
    }

    public Bundle zzu(@NonNull Uri uri) {
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
                zzbwb().zzbxa().zzj("Install referrer url isn't a hierarchical URI", e);
            }
        }
        return bundle;
    }

    public byte[] zzx(byte[] bArr) throws IOException {
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
            zzbwb().zzbwy().zzj("Failed to ungzip content", e);
            throw e;
        }
    }

    @WorkerThread
    public long zzz(byte[] bArr) {
        zzaa.zzy(bArr);
        zzzx();
        MessageDigest zzfl = zzfl("MD5");
        if (zzfl != null) {
            return zzy(zzfl.digest(bArr));
        }
        zzbwb().zzbwy().log("Failed to get MD5");
        return 0;
    }

    public /* bridge */ /* synthetic */ void zzzx() {
        super.zzzx();
    }
}

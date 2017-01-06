package com.google.android.gms.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzauf.zzb;
import com.google.android.gms.internal.zzauf.zzc;
import com.google.android.gms.internal.zzauf.zzd;
import com.google.android.gms.internal.zzauf.zze;
import com.google.android.gms.internal.zzauf.zzf;
import com.google.android.gms.internal.zzauh.zza;
import com.google.android.gms.internal.zzauh.zzg;
import com.google.android.gms.measurement.AppMeasurement;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.security.auth.x500.X500Principal;
import org.telegram.messenger.NotificationBadge.NewHtcHomeBadger;

public class zzaue extends zzats {
    private final AtomicLong zzbve = new AtomicLong(0);
    private int zzbvf;

    zzaue(zzatp com_google_android_gms_internal_zzatp) {
        super(com_google_android_gms_internal_zzatp);
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

    public static String zza(zzb com_google_android_gms_internal_zzauf_zzb) {
        int i = 0;
        if (com_google_android_gms_internal_zzauf_zzb == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nevent_filter {\n");
        zza(stringBuilder, 0, "filter_id", com_google_android_gms_internal_zzauf_zzb.zzbvl);
        zza(stringBuilder, 0, "event_name", com_google_android_gms_internal_zzauf_zzb.zzbvm);
        zza(stringBuilder, 1, "event_count_filter", com_google_android_gms_internal_zzauf_zzb.zzbvp);
        stringBuilder.append("  filters {\n");
        zzc[] com_google_android_gms_internal_zzauf_zzcArr = com_google_android_gms_internal_zzauf_zzb.zzbvn;
        int length = com_google_android_gms_internal_zzauf_zzcArr.length;
        while (i < length) {
            zza(stringBuilder, 2, com_google_android_gms_internal_zzauf_zzcArr[i]);
            i++;
        }
        zza(stringBuilder, 1);
        stringBuilder.append("}\n}\n");
        return stringBuilder.toString();
    }

    public static String zza(zze com_google_android_gms_internal_zzauf_zze) {
        if (com_google_android_gms_internal_zzauf_zze == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nproperty_filter {\n");
        zza(stringBuilder, 0, "filter_id", com_google_android_gms_internal_zzauf_zze.zzbvl);
        zza(stringBuilder, 0, "property_name", com_google_android_gms_internal_zzauf_zze.zzbvB);
        zza(stringBuilder, 1, com_google_android_gms_internal_zzauf_zze.zzbvC);
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    private static void zza(StringBuilder stringBuilder, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            stringBuilder.append("  ");
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, zzc com_google_android_gms_internal_zzauf_zzc) {
        if (com_google_android_gms_internal_zzauf_zzc != null) {
            zza(stringBuilder, i);
            stringBuilder.append("filter {\n");
            zza(stringBuilder, i, "complement", com_google_android_gms_internal_zzauf_zzc.zzbvt);
            zza(stringBuilder, i, "param_name", com_google_android_gms_internal_zzauf_zzc.zzbvu);
            zza(stringBuilder, i + 1, "string_filter", com_google_android_gms_internal_zzauf_zzc.zzbvr);
            zza(stringBuilder, i + 1, "number_filter", com_google_android_gms_internal_zzauf_zzc.zzbvs);
            zza(stringBuilder, i);
            stringBuilder.append("}\n");
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, zzauh.zze com_google_android_gms_internal_zzauh_zze) {
        if (com_google_android_gms_internal_zzauh_zze != null) {
            zza(stringBuilder, i);
            stringBuilder.append("bundle {\n");
            zza(stringBuilder, i, "protocol_version", com_google_android_gms_internal_zzauh_zze.zzbwc);
            zza(stringBuilder, i, "platform", com_google_android_gms_internal_zzauh_zze.zzbwk);
            zza(stringBuilder, i, "gmp_version", com_google_android_gms_internal_zzauh_zze.zzbwo);
            zza(stringBuilder, i, "uploading_gmp_version", com_google_android_gms_internal_zzauh_zze.zzbwp);
            zza(stringBuilder, i, "config_version", com_google_android_gms_internal_zzauh_zze.zzbwB);
            zza(stringBuilder, i, "gmp_app_id", com_google_android_gms_internal_zzauh_zze.zzbqf);
            zza(stringBuilder, i, "app_id", com_google_android_gms_internal_zzauh_zze.zzaR);
            zza(stringBuilder, i, "app_version", com_google_android_gms_internal_zzauh_zze.zzbhg);
            zza(stringBuilder, i, "app_version_major", com_google_android_gms_internal_zzauh_zze.zzbwx);
            zza(stringBuilder, i, "firebase_instance_id", com_google_android_gms_internal_zzauh_zze.zzbqn);
            zza(stringBuilder, i, "dev_cert_hash", com_google_android_gms_internal_zzauh_zze.zzbwt);
            zza(stringBuilder, i, "app_store", com_google_android_gms_internal_zzauh_zze.zzbqg);
            zza(stringBuilder, i, "upload_timestamp_millis", com_google_android_gms_internal_zzauh_zze.zzbwf);
            zza(stringBuilder, i, "start_timestamp_millis", com_google_android_gms_internal_zzauh_zze.zzbwg);
            zza(stringBuilder, i, "end_timestamp_millis", com_google_android_gms_internal_zzauh_zze.zzbwh);
            zza(stringBuilder, i, "previous_bundle_start_timestamp_millis", com_google_android_gms_internal_zzauh_zze.zzbwi);
            zza(stringBuilder, i, "previous_bundle_end_timestamp_millis", com_google_android_gms_internal_zzauh_zze.zzbwj);
            zza(stringBuilder, i, "app_instance_id", com_google_android_gms_internal_zzauh_zze.zzbws);
            zza(stringBuilder, i, "resettable_device_id", com_google_android_gms_internal_zzauh_zze.zzbwq);
            zza(stringBuilder, i, "device_id", com_google_android_gms_internal_zzauh_zze.zzbwA);
            zza(stringBuilder, i, "limited_ad_tracking", com_google_android_gms_internal_zzauh_zze.zzbwr);
            zza(stringBuilder, i, "os_version", com_google_android_gms_internal_zzauh_zze.zzba);
            zza(stringBuilder, i, "device_model", com_google_android_gms_internal_zzauh_zze.zzbwl);
            zza(stringBuilder, i, "user_default_language", com_google_android_gms_internal_zzauh_zze.zzbwm);
            zza(stringBuilder, i, "time_zone_offset_minutes", com_google_android_gms_internal_zzauh_zze.zzbwn);
            zza(stringBuilder, i, "bundle_sequential_index", com_google_android_gms_internal_zzauh_zze.zzbwu);
            zza(stringBuilder, i, "service_upload", com_google_android_gms_internal_zzauh_zze.zzbwv);
            zza(stringBuilder, i, "health_monitor", com_google_android_gms_internal_zzauh_zze.zzbqj);
            zza(stringBuilder, i, com_google_android_gms_internal_zzauh_zze.zzbwe);
            zza(stringBuilder, i, com_google_android_gms_internal_zzauh_zze.zzbww);
            zza(stringBuilder, i, com_google_android_gms_internal_zzauh_zze.zzbwd);
            zza(stringBuilder, i);
            stringBuilder.append("}\n");
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, String str, zzd com_google_android_gms_internal_zzauf_zzd) {
        if (com_google_android_gms_internal_zzauf_zzd != null) {
            zza(stringBuilder, i);
            stringBuilder.append(str);
            stringBuilder.append(" {\n");
            if (com_google_android_gms_internal_zzauf_zzd.zzbvv != null) {
                Object obj = "UNKNOWN_COMPARISON_TYPE";
                switch (com_google_android_gms_internal_zzauf_zzd.zzbvv.intValue()) {
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
            zza(stringBuilder, i, "match_as_float", com_google_android_gms_internal_zzauf_zzd.zzbvw);
            zza(stringBuilder, i, "comparison_value", com_google_android_gms_internal_zzauf_zzd.zzbvx);
            zza(stringBuilder, i, "min_comparison_value", com_google_android_gms_internal_zzauf_zzd.zzbvy);
            zza(stringBuilder, i, "max_comparison_value", com_google_android_gms_internal_zzauf_zzd.zzbvz);
            zza(stringBuilder, i);
            stringBuilder.append("}\n");
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, String str, zzf com_google_android_gms_internal_zzauf_zzf) {
        if (com_google_android_gms_internal_zzauf_zzf != null) {
            zza(stringBuilder, i);
            stringBuilder.append(str);
            stringBuilder.append(" {\n");
            if (com_google_android_gms_internal_zzauf_zzf.zzbvD != null) {
                Object obj = "UNKNOWN_MATCH_TYPE";
                switch (com_google_android_gms_internal_zzauf_zzf.zzbvD.intValue()) {
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
            zza(stringBuilder, i, "expression", com_google_android_gms_internal_zzauf_zzf.zzbvE);
            zza(stringBuilder, i, "case_sensitive", com_google_android_gms_internal_zzauf_zzf.zzbvF);
            if (com_google_android_gms_internal_zzauf_zzf.zzbvG.length > 0) {
                zza(stringBuilder, i + 1);
                stringBuilder.append("expression_list {\n");
                for (String str2 : com_google_android_gms_internal_zzauf_zzf.zzbvG) {
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

    private static void zza(StringBuilder stringBuilder, int i, String str, zzauh.zzf com_google_android_gms_internal_zzauh_zzf) {
        int i2 = 0;
        if (com_google_android_gms_internal_zzauh_zzf != null) {
            int i3;
            int i4;
            int i5 = i + 1;
            zza(stringBuilder, i5);
            stringBuilder.append(str);
            stringBuilder.append(" {\n");
            if (com_google_android_gms_internal_zzauh_zzf.zzbwD != null) {
                zza(stringBuilder, i5 + 1);
                stringBuilder.append("results: ");
                long[] jArr = com_google_android_gms_internal_zzauh_zzf.zzbwD;
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
            if (com_google_android_gms_internal_zzauh_zzf.zzbwC != null) {
                zza(stringBuilder, i5 + 1);
                stringBuilder.append("status: ");
                long[] jArr2 = com_google_android_gms_internal_zzauh_zzf.zzbwC;
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

    private static void zza(StringBuilder stringBuilder, int i, zza[] com_google_android_gms_internal_zzauh_zzaArr) {
        if (com_google_android_gms_internal_zzauh_zzaArr != null) {
            int i2 = i + 1;
            for (zza com_google_android_gms_internal_zzauh_zza : com_google_android_gms_internal_zzauh_zzaArr) {
                if (com_google_android_gms_internal_zzauh_zza != null) {
                    zza(stringBuilder, i2);
                    stringBuilder.append("audience_membership {\n");
                    zza(stringBuilder, i2, "audience_id", com_google_android_gms_internal_zzauh_zza.zzbvh);
                    zza(stringBuilder, i2, "new_audience", com_google_android_gms_internal_zzauh_zza.zzbvT);
                    zza(stringBuilder, i2, "current_data", com_google_android_gms_internal_zzauh_zza.zzbvR);
                    zza(stringBuilder, i2, "previous_data", com_google_android_gms_internal_zzauh_zza.zzbvS);
                    zza(stringBuilder, i2);
                    stringBuilder.append("}\n");
                }
            }
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, zzauh.zzb[] com_google_android_gms_internal_zzauh_zzbArr) {
        if (com_google_android_gms_internal_zzauh_zzbArr != null) {
            int i2 = i + 1;
            for (zzauh.zzb com_google_android_gms_internal_zzauh_zzb : com_google_android_gms_internal_zzauh_zzbArr) {
                if (com_google_android_gms_internal_zzauh_zzb != null) {
                    zza(stringBuilder, i2);
                    stringBuilder.append("event {\n");
                    zza(stringBuilder, i2, "name", com_google_android_gms_internal_zzauh_zzb.name);
                    zza(stringBuilder, i2, "timestamp_millis", com_google_android_gms_internal_zzauh_zzb.zzbvW);
                    zza(stringBuilder, i2, "previous_timestamp_millis", com_google_android_gms_internal_zzauh_zzb.zzbvX);
                    zza(stringBuilder, i2, NewHtcHomeBadger.COUNT, com_google_android_gms_internal_zzauh_zzb.count);
                    zza(stringBuilder, i2, com_google_android_gms_internal_zzauh_zzb.zzbvV);
                    zza(stringBuilder, i2);
                    stringBuilder.append("}\n");
                }
            }
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, zzauh.zzc[] com_google_android_gms_internal_zzauh_zzcArr) {
        if (com_google_android_gms_internal_zzauh_zzcArr != null) {
            int i2 = i + 1;
            for (zzauh.zzc com_google_android_gms_internal_zzauh_zzc : com_google_android_gms_internal_zzauh_zzcArr) {
                if (com_google_android_gms_internal_zzauh_zzc != null) {
                    zza(stringBuilder, i2);
                    stringBuilder.append("param {\n");
                    zza(stringBuilder, i2, "name", com_google_android_gms_internal_zzauh_zzc.name);
                    zza(stringBuilder, i2, "string_value", com_google_android_gms_internal_zzauh_zzc.zzaFy);
                    zza(stringBuilder, i2, "int_value", com_google_android_gms_internal_zzauh_zzc.zzbvZ);
                    zza(stringBuilder, i2, "double_value", com_google_android_gms_internal_zzauh_zzc.zzbvc);
                    zza(stringBuilder, i2);
                    stringBuilder.append("}\n");
                }
            }
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, zzg[] com_google_android_gms_internal_zzauh_zzgArr) {
        if (com_google_android_gms_internal_zzauh_zzgArr != null) {
            int i2 = i + 1;
            for (zzg com_google_android_gms_internal_zzauh_zzg : com_google_android_gms_internal_zzauh_zzgArr) {
                if (com_google_android_gms_internal_zzauh_zzg != null) {
                    zza(stringBuilder, i2);
                    stringBuilder.append("user_property {\n");
                    zza(stringBuilder, i2, "set_timestamp_millis", com_google_android_gms_internal_zzauh_zzg.zzbwF);
                    zza(stringBuilder, i2, "name", com_google_android_gms_internal_zzauh_zzg.name);
                    zza(stringBuilder, i2, "string_value", com_google_android_gms_internal_zzauh_zzg.zzaFy);
                    zza(stringBuilder, i2, "int_value", com_google_android_gms_internal_zzauh_zzg.zzbvZ);
                    zza(stringBuilder, i2, "double_value", com_google_android_gms_internal_zzauh_zzg.zzbvc);
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

    public static boolean zzab(String str, String str2) {
        return (str == null && str2 == null) ? true : str == null ? false : str.equals(str2);
    }

    public static String zzb(zzauh.zzd com_google_android_gms_internal_zzauh_zzd) {
        if (com_google_android_gms_internal_zzauh_zzd == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nbatch {\n");
        if (com_google_android_gms_internal_zzauh_zzd.zzbwa != null) {
            for (zzauh.zze com_google_android_gms_internal_zzauh_zze : com_google_android_gms_internal_zzauh_zzd.zzbwa) {
                if (com_google_android_gms_internal_zzauh_zze != null) {
                    zza(stringBuilder, 1, com_google_android_gms_internal_zzauh_zze);
                }
            }
        }
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    @WorkerThread
    static boolean zzc(zzatb com_google_android_gms_internal_zzatb, zzasq com_google_android_gms_internal_zzasq) {
        zzac.zzw(com_google_android_gms_internal_zzatb);
        zzac.zzw(com_google_android_gms_internal_zzasq);
        return !TextUtils.isEmpty(com_google_android_gms_internal_zzasq.zzbqf) || "_in".equals(com_google_android_gms_internal_zzatb.name);
    }

    static MessageDigest zzcg(String str) {
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

    static boolean zzfW(String str) {
        zzac.zzdv(str);
        return str.charAt(0) != '_';
    }

    private int zzgf(String str) {
        return "_ldl".equals(str) ? zzJv().zzKa() : zzJv().zzJZ();
    }

    public static boolean zzgg(String str) {
        return !TextUtils.isEmpty(str) && str.startsWith("_");
    }

    static boolean zzgi(String str) {
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
        zzac.zzw(bArr);
        zzac.zzar(bArr.length > 0);
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

    public boolean zzD(Intent intent) {
        String stringExtra = intent.getStringExtra("android.intent.extra.REFERRER_NAME");
        return "android-app://com.google.android.googlequicksearchbox/https/www.google.com".equals(stringExtra) || "https://www.google.com".equals(stringExtra) || "android-app://com.google.appcrawler".equals(stringExtra);
    }

    @WorkerThread
    long zzE(Context context, String str) {
        zzmq();
        zzac.zzw(context);
        zzac.zzdv(str);
        PackageManager packageManager = context.getPackageManager();
        MessageDigest zzcg = zzcg("MD5");
        if (zzcg == null) {
            zzJt().zzLa().log("Could not get MD5 instance");
            return -1;
        }
        if (packageManager != null) {
            try {
                if (!zzF(context, str)) {
                    PackageInfo packageInfo = zzacx.zzaQ(context).getPackageInfo(getContext().getPackageName(), 64);
                    if (packageInfo.signatures != null && packageInfo.signatures.length > 0) {
                        return zzy(zzcg.digest(packageInfo.signatures[0].toByteArray()));
                    }
                    zzJt().zzLc().log("Could not get signatures");
                    return -1;
                }
            } catch (NameNotFoundException e) {
                zzJt().zzLa().zzj("Package name not found", e);
            }
        }
        return 0;
    }

    boolean zzF(Context context, String str) {
        X500Principal x500Principal = new X500Principal("CN=Android Debug,O=Android,C=US");
        try {
            PackageInfo packageInfo = zzacx.zzaQ(context).getPackageInfo(str, 64);
            if (!(packageInfo == null || packageInfo.signatures == null || packageInfo.signatures.length <= 0)) {
                return ((X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(packageInfo.signatures[0].toByteArray()))).getSubjectX500Principal().equals(x500Principal);
            }
        } catch (CertificateException e) {
            zzJt().zzLa().zzj("Error obtaining certificate", e);
        } catch (NameNotFoundException e2) {
            zzJt().zzLa().zzj("Package name not found", e2);
        }
        return true;
    }

    public /* bridge */ /* synthetic */ void zzJd() {
        super.zzJd();
    }

    public /* bridge */ /* synthetic */ void zzJe() {
        super.zzJe();
    }

    public /* bridge */ /* synthetic */ void zzJf() {
        super.zzJf();
    }

    public /* bridge */ /* synthetic */ zzaso zzJg() {
        return super.zzJg();
    }

    public /* bridge */ /* synthetic */ zzass zzJh() {
        return super.zzJh();
    }

    public /* bridge */ /* synthetic */ zzatu zzJi() {
        return super.zzJi();
    }

    public /* bridge */ /* synthetic */ zzatf zzJj() {
        return super.zzJj();
    }

    public /* bridge */ /* synthetic */ zzasw zzJk() {
        return super.zzJk();
    }

    public /* bridge */ /* synthetic */ zzatw zzJl() {
        return super.zzJl();
    }

    public /* bridge */ /* synthetic */ zzatv zzJm() {
        return super.zzJm();
    }

    public /* bridge */ /* synthetic */ zzatg zzJn() {
        return super.zzJn();
    }

    public /* bridge */ /* synthetic */ zzasu zzJo() {
        return super.zzJo();
    }

    public /* bridge */ /* synthetic */ zzaue zzJp() {
        return super.zzJp();
    }

    public /* bridge */ /* synthetic */ zzatn zzJq() {
        return super.zzJq();
    }

    public /* bridge */ /* synthetic */ zzaty zzJr() {
        return super.zzJr();
    }

    public /* bridge */ /* synthetic */ zzato zzJs() {
        return super.zzJs();
    }

    public /* bridge */ /* synthetic */ zzati zzJt() {
        return super.zzJt();
    }

    public /* bridge */ /* synthetic */ zzatl zzJu() {
        return super.zzJu();
    }

    public /* bridge */ /* synthetic */ zzast zzJv() {
        return super.zzJv();
    }

    public long zzMi() {
        long nextLong;
        if (this.zzbve.get() == 0) {
            synchronized (this.zzbve) {
                nextLong = new Random(System.nanoTime() ^ zznq().currentTimeMillis()).nextLong();
                int i = this.zzbvf + 1;
                this.zzbvf = i;
                nextLong += (long) i;
            }
        } else {
            synchronized (this.zzbve) {
                this.zzbve.compareAndSet(-1, 1);
                nextLong = this.zzbve.getAndIncrement();
            }
        }
        return nextLong;
    }

    boolean zzZ(String str, String str2) {
        if (str2 == null) {
            zzJt().zzLa().zzj("Name is required and can't be null. Type", str);
            return false;
        } else if (str2.length() == 0) {
            zzJt().zzLa().zzj("Name is required and can't be empty. Type", str);
            return false;
        } else {
            int codePointAt = str2.codePointAt(0);
            if (Character.isLetter(codePointAt)) {
                int length = str2.length();
                codePointAt = Character.charCount(codePointAt);
                while (codePointAt < length) {
                    int codePointAt2 = str2.codePointAt(codePointAt);
                    if (codePointAt2 == 95 || Character.isLetterOrDigit(codePointAt2)) {
                        codePointAt += Character.charCount(codePointAt2);
                    } else {
                        zzJt().zzLa().zze("Name must consist of letters, digits or _ (underscores). Type, name", str, str2);
                        return false;
                    }
                }
                return true;
            }
            zzJt().zzLa().zze("Name must start with a letter. Type, name", str, str2);
            return false;
        }
    }

    public Bundle zza(String str, Bundle bundle, @Nullable List<String> list, boolean z) {
        if (bundle == null) {
            return null;
        }
        Bundle bundle2 = new Bundle(bundle);
        zzJv().zzJT();
        int i = 0;
        for (String str2 : bundle.keySet()) {
            int zzgb;
            if (list == null || !list.contains(str2)) {
                zzgb = z ? zzgb(str2) : 0;
                if (zzgb == 0) {
                    zzgb = zzgc(str2);
                }
            } else {
                zzgb = 0;
            }
            if (zzgb != 0) {
                if (zzd(bundle2, zzgb)) {
                    bundle2.putString("_ev", zza(str2, zzJv().zzJW(), true));
                    if (zzgb == 3) {
                        zzb(bundle2, str2);
                    }
                }
                bundle2.remove(str2);
            } else if (zzk(str2, bundle.get(str2)) || "_ev".equals(str2)) {
                if (zzfW(str2)) {
                    i++;
                    if (i > 25) {
                        zzJt().zzLa().zze("Event can't contain more then " + 25 + " params", str, bundle);
                        zzd(bundle2, 5);
                        bundle2.remove(str2);
                    }
                }
                i = i;
            } else {
                if (zzd(bundle2, 4)) {
                    bundle2.putString("_ev", zza(str2, zzJv().zzJW(), true));
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
        zza(null, i, str, str2, i2);
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
                zzJt().zzLd().zze("Not putting event parameter. Invalid value type. name, type", str, obj != null ? obj.getClass().getSimpleName() : null);
            }
        }
    }

    public void zza(zzauh.zzc com_google_android_gms_internal_zzauh_zzc, Object obj) {
        zzac.zzw(obj);
        com_google_android_gms_internal_zzauh_zzc.zzaFy = null;
        com_google_android_gms_internal_zzauh_zzc.zzbvZ = null;
        com_google_android_gms_internal_zzauh_zzc.zzbvc = null;
        if (obj instanceof String) {
            com_google_android_gms_internal_zzauh_zzc.zzaFy = (String) obj;
        } else if (obj instanceof Long) {
            com_google_android_gms_internal_zzauh_zzc.zzbvZ = (Long) obj;
        } else if (obj instanceof Double) {
            com_google_android_gms_internal_zzauh_zzc.zzbvc = (Double) obj;
        } else {
            zzJt().zzLa().zzj("Ignoring invalid (type) event param value", obj);
        }
    }

    public void zza(zzg com_google_android_gms_internal_zzauh_zzg, Object obj) {
        zzac.zzw(obj);
        com_google_android_gms_internal_zzauh_zzg.zzaFy = null;
        com_google_android_gms_internal_zzauh_zzg.zzbvZ = null;
        com_google_android_gms_internal_zzauh_zzg.zzbvc = null;
        if (obj instanceof String) {
            com_google_android_gms_internal_zzauh_zzg.zzaFy = (String) obj;
        } else if (obj instanceof Long) {
            com_google_android_gms_internal_zzauh_zzg.zzbvZ = (Long) obj;
        } else if (obj instanceof Double) {
            com_google_android_gms_internal_zzauh_zzg.zzbvc = (Double) obj;
        } else {
            zzJt().zzLa().zzj("Ignoring invalid (type) user attribute value", obj);
        }
    }

    public void zza(String str, int i, String str2, String str3, int i2) {
        Bundle bundle = new Bundle();
        zzd(bundle, i);
        if (!TextUtils.isEmpty(str2)) {
            bundle.putString(str2, str3);
        }
        if (i == 6 || i == 7 || i == 2) {
            bundle.putLong("_el", (long) i2);
        }
        this.zzbpw.zzJv().zzKk();
        this.zzbpw.zzJi().zze("auto", "_err", bundle);
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
        zzJt().zzLc().zzd("Value is too long; discarded. Value kind, name, value length", str, str2, Integer.valueOf(valueOf.length()));
        return false;
    }

    public byte[] zza(zzauh.zzd com_google_android_gms_internal_zzauh_zzd) {
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzauh_zzd.zzacZ()];
            zzbum zzae = zzbum.zzae(bArr);
            com_google_android_gms_internal_zzauh_zzd.zza(zzae);
            zzae.zzacM();
            return bArr;
        } catch (IOException e) {
            zzJt().zzLa().zzj("Data loss. Failed to serialize batch", e);
            return null;
        }
    }

    boolean zzaa(String str, String str2) {
        if (str2 == null) {
            zzJt().zzLa().zzj("Name is required and can't be null. Type", str);
            return false;
        } else if (str2.length() == 0) {
            zzJt().zzLa().zzj("Name is required and can't be empty. Type", str);
            return false;
        } else {
            int codePointAt = str2.codePointAt(0);
            if (Character.isLetter(codePointAt) || codePointAt == 95) {
                int length = str2.length();
                codePointAt = Character.charCount(codePointAt);
                while (codePointAt < length) {
                    int codePointAt2 = str2.codePointAt(codePointAt);
                    if (codePointAt2 == 95 || Character.isLetterOrDigit(codePointAt2)) {
                        codePointAt += Character.charCount(codePointAt2);
                    } else {
                        zzJt().zzLa().zze("Name must consist of letters, digits or _ (underscores). Type, name", str, str2);
                        return false;
                    }
                }
                return true;
            }
            zzJt().zzLa().zze("Name must start with a letter or _ (underscore). Type, name", str, str2);
            return false;
        }
    }

    public void zzb(Bundle bundle, Object obj) {
        zzac.zzw(bundle);
        if (obj == null) {
            return;
        }
        if ((obj instanceof String) || (obj instanceof CharSequence)) {
            bundle.putLong("_el", (long) String.valueOf(obj).length());
        }
    }

    @WorkerThread
    public boolean zzbV(String str) {
        zzmq();
        if (zzacx.zzaQ(getContext()).checkCallingOrSelfPermission(str) == 0) {
            return true;
        }
        zzJt().zzLf().zzj("Permission not granted", str);
        return false;
    }

    boolean zzc(String str, int i, String str2) {
        if (str2 == null) {
            zzJt().zzLa().zzj("Name is required and can't be null. Type", str);
            return false;
        } else if (str2.length() <= i) {
            return true;
        } else {
            zzJt().zzLa().zzd("Name is too long. Type, maximum supported length, name", str, Integer.valueOf(i), str2);
            return false;
        }
    }

    boolean zzc(String str, Map<String, String> map, String str2) {
        if (str2 == null) {
            zzJt().zzLa().zzj("Name is required and can't be null. Type", str);
            return false;
        } else if (str2.startsWith("firebase_")) {
            zzJt().zzLa().zze("Name starts with reserved prefix. Type, name", str, str2);
            return false;
        } else if (map == null || !map.containsKey(str2)) {
            return true;
        } else {
            zzJt().zzLa().zze("Name is reserved. Type, name", str, str2);
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

    public boolean zzf(long j, long j2) {
        return j == 0 || j2 <= 0 || Math.abs(zznq().currentTimeMillis() - j) > j2;
    }

    public int zzfX(String str) {
        return !zzZ("event", str) ? 2 : !zzc("event", AppMeasurement.zza.zzbpx, str) ? 13 : zzc("event", zzJv().zzJU(), str) ? 0 : 2;
    }

    public int zzfY(String str) {
        return !zzaa("event", str) ? 2 : !zzc("event", AppMeasurement.zza.zzbpx, str) ? 13 : zzc("event", zzJv().zzJU(), str) ? 0 : 2;
    }

    public int zzfZ(String str) {
        return !zzZ("user property", str) ? 6 : !zzc("user property", AppMeasurement.zzg.zzbpC, str) ? 15 : zzc("user property", zzJv().zzJV(), str) ? 0 : 6;
    }

    public int zzga(String str) {
        return !zzaa("user property", str) ? 6 : !zzc("user property", AppMeasurement.zzg.zzbpC, str) ? 15 : zzc("user property", zzJv().zzJV(), str) ? 0 : 6;
    }

    public int zzgb(String str) {
        return !zzZ("event param", str) ? 3 : !zzc("event param", null, str) ? 14 : zzc("event param", zzJv().zzJW(), str) ? 0 : 3;
    }

    public int zzgc(String str) {
        return !zzaa("event param", str) ? 3 : !zzc("event param", null, str) ? 14 : zzc("event param", zzJv().zzJW(), str) ? 0 : 3;
    }

    public boolean zzgd(String str) {
        if (TextUtils.isEmpty(str)) {
            zzJt().zzLa().log("Missing google_app_id. Firebase Analytics disabled. See https://goo.gl/NAOOOI");
            return false;
        } else if (zzge(str)) {
            return true;
        } else {
            zzJt().zzLa().zzj("Invalid google_app_id. Firebase Analytics disabled. See https://goo.gl/NAOOOI. provided id", str);
            return false;
        }
    }

    boolean zzge(String str) {
        zzac.zzw(str);
        return str.matches("^1:\\d+:android:[a-f0-9]+$");
    }

    public boolean zzgh(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        String zzKF = zzJv().zzKF();
        zzJv().zzKk();
        return zzKF.equals(str);
    }

    boolean zzgj(String str) {
        return "1".equals(zzJq().zzW(str, "measurement.upload.blacklist_internal"));
    }

    boolean zzgk(String str) {
        return "1".equals(zzJq().zzW(str, "measurement.upload.blacklist_public"));
    }

    public boolean zzk(String str, Object obj) {
        return zzgg(str) ? zza("param", str, zzJv().zzJY(), obj) : zza("param", str, zzJv().zzJX(), obj);
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
            zzJt().zzLa().zzj("Failed to gzip content", e);
            throw e;
        }
    }

    public Object zzl(String str, Object obj) {
        if ("_ev".equals(str)) {
            return zza(zzJv().zzJY(), obj, true);
        }
        return zza(zzgg(str) ? zzJv().zzJY() : zzJv().zzJX(), obj, false);
    }

    public int zzm(String str, Object obj) {
        return "_ldl".equals(str) ? zza("user property referrer", str, zzgf(str), obj) : zza("user property", str, zzgf(str), obj) ? 0 : 7;
    }

    public /* bridge */ /* synthetic */ void zzmq() {
        super.zzmq();
    }

    protected void zzmr() {
        SecureRandom secureRandom = new SecureRandom();
        long nextLong = secureRandom.nextLong();
        if (nextLong == 0) {
            nextLong = secureRandom.nextLong();
            if (nextLong == 0) {
                zzJt().zzLc().log("Utils falling back to Random for random id");
            }
        }
        this.zzbve.set(nextLong);
    }

    public Object zzn(String str, Object obj) {
        return "_ldl".equals(str) ? zza(zzgf(str), obj, true) : zza(zzgf(str), obj, false);
    }

    public /* bridge */ /* synthetic */ com.google.android.gms.common.util.zze zznq() {
        return super.zznq();
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
                zzJt().zzLc().zzj("Install referrer url isn't a hierarchical URI", e);
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
            zzJt().zzLa().zzj("Failed to ungzip content", e);
            throw e;
        }
    }

    @WorkerThread
    public long zzz(byte[] bArr) {
        zzac.zzw(bArr);
        zzmq();
        MessageDigest zzcg = zzcg("MD5");
        if (zzcg != null) {
            return zzy(zzcg.digest(bArr));
        }
        zzJt().zzLa().log("Failed to get MD5");
        return 0;
    }
}

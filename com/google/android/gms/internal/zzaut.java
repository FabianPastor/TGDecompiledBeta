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
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzauu.zzb;
import com.google.android.gms.internal.zzauu.zzc;
import com.google.android.gms.internal.zzauu.zzd;
import com.google.android.gms.internal.zzauu.zze;
import com.google.android.gms.internal.zzauu.zzf;
import com.google.android.gms.internal.zzauw.zza;
import com.google.android.gms.internal.zzauw.zzg;
import com.google.android.gms.measurement.AppMeasurement;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

public class zzaut extends zzauh {
    private final AtomicLong zzbwh = new AtomicLong(0);
    private int zzbwi;

    zzaut(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
    }

    public static Object zzH(Object obj) {
        ObjectOutputStream objectOutputStream;
        Throwable th;
        if (obj == null) {
            return null;
        }
        ObjectInputStream objectInputStream;
        try {
            OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            try {
                objectOutputStream.writeObject(obj);
                objectOutputStream.flush();
                objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
            } catch (Throwable th2) {
                th = th2;
                objectInputStream = null;
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
                throw th;
            }
            try {
                Object readObject = objectInputStream.readObject();
                try {
                    objectOutputStream.close();
                    objectInputStream.close();
                    return readObject;
                } catch (IOException e) {
                    return null;
                } catch (ClassNotFoundException e2) {
                    return null;
                }
            } catch (Throwable th3) {
                th = th3;
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            objectInputStream = null;
            objectOutputStream = null;
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
            if (objectInputStream != null) {
                objectInputStream.close();
            }
            throw th;
        }
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

    public static String zza(zzb com_google_android_gms_internal_zzauu_zzb) {
        int i = 0;
        if (com_google_android_gms_internal_zzauu_zzb == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nevent_filter {\n");
        zza(stringBuilder, 0, "filter_id", com_google_android_gms_internal_zzauu_zzb.zzbwo);
        zza(stringBuilder, 0, "event_name", com_google_android_gms_internal_zzauu_zzb.zzbwp);
        zza(stringBuilder, 1, "event_count_filter", com_google_android_gms_internal_zzauu_zzb.zzbws);
        stringBuilder.append("  filters {\n");
        zzc[] com_google_android_gms_internal_zzauu_zzcArr = com_google_android_gms_internal_zzauu_zzb.zzbwq;
        int length = com_google_android_gms_internal_zzauu_zzcArr.length;
        while (i < length) {
            zza(stringBuilder, 2, com_google_android_gms_internal_zzauu_zzcArr[i]);
            i++;
        }
        zza(stringBuilder, 1);
        stringBuilder.append("}\n}\n");
        return stringBuilder.toString();
    }

    public static String zza(zze com_google_android_gms_internal_zzauu_zze) {
        if (com_google_android_gms_internal_zzauu_zze == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nproperty_filter {\n");
        zza(stringBuilder, 0, "filter_id", com_google_android_gms_internal_zzauu_zze.zzbwo);
        zza(stringBuilder, 0, "property_name", com_google_android_gms_internal_zzauu_zze.zzbwE);
        zza(stringBuilder, 1, com_google_android_gms_internal_zzauu_zze.zzbwF);
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    private static void zza(StringBuilder stringBuilder, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            stringBuilder.append("  ");
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, zzc com_google_android_gms_internal_zzauu_zzc) {
        if (com_google_android_gms_internal_zzauu_zzc != null) {
            zza(stringBuilder, i);
            stringBuilder.append("filter {\n");
            zza(stringBuilder, i, "complement", com_google_android_gms_internal_zzauu_zzc.zzbww);
            zza(stringBuilder, i, "param_name", com_google_android_gms_internal_zzauu_zzc.zzbwx);
            zza(stringBuilder, i + 1, "string_filter", com_google_android_gms_internal_zzauu_zzc.zzbwu);
            zza(stringBuilder, i + 1, "number_filter", com_google_android_gms_internal_zzauu_zzc.zzbwv);
            zza(stringBuilder, i);
            stringBuilder.append("}\n");
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, zzauw.zze com_google_android_gms_internal_zzauw_zze) {
        if (com_google_android_gms_internal_zzauw_zze != null) {
            zza(stringBuilder, i);
            stringBuilder.append("bundle {\n");
            zza(stringBuilder, i, "protocol_version", com_google_android_gms_internal_zzauw_zze.zzbxf);
            zza(stringBuilder, i, "platform", com_google_android_gms_internal_zzauw_zze.zzbxn);
            zza(stringBuilder, i, "gmp_version", com_google_android_gms_internal_zzauw_zze.zzbxr);
            zza(stringBuilder, i, "uploading_gmp_version", com_google_android_gms_internal_zzauw_zze.zzbxs);
            zza(stringBuilder, i, "config_version", com_google_android_gms_internal_zzauw_zze.zzbxE);
            zza(stringBuilder, i, "gmp_app_id", com_google_android_gms_internal_zzauw_zze.zzbqL);
            zza(stringBuilder, i, "app_id", com_google_android_gms_internal_zzauw_zze.zzaS);
            zza(stringBuilder, i, "app_version", com_google_android_gms_internal_zzauw_zze.zzbhN);
            zza(stringBuilder, i, "app_version_major", com_google_android_gms_internal_zzauw_zze.zzbxA);
            zza(stringBuilder, i, "app_version_minor", com_google_android_gms_internal_zzauw_zze.zzbxB);
            zza(stringBuilder, i, "app_version_release", com_google_android_gms_internal_zzauw_zze.zzbxC);
            zza(stringBuilder, i, "firebase_instance_id", com_google_android_gms_internal_zzauw_zze.zzbqT);
            zza(stringBuilder, i, "dev_cert_hash", com_google_android_gms_internal_zzauw_zze.zzbxw);
            zza(stringBuilder, i, "app_store", com_google_android_gms_internal_zzauw_zze.zzbqM);
            zza(stringBuilder, i, "upload_timestamp_millis", com_google_android_gms_internal_zzauw_zze.zzbxi);
            zza(stringBuilder, i, "start_timestamp_millis", com_google_android_gms_internal_zzauw_zze.zzbxj);
            zza(stringBuilder, i, "end_timestamp_millis", com_google_android_gms_internal_zzauw_zze.zzbxk);
            zza(stringBuilder, i, "previous_bundle_start_timestamp_millis", com_google_android_gms_internal_zzauw_zze.zzbxl);
            zza(stringBuilder, i, "previous_bundle_end_timestamp_millis", com_google_android_gms_internal_zzauw_zze.zzbxm);
            zza(stringBuilder, i, "app_instance_id", com_google_android_gms_internal_zzauw_zze.zzbxv);
            zza(stringBuilder, i, "resettable_device_id", com_google_android_gms_internal_zzauw_zze.zzbxt);
            zza(stringBuilder, i, "device_id", com_google_android_gms_internal_zzauw_zze.zzbxD);
            zza(stringBuilder, i, "limited_ad_tracking", com_google_android_gms_internal_zzauw_zze.zzbxu);
            zza(stringBuilder, i, "os_version", com_google_android_gms_internal_zzauw_zze.zzbb);
            zza(stringBuilder, i, "device_model", com_google_android_gms_internal_zzauw_zze.zzbxo);
            zza(stringBuilder, i, "user_default_language", com_google_android_gms_internal_zzauw_zze.zzbxp);
            zza(stringBuilder, i, "time_zone_offset_minutes", com_google_android_gms_internal_zzauw_zze.zzbxq);
            zza(stringBuilder, i, "bundle_sequential_index", com_google_android_gms_internal_zzauw_zze.zzbxx);
            zza(stringBuilder, i, "service_upload", com_google_android_gms_internal_zzauw_zze.zzbxy);
            zza(stringBuilder, i, "health_monitor", com_google_android_gms_internal_zzauw_zze.zzbqP);
            if (com_google_android_gms_internal_zzauw_zze.zzbxF.longValue() != 0) {
                zza(stringBuilder, i, "android_id", com_google_android_gms_internal_zzauw_zze.zzbxF);
            }
            zza(stringBuilder, i, com_google_android_gms_internal_zzauw_zze.zzbxh);
            zza(stringBuilder, i, com_google_android_gms_internal_zzauw_zze.zzbxz);
            zza(stringBuilder, i, com_google_android_gms_internal_zzauw_zze.zzbxg);
            zza(stringBuilder, i);
            stringBuilder.append("}\n");
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, String str, zzd com_google_android_gms_internal_zzauu_zzd) {
        if (com_google_android_gms_internal_zzauu_zzd != null) {
            zza(stringBuilder, i);
            stringBuilder.append(str);
            stringBuilder.append(" {\n");
            if (com_google_android_gms_internal_zzauu_zzd.zzbwy != null) {
                Object obj = "UNKNOWN_COMPARISON_TYPE";
                switch (com_google_android_gms_internal_zzauu_zzd.zzbwy.intValue()) {
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
            zza(stringBuilder, i, "match_as_float", com_google_android_gms_internal_zzauu_zzd.zzbwz);
            zza(stringBuilder, i, "comparison_value", com_google_android_gms_internal_zzauu_zzd.zzbwA);
            zza(stringBuilder, i, "min_comparison_value", com_google_android_gms_internal_zzauu_zzd.zzbwB);
            zza(stringBuilder, i, "max_comparison_value", com_google_android_gms_internal_zzauu_zzd.zzbwC);
            zza(stringBuilder, i);
            stringBuilder.append("}\n");
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, String str, zzf com_google_android_gms_internal_zzauu_zzf) {
        if (com_google_android_gms_internal_zzauu_zzf != null) {
            zza(stringBuilder, i);
            stringBuilder.append(str);
            stringBuilder.append(" {\n");
            if (com_google_android_gms_internal_zzauu_zzf.zzbwG != null) {
                Object obj = "UNKNOWN_MATCH_TYPE";
                switch (com_google_android_gms_internal_zzauu_zzf.zzbwG.intValue()) {
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
            zza(stringBuilder, i, "expression", com_google_android_gms_internal_zzauu_zzf.zzbwH);
            zza(stringBuilder, i, "case_sensitive", com_google_android_gms_internal_zzauu_zzf.zzbwI);
            if (com_google_android_gms_internal_zzauu_zzf.zzbwJ.length > 0) {
                zza(stringBuilder, i + 1);
                stringBuilder.append("expression_list {\n");
                for (String str2 : com_google_android_gms_internal_zzauu_zzf.zzbwJ) {
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

    private static void zza(StringBuilder stringBuilder, int i, String str, zzauw.zzf com_google_android_gms_internal_zzauw_zzf) {
        int i2 = 0;
        if (com_google_android_gms_internal_zzauw_zzf != null) {
            int i3;
            int i4;
            int i5 = i + 1;
            zza(stringBuilder, i5);
            stringBuilder.append(str);
            stringBuilder.append(" {\n");
            if (com_google_android_gms_internal_zzauw_zzf.zzbxH != null) {
                zza(stringBuilder, i5 + 1);
                stringBuilder.append("results: ");
                long[] jArr = com_google_android_gms_internal_zzauw_zzf.zzbxH;
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
            if (com_google_android_gms_internal_zzauw_zzf.zzbxG != null) {
                zza(stringBuilder, i5 + 1);
                stringBuilder.append("status: ");
                long[] jArr2 = com_google_android_gms_internal_zzauw_zzf.zzbxG;
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

    private static void zza(StringBuilder stringBuilder, int i, zza[] com_google_android_gms_internal_zzauw_zzaArr) {
        if (com_google_android_gms_internal_zzauw_zzaArr != null) {
            int i2 = i + 1;
            for (zza com_google_android_gms_internal_zzauw_zza : com_google_android_gms_internal_zzauw_zzaArr) {
                if (com_google_android_gms_internal_zzauw_zza != null) {
                    zza(stringBuilder, i2);
                    stringBuilder.append("audience_membership {\n");
                    zza(stringBuilder, i2, "audience_id", com_google_android_gms_internal_zzauw_zza.zzbwk);
                    zza(stringBuilder, i2, "new_audience", com_google_android_gms_internal_zzauw_zza.zzbwW);
                    zza(stringBuilder, i2, "current_data", com_google_android_gms_internal_zzauw_zza.zzbwU);
                    zza(stringBuilder, i2, "previous_data", com_google_android_gms_internal_zzauw_zza.zzbwV);
                    zza(stringBuilder, i2);
                    stringBuilder.append("}\n");
                }
            }
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, zzauw.zzb[] com_google_android_gms_internal_zzauw_zzbArr) {
        if (com_google_android_gms_internal_zzauw_zzbArr != null) {
            int i2 = i + 1;
            for (zzauw.zzb com_google_android_gms_internal_zzauw_zzb : com_google_android_gms_internal_zzauw_zzbArr) {
                if (com_google_android_gms_internal_zzauw_zzb != null) {
                    zza(stringBuilder, i2);
                    stringBuilder.append("event {\n");
                    zza(stringBuilder, i2, "name", com_google_android_gms_internal_zzauw_zzb.name);
                    zza(stringBuilder, i2, "timestamp_millis", com_google_android_gms_internal_zzauw_zzb.zzbwZ);
                    zza(stringBuilder, i2, "previous_timestamp_millis", com_google_android_gms_internal_zzauw_zzb.zzbxa);
                    zza(stringBuilder, i2, NewHtcHomeBadger.COUNT, com_google_android_gms_internal_zzauw_zzb.count);
                    zza(stringBuilder, i2, com_google_android_gms_internal_zzauw_zzb.zzbwY);
                    zza(stringBuilder, i2);
                    stringBuilder.append("}\n");
                }
            }
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, zzauw.zzc[] com_google_android_gms_internal_zzauw_zzcArr) {
        if (com_google_android_gms_internal_zzauw_zzcArr != null) {
            int i2 = i + 1;
            for (zzauw.zzc com_google_android_gms_internal_zzauw_zzc : com_google_android_gms_internal_zzauw_zzcArr) {
                if (com_google_android_gms_internal_zzauw_zzc != null) {
                    zza(stringBuilder, i2);
                    stringBuilder.append("param {\n");
                    zza(stringBuilder, i2, "name", com_google_android_gms_internal_zzauw_zzc.name);
                    zza(stringBuilder, i2, "string_value", com_google_android_gms_internal_zzauw_zzc.zzaGV);
                    zza(stringBuilder, i2, "int_value", com_google_android_gms_internal_zzauw_zzc.zzbxc);
                    zza(stringBuilder, i2, "double_value", com_google_android_gms_internal_zzauw_zzc.zzbwf);
                    zza(stringBuilder, i2);
                    stringBuilder.append("}\n");
                }
            }
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, zzg[] com_google_android_gms_internal_zzauw_zzgArr) {
        if (com_google_android_gms_internal_zzauw_zzgArr != null) {
            int i2 = i + 1;
            for (zzg com_google_android_gms_internal_zzauw_zzg : com_google_android_gms_internal_zzauw_zzgArr) {
                if (com_google_android_gms_internal_zzauw_zzg != null) {
                    zza(stringBuilder, i2);
                    stringBuilder.append("user_property {\n");
                    zza(stringBuilder, i2, "set_timestamp_millis", com_google_android_gms_internal_zzauw_zzg.zzbxJ);
                    zza(stringBuilder, i2, "name", com_google_android_gms_internal_zzauw_zzg.name);
                    zza(stringBuilder, i2, "string_value", com_google_android_gms_internal_zzauw_zzg.zzaGV);
                    zza(stringBuilder, i2, "int_value", com_google_android_gms_internal_zzauw_zzg.zzbxc);
                    zza(stringBuilder, i2, "double_value", com_google_android_gms_internal_zzauw_zzg.zzbwf);
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

    public static boolean zzae(String str, String str2) {
        return (str == null && str2 == null) ? true : str == null ? false : str.equals(str2);
    }

    public static String zzb(zzauw.zzd com_google_android_gms_internal_zzauw_zzd) {
        if (com_google_android_gms_internal_zzauw_zzd == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nbatch {\n");
        if (com_google_android_gms_internal_zzauw_zzd.zzbxd != null) {
            for (zzauw.zze com_google_android_gms_internal_zzauw_zze : com_google_android_gms_internal_zzauw_zzd.zzbxd) {
                if (com_google_android_gms_internal_zzauw_zze != null) {
                    zza(stringBuilder, 1, com_google_android_gms_internal_zzauw_zze);
                }
            }
        }
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    static MessageDigest zzch(String str) {
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

    static boolean zzfT(String str) {
        zzac.zzdr(str);
        return str.charAt(0) != '_';
    }

    private int zzgc(String str) {
        return "_ldl".equals(str) ? zzKn().zzKS() : zzKn().zzKR();
    }

    public static boolean zzgd(String str) {
        return !TextUtils.isEmpty(str) && str.startsWith("_");
    }

    static boolean zzgf(String str) {
        return str != null && str.matches("(\\+|-)?([0-9]+\\.?[0-9]*|[0-9]*\\.?[0-9]+)") && str.length() <= 310;
    }

    static long zzy(byte[] bArr) {
        long j = null;
        zzac.zzw(bArr);
        zzac.zzaw(bArr.length > 0);
        long j2 = 0;
        int length = bArr.length - 1;
        while (length >= 0 && length >= bArr.length - 8) {
            j2 += (((long) bArr[length]) & 255) << j;
            j += 8;
            length--;
        }
        return j2;
    }

    public static boolean zzy(Context context, String str) {
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

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public boolean zzA(Intent intent) {
        String stringExtra = intent.getStringExtra("android.intent.extra.REFERRER_NAME");
        return "android-app://com.google.android.googlequicksearchbox/https/www.google.com".equals(stringExtra) || "https://www.google.com".equals(stringExtra) || "android-app://com.google.appcrawler".equals(stringExtra);
    }

    public /* bridge */ /* synthetic */ void zzJV() {
        super.zzJV();
    }

    public /* bridge */ /* synthetic */ void zzJW() {
        super.zzJW();
    }

    public /* bridge */ /* synthetic */ void zzJX() {
        super.zzJX();
    }

    public /* bridge */ /* synthetic */ zzatb zzJY() {
        return super.zzJY();
    }

    public /* bridge */ /* synthetic */ zzatf zzJZ() {
        return super.zzJZ();
    }

    public /* bridge */ /* synthetic */ zzauj zzKa() {
        return super.zzKa();
    }

    public /* bridge */ /* synthetic */ zzatu zzKb() {
        return super.zzKb();
    }

    public /* bridge */ /* synthetic */ zzatl zzKc() {
        return super.zzKc();
    }

    public /* bridge */ /* synthetic */ zzaul zzKd() {
        return super.zzKd();
    }

    public /* bridge */ /* synthetic */ zzauk zzKe() {
        return super.zzKe();
    }

    public /* bridge */ /* synthetic */ zzatv zzKf() {
        return super.zzKf();
    }

    public /* bridge */ /* synthetic */ zzatj zzKg() {
        return super.zzKg();
    }

    public /* bridge */ /* synthetic */ zzaut zzKh() {
        return super.zzKh();
    }

    public /* bridge */ /* synthetic */ zzauc zzKi() {
        return super.zzKi();
    }

    public /* bridge */ /* synthetic */ zzaun zzKj() {
        return super.zzKj();
    }

    public /* bridge */ /* synthetic */ zzaud zzKk() {
        return super.zzKk();
    }

    public /* bridge */ /* synthetic */ zzatx zzKl() {
        return super.zzKl();
    }

    public /* bridge */ /* synthetic */ zzaua zzKm() {
        return super.zzKm();
    }

    public /* bridge */ /* synthetic */ zzati zzKn() {
        return super.zzKn();
    }

    @WorkerThread
    long zzL(Context context, String str) {
        zzmR();
        zzac.zzw(context);
        zzac.zzdr(str);
        PackageManager packageManager = context.getPackageManager();
        MessageDigest zzch = zzch("MD5");
        if (zzch == null) {
            zzKl().zzLY().log("Could not get MD5 instance");
            return -1;
        }
        if (packageManager != null) {
            try {
                if (!zzM(context, str)) {
                    PackageInfo packageInfo = zzadg.zzbi(context).getPackageInfo(getContext().getPackageName(), 64);
                    if (packageInfo.signatures != null && packageInfo.signatures.length > 0) {
                        return zzy(zzch.digest(packageInfo.signatures[0].toByteArray()));
                    }
                    zzKl().zzMa().log("Could not get signatures");
                    return -1;
                }
            } catch (NameNotFoundException e) {
                zzKl().zzLY().zzj("Package name not found", e);
            }
        }
        return 0;
    }

    Bundle zzM(Bundle bundle) {
        Bundle bundle2 = new Bundle();
        if (bundle != null) {
            for (String str : bundle.keySet()) {
                Object zzl = zzKh().zzl(str, bundle.get(str));
                if (zzl == null) {
                    zzKl().zzMa().zzj("Param value can't be null", str);
                } else {
                    zzKh().zza(bundle2, str, zzl);
                }
            }
        }
        return bundle2;
    }

    boolean zzM(Context context, String str) {
        X500Principal x500Principal = new X500Principal("CN=Android Debug,O=Android,C=US");
        try {
            PackageInfo packageInfo = zzadg.zzbi(context).getPackageInfo(str, 64);
            if (!(packageInfo == null || packageInfo.signatures == null || packageInfo.signatures.length <= 0)) {
                return ((X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(packageInfo.signatures[0].toByteArray()))).getSubjectX500Principal().equals(x500Principal);
            }
        } catch (CertificateException e) {
            zzKl().zzLY().zzj("Error obtaining certificate", e);
        } catch (NameNotFoundException e2) {
            zzKl().zzLY().zzj("Package name not found", e2);
        }
        return true;
    }

    public long zzNi() {
        long nextLong;
        if (this.zzbwh.get() == 0) {
            synchronized (this.zzbwh) {
                nextLong = new Random(System.nanoTime() ^ zznR().currentTimeMillis()).nextLong();
                int i = this.zzbwi + 1;
                this.zzbwi = i;
                nextLong += (long) i;
            }
        } else {
            synchronized (this.zzbwh) {
                this.zzbwh.compareAndSet(-1, 1);
                nextLong = this.zzbwh.getAndIncrement();
            }
        }
        return nextLong;
    }

    public Bundle zza(String str, Bundle bundle, @Nullable List<String> list, boolean z) {
        if (bundle == null) {
            return null;
        }
        Bundle bundle2 = new Bundle(bundle);
        zzKn().zzKL();
        int i = 0;
        for (String str2 : bundle.keySet()) {
            int zzfY;
            if (list == null || !list.contains(str2)) {
                zzfY = z ? zzfY(str2) : 0;
                if (zzfY == 0) {
                    zzfY = zzfZ(str2);
                }
            } else {
                zzfY = 0;
            }
            if (zzfY != 0) {
                if (zzd(bundle2, zzfY)) {
                    bundle2.putString("_ev", zza(str2, zzKn().zzKO(), true));
                    if (zzfY == 3) {
                        zzb(bundle2, (Object) str2);
                    }
                }
                bundle2.remove(str2);
            } else if (zzk(str2, bundle.get(str2)) || "_ev".equals(str2)) {
                if (zzfT(str2)) {
                    i++;
                    if (i > 25) {
                        zzKl().zzLY().zze("Event can't contain more then " + 25 + " params", str, bundle);
                        zzd(bundle2, 5);
                        bundle2.remove(str2);
                    }
                }
                i = i;
            } else {
                if (zzd(bundle2, 4)) {
                    bundle2.putString("_ev", zza(str2, zzKn().zzKO(), true));
                    zzb(bundle2, bundle.get(str2));
                }
                bundle2.remove(str2);
            }
        }
        return bundle2;
    }

    zzatq zza(String str, Bundle bundle, String str2, long j, boolean z, boolean z2) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        if (zzKh().zzfV(str) != 0) {
            zzKl().zzLY().zzj("Invalid conditional property event name", str);
            throw new IllegalArgumentException();
        }
        Bundle bundle2 = bundle != null ? new Bundle(bundle) : new Bundle();
        bundle2.putString("_o", str2);
        bundle2 = zzKh().zza(str, bundle2, com.google.android.gms.common.util.zzf.zzx("_o"), z2);
        return new zzatq(str, new zzato(z ? zzM(bundle2) : bundle2), str2, j);
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
                zzKl().zzMb().zze("Not putting event parameter. Invalid value type. name, type", str, obj != null ? obj.getClass().getSimpleName() : null);
            }
        }
    }

    public void zza(zzauw.zzc com_google_android_gms_internal_zzauw_zzc, Object obj) {
        zzac.zzw(obj);
        com_google_android_gms_internal_zzauw_zzc.zzaGV = null;
        com_google_android_gms_internal_zzauw_zzc.zzbxc = null;
        com_google_android_gms_internal_zzauw_zzc.zzbwf = null;
        if (obj instanceof String) {
            com_google_android_gms_internal_zzauw_zzc.zzaGV = (String) obj;
        } else if (obj instanceof Long) {
            com_google_android_gms_internal_zzauw_zzc.zzbxc = (Long) obj;
        } else if (obj instanceof Double) {
            com_google_android_gms_internal_zzauw_zzc.zzbwf = (Double) obj;
        } else {
            zzKl().zzLY().zzj("Ignoring invalid (type) event param value", obj);
        }
    }

    public void zza(zzg com_google_android_gms_internal_zzauw_zzg, Object obj) {
        zzac.zzw(obj);
        com_google_android_gms_internal_zzauw_zzg.zzaGV = null;
        com_google_android_gms_internal_zzauw_zzg.zzbxc = null;
        com_google_android_gms_internal_zzauw_zzg.zzbwf = null;
        if (obj instanceof String) {
            com_google_android_gms_internal_zzauw_zzg.zzaGV = (String) obj;
        } else if (obj instanceof Long) {
            com_google_android_gms_internal_zzauw_zzg.zzbxc = (Long) obj;
        } else if (obj instanceof Double) {
            com_google_android_gms_internal_zzauw_zzg.zzbwf = (Double) obj;
        } else {
            zzKl().zzLY().zzj("Ignoring invalid (type) user attribute value", obj);
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
        this.zzbqc.zzKn().zzLg();
        this.zzbqc.zzKa().zze("auto", "_err", bundle);
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
        zzKl().zzMa().zzd("Value is too long; discarded. Value kind, name, value length", str, str2, Integer.valueOf(valueOf.length()));
        return false;
    }

    byte[] zza(Parcelable parcelable) {
        if (parcelable == null) {
            return null;
        }
        Parcel obtain = Parcel.obtain();
        try {
            parcelable.writeToParcel(obtain, 0);
            byte[] marshall = obtain.marshall();
            return marshall;
        } finally {
            obtain.recycle();
        }
    }

    public byte[] zza(zzauw.zzd com_google_android_gms_internal_zzauw_zzd) {
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzauw_zzd.zzaeT()];
            zzbxm zzag = zzbxm.zzag(bArr);
            com_google_android_gms_internal_zzauw_zzd.zza(zzag);
            zzag.zzaeG();
            return bArr;
        } catch (IOException e) {
            zzKl().zzLY().zzj("Data loss. Failed to serialize batch", e);
            return null;
        }
    }

    boolean zzac(String str, String str2) {
        if (str2 == null) {
            zzKl().zzLY().zzj("Name is required and can't be null. Type", str);
            return false;
        } else if (str2.length() == 0) {
            zzKl().zzLY().zzj("Name is required and can't be empty. Type", str);
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
                        zzKl().zzLY().zze("Name must consist of letters, digits or _ (underscores). Type, name", str, str2);
                        return false;
                    }
                }
                return true;
            }
            zzKl().zzLY().zze("Name must start with a letter. Type, name", str, str2);
            return false;
        }
    }

    boolean zzad(String str, String str2) {
        if (str2 == null) {
            zzKl().zzLY().zzj("Name is required and can't be null. Type", str);
            return false;
        } else if (str2.length() == 0) {
            zzKl().zzLY().zzj("Name is required and can't be empty. Type", str);
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
                        zzKl().zzLY().zze("Name must consist of letters, digits or _ (underscores). Type, name", str, str2);
                        return false;
                    }
                }
                return true;
            }
            zzKl().zzLY().zze("Name must start with a letter or _ (underscore). Type, name", str, str2);
            return false;
        }
    }

    <T extends Parcelable> T zzb(byte[] bArr, Creator<T> creator) {
        if (bArr == null) {
            return null;
        }
        Parcel obtain = Parcel.obtain();
        T t;
        try {
            obtain.unmarshall(bArr, 0, bArr.length);
            obtain.setDataPosition(0);
            t = (Parcelable) creator.createFromParcel(obtain);
            return t;
        } catch (com.google.android.gms.common.internal.safeparcel.zzb.zza e) {
            t = zzKl().zzLY();
            t.log("Failed to load parcelable from buffer");
            return null;
        } finally {
            obtain.recycle();
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

    boolean zzb(String str, int i, String str2) {
        if (str2 == null) {
            zzKl().zzLY().zzj("Name is required and can't be null. Type", str);
            return false;
        } else if (str2.length() <= i) {
            return true;
        } else {
            zzKl().zzLY().zzd("Name is too long. Type, maximum supported length, name", str, Integer.valueOf(i), str2);
            return false;
        }
    }

    @WorkerThread
    public boolean zzbW(String str) {
        zzmR();
        if (zzadg.zzbi(getContext()).checkCallingOrSelfPermission(str) == 0) {
            return true;
        }
        zzKl().zzMd().zzj("Permission not granted", str);
        return false;
    }

    boolean zzc(String str, Map<String, String> map, String str2) {
        if (str2 == null) {
            zzKl().zzLY().zzj("Name is required and can't be null. Type", str);
            return false;
        } else if (str2.startsWith("firebase_")) {
            zzKl().zzLY().zze("Name starts with reserved prefix. Type, name", str, str2);
            return false;
        } else if (map == null || !map.containsKey(str2)) {
            return true;
        } else {
            zzKl().zzLY().zze("Name is reserved. Type, name", str, str2);
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
    boolean zzd(zzatq com_google_android_gms_internal_zzatq, zzatd com_google_android_gms_internal_zzatd) {
        zzac.zzw(com_google_android_gms_internal_zzatq);
        zzac.zzw(com_google_android_gms_internal_zzatd);
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzatd.zzbqL)) {
            return true;
        }
        zzKn().zzLg();
        return false;
    }

    public int zzfU(String str) {
        return !zzac("event", str) ? 2 : !zzc("event", AppMeasurement.zza.zzbqd, str) ? 13 : zzb("event", zzKn().zzKM(), str) ? 0 : 2;
    }

    public int zzfV(String str) {
        return !zzad("event", str) ? 2 : !zzc("event", AppMeasurement.zza.zzbqd, str) ? 13 : zzb("event", zzKn().zzKM(), str) ? 0 : 2;
    }

    public int zzfW(String str) {
        return !zzac("user property", str) ? 6 : !zzc("user property", AppMeasurement.zzg.zzbqi, str) ? 15 : zzb("user property", zzKn().zzKN(), str) ? 0 : 6;
    }

    public int zzfX(String str) {
        return !zzad("user property", str) ? 6 : !zzc("user property", AppMeasurement.zzg.zzbqi, str) ? 15 : zzb("user property", zzKn().zzKN(), str) ? 0 : 6;
    }

    public int zzfY(String str) {
        return !zzac("event param", str) ? 3 : !zzc("event param", null, str) ? 14 : zzb("event param", zzKn().zzKO(), str) ? 0 : 3;
    }

    public int zzfZ(String str) {
        return !zzad("event param", str) ? 3 : !zzc("event param", null, str) ? 14 : zzb("event param", zzKn().zzKO(), str) ? 0 : 3;
    }

    public boolean zzga(String str) {
        if (TextUtils.isEmpty(str)) {
            zzKl().zzLY().log("Missing google_app_id. Firebase Analytics disabled. See https://goo.gl/NAOOOI");
            return false;
        } else if (zzgb(str)) {
            return true;
        } else {
            zzKl().zzLY().zzj("Invalid google_app_id. Firebase Analytics disabled. See https://goo.gl/NAOOOI. provided id", str);
            return false;
        }
    }

    boolean zzgb(String str) {
        zzac.zzw(str);
        return str.matches("^1:\\d+:android:[a-f0-9]+$");
    }

    public boolean zzge(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        String zzLC = zzKn().zzLC();
        zzKn().zzLg();
        return zzLC.equals(str);
    }

    boolean zzgg(String str) {
        return "1".equals(zzKi().zzZ(str, "measurement.upload.blacklist_internal"));
    }

    boolean zzgh(String str) {
        return "1".equals(zzKi().zzZ(str, "measurement.upload.blacklist_public"));
    }

    @WorkerThread
    boolean zzgi(String str) {
        zzac.zzdr(str);
        boolean z = true;
        switch (str.hashCode()) {
            case 94660:
                if (str.equals("_in")) {
                    z = false;
                    break;
                }
                break;
            case 95025:
                if (str.equals("_ug")) {
                    z = true;
                    break;
                }
                break;
            case 95027:
                if (str.equals("_ui")) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
            case true:
            case true:
                return true;
            default:
                return false;
        }
    }

    public boolean zzh(long j, long j2) {
        return j == 0 || j2 <= 0 || Math.abs(zznR().currentTimeMillis() - j) > j2;
    }

    public boolean zzk(String str, Object obj) {
        return zzgd(str) ? zza("param", str, zzKn().zzKQ(), obj) : zza("param", str, zzKn().zzKP(), obj);
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
            zzKl().zzLY().zzj("Failed to gzip content", e);
            throw e;
        }
    }

    public Object zzl(String str, Object obj) {
        if ("_ev".equals(str)) {
            return zza(zzKn().zzKQ(), obj, true);
        }
        return zza(zzgd(str) ? zzKn().zzKQ() : zzKn().zzKP(), obj, false);
    }

    public int zzm(String str, Object obj) {
        return "_ldl".equals(str) ? zza("user property referrer", str, zzgc(str), obj) : zza("user property", str, zzgc(str), obj) ? 0 : 7;
    }

    public /* bridge */ /* synthetic */ void zzmR() {
        super.zzmR();
    }

    protected void zzmS() {
        SecureRandom secureRandom = new SecureRandom();
        long nextLong = secureRandom.nextLong();
        if (nextLong == 0) {
            nextLong = secureRandom.nextLong();
            if (nextLong == 0) {
                zzKl().zzMa().log("Utils falling back to Random for random id");
            }
        }
        this.zzbwh.set(nextLong);
    }

    public Object zzn(String str, Object obj) {
        return "_ldl".equals(str) ? zza(zzgc(str), obj, true) : zza(zzgc(str), obj, false);
    }

    public /* bridge */ /* synthetic */ com.google.android.gms.common.util.zze zznR() {
        return super.zznR();
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
                        bundle.putString(Param.CAMPAIGN, queryParameter);
                    }
                    if (!TextUtils.isEmpty(queryParameter2)) {
                        bundle.putString(Param.SOURCE, queryParameter2);
                    }
                    if (!TextUtils.isEmpty(queryParameter3)) {
                        bundle.putString(Param.MEDIUM, queryParameter3);
                    }
                    if (!TextUtils.isEmpty(queryParameter4)) {
                        bundle.putString("gclid", queryParameter4);
                    }
                    queryParameter4 = uri.getQueryParameter("utm_term");
                    if (!TextUtils.isEmpty(queryParameter4)) {
                        bundle.putString(Param.TERM, queryParameter4);
                    }
                    queryParameter4 = uri.getQueryParameter("utm_content");
                    if (!TextUtils.isEmpty(queryParameter4)) {
                        bundle.putString(Param.CONTENT, queryParameter4);
                    }
                    queryParameter4 = uri.getQueryParameter(Param.ACLID);
                    if (!TextUtils.isEmpty(queryParameter4)) {
                        bundle.putString(Param.ACLID, queryParameter4);
                    }
                    queryParameter4 = uri.getQueryParameter(Param.CP1);
                    if (!TextUtils.isEmpty(queryParameter4)) {
                        bundle.putString(Param.CP1, queryParameter4);
                    }
                    queryParameter4 = uri.getQueryParameter("anid");
                    if (!TextUtils.isEmpty(queryParameter4)) {
                        bundle.putString("anid", queryParameter4);
                    }
                }
            } catch (UnsupportedOperationException e) {
                zzKl().zzMa().zzj("Install referrer url isn't a hierarchical URI", e);
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
            zzKl().zzLY().zzj("Failed to ungzip content", e);
            throw e;
        }
    }

    @WorkerThread
    public long zzz(byte[] bArr) {
        zzac.zzw(bArr);
        zzmR();
        MessageDigest zzch = zzch("MD5");
        if (zzch != null) {
            return zzy(zzch.digest(bArr));
        }
        zzKl().zzLY().log("Failed to get MD5");
        return 0;
    }
}

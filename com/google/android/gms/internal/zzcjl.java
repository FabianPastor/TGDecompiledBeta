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
import com.google.android.gms.common.internal.safeparcel.zzc;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.measurement.AppMeasurement.Event;
import com.google.android.gms.measurement.AppMeasurement.UserProperty;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.security.auth.x500.X500Principal;

public final class zzcjl extends zzchj {
    private static String[] zzbuD = new String[]{"firebase_"};
    private SecureRandom zzbuE;
    private final AtomicLong zzbuF = new AtomicLong(0);
    private int zzbuG;

    zzcjl(zzcgl com_google_android_gms_internal_zzcgl) {
        super(com_google_android_gms_internal_zzcgl);
    }

    public static Bundle[] zzC(Object obj) {
        if (obj instanceof Bundle) {
            return new Bundle[]{(Bundle) obj};
        } else if (obj instanceof Parcelable[]) {
            return (Bundle[]) Arrays.copyOf((Parcelable[]) obj, ((Parcelable[]) obj).length, Bundle[].class);
        } else {
            if (!(obj instanceof ArrayList)) {
                return null;
            }
            ArrayList arrayList = (ArrayList) obj;
            return (Bundle[]) arrayList.toArray(new Bundle[arrayList.size()]);
        }
    }

    public static Object zzD(Object obj) {
        ObjectOutputStream objectOutputStream;
        ObjectInputStream objectInputStream;
        Throwable th;
        if (obj == null) {
            return null;
        }
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

    private final boolean zzJ(Context context, String str) {
        X500Principal x500Principal = new X500Principal("CN=Android Debug,O=Android,C=US");
        try {
            PackageInfo packageInfo = zzbha.zzaP(context).getPackageInfo(str, 64);
            if (!(packageInfo == null || packageInfo.signatures == null || packageInfo.signatures.length <= 0)) {
                return ((X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(packageInfo.signatures[0].toByteArray()))).getSubjectX500Principal().equals(x500Principal);
            }
        } catch (CertificateException e) {
            super.zzwF().zzyx().zzj("Error obtaining certificate", e);
        } catch (NameNotFoundException e2) {
            super.zzwF().zzyx().zzj("Package name not found", e2);
        }
        return true;
    }

    private final boolean zzP(String str, String str2) {
        if (str2 == null) {
            super.zzwF().zzyx().zzj("Name is required and can't be null. Type", str);
            return false;
        } else if (str2.length() == 0) {
            super.zzwF().zzyx().zzj("Name is required and can't be empty. Type", str);
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
                        super.zzwF().zzyx().zze("Name must consist of letters, digits or _ (underscores). Type, name", str, str2);
                        return false;
                    }
                }
                return true;
            }
            super.zzwF().zzyx().zze("Name must start with a letter. Type, name", str, str2);
            return false;
        }
    }

    private final boolean zzQ(String str, String str2) {
        if (str2 == null) {
            super.zzwF().zzyx().zzj("Name is required and can't be null. Type", str);
            return false;
        } else if (str2.length() == 0) {
            super.zzwF().zzyx().zzj("Name is required and can't be empty. Type", str);
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
                        super.zzwF().zzyx().zze("Name must consist of letters, digits or _ (underscores). Type, name", str, str2);
                        return false;
                    }
                }
                return true;
            }
            super.zzwF().zzyx().zze("Name must start with a letter or _ (underscore). Type, name", str, str2);
            return false;
        }
    }

    public static boolean zzR(String str, String str2) {
        return (str == null && str2 == null) ? true : str == null ? false : str.equals(str2);
    }

    private final int zza(String str, Object obj, boolean z) {
        if (z) {
            int length;
            Object obj2;
            String str2 = "param";
            zzcem.zzxm();
            if (obj instanceof Parcelable[]) {
                length = ((Parcelable[]) obj).length;
            } else if (obj instanceof ArrayList) {
                length = ((ArrayList) obj).size();
            } else {
                length = 1;
                if (obj2 == null) {
                    return 17;
                }
            }
            if (length > 1000) {
                super.zzwF().zzyz().zzd("Parameter array is too long; discarded. Value kind, name, array length", str2, str, Integer.valueOf(length));
                obj2 = null;
            } else {
                length = 1;
            }
            if (obj2 == null) {
                return 17;
            }
        }
        return zzex(str) ? zza("param", str, zzcem.zzxl(), obj, z) : zza("param", str, zzcem.zzxk(), obj, z) ? 0 : 4;
    }

    private static Object zza(int i, Object obj, boolean z) {
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

    public static String zza(String str, int i, boolean z) {
        return str.codePointCount(0, str.length()) > i ? z ? String.valueOf(str.substring(0, str.offsetByCodePoints(0, i))).concat("...") : null : str;
    }

    @Nullable
    public static String zza(String str, String[] strArr, String[] strArr2) {
        zzbo.zzu(strArr);
        zzbo.zzu(strArr2);
        int min = Math.min(strArr.length, strArr2.length);
        for (int i = 0; i < min; i++) {
            if (zzR(str, strArr[i])) {
                return strArr2[i];
            }
        }
        return null;
    }

    public static boolean zza(Context context, String str, boolean z) {
        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager == null) {
                return false;
            }
            ActivityInfo receiverInfo = packageManager.getReceiverInfo(new ComponentName(context, str), 2);
            return receiverInfo != null && receiverInfo.enabled;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    private final boolean zza(String str, String str2, int i, Object obj, boolean z) {
        if (obj == null || (obj instanceof Long) || (obj instanceof Float) || (obj instanceof Integer) || (obj instanceof Byte) || (obj instanceof Short) || (obj instanceof Boolean) || (obj instanceof Double)) {
            return true;
        }
        if ((obj instanceof String) || (obj instanceof Character) || (obj instanceof CharSequence)) {
            String valueOf = String.valueOf(obj);
            if (valueOf.codePointCount(0, valueOf.length()) <= i) {
                return true;
            }
            super.zzwF().zzyz().zzd("Value is too long; discarded. Value kind, name, value length", str, str2, Integer.valueOf(valueOf.length()));
            return false;
        } else if ((obj instanceof Bundle) && z) {
            return true;
        } else {
            int length;
            int i2;
            Object obj2;
            if ((obj instanceof Parcelable[]) && z) {
                Parcelable[] parcelableArr = (Parcelable[]) obj;
                length = parcelableArr.length;
                i2 = 0;
                while (i2 < length) {
                    obj2 = parcelableArr[i2];
                    if (obj2 instanceof Bundle) {
                        i2++;
                    } else {
                        super.zzwF().zzyz().zze("All Parcelable[] elements must be of type Bundle. Value type, name", obj2.getClass(), str2);
                        return false;
                    }
                }
                return true;
            } else if (!(obj instanceof ArrayList) || !z) {
                return false;
            } else {
                ArrayList arrayList = (ArrayList) obj;
                length = arrayList.size();
                i2 = 0;
                while (i2 < length) {
                    obj2 = arrayList.get(i2);
                    i2++;
                    if (!(obj2 instanceof Bundle)) {
                        super.zzwF().zzyz().zze("All ArrayList elements must be of type Bundle. Value type, name", obj2.getClass(), str2);
                        return false;
                    }
                }
                return true;
            }
        }
    }

    private final boolean zza(String str, String[] strArr, String str2) {
        if (str2 == null) {
            super.zzwF().zzyx().zzj("Name is required and can't be null. Type", str);
            return false;
        }
        boolean z;
        zzbo.zzu(str2);
        for (String startsWith : zzbuD) {
            if (str2.startsWith(startsWith)) {
                z = true;
                break;
            }
        }
        z = false;
        if (z) {
            super.zzwF().zzyx().zze("Name starts with reserved prefix. Type, name", str, str2);
            return false;
        }
        if (strArr != null) {
            zzbo.zzu(strArr);
            for (String startsWith2 : strArr) {
                if (zzR(str2, startsWith2)) {
                    z = true;
                    break;
                }
            }
            z = false;
            if (z) {
                super.zzwF().zzyx().zze("Name is reserved. Type, name", str, str2);
                return false;
            }
        }
        return true;
    }

    public static boolean zza(long[] jArr, int i) {
        return i < (jArr.length << 6) && (jArr[i / 64] & (1 << (i % 64))) != 0;
    }

    static byte[] zza(Parcelable parcelable) {
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

    public static long[] zza(BitSet bitSet) {
        int length = (bitSet.length() + 63) / 64;
        long[] jArr = new long[length];
        int i = 0;
        while (i < length) {
            jArr[i] = 0;
            int i2 = 0;
            while (i2 < 64 && (i << 6) + i2 < bitSet.length()) {
                if (bitSet.get((i << 6) + i2)) {
                    jArr[i] = jArr[i] | (1 << i2);
                }
                i2++;
            }
            i++;
        }
        return jArr;
    }

    private static void zzb(Bundle bundle, Object obj) {
        zzbo.zzu(bundle);
        if (obj == null) {
            return;
        }
        if ((obj instanceof String) || (obj instanceof CharSequence)) {
            bundle.putLong("_el", (long) String.valueOf(obj).length());
        }
    }

    private final boolean zzb(String str, int i, String str2) {
        if (str2 == null) {
            super.zzwF().zzyx().zzj("Name is required and can't be null. Type", str);
            return false;
        } else if (str2.codePointCount(0, str2.length()) <= i) {
            return true;
        } else {
            super.zzwF().zzyx().zzd("Name is too long. Type, maximum supported length, name", str, Integer.valueOf(i), str2);
            return false;
        }
    }

    static MessageDigest zzbE(String str) {
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

    private static boolean zzd(Bundle bundle, int i) {
        if (bundle.getLong("_err") != 0) {
            return false;
        }
        bundle.putLong("_err", (long) i);
        return true;
    }

    @WorkerThread
    static boolean zzd(zzcez com_google_android_gms_internal_zzcez, zzceh com_google_android_gms_internal_zzceh) {
        zzbo.zzu(com_google_android_gms_internal_zzcez);
        zzbo.zzu(com_google_android_gms_internal_zzceh);
        if (!TextUtils.isEmpty(com_google_android_gms_internal_zzceh.zzboQ)) {
            return true;
        }
        zzcem.zzxE();
        return false;
    }

    @WorkerThread
    static boolean zzeC(String str) {
        zzbo.zzcF(str);
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

    static boolean zzeo(String str) {
        zzbo.zzcF(str);
        return str.charAt(0) != '_' || str.equals("_ep");
    }

    private final int zzet(String str) {
        return !zzP("event param", str) ? 3 : !zza("event param", null, str) ? 14 : zzb("event param", zzcem.zzxj(), str) ? 0 : 3;
    }

    private final int zzeu(String str) {
        return !zzQ("event param", str) ? 3 : !zza("event param", null, str) ? 14 : zzb("event param", zzcem.zzxj(), str) ? 0 : 3;
    }

    private static int zzew(String str) {
        return "_ldl".equals(str) ? zzcem.zzxo() : zzcem.zzxn();
    }

    public static boolean zzex(String str) {
        return !TextUtils.isEmpty(str) && str.startsWith("_");
    }

    static boolean zzez(String str) {
        return str != null && str.matches("(\\+|-)?([0-9]+\\.?[0-9]*|[0-9]*\\.?[0-9]+)") && str.length() <= 310;
    }

    public static boolean zzl(Intent intent) {
        String stringExtra = intent.getStringExtra("android.intent.extra.REFERRER_NAME");
        return "android-app://com.google.android.googlequicksearchbox/https/www.google.com".equals(stringExtra) || "https://www.google.com".equals(stringExtra) || "android-app://com.google.appcrawler".equals(stringExtra);
    }

    static long zzn(byte[] bArr) {
        long j = null;
        zzbo.zzu(bArr);
        zzbo.zzae(bArr.length > 0);
        long j2 = 0;
        int length = bArr.length - 1;
        while (length >= 0 && length >= bArr.length - 8) {
            j2 += (((long) bArr[length]) & 255) << j;
            j += 8;
            length--;
        }
        return j2;
    }

    public static boolean zzw(Context context, String str) {
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

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    final Bundle zzB(Bundle bundle) {
        Bundle bundle2 = new Bundle();
        if (bundle != null) {
            for (String str : bundle.keySet()) {
                Object zzk = zzk(str, bundle.get(str));
                if (zzk == null) {
                    super.zzwF().zzyz().zzj("Param value can't be null", super.zzwA().zzdX(str));
                } else {
                    zza(bundle2, str, zzk);
                }
            }
        }
        return bundle2;
    }

    @WorkerThread
    final long zzI(Context context, String str) {
        super.zzjC();
        zzbo.zzu(context);
        zzbo.zzcF(str);
        PackageManager packageManager = context.getPackageManager();
        MessageDigest zzbE = zzbE("MD5");
        if (zzbE == null) {
            super.zzwF().zzyx().log("Could not get MD5 instance");
            return -1;
        }
        if (packageManager != null) {
            try {
                if (!zzJ(context, str)) {
                    PackageInfo packageInfo = zzbha.zzaP(context).getPackageInfo(super.getContext().getPackageName(), 64);
                    if (packageInfo.signatures != null && packageInfo.signatures.length > 0) {
                        return zzn(zzbE.digest(packageInfo.signatures[0].toByteArray()));
                    }
                    super.zzwF().zzyz().log("Could not get signatures");
                    return -1;
                }
            } catch (NameNotFoundException e) {
                super.zzwF().zzyx().zzj("Package name not found", e);
            }
        }
        return 0;
    }

    public final Bundle zza(String str, Bundle bundle, @Nullable List<String> list, boolean z, boolean z2) {
        if (bundle == null) {
            return null;
        }
        Bundle bundle2 = new Bundle(bundle);
        zzcem.zzxg();
        int i = 0;
        for (String str2 : bundle.keySet()) {
            int zzet;
            if (list == null || !list.contains(str2)) {
                zzet = z ? zzet(str2) : 0;
                if (zzet == 0) {
                    zzet = zzeu(str2);
                }
            } else {
                zzet = 0;
            }
            if (zzet != 0) {
                if (zzd(bundle2, zzet)) {
                    bundle2.putString("_ev", zza(str2, zzcem.zzxj(), true));
                    if (zzet == 3) {
                        zzb(bundle2, (Object) str2);
                    }
                }
                bundle2.remove(str2);
            } else {
                zzet = zza(str2, bundle.get(str2), z2);
                if (zzet == 0 || "_ev".equals(str2)) {
                    if (zzeo(str2)) {
                        i++;
                        if (i > 25) {
                            super.zzwF().zzyx().zze("Event can't contain more then 25 params", super.zzwA().zzdW(str), super.zzwA().zzA(bundle));
                            zzd(bundle2, 5);
                            bundle2.remove(str2);
                        }
                    }
                    i = i;
                } else {
                    if (zzd(bundle2, zzet)) {
                        bundle2.putString("_ev", zza(str2, zzcem.zzxj(), true));
                        zzb(bundle2, bundle.get(str2));
                    }
                    bundle2.remove(str2);
                }
            }
        }
        return bundle2;
    }

    final zzcez zza(String str, Bundle bundle, String str2, long j, boolean z, boolean z2) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        if (zzeq(str) != 0) {
            super.zzwF().zzyx().zzj("Invalid conditional property event name", super.zzwA().zzdY(str));
            throw new IllegalArgumentException();
        }
        Bundle bundle2 = bundle != null ? new Bundle(bundle) : new Bundle();
        bundle2.putString("_o", str2);
        return new zzcez(str, new zzcew(zzB(zza(str, bundle2, Collections.singletonList("_o"), false, false))), str2, j);
    }

    public final void zza(int i, String str, String str2, int i2) {
        zza(null, i, str, str2, i2);
    }

    public final void zza(Bundle bundle, String str, Object obj) {
        if (bundle != null) {
            if (obj instanceof Long) {
                bundle.putLong(str, ((Long) obj).longValue());
            } else if (obj instanceof String) {
                bundle.putString(str, String.valueOf(obj));
            } else if (obj instanceof Double) {
                bundle.putDouble(str, ((Double) obj).doubleValue());
            } else if (str != null) {
                super.zzwF().zzyA().zze("Not putting event parameter. Invalid value type. name, type", super.zzwA().zzdX(str), obj != null ? obj.getClass().getSimpleName() : null);
            }
        }
    }

    public final void zza(zzcjx com_google_android_gms_internal_zzcjx, Object obj) {
        zzbo.zzu(obj);
        com_google_android_gms_internal_zzcjx.zzaIF = null;
        com_google_android_gms_internal_zzcjx.zzbvA = null;
        com_google_android_gms_internal_zzcjx.zzbuB = null;
        if (obj instanceof String) {
            com_google_android_gms_internal_zzcjx.zzaIF = (String) obj;
        } else if (obj instanceof Long) {
            com_google_android_gms_internal_zzcjx.zzbvA = (Long) obj;
        } else if (obj instanceof Double) {
            com_google_android_gms_internal_zzcjx.zzbuB = (Double) obj;
        } else {
            super.zzwF().zzyx().zzj("Ignoring invalid (type) event param value", obj);
        }
    }

    public final void zza(zzckb com_google_android_gms_internal_zzckb, Object obj) {
        zzbo.zzu(obj);
        com_google_android_gms_internal_zzckb.zzaIF = null;
        com_google_android_gms_internal_zzckb.zzbvA = null;
        com_google_android_gms_internal_zzckb.zzbuB = null;
        if (obj instanceof String) {
            com_google_android_gms_internal_zzckb.zzaIF = (String) obj;
        } else if (obj instanceof Long) {
            com_google_android_gms_internal_zzckb.zzbvA = (Long) obj;
        } else if (obj instanceof Double) {
            com_google_android_gms_internal_zzckb.zzbuB = (Double) obj;
        } else {
            super.zzwF().zzyx().zzj("Ignoring invalid (type) user attribute value", obj);
        }
    }

    public final void zza(String str, int i, String str2, String str3, int i2) {
        Bundle bundle = new Bundle();
        zzd(bundle, i);
        if (!TextUtils.isEmpty(str2)) {
            bundle.putString(str2, str3);
        }
        if (i == 6 || i == 7 || i == 2) {
            bundle.putLong("_el", (long) i2);
        }
        zzcem.zzxE();
        this.zzboe.zzwt().zzd("auto", "_err", bundle);
    }

    final <T extends Parcelable> T zzb(byte[] bArr, Creator<T> creator) {
        T t;
        if (bArr == null) {
            return null;
        }
        Parcel obtain = Parcel.obtain();
        try {
            obtain.unmarshall(bArr, 0, bArr.length);
            obtain.setDataPosition(0);
            t = (Parcelable) creator.createFromParcel(obtain);
            return t;
        } catch (zzc e) {
            t = super.zzwF().zzyx();
            t.log("Failed to load parcelable from buffer");
            return null;
        } finally {
            obtain.recycle();
        }
    }

    public final byte[] zzb(zzcjy com_google_android_gms_internal_zzcjy) {
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzcjy.zzLV()];
            adh zzc = adh.zzc(bArr, 0, bArr.length);
            com_google_android_gms_internal_zzcjy.zza(zzc);
            zzc.zzLM();
            return bArr;
        } catch (IOException e) {
            super.zzwF().zzyx().zzj("Data loss. Failed to serialize batch", e);
            return null;
        }
    }

    @WorkerThread
    public final boolean zzbv(String str) {
        super.zzjC();
        if (zzbha.zzaP(super.getContext()).checkCallingOrSelfPermission(str) == 0) {
            return true;
        }
        super.zzwF().zzyC().zzj("Permission not granted", str);
        return false;
    }

    final boolean zzeA(String str) {
        return "1".equals(super.zzwC().zzM(str, "measurement.upload.blacklist_internal"));
    }

    final boolean zzeB(String str) {
        return "1".equals(super.zzwC().zzM(str, "measurement.upload.blacklist_public"));
    }

    public final int zzep(String str) {
        return !zzP("event", str) ? 2 : !zza("event", Event.zzbof, str) ? 13 : zzb("event", zzcem.zzxh(), str) ? 0 : 2;
    }

    public final int zzeq(String str) {
        return !zzQ("event", str) ? 2 : !zza("event", Event.zzbof, str) ? 13 : zzb("event", zzcem.zzxh(), str) ? 0 : 2;
    }

    public final int zzer(String str) {
        return !zzP("user property", str) ? 6 : !zza("user property", UserProperty.zzbom, str) ? 15 : zzb("user property", zzcem.zzxi(), str) ? 0 : 6;
    }

    public final int zzes(String str) {
        return !zzQ("user property", str) ? 6 : !zza("user property", UserProperty.zzbom, str) ? 15 : zzb("user property", zzcem.zzxi(), str) ? 0 : 6;
    }

    public final boolean zzev(String str) {
        if (TextUtils.isEmpty(str)) {
            super.zzwF().zzyx().log("Missing google_app_id. Firebase Analytics disabled. See https://goo.gl/NAOOOI");
            return false;
        }
        zzbo.zzu(str);
        if (str.matches("^1:\\d+:android:[a-f0-9]+$")) {
            return true;
        }
        super.zzwF().zzyx().zzj("Invalid google_app_id. Firebase Analytics disabled. See https://goo.gl/NAOOOI. provided id", str);
        return false;
    }

    public final boolean zzey(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        String zzya = super.zzwH().zzya();
        zzcem.zzxE();
        return zzya.equals(str);
    }

    public final boolean zzf(long j, long j2) {
        return j == 0 || j2 <= 0 || Math.abs(super.zzkq().currentTimeMillis() - j) > j2;
    }

    public final /* bridge */ /* synthetic */ void zzjC() {
        super.zzjC();
    }

    protected final void zzjD() {
        SecureRandom secureRandom = new SecureRandom();
        long nextLong = secureRandom.nextLong();
        if (nextLong == 0) {
            nextLong = secureRandom.nextLong();
            if (nextLong == 0) {
                super.zzwF().zzyz().log("Utils falling back to Random for random id");
            }
        }
        this.zzbuF.set(nextLong);
    }

    public final Object zzk(String str, Object obj) {
        if ("_ev".equals(str)) {
            return zza(zzcem.zzxl(), obj, true);
        }
        return zza(zzex(str) ? zzcem.zzxl() : zzcem.zzxk(), obj, false);
    }

    public final /* bridge */ /* synthetic */ zze zzkq() {
        return super.zzkq();
    }

    public final int zzl(String str, Object obj) {
        return "_ldl".equals(str) ? zza("user property referrer", str, zzew(str), obj, false) : zza("user property", str, zzew(str), obj, false) ? 0 : 7;
    }

    public final byte[] zzl(byte[] bArr) throws IOException {
        try {
            OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gZIPOutputStream.write(bArr);
            gZIPOutputStream.close();
            byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            super.zzwF().zzyx().zzj("Failed to gzip content", e);
            throw e;
        }
    }

    public final Object zzm(String str, Object obj) {
        return "_ldl".equals(str) ? zza(zzew(str), obj, true) : zza(zzew(str), obj, false);
    }

    public final byte[] zzm(byte[] bArr) throws IOException {
        try {
            InputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
            GZIPInputStream gZIPInputStream = new GZIPInputStream(byteArrayInputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bArr2 = new byte[1024];
            while (true) {
                int read = gZIPInputStream.read(bArr2);
                if (read > 0) {
                    byteArrayOutputStream.write(bArr2, 0, read);
                } else {
                    gZIPInputStream.close();
                    byteArrayInputStream.close();
                    return byteArrayOutputStream.toByteArray();
                }
            }
        } catch (IOException e) {
            super.zzwF().zzyx().zzj("Failed to ungzip content", e);
            throw e;
        }
    }

    public final Bundle zzq(@NonNull Uri uri) {
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
                super.zzwF().zzyz().zzj("Install referrer url isn't a hierarchical URI", e);
            }
        }
        return bundle;
    }

    public final /* bridge */ /* synthetic */ zzcfj zzwA() {
        return super.zzwA();
    }

    public final /* bridge */ /* synthetic */ zzcjl zzwB() {
        return super.zzwB();
    }

    public final /* bridge */ /* synthetic */ zzcgf zzwC() {
        return super.zzwC();
    }

    public final /* bridge */ /* synthetic */ zzcja zzwD() {
        return super.zzwD();
    }

    public final /* bridge */ /* synthetic */ zzcgg zzwE() {
        return super.zzwE();
    }

    public final /* bridge */ /* synthetic */ zzcfl zzwF() {
        return super.zzwF();
    }

    public final /* bridge */ /* synthetic */ zzcfw zzwG() {
        return super.zzwG();
    }

    public final /* bridge */ /* synthetic */ zzcem zzwH() {
        return super.zzwH();
    }

    public final /* bridge */ /* synthetic */ void zzwo() {
        super.zzwo();
    }

    public final /* bridge */ /* synthetic */ void zzwp() {
        super.zzwp();
    }

    public final /* bridge */ /* synthetic */ void zzwq() {
        super.zzwq();
    }

    public final /* bridge */ /* synthetic */ zzcec zzwr() {
        return super.zzwr();
    }

    public final /* bridge */ /* synthetic */ zzcej zzws() {
        return super.zzws();
    }

    public final /* bridge */ /* synthetic */ zzchl zzwt() {
        return super.zzwt();
    }

    public final /* bridge */ /* synthetic */ zzcfg zzwu() {
        return super.zzwu();
    }

    public final /* bridge */ /* synthetic */ zzcet zzwv() {
        return super.zzwv();
    }

    public final /* bridge */ /* synthetic */ zzcid zzww() {
        return super.zzww();
    }

    public final /* bridge */ /* synthetic */ zzchz zzwx() {
        return super.zzwx();
    }

    public final /* bridge */ /* synthetic */ zzcfh zzwy() {
        return super.zzwy();
    }

    public final /* bridge */ /* synthetic */ zzcen zzwz() {
        return super.zzwz();
    }

    public final long zzzs() {
        long nextLong;
        if (this.zzbuF.get() == 0) {
            synchronized (this.zzbuF) {
                nextLong = new Random(System.nanoTime() ^ super.zzkq().currentTimeMillis()).nextLong();
                int i = this.zzbuG + 1;
                this.zzbuG = i;
                nextLong += (long) i;
            }
        } else {
            synchronized (this.zzbuF) {
                this.zzbuF.compareAndSet(-1, 1);
                nextLong = this.zzbuF.getAndIncrement();
            }
        }
        return nextLong;
    }

    @WorkerThread
    final SecureRandom zzzt() {
        super.zzjC();
        if (this.zzbuE == null) {
            this.zzbuE = new SecureRandom();
        }
        return this.zzbuE;
    }
}

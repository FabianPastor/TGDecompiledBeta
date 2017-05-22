package com.google.android.gms.dynamite;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.DynamiteApi;
import com.google.android.gms.common.zze;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.zzd;
import dalvik.system.PathClassLoader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public final class DynamiteModule {
    private static Boolean zzaRO;
    private static zza zzaRP;
    private static zzb zzaRQ;
    private static final HashMap<String, byte[]> zzaRR = new HashMap();
    private static String zzaRS;
    private static final zza zzaRT = new zza() {
        public int zzI(Context context, String str) {
            return DynamiteModule.zzI(context, str);
        }

        public int zzb(Context context, String str, boolean z) throws zza {
            return DynamiteModule.zzb(context, str, z);
        }
    };
    public static final zzb zzaRU = new zzb() {
        public zzb zza(Context context, String str, zza com_google_android_gms_dynamite_DynamiteModule_zzb_zza) throws zza {
            zzb com_google_android_gms_dynamite_DynamiteModule_zzb_zzb = new zzb();
            com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSc = com_google_android_gms_dynamite_DynamiteModule_zzb_zza.zzb(context, str, true);
            if (com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSc != 0) {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSd = 1;
            } else {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSb = com_google_android_gms_dynamite_DynamiteModule_zzb_zza.zzI(context, str);
                if (com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSb != 0) {
                    com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSd = -1;
                }
            }
            return com_google_android_gms_dynamite_DynamiteModule_zzb_zzb;
        }
    };
    public static final zzb zzaRV = new zzb() {
        public zzb zza(Context context, String str, zza com_google_android_gms_dynamite_DynamiteModule_zzb_zza) throws zza {
            zzb com_google_android_gms_dynamite_DynamiteModule_zzb_zzb = new zzb();
            com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSb = com_google_android_gms_dynamite_DynamiteModule_zzb_zza.zzI(context, str);
            if (com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSb != 0) {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSd = -1;
            } else {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSc = com_google_android_gms_dynamite_DynamiteModule_zzb_zza.zzb(context, str, true);
                if (com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSc != 0) {
                    com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSd = 1;
                }
            }
            return com_google_android_gms_dynamite_DynamiteModule_zzb_zzb;
        }
    };
    public static final zzb zzaRW = new zzb() {
        public zzb zza(Context context, String str, zza com_google_android_gms_dynamite_DynamiteModule_zzb_zza) throws zza {
            zzb com_google_android_gms_dynamite_DynamiteModule_zzb_zzb = new zzb();
            com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSb = com_google_android_gms_dynamite_DynamiteModule_zzb_zza.zzI(context, str);
            com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSc = com_google_android_gms_dynamite_DynamiteModule_zzb_zza.zzb(context, str, true);
            if (com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSb == 0 && com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSc == 0) {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSd = 0;
            } else if (com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSb >= com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSc) {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSd = -1;
            } else {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSd = 1;
            }
            return com_google_android_gms_dynamite_DynamiteModule_zzb_zzb;
        }
    };
    public static final zzb zzaRX = new zzb() {
        public zzb zza(Context context, String str, zza com_google_android_gms_dynamite_DynamiteModule_zzb_zza) throws zza {
            zzb com_google_android_gms_dynamite_DynamiteModule_zzb_zzb = new zzb();
            com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSb = com_google_android_gms_dynamite_DynamiteModule_zzb_zza.zzI(context, str);
            com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSc = com_google_android_gms_dynamite_DynamiteModule_zzb_zza.zzb(context, str, true);
            if (com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSb == 0 && com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSc == 0) {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSd = 0;
            } else if (com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSc >= com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSb) {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSd = 1;
            } else {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSd = -1;
            }
            return com_google_android_gms_dynamite_DynamiteModule_zzb_zzb;
        }
    };
    public static final zzb zzaRY = new zzb() {
        public zzb zza(Context context, String str, zza com_google_android_gms_dynamite_DynamiteModule_zzb_zza) throws zza {
            zzb com_google_android_gms_dynamite_DynamiteModule_zzb_zzb = new zzb();
            com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSb = com_google_android_gms_dynamite_DynamiteModule_zzb_zza.zzI(context, str);
            if (com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSb != 0) {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSc = com_google_android_gms_dynamite_DynamiteModule_zzb_zza.zzb(context, str, false);
            } else {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSc = com_google_android_gms_dynamite_DynamiteModule_zzb_zza.zzb(context, str, true);
            }
            if (com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSb == 0 && com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSc == 0) {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSd = 0;
            } else if (com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSc >= com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSb) {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSd = 1;
            } else {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaSd = -1;
            }
            return com_google_android_gms_dynamite_DynamiteModule_zzb_zzb;
        }
    };
    private final Context zzaRZ;

    class AnonymousClass8 extends PathClassLoader {
        AnonymousClass8(String str, ClassLoader classLoader) {
            super(str, classLoader);
        }

        protected Class<?> loadClass(String str, boolean z) throws ClassNotFoundException {
            if (!(str.startsWith("java.") || str.startsWith("android."))) {
                try {
                    return findClass(str);
                } catch (ClassNotFoundException e) {
                }
            }
            return super.loadClass(str, z);
        }
    }

    @DynamiteApi
    public static class DynamiteLoaderClassLoader {
        public static ClassLoader sClassLoader;
    }

    public static class zza extends Exception {
        private zza(String str) {
            super(str);
        }

        private zza(String str, Throwable th) {
            super(str, th);
        }
    }

    public interface zzb {

        public interface zza {
            int zzI(Context context, String str);

            int zzb(Context context, String str, boolean z) throws zza;
        }

        public static class zzb {
            public int zzaSb = 0;
            public int zzaSc = 0;
            public int zzaSd = 0;
        }

        zzb zza(Context context, String str, zza com_google_android_gms_dynamite_DynamiteModule_zzb_zza) throws zza;
    }

    class AnonymousClass7 implements zza {
        final /* synthetic */ int zzaSa;

        AnonymousClass7(int i) {
            this.zzaSa = i;
        }

        public int zzI(Context context, String str) {
            return this.zzaSa;
        }

        public int zzb(Context context, String str, boolean z) {
            return 0;
        }
    }

    private DynamiteModule(Context context) {
        this.zzaRZ = (Context) zzac.zzw(context);
    }

    private static ClassLoader zzBT() {
        return new AnonymousClass8(zzaRS, ClassLoader.getSystemClassLoader());
    }

    public static int zzI(Context context, String str) {
        String valueOf;
        String valueOf2;
        try {
            ClassLoader classLoader = context.getApplicationContext().getClassLoader();
            valueOf2 = String.valueOf("com.google.android.gms.dynamite.descriptors.");
            valueOf = String.valueOf("ModuleDescriptor");
            Class loadClass = classLoader.loadClass(new StringBuilder(((String.valueOf(valueOf2).length() + 1) + String.valueOf(str).length()) + String.valueOf(valueOf).length()).append(valueOf2).append(str).append(".").append(valueOf).toString());
            Field declaredField = loadClass.getDeclaredField("MODULE_ID");
            Field declaredField2 = loadClass.getDeclaredField("MODULE_VERSION");
            if (declaredField.get(null).equals(str)) {
                return declaredField2.getInt(null);
            }
            valueOf2 = String.valueOf(declaredField.get(null));
            Log.e("DynamiteModule", new StringBuilder((String.valueOf(valueOf2).length() + 51) + String.valueOf(str).length()).append("Module descriptor id '").append(valueOf2).append("' didn't match expected id '").append(str).append("'").toString());
            return 0;
        } catch (ClassNotFoundException e) {
            Log.w("DynamiteModule", new StringBuilder(String.valueOf(str).length() + 45).append("Local module descriptor class for ").append(str).append(" not found.").toString());
            return 0;
        } catch (Exception e2) {
            valueOf2 = "DynamiteModule";
            valueOf = "Failed to load module descriptor class: ";
            String valueOf3 = String.valueOf(e2.getMessage());
            Log.e(valueOf2, valueOf3.length() != 0 ? valueOf.concat(valueOf3) : new String(valueOf));
            return 0;
        }
    }

    public static int zzJ(Context context, String str) {
        return zzb(context, str, false);
    }

    private static DynamiteModule zzK(Context context, String str) {
        String str2 = "DynamiteModule";
        String str3 = "Selected local version of ";
        String valueOf = String.valueOf(str);
        Log.i(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
        return new DynamiteModule(context.getApplicationContext());
    }

    private static Context zza(Context context, String str, byte[] bArr, zzb com_google_android_gms_dynamite_zzb) {
        try {
            return (Context) zzd.zzF(com_google_android_gms_dynamite_zzb.zza(zzd.zzA(context), str, bArr));
        } catch (Exception e) {
            String str2 = "DynamiteModule";
            String str3 = "Failed to load DynamiteLoader: ";
            String valueOf = String.valueOf(e.toString());
            Log.e(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
            return null;
        }
    }

    public static DynamiteModule zza(Context context, zzb com_google_android_gms_dynamite_DynamiteModule_zzb, String str) throws zza {
        zzb zza = com_google_android_gms_dynamite_DynamiteModule_zzb.zza(context, str, zzaRT);
        Log.i("DynamiteModule", new StringBuilder((String.valueOf(str).length() + 68) + String.valueOf(str).length()).append("Considering local module ").append(str).append(":").append(zza.zzaSb).append(" and remote module ").append(str).append(":").append(zza.zzaSc).toString());
        if (zza.zzaSd == 0 || ((zza.zzaSd == -1 && zza.zzaSb == 0) || (zza.zzaSd == 1 && zza.zzaSc == 0))) {
            throw new zza("No acceptable module found. Local version is " + zza.zzaSb + " and remote version is " + zza.zzaSc + ".");
        } else if (zza.zzaSd == -1) {
            return zzK(context, str);
        } else {
            if (zza.zzaSd == 1) {
                try {
                    return zza(context, str, zza.zzaSc);
                } catch (Throwable e) {
                    Throwable th = e;
                    String str2 = "DynamiteModule";
                    String str3 = "Failed to load remote module: ";
                    String valueOf = String.valueOf(th.getMessage());
                    Log.w(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
                    if (zza.zzaSb != 0 && com_google_android_gms_dynamite_DynamiteModule_zzb.zza(context, str, new AnonymousClass7(zza.zzaSb)).zzaSd == -1) {
                        return zzK(context, str);
                    }
                    throw new zza("Remote load failed. No local fallback found.", th);
                }
            }
            throw new zza("VersionPolicy returned invalid code:" + zza.zzaSd);
        }
    }

    private static DynamiteModule zza(Context context, String str, int i) throws zza {
        synchronized (DynamiteModule.class) {
            Boolean bool = zzaRO;
        }
        if (bool != null) {
            return bool.booleanValue() ? zzc(context, str, i) : zzb(context, str, i);
        } else {
            throw new zza("Failed to determine which loading route to use.");
        }
    }

    private static void zza(ClassLoader classLoader) throws zza {
        Throwable e;
        try {
            zzaRQ = com.google.android.gms.dynamite.zzb.zza.zzcf((IBinder) classLoader.loadClass("com.google.android.gms.dynamiteloader.DynamiteLoaderV2").getConstructor(new Class[0]).newInstance(new Object[0]));
        } catch (ClassNotFoundException e2) {
            e = e2;
            throw new zza("Failed to instantiate dynamite loader", e);
        } catch (IllegalAccessException e3) {
            e = e3;
            throw new zza("Failed to instantiate dynamite loader", e);
        } catch (InstantiationException e4) {
            e = e4;
            throw new zza("Failed to instantiate dynamite loader", e);
        } catch (InvocationTargetException e5) {
            e = e5;
            throw new zza("Failed to instantiate dynamite loader", e);
        } catch (NoSuchMethodException e6) {
            e = e6;
            throw new zza("Failed to instantiate dynamite loader", e);
        }
    }

    public static int zzb(Context context, String str, boolean z) {
        Object e;
        synchronized (DynamiteModule.class) {
            Boolean bool = zzaRO;
            if (bool == null) {
                try {
                    Class loadClass = context.getApplicationContext().getClassLoader().loadClass(DynamiteLoaderClassLoader.class.getName());
                    Field declaredField = loadClass.getDeclaredField("sClassLoader");
                    synchronized (loadClass) {
                        ClassLoader classLoader = (ClassLoader) declaredField.get(null);
                        if (classLoader != null) {
                            if (classLoader == ClassLoader.getSystemClassLoader()) {
                                bool = Boolean.FALSE;
                            } else {
                                try {
                                    zza(classLoader);
                                } catch (zza e2) {
                                }
                                bool = Boolean.TRUE;
                            }
                        } else if ("com.google.android.gms".equals(context.getApplicationContext().getPackageName())) {
                            declaredField.set(null, ClassLoader.getSystemClassLoader());
                            bool = Boolean.FALSE;
                        } else {
                            try {
                                int zzd = zzd(context, str, z);
                                if (zzaRS == null || zzaRS.isEmpty()) {
                                    return zzd;
                                }
                                ClassLoader zzBT = zzBT();
                                zza(zzBT);
                                declaredField.set(null, zzBT);
                                zzaRO = Boolean.TRUE;
                                return zzd;
                            } catch (zza e3) {
                                declaredField.set(null, ClassLoader.getSystemClassLoader());
                                bool = Boolean.FALSE;
                                zzaRO = bool;
                                if (!bool.booleanValue()) {
                                    try {
                                    } catch (zza e4) {
                                        String str2 = "DynamiteModule";
                                        String str3 = "Failed to retrieve remote module version: ";
                                        String valueOf = String.valueOf(e4.getMessage());
                                        Log.w(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
                                        return 0;
                                    }
                                }
                            }
                        }
                    }
                } catch (ClassNotFoundException e5) {
                    e = e5;
                } catch (IllegalAccessException e6) {
                    e = e6;
                } catch (NoSuchFieldException e7) {
                    e = e7;
                }
            }
        }
        valueOf = String.valueOf(e);
        Log.w("DynamiteModule", new StringBuilder(String.valueOf(valueOf).length() + 30).append("Failed to load module via V2: ").append(valueOf).toString());
        bool = Boolean.FALSE;
        zzaRO = bool;
        return !bool.booleanValue() ? zzc(context, str, z) : zzd(context, str, z);
    }

    private static DynamiteModule zzb(Context context, String str, int i) throws zza {
        Log.i("DynamiteModule", new StringBuilder(String.valueOf(str).length() + 51).append("Selected remote version of ").append(str).append(", version >= ").append(i).toString());
        zza zzbm = zzbm(context);
        if (zzbm == null) {
            throw new zza("Failed to create IDynamiteLoader.");
        }
        try {
            IObjectWrapper zza = zzbm.zza(zzd.zzA(context), str, i);
            if (zzd.zzF(zza) != null) {
                return new DynamiteModule((Context) zzd.zzF(zza));
            }
            throw new zza("Failed to load remote module.");
        } catch (Throwable e) {
            throw new zza("Failed to load remote module.", e);
        }
    }

    private static zza zzbm(Context context) {
        synchronized (DynamiteModule.class) {
            zza com_google_android_gms_dynamite_zza;
            if (zzaRP != null) {
                com_google_android_gms_dynamite_zza = zzaRP;
                return com_google_android_gms_dynamite_zza;
            } else if (zze.zzuY().isGooglePlayServicesAvailable(context) != 0) {
                return null;
            } else {
                try {
                    com_google_android_gms_dynamite_zza = com.google.android.gms.dynamite.zza.zza.zzce((IBinder) context.createPackageContext("com.google.android.gms", 3).getClassLoader().loadClass("com.google.android.gms.chimera.container.DynamiteLoaderImpl").newInstance());
                    if (com_google_android_gms_dynamite_zza != null) {
                        zzaRP = com_google_android_gms_dynamite_zza;
                        return com_google_android_gms_dynamite_zza;
                    }
                } catch (Exception e) {
                    String str = "DynamiteModule";
                    String str2 = "Failed to load IDynamiteLoader from GmsCore: ";
                    String valueOf = String.valueOf(e.getMessage());
                    Log.e(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
                    return null;
                }
            }
        }
    }

    private static int zzc(Context context, String str, boolean z) {
        zza zzbm = zzbm(context);
        if (zzbm == null) {
            return 0;
        }
        try {
            return zzbm.zza(zzd.zzA(context), str, z);
        } catch (RemoteException e) {
            String str2 = "DynamiteModule";
            String str3 = "Failed to retrieve remote module version: ";
            String valueOf = String.valueOf(e.getMessage());
            Log.w(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
            return 0;
        }
    }

    private static DynamiteModule zzc(Context context, String str, int i) throws zza {
        Log.i("DynamiteModule", new StringBuilder(String.valueOf(str).length() + 51).append("Selected remote version of ").append(str).append(", version >= ").append(i).toString());
        synchronized (DynamiteModule.class) {
            byte[] bArr = (byte[]) zzaRR.get(new StringBuilder(String.valueOf(str).length() + 12).append(str).append(":").append(i).toString());
            zzb com_google_android_gms_dynamite_zzb = zzaRQ;
        }
        if (bArr == null) {
            throw new zza("Module implementation could not be found.");
        } else if (com_google_android_gms_dynamite_zzb == null) {
            throw new zza("DynamiteLoaderV2 was not cached.");
        } else {
            Context zza = zza(context.getApplicationContext(), str, bArr, com_google_android_gms_dynamite_zzb);
            if (zza != null) {
                return new DynamiteModule(zza);
            }
            throw new zza("Failed to get module context");
        }
    }

    private static int zzd(Context context, String str, boolean z) throws zza {
        Cursor cursor = null;
        try {
            cursor = zze(context, str, z);
            if (cursor == null || !cursor.moveToFirst()) {
                Log.w("DynamiteModule", "Failed to retrieve remote module version.");
                throw new zza("Failed to connect to dynamite module ContentResolver.");
            }
            int i = cursor.getInt(0);
            if (i > 0) {
                synchronized (DynamiteModule.class) {
                    zzaRR.put(new StringBuilder(String.valueOf(str).length() + 12).append(str).append(":").append(i).toString(), Base64.decode(cursor.getString(3), 0));
                    zzaRS = cursor.getString(2);
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            return i;
        } catch (Throwable e) {
            if (e instanceof zza) {
                throw e;
            }
            throw new zza("V2 version check failed", e);
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static Cursor zze(Context context, String str, boolean z) {
        String str2 = z ? "api_force_staging" : "api";
        String valueOf = String.valueOf("content://com.google.android.gms.chimera/");
        return context.getContentResolver().query(Uri.parse(new StringBuilder(((String.valueOf(valueOf).length() + 1) + String.valueOf(str2).length()) + String.valueOf(str).length()).append(valueOf).append(str2).append("/").append(str).toString()), null, null, null, null);
    }

    public Context zzBS() {
        return this.zzaRZ;
    }

    public IBinder zzdT(String str) throws zza {
        Throwable e;
        String str2;
        String valueOf;
        try {
            return (IBinder) this.zzaRZ.getClassLoader().loadClass(str).newInstance();
        } catch (ClassNotFoundException e2) {
            e = e2;
            str2 = "Failed to instantiate module class: ";
            valueOf = String.valueOf(str);
            throw new zza(valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2), e);
        } catch (InstantiationException e3) {
            e = e3;
            str2 = "Failed to instantiate module class: ";
            valueOf = String.valueOf(str);
            if (valueOf.length() != 0) {
            }
            throw new zza(valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2), e);
        } catch (IllegalAccessException e4) {
            e = e4;
            str2 = "Failed to instantiate module class: ";
            valueOf = String.valueOf(str);
            if (valueOf.length() != 0) {
            }
            throw new zza(valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2), e);
        }
    }
}

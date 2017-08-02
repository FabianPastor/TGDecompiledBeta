package com.google.android.gms.dynamite;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.DynamiteApi;
import com.google.android.gms.common.zze;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.zzn;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public final class DynamiteModule {
    private static Boolean zzaSF;
    private static zzj zzaSG;
    private static zzl zzaSH;
    private static String zzaSI;
    private static final ThreadLocal<zza> zzaSJ = new ThreadLocal();
    private static final zzh zzaSK = new zza();
    public static final zzd zzaSL = new zzb();
    private static zzd zzaSM = new zzc();
    public static final zzd zzaSN = new zzd();
    public static final zzd zzaSO = new zze();
    public static final zzd zzaSP = new zzf();
    private final Context zzaSQ;

    @DynamiteApi
    public static class DynamiteLoaderClassLoader {
        public static ClassLoader sClassLoader;
    }

    static class zza {
        public Cursor zzaSR;

        private zza() {
        }
    }

    public static class zzc extends Exception {
        private zzc(String str) {
            super(str);
        }

        private zzc(String str, Throwable th) {
            super(str, th);
        }
    }

    public interface zzd {
        zzi zza(Context context, String str, zzh com_google_android_gms_dynamite_zzh) throws zzc;
    }

    static class zzb implements zzh {
        private final int zzaSS;
        private final int zzaST = 0;

        public zzb(int i, int i2) {
            this.zzaSS = i;
        }

        public final int zzE(Context context, String str) {
            return this.zzaSS;
        }

        public final int zzb(Context context, String str, boolean z) {
            return 0;
        }
    }

    private DynamiteModule(Context context) {
        this.zzaSQ = (Context) zzbo.zzu(context);
    }

    public static int zzE(Context context, String str) {
        String valueOf;
        String valueOf2;
        try {
            ClassLoader classLoader = context.getApplicationContext().getClassLoader();
            valueOf = String.valueOf("com.google.android.gms.dynamite.descriptors.");
            valueOf2 = String.valueOf("ModuleDescriptor");
            Class loadClass = classLoader.loadClass(new StringBuilder(((String.valueOf(valueOf).length() + 1) + String.valueOf(str).length()) + String.valueOf(valueOf2).length()).append(valueOf).append(str).append(".").append(valueOf2).toString());
            Field declaredField = loadClass.getDeclaredField("MODULE_ID");
            Field declaredField2 = loadClass.getDeclaredField("MODULE_VERSION");
            if (declaredField.get(null).equals(str)) {
                return declaredField2.getInt(null);
            }
            valueOf = String.valueOf(declaredField.get(null));
            Log.e("DynamiteModule", new StringBuilder((String.valueOf(valueOf).length() + 51) + String.valueOf(str).length()).append("Module descriptor id '").append(valueOf).append("' didn't match expected id '").append(str).append("'").toString());
            return 0;
        } catch (ClassNotFoundException e) {
            Log.w("DynamiteModule", new StringBuilder(String.valueOf(str).length() + 45).append("Local module descriptor class for ").append(str).append(" not found.").toString());
            return 0;
        } catch (Exception e2) {
            valueOf = "DynamiteModule";
            valueOf2 = "Failed to load module descriptor class: ";
            String valueOf3 = String.valueOf(e2.getMessage());
            Log.e(valueOf, valueOf3.length() != 0 ? valueOf2.concat(valueOf3) : new String(valueOf2));
            return 0;
        }
    }

    public static int zzF(Context context, String str) {
        return zzb(context, str, false);
    }

    private static DynamiteModule zzG(Context context, String str) {
        String str2 = "DynamiteModule";
        String str3 = "Selected local version of ";
        String valueOf = String.valueOf(str);
        Log.i(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
        return new DynamiteModule(context.getApplicationContext());
    }

    private static Context zza(Context context, String str, int i, Cursor cursor, zzl com_google_android_gms_dynamite_zzl) {
        try {
            return (Context) zzn.zzE(com_google_android_gms_dynamite_zzl.zza(zzn.zzw(context), str, i, zzn.zzw(cursor)));
        } catch (Exception e) {
            String str2 = "DynamiteModule";
            String str3 = "Failed to load DynamiteLoader: ";
            String valueOf = String.valueOf(e.toString());
            Log.e(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
            return null;
        }
    }

    public static DynamiteModule zza(Context context, zzd com_google_android_gms_dynamite_DynamiteModule_zzd, String str) throws zzc {
        zzi zza;
        DynamiteModule zzG;
        zza com_google_android_gms_dynamite_DynamiteModule_zza = (zza) zzaSJ.get();
        zza com_google_android_gms_dynamite_DynamiteModule_zza2 = new zza();
        zzaSJ.set(com_google_android_gms_dynamite_DynamiteModule_zza2);
        try {
            zza = com_google_android_gms_dynamite_DynamiteModule_zzd.zza(context, str, zzaSK);
            Log.i("DynamiteModule", new StringBuilder((String.valueOf(str).length() + 68) + String.valueOf(str).length()).append("Considering local module ").append(str).append(":").append(zza.zzaSU).append(" and remote module ").append(str).append(":").append(zza.zzaSV).toString());
            if (zza.zzaSW == 0 || ((zza.zzaSW == -1 && zza.zzaSU == 0) || (zza.zzaSW == 1 && zza.zzaSV == 0))) {
                throw new zzc("No acceptable module found. Local version is " + zza.zzaSU + " and remote version is " + zza.zzaSV + ".");
            } else if (zza.zzaSW == -1) {
                zzG = zzG(context, str);
                if (com_google_android_gms_dynamite_DynamiteModule_zza2.zzaSR != null) {
                    com_google_android_gms_dynamite_DynamiteModule_zza2.zzaSR.close();
                }
                zzaSJ.set(com_google_android_gms_dynamite_DynamiteModule_zza);
                return zzG;
            } else if (zza.zzaSW == 1) {
                zzG = zza(context, str, zza.zzaSV);
                if (com_google_android_gms_dynamite_DynamiteModule_zza2.zzaSR != null) {
                    com_google_android_gms_dynamite_DynamiteModule_zza2.zzaSR.close();
                }
                zzaSJ.set(com_google_android_gms_dynamite_DynamiteModule_zza);
                return zzG;
            } else {
                throw new zzc("VersionPolicy returned invalid code:" + zza.zzaSW);
            }
        } catch (Throwable e) {
            String str2 = "DynamiteModule";
            String str3 = "Failed to load remote module: ";
            String valueOf = String.valueOf(e.getMessage());
            Log.w(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
            if (zza.zzaSU == 0 || com_google_android_gms_dynamite_DynamiteModule_zzd.zza(context, str, new zzb(zza.zzaSU, 0)).zzaSW != -1) {
                throw new zzc("Remote load failed. No local fallback found.", e);
            }
            zzG = zzG(context, str);
            if (com_google_android_gms_dynamite_DynamiteModule_zza2.zzaSR != null) {
                com_google_android_gms_dynamite_DynamiteModule_zza2.zzaSR.close();
            }
            zzaSJ.set(com_google_android_gms_dynamite_DynamiteModule_zza);
            return zzG;
        } catch (Throwable th) {
            if (com_google_android_gms_dynamite_DynamiteModule_zza2.zzaSR != null) {
                com_google_android_gms_dynamite_DynamiteModule_zza2.zzaSR.close();
            }
            zzaSJ.set(com_google_android_gms_dynamite_DynamiteModule_zza);
        }
    }

    private static DynamiteModule zza(Context context, String str, int i) throws zzc {
        synchronized (DynamiteModule.class) {
            Boolean bool = zzaSF;
        }
        if (bool != null) {
            return bool.booleanValue() ? zzc(context, str, i) : zzb(context, str, i);
        } else {
            throw new zzc("Failed to determine which loading route to use.");
        }
    }

    private static void zza(ClassLoader classLoader) throws zzc {
        Throwable e;
        try {
            zzl com_google_android_gms_dynamite_zzl;
            IBinder iBinder = (IBinder) classLoader.loadClass("com.google.android.gms.dynamiteloader.DynamiteLoaderV2").getConstructor(new Class[0]).newInstance(new Object[0]);
            if (iBinder == null) {
                com_google_android_gms_dynamite_zzl = null;
            } else {
                IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.dynamite.IDynamiteLoaderV2");
                if (queryLocalInterface instanceof zzl) {
                    com_google_android_gms_dynamite_zzl = (zzl) queryLocalInterface;
                } else {
                    Object com_google_android_gms_dynamite_zzm = new zzm(iBinder);
                }
            }
            zzaSH = com_google_android_gms_dynamite_zzl;
        } catch (ClassNotFoundException e2) {
            e = e2;
            throw new zzc("Failed to instantiate dynamite loader", e);
        } catch (IllegalAccessException e3) {
            e = e3;
            throw new zzc("Failed to instantiate dynamite loader", e);
        } catch (InstantiationException e4) {
            e = e4;
            throw new zzc("Failed to instantiate dynamite loader", e);
        } catch (InvocationTargetException e5) {
            e = e5;
            throw new zzc("Failed to instantiate dynamite loader", e);
        } catch (NoSuchMethodException e6) {
            e = e6;
            throw new zzc("Failed to instantiate dynamite loader", e);
        }
    }

    private static zzj zzaT(Context context) {
        synchronized (DynamiteModule.class) {
            zzj com_google_android_gms_dynamite_zzj;
            if (zzaSG != null) {
                com_google_android_gms_dynamite_zzj = zzaSG;
                return com_google_android_gms_dynamite_zzj;
            } else if (zze.zzoW().isGooglePlayServicesAvailable(context) != 0) {
                return null;
            } else {
                try {
                    IBinder iBinder = (IBinder) context.createPackageContext("com.google.android.gms", 3).getClassLoader().loadClass("com.google.android.gms.chimera.container.DynamiteLoaderImpl").newInstance();
                    if (iBinder == null) {
                        com_google_android_gms_dynamite_zzj = null;
                    } else {
                        IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.dynamite.IDynamiteLoader");
                        if (queryLocalInterface instanceof zzj) {
                            com_google_android_gms_dynamite_zzj = (zzj) queryLocalInterface;
                        } else {
                            Object com_google_android_gms_dynamite_zzk = new zzk(iBinder);
                        }
                    }
                    if (com_google_android_gms_dynamite_zzj != null) {
                        zzaSG = com_google_android_gms_dynamite_zzj;
                        return com_google_android_gms_dynamite_zzj;
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

    public static int zzb(Context context, String str, boolean z) {
        Object e;
        synchronized (DynamiteModule.class) {
            Boolean bool = zzaSF;
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
                                } catch (zzc e2) {
                                }
                                bool = Boolean.TRUE;
                            }
                        } else if ("com.google.android.gms".equals(context.getApplicationContext().getPackageName())) {
                            declaredField.set(null, ClassLoader.getSystemClassLoader());
                            bool = Boolean.FALSE;
                        } else {
                            try {
                                int zzd = zzd(context, str, z);
                                if (zzaSI == null || zzaSI.isEmpty()) {
                                    return zzd;
                                }
                                ClassLoader com_google_android_gms_dynamite_zzg = new zzg(zzaSI, ClassLoader.getSystemClassLoader());
                                zza(com_google_android_gms_dynamite_zzg);
                                declaredField.set(null, com_google_android_gms_dynamite_zzg);
                                zzaSF = Boolean.TRUE;
                                return zzd;
                            } catch (zzc e3) {
                                declaredField.set(null, ClassLoader.getSystemClassLoader());
                                bool = Boolean.FALSE;
                                zzaSF = bool;
                                if (!bool.booleanValue()) {
                                    try {
                                    } catch (zzc e4) {
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
        zzaSF = bool;
        return !bool.booleanValue() ? zzc(context, str, z) : zzd(context, str, z);
    }

    private static DynamiteModule zzb(Context context, String str, int i) throws zzc {
        Log.i("DynamiteModule", new StringBuilder(String.valueOf(str).length() + 51).append("Selected remote version of ").append(str).append(", version >= ").append(i).toString());
        zzj zzaT = zzaT(context);
        if (zzaT == null) {
            throw new zzc("Failed to create IDynamiteLoader.");
        }
        try {
            IObjectWrapper zza = zzaT.zza(zzn.zzw(context), str, i);
            if (zzn.zzE(zza) != null) {
                return new DynamiteModule((Context) zzn.zzE(zza));
            }
            throw new zzc("Failed to load remote module.");
        } catch (Throwable e) {
            throw new zzc("Failed to load remote module.", e);
        }
    }

    private static int zzc(Context context, String str, boolean z) {
        zzj zzaT = zzaT(context);
        if (zzaT == null) {
            return 0;
        }
        try {
            return zzaT.zza(zzn.zzw(context), str, z);
        } catch (RemoteException e) {
            String str2 = "DynamiteModule";
            String str3 = "Failed to retrieve remote module version: ";
            String valueOf = String.valueOf(e.getMessage());
            Log.w(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
            return 0;
        }
    }

    private static DynamiteModule zzc(Context context, String str, int i) throws zzc {
        Log.i("DynamiteModule", new StringBuilder(String.valueOf(str).length() + 51).append("Selected remote version of ").append(str).append(", version >= ").append(i).toString());
        synchronized (DynamiteModule.class) {
            zzl com_google_android_gms_dynamite_zzl = zzaSH;
        }
        if (com_google_android_gms_dynamite_zzl == null) {
            throw new zzc("DynamiteLoaderV2 was not cached.");
        }
        zza com_google_android_gms_dynamite_DynamiteModule_zza = (zza) zzaSJ.get();
        if (com_google_android_gms_dynamite_DynamiteModule_zza == null || com_google_android_gms_dynamite_DynamiteModule_zza.zzaSR == null) {
            throw new zzc("No result cursor");
        }
        Context zza = zza(context.getApplicationContext(), str, i, com_google_android_gms_dynamite_DynamiteModule_zza.zzaSR, com_google_android_gms_dynamite_zzl);
        if (zza != null) {
            return new DynamiteModule(zza);
        }
        throw new zzc("Failed to get module context");
    }

    private static int zzd(Context context, String str, boolean z) throws zzc {
        String str2;
        Throwable e;
        Cursor cursor;
        if (z) {
            try {
                str2 = "api_force_staging";
            } catch (Exception e2) {
                e = e2;
                cursor = null;
                try {
                    if (e instanceof zzc) {
                        throw e;
                    }
                    throw new zzc("V2 version check failed", e);
                } catch (Throwable th) {
                    e = th;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw e;
                }
            } catch (Throwable th2) {
                e = th2;
                cursor = null;
                if (cursor != null) {
                    cursor.close();
                }
                throw e;
            }
        }
        str2 = "api";
        String valueOf = String.valueOf("content://com.google.android.gms.chimera/");
        cursor = context.getContentResolver().query(Uri.parse(new StringBuilder(((String.valueOf(valueOf).length() + 1) + String.valueOf(str2).length()) + String.valueOf(str).length()).append(valueOf).append(str2).append("/").append(str).toString()), null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int i = cursor.getInt(0);
                    if (i > 0) {
                        synchronized (DynamiteModule.class) {
                            zzaSI = cursor.getString(2);
                        }
                        zza com_google_android_gms_dynamite_DynamiteModule_zza = (zza) zzaSJ.get();
                        if (com_google_android_gms_dynamite_DynamiteModule_zza != null && com_google_android_gms_dynamite_DynamiteModule_zza.zzaSR == null) {
                            com_google_android_gms_dynamite_DynamiteModule_zza.zzaSR = cursor;
                            cursor = null;
                        }
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                    return i;
                }
            } catch (Exception e3) {
                e = e3;
            }
        }
        Log.w("DynamiteModule", "Failed to retrieve remote module version.");
        throw new zzc("Failed to connect to dynamite module ContentResolver.");
    }

    public final IBinder zzcV(String str) throws zzc {
        Throwable e;
        String str2;
        String valueOf;
        try {
            return (IBinder) this.zzaSQ.getClassLoader().loadClass(str).newInstance();
        } catch (ClassNotFoundException e2) {
            e = e2;
            str2 = "Failed to instantiate module class: ";
            valueOf = String.valueOf(str);
            throw new zzc(valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2), e);
        } catch (InstantiationException e3) {
            e = e3;
            str2 = "Failed to instantiate module class: ";
            valueOf = String.valueOf(str);
            if (valueOf.length() != 0) {
            }
            throw new zzc(valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2), e);
        } catch (IllegalAccessException e4) {
            e = e4;
            str2 = "Failed to instantiate module class: ";
            valueOf = String.valueOf(str);
            if (valueOf.length() != 0) {
            }
            throw new zzc(valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2), e);
        }
    }

    public final Context zztC() {
        return this.zzaSQ;
    }
}

package com.google.android.gms.internal;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.zzc;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamic.zze;
import dalvik.system.PathClassLoader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public final class zztl {
    private static zztm Qh;
    private static final HashMap<String, byte[]> Qi = new HashMap();
    private static String Qj;
    private static final zza Qk = new zza() {
        public zztl zza(Context context, String str, int i) throws zza {
            return zztl.zza(context, str, i);
        }

        public int zzaa(Context context, String str) {
            return zztl.zzaa(context, str);
        }

        public int zzb(Context context, String str, boolean z) throws zza {
            return zztl.zzb(context, str, z);
        }
    };
    private static final zza Ql = new zza() {
        public zztl zza(Context context, String str, int i) throws zza {
            return zztl.zzb(context, str, i);
        }

        public int zzaa(Context context, String str) {
            return zztl.zzaa(context, str);
        }

        public int zzb(Context context, String str, boolean z) throws zza {
            return zztl.zzc(context, str, z);
        }
    };
    public static final zzb Qm = new zzb() {
        public zzb zza(Context context, String str, zza com_google_android_gms_internal_zztl_zzb_zza) throws zza {
            zzb com_google_android_gms_internal_zztl_zzb_zzb = new zzb();
            com_google_android_gms_internal_zztl_zzb_zzb.Qu = com_google_android_gms_internal_zztl_zzb_zza.zzb(context, str, true);
            if (com_google_android_gms_internal_zztl_zzb_zzb.Qu != 0) {
                com_google_android_gms_internal_zztl_zzb_zzb.Qv = 1;
            } else {
                com_google_android_gms_internal_zztl_zzb_zzb.Qt = com_google_android_gms_internal_zztl_zzb_zza.zzaa(context, str);
                if (com_google_android_gms_internal_zztl_zzb_zzb.Qt != 0) {
                    com_google_android_gms_internal_zztl_zzb_zzb.Qv = -1;
                }
            }
            return com_google_android_gms_internal_zztl_zzb_zzb;
        }
    };
    public static final zzb Qn = new zzb() {
        public zzb zza(Context context, String str, zza com_google_android_gms_internal_zztl_zzb_zza) throws zza {
            zzb com_google_android_gms_internal_zztl_zzb_zzb = new zzb();
            com_google_android_gms_internal_zztl_zzb_zzb.Qt = com_google_android_gms_internal_zztl_zzb_zza.zzaa(context, str);
            if (com_google_android_gms_internal_zztl_zzb_zzb.Qt != 0) {
                com_google_android_gms_internal_zztl_zzb_zzb.Qv = -1;
            } else {
                com_google_android_gms_internal_zztl_zzb_zzb.Qu = com_google_android_gms_internal_zztl_zzb_zza.zzb(context, str, true);
                if (com_google_android_gms_internal_zztl_zzb_zzb.Qu != 0) {
                    com_google_android_gms_internal_zztl_zzb_zzb.Qv = 1;
                }
            }
            return com_google_android_gms_internal_zztl_zzb_zzb;
        }
    };
    public static final zzb Qo = new zzb() {
        public zzb zza(Context context, String str, zza com_google_android_gms_internal_zztl_zzb_zza) throws zza {
            zzb com_google_android_gms_internal_zztl_zzb_zzb = new zzb();
            com_google_android_gms_internal_zztl_zzb_zzb.Qt = com_google_android_gms_internal_zztl_zzb_zza.zzaa(context, str);
            com_google_android_gms_internal_zztl_zzb_zzb.Qu = com_google_android_gms_internal_zztl_zzb_zza.zzb(context, str, true);
            if (com_google_android_gms_internal_zztl_zzb_zzb.Qt == 0 && com_google_android_gms_internal_zztl_zzb_zzb.Qu == 0) {
                com_google_android_gms_internal_zztl_zzb_zzb.Qv = 0;
            } else if (com_google_android_gms_internal_zztl_zzb_zzb.Qt >= com_google_android_gms_internal_zztl_zzb_zzb.Qu) {
                com_google_android_gms_internal_zztl_zzb_zzb.Qv = -1;
            } else {
                com_google_android_gms_internal_zztl_zzb_zzb.Qv = 1;
            }
            return com_google_android_gms_internal_zztl_zzb_zzb;
        }
    };
    public static final zzb Qp = new zzb() {
        public zzb zza(Context context, String str, zza com_google_android_gms_internal_zztl_zzb_zza) throws zza {
            zzb com_google_android_gms_internal_zztl_zzb_zzb = new zzb();
            com_google_android_gms_internal_zztl_zzb_zzb.Qt = com_google_android_gms_internal_zztl_zzb_zza.zzaa(context, str);
            com_google_android_gms_internal_zztl_zzb_zzb.Qu = com_google_android_gms_internal_zztl_zzb_zza.zzb(context, str, true);
            if (com_google_android_gms_internal_zztl_zzb_zzb.Qt == 0 && com_google_android_gms_internal_zztl_zzb_zzb.Qu == 0) {
                com_google_android_gms_internal_zztl_zzb_zzb.Qv = 0;
            } else if (com_google_android_gms_internal_zztl_zzb_zzb.Qu >= com_google_android_gms_internal_zztl_zzb_zzb.Qt) {
                com_google_android_gms_internal_zztl_zzb_zzb.Qv = 1;
            } else {
                com_google_android_gms_internal_zztl_zzb_zzb.Qv = -1;
            }
            return com_google_android_gms_internal_zztl_zzb_zzb;
        }
    };
    public static final zzb Qq = new zzb() {
        public zzb zza(Context context, String str, zza com_google_android_gms_internal_zztl_zzb_zza) throws zza {
            zzb com_google_android_gms_internal_zztl_zzb_zzb = new zzb();
            com_google_android_gms_internal_zztl_zzb_zzb.Qt = com_google_android_gms_internal_zztl_zzb_zza.zzaa(context, str);
            if (com_google_android_gms_internal_zztl_zzb_zzb.Qt != 0) {
                com_google_android_gms_internal_zztl_zzb_zzb.Qu = com_google_android_gms_internal_zztl_zzb_zza.zzb(context, str, false);
            } else {
                com_google_android_gms_internal_zztl_zzb_zzb.Qu = com_google_android_gms_internal_zztl_zzb_zza.zzb(context, str, true);
            }
            if (com_google_android_gms_internal_zztl_zzb_zzb.Qt == 0 && com_google_android_gms_internal_zztl_zzb_zzb.Qu == 0) {
                com_google_android_gms_internal_zztl_zzb_zzb.Qv = 0;
            } else if (com_google_android_gms_internal_zztl_zzb_zzb.Qu >= com_google_android_gms_internal_zztl_zzb_zzb.Qt) {
                com_google_android_gms_internal_zztl_zzb_zzb.Qv = 1;
            } else {
                com_google_android_gms_internal_zztl_zzb_zzb.Qv = -1;
            }
            return com_google_android_gms_internal_zztl_zzb_zzb;
        }
    };
    private final Context Qr;

    class AnonymousClass9 extends PathClassLoader {
        AnonymousClass9(String str, ClassLoader classLoader) {
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
            zztl zza(Context context, String str, int i) throws zza;

            int zzaa(Context context, String str);

            int zzb(Context context, String str, boolean z) throws zza;
        }

        public static class zzb {
            public int Qt = 0;
            public int Qu = 0;
            public int Qv = 0;
        }

        zzb zza(Context context, String str, zza com_google_android_gms_internal_zztl_zzb_zza) throws zza;
    }

    class AnonymousClass8 implements zza {
        final /* synthetic */ int Qs;

        AnonymousClass8(int i) {
            this.Qs = i;
        }

        public zztl zza(Context context, String str, int i) throws zza {
            throw new zza("local only VersionPolicy should not load from remote");
        }

        public int zzaa(Context context, String str) {
            return this.Qs;
        }

        public int zzb(Context context, String str, boolean z) {
            return 0;
        }
    }

    private zztl(Context context) {
        this.Qr = (Context) zzaa.zzy(context);
    }

    private static Context zza(Context context, String str, byte[] bArr, String str2) {
        Exception e;
        String str3;
        String str4;
        String valueOf;
        if (str2 == null || str2.isEmpty()) {
            Log.e("DynamiteModule", "No valid DynamiteLoader APK path");
            return null;
        }
        try {
            return (Context) zze.zzae(com.google.android.gms.internal.zztn.zza.zzff((IBinder) new AnonymousClass9(str2, context.getClassLoader()).loadClass("com.google.android.gms.dynamiteloader.DynamiteLoaderV2").getConstructor(new Class[0]).newInstance(new Object[0])).zza(zze.zzac(context), str, bArr));
        } catch (RemoteException e2) {
            e = e2;
            str3 = "DynamiteModule";
            str4 = "Failed to load DynamiteLoader: ";
            valueOf = String.valueOf(e.toString());
            Log.e(str3, valueOf.length() == 0 ? str4.concat(valueOf) : new String(str4));
            return null;
        } catch (ClassNotFoundException e3) {
            e = e3;
            str3 = "DynamiteModule";
            str4 = "Failed to load DynamiteLoader: ";
            valueOf = String.valueOf(e.toString());
            if (valueOf.length() == 0) {
            }
            Log.e(str3, valueOf.length() == 0 ? str4.concat(valueOf) : new String(str4));
            return null;
        } catch (InstantiationException e4) {
            e = e4;
            str3 = "DynamiteModule";
            str4 = "Failed to load DynamiteLoader: ";
            valueOf = String.valueOf(e.toString());
            if (valueOf.length() == 0) {
            }
            Log.e(str3, valueOf.length() == 0 ? str4.concat(valueOf) : new String(str4));
            return null;
        } catch (IllegalAccessException e5) {
            e = e5;
            str3 = "DynamiteModule";
            str4 = "Failed to load DynamiteLoader: ";
            valueOf = String.valueOf(e.toString());
            if (valueOf.length() == 0) {
            }
            Log.e(str3, valueOf.length() == 0 ? str4.concat(valueOf) : new String(str4));
            return null;
        } catch (InvocationTargetException e6) {
            e = e6;
            str3 = "DynamiteModule";
            str4 = "Failed to load DynamiteLoader: ";
            valueOf = String.valueOf(e.toString());
            if (valueOf.length() == 0) {
            }
            Log.e(str3, valueOf.length() == 0 ? str4.concat(valueOf) : new String(str4));
            return null;
        } catch (NoSuchMethodException e7) {
            e = e7;
            str3 = "DynamiteModule";
            str4 = "Failed to load DynamiteLoader: ";
            valueOf = String.valueOf(e.toString());
            if (valueOf.length() == 0) {
            }
            Log.e(str3, valueOf.length() == 0 ? str4.concat(valueOf) : new String(str4));
            return null;
        }
    }

    public static zztl zza(Context context, zzb com_google_android_gms_internal_zztl_zzb, String str) throws zza {
        return zza(context, com_google_android_gms_internal_zztl_zzb, str, Qk);
    }

    public static zztl zza(Context context, zzb com_google_android_gms_internal_zztl_zzb, String str, zza com_google_android_gms_internal_zztl_zzb_zza) throws zza {
        zzb zza = com_google_android_gms_internal_zztl_zzb.zza(context, str, com_google_android_gms_internal_zztl_zzb_zza);
        Log.i("DynamiteModule", new StringBuilder((String.valueOf(str).length() + 68) + String.valueOf(str).length()).append("Considering local module ").append(str).append(":").append(zza.Qt).append(" and remote module ").append(str).append(":").append(zza.Qu).toString());
        if (zza.Qv == 0 || ((zza.Qv == -1 && zza.Qt == 0) || (zza.Qv == 1 && zza.Qu == 0))) {
            throw new zza("No acceptable module found. Local version is " + zza.Qt + " and remote version is " + zza.Qu + ".");
        } else if (zza.Qv == -1) {
            return zzac(context, str);
        } else {
            if (zza.Qv == 1) {
                try {
                    return com_google_android_gms_internal_zztl_zzb_zza.zza(context, str, zza.Qu);
                } catch (Throwable e) {
                    Throwable th = e;
                    String str2 = "DynamiteModule";
                    String str3 = "Failed to load remote module: ";
                    String valueOf = String.valueOf(th.getMessage());
                    Log.w(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
                    if (zza.Qt != 0 && com_google_android_gms_internal_zztl_zzb.zza(context, str, new AnonymousClass8(zza.Qt)).Qv == -1) {
                        return zzac(context, str);
                    }
                    throw new zza("Remote load failed. No local fallback found.", th);
                }
            }
            throw new zza("VersionPolicy returned invalid code:" + zza.Qv);
        }
    }

    private static zztl zza(Context context, String str, int i) throws zza {
        Log.i("DynamiteModule", new StringBuilder(String.valueOf(str).length() + 51).append("Selected remote version of ").append(str).append(", version >= ").append(i).toString());
        zztm zzcs = zzcs(context);
        if (zzcs == null) {
            throw new zza("Failed to create IDynamiteLoader.");
        }
        try {
            zzd zza = zzcs.zza(zze.zzac(context), str, i);
            if (zze.zzae(zza) != null) {
                return new zztl((Context) zze.zzae(zza));
            }
            throw new zza("Failed to load remote module.");
        } catch (Throwable e) {
            throw new zza("Failed to load remote module.", e);
        }
    }

    public static int zzaa(Context context, String str) {
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

    public static int zzab(Context context, String str) {
        return zzb(context, str, false);
    }

    private static zztl zzac(Context context, String str) {
        String str2 = "DynamiteModule";
        String str3 = "Selected local version of ";
        String valueOf = String.valueOf(str);
        Log.i(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
        return new zztl(context.getApplicationContext());
    }

    public static int zzb(Context context, String str, boolean z) {
        zztm zzcs = zzcs(context);
        if (zzcs == null) {
            return 0;
        }
        try {
            return zzcs.zza(zze.zzac(context), str, z);
        } catch (RemoteException e) {
            String str2 = "DynamiteModule";
            String str3 = "Failed to retrieve remote module version: ";
            String valueOf = String.valueOf(e.getMessage());
            Log.w(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
            return 0;
        }
    }

    private static zztl zzb(Context context, String str, int i) throws zza {
        Log.i("DynamiteModule", new StringBuilder(String.valueOf(str).length() + 51).append("Selected remote version of ").append(str).append(", version >= ").append(i).toString());
        synchronized (zztl.class) {
            byte[] bArr = (byte[]) Qi.get(new StringBuilder(String.valueOf(str).length() + 12).append(str).append(":").append(i).toString());
            String str2 = Qj;
        }
        if (bArr == null) {
            throw new zza("Module implementation could not be found.");
        }
        Context zza = zza(context.getApplicationContext(), str, bArr, str2);
        if (zza != null) {
            return new zztl(zza);
        }
        throw new zza("Failed to get module context");
    }

    public static int zzc(Context context, String str, boolean z) throws zza {
        String str2 = z ? "api_force_staging" : "api";
        String valueOf = String.valueOf("content://com.google.android.gms.chimera/");
        Uri parse = Uri.parse(new StringBuilder(((String.valueOf(valueOf).length() + 1) + String.valueOf(str2).length()) + String.valueOf(str).length()).append(valueOf).append(str2).append("/").append(str).toString());
        if (context != null) {
            ContentResolver contentResolver = context.getContentResolver();
            if (contentResolver != null) {
                Cursor query = contentResolver.query(parse, null, null, null, null);
                if (query != null) {
                    try {
                        if (query.moveToFirst()) {
                            int i = query.getInt(0);
                            if (i > 0) {
                                synchronized (zztl.class) {
                                    Qi.put(new StringBuilder(String.valueOf(str).length() + 12).append(str).append(":").append(i).toString(), query.getBlob(1));
                                    Qj = query.getString(2);
                                }
                            }
                            if (query != null) {
                                query.close();
                            }
                            return i;
                        }
                    } catch (Throwable th) {
                        if (query != null) {
                            query.close();
                        }
                    }
                }
                Log.w("DynamiteModule", "Failed to retrieve remote module version.");
                throw new zza("Failed to connect to dynamite module ContentResolver.");
            }
        }
        throw new zza("Failed to get dynamite module ContentResolver.");
    }

    private static zztm zzcs(Context context) {
        synchronized (zztl.class) {
            zztm com_google_android_gms_internal_zztm;
            if (Qh != null) {
                com_google_android_gms_internal_zztm = Qh;
                return com_google_android_gms_internal_zztm;
            } else if (zzc.zzaql().isGooglePlayServicesAvailable(context) != 0) {
                return null;
            } else {
                try {
                    com_google_android_gms_internal_zztm = com.google.android.gms.internal.zztm.zza.zzfe((IBinder) context.createPackageContext("com.google.android.gms", 3).getClassLoader().loadClass("com.google.android.gms.chimera.container.DynamiteLoaderImpl").newInstance());
                    if (com_google_android_gms_internal_zztm != null) {
                        Qh = com_google_android_gms_internal_zztm;
                        return com_google_android_gms_internal_zztm;
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

    public Context zzbdt() {
        return this.Qr;
    }

    public IBinder zzjd(String str) throws zza {
        Throwable e;
        String str2;
        String valueOf;
        try {
            return (IBinder) this.Qr.getClassLoader().loadClass(str).newInstance();
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

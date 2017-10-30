package com.google.android.gms.internal;

import android.os.SystemClock;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.telegram.messenger.exoplayer2.trackselection.AdaptiveTrackSelection;

public final class zzag implements zzb {
    private final Map<String, zzai> zzav;
    private long zzaw;
    private final File zzax;
    private final int zzay;

    public zzag(File file) {
        this(file, 5242880);
    }

    private zzag(File file, int i) {
        this.zzav = new LinkedHashMap(16, AdaptiveTrackSelection.DEFAULT_BANDWIDTH_FRACTION, true);
        this.zzaw = 0;
        this.zzax = file;
        this.zzay = 5242880;
    }

    private final synchronized void remove(String str) {
        boolean delete = zze(str).delete();
        zzai com_google_android_gms_internal_zzai = (zzai) this.zzav.get(str);
        if (com_google_android_gms_internal_zzai != null) {
            this.zzaw -= com_google_android_gms_internal_zzai.size;
            this.zzav.remove(str);
        }
        if (!delete) {
            zzab.zzb("Could not delete cache entry for key=%s, filename=%s", str, zzd(str));
        }
    }

    private static int zza(InputStream inputStream) throws IOException {
        int read = inputStream.read();
        if (read != -1) {
            return read;
        }
        throw new EOFException();
    }

    static void zza(OutputStream outputStream, int i) throws IOException {
        outputStream.write(i & 255);
        outputStream.write((i >> 8) & 255);
        outputStream.write((i >> 16) & 255);
        outputStream.write(i >>> 24);
    }

    static void zza(OutputStream outputStream, long j) throws IOException {
        outputStream.write((byte) ((int) j));
        outputStream.write((byte) ((int) (j >>> 8)));
        outputStream.write((byte) ((int) (j >>> 16)));
        outputStream.write((byte) ((int) (j >>> 24)));
        outputStream.write((byte) ((int) (j >>> 32)));
        outputStream.write((byte) ((int) (j >>> 40)));
        outputStream.write((byte) ((int) (j >>> 48)));
        outputStream.write((byte) ((int) (j >>> 56)));
    }

    static void zza(OutputStream outputStream, String str) throws IOException {
        byte[] bytes = str.getBytes("UTF-8");
        zza(outputStream, (long) bytes.length);
        outputStream.write(bytes, 0, bytes.length);
    }

    private final void zza(String str, zzai com_google_android_gms_internal_zzai) {
        if (this.zzav.containsKey(str)) {
            zzai com_google_android_gms_internal_zzai2 = (zzai) this.zzav.get(str);
            this.zzaw = (com_google_android_gms_internal_zzai.size - com_google_android_gms_internal_zzai2.size) + this.zzaw;
        } else {
            this.zzaw += com_google_android_gms_internal_zzai.size;
        }
        this.zzav.put(str, com_google_android_gms_internal_zzai);
    }

    private static byte[] zza(InputStream inputStream, int i) throws IOException {
        byte[] bArr = new byte[i];
        int i2 = 0;
        while (i2 < i) {
            int read = inputStream.read(bArr, i2, i - i2);
            if (read == -1) {
                break;
            }
            i2 += read;
        }
        if (i2 == i) {
            return bArr;
        }
        throw new IOException("Expected " + i + " bytes, read " + i2 + " bytes");
    }

    static int zzb(InputStream inputStream) throws IOException {
        return (((zza(inputStream) | 0) | (zza(inputStream) << 8)) | (zza(inputStream) << 16)) | (zza(inputStream) << 24);
    }

    static long zzc(InputStream inputStream) throws IOException {
        return (((((((0 | (((long) zza(inputStream)) & 255)) | ((((long) zza(inputStream)) & 255) << 8)) | ((((long) zza(inputStream)) & 255) << 16)) | ((((long) zza(inputStream)) & 255) << 24)) | ((((long) zza(inputStream)) & 255) << 32)) | ((((long) zza(inputStream)) & 255) << 40)) | ((((long) zza(inputStream)) & 255) << 48)) | ((((long) zza(inputStream)) & 255) << 56);
    }

    static String zzd(InputStream inputStream) throws IOException {
        return new String(zza(inputStream, (int) zzc(inputStream)), "UTF-8");
    }

    private static String zzd(String str) {
        int length = str.length() / 2;
        String valueOf = String.valueOf(String.valueOf(str.substring(0, length).hashCode()));
        String valueOf2 = String.valueOf(String.valueOf(str.substring(length).hashCode()));
        return valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf);
    }

    private final File zze(String str) {
        return new File(this.zzax, zzd(str));
    }

    static Map<String, String> zze(InputStream inputStream) throws IOException {
        int zzb = zzb(inputStream);
        Map<String, String> emptyMap = zzb == 0 ? Collections.emptyMap() : new HashMap(zzb);
        for (int i = 0; i < zzb; i++) {
            emptyMap.put(zzd(inputStream).intern(), zzd(inputStream).intern());
        }
        return emptyMap;
    }

    public final synchronized void initialize() {
        BufferedInputStream bufferedInputStream;
        Throwable th;
        if (this.zzax.exists()) {
            File[] listFiles = this.zzax.listFiles();
            if (listFiles != null) {
                for (File file : listFiles) {
                    BufferedInputStream bufferedInputStream2 = null;
                    try {
                        bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
                        try {
                            zzai zzf = zzai.zzf(bufferedInputStream);
                            zzf.size = file.length();
                            zza(zzf.key, zzf);
                            try {
                                bufferedInputStream.close();
                            } catch (IOException e) {
                            }
                        } catch (IOException e2) {
                            if (file != null) {
                                try {
                                    file.delete();
                                } catch (Throwable th2) {
                                    Throwable th3 = th2;
                                    bufferedInputStream2 = bufferedInputStream;
                                    th = th3;
                                }
                            }
                            if (bufferedInputStream != null) {
                                try {
                                    bufferedInputStream.close();
                                } catch (IOException e3) {
                                }
                            }
                        }
                    } catch (IOException e4) {
                        bufferedInputStream = null;
                        if (file != null) {
                            file.delete();
                        }
                        if (bufferedInputStream != null) {
                            bufferedInputStream.close();
                        }
                    } catch (Throwable th4) {
                        th = th4;
                    }
                }
            }
        } else if (!this.zzax.mkdirs()) {
            zzab.zzc("Unable to create cache dir %s", this.zzax.getAbsolutePath());
        }
        return;
        if (bufferedInputStream2 != null) {
            try {
                bufferedInputStream2.close();
            } catch (IOException e5) {
            }
        }
        throw th;
        throw th;
    }

    public final synchronized zzc zza(String str) {
        zzc com_google_android_gms_internal_zzc;
        zzaj com_google_android_gms_internal_zzaj;
        IOException e;
        zzaj com_google_android_gms_internal_zzaj2;
        Throwable th;
        NegativeArraySizeException e2;
        zzai com_google_android_gms_internal_zzai = (zzai) this.zzav.get(str);
        if (com_google_android_gms_internal_zzai == null) {
            com_google_android_gms_internal_zzc = null;
        } else {
            File zze = zze(str);
            try {
                com_google_android_gms_internal_zzaj = new zzaj(new BufferedInputStream(new FileInputStream(zze)));
                try {
                    zzai.zzf(com_google_android_gms_internal_zzaj);
                    byte[] zza = zza((InputStream) com_google_android_gms_internal_zzaj, (int) (zze.length() - ((long) com_google_android_gms_internal_zzaj.zzaz)));
                    zzc com_google_android_gms_internal_zzc2 = new zzc();
                    com_google_android_gms_internal_zzc2.data = zza;
                    com_google_android_gms_internal_zzc2.zza = com_google_android_gms_internal_zzai.zza;
                    com_google_android_gms_internal_zzc2.zzb = com_google_android_gms_internal_zzai.zzb;
                    com_google_android_gms_internal_zzc2.zzc = com_google_android_gms_internal_zzai.zzc;
                    com_google_android_gms_internal_zzc2.zzd = com_google_android_gms_internal_zzai.zzd;
                    com_google_android_gms_internal_zzc2.zze = com_google_android_gms_internal_zzai.zze;
                    com_google_android_gms_internal_zzc2.zzf = com_google_android_gms_internal_zzai.zzf;
                    try {
                        com_google_android_gms_internal_zzaj.close();
                        com_google_android_gms_internal_zzc = com_google_android_gms_internal_zzc2;
                    } catch (IOException e3) {
                        com_google_android_gms_internal_zzc = null;
                    }
                } catch (IOException e4) {
                    e = e4;
                    com_google_android_gms_internal_zzaj2 = com_google_android_gms_internal_zzaj;
                    try {
                        zzab.zzb("%s: %s", zze.getAbsolutePath(), e.toString());
                        remove(str);
                        if (com_google_android_gms_internal_zzaj2 != null) {
                            try {
                                com_google_android_gms_internal_zzaj2.close();
                            } catch (IOException e5) {
                                com_google_android_gms_internal_zzc = null;
                            }
                        }
                        com_google_android_gms_internal_zzc = null;
                        return com_google_android_gms_internal_zzc;
                    } catch (Throwable th2) {
                        th = th2;
                        com_google_android_gms_internal_zzaj = com_google_android_gms_internal_zzaj2;
                        if (com_google_android_gms_internal_zzaj != null) {
                            try {
                                com_google_android_gms_internal_zzaj.close();
                            } catch (IOException e6) {
                                com_google_android_gms_internal_zzc = null;
                            }
                        }
                        throw th;
                    }
                } catch (NegativeArraySizeException e7) {
                    e2 = e7;
                    try {
                        zzab.zzb("%s: %s", zze.getAbsolutePath(), e2.toString());
                        remove(str);
                        if (com_google_android_gms_internal_zzaj != null) {
                            try {
                                com_google_android_gms_internal_zzaj.close();
                            } catch (IOException e8) {
                                com_google_android_gms_internal_zzc = null;
                            }
                        }
                        com_google_android_gms_internal_zzc = null;
                        return com_google_android_gms_internal_zzc;
                    } catch (Throwable th3) {
                        th = th3;
                        if (com_google_android_gms_internal_zzaj != null) {
                            com_google_android_gms_internal_zzaj.close();
                        }
                        throw th;
                    }
                }
            } catch (IOException e9) {
                e = e9;
                com_google_android_gms_internal_zzaj2 = null;
                zzab.zzb("%s: %s", zze.getAbsolutePath(), e.toString());
                remove(str);
                if (com_google_android_gms_internal_zzaj2 != null) {
                    com_google_android_gms_internal_zzaj2.close();
                }
                com_google_android_gms_internal_zzc = null;
                return com_google_android_gms_internal_zzc;
            } catch (NegativeArraySizeException e10) {
                e2 = e10;
                com_google_android_gms_internal_zzaj = null;
                zzab.zzb("%s: %s", zze.getAbsolutePath(), e2.toString());
                remove(str);
                if (com_google_android_gms_internal_zzaj != null) {
                    com_google_android_gms_internal_zzaj.close();
                }
                com_google_android_gms_internal_zzc = null;
                return com_google_android_gms_internal_zzc;
            } catch (Throwable th4) {
                th = th4;
                com_google_android_gms_internal_zzaj = null;
                if (com_google_android_gms_internal_zzaj != null) {
                    com_google_android_gms_internal_zzaj.close();
                }
                throw th;
            }
        }
        return com_google_android_gms_internal_zzc;
    }

    public final synchronized void zza(String str, zzc com_google_android_gms_internal_zzc) {
        int i = 0;
        synchronized (this) {
            int length = com_google_android_gms_internal_zzc.data.length;
            if (this.zzaw + ((long) length) >= ((long) this.zzay)) {
                int i2;
                if (zzab.DEBUG) {
                    zzab.zza("Pruning old cache entries.", new Object[0]);
                }
                long j = this.zzaw;
                long elapsedRealtime = SystemClock.elapsedRealtime();
                Iterator it = this.zzav.entrySet().iterator();
                while (it.hasNext()) {
                    zzai com_google_android_gms_internal_zzai = (zzai) ((Entry) it.next()).getValue();
                    if (zze(com_google_android_gms_internal_zzai.key).delete()) {
                        this.zzaw -= com_google_android_gms_internal_zzai.size;
                    } else {
                        zzab.zzb("Could not delete cache entry for key=%s, filename=%s", com_google_android_gms_internal_zzai.key, zzd(com_google_android_gms_internal_zzai.key));
                    }
                    it.remove();
                    i2 = i + 1;
                    if (((float) (this.zzaw + ((long) length))) < ((float) this.zzay) * 0.9f) {
                        break;
                    }
                    i = i2;
                }
                i2 = i;
                if (zzab.DEBUG) {
                    zzab.zza("pruned %d files, %d bytes, %d ms", Integer.valueOf(i2), Long.valueOf(this.zzaw - j), Long.valueOf(SystemClock.elapsedRealtime() - elapsedRealtime));
                }
            }
            File zze = zze(str);
            try {
                OutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(zze));
                zzai com_google_android_gms_internal_zzai2 = new zzai(str, com_google_android_gms_internal_zzc);
                if (com_google_android_gms_internal_zzai2.zza(bufferedOutputStream)) {
                    bufferedOutputStream.write(com_google_android_gms_internal_zzc.data);
                    bufferedOutputStream.close();
                    zza(str, com_google_android_gms_internal_zzai2);
                } else {
                    bufferedOutputStream.close();
                    zzab.zzb("Failed to write header for %s", zze.getAbsolutePath());
                    throw new IOException();
                }
            } catch (IOException e) {
                if (!zze.delete()) {
                    zzab.zzb("Could not clean up file %s", zze.getAbsolutePath());
                }
            }
        }
    }
}

package com.google.android.gms.internal;

import android.os.SystemClock;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
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

public class zzw implements zzb {
    private final Map<String, zza> zzaw;
    private long zzax;
    private final File zzay;
    private final int zzaz;

    static class zza {
        public String zza;
        public long zzaA;
        public String zzaB;
        public long zzb;
        public long zzc;
        public long zzd;
        public long zze;
        public Map<String, String> zzf;

        private zza() {
        }

        public zza(String str, com.google.android.gms.internal.zzb.zza com_google_android_gms_internal_zzb_zza) {
            this.zzaB = str;
            this.zzaA = (long) com_google_android_gms_internal_zzb_zza.data.length;
            this.zza = com_google_android_gms_internal_zzb_zza.zza;
            this.zzb = com_google_android_gms_internal_zzb_zza.zzb;
            this.zzc = com_google_android_gms_internal_zzb_zza.zzc;
            this.zzd = com_google_android_gms_internal_zzb_zza.zzd;
            this.zze = com_google_android_gms_internal_zzb_zza.zze;
            this.zzf = com_google_android_gms_internal_zzb_zza.zzf;
        }

        public static zza zzf(InputStream inputStream) throws IOException {
            zza com_google_android_gms_internal_zzw_zza = new zza();
            if (zzw.zzb(inputStream) != 538247942) {
                throw new IOException();
            }
            com_google_android_gms_internal_zzw_zza.zzaB = zzw.zzd(inputStream);
            com_google_android_gms_internal_zzw_zza.zza = zzw.zzd(inputStream);
            if (com_google_android_gms_internal_zzw_zza.zza.equals("")) {
                com_google_android_gms_internal_zzw_zza.zza = null;
            }
            com_google_android_gms_internal_zzw_zza.zzb = zzw.zzc(inputStream);
            com_google_android_gms_internal_zzw_zza.zzc = zzw.zzc(inputStream);
            com_google_android_gms_internal_zzw_zza.zzd = zzw.zzc(inputStream);
            com_google_android_gms_internal_zzw_zza.zze = zzw.zzc(inputStream);
            com_google_android_gms_internal_zzw_zza.zzf = zzw.zze(inputStream);
            return com_google_android_gms_internal_zzw_zza;
        }

        public boolean zza(OutputStream outputStream) {
            try {
                zzw.zza(outputStream, 538247942);
                zzw.zza(outputStream, this.zzaB);
                zzw.zza(outputStream, this.zza == null ? "" : this.zza);
                zzw.zza(outputStream, this.zzb);
                zzw.zza(outputStream, this.zzc);
                zzw.zza(outputStream, this.zzd);
                zzw.zza(outputStream, this.zze);
                zzw.zza(this.zzf, outputStream);
                outputStream.flush();
                return true;
            } catch (IOException e) {
                zzt.zzb("%s", e.toString());
                return false;
            }
        }

        public com.google.android.gms.internal.zzb.zza zzb(byte[] bArr) {
            com.google.android.gms.internal.zzb.zza com_google_android_gms_internal_zzb_zza = new com.google.android.gms.internal.zzb.zza();
            com_google_android_gms_internal_zzb_zza.data = bArr;
            com_google_android_gms_internal_zzb_zza.zza = this.zza;
            com_google_android_gms_internal_zzb_zza.zzb = this.zzb;
            com_google_android_gms_internal_zzb_zza.zzc = this.zzc;
            com_google_android_gms_internal_zzb_zza.zzd = this.zzd;
            com_google_android_gms_internal_zzb_zza.zze = this.zze;
            com_google_android_gms_internal_zzb_zza.zzf = this.zzf;
            return com_google_android_gms_internal_zzb_zza;
        }
    }

    private static class zzb extends FilterInputStream {
        private int zzaC;

        private zzb(InputStream inputStream) {
            super(inputStream);
            this.zzaC = 0;
        }

        public int read() throws IOException {
            int read = super.read();
            if (read != -1) {
                this.zzaC++;
            }
            return read;
        }

        public int read(byte[] bArr, int i, int i2) throws IOException {
            int read = super.read(bArr, i, i2);
            if (read != -1) {
                this.zzaC += read;
            }
            return read;
        }
    }

    public zzw(File file) {
        this(file, 5242880);
    }

    public zzw(File file, int i) {
        this.zzaw = new LinkedHashMap(16, AdaptiveTrackSelection.DEFAULT_BANDWIDTH_FRACTION, true);
        this.zzax = 0;
        this.zzay = file;
        this.zzaz = i;
    }

    private void removeEntry(String str) {
        zza com_google_android_gms_internal_zzw_zza = (zza) this.zzaw.get(str);
        if (com_google_android_gms_internal_zzw_zza != null) {
            this.zzax -= com_google_android_gms_internal_zzw_zza.zzaA;
            this.zzaw.remove(str);
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
        outputStream.write((i >> 0) & 255);
        outputStream.write((i >> 8) & 255);
        outputStream.write((i >> 16) & 255);
        outputStream.write((i >> 24) & 255);
    }

    static void zza(OutputStream outputStream, long j) throws IOException {
        outputStream.write((byte) ((int) (j >>> null)));
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

    private void zza(String str, zza com_google_android_gms_internal_zzw_zza) {
        if (this.zzaw.containsKey(str)) {
            zza com_google_android_gms_internal_zzw_zza2 = (zza) this.zzaw.get(str);
            this.zzax = (com_google_android_gms_internal_zzw_zza.zzaA - com_google_android_gms_internal_zzw_zza2.zzaA) + this.zzax;
        } else {
            this.zzax += com_google_android_gms_internal_zzw_zza.zzaA;
        }
        this.zzaw.put(str, com_google_android_gms_internal_zzw_zza);
    }

    static void zza(Map<String, String> map, OutputStream outputStream) throws IOException {
        if (map != null) {
            zza(outputStream, map.size());
            for (Entry entry : map.entrySet()) {
                zza(outputStream, (String) entry.getKey());
                zza(outputStream, (String) entry.getValue());
            }
            return;
        }
        zza(outputStream, 0);
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
        return ((((zza(inputStream) << 0) | 0) | (zza(inputStream) << 8)) | (zza(inputStream) << 16)) | (zza(inputStream) << 24);
    }

    static long zzc(InputStream inputStream) throws IOException {
        return (((((((0 | ((((long) zza(inputStream)) & 255) << null)) | ((((long) zza(inputStream)) & 255) << 8)) | ((((long) zza(inputStream)) & 255) << 16)) | ((((long) zza(inputStream)) & 255) << 24)) | ((((long) zza(inputStream)) & 255) << 32)) | ((((long) zza(inputStream)) & 255) << 40)) | ((((long) zza(inputStream)) & 255) << 48)) | ((((long) zza(inputStream)) & 255) << 56);
    }

    private void zzc(int i) {
        if (this.zzax + ((long) i) >= ((long) this.zzaz)) {
            int i2;
            if (zzt.DEBUG) {
                zzt.zza("Pruning old cache entries.", new Object[0]);
            }
            long j = this.zzax;
            long elapsedRealtime = SystemClock.elapsedRealtime();
            Iterator it = this.zzaw.entrySet().iterator();
            int i3 = 0;
            while (it.hasNext()) {
                zza com_google_android_gms_internal_zzw_zza = (zza) ((Entry) it.next()).getValue();
                if (zzf(com_google_android_gms_internal_zzw_zza.zzaB).delete()) {
                    this.zzax -= com_google_android_gms_internal_zzw_zza.zzaA;
                } else {
                    zzt.zzb("Could not delete cache entry for key=%s, filename=%s", com_google_android_gms_internal_zzw_zza.zzaB, zze(com_google_android_gms_internal_zzw_zza.zzaB));
                }
                it.remove();
                i2 = i3 + 1;
                if (((float) (this.zzax + ((long) i))) < ((float) this.zzaz) * 0.9f) {
                    break;
                }
                i3 = i2;
            }
            i2 = i3;
            if (zzt.DEBUG) {
                zzt.zza("pruned %d files, %d bytes, %d ms", Integer.valueOf(i2), Long.valueOf(this.zzax - j), Long.valueOf(SystemClock.elapsedRealtime() - elapsedRealtime));
            }
        }
    }

    static String zzd(InputStream inputStream) throws IOException {
        return new String(zza(inputStream, (int) zzc(inputStream)), "UTF-8");
    }

    private String zze(String str) {
        int length = str.length() / 2;
        return String.valueOf(str.substring(0, length).hashCode()) + String.valueOf(str.substring(length).hashCode());
    }

    static Map<String, String> zze(InputStream inputStream) throws IOException {
        int zzb = zzb(inputStream);
        Map<String, String> emptyMap = zzb == 0 ? Collections.emptyMap() : new HashMap(zzb);
        for (int i = 0; i < zzb; i++) {
            emptyMap.put(zzd(inputStream).intern(), zzd(inputStream).intern());
        }
        return emptyMap;
    }

    public synchronized void initialize() {
        Throwable th;
        if (this.zzay.exists()) {
            File[] listFiles = this.zzay.listFiles();
            if (listFiles != null) {
                for (File file : listFiles) {
                    BufferedInputStream bufferedInputStream = null;
                    BufferedInputStream bufferedInputStream2;
                    try {
                        bufferedInputStream2 = new BufferedInputStream(new FileInputStream(file));
                        try {
                            zza zzf = zza.zzf(bufferedInputStream2);
                            zzf.zzaA = file.length();
                            zza(zzf.zzaB, zzf);
                            try {
                                bufferedInputStream2.close();
                            } catch (IOException e) {
                            }
                        } catch (IOException e2) {
                            if (file != null) {
                                try {
                                    file.delete();
                                } catch (Throwable th2) {
                                    Throwable th3 = th2;
                                    bufferedInputStream = bufferedInputStream2;
                                    th = th3;
                                }
                            }
                            if (bufferedInputStream2 != null) {
                                try {
                                    bufferedInputStream2.close();
                                } catch (IOException e3) {
                                }
                            }
                        }
                    } catch (IOException e4) {
                        bufferedInputStream2 = null;
                        if (file != null) {
                            file.delete();
                        }
                        if (bufferedInputStream2 != null) {
                            bufferedInputStream2.close();
                        }
                    } catch (Throwable th4) {
                        th = th4;
                    }
                }
            }
        } else if (!this.zzay.mkdirs()) {
            zzt.zzc("Unable to create cache dir %s", this.zzay.getAbsolutePath());
        }
        return;
        if (bufferedInputStream != null) {
            try {
                bufferedInputStream.close();
            } catch (IOException e5) {
            }
        }
        throw th;
        throw th;
    }

    public synchronized void remove(String str) {
        boolean delete = zzf(str).delete();
        removeEntry(str);
        if (!delete) {
            zzt.zzb("Could not delete cache entry for key=%s, filename=%s", str, zze(str));
        }
    }

    public synchronized com.google.android.gms.internal.zzb.zza zza(String str) {
        com.google.android.gms.internal.zzb.zza com_google_android_gms_internal_zzb_zza;
        zzb com_google_android_gms_internal_zzw_zzb;
        IOException e;
        Throwable th;
        zza com_google_android_gms_internal_zzw_zza = (zza) this.zzaw.get(str);
        if (com_google_android_gms_internal_zzw_zza == null) {
            com_google_android_gms_internal_zzb_zza = null;
        } else {
            File zzf = zzf(str);
            try {
                com_google_android_gms_internal_zzw_zzb = new zzb(new BufferedInputStream(new FileInputStream(zzf)));
                try {
                    zza.zzf(com_google_android_gms_internal_zzw_zzb);
                    com_google_android_gms_internal_zzb_zza = com_google_android_gms_internal_zzw_zza.zzb(zza((InputStream) com_google_android_gms_internal_zzw_zzb, (int) (zzf.length() - ((long) com_google_android_gms_internal_zzw_zzb.zzaC))));
                    try {
                        com_google_android_gms_internal_zzw_zzb.close();
                    } catch (IOException e2) {
                        com_google_android_gms_internal_zzb_zza = null;
                    }
                } catch (IOException e3) {
                    e = e3;
                    try {
                        zzt.zzb("%s: %s", zzf.getAbsolutePath(), e.toString());
                        remove(str);
                        if (com_google_android_gms_internal_zzw_zzb != null) {
                            try {
                                com_google_android_gms_internal_zzw_zzb.close();
                            } catch (IOException e4) {
                                com_google_android_gms_internal_zzb_zza = null;
                            }
                        }
                        com_google_android_gms_internal_zzb_zza = null;
                        return com_google_android_gms_internal_zzb_zza;
                    } catch (Throwable th2) {
                        th = th2;
                        if (com_google_android_gms_internal_zzw_zzb != null) {
                            try {
                                com_google_android_gms_internal_zzw_zzb.close();
                            } catch (IOException e5) {
                                com_google_android_gms_internal_zzb_zza = null;
                            }
                        }
                        throw th;
                    }
                }
            } catch (IOException e6) {
                e = e6;
                com_google_android_gms_internal_zzw_zzb = null;
                zzt.zzb("%s: %s", zzf.getAbsolutePath(), e.toString());
                remove(str);
                if (com_google_android_gms_internal_zzw_zzb != null) {
                    com_google_android_gms_internal_zzw_zzb.close();
                }
                com_google_android_gms_internal_zzb_zza = null;
                return com_google_android_gms_internal_zzb_zza;
            } catch (Throwable th3) {
                th = th3;
                com_google_android_gms_internal_zzw_zzb = null;
                if (com_google_android_gms_internal_zzw_zzb != null) {
                    com_google_android_gms_internal_zzw_zzb.close();
                }
                throw th;
            }
        }
        return com_google_android_gms_internal_zzb_zza;
    }

    public synchronized void zza(String str, com.google.android.gms.internal.zzb.zza com_google_android_gms_internal_zzb_zza) {
        zzc(com_google_android_gms_internal_zzb_zza.data.length);
        File zzf = zzf(str);
        try {
            OutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(zzf));
            zza com_google_android_gms_internal_zzw_zza = new zza(str, com_google_android_gms_internal_zzb_zza);
            if (com_google_android_gms_internal_zzw_zza.zza(bufferedOutputStream)) {
                bufferedOutputStream.write(com_google_android_gms_internal_zzb_zza.data);
                bufferedOutputStream.close();
                zza(str, com_google_android_gms_internal_zzw_zza);
            } else {
                bufferedOutputStream.close();
                zzt.zzb("Failed to write header for %s", zzf.getAbsolutePath());
                throw new IOException();
            }
        } catch (IOException e) {
            if (!zzf.delete()) {
                zzt.zzb("Could not clean up file %s", zzf.getAbsolutePath());
            }
        }
    }

    public File zzf(String str) {
        return new File(this.zzay, zze(str));
    }
}

package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class zzq extends zzaa {

    @WorkerThread
    interface zza {
        void zza(String str, int i, Throwable th, byte[] bArr, Map<String, List<String>> map);
    }

    @WorkerThread
    private static class zzb implements Runnable {
        private final zza apA;
        private final byte[] apB;
        private final Map<String, List<String>> apC;
        private final String ed;
        private final int zzbqm;
        private final Throwable zzctw;

        private zzb(String str, zza com_google_android_gms_measurement_internal_zzq_zza, int i, Throwable th, byte[] bArr, Map<String, List<String>> map) {
            zzac.zzy(com_google_android_gms_measurement_internal_zzq_zza);
            this.apA = com_google_android_gms_measurement_internal_zzq_zza;
            this.zzbqm = i;
            this.zzctw = th;
            this.apB = bArr;
            this.ed = str;
            this.apC = map;
        }

        public void run() {
            this.apA.zza(this.ed, this.zzbqm, this.zzctw, this.apB, this.apC);
        }
    }

    @WorkerThread
    private class zzc implements Runnable {
        private final byte[] apD;
        private final zza apE;
        private final Map<String, String> apF;
        final /* synthetic */ zzq apG;
        private final String ed;
        private final URL zzbnb;

        public zzc(zzq com_google_android_gms_measurement_internal_zzq, String str, URL url, byte[] bArr, Map<String, String> map, zza com_google_android_gms_measurement_internal_zzq_zza) {
            this.apG = com_google_android_gms_measurement_internal_zzq;
            zzac.zzhz(str);
            zzac.zzy(url);
            zzac.zzy(com_google_android_gms_measurement_internal_zzq_zza);
            this.zzbnb = url;
            this.apD = bArr;
            this.apE = com_google_android_gms_measurement_internal_zzq_zza;
            this.ed = str;
            this.apF = map;
        }

        public void run() {
            HttpURLConnection zzc;
            OutputStream outputStream;
            Throwable e;
            Map map;
            int i;
            HttpURLConnection httpURLConnection;
            Throwable th;
            Map map2;
            this.apG.zzbuv();
            int i2 = 0;
            try {
                this.apG.zzfb(this.ed);
                zzc = this.apG.zzc(this.zzbnb);
                try {
                    if (this.apF != null) {
                        for (Entry entry : this.apF.entrySet()) {
                            zzc.addRequestProperty((String) entry.getKey(), (String) entry.getValue());
                        }
                    }
                    if (this.apD != null) {
                        byte[] zzj = this.apG.zzbvc().zzj(this.apD);
                        this.apG.zzbvg().zzbwj().zzj("Uploading data. size", Integer.valueOf(zzj.length));
                        zzc.setDoOutput(true);
                        zzc.addRequestProperty("Content-Encoding", "gzip");
                        zzc.setFixedLengthStreamingMode(zzj.length);
                        zzc.connect();
                        outputStream = zzc.getOutputStream();
                        try {
                            outputStream.write(zzj);
                            outputStream.close();
                        } catch (IOException e2) {
                            e = e2;
                            map = null;
                            i = 0;
                            httpURLConnection = zzc;
                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                } catch (IOException e3) {
                                    this.apG.zzbvg().zzbwc().zzj("Error closing HTTP compressed POST connection output stream", e3);
                                }
                            }
                            if (httpURLConnection != null) {
                                httpURLConnection.disconnect();
                            }
                            this.apG.zzsm();
                            this.apG.zzbvf().zzm(new zzb(this.ed, this.apE, i, e, null, map));
                        } catch (Throwable th2) {
                            th = th2;
                            map2 = null;
                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                } catch (IOException e32) {
                                    this.apG.zzbvg().zzbwc().zzj("Error closing HTTP compressed POST connection output stream", e32);
                                }
                            }
                            if (zzc != null) {
                                zzc.disconnect();
                            }
                            this.apG.zzsm();
                            this.apG.zzbvf().zzm(new zzb(this.ed, this.apE, i2, null, null, map2));
                            throw th;
                        }
                    }
                    i2 = zzc.getResponseCode();
                    map2 = zzc.getHeaderFields();
                } catch (IOException e4) {
                    e = e4;
                    map = null;
                    i = i2;
                    outputStream = null;
                    httpURLConnection = zzc;
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    this.apG.zzsm();
                    this.apG.zzbvf().zzm(new zzb(this.ed, this.apE, i, e, null, map));
                } catch (Throwable th3) {
                    th = th3;
                    map2 = null;
                    outputStream = null;
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    if (zzc != null) {
                        zzc.disconnect();
                    }
                    this.apG.zzsm();
                    this.apG.zzbvf().zzm(new zzb(this.ed, this.apE, i2, null, null, map2));
                    throw th;
                }
                try {
                    byte[] zza = this.apG.zzc(zzc);
                    if (zzc != null) {
                        zzc.disconnect();
                    }
                    this.apG.zzsm();
                    this.apG.zzbvf().zzm(new zzb(this.ed, this.apE, i2, null, zza, map2));
                } catch (IOException e5) {
                    e = e5;
                    map = map2;
                    i = i2;
                    outputStream = null;
                    httpURLConnection = zzc;
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    this.apG.zzsm();
                    this.apG.zzbvf().zzm(new zzb(this.ed, this.apE, i, e, null, map));
                } catch (Throwable th32) {
                    th = th32;
                    outputStream = null;
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    if (zzc != null) {
                        zzc.disconnect();
                    }
                    this.apG.zzsm();
                    this.apG.zzbvf().zzm(new zzb(this.ed, this.apE, i2, null, null, map2));
                    throw th;
                }
            } catch (IOException e6) {
                e = e6;
                map = null;
                i = 0;
                outputStream = null;
                httpURLConnection = null;
                if (outputStream != null) {
                    outputStream.close();
                }
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                this.apG.zzsm();
                this.apG.zzbvf().zzm(new zzb(this.ed, this.apE, i, e, null, map));
            } catch (Throwable th322) {
                th = th322;
                map2 = null;
                zzc = null;
                outputStream = null;
                if (outputStream != null) {
                    outputStream.close();
                }
                if (zzc != null) {
                    zzc.disconnect();
                }
                this.apG.zzsm();
                this.apG.zzbvf().zzm(new zzb(this.ed, this.apE, i2, null, null, map2));
                throw th;
            }
        }
    }

    public zzq(zzx com_google_android_gms_measurement_internal_zzx) {
        super(com_google_android_gms_measurement_internal_zzx);
    }

    @WorkerThread
    private byte[] zzc(HttpURLConnection httpURLConnection) throws IOException {
        InputStream inputStream = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            inputStream = httpURLConnection.getInputStream();
            byte[] bArr = new byte[1024];
            while (true) {
                int read = inputStream.read(bArr);
                if (read <= 0) {
                    break;
                }
                byteArrayOutputStream.write(bArr, 0, read);
            }
            byte[] toByteArray = byteArrayOutputStream.toByteArray();
            return toByteArray;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    @WorkerThread
    public void zza(String str, URL url, Map<String, String> map, zza com_google_android_gms_measurement_internal_zzq_zza) {
        zzyl();
        zzaax();
        zzac.zzy(url);
        zzac.zzy(com_google_android_gms_measurement_internal_zzq_zza);
        zzbvf().zzn(new zzc(this, str, url, null, map, com_google_android_gms_measurement_internal_zzq_zza));
    }

    @WorkerThread
    public void zza(String str, URL url, byte[] bArr, Map<String, String> map, zza com_google_android_gms_measurement_internal_zzq_zza) {
        zzyl();
        zzaax();
        zzac.zzy(url);
        zzac.zzy(bArr);
        zzac.zzy(com_google_android_gms_measurement_internal_zzq_zza);
        zzbvf().zzn(new zzc(this, str, url, bArr, map, com_google_android_gms_measurement_internal_zzq_zza));
    }

    public /* bridge */ /* synthetic */ void zzaam() {
        super.zzaam();
    }

    public /* bridge */ /* synthetic */ zze zzaan() {
        return super.zzaan();
    }

    public boolean zzafa() {
        NetworkInfo activeNetworkInfo;
        zzaax();
        try {
            activeNetworkInfo = ((ConnectivityManager) getContext().getSystemService("connectivity")).getActiveNetworkInfo();
        } catch (SecurityException e) {
            activeNetworkInfo = null;
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

    @WorkerThread
    protected HttpURLConnection zzc(URL url) throws IOException {
        URLConnection openConnection = url.openConnection();
        if (openConnection instanceof HttpURLConnection) {
            HttpURLConnection httpURLConnection = (HttpURLConnection) openConnection;
            httpURLConnection.setDefaultUseCaches(false);
            httpURLConnection.setConnectTimeout((int) zzbvi().zzbua());
            httpURLConnection.setReadTimeout((int) zzbvi().zzbub());
            httpURLConnection.setInstanceFollowRedirects(false);
            httpURLConnection.setDoInput(true);
            return httpURLConnection;
        }
        throw new IOException("Failed to obtain HTTP connection");
    }

    protected void zzfb(String str) {
    }

    protected void zzsm() {
    }

    public /* bridge */ /* synthetic */ void zzyl() {
        super.zzyl();
    }

    protected void zzym() {
    }
}

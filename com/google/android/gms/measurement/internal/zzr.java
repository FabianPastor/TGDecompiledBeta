package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzaa;
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

public class zzr extends zzaa {

    @WorkerThread
    interface zza {
        void zza(String str, int i, Throwable th, byte[] bArr, Map<String, List<String>> map);
    }

    @WorkerThread
    private static class zzb implements Runnable {
        private final zza asO;
        private final byte[] asP;
        private final Map<String, List<String>> asQ;
        private final int zzbtt;
        private final String zzcjc;
        private final Throwable zzcye;

        private zzb(String str, zza com_google_android_gms_measurement_internal_zzr_zza, int i, Throwable th, byte[] bArr, Map<String, List<String>> map) {
            zzaa.zzy(com_google_android_gms_measurement_internal_zzr_zza);
            this.asO = com_google_android_gms_measurement_internal_zzr_zza;
            this.zzbtt = i;
            this.zzcye = th;
            this.asP = bArr;
            this.zzcjc = str;
            this.asQ = map;
        }

        public void run() {
            this.asO.zza(this.zzcjc, this.zzbtt, this.zzcye, this.asP, this.asQ);
        }
    }

    @WorkerThread
    private class zzc implements Runnable {
        private final byte[] asR;
        private final zza asS;
        private final Map<String, String> asT;
        final /* synthetic */ zzr asU;
        private final URL zzbqj;
        private final String zzcjc;

        public zzc(zzr com_google_android_gms_measurement_internal_zzr, String str, URL url, byte[] bArr, Map<String, String> map, zza com_google_android_gms_measurement_internal_zzr_zza) {
            this.asU = com_google_android_gms_measurement_internal_zzr;
            zzaa.zzib(str);
            zzaa.zzy(url);
            zzaa.zzy(com_google_android_gms_measurement_internal_zzr_zza);
            this.zzbqj = url;
            this.asR = bArr;
            this.asS = com_google_android_gms_measurement_internal_zzr_zza;
            this.zzcjc = str;
            this.asT = map;
        }

        public void run() {
            HttpURLConnection zzc;
            Throwable e;
            Map map;
            int i;
            HttpURLConnection httpURLConnection;
            Throwable th;
            this.asU.zzbvo();
            int i2 = 0;
            OutputStream outputStream;
            Map map2;
            try {
                this.asU.zzfe(this.zzcjc);
                zzc = this.asU.zzc(this.zzbqj);
                try {
                    if (this.asT != null) {
                        for (Entry entry : this.asT.entrySet()) {
                            zzc.addRequestProperty((String) entry.getKey(), (String) entry.getValue());
                        }
                    }
                    if (this.asR != null) {
                        byte[] zzk = this.asU.zzbvx().zzk(this.asR);
                        this.asU.zzbwb().zzbxe().zzj("Uploading data. size", Integer.valueOf(zzk.length));
                        zzc.setDoOutput(true);
                        zzc.addRequestProperty("Content-Encoding", "gzip");
                        zzc.setFixedLengthStreamingMode(zzk.length);
                        zzc.connect();
                        outputStream = zzc.getOutputStream();
                        try {
                            outputStream.write(zzk);
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
                                    this.asU.zzbwb().zzbwy().zzj("Error closing HTTP compressed POST connection output stream", e3);
                                }
                            }
                            if (httpURLConnection != null) {
                                httpURLConnection.disconnect();
                            }
                            this.asU.zztt();
                            this.asU.zzbwa().zzm(new zzb(this.zzcjc, this.asS, i, e, null, map));
                        } catch (Throwable th2) {
                            th = th2;
                            map2 = null;
                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                } catch (IOException e32) {
                                    this.asU.zzbwb().zzbwy().zzj("Error closing HTTP compressed POST connection output stream", e32);
                                }
                            }
                            if (zzc != null) {
                                zzc.disconnect();
                            }
                            this.asU.zztt();
                            this.asU.zzbwa().zzm(new zzb(this.zzcjc, this.asS, i2, null, null, map2));
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
                    this.asU.zztt();
                    this.asU.zzbwa().zzm(new zzb(this.zzcjc, this.asS, i, e, null, map));
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
                    this.asU.zztt();
                    this.asU.zzbwa().zzm(new zzb(this.zzcjc, this.asS, i2, null, null, map2));
                    throw th;
                }
                try {
                    byte[] zza = this.asU.zzc(zzc);
                    if (zzc != null) {
                        zzc.disconnect();
                    }
                    this.asU.zztt();
                    this.asU.zzbwa().zzm(new zzb(this.zzcjc, this.asS, i2, null, zza, map2));
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
                    this.asU.zztt();
                    this.asU.zzbwa().zzm(new zzb(this.zzcjc, this.asS, i, e, null, map));
                } catch (Throwable th32) {
                    th = th32;
                    outputStream = null;
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    if (zzc != null) {
                        zzc.disconnect();
                    }
                    this.asU.zztt();
                    this.asU.zzbwa().zzm(new zzb(this.zzcjc, this.asS, i2, null, null, map2));
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
                this.asU.zztt();
                this.asU.zzbwa().zzm(new zzb(this.zzcjc, this.asS, i, e, null, map));
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
                this.asU.zztt();
                this.asU.zzbwa().zzm(new zzb(this.zzcjc, this.asS, i2, null, null, map2));
                throw th;
            }
        }
    }

    public zzr(zzx com_google_android_gms_measurement_internal_zzx) {
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
    public void zza(String str, URL url, Map<String, String> map, zza com_google_android_gms_measurement_internal_zzr_zza) {
        zzzx();
        zzacj();
        zzaa.zzy(url);
        zzaa.zzy(com_google_android_gms_measurement_internal_zzr_zza);
        zzbwa().zzn(new zzc(this, str, url, null, map, com_google_android_gms_measurement_internal_zzr_zza));
    }

    @WorkerThread
    public void zza(String str, URL url, byte[] bArr, Map<String, String> map, zza com_google_android_gms_measurement_internal_zzr_zza) {
        zzzx();
        zzacj();
        zzaa.zzy(url);
        zzaa.zzy(bArr);
        zzaa.zzy(com_google_android_gms_measurement_internal_zzr_zza);
        zzbwa().zzn(new zzc(this, str, url, bArr, map, com_google_android_gms_measurement_internal_zzr_zza));
    }

    public /* bridge */ /* synthetic */ void zzaby() {
        super.zzaby();
    }

    public /* bridge */ /* synthetic */ zze zzabz() {
        return super.zzabz();
    }

    public boolean zzagk() {
        NetworkInfo activeNetworkInfo;
        zzacj();
        try {
            activeNetworkInfo = ((ConnectivityManager) getContext().getSystemService("connectivity")).getActiveNetworkInfo();
        } catch (SecurityException e) {
            activeNetworkInfo = null;
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

    @WorkerThread
    protected HttpURLConnection zzc(URL url) throws IOException {
        URLConnection openConnection = url.openConnection();
        if (openConnection instanceof HttpURLConnection) {
            HttpURLConnection httpURLConnection = (HttpURLConnection) openConnection;
            httpURLConnection.setDefaultUseCaches(false);
            zzbwd().zzbuq();
            httpURLConnection.setConnectTimeout(60000);
            zzbwd().zzbur();
            httpURLConnection.setReadTimeout(61000);
            httpURLConnection.setInstanceFollowRedirects(false);
            httpURLConnection.setDoInput(true);
            return httpURLConnection;
        }
        throw new IOException("Failed to obtain HTTP connection");
    }

    protected void zzfe(String str) {
    }

    protected void zztt() {
    }

    public /* bridge */ /* synthetic */ void zzzx() {
        super.zzzx();
    }

    protected void zzzy() {
    }
}

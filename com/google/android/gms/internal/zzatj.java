package com.google.android.gms.internal;

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

public class zzatj extends zzats {

    @WorkerThread
    interface zza {
        void zza(String str, int i, Throwable th, byte[] bArr, Map<String, List<String>> map);
    }

    @WorkerThread
    private static class zzb implements Runnable {
        private final int zzJh;
        private final String zzQL;
        private final Throwable zzYf;
        private final zza zzbrV;
        private final byte[] zzbrW;
        private final Map<String, List<String>> zzbrX;

        private zzb(String str, zza com_google_android_gms_internal_zzatj_zza, int i, Throwable th, byte[] bArr, Map<String, List<String>> map) {
            zzac.zzw(com_google_android_gms_internal_zzatj_zza);
            this.zzbrV = com_google_android_gms_internal_zzatj_zza;
            this.zzJh = i;
            this.zzYf = th;
            this.zzbrW = bArr;
            this.zzQL = str;
            this.zzbrX = map;
        }

        public void run() {
            this.zzbrV.zza(this.zzQL, this.zzJh, this.zzYf, this.zzbrW, this.zzbrX);
        }
    }

    @WorkerThread
    private class zzc implements Runnable {
        private final URL zzHD;
        private final String zzQL;
        private final byte[] zzbrY;
        private final zza zzbrZ;
        private final Map<String, String> zzbsa;
        final /* synthetic */ zzatj zzbsb;

        public zzc(zzatj com_google_android_gms_internal_zzatj, String str, URL url, byte[] bArr, Map<String, String> map, zza com_google_android_gms_internal_zzatj_zza) {
            this.zzbsb = com_google_android_gms_internal_zzatj;
            zzac.zzdv(str);
            zzac.zzw(url);
            zzac.zzw(com_google_android_gms_internal_zzatj_zza);
            this.zzHD = url;
            this.zzbrY = bArr;
            this.zzbrZ = com_google_android_gms_internal_zzatj_zza;
            this.zzQL = str;
            this.zzbsa = map;
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
            this.zzbsb.zzJf();
            int i2 = 0;
            try {
                zzc = this.zzbsb.zzc(this.zzHD);
                try {
                    if (this.zzbsa != null) {
                        for (Entry entry : this.zzbsa.entrySet()) {
                            zzc.addRequestProperty((String) entry.getKey(), (String) entry.getValue());
                        }
                    }
                    if (this.zzbrY != null) {
                        byte[] zzk = this.zzbsb.zzJp().zzk(this.zzbrY);
                        this.zzbsb.zzJt().zzLg().zzj("Uploading data. size", Integer.valueOf(zzk.length));
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
                                    this.zzbsb.zzJt().zzLa().zze("Error closing HTTP compressed POST connection output stream. appId", zzati.zzfI(this.zzQL), e3);
                                }
                            }
                            if (httpURLConnection != null) {
                                httpURLConnection.disconnect();
                            }
                            this.zzbsb.zzJs().zzm(new zzb(this.zzQL, this.zzbrZ, i, e, null, map));
                        } catch (Throwable th2) {
                            th = th2;
                            map2 = null;
                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                } catch (IOException e32) {
                                    this.zzbsb.zzJt().zzLa().zze("Error closing HTTP compressed POST connection output stream. appId", zzati.zzfI(this.zzQL), e32);
                                }
                            }
                            if (zzc != null) {
                                zzc.disconnect();
                            }
                            this.zzbsb.zzJs().zzm(new zzb(this.zzQL, this.zzbrZ, i2, null, null, map2));
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
                    this.zzbsb.zzJs().zzm(new zzb(this.zzQL, this.zzbrZ, i, e, null, map));
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
                    this.zzbsb.zzJs().zzm(new zzb(this.zzQL, this.zzbrZ, i2, null, null, map2));
                    throw th;
                }
                try {
                    byte[] zza = this.zzbsb.zzc(zzc);
                    if (zzc != null) {
                        zzc.disconnect();
                    }
                    this.zzbsb.zzJs().zzm(new zzb(this.zzQL, this.zzbrZ, i2, null, zza, map2));
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
                    this.zzbsb.zzJs().zzm(new zzb(this.zzQL, this.zzbrZ, i, e, null, map));
                } catch (Throwable th32) {
                    th = th32;
                    outputStream = null;
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    if (zzc != null) {
                        zzc.disconnect();
                    }
                    this.zzbsb.zzJs().zzm(new zzb(this.zzQL, this.zzbrZ, i2, null, null, map2));
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
                this.zzbsb.zzJs().zzm(new zzb(this.zzQL, this.zzbrZ, i, e, null, map));
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
                this.zzbsb.zzJs().zzm(new zzb(this.zzQL, this.zzbrZ, i2, null, null, map2));
                throw th;
            }
        }
    }

    public zzatj(zzatp com_google_android_gms_internal_zzatp) {
        super(com_google_android_gms_internal_zzatp);
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

    @WorkerThread
    public void zza(String str, URL url, Map<String, String> map, zza com_google_android_gms_internal_zzatj_zza) {
        zzmq();
        zznA();
        zzac.zzw(url);
        zzac.zzw(com_google_android_gms_internal_zzatj_zza);
        zzJs().zzn(new zzc(this, str, url, null, map, com_google_android_gms_internal_zzatj_zza));
    }

    @WorkerThread
    public void zza(String str, URL url, byte[] bArr, Map<String, String> map, zza com_google_android_gms_internal_zzatj_zza) {
        zzmq();
        zznA();
        zzac.zzw(url);
        zzac.zzw(bArr);
        zzac.zzw(com_google_android_gms_internal_zzatj_zza);
        zzJs().zzn(new zzc(this, str, url, bArr, map, com_google_android_gms_internal_zzatj_zza));
    }

    @WorkerThread
    protected HttpURLConnection zzc(URL url) throws IOException {
        URLConnection openConnection = url.openConnection();
        if (openConnection instanceof HttpURLConnection) {
            HttpURLConnection httpURLConnection = (HttpURLConnection) openConnection;
            httpURLConnection.setDefaultUseCaches(false);
            zzJv().zzKh();
            httpURLConnection.setConnectTimeout(60000);
            zzJv().zzKi();
            httpURLConnection.setReadTimeout(61000);
            httpURLConnection.setInstanceFollowRedirects(false);
            httpURLConnection.setDoInput(true);
            return httpURLConnection;
        }
        throw new IOException("Failed to obtain HTTP connection");
    }

    public /* bridge */ /* synthetic */ void zzmq() {
        super.zzmq();
    }

    protected void zzmr() {
    }

    public /* bridge */ /* synthetic */ zze zznq() {
        return super.zznq();
    }

    public boolean zzpA() {
        NetworkInfo activeNetworkInfo;
        zznA();
        try {
            activeNetworkInfo = ((ConnectivityManager) getContext().getSystemService("connectivity")).getActiveNetworkInfo();
        } catch (SecurityException e) {
            activeNetworkInfo = null;
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

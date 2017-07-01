package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzbo;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Map.Entry;

@WorkerThread
final class zzcfs implements Runnable {
    private final String mPackageName;
    private final URL zzJw;
    private final byte[] zzaKA;
    private final zzcfq zzbrd;
    private final Map<String, String> zzbre;
    private /* synthetic */ zzcfo zzbrf;

    public zzcfs(zzcfo com_google_android_gms_internal_zzcfo, String str, URL url, byte[] bArr, Map<String, String> map, zzcfq com_google_android_gms_internal_zzcfq) {
        this.zzbrf = com_google_android_gms_internal_zzcfo;
        zzbo.zzcF(str);
        zzbo.zzu(url);
        zzbo.zzu(com_google_android_gms_internal_zzcfq);
        this.zzJw = url;
        this.zzaKA = bArr;
        this.zzbrd = com_google_android_gms_internal_zzcfq;
        this.mPackageName = str;
        this.zzbre = map;
    }

    public final void run() {
        HttpURLConnection httpURLConnection;
        OutputStream outputStream;
        Throwable e;
        Map map;
        int i;
        HttpURLConnection httpURLConnection2;
        OutputStream outputStream2;
        Throwable th;
        Map map2;
        int i2 = 0;
        this.zzbrf.zzwq();
        try {
            URLConnection openConnection = this.zzJw.openConnection();
            if (openConnection instanceof HttpURLConnection) {
                httpURLConnection = (HttpURLConnection) openConnection;
                httpURLConnection.setDefaultUseCaches(false);
                zzcel.zzxz();
                httpURLConnection.setConnectTimeout(60000);
                zzcel.zzxA();
                httpURLConnection.setReadTimeout(61000);
                httpURLConnection.setInstanceFollowRedirects(false);
                httpURLConnection.setDoInput(true);
                try {
                    if (this.zzbre != null) {
                        for (Entry entry : this.zzbre.entrySet()) {
                            httpURLConnection.addRequestProperty((String) entry.getKey(), (String) entry.getValue());
                        }
                    }
                    if (this.zzaKA != null) {
                        byte[] zzl = this.zzbrf.zzwB().zzl(this.zzaKA);
                        this.zzbrf.zzwF().zzyD().zzj("Uploading data. size", Integer.valueOf(zzl.length));
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.addRequestProperty("Content-Encoding", "gzip");
                        httpURLConnection.setFixedLengthStreamingMode(zzl.length);
                        httpURLConnection.connect();
                        outputStream = httpURLConnection.getOutputStream();
                        try {
                            outputStream.write(zzl);
                            outputStream.close();
                        } catch (IOException e2) {
                            e = e2;
                            map = null;
                            i = 0;
                            OutputStream outputStream3 = outputStream;
                            httpURLConnection2 = httpURLConnection;
                            outputStream2 = outputStream3;
                            if (outputStream2 != null) {
                                try {
                                    outputStream2.close();
                                } catch (IOException e3) {
                                    this.zzbrf.zzwF().zzyx().zze("Error closing HTTP compressed POST connection output stream. appId", zzcfk.zzdZ(this.mPackageName), e3);
                                }
                            }
                            if (httpURLConnection2 != null) {
                                httpURLConnection2.disconnect();
                            }
                            this.zzbrf.zzwE().zzj(new zzcfr(this.mPackageName, this.zzbrd, i, e, null, map));
                        } catch (Throwable th2) {
                            th = th2;
                            map2 = null;
                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                } catch (IOException e4) {
                                    this.zzbrf.zzwF().zzyx().zze("Error closing HTTP compressed POST connection output stream. appId", zzcfk.zzdZ(this.mPackageName), e4);
                                }
                            }
                            if (httpURLConnection != null) {
                                httpURLConnection.disconnect();
                            }
                            this.zzbrf.zzwE().zzj(new zzcfr(this.mPackageName, this.zzbrd, i2, null, null, map2));
                            throw th;
                        }
                    }
                    i2 = httpURLConnection.getResponseCode();
                    map2 = httpURLConnection.getHeaderFields();
                } catch (IOException e5) {
                    e = e5;
                    map = null;
                    i = i2;
                    httpURLConnection2 = httpURLConnection;
                    outputStream2 = null;
                    if (outputStream2 != null) {
                        outputStream2.close();
                    }
                    if (httpURLConnection2 != null) {
                        httpURLConnection2.disconnect();
                    }
                    this.zzbrf.zzwE().zzj(new zzcfr(this.mPackageName, this.zzbrd, i, e, null, map));
                } catch (Throwable th3) {
                    th = th3;
                    map2 = null;
                    outputStream = null;
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    this.zzbrf.zzwE().zzj(new zzcfr(this.mPackageName, this.zzbrd, i2, null, null, map2));
                    throw th;
                }
                try {
                    byte[] zza = zzcfo.zzc(httpURLConnection);
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    this.zzbrf.zzwE().zzj(new zzcfr(this.mPackageName, this.zzbrd, i2, null, zza, map2));
                    return;
                } catch (IOException e6) {
                    e = e6;
                    map = map2;
                    i = i2;
                    httpURLConnection2 = httpURLConnection;
                    outputStream2 = null;
                    if (outputStream2 != null) {
                        outputStream2.close();
                    }
                    if (httpURLConnection2 != null) {
                        httpURLConnection2.disconnect();
                    }
                    this.zzbrf.zzwE().zzj(new zzcfr(this.mPackageName, this.zzbrd, i, e, null, map));
                } catch (Throwable th32) {
                    th = th32;
                    outputStream = null;
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    this.zzbrf.zzwE().zzj(new zzcfr(this.mPackageName, this.zzbrd, i2, null, null, map2));
                    throw th;
                }
            }
            throw new IOException("Failed to obtain HTTP connection");
        } catch (IOException e7) {
            e = e7;
            map = null;
            i = 0;
            outputStream2 = null;
            httpURLConnection2 = null;
            if (outputStream2 != null) {
                outputStream2.close();
            }
            if (httpURLConnection2 != null) {
                httpURLConnection2.disconnect();
            }
            this.zzbrf.zzwE().zzj(new zzcfr(this.mPackageName, this.zzbrd, i, e, null, map));
        } catch (Throwable th4) {
            th = th4;
            map2 = null;
            outputStream = null;
            httpURLConnection = null;
            if (outputStream != null) {
                outputStream.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            this.zzbrf.zzwE().zzj(new zzcfr(this.mPackageName, this.zzbrd, i2, null, null, map2));
            throw th;
        }
    }
}

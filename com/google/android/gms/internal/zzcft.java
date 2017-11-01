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
final class zzcft implements Runnable {
    private final String mPackageName;
    private final URL zzJu;
    private final byte[] zzaKA;
    private final zzcfr zzbrd;
    private final Map<String, String> zzbre;
    private /* synthetic */ zzcfp zzbrf;

    public zzcft(zzcfp com_google_android_gms_internal_zzcfp, String str, URL url, byte[] bArr, Map<String, String> map, zzcfr com_google_android_gms_internal_zzcfr) {
        this.zzbrf = com_google_android_gms_internal_zzcfp;
        zzbo.zzcF(str);
        zzbo.zzu(url);
        zzbo.zzu(com_google_android_gms_internal_zzcfr);
        this.zzJu = url;
        this.zzaKA = bArr;
        this.zzbrd = com_google_android_gms_internal_zzcfr;
        this.mPackageName = str;
        this.zzbre = map;
    }

    public final void run() {
        Throwable e;
        Map map;
        int i;
        HttpURLConnection httpURLConnection;
        OutputStream outputStream;
        Throwable th;
        Map map2;
        int i2 = 0;
        this.zzbrf.zzwq();
        HttpURLConnection httpURLConnection2;
        OutputStream outputStream2;
        try {
            URLConnection openConnection = this.zzJu.openConnection();
            if (openConnection instanceof HttpURLConnection) {
                httpURLConnection2 = (HttpURLConnection) openConnection;
                httpURLConnection2.setDefaultUseCaches(false);
                zzcem.zzxz();
                httpURLConnection2.setConnectTimeout(60000);
                zzcem.zzxA();
                httpURLConnection2.setReadTimeout(61000);
                httpURLConnection2.setInstanceFollowRedirects(false);
                httpURLConnection2.setDoInput(true);
                try {
                    if (this.zzbre != null) {
                        for (Entry entry : this.zzbre.entrySet()) {
                            httpURLConnection2.addRequestProperty((String) entry.getKey(), (String) entry.getValue());
                        }
                    }
                    if (this.zzaKA != null) {
                        byte[] zzl = this.zzbrf.zzwB().zzl(this.zzaKA);
                        this.zzbrf.zzwF().zzyD().zzj("Uploading data. size", Integer.valueOf(zzl.length));
                        httpURLConnection2.setDoOutput(true);
                        httpURLConnection2.addRequestProperty("Content-Encoding", "gzip");
                        httpURLConnection2.setFixedLengthStreamingMode(zzl.length);
                        httpURLConnection2.connect();
                        outputStream2 = httpURLConnection2.getOutputStream();
                        try {
                            outputStream2.write(zzl);
                            outputStream2.close();
                        } catch (IOException e2) {
                            e = e2;
                            map = null;
                            i = 0;
                            OutputStream outputStream3 = outputStream2;
                            httpURLConnection = httpURLConnection2;
                            outputStream = outputStream3;
                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                } catch (IOException e3) {
                                    this.zzbrf.zzwF().zzyx().zze("Error closing HTTP compressed POST connection output stream. appId", zzcfl.zzdZ(this.mPackageName), e3);
                                }
                            }
                            if (httpURLConnection != null) {
                                httpURLConnection.disconnect();
                            }
                            this.zzbrf.zzwE().zzj(new zzcfs(this.mPackageName, this.zzbrd, i, e, null, map));
                        } catch (Throwable th2) {
                            th = th2;
                            map2 = null;
                            if (outputStream2 != null) {
                                try {
                                    outputStream2.close();
                                } catch (IOException e4) {
                                    this.zzbrf.zzwF().zzyx().zze("Error closing HTTP compressed POST connection output stream. appId", zzcfl.zzdZ(this.mPackageName), e4);
                                }
                            }
                            if (httpURLConnection2 != null) {
                                httpURLConnection2.disconnect();
                            }
                            this.zzbrf.zzwE().zzj(new zzcfs(this.mPackageName, this.zzbrd, i2, null, null, map2));
                            throw th;
                        }
                    }
                    i2 = httpURLConnection2.getResponseCode();
                    map2 = httpURLConnection2.getHeaderFields();
                } catch (IOException e5) {
                    e = e5;
                    map = null;
                    i = i2;
                    httpURLConnection = httpURLConnection2;
                    outputStream = null;
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    this.zzbrf.zzwE().zzj(new zzcfs(this.mPackageName, this.zzbrd, i, e, null, map));
                } catch (Throwable th3) {
                    th = th3;
                    map2 = null;
                    outputStream2 = null;
                    if (outputStream2 != null) {
                        outputStream2.close();
                    }
                    if (httpURLConnection2 != null) {
                        httpURLConnection2.disconnect();
                    }
                    this.zzbrf.zzwE().zzj(new zzcfs(this.mPackageName, this.zzbrd, i2, null, null, map2));
                    throw th;
                }
                try {
                    byte[] zza = zzcfp.zzc(httpURLConnection2);
                    if (httpURLConnection2 != null) {
                        httpURLConnection2.disconnect();
                    }
                    this.zzbrf.zzwE().zzj(new zzcfs(this.mPackageName, this.zzbrd, i2, null, zza, map2));
                    return;
                } catch (IOException e6) {
                    e = e6;
                    map = map2;
                    i = i2;
                    httpURLConnection = httpURLConnection2;
                    outputStream = null;
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    this.zzbrf.zzwE().zzj(new zzcfs(this.mPackageName, this.zzbrd, i, e, null, map));
                } catch (Throwable th32) {
                    th = th32;
                    outputStream2 = null;
                    if (outputStream2 != null) {
                        outputStream2.close();
                    }
                    if (httpURLConnection2 != null) {
                        httpURLConnection2.disconnect();
                    }
                    this.zzbrf.zzwE().zzj(new zzcfs(this.mPackageName, this.zzbrd, i2, null, null, map2));
                    throw th;
                }
            }
            throw new IOException("Failed to obtain HTTP connection");
        } catch (IOException e7) {
            e = e7;
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
            this.zzbrf.zzwE().zzj(new zzcfs(this.mPackageName, this.zzbrd, i, e, null, map));
        } catch (Throwable th4) {
            th = th4;
            map2 = null;
            outputStream2 = null;
            httpURLConnection2 = null;
            if (outputStream2 != null) {
                outputStream2.close();
            }
            if (httpURLConnection2 != null) {
                httpURLConnection2.disconnect();
            }
            this.zzbrf.zzwE().zzj(new zzcfs(this.mPackageName, this.zzbrd, i2, null, null, map2));
            throw th;
        }
    }
}

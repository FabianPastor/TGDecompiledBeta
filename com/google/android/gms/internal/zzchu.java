package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbq;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Map.Entry;

final class zzchu implements Runnable {
    private final String mPackageName;
    private final URL zzbxv;
    private final byte[] zzgfx;
    private final zzchs zzjck;
    private final Map<String, String> zzjcl;
    private /* synthetic */ zzchq zzjcm;

    public zzchu(zzchq com_google_android_gms_internal_zzchq, String str, URL url, byte[] bArr, Map<String, String> map, zzchs com_google_android_gms_internal_zzchs) {
        this.zzjcm = com_google_android_gms_internal_zzchq;
        zzbq.zzgm(str);
        zzbq.checkNotNull(url);
        zzbq.checkNotNull(com_google_android_gms_internal_zzchs);
        this.zzbxv = url;
        this.zzgfx = bArr;
        this.zzjck = com_google_android_gms_internal_zzchs;
        this.mPackageName = str;
        this.zzjcl = map;
    }

    public final void run() {
        HttpURLConnection httpURLConnection;
        Throwable e;
        Map map;
        int i;
        HttpURLConnection httpURLConnection2;
        OutputStream outputStream;
        Throwable th;
        Map map2;
        int i2 = 0;
        this.zzjcm.zzawj();
        OutputStream outputStream2;
        try {
            URLConnection openConnection = this.zzbxv.openConnection();
            if (openConnection instanceof HttpURLConnection) {
                httpURLConnection = (HttpURLConnection) openConnection;
                httpURLConnection.setDefaultUseCaches(false);
                httpURLConnection.setConnectTimeout(60000);
                httpURLConnection.setReadTimeout(61000);
                httpURLConnection.setInstanceFollowRedirects(false);
                httpURLConnection.setDoInput(true);
                try {
                    if (this.zzjcl != null) {
                        for (Entry entry : this.zzjcl.entrySet()) {
                            httpURLConnection.addRequestProperty((String) entry.getKey(), (String) entry.getValue());
                        }
                    }
                    if (this.zzgfx != null) {
                        byte[] zzq = this.zzjcm.zzawu().zzq(this.zzgfx);
                        this.zzjcm.zzawy().zzazj().zzj("Uploading data. size", Integer.valueOf(zzq.length));
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.addRequestProperty("Content-Encoding", "gzip");
                        httpURLConnection.setFixedLengthStreamingMode(zzq.length);
                        httpURLConnection.connect();
                        outputStream2 = httpURLConnection.getOutputStream();
                        try {
                            outputStream2.write(zzq);
                            outputStream2.close();
                        } catch (IOException e2) {
                            e = e2;
                            map = null;
                            i = 0;
                            OutputStream outputStream3 = outputStream2;
                            httpURLConnection2 = httpURLConnection;
                            outputStream = outputStream3;
                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                } catch (IOException e3) {
                                    this.zzjcm.zzawy().zzazd().zze("Error closing HTTP compressed POST connection output stream. appId", zzchm.zzjk(this.mPackageName), e3);
                                }
                            }
                            if (httpURLConnection2 != null) {
                                httpURLConnection2.disconnect();
                            }
                            this.zzjcm.zzawx().zzg(new zzcht(this.mPackageName, this.zzjck, i, e, null, map));
                        } catch (Throwable th2) {
                            th = th2;
                            map2 = null;
                            if (outputStream2 != null) {
                                try {
                                    outputStream2.close();
                                } catch (IOException e4) {
                                    this.zzjcm.zzawy().zzazd().zze("Error closing HTTP compressed POST connection output stream. appId", zzchm.zzjk(this.mPackageName), e4);
                                }
                            }
                            if (httpURLConnection != null) {
                                httpURLConnection.disconnect();
                            }
                            this.zzjcm.zzawx().zzg(new zzcht(this.mPackageName, this.zzjck, i2, null, null, map2));
                            throw th;
                        }
                    }
                    i2 = httpURLConnection.getResponseCode();
                    map2 = httpURLConnection.getHeaderFields();
                    try {
                        byte[] zza = zzchq.zzc(httpURLConnection);
                        if (httpURLConnection != null) {
                            httpURLConnection.disconnect();
                        }
                        this.zzjcm.zzawx().zzg(new zzcht(this.mPackageName, this.zzjck, i2, null, zza, map2));
                        return;
                    } catch (IOException e5) {
                        e = e5;
                        map = map2;
                        i = i2;
                        httpURLConnection2 = httpURLConnection;
                        outputStream = null;
                        if (outputStream != null) {
                            outputStream.close();
                        }
                        if (httpURLConnection2 != null) {
                            httpURLConnection2.disconnect();
                        }
                        this.zzjcm.zzawx().zzg(new zzcht(this.mPackageName, this.zzjck, i, e, null, map));
                    } catch (Throwable th3) {
                        th = th3;
                        outputStream2 = null;
                        if (outputStream2 != null) {
                            outputStream2.close();
                        }
                        if (httpURLConnection != null) {
                            httpURLConnection.disconnect();
                        }
                        this.zzjcm.zzawx().zzg(new zzcht(this.mPackageName, this.zzjck, i2, null, null, map2));
                        throw th;
                    }
                } catch (IOException e6) {
                    e = e6;
                    map = null;
                    i = i2;
                    httpURLConnection2 = httpURLConnection;
                    outputStream = null;
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    if (httpURLConnection2 != null) {
                        httpURLConnection2.disconnect();
                    }
                    this.zzjcm.zzawx().zzg(new zzcht(this.mPackageName, this.zzjck, i, e, null, map));
                } catch (Throwable th32) {
                    th = th32;
                    map2 = null;
                    outputStream2 = null;
                    if (outputStream2 != null) {
                        outputStream2.close();
                    }
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    this.zzjcm.zzawx().zzg(new zzcht(this.mPackageName, this.zzjck, i2, null, null, map2));
                    throw th;
                }
            }
            throw new IOException("Failed to obtain HTTP connection");
        } catch (IOException e7) {
            e = e7;
            map = null;
            i = 0;
            outputStream = null;
            httpURLConnection2 = null;
            if (outputStream != null) {
                outputStream.close();
            }
            if (httpURLConnection2 != null) {
                httpURLConnection2.disconnect();
            }
            this.zzjcm.zzawx().zzg(new zzcht(this.mPackageName, this.zzjck, i, e, null, map));
        } catch (Throwable th4) {
            th = th4;
            map2 = null;
            outputStream2 = null;
            httpURLConnection = null;
            if (outputStream2 != null) {
                outputStream2.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            this.zzjcm.zzawx().zzg(new zzcht(this.mPackageName, this.zzjck, i2, null, null, map2));
            throw th;
        }
    }
}

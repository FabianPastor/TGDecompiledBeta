package com.google.android.gms.internal;

import android.os.SystemClock;
import com.coremedia.iso.boxes.AuthorBox;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.cookie.DateUtils;

public final class zzad implements zzk {
    private static boolean DEBUG = zzab.DEBUG;
    private static int zzam = 3000;
    private static int zzan = 4096;
    private zzan zzao;
    private zzae zzap;

    public zzad(zzan com_google_android_gms_internal_zzan) {
        this(com_google_android_gms_internal_zzan, new zzae(zzan));
    }

    private zzad(zzan com_google_android_gms_internal_zzan, zzae com_google_android_gms_internal_zzae) {
        this.zzao = com_google_android_gms_internal_zzan;
        this.zzap = com_google_android_gms_internal_zzae;
    }

    private static Map<String, String> zza(Header[] headerArr) {
        Map<String, String> treeMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < headerArr.length; i++) {
            treeMap.put(headerArr[i].getName(), headerArr[i].getValue());
        }
        return treeMap;
    }

    private static void zza(String str, zzp<?> com_google_android_gms_internal_zzp_, zzaa com_google_android_gms_internal_zzaa) throws zzaa {
        zzx zzj = com_google_android_gms_internal_zzp_.zzj();
        int zzi = com_google_android_gms_internal_zzp_.zzi();
        try {
            zzj.zza(com_google_android_gms_internal_zzaa);
            com_google_android_gms_internal_zzp_.zzb(String.format("%s-retry [timeout=%s]", new Object[]{str, Integer.valueOf(zzi)}));
        } catch (zzaa e) {
            com_google_android_gms_internal_zzp_.zzb(String.format("%s-timeout-giveup [timeout=%s]", new Object[]{str, Integer.valueOf(zzi)}));
            throw e;
        }
    }

    private final byte[] zza(HttpEntity httpEntity) throws IOException, zzy {
        zzaq com_google_android_gms_internal_zzaq = new zzaq(this.zzap, (int) httpEntity.getContentLength());
        byte[] bArr = null;
        try {
            InputStream content = httpEntity.getContent();
            if (content == null) {
                throw new zzy();
            }
            bArr = this.zzap.zzb(1024);
            while (true) {
                int read = content.read(bArr);
                if (read == -1) {
                    break;
                }
                com_google_android_gms_internal_zzaq.write(bArr, 0, read);
            }
            byte[] toByteArray = com_google_android_gms_internal_zzaq.toByteArray();
            return toByteArray;
        } finally {
            try {
                httpEntity.consumeContent();
            } catch (IOException e) {
                zzab.zza("Error occured when calling consumingContent", new Object[0]);
            }
            this.zzap.zza(bArr);
            com_google_android_gms_internal_zzaq.close();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final zzn zza(zzp<?> com_google_android_gms_internal_zzp_) throws zzaa {
        String valueOf;
        Throwable e;
        long elapsedRealtime = SystemClock.elapsedRealtime();
        while (true) {
            int statusCode;
            HttpResponse httpResponse = null;
            Map emptyMap = Collections.emptyMap();
            HttpResponse zza;
            byte[] zza2;
            try {
                Map hashMap = new HashMap();
                zzc zze = com_google_android_gms_internal_zzp_.zze();
                if (zze != null) {
                    if (zze.zza != null) {
                        hashMap.put("If-None-Match", zze.zza);
                    }
                    if (zze.zzc > 0) {
                        hashMap.put("If-Modified-Since", DateUtils.formatDate(new Date(zze.zzc)));
                    }
                }
                zza = this.zzao.zza(com_google_android_gms_internal_zzp_, hashMap);
                StatusLine statusLine = zza.getStatusLine();
                statusCode = statusLine.getStatusCode();
                emptyMap = zza(zza.getAllHeaders());
                if (statusCode != 304) {
                    zza2 = zza.getEntity() != null ? zza(zza.getEntity()) : new byte[0];
                    long elapsedRealtime2 = SystemClock.elapsedRealtime() - elapsedRealtime;
                    if (!DEBUG && elapsedRealtime2 <= ((long) zzam)) {
                        break;
                    }
                    String str = "HTTP response for request=<%s> [lifetime=%d], [size=%s], [rc=%d], [retryCount=%s]";
                    Object[] objArr = new Object[5];
                    objArr[0] = com_google_android_gms_internal_zzp_;
                    objArr[1] = Long.valueOf(elapsedRealtime2);
                    if (zza2 == null) {
                        break;
                    }
                    break;
                    objArr[2] = valueOf;
                    objArr[3] = Integer.valueOf(statusLine.getStatusCode());
                    objArr[4] = Integer.valueOf(com_google_android_gms_internal_zzp_.zzj().zzb());
                    zzab.zzb(str, objArr);
                } else {
                    break;
                }
            } catch (SocketTimeoutException e2) {
                zza("socket", com_google_android_gms_internal_zzp_, new zzz());
            } catch (ConnectTimeoutException e3) {
                zza("connection", com_google_android_gms_internal_zzp_, new zzz());
            } catch (Throwable e4) {
                Throwable th = e4;
                String str2 = "Bad URL ";
                valueOf = String.valueOf(com_google_android_gms_internal_zzp_.getUrl());
                throw new RuntimeException(valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2), th);
            } catch (IOException e5) {
                e4 = e5;
                httpResponse = zza;
                if (httpResponse != null) {
                    statusCode = httpResponse.getStatusLine().getStatusCode();
                    zzab.zzc("Unexpected response code %d for %s", Integer.valueOf(statusCode), com_google_android_gms_internal_zzp_.getUrl());
                    if (zza2 != null) {
                        zzn com_google_android_gms_internal_zzn = new zzn(statusCode, zza2, emptyMap, false, SystemClock.elapsedRealtime() - elapsedRealtime);
                        if (statusCode != 401) {
                        }
                        zza(AuthorBox.TYPE, com_google_android_gms_internal_zzp_, new zza(com_google_android_gms_internal_zzn));
                    } else {
                        zza("network", com_google_android_gms_internal_zzp_, new zzm());
                    }
                } else {
                    throw new zzo(e4);
                }
            }
        }
        if (statusCode < 400 && statusCode <= 499) {
            throw new zzf(com_google_android_gms_internal_zzn);
        } else if (statusCode >= 500 || statusCode > 599) {
            throw new zzy(com_google_android_gms_internal_zzn);
        } else {
            throw new zzy(com_google_android_gms_internal_zzn);
        }
    }
}

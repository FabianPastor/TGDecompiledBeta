package com.google.android.gms.internal;

import android.os.SystemClock;
import com.coremedia.iso.boxes.AuthorBox;
import com.google.android.gms.internal.zzb.zza;
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
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public class zzt implements zzf {
    protected static final boolean DEBUG = zzs.DEBUG;
    private static int zzbn = 3000;
    private static int zzbo = 4096;
    protected final zzy zzbp;
    protected final zzu zzbq;

    public zzt(zzy com_google_android_gms_internal_zzy) {
        this(com_google_android_gms_internal_zzy, new zzu(zzbo));
    }

    public zzt(zzy com_google_android_gms_internal_zzy, zzu com_google_android_gms_internal_zzu) {
        this.zzbp = com_google_android_gms_internal_zzy;
        this.zzbq = com_google_android_gms_internal_zzu;
    }

    protected static Map<String, String> zza(Header[] headerArr) {
        Map<String, String> treeMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < headerArr.length; i++) {
            treeMap.put(headerArr[i].getName(), headerArr[i].getValue());
        }
        return treeMap;
    }

    private void zza(long j, zzk<?> com_google_android_gms_internal_zzk_, byte[] bArr, StatusLine statusLine) {
        if (DEBUG || j > ((long) zzbn)) {
            String str = "HTTP response for request=<%s> [lifetime=%d], [size=%s], [rc=%d], [retryCount=%s]";
            Object[] objArr = new Object[5];
            objArr[0] = com_google_android_gms_internal_zzk_;
            objArr[1] = Long.valueOf(j);
            objArr[2] = bArr != null ? Integer.valueOf(bArr.length) : "null";
            objArr[3] = Integer.valueOf(statusLine.getStatusCode());
            objArr[4] = Integer.valueOf(com_google_android_gms_internal_zzk_.zzt().zzd());
            zzs.zzb(str, objArr);
        }
    }

    private static void zza(String str, zzk<?> com_google_android_gms_internal_zzk_, zzr com_google_android_gms_internal_zzr) throws zzr {
        zzo zzt = com_google_android_gms_internal_zzk_.zzt();
        int zzs = com_google_android_gms_internal_zzk_.zzs();
        try {
            zzt.zza(com_google_android_gms_internal_zzr);
            com_google_android_gms_internal_zzk_.zzc(String.format("%s-retry [timeout=%s]", new Object[]{str, Integer.valueOf(zzs)}));
        } catch (zzr e) {
            com_google_android_gms_internal_zzk_.zzc(String.format("%s-timeout-giveup [timeout=%s]", new Object[]{str, Integer.valueOf(zzs)}));
            throw e;
        }
    }

    private void zza(Map<String, String> map, zza com_google_android_gms_internal_zzb_zza) {
        if (com_google_android_gms_internal_zzb_zza != null) {
            if (com_google_android_gms_internal_zzb_zza.zza != null) {
                map.put("If-None-Match", com_google_android_gms_internal_zzb_zza.zza);
            }
            if (com_google_android_gms_internal_zzb_zza.zzc > 0) {
                map.put("If-Modified-Since", DateUtils.formatDate(new Date(com_google_android_gms_internal_zzb_zza.zzc)));
            }
        }
    }

    private byte[] zza(HttpEntity httpEntity) throws IOException, zzp {
        zzaa com_google_android_gms_internal_zzaa = new zzaa(this.zzbq, (int) httpEntity.getContentLength());
        byte[] bArr = null;
        try {
            InputStream content = httpEntity.getContent();
            if (content == null) {
                throw new zzp();
            }
            bArr = this.zzbq.zzb(1024);
            while (true) {
                int read = content.read(bArr);
                if (read == -1) {
                    break;
                }
                com_google_android_gms_internal_zzaa.write(bArr, 0, read);
            }
            byte[] toByteArray = com_google_android_gms_internal_zzaa.toByteArray();
            return toByteArray;
        } finally {
            try {
                httpEntity.consumeContent();
            } catch (IOException e) {
                zzs.zza("Error occured when calling consumingContent", new Object[0]);
            }
            this.zzbq.zza(bArr);
            com_google_android_gms_internal_zzaa.close();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public zzi zza(zzk<?> com_google_android_gms_internal_zzk_) throws zzr {
        int statusCode;
        byte[] zza;
        Throwable e;
        long elapsedRealtime = SystemClock.elapsedRealtime();
        while (true) {
            HttpResponse httpResponse = null;
            Map emptyMap = Collections.emptyMap();
            HttpResponse zza2;
            try {
                Map hashMap = new HashMap();
                zza(hashMap, com_google_android_gms_internal_zzk_.zzh());
                zza2 = this.zzbp.zza(com_google_android_gms_internal_zzk_, hashMap);
                StatusLine statusLine = zza2.getStatusLine();
                statusCode = statusLine.getStatusCode();
                emptyMap = zza(zza2.getAllHeaders());
                if (statusCode == 304) {
                    break;
                }
                zza = zza2.getEntity() != null ? zza(zza2.getEntity()) : new byte[0];
                zza(SystemClock.elapsedRealtime() - elapsedRealtime, com_google_android_gms_internal_zzk_, zza, statusLine);
                if (statusCode >= Callback.DEFAULT_DRAG_ANIMATION_DURATION && statusCode <= 299) {
                    return new zzi(statusCode, zza, emptyMap, false, SystemClock.elapsedRealtime() - elapsedRealtime);
                }
            } catch (SocketTimeoutException e2) {
                zza("socket", com_google_android_gms_internal_zzk_, new zzq());
            } catch (ConnectTimeoutException e3) {
                zza("connection", com_google_android_gms_internal_zzk_, new zzq());
            } catch (Throwable e4) {
                Throwable th = e4;
                String str = "Bad URL ";
                String valueOf = String.valueOf(com_google_android_gms_internal_zzk_.getUrl());
                throw new RuntimeException(valueOf.length() != 0 ? str.concat(valueOf) : new String(str), th);
            } catch (IOException e5) {
                e4 = e5;
                byte[] bArr = zza;
                httpResponse = zza2;
                if (httpResponse != null) {
                    statusCode = httpResponse.getStatusLine().getStatusCode();
                    zzs.zzc("Unexpected response code %d for %s", Integer.valueOf(statusCode), com_google_android_gms_internal_zzk_.getUrl());
                    if (bArr != null) {
                        zzi com_google_android_gms_internal_zzi = new zzi(statusCode, bArr, emptyMap, false, SystemClock.elapsedRealtime() - elapsedRealtime);
                        if (statusCode != 401) {
                        }
                        zza(AuthorBox.TYPE, com_google_android_gms_internal_zzk_, new zza(com_google_android_gms_internal_zzi));
                    } else {
                        throw new zzh(null);
                    }
                }
                throw new zzj(e4);
            }
        }
        zza zzh = com_google_android_gms_internal_zzk_.zzh();
        if (zzh == null) {
            return new zzi(304, null, emptyMap, true, SystemClock.elapsedRealtime() - elapsedRealtime);
        }
        zzh.zzf.putAll(emptyMap);
        return new zzi(304, zzh.data, zzh.zzf, true, SystemClock.elapsedRealtime() - elapsedRealtime);
    }
}

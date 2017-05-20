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

public class zzu implements zzg {
    protected static final boolean DEBUG = zzt.DEBUG;
    private static int zzan = 3000;
    private static int zzao = 4096;
    protected final zzz zzap;
    protected final zzv zzaq;

    public zzu(zzz com_google_android_gms_internal_zzz) {
        this(com_google_android_gms_internal_zzz, new zzv(zzao));
    }

    public zzu(zzz com_google_android_gms_internal_zzz, zzv com_google_android_gms_internal_zzv) {
        this.zzap = com_google_android_gms_internal_zzz;
        this.zzaq = com_google_android_gms_internal_zzv;
    }

    protected static Map<String, String> zza(Header[] headerArr) {
        Map<String, String> treeMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < headerArr.length; i++) {
            treeMap.put(headerArr[i].getName(), headerArr[i].getValue());
        }
        return treeMap;
    }

    private void zza(long j, zzl<?> com_google_android_gms_internal_zzl_, byte[] bArr, StatusLine statusLine) {
        if (DEBUG || j > ((long) zzan)) {
            String str = "HTTP response for request=<%s> [lifetime=%d], [size=%s], [rc=%d], [retryCount=%s]";
            Object[] objArr = new Object[5];
            objArr[0] = com_google_android_gms_internal_zzl_;
            objArr[1] = Long.valueOf(j);
            objArr[2] = bArr != null ? Integer.valueOf(bArr.length) : "null";
            objArr[3] = Integer.valueOf(statusLine.getStatusCode());
            objArr[4] = Integer.valueOf(com_google_android_gms_internal_zzl_.zzq().zzd());
            zzt.zzb(str, objArr);
        }
    }

    private static void zza(String str, zzl<?> com_google_android_gms_internal_zzl_, zzs com_google_android_gms_internal_zzs) throws zzs {
        zzp zzq = com_google_android_gms_internal_zzl_.zzq();
        int zzp = com_google_android_gms_internal_zzl_.zzp();
        try {
            zzq.zza(com_google_android_gms_internal_zzs);
            com_google_android_gms_internal_zzl_.zzc(String.format("%s-retry [timeout=%s]", new Object[]{str, Integer.valueOf(zzp)}));
        } catch (zzs e) {
            com_google_android_gms_internal_zzl_.zzc(String.format("%s-timeout-giveup [timeout=%s]", new Object[]{str, Integer.valueOf(zzp)}));
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

    private byte[] zza(HttpEntity httpEntity) throws IOException, zzq {
        zzab com_google_android_gms_internal_zzab = new zzab(this.zzaq, (int) httpEntity.getContentLength());
        byte[] bArr = null;
        try {
            InputStream content = httpEntity.getContent();
            if (content == null) {
                throw new zzq();
            }
            bArr = this.zzaq.zzb(1024);
            while (true) {
                int read = content.read(bArr);
                if (read == -1) {
                    break;
                }
                com_google_android_gms_internal_zzab.write(bArr, 0, read);
            }
            byte[] toByteArray = com_google_android_gms_internal_zzab.toByteArray();
            return toByteArray;
        } finally {
            try {
                httpEntity.consumeContent();
            } catch (IOException e) {
                zzt.zza("Error occured when calling consumingContent", new Object[0]);
            }
            this.zzaq.zza(bArr);
            com_google_android_gms_internal_zzab.close();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public zzj zza(zzl<?> com_google_android_gms_internal_zzl_) throws zzs {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        while (true) {
            HttpResponse httpResponse = null;
            Map emptyMap = Collections.emptyMap();
            HttpResponse zza;
            int statusCode;
            try {
                Map hashMap = new HashMap();
                zza(hashMap, com_google_android_gms_internal_zzl_.zzh());
                zza = this.zzap.zza(com_google_android_gms_internal_zzl_, hashMap);
                StatusLine statusLine = zza.getStatusLine();
                statusCode = statusLine.getStatusCode();
                emptyMap = zza(zza.getAllHeaders());
                if (statusCode == 304) {
                    break;
                }
                byte[] zza2 = zza.getEntity() != null ? zza(zza.getEntity()) : new byte[0];
                zza(SystemClock.elapsedRealtime() - elapsedRealtime, com_google_android_gms_internal_zzl_, zza2, statusLine);
                if (statusCode >= Callback.DEFAULT_DRAG_ANIMATION_DURATION && statusCode <= 299) {
                    return new zzj(statusCode, zza2, emptyMap, false, SystemClock.elapsedRealtime() - elapsedRealtime);
                }
            } catch (SocketTimeoutException e) {
                zza("socket", com_google_android_gms_internal_zzl_, new zzr());
            } catch (ConnectTimeoutException e2) {
                zza("connection", com_google_android_gms_internal_zzl_, new zzr());
            } catch (Throwable e3) {
                throw new RuntimeException("Bad URL " + com_google_android_gms_internal_zzl_.getUrl(), e3);
            } catch (IOException e4) {
                e3 = e4;
                r5 = null;
                httpResponse = zza;
                if (httpResponse != null) {
                    statusCode = httpResponse.getStatusLine().getStatusCode();
                    zzt.zzc("Unexpected response code %d for %s", Integer.valueOf(statusCode), com_google_android_gms_internal_zzl_.getUrl());
                    byte[] bArr;
                    if (bArr != null) {
                        zzj com_google_android_gms_internal_zzj = new zzj(statusCode, bArr, emptyMap, false, SystemClock.elapsedRealtime() - elapsedRealtime);
                        if (statusCode != 401) {
                        }
                        zza(AuthorBox.TYPE, com_google_android_gms_internal_zzl_, new zza(com_google_android_gms_internal_zzj));
                    } else {
                        zza("network", com_google_android_gms_internal_zzl_, new zzi());
                    }
                } else {
                    Throwable e32;
                    throw new zzk(e32);
                }
            }
        }
        zza zzh = com_google_android_gms_internal_zzl_.zzh();
        if (zzh == null) {
            return new zzj(304, null, emptyMap, true, SystemClock.elapsedRealtime() - elapsedRealtime);
        }
        zzh.zzf.putAll(emptyMap);
        return new zzj(304, zzh.data, zzh.zzf, true, SystemClock.elapsedRealtime() - elapsedRealtime);
    }
}

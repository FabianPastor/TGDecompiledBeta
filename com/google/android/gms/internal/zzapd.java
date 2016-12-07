package com.google.android.gms.internal;

import java.io.Reader;
import java.io.StringReader;

public final class zzapd {
    public zzaoy zza(Reader reader) throws zzaoz, zzaph {
        try {
            zzaqp com_google_android_gms_internal_zzaqp = new zzaqp(reader);
            zzaoy zzh = zzh(com_google_android_gms_internal_zzaqp);
            if (zzh.aY() || com_google_android_gms_internal_zzaqp.bq() == zzaqq.END_DOCUMENT) {
                return zzh;
            }
            throw new zzaph("Did not consume the entire document.");
        } catch (Throwable e) {
            throw new zzaph(e);
        } catch (Throwable e2) {
            throw new zzaoz(e2);
        } catch (Throwable e22) {
            throw new zzaph(e22);
        }
    }

    public zzaoy zzh(zzaqp com_google_android_gms_internal_zzaqp) throws zzaoz, zzaph {
        String valueOf;
        boolean isLenient = com_google_android_gms_internal_zzaqp.isLenient();
        com_google_android_gms_internal_zzaqp.setLenient(true);
        try {
            zzaoy zzh = zzapz.zzh(com_google_android_gms_internal_zzaqp);
            com_google_android_gms_internal_zzaqp.setLenient(isLenient);
            return zzh;
        } catch (Throwable e) {
            valueOf = String.valueOf(com_google_android_gms_internal_zzaqp);
            throw new zzapc(new StringBuilder(String.valueOf(valueOf).length() + 36).append("Failed parsing JSON source: ").append(valueOf).append(" to Json").toString(), e);
        } catch (Throwable e2) {
            valueOf = String.valueOf(com_google_android_gms_internal_zzaqp);
            throw new zzapc(new StringBuilder(String.valueOf(valueOf).length() + 36).append("Failed parsing JSON source: ").append(valueOf).append(" to Json").toString(), e2);
        } catch (Throwable th) {
            com_google_android_gms_internal_zzaqp.setLenient(isLenient);
        }
    }

    public zzaoy zzuq(String str) throws zzaph {
        return zza(new StringReader(str));
    }
}

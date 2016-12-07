package com.google.android.gms.internal;

import java.io.Reader;
import java.io.StringReader;

public final class zzaom {
    public zzaoh zza(Reader reader) throws zzaoi, zzaoq {
        try {
            zzapy com_google_android_gms_internal_zzapy = new zzapy(reader);
            zzaoh zzh = zzh(com_google_android_gms_internal_zzapy);
            if (zzh.aV() || com_google_android_gms_internal_zzapy.bn() == zzapz.END_DOCUMENT) {
                return zzh;
            }
            throw new zzaoq("Did not consume the entire document.");
        } catch (Throwable e) {
            throw new zzaoq(e);
        } catch (Throwable e2) {
            throw new zzaoi(e2);
        } catch (Throwable e22) {
            throw new zzaoq(e22);
        }
    }

    public zzaoh zzh(zzapy com_google_android_gms_internal_zzapy) throws zzaoi, zzaoq {
        String valueOf;
        boolean isLenient = com_google_android_gms_internal_zzapy.isLenient();
        com_google_android_gms_internal_zzapy.setLenient(true);
        try {
            zzaoh zzh = zzapi.zzh(com_google_android_gms_internal_zzapy);
            com_google_android_gms_internal_zzapy.setLenient(isLenient);
            return zzh;
        } catch (Throwable e) {
            valueOf = String.valueOf(com_google_android_gms_internal_zzapy);
            throw new zzaol(new StringBuilder(String.valueOf(valueOf).length() + 36).append("Failed parsing JSON source: ").append(valueOf).append(" to Json").toString(), e);
        } catch (Throwable e2) {
            valueOf = String.valueOf(com_google_android_gms_internal_zzapy);
            throw new zzaol(new StringBuilder(String.valueOf(valueOf).length() + 36).append("Failed parsing JSON source: ").append(valueOf).append(" to Json").toString(), e2);
        } catch (Throwable th) {
            com_google_android_gms_internal_zzapy.setLenient(isLenient);
        }
    }

    public zzaoh zzuq(String str) throws zzaoq {
        return zza(new StringReader(str));
    }
}

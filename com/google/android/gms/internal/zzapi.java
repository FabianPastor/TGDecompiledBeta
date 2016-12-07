package com.google.android.gms.internal;

import java.io.IOException;
import java.io.Writer;

public final class zzapi {

    private static final class zza extends Writer {
        private final Appendable bmi;
        private final zza bmj;

        static class zza implements CharSequence {
            char[] bmk;

            zza() {
            }

            public char charAt(int i) {
                return this.bmk[i];
            }

            public int length() {
                return this.bmk.length;
            }

            public CharSequence subSequence(int i, int i2) {
                return new String(this.bmk, i, i2 - i);
            }
        }

        private zza(Appendable appendable) {
            this.bmj = new zza();
            this.bmi = appendable;
        }

        public void close() {
        }

        public void flush() {
        }

        public void write(int i) throws IOException {
            this.bmi.append((char) i);
        }

        public void write(char[] cArr, int i, int i2) throws IOException {
            this.bmj.bmk = cArr;
            this.bmi.append(this.bmj, i, i + i2);
        }
    }

    public static Writer zza(Appendable appendable) {
        return appendable instanceof Writer ? (Writer) appendable : new zza(appendable);
    }

    public static void zzb(zzaoh com_google_android_gms_internal_zzaoh, zzaqa com_google_android_gms_internal_zzaqa) throws IOException {
        zzapw.bnH.zza(com_google_android_gms_internal_zzaqa, com_google_android_gms_internal_zzaoh);
    }

    public static zzaoh zzh(zzapy com_google_android_gms_internal_zzapy) throws zzaol {
        Object obj = 1;
        try {
            com_google_android_gms_internal_zzapy.bn();
            obj = null;
            return (zzaoh) zzapw.bnH.zzb(com_google_android_gms_internal_zzapy);
        } catch (Throwable e) {
            if (obj != null) {
                return zzaoj.bld;
            }
            throw new zzaoq(e);
        } catch (Throwable e2) {
            throw new zzaoq(e2);
        } catch (Throwable e22) {
            throw new zzaoi(e22);
        } catch (Throwable e222) {
            throw new zzaoq(e222);
        }
    }
}

package com.google.android.gms.internal;

import java.io.IOException;
import java.io.Writer;

public final class zzapz {

    private static final class zza extends Writer {
        private final zza bpA;
        private final Appendable bpz;

        static class zza implements CharSequence {
            char[] bpB;

            zza() {
            }

            public char charAt(int i) {
                return this.bpB[i];
            }

            public int length() {
                return this.bpB.length;
            }

            public CharSequence subSequence(int i, int i2) {
                return new String(this.bpB, i, i2 - i);
            }
        }

        private zza(Appendable appendable) {
            this.bpA = new zza();
            this.bpz = appendable;
        }

        public void close() {
        }

        public void flush() {
        }

        public void write(int i) throws IOException {
            this.bpz.append((char) i);
        }

        public void write(char[] cArr, int i, int i2) throws IOException {
            this.bpA.bpB = cArr;
            this.bpz.append(this.bpA, i, i + i2);
        }
    }

    public static Writer zza(Appendable appendable) {
        return appendable instanceof Writer ? (Writer) appendable : new zza(appendable);
    }

    public static void zzb(zzaoy com_google_android_gms_internal_zzaoy, zzaqr com_google_android_gms_internal_zzaqr) throws IOException {
        zzaqn.bqY.zza(com_google_android_gms_internal_zzaqr, com_google_android_gms_internal_zzaoy);
    }

    public static zzaoy zzh(zzaqp com_google_android_gms_internal_zzaqp) throws zzapc {
        Object obj = 1;
        try {
            com_google_android_gms_internal_zzaqp.bq();
            obj = null;
            return (zzaoy) zzaqn.bqY.zzb(com_google_android_gms_internal_zzaqp);
        } catch (Throwable e) {
            if (obj != null) {
                return zzapa.bou;
            }
            throw new zzaph(e);
        } catch (Throwable e2) {
            throw new zzaph(e2);
        } catch (Throwable e22) {
            throw new zzaoz(e22);
        } catch (Throwable e222) {
            throw new zzaph(e222);
        }
    }
}

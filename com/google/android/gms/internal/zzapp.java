package com.google.android.gms.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public final class zzapp extends zzaqa {
    private static final Writer bmx = new Writer() {
        public void close() throws IOException {
            throw new AssertionError();
        }

        public void flush() throws IOException {
            throw new AssertionError();
        }

        public void write(char[] cArr, int i, int i2) {
            throw new AssertionError();
        }
    };
    private static final zzaon bmy = new zzaon("closed");
    private zzaoh bmA = zzaoj.bld;
    private final List<zzaoh> bmw = new ArrayList();
    private String bmz;

    public zzapp() {
        super(bmx);
    }

    private zzaoh bs() {
        return (zzaoh) this.bmw.get(this.bmw.size() - 1);
    }

    private void zzd(zzaoh com_google_android_gms_internal_zzaoh) {
        if (this.bmz != null) {
            if (!com_google_android_gms_internal_zzaoh.aV() || bK()) {
                ((zzaok) bs()).zza(this.bmz, com_google_android_gms_internal_zzaoh);
            }
            this.bmz = null;
        } else if (this.bmw.isEmpty()) {
            this.bmA = com_google_android_gms_internal_zzaoh;
        } else {
            zzaoh bs = bs();
            if (bs instanceof zzaoe) {
                ((zzaoe) bs).zzc(com_google_android_gms_internal_zzaoh);
                return;
            }
            throw new IllegalStateException();
        }
    }

    public zzaoh br() {
        if (this.bmw.isEmpty()) {
            return this.bmA;
        }
        String valueOf = String.valueOf(this.bmw);
        throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 34).append("Expected one JSON element but was ").append(valueOf).toString());
    }

    public zzaqa bt() throws IOException {
        zzaoh com_google_android_gms_internal_zzaoe = new zzaoe();
        zzd(com_google_android_gms_internal_zzaoe);
        this.bmw.add(com_google_android_gms_internal_zzaoe);
        return this;
    }

    public zzaqa bu() throws IOException {
        if (this.bmw.isEmpty() || this.bmz != null) {
            throw new IllegalStateException();
        } else if (bs() instanceof zzaoe) {
            this.bmw.remove(this.bmw.size() - 1);
            return this;
        } else {
            throw new IllegalStateException();
        }
    }

    public zzaqa bv() throws IOException {
        zzaoh com_google_android_gms_internal_zzaok = new zzaok();
        zzd(com_google_android_gms_internal_zzaok);
        this.bmw.add(com_google_android_gms_internal_zzaok);
        return this;
    }

    public zzaqa bw() throws IOException {
        if (this.bmw.isEmpty() || this.bmz != null) {
            throw new IllegalStateException();
        } else if (bs() instanceof zzaok) {
            this.bmw.remove(this.bmw.size() - 1);
            return this;
        } else {
            throw new IllegalStateException();
        }
    }

    public zzaqa bx() throws IOException {
        zzd(zzaoj.bld);
        return this;
    }

    public void close() throws IOException {
        if (this.bmw.isEmpty()) {
            this.bmw.add(bmy);
            return;
        }
        throw new IOException("Incomplete document");
    }

    public void flush() throws IOException {
    }

    public zzaqa zza(Number number) throws IOException {
        if (number == null) {
            return bx();
        }
        if (!isLenient()) {
            double doubleValue = number.doubleValue();
            if (Double.isNaN(doubleValue) || Double.isInfinite(doubleValue)) {
                String valueOf = String.valueOf(number);
                throw new IllegalArgumentException(new StringBuilder(String.valueOf(valueOf).length() + 33).append("JSON forbids NaN and infinities: ").append(valueOf).toString());
            }
        }
        zzd(new zzaon(number));
        return this;
    }

    public zzaqa zzcu(long j) throws IOException {
        zzd(new zzaon(Long.valueOf(j)));
        return this;
    }

    public zzaqa zzdf(boolean z) throws IOException {
        zzd(new zzaon(Boolean.valueOf(z)));
        return this;
    }

    public zzaqa zzus(String str) throws IOException {
        if (this.bmw.isEmpty() || this.bmz != null) {
            throw new IllegalStateException();
        } else if (bs() instanceof zzaok) {
            this.bmz = str;
            return this;
        } else {
            throw new IllegalStateException();
        }
    }

    public zzaqa zzut(String str) throws IOException {
        if (str == null) {
            return bx();
        }
        zzd(new zzaon(str));
        return this;
    }
}

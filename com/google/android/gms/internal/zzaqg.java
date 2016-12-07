package com.google.android.gms.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public final class zzaqg extends zzaqr {
    private static final Writer bpO = new Writer() {
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
    private static final zzape bpP = new zzape("closed");
    private final List<zzaoy> bpN = new ArrayList();
    private String bpQ;
    private zzaoy bpR = zzapa.bou;

    public zzaqg() {
        super(bpO);
    }

    private zzaoy bv() {
        return (zzaoy) this.bpN.get(this.bpN.size() - 1);
    }

    private void zzd(zzaoy com_google_android_gms_internal_zzaoy) {
        if (this.bpQ != null) {
            if (!com_google_android_gms_internal_zzaoy.aY() || bN()) {
                ((zzapb) bv()).zza(this.bpQ, com_google_android_gms_internal_zzaoy);
            }
            this.bpQ = null;
        } else if (this.bpN.isEmpty()) {
            this.bpR = com_google_android_gms_internal_zzaoy;
        } else {
            zzaoy bv = bv();
            if (bv instanceof zzaov) {
                ((zzaov) bv).zzc(com_google_android_gms_internal_zzaoy);
                return;
            }
            throw new IllegalStateException();
        }
    }

    public zzaqr bA() throws IOException {
        zzd(zzapa.bou);
        return this;
    }

    public zzaoy bu() {
        if (this.bpN.isEmpty()) {
            return this.bpR;
        }
        String valueOf = String.valueOf(this.bpN);
        throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 34).append("Expected one JSON element but was ").append(valueOf).toString());
    }

    public zzaqr bw() throws IOException {
        zzaoy com_google_android_gms_internal_zzaov = new zzaov();
        zzd(com_google_android_gms_internal_zzaov);
        this.bpN.add(com_google_android_gms_internal_zzaov);
        return this;
    }

    public zzaqr bx() throws IOException {
        if (this.bpN.isEmpty() || this.bpQ != null) {
            throw new IllegalStateException();
        } else if (bv() instanceof zzaov) {
            this.bpN.remove(this.bpN.size() - 1);
            return this;
        } else {
            throw new IllegalStateException();
        }
    }

    public zzaqr by() throws IOException {
        zzaoy com_google_android_gms_internal_zzapb = new zzapb();
        zzd(com_google_android_gms_internal_zzapb);
        this.bpN.add(com_google_android_gms_internal_zzapb);
        return this;
    }

    public zzaqr bz() throws IOException {
        if (this.bpN.isEmpty() || this.bpQ != null) {
            throw new IllegalStateException();
        } else if (bv() instanceof zzapb) {
            this.bpN.remove(this.bpN.size() - 1);
            return this;
        } else {
            throw new IllegalStateException();
        }
    }

    public void close() throws IOException {
        if (this.bpN.isEmpty()) {
            this.bpN.add(bpP);
            return;
        }
        throw new IOException("Incomplete document");
    }

    public void flush() throws IOException {
    }

    public zzaqr zza(Number number) throws IOException {
        if (number == null) {
            return bA();
        }
        if (!isLenient()) {
            double doubleValue = number.doubleValue();
            if (Double.isNaN(doubleValue) || Double.isInfinite(doubleValue)) {
                String valueOf = String.valueOf(number);
                throw new IllegalArgumentException(new StringBuilder(String.valueOf(valueOf).length() + 33).append("JSON forbids NaN and infinities: ").append(valueOf).toString());
            }
        }
        zzd(new zzape(number));
        return this;
    }

    public zzaqr zzcs(long j) throws IOException {
        zzd(new zzape(Long.valueOf(j)));
        return this;
    }

    public zzaqr zzdh(boolean z) throws IOException {
        zzd(new zzape(Boolean.valueOf(z)));
        return this;
    }

    public zzaqr zzus(String str) throws IOException {
        if (this.bpN.isEmpty() || this.bpQ != null) {
            throw new IllegalStateException();
        } else if (bv() instanceof zzapb) {
            this.bpQ = str;
            return this;
        } else {
            throw new IllegalStateException();
        }
    }

    public zzaqr zzut(String str) throws IOException {
        if (str == null) {
            return bA();
        }
        zzd(new zzape(str));
        return this;
    }
}

package com.google.android.gms.internal;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;

public class zzaqa implements Closeable, Flushable {
    private static final String[] bov = new String[128];
    private static final String[] bow = ((String[]) bov.clone());
    private boolean bkN;
    private boolean bkO;
    private boolean bnY;
    private int[] bog = new int[32];
    private int boh = 0;
    private String box;
    private String boy;
    private final Writer out;
    private String separator;

    static {
        for (int i = 0; i <= 31; i++) {
            bov[i] = String.format("\\u%04x", new Object[]{Integer.valueOf(i)});
        }
        bov[34] = "\\\"";
        bov[92] = "\\\\";
        bov[9] = "\\t";
        bov[8] = "\\b";
        bov[10] = "\\n";
        bov[13] = "\\r";
        bov[12] = "\\f";
        bow[60] = "\\u003c";
        bow[62] = "\\u003e";
        bow[38] = "\\u0026";
        bow[61] = "\\u003d";
        bow[39] = "\\u0027";
    }

    public zzaqa(Writer writer) {
        zzagw(6);
        this.separator = ":";
        this.bkN = true;
        if (writer == null) {
            throw new NullPointerException("out == null");
        }
        this.out = writer;
    }

    private int bL() {
        if (this.boh != 0) {
            return this.bog[this.boh - 1];
        }
        throw new IllegalStateException("JsonWriter is closed.");
    }

    private void bM() throws IOException {
        if (this.boy != null) {
            bO();
            zzuw(this.boy);
            this.boy = null;
        }
    }

    private void bN() throws IOException {
        if (this.box != null) {
            this.out.write("\n");
            int i = this.boh;
            for (int i2 = 1; i2 < i; i2++) {
                this.out.write(this.box);
            }
        }
    }

    private void bO() throws IOException {
        int bL = bL();
        if (bL == 5) {
            this.out.write(44);
        } else if (bL != 3) {
            throw new IllegalStateException("Nesting problem.");
        }
        bN();
        zzagy(4);
    }

    private void zzagw(int i) {
        if (this.boh == this.bog.length) {
            Object obj = new int[(this.boh * 2)];
            System.arraycopy(this.bog, 0, obj, 0, this.boh);
            this.bog = obj;
        }
        int[] iArr = this.bog;
        int i2 = this.boh;
        this.boh = i2 + 1;
        iArr[i2] = i;
    }

    private void zzagy(int i) {
        this.bog[this.boh - 1] = i;
    }

    private zzaqa zzc(int i, int i2, String str) throws IOException {
        int bL = bL();
        if (bL != i2 && bL != i) {
            throw new IllegalStateException("Nesting problem.");
        } else if (this.boy != null) {
            String str2 = "Dangling name: ";
            String valueOf = String.valueOf(this.boy);
            throw new IllegalStateException(valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
        } else {
            this.boh--;
            if (bL == i2) {
                bN();
            }
            this.out.write(str);
            return this;
        }
    }

    private void zzdj(boolean z) throws IOException {
        switch (bL()) {
            case 1:
                zzagy(2);
                bN();
                return;
            case 2:
                this.out.append(',');
                bN();
                return;
            case 4:
                this.out.append(this.separator);
                zzagy(5);
                return;
            case 6:
                break;
            case 7:
                if (!this.bnY) {
                    throw new IllegalStateException("JSON must have only one top-level value.");
                }
                break;
            default:
                throw new IllegalStateException("Nesting problem.");
        }
        if (this.bnY || z) {
            zzagy(7);
            return;
        }
        throw new IllegalStateException("JSON must start with an array or an object.");
    }

    private zzaqa zzq(int i, String str) throws IOException {
        zzdj(true);
        zzagw(i);
        this.out.write(str);
        return this;
    }

    private void zzuw(String str) throws IOException {
        int i = 0;
        String[] strArr = this.bkO ? bow : bov;
        this.out.write("\"");
        int length = str.length();
        for (int i2 = 0; i2 < length; i2++) {
            char charAt = str.charAt(i2);
            String str2;
            if (charAt < '') {
                str2 = strArr[charAt];
                if (str2 == null) {
                }
                if (i < i2) {
                    this.out.write(str, i, i2 - i);
                }
                this.out.write(str2);
                i = i2 + 1;
            } else {
                if (charAt == ' ') {
                    str2 = "\\u2028";
                } else if (charAt == ' ') {
                    str2 = "\\u2029";
                }
                if (i < i2) {
                    this.out.write(str, i, i2 - i);
                }
                this.out.write(str2);
                i = i2 + 1;
            }
        }
        if (i < length) {
            this.out.write(str, i, length - i);
        }
        this.out.write("\"");
    }

    public final boolean bJ() {
        return this.bkO;
    }

    public final boolean bK() {
        return this.bkN;
    }

    public zzaqa bt() throws IOException {
        bM();
        return zzq(1, "[");
    }

    public zzaqa bu() throws IOException {
        return zzc(1, 2, "]");
    }

    public zzaqa bv() throws IOException {
        bM();
        return zzq(3, "{");
    }

    public zzaqa bw() throws IOException {
        return zzc(3, 5, "}");
    }

    public zzaqa bx() throws IOException {
        if (this.boy != null) {
            if (this.bkN) {
                bM();
            } else {
                this.boy = null;
                return this;
            }
        }
        zzdj(false);
        this.out.write("null");
        return this;
    }

    public void close() throws IOException {
        this.out.close();
        int i = this.boh;
        if (i > 1 || (i == 1 && this.bog[i - 1] != 7)) {
            throw new IOException("Incomplete document");
        }
        this.boh = 0;
    }

    public void flush() throws IOException {
        if (this.boh == 0) {
            throw new IllegalStateException("JsonWriter is closed.");
        }
        this.out.flush();
    }

    public boolean isLenient() {
        return this.bnY;
    }

    public final void setIndent(String str) {
        if (str.length() == 0) {
            this.box = null;
            this.separator = ":";
            return;
        }
        this.box = str;
        this.separator = ": ";
    }

    public final void setLenient(boolean z) {
        this.bnY = z;
    }

    public zzaqa zza(Number number) throws IOException {
        if (number == null) {
            return bx();
        }
        bM();
        CharSequence obj = number.toString();
        if (this.bnY || !(obj.equals("-Infinity") || obj.equals("Infinity") || obj.equals("NaN"))) {
            zzdj(false);
            this.out.append(obj);
            return this;
        }
        String valueOf = String.valueOf(number);
        throw new IllegalArgumentException(new StringBuilder(String.valueOf(valueOf).length() + 39).append("Numeric values must be finite, but was ").append(valueOf).toString());
    }

    public zzaqa zzcu(long j) throws IOException {
        bM();
        zzdj(false);
        this.out.write(Long.toString(j));
        return this;
    }

    public zzaqa zzdf(boolean z) throws IOException {
        bM();
        zzdj(false);
        this.out.write(z ? "true" : "false");
        return this;
    }

    public final void zzdh(boolean z) {
        this.bkO = z;
    }

    public final void zzdi(boolean z) {
        this.bkN = z;
    }

    public zzaqa zzus(String str) throws IOException {
        if (str == null) {
            throw new NullPointerException("name == null");
        } else if (this.boy != null) {
            throw new IllegalStateException();
        } else if (this.boh == 0) {
            throw new IllegalStateException("JsonWriter is closed.");
        } else {
            this.boy = str;
            return this;
        }
    }

    public zzaqa zzut(String str) throws IOException {
        if (str == null) {
            return bx();
        }
        bM();
        zzdj(false);
        zzuw(str);
        return this;
    }
}

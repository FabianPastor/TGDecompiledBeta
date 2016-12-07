package com.google.android.gms.internal;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;

public class zzaqr implements Closeable, Flushable {
    private static final String[] brM = new String[128];
    private static final String[] brN = ((String[]) brM.clone());
    private boolean boe;
    private boolean bof;
    private String brO;
    private String brP;
    private boolean brp;
    private int[] brx = new int[32];
    private int bry = 0;
    private final Writer out;
    private String separator;

    static {
        for (int i = 0; i <= 31; i++) {
            brM[i] = String.format("\\u%04x", new Object[]{Integer.valueOf(i)});
        }
        brM[34] = "\\\"";
        brM[92] = "\\\\";
        brM[9] = "\\t";
        brM[8] = "\\b";
        brM[10] = "\\n";
        brM[13] = "\\r";
        brM[12] = "\\f";
        brN[60] = "\\u003c";
        brN[62] = "\\u003e";
        brN[38] = "\\u0026";
        brN[61] = "\\u003d";
        brN[39] = "\\u0027";
    }

    public zzaqr(Writer writer) {
        zzagn(6);
        this.separator = ":";
        this.boe = true;
        if (writer == null) {
            throw new NullPointerException("out == null");
        }
        this.out = writer;
    }

    private int bO() {
        if (this.bry != 0) {
            return this.brx[this.bry - 1];
        }
        throw new IllegalStateException("JsonWriter is closed.");
    }

    private void bP() throws IOException {
        if (this.brP != null) {
            bR();
            zzuw(this.brP);
            this.brP = null;
        }
    }

    private void bQ() throws IOException {
        if (this.brO != null) {
            this.out.write("\n");
            int i = this.bry;
            for (int i2 = 1; i2 < i; i2++) {
                this.out.write(this.brO);
            }
        }
    }

    private void bR() throws IOException {
        int bO = bO();
        if (bO == 5) {
            this.out.write(44);
        } else if (bO != 3) {
            throw new IllegalStateException("Nesting problem.");
        }
        bQ();
        zzagp(4);
    }

    private void zzagn(int i) {
        if (this.bry == this.brx.length) {
            Object obj = new int[(this.bry * 2)];
            System.arraycopy(this.brx, 0, obj, 0, this.bry);
            this.brx = obj;
        }
        int[] iArr = this.brx;
        int i2 = this.bry;
        this.bry = i2 + 1;
        iArr[i2] = i;
    }

    private void zzagp(int i) {
        this.brx[this.bry - 1] = i;
    }

    private zzaqr zzc(int i, int i2, String str) throws IOException {
        int bO = bO();
        if (bO != i2 && bO != i) {
            throw new IllegalStateException("Nesting problem.");
        } else if (this.brP != null) {
            String str2 = "Dangling name: ";
            String valueOf = String.valueOf(this.brP);
            throw new IllegalStateException(valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
        } else {
            this.bry--;
            if (bO == i2) {
                bQ();
            }
            this.out.write(str);
            return this;
        }
    }

    private void zzdl(boolean z) throws IOException {
        switch (bO()) {
            case 1:
                zzagp(2);
                bQ();
                return;
            case 2:
                this.out.append(',');
                bQ();
                return;
            case 4:
                this.out.append(this.separator);
                zzagp(5);
                return;
            case 6:
                break;
            case 7:
                if (!this.brp) {
                    throw new IllegalStateException("JSON must have only one top-level value.");
                }
                break;
            default:
                throw new IllegalStateException("Nesting problem.");
        }
        if (this.brp || z) {
            zzagp(7);
            return;
        }
        throw new IllegalStateException("JSON must start with an array or an object.");
    }

    private zzaqr zzp(int i, String str) throws IOException {
        zzdl(true);
        zzagn(i);
        this.out.write(str);
        return this;
    }

    private void zzuw(String str) throws IOException {
        int i = 0;
        String[] strArr = this.bof ? brN : brM;
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

    public zzaqr bA() throws IOException {
        if (this.brP != null) {
            if (this.boe) {
                bP();
            } else {
                this.brP = null;
                return this;
            }
        }
        zzdl(false);
        this.out.write("null");
        return this;
    }

    public final boolean bM() {
        return this.bof;
    }

    public final boolean bN() {
        return this.boe;
    }

    public zzaqr bw() throws IOException {
        bP();
        return zzp(1, "[");
    }

    public zzaqr bx() throws IOException {
        return zzc(1, 2, "]");
    }

    public zzaqr by() throws IOException {
        bP();
        return zzp(3, "{");
    }

    public zzaqr bz() throws IOException {
        return zzc(3, 5, "}");
    }

    public void close() throws IOException {
        this.out.close();
        int i = this.bry;
        if (i > 1 || (i == 1 && this.brx[i - 1] != 7)) {
            throw new IOException("Incomplete document");
        }
        this.bry = 0;
    }

    public void flush() throws IOException {
        if (this.bry == 0) {
            throw new IllegalStateException("JsonWriter is closed.");
        }
        this.out.flush();
    }

    public boolean isLenient() {
        return this.brp;
    }

    public final void setIndent(String str) {
        if (str.length() == 0) {
            this.brO = null;
            this.separator = ":";
            return;
        }
        this.brO = str;
        this.separator = ": ";
    }

    public final void setLenient(boolean z) {
        this.brp = z;
    }

    public zzaqr zza(Number number) throws IOException {
        if (number == null) {
            return bA();
        }
        bP();
        CharSequence obj = number.toString();
        if (this.brp || !(obj.equals("-Infinity") || obj.equals("Infinity") || obj.equals("NaN"))) {
            zzdl(false);
            this.out.append(obj);
            return this;
        }
        String valueOf = String.valueOf(number);
        throw new IllegalArgumentException(new StringBuilder(String.valueOf(valueOf).length() + 39).append("Numeric values must be finite, but was ").append(valueOf).toString());
    }

    public zzaqr zzcs(long j) throws IOException {
        bP();
        zzdl(false);
        this.out.write(Long.toString(j));
        return this;
    }

    public zzaqr zzdh(boolean z) throws IOException {
        bP();
        zzdl(false);
        this.out.write(z ? "true" : "false");
        return this;
    }

    public final void zzdj(boolean z) {
        this.bof = z;
    }

    public final void zzdk(boolean z) {
        this.boe = z;
    }

    public zzaqr zzus(String str) throws IOException {
        if (str == null) {
            throw new NullPointerException("name == null");
        } else if (this.brP != null) {
            throw new IllegalStateException();
        } else if (this.bry == 0) {
            throw new IllegalStateException("JsonWriter is closed.");
        } else {
            this.brP = str;
            return this;
        }
    }

    public zzaqr zzut(String str) throws IOException {
        if (str == null) {
            return bA();
        }
        bP();
        zzdl(false);
        zzuw(str);
        return this;
    }
}

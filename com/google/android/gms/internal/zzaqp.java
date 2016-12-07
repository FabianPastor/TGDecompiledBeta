package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;

public class zzaqp implements Closeable {
    private static final char[] bro = ")]}'\n".toCharArray();
    private int[] brA;
    private boolean brp = false;
    private final char[] brq = new char[1024];
    private int brr = 0;
    private int brs = 0;
    private int brt = 0;
    private long bru;
    private int brv;
    private String brw;
    private int[] brx = new int[32];
    private int bry = 0;
    private String[] brz;
    private final Reader in;
    private int limit = 0;
    private int pos = 0;

    static {
        zzapu.bph = new zzapu() {
            public void zzi(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
                if (com_google_android_gms_internal_zzaqp instanceof zzaqf) {
                    ((zzaqf) com_google_android_gms_internal_zzaqp).bt();
                    return;
                }
                int zzag = com_google_android_gms_internal_zzaqp.brt;
                if (zzag == 0) {
                    zzag = com_google_android_gms_internal_zzaqp.bD();
                }
                if (zzag == 13) {
                    com_google_android_gms_internal_zzaqp.brt = 9;
                } else if (zzag == 12) {
                    com_google_android_gms_internal_zzaqp.brt = 8;
                } else if (zzag == 14) {
                    com_google_android_gms_internal_zzaqp.brt = 10;
                } else {
                    String valueOf = String.valueOf(com_google_android_gms_internal_zzaqp.bq());
                    int zzai = com_google_android_gms_internal_zzaqp.getLineNumber();
                    int zzaj = com_google_android_gms_internal_zzaqp.getColumnNumber();
                    String path = com_google_android_gms_internal_zzaqp.getPath();
                    throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 70) + String.valueOf(path).length()).append("Expected a name but was ").append(valueOf).append(" ").append(" at line ").append(zzai).append(" column ").append(zzaj).append(" path ").append(path).toString());
                }
            }
        };
    }

    public zzaqp(Reader reader) {
        int[] iArr = this.brx;
        int i = this.bry;
        this.bry = i + 1;
        iArr[i] = 6;
        this.brz = new String[32];
        this.brA = new int[32];
        if (reader == null) {
            throw new NullPointerException("in == null");
        }
        this.in = reader;
    }

    private int bD() throws IOException {
        int zzdi;
        int i = this.brx[this.bry - 1];
        if (i == 1) {
            this.brx[this.bry - 1] = 2;
        } else if (i == 2) {
            switch (zzdi(true)) {
                case 44:
                    break;
                case 59:
                    bI();
                    break;
                case 93:
                    this.brt = 4;
                    return 4;
                default:
                    throw zzuv("Unterminated array");
            }
        } else if (i == 3 || i == 5) {
            this.brx[this.bry - 1] = 4;
            if (i == 5) {
                switch (zzdi(true)) {
                    case 44:
                        break;
                    case 59:
                        bI();
                        break;
                    case 125:
                        this.brt = 2;
                        return 2;
                    default:
                        throw zzuv("Unterminated object");
                }
            }
            zzdi = zzdi(true);
            switch (zzdi) {
                case 34:
                    this.brt = 13;
                    return 13;
                case 39:
                    bI();
                    this.brt = 12;
                    return 12;
                case 125:
                    if (i != 5) {
                        this.brt = 2;
                        return 2;
                    }
                    throw zzuv("Expected name");
                default:
                    bI();
                    this.pos--;
                    if (zzc((char) zzdi)) {
                        this.brt = 14;
                        return 14;
                    }
                    throw zzuv("Expected name");
            }
        } else if (i == 4) {
            this.brx[this.bry - 1] = 5;
            switch (zzdi(true)) {
                case 58:
                    break;
                case 61:
                    bI();
                    if ((this.pos < this.limit || zzago(1)) && this.brq[this.pos] == '>') {
                        this.pos++;
                        break;
                    }
                default:
                    throw zzuv("Expected ':'");
            }
        } else if (i == 6) {
            if (this.brp) {
                bL();
            }
            this.brx[this.bry - 1] = 7;
        } else if (i == 7) {
            if (zzdi(false) == -1) {
                this.brt = 17;
                return 17;
            }
            bI();
            this.pos--;
        } else if (i == 8) {
            throw new IllegalStateException("JsonReader is closed");
        }
        switch (zzdi(true)) {
            case 34:
                if (this.bry == 1) {
                    bI();
                }
                this.brt = 9;
                return 9;
            case 39:
                bI();
                this.brt = 8;
                return 8;
            case 44:
            case 59:
                break;
            case 91:
                this.brt = 3;
                return 3;
            case 93:
                if (i == 1) {
                    this.brt = 4;
                    return 4;
                }
                break;
            case 123:
                this.brt = 1;
                return 1;
            default:
                this.pos--;
                if (this.bry == 1) {
                    bI();
                }
                zzdi = bE();
                if (zzdi != 0) {
                    return zzdi;
                }
                zzdi = bF();
                if (zzdi != 0) {
                    return zzdi;
                }
                if (zzc(this.brq[this.pos])) {
                    bI();
                    this.brt = 10;
                    return 10;
                }
                throw zzuv("Expected value");
        }
        if (i == 1 || i == 2) {
            bI();
            this.pos--;
            this.brt = 7;
            return 7;
        }
        throw zzuv("Unexpected value");
    }

    private int bE() throws IOException {
        String str;
        int i;
        char c = this.brq[this.pos];
        String str2;
        if (c == 't' || c == 'T') {
            str = "true";
            str2 = "TRUE";
            i = 5;
        } else if (c == 'f' || c == 'F') {
            str = "false";
            str2 = "FALSE";
            i = 6;
        } else if (c != 'n' && c != 'N') {
            return 0;
        } else {
            str = "null";
            str2 = "NULL";
            i = 7;
        }
        int length = str.length();
        int i2 = 1;
        while (i2 < length) {
            if (this.pos + i2 >= this.limit && !zzago(i2 + 1)) {
                return 0;
            }
            char c2 = this.brq[this.pos + i2];
            if (c2 != str.charAt(i2) && c2 != r1.charAt(i2)) {
                return 0;
            }
            i2++;
        }
        if ((this.pos + length < this.limit || zzago(length + 1)) && zzc(this.brq[this.pos + length])) {
            return 0;
        }
        this.pos += length;
        this.brt = i;
        return i;
    }

    private int bF() throws IOException {
        char[] cArr = this.brq;
        int i = this.pos;
        long j = 0;
        Object obj = null;
        int i2 = 1;
        int i3 = 0;
        int i4 = 0;
        int i5 = this.limit;
        int i6 = i;
        while (true) {
            Object obj2;
            if (i6 + i4 == i5) {
                if (i4 == cArr.length) {
                    return 0;
                }
                if (zzago(i4 + 1)) {
                    i6 = this.pos;
                    i5 = this.limit;
                } else if (i3 != 2 && i2 != 0 && (j != Long.MIN_VALUE || obj != null)) {
                    if (obj == null) {
                        j = -j;
                    }
                    this.bru = j;
                    this.pos += i4;
                    this.brt = 15;
                    return 15;
                } else if (i3 == 2 && i3 != 4 && i3 != 7) {
                    return 0;
                } else {
                    this.brv = i4;
                    this.brt = 16;
                    return 16;
                }
            }
            char c = cArr[i6 + i4];
            int i7;
            switch (c) {
                case '+':
                    if (i3 != 5) {
                        return 0;
                    }
                    i = 6;
                    i3 = i2;
                    obj2 = obj;
                    continue;
                case MotionEventCompat.AXIS_GENERIC_14 /*45*/:
                    if (i3 == 0) {
                        i = 1;
                        i7 = i2;
                        obj2 = 1;
                        i3 = i7;
                        continue;
                    } else if (i3 == 5) {
                        i = 6;
                        i3 = i2;
                        obj2 = obj;
                        break;
                    } else {
                        return 0;
                    }
                case '.':
                    if (i3 != 2) {
                        return 0;
                    }
                    i = 3;
                    i3 = i2;
                    obj2 = obj;
                    continue;
                case 'E':
                case 'e':
                    if (i3 != 2 && i3 != 4) {
                        return 0;
                    }
                    i = 5;
                    i3 = i2;
                    obj2 = obj;
                    continue;
                default:
                    if (c >= '0' && c <= '9') {
                        if (i3 != 1 && i3 != 0) {
                            if (i3 != 2) {
                                if (i3 != 3) {
                                    if (i3 != 5 && i3 != 6) {
                                        i = i3;
                                        i3 = i2;
                                        obj2 = obj;
                                        break;
                                    }
                                    i = 7;
                                    i3 = i2;
                                    obj2 = obj;
                                    break;
                                }
                                i = 4;
                                i3 = i2;
                                obj2 = obj;
                                break;
                            } else if (j != 0) {
                                long j2 = (10 * j) - ((long) (c - 48));
                                i = (j > -922337203685477580L || (j == -922337203685477580L && j2 < j)) ? 1 : 0;
                                i &= i2;
                                obj2 = obj;
                                j = j2;
                                i7 = i3;
                                i3 = i;
                                i = i7;
                                break;
                            } else {
                                return 0;
                            }
                        }
                        j = (long) (-(c - 48));
                        i = 2;
                        i3 = i2;
                        obj2 = obj;
                        continue;
                    } else if (zzc(c)) {
                        return 0;
                    }
                    break;
            }
            if (i3 != 2) {
            }
            if (i3 == 2) {
            }
            this.brv = i4;
            this.brt = 16;
            return 16;
            i4++;
            obj = obj2;
            i2 = i3;
            i3 = i;
        }
    }

    private String bG() throws IOException {
        StringBuilder stringBuilder = null;
        int i = 0;
        while (true) {
            String str;
            if (this.pos + i < this.limit) {
                switch (this.brq[this.pos + i]) {
                    case '\t':
                    case '\n':
                    case '\f':
                    case '\r':
                    case ' ':
                    case ',':
                    case ':':
                    case '[':
                    case ']':
                    case '{':
                    case '}':
                        break;
                    case '#':
                    case MotionEventCompat.AXIS_GENERIC_16 /*47*/:
                    case ';':
                    case '=':
                    case '\\':
                        bI();
                        break;
                    default:
                        i++;
                        continue;
                }
            } else if (i >= this.brq.length) {
                if (stringBuilder == null) {
                    stringBuilder = new StringBuilder();
                }
                stringBuilder.append(this.brq, this.pos, i);
                this.pos = i + this.pos;
                i = !zzago(1) ? 0 : 0;
            } else if (zzago(i + 1)) {
            }
            if (stringBuilder == null) {
                str = new String(this.brq, this.pos, i);
            } else {
                stringBuilder.append(this.brq, this.pos, i);
                str = stringBuilder.toString();
            }
            this.pos = i + this.pos;
            return str;
        }
    }

    private void bH() throws IOException {
        do {
            int i = 0;
            while (this.pos + i < this.limit) {
                switch (this.brq[this.pos + i]) {
                    case '\t':
                    case '\n':
                    case '\f':
                    case '\r':
                    case ' ':
                    case ',':
                    case ':':
                    case '[':
                    case ']':
                    case '{':
                    case '}':
                        break;
                    case '#':
                    case MotionEventCompat.AXIS_GENERIC_16 /*47*/:
                    case ';':
                    case '=':
                    case '\\':
                        bI();
                        break;
                    default:
                        i++;
                }
                this.pos = i + this.pos;
                return;
            }
            this.pos = i + this.pos;
        } while (zzago(1));
    }

    private void bI() throws IOException {
        if (!this.brp) {
            throw zzuv("Use JsonReader.setLenient(true) to accept malformed JSON");
        }
    }

    private void bJ() throws IOException {
        char c;
        do {
            if (this.pos < this.limit || zzago(1)) {
                char[] cArr = this.brq;
                int i = this.pos;
                this.pos = i + 1;
                c = cArr[i];
                if (c == '\n') {
                    this.brr++;
                    this.brs = this.pos;
                    return;
                }
            } else {
                return;
            }
        } while (c != '\r');
    }

    private char bK() throws IOException {
        if (this.pos != this.limit || zzago(1)) {
            char[] cArr = this.brq;
            int i = this.pos;
            this.pos = i + 1;
            char c = cArr[i];
            switch (c) {
                case '\n':
                    this.brr++;
                    this.brs = this.pos;
                    return c;
                case 'b':
                    return '\b';
                case 'f':
                    return '\f';
                case 'n':
                    return '\n';
                case 'r':
                    return '\r';
                case 't':
                    return '\t';
                case 'u':
                    if (this.pos + 4 <= this.limit || zzago(4)) {
                        int i2 = this.pos;
                        int i3 = i2 + 4;
                        int i4 = i2;
                        c = '\u0000';
                        for (i = i4; i < i3; i++) {
                            char c2 = this.brq[i];
                            c = (char) (c << 4);
                            if (c2 >= '0' && c2 <= '9') {
                                c = (char) (c + (c2 - 48));
                            } else if (c2 >= 'a' && c2 <= 'f') {
                                c = (char) (c + ((c2 - 97) + 10));
                            } else if (c2 < 'A' || c2 > 'F') {
                                String str = "\\u";
                                String valueOf = String.valueOf(new String(this.brq, this.pos, 4));
                                throw new NumberFormatException(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
                            } else {
                                c = (char) (c + ((c2 - 65) + 10));
                            }
                        }
                        this.pos += 4;
                        return c;
                    }
                    throw zzuv("Unterminated escape sequence");
                default:
                    return c;
            }
        }
        throw zzuv("Unterminated escape sequence");
    }

    private void bL() throws IOException {
        zzdi(true);
        this.pos--;
        if (this.pos + bro.length <= this.limit || zzago(bro.length)) {
            int i = 0;
            while (i < bro.length) {
                if (this.brq[this.pos + i] == bro[i]) {
                    i++;
                } else {
                    return;
                }
            }
            this.pos += bro.length;
        }
    }

    private int getColumnNumber() {
        return (this.pos - this.brs) + 1;
    }

    private int getLineNumber() {
        return this.brr + 1;
    }

    private void zzagn(int i) {
        if (this.bry == this.brx.length) {
            Object obj = new int[(this.bry * 2)];
            Object obj2 = new int[(this.bry * 2)];
            Object obj3 = new String[(this.bry * 2)];
            System.arraycopy(this.brx, 0, obj, 0, this.bry);
            System.arraycopy(this.brA, 0, obj2, 0, this.bry);
            System.arraycopy(this.brz, 0, obj3, 0, this.bry);
            this.brx = obj;
            this.brA = obj2;
            this.brz = obj3;
        }
        int[] iArr = this.brx;
        int i2 = this.bry;
        this.bry = i2 + 1;
        iArr[i2] = i;
    }

    private boolean zzago(int i) throws IOException {
        Object obj = this.brq;
        this.brs -= this.pos;
        if (this.limit != this.pos) {
            this.limit -= this.pos;
            System.arraycopy(obj, this.pos, obj, 0, this.limit);
        } else {
            this.limit = 0;
        }
        this.pos = 0;
        do {
            int read = this.in.read(obj, this.limit, obj.length - this.limit);
            if (read == -1) {
                return false;
            }
            this.limit = read + this.limit;
            if (this.brr == 0 && this.brs == 0 && this.limit > 0 && obj[0] == '﻿') {
                this.pos++;
                this.brs++;
                i++;
            }
        } while (this.limit < i);
        return true;
    }

    private boolean zzc(char c) throws IOException {
        switch (c) {
            case '\t':
            case '\n':
            case '\f':
            case '\r':
            case ' ':
            case ',':
            case ':':
            case '[':
            case ']':
            case '{':
            case '}':
                break;
            case '#':
            case MotionEventCompat.AXIS_GENERIC_16 /*47*/:
            case ';':
            case '=':
            case '\\':
                bI();
                break;
            default:
                return true;
        }
        return false;
    }

    private String zzd(char c) throws IOException {
        char[] cArr = this.brq;
        StringBuilder stringBuilder = new StringBuilder();
        do {
            int i = this.pos;
            int i2 = this.limit;
            int i3 = i;
            while (i3 < i2) {
                int i4 = i3 + 1;
                char c2 = cArr[i3];
                if (c2 == c) {
                    this.pos = i4;
                    stringBuilder.append(cArr, i, (i4 - i) - 1);
                    return stringBuilder.toString();
                }
                if (c2 == '\\') {
                    this.pos = i4;
                    stringBuilder.append(cArr, i, (i4 - i) - 1);
                    stringBuilder.append(bK());
                    i = this.pos;
                    i2 = this.limit;
                    i4 = i;
                } else if (c2 == '\n') {
                    this.brr++;
                    this.brs = i4;
                }
                i3 = i4;
            }
            stringBuilder.append(cArr, i, i3 - i);
            this.pos = i3;
        } while (zzago(1));
        throw zzuv("Unterminated string");
    }

    private int zzdi(boolean z) throws IOException {
        char[] cArr = this.brq;
        int i = this.pos;
        int i2 = this.limit;
        while (true) {
            int lineNumber;
            if (i == i2) {
                this.pos = i;
                if (zzago(1)) {
                    i = this.pos;
                    i2 = this.limit;
                } else if (!z) {
                    return -1;
                } else {
                    String valueOf = String.valueOf("End of input at line ");
                    lineNumber = getLineNumber();
                    throw new EOFException(new StringBuilder(String.valueOf(valueOf).length() + 30).append(valueOf).append(lineNumber).append(" column ").append(getColumnNumber()).toString());
                }
            }
            lineNumber = i + 1;
            char c = cArr[i];
            if (c == '\n') {
                this.brr++;
                this.brs = lineNumber;
                i = lineNumber;
            } else if (c == ' ' || c == '\r') {
                i = lineNumber;
            } else if (c == '\t') {
                i = lineNumber;
            } else if (c == '/') {
                this.pos = lineNumber;
                if (lineNumber == i2) {
                    this.pos--;
                    boolean zzago = zzago(2);
                    this.pos++;
                    if (!zzago) {
                        return c;
                    }
                }
                bI();
                switch (cArr[this.pos]) {
                    case '*':
                        this.pos++;
                        if (zzuu("*/")) {
                            i = this.pos + 2;
                            i2 = this.limit;
                            break;
                        }
                        throw zzuv("Unterminated comment");
                    case MotionEventCompat.AXIS_GENERIC_16 /*47*/:
                        this.pos++;
                        bJ();
                        i = this.pos;
                        i2 = this.limit;
                        break;
                    default:
                        return c;
                }
            } else if (c == '#') {
                this.pos = lineNumber;
                bI();
                bJ();
                i = this.pos;
                i2 = this.limit;
            } else {
                this.pos = lineNumber;
                return c;
            }
        }
    }

    private void zze(char c) throws IOException {
        char[] cArr = this.brq;
        do {
            int i = this.pos;
            int i2 = this.limit;
            int i3 = i;
            while (i3 < i2) {
                i = i3 + 1;
                char c2 = cArr[i3];
                if (c2 == c) {
                    this.pos = i;
                    return;
                }
                if (c2 == '\\') {
                    this.pos = i;
                    bK();
                    i = this.pos;
                    i2 = this.limit;
                } else if (c2 == '\n') {
                    this.brr++;
                    this.brs = i;
                }
                i3 = i;
            }
            this.pos = i3;
        } while (zzago(1));
        throw zzuv("Unterminated string");
    }

    private boolean zzuu(String str) throws IOException {
        while (true) {
            if (this.pos + str.length() > this.limit && !zzago(str.length())) {
                return false;
            }
            if (this.brq[this.pos] == '\n') {
                this.brr++;
                this.brs = this.pos + 1;
            } else {
                int i = 0;
                while (i < str.length()) {
                    if (this.brq[this.pos + i] == str.charAt(i)) {
                        i++;
                    }
                }
                return true;
            }
            this.pos++;
        }
    }

    private IOException zzuv(String str) throws IOException {
        int lineNumber = getLineNumber();
        int columnNumber = getColumnNumber();
        String path = getPath();
        throw new zzaqs(new StringBuilder((String.valueOf(str).length() + 45) + String.valueOf(path).length()).append(str).append(" at line ").append(lineNumber).append(" column ").append(columnNumber).append(" path ").append(path).toString());
    }

    public void beginArray() throws IOException {
        int i = this.brt;
        if (i == 0) {
            i = bD();
        }
        if (i == 3) {
            zzagn(1);
            this.brA[this.bry - 1] = 0;
            this.brt = 0;
            return;
        }
        String valueOf = String.valueOf(bq());
        int lineNumber = getLineNumber();
        int columnNumber = getColumnNumber();
        String path = getPath();
        throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 74) + String.valueOf(path).length()).append("Expected BEGIN_ARRAY but was ").append(valueOf).append(" at line ").append(lineNumber).append(" column ").append(columnNumber).append(" path ").append(path).toString());
    }

    public void beginObject() throws IOException {
        int i = this.brt;
        if (i == 0) {
            i = bD();
        }
        if (i == 1) {
            zzagn(3);
            this.brt = 0;
            return;
        }
        String valueOf = String.valueOf(bq());
        int lineNumber = getLineNumber();
        int columnNumber = getColumnNumber();
        String path = getPath();
        throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 75) + String.valueOf(path).length()).append("Expected BEGIN_OBJECT but was ").append(valueOf).append(" at line ").append(lineNumber).append(" column ").append(columnNumber).append(" path ").append(path).toString());
    }

    public zzaqq bq() throws IOException {
        int i = this.brt;
        if (i == 0) {
            i = bD();
        }
        switch (i) {
            case 1:
                return zzaqq.BEGIN_OBJECT;
            case 2:
                return zzaqq.END_OBJECT;
            case 3:
                return zzaqq.BEGIN_ARRAY;
            case 4:
                return zzaqq.END_ARRAY;
            case 5:
            case 6:
                return zzaqq.BOOLEAN;
            case 7:
                return zzaqq.NULL;
            case 8:
            case 9:
            case 10:
            case 11:
                return zzaqq.STRING;
            case 12:
            case 13:
            case 14:
                return zzaqq.NAME;
            case 15:
            case 16:
                return zzaqq.NUMBER;
            case 17:
                return zzaqq.END_DOCUMENT;
            default:
                throw new AssertionError();
        }
    }

    public void close() throws IOException {
        this.brt = 0;
        this.brx[0] = 8;
        this.bry = 1;
        this.in.close();
    }

    public void endArray() throws IOException {
        int i = this.brt;
        if (i == 0) {
            i = bD();
        }
        if (i == 4) {
            this.bry--;
            int[] iArr = this.brA;
            int i2 = this.bry - 1;
            iArr[i2] = iArr[i2] + 1;
            this.brt = 0;
            return;
        }
        String valueOf = String.valueOf(bq());
        int lineNumber = getLineNumber();
        int columnNumber = getColumnNumber();
        String path = getPath();
        throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 72) + String.valueOf(path).length()).append("Expected END_ARRAY but was ").append(valueOf).append(" at line ").append(lineNumber).append(" column ").append(columnNumber).append(" path ").append(path).toString());
    }

    public void endObject() throws IOException {
        int i = this.brt;
        if (i == 0) {
            i = bD();
        }
        if (i == 2) {
            this.bry--;
            this.brz[this.bry] = null;
            int[] iArr = this.brA;
            int i2 = this.bry - 1;
            iArr[i2] = iArr[i2] + 1;
            this.brt = 0;
            return;
        }
        String valueOf = String.valueOf(bq());
        int lineNumber = getLineNumber();
        int columnNumber = getColumnNumber();
        String path = getPath();
        throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 73) + String.valueOf(path).length()).append("Expected END_OBJECT but was ").append(valueOf).append(" at line ").append(lineNumber).append(" column ").append(columnNumber).append(" path ").append(path).toString());
    }

    public String getPath() {
        StringBuilder append = new StringBuilder().append('$');
        int i = this.bry;
        for (int i2 = 0; i2 < i; i2++) {
            switch (this.brx[i2]) {
                case 1:
                case 2:
                    append.append('[').append(this.brA[i2]).append(']');
                    break;
                case 3:
                case 4:
                case 5:
                    append.append('.');
                    if (this.brz[i2] == null) {
                        break;
                    }
                    append.append(this.brz[i2]);
                    break;
                default:
                    break;
            }
        }
        return append.toString();
    }

    public boolean hasNext() throws IOException {
        int i = this.brt;
        if (i == 0) {
            i = bD();
        }
        return (i == 2 || i == 4) ? false : true;
    }

    public final boolean isLenient() {
        return this.brp;
    }

    public boolean nextBoolean() throws IOException {
        int i = this.brt;
        if (i == 0) {
            i = bD();
        }
        if (i == 5) {
            this.brt = 0;
            int[] iArr = this.brA;
            i = this.bry - 1;
            iArr[i] = iArr[i] + 1;
            return true;
        } else if (i == 6) {
            this.brt = 0;
            int[] iArr2 = this.brA;
            r2 = this.bry - 1;
            iArr2[r2] = iArr2[r2] + 1;
            return false;
        } else {
            String valueOf = String.valueOf(bq());
            r2 = getLineNumber();
            int columnNumber = getColumnNumber();
            String path = getPath();
            throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 72) + String.valueOf(path).length()).append("Expected a boolean but was ").append(valueOf).append(" at line ").append(r2).append(" column ").append(columnNumber).append(" path ").append(path).toString());
        }
    }

    public double nextDouble() throws IOException {
        int i = this.brt;
        if (i == 0) {
            i = bD();
        }
        if (i == 15) {
            this.brt = 0;
            int[] iArr = this.brA;
            int i2 = this.bry - 1;
            iArr[i2] = iArr[i2] + 1;
            return (double) this.bru;
        }
        if (i == 16) {
            this.brw = new String(this.brq, this.pos, this.brv);
            this.pos += this.brv;
        } else if (i == 8 || i == 9) {
            this.brw = zzd(i == 8 ? '\'' : '\"');
        } else if (i == 10) {
            this.brw = bG();
        } else if (i != 11) {
            String valueOf = String.valueOf(bq());
            int lineNumber = getLineNumber();
            int columnNumber = getColumnNumber();
            String path = getPath();
            throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 71) + String.valueOf(path).length()).append("Expected a double but was ").append(valueOf).append(" at line ").append(lineNumber).append(" column ").append(columnNumber).append(" path ").append(path).toString());
        }
        this.brt = 11;
        double parseDouble = Double.parseDouble(this.brw);
        if (this.brp || !(Double.isNaN(parseDouble) || Double.isInfinite(parseDouble))) {
            this.brw = null;
            this.brt = 0;
            int[] iArr2 = this.brA;
            columnNumber = this.bry - 1;
            iArr2[columnNumber] = iArr2[columnNumber] + 1;
            return parseDouble;
        }
        columnNumber = getLineNumber();
        int columnNumber2 = getColumnNumber();
        String path2 = getPath();
        throw new zzaqs(new StringBuilder(String.valueOf(path2).length() + 102).append("JSON forbids NaN and infinities: ").append(parseDouble).append(" at line ").append(columnNumber).append(" column ").append(columnNumber2).append(" path ").append(path2).toString());
    }

    public int nextInt() throws IOException {
        int i = this.brt;
        if (i == 0) {
            i = bD();
        }
        int[] iArr;
        int i2;
        if (i == 15) {
            i = (int) this.bru;
            if (this.bru != ((long) i)) {
                long j = this.bru;
                int lineNumber = getLineNumber();
                int columnNumber = getColumnNumber();
                String path = getPath();
                throw new NumberFormatException(new StringBuilder(String.valueOf(path).length() + 89).append("Expected an int but was ").append(j).append(" at line ").append(lineNumber).append(" column ").append(columnNumber).append(" path ").append(path).toString());
            }
            this.brt = 0;
            iArr = this.brA;
            i2 = this.bry - 1;
            iArr[i2] = iArr[i2] + 1;
        } else {
            String valueOf;
            int columnNumber2;
            String path2;
            if (i == 16) {
                this.brw = new String(this.brq, this.pos, this.brv);
                this.pos += this.brv;
            } else if (i == 8 || i == 9) {
                this.brw = zzd(i == 8 ? '\'' : '\"');
                try {
                    i = Integer.parseInt(this.brw);
                    this.brt = 0;
                    iArr = this.brA;
                    i2 = this.bry - 1;
                    iArr[i2] = iArr[i2] + 1;
                } catch (NumberFormatException e) {
                }
            } else {
                valueOf = String.valueOf(bq());
                i2 = getLineNumber();
                columnNumber2 = getColumnNumber();
                path2 = getPath();
                throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 69) + String.valueOf(path2).length()).append("Expected an int but was ").append(valueOf).append(" at line ").append(i2).append(" column ").append(columnNumber2).append(" path ").append(path2).toString());
            }
            this.brt = 11;
            double parseDouble = Double.parseDouble(this.brw);
            i = (int) parseDouble;
            if (((double) i) != parseDouble) {
                valueOf = this.brw;
                i2 = getLineNumber();
                columnNumber2 = getColumnNumber();
                path2 = getPath();
                throw new NumberFormatException(new StringBuilder((String.valueOf(valueOf).length() + 69) + String.valueOf(path2).length()).append("Expected an int but was ").append(valueOf).append(" at line ").append(i2).append(" column ").append(columnNumber2).append(" path ").append(path2).toString());
            }
            this.brw = null;
            this.brt = 0;
            iArr = this.brA;
            i2 = this.bry - 1;
            iArr[i2] = iArr[i2] + 1;
        }
        return i;
    }

    public long nextLong() throws IOException {
        int i = this.brt;
        if (i == 0) {
            i = bD();
        }
        if (i == 15) {
            this.brt = 0;
            int[] iArr = this.brA;
            int i2 = this.bry - 1;
            iArr[i2] = iArr[i2] + 1;
            return this.bru;
        }
        long parseLong;
        int i3;
        if (i == 16) {
            this.brw = new String(this.brq, this.pos, this.brv);
            this.pos += this.brv;
        } else if (i == 8 || i == 9) {
            this.brw = zzd(i == 8 ? '\'' : '\"');
            try {
                parseLong = Long.parseLong(this.brw);
                this.brt = 0;
                int[] iArr2 = this.brA;
                i3 = this.bry - 1;
                iArr2[i3] = iArr2[i3] + 1;
                return parseLong;
            } catch (NumberFormatException e) {
            }
        } else {
            String valueOf = String.valueOf(bq());
            int lineNumber = getLineNumber();
            i3 = getColumnNumber();
            String path = getPath();
            throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 69) + String.valueOf(path).length()).append("Expected a long but was ").append(valueOf).append(" at line ").append(lineNumber).append(" column ").append(i3).append(" path ").append(path).toString());
        }
        this.brt = 11;
        double parseDouble = Double.parseDouble(this.brw);
        parseLong = (long) parseDouble;
        if (((double) parseLong) != parseDouble) {
            valueOf = this.brw;
            lineNumber = getLineNumber();
            i3 = getColumnNumber();
            path = getPath();
            throw new NumberFormatException(new StringBuilder((String.valueOf(valueOf).length() + 69) + String.valueOf(path).length()).append("Expected a long but was ").append(valueOf).append(" at line ").append(lineNumber).append(" column ").append(i3).append(" path ").append(path).toString());
        }
        this.brw = null;
        this.brt = 0;
        iArr2 = this.brA;
        i3 = this.bry - 1;
        iArr2[i3] = iArr2[i3] + 1;
        return parseLong;
    }

    public String nextName() throws IOException {
        String bG;
        int i = this.brt;
        if (i == 0) {
            i = bD();
        }
        if (i == 14) {
            bG = bG();
        } else if (i == 12) {
            bG = zzd('\'');
        } else if (i == 13) {
            bG = zzd('\"');
        } else {
            String valueOf = String.valueOf(bq());
            int lineNumber = getLineNumber();
            int columnNumber = getColumnNumber();
            String path = getPath();
            throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 69) + String.valueOf(path).length()).append("Expected a name but was ").append(valueOf).append(" at line ").append(lineNumber).append(" column ").append(columnNumber).append(" path ").append(path).toString());
        }
        this.brt = 0;
        this.brz[this.bry - 1] = bG;
        return bG;
    }

    public void nextNull() throws IOException {
        int i = this.brt;
        if (i == 0) {
            i = bD();
        }
        if (i == 7) {
            this.brt = 0;
            int[] iArr = this.brA;
            int i2 = this.bry - 1;
            iArr[i2] = iArr[i2] + 1;
            return;
        }
        String valueOf = String.valueOf(bq());
        int lineNumber = getLineNumber();
        int columnNumber = getColumnNumber();
        String path = getPath();
        throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 67) + String.valueOf(path).length()).append("Expected null but was ").append(valueOf).append(" at line ").append(lineNumber).append(" column ").append(columnNumber).append(" path ").append(path).toString());
    }

    public String nextString() throws IOException {
        String bG;
        int lineNumber;
        int i = this.brt;
        if (i == 0) {
            i = bD();
        }
        if (i == 10) {
            bG = bG();
        } else if (i == 8) {
            bG = zzd('\'');
        } else if (i == 9) {
            bG = zzd('\"');
        } else if (i == 11) {
            bG = this.brw;
            this.brw = null;
        } else if (i == 15) {
            bG = Long.toString(this.bru);
        } else if (i == 16) {
            bG = new String(this.brq, this.pos, this.brv);
            this.pos += this.brv;
        } else {
            String valueOf = String.valueOf(bq());
            lineNumber = getLineNumber();
            int columnNumber = getColumnNumber();
            String path = getPath();
            throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 71) + String.valueOf(path).length()).append("Expected a string but was ").append(valueOf).append(" at line ").append(lineNumber).append(" column ").append(columnNumber).append(" path ").append(path).toString());
        }
        this.brt = 0;
        int[] iArr = this.brA;
        lineNumber = this.bry - 1;
        iArr[lineNumber] = iArr[lineNumber] + 1;
        return bG;
    }

    public final void setLenient(boolean z) {
        this.brp = z;
    }

    public void skipValue() throws IOException {
        int i = 0;
        do {
            int i2 = this.brt;
            if (i2 == 0) {
                i2 = bD();
            }
            if (i2 == 3) {
                zzagn(1);
                i++;
            } else if (i2 == 1) {
                zzagn(3);
                i++;
            } else if (i2 == 4) {
                this.bry--;
                i--;
            } else if (i2 == 2) {
                this.bry--;
                i--;
            } else if (i2 == 14 || i2 == 10) {
                bH();
            } else if (i2 == 8 || i2 == 12) {
                zze('\'');
            } else if (i2 == 9 || i2 == 13) {
                zze('\"');
            } else if (i2 == 16) {
                this.pos += this.brv;
            }
            this.brt = 0;
        } while (i != 0);
        int[] iArr = this.brA;
        int i3 = this.bry - 1;
        iArr[i3] = iArr[i3] + 1;
        this.brz[this.bry - 1] = "null";
    }

    public String toString() {
        String valueOf = String.valueOf(getClass().getSimpleName());
        int lineNumber = getLineNumber();
        return new StringBuilder(String.valueOf(valueOf).length() + 39).append(valueOf).append(" at line ").append(lineNumber).append(" column ").append(getColumnNumber()).toString();
    }
}

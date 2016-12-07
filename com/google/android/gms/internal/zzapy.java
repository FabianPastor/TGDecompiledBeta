package com.google.android.gms.internal;

import android.support.v4.view.MotionEventCompat;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;

public class zzapy implements Closeable {
    private static final char[] bnX = ")]}'\n".toCharArray();
    private boolean bnY = false;
    private final char[] bnZ = new char[1024];
    private int boa = 0;
    private int bob = 0;
    private int boc = 0;
    private long bod;
    private int boe;
    private String bof;
    private int[] bog = new int[32];
    private int boh = 0;
    private String[] boi;
    private int[] boj;
    private final Reader in;
    private int limit = 0;
    private int pos = 0;

    static {
        zzapd.blQ = new zzapd() {
            public void zzi(zzapy com_google_android_gms_internal_zzapy) throws IOException {
                if (com_google_android_gms_internal_zzapy instanceof zzapo) {
                    ((zzapo) com_google_android_gms_internal_zzapy).bq();
                    return;
                }
                int zzag = com_google_android_gms_internal_zzapy.boc;
                if (zzag == 0) {
                    zzag = com_google_android_gms_internal_zzapy.bA();
                }
                if (zzag == 13) {
                    com_google_android_gms_internal_zzapy.boc = 9;
                } else if (zzag == 12) {
                    com_google_android_gms_internal_zzapy.boc = 8;
                } else if (zzag == 14) {
                    com_google_android_gms_internal_zzapy.boc = 10;
                } else {
                    String valueOf = String.valueOf(com_google_android_gms_internal_zzapy.bn());
                    int zzai = com_google_android_gms_internal_zzapy.getLineNumber();
                    int zzaj = com_google_android_gms_internal_zzapy.getColumnNumber();
                    String path = com_google_android_gms_internal_zzapy.getPath();
                    throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 70) + String.valueOf(path).length()).append("Expected a name but was ").append(valueOf).append(" ").append(" at line ").append(zzai).append(" column ").append(zzaj).append(" path ").append(path).toString());
                }
            }
        };
    }

    public zzapy(Reader reader) {
        int[] iArr = this.bog;
        int i = this.boh;
        this.boh = i + 1;
        iArr[i] = 6;
        this.boi = new String[32];
        this.boj = new int[32];
        if (reader == null) {
            throw new NullPointerException("in == null");
        }
        this.in = reader;
    }

    private int bA() throws IOException {
        int zzdg;
        int i = this.bog[this.boh - 1];
        if (i == 1) {
            this.bog[this.boh - 1] = 2;
        } else if (i == 2) {
            switch (zzdg(true)) {
                case 44:
                    break;
                case 59:
                    bF();
                    break;
                case 93:
                    this.boc = 4;
                    return 4;
                default:
                    throw zzuv("Unterminated array");
            }
        } else if (i == 3 || i == 5) {
            this.bog[this.boh - 1] = 4;
            if (i == 5) {
                switch (zzdg(true)) {
                    case 44:
                        break;
                    case 59:
                        bF();
                        break;
                    case 125:
                        this.boc = 2;
                        return 2;
                    default:
                        throw zzuv("Unterminated object");
                }
            }
            zzdg = zzdg(true);
            switch (zzdg) {
                case 34:
                    this.boc = 13;
                    return 13;
                case 39:
                    bF();
                    this.boc = 12;
                    return 12;
                case 125:
                    if (i != 5) {
                        this.boc = 2;
                        return 2;
                    }
                    throw zzuv("Expected name");
                default:
                    bF();
                    this.pos--;
                    if (zze((char) zzdg)) {
                        this.boc = 14;
                        return 14;
                    }
                    throw zzuv("Expected name");
            }
        } else if (i == 4) {
            this.bog[this.boh - 1] = 5;
            switch (zzdg(true)) {
                case 58:
                    break;
                case 61:
                    bF();
                    if ((this.pos < this.limit || zzagx(1)) && this.bnZ[this.pos] == '>') {
                        this.pos++;
                        break;
                    }
                default:
                    throw zzuv("Expected ':'");
            }
        } else if (i == 6) {
            if (this.bnY) {
                bI();
            }
            this.bog[this.boh - 1] = 7;
        } else if (i == 7) {
            if (zzdg(false) == -1) {
                this.boc = 17;
                return 17;
            }
            bF();
            this.pos--;
        } else if (i == 8) {
            throw new IllegalStateException("JsonReader is closed");
        }
        switch (zzdg(true)) {
            case 34:
                if (this.boh == 1) {
                    bF();
                }
                this.boc = 9;
                return 9;
            case 39:
                bF();
                this.boc = 8;
                return 8;
            case 44:
            case 59:
                break;
            case 91:
                this.boc = 3;
                return 3;
            case 93:
                if (i == 1) {
                    this.boc = 4;
                    return 4;
                }
                break;
            case 123:
                this.boc = 1;
                return 1;
            default:
                this.pos--;
                if (this.boh == 1) {
                    bF();
                }
                zzdg = bB();
                if (zzdg != 0) {
                    return zzdg;
                }
                zzdg = bC();
                if (zzdg != 0) {
                    return zzdg;
                }
                if (zze(this.bnZ[this.pos])) {
                    bF();
                    this.boc = 10;
                    return 10;
                }
                throw zzuv("Expected value");
        }
        if (i == 1 || i == 2) {
            bF();
            this.pos--;
            this.boc = 7;
            return 7;
        }
        throw zzuv("Unexpected value");
    }

    private int bB() throws IOException {
        String str;
        int i;
        char c = this.bnZ[this.pos];
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
            if (this.pos + i2 >= this.limit && !zzagx(i2 + 1)) {
                return 0;
            }
            char c2 = this.bnZ[this.pos + i2];
            if (c2 != str.charAt(i2) && c2 != r1.charAt(i2)) {
                return 0;
            }
            i2++;
        }
        if ((this.pos + length < this.limit || zzagx(length + 1)) && zze(this.bnZ[this.pos + length])) {
            return 0;
        }
        this.pos += length;
        this.boc = i;
        return i;
    }

    private int bC() throws IOException {
        char[] cArr = this.bnZ;
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
                if (zzagx(i4 + 1)) {
                    i6 = this.pos;
                    i5 = this.limit;
                } else if (i3 != 2 && i2 != 0 && (j != Long.MIN_VALUE || obj != null)) {
                    if (obj == null) {
                        j = -j;
                    }
                    this.bod = j;
                    this.pos += i4;
                    this.boc = 15;
                    return 15;
                } else if (i3 == 2 && i3 != 4 && i3 != 7) {
                    return 0;
                } else {
                    this.boe = i4;
                    this.boc = 16;
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
                    } else if (zze(c)) {
                        return 0;
                    }
                    break;
            }
            if (i3 != 2) {
            }
            if (i3 == 2) {
            }
            this.boe = i4;
            this.boc = 16;
            return 16;
            i4++;
            obj = obj2;
            i2 = i3;
            i3 = i;
        }
    }

    private String bD() throws IOException {
        StringBuilder stringBuilder = null;
        int i = 0;
        while (true) {
            String str;
            if (this.pos + i < this.limit) {
                switch (this.bnZ[this.pos + i]) {
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
                        bF();
                        break;
                    default:
                        i++;
                        continue;
                }
            } else if (i >= this.bnZ.length) {
                if (stringBuilder == null) {
                    stringBuilder = new StringBuilder();
                }
                stringBuilder.append(this.bnZ, this.pos, i);
                this.pos = i + this.pos;
                i = !zzagx(1) ? 0 : 0;
            } else if (zzagx(i + 1)) {
            }
            if (stringBuilder == null) {
                str = new String(this.bnZ, this.pos, i);
            } else {
                stringBuilder.append(this.bnZ, this.pos, i);
                str = stringBuilder.toString();
            }
            this.pos = i + this.pos;
            return str;
        }
    }

    private void bE() throws IOException {
        do {
            int i = 0;
            while (this.pos + i < this.limit) {
                switch (this.bnZ[this.pos + i]) {
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
                        bF();
                        break;
                    default:
                        i++;
                }
                this.pos = i + this.pos;
                return;
            }
            this.pos = i + this.pos;
        } while (zzagx(1));
    }

    private void bF() throws IOException {
        if (!this.bnY) {
            throw zzuv("Use JsonReader.setLenient(true) to accept malformed JSON");
        }
    }

    private void bG() throws IOException {
        char c;
        do {
            if (this.pos < this.limit || zzagx(1)) {
                char[] cArr = this.bnZ;
                int i = this.pos;
                this.pos = i + 1;
                c = cArr[i];
                if (c == '\n') {
                    this.boa++;
                    this.bob = this.pos;
                    return;
                }
            } else {
                return;
            }
        } while (c != '\r');
    }

    private char bH() throws IOException {
        if (this.pos != this.limit || zzagx(1)) {
            char[] cArr = this.bnZ;
            int i = this.pos;
            this.pos = i + 1;
            char c = cArr[i];
            switch (c) {
                case '\n':
                    this.boa++;
                    this.bob = this.pos;
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
                    if (this.pos + 4 <= this.limit || zzagx(4)) {
                        int i2 = this.pos;
                        int i3 = i2 + 4;
                        int i4 = i2;
                        c = '\u0000';
                        for (i = i4; i < i3; i++) {
                            char c2 = this.bnZ[i];
                            c = (char) (c << 4);
                            if (c2 >= '0' && c2 <= '9') {
                                c = (char) (c + (c2 - 48));
                            } else if (c2 >= 'a' && c2 <= 'f') {
                                c = (char) (c + ((c2 - 97) + 10));
                            } else if (c2 < 'A' || c2 > 'F') {
                                String str = "\\u";
                                String valueOf = String.valueOf(new String(this.bnZ, this.pos, 4));
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

    private void bI() throws IOException {
        zzdg(true);
        this.pos--;
        if (this.pos + bnX.length <= this.limit || zzagx(bnX.length)) {
            int i = 0;
            while (i < bnX.length) {
                if (this.bnZ[this.pos + i] == bnX[i]) {
                    i++;
                } else {
                    return;
                }
            }
            this.pos += bnX.length;
        }
    }

    private int getColumnNumber() {
        return (this.pos - this.bob) + 1;
    }

    private int getLineNumber() {
        return this.boa + 1;
    }

    private void zzagw(int i) {
        if (this.boh == this.bog.length) {
            Object obj = new int[(this.boh * 2)];
            Object obj2 = new int[(this.boh * 2)];
            Object obj3 = new String[(this.boh * 2)];
            System.arraycopy(this.bog, 0, obj, 0, this.boh);
            System.arraycopy(this.boj, 0, obj2, 0, this.boh);
            System.arraycopy(this.boi, 0, obj3, 0, this.boh);
            this.bog = obj;
            this.boj = obj2;
            this.boi = obj3;
        }
        int[] iArr = this.bog;
        int i2 = this.boh;
        this.boh = i2 + 1;
        iArr[i2] = i;
    }

    private boolean zzagx(int i) throws IOException {
        Object obj = this.bnZ;
        this.bob -= this.pos;
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
            if (this.boa == 0 && this.bob == 0 && this.limit > 0 && obj[0] == '﻿') {
                this.pos++;
                this.bob++;
                i++;
            }
        } while (this.limit < i);
        return true;
    }

    private int zzdg(boolean z) throws IOException {
        char[] cArr = this.bnZ;
        int i = this.pos;
        int i2 = this.limit;
        while (true) {
            int lineNumber;
            if (i == i2) {
                this.pos = i;
                if (zzagx(1)) {
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
                this.boa++;
                this.bob = lineNumber;
                i = lineNumber;
            } else if (c == ' ' || c == '\r') {
                i = lineNumber;
            } else if (c == '\t') {
                i = lineNumber;
            } else if (c == '/') {
                this.pos = lineNumber;
                if (lineNumber == i2) {
                    this.pos--;
                    boolean zzagx = zzagx(2);
                    this.pos++;
                    if (!zzagx) {
                        return c;
                    }
                }
                bF();
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
                        bG();
                        i = this.pos;
                        i2 = this.limit;
                        break;
                    default:
                        return c;
                }
            } else if (c == '#') {
                this.pos = lineNumber;
                bF();
                bG();
                i = this.pos;
                i2 = this.limit;
            } else {
                this.pos = lineNumber;
                return c;
            }
        }
    }

    private boolean zze(char c) throws IOException {
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
                bF();
                break;
            default:
                return true;
        }
        return false;
    }

    private String zzf(char c) throws IOException {
        char[] cArr = this.bnZ;
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
                    stringBuilder.append(bH());
                    i = this.pos;
                    i2 = this.limit;
                    i4 = i;
                } else if (c2 == '\n') {
                    this.boa++;
                    this.bob = i4;
                }
                i3 = i4;
            }
            stringBuilder.append(cArr, i, i3 - i);
            this.pos = i3;
        } while (zzagx(1));
        throw zzuv("Unterminated string");
    }

    private void zzg(char c) throws IOException {
        char[] cArr = this.bnZ;
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
                    bH();
                    i = this.pos;
                    i2 = this.limit;
                } else if (c2 == '\n') {
                    this.boa++;
                    this.bob = i;
                }
                i3 = i;
            }
            this.pos = i3;
        } while (zzagx(1));
        throw zzuv("Unterminated string");
    }

    private boolean zzuu(String str) throws IOException {
        while (true) {
            if (this.pos + str.length() > this.limit && !zzagx(str.length())) {
                return false;
            }
            if (this.bnZ[this.pos] == '\n') {
                this.boa++;
                this.bob = this.pos + 1;
            } else {
                int i = 0;
                while (i < str.length()) {
                    if (this.bnZ[this.pos + i] == str.charAt(i)) {
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
        throw new zzaqb(new StringBuilder((String.valueOf(str).length() + 45) + String.valueOf(path).length()).append(str).append(" at line ").append(lineNumber).append(" column ").append(columnNumber).append(" path ").append(path).toString());
    }

    public void beginArray() throws IOException {
        int i = this.boc;
        if (i == 0) {
            i = bA();
        }
        if (i == 3) {
            zzagw(1);
            this.boj[this.boh - 1] = 0;
            this.boc = 0;
            return;
        }
        String valueOf = String.valueOf(bn());
        int lineNumber = getLineNumber();
        int columnNumber = getColumnNumber();
        String path = getPath();
        throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 74) + String.valueOf(path).length()).append("Expected BEGIN_ARRAY but was ").append(valueOf).append(" at line ").append(lineNumber).append(" column ").append(columnNumber).append(" path ").append(path).toString());
    }

    public void beginObject() throws IOException {
        int i = this.boc;
        if (i == 0) {
            i = bA();
        }
        if (i == 1) {
            zzagw(3);
            this.boc = 0;
            return;
        }
        String valueOf = String.valueOf(bn());
        int lineNumber = getLineNumber();
        int columnNumber = getColumnNumber();
        String path = getPath();
        throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 75) + String.valueOf(path).length()).append("Expected BEGIN_OBJECT but was ").append(valueOf).append(" at line ").append(lineNumber).append(" column ").append(columnNumber).append(" path ").append(path).toString());
    }

    public zzapz bn() throws IOException {
        int i = this.boc;
        if (i == 0) {
            i = bA();
        }
        switch (i) {
            case 1:
                return zzapz.BEGIN_OBJECT;
            case 2:
                return zzapz.END_OBJECT;
            case 3:
                return zzapz.BEGIN_ARRAY;
            case 4:
                return zzapz.END_ARRAY;
            case 5:
            case 6:
                return zzapz.BOOLEAN;
            case 7:
                return zzapz.NULL;
            case 8:
            case 9:
            case 10:
            case 11:
                return zzapz.STRING;
            case 12:
            case 13:
            case 14:
                return zzapz.NAME;
            case 15:
            case 16:
                return zzapz.NUMBER;
            case 17:
                return zzapz.END_DOCUMENT;
            default:
                throw new AssertionError();
        }
    }

    public void close() throws IOException {
        this.boc = 0;
        this.bog[0] = 8;
        this.boh = 1;
        this.in.close();
    }

    public void endArray() throws IOException {
        int i = this.boc;
        if (i == 0) {
            i = bA();
        }
        if (i == 4) {
            this.boh--;
            int[] iArr = this.boj;
            int i2 = this.boh - 1;
            iArr[i2] = iArr[i2] + 1;
            this.boc = 0;
            return;
        }
        String valueOf = String.valueOf(bn());
        int lineNumber = getLineNumber();
        int columnNumber = getColumnNumber();
        String path = getPath();
        throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 72) + String.valueOf(path).length()).append("Expected END_ARRAY but was ").append(valueOf).append(" at line ").append(lineNumber).append(" column ").append(columnNumber).append(" path ").append(path).toString());
    }

    public void endObject() throws IOException {
        int i = this.boc;
        if (i == 0) {
            i = bA();
        }
        if (i == 2) {
            this.boh--;
            this.boi[this.boh] = null;
            int[] iArr = this.boj;
            int i2 = this.boh - 1;
            iArr[i2] = iArr[i2] + 1;
            this.boc = 0;
            return;
        }
        String valueOf = String.valueOf(bn());
        int lineNumber = getLineNumber();
        int columnNumber = getColumnNumber();
        String path = getPath();
        throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 73) + String.valueOf(path).length()).append("Expected END_OBJECT but was ").append(valueOf).append(" at line ").append(lineNumber).append(" column ").append(columnNumber).append(" path ").append(path).toString());
    }

    public String getPath() {
        StringBuilder append = new StringBuilder().append('$');
        int i = this.boh;
        for (int i2 = 0; i2 < i; i2++) {
            switch (this.bog[i2]) {
                case 1:
                case 2:
                    append.append('[').append(this.boj[i2]).append(']');
                    break;
                case 3:
                case 4:
                case 5:
                    append.append('.');
                    if (this.boi[i2] == null) {
                        break;
                    }
                    append.append(this.boi[i2]);
                    break;
                default:
                    break;
            }
        }
        return append.toString();
    }

    public boolean hasNext() throws IOException {
        int i = this.boc;
        if (i == 0) {
            i = bA();
        }
        return (i == 2 || i == 4) ? false : true;
    }

    public final boolean isLenient() {
        return this.bnY;
    }

    public boolean nextBoolean() throws IOException {
        int i = this.boc;
        if (i == 0) {
            i = bA();
        }
        if (i == 5) {
            this.boc = 0;
            int[] iArr = this.boj;
            i = this.boh - 1;
            iArr[i] = iArr[i] + 1;
            return true;
        } else if (i == 6) {
            this.boc = 0;
            int[] iArr2 = this.boj;
            r2 = this.boh - 1;
            iArr2[r2] = iArr2[r2] + 1;
            return false;
        } else {
            String valueOf = String.valueOf(bn());
            r2 = getLineNumber();
            int columnNumber = getColumnNumber();
            String path = getPath();
            throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 72) + String.valueOf(path).length()).append("Expected a boolean but was ").append(valueOf).append(" at line ").append(r2).append(" column ").append(columnNumber).append(" path ").append(path).toString());
        }
    }

    public double nextDouble() throws IOException {
        int i = this.boc;
        if (i == 0) {
            i = bA();
        }
        if (i == 15) {
            this.boc = 0;
            int[] iArr = this.boj;
            int i2 = this.boh - 1;
            iArr[i2] = iArr[i2] + 1;
            return (double) this.bod;
        }
        if (i == 16) {
            this.bof = new String(this.bnZ, this.pos, this.boe);
            this.pos += this.boe;
        } else if (i == 8 || i == 9) {
            this.bof = zzf(i == 8 ? '\'' : '\"');
        } else if (i == 10) {
            this.bof = bD();
        } else if (i != 11) {
            String valueOf = String.valueOf(bn());
            int lineNumber = getLineNumber();
            int columnNumber = getColumnNumber();
            String path = getPath();
            throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 71) + String.valueOf(path).length()).append("Expected a double but was ").append(valueOf).append(" at line ").append(lineNumber).append(" column ").append(columnNumber).append(" path ").append(path).toString());
        }
        this.boc = 11;
        double parseDouble = Double.parseDouble(this.bof);
        if (this.bnY || !(Double.isNaN(parseDouble) || Double.isInfinite(parseDouble))) {
            this.bof = null;
            this.boc = 0;
            int[] iArr2 = this.boj;
            columnNumber = this.boh - 1;
            iArr2[columnNumber] = iArr2[columnNumber] + 1;
            return parseDouble;
        }
        columnNumber = getLineNumber();
        int columnNumber2 = getColumnNumber();
        String path2 = getPath();
        throw new zzaqb(new StringBuilder(String.valueOf(path2).length() + 102).append("JSON forbids NaN and infinities: ").append(parseDouble).append(" at line ").append(columnNumber).append(" column ").append(columnNumber2).append(" path ").append(path2).toString());
    }

    public int nextInt() throws IOException {
        int i = this.boc;
        if (i == 0) {
            i = bA();
        }
        int[] iArr;
        int i2;
        if (i == 15) {
            i = (int) this.bod;
            if (this.bod != ((long) i)) {
                long j = this.bod;
                int lineNumber = getLineNumber();
                int columnNumber = getColumnNumber();
                String path = getPath();
                throw new NumberFormatException(new StringBuilder(String.valueOf(path).length() + 89).append("Expected an int but was ").append(j).append(" at line ").append(lineNumber).append(" column ").append(columnNumber).append(" path ").append(path).toString());
            }
            this.boc = 0;
            iArr = this.boj;
            i2 = this.boh - 1;
            iArr[i2] = iArr[i2] + 1;
        } else {
            String valueOf;
            int columnNumber2;
            String path2;
            if (i == 16) {
                this.bof = new String(this.bnZ, this.pos, this.boe);
                this.pos += this.boe;
            } else if (i == 8 || i == 9) {
                this.bof = zzf(i == 8 ? '\'' : '\"');
                try {
                    i = Integer.parseInt(this.bof);
                    this.boc = 0;
                    iArr = this.boj;
                    i2 = this.boh - 1;
                    iArr[i2] = iArr[i2] + 1;
                } catch (NumberFormatException e) {
                }
            } else {
                valueOf = String.valueOf(bn());
                i2 = getLineNumber();
                columnNumber2 = getColumnNumber();
                path2 = getPath();
                throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 69) + String.valueOf(path2).length()).append("Expected an int but was ").append(valueOf).append(" at line ").append(i2).append(" column ").append(columnNumber2).append(" path ").append(path2).toString());
            }
            this.boc = 11;
            double parseDouble = Double.parseDouble(this.bof);
            i = (int) parseDouble;
            if (((double) i) != parseDouble) {
                valueOf = this.bof;
                i2 = getLineNumber();
                columnNumber2 = getColumnNumber();
                path2 = getPath();
                throw new NumberFormatException(new StringBuilder((String.valueOf(valueOf).length() + 69) + String.valueOf(path2).length()).append("Expected an int but was ").append(valueOf).append(" at line ").append(i2).append(" column ").append(columnNumber2).append(" path ").append(path2).toString());
            }
            this.bof = null;
            this.boc = 0;
            iArr = this.boj;
            i2 = this.boh - 1;
            iArr[i2] = iArr[i2] + 1;
        }
        return i;
    }

    public long nextLong() throws IOException {
        int i = this.boc;
        if (i == 0) {
            i = bA();
        }
        if (i == 15) {
            this.boc = 0;
            int[] iArr = this.boj;
            int i2 = this.boh - 1;
            iArr[i2] = iArr[i2] + 1;
            return this.bod;
        }
        long parseLong;
        int i3;
        if (i == 16) {
            this.bof = new String(this.bnZ, this.pos, this.boe);
            this.pos += this.boe;
        } else if (i == 8 || i == 9) {
            this.bof = zzf(i == 8 ? '\'' : '\"');
            try {
                parseLong = Long.parseLong(this.bof);
                this.boc = 0;
                int[] iArr2 = this.boj;
                i3 = this.boh - 1;
                iArr2[i3] = iArr2[i3] + 1;
                return parseLong;
            } catch (NumberFormatException e) {
            }
        } else {
            String valueOf = String.valueOf(bn());
            int lineNumber = getLineNumber();
            i3 = getColumnNumber();
            String path = getPath();
            throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 69) + String.valueOf(path).length()).append("Expected a long but was ").append(valueOf).append(" at line ").append(lineNumber).append(" column ").append(i3).append(" path ").append(path).toString());
        }
        this.boc = 11;
        double parseDouble = Double.parseDouble(this.bof);
        parseLong = (long) parseDouble;
        if (((double) parseLong) != parseDouble) {
            valueOf = this.bof;
            lineNumber = getLineNumber();
            i3 = getColumnNumber();
            path = getPath();
            throw new NumberFormatException(new StringBuilder((String.valueOf(valueOf).length() + 69) + String.valueOf(path).length()).append("Expected a long but was ").append(valueOf).append(" at line ").append(lineNumber).append(" column ").append(i3).append(" path ").append(path).toString());
        }
        this.bof = null;
        this.boc = 0;
        iArr2 = this.boj;
        i3 = this.boh - 1;
        iArr2[i3] = iArr2[i3] + 1;
        return parseLong;
    }

    public String nextName() throws IOException {
        String bD;
        int i = this.boc;
        if (i == 0) {
            i = bA();
        }
        if (i == 14) {
            bD = bD();
        } else if (i == 12) {
            bD = zzf('\'');
        } else if (i == 13) {
            bD = zzf('\"');
        } else {
            String valueOf = String.valueOf(bn());
            int lineNumber = getLineNumber();
            int columnNumber = getColumnNumber();
            String path = getPath();
            throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 69) + String.valueOf(path).length()).append("Expected a name but was ").append(valueOf).append(" at line ").append(lineNumber).append(" column ").append(columnNumber).append(" path ").append(path).toString());
        }
        this.boc = 0;
        this.boi[this.boh - 1] = bD;
        return bD;
    }

    public void nextNull() throws IOException {
        int i = this.boc;
        if (i == 0) {
            i = bA();
        }
        if (i == 7) {
            this.boc = 0;
            int[] iArr = this.boj;
            int i2 = this.boh - 1;
            iArr[i2] = iArr[i2] + 1;
            return;
        }
        String valueOf = String.valueOf(bn());
        int lineNumber = getLineNumber();
        int columnNumber = getColumnNumber();
        String path = getPath();
        throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 67) + String.valueOf(path).length()).append("Expected null but was ").append(valueOf).append(" at line ").append(lineNumber).append(" column ").append(columnNumber).append(" path ").append(path).toString());
    }

    public String nextString() throws IOException {
        String bD;
        int lineNumber;
        int i = this.boc;
        if (i == 0) {
            i = bA();
        }
        if (i == 10) {
            bD = bD();
        } else if (i == 8) {
            bD = zzf('\'');
        } else if (i == 9) {
            bD = zzf('\"');
        } else if (i == 11) {
            bD = this.bof;
            this.bof = null;
        } else if (i == 15) {
            bD = Long.toString(this.bod);
        } else if (i == 16) {
            bD = new String(this.bnZ, this.pos, this.boe);
            this.pos += this.boe;
        } else {
            String valueOf = String.valueOf(bn());
            lineNumber = getLineNumber();
            int columnNumber = getColumnNumber();
            String path = getPath();
            throw new IllegalStateException(new StringBuilder((String.valueOf(valueOf).length() + 71) + String.valueOf(path).length()).append("Expected a string but was ").append(valueOf).append(" at line ").append(lineNumber).append(" column ").append(columnNumber).append(" path ").append(path).toString());
        }
        this.boc = 0;
        int[] iArr = this.boj;
        lineNumber = this.boh - 1;
        iArr[lineNumber] = iArr[lineNumber] + 1;
        return bD;
    }

    public final void setLenient(boolean z) {
        this.bnY = z;
    }

    public void skipValue() throws IOException {
        int i = 0;
        do {
            int i2 = this.boc;
            if (i2 == 0) {
                i2 = bA();
            }
            if (i2 == 3) {
                zzagw(1);
                i++;
            } else if (i2 == 1) {
                zzagw(3);
                i++;
            } else if (i2 == 4) {
                this.boh--;
                i--;
            } else if (i2 == 2) {
                this.boh--;
                i--;
            } else if (i2 == 14 || i2 == 10) {
                bE();
            } else if (i2 == 8 || i2 == 12) {
                zzg('\'');
            } else if (i2 == 9 || i2 == 13) {
                zzg('\"');
            } else if (i2 == 16) {
                this.pos += this.boe;
            }
            this.boc = 0;
        } while (i != 0);
        int[] iArr = this.boj;
        int i3 = this.boh - 1;
        iArr[i3] = iArr[i3] + 1;
        this.boi[this.boh - 1] = "null";
    }

    public String toString() {
        String valueOf = String.valueOf(getClass().getSimpleName());
        int lineNumber = getLineNumber();
        return new StringBuilder(String.valueOf(valueOf).length() + 39).append(valueOf).append(" at line ").append(lineNumber).append(" column ").append(getColumnNumber()).toString();
    }
}

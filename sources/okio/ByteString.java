package okio;

import java.io.Serializable;
import java.util.Arrays;

public class ByteString implements Serializable, Comparable<ByteString> {
    public static final ByteString EMPTY = of(new byte[0]);
    static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    final byte[] data;
    transient int hashCode;
    transient String utf8;

    ByteString(byte[] bArr) {
        this.data = bArr;
    }

    public static ByteString of(byte... bArr) {
        if (bArr != null) {
            return new ByteString((byte[]) bArr.clone());
        }
        throw new IllegalArgumentException("data == null");
    }

    public static ByteString encodeUtf8(String str) {
        if (str != null) {
            ByteString byteString = new ByteString(str.getBytes(Util.UTF_8));
            byteString.utf8 = str;
            return byteString;
        }
        throw new IllegalArgumentException("s == null");
    }

    public String utf8() {
        String str = this.utf8;
        if (str != null) {
            return str;
        }
        str = new String(this.data, Util.UTF_8);
        this.utf8 = str;
        return str;
    }

    public String hex() {
        byte[] bArr = this.data;
        char[] cArr = new char[(bArr.length * 2)];
        int i = 0;
        for (byte b : bArr) {
            int i2 = i + 1;
            char[] cArr2 = HEX_DIGITS;
            cArr[i] = cArr2[(b >> 4) & 15];
            i = i2 + 1;
            cArr[i2] = cArr2[b & 15];
        }
        return new String(cArr);
    }

    public ByteString substring(int i, int i2) {
        if (i >= 0) {
            byte[] bArr = this.data;
            if (i2 <= bArr.length) {
                int i3 = i2 - i;
                if (i3 < 0) {
                    throw new IllegalArgumentException("endIndex < beginIndex");
                } else if (i == 0 && i2 == bArr.length) {
                    return this;
                } else {
                    byte[] bArr2 = new byte[i3];
                    System.arraycopy(this.data, i, bArr2, 0, i3);
                    return new ByteString(bArr2);
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("endIndex > length(");
            stringBuilder.append(this.data.length);
            stringBuilder.append(")");
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        throw new IllegalArgumentException("beginIndex < 0");
    }

    public byte getByte(int i) {
        return this.data[i];
    }

    public int size() {
        return this.data.length;
    }

    /* Access modifiers changed, original: 0000 */
    public byte[] internalArray() {
        return this.data;
    }

    public boolean rangeEquals(int i, ByteString byteString, int i2, int i3) {
        return byteString.rangeEquals(i2, this.data, i, i3);
    }

    public boolean rangeEquals(int i, byte[] bArr, int i2, int i3) {
        if (i >= 0) {
            byte[] bArr2 = this.data;
            if (i <= bArr2.length - i3 && i2 >= 0 && i2 <= bArr.length - i3 && Util.arrayRangeEquals(bArr2, i, bArr, i2, i3)) {
                return true;
            }
        }
        return false;
    }

    public final boolean startsWith(ByteString byteString) {
        return rangeEquals(0, byteString, 0, byteString.size());
    }

    /* JADX WARNING: Missing block: B:8:0x0019, code skipped:
            if (r6.rangeEquals(0, r3, 0, r3.length) != false) goto L_0x001d;
     */
    public boolean equals(java.lang.Object r6) {
        /*
        r5 = this;
        r0 = 1;
        if (r6 != r5) goto L_0x0004;
    L_0x0003:
        return r0;
    L_0x0004:
        r1 = r6 instanceof okio.ByteString;
        r2 = 0;
        if (r1 == 0) goto L_0x001c;
    L_0x0009:
        r6 = (okio.ByteString) r6;
        r1 = r6.size();
        r3 = r5.data;
        r4 = r3.length;
        if (r1 != r4) goto L_0x001c;
    L_0x0014:
        r1 = r3.length;
        r6 = r6.rangeEquals(r2, r3, r2, r1);
        if (r6 == 0) goto L_0x001c;
    L_0x001b:
        goto L_0x001d;
    L_0x001c:
        r0 = 0;
    L_0x001d:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.ByteString.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        int i = this.hashCode;
        if (i != 0) {
            return i;
        }
        i = Arrays.hashCode(this.data);
        this.hashCode = i;
        return i;
    }

    public int compareTo(ByteString byteString) {
        int size = size();
        int size2 = byteString.size();
        int min = Math.min(size, size2);
        int i = 0;
        while (true) {
            int i2 = -1;
            if (i < min) {
                int i3 = getByte(i) & 255;
                int i4 = byteString.getByte(i) & 255;
                if (i3 == i4) {
                    i++;
                } else {
                    if (i3 >= i4) {
                        i2 = 1;
                    }
                    return i2;
                }
            } else if (size == size2) {
                return 0;
            } else {
                if (size >= size2) {
                    i2 = 1;
                }
                return i2;
            }
        }
    }

    public String toString() {
        if (this.data.length == 0) {
            return "[size=0]";
        }
        String utf8 = utf8();
        int codePointIndexToCharIndex = codePointIndexToCharIndex(utf8, 64);
        String str = "…]";
        String str2 = "[size=";
        String str3 = "]";
        StringBuilder stringBuilder;
        if (codePointIndexToCharIndex == -1) {
            if (this.data.length <= 64) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("[hex=");
                stringBuilder.append(hex());
                stringBuilder.append(str3);
                utf8 = stringBuilder.toString();
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str2);
                stringBuilder.append(this.data.length);
                stringBuilder.append(" hex=");
                stringBuilder.append(substring(0, 64).hex());
                stringBuilder.append(str);
                utf8 = stringBuilder.toString();
            }
            return utf8;
        }
        String replace = utf8.substring(0, codePointIndexToCharIndex).replace("\\", "\\\\").replace("\n", "\\n").replace("\r", "\\r");
        if (codePointIndexToCharIndex < utf8.length()) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(str2);
            stringBuilder.append(this.data.length);
            stringBuilder.append(" text=");
            stringBuilder.append(replace);
            stringBuilder.append(str);
            utf8 = stringBuilder.toString();
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append("[text=");
            stringBuilder.append(replace);
            stringBuilder.append(str3);
            utf8 = stringBuilder.toString();
        }
        return utf8;
    }

    static int codePointIndexToCharIndex(String str, int i) {
        int length = str.length();
        int i2 = 0;
        int i3 = 0;
        while (i2 < length) {
            if (i3 == i) {
                return i2;
            }
            int codePointAt = str.codePointAt(i2);
            if ((Character.isISOControl(codePointAt) && codePointAt != 10 && codePointAt != 13) || codePointAt == 65533) {
                return -1;
            }
            i3++;
            i2 += Character.charCount(codePointAt);
        }
        return str.length();
    }
}

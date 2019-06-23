package okio;

import java.util.Arrays;

final class SegmentedByteString extends ByteString {
    final transient int[] directory;
    final transient byte[][] segments;

    SegmentedByteString(Buffer buffer, int i) {
        super(null);
        Util.checkOffsetAndCount(buffer.size, 0, (long) i);
        int i2 = 0;
        Segment segment = buffer.head;
        int i3 = 0;
        int i4 = 0;
        while (i3 < i) {
            int i5 = segment.limit;
            int i6 = segment.pos;
            if (i5 != i6) {
                i3 += i5 - i6;
                i4++;
                segment = segment.next;
            } else {
                throw new AssertionError("s.limit == s.pos");
            }
        }
        this.segments = new byte[i4][];
        this.directory = new int[(i4 * 2)];
        Segment segment2 = buffer.head;
        int i7 = 0;
        while (i2 < i) {
            this.segments[i7] = segment2.data;
            i2 += segment2.limit - segment2.pos;
            if (i2 > i) {
                i2 = i;
            }
            int[] iArr = this.directory;
            iArr[i7] = i2;
            iArr[this.segments.length + i7] = segment2.pos;
            segment2.shared = true;
            i7++;
            segment2 = segment2.next;
        }
    }

    public String utf8() {
        return toByteString().utf8();
    }

    public String hex() {
        return toByteString().hex();
    }

    public ByteString substring(int i, int i2) {
        return toByteString().substring(i, i2);
    }

    public byte getByte(int i) {
        int i2;
        Util.checkOffsetAndCount((long) this.directory[this.segments.length - 1], (long) i, 1);
        int segment = segment(i);
        if (segment == 0) {
            i2 = 0;
        } else {
            i2 = this.directory[segment - 1];
        }
        int[] iArr = this.directory;
        byte[][] bArr = this.segments;
        return bArr[segment][(i - i2) + iArr[bArr.length + segment]];
    }

    private int segment(int i) {
        i = Arrays.binarySearch(this.directory, 0, this.segments.length, i + 1);
        return i >= 0 ? i : i ^ -1;
    }

    public int size() {
        return this.directory[this.segments.length - 1];
    }

    public byte[] toByteArray() {
        int[] iArr = this.directory;
        byte[][] bArr = this.segments;
        byte[] bArr2 = new byte[iArr[bArr.length - 1]];
        int length = bArr.length;
        int i = 0;
        int i2 = 0;
        while (i < length) {
            int[] iArr2 = this.directory;
            int i3 = iArr2[length + i];
            int i4 = iArr2[i];
            System.arraycopy(this.segments[i], i3, bArr2, i2, i4 - i2);
            i++;
            i2 = i4;
        }
        return bArr2;
    }

    public boolean rangeEquals(int i, ByteString byteString, int i2, int i3) {
        if (i < 0 || i > size() - i3) {
            return false;
        }
        int segment = segment(i);
        while (i3 > 0) {
            int i4;
            if (segment == 0) {
                i4 = 0;
            } else {
                i4 = this.directory[segment - 1];
            }
            int min = Math.min(i3, ((this.directory[segment] - i4) + i4) - i);
            int[] iArr = this.directory;
            byte[][] bArr = this.segments;
            if (!byteString.rangeEquals(i2, bArr[segment], (i - i4) + iArr[bArr.length + segment], min)) {
                return false;
            }
            i += min;
            i2 += min;
            i3 -= min;
            segment++;
        }
        return true;
    }

    public boolean rangeEquals(int i, byte[] bArr, int i2, int i3) {
        if (i < 0 || i > size() - i3 || i2 < 0 || i2 > bArr.length - i3) {
            return false;
        }
        int segment = segment(i);
        while (i3 > 0) {
            int i4;
            if (segment == 0) {
                i4 = 0;
            } else {
                i4 = this.directory[segment - 1];
            }
            int min = Math.min(i3, ((this.directory[segment] - i4) + i4) - i);
            int[] iArr = this.directory;
            byte[][] bArr2 = this.segments;
            if (!Util.arrayRangeEquals(bArr2[segment], (i - i4) + iArr[bArr2.length + segment], bArr, i2, min)) {
                return false;
            }
            i += min;
            i2 += min;
            i3 -= min;
            segment++;
        }
        return true;
    }

    private ByteString toByteString() {
        return new ByteString(toByteArray());
    }

    /* Access modifiers changed, original: 0000 */
    public byte[] internalArray() {
        return toByteArray();
    }

    /* JADX WARNING: Missing block: B:8:0x001d, code skipped:
            if (rangeEquals(0, r5, 0, size()) != false) goto L_0x0021;
     */
    public boolean equals(java.lang.Object r5) {
        /*
        r4 = this;
        r0 = 1;
        if (r5 != r4) goto L_0x0004;
    L_0x0003:
        return r0;
    L_0x0004:
        r1 = r5 instanceof okio.ByteString;
        r2 = 0;
        if (r1 == 0) goto L_0x0020;
    L_0x0009:
        r5 = (okio.ByteString) r5;
        r1 = r5.size();
        r3 = r4.size();
        if (r1 != r3) goto L_0x0020;
    L_0x0015:
        r1 = r4.size();
        r5 = r4.rangeEquals(r2, r5, r2, r1);
        if (r5 == 0) goto L_0x0020;
    L_0x001f:
        goto L_0x0021;
    L_0x0020:
        r0 = 0;
    L_0x0021:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.SegmentedByteString.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        int i = this.hashCode;
        if (i != 0) {
            return i;
        }
        i = this.segments.length;
        int i2 = 0;
        int i3 = 1;
        int i4 = 0;
        while (i2 < i) {
            byte[] bArr = this.segments[i2];
            int[] iArr = this.directory;
            int i5 = iArr[i + i2];
            int i6 = iArr[i2];
            i4 = (i6 - i4) + i5;
            while (i5 < i4) {
                i3 = (i3 * 31) + bArr[i5];
                i5++;
            }
            i2++;
            i4 = i6;
        }
        this.hashCode = i3;
        return i3;
    }

    public String toString() {
        return toByteString().toString();
    }
}

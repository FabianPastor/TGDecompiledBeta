package okio;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.charset.Charset;
import java.util.AbstractList;

public final class Buffer implements BufferedSource, BufferedSink, Cloneable, ByteChannel {
    private static final byte[] DIGITS = new byte[]{(byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102};
    Segment head;
    long size;

    public void close() {
    }

    public void flush() {
    }

    public Buffer getBuffer() {
        return this;
    }

    public boolean isOpen() {
        return true;
    }

    public final long size() {
        return this.size;
    }

    public boolean exhausted() {
        return this.size == 0;
    }

    public boolean request(long j) {
        return this.size >= j;
    }

    public byte readByte() {
        long j = this.size;
        if (j != 0) {
            Segment segment = this.head;
            int i = segment.pos;
            int i2 = segment.limit;
            int i3 = i + 1;
            byte b = segment.data[i];
            this.size = j - 1;
            if (i3 == i2) {
                this.head = segment.pop();
                SegmentPool.recycle(segment);
            } else {
                segment.pos = i3;
            }
            return b;
        }
        throw new IllegalStateException("size == 0");
    }

    public final byte getByte(long j) {
        Util.checkOffsetAndCount(this.size, j, 1);
        long j2 = this.size;
        Segment segment;
        int i;
        int i2;
        if (j2 - j > j) {
            segment = this.head;
            while (true) {
                i = segment.limit;
                i2 = segment.pos;
                long j3 = (long) (i - i2);
                if (j < j3) {
                    return segment.data[i2 + ((int) j)];
                }
                j -= j3;
                segment = segment.next;
            }
        } else {
            j -= j2;
            segment = this.head;
            do {
                segment = segment.prev;
                i = segment.limit;
                i2 = segment.pos;
                j += (long) (i - i2);
            } while (j < 0);
            return segment.data[i2 + ((int) j)];
        }
    }

    public int readInt() {
        long j = this.size;
        if (j >= 4) {
            Segment segment = this.head;
            int i = segment.pos;
            int i2 = segment.limit;
            if (i2 - i < 4) {
                return ((((readByte() & 255) << 24) | ((readByte() & 255) << 16)) | ((readByte() & 255) << 8)) | (readByte() & 255);
            }
            byte[] bArr = segment.data;
            int i3 = i + 1;
            int i4 = i3 + 1;
            i = ((bArr[i] & 255) << 24) | ((bArr[i3] & 255) << 16);
            i3 = i4 + 1;
            i |= (bArr[i4] & 255) << 8;
            i4 = i3 + 1;
            i |= bArr[i3] & 255;
            this.size = j - 4;
            if (i4 == i2) {
                this.head = segment.pop();
                SegmentPool.recycle(segment);
            } else {
                segment.pos = i4;
            }
            return i;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("size < 4: ");
        stringBuilder.append(this.size);
        throw new IllegalStateException(stringBuilder.toString());
    }

    public ByteString readByteString() {
        return new ByteString(readByteArray());
    }

    public int select(Options options) {
        int selectPrefix = selectPrefix(options, false);
        if (selectPrefix == -1) {
            return -1;
        }
        try {
            skip((long) options.byteStrings[selectPrefix].size());
            return selectPrefix;
        } catch (EOFException unused) {
            throw new AssertionError();
        }
    }

    /* Access modifiers changed, original: 0000 */
    public int selectPrefix(Options options, boolean z) {
        AbstractList abstractList = options;
        Segment segment = this.head;
        int i = -2;
        if (segment != null) {
            byte[] bArr = segment.data;
            int i2 = segment.pos;
            int i3 = segment.limit;
            int[] iArr = abstractList.trie;
            int i4 = i2;
            int i5 = i3;
            int i6 = -1;
            Segment segment2 = segment;
            byte[] bArr2 = bArr;
            int i7 = 0;
            loop0:
            while (true) {
                int i8 = i7 + 1;
                i7 = iArr[i7];
                int i9 = i8 + 1;
                i8 = iArr[i8];
                if (i8 != -1) {
                    i6 = i8;
                }
                if (segment2 == null) {
                    break;
                }
                int i10;
                int i11;
                if (i7 < 0) {
                    i10 = i9 + (i7 * -1);
                    while (true) {
                        i7 = i4 + 1;
                        i11 = i9 + 1;
                        if ((bArr2[i4] & 255) != iArr[i9]) {
                            return i6;
                        }
                        Object obj = i11 == i10 ? 1 : null;
                        if (i7 == i5) {
                            Segment segment3 = segment2.next;
                            i2 = segment3.pos;
                            bArr2 = segment3.data;
                            i5 = segment3.limit;
                            if (segment3 != segment) {
                                int i12 = i2;
                                segment2 = segment3;
                                i7 = i12;
                            } else if (obj == null) {
                                break loop0;
                            } else {
                                i7 = i2;
                                segment2 = null;
                            }
                        }
                        if (obj != null) {
                            i4 = iArr[i11];
                            break;
                        }
                        i4 = i7;
                        i9 = i11;
                    }
                } else {
                    i10 = i4 + 1;
                    i4 = bArr2[i4] & 255;
                    i11 = i9 + i7;
                    while (i9 != i11) {
                        if (i4 == iArr[i9]) {
                            i4 = iArr[i9 + i7];
                            if (i10 == i5) {
                                Segment segment4 = segment2.next;
                                i7 = segment4.pos;
                                byte[] bArr3 = segment4.data;
                                i3 = segment4.limit;
                                if (segment4 == segment) {
                                    i5 = i3;
                                    bArr2 = bArr3;
                                    segment2 = null;
                                } else {
                                    i5 = i3;
                                    bArr2 = bArr3;
                                    segment2 = segment4;
                                }
                            } else {
                                i7 = i10;
                            }
                        } else {
                            i9++;
                        }
                    }
                    return i6;
                }
                if (i4 >= 0) {
                    return i4;
                }
                i = -i4;
                i4 = i7;
                i7 = i;
                i = -2;
            }
            return z ? i : i6;
        } else if (z) {
            return -2;
        } else {
            return abstractList.indexOf(ByteString.EMPTY);
        }
    }

    public String readUtf8() {
        try {
            return readString(this.size, Util.UTF_8);
        } catch (EOFException e) {
            throw new AssertionError(e);
        }
    }

    public String readUtf8(long j) throws EOFException {
        return readString(j, Util.UTF_8);
    }

    public String readString(long j, Charset charset) throws EOFException {
        Util.checkOffsetAndCount(this.size, 0, j);
        if (charset == null) {
            throw new IllegalArgumentException("charset == null");
        } else if (j > 2147483647L) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("byteCount > Integer.MAX_VALUE: ");
            stringBuilder.append(j);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (j == 0) {
            return "";
        } else {
            Segment segment = this.head;
            int i = segment.pos;
            if (((long) i) + j > ((long) segment.limit)) {
                return new String(readByteArray(j), charset);
            }
            String str = new String(segment.data, i, (int) j, charset);
            segment.pos = (int) (((long) segment.pos) + j);
            this.size -= j;
            if (segment.pos == segment.limit) {
                this.head = segment.pop();
                SegmentPool.recycle(segment);
            }
            return str;
        }
    }

    public byte[] readByteArray() {
        try {
            return readByteArray(this.size);
        } catch (EOFException e) {
            throw new AssertionError(e);
        }
    }

    public byte[] readByteArray(long j) throws EOFException {
        Util.checkOffsetAndCount(this.size, 0, j);
        if (j <= 2147483647L) {
            byte[] bArr = new byte[((int) j)];
            readFully(bArr);
            return bArr;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("byteCount > Integer.MAX_VALUE: ");
        stringBuilder.append(j);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public void readFully(byte[] bArr) throws EOFException {
        int i = 0;
        while (i < bArr.length) {
            int read = read(bArr, i, bArr.length - i);
            if (read != -1) {
                i += read;
            } else {
                throw new EOFException();
            }
        }
    }

    public int read(byte[] bArr, int i, int i2) {
        Util.checkOffsetAndCount((long) bArr.length, (long) i, (long) i2);
        Segment segment = this.head;
        if (segment == null) {
            return -1;
        }
        i2 = Math.min(i2, segment.limit - segment.pos);
        System.arraycopy(segment.data, segment.pos, bArr, i, i2);
        segment.pos += i2;
        this.size -= (long) i2;
        if (segment.pos == segment.limit) {
            this.head = segment.pop();
            SegmentPool.recycle(segment);
        }
        return i2;
    }

    public int read(ByteBuffer byteBuffer) throws IOException {
        Segment segment = this.head;
        if (segment == null) {
            return -1;
        }
        int min = Math.min(byteBuffer.remaining(), segment.limit - segment.pos);
        byteBuffer.put(segment.data, segment.pos, min);
        segment.pos += min;
        this.size -= (long) min;
        if (segment.pos == segment.limit) {
            this.head = segment.pop();
            SegmentPool.recycle(segment);
        }
        return min;
    }

    public final void clear() {
        try {
            skip(this.size);
        } catch (EOFException e) {
            throw new AssertionError(e);
        }
    }

    public void skip(long j) throws EOFException {
        while (j > 0) {
            Segment segment = this.head;
            if (segment != null) {
                int min = (int) Math.min(j, (long) (segment.limit - segment.pos));
                long j2 = (long) min;
                this.size -= j2;
                j -= j2;
                segment = this.head;
                segment.pos += min;
                if (segment.pos == segment.limit) {
                    this.head = segment.pop();
                    SegmentPool.recycle(segment);
                }
            } else {
                throw new EOFException();
            }
        }
    }

    public Buffer writeUtf8(String str) {
        writeUtf8(str, 0, str.length());
        return this;
    }

    public Buffer writeUtf8(String str, int i, int i2) {
        StringBuilder stringBuilder;
        if (str == null) {
            throw new IllegalArgumentException("string == null");
        } else if (i < 0) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("beginIndex < 0: ");
            stringBuilder2.append(i);
            throw new IllegalArgumentException(stringBuilder2.toString());
        } else if (i2 < i) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("endIndex < beginIndex: ");
            stringBuilder.append(i2);
            stringBuilder.append(" < ");
            stringBuilder.append(i);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else if (i2 <= str.length()) {
            while (i < i2) {
                char charAt = str.charAt(i);
                int i3;
                int min;
                int i4;
                if (charAt < 128) {
                    Segment writableSegment = writableSegment(1);
                    byte[] bArr = writableSegment.data;
                    i3 = writableSegment.limit - i;
                    min = Math.min(i2, 8192 - i3);
                    int i5 = i + 1;
                    bArr[i + i3] = (byte) charAt;
                    while (i5 < min) {
                        char charAt2 = str.charAt(i5);
                        if (charAt2 >= 128) {
                            break;
                        }
                        i4 = i5 + 1;
                        bArr[i5 + i3] = (byte) charAt2;
                        i5 = i4;
                    }
                    i3 += i5;
                    i = writableSegment.limit;
                    i3 -= i;
                    writableSegment.limit = i + i3;
                    this.size += (long) i3;
                    i = i5;
                } else {
                    if (charAt < 2048) {
                        writeByte((charAt >> 6) | 192);
                        writeByte((charAt & 63) | 128);
                    } else if (charAt < 55296 || charAt > 57343) {
                        writeByte((charAt >> 12) | 224);
                        writeByte(((charAt >> 6) & 63) | 128);
                        writeByte((charAt & 63) | 128);
                    } else {
                        i3 = i + 1;
                        if (i3 < i2) {
                            min = str.charAt(i3);
                        } else {
                            min = 0;
                        }
                        if (charAt > 56319 || min < 56320 || min > 57343) {
                            writeByte(63);
                            i = i3;
                        } else {
                            i4 = (((charAt & -55297) << 10) | (-56321 & min)) + 65536;
                            writeByte((i4 >> 18) | 240);
                            writeByte(((i4 >> 12) & 63) | 128);
                            writeByte(((i4 >> 6) & 63) | 128);
                            writeByte((i4 & 63) | 128);
                            i += 2;
                        }
                    }
                    i++;
                }
            }
            return this;
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append("endIndex > string.length: ");
            stringBuilder.append(i2);
            stringBuilder.append(" > ");
            stringBuilder.append(str.length());
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public int write(ByteBuffer byteBuffer) throws IOException {
        if (byteBuffer != null) {
            int remaining = byteBuffer.remaining();
            int i = remaining;
            while (i > 0) {
                Segment writableSegment = writableSegment(1);
                int min = Math.min(i, 8192 - writableSegment.limit);
                byteBuffer.get(writableSegment.data, writableSegment.limit, min);
                i -= min;
                writableSegment.limit += min;
            }
            this.size += (long) remaining;
            return remaining;
        }
        throw new IllegalArgumentException("source == null");
    }

    public Buffer writeByte(int i) {
        Segment writableSegment = writableSegment(1);
        byte[] bArr = writableSegment.data;
        int i2 = writableSegment.limit;
        writableSegment.limit = i2 + 1;
        bArr[i2] = (byte) i;
        this.size++;
        return this;
    }

    public Buffer writeInt(int i) {
        Segment writableSegment = writableSegment(4);
        byte[] bArr = writableSegment.data;
        int i2 = writableSegment.limit;
        int i3 = i2 + 1;
        bArr[i2] = (byte) ((i >>> 24) & 255);
        i2 = i3 + 1;
        bArr[i3] = (byte) ((i >>> 16) & 255);
        i3 = i2 + 1;
        bArr[i2] = (byte) ((i >>> 8) & 255);
        i2 = i3 + 1;
        bArr[i3] = (byte) (i & 255);
        writableSegment.limit = i2;
        this.size += 4;
        return this;
    }

    /* Access modifiers changed, original: 0000 */
    public Segment writableSegment(int i) {
        if (i < 1 || i > 8192) {
            throw new IllegalArgumentException();
        }
        Segment segment = this.head;
        Segment segment2;
        if (segment == null) {
            this.head = SegmentPool.take();
            segment2 = this.head;
            segment2.prev = segment2;
            segment2.next = segment2;
            return segment2;
        }
        segment = segment.prev;
        if (segment.limit + i > 8192 || !segment.owner) {
            segment2 = SegmentPool.take();
            segment.push(segment2);
        } else {
            segment2 = segment;
        }
        return segment2;
    }

    public void write(Buffer buffer, long j) {
        if (buffer == null) {
            throw new IllegalArgumentException("source == null");
        } else if (buffer != this) {
            Util.checkOffsetAndCount(buffer.size, 0, j);
            while (j > 0) {
                long j2;
                Segment segment = buffer.head;
                if (j < ((long) (segment.limit - segment.pos))) {
                    segment = this.head;
                    segment = segment != null ? segment.prev : null;
                    if (segment != null && segment.owner) {
                        int i;
                        j2 = ((long) segment.limit) + j;
                        if (segment.shared) {
                            i = 0;
                        } else {
                            i = segment.pos;
                        }
                        if (j2 - ((long) i) <= 8192) {
                            buffer.head.writeTo(segment, (int) j);
                            buffer.size -= j;
                            this.size += j;
                            return;
                        }
                    }
                    buffer.head = buffer.head.split((int) j);
                }
                segment = buffer.head;
                j2 = (long) (segment.limit - segment.pos);
                buffer.head = segment.pop();
                Segment segment2 = this.head;
                if (segment2 == null) {
                    this.head = segment;
                    segment = this.head;
                    segment.prev = segment;
                    segment.next = segment;
                } else {
                    segment2.prev.push(segment);
                    segment.compact();
                }
                buffer.size -= j2;
                this.size += j2;
                j -= j2;
            }
        } else {
            throw new IllegalArgumentException("source == this");
        }
    }

    public long read(Buffer buffer, long j) {
        if (buffer == null) {
            throw new IllegalArgumentException("sink == null");
        } else if (j >= 0) {
            long j2 = this.size;
            if (j2 == 0) {
                return -1;
            }
            if (j > j2) {
                j = j2;
            }
            buffer.write(this, j);
            return j;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("byteCount < 0: ");
            stringBuilder.append(j);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public long indexOf(ByteString byteString) throws IOException {
        return indexOf(byteString, 0);
    }

    public long indexOf(ByteString byteString, long j) throws IOException {
        if (byteString.size() != 0) {
            long j2 = 0;
            if (j >= 0) {
                Segment segment = this.head;
                long j3 = -1;
                if (segment == null) {
                    return -1;
                }
                long j4 = this.size;
                if (j4 - j >= j) {
                    while (true) {
                        j4 = j2;
                        j2 = ((long) (segment.limit - segment.pos)) + j4;
                        if (j2 >= j) {
                            break;
                        }
                        segment = segment.next;
                    }
                } else {
                    while (j4 > j) {
                        segment = segment.prev;
                        j4 -= (long) (segment.limit - segment.pos);
                    }
                }
                byte b = byteString.getByte(0);
                int size = byteString.size();
                long j5 = 1 + (this.size - ((long) size));
                j2 = j;
                Segment segment2 = segment;
                long j6 = j4;
                while (j6 < j5) {
                    Segment segment3;
                    byte[] bArr = segment2.data;
                    int min = (int) Math.min((long) segment2.limit, (((long) segment2.pos) + j5) - j6);
                    int i = (int) ((((long) segment2.pos) + j2) - j6);
                    while (i < min) {
                        byte[] bArr2;
                        if (bArr[i] == b) {
                            bArr2 = bArr;
                            segment3 = segment2;
                            if (rangeEquals(segment2, i + 1, byteString, 1, size)) {
                                return ((long) (i - segment3.pos)) + j6;
                            }
                        } else {
                            bArr2 = bArr;
                            segment3 = segment2;
                        }
                        i++;
                        segment2 = segment3;
                        bArr = bArr2;
                    }
                    segment3 = segment2;
                    j2 = ((long) (segment3.limit - segment3.pos)) + j6;
                    segment2 = segment3.next;
                    j3 = -1;
                    j6 = j2;
                }
                return j3;
            }
            throw new IllegalArgumentException("fromIndex < 0");
        }
        throw new IllegalArgumentException("bytes is empty");
    }

    public long indexOfElement(ByteString byteString) {
        return indexOfElement(byteString, 0);
    }

    public long indexOfElement(ByteString byteString, long j) {
        long j2 = 0;
        if (j >= 0) {
            Segment segment = this.head;
            if (segment == null) {
                return -1;
            }
            int i;
            int i2;
            long j3 = this.size;
            if (j3 - j >= j) {
                while (true) {
                    j3 = j2;
                    j2 = ((long) (segment.limit - segment.pos)) + j3;
                    if (j2 >= j) {
                        break;
                    }
                    segment = segment.next;
                }
            } else {
                while (j3 > j) {
                    segment = segment.prev;
                    j3 -= (long) (segment.limit - segment.pos);
                }
            }
            int i3;
            if (byteString.size() == 2) {
                byte b = byteString.getByte(0);
                byte b2 = byteString.getByte(1);
                while (j3 < this.size) {
                    byte[] bArr = segment.data;
                    i = (int) ((((long) segment.pos) + j) - j3);
                    i3 = segment.limit;
                    while (i < i3) {
                        byte b3 = bArr[i];
                        if (b3 == b || b3 == b2) {
                            i2 = segment.pos;
                        } else {
                            i++;
                        }
                    }
                    j = ((long) (segment.limit - segment.pos)) + j3;
                    segment = segment.next;
                    j3 = j;
                }
                return -1;
            }
            byte[] internalArray = byteString.internalArray();
            while (j3 < this.size) {
                byte[] bArr2 = segment.data;
                i = (int) ((((long) segment.pos) + j) - j3);
                i3 = segment.limit;
                while (i < i3) {
                    byte b4 = bArr2[i];
                    int length = internalArray.length;
                    int i4 = 0;
                    while (i4 < length) {
                        if (b4 == internalArray[i4]) {
                            i2 = segment.pos;
                        } else {
                            i4++;
                        }
                    }
                    i++;
                }
                j = ((long) (segment.limit - segment.pos)) + j3;
                segment = segment.next;
                j3 = j;
            }
            return -1;
            return ((long) (i - i2)) + j3;
        }
        throw new IllegalArgumentException("fromIndex < 0");
    }

    private boolean rangeEquals(Segment segment, int i, ByteString byteString, int i2, int i3) {
        int i4 = segment.limit;
        byte[] bArr = segment.data;
        while (i2 < i3) {
            if (i == i4) {
                segment = segment.next;
                byte[] bArr2 = segment.data;
                i4 = segment.pos;
                bArr = bArr2;
                i = i4;
                i4 = segment.limit;
            }
            if (bArr[i] != byteString.getByte(i2)) {
                return false;
            }
            i++;
            i2++;
        }
        return true;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Buffer)) {
            return false;
        }
        Buffer buffer = (Buffer) obj;
        long j = this.size;
        if (j != buffer.size) {
            return false;
        }
        long j2 = 0;
        if (j == 0) {
            return true;
        }
        Segment segment = this.head;
        Segment segment2 = buffer.head;
        int i = segment.pos;
        int i2 = segment2.pos;
        while (j2 < this.size) {
            long min = (long) Math.min(segment.limit - i, segment2.limit - i2);
            int i3 = i2;
            i2 = i;
            i = 0;
            while (((long) i) < min) {
                int i4 = i2 + 1;
                int i5 = i3 + 1;
                if (segment.data[i2] != segment2.data[i3]) {
                    return false;
                }
                i++;
                i2 = i4;
                i3 = i5;
            }
            if (i2 == segment.limit) {
                segment = segment.next;
                i = segment.pos;
            } else {
                i = i2;
            }
            if (i3 == segment2.limit) {
                segment2 = segment2.next;
                i2 = segment2.pos;
            } else {
                i2 = i3;
            }
            j2 += min;
        }
        return true;
    }

    public int hashCode() {
        Segment segment = this.head;
        if (segment == null) {
            return 0;
        }
        int i = 1;
        do {
            for (int i2 = segment.pos; i2 < segment.limit; i2++) {
                i = (i * 31) + segment.data[i2];
            }
            segment = segment.next;
        } while (segment != this.head);
        return i;
    }

    public String toString() {
        return snapshot().toString();
    }

    public Buffer clone() {
        Buffer buffer = new Buffer();
        if (this.size == 0) {
            return buffer;
        }
        buffer.head = this.head.sharedCopy();
        Segment segment = buffer.head;
        segment.prev = segment;
        segment.next = segment;
        segment = this.head;
        while (true) {
            segment = segment.next;
            if (segment != this.head) {
                buffer.head.prev.push(segment.sharedCopy());
            } else {
                buffer.size = this.size;
                return buffer;
            }
        }
    }

    public final ByteString snapshot() {
        long j = this.size;
        if (j <= 2147483647L) {
            return snapshot((int) j);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("size > Integer.MAX_VALUE: ");
        stringBuilder.append(this.size);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public final ByteString snapshot(int i) {
        if (i == 0) {
            return ByteString.EMPTY;
        }
        return new SegmentedByteString(this, i);
    }
}

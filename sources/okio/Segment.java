package okio;

final class Segment {
    final byte[] data;
    int limit;
    Segment next;
    boolean owner;
    int pos;
    Segment prev;
    boolean shared;

    Segment() {
        this.data = new byte[8192];
        this.owner = true;
        this.shared = false;
    }

    Segment(byte[] bArr, int i, int i2, boolean z, boolean z2) {
        this.data = bArr;
        this.pos = i;
        this.limit = i2;
        this.shared = z;
        this.owner = z2;
    }

    /* Access modifiers changed, original: final */
    public final Segment sharedCopy() {
        this.shared = true;
        return new Segment(this.data, this.pos, this.limit, true, false);
    }

    public final Segment pop() {
        Segment segment = this.next;
        if (segment == this) {
            segment = null;
        }
        Segment segment2 = this.prev;
        segment2.next = this.next;
        this.next.prev = segment2;
        this.next = null;
        this.prev = null;
        return segment;
    }

    public final Segment push(Segment segment) {
        segment.prev = this;
        segment.next = this.next;
        this.next.prev = segment;
        this.next = segment;
        return segment;
    }

    public final Segment split(int i) {
        if (i <= 0 || i > this.limit - this.pos) {
            throw new IllegalArgumentException();
        }
        Segment sharedCopy;
        if (i >= 1024) {
            sharedCopy = sharedCopy();
        } else {
            sharedCopy = SegmentPool.take();
            System.arraycopy(this.data, this.pos, sharedCopy.data, 0, i);
        }
        sharedCopy.limit = sharedCopy.pos + i;
        this.pos += i;
        this.prev.push(sharedCopy);
        return sharedCopy;
    }

    public final void compact() {
        Segment segment = this.prev;
        if (segment == this) {
            throw new IllegalStateException();
        } else if (segment.owner) {
            int i = this.limit - this.pos;
            if (i <= (8192 - segment.limit) + (segment.shared ? 0 : segment.pos)) {
                writeTo(this.prev, i);
                pop();
                SegmentPool.recycle(this);
            }
        }
    }

    public final void writeTo(Segment segment, int i) {
        if (segment.owner) {
            int i2 = segment.limit;
            if (i2 + i > 8192) {
                if (segment.shared) {
                    throw new IllegalArgumentException();
                }
                int i3 = i2 + i;
                int i4 = segment.pos;
                if (i3 - i4 <= 8192) {
                    byte[] bArr = segment.data;
                    System.arraycopy(bArr, i4, bArr, 0, i2 - i4);
                    segment.limit -= segment.pos;
                    segment.pos = 0;
                } else {
                    throw new IllegalArgumentException();
                }
            }
            System.arraycopy(this.data, this.pos, segment.data, segment.limit, i);
            segment.limit += i;
            this.pos += i;
            return;
        }
        throw new IllegalArgumentException();
    }
}

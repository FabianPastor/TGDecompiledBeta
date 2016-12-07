package com.googlecode.mp4parser.h264.read;

import com.googlecode.mp4parser.h264.CharCache;
import java.io.IOException;
import java.io.InputStream;

public class BitstreamReader {
    protected static int bitsRead;
    private int curByte;
    protected CharCache debugBits = new CharCache(50);
    private InputStream is;
    int nBit;
    private int nextByte;

    public BitstreamReader(InputStream is) throws IOException {
        this.is = is;
        this.curByte = is.read();
        this.nextByte = is.read();
    }

    public boolean readBool() throws IOException {
        return read1Bit() == 1;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int read1Bit() throws IOException {
        int i = -1;
        if (this.nBit == 8) {
            advance();
        }
        i = (this.curByte >> (7 - this.nBit)) & 1;
        this.nBit++;
        this.debugBits.append(i == 0 ? '0' : '1');
        bitsRead++;
        return i;
    }

    public long readNBit(int n) throws IOException {
        if (n > 64) {
            throw new IllegalArgumentException("Can not readByte more then 64 bit");
        }
        long val = 0;
        for (int i = 0; i < n; i++) {
            val = (val << 1) | ((long) read1Bit());
        }
        return val;
    }

    private void advance() throws IOException {
        this.curByte = this.nextByte;
        this.nextByte = this.is.read();
        this.nBit = 0;
    }

    public int readByte() throws IOException {
        if (this.nBit > 0) {
            advance();
        }
        int res = this.curByte;
        advance();
        return res;
    }

    public boolean moreRBSPData() throws IOException {
        if (this.nBit == 8) {
            advance();
        }
        int tail = 1 << ((8 - this.nBit) - 1);
        boolean hasTail;
        if ((this.curByte & ((tail << 1) - 1)) == tail) {
            hasTail = true;
        } else {
            hasTail = false;
        }
        if (this.curByte == -1 || (this.nextByte == -1 && hasTail)) {
            return false;
        }
        return true;
    }

    public long getBitPosition() {
        return (long) ((bitsRead * 8) + (this.nBit % 8));
    }

    public long readRemainingByte() throws IOException {
        return readNBit(8 - this.nBit);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int peakNextBits(int n) throws IOException {
        int i = -1;
        if (n > 8) {
            throw new IllegalArgumentException("N should be less then 8");
        }
        if (this.nBit == 8) {
            advance();
        }
        int[] bits = new int[(16 - this.nBit)];
        int i2 = this.nBit;
        int cnt = 0;
        while (i2 < 8) {
            int cnt2 = cnt + 1;
            bits[cnt] = (this.curByte >> (7 - i2)) & 1;
            i2++;
            cnt = cnt2;
        }
        i2 = 0;
        while (i2 < 8) {
            cnt2 = cnt + 1;
            bits[cnt] = (this.nextByte >> (7 - i2)) & 1;
            i2++;
            cnt = cnt2;
        }
        i = 0;
        for (i2 = 0; i2 < n; i2++) {
            i = (i << 1) | bits[i2];
        }
        return i;
    }

    public boolean isByteAligned() {
        return this.nBit % 8 == 0;
    }

    public void close() throws IOException {
    }

    public int getCurBit() {
        return this.nBit;
    }
}

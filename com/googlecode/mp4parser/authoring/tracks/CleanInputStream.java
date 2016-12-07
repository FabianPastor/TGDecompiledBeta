package com.googlecode.mp4parser.authoring.tracks;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CleanInputStream extends FilterInputStream {
    int prev = -1;
    int prevprev = -1;

    public CleanInputStream(InputStream in) {
        super(in);
    }

    public boolean markSupported() {
        return false;
    }

    public int read() throws IOException {
        int c = super.read();
        if (c == 3 && this.prevprev == 0 && this.prev == 0) {
            this.prevprev = -1;
            this.prev = -1;
            c = super.read();
        }
        this.prevprev = this.prev;
        this.prev = c;
        return c;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        } else {
            int c = read();
            if (c == -1) {
                return -1;
            }
            b[off] = (byte) c;
            int i = 1;
            while (i < len) {
                try {
                    c = read();
                    if (c == -1) {
                        return i;
                    }
                    b[off + i] = (byte) c;
                    i++;
                } catch (IOException e) {
                    return i;
                }
            }
            return i;
        }
    }
}

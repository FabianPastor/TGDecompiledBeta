package org.telegram.messenger.exoplayer.util.extensions;

public interface Decoder<I, O, E extends Exception> {
    I dequeueInputBuffer() throws Exception;

    O dequeueOutputBuffer() throws Exception;

    void flush();

    void queueInputBuffer(I i) throws Exception;

    void release();
}

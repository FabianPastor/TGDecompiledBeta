package org.telegram.messenger.exoplayer2.decoder;

import java.util.LinkedList;
import org.telegram.messenger.exoplayer2.util.Assertions;

public abstract class SimpleDecoder<I extends DecoderInputBuffer, O extends OutputBuffer, E extends Exception> implements Decoder<I, O, E> {
    private int availableInputBufferCount;
    private final I[] availableInputBuffers;
    private int availableOutputBufferCount;
    private final O[] availableOutputBuffers;
    private final Thread decodeThread;
    private I dequeuedInputBuffer;
    private E exception;
    private boolean flushed;
    private final Object lock = new Object();
    private final LinkedList<I> queuedInputBuffers = new LinkedList();
    private final LinkedList<O> queuedOutputBuffers = new LinkedList();
    private boolean released;
    private int skippedOutputBufferCount;

    /* renamed from: org.telegram.messenger.exoplayer2.decoder.SimpleDecoder$1 */
    class C05731 extends Thread {
        C05731() {
        }

        public void run() {
            SimpleDecoder.this.run();
        }
    }

    protected abstract I createInputBuffer();

    protected abstract O createOutputBuffer();

    protected abstract E createUnexpectedDecodeException(Throwable th);

    protected abstract E decode(I i, O o, boolean z);

    protected SimpleDecoder(I[] inputBuffers, O[] outputBuffers) {
        int i;
        this.availableInputBuffers = inputBuffers;
        this.availableInputBufferCount = inputBuffers.length;
        for (i = 0; i < this.availableInputBufferCount; i++) {
            this.availableInputBuffers[i] = createInputBuffer();
        }
        this.availableOutputBuffers = outputBuffers;
        this.availableOutputBufferCount = outputBuffers.length;
        for (i = 0; i < this.availableOutputBufferCount; i++) {
            this.availableOutputBuffers[i] = createOutputBuffer();
        }
        this.decodeThread = new C05731();
        this.decodeThread.start();
    }

    protected final void setInitialInputBufferSize(int size) {
        boolean z;
        int i = 0;
        if (this.availableInputBufferCount == this.availableInputBuffers.length) {
            z = true;
        } else {
            z = false;
        }
        Assertions.checkState(z);
        DecoderInputBuffer[] decoderInputBufferArr = this.availableInputBuffers;
        int length = decoderInputBufferArr.length;
        while (i < length) {
            decoderInputBufferArr[i].ensureSpaceForWrite(size);
            i++;
        }
    }

    public final I dequeueInputBuffer() throws Exception {
        I i;
        synchronized (this.lock) {
            DecoderInputBuffer decoderInputBuffer;
            maybeThrowException();
            Assertions.checkState(this.dequeuedInputBuffer == null);
            if (this.availableInputBufferCount == 0) {
                decoderInputBuffer = null;
            } else {
                DecoderInputBuffer[] decoderInputBufferArr = this.availableInputBuffers;
                int i2 = this.availableInputBufferCount - 1;
                this.availableInputBufferCount = i2;
                decoderInputBuffer = decoderInputBufferArr[i2];
            }
            this.dequeuedInputBuffer = decoderInputBuffer;
            i = this.dequeuedInputBuffer;
        }
        return i;
    }

    public final void queueInputBuffer(I inputBuffer) throws Exception {
        synchronized (this.lock) {
            maybeThrowException();
            Assertions.checkArgument(inputBuffer == this.dequeuedInputBuffer);
            this.queuedInputBuffers.addLast(inputBuffer);
            maybeNotifyDecodeLoop();
            this.dequeuedInputBuffer = null;
        }
    }

    public final O dequeueOutputBuffer() throws Exception {
        O o;
        synchronized (this.lock) {
            maybeThrowException();
            if (this.queuedOutputBuffers.isEmpty()) {
                o = null;
            } else {
                OutputBuffer outputBuffer = (OutputBuffer) this.queuedOutputBuffers.removeFirst();
            }
        }
        return o;
    }

    protected void releaseOutputBuffer(O outputBuffer) {
        synchronized (this.lock) {
            releaseOutputBufferInternal(outputBuffer);
            maybeNotifyDecodeLoop();
        }
    }

    public final void flush() {
        synchronized (this.lock) {
            this.flushed = true;
            this.skippedOutputBufferCount = 0;
            if (this.dequeuedInputBuffer != null) {
                releaseInputBufferInternal(this.dequeuedInputBuffer);
                this.dequeuedInputBuffer = null;
            }
            while (!this.queuedInputBuffers.isEmpty()) {
                releaseInputBufferInternal((DecoderInputBuffer) this.queuedInputBuffers.removeFirst());
            }
            while (!this.queuedOutputBuffers.isEmpty()) {
                releaseOutputBufferInternal((OutputBuffer) this.queuedOutputBuffers.removeFirst());
            }
        }
    }

    public void release() {
        synchronized (this.lock) {
            this.released = true;
            this.lock.notify();
        }
        try {
            this.decodeThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void maybeThrowException() throws Exception {
        if (this.exception != null) {
            throw this.exception;
        }
    }

    private void maybeNotifyDecodeLoop() {
        if (canDecodeBuffer()) {
            this.lock.notify();
        }
    }

    private void run() {
        do {
            try {
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        } while (decode());
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean decode() throws InterruptedException {
        synchronized (this.lock) {
            while (!this.released && !canDecodeBuffer()) {
                this.lock.wait();
            }
            if (this.released) {
                return false;
            }
            DecoderInputBuffer inputBuffer = (DecoderInputBuffer) this.queuedInputBuffers.removeFirst();
            OutputBuffer[] outputBufferArr = this.availableOutputBuffers;
            int i = this.availableOutputBufferCount - 1;
            this.availableOutputBufferCount = i;
            O outputBuffer = outputBufferArr[i];
            boolean resetDecoder = this.flushed;
            this.flushed = false;
        }
        if (this.exception != null) {
            synchronized (this.lock) {
            }
            return false;
        }
        synchronized (this.lock) {
            if (this.flushed) {
                releaseOutputBufferInternal(outputBuffer);
            } else if (outputBuffer.isDecodeOnly()) {
                this.skippedOutputBufferCount++;
                releaseOutputBufferInternal(outputBuffer);
            } else {
                outputBuffer.skippedOutputBufferCount = this.skippedOutputBufferCount;
                this.skippedOutputBufferCount = 0;
                this.queuedOutputBuffers.addLast(outputBuffer);
            }
            releaseInputBufferInternal(inputBuffer);
        }
        return true;
    }

    private boolean canDecodeBuffer() {
        return !this.queuedInputBuffers.isEmpty() && this.availableOutputBufferCount > 0;
    }

    private void releaseInputBufferInternal(I inputBuffer) {
        inputBuffer.clear();
        DecoderInputBuffer[] decoderInputBufferArr = this.availableInputBuffers;
        int i = this.availableInputBufferCount;
        this.availableInputBufferCount = i + 1;
        decoderInputBufferArr[i] = inputBuffer;
    }

    private void releaseOutputBufferInternal(O outputBuffer) {
        outputBuffer.clear();
        OutputBuffer[] outputBufferArr = this.availableOutputBuffers;
        int i = this.availableOutputBufferCount;
        this.availableOutputBufferCount = i + 1;
        outputBufferArr[i] = outputBuffer;
    }
}

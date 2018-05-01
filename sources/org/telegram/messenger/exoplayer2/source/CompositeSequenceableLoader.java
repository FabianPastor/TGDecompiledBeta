package org.telegram.messenger.exoplayer2.source;

public class CompositeSequenceableLoader implements SequenceableLoader {
    protected final SequenceableLoader[] loaders;

    public CompositeSequenceableLoader(SequenceableLoader[] sequenceableLoaderArr) {
        this.loaders = sequenceableLoaderArr;
    }

    public final long getBufferedPositionUs() {
        long j = Long.MAX_VALUE;
        for (SequenceableLoader bufferedPositionUs : this.loaders) {
            long bufferedPositionUs2 = bufferedPositionUs.getBufferedPositionUs();
            if (bufferedPositionUs2 != Long.MIN_VALUE) {
                j = Math.min(j, bufferedPositionUs2);
            }
        }
        return j == Long.MAX_VALUE ? Long.MIN_VALUE : j;
    }

    public final long getNextLoadPositionUs() {
        long j = Long.MAX_VALUE;
        for (SequenceableLoader nextLoadPositionUs : this.loaders) {
            long nextLoadPositionUs2 = nextLoadPositionUs.getNextLoadPositionUs();
            if (nextLoadPositionUs2 != Long.MIN_VALUE) {
                j = Math.min(j, nextLoadPositionUs2);
            }
        }
        return j == Long.MAX_VALUE ? Long.MIN_VALUE : j;
    }

    public final void reevaluateBuffer(long j) {
        for (SequenceableLoader reevaluateBuffer : this.loaders) {
            reevaluateBuffer.reevaluateBuffer(j);
        }
    }

    public boolean continueLoading(long j) {
        long j2 = j;
        boolean z = false;
        int i;
        do {
            long nextLoadPositionUs = getNextLoadPositionUs();
            if (nextLoadPositionUs == Long.MIN_VALUE) {
                CompositeSequenceableLoader compositeSequenceableLoader = this;
                break;
            }
            SequenceableLoader[] sequenceableLoaderArr = this.loaders;
            int length = sequenceableLoaderArr.length;
            int i2 = 0;
            i = i2;
            while (i2 < length) {
                SequenceableLoader sequenceableLoader = sequenceableLoaderArr[i2];
                long nextLoadPositionUs2 = sequenceableLoader.getNextLoadPositionUs();
                Object obj = (nextLoadPositionUs2 == Long.MIN_VALUE || nextLoadPositionUs2 > j2) ? null : 1;
                if (nextLoadPositionUs2 == nextLoadPositionUs || obj != null) {
                    i |= sequenceableLoader.continueLoading(j2);
                }
                i2++;
            }
            z |= i;
        } while (i != 0);
        return z;
    }
}

package org.telegram.messenger.exoplayer2.source;

public class CompositeSequenceableLoader implements SequenceableLoader {
    protected final SequenceableLoader[] loaders;

    public CompositeSequenceableLoader(SequenceableLoader[] loaders) {
        this.loaders = loaders;
    }

    public final long getBufferedPositionUs() {
        long bufferedPositionUs = Long.MAX_VALUE;
        for (SequenceableLoader loader : this.loaders) {
            long loaderBufferedPositionUs = loader.getBufferedPositionUs();
            if (loaderBufferedPositionUs != Long.MIN_VALUE) {
                bufferedPositionUs = Math.min(bufferedPositionUs, loaderBufferedPositionUs);
            }
        }
        return bufferedPositionUs == Long.MAX_VALUE ? Long.MIN_VALUE : bufferedPositionUs;
    }

    public final long getNextLoadPositionUs() {
        long nextLoadPositionUs = Long.MAX_VALUE;
        for (SequenceableLoader loader : this.loaders) {
            long loaderNextLoadPositionUs = loader.getNextLoadPositionUs();
            if (loaderNextLoadPositionUs != Long.MIN_VALUE) {
                nextLoadPositionUs = Math.min(nextLoadPositionUs, loaderNextLoadPositionUs);
            }
        }
        return nextLoadPositionUs == Long.MAX_VALUE ? Long.MIN_VALUE : nextLoadPositionUs;
    }

    public final void reevaluateBuffer(long positionUs) {
        for (SequenceableLoader loader : this.loaders) {
            loader.reevaluateBuffer(positionUs);
        }
    }

    public boolean continueLoading(long positionUs) {
        boolean madeProgress = false;
        boolean madeProgressThisIteration;
        do {
            madeProgressThisIteration = false;
            long nextLoadPositionUs = getNextLoadPositionUs();
            if (nextLoadPositionUs == Long.MIN_VALUE) {
                break;
            }
            for (SequenceableLoader loader : this.loaders) {
                long loaderNextLoadPositionUs = loader.getNextLoadPositionUs();
                boolean isLoaderBehind = loaderNextLoadPositionUs != Long.MIN_VALUE && loaderNextLoadPositionUs <= positionUs;
                if (loaderNextLoadPositionUs == nextLoadPositionUs || isLoaderBehind) {
                    madeProgressThisIteration |= loader.continueLoading(positionUs);
                }
            }
            madeProgress |= madeProgressThisIteration;
        } while (madeProgressThisIteration);
        return madeProgress;
    }
}

package org.telegram.messenger.exoplayer2.source;

public final class CompositeSequenceableLoader implements SequenceableLoader {
    private final SequenceableLoader[] loaders;

    public CompositeSequenceableLoader(SequenceableLoader[] loaders) {
        this.loaders = loaders;
    }

    public long getNextLoadPositionUs() {
        long nextLoadPositionUs = Long.MAX_VALUE;
        for (SequenceableLoader loader : this.loaders) {
            long loaderNextLoadPositionUs = loader.getNextLoadPositionUs();
            if (loaderNextLoadPositionUs != Long.MIN_VALUE) {
                nextLoadPositionUs = Math.min(nextLoadPositionUs, loaderNextLoadPositionUs);
            }
        }
        return nextLoadPositionUs == Long.MAX_VALUE ? Long.MIN_VALUE : nextLoadPositionUs;
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
                if (loader.getNextLoadPositionUs() == nextLoadPositionUs) {
                    madeProgressThisIteration |= loader.continueLoading(positionUs);
                }
            }
            madeProgress |= madeProgressThisIteration;
        } while (madeProgressThisIteration);
        return madeProgress;
    }
}

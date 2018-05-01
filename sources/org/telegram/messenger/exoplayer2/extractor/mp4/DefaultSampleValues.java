package org.telegram.messenger.exoplayer2.extractor.mp4;

final class DefaultSampleValues {
    public final int duration;
    public final int flags;
    public final int sampleDescriptionIndex;
    public final int size;

    public DefaultSampleValues(int i, int i2, int i3, int i4) {
        this.sampleDescriptionIndex = i;
        this.duration = i2;
        this.size = i3;
        this.flags = i4;
    }
}

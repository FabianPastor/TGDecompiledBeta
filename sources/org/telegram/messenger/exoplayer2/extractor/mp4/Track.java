package org.telegram.messenger.exoplayer2.extractor.mp4;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.telegram.messenger.exoplayer2.Format;

public final class Track {
    public static final int TRANSFORMATION_CEA608_CDAT = 1;
    public static final int TRANSFORMATION_NONE = 0;
    public final long durationUs;
    public final long[] editListDurations;
    public final long[] editListMediaTimes;
    public final Format format;
    public final int id;
    public final long movieTimescale;
    public final int nalUnitLengthFieldLength;
    private final TrackEncryptionBox[] sampleDescriptionEncryptionBoxes;
    public final int sampleTransformation;
    public final long timescale;
    public final int type;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Transformation {
    }

    public Track(int id, int type, long timescale, long movieTimescale, long durationUs, Format format, int sampleTransformation, TrackEncryptionBox[] sampleDescriptionEncryptionBoxes, int nalUnitLengthFieldLength, long[] editListDurations, long[] editListMediaTimes) {
        this.id = id;
        this.type = type;
        this.timescale = timescale;
        this.movieTimescale = movieTimescale;
        this.durationUs = durationUs;
        this.format = format;
        this.sampleTransformation = sampleTransformation;
        this.sampleDescriptionEncryptionBoxes = sampleDescriptionEncryptionBoxes;
        this.nalUnitLengthFieldLength = nalUnitLengthFieldLength;
        this.editListDurations = editListDurations;
        this.editListMediaTimes = editListMediaTimes;
    }

    public TrackEncryptionBox getSampleDescriptionEncryptionBox(int sampleDescriptionIndex) {
        return this.sampleDescriptionEncryptionBoxes == null ? null : this.sampleDescriptionEncryptionBoxes[sampleDescriptionIndex];
    }
}

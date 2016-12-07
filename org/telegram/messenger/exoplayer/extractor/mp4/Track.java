package org.telegram.messenger.exoplayer.extractor.mp4;

import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.util.Util;

public final class Track {
    public static final int TYPE_sbtl = Util.getIntegerCodeForString("sbtl");
    public static final int TYPE_soun = Util.getIntegerCodeForString("soun");
    public static final int TYPE_subt = Util.getIntegerCodeForString("subt");
    public static final int TYPE_text = Util.getIntegerCodeForString("text");
    public static final int TYPE_vide = Util.getIntegerCodeForString("vide");
    public final long durationUs;
    public final long[] editListDurations;
    public final long[] editListMediaTimes;
    public final int id;
    public final MediaFormat mediaFormat;
    public final long movieTimescale;
    public final int nalUnitLengthFieldLength;
    public final TrackEncryptionBox[] sampleDescriptionEncryptionBoxes;
    public final long timescale;
    public final int type;

    public Track(int id, int type, long timescale, long movieTimescale, long durationUs, MediaFormat mediaFormat, TrackEncryptionBox[] sampleDescriptionEncryptionBoxes, int nalUnitLengthFieldLength, long[] editListDurations, long[] editListMediaTimes) {
        this.id = id;
        this.type = type;
        this.timescale = timescale;
        this.movieTimescale = movieTimescale;
        this.durationUs = durationUs;
        this.mediaFormat = mediaFormat;
        this.sampleDescriptionEncryptionBoxes = sampleDescriptionEncryptionBoxes;
        this.nalUnitLengthFieldLength = nalUnitLengthFieldLength;
        this.editListDurations = editListDurations;
        this.editListMediaTimes = editListMediaTimes;
    }
}

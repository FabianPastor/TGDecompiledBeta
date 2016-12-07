package org.telegram.messenger.exoplayer2.metadata.scte35;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class TimeSignalCommand extends SpliceCommand {
    public static final Creator<TimeSignalCommand> CREATOR = new Creator<TimeSignalCommand>() {
        public TimeSignalCommand createFromParcel(Parcel in) {
            return new TimeSignalCommand(in.readLong());
        }

        public TimeSignalCommand[] newArray(int size) {
            return new TimeSignalCommand[size];
        }
    };
    public final long ptsTime;

    private TimeSignalCommand(long ptsTime) {
        this.ptsTime = ptsTime;
    }

    static TimeSignalCommand parseFromSection(ParsableByteArray sectionData, long ptsAdjustment) {
        return new TimeSignalCommand(parseSpliceTime(sectionData, ptsAdjustment));
    }

    static long parseSpliceTime(ParsableByteArray sectionData, long ptsAdjustment) {
        long firstByte = (long) sectionData.readUnsignedByte();
        if ((128 & firstByte) != 0) {
            return ((((1 & firstByte) << 32) | sectionData.readUnsignedInt()) + ptsAdjustment) & 8589934591L;
        }
        return C.TIME_UNSET;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.ptsTime);
    }
}

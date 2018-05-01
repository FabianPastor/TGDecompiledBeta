package org.telegram.messenger.exoplayer2.metadata.scte35;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.TimestampAdjuster;

public final class TimeSignalCommand extends SpliceCommand {
    public static final Creator<TimeSignalCommand> CREATOR = new C05921();
    public final long playbackPositionUs;
    public final long ptsTime;

    /* renamed from: org.telegram.messenger.exoplayer2.metadata.scte35.TimeSignalCommand$1 */
    static class C05921 implements Creator<TimeSignalCommand> {
        C05921() {
        }

        public TimeSignalCommand createFromParcel(Parcel parcel) {
            return new TimeSignalCommand(parcel.readLong(), parcel.readLong());
        }

        public TimeSignalCommand[] newArray(int i) {
            return new TimeSignalCommand[i];
        }
    }

    private TimeSignalCommand(long j, long j2) {
        this.ptsTime = j;
        this.playbackPositionUs = j2;
    }

    static TimeSignalCommand parseFromSection(ParsableByteArray parsableByteArray, long j, TimestampAdjuster timestampAdjuster) {
        parsableByteArray = parseSpliceTime(parsableByteArray, j);
        return new TimeSignalCommand(parsableByteArray, timestampAdjuster.adjustTsTimestamp(parsableByteArray));
    }

    static long parseSpliceTime(ParsableByteArray parsableByteArray, long j) {
        long readUnsignedByte = (long) parsableByteArray.readUnsignedByte();
        return (readUnsignedByte & 128) != 0 ? ((((readUnsignedByte & 1) << 32) | parsableByteArray.readUnsignedInt()) + j) & -1 : C0542C.TIME_UNSET;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.ptsTime);
        parcel.writeLong(this.playbackPositionUs);
    }
}

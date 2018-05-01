package org.telegram.messenger.exoplayer2.metadata.scte35;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.TimestampAdjuster;

public final class SpliceInsertCommand extends SpliceCommand {
    public static final Creator<SpliceInsertCommand> CREATOR = new C05891();
    public final boolean autoReturn;
    public final int availNum;
    public final int availsExpected;
    public final long breakDurationUs;
    public final List<ComponentSplice> componentSpliceList;
    public final boolean outOfNetworkIndicator;
    public final boolean programSpliceFlag;
    public final long programSplicePlaybackPositionUs;
    public final long programSplicePts;
    public final boolean spliceEventCancelIndicator;
    public final long spliceEventId;
    public final boolean spliceImmediateFlag;
    public final int uniqueProgramId;

    /* renamed from: org.telegram.messenger.exoplayer2.metadata.scte35.SpliceInsertCommand$1 */
    static class C05891 implements Creator<SpliceInsertCommand> {
        C05891() {
        }

        public SpliceInsertCommand createFromParcel(Parcel parcel) {
            return new SpliceInsertCommand(parcel);
        }

        public SpliceInsertCommand[] newArray(int i) {
            return new SpliceInsertCommand[i];
        }
    }

    public static final class ComponentSplice {
        public final long componentSplicePlaybackPositionUs;
        public final long componentSplicePts;
        public final int componentTag;

        private ComponentSplice(int i, long j, long j2) {
            this.componentTag = i;
            this.componentSplicePts = j;
            this.componentSplicePlaybackPositionUs = j2;
        }

        public void writeToParcel(Parcel parcel) {
            parcel.writeInt(this.componentTag);
            parcel.writeLong(this.componentSplicePts);
            parcel.writeLong(this.componentSplicePlaybackPositionUs);
        }

        public static ComponentSplice createFromParcel(Parcel parcel) {
            return new ComponentSplice(parcel.readInt(), parcel.readLong(), parcel.readLong());
        }
    }

    private SpliceInsertCommand(long j, boolean z, boolean z2, boolean z3, boolean z4, long j2, long j3, List<ComponentSplice> list, boolean z5, long j4, int i, int i2, int i3) {
        this.spliceEventId = j;
        this.spliceEventCancelIndicator = z;
        this.outOfNetworkIndicator = z2;
        this.programSpliceFlag = z3;
        this.spliceImmediateFlag = z4;
        this.programSplicePts = j2;
        this.programSplicePlaybackPositionUs = j3;
        this.componentSpliceList = Collections.unmodifiableList(list);
        this.autoReturn = z5;
        this.breakDurationUs = j4;
        this.uniqueProgramId = i;
        this.availNum = i2;
        this.availsExpected = i3;
    }

    private SpliceInsertCommand(Parcel parcel) {
        this.spliceEventId = parcel.readLong();
        boolean z = false;
        this.spliceEventCancelIndicator = parcel.readByte() == (byte) 1;
        this.outOfNetworkIndicator = parcel.readByte() == (byte) 1;
        this.programSpliceFlag = parcel.readByte() == (byte) 1;
        this.spliceImmediateFlag = parcel.readByte() == (byte) 1;
        this.programSplicePts = parcel.readLong();
        this.programSplicePlaybackPositionUs = parcel.readLong();
        int readInt = parcel.readInt();
        List arrayList = new ArrayList(readInt);
        for (int i = 0; i < readInt; i++) {
            arrayList.add(ComponentSplice.createFromParcel(parcel));
        }
        this.componentSpliceList = Collections.unmodifiableList(arrayList);
        if (parcel.readByte() == (byte) 1) {
            z = true;
        }
        this.autoReturn = z;
        this.breakDurationUs = parcel.readLong();
        this.uniqueProgramId = parcel.readInt();
        this.availNum = parcel.readInt();
        this.availsExpected = parcel.readInt();
    }

    static SpliceInsertCommand parseFromSection(ParsableByteArray parsableByteArray, long j, TimestampAdjuster timestampAdjuster) {
        List list;
        boolean z;
        boolean z2;
        boolean z3;
        long j2;
        boolean z4;
        long j3;
        int i;
        int i2;
        int i3;
        TimestampAdjuster timestampAdjuster2 = timestampAdjuster;
        long readUnsignedInt = parsableByteArray.readUnsignedInt();
        boolean z5 = (parsableByteArray.readUnsignedByte() & 128) != 0;
        List emptyList = Collections.emptyList();
        if (z5) {
            list = emptyList;
            z = false;
            z2 = false;
            z3 = false;
            j2 = C0542C.TIME_UNSET;
            z4 = false;
            j3 = C0542C.TIME_UNSET;
            i = 0;
            i2 = 0;
            i3 = 0;
        } else {
            List list2;
            int i4;
            boolean z6;
            long readUnsignedInt2;
            int readUnsignedByte = parsableByteArray.readUnsignedByte();
            boolean z7 = (readUnsignedByte & 128) != 0;
            boolean z8 = (readUnsignedByte & 64) != 0;
            Object obj = (readUnsignedByte & 32) != 0 ? 1 : null;
            boolean z9 = (readUnsignedByte & 16) != 0;
            j3 = (!z8 || z9) ? C0542C.TIME_UNSET : TimeSignalCommand.parseSpliceTime(parsableByteArray, j);
            if (z8) {
                list2 = emptyList;
            } else {
                int readUnsignedByte2 = parsableByteArray.readUnsignedByte();
                list2 = new ArrayList(readUnsignedByte2);
                i4 = 0;
                while (i4 < readUnsignedByte2) {
                    int i5;
                    long j4;
                    int readUnsignedByte3 = parsableByteArray.readUnsignedByte();
                    if (z9) {
                        i5 = readUnsignedByte2;
                        j4 = C0542C.TIME_UNSET;
                    } else {
                        i5 = readUnsignedByte2;
                        j4 = TimeSignalCommand.parseSpliceTime(parsableByteArray, j);
                    }
                    list2.add(new ComponentSplice(readUnsignedByte3, j4, timestampAdjuster2.adjustTsTimestamp(j4)));
                    i4++;
                    readUnsignedByte2 = i5;
                }
            }
            if (obj != null) {
                long readUnsignedByte4 = (long) parsableByteArray.readUnsignedByte();
                z6 = (readUnsignedByte4 & 128) != 0;
                readUnsignedInt2 = ((((readUnsignedByte4 & 1) << 32) | parsableByteArray.readUnsignedInt()) * 1000) / 90;
            } else {
                z6 = false;
                readUnsignedInt2 = C0542C.TIME_UNSET;
            }
            i4 = parsableByteArray.readUnsignedShort();
            i2 = parsableByteArray.readUnsignedByte();
            i3 = parsableByteArray.readUnsignedByte();
            z3 = z9;
            z2 = z8;
            j2 = j3;
            list = list2;
            z4 = z6;
            j3 = readUnsignedInt2;
            i = i4;
            z = z7;
        }
        return new SpliceInsertCommand(readUnsignedInt, z5, z, z2, z3, j2, timestampAdjuster2.adjustTsTimestamp(j2), list, z4, j3, i, i2, i3);
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.spliceEventId);
        parcel.writeByte((byte) this.spliceEventCancelIndicator);
        parcel.writeByte((byte) this.outOfNetworkIndicator);
        parcel.writeByte((byte) this.programSpliceFlag);
        parcel.writeByte((byte) this.spliceImmediateFlag);
        parcel.writeLong(this.programSplicePts);
        parcel.writeLong(this.programSplicePlaybackPositionUs);
        i = this.componentSpliceList.size();
        parcel.writeInt(i);
        for (int i2 = 0; i2 < i; i2++) {
            ((ComponentSplice) this.componentSpliceList.get(i2)).writeToParcel(parcel);
        }
        parcel.writeByte((byte) this.autoReturn);
        parcel.writeLong(this.breakDurationUs);
        parcel.writeInt(this.uniqueProgramId);
        parcel.writeInt(this.availNum);
        parcel.writeInt(this.availsExpected);
    }
}

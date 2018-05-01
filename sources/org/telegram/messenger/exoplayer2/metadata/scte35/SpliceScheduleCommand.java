package org.telegram.messenger.exoplayer2.metadata.scte35;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class SpliceScheduleCommand extends SpliceCommand {
    public static final Creator<SpliceScheduleCommand> CREATOR = new C05911();
    public final List<Event> events;

    /* renamed from: org.telegram.messenger.exoplayer2.metadata.scte35.SpliceScheduleCommand$1 */
    static class C05911 implements Creator<SpliceScheduleCommand> {
        C05911() {
        }

        public SpliceScheduleCommand createFromParcel(Parcel parcel) {
            return new SpliceScheduleCommand(parcel);
        }

        public SpliceScheduleCommand[] newArray(int i) {
            return new SpliceScheduleCommand[i];
        }
    }

    public static final class ComponentSplice {
        public final int componentTag;
        public final long utcSpliceTime;

        private ComponentSplice(int i, long j) {
            this.componentTag = i;
            this.utcSpliceTime = j;
        }

        private static ComponentSplice createFromParcel(Parcel parcel) {
            return new ComponentSplice(parcel.readInt(), parcel.readLong());
        }

        private void writeToParcel(Parcel parcel) {
            parcel.writeInt(this.componentTag);
            parcel.writeLong(this.utcSpliceTime);
        }
    }

    public static final class Event {
        public final boolean autoReturn;
        public final int availNum;
        public final int availsExpected;
        public final long breakDurationUs;
        public final List<ComponentSplice> componentSpliceList;
        public final boolean outOfNetworkIndicator;
        public final boolean programSpliceFlag;
        public final boolean spliceEventCancelIndicator;
        public final long spliceEventId;
        public final int uniqueProgramId;
        public final long utcSpliceTime;

        private Event(long j, boolean z, boolean z2, boolean z3, List<ComponentSplice> list, long j2, boolean z4, long j3, int i, int i2, int i3) {
            this.spliceEventId = j;
            this.spliceEventCancelIndicator = z;
            this.outOfNetworkIndicator = z2;
            this.programSpliceFlag = z3;
            this.componentSpliceList = Collections.unmodifiableList(list);
            this.utcSpliceTime = j2;
            this.autoReturn = z4;
            this.breakDurationUs = j3;
            this.uniqueProgramId = i;
            this.availNum = i2;
            this.availsExpected = i3;
        }

        private Event(Parcel parcel) {
            this.spliceEventId = parcel.readLong();
            boolean z = false;
            this.spliceEventCancelIndicator = parcel.readByte() == (byte) 1;
            this.outOfNetworkIndicator = parcel.readByte() == (byte) 1;
            this.programSpliceFlag = parcel.readByte() == (byte) 1;
            int readInt = parcel.readInt();
            List arrayList = new ArrayList(readInt);
            for (int i = 0; i < readInt; i++) {
                arrayList.add(ComponentSplice.createFromParcel(parcel));
            }
            this.componentSpliceList = Collections.unmodifiableList(arrayList);
            this.utcSpliceTime = parcel.readLong();
            if (parcel.readByte() == (byte) 1) {
                z = true;
            }
            this.autoReturn = z;
            this.breakDurationUs = parcel.readLong();
            this.uniqueProgramId = parcel.readInt();
            this.availNum = parcel.readInt();
            this.availsExpected = parcel.readInt();
        }

        private static Event parseFromSection(ParsableByteArray parsableByteArray) {
            List list;
            boolean z;
            long j;
            boolean z2;
            long j2;
            int i;
            int i2;
            int i3;
            boolean z3;
            long readUnsignedInt = parsableByteArray.readUnsignedInt();
            boolean z4 = (parsableByteArray.readUnsignedByte() & 128) != 0;
            ArrayList arrayList = new ArrayList();
            if (z4) {
                list = arrayList;
                z = false;
                j = C0542C.TIME_UNSET;
                z2 = false;
                j2 = C0542C.TIME_UNSET;
                i = 0;
                i2 = 0;
                i3 = 0;
                z3 = false;
            } else {
                ArrayList arrayList2;
                boolean z5;
                long readUnsignedInt2;
                int readUnsignedByte = parsableByteArray.readUnsignedByte();
                z2 = (readUnsignedByte & 128) != 0;
                boolean z6 = (readUnsignedByte & 64) != 0;
                Object obj = (readUnsignedByte & 32) != 0 ? 1 : null;
                long readUnsignedInt3 = z6 ? parsableByteArray.readUnsignedInt() : C0542C.TIME_UNSET;
                if (z6) {
                    arrayList2 = arrayList;
                } else {
                    int readUnsignedByte2 = parsableByteArray.readUnsignedByte();
                    arrayList2 = new ArrayList(readUnsignedByte2);
                    for (int i4 = 0; i4 < readUnsignedByte2; i4++) {
                        arrayList2.add(new ComponentSplice(parsableByteArray.readUnsignedByte(), parsableByteArray.readUnsignedInt()));
                    }
                }
                if (obj != null) {
                    long readUnsignedByte3 = (long) parsableByteArray.readUnsignedByte();
                    z5 = (readUnsignedByte3 & 128) != 0;
                    readUnsignedInt2 = ((((readUnsignedByte3 & 1) << 32) | parsableByteArray.readUnsignedInt()) * 1000) / 90;
                } else {
                    z5 = false;
                    readUnsignedInt2 = C0542C.TIME_UNSET;
                }
                z3 = z6;
                j = readUnsignedInt3;
                list = arrayList2;
                j2 = readUnsignedInt2;
                i = parsableByteArray.readUnsignedShort();
                i2 = parsableByteArray.readUnsignedByte();
                i3 = parsableByteArray.readUnsignedByte();
                z = z2;
                z2 = z5;
            }
            return new Event(readUnsignedInt, z4, z, z3, list, j, z2, j2, i, i2, i3);
        }

        private void writeToParcel(Parcel parcel) {
            parcel.writeLong(this.spliceEventId);
            parcel.writeByte((byte) this.spliceEventCancelIndicator);
            parcel.writeByte((byte) this.outOfNetworkIndicator);
            parcel.writeByte((byte) this.programSpliceFlag);
            int size = this.componentSpliceList.size();
            parcel.writeInt(size);
            for (int i = 0; i < size; i++) {
                ((ComponentSplice) this.componentSpliceList.get(i)).writeToParcel(parcel);
            }
            parcel.writeLong(this.utcSpliceTime);
            parcel.writeByte((byte) this.autoReturn);
            parcel.writeLong(this.breakDurationUs);
            parcel.writeInt(this.uniqueProgramId);
            parcel.writeInt(this.availNum);
            parcel.writeInt(this.availsExpected);
        }

        private static Event createFromParcel(Parcel parcel) {
            return new Event(parcel);
        }
    }

    private SpliceScheduleCommand(List<Event> list) {
        this.events = Collections.unmodifiableList(list);
    }

    private SpliceScheduleCommand(Parcel parcel) {
        int readInt = parcel.readInt();
        List arrayList = new ArrayList(readInt);
        for (int i = 0; i < readInt; i++) {
            arrayList.add(Event.createFromParcel(parcel));
        }
        this.events = Collections.unmodifiableList(arrayList);
    }

    static SpliceScheduleCommand parseFromSection(ParsableByteArray parsableByteArray) {
        int readUnsignedByte = parsableByteArray.readUnsignedByte();
        List arrayList = new ArrayList(readUnsignedByte);
        for (int i = 0; i < readUnsignedByte; i++) {
            arrayList.add(Event.parseFromSection(parsableByteArray));
        }
        return new SpliceScheduleCommand(arrayList);
    }

    public void writeToParcel(Parcel parcel, int i) {
        i = this.events.size();
        parcel.writeInt(i);
        for (int i2 = 0; i2 < i; i2++) {
            ((Event) this.events.get(i2)).writeToParcel(parcel);
        }
    }
}

package org.telegram.messenger.exoplayer2.metadata.scte35;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0539C;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class SpliceScheduleCommand extends SpliceCommand {
    public static final Creator<SpliceScheduleCommand> CREATOR = new C05881();
    public final List<Event> events;

    /* renamed from: org.telegram.messenger.exoplayer2.metadata.scte35.SpliceScheduleCommand$1 */
    static class C05881 implements Creator<SpliceScheduleCommand> {
        C05881() {
        }

        public SpliceScheduleCommand createFromParcel(Parcel in) {
            return new SpliceScheduleCommand(in);
        }

        public SpliceScheduleCommand[] newArray(int size) {
            return new SpliceScheduleCommand[size];
        }
    }

    public static final class ComponentSplice {
        public final int componentTag;
        public final long utcSpliceTime;

        private ComponentSplice(int componentTag, long utcSpliceTime) {
            this.componentTag = componentTag;
            this.utcSpliceTime = utcSpliceTime;
        }

        private static ComponentSplice createFromParcel(Parcel in) {
            return new ComponentSplice(in.readInt(), in.readLong());
        }

        private void writeToParcel(Parcel dest) {
            dest.writeInt(this.componentTag);
            dest.writeLong(this.utcSpliceTime);
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

        private Event(long spliceEventId, boolean spliceEventCancelIndicator, boolean outOfNetworkIndicator, boolean programSpliceFlag, List<ComponentSplice> componentSpliceList, long utcSpliceTime, boolean autoReturn, long breakDurationUs, int uniqueProgramId, int availNum, int availsExpected) {
            this.spliceEventId = spliceEventId;
            this.spliceEventCancelIndicator = spliceEventCancelIndicator;
            this.outOfNetworkIndicator = outOfNetworkIndicator;
            this.programSpliceFlag = programSpliceFlag;
            this.componentSpliceList = Collections.unmodifiableList(componentSpliceList);
            this.utcSpliceTime = utcSpliceTime;
            this.autoReturn = autoReturn;
            this.breakDurationUs = breakDurationUs;
            this.uniqueProgramId = uniqueProgramId;
            this.availNum = availNum;
            this.availsExpected = availsExpected;
        }

        private Event(Parcel in) {
            this.spliceEventId = in.readLong();
            boolean z = false;
            this.spliceEventCancelIndicator = in.readByte() == (byte) 1;
            this.outOfNetworkIndicator = in.readByte() == (byte) 1;
            this.programSpliceFlag = in.readByte() == (byte) 1;
            int componentSpliceListLength = in.readInt();
            ArrayList<ComponentSplice> componentSpliceList = new ArrayList(componentSpliceListLength);
            for (int i = 0; i < componentSpliceListLength; i++) {
                componentSpliceList.add(ComponentSplice.createFromParcel(in));
            }
            this.componentSpliceList = Collections.unmodifiableList(componentSpliceList);
            this.utcSpliceTime = in.readLong();
            if (in.readByte() == (byte) 1) {
                z = true;
            }
            this.autoReturn = z;
            this.breakDurationUs = in.readLong();
            this.uniqueProgramId = in.readInt();
            this.availNum = in.readInt();
            this.availsExpected = in.readInt();
        }

        private static Event parseFromSection(ParsableByteArray sectionData) {
            boolean outOfNetworkIndicator;
            boolean programSpliceFlag;
            long utcSpliceTime;
            ArrayList<ComponentSplice> componentSplices;
            int uniqueProgramId;
            int availNum;
            int availsExpected;
            long spliceEventId = sectionData.readUnsignedInt();
            boolean spliceEventCancelIndicator = (sectionData.readUnsignedByte() & 128) != 0;
            long utcSpliceTime2 = C0539C.TIME_UNSET;
            ArrayList<ComponentSplice> componentSplices2 = new ArrayList();
            boolean autoReturn = false;
            long breakDurationUs = C0539C.TIME_UNSET;
            if (spliceEventCancelIndicator) {
                outOfNetworkIndicator = false;
                programSpliceFlag = false;
                utcSpliceTime = C0539C.TIME_UNSET;
                componentSplices = componentSplices2;
                uniqueProgramId = 0;
                availNum = 0;
                availsExpected = 0;
            } else {
                int headerByte = sectionData.readUnsignedByte();
                boolean outOfNetworkIndicator2 = (headerByte & 128) != 0;
                boolean programSpliceFlag2 = (headerByte & 64) != 0;
                boolean durationFlag = (headerByte & 32) != 0;
                if (programSpliceFlag2) {
                    utcSpliceTime2 = sectionData.readUnsignedInt();
                }
                if (!programSpliceFlag2) {
                    int componentCount = sectionData.readUnsignedByte();
                    componentSplices2 = new ArrayList(componentCount);
                    int i = 0;
                    while (i < componentCount) {
                        outOfNetworkIndicator = outOfNetworkIndicator2;
                        programSpliceFlag = programSpliceFlag2;
                        utcSpliceTime = utcSpliceTime2;
                        int componentCount2 = componentCount;
                        componentSplices2.add(new ComponentSplice(sectionData.readUnsignedByte(), sectionData.readUnsignedInt()));
                        i++;
                        outOfNetworkIndicator2 = outOfNetworkIndicator;
                        programSpliceFlag2 = programSpliceFlag;
                        utcSpliceTime2 = utcSpliceTime;
                        componentCount = componentCount2;
                    }
                }
                outOfNetworkIndicator = outOfNetworkIndicator2;
                programSpliceFlag = programSpliceFlag2;
                utcSpliceTime = utcSpliceTime2;
                if (durationFlag) {
                    outOfNetworkIndicator2 = (long) sectionData.readUnsignedByte();
                    autoReturn = (outOfNetworkIndicator2 & 128) != 0;
                    breakDurationUs = (1000 * (((outOfNetworkIndicator2 & 1) << 32) | sectionData.readUnsignedInt())) / 90;
                }
                uniqueProgramId = sectionData.readUnsignedShort();
                availNum = sectionData.readUnsignedByte();
                availsExpected = sectionData.readUnsignedByte();
                componentSplices = componentSplices2;
            }
            return new Event(spliceEventId, spliceEventCancelIndicator, outOfNetworkIndicator, programSpliceFlag, componentSplices, utcSpliceTime, autoReturn, breakDurationUs, uniqueProgramId, availNum, availsExpected);
        }

        private void writeToParcel(Parcel dest) {
            dest.writeLong(this.spliceEventId);
            dest.writeByte((byte) this.spliceEventCancelIndicator);
            dest.writeByte((byte) this.outOfNetworkIndicator);
            dest.writeByte((byte) this.programSpliceFlag);
            int componentSpliceListSize = this.componentSpliceList.size();
            dest.writeInt(componentSpliceListSize);
            for (int i = 0; i < componentSpliceListSize; i++) {
                ((ComponentSplice) this.componentSpliceList.get(i)).writeToParcel(dest);
            }
            dest.writeLong(this.utcSpliceTime);
            dest.writeByte((byte) this.autoReturn);
            dest.writeLong(this.breakDurationUs);
            dest.writeInt(this.uniqueProgramId);
            dest.writeInt(this.availNum);
            dest.writeInt(this.availsExpected);
        }

        private static Event createFromParcel(Parcel in) {
            return new Event(in);
        }
    }

    private SpliceScheduleCommand(List<Event> events) {
        this.events = Collections.unmodifiableList(events);
    }

    private SpliceScheduleCommand(Parcel in) {
        int eventsSize = in.readInt();
        ArrayList<Event> events = new ArrayList(eventsSize);
        for (int i = 0; i < eventsSize; i++) {
            events.add(Event.createFromParcel(in));
        }
        this.events = Collections.unmodifiableList(events);
    }

    static SpliceScheduleCommand parseFromSection(ParsableByteArray sectionData) {
        int spliceCount = sectionData.readUnsignedByte();
        List events = new ArrayList(spliceCount);
        for (int i = 0; i < spliceCount; i++) {
            events.add(Event.parseFromSection(sectionData));
        }
        return new SpliceScheduleCommand(events);
    }

    public void writeToParcel(Parcel dest, int flags) {
        int eventsSize = this.events.size();
        dest.writeInt(eventsSize);
        for (int i = 0; i < eventsSize; i++) {
            ((Event) this.events.get(i)).writeToParcel(dest);
        }
    }
}

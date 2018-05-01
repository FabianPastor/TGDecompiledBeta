package org.telegram.messenger.exoplayer2.metadata.scte35;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        public SpliceInsertCommand createFromParcel(Parcel in) {
            return new SpliceInsertCommand(in);
        }

        public SpliceInsertCommand[] newArray(int size) {
            return new SpliceInsertCommand[size];
        }
    }

    public static final class ComponentSplice {
        public final long componentSplicePlaybackPositionUs;
        public final long componentSplicePts;
        public final int componentTag;

        private ComponentSplice(int componentTag, long componentSplicePts, long componentSplicePlaybackPositionUs) {
            this.componentTag = componentTag;
            this.componentSplicePts = componentSplicePts;
            this.componentSplicePlaybackPositionUs = componentSplicePlaybackPositionUs;
        }

        public void writeToParcel(Parcel dest) {
            dest.writeInt(this.componentTag);
            dest.writeLong(this.componentSplicePts);
            dest.writeLong(this.componentSplicePlaybackPositionUs);
        }

        public static ComponentSplice createFromParcel(Parcel in) {
            return new ComponentSplice(in.readInt(), in.readLong(), in.readLong());
        }
    }

    private SpliceInsertCommand(long spliceEventId, boolean spliceEventCancelIndicator, boolean outOfNetworkIndicator, boolean programSpliceFlag, boolean spliceImmediateFlag, long programSplicePts, long programSplicePlaybackPositionUs, List<ComponentSplice> componentSpliceList, boolean autoReturn, long breakDurationUs, int uniqueProgramId, int availNum, int availsExpected) {
        this.spliceEventId = spliceEventId;
        this.spliceEventCancelIndicator = spliceEventCancelIndicator;
        this.outOfNetworkIndicator = outOfNetworkIndicator;
        this.programSpliceFlag = programSpliceFlag;
        this.spliceImmediateFlag = spliceImmediateFlag;
        this.programSplicePts = programSplicePts;
        this.programSplicePlaybackPositionUs = programSplicePlaybackPositionUs;
        this.componentSpliceList = Collections.unmodifiableList(componentSpliceList);
        this.autoReturn = autoReturn;
        this.breakDurationUs = breakDurationUs;
        this.uniqueProgramId = uniqueProgramId;
        this.availNum = availNum;
        this.availsExpected = availsExpected;
    }

    private SpliceInsertCommand(Parcel in) {
        boolean z;
        boolean z2 = true;
        this.spliceEventId = in.readLong();
        if (in.readByte() == (byte) 1) {
            z = true;
        } else {
            z = false;
        }
        this.spliceEventCancelIndicator = z;
        if (in.readByte() == (byte) 1) {
            z = true;
        } else {
            z = false;
        }
        this.outOfNetworkIndicator = z;
        if (in.readByte() == (byte) 1) {
            z = true;
        } else {
            z = false;
        }
        this.programSpliceFlag = z;
        if (in.readByte() == (byte) 1) {
            z = true;
        } else {
            z = false;
        }
        this.spliceImmediateFlag = z;
        this.programSplicePts = in.readLong();
        this.programSplicePlaybackPositionUs = in.readLong();
        int componentSpliceListSize = in.readInt();
        List<ComponentSplice> componentSpliceList = new ArrayList(componentSpliceListSize);
        for (int i = 0; i < componentSpliceListSize; i++) {
            componentSpliceList.add(ComponentSplice.createFromParcel(in));
        }
        this.componentSpliceList = Collections.unmodifiableList(componentSpliceList);
        if (in.readByte() != (byte) 1) {
            z2 = false;
        }
        this.autoReturn = z2;
        this.breakDurationUs = in.readLong();
        this.uniqueProgramId = in.readInt();
        this.availNum = in.readInt();
        this.availsExpected = in.readInt();
    }

    static org.telegram.messenger.exoplayer2.metadata.scte35.SpliceInsertCommand parseFromSection(org.telegram.messenger.exoplayer2.util.ParsableByteArray r37, long r38, org.telegram.messenger.exoplayer2.util.TimestampAdjuster r40) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r18_1 java.util.List<org.telegram.messenger.exoplayer2.metadata.scte35.SpliceInsertCommand$ComponentSplice>) in PHI: PHI: (r18_2 'componentSplices' java.util.List<org.telegram.messenger.exoplayer2.metadata.scte35.SpliceInsertCommand$ComponentSplice>) = (r18_0 'componentSplices' java.util.List<org.telegram.messenger.exoplayer2.metadata.scte35.SpliceInsertCommand$ComponentSplice>), (r18_1 java.util.List<org.telegram.messenger.exoplayer2.metadata.scte35.SpliceInsertCommand$ComponentSplice>) binds: {(r18_0 'componentSplices' java.util.List<org.telegram.messenger.exoplayer2.metadata.scte35.SpliceInsertCommand$ComponentSplice>)=B:20:0x004b, (r18_1 java.util.List<org.telegram.messenger.exoplayer2.metadata.scte35.SpliceInsertCommand$ComponentSplice>)=B:42:0x0090}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r34 = r37.readUnsignedInt();
        r2 = r37.readUnsignedByte();
        r2 = r2 & 128;
        if (r2 == 0) goto L_0x0085;
    L_0x000c:
        r10 = 1;
    L_0x000d:
        r11 = 0;
        r12 = 0;
        r13 = 0;
        r14 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
        r18 = java.util.Collections.emptyList();
        r22 = 0;
        r23 = 0;
        r24 = 0;
        r19 = 0;
        r20 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
        if (r10 != 0) goto L_0x00c6;
    L_0x0028:
        r29 = r37.readUnsignedByte();
        r0 = r29;
        r2 = r0 & 128;
        if (r2 == 0) goto L_0x0087;
    L_0x0032:
        r11 = 1;
    L_0x0033:
        r2 = r29 & 64;
        if (r2 == 0) goto L_0x0089;
    L_0x0037:
        r12 = 1;
    L_0x0038:
        r2 = r29 & 32;
        if (r2 == 0) goto L_0x008b;
    L_0x003c:
        r28 = 1;
    L_0x003e:
        r2 = r29 & 16;
        if (r2 == 0) goto L_0x008e;
    L_0x0042:
        r13 = 1;
    L_0x0043:
        if (r12 == 0) goto L_0x004b;
    L_0x0045:
        if (r13 != 0) goto L_0x004b;
    L_0x0047:
        r14 = org.telegram.messenger.exoplayer2.metadata.scte35.TimeSignalCommand.parseSpliceTime(r37, r38);
    L_0x004b:
        if (r12 != 0) goto L_0x0090;
    L_0x004d:
        r25 = r37.readUnsignedByte();
        r18 = new java.util.ArrayList;
        r0 = r18;
        r1 = r25;
        r0.<init>(r1);
        r32 = 0;
    L_0x005c:
        r0 = r32;
        r1 = r25;
        if (r0 >= r1) goto L_0x0090;
    L_0x0062:
        r3 = r37.readUnsignedByte();
        r4 = -922337203NUM; // 0x800000NUM float:1.4E-45 double:-4.9E-324;
        if (r13 != 0) goto L_0x0071;
    L_0x006d:
        r4 = org.telegram.messenger.exoplayer2.metadata.scte35.TimeSignalCommand.parseSpliceTime(r37, r38);
    L_0x0071:
        r2 = new org.telegram.messenger.exoplayer2.metadata.scte35.SpliceInsertCommand$ComponentSplice;
        r0 = r40;
        r6 = r0.adjustTsTimestamp(r4);
        r8 = 0;
        r2.<init>(r3, r4, r6);
        r0 = r18;
        r0.add(r2);
        r32 = r32 + 1;
        goto L_0x005c;
    L_0x0085:
        r10 = 0;
        goto L_0x000d;
    L_0x0087:
        r11 = 0;
        goto L_0x0033;
    L_0x0089:
        r12 = 0;
        goto L_0x0038;
    L_0x008b:
        r28 = 0;
        goto L_0x003e;
    L_0x008e:
        r13 = 0;
        goto L_0x0043;
    L_0x0090:
        if (r28 == 0) goto L_0x00ba;
    L_0x0092:
        r2 = r37.readUnsignedByte();
        r0 = (long) r2;
        r30 = r0;
        r6 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
        r6 = r6 & r30;
        r8 = 0;
        r2 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r2 == 0) goto L_0x00d4;
    L_0x00a3:
        r19 = 1;
    L_0x00a5:
        r6 = 1;
        r6 = r6 & r30;
        r2 = 32;
        r6 = r6 << r2;
        r8 = r37.readUnsignedInt();
        r26 = r6 | r8;
        r6 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r6 = r6 * r26;
        r8 = 90;
        r20 = r6 / r8;
    L_0x00ba:
        r22 = r37.readUnsignedShort();
        r23 = r37.readUnsignedByte();
        r24 = r37.readUnsignedByte();
    L_0x00c6:
        r7 = new org.telegram.messenger.exoplayer2.metadata.scte35.SpliceInsertCommand;
        r0 = r40;
        r16 = r0.adjustTsTimestamp(r14);
        r8 = r34;
        r7.<init>(r8, r10, r11, r12, r13, r14, r16, r18, r19, r20, r22, r23, r24);
        return r7;
    L_0x00d4:
        r19 = 0;
        goto L_0x00a5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.metadata.scte35.SpliceInsertCommand.parseFromSection(org.telegram.messenger.exoplayer2.util.ParsableByteArray, long, org.telegram.messenger.exoplayer2.util.TimestampAdjuster):org.telegram.messenger.exoplayer2.metadata.scte35.SpliceInsertCommand");
    }

    public void writeToParcel(Parcel dest, int flags) {
        int i;
        int i2 = 1;
        dest.writeLong(this.spliceEventId);
        if (this.spliceEventCancelIndicator) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeByte((byte) i);
        if (this.outOfNetworkIndicator) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeByte((byte) i);
        if (this.programSpliceFlag) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeByte((byte) i);
        if (this.spliceImmediateFlag) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeByte((byte) i);
        dest.writeLong(this.programSplicePts);
        dest.writeLong(this.programSplicePlaybackPositionUs);
        int componentSpliceListSize = this.componentSpliceList.size();
        dest.writeInt(componentSpliceListSize);
        for (int i3 = 0; i3 < componentSpliceListSize; i3++) {
            ((ComponentSplice) this.componentSpliceList.get(i3)).writeToParcel(dest);
        }
        if (!this.autoReturn) {
            i2 = 0;
        }
        dest.writeByte((byte) i2);
        dest.writeLong(this.breakDurationUs);
        dest.writeInt(this.uniqueProgramId);
        dest.writeInt(this.availNum);
        dest.writeInt(this.availsExpected);
    }
}

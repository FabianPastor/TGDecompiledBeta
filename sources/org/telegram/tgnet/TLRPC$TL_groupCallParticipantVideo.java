package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_groupCallParticipantVideo extends TLObject {
    public static int constructor = NUM;
    public int audio_source;
    public String endpoint;
    public int flags;
    public boolean paused;
    public ArrayList<TLRPC$TL_groupCallParticipantVideoSourceGroup> source_groups = new ArrayList<>();

    public static TLRPC$TL_groupCallParticipantVideo TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_groupCallParticipantVideo tLRPC$TL_groupCallParticipantVideo = new TLRPC$TL_groupCallParticipantVideo();
            tLRPC$TL_groupCallParticipantVideo.readParams(abstractSerializedData, z);
            return tLRPC$TL_groupCallParticipantVideo;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_groupCallParticipantVideo", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        int i = 0;
        this.paused = (readInt32 & 1) != 0;
        this.endpoint = abstractSerializedData.readString(z);
        int readInt322 = abstractSerializedData.readInt32(z);
        if (readInt322 == NUM) {
            int readInt323 = abstractSerializedData.readInt32(z);
            while (i < readInt323) {
                TLRPC$TL_groupCallParticipantVideoSourceGroup TLdeserialize = TLRPC$TL_groupCallParticipantVideoSourceGroup.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.source_groups.add(TLdeserialize);
                    i++;
                } else {
                    return;
                }
            }
            if ((this.flags & 2) != 0) {
                this.audio_source = abstractSerializedData.readInt32(z);
            }
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt322)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.paused ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        abstractSerializedData.writeString(this.endpoint);
        abstractSerializedData.writeInt32(NUM);
        int size = this.source_groups.size();
        abstractSerializedData.writeInt32(size);
        for (int i2 = 0; i2 < size; i2++) {
            this.source_groups.get(i2).serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeInt32(this.audio_source);
        }
    }
}

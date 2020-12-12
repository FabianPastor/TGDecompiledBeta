package org.telegram.tgnet;

public class TLRPC$TL_groupCallParticipant extends TLObject {
    public static int constructor = NUM;
    public int active_date;
    public float amplitude;
    public boolean can_self_unmute;
    public int date;
    public int flags;
    public boolean hasVoice;
    public boolean just_joined;
    public long lastSpeakTime;
    public boolean left;
    public boolean muted;
    public int source;
    public int user_id;

    public static TLRPC$TL_groupCallParticipant TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = new TLRPC$TL_groupCallParticipant();
            tLRPC$TL_groupCallParticipant.readParams(abstractSerializedData, z);
            return tLRPC$TL_groupCallParticipant;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_groupCallParticipant", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = false;
        this.muted = (readInt32 & 1) != 0;
        this.left = (readInt32 & 2) != 0;
        this.can_self_unmute = (readInt32 & 4) != 0;
        if ((readInt32 & 16) != 0) {
            z2 = true;
        }
        this.just_joined = z2;
        this.user_id = abstractSerializedData.readInt32(z);
        this.date = abstractSerializedData.readInt32(z);
        if ((this.flags & 8) != 0) {
            this.active_date = abstractSerializedData.readInt32(z);
        }
        this.source = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.muted ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.left ? i | 2 : i & -3;
        this.flags = i2;
        int i3 = this.can_self_unmute ? i2 | 4 : i2 & -5;
        this.flags = i3;
        int i4 = this.just_joined ? i3 | 16 : i3 & -17;
        this.flags = i4;
        abstractSerializedData.writeInt32(i4);
        abstractSerializedData.writeInt32(this.user_id);
        abstractSerializedData.writeInt32(this.date);
        if ((this.flags & 8) != 0) {
            abstractSerializedData.writeInt32(this.active_date);
        }
        abstractSerializedData.writeInt32(this.source);
    }
}

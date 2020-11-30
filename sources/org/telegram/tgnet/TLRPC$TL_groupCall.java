package org.telegram.tgnet;

public class TLRPC$TL_groupCall extends TLRPC$GroupCall {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = false;
        this.join_muted = (readInt32 & 2) != 0;
        if ((readInt32 & 4) != 0) {
            z2 = true;
        }
        this.can_change_join_muted = z2;
        this.id = abstractSerializedData.readInt64(z);
        this.access_hash = abstractSerializedData.readInt64(z);
        this.participants_count = abstractSerializedData.readInt32(z);
        if ((this.flags & 1) != 0) {
            this.params = TLRPC$TL_dataJSON.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        this.version = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.join_muted ? this.flags | 2 : this.flags & -3;
        this.flags = i;
        int i2 = this.can_change_join_muted ? i | 4 : i & -5;
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
        abstractSerializedData.writeInt64(this.id);
        abstractSerializedData.writeInt64(this.access_hash);
        abstractSerializedData.writeInt32(this.participants_count);
        if ((this.flags & 1) != 0) {
            this.params.serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(this.version);
    }
}

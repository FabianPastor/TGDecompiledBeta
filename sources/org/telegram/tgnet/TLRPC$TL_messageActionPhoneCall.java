package org.telegram.tgnet;

public class TLRPC$TL_messageActionPhoneCall extends TLRPC$MessageAction {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        this.video = (readInt32 & 4) != 0;
        this.call_id = abstractSerializedData.readInt64(z);
        if ((this.flags & 1) != 0) {
            this.reason = TLRPC$PhoneCallDiscardReason.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 2) != 0) {
            this.duration = abstractSerializedData.readInt32(z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.video ? this.flags | 4 : this.flags & -5;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        abstractSerializedData.writeInt64(this.call_id);
        if ((this.flags & 1) != 0) {
            this.reason.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeInt32(this.duration);
        }
    }
}

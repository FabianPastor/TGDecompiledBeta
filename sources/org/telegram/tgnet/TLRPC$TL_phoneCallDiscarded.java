package org.telegram.tgnet;

public class TLRPC$TL_phoneCallDiscarded extends TLRPC$PhoneCall {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = false;
        this.need_rating = (readInt32 & 4) != 0;
        this.need_debug = (this.flags & 8) != 0;
        if ((this.flags & 64) != 0) {
            z2 = true;
        }
        this.video = z2;
        this.id = abstractSerializedData.readInt64(z);
        if ((this.flags & 1) != 0) {
            this.reason = TLRPC$PhoneCallDiscardReason.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 2) != 0) {
            this.duration = abstractSerializedData.readInt32(z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.need_rating ? this.flags | 4 : this.flags & -5;
        this.flags = i;
        int i2 = this.need_debug ? i | 8 : i & -9;
        this.flags = i2;
        int i3 = this.video ? i2 | 64 : i2 & -65;
        this.flags = i3;
        abstractSerializedData.writeInt32(i3);
        abstractSerializedData.writeInt64(this.id);
        if ((this.flags & 1) != 0) {
            this.reason.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeInt32(this.duration);
        }
    }
}

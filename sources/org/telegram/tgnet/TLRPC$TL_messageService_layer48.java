package org.telegram.tgnet;

public class TLRPC$TL_messageService_layer48 extends TLRPC$TL_messageService {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        this.unread = (readInt32 & 1) != 0;
        this.out = (this.flags & 2) != 0;
        this.mentioned = (this.flags & 16) != 0;
        this.media_unread = (this.flags & 32) != 0;
        this.silent = (this.flags & 8192) != 0;
        if ((this.flags & 16384) == 0) {
            z2 = false;
        }
        this.post = z2;
        this.id = abstractSerializedData.readInt32(z);
        if ((this.flags & 256) != 0) {
            this.from_id = abstractSerializedData.readInt32(z);
        }
        TLRPC$Peer TLdeserialize = TLRPC$Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.to_id = TLdeserialize;
        if (this.from_id == 0) {
            int i = TLdeserialize.user_id;
            if (i != 0) {
                this.from_id = i;
            } else {
                this.from_id = -TLdeserialize.channel_id;
            }
        }
        this.date = abstractSerializedData.readInt32(z);
        this.action = TLRPC$MessageAction.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.unread ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.out ? i | 2 : i & -3;
        this.flags = i2;
        int i3 = this.mentioned ? i2 | 16 : i2 & -17;
        this.flags = i3;
        int i4 = this.media_unread ? i3 | 32 : i3 & -33;
        this.flags = i4;
        int i5 = this.silent ? i4 | 8192 : i4 & -8193;
        this.flags = i5;
        int i6 = this.post ? i5 | 16384 : i5 & -16385;
        this.flags = i6;
        abstractSerializedData.writeInt32(i6);
        abstractSerializedData.writeInt32(this.id);
        if ((this.flags & 256) != 0) {
            abstractSerializedData.writeInt32(this.from_id);
        }
        this.to_id.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.date);
        this.action.serializeToStream(abstractSerializedData);
    }
}

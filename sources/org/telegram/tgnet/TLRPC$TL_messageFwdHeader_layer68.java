package org.telegram.tgnet;

public class TLRPC$TL_messageFwdHeader_layer68 extends TLRPC$TL_messageFwdHeader {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        if ((readInt32 & 1) != 0) {
            TLRPC$TL_peerChannel tLRPC$TL_peerChannel = new TLRPC$TL_peerChannel();
            this.from_id = tLRPC$TL_peerChannel;
            tLRPC$TL_peerChannel.user_id = (long) abstractSerializedData.readInt32(z);
        }
        this.date = abstractSerializedData.readInt32(z);
        if ((this.flags & 2) != 0) {
            TLRPC$TL_peerChannel tLRPC$TL_peerChannel2 = new TLRPC$TL_peerChannel();
            this.from_id = tLRPC$TL_peerChannel2;
            tLRPC$TL_peerChannel2.channel_id = (long) abstractSerializedData.readInt32(z);
        }
        if ((this.flags & 4) != 0) {
            this.channel_post = abstractSerializedData.readInt32(z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt32((int) this.from_id.user_id);
        }
        abstractSerializedData.writeInt32(this.date);
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeInt32((int) this.from_id.channel_id);
        }
        if ((this.flags & 4) != 0) {
            abstractSerializedData.writeInt32(this.channel_post);
        }
    }
}

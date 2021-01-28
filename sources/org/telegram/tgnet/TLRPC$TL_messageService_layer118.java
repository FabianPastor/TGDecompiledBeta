package org.telegram.tgnet;

public class TLRPC$TL_messageService_layer118 extends TLRPC$TL_messageService {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = false;
        this.unread = (readInt32 & 1) != 0;
        this.out = (readInt32 & 2) != 0;
        this.mentioned = (readInt32 & 16) != 0;
        this.media_unread = (readInt32 & 32) != 0;
        this.silent = (readInt32 & 8192) != 0;
        this.post = (readInt32 & 16384) != 0;
        if ((readInt32 & 524288) != 0) {
            z2 = true;
        }
        this.legacy = z2;
        this.id = abstractSerializedData.readInt32(z);
        if ((this.flags & 256) != 0) {
            TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
            this.from_id = tLRPC$TL_peerUser;
            tLRPC$TL_peerUser.user_id = abstractSerializedData.readInt32(z);
        }
        this.peer_id = TLRPC$Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        if ((this.flags & 8) != 0) {
            TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader = new TLRPC$TL_messageReplyHeader();
            this.reply_to = tLRPC$TL_messageReplyHeader;
            tLRPC$TL_messageReplyHeader.reply_to_msg_id = abstractSerializedData.readInt32(z);
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
        int i7 = this.legacy ? i6 | 524288 : i6 & -524289;
        this.flags = i7;
        abstractSerializedData.writeInt32(i7);
        abstractSerializedData.writeInt32(this.id);
        if ((this.flags & 256) != 0) {
            abstractSerializedData.writeInt32(this.from_id.user_id);
        }
        this.peer_id.serializeToStream(abstractSerializedData);
        if ((this.flags & 8) != 0) {
            abstractSerializedData.writeInt32(this.reply_to.reply_to_msg_id);
        }
        abstractSerializedData.writeInt32(this.date);
        this.action.serializeToStream(abstractSerializedData);
        writeAttachPath(abstractSerializedData);
    }
}

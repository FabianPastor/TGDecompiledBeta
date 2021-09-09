package org.telegram.tgnet;

import android.text.TextUtils;

public class TLRPC$TL_messageForwarded_old extends TLRPC$TL_messageForwarded_old2 {
    public static int constructor = 99903492;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.id = abstractSerializedData.readInt32(z);
        TLRPC$TL_messageFwdHeader tLRPC$TL_messageFwdHeader = new TLRPC$TL_messageFwdHeader();
        this.fwd_from = tLRPC$TL_messageFwdHeader;
        tLRPC$TL_messageFwdHeader.from_id = new TLRPC$TL_peerUser();
        this.fwd_from.from_id.user_id = (long) abstractSerializedData.readInt32(z);
        TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = this.fwd_from;
        tLRPC$MessageFwdHeader.flags |= 1;
        tLRPC$MessageFwdHeader.date = abstractSerializedData.readInt32(z);
        TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
        this.from_id = tLRPC$TL_peerUser;
        tLRPC$TL_peerUser.user_id = (long) abstractSerializedData.readInt32(z);
        this.peer_id = TLRPC$Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.out = abstractSerializedData.readBool(z);
        this.unread = abstractSerializedData.readBool(z);
        this.flags |= 772;
        this.date = abstractSerializedData.readInt32(z);
        this.message = abstractSerializedData.readString(z);
        TLRPC$MessageMedia TLdeserialize = TLRPC$MessageMedia.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.media = TLdeserialize;
        if (TLdeserialize != null && !TextUtils.isEmpty(TLdeserialize.captionLegacy)) {
            this.message = this.media.captionLegacy;
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.id);
        abstractSerializedData.writeInt32((int) this.fwd_from.from_id.user_id);
        abstractSerializedData.writeInt32(this.fwd_from.date);
        abstractSerializedData.writeInt32((int) this.from_id.user_id);
        this.peer_id.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeBool(this.out);
        abstractSerializedData.writeBool(this.unread);
        abstractSerializedData.writeInt32(this.date);
        abstractSerializedData.writeString(this.message);
        this.media.serializeToStream(abstractSerializedData);
        writeAttachPath(abstractSerializedData);
    }
}

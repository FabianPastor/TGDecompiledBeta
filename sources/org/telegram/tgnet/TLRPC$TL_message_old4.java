package org.telegram.tgnet;

import android.text.TextUtils;

public class TLRPC$TL_message_old4 extends TLRPC$TL_message {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z) | 256 | 512;
        this.flags = readInt32;
        boolean z2 = false;
        this.unread = (readInt32 & 1) != 0;
        this.out = (this.flags & 2) != 0;
        this.mentioned = (this.flags & 16) != 0;
        if ((this.flags & 32) != 0) {
            z2 = true;
        }
        this.media_unread = z2;
        this.id = abstractSerializedData.readInt32(z);
        this.from_id = abstractSerializedData.readInt32(z);
        this.to_id = TLRPC$Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        if ((this.flags & 4) != 0) {
            TLRPC$TL_messageFwdHeader tLRPC$TL_messageFwdHeader = new TLRPC$TL_messageFwdHeader();
            this.fwd_from = tLRPC$TL_messageFwdHeader;
            tLRPC$TL_messageFwdHeader.from_id = abstractSerializedData.readInt32(z);
            TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = this.fwd_from;
            tLRPC$MessageFwdHeader.flags = 1 | tLRPC$MessageFwdHeader.flags;
            tLRPC$MessageFwdHeader.date = abstractSerializedData.readInt32(z);
        }
        if ((this.flags & 8) != 0) {
            this.reply_to_msg_id = abstractSerializedData.readInt32(z);
        }
        this.date = abstractSerializedData.readInt32(z);
        this.message = abstractSerializedData.readString(z);
        TLRPC$MessageMedia TLdeserialize = TLRPC$MessageMedia.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.media = TLdeserialize;
        if (TLdeserialize != null && !TextUtils.isEmpty(TLdeserialize.captionLegacy)) {
            this.message = this.media.captionLegacy;
        }
        if ((this.flags & 64) != 0) {
            this.reply_markup = TLRPC$ReplyMarkup.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
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
        abstractSerializedData.writeInt32(i4);
        abstractSerializedData.writeInt32(this.id);
        abstractSerializedData.writeInt32(this.from_id);
        this.to_id.serializeToStream(abstractSerializedData);
        if ((this.flags & 4) != 0) {
            abstractSerializedData.writeInt32(this.fwd_from.from_id);
            abstractSerializedData.writeInt32(this.fwd_from.date);
        }
        if ((this.flags & 8) != 0) {
            abstractSerializedData.writeInt32(this.reply_to_msg_id);
        }
        abstractSerializedData.writeInt32(this.date);
        abstractSerializedData.writeString(this.message);
        this.media.serializeToStream(abstractSerializedData);
        if ((this.flags & 64) != 0) {
            this.reply_markup.serializeToStream(abstractSerializedData);
        }
        writeAttachPath(abstractSerializedData);
    }
}

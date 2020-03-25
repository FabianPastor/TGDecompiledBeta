package org.telegram.tgnet;

import android.text.TextUtils;

public class TLRPC$TL_message_old extends TLRPC$TL_message {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.id = abstractSerializedData.readInt32(z);
        this.from_id = abstractSerializedData.readInt32(z);
        this.to_id = TLRPC$Peer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.out = abstractSerializedData.readBool(z);
        this.unread = abstractSerializedData.readBool(z);
        this.flags |= 768;
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
        abstractSerializedData.writeInt32(this.from_id);
        this.to_id.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeBool(this.out);
        abstractSerializedData.writeBool(this.unread);
        abstractSerializedData.writeInt32(this.date);
        abstractSerializedData.writeString(this.message);
        this.media.serializeToStream(abstractSerializedData);
        writeAttachPath(abstractSerializedData);
    }
}

package org.telegram.tgnet;

public class TLRPC$TL_channelParticipantBanned_layer92 extends TLRPC$TL_channelParticipantBanned {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        if ((readInt32 & 1) == 0) {
            z2 = false;
        }
        this.left = z2;
        this.user_id = abstractSerializedData.readInt32(z);
        this.kicked_by = abstractSerializedData.readInt32(z);
        this.date = abstractSerializedData.readInt32(z);
        TLRPC$TL_channelBannedRights_layer92 TLdeserialize = TLRPC$TL_channelBannedRights_layer92.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.banned_rights_layer92 = TLdeserialize;
        this.banned_rights = TLRPC$Chat.mergeBannedRights(TLdeserialize);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.left ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        abstractSerializedData.writeInt32(i);
        abstractSerializedData.writeInt32(this.user_id);
        abstractSerializedData.writeInt32(this.kicked_by);
        abstractSerializedData.writeInt32(this.date);
        this.banned_rights_layer92.serializeToStream(abstractSerializedData);
    }
}

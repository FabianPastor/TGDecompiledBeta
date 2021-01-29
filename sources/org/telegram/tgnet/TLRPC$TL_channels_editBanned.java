package org.telegram.tgnet;

public class TLRPC$TL_channels_editBanned extends TLObject {
    public static int constructor = NUM;
    public TLRPC$TL_chatBannedRights banned_rights;
    public TLRPC$InputChannel channel;
    public TLRPC$InputUser user_id;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.channel.serializeToStream(abstractSerializedData);
        this.user_id.serializeToStream(abstractSerializedData);
        this.banned_rights.serializeToStream(abstractSerializedData);
    }
}

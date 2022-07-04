package org.telegram.tgnet;

public class TLRPC$TL_channels_getAdminedPublicChannels extends TLObject {
    public static int constructor = -NUM;
    public boolean by_location;
    public boolean check_limit;
    public int flags;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$messages_Chats.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.by_location ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.check_limit ? i | 2 : i & -3;
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
    }
}

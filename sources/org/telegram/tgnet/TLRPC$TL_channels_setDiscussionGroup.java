package org.telegram.tgnet;

public class TLRPC$TL_channels_setDiscussionGroup extends TLObject {
    public static int constructor = NUM;
    public TLRPC$InputChannel broadcast;
    public TLRPC$InputChannel group;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.broadcast.serializeToStream(abstractSerializedData);
        this.group.serializeToStream(abstractSerializedData);
    }
}

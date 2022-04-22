package org.telegram.tgnet;

public class TLRPC$TL_phone_getGroupCallStreamChannels extends TLObject {
    public static int constructor = NUM;
    public TLRPC$TL_inputGroupCall call;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_phone_groupCallStreamChannels.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.call.serializeToStream(abstractSerializedData);
    }
}

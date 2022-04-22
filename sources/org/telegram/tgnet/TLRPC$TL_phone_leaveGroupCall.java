package org.telegram.tgnet;

public class TLRPC$TL_phone_leaveGroupCall extends TLObject {
    public static int constructor = NUM;
    public TLRPC$TL_inputGroupCall call;
    public int source;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.call.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.source);
    }
}

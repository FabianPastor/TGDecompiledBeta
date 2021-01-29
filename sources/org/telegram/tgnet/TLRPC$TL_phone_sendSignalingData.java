package org.telegram.tgnet;

public class TLRPC$TL_phone_sendSignalingData extends TLObject {
    public static int constructor = -8744061;
    public byte[] data;
    public TLRPC$TL_inputPhoneCall peer;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeByteArray(this.data);
    }
}

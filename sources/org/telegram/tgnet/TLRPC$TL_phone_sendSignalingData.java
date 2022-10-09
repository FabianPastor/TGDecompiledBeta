package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_phone_sendSignalingData extends TLObject {
    public static int constructor = -8744061;
    public byte[] data;
    public TLRPC$TL_inputPhoneCall peer;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeByteArray(this.data);
    }
}

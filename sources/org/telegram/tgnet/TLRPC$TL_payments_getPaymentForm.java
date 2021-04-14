package org.telegram.tgnet;

public class TLRPC$TL_payments_getPaymentForm extends TLObject {
    public static int constructor = -NUM;
    public int flags;
    public int msg_id;
    public TLRPC$InputPeer peer;
    public TLRPC$TL_dataJSON theme_params;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_payments_paymentForm.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.msg_id);
        if ((this.flags & 1) != 0) {
            this.theme_params.serializeToStream(abstractSerializedData);
        }
    }
}

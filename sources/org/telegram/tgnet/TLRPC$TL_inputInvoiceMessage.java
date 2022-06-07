package org.telegram.tgnet;

public class TLRPC$TL_inputInvoiceMessage extends TLRPC$InputInvoice {
    public static int constructor = -NUM;
    public int msg_id;
    public TLRPC$InputPeer peer;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.peer = TLRPC$InputPeer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.msg_id = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.msg_id);
    }
}

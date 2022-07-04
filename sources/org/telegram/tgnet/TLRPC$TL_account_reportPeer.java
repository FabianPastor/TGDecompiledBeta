package org.telegram.tgnet;

public class TLRPC$TL_account_reportPeer extends TLObject {
    public static int constructor = -NUM;
    public String message;
    public TLRPC$InputPeer peer;
    public TLRPC$ReportReason reason;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
        this.reason.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeString(this.message);
    }
}

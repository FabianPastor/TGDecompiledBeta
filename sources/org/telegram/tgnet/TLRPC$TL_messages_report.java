package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_messages_report extends TLObject {
    public static int constructor = -NUM;
    public ArrayList<Integer> id = new ArrayList<>();
    public TLRPC$InputPeer peer;
    public TLRPC$ReportReason reason;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(NUM);
        int size = this.id.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            abstractSerializedData.writeInt32(this.id.get(i).intValue());
        }
        this.reason.serializeToStream(abstractSerializedData);
    }
}

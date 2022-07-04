package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_messages_getSearchCounters extends TLObject {
    public static int constructor = NUM;
    public ArrayList<TLRPC$MessagesFilter> filters = new ArrayList<>();
    public TLRPC$InputPeer peer;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$Vector tLRPC$Vector = new TLRPC$Vector();
        int readInt32 = abstractSerializedData.readInt32(z);
        for (int i2 = 0; i2 < readInt32; i2++) {
            TLRPC$TL_messages_searchCounter TLdeserialize = TLRPC$TL_messages_searchCounter.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if (TLdeserialize == null) {
                return tLRPC$Vector;
            }
            tLRPC$Vector.objects.add(TLdeserialize);
        }
        return tLRPC$Vector;
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.peer.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(NUM);
        int size = this.filters.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.filters.get(i).serializeToStream(abstractSerializedData);
        }
    }
}

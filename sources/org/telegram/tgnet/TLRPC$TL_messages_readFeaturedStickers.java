package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_messages_readFeaturedStickers extends TLObject {
    public static int constructor = NUM;
    public ArrayList<Long> id = new ArrayList<>();

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(NUM);
        int size = this.id.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            abstractSerializedData.writeInt64(this.id.get(i).longValue());
        }
    }
}

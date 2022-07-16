package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_messages_reorderStickerSets extends TLObject {
    public static int constructor = NUM;
    public boolean emojis;
    public int flags;
    public boolean masks;
    public ArrayList<Long> order = new ArrayList<>();

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.masks ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.emojis ? i | 2 : i & -3;
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
        abstractSerializedData.writeInt32(NUM);
        int size = this.order.size();
        abstractSerializedData.writeInt32(size);
        for (int i3 = 0; i3 < size; i3++) {
            abstractSerializedData.writeInt64(this.order.get(i3).longValue());
        }
    }
}

package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public class TLRPC$TL_account_reorderUsernames extends TLObject {
    public static int constructor = -NUM;
    public ArrayList<String> order = new ArrayList<>();

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(NUM);
        int size = this.order.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            abstractSerializedData.writeString(this.order.get(i));
        }
    }
}

package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_phone_getGroupParticipants extends TLObject {
    public static int constructor = -NUM;
    public TLRPC$TL_inputGroupCall call;
    public ArrayList<Integer> ids = new ArrayList<>();
    public int limit;
    public String offset;
    public ArrayList<Integer> sources = new ArrayList<>();

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_phone_groupParticipants.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.call.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(NUM);
        int size = this.ids.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            abstractSerializedData.writeInt32(this.ids.get(i).intValue());
        }
        abstractSerializedData.writeInt32(NUM);
        int size2 = this.sources.size();
        abstractSerializedData.writeInt32(size2);
        for (int i2 = 0; i2 < size2; i2++) {
            abstractSerializedData.writeInt32(this.sources.get(i2).intValue());
        }
        abstractSerializedData.writeString(this.offset);
        abstractSerializedData.writeInt32(this.limit);
    }
}

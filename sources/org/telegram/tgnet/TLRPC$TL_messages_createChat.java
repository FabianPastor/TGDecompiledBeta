package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_messages_createChat extends TLObject {
    public static int constructor = NUM;
    public String title;
    public ArrayList<TLRPC$InputUser> users = new ArrayList<>();

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Updates.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(NUM);
        int size = this.users.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.users.get(i).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeString(this.title);
    }
}

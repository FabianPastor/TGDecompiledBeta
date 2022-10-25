package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public class TLRPC$TL_updateUserName extends TLRPC$Update {
    public static int constructor = -NUM;
    public String first_name;
    public String last_name;
    public long user_id;
    public ArrayList<String> usernames;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.user_id = abstractSerializedData.readInt64(z);
        this.first_name = abstractSerializedData.readString(z);
        this.last_name = abstractSerializedData.readString(z);
        int readInt32 = abstractSerializedData.readInt32(z);
        if (readInt32 != NUM) {
            if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
            }
            return;
        }
        int readInt322 = abstractSerializedData.readInt32(z);
        for (int i = 0; i < readInt322; i++) {
            this.usernames.add(abstractSerializedData.readString(z));
        }
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.user_id);
        abstractSerializedData.writeString(this.first_name);
        abstractSerializedData.writeString(this.last_name);
        abstractSerializedData.writeInt32(NUM);
    }
}

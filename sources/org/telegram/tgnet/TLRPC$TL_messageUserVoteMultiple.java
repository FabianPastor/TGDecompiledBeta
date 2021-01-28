package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_messageUserVoteMultiple extends TLRPC$MessageUserVote {
    public static int constructor = NUM;
    public ArrayList<byte[]> options = new ArrayList<>();

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.user_id = abstractSerializedData.readInt32(z);
        int readInt32 = abstractSerializedData.readInt32(z);
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                this.options.add(abstractSerializedData.readByteArray(z));
            }
            this.date = abstractSerializedData.readInt32(z);
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt32)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.user_id);
        abstractSerializedData.writeInt32(NUM);
        int size = this.options.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            abstractSerializedData.writeByteArray(this.options.get(i));
        }
        abstractSerializedData.writeInt32(this.date);
    }
}

package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_privacyValueDisallowChatParticipants extends TLRPC$PrivacyRule {
    public static int constructor = -NUM;
    public ArrayList<Integer> chats = new ArrayList<>();

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                this.chats.add(Integer.valueOf(abstractSerializedData.readInt32(z)));
            }
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt32)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(NUM);
        int size = this.chats.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            abstractSerializedData.writeInt32(this.chats.get(i).intValue());
        }
    }
}

package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_secureRequiredTypeOneOf extends TLRPC$SecureRequiredType {
    public static int constructor = 41187252;
    public ArrayList<TLRPC$SecureRequiredType> types = new ArrayList<>();

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            while (i < readInt322) {
                TLRPC$SecureRequiredType TLdeserialize = TLRPC$SecureRequiredType.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.types.add(TLdeserialize);
                    i++;
                } else {
                    return;
                }
            }
        } else if (z) {
            throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt32)}));
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(NUM);
        int size = this.types.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.types.get(i).serializeToStream(abstractSerializedData);
        }
    }
}

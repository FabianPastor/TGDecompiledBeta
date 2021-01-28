package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_updateDcOptions extends TLRPC$Update {
    public static int constructor = -NUM;
    public ArrayList<TLRPC$TL_dcOption> dc_options = new ArrayList<>();

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            while (i < readInt322) {
                TLRPC$TL_dcOption TLdeserialize = TLRPC$TL_dcOption.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.dc_options.add(TLdeserialize);
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
        int size = this.dc_options.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.dc_options.get(i).serializeToStream(abstractSerializedData);
        }
    }
}

package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_keyboardButtonRow extends TLObject {
    public static int constructor = NUM;
    public ArrayList<TLRPC$KeyboardButton> buttons = new ArrayList<>();

    public static TLRPC$TL_keyboardButtonRow TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_keyboardButtonRow tLRPC$TL_keyboardButtonRow = new TLRPC$TL_keyboardButtonRow();
            tLRPC$TL_keyboardButtonRow.readParams(abstractSerializedData, z);
            return tLRPC$TL_keyboardButtonRow;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_keyboardButtonRow", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        int i = 0;
        if (readInt32 == NUM) {
            int readInt322 = abstractSerializedData.readInt32(z);
            while (i < readInt322) {
                TLRPC$KeyboardButton TLdeserialize = TLRPC$KeyboardButton.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize != null) {
                    this.buttons.add(TLdeserialize);
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
        int size = this.buttons.size();
        abstractSerializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            this.buttons.get(i).serializeToStream(abstractSerializedData);
        }
    }
}

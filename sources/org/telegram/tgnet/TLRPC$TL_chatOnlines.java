package org.telegram.tgnet;

public class TLRPC$TL_chatOnlines extends TLObject {
    public static int constructor = -NUM;
    public int onlines;

    public static TLRPC$TL_chatOnlines TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_chatOnlines tLRPC$TL_chatOnlines = new TLRPC$TL_chatOnlines();
            tLRPC$TL_chatOnlines.readParams(abstractSerializedData, z);
            return tLRPC$TL_chatOnlines;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_chatOnlines", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.onlines = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.onlines);
    }
}

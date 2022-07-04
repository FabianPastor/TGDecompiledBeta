package org.telegram.tgnet;

public class TLRPC$TL_account_savedRingtone extends TLRPC$account_SavedRingtone {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}

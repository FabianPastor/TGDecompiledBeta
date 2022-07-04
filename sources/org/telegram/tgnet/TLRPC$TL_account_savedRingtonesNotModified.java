package org.telegram.tgnet;

public class TLRPC$TL_account_savedRingtonesNotModified extends TLRPC$account_SavedRingtones {
    public static int constructor = -67704655;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}

package org.telegram.tgnet;

public class TLRPC$TL_secureValueTypeUtilityBill extends TLRPC$SecureValueType {
    public static int constructor = -63531698;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}

package org.telegram.tgnet;

public class TLRPC$TL_updatePhoneCall extends TLRPC$Update {
    public static int constructor = -NUM;
    public TLRPC$PhoneCall phone_call;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.phone_call = TLRPC$PhoneCall.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.phone_call.serializeToStream(abstractSerializedData);
    }
}

package org.telegram.tgnet;

public class TLRPC$TL_contacts_contactsNotModified extends TLRPC$contacts_Contacts {
    public static int constructor = -NUM;

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}

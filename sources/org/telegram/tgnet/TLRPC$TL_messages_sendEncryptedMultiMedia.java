package org.telegram.tgnet;

import java.util.ArrayList;

public class TLRPC$TL_messages_sendEncryptedMultiMedia extends TLObject {
    public ArrayList<TLRPC$InputEncryptedFile> files = new ArrayList<>();
    public ArrayList<TLRPC$TL_decryptedMessage> messages = new ArrayList<>();

    public void freeResources() {
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
    }

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$messages_SentEncryptedMessage.TLdeserialize(abstractSerializedData, i, z);
    }
}

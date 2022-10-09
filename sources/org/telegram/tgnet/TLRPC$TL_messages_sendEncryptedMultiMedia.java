package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public class TLRPC$TL_messages_sendEncryptedMultiMedia extends TLObject {
    public ArrayList<TLRPC$TL_decryptedMessage> messages = new ArrayList<>();
    public ArrayList<TLRPC$InputEncryptedFile> files = new ArrayList<>();

    @Override // org.telegram.tgnet.TLObject
    public void freeResources() {
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
    }

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$messages_SentEncryptedMessage.TLdeserialize(abstractSerializedData, i, z);
    }
}

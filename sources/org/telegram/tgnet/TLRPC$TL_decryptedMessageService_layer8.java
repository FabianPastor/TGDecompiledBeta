package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_decryptedMessageService_layer8 extends TLRPC$TL_decryptedMessageService {
    public static int constructor = -NUM;

    @Override // org.telegram.tgnet.TLRPC$TL_decryptedMessageService, org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.random_id = abstractSerializedData.readInt64(z);
        this.random_bytes = abstractSerializedData.readByteArray(z);
        this.action = TLRPC$DecryptedMessageAction.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    @Override // org.telegram.tgnet.TLRPC$TL_decryptedMessageService, org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt64(this.random_id);
        abstractSerializedData.writeByteArray(this.random_bytes);
        this.action.serializeToStream(abstractSerializedData);
    }
}

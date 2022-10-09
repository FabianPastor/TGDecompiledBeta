package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_updateEncryption extends TLRPC$Update {
    public static int constructor = -NUM;
    public TLRPC$EncryptedChat chat;
    public int date;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.chat = TLRPC$EncryptedChat.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.date = abstractSerializedData.readInt32(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.chat.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.date);
    }
}

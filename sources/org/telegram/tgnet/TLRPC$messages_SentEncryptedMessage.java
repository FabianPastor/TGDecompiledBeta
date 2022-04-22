package org.telegram.tgnet;

public abstract class TLRPC$messages_SentEncryptedMessage extends TLObject {
    public int date;
    public TLRPC$EncryptedFile file;

    public static TLRPC$messages_SentEncryptedMessage TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$messages_SentEncryptedMessage tLRPC$messages_SentEncryptedMessage;
        if (i != -NUM) {
            tLRPC$messages_SentEncryptedMessage = i != NUM ? null : new TLRPC$TL_messages_sentEncryptedMessage();
        } else {
            tLRPC$messages_SentEncryptedMessage = new TLRPC$TL_messages_sentEncryptedFile();
        }
        if (tLRPC$messages_SentEncryptedMessage != null || !z) {
            if (tLRPC$messages_SentEncryptedMessage != null) {
                tLRPC$messages_SentEncryptedMessage.readParams(abstractSerializedData, z);
            }
            return tLRPC$messages_SentEncryptedMessage;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_SentEncryptedMessage", new Object[]{Integer.valueOf(i)}));
    }
}

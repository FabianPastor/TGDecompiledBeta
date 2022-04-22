package org.telegram.tgnet;

public abstract class TLRPC$messages_TranslatedText extends TLObject {
    public static TLRPC$messages_TranslatedText TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$messages_TranslatedText tLRPC$messages_TranslatedText;
        if (i != -NUM) {
            tLRPC$messages_TranslatedText = i != NUM ? null : new TLRPC$TL_messages_translateNoResult();
        } else {
            tLRPC$messages_TranslatedText = new TLRPC$TL_messages_translateResultText();
        }
        if (tLRPC$messages_TranslatedText != null || !z) {
            if (tLRPC$messages_TranslatedText != null) {
                tLRPC$messages_TranslatedText.readParams(abstractSerializedData, z);
            }
            return tLRPC$messages_TranslatedText;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_TranslatedText", new Object[]{Integer.valueOf(i)}));
    }
}

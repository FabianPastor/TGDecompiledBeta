package org.telegram.tgnet;

public abstract class TLRPC$account_ChatThemes extends TLObject {
    public static TLRPC$account_ChatThemes TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$account_ChatThemes tLRPC$account_ChatThemes;
        if (i != -NUM) {
            tLRPC$account_ChatThemes = i != -28524867 ? null : new TLRPC$TL_account_chatThemes();
        } else {
            tLRPC$account_ChatThemes = new TLRPC$TL_account_chatThemesNotModified();
        }
        if (tLRPC$account_ChatThemes != null || !z) {
            if (tLRPC$account_ChatThemes != null) {
                tLRPC$account_ChatThemes.readParams(abstractSerializedData, z);
            }
            return tLRPC$account_ChatThemes;
        }
        throw new RuntimeException(String.format("can't parse magic %x in account_ChatThemes", new Object[]{Integer.valueOf(i)}));
    }
}

package org.telegram.tgnet;

public abstract class TLRPC$account_Themes extends TLObject {
    public static TLRPC$account_Themes TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$account_Themes tLRPC$account_Themes;
        if (i != -NUM) {
            tLRPC$account_Themes = i != NUM ? null : new TLRPC$TL_account_themes();
        } else {
            tLRPC$account_Themes = new TLRPC$TL_account_themesNotModified();
        }
        if (tLRPC$account_Themes != null || !z) {
            if (tLRPC$account_Themes != null) {
                tLRPC$account_Themes.readParams(abstractSerializedData, z);
            }
            return tLRPC$account_Themes;
        }
        throw new RuntimeException(String.format("can't parse magic %x in account_Themes", new Object[]{Integer.valueOf(i)}));
    }
}

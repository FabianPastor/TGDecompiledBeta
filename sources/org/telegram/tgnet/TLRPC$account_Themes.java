package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$account_Themes extends TLObject {
    public static TLRPC$account_Themes TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$account_Themes tLRPC$TL_account_themes;
        if (i == -NUM) {
            tLRPC$TL_account_themes = new TLRPC$TL_account_themes();
        } else {
            tLRPC$TL_account_themes = i != -NUM ? null : new TLRPC$account_Themes() { // from class: org.telegram.tgnet.TLRPC$TL_account_themesNotModified
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                }
            };
        }
        if (tLRPC$TL_account_themes != null || !z) {
            if (tLRPC$TL_account_themes != null) {
                tLRPC$TL_account_themes.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_account_themes;
        }
        throw new RuntimeException(String.format("can't parse magic %x in account_Themes", Integer.valueOf(i)));
    }
}

package org.telegram.tgnet;

public abstract class TLRPC$Theme extends TLObject {
    public static TLRPC$Theme TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$Theme tLRPC$Theme;
        if (i == -NUM) {
            tLRPC$Theme = new TLRPC$TL_theme_layer106();
        } else if (i != 42930452) {
            tLRPC$Theme = i != NUM ? null : new TLRPC$TL_themeDocumentNotModified_layer106();
        } else {
            tLRPC$Theme = new TLRPC$TL_theme();
        }
        if (tLRPC$Theme != null || !z) {
            if (tLRPC$Theme != null) {
                tLRPC$Theme.readParams(abstractSerializedData, z);
            }
            return tLRPC$Theme;
        }
        throw new RuntimeException(String.format("can't parse magic %x in Theme", new Object[]{Integer.valueOf(i)}));
    }
}

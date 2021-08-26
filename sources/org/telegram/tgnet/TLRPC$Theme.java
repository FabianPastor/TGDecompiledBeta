package org.telegram.tgnet;

public abstract class TLRPC$Theme extends TLObject {
    public static TLRPC$Theme TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$Theme tLRPC$Theme;
        switch (i) {
            case -402474788:
                tLRPC$Theme = new TLRPC$TL_theme();
                break;
            case -136770336:
                tLRPC$Theme = new TLRPC$TL_theme_layer106();
                break;
            case 42930452:
                tLRPC$Theme = new TLRPC$TL_theme_layer131();
                break;
            case 1211967244:
                tLRPC$Theme = new TLRPC$TL_themeDocumentNotModified_layer106();
                break;
            default:
                tLRPC$Theme = null;
                break;
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

package org.telegram.tgnet;

public abstract class TLRPC$Theme extends TLObject {
    public static TLRPC$TL_theme TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$TL_theme tLRPC$TL_theme;
        switch (i) {
            case -1609668650:
                tLRPC$TL_theme = new TLRPC$TL_theme();
                break;
            case -402474788:
                tLRPC$TL_theme = new TLRPC$TL_theme_layer133();
                break;
            case -136770336:
                tLRPC$TL_theme = new TLRPC$TL_theme_layer106();
                break;
            case 42930452:
                tLRPC$TL_theme = new TLRPC$TL_theme_layer131();
                break;
            case 1211967244:
                tLRPC$TL_theme = new TLRPC$TL_themeDocumentNotModified_layer106();
                break;
            default:
                tLRPC$TL_theme = null;
                break;
        }
        if (tLRPC$TL_theme != null || !z) {
            if (tLRPC$TL_theme != null) {
                tLRPC$TL_theme.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_theme;
        }
        throw new RuntimeException(String.format("can't parse magic %x in Theme", new Object[]{Integer.valueOf(i)}));
    }
}

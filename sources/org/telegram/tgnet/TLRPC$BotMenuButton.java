package org.telegram.tgnet;

public abstract class TLRPC$BotMenuButton extends TLObject {
    public static TLRPC$BotMenuButton TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$BotMenuButton tLRPC$BotMenuButton;
        if (i != -NUM) {
            tLRPC$BotMenuButton = i != NUM ? i != NUM ? null : new TLRPC$TL_botMenuButtonDefault() : new TLRPC$TL_botMenuButtonCommands();
        } else {
            tLRPC$BotMenuButton = new TLRPC$TL_botMenuButton();
        }
        if (tLRPC$BotMenuButton != null || !z) {
            if (tLRPC$BotMenuButton != null) {
                tLRPC$BotMenuButton.readParams(abstractSerializedData, z);
            }
            return tLRPC$BotMenuButton;
        }
        throw new RuntimeException(String.format("can't parse magic %x in BotMenuButton", new Object[]{Integer.valueOf(i)}));
    }
}

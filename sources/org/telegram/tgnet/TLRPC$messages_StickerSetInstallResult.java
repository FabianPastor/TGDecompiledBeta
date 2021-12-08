package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$messages_StickerSetInstallResult extends TLObject {
    public ArrayList<TLRPC$StickerSetCovered> sets = new ArrayList<>();

    public static TLRPC$messages_StickerSetInstallResult TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$messages_StickerSetInstallResult tLRPC$messages_StickerSetInstallResult;
        if (i != NUM) {
            tLRPC$messages_StickerSetInstallResult = i != NUM ? null : new TLRPC$TL_messages_stickerSetInstallResultSuccess();
        } else {
            tLRPC$messages_StickerSetInstallResult = new TLRPC$TL_messages_stickerSetInstallResultArchive();
        }
        if (tLRPC$messages_StickerSetInstallResult != null || !z) {
            if (tLRPC$messages_StickerSetInstallResult != null) {
                tLRPC$messages_StickerSetInstallResult.readParams(abstractSerializedData, z);
            }
            return tLRPC$messages_StickerSetInstallResult;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_StickerSetInstallResult", new Object[]{Integer.valueOf(i)}));
    }
}

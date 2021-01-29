package org.telegram.tgnet;

public abstract class TLRPC$InputWallPaper extends TLObject {
    public static TLRPC$InputWallPaper TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$InputWallPaper tLRPC$InputWallPaper;
        if (i == -NUM) {
            tLRPC$InputWallPaper = new TLRPC$TL_inputWallPaperNoFile();
        } else if (i != -NUM) {
            tLRPC$InputWallPaper = i != NUM ? null : new TLRPC$TL_inputWallPaperSlug();
        } else {
            tLRPC$InputWallPaper = new TLRPC$TL_inputWallPaper();
        }
        if (tLRPC$InputWallPaper != null || !z) {
            if (tLRPC$InputWallPaper != null) {
                tLRPC$InputWallPaper.readParams(abstractSerializedData, z);
            }
            return tLRPC$InputWallPaper;
        }
        throw new RuntimeException(String.format("can't parse magic %x in InputWallPaper", new Object[]{Integer.valueOf(i)}));
    }
}

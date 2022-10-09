package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$InputWallPaper extends TLObject {
    public static TLRPC$InputWallPaper TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$InputWallPaper tLRPC$TL_inputWallPaperNoFile;
        if (i == -NUM) {
            tLRPC$TL_inputWallPaperNoFile = new TLRPC$TL_inputWallPaperNoFile();
        } else if (i == -NUM) {
            tLRPC$TL_inputWallPaperNoFile = new TLRPC$TL_inputWallPaper();
        } else {
            tLRPC$TL_inputWallPaperNoFile = i != NUM ? null : new TLRPC$TL_inputWallPaperSlug();
        }
        if (tLRPC$TL_inputWallPaperNoFile != null || !z) {
            if (tLRPC$TL_inputWallPaperNoFile != null) {
                tLRPC$TL_inputWallPaperNoFile.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_inputWallPaperNoFile;
        }
        throw new RuntimeException(String.format("can't parse magic %x in InputWallPaper", Integer.valueOf(i)));
    }
}

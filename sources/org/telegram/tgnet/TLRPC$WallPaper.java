package org.telegram.tgnet;

public abstract class TLRPC$WallPaper extends TLObject {
    public long access_hash;
    public boolean creator;
    public boolean dark;
    public TLRPC$Document document;
    public int flags;
    public long id;
    public boolean isDefault;
    public boolean pattern;
    public TLRPC$WallPaperSettings settings;
    public String slug;

    public static TLRPC$WallPaper TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$WallPaper tLRPC$WallPaper;
        switch (i) {
            case -1963717851:
                tLRPC$WallPaper = new TLRPC$TL_wallPaperNoFile_layer128();
                break;
            case -1539849235:
                tLRPC$WallPaper = new TLRPC$TL_wallPaper();
                break;
            case -528465642:
                tLRPC$WallPaper = new TLRPC$TL_wallPaperNoFile();
                break;
            case -263220756:
                tLRPC$WallPaper = new TLRPC$TL_wallPaper_layer94();
                break;
            default:
                tLRPC$WallPaper = null;
                break;
        }
        if (tLRPC$WallPaper != null || !z) {
            if (tLRPC$WallPaper != null) {
                tLRPC$WallPaper.readParams(abstractSerializedData, z);
            }
            return tLRPC$WallPaper;
        }
        throw new RuntimeException(String.format("can't parse magic %x in WallPaper", new Object[]{Integer.valueOf(i)}));
    }
}

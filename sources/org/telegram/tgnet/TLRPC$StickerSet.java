package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$StickerSet extends TLObject {
    public long access_hash;
    public boolean animated;
    public boolean archived;
    public int count;
    public int flags;
    public boolean gifs;
    public int hash;
    public long id;
    public boolean installed;
    public int installed_date;
    public boolean masks;
    public boolean official;
    public String short_name;
    public int thumb_dc_id;
    public int thumb_version;
    public ArrayList<TLRPC$PhotoSize> thumbs = new ArrayList<>();
    public String title;

    public static TLRPC$StickerSet TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$StickerSet tLRPC$StickerSet;
        switch (i) {
            case -1482409193:
                tLRPC$StickerSet = new TLRPC$TL_stickerSet_old();
                break;
            case -852477119:
                tLRPC$StickerSet = new TLRPC$TL_stickerSet_layer75();
                break;
            case -673242758:
                tLRPC$StickerSet = new TLRPC$TL_stickerSet();
                break;
            case -290164953:
                tLRPC$StickerSet = new TLRPC$TL_stickerSet_layer121();
                break;
            case 1088567208:
                tLRPC$StickerSet = new TLRPC$TL_stickerSet_layer126();
                break;
            case 1434820921:
                tLRPC$StickerSet = new TLRPC$TL_stickerSet_layer96();
                break;
            case 1787870391:
                tLRPC$StickerSet = new TLRPC$TL_stickerSet_layer97();
                break;
            default:
                tLRPC$StickerSet = null;
                break;
        }
        if (tLRPC$StickerSet != null || !z) {
            if (tLRPC$StickerSet != null) {
                tLRPC$StickerSet.readParams(abstractSerializedData, z);
            }
            return tLRPC$StickerSet;
        }
        throw new RuntimeException(String.format("can't parse magic %x in StickerSet", new Object[]{Integer.valueOf(i)}));
    }
}

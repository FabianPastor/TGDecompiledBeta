package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$Photo extends TLObject {
    public long access_hash;
    public String caption;
    public int date;
    public int dc_id;
    public byte[] file_reference;
    public int flags;
    public TLRPC$GeoPoint geo;
    public boolean has_stickers;
    public long id;
    public ArrayList<TLRPC$PhotoSize> sizes = new ArrayList<>();
    public int user_id;
    public ArrayList<TLRPC$VideoSize> video_sizes = new ArrayList<>();

    public static TLRPC$Photo TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$Photo tLRPC$Photo;
        switch (i) {
            case -1836524247:
                tLRPC$Photo = new TLRPC$TL_photo_layer82();
                break;
            case -1673036328:
                tLRPC$Photo = new TLRPC$TL_photo_layer97();
                break;
            case -1014792074:
                tLRPC$Photo = new TLRPC$TL_photo_old2();
                break;
            case -840088834:
                tLRPC$Photo = new TLRPC$TL_photo_layer55();
                break;
            case -797637467:
                tLRPC$Photo = new TLRPC$TL_photo_layer115();
                break;
            case -82216347:
                tLRPC$Photo = new TLRPC$TL_photo();
                break;
            case 582313809:
                tLRPC$Photo = new TLRPC$TL_photo_old();
                break;
            case 590459437:
                tLRPC$Photo = new TLRPC$TL_photoEmpty();
                break;
            default:
                tLRPC$Photo = null;
                break;
        }
        if (tLRPC$Photo != null || !z) {
            if (tLRPC$Photo != null) {
                tLRPC$Photo.readParams(abstractSerializedData, z);
            }
            return tLRPC$Photo;
        }
        throw new RuntimeException(String.format("can't parse magic %x in Photo", new Object[]{Integer.valueOf(i)}));
    }
}

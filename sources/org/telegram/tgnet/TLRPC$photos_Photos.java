package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$photos_Photos extends TLObject {
    public int count;
    public ArrayList<TLRPC$Photo> photos = new ArrayList<>();
    public ArrayList<TLRPC$User> users = new ArrayList<>();

    public static TLRPC$photos_Photos TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$photos_Photos tLRPC$photos_Photos;
        if (i != -NUM) {
            tLRPC$photos_Photos = i != NUM ? null : new TLRPC$TL_photos_photosSlice();
        } else {
            tLRPC$photos_Photos = new TLRPC$TL_photos_photos();
        }
        if (tLRPC$photos_Photos != null || !z) {
            if (tLRPC$photos_Photos != null) {
                tLRPC$photos_Photos.readParams(abstractSerializedData, z);
            }
            return tLRPC$photos_Photos;
        }
        throw new RuntimeException(String.format("can't parse magic %x in photos_Photos", new Object[]{Integer.valueOf(i)}));
    }
}

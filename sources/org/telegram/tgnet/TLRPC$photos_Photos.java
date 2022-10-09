package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class TLRPC$photos_Photos extends TLObject {
    public int count;
    public ArrayList<TLRPC$Photo> photos = new ArrayList<>();
    public ArrayList<TLRPC$User> users = new ArrayList<>();

    public static TLRPC$photos_Photos TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$photos_Photos tLRPC$TL_photos_photos;
        if (i == -NUM) {
            tLRPC$TL_photos_photos = new TLRPC$TL_photos_photos();
        } else {
            tLRPC$TL_photos_photos = i != NUM ? null : new TLRPC$photos_Photos() { // from class: org.telegram.tgnet.TLRPC$TL_photos_photosSlice
                public static int constructor = NUM;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.count = abstractSerializedData2.readInt32(z2);
                    int readInt32 = abstractSerializedData2.readInt32(z2);
                    if (readInt32 != NUM) {
                        if (z2) {
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                        }
                        return;
                    }
                    int readInt322 = abstractSerializedData2.readInt32(z2);
                    for (int i2 = 0; i2 < readInt322; i2++) {
                        TLRPC$Photo TLdeserialize = TLRPC$Photo.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if (TLdeserialize == null) {
                            return;
                        }
                        this.photos.add(TLdeserialize);
                    }
                    int readInt323 = abstractSerializedData2.readInt32(z2);
                    if (readInt323 != NUM) {
                        if (z2) {
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt323)));
                        }
                        return;
                    }
                    int readInt324 = abstractSerializedData2.readInt32(z2);
                    for (int i3 = 0; i3 < readInt324; i3++) {
                        TLRPC$User TLdeserialize2 = TLRPC$User.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if (TLdeserialize2 == null) {
                            return;
                        }
                        this.users.add(TLdeserialize2);
                    }
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    abstractSerializedData2.writeInt32(this.count);
                    abstractSerializedData2.writeInt32(NUM);
                    int size = this.photos.size();
                    abstractSerializedData2.writeInt32(size);
                    for (int i2 = 0; i2 < size; i2++) {
                        this.photos.get(i2).serializeToStream(abstractSerializedData2);
                    }
                    abstractSerializedData2.writeInt32(NUM);
                    int size2 = this.users.size();
                    abstractSerializedData2.writeInt32(size2);
                    for (int i3 = 0; i3 < size2; i3++) {
                        this.users.get(i3).serializeToStream(abstractSerializedData2);
                    }
                }
            };
        }
        if (tLRPC$TL_photos_photos != null || !z) {
            if (tLRPC$TL_photos_photos != null) {
                tLRPC$TL_photos_photos.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_photos_photos;
        }
        throw new RuntimeException(String.format("can't parse magic %x in photos_Photos", Integer.valueOf(i)));
    }
}

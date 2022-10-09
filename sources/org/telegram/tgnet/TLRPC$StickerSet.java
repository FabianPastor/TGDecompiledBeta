package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class TLRPC$StickerSet extends TLObject {
    public long access_hash;
    public boolean animated;
    public boolean archived;
    public int count;
    public boolean emojis;
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
    public long thumb_document_id;
    public int thumb_version;
    public ArrayList<TLRPC$PhotoSize> thumbs = new ArrayList<>();
    public String title;
    public boolean videos;

    public static TLRPC$StickerSet TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$TL_stickerSet tLRPC$TL_stickerSet;
        switch (i) {
            case -1482409193:
                tLRPC$TL_stickerSet = new TLRPC$TL_stickerSet() { // from class: org.telegram.tgnet.TLRPC$TL_stickerSet_old
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_stickerSet, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.id = abstractSerializedData2.readInt64(z2);
                        this.access_hash = abstractSerializedData2.readInt64(z2);
                        this.title = abstractSerializedData2.readString(z2);
                        this.short_name = abstractSerializedData2.readString(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_stickerSet, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt64(this.id);
                        abstractSerializedData2.writeInt64(this.access_hash);
                        abstractSerializedData2.writeString(this.title);
                        abstractSerializedData2.writeString(this.short_name);
                    }
                };
                break;
            case -852477119:
                tLRPC$TL_stickerSet = new TLRPC$TL_stickerSet() { // from class: org.telegram.tgnet.TLRPC$TL_stickerSet_layer75
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_stickerSet, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = false;
                        this.installed = (readInt32 & 1) != 0;
                        this.archived = (readInt32 & 2) != 0;
                        this.official = (readInt32 & 4) != 0;
                        if ((readInt32 & 8) != 0) {
                            z3 = true;
                        }
                        this.masks = z3;
                        this.id = abstractSerializedData2.readInt64(z2);
                        this.access_hash = abstractSerializedData2.readInt64(z2);
                        this.title = abstractSerializedData2.readString(z2);
                        this.short_name = abstractSerializedData2.readString(z2);
                        this.count = abstractSerializedData2.readInt32(z2);
                        this.hash = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_stickerSet, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.installed ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        int i3 = this.archived ? i2 | 2 : i2 & (-3);
                        this.flags = i3;
                        int i4 = this.official ? i3 | 4 : i3 & (-5);
                        this.flags = i4;
                        int i5 = this.masks ? i4 | 8 : i4 & (-9);
                        this.flags = i5;
                        abstractSerializedData2.writeInt32(i5);
                        abstractSerializedData2.writeInt64(this.id);
                        abstractSerializedData2.writeInt64(this.access_hash);
                        abstractSerializedData2.writeString(this.title);
                        abstractSerializedData2.writeString(this.short_name);
                        abstractSerializedData2.writeInt32(this.count);
                        abstractSerializedData2.writeInt32(this.hash);
                    }
                };
                break;
            case -673242758:
                tLRPC$TL_stickerSet = new TLRPC$TL_stickerSet() { // from class: org.telegram.tgnet.TLRPC$TL_stickerSet_layer143
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_stickerSet, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        this.archived = (readInt32 & 2) != 0;
                        this.official = (readInt32 & 4) != 0;
                        this.masks = (readInt32 & 8) != 0;
                        this.animated = (readInt32 & 32) != 0;
                        this.videos = (readInt32 & 64) != 0;
                        this.emojis = (readInt32 & 128) != 0;
                        if ((readInt32 & 1) != 0) {
                            this.installed_date = abstractSerializedData2.readInt32(z2);
                        }
                        this.id = abstractSerializedData2.readInt64(z2);
                        this.access_hash = abstractSerializedData2.readInt64(z2);
                        this.title = abstractSerializedData2.readString(z2);
                        this.short_name = abstractSerializedData2.readString(z2);
                        if ((this.flags & 16) != 0) {
                            int readInt322 = abstractSerializedData2.readInt32(z2);
                            if (readInt322 != NUM) {
                                if (z2) {
                                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                                }
                                return;
                            }
                            int readInt323 = abstractSerializedData2.readInt32(z2);
                            for (int i2 = 0; i2 < readInt323; i2++) {
                                TLRPC$PhotoSize TLdeserialize = TLRPC$PhotoSize.TLdeserialize(0L, 0L, this.id, abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                                if (TLdeserialize == null) {
                                    return;
                                }
                                this.thumbs.add(TLdeserialize);
                            }
                        }
                        if ((this.flags & 16) != 0) {
                            this.thumb_dc_id = abstractSerializedData2.readInt32(z2);
                        }
                        if ((this.flags & 16) != 0) {
                            this.thumb_version = abstractSerializedData2.readInt32(z2);
                        }
                        if ((this.flags & 256) != 0) {
                            this.thumb_document_id = abstractSerializedData2.readInt64(z2);
                        }
                        this.count = abstractSerializedData2.readInt32(z2);
                        this.hash = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_stickerSet, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.archived ? this.flags | 2 : this.flags & (-3);
                        this.flags = i2;
                        int i3 = this.official ? i2 | 4 : i2 & (-5);
                        this.flags = i3;
                        int i4 = this.masks ? i3 | 8 : i3 & (-9);
                        this.flags = i4;
                        int i5 = this.animated ? i4 | 32 : i4 & (-33);
                        this.flags = i5;
                        int i6 = this.videos ? i5 | 64 : i5 & (-65);
                        this.flags = i6;
                        int i7 = this.emojis ? i6 | 128 : i6 & (-129);
                        this.flags = i7;
                        abstractSerializedData2.writeInt32(i7);
                        if ((this.flags & 1) != 0) {
                            abstractSerializedData2.writeInt32(this.installed_date);
                        }
                        abstractSerializedData2.writeInt64(this.id);
                        abstractSerializedData2.writeInt64(this.access_hash);
                        abstractSerializedData2.writeString(this.title);
                        abstractSerializedData2.writeString(this.short_name);
                        if ((this.flags & 16) != 0) {
                            abstractSerializedData2.writeInt32(NUM);
                            int size = this.thumbs.size();
                            abstractSerializedData2.writeInt32(size);
                            for (int i8 = 0; i8 < size; i8++) {
                                this.thumbs.get(i8).serializeToStream(abstractSerializedData2);
                            }
                        }
                        if ((this.flags & 16) != 0) {
                            abstractSerializedData2.writeInt32(this.thumb_dc_id);
                        }
                        if ((this.flags & 16) != 0) {
                            abstractSerializedData2.writeInt32(this.thumb_version);
                        }
                        if ((this.flags & 256) != 0) {
                            abstractSerializedData2.writeInt64(this.thumb_document_id);
                        }
                        abstractSerializedData2.writeInt32(this.count);
                        abstractSerializedData2.writeInt32(this.hash);
                    }
                };
                break;
            case -290164953:
                tLRPC$TL_stickerSet = new TLRPC$TL_stickerSet() { // from class: org.telegram.tgnet.TLRPC$TL_stickerSet_layer121
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_stickerSet, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        TLRPC$PhotoSize TLdeserialize;
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = false;
                        this.archived = (readInt32 & 2) != 0;
                        this.official = (readInt32 & 4) != 0;
                        this.masks = (readInt32 & 8) != 0;
                        if ((readInt32 & 32) != 0) {
                            z3 = true;
                        }
                        this.animated = z3;
                        if ((readInt32 & 1) != 0) {
                            this.installed_date = abstractSerializedData2.readInt32(z2);
                        }
                        this.id = abstractSerializedData2.readInt64(z2);
                        this.access_hash = abstractSerializedData2.readInt64(z2);
                        this.title = abstractSerializedData2.readString(z2);
                        this.short_name = abstractSerializedData2.readString(z2);
                        if ((this.flags & 16) != 0 && (TLdeserialize = TLRPC$PhotoSize.TLdeserialize(0L, 0L, 0L, abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2)) != null) {
                            this.thumbs.add(TLdeserialize);
                        }
                        if ((this.flags & 16) != 0) {
                            this.thumb_dc_id = abstractSerializedData2.readInt32(z2);
                        }
                        this.count = abstractSerializedData2.readInt32(z2);
                        this.hash = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_stickerSet, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.archived ? this.flags | 2 : this.flags & (-3);
                        this.flags = i2;
                        int i3 = this.official ? i2 | 4 : i2 & (-5);
                        this.flags = i3;
                        int i4 = this.masks ? i3 | 8 : i3 & (-9);
                        this.flags = i4;
                        int i5 = this.animated ? i4 | 32 : i4 & (-33);
                        this.flags = i5;
                        abstractSerializedData2.writeInt32(i5);
                        if ((this.flags & 1) != 0) {
                            abstractSerializedData2.writeInt32(this.installed_date);
                        }
                        abstractSerializedData2.writeInt64(this.id);
                        abstractSerializedData2.writeInt64(this.access_hash);
                        abstractSerializedData2.writeString(this.title);
                        abstractSerializedData2.writeString(this.short_name);
                        if ((this.flags & 16) != 0) {
                            this.thumbs.get(0).serializeToStream(abstractSerializedData2);
                        }
                        if ((this.flags & 16) != 0) {
                            abstractSerializedData2.writeInt32(this.thumb_dc_id);
                        }
                        abstractSerializedData2.writeInt32(this.count);
                        abstractSerializedData2.writeInt32(this.hash);
                    }
                };
                break;
            case 768691932:
                tLRPC$TL_stickerSet = new TLRPC$TL_stickerSet();
                break;
            case 1088567208:
                tLRPC$TL_stickerSet = new TLRPC$TL_stickerSet() { // from class: org.telegram.tgnet.TLRPC$TL_stickerSet_layer126
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_stickerSet, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        this.archived = (readInt32 & 2) != 0;
                        this.official = (readInt32 & 4) != 0;
                        this.masks = (readInt32 & 8) != 0;
                        this.animated = (readInt32 & 32) != 0;
                        if ((readInt32 & 1) != 0) {
                            this.installed_date = abstractSerializedData2.readInt32(z2);
                        }
                        this.id = abstractSerializedData2.readInt64(z2);
                        this.access_hash = abstractSerializedData2.readInt64(z2);
                        this.title = abstractSerializedData2.readString(z2);
                        this.short_name = abstractSerializedData2.readString(z2);
                        if ((this.flags & 16) != 0) {
                            int readInt322 = abstractSerializedData2.readInt32(z2);
                            if (readInt322 != NUM) {
                                if (z2) {
                                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                                }
                                return;
                            }
                            int readInt323 = abstractSerializedData2.readInt32(z2);
                            for (int i2 = 0; i2 < readInt323; i2++) {
                                TLRPC$PhotoSize TLdeserialize = TLRPC$PhotoSize.TLdeserialize(0L, 0L, 0L, abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                                if (TLdeserialize == null) {
                                    return;
                                }
                                this.thumbs.add(TLdeserialize);
                            }
                        }
                        if ((this.flags & 16) != 0) {
                            this.thumb_dc_id = abstractSerializedData2.readInt32(z2);
                        }
                        this.count = abstractSerializedData2.readInt32(z2);
                        this.hash = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_stickerSet, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.archived ? this.flags | 2 : this.flags & (-3);
                        this.flags = i2;
                        int i3 = this.official ? i2 | 4 : i2 & (-5);
                        this.flags = i3;
                        int i4 = this.masks ? i3 | 8 : i3 & (-9);
                        this.flags = i4;
                        int i5 = this.animated ? i4 | 32 : i4 & (-33);
                        this.flags = i5;
                        abstractSerializedData2.writeInt32(i5);
                        if ((this.flags & 1) != 0) {
                            abstractSerializedData2.writeInt32(this.installed_date);
                        }
                        abstractSerializedData2.writeInt64(this.id);
                        abstractSerializedData2.writeInt64(this.access_hash);
                        abstractSerializedData2.writeString(this.title);
                        abstractSerializedData2.writeString(this.short_name);
                        if ((this.flags & 16) != 0) {
                            abstractSerializedData2.writeInt32(NUM);
                            int size = this.thumbs.size();
                            abstractSerializedData2.writeInt32(size);
                            for (int i6 = 0; i6 < size; i6++) {
                                this.thumbs.get(i6).serializeToStream(abstractSerializedData2);
                            }
                        }
                        if ((this.flags & 16) != 0) {
                            abstractSerializedData2.writeInt32(this.thumb_dc_id);
                        }
                        abstractSerializedData2.writeInt32(this.count);
                        abstractSerializedData2.writeInt32(this.hash);
                    }
                };
                break;
            case 1434820921:
                tLRPC$TL_stickerSet = new TLRPC$TL_stickerSet() { // from class: org.telegram.tgnet.TLRPC$TL_stickerSet_layer96
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_stickerSet, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = false;
                        this.archived = (readInt32 & 2) != 0;
                        this.official = (readInt32 & 4) != 0;
                        if ((readInt32 & 8) != 0) {
                            z3 = true;
                        }
                        this.masks = z3;
                        if ((readInt32 & 1) != 0) {
                            this.installed_date = abstractSerializedData2.readInt32(z2);
                        }
                        this.id = abstractSerializedData2.readInt64(z2);
                        this.access_hash = abstractSerializedData2.readInt64(z2);
                        this.title = abstractSerializedData2.readString(z2);
                        this.short_name = abstractSerializedData2.readString(z2);
                        this.count = abstractSerializedData2.readInt32(z2);
                        this.hash = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_stickerSet, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.archived ? this.flags | 2 : this.flags & (-3);
                        this.flags = i2;
                        int i3 = this.official ? i2 | 4 : i2 & (-5);
                        this.flags = i3;
                        int i4 = this.masks ? i3 | 8 : i3 & (-9);
                        this.flags = i4;
                        abstractSerializedData2.writeInt32(i4);
                        if ((this.flags & 1) != 0) {
                            abstractSerializedData2.writeInt32(this.installed_date);
                        }
                        abstractSerializedData2.writeInt64(this.id);
                        abstractSerializedData2.writeInt64(this.access_hash);
                        abstractSerializedData2.writeString(this.title);
                        abstractSerializedData2.writeString(this.short_name);
                        abstractSerializedData2.writeInt32(this.count);
                        abstractSerializedData2.writeInt32(this.hash);
                    }
                };
                break;
            case 1787870391:
                tLRPC$TL_stickerSet = new TLRPC$TL_stickerSet() { // from class: org.telegram.tgnet.TLRPC$TL_stickerSet_layer97
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_stickerSet, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        TLRPC$PhotoSize TLdeserialize;
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = false;
                        this.archived = (readInt32 & 2) != 0;
                        this.official = (readInt32 & 4) != 0;
                        if ((readInt32 & 8) != 0) {
                            z3 = true;
                        }
                        this.masks = z3;
                        if ((readInt32 & 1) != 0) {
                            this.installed_date = abstractSerializedData2.readInt32(z2);
                        }
                        this.id = abstractSerializedData2.readInt64(z2);
                        this.access_hash = abstractSerializedData2.readInt64(z2);
                        this.title = abstractSerializedData2.readString(z2);
                        this.short_name = abstractSerializedData2.readString(z2);
                        if ((this.flags & 16) != 0 && (TLdeserialize = TLRPC$PhotoSize.TLdeserialize(0L, 0L, 0L, abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2)) != null) {
                            this.thumbs.add(TLdeserialize);
                        }
                        this.count = abstractSerializedData2.readInt32(z2);
                        this.hash = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_stickerSet, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.archived ? this.flags | 2 : this.flags & (-3);
                        this.flags = i2;
                        int i3 = this.official ? i2 | 4 : i2 & (-5);
                        this.flags = i3;
                        int i4 = this.masks ? i3 | 8 : i3 & (-9);
                        this.flags = i4;
                        abstractSerializedData2.writeInt32(i4);
                        if ((this.flags & 1) != 0) {
                            abstractSerializedData2.writeInt32(this.installed_date);
                        }
                        abstractSerializedData2.writeInt64(this.id);
                        abstractSerializedData2.writeInt64(this.access_hash);
                        abstractSerializedData2.writeString(this.title);
                        abstractSerializedData2.writeString(this.short_name);
                        if ((this.flags & 16) != 0) {
                            this.thumbs.get(0).serializeToStream(abstractSerializedData2);
                        }
                        abstractSerializedData2.writeInt32(this.count);
                        abstractSerializedData2.writeInt32(this.hash);
                    }
                };
                break;
            default:
                tLRPC$TL_stickerSet = null;
                break;
        }
        if (tLRPC$TL_stickerSet != null || !z) {
            if (tLRPC$TL_stickerSet != null) {
                tLRPC$TL_stickerSet.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_stickerSet;
        }
        throw new RuntimeException(String.format("can't parse magic %x in StickerSet", Integer.valueOf(i)));
    }
}

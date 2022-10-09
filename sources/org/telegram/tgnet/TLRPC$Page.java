package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class TLRPC$Page extends TLObject {
    public int flags;
    public boolean part;
    public boolean rtl;
    public String url;
    public boolean v2;
    public int views;
    public ArrayList<TLRPC$PageBlock> blocks = new ArrayList<>();
    public ArrayList<TLRPC$Photo> photos = new ArrayList<>();
    public ArrayList<TLRPC$Document> documents = new ArrayList<>();

    public static TLRPC$Page TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$Page tLRPC$Page;
        switch (i) {
            case -1913754556:
                tLRPC$Page = new TLRPC$TL_pagePart_layer82() { // from class: org.telegram.tgnet.TLRPC$TL_pagePart_layer67
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_pagePart_layer82, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        if (readInt32 != NUM) {
                            if (z2) {
                                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                            }
                            return;
                        }
                        int readInt322 = abstractSerializedData2.readInt32(z2);
                        for (int i2 = 0; i2 < readInt322; i2++) {
                            TLRPC$PageBlock TLdeserialize = TLRPC$PageBlock.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                            if (TLdeserialize == null) {
                                return;
                            }
                            this.blocks.add(TLdeserialize);
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
                            TLRPC$Photo TLdeserialize2 = TLRPC$Photo.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                            if (TLdeserialize2 == null) {
                                return;
                            }
                            this.photos.add(TLdeserialize2);
                        }
                        int readInt325 = abstractSerializedData2.readInt32(z2);
                        if (readInt325 != NUM) {
                            if (z2) {
                                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt325)));
                            }
                            return;
                        }
                        int readInt326 = abstractSerializedData2.readInt32(z2);
                        for (int i4 = 0; i4 < readInt326; i4++) {
                            TLRPC$Document TLdeserialize3 = TLRPC$Document.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                            if (TLdeserialize3 == null) {
                                return;
                            }
                            this.documents.add(TLdeserialize3);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_pagePart_layer82, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(NUM);
                        int size = this.blocks.size();
                        abstractSerializedData2.writeInt32(size);
                        for (int i2 = 0; i2 < size; i2++) {
                            this.blocks.get(i2).serializeToStream(abstractSerializedData2);
                        }
                        abstractSerializedData2.writeInt32(NUM);
                        int size2 = this.photos.size();
                        abstractSerializedData2.writeInt32(size2);
                        for (int i3 = 0; i3 < size2; i3++) {
                            this.photos.get(i3).serializeToStream(abstractSerializedData2);
                        }
                        abstractSerializedData2.writeInt32(NUM);
                        int size3 = this.documents.size();
                        abstractSerializedData2.writeInt32(size3);
                        for (int i4 = 0; i4 < size3; i4++) {
                            this.documents.get(i4).serializeToStream(abstractSerializedData2);
                        }
                    }
                };
                break;
            case -1908433218:
                tLRPC$Page = new TLRPC$TL_pagePart_layer82();
                break;
            case -1738178803:
                tLRPC$Page = new TLRPC$TL_page();
                break;
            case -1366746132:
                tLRPC$Page = new TLRPC$TL_page() { // from class: org.telegram.tgnet.TLRPC$TL_page_layer110
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_page, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        this.part = (readInt32 & 1) != 0;
                        this.rtl = (readInt32 & 2) != 0;
                        this.url = abstractSerializedData2.readString(z2);
                        int readInt322 = abstractSerializedData2.readInt32(z2);
                        if (readInt322 != NUM) {
                            if (z2) {
                                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                            }
                            return;
                        }
                        int readInt323 = abstractSerializedData2.readInt32(z2);
                        for (int i2 = 0; i2 < readInt323; i2++) {
                            TLRPC$PageBlock TLdeserialize = TLRPC$PageBlock.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                            if (TLdeserialize == null) {
                                return;
                            }
                            this.blocks.add(TLdeserialize);
                        }
                        int readInt324 = abstractSerializedData2.readInt32(z2);
                        if (readInt324 != NUM) {
                            if (z2) {
                                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt324)));
                            }
                            return;
                        }
                        int readInt325 = abstractSerializedData2.readInt32(z2);
                        for (int i3 = 0; i3 < readInt325; i3++) {
                            TLRPC$Photo TLdeserialize2 = TLRPC$Photo.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                            if (TLdeserialize2 == null) {
                                return;
                            }
                            this.photos.add(TLdeserialize2);
                        }
                        int readInt326 = abstractSerializedData2.readInt32(z2);
                        if (readInt326 != NUM) {
                            if (z2) {
                                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt326)));
                            }
                            return;
                        }
                        int readInt327 = abstractSerializedData2.readInt32(z2);
                        for (int i4 = 0; i4 < readInt327; i4++) {
                            TLRPC$Document TLdeserialize3 = TLRPC$Document.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                            if (TLdeserialize3 == null) {
                                return;
                            }
                            this.documents.add(TLdeserialize3);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_page, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.part ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        int i3 = this.rtl ? i2 | 2 : i2 & (-3);
                        this.flags = i3;
                        abstractSerializedData2.writeInt32(i3);
                        abstractSerializedData2.writeString(this.url);
                        abstractSerializedData2.writeInt32(NUM);
                        int size = this.blocks.size();
                        abstractSerializedData2.writeInt32(size);
                        for (int i4 = 0; i4 < size; i4++) {
                            this.blocks.get(i4).serializeToStream(abstractSerializedData2);
                        }
                        abstractSerializedData2.writeInt32(NUM);
                        int size2 = this.photos.size();
                        abstractSerializedData2.writeInt32(size2);
                        for (int i5 = 0; i5 < size2; i5++) {
                            this.photos.get(i5).serializeToStream(abstractSerializedData2);
                        }
                        abstractSerializedData2.writeInt32(NUM);
                        int size3 = this.documents.size();
                        abstractSerializedData2.writeInt32(size3);
                        for (int i6 = 0; i6 < size3; i6++) {
                            this.documents.get(i6).serializeToStream(abstractSerializedData2);
                        }
                    }
                };
                break;
            case -677274263:
                tLRPC$Page = new TLRPC$TL_page() { // from class: org.telegram.tgnet.TLRPC$TL_pageFull_layer67
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_page, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        if (readInt32 != NUM) {
                            if (z2) {
                                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                            }
                            return;
                        }
                        int readInt322 = abstractSerializedData2.readInt32(z2);
                        for (int i2 = 0; i2 < readInt322; i2++) {
                            TLRPC$PageBlock TLdeserialize = TLRPC$PageBlock.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                            if (TLdeserialize == null) {
                                return;
                            }
                            this.blocks.add(TLdeserialize);
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
                            TLRPC$Photo TLdeserialize2 = TLRPC$Photo.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                            if (TLdeserialize2 == null) {
                                return;
                            }
                            this.photos.add(TLdeserialize2);
                        }
                        int readInt325 = abstractSerializedData2.readInt32(z2);
                        if (readInt325 != NUM) {
                            if (z2) {
                                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt325)));
                            }
                            return;
                        }
                        int readInt326 = abstractSerializedData2.readInt32(z2);
                        for (int i4 = 0; i4 < readInt326; i4++) {
                            TLRPC$Document TLdeserialize3 = TLRPC$Document.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                            if (TLdeserialize3 == null) {
                                return;
                            }
                            this.documents.add(TLdeserialize3);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_page, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(NUM);
                        int size = this.blocks.size();
                        abstractSerializedData2.writeInt32(size);
                        for (int i2 = 0; i2 < size; i2++) {
                            this.blocks.get(i2).serializeToStream(abstractSerializedData2);
                        }
                        abstractSerializedData2.writeInt32(NUM);
                        int size2 = this.photos.size();
                        abstractSerializedData2.writeInt32(size2);
                        for (int i3 = 0; i3 < size2; i3++) {
                            this.photos.get(i3).serializeToStream(abstractSerializedData2);
                        }
                        abstractSerializedData2.writeInt32(NUM);
                        int size3 = this.documents.size();
                        abstractSerializedData2.writeInt32(size3);
                        for (int i4 = 0; i4 < size3; i4++) {
                            this.documents.get(i4).serializeToStream(abstractSerializedData2);
                        }
                    }
                };
                break;
            case 1433323434:
                tLRPC$Page = new TLRPC$TL_page() { // from class: org.telegram.tgnet.TLRPC$TL_pageFull_layer82
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_page, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        if (readInt32 != NUM) {
                            if (z2) {
                                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                            }
                            return;
                        }
                        int readInt322 = abstractSerializedData2.readInt32(z2);
                        for (int i2 = 0; i2 < readInt322; i2++) {
                            TLRPC$PageBlock TLdeserialize = TLRPC$PageBlock.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                            if (TLdeserialize == null) {
                                return;
                            }
                            this.blocks.add(TLdeserialize);
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
                            TLRPC$Photo TLdeserialize2 = TLRPC$Photo.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                            if (TLdeserialize2 == null) {
                                return;
                            }
                            this.photos.add(TLdeserialize2);
                        }
                        int readInt325 = abstractSerializedData2.readInt32(z2);
                        if (readInt325 != NUM) {
                            if (z2) {
                                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt325)));
                            }
                            return;
                        }
                        int readInt326 = abstractSerializedData2.readInt32(z2);
                        for (int i4 = 0; i4 < readInt326; i4++) {
                            TLRPC$Document TLdeserialize3 = TLRPC$Document.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                            if (TLdeserialize3 == null) {
                                return;
                            }
                            this.documents.add(TLdeserialize3);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_page, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(NUM);
                        int size = this.blocks.size();
                        abstractSerializedData2.writeInt32(size);
                        for (int i2 = 0; i2 < size; i2++) {
                            this.blocks.get(i2).serializeToStream(abstractSerializedData2);
                        }
                        abstractSerializedData2.writeInt32(NUM);
                        int size2 = this.photos.size();
                        abstractSerializedData2.writeInt32(size2);
                        for (int i3 = 0; i3 < size2; i3++) {
                            this.photos.get(i3).serializeToStream(abstractSerializedData2);
                        }
                        abstractSerializedData2.writeInt32(NUM);
                        int size3 = this.documents.size();
                        abstractSerializedData2.writeInt32(size3);
                        for (int i4 = 0; i4 < size3; i4++) {
                            this.documents.get(i4).serializeToStream(abstractSerializedData2);
                        }
                    }
                };
                break;
            default:
                tLRPC$Page = null;
                break;
        }
        if (tLRPC$Page != null || !z) {
            if (tLRPC$Page != null) {
                tLRPC$Page.readParams(abstractSerializedData, z);
            }
            return tLRPC$Page;
        }
        throw new RuntimeException(String.format("can't parse magic %x in Page", Integer.valueOf(i)));
    }
}

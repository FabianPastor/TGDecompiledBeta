package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_webPage extends TLRPC$WebPage {
    public static int constructor = -NUM;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.flags = abstractSerializedData.readInt32(z);
        this.id = abstractSerializedData.readInt64(z);
        this.url = abstractSerializedData.readString(z);
        this.display_url = abstractSerializedData.readString(z);
        this.hash = abstractSerializedData.readInt32(z);
        if ((this.flags & 1) != 0) {
            this.type = abstractSerializedData.readString(z);
        }
        if ((this.flags & 2) != 0) {
            this.site_name = abstractSerializedData.readString(z);
        }
        if ((this.flags & 4) != 0) {
            this.title = abstractSerializedData.readString(z);
        }
        if ((this.flags & 8) != 0) {
            this.description = abstractSerializedData.readString(z);
        }
        if ((this.flags & 16) != 0) {
            this.photo = TLRPC$Photo.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 32) != 0) {
            this.embed_url = abstractSerializedData.readString(z);
        }
        if ((this.flags & 32) != 0) {
            this.embed_type = abstractSerializedData.readString(z);
        }
        if ((this.flags & 64) != 0) {
            this.embed_width = abstractSerializedData.readInt32(z);
        }
        if ((this.flags & 64) != 0) {
            this.embed_height = abstractSerializedData.readInt32(z);
        }
        if ((this.flags & 128) != 0) {
            this.duration = abstractSerializedData.readInt32(z);
        }
        if ((this.flags & 256) != 0) {
            this.author = abstractSerializedData.readString(z);
        }
        if ((this.flags & 512) != 0) {
            this.document = TLRPC$Document.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 1024) != 0) {
            this.cached_page = TLRPC$Page.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 4096) != 0) {
            int readInt32 = abstractSerializedData.readInt32(z);
            if (readInt32 != NUM) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                TLRPC$TL_webPageAttributeTheme TLdeserialize = TLRPC$TL_webPageAttributeTheme.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.attributes.add(TLdeserialize);
            }
        }
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        abstractSerializedData.writeInt64(this.id);
        abstractSerializedData.writeString(this.url);
        abstractSerializedData.writeString(this.display_url);
        abstractSerializedData.writeInt32(this.hash);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeString(this.type);
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeString(this.site_name);
        }
        if ((this.flags & 4) != 0) {
            abstractSerializedData.writeString(this.title);
        }
        if ((this.flags & 8) != 0) {
            abstractSerializedData.writeString(this.description);
        }
        if ((this.flags & 16) != 0) {
            this.photo.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 32) != 0) {
            abstractSerializedData.writeString(this.embed_url);
        }
        if ((this.flags & 32) != 0) {
            abstractSerializedData.writeString(this.embed_type);
        }
        if ((this.flags & 64) != 0) {
            abstractSerializedData.writeInt32(this.embed_width);
        }
        if ((this.flags & 64) != 0) {
            abstractSerializedData.writeInt32(this.embed_height);
        }
        if ((this.flags & 128) != 0) {
            abstractSerializedData.writeInt32(this.duration);
        }
        if ((this.flags & 256) != 0) {
            abstractSerializedData.writeString(this.author);
        }
        if ((this.flags & 512) != 0) {
            this.document.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 1024) != 0) {
            this.cached_page.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 4096) != 0) {
            abstractSerializedData.writeInt32(NUM);
            int size = this.attributes.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.attributes.get(i).serializeToStream(abstractSerializedData);
            }
        }
    }
}

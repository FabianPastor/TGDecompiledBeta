package org.telegram.tgnet;

public class TLRPC$TL_webPage_layer107 extends TLRPC$TL_webPage {
    public static int constructor = -94051982;

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
        if ((this.flags & 2048) != 0) {
            int readInt32 = abstractSerializedData.readInt32(z);
            int i = 0;
            if (readInt32 == NUM) {
                TLRPC$TL_webPageAttributeTheme tLRPC$TL_webPageAttributeTheme = new TLRPC$TL_webPageAttributeTheme();
                int readInt322 = abstractSerializedData.readInt32(z);
                while (i < readInt322) {
                    TLRPC$Document TLdeserialize = TLRPC$Document.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize != null) {
                        tLRPC$TL_webPageAttributeTheme.documents.add(TLdeserialize);
                        i++;
                    } else {
                        return;
                    }
                }
                this.attributes.add(tLRPC$TL_webPageAttributeTheme);
            } else if (z) {
                throw new RuntimeException(String.format("wrong Vector magic, got %x", new Object[]{Integer.valueOf(readInt32)}));
            } else {
                return;
            }
        }
        if ((this.flags & 1024) != 0) {
            this.cached_page = TLRPC$Page.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
    }

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
        if ((this.flags & 2048) != 0) {
            abstractSerializedData.writeInt32(NUM);
            abstractSerializedData.writeInt32(0);
        }
        if ((this.flags & 1024) != 0) {
            this.cached_page.serializeToStream(abstractSerializedData);
        }
    }
}

package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$BotInlineResult extends TLObject {
    public TLRPC$WebDocument content;
    public String description;
    public TLRPC$Document document;
    public int flags;
    public String id;
    public TLRPC$Photo photo;
    public long query_id;
    public TLRPC$BotInlineMessage send_message;
    public TLRPC$WebDocument thumb;
    public String title;
    public String type;
    public String url;

    public static TLRPC$BotInlineResult TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$BotInlineResult tLRPC$BotInlineResult;
        if (i == NUM) {
            tLRPC$BotInlineResult = new TLRPC$BotInlineResult() { // from class: org.telegram.tgnet.TLRPC$TL_botInlineResult
                public static int constructor = NUM;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.flags = abstractSerializedData2.readInt32(z2);
                    this.id = abstractSerializedData2.readString(z2);
                    this.type = abstractSerializedData2.readString(z2);
                    if ((this.flags & 2) != 0) {
                        this.title = abstractSerializedData2.readString(z2);
                    }
                    if ((this.flags & 4) != 0) {
                        this.description = abstractSerializedData2.readString(z2);
                    }
                    if ((this.flags & 8) != 0) {
                        this.url = abstractSerializedData2.readString(z2);
                    }
                    if ((this.flags & 16) != 0) {
                        this.thumb = TLRPC$WebDocument.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                    }
                    if ((this.flags & 32) != 0) {
                        this.content = TLRPC$WebDocument.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                    }
                    this.send_message = TLRPC$BotInlineMessage.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    abstractSerializedData2.writeInt32(this.flags);
                    abstractSerializedData2.writeString(this.id);
                    abstractSerializedData2.writeString(this.type);
                    if ((this.flags & 2) != 0) {
                        abstractSerializedData2.writeString(this.title);
                    }
                    if ((this.flags & 4) != 0) {
                        abstractSerializedData2.writeString(this.description);
                    }
                    if ((this.flags & 8) != 0) {
                        abstractSerializedData2.writeString(this.url);
                    }
                    if ((this.flags & 16) != 0) {
                        this.thumb.serializeToStream(abstractSerializedData2);
                    }
                    if ((this.flags & 32) != 0) {
                        this.content.serializeToStream(abstractSerializedData2);
                    }
                    this.send_message.serializeToStream(abstractSerializedData2);
                }
            };
        } else {
            tLRPC$BotInlineResult = i != NUM ? null : new TLRPC$BotInlineResult() { // from class: org.telegram.tgnet.TLRPC$TL_botInlineMediaResult
                public static int constructor = NUM;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.flags = abstractSerializedData2.readInt32(z2);
                    this.id = abstractSerializedData2.readString(z2);
                    this.type = abstractSerializedData2.readString(z2);
                    if ((this.flags & 1) != 0) {
                        this.photo = TLRPC$Photo.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                    }
                    if ((this.flags & 2) != 0) {
                        this.document = TLRPC$Document.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                    }
                    if ((this.flags & 4) != 0) {
                        this.title = abstractSerializedData2.readString(z2);
                    }
                    if ((this.flags & 8) != 0) {
                        this.description = abstractSerializedData2.readString(z2);
                    }
                    this.send_message = TLRPC$BotInlineMessage.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    abstractSerializedData2.writeInt32(this.flags);
                    abstractSerializedData2.writeString(this.id);
                    abstractSerializedData2.writeString(this.type);
                    if ((this.flags & 1) != 0) {
                        this.photo.serializeToStream(abstractSerializedData2);
                    }
                    if ((this.flags & 2) != 0) {
                        this.document.serializeToStream(abstractSerializedData2);
                    }
                    if ((this.flags & 4) != 0) {
                        abstractSerializedData2.writeString(this.title);
                    }
                    if ((this.flags & 8) != 0) {
                        abstractSerializedData2.writeString(this.description);
                    }
                    this.send_message.serializeToStream(abstractSerializedData2);
                }
            };
        }
        if (tLRPC$BotInlineResult != null || !z) {
            if (tLRPC$BotInlineResult != null) {
                tLRPC$BotInlineResult.readParams(abstractSerializedData, z);
            }
            return tLRPC$BotInlineResult;
        }
        throw new RuntimeException(String.format("can't parse magic %x in BotInlineResult", Integer.valueOf(i)));
    }
}

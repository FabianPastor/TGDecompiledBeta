package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class TLRPC$BotInlineMessage extends TLObject {
    public String address;
    public ArrayList<TLRPC$MessageEntity> entities = new ArrayList<>();
    public String first_name;
    public int flags;
    public TLRPC$GeoPoint geo;
    public int heading;
    public String last_name;
    public String message;
    public boolean no_webpage;
    public int period;
    public String phone_number;
    public String provider;
    public int proximity_notification_radius;
    public TLRPC$ReplyMarkup reply_markup;
    public String title;
    public String vcard;
    public String venue_id;
    public String venue_type;

    public static TLRPC$BotInlineMessage TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$BotInlineMessage tLRPC$TL_botInlineMessageMediaVenue;
        switch (i) {
            case -1970903652:
                tLRPC$TL_botInlineMessageMediaVenue = new TLRPC$TL_botInlineMessageMediaVenue();
                break;
            case -1937807902:
                tLRPC$TL_botInlineMessageMediaVenue = new TLRPC$BotInlineMessage() { // from class: org.telegram.tgnet.TLRPC$TL_botInlineMessageText
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        this.no_webpage = (readInt32 & 1) != 0;
                        this.message = abstractSerializedData2.readString(z2);
                        if ((this.flags & 2) != 0) {
                            int readInt322 = abstractSerializedData2.readInt32(z2);
                            if (readInt322 != NUM) {
                                if (z2) {
                                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                                }
                                return;
                            }
                            int readInt323 = abstractSerializedData2.readInt32(z2);
                            for (int i2 = 0; i2 < readInt323; i2++) {
                                TLRPC$MessageEntity TLdeserialize = TLRPC$MessageEntity.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                                if (TLdeserialize == null) {
                                    return;
                                }
                                this.entities.add(TLdeserialize);
                            }
                        }
                        if ((this.flags & 4) != 0) {
                            this.reply_markup = TLRPC$ReplyMarkup.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.no_webpage ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        abstractSerializedData2.writeInt32(i2);
                        abstractSerializedData2.writeString(this.message);
                        if ((this.flags & 2) != 0) {
                            abstractSerializedData2.writeInt32(NUM);
                            int size = this.entities.size();
                            abstractSerializedData2.writeInt32(size);
                            for (int i3 = 0; i3 < size; i3++) {
                                this.entities.get(i3).serializeToStream(abstractSerializedData2);
                            }
                        }
                        if ((this.flags & 4) != 0) {
                            this.reply_markup.serializeToStream(abstractSerializedData2);
                        }
                    }
                };
                break;
            case -1222451611:
                tLRPC$TL_botInlineMessageMediaVenue = new TLRPC$TL_botInlineMessageMediaGeo() { // from class: org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaGeo_layer119
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaGeo, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.flags = abstractSerializedData2.readInt32(z2);
                        this.geo = TLRPC$GeoPoint.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.period = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 4) != 0) {
                            this.reply_markup = TLRPC$ReplyMarkup.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaGeo, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(this.flags);
                        this.geo.serializeToStream(abstractSerializedData2);
                        abstractSerializedData2.writeInt32(this.period);
                        if ((this.flags & 4) != 0) {
                            this.reply_markup.serializeToStream(abstractSerializedData2);
                        }
                    }
                };
                break;
            case 85477117:
                tLRPC$TL_botInlineMessageMediaVenue = new TLRPC$TL_botInlineMessageMediaGeo();
                break;
            case 175419739:
                tLRPC$TL_botInlineMessageMediaVenue = new TLRPC$TL_botInlineMessageMediaAuto() { // from class: org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaAuto_layer74
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaAuto, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.flags = abstractSerializedData2.readInt32(z2);
                        this.message = abstractSerializedData2.readString(z2);
                        if ((this.flags & 4) != 0) {
                            this.reply_markup = TLRPC$ReplyMarkup.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaAuto, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(this.flags);
                        abstractSerializedData2.writeString(this.message);
                        if ((this.flags & 4) != 0) {
                            this.reply_markup.serializeToStream(abstractSerializedData2);
                        }
                    }
                };
                break;
            case 416402882:
                tLRPC$TL_botInlineMessageMediaVenue = new TLRPC$TL_botInlineMessageMediaContact();
                break;
            case 894081801:
                tLRPC$TL_botInlineMessageMediaVenue = new TLRPC$TL_botInlineMessageMediaInvoice();
                break;
            case 904770772:
                tLRPC$TL_botInlineMessageMediaVenue = new TLRPC$TL_botInlineMessageMediaContact() { // from class: org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaContact_layer81
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaContact, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.flags = abstractSerializedData2.readInt32(z2);
                        this.phone_number = abstractSerializedData2.readString(z2);
                        this.first_name = abstractSerializedData2.readString(z2);
                        this.last_name = abstractSerializedData2.readString(z2);
                        if ((this.flags & 4) != 0) {
                            this.reply_markup = TLRPC$ReplyMarkup.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaContact, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(this.flags);
                        abstractSerializedData2.writeString(this.phone_number);
                        abstractSerializedData2.writeString(this.first_name);
                        abstractSerializedData2.writeString(this.last_name);
                        if ((this.flags & 4) != 0) {
                            this.reply_markup.serializeToStream(abstractSerializedData2);
                        }
                    }
                };
                break;
            case 982505656:
                tLRPC$TL_botInlineMessageMediaVenue = new TLRPC$TL_botInlineMessageMediaGeo() { // from class: org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaGeo_layer71
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaGeo, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.flags = abstractSerializedData2.readInt32(z2);
                        this.geo = TLRPC$GeoPoint.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if ((this.flags & 4) != 0) {
                            this.reply_markup = TLRPC$ReplyMarkup.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaGeo, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(this.flags);
                        this.geo.serializeToStream(abstractSerializedData2);
                        if ((this.flags & 4) != 0) {
                            this.reply_markup.serializeToStream(abstractSerializedData2);
                        }
                    }
                };
                break;
            case 1130767150:
                tLRPC$TL_botInlineMessageMediaVenue = new TLRPC$TL_botInlineMessageMediaVenue() { // from class: org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaVenue_layer77
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaVenue, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.flags = abstractSerializedData2.readInt32(z2);
                        this.geo = TLRPC$GeoPoint.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.title = abstractSerializedData2.readString(z2);
                        this.address = abstractSerializedData2.readString(z2);
                        this.provider = abstractSerializedData2.readString(z2);
                        this.venue_id = abstractSerializedData2.readString(z2);
                        if ((this.flags & 4) != 0) {
                            this.reply_markup = TLRPC$ReplyMarkup.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        }
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaVenue, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(this.flags);
                        this.geo.serializeToStream(abstractSerializedData2);
                        abstractSerializedData2.writeString(this.title);
                        abstractSerializedData2.writeString(this.address);
                        abstractSerializedData2.writeString(this.provider);
                        abstractSerializedData2.writeString(this.venue_id);
                        if ((this.flags & 4) != 0) {
                            this.reply_markup.serializeToStream(abstractSerializedData2);
                        }
                    }
                };
                break;
            case 1984755728:
                tLRPC$TL_botInlineMessageMediaVenue = new TLRPC$TL_botInlineMessageMediaAuto();
                break;
            default:
                tLRPC$TL_botInlineMessageMediaVenue = null;
                break;
        }
        if (tLRPC$TL_botInlineMessageMediaVenue != null || !z) {
            if (tLRPC$TL_botInlineMessageMediaVenue != null) {
                tLRPC$TL_botInlineMessageMediaVenue.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_botInlineMessageMediaVenue;
        }
        throw new RuntimeException(String.format("can't parse magic %x in BotInlineMessage", Integer.valueOf(i)));
    }
}

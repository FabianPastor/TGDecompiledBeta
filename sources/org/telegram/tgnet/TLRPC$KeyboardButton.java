package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$KeyboardButton extends TLObject {
    public TLRPC$InputUser bot;
    public int button_id;
    public byte[] data;
    public int flags;
    public String fwd_text;
    public TLRPC$InputUser inputUser;
    public String query;
    public boolean quiz;
    public boolean request_write_access;
    public boolean requires_password;
    public boolean same_peer;
    public String text;
    public String url;
    public long user_id;

    public static TLRPC$KeyboardButton TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$KeyboardButton tLRPC$KeyboardButton;
        switch (i) {
            case -1598009252:
                tLRPC$KeyboardButton = new TLRPC$KeyboardButton() { // from class: org.telegram.tgnet.TLRPC$TL_keyboardButtonSimpleWebView
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.text = abstractSerializedData2.readString(z2);
                        this.url = abstractSerializedData2.readString(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.text);
                        abstractSerializedData2.writeString(this.url);
                    }
                };
                break;
            case -1560655744:
                tLRPC$KeyboardButton = new TLRPC$KeyboardButton() { // from class: org.telegram.tgnet.TLRPC$TL_keyboardButton
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.text = abstractSerializedData2.readString(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.text);
                    }
                };
                break;
            case -1344716869:
                tLRPC$KeyboardButton = new TLRPC$KeyboardButton() { // from class: org.telegram.tgnet.TLRPC$TL_keyboardButtonBuy
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.text = abstractSerializedData2.readString(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.text);
                    }
                };
                break;
            case -1318425559:
                tLRPC$KeyboardButton = new TLRPC$KeyboardButton() { // from class: org.telegram.tgnet.TLRPC$TL_keyboardButtonRequestPhone
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.text = abstractSerializedData2.readString(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.text);
                    }
                };
                break;
            case -1144565411:
                tLRPC$KeyboardButton = new TLRPC$KeyboardButton() { // from class: org.telegram.tgnet.TLRPC$TL_keyboardButtonRequestPoll
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        if ((readInt32 & 1) != 0) {
                            this.quiz = abstractSerializedData2.readBool(z2);
                        }
                        this.text = abstractSerializedData2.readString(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(this.flags);
                        if ((this.flags & 1) != 0) {
                            abstractSerializedData2.writeBool(this.quiz);
                        }
                        abstractSerializedData2.writeString(this.text);
                    }
                };
                break;
            case -802258988:
                tLRPC$KeyboardButton = new TLRPC$KeyboardButton() { // from class: org.telegram.tgnet.TLRPC$TL_inputKeyboardButtonUrlAuth
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = true;
                        if ((readInt32 & 1) == 0) {
                            z3 = false;
                        }
                        this.request_write_access = z3;
                        this.text = abstractSerializedData2.readString(z2);
                        if ((this.flags & 2) != 0) {
                            this.fwd_text = abstractSerializedData2.readString(z2);
                        }
                        this.url = abstractSerializedData2.readString(z2);
                        this.bot = TLRPC$InputUser.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.request_write_access ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        abstractSerializedData2.writeInt32(i2);
                        abstractSerializedData2.writeString(this.text);
                        if ((this.flags & 2) != 0) {
                            abstractSerializedData2.writeString(this.fwd_text);
                        }
                        abstractSerializedData2.writeString(this.url);
                        this.bot.serializeToStream(abstractSerializedData2);
                    }
                };
                break;
            case -376962181:
                tLRPC$KeyboardButton = new TLRPC$KeyboardButton() { // from class: org.telegram.tgnet.TLRPC$TL_inputKeyboardButtonUserProfile
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.text = abstractSerializedData2.readString(z2);
                        this.inputUser = TLRPC$InputUser.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.text);
                        this.inputUser.serializeToStream(abstractSerializedData2);
                    }
                };
                break;
            case -59151553:
                tLRPC$KeyboardButton = new TLRPC$KeyboardButton() { // from class: org.telegram.tgnet.TLRPC$TL_keyboardButtonRequestGeoLocation
                    public static int constructor = -59151553;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.text = abstractSerializedData2.readString(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.text);
                    }
                };
                break;
            case 90744648:
                tLRPC$KeyboardButton = new TLRPC$KeyboardButton() { // from class: org.telegram.tgnet.TLRPC$TL_keyboardButtonSwitchInline
                    public static int constructor = 90744648;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = true;
                        if ((readInt32 & 1) == 0) {
                            z3 = false;
                        }
                        this.same_peer = z3;
                        this.text = abstractSerializedData2.readString(z2);
                        this.query = abstractSerializedData2.readString(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.same_peer ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        abstractSerializedData2.writeInt32(i2);
                        abstractSerializedData2.writeString(this.text);
                        abstractSerializedData2.writeString(this.query);
                    }
                };
                break;
            case 280464681:
                tLRPC$KeyboardButton = new TLRPC$TL_keyboardButtonUrlAuth();
                break;
            case 326529584:
                tLRPC$KeyboardButton = new TLRPC$KeyboardButton() { // from class: org.telegram.tgnet.TLRPC$TL_keyboardButtonWebView
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.text = abstractSerializedData2.readString(z2);
                        this.url = abstractSerializedData2.readString(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.text);
                        abstractSerializedData2.writeString(this.url);
                    }
                };
                break;
            case 629866245:
                tLRPC$KeyboardButton = new TLRPC$KeyboardButton() { // from class: org.telegram.tgnet.TLRPC$TL_keyboardButtonUrl
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.text = abstractSerializedData2.readString(z2);
                        this.url = abstractSerializedData2.readString(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.text);
                        abstractSerializedData2.writeString(this.url);
                    }
                };
                break;
            case 814112961:
                tLRPC$KeyboardButton = new TLRPC$KeyboardButton() { // from class: org.telegram.tgnet.TLRPC$TL_keyboardButtonUserProfile
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.text = abstractSerializedData2.readString(z2);
                        this.user_id = abstractSerializedData2.readInt64(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.text);
                        abstractSerializedData2.writeInt64(this.user_id);
                    }
                };
                break;
            case 901503851:
                tLRPC$KeyboardButton = new TLRPC$TL_keyboardButtonCallback();
                break;
            case 1358175439:
                tLRPC$KeyboardButton = new TLRPC$KeyboardButton() { // from class: org.telegram.tgnet.TLRPC$TL_keyboardButtonGame
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.text = abstractSerializedData2.readString(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.text);
                    }
                };
                break;
            case 1748655686:
                tLRPC$KeyboardButton = new TLRPC$TL_keyboardButtonCallback() { // from class: org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback_layer117
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.text = abstractSerializedData2.readString(z2);
                        this.data = abstractSerializedData2.readByteArray(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.text);
                        abstractSerializedData2.writeByteArray(this.data);
                    }
                };
                break;
            default:
                tLRPC$KeyboardButton = null;
                break;
        }
        if (tLRPC$KeyboardButton != null || !z) {
            if (tLRPC$KeyboardButton != null) {
                tLRPC$KeyboardButton.readParams(abstractSerializedData, z);
            }
            return tLRPC$KeyboardButton;
        }
        throw new RuntimeException(String.format("can't parse magic %x in KeyboardButton", Integer.valueOf(i)));
    }
}

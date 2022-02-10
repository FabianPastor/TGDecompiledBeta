package org.telegram.tgnet;

import java.util.ArrayList;
import org.telegram.messenger.FileLoader;

public abstract class TLRPC$Document extends TLObject {
    public long access_hash;
    public ArrayList<TLRPC$DocumentAttribute> attributes = new ArrayList<>();
    public int date;
    public int dc_id;
    public String file_name;
    public String file_name_fixed;
    public byte[] file_reference;
    public int flags;
    public long id;
    public byte[] iv;
    public byte[] key;
    public String mime_type;
    public int size;
    public ArrayList<TLRPC$PhotoSize> thumbs = new ArrayList<>();
    public long user_id;
    public int version;
    public ArrayList<TLRPC$VideoSize> video_thumbs = new ArrayList<>();

    public static TLRPC$Document TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$Document tLRPC$Document;
        switch (i) {
            case -2027738169:
                tLRPC$Document = new TLRPC$TL_document_layer82();
                break;
            case -1683841855:
                tLRPC$Document = new TLRPC$TL_document_layer113();
                break;
            case -1627626714:
                tLRPC$Document = new TLRPC$TL_document_old();
                break;
            case -106717361:
                tLRPC$Document = new TLRPC$TL_document_layer53();
                break;
            case 512177195:
                tLRPC$Document = new TLRPC$TL_document();
                break;
            case 922273905:
                tLRPC$Document = new TLRPC$TL_documentEmpty();
                break;
            case 1431655766:
                tLRPC$Document = new TLRPC$TL_documentEncrypted_old();
                break;
            case 1431655768:
                tLRPC$Document = new TLRPC$TL_documentEncrypted();
                break;
            case 1498631756:
                tLRPC$Document = new TLRPC$TL_document_layer92();
                break;
            default:
                tLRPC$Document = null;
                break;
        }
        if (tLRPC$Document != null || !z) {
            if (tLRPC$Document != null) {
                tLRPC$Document.readParams(abstractSerializedData, z);
                tLRPC$Document.file_name_fixed = FileLoader.getDocumentFileName(tLRPC$Document);
            }
            return tLRPC$Document;
        }
        throw new RuntimeException(String.format("can't parse magic %x in Document", new Object[]{Integer.valueOf(i)}));
    }
}

package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$Page extends TLObject {
    public ArrayList<TLRPC$PageBlock> blocks = new ArrayList<>();
    public ArrayList<TLRPC$Document> documents = new ArrayList<>();
    public int flags;
    public boolean part;
    public ArrayList<TLRPC$Photo> photos = new ArrayList<>();
    public boolean rtl;
    public String url;
    public boolean v2;
    public int views;

    public static TLRPC$Page TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$Page tLRPC$Page;
        switch (i) {
            case -1913754556:
                tLRPC$Page = new TLRPC$TL_pagePart_layer67();
                break;
            case -1908433218:
                tLRPC$Page = new TLRPC$TL_pagePart_layer82();
                break;
            case -1738178803:
                tLRPC$Page = new TLRPC$TL_page();
                break;
            case -1366746132:
                tLRPC$Page = new TLRPC$TL_page_layer110();
                break;
            case -677274263:
                tLRPC$Page = new TLRPC$TL_pageFull_layer67();
                break;
            case 1433323434:
                tLRPC$Page = new TLRPC$TL_pageFull_layer82();
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
        throw new RuntimeException(String.format("can't parse magic %x in Page", new Object[]{Integer.valueOf(i)}));
    }
}

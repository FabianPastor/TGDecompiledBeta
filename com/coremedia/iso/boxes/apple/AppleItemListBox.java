package com.coremedia.iso.boxes.apple;

import com.googlecode.mp4parser.AbstractContainerBox;

public class AppleItemListBox extends AbstractContainerBox {
    public static final String TYPE = "ilst";

    public AppleItemListBox() {
        super(TYPE);
    }
}

package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractContainerBox;

public class ProtectionSchemeInformationBox extends AbstractContainerBox {
    public static final String TYPE = "sinf";

    public ProtectionSchemeInformationBox() {
        super(TYPE);
    }
}

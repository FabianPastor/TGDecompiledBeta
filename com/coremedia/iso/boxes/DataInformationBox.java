package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractContainerBox;

public class DataInformationBox extends AbstractContainerBox {
    public static final String TYPE = "dinf";

    public DataInformationBox() {
        super(TYPE);
    }
}

package com.coremedia.iso.boxes.fragment;

import com.googlecode.mp4parser.AbstractContainerBox;

public class MovieExtendsBox extends AbstractContainerBox {
    public static final String TYPE = "mvex";

    public MovieExtendsBox() {
        super(TYPE);
    }
}

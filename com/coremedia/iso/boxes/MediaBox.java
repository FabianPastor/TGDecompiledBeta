package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractContainerBox;

public class MediaBox extends AbstractContainerBox {
    public static final String TYPE = "mdia";

    public MediaBox() {
        super(TYPE);
    }

    public MediaInformationBox getMediaInformationBox() {
        for (Box box : getBoxes()) {
            if (box instanceof MediaInformationBox) {
                return (MediaInformationBox) box;
            }
        }
        return null;
    }

    public MediaHeaderBox getMediaHeaderBox() {
        for (Box box : getBoxes()) {
            if (box instanceof MediaHeaderBox) {
                return (MediaHeaderBox) box;
            }
        }
        return null;
    }

    public HandlerBox getHandlerBox() {
        for (Box box : getBoxes()) {
            if (box instanceof HandlerBox) {
                return (HandlerBox) box;
            }
        }
        return null;
    }
}

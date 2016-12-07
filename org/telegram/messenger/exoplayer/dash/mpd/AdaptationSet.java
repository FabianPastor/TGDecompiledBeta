package org.telegram.messenger.exoplayer.dash.mpd;

import java.util.Collections;
import java.util.List;

public class AdaptationSet {
    public static final int TYPE_AUDIO = 1;
    public static final int TYPE_TEXT = 2;
    public static final int TYPE_UNKNOWN = -1;
    public static final int TYPE_VIDEO = 0;
    public final List<ContentProtection> contentProtections;
    public final int id;
    public final List<Representation> representations;
    public final int type;

    public AdaptationSet(int id, int type, List<Representation> representations, List<ContentProtection> contentProtections) {
        this.id = id;
        this.type = type;
        this.representations = Collections.unmodifiableList(representations);
        if (contentProtections == null) {
            this.contentProtections = Collections.emptyList();
        } else {
            this.contentProtections = Collections.unmodifiableList(contentProtections);
        }
    }

    public AdaptationSet(int id, int type, List<Representation> representations) {
        this(id, type, representations, null);
    }

    public boolean hasContentProtection() {
        return !this.contentProtections.isEmpty();
    }
}

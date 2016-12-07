package org.telegram.messenger.exoplayer2.source.dash.manifest;

import java.util.Collections;
import java.util.List;

public class AdaptationSet {
    public static final int UNSET_ID = -1;
    public final int id;
    public final List<Representation> representations;
    public final int type;

    public AdaptationSet(int id, int type, List<Representation> representations) {
        this.id = id;
        this.type = type;
        this.representations = Collections.unmodifiableList(representations);
    }
}

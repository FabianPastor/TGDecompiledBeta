package org.telegram.ui.Components;

import android.view.View;
import org.telegram.messenger.MediaController;

/* renamed from: org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$4X94PKs6A-0UhkXhWsjRM6iEc3I  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AudioPlayerAlert$4X94PKs6A0UhkXhWsjRM6iEc3I implements View.OnClickListener {
    public static final /* synthetic */ $$Lambda$AudioPlayerAlert$4X94PKs6A0UhkXhWsjRM6iEc3I INSTANCE = new $$Lambda$AudioPlayerAlert$4X94PKs6A0UhkXhWsjRM6iEc3I();

    private /* synthetic */ $$Lambda$AudioPlayerAlert$4X94PKs6A0UhkXhWsjRM6iEc3I() {
    }

    public final void onClick(View view) {
        MediaController.getInstance().playNextMessage();
    }
}

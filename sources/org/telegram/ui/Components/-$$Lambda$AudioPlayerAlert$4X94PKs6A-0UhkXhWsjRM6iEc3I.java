package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.messenger.MediaController;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AudioPlayerAlert$4X94PKs6A-0UhkXhWsjRM6iEc3I implements OnClickListener {
    public static final /* synthetic */ -$$Lambda$AudioPlayerAlert$4X94PKs6A-0UhkXhWsjRM6iEc3I INSTANCE = new -$$Lambda$AudioPlayerAlert$4X94PKs6A-0UhkXhWsjRM6iEc3I();

    private /* synthetic */ -$$Lambda$AudioPlayerAlert$4X94PKs6A-0UhkXhWsjRM6iEc3I() {
    }

    public final void onClick(View view) {
        MediaController.getInstance().playNextMessage();
    }
}

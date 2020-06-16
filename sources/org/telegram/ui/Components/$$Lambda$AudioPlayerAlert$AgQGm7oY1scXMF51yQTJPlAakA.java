package org.telegram.ui.Components;

import android.view.View;
import org.telegram.messenger.MediaController;

/* renamed from: org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$Ag-QGm7oY1scXMvar_yQTJPlAakA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AudioPlayerAlert$AgQGm7oY1scXMvar_yQTJPlAakA implements View.OnClickListener {
    public static final /* synthetic */ $$Lambda$AudioPlayerAlert$AgQGm7oY1scXMvar_yQTJPlAakA INSTANCE = new $$Lambda$AudioPlayerAlert$AgQGm7oY1scXMvar_yQTJPlAakA();

    private /* synthetic */ $$Lambda$AudioPlayerAlert$AgQGm7oY1scXMvar_yQTJPlAakA() {
    }

    public final void onClick(View view) {
        MediaController.getInstance().playPreviousMessage();
    }
}

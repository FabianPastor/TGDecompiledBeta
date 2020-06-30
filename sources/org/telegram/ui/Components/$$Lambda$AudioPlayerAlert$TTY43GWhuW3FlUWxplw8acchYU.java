package org.telegram.ui.Components;

import android.view.View;
import org.telegram.messenger.MediaController;

/* renamed from: org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$TTY43GWhuW3FlUWxplw8ac-chYU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AudioPlayerAlert$TTY43GWhuW3FlUWxplw8acchYU implements View.OnClickListener {
    public static final /* synthetic */ $$Lambda$AudioPlayerAlert$TTY43GWhuW3FlUWxplw8acchYU INSTANCE = new $$Lambda$AudioPlayerAlert$TTY43GWhuW3FlUWxplw8acchYU();

    private /* synthetic */ $$Lambda$AudioPlayerAlert$TTY43GWhuW3FlUWxplw8acchYU() {
    }

    public final void onClick(View view) {
        MediaController.getInstance().playNextMessage();
    }
}

package org.telegram.p005ui.Components;

import android.graphics.drawable.Drawable;

/* renamed from: org.telegram.ui.Components.StatusDrawable */
public abstract class StatusDrawable extends Drawable {
    public abstract void setIsChat(boolean z);

    public abstract void start();

    public abstract void stop();
}

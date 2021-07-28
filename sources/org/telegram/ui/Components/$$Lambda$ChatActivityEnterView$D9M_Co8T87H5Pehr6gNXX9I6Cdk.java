package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;
import org.telegram.ui.Components.AlertsCreator;

/* renamed from: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$D9M_Co8T87H5Pehr6gNXX9I6Cdk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$D9M_Co8T87H5Pehr6gNXX9I6Cdk implements AlertsCreator.ScheduleDatePickerDelegate {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$D9M_Co8T87H5Pehr6gNXX9I6Cdk INSTANCE = new $$Lambda$ChatActivityEnterView$D9M_Co8T87H5Pehr6gNXX9I6Cdk();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$D9M_Co8T87H5Pehr6gNXX9I6Cdk() {
    }

    public final void didSelectDate(boolean z, int i) {
        MediaController.getInstance().stopRecording(1, z, i);
    }
}

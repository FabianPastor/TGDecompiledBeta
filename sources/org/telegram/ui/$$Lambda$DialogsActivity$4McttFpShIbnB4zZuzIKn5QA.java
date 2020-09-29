package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.MessagesController;

/* renamed from: org.telegram.ui.-$$Lambda$DialogsActivity$4McttFpShIbnB4zZuzI-Kn5Q--A  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$DialogsActivity$4McttFpShIbnB4zZuzIKn5QA implements DialogInterface.OnClickListener {
    public static final /* synthetic */ $$Lambda$DialogsActivity$4McttFpShIbnB4zZuzIKn5QA INSTANCE = new $$Lambda$DialogsActivity$4McttFpShIbnB4zZuzIKn5QA();

    private /* synthetic */ $$Lambda$DialogsActivity$4McttFpShIbnB4zZuzIKn5QA() {
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askedAboutMiuiLockscreen", true).commit();
    }
}

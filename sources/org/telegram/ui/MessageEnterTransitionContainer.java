package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.NotificationCenter;

public class MessageEnterTransitionContainer extends View {
    private final int currentAccount;
    Runnable hideRunnable = new MessageEnterTransitionContainer$$ExternalSyntheticLambda0(this);
    private ArrayList<Transition> transitions = new ArrayList<>();

    public interface Transition {
        void onDraw(Canvas canvas);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-MessageEnterTransitionContainer  reason: not valid java name */
    public /* synthetic */ void m3293lambda$new$0$orgtelegramuiMessageEnterTransitionContainer() {
        setVisibility(8);
    }

    public MessageEnterTransitionContainer(Context context, int currentAccount2) {
        super(context);
        this.currentAccount = currentAccount2;
    }

    /* access modifiers changed from: package-private */
    public void addTransition(Transition transition) {
        this.transitions.add(transition);
        checkVisibility();
    }

    /* access modifiers changed from: package-private */
    public void removeTransition(Transition transition) {
        this.transitions.remove(transition);
        checkVisibility();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (!this.transitions.isEmpty()) {
            for (int i = 0; i < this.transitions.size(); i++) {
                this.transitions.get(i).onDraw(canvas);
            }
        }
    }

    private void checkVisibility() {
        if (this.transitions.isEmpty() && getVisibility() != 8) {
            NotificationCenter.getInstance(this.currentAccount).removeDelayed(this.hideRunnable);
            NotificationCenter.getInstance(this.currentAccount).doOnIdle(this.hideRunnable);
        } else if (!this.transitions.isEmpty() && getVisibility() != 0) {
            NotificationCenter.getInstance(this.currentAccount).removeDelayed(this.hideRunnable);
            setVisibility(0);
        }
    }
}

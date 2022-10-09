package org.telegram.ui;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import org.telegram.messenger.NotificationCenter;
@SuppressLint({"ViewConstructor"})
/* loaded from: classes3.dex */
public class MessageEnterTransitionContainer extends View {
    private final int currentAccount;
    Runnable hideRunnable;
    private final ViewGroup parent;
    private ArrayList<Transition> transitions;

    /* loaded from: classes3.dex */
    public interface Transition {
        void onDraw(Canvas canvas);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        setVisibility(8);
    }

    public MessageEnterTransitionContainer(ViewGroup viewGroup, int i) {
        super(viewGroup.getContext());
        this.transitions = new ArrayList<>();
        this.hideRunnable = new Runnable() { // from class: org.telegram.ui.MessageEnterTransitionContainer$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                MessageEnterTransitionContainer.this.lambda$new$0();
            }
        };
        this.parent = viewGroup;
        this.currentAccount = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addTransition(Transition transition) {
        this.transitions.add(transition);
        checkVisibility();
        this.parent.invalidate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeTransition(Transition transition) {
        this.transitions.remove(transition);
        checkVisibility();
        this.parent.invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.transitions.isEmpty()) {
            return;
        }
        for (int i = 0; i < this.transitions.size(); i++) {
            this.transitions.get(i).onDraw(canvas);
        }
    }

    private void checkVisibility() {
        if (this.transitions.isEmpty() && getVisibility() != 8) {
            NotificationCenter.getInstance(this.currentAccount).removeDelayed(this.hideRunnable);
            NotificationCenter.getInstance(this.currentAccount).doOnIdle(this.hideRunnable);
        } else if (this.transitions.isEmpty() || getVisibility() == 0) {
        } else {
            NotificationCenter.getInstance(this.currentAccount).removeDelayed(this.hideRunnable);
            setVisibility(0);
        }
    }

    public boolean isRunning() {
        return this.transitions.size() > 0;
    }
}

package org.telegram.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.NotificationCenter;

@SuppressLint({"ViewConstructor"})
public class MessageEnterTransitionContainer extends View {
    private final int currentAccount;
    Runnable hideRunnable = new Runnable() {
        public final void run() {
            MessageEnterTransitionContainer.this.lambda$new$0$MessageEnterTransitionContainer();
        }
    };
    long time;
    private ArrayList<Transition> transitions = new ArrayList<>();

    public interface Transition {
        void onDraw(Canvas canvas);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$MessageEnterTransitionContainer() {
        setVisibility(8);
    }

    public MessageEnterTransitionContainer(Context context, int i) {
        super(context);
        this.currentAccount = i;
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
            this.time = System.currentTimeMillis();
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

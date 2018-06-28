package org.telegram.messenger.exoplayer2.drm;

import android.os.Handler;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import org.telegram.messenger.exoplayer2.util.Assertions;

public interface DefaultDrmSessionEventListener {

    public static final class EventDispatcher {
        private final CopyOnWriteArrayList<HandlerAndListener> listeners = new CopyOnWriteArrayList();

        private static final class HandlerAndListener {
            public final Handler handler;
            public final DefaultDrmSessionEventListener listener;

            public HandlerAndListener(Handler handler, DefaultDrmSessionEventListener eventListener) {
                this.handler = handler;
                this.listener = eventListener;
            }
        }

        public void addListener(Handler handler, DefaultDrmSessionEventListener eventListener) {
            boolean z = (handler == null || eventListener == null) ? false : true;
            Assertions.checkArgument(z);
            this.listeners.add(new HandlerAndListener(handler, eventListener));
        }

        public void removeListener(DefaultDrmSessionEventListener eventListener) {
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                HandlerAndListener handlerAndListener = (HandlerAndListener) it.next();
                if (handlerAndListener.listener == eventListener) {
                    this.listeners.remove(handlerAndListener);
                }
            }
        }

        public void drmKeysLoaded() {
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                HandlerAndListener handlerAndListener = (HandlerAndListener) it.next();
                final DefaultDrmSessionEventListener listener = handlerAndListener.listener;
                handlerAndListener.handler.post(new Runnable() {
                    public void run() {
                        listener.onDrmKeysLoaded();
                    }
                });
            }
        }

        public void drmSessionManagerError(final Exception e) {
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                HandlerAndListener handlerAndListener = (HandlerAndListener) it.next();
                final DefaultDrmSessionEventListener listener = handlerAndListener.listener;
                handlerAndListener.handler.post(new Runnable() {
                    public void run() {
                        listener.onDrmSessionManagerError(e);
                    }
                });
            }
        }

        public void drmKeysRestored() {
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                HandlerAndListener handlerAndListener = (HandlerAndListener) it.next();
                final DefaultDrmSessionEventListener listener = handlerAndListener.listener;
                handlerAndListener.handler.post(new Runnable() {
                    public void run() {
                        listener.onDrmKeysRestored();
                    }
                });
            }
        }

        public void drmKeysRemoved() {
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                HandlerAndListener handlerAndListener = (HandlerAndListener) it.next();
                final DefaultDrmSessionEventListener listener = handlerAndListener.listener;
                handlerAndListener.handler.post(new Runnable() {
                    public void run() {
                        listener.onDrmKeysRemoved();
                    }
                });
            }
        }
    }

    void onDrmKeysLoaded();

    void onDrmKeysRemoved();

    void onDrmKeysRestored();

    void onDrmSessionManagerError(Exception exception);
}

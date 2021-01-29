package org.telegram.messenger;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class ShortcutWidgetService extends RemoteViewsService {
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ShortcutRemoteViewsFactory(getApplicationContext(), intent);
    }
}

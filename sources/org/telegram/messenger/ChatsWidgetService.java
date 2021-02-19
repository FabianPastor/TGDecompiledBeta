package org.telegram.messenger;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class ChatsWidgetService extends RemoteViewsService {
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ChatsRemoteViewsFactory(getApplicationContext(), intent);
    }
}

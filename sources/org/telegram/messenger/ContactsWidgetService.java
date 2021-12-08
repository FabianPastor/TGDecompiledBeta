package org.telegram.messenger;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class ContactsWidgetService extends RemoteViewsService {
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ContactsRemoteViewsFactory(getApplicationContext(), intent);
    }
}

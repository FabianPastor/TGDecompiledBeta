package android.support.v4.app;

import android.app.Notification;
import android.app.Notification.Builder;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;

@RestrictTo({Scope.GROUP_ID})
public interface NotificationBuilderWithBuilderAccessor {
    Notification build();

    Builder getBuilder();
}

package j$.retarget;

import j$.time.ZoneId;
import j$.util.DesugarTimeZone;
import java.util.TimeZone;

/* renamed from: j$.retarget.$r8$retargetLibraryMember$virtualDispatch$TimeZone$toZoneId$dispatchHolder  reason: invalid class name */
public /* synthetic */ class C$r8$retargetLibraryMember$virtualDispatch$TimeZone$toZoneId$dispatchHolder {
    public static /* synthetic */ ZoneId toZoneId(TimeZone timeZone) {
        return timeZone instanceof C$r8$retargetLibraryMember$virtualDispatch$TimeZone$toZoneId$dispatchInterface ? ((C$r8$retargetLibraryMember$virtualDispatch$TimeZone$toZoneId$dispatchInterface) timeZone).toZoneId() : DesugarTimeZone.toZoneId(timeZone);
    }
}

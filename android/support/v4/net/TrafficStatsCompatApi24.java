package android.support.v4.net;

import android.net.TrafficStats;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import java.net.DatagramSocket;
import java.net.SocketException;

@RestrictTo({Scope.GROUP_ID})
public class TrafficStatsCompatApi24 {
    public static void tagDatagramSocket(DatagramSocket socket) throws SocketException {
        TrafficStats.tagDatagramSocket(socket);
    }

    public static void untagDatagramSocket(DatagramSocket socket) throws SocketException {
        TrafficStats.untagDatagramSocket(socket);
    }
}

package org.telegram.messenger;

import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class EmuInputDevicesDetector {
    private static final String INPUT_DEVICES_FILE = "/proc/bus/input/devices";
    private static final String NAME_PREFIX = "N: Name=\"";
    private static final String[] RESTRICTED_DEVICES = {"bluestacks", "memuhyperv", "virtualbox"};

    private EmuInputDevicesDetector() {
    }

    public static boolean detect() {
        List<String> deviceNames = getInputDevicesNames();
        if (deviceNames != null) {
            for (String deviceName : deviceNames) {
                String[] strArr = RESTRICTED_DEVICES;
                int length = strArr.length;
                int i = 0;
                while (true) {
                    if (i < length) {
                        if (deviceName.toLowerCase().contains(strArr[i])) {
                            return true;
                        }
                        i++;
                    }
                }
            }
        }
        return false;
    }

    private static List<String> getInputDevicesNames() {
        File devicesFile = new File("/proc/bus/input/devices");
        if (!devicesFile.canRead()) {
            return null;
        }
        try {
            List<String> lines = new ArrayList<>();
            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(devicesFile)));
            while (true) {
                String readLine = r.readLine();
                String line = readLine;
                if (readLine == null) {
                    return lines;
                }
                if (line.startsWith("N: Name=\"")) {
                    String name = line.substring("N: Name=\"".length(), line.length() - 1);
                    if (!TextUtils.isEmpty(name)) {
                        lines.add(name);
                    }
                }
            }
        } catch (IOException e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }
}

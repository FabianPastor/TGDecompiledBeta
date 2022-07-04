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
        List<String> inputDevicesNames = getInputDevicesNames();
        if (inputDevicesNames != null) {
            for (String next : inputDevicesNames) {
                String[] strArr = RESTRICTED_DEVICES;
                int length = strArr.length;
                int i = 0;
                while (true) {
                    if (i < length) {
                        if (next.toLowerCase().contains(strArr[i])) {
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
        File file = new File("/proc/bus/input/devices");
        if (!file.canRead()) {
            return null;
        }
        try {
            ArrayList arrayList = new ArrayList();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    return arrayList;
                }
                if (readLine.startsWith("N: Name=\"")) {
                    String substring = readLine.substring(9, readLine.length() - 1);
                    if (!TextUtils.isEmpty(substring)) {
                        arrayList.add(substring);
                    }
                }
            }
        } catch (IOException e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }
}

package org.telegram.messenger;

import java.util.ArrayList;

public class FileLoaderPriorityQueue {
    private int PRIORITY_VALUE_LOW = 0;
    private int PRIORITY_VALUE_MAX = 1048576;
    private int PRIORITY_VALUE_NORMAL = 65536;
    ArrayList<FileLoadOperation> activeOperations = new ArrayList<>();
    ArrayList<FileLoadOperation> allOperations = new ArrayList<>();
    private final int maxActiveOperationsCount;
    String name;

    FileLoaderPriorityQueue(String str, int i) {
        this.name = str;
        this.maxActiveOperationsCount = i;
    }

    public void add(FileLoadOperation fileLoadOperation) {
        if (fileLoadOperation != null) {
            int i = -1;
            this.allOperations.remove(fileLoadOperation);
            int i2 = 0;
            while (true) {
                if (i2 >= this.allOperations.size()) {
                    break;
                } else if (fileLoadOperation.getPriority() > this.allOperations.get(i2).getPriority()) {
                    i = i2;
                    break;
                } else {
                    i2++;
                }
            }
            if (i >= 0) {
                this.allOperations.add(i, fileLoadOperation);
            } else {
                this.allOperations.add(fileLoadOperation);
            }
        }
    }

    public void cancel(FileLoadOperation fileLoadOperation) {
        if (fileLoadOperation != null) {
            this.allOperations.remove(fileLoadOperation);
            fileLoadOperation.cancel();
        }
    }

    public void checkLoadingOperations() {
        boolean z = false;
        int i = 0;
        for (int i2 = 0; i2 < this.allOperations.size(); i2++) {
            FileLoadOperation fileLoadOperation = this.allOperations.get(i2);
            if (i2 > 0 && !z && i > this.PRIORITY_VALUE_LOW && fileLoadOperation.getPriority() == this.PRIORITY_VALUE_LOW) {
                z = true;
            }
            if (!z && i2 < this.maxActiveOperationsCount) {
                fileLoadOperation.start();
            } else if (fileLoadOperation.wasStarted()) {
                fileLoadOperation.pause();
            }
            i = fileLoadOperation.getPriority();
        }
    }

    public void remove(FileLoadOperation fileLoadOperation) {
        if (fileLoadOperation != null) {
            this.allOperations.remove(fileLoadOperation);
        }
    }

    private FileLoadOperation remove() {
        if (this.allOperations.isEmpty()) {
            return null;
        }
        return this.allOperations.remove(0);
    }
}

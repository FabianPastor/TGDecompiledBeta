package org.telegram.messenger.exoplayer2.util;

public final class LibraryLoader {
    private boolean isAvailable;
    private boolean loadAttempted;
    private String[] nativeLibraries;

    public LibraryLoader(String... libraries) {
        this.nativeLibraries = libraries;
    }

    public synchronized void setLibraries(String... libraries) {
        Assertions.checkState(this.loadAttempted ^ 1, "Cannot set libraries after loading");
        this.nativeLibraries = libraries;
    }

    public synchronized boolean isAvailable() {
        if (this.loadAttempted) {
            return this.isAvailable;
        }
        this.loadAttempted = true;
        try {
            for (String lib : this.nativeLibraries) {
                System.loadLibrary(lib);
            }
            this.isAvailable = true;
        } catch (UnsatisfiedLinkError e) {
        }
        return this.isAvailable;
    }
}

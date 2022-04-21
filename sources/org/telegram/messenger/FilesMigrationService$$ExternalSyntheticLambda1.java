package org.telegram.messenger;

import j$.util.function.Consumer;
import java.io.File;
import java.nio.file.Path;

public final /* synthetic */ class FilesMigrationService$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ FilesMigrationService f$0;
    public final /* synthetic */ File f$1;

    public /* synthetic */ FilesMigrationService$$ExternalSyntheticLambda1(FilesMigrationService filesMigrationService, File file) {
        this.f$0 = filesMigrationService;
        this.f$1 = file;
    }

    public final void accept(Object obj) {
        this.f$0.m604x55fd53fa(this.f$1, (Path) obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }
}

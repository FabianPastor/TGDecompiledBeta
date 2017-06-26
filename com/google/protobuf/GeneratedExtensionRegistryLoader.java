package com.google.protobuf;

import java.util.logging.Logger;

abstract class GeneratedExtensionRegistryLoader<T extends zzd> {
    private static final Logger logger = Logger.getLogger(zzb.class.getName());

    GeneratedExtensionRegistryLoader() {
    }

    protected abstract T getInstance();
}

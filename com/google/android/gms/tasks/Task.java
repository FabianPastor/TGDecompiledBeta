package com.google.android.gms.tasks;

import java.util.concurrent.Executor;

public abstract class Task<TResult> {
    public Task<TResult> addOnCompleteListener(Executor executor, OnCompleteListener<TResult> onCompleteListener) {
        throw new UnsupportedOperationException("addOnCompleteListener is not implemented");
    }

    public abstract Task<TResult> addOnFailureListener(Executor executor, OnFailureListener onFailureListener);

    public abstract Task<TResult> addOnSuccessListener(Executor executor, OnSuccessListener<? super TResult> onSuccessListener);

    public abstract Exception getException();

    public abstract TResult getResult();

    public abstract boolean isComplete();

    public abstract boolean isSuccessful();
}

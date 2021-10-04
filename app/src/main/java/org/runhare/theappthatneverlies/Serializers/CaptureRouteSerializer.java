package org.runhare.theappthatneverlies.Serializers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.datastore.core.Serializer;

import org.runhare.theappthatneverlies.AppData;


import java.io.InputStream;
import java.io.OutputStream;

import kotlin.Unit;
import kotlin.coroutines.Continuation;

public class CaptureRouteSerializer implements Serializer<AppData> {
    @Override
    public AppData getDefaultValue() {
        return AppData.getDefaultInstance();
    }

    @Nullable
    @Override
    public Object readFrom(@NonNull InputStream inputStream, @NonNull Continuation<? super AppData> continuation) {
        return this.readFrom(inputStream, continuation);
    }

    @Nullable
    @Override
    public Object writeTo(AppData routeCapture, @NonNull OutputStream outputStream, @NonNull Continuation<? super Unit> continuation) {
        return this.writeTo(routeCapture, outputStream, continuation);
    }
}

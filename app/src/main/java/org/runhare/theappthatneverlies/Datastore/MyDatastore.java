package org.runhare.theappthatneverlies.Datastore;

import android.content.Context;

import androidx.datastore.rxjava3.RxDataStore;
import androidx.datastore.rxjava3.RxDataStoreBuilder;

import org.runhare.theappthatneverlies.AppData;
import org.runhare.theappthatneverlies.Serializers.CaptureRouteSerializer;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;


public class MyDatastore {

    RxDataStore<AppData> routeCaptureDataStore;

    public Flowable<AppData> getRouteCaptureFlow() {
        return routeCaptureFlow;
    }

    private Flowable<AppData> routeCaptureFlow;

    public MyDatastore(Context context) {
        routeCaptureDataStore = new RxDataStoreBuilder<>(context, "Appdata.pb", new CaptureRouteSerializer()).build();
        routeCaptureFlow = routeCaptureDataStore.data();
    }

    public void addRouteIndex() {
        routeCaptureDataStore.updateDataAsync(capture ->
                Single.just(capture.toBuilder()
                        .setRouteIndex(capture.getRouteIndex() + 1)
                        .build()
                )
                );
    }

    public int getRouteIndex() {
        return routeCaptureFlow.map(AppData::getRouteIndex).blockingFirst();
    }





}

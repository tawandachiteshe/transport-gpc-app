package org.runhare.theappthatneverlies.API;

import android.util.Log;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import org.runhare.theappthatneverlies.Activities.UploadActivity;
import org.runhare.theappthatneverlies.CaptureRoute;
import org.runhare.theappthatneverlies.CaptureRouteUploadGrpc;
import org.runhare.theappthatneverlies.RouteCapture;
import org.runhare.theappthatneverlies.RouteCaptureControllerGrpc;
import org.runhare.theappthatneverlies.RouteCaptureRequest;


import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class GrpcClient {
    String url;
    int port;
    ManagedChannel managedChannel;
    RouteCaptureControllerGrpc.RouteCaptureControllerStub uploadStub;

    public GrpcClient(String url, int port){
        managedChannel = ManagedChannelBuilder.forAddress(url, port).usePlaintext().build();
        uploadStub = RouteCaptureControllerGrpc.newStub(managedChannel);
    }

    public void uploadRoute(RouteCapture route, StreamObserver<RouteCapture> streamObserver) {
        uploadStub.withDeadlineAfter(5000, TimeUnit.MILLISECONDS).create(route, streamObserver);
    }




}

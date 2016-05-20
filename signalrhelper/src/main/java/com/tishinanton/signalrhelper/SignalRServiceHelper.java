package com.tishinanton.signalrhelper;

import android.util.Log;

import com.google.gson.JsonElement;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.ConnectionState;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.Logger;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.StateChangedCallback;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.transport.LongPollingTransport;

/**
 * Created by atish on 20.05.2016.
 */
public class SignalRServiceHelper {

    private HubConnection connection;
    private HubProxy hubProxy;

    private StateChangedCallback stateChangedCallback = null;
    private ErrorCallback errorCallback = null;
    private Runnable closedCallBack = null;
    private Runnable connectedCallback = null;
    private Action<Void> startedCallback = null;

    public SignalRServiceHelper(String url, String proxyName) {
        Platform.loadPlatformComponent(new AndroidPlatformComponent());
        connection = new HubConnection(url, true);
        hubProxy = connection.createHubProxy(proxyName);

        connection.stateChanged(new StateChangedCallback() {
            @Override
            public void stateChanged(ConnectionState connectionState, ConnectionState connectionState1) {
                if (stateChangedCallback != null) stateChangedCallback.stateChanged(connectionState, connectionState1);
            }
        });
        connection.connected(new Runnable() {
            @Override
            public void run() {
                if (connectedCallback != null) connectedCallback.run();
            }
        });
        connection.error(new ErrorCallback() {
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                if (errorCallback != null) errorCallback.onError(throwable);
            }
        });
        connection.closed(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (closedCallBack != null) closedCallBack.run();
            }
        });

    }

    public void start() {
        Logger logger = new Logger() {
            @Override
            public void log(String message, LogLevel level) {
                Log.d("SignalR", message);
            }
        };
        connection.start(new LongPollingTransport(logger)).done(new Action<Void>() {
            @Override
            public void run(Void aVoid) throws Exception {
                if (startedCallback != null) startedCallback.run(aVoid);
            }
        });
    }

    public void subscribe(String methodName, Action<JsonElement[]> receivedHandler) {
        hubProxy.subscribe(methodName).addReceivedHandler(receivedHandler);
    }

    public void invoke(String sendMessage, Object... args) {
        hubProxy.invoke(sendMessage, args);
    }
}

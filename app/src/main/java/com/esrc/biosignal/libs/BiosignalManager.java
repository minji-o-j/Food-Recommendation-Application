package com.esrc.biosignal.libs;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.esrc.biosignal.service.BiosignalService;
import com.esrc.biosignal.service.SignalData;
import com.esrc.biosignal.service.StartRMData;
import com.esrc.biosignal.service.StateData;
import com.esrc.biosignal.signalutils.SignalManager;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by lhw48 on 2016-06-22.
 */
public class BiosignalManager {
    private static final String TAG = "BiosignalManager";

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTED = 1;

    private static Context context;
    private static BiosignalManager client = null;
    private Map<BiosignalConsumer, Boolean> consumers = new HashMap<BiosignalConsumer, Boolean>();
    private Messenger serviceMessenger = null;
    protected SignalNotifier signalNotifier = null;
    protected StateNotifier stateNotifier = null;

    private static SignalManager signalManager = new SignalManager();

    public static BiosignalManager getInstanceForApplication(Context context) {
        if (!isInstantiated()) {
            Log.d(TAG, "BiosignalManager instance creation");
            client = new BiosignalManager(context);
        }
        return client;
    }

    private BiosignalManager(Context context) {
        this.context = context;
    }

    public void bind(BiosignalConsumer consumer) {
        if(consumers.keySet().contains(consumer)) {
            Log.i(TAG, "This consumer is already bound");
        } else {
            Log.i(TAG, "This consumer is not bound, binding: " + consumer);
            consumers.put(consumer, false);
            Intent intent = new Intent(consumer.getApplicationContext(), BiosignalService.class);
            consumer.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
            Log.i(TAG, "consumer count is now:" + consumers.size());
        }
    }

    public void unBind(BiosignalConsumer consumer) {
        if(consumers.keySet().contains(consumer)) {
            Log.i(TAG, "Unbinding");
            consumer.unbindService(mServiceConnection);
            consumers.remove(consumer);
        } else {
            Log.i(TAG, "This consumer is not bound to: " + consumer);
            Log.i(TAG, "Bound consumers: ");
            for(int i=0; i<consumers.size(); i++) {
                Log.i(TAG,  " " + consumers.get(i));
            }
        }
    }

    public void setSignalNotifier(SignalNotifier notifier) {
        signalNotifier = notifier;
    }

    public void setStateNotifier(StateNotifier notifier) {
        stateNotifier = notifier;
    }

    // connect
    public void connect(Integer uniqueId, String address) throws RemoteException {
        Message msg = Message.obtain(null, BiosignalService.MSG_CONNECT_DEVICE, 0, 0);
        msg.obj = new StartRMData(uniqueId, address);
        msg.replyTo = statingCallback;
        serviceMessenger.send(msg);
    }

    // disconnect
    public void disconnect(Integer uniqueId) throws RemoteException {
        Message msg = Message.obtain(null, BiosignalService.MSG_DISCONNECT_DEVICE, 0, 0);
        msg.obj = new StartRMData(uniqueId);
        serviceMessenger.send(msg);
    }

    // start ppg
    public void startSignaling(Integer uniqueId) throws RemoteException {
        Message msg = Message.obtain(null, BiosignalService.MSG_SIGNALING_START, 0, 0);
        msg.obj = new StartRMData(uniqueId);
        msg.replyTo = signalingCallback;
        serviceMessenger.send(msg);
    }

    // stop
    public void stopSignaling(Integer uniqueId) throws RemoteException {
        Message msg = Message.obtain(null, BiosignalService.MSG_SIGNALING_STOP, 0, 0);
        msg.obj = new StartRMData(uniqueId);
        serviceMessenger.send(msg);
    }

    // UART service connected/disconnected
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "we have a connection to the service now");
            serviceMessenger = new Messenger(service);
            Iterator<BiosignalConsumer> consumerIterator = consumers.keySet().iterator();
            while (consumerIterator.hasNext()) {
                BiosignalConsumer consumer = consumerIterator.next();
                Boolean alreadyConnected = consumers.get(consumer);
                if (!alreadyConnected) {
                    consumer.onBiosignalServiceConnect();
                    consumers.put(consumer, true);
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected");
        }
    };

    // signal callback
    static class IncommingSignalHandler extends Handler {
        private final WeakReference<BiosignalManager> biosignalManager;

        IncommingSignalHandler(BiosignalManager manager) {
            this.biosignalManager = new WeakReference<BiosignalManager>(manager);
        }

        @Override
        public void handleMessage(Message msg) {
            SignalData data = (SignalData) msg.obj;
            BiosignalManager manager = biosignalManager.get();
            if (data == null) {
                Log.d(TAG, "null signal received");
                return;
            }
            if (manager.signalNotifier != null) {
                //Log.d(TAG, "Calling ppg signaling notifier on :" + manager.signalNotifier);
                int ppg = (int) data.getValue();
                double bpm = signalManager.add(ppg);
                manager.signalNotifier.onReceivedPPG((int) data.getValue());
                if(bpm != 0) {
                    manager.signalNotifier.onReceivedBPM(bpm);
                }
            }
        }
    }

    final Messenger signalingCallback = new Messenger(new IncommingSignalHandler(this));

    public SignalNotifier getSignalNotifier() {
        return signalNotifier;
    }

    // state callback
    static class IncommingStateHandler extends Handler {
        private final WeakReference<BiosignalManager> biosignalManager;

        IncommingStateHandler(BiosignalManager manager) {
            this.biosignalManager = new WeakReference<BiosignalManager>(manager);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    super.handleMessage(msg);
                    StateData data = (StateData) msg.obj;
                    if (data == null) {
                        Log.d(TAG, "null state received");
                    } else {
                        BiosignalManager manager = biosignalManager.get();
                        if (manager.stateNotifier != null) {
                            Log.d(TAG, "Calling signaling notifier on :" + manager.stateNotifier);
                            manager.stateNotifier.didChangedState(data.getState());
                        }
                    }
            }
        }
    }

    final Messenger statingCallback = new Messenger(new IncommingStateHandler(this));

    public StateNotifier getStateNotifier() {
        return stateNotifier;
    }

    /**
     * Determines if the singleton has been constructed already. Useful for not overriding settings set declaratively in
     * XML
     *
     * @return true, if the class has been constructed
     */
    public static boolean isInstantiated() {
        return (client != null);
    }

    public static final int REQUEST_PERMISSION_COARSE_LOCATION = 0;

    @TargetApi(23)
    public boolean checkPermission(Activity activity) {
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return true;
        }

        if(activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            //if (activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))
            //Toast.makeText(activity, "ACCESS_FINE_LOCATION", Toast.LENGTH_SHORT).show();

            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_COARSE_LOCATION);
        } else {
            return true;
        }

        return false;
    }

    public String getLastConnectedAddress() {
        return context.getSharedPreferences("android-biosignal-service", MODE_PRIVATE).getString("address", "00:00:00:00");
    }

    public int getConnectionState() {
        return context.getSharedPreferences("android-biosignal-service", MODE_PRIVATE).getInt("connection_state", STATE_DISCONNECTED);
    }
}

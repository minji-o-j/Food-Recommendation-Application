package com.esrc.biosignal.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.esrc.biosignal.libs.BiosignalManager;
import com.esrc.biosignal.libs.Signal;
import com.esrc.biosignal.libs.State;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lhw48 on 2016-06-22.
 */
public class BiosignalService extends Service {
    private final static String TAG = "BiosignalService";

    private Map<Integer, Callback> signalCallback = new HashMap<Integer, Callback>();
    private Map<Integer, Callback> stateCallback = new HashMap<Integer, Callback>();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = BiosignalManager.STATE_DISCONNECTED;
    private int bindCount = 0;

    private SharedPreferences pref;

    // Incoming handler message
    public static final int MSG_CONNECT_DEVICE = 10;
    public static final int MSG_DISCONNECT_DEVICE = 11;
    public static final int MSG_SIGNALING_START = 12;
    public static final int MSG_SIGNALING_STOP = 13;

    // Biosignal protocol
    private static final byte[] INT_STX = new String("r").getBytes();
    private static final byte[] INT_ETX = new String("q").getBytes();

    public static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID RX_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID RX_CHAR_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID TX_CHAR_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");

    class IncomingHandler extends Handler {
        private final WeakReference<BiosignalService> mService;

        IncomingHandler(BiosignalService service) {
            mService = new WeakReference<BiosignalService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            BiosignalService service = mService.get();
            StartRMData startRMData = (StartRMData) msg.obj;

            if(service != null) {
                switch(msg.what) {
                    case MSG_CONNECT_DEVICE:
                        Log.d(TAG, "MSG_CONNECT_DEVICE");
                        if(mConnectionState == BiosignalManager.STATE_DISCONNECTED) {
                            String address = startRMData.getAddress();
                            service.connect(startRMData.getUniqueId(), address, new com.esrc.biosignal.service.Callback(msg.replyTo));
                        } else {
                            Log.d(TAG, "mConnectionState is " + mConnectionState);
                        }
                        break;
                    case MSG_DISCONNECT_DEVICE:
                        Log.d(TAG, "MSG_DISCONNECT_DEVICE");
                        if(mConnectionState == BiosignalManager.STATE_CONNECTED) {
                            service.disconnect(startRMData.getUniqueId());
                        } else {
                            Log.d(TAG, "mConnectionState is " + mConnectionState);
                        }
                        break;
                    case MSG_SIGNALING_START:
                        Log.d(TAG, "MSG_SIGNALING_START");
                        if(mConnectionState == BiosignalManager.STATE_CONNECTED) {
                            service.startSignaling(startRMData.getUniqueId(), new com.esrc.biosignal.service.Callback(msg.replyTo));
                        }
                        break;
                    case MSG_SIGNALING_STOP:
                        Log.d(TAG, "MSG_SIGNALING_STOP");
                        if(mConnectionState == BiosignalManager.STATE_CONNECTED) {
                            service.stopSignaling(startRMData.getUniqueId());
                        }
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler(this));

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "binding");
        bindCount++;
        return mMessenger.getBinder();
    }
    @Override
    public boolean onUnbind (Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        Log.i(TAG, "unbind called");
        bindCount--;
        if(bindCount <= 0) close();
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate of BiosignalService called");
        initialize();
        pref = getSharedPreferences("android-biosignal-service", MODE_PRIVATE);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy of BiosignalService called");
        super.onDestroy();
    }

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnectionState = BiosignalManager.STATE_CONNECTED;
                pref.edit().putInt("connection_state", BiosignalManager.STATE_CONNECTED).commit();
                Log.i(TAG, "Connected to GATT server.");

                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

                // state callback
                State state = State.fromReceivedData(mConnectionState);
                Iterator<Integer> stateCallbackIterator = stateCallback.keySet().iterator();
                while(stateCallbackIterator.hasNext()) {
                    stateCallback.get(stateCallbackIterator.next()).call(new StateData(state));
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                if(mConnectionState != BiosignalManager.STATE_DISCONNECTED) {
                    mConnectionState = BiosignalManager.STATE_DISCONNECTED;
                    pref.edit().putInt("connection_state", BiosignalManager.STATE_DISCONNECTED).commit();
                    Log.i(TAG, "Disconnected from GATT server.");

                    // state callback
                    State state = State.fromReceivedData(mConnectionState);
                    Iterator<Integer> stateCallbackIterator = stateCallback.keySet().iterator();
                    while(stateCallbackIterator.hasNext()) {
                        stateCallback.get(stateCallbackIterator.next()).call(new StateData(state));
                    }

                    if(pref.getBoolean("connecting", false)) {
                        Log.d(TAG, "reconnecting");
                        mBluetoothGatt.connect();
                    }

                }
                /*if(pref.getBoolean("connecting", false)) {
                    autoReconnect();
                }*/
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.w(TAG, "mBluetoothGatt = " + mBluetoothGatt);
                enableTXNotification();
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if(characteristic.getValue().length == 2) {
                    Signal signal = Signal.fromReceivedData(characteristic);
                    Iterator<Integer> signalCallbackIterator = signalCallback.keySet().iterator();
                    while(signalCallbackIterator.hasNext()) {
                        signalCallback.get(signalCallbackIterator.next()).call(new SignalData(signal));
                    }
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            if(characteristic.getValue().length == 2) {
                Signal signal = Signal.fromReceivedData(characteristic);
                Iterator<Integer> signalCallbackIterator = signalCallback.keySet().iterator();
                while(signalCallbackIterator.hasNext()) {
                    signalCallback.get(signalCallbackIterator.next()).call(new SignalData(signal));
                }
            }
        }
    };

    public class LocalBinder extends Binder {
        BiosignalService getService() {
            return BiosignalService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(Integer uniqueId, String address, Callback callback) {
        pref.edit().putBoolean("connecting", true).commit();

        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        if(stateCallback.containsKey(uniqueId)) {
            Log.d(TAG, "Already stating -- will replace existing callback");
            stateCallback.remove(uniqueId);
        }
        stateCallback.put(uniqueId, callback);

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if(mBluetoothGatt.connect()) {
                return true;
            }
            else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }

        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        pref.edit().putString("address", address).commit();
        mBluetoothDeviceAddress = address;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect(Integer uniqueId) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
        pref.edit().putBoolean("connecting", false).commit();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        Log.w(TAG, "mBluetoothGatt closed");
        mBluetoothDeviceAddress = null;
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enable Notification on TX characteristic
     *
     * @return
     */
    public void enableTXNotification()
    {
    	/*
    	if (mBluetoothGatt == null) {
    		showMessage("mBluetoothGatt null" + mBluetoothGatt);
    		broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
    		return;
    	}
    		*/
        BluetoothGattService RxService = mBluetoothGatt.getService(RX_SERVICE_UUID);
        if (RxService == null) {
            showMessage("Rx service not found!");
            return;
        }
        BluetoothGattCharacteristic TxChar = RxService.getCharacteristic(TX_CHAR_UUID);
        if (TxChar == null) {
            showMessage("Tx characteristic not found!");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(TxChar,true);

        BluetoothGattDescriptor descriptor = TxChar.getDescriptor(CCCD);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);

    }

    public void writeRXCharacteristic(byte[] value)
    {
        BluetoothGattService RxService = mBluetoothGatt.getService(RX_SERVICE_UUID);
        showMessage("mBluetoothGatt null" + mBluetoothGatt);
        if (RxService == null) {
            showMessage("Rx service not found!");
            return;
        }
        BluetoothGattCharacteristic RxChar = RxService.getCharacteristic(RX_CHAR_UUID);
        if (RxChar == null) {
            showMessage("Rx characteristic not found!");
            return;
        }
        RxChar.setValue(value);
        boolean status = mBluetoothGatt.writeCharacteristic(RxChar);

        Log.d(TAG, "write TXchar - status=" + status);
    }

    private void showMessage(String msg) {
        Log.e(TAG, msg);
    }
    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    public void startSignaling(Integer uniqueId, Callback callback) {
        if(signalCallback.containsKey(uniqueId)) {
            Log.d(TAG, "Already signaling -- will replace existing callback");
            signalCallback.remove(uniqueId);
        }
        signalCallback.put(uniqueId, callback);
        if(mConnectionState == BiosignalManager.STATE_CONNECTED) {
            writeRXCharacteristic(INT_STX);
        }
    }

    public void stopSignaling(Integer uniqueId) {
        signalCallback.remove(uniqueId);
        if(mConnectionState == BiosignalManager.STATE_CONNECTED) {
            writeRXCharacteristic(INT_ETX);
        }
    }
}
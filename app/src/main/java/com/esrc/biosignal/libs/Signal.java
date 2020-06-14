package com.esrc.biosignal.libs;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by lhw48 on 2016-06-22.
 */
public class Signal {
    protected double value;

    public static Signal fromReceivedData(double value) {
        return new Signal(value);
    }

    public double getValue() {
        return value;
    }

    public static Signal fromReceivedData(BluetoothGattCharacteristic characteristic) {
        byte[] value = characteristic.getValue();
        double result = 0;
        if(value.length == 2) {
            result = calByteData(value[0], value[1]);  // ppg
            //result =(double)calByteData(value[0], value[1]) / 10000.f;  // ppi
        }
        return new Signal(result);
    }

    // Sum data
    private static int calByteData(Byte highBuffer, Byte lowBuffer) {
        return (int)(highBuffer & 0xff) * 256 + (int)(lowBuffer & 0xff);
    }

    protected Signal(double value) {
        this.value = value;
    }

    protected Signal(Signal otherSignal) {
        value = otherSignal.value;
    }

    protected Signal() {
        value = 0;
    }
}

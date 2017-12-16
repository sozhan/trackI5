package tmm.tracki5.model;

/**
 * Created by Arun on 25/02/16.
 */
public class MyBluetoothDevice {

    public String DeviceName;

    public String DeviceHardwareAddress;

    public MyBluetoothDevice(String name, String address)
    {
        DeviceName = name;
        DeviceHardwareAddress = address;
    }

    public boolean IsEqual(MyBluetoothDevice obj)
    {
        return obj.DeviceHardwareAddress.equals(DeviceHardwareAddress);
    }
}

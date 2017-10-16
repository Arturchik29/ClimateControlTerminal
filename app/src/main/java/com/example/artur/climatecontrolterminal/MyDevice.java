package com.example.artur.climatecontrolterminal;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;

import java.util.HashMap;

public class MyDevice {

    public static final int MESSAGE_SIZE = 14;

    public MyDevice(Context context, MyDeviceEventReceiver eventReceiver) {
        _usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        _permissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
        //_context = context;
        _eventReceiver = eventReceiver;
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        context.registerReceiver(_usbReceiver, filter);
    }

    public void Connect() {
        _device = FindDevice();
        if (_device == null) {
            _eventReceiver.SetConnectionStatus(ConnectionStatus.DeviceNotFound);
            return;
        }
        if (!_usbManager.hasPermission(_device)) {

            /*UsbManager manager = (UsbManager) _context.getApplicationContext().getSystemService(Context.USB_SERVICE);
            IBinder b = ServiceManager.getService(Context.USB_SERVICE);
            IUsbManager service = IUsbManager.Stub.asInterface(b);
            PackageManager pm = _context.getPackageManager();
            ApplicationInfo ai = null;
            try {
                ai = pm.getApplicationInfo( "com.example.artur.climatecontrolterminal", 0 );
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            try {
                service.grantDevicePermission( _device, ai.uid );
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            try {
                service.setDevicePackage( _device, "com.example.artur.climatecontrolterminal", ai.uid );
            } catch (RemoteException e) {
                e.printStackTrace();
            }
*/
            _usbManager.requestPermission( _device, _permissionIntent );
            return;
        }
        ConnectWithPermissions();
    }

    public boolean IsReady() {
        return _device != null;
    }

    public void SendCommand(DeviceCommand command) {
        if (_device == null) {
            _delayedCommand = command;
            Connect();
            return;
        }
        SendCommandImmediately(command);
    }

    public void RequestLCD(){
        SendCommand( new DeviceReport() );
    }

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static final int USBRQ_HID_SET_REPORT = 9;
    private static final int USBRQ_HID_GET_REPORT = 1;
    private UsbDevice _device;
    private volatile boolean _commandInAction = false;
    private final PendingIntent _permissionIntent;
    //private final Context _context;
    private final MyDeviceEventReceiver _eventReceiver;
    private final UsbManager _usbManager;
    private DeviceCommand _delayedCommand;
    private UsbInterface _usbInterface;
    private UsbDeviceConnection _connection;

    private final BroadcastReceiver _usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        ConnectWithPermissions();
                    } else {
                        Disconnect();
                        _eventReceiver.SetConnectionStatus(ConnectionStatus.PermissionDenied);
                    }
                    _delayedCommand = null;
                }
            }
        }
    };

    private void ConnectWithPermissions(){
        _usbInterface = _device.getInterface(0);
        _connection = _usbManager.openDevice(_device);
        _connection.claimInterface(_usbInterface, true);
        if (_delayedCommand != null) {
            SendCommandImmediately(_delayedCommand);
        } else {
            _eventReceiver.SetConnectionStatus(ConnectionStatus.Connected);
        }
    }

    private void Disconnect() {
        _device = null;
        if (_connection != null) {
            _connection.close();
            _connection = null;
        }
        _usbInterface = null;
    }

    private void SendCommandImmediately(final DeviceCommand command) {
        if (_commandInAction || _device == null) return;
        _commandInAction = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                final byte[] bytes = command.GetBytes();
                int bytesRead;
                if (command instanceof DeviceReport) {
                    bytesRead = _connection.controlTransfer(
                            UsbConstants.USB_TYPE_CLASS | UsbConstants.USB_DIR_IN,
                            USBRQ_HID_GET_REPORT,
                            0,                      // wValue: ReportType (highbyte), ReportID (lowbyte)
                            0,
                            bytes,
                            bytes.length,
                            200);
                } else {
                    bytesRead = _connection.controlTransfer(
                            UsbConstants.USB_TYPE_CLASS | UsbConstants.USB_DIR_OUT,
                            USBRQ_HID_SET_REPORT,
                            0,                      // wValue: ReportType (highbyte), ReportID (lowbyte)
                            0,
                            bytes,
                            bytes.length,
                            200);
                }

                if (bytesRead != bytes.length) {
                    _eventReceiver.SetConnectionStatus(command instanceof DeviceReport ? ConnectionStatus.ReadError : ConnectionStatus.WriteError, command.TryCountRemains-1);
                    if (--command.TryCountRemains > 0) {
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        _commandInAction = false;
                        SendCommandImmediately(command);
                        return;
                    }
                    else
                    {
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        _device = null;
                        command.ResetTryCount();
                        SendCommand( command );
                    }
                } else {
                    _eventReceiver.CommandComplete(command);
                    if (!(command instanceof DeviceReport)) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        _commandInAction = false;
                        SendCommandImmediately(new DeviceReport());
                        return;
                    }
                }
                _commandInAction = false;
            }
        }).start();
    }

    private UsbDevice FindDevice() {
        HashMap<String, UsbDevice> deviceList = _usbManager.getDeviceList();
        for (UsbDevice device : deviceList.values()) {
            if (device.getVendorId() == 5824 && device.getProductId() == 1488) {
                return device;
            }
        }
        return null;
    }
}

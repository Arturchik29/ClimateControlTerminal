package com.example.artur.climatecontrolterminal;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements MyDeviceEventReceiver {

    private static final int PERMISSION_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //noinspection ResourceType
        //setTheme(0x010300f1);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        /*ActivityCompat.requestPermissions(this,
                new String[] {
                        "android.permission.MANAGE_USB"
                },
                PERMISSION_REQUEST_CODE);
*/

        _device = new MyDevice(this, this);
        _drawer = new LCDDrawer(getResources());
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;

        switch (requestCode){
            case PERMISSION_REQUEST_CODE:

                for (int res : grantResults){
                    // if user granted all permissions.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }

                break;
            default:
                // if user not granted permissions.
                allowed = false;
                break;
        }

        if (allowed) {
            //user granted all permissions we can perform our task.
            _device = new MyDevice(this, this);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            _device.Connect();
                        }
                    });
                }
            }).start();

        }
    }



    private MyDevice _device;
    private LCDDrawer _drawer;

    private void SetButtonState(Button btn, int state) {

        switch (state) {
            case 0:
                btn.setShadowLayer(0, 0, 0, 0);
                btn.setTextColor(Color.BLACK);
                return;
            case 1:
                btn.setTextColor(Color.rgb(0, 255, 0));
                break;
            case 2:
                btn.setTextColor(Color.YELLOW);
                break;
            case 3:
                btn.setTextColor(Color.BLUE);
                break;
        }
        btn.setShadowLayer(9, 1, 1, Color.rgb(44, 44, 44));
    }

    private static final SparseArray<Byte> _btnDictionary;

    static {
        _btnDictionary = new SparseArray<>();
        _btnDictionary.put(R.id.btnFanMinus, (byte) 4);
        _btnDictionary.put(R.id.btnFanPlus, (byte) 3);
        _btnDictionary.put(R.id.btnMode, (byte) 12);
        _btnDictionary.put(R.id.btnPassTempMinus, (byte) 5);
        _btnDictionary.put(R.id.btnPassTempPlus, (byte) 6);
        _btnDictionary.put(R.id.btnTempMinus, (byte) 7);
        _btnDictionary.put(R.id.btnTempPlus, (byte) 8);
        _btnDictionary.put(R.id.btnAC, (byte) 2);
        _btnDictionary.put(R.id.btnDual, (byte) 0);
        _btnDictionary.put(R.id.btnRear, (byte) 10);
        _btnDictionary.put(R.id.btnFront, (byte) 1);
        _btnDictionary.put(R.id.btnRecirc, (byte) 11);
        _btnDictionary.put(R.id.btnAuto, (byte) 9);
        _btnDictionary.put(R.id.btnOff, (byte) 13);
    }

    public void btnClick(View view) {
        int id = view.getId();

        if (id == R.id.btnReadLCD) {
            _device.RequestLCD();
            return;
        }

        byte param = _btnDictionary.get(id, Byte.MAX_VALUE);
        if (param == Byte.MAX_VALUE) return;

        _device.SendCommand(new DeviceCommand(CommandType.PushButton, param));
    }

    @Override
    public void SetConnectionStatus(ConnectionStatus status) {
        SetConnectionStatus(status, -1);
    }

    @Override
    public void SetConnectionStatus(final ConnectionStatus status, final int tryRemains) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String tc = tryRemains < 0 ? "" : " " + Integer.toString(tryRemains);
                int resId = -1;
                switch (status) {
                    case NotYetConnected:
                        resId = R.string.StatusNotYetConnected;
                        break;
                    case Connected:
                        resId = R.string.StatusConnected;
                        _device.RequestLCD();
                        break;
                    case DeviceNotFound:
                        resId = R.string.StatusDeviceNotFound;

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        _device.Connect();
                                    }
                                });
                            }
                        }).start();

                        break;
                    case ReadError:
                        resId = R.string.StatusReadError;
                        break;
                    case WriteError:
                        resId = R.string.StatusWriteError;
                        break;
                    case PermissionDenied:
                        resId = R.string.StatusPermissionDenied;
                        break;
                }
                if ( resId != -1 )
                    ((TextView) findViewById(R.id.epta)).setText(getResources().getString(resId) + tc);
            }
        });
    }

    /*private void SetLeds( int value ) {
        SetButtonSatate(((Button) findViewById(R.id.btnFront)), (value & (1 << 0)) != 0 ? 1 : 0);
        SetButtonSatate(((Button) findViewById(R.id.btnAuto)), (value & (1 << 2)) != 0 ? 1 : 0);
        SetButtonSatate(((Button) findViewById(R.id.btnAC)), (value & (1 << 3)) != 0 ? 1 : 0);
        SetButtonSatate(((Button) findViewById(R.id.btnDual)), (value & (1 << 6)) != 0 ? 1 : 0);
        SetButtonSatate(((Button) findViewById(R.id.btnRear)), (value & (1 << 7)) != 0 ? 1 : 0);
        if ( (( value & ( 1 << 1 )) == 0 )  && ( (value & ( 1 << 4 ) )== 0 ))
            SetButtonSatate(((Button) findViewById(R.id.btnRecirc)), 0);
        else
            SetButtonSatate(((Button) findViewById(R.id.btnRecirc)), (value & (1 << 1)) != 0 ? 2 : 3);
    }*/


    @Override
    public void CommandComplete(final DeviceCommand command) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.epta)).setText("Command ok!");
                if (!(command instanceof DeviceReport)) return;
                DeviceReport report = (DeviceReport) command;
                report.Decode();
                ((ImageView) findViewById(R.id.btnReadLCD)).setImageDrawable(_drawer.GetDrawable(report));

                ((ToggleButton)findViewById(R.id.btnFront)).setChecked( report.GetBlowModeFront() );
                ((ToggleButton)findViewById(R.id.btnAuto)).setChecked( report.GetAuto() );
                ((ToggleButton)findViewById(R.id.btnAC)).setChecked( report.GetAC() );
                ((ToggleButton)findViewById(R.id.btnDual)).setChecked( report.GetDual() );
                ((ToggleButton)findViewById(R.id.btnRear)).setChecked( report.GetRear() );

                /*SetButtonState(((Button) findViewById(R.id.btnFront)), report.GetBlowModeFront() ? 1 : 0);
                SetButtonState(((Button) findViewById(R.id.btnAuto)), report.GetAuto() ? 1 : 0);
                SetButtonState(((Button) findViewById(R.id.btnAC)), report.GetAC() ? 1 : 0);
                SetButtonState(((Button) findViewById(R.id.btnDual)), report.GetDual() ? 1 : 0);
                SetButtonState(((Button) findViewById(R.id.btnRear)), report.GetRear() ? 1 : 0);*/

                AirCirculation airCirculation = report.GetAirCirculation();

                switch (airCirculation) {
                    case Fresh:
                        SetButtonState(((Button) findViewById(R.id.btnRecirc)), 3);
                        break;
                    case Recirculation:
                        SetButtonState(((Button) findViewById(R.id.btnRecirc)), 2);
                        break;
                    case None:
                        SetButtonState(((Button) findViewById(R.id.btnRecirc)), 0);
                        break;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        _device.RequestLCD();
                    }
                }).start();

            }
        });
    }
}
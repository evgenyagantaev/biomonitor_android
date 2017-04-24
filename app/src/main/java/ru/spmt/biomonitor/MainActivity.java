package ru.spmt.biomonitor;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity
{

    private TextView main_text_panel_var;
    private UsbManager usb_manager;

    private static final String ACTION_USB_PERMISSION =
            "ru.spmt.biomonitor.USB_PERMISSION";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_text_panel_var = (TextView)findViewById(R.id.main_text_panel);
        main_text_panel_var.setMovementMethod(new ScrollingMovementMethod());

        PendingIntent permission_intent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);

    }

    public void on_start_button_click(View view)
    {
        UsbDevice device;
        boolean nodevices = true;

        usb_manager = (UsbManager)getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usb_manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        while(deviceIterator.hasNext())
        {
            nodevices = false;
            device = deviceIterator.next();
            clog("getDeviceName: " + device.getDeviceName());
            clog("toString: " + device.toString());
        }
        if (nodevices)
        {
            clog("Устройства USB не найдены");
        }
    }

    private void clog(String text)
    {
        if(main_text_panel_var != null)
        {
            main_text_panel_var.append(text + "\n");
            final Layout layout = main_text_panel_var.getLayout();
            if(layout != null)
            {
                int scrollDelta = layout.getLineBottom(main_text_panel_var.getLineCount() - 1)
                        - main_text_panel_var.getScrollY() - main_text_panel_var.getHeight();
                if(scrollDelta > 0)
                    main_text_panel_var.scrollBy(0, scrollDelta);
            }
        }
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver()
    {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(device != null){
                            //call method to set up device communication
                            clog("device = " + device);
                        }
                    }
                    else {
                        clog("permission denied for device" + device);
                    }
                }
            }
        }
    };
}

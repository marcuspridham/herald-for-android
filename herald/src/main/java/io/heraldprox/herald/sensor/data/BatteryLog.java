//  Copyright 2020-2021 Herald Project Contributors
//  SPDX-License-Identifier: Apache-2.0
//

package io.heraldprox.herald.sensor.data;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import io.heraldprox.herald.sensor.datatype.TimeInterval;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/// CSV battery log for post event analysis and visualisation
public class BatteryLog {
    private final SensorLogger logger = new ConcreteSensorLogger("Sensor", "BatteryLog");
    private final static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
    static {
        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    private final static TimeInterval updateInterval = TimeInterval.seconds(30);
    private final Context context;
    private final TextFile textFile;

    public BatteryLog(final Context context, final String filename) {
        this.context = context;
        textFile = new TextFile(context, filename);
        if (textFile.empty()) {
            textFile.write("time,source,level");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        update();
                    } catch (Throwable e) {
                        logger.fault("Update failed", e);
                    }
                    try {
                        Thread.sleep(updateInterval.millis());
                    } catch (Throwable e) {
                        logger.fault("Timer interrupted", e);
                    }
                }
            }
        }).start();
    }

    private void update() {
        final IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        final Intent batteryStatus = context.registerReceiver(null, intentFilter);
        if (null == batteryStatus) {
            return;
        }
        final int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        final boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
        final int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        final int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        final float batteryLevel = level * 100 / (float) scale;

        final String powerSource = (isCharging ? "external" : "battery");
        final String timestamp = dateFormatter.format(new Date());
        textFile.write(timestamp + "," + powerSource + "," + batteryLevel);
        logger.debug("update (powerSource={},batteryLevel={})", powerSource, batteryLevel);
    }
}

//  Copyright 2020-2021 Herald Project Contributors
//  SPDX-License-Identifier: Apache-2.0
//

package io.heraldprox.herald.sensor.data;

import android.content.Context;

import io.heraldprox.herald.sensor.datatype.Location;
import io.heraldprox.herald.sensor.datatype.PayloadData;
import io.heraldprox.herald.sensor.datatype.Proximity;
import io.heraldprox.herald.sensor.datatype.SensorType;
import io.heraldprox.herald.sensor.datatype.TargetIdentifier;
import io.heraldprox.herald.sensor.DefaultSensorDelegate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/// CSV contact log for post event analysis and visualisation
public class ContactLog extends DefaultSensorDelegate {
    private final static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
    static {
        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    private final TextFile textFile;
    private final PayloadDataFormatter payloadDataFormatter;

    public ContactLog(final Context context, final String filename, PayloadDataFormatter payloadDataFormatter) {
        textFile = new TextFile(context, filename);
        this.payloadDataFormatter = payloadDataFormatter;
        if (textFile.empty()) {
            textFile.write("time,sensor,id,detect,read,measure,share,visit,data");
        }
    }

    public ContactLog(final Context context, final String filename) {
        this(context, filename, new ConcretePayloadDataFormatter());
    }

    private String timestamp() {
        return dateFormatter.format(new Date());
    }

    private String csv(String value) {
        return TextFile.csv(value);
    }

    // MARK:- SensorDelegate

    @Override
    public void sensor(SensorType sensor, TargetIdentifier didDetect) {
        textFile.write(timestamp() + "," + sensor.name() + "," + csv(didDetect.value) + ",1,,,,,");
    }

    @Override
    public void sensor(SensorType sensor, PayloadData didRead, TargetIdentifier fromTarget) {
        textFile.write(timestamp() + "," + sensor.name() + "," + csv(fromTarget.value) + ",,2,,,," + csv(payloadDataFormatter.shortFormat(didRead)));
    }

    @Override
    public void sensor(SensorType sensor, List<PayloadData> didShare, TargetIdentifier fromTarget) {
        final String prefix = timestamp() + "," + sensor.name() + "," + csv(fromTarget.value);
        for (PayloadData payloadData : didShare) {
            textFile.write(prefix + ",,,,4,," + csv(payloadDataFormatter.shortFormat(payloadData)));
        }
    }

    @Override
    public void sensor(SensorType sensor, Proximity didMeasure, TargetIdentifier fromTarget) {
        textFile.write(timestamp() + "," + sensor.name() + "," + csv(fromTarget.value) + ",,,3,,," + csv(didMeasure.description()));
    }

    @Override
    public void sensor(SensorType sensor, Location didVisit) {
        textFile.write(timestamp() + "," + sensor.name() + ",,,,,,5," + csv(didVisit.description()));
    }
}

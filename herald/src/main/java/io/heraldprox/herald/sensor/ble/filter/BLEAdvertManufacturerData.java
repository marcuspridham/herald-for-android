//  Copyright 2020-2021 Herald Project Contributors
//  SPDX-License-Identifier: Apache-2.0
//

package io.heraldprox.herald.sensor.ble.filter;

import io.heraldprox.herald.sensor.datatype.Data;

public class BLEAdvertManufacturerData {
    public final int manufacturer;
    public final byte[] data; // BIG ENDIAN (network order) AT THIS POINT
    public final Data raw;

    public BLEAdvertManufacturerData(final int manufacturer, final byte[] dataBigEndian, final Data raw) {
        this.manufacturer = manufacturer;
        this.data = dataBigEndian;
        this.raw = raw;
    }

    @Override
    public String toString() {
        return "BLEAdvertManufacturerData{" +
                "manufacturer=" + manufacturer +
                ", data=" + new Data(data).hexEncodedString() +
                ", raw=" + raw.hexEncodedString() +
                '}';
    }
}

//  Copyright 2020-2021 Herald Project Contributors
//  SPDX-License-Identifier: Apache-2.0
//

package io.heraldprox.herald.sensor.payload.simple;

import io.heraldprox.herald.sensor.data.ConcreteSensorLogger;
import io.heraldprox.herald.sensor.data.SensorLogger;
import io.heraldprox.herald.sensor.datatype.Data;

import java.security.MessageDigest;

/// Elementary functions
public class F {
    private final static SensorLogger logger = new ConcreteSensorLogger("Sensor", "Payload.SimplePayloadDataSupplier");

    /**
     * Cryptographic hash function : SHA256
     */
    protected final static Data hash(final Data data) {
        try {
            final MessageDigest sha = MessageDigest.getInstance("SHA-256");
            final byte[] hash = sha.digest(data.value);
            return new Data(hash);
        } catch (Throwable e) {
            logger.fault("SHA-256 unavailable", e);
            return null;
        }
    }

    /**
     * Truncation function : Delete second half of data
     */
    protected final static Data truncate(final Data data) {
        return truncate(data, data.value.length / 2);
    }

    /**
     * Truncation function : Retain first n bytes of data
     */
    protected final static Data truncate(final Data data, int n) {
        return data.subdata(0, n);
    }

    /**
     * XOR function : Compute left xor right, assumes left and right are the same length
     */
    protected final static Data xor(final Data left, final Data right) {
        final byte[] leftByteArray = left.value;
        final byte[] rightByteArray = right.value;
        final byte[] resultByteArray = new byte[left.value.length];
        for (int i=0; i<leftByteArray.length; i++) {
            resultByteArray[i] = (byte) (leftByteArray[i] ^ rightByteArray[i]);
        }
        return new Data(resultByteArray);
    }
}

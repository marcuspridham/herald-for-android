package org.c19x.sensor.payload.c19x;

import android.util.Base64;

import java.util.Arrays;

/// Shared secret between device and server for deriving day codes and beacon codes.
public class SharedSecret {
    public byte[] value;

    public SharedSecret(byte[] value) {
        this.value = value;
    }

    public SharedSecret(String base64EncodedString) {
        this.value = Base64.decode(base64EncodedString, Base64.DEFAULT);
    }

    @Override
    public String toString() {
        return "SharedSecret{" +
                "value=" + Arrays.toString(value) +
                '}';
    }
}

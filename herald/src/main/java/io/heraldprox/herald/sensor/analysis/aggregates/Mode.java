//  Copyright 2021 Herald Project Contributors
//  SPDX-License-Identifier: Apache-2.0
//

package io.heraldprox.herald.sensor.analysis.aggregates;

import io.heraldprox.herald.sensor.analysis.sampling.Aggregate;
import io.heraldprox.herald.sensor.analysis.sampling.Sample;
import io.heraldprox.herald.sensor.datatype.DoubleValue;

import java.util.HashMap;
import java.util.Map;

public class Mode<T extends DoubleValue> implements Aggregate<T> {
    private int run = 1;
    private Map<Double,Counter> counts = new HashMap<>();
    private final static class Counter {
        public long value = 1;
    }

    @Override
    public int runs() {
        return 1;
    }

    @Override
    public void beginRun(int thisRun) {
        run = thisRun;
    }

    @Override
    public void map(Sample<T> value) {
        if (run > 1) return;
        Counter counter = counts.get(value.value().doubleValue());
        if (null == counter) {
            counts.put(value.value().doubleValue(), new Counter());
        } else {
            counter.value++;
        }
    }

    @Override
    public Double reduce() {
        if (counts.isEmpty()) {
            return null;
        }
        double largest = 0;
        long largestCount = 0;
        for (Map.Entry<Double,Counter> entry : counts.entrySet()) {
            if (entry.getValue().value > largestCount || (entry.getValue().value == largestCount && entry.getKey() > largest)) {
                largest = entry.getKey();
                largestCount = entry.getValue().value;
            }
        }
        return largest;
    }

    @Override
    public void reset() {
        counts.clear();
    }
}

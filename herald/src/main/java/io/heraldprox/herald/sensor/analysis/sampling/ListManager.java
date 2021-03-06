//  Copyright 2021 Herald Project Contributors
//  SPDX-License-Identifier: Apache-2.0
//

package io.heraldprox.herald.sensor.analysis.sampling;

import io.heraldprox.herald.sensor.datatype.DoubleValue;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ListManager<T extends DoubleValue> {
    private final int listSize;
    private final Map<SampledID, SampleList<T>> map = new ConcurrentHashMap<>();

    public ListManager(final int listSize) {
        this.listSize = listSize;
    }

    public synchronized SampleList<T> list(final SampledID listFor) {
        SampleList<T> list = map.get(listFor);
        if (null == list) {
            list = new SampleList<>(listSize);
            map.put(listFor, list);
        }
        return list;
    }

    public synchronized Set<SampledID> sampledIDs() {
        return map.keySet();
    }

    public synchronized void remove(final SampledID listFor) {
        map.remove(listFor);

    }

    public synchronized int size() {
        return map.size();
    }

    public synchronized void clear() {
        map.clear();
    }

    public synchronized void push(final SampledID sampledID, final Sample<T> sample) {
        list(sampledID).push(sample);
    }
}

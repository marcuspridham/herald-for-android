//  Copyright 2021 Herald Project Contributors
//  SPDX-License-Identifier: Apache-2.0
//

package io.heraldprox.herald.sensor.analysis.sampling;

import io.heraldprox.herald.sensor.datatype.DoubleValue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class VariantSet {
    private final int defaultListSize;
    private final Map<Class<? extends DoubleValue>, ListManager<? extends DoubleValue>> map = new ConcurrentHashMap<>();

    public VariantSet(final int defaultListSize) {
        this.defaultListSize = defaultListSize;
    }

    public Set<Class<? extends DoubleValue>> variants() {
        return map.keySet();
    }

    public Set<SampledID> sampledIDs() {
        final Set<SampledID> sampledIDs = new HashSet<>();
        for (final ListManager<? extends DoubleValue> listManager : map.values()) {
            sampledIDs.addAll(listManager.sampledIDs());
        }
        return sampledIDs;
    }

    public <T extends DoubleValue> ListManager<T> add(final Class<T> variant, final int listSize) {
        final ListManager<T> listManager = new ListManager<>(listSize);
        map.put(variant, listManager);
        return listManager;
    }

    public <T extends DoubleValue> void remove(final Class<T> variant) {
        map.remove(variant);
    }

    public void remove(SampledID sampledID) {
        for (final ListManager<? extends DoubleValue> listManager : map.values()) {
            listManager.remove(sampledID);
        }
    }

    public void clear() {
        map.clear();
    }

    public <T extends DoubleValue> ListManager<T> listManager(final Class<T> variant) {
        ListManager<T> listManager = (ListManager<T>) map.get(variant);
        if (null == listManager) {
            listManager = add(variant, defaultListSize);
        }
        return listManager;
    }

    public <T extends DoubleValue> SampleList<T> listManager(final Class<T> variant, final SampledID listFor) {
        final ListManager<T> listManager = listManager(variant);
        return listManager.list(listFor);
    }

    public int size() {
        return map.size();
    }

    public <T extends DoubleValue> void push(final SampledID sampledID, final Sample<T> sample) {
        ((ListManager<T>) listManager(sample.value().getClass())).push(sampledID, sample);
    }
}

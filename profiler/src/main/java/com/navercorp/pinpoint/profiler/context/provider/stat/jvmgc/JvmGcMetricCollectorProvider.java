/*
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.profiler.context.provider.stat.jvmgc;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import com.navercorp.pinpoint.profiler.monitor.collector.AgentStatMetricCollector;
import com.navercorp.pinpoint.profiler.monitor.collector.jvmgc.DetailedJvmGcMetricCollector;
import com.navercorp.pinpoint.profiler.monitor.collector.jvmgc.BasicJvmGcMetricCollector;
import com.navercorp.pinpoint.profiler.monitor.metric.JvmGcMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.gc.DetailedGarbageCollectorMetric;
import com.navercorp.pinpoint.profiler.monitor.metric.gc.GarbageCollectorMetric;
import com.navercorp.pinpoint.profiler.monitor.metric.memory.DetailedMemoryMetric;
import com.navercorp.pinpoint.profiler.monitor.metric.memory.MemoryMetric;


/**
 * @author HyunGil Jeong
 */
public class JvmGcMetricCollectorProvider implements Provider<AgentStatMetricCollector<JvmGcMetricSnapshot>> {

    private final boolean collectDetailedMetrics;
    private final Provider<MemoryMetric> memoryMetricProivider;
    private final Provider<DetailedMemoryMetric> detailedMemoryMetricProvider;
    private final Provider<GarbageCollectorMetric> garbageCollectorMetricProvider;
    private final Provider<DetailedGarbageCollectorMetric> detailedGarbageCollectorMetricProvider;

    @Inject
    public JvmGcMetricCollectorProvider(
            ProfilerConfig profilerConfig,
            Provider<MemoryMetric> memoryMetricProivider,
            Provider<DetailedMemoryMetric> detailedMemoryMetricProvider,
            Provider<GarbageCollectorMetric> garbageCollectorMetricProvider,
            Provider<DetailedGarbageCollectorMetric> detailedGarbageCollectorMetricProvider) {
        if (profilerConfig == null) {
            throw new NullPointerException("profilerConfig");
        }
        if (memoryMetricProivider == null) {
            throw new NullPointerException("memoryMetricProivider");
        }
        if (detailedMemoryMetricProvider == null) {
            throw new NullPointerException("detailedMemoryMetricProvider");
        }
        if (garbageCollectorMetricProvider == null) {
            throw new NullPointerException("garbageCollectorMetricProvider");
        }
        if (detailedGarbageCollectorMetricProvider == null) {
            throw new NullPointerException("detailedGarbageCollectorMetricProvider");
        }
        this.collectDetailedMetrics = profilerConfig.isProfilerJvmStatCollectDetailedMetrics();
        this.memoryMetricProivider = memoryMetricProivider;
        this.detailedMemoryMetricProvider = detailedMemoryMetricProvider;
        this.garbageCollectorMetricProvider = garbageCollectorMetricProvider;
        this.detailedGarbageCollectorMetricProvider = detailedGarbageCollectorMetricProvider;
    }

    @Override
    public AgentStatMetricCollector<JvmGcMetricSnapshot> get() {
        MemoryMetric memoryMetric = memoryMetricProivider.get();
        GarbageCollectorMetric garbageCollectorMetric = garbageCollectorMetricProvider.get();
        BasicJvmGcMetricCollector jvmGcMetricCollector = new BasicJvmGcMetricCollector(memoryMetric, garbageCollectorMetric);
        if (collectDetailedMetrics) {
            DetailedMemoryMetric detailedMemoryMetric = detailedMemoryMetricProvider.get();
            DetailedGarbageCollectorMetric detailedGarbageCollectorMetric = detailedGarbageCollectorMetricProvider.get();
            return new DetailedJvmGcMetricCollector(jvmGcMetricCollector, detailedMemoryMetric, detailedGarbageCollectorMetric);
        }
        return jvmGcMetricCollector;
    }
}

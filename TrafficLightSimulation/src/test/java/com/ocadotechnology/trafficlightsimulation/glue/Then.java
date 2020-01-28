/*
 * Copyright © 2017-2020 Ocado (Ocava)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ocadotechnology.trafficlightsimulation.glue;

import com.ocadotechnology.scenario.NotificationCache;
import com.ocadotechnology.scenario.ScenarioNotificationListener;
import com.ocadotechnology.scenario.StepManager;
import com.ocadotechnology.scenario.TimeThenSteps;
import com.ocadotechnology.scenario.UnorderedSteps;
import com.ocadotechnology.trafficlightsimulation.TrafficSimulationApi;
import com.ocadotechnology.trafficlightsimulation.steps.CarThenSteps;
import com.ocadotechnology.trafficlightsimulation.steps.SimulationThenSteps;
import com.ocadotechnology.trafficlightsimulation.steps.TrafficLightThenSteps;

public class Then {

    //generic steps
    public final SimulationThenSteps simulation;
    public final TimeThenSteps time;
    public final UnorderedSteps unordered;

    //simulation specific steps
    public final CarThenSteps car;
    public final TrafficLightThenSteps trafficLight;

    public Then(StepManager stepManager, TrafficSimulationApi simulationApi, ScenarioNotificationListener listener, NotificationCache notificationCache) {

        this.simulation = new SimulationThenSteps(stepManager, notificationCache);
        this.time = new TimeThenSteps(simulationApi, stepManager, listener);

        this.unordered = new UnorderedSteps(stepManager);
        this.car = new CarThenSteps(stepManager, notificationCache);
        trafficLight = new TrafficLightThenSteps(stepManager, notificationCache);
    }
}

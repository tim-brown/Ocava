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
package com.ocadotechnology.trafficlightsimulation.steps;

import com.ocadotechnology.scenario.AbstractWhenSteps;
import com.ocadotechnology.scenario.StepManager;
import com.ocadotechnology.trafficlightsimulation.TrafficSimulationApi;
import com.ocadotechnology.trafficlightsimulation.controller.TrafficLightController.State;

public class TrafficLightWhenSteps extends AbstractWhenSteps {
    private final TrafficSimulationApi simulationApi;

    public TrafficLightWhenSteps(StepManager stepManager, TrafficSimulationApi simulationApi) {
        super(stepManager);
        this.simulationApi = simulationApi;
    }

    public void isChangedTo(State state) {
        addExecuteStep(() -> simulationApi.getTrafficSimulation().getTrafficLightController().changeState(state));
    }
}

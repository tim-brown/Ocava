/*
 * Copyright © 2017-2023 Ocado (Ocava)
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
package com.ocadotechnology.config;

import java.time.Duration;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.ocadotechnology.physics.units.LengthUnit;

/**
 * Parser class to convert a config value into a typed optional result. All parsing methods will return {@link
 * Optional#empty()} if the value is am empty String.
 */
public class OptionalValueParser {
    private final Optional<StrictValueParser> parser;

    @VisibleForTesting
    OptionalValueParser(Enum<?> key, String value) {
        this(key, value, null, null);
    }

    OptionalValueParser(Enum<?> key, String value, @Nullable TimeUnit timeUnit, @Nullable LengthUnit lengthUnit) {
        if (value.isEmpty()) {
            parser = Optional.empty();
        } else {
            parser = Optional.of(new StrictValueParser(key, value, timeUnit, lengthUnit));
        }
    }

    /**
     * @return {@link Optional#empty()} if the config value is an empty String, otherwise returns an {@link Optional}
     *          containing the the config value.
     */
    public Optional<String> asString() {
        return parser.map(StrictValueParser::asString);
    }

    /**
     * @return {@link Optional#empty()} if the config value is an empty String, otherwise returns an {@link Optional}
     *          containing the string config value parsed to a boolean.
     * @throws IllegalStateException if the config value does not strictly equal "true" or "false", case insensitive.
     */
    public Optional<Boolean> asBoolean() {
        return parser.map(StrictValueParser::asBoolean);
    }

    /**
     * @return {@link OptionalInt#empty()} if the config value is an empty String, otherwise returns an {@link
     *          OptionalInt} containing the string config value parsed to an integer. If the value is the String "max"
     *          or "min" (case insensitive) parses the value to {@link Integer#MAX_VALUE} or {@link Integer#MIN_VALUE}
     *          respectively, otherwise defers to {@link Integer#parseInt(String)}.
     * @throws NumberFormatException if the config value cannot be parsed to an integer.
     */
    public OptionalInt asInt() {
        return parser.map(p -> OptionalInt.of(p.asInt())).orElse(OptionalInt.empty());
    }

    /**
     * @return {@link OptionalLong#empty()} if the config value is an empty String, otherwise returns an {@link
     *          OptionalLong} containing the string config value parsed to a long. If the value is the String "max" or
     *          "min" (case insensitive) parses the value to {@link Long#MAX_VALUE} or {@link Long#MIN_VALUE}
     *          respectively, otherwise defers to {@link Long#parseLong(String)}.
     * @throws NumberFormatException if the config value cannot be parsed to a long.
     */
    public OptionalLong asLong() {
        return parser.map(p -> OptionalLong.of(p.asLong())).orElse(OptionalLong.empty());
    }

    /**
     * @return {@link OptionalDouble#empty()} if the config value is an empty String, otherwise returns an {@link
     *          OptionalDouble} containing the string config value parsed to a double via {@link
     *          Double#parseDouble(String)}.
     * @throws NumberFormatException if the config value cannot be parsed to a double.
     */
    public OptionalDouble asDouble() {
        return parser.map(p -> OptionalDouble.of(p.asDouble())).orElse(OptionalDouble.empty());
    }

    /**
     * @return {@link Optional#empty()} if the config value is an empty String, otherwise returns an {@link Optional}
     *          containing the string config value parsed to an enum value via {@link Enum#valueOf(Class, String)}.
     * @throws IllegalArgumentException if the string config value does not match a defined enum value.
     */
    public <T extends Enum<T>> Optional<T> asEnum(Class<T> enumClass) {
        return parser.map(p -> p.asEnum(enumClass));
    }

    /**
     * @return {@link Optional#empty()} if the config value is an empty String, otherwise returns an {@link Optional}
     *          containing the string config value parsed as a time using the declared application time unit.
     * <p>
     * Time config values can be given either
     * - as a double, in which case Config will assume that the value is being specified in s
     * - in the form {@code <value>,<time unit>} or {@code <value>:<time unit>}
     *
     * @throws NullPointerException       if the application time unit has not been set
     * @throws IllegalStateException      if the config value does not satisfy one of the formats given above
     * @throws IllegalArgumentException   if the time unit in the config value does not match an enum value
     * @throws NumberFormatException      if the value given cannot be parsed as a double
     */
    public OptionalDouble asFractionalTime() {
        return parser.map(p -> OptionalDouble.of(p.asFractionalTime())).orElse(OptionalDouble.empty());
    }

    /**
     * @return {@link Optional#empty()} if the config value is an empty String, otherwise returns an {@link Optional}
     *          containing the string config value parsed as a time using the declared application time unit, rounded
     *          to the nearest whole number of units.
     * <p>
     * Time config values can be given either
     * - as a double, in which case Config will assume that the value is being specified in seconds
     * - in the form {@code <value>,<time unit>} or {@code <value>:<time unit>}.
     *
     * @throws NullPointerException       if the application time unit has not been set.
     * @throws IllegalStateException      if the config value does not satisfy one of the formats given above.
     * @throws IllegalArgumentException   if the time unit in the config value does not match an enum value.
     * @throws NumberFormatException      if the value given cannot be parsed as a double.
     */
    public OptionalLong asTime() {
        return parser.map(p -> OptionalLong.of(p.asTime())).orElse(OptionalLong.empty());
    }

    /**
     * @return {@link Optional#empty()} if the config value is an empty String, otherwise returns an {@link Optional}
     *          containing the string config value parsed as a {@link Duration} rounded to the nearest nanosecond.
     * <p>
     * Duration config values can be given either:
     * - As a double on its own, in which case it will be assumed that the value is being specified in seconds
     * - In the form {@code <value>,<time unit>} or {@code <value>:<time unit>}.
     *
     * @throws IllegalStateException      if the config value does not satisfy one of the formats given above.
     * @throws IllegalArgumentException   if the time unit in the config value does not match an enum value.
     * @throws NumberFormatException      if the value given cannot be parsed as a double.
     */
    public Optional<Duration> asDuration() {
        return parser.map(StrictValueParser::asDuration);
    }

    /**
     * @return {@link Optional#empty()} if the config value is an empty String, otherwise returns an {@link Optional}
     *          containing the string config value parsed as a length using the declared application length unit.
     * <p>
     * Length config values can be given either
     * - as a double, in which case Config will assume that the value is being specified in meters.
     * - in the form {@code <value>,<length unit>} or {@code <value>:<length unit>}.
     *
     * @throws NullPointerException       if the application length unit has not been set.
     * @throws IllegalStateException      if the config value does not satisfy one of the formats given above.
     * @throws IllegalArgumentException   if the length unit in the config value does not match an enum value.
     * @throws NumberFormatException      if the value given cannot be parsed as a double.
     */
    public OptionalDouble asLength() {
        return parser.map(p -> OptionalDouble.of(p.asLength())).orElse(OptionalDouble.empty());
    }

    /**
     * @return {@link Optional#empty()} if the config value is an empty String, otherwise returns an {@link Optional}
     *          containing the string config value parsed as a speed using the declared application time and length
     *          units.
     * <p>
     * Speed config values can be given either
     * - as a double, in which case Config will assume that the value is being specified in meters per second
     * - in the form {@code <value>,<length unit>,<time unit>} or {@code <value>:<length unit>:<time unit>}
     *
     * @throws NullPointerException       if the application time or length units have not been set
     * @throws IllegalStateException      if the config value does not satisfy one of the formats given above
     * @throws IllegalArgumentException   if the time or length units in the config value do not match an enum value
     * @throws NumberFormatException      if the value given cannot be parsed as a double
     */
    public OptionalDouble asSpeed() {
        return parser.map(p -> OptionalDouble.of(p.asSpeed())).orElse(OptionalDouble.empty());
    }

    /**
     * @return {@link Optional#empty()} if the config value is an empty String, otherwise returns an {@link Optional}
     *          containing the string config value parsed as an acceleration using the declared application time and
     *          length units.
     * <p>
     * Acceleration config values can be given either
     * - as a double, in which case Config will assume that the value is being specified in meters per second squared
     * - in the form {@code <value>,<length unit>,<time unit>} or {@code <value>:<length unit>:<time unit>}
     *
     * @throws NullPointerException       if the application time or length units have not been set
     * @throws IllegalStateException      if the config value does not satisfy one of the formats given above
     * @throws IllegalArgumentException   if the time or length units in the config value do not match an enum value
     * @throws NumberFormatException      if the value given cannot be parsed as a double
     */
    public OptionalDouble asAcceleration() {
        return parser.map(p -> OptionalDouble.of(p.asAcceleration())).orElse(OptionalDouble.empty());
    }

    /**
     * @return {@link Optional#empty()} if the config value is an empty String, otherwise returns an {@link Optional}
     *          containing the string config value parsed as a jerk using the declared application time and length
     *          units.
     * <p>
     * Jerk config values can be given either
     * - as a double, in which case Config will assume that the value is being specified in meters per second cubed
     * - in the form {@code <value>,<length unit>,<time unit>} or {@code <value>:<length unit>:<time unit>}
     *
     * @throws NullPointerException       if the application time or length units have not been set
     * @throws IllegalStateException      if the config value does not satisfy one of the formats given above
     * @throws IllegalArgumentException   if the time or length units in the config value do not match an enum value
     * @throws NumberFormatException      if the value given cannot be parsed as a double
     */
    public OptionalDouble asJerk() {
        return parser.map(p -> OptionalDouble.of(p.asJerk())).orElse(OptionalDouble.empty());
    }

    /**
     * @return a {@link OptionalListValueParser} operating on the String config value.
     */
    public OptionalListValueParser asList() {
        return new OptionalListValueParser(parser.map(StrictValueParser::asList));
    }

    /**
     * @return a {@link OptionalSetValueParser} operating on the String config value.
     */
    public OptionalSetValueParser asSet() {
        return new OptionalSetValueParser(parser.map(StrictValueParser::asSet));
    }

    /**
     * @return a {@link OptionalMapValueParser} operating on the String config value.
     */
    public OptionalMapValueParser asMap() {
        return new OptionalMapValueParser(parser.map(StrictValueParser::asMap));
    }

    /**
     * @return a {@link OptionalMapValueParser} operating on the String config value.
     */
    public OptionalSetMultimapValueParser asSetMultimap() {
        return new OptionalSetMultimapValueParser(parser.map(StrictValueParser::asSetMultimap));
    }

    /**
     * @return {@link Optional#empty()} if the config value is an empty String, otherwise returns an {@link Optional}
     *          containing the the result of the the provided custom parser applied to the config value.
     */
    public <T> Optional<T> withCustomParser(Function<String, T> parser) {
        return this.parser.map(p -> p.withCustomParser(parser));
    }

    /**
     * @deprecated to help avoid calling this when {@link OptionalValueParser#asString()} is desired
     */
    @Deprecated
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("value", asString().orElse(""))
                .toString();
    }
}

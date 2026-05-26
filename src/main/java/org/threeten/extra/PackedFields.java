/*
 * Copyright (c) 2007-present, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.threeten.extra;

import static java.time.temporal.ChronoField.EPOCH_DAY;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.SECOND_OF_DAY;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.FOREVER;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.format.ResolverStyle;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;
import java.util.Map;

/**
 * Temporal fields based on a packed representation.
 * <p>
 * This provides three fields that use a packed integer representation for dates and times.
 */
public final class PackedFields {

    /**
     * Packed date field.
     * <p>
     * This returns the date as a single integer value.
     * Only dates from year 1000 to year 9999 are supported.
     * The output is always an 8 digit integer.
     * For example, the date 2015-12-03 is packed to the integer 20151203.
     * <p>
     * This field has invalid values within the range of value values.
     * For example, 20121301 is invalid as it implies month 13.
     * <p>
     * When parsing in {@linkplain ResolverStyle#LENIENT lenient mode}, invalid
     * dates will be accepted. For example, 20121301 will result in 2013-01-01.
     */
    public static final TemporalField PACKED_DATE = PackedDate.INSTANCE;

    /**
     * Packed hour-minute time field.
     * <p>
     * This returns the time as a single integer value.
     * The output is an integer from 0 to 2359.
     * For example, the date 11:30 is packed to the integer 1130.
     * <p>
     * This field has invalid values within the range of value values.
     * For example, 1073 is invalid as it implies the minute is 73.
     * <p>
     * When parsing in {@linkplain ResolverStyle#LENIENT lenient mode}, invalid
     * times will be accepted. For example, 1073 will result in 11:13.
     */
    public static final TemporalField PACKED_HOUR_MIN = PackedHourMin.INSTANCE;

    /**
     * Packed hour-minute-second time field.
     * <p>
     * This returns the time as a single integer value.
     * The output is an integer from 0 to 235959.
     * For example, the date 11:30:52 is packed to the integer 113052.
     * <p>
     * This field has invalid values within the range of value values.
     * For example, 107310 is invalid as it implies the minute is 73.
     * <p>
     * When parsing in {@linkplain ResolverStyle#LENIENT lenient mode}, invalid
     * times will be accepted. For example, 107310 will result in 11:13:10.
     */
    public static final TemporalField PACKED_TIME = PackedTime.INSTANCE;

    /**
     * Restricted constructor.
     */
    private PackedFields() {
    }

    //-------------------------------------------------------------------------
    /**
     * Implementation of packed date.
     */
    private static enum PackedDate implements TemporalField {

        INSTANCE;

        private static final ValueRange RANGE = ValueRange.of(10000101, 99991231);

        private static final long serialVersionUID = -38752465672576L;

        //-----------------------------------------------------------------------
        @Override
        public TemporalUnit getBaseUnit() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public TemporalUnit getRangeUnit() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public boolean isDateBased() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public boolean isTimeBased() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public ValueRange range() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        //-----------------------------------------------------------------------
        @Override
        public boolean isSupportedBy(TemporalAccessor temporal) {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public ValueRange rangeRefinedBy(TemporalAccessor temporal) {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public long getFrom(TemporalAccessor temporal) {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R extends Temporal> R adjustInto(R temporal, long newValue) {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        private LocalDate toDate(long newValue) {
            if (range().isValidValue(newValue) == false) {
                throw new DateTimeException("Invalid value: PackedDate " + newValue);
            }
            int val = (int) newValue;
            int year = val / 10000;
            int moy = (val % 10000) / 100;
            int dom = val % 100;
            return LocalDate.of(year, moy, dom);
        }

        //-----------------------------------------------------------------------
        @Override
        public ChronoLocalDate resolve(Map<TemporalField, Long> fieldValues, TemporalAccessor partialTemporal, ResolverStyle resolverStyle) {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        //-----------------------------------------------------------------------
        @Override
        public String toString() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }
    }

    //-------------------------------------------------------------------------
    /**
     * Implementation of packed hour-min.
     */
    private static enum PackedHourMin implements TemporalField {

        INSTANCE;

        private static final ValueRange RANGE = ValueRange.of(0, 2359);

        private static final long serialVersionUID = -871357658587L;

        //-----------------------------------------------------------------------
        @Override
        public TemporalUnit getBaseUnit() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public TemporalUnit getRangeUnit() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public boolean isDateBased() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public boolean isTimeBased() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public ValueRange range() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        //-----------------------------------------------------------------------
        @Override
        public boolean isSupportedBy(TemporalAccessor temporal) {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public ValueRange rangeRefinedBy(TemporalAccessor temporal) {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public long getFrom(TemporalAccessor temporal) {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R extends Temporal> R adjustInto(R temporal, long newValue) {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        //-----------------------------------------------------------------------
        @Override
        public ChronoLocalDate resolve(Map<TemporalField, Long> fieldValues, TemporalAccessor partialTemporal, ResolverStyle resolverStyle) {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        //-----------------------------------------------------------------------
        @Override
        public String toString() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }
    }

    //-------------------------------------------------------------------------
    /**
     * Implementation of packed hour-min-sec.
     */
    private static enum PackedTime implements TemporalField {

        INSTANCE;

        private static final ValueRange RANGE = ValueRange.of(0, 235959);

        private static final long serialVersionUID = -98266827687L;

        //-----------------------------------------------------------------------
        @Override
        public TemporalUnit getBaseUnit() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public TemporalUnit getRangeUnit() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public boolean isDateBased() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public boolean isTimeBased() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public ValueRange range() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        //-----------------------------------------------------------------------
        @Override
        public boolean isSupportedBy(TemporalAccessor temporal) {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public ValueRange rangeRefinedBy(TemporalAccessor temporal) {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @Override
        public long getFrom(TemporalAccessor temporal) {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R extends Temporal> R adjustInto(R temporal, long newValue) {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        //-----------------------------------------------------------------------
        @Override
        public ChronoLocalDate resolve(Map<TemporalField, Long> fieldValues, TemporalAccessor partialTemporal, ResolverStyle resolverStyle) {
            throw new UnsupportedOperationException("STUB: not implemented");
        }

        //-----------------------------------------------------------------------
        @Override
        public String toString() {
            throw new UnsupportedOperationException("STUB: not implemented");
        }
    }

    //-------------------------------------------------------------------------
    private static void updateCheckConflict(Map<TemporalField, Long> fieldValues, TemporalField targetField, TemporalField changeField, long changeValue) {
        Long old = fieldValues.put(changeField, changeValue);
        if (old != null && changeValue != old.longValue()) {
            throw new DateTimeException("Conflict found: " + changeField + " " + old + " differs from " + changeField + " " + changeValue + " while resolving  " + targetField);
        }
    }
}

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
package org.threeten.extra.chrono;

import static java.time.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH;
import static java.time.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR;
import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_MONTH;
import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_YEAR;
import static java.time.temporal.ChronoField.ERA;
import static java.time.temporal.ChronoField.YEAR;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;

/**
 * An abstract date based on a year, month and day.
 *
 * <h3>Implementation Requirements</h3>
 * Implementations must be immutable and thread-safe.
 */
abstract class AbstractDate implements ChronoLocalDate {

    /**
     * Creates an instance.
     */
    AbstractDate() {
    }

    //-----------------------------------------------------------------------
    abstract int getProlepticYear();

    abstract int getMonth();

    abstract int getDayOfMonth();

    abstract int getDayOfYear();

    AbstractDate withDayOfYear(int value) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    int lengthOfWeek() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    int lengthOfYearInMonths() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    abstract ValueRange rangeAlignedWeekOfMonth();

    abstract AbstractDate resolvePrevious(int newYear, int newMonth, int dayOfMonth);

    AbstractDate resolveEpochDay(long epochDay) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    @Override
    public ValueRange range(TemporalField field) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    ValueRange rangeChrono(ChronoField field) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    @Override
    public long getLong(TemporalField field) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    int getAlignedDayOfWeekInMonth() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    int getAlignedDayOfWeekInYear() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    int getAlignedWeekOfMonth() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    int getAlignedWeekOfYear() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    int getDayOfWeek() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    long getProlepticMonth() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    int getYearOfEra() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-------------------------------------------------------------------------
    @Override
    public AbstractDate with(TemporalField field, long newValue) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public AbstractDate plus(long amountToAdd, TemporalUnit unit) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    AbstractDate plusYears(long yearsToAdd) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    AbstractDate plusMonths(long months) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    AbstractDate plusWeeks(long amountToAdd) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    AbstractDate plusDays(long days) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-------------------------------------------------------------------------
    long until(AbstractDate end, TemporalUnit unit) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    long daysUntil(ChronoLocalDate end) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    long weeksUntil(AbstractDate end) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    long monthsUntil(AbstractDate end) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    ChronoPeriod doUntil(AbstractDate end) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-------------------------------------------------------------------------
    /**
     * Compares this date to another date, including the chronology.
     * <p>
     * Compares this date with another ensuring that the date is the same.
     * <p>
     * Only objects of this concrete type are compared, other types return false.
     * To compare the dates of two {@code TemporalAccessor} instances, including dates
     * in two different chronologies, use {@link ChronoField#EPOCH_DAY} as a comparator.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other date
     */
    // override for performance
    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * A hash code for this date.
     *
     * @return a suitable hash code based only on the Chronology and the date
     */
    // override for performance
    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}

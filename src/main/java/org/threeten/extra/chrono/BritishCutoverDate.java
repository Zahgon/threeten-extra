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

import static org.threeten.extra.chrono.BritishCutoverChronology.CUTOVER;
import static org.threeten.extra.chrono.BritishCutoverChronology.CUTOVER_DAYS;
import static org.threeten.extra.chrono.BritishCutoverChronology.CUTOVER_YEAR;
import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;
import java.util.Objects;

/**
 * A date in the British Cutover calendar system.
 * <p>
 * This date operates using the {@linkplain BritishCutoverChronology British Cutover calendar}.
 *
 * <h3>Implementation Requirements</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class BritishCutoverDate extends AbstractDate implements ChronoLocalDate, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -9626278512674L;

    /**
     * The underlying date.
     */
    private final LocalDate isoDate;

    /**
     * The underlying Julian date if before the cutover.
     */
    private final transient JulianDate julianDate;

    //-----------------------------------------------------------------------
    /**
     * Obtains the current {@code BritishCutoverDate} from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date using the system clock and default time-zone, not null
     */
    public static BritishCutoverDate now() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Obtains the current {@code BritishCutoverDate} from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @param zone  the zone ID to use, not null
     * @return the current date using the system clock, not null
     */
    public static BritishCutoverDate now(ZoneId zone) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Obtains the current {@code BritishCutoverDate} from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@linkplain Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current date, not null
     * @throws DateTimeException if the current date cannot be obtained
     */
    public static BritishCutoverDate now(Clock clock) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Obtains a {@code BritishCutoverDate} representing a date in the British Cutover calendar
     * system from the proleptic-year, month-of-year and day-of-month fields.
     * <p>
     * This returns a {@code BritishCutoverDate} with the specified fields.
     * <p>
     * Dates in the middle of the cutover gap, such as the 10th September 1752,
     * will not throw an exception. Instead, the date will be treated as a Julian date
     * and converted to an ISO date, with the day of month shifted by 11 days.
     * <p>
     * Invalid dates, such as September 31st will throw an exception.
     *
     * @param prolepticYear  the British Cutover proleptic-year
     * @param month  the British Cutover month-of-year, from 1 to 12
     * @param dayOfMonth  the British Cutover day-of-month, from 1 to 31
     * @return the date in British Cutover calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-month is invalid for the month-year
     */
    public static BritishCutoverDate of(int prolepticYear, int month, int dayOfMonth) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Obtains a {@code BritishCutoverDate} from a temporal object.
     * <p>
     * This obtains a date in the British Cutover calendar system based on the specified temporal.
     * A {@code TemporalAccessor} represents an arbitrary set of date and time information,
     * which this factory converts to an instance of {@code BritishCutoverDate}.
     * <p>
     * The conversion uses the {@link ChronoField#EPOCH_DAY EPOCH_DAY}
     * field, which is standardized across calendar systems.
     * <p>
     * This method matches the signature of the functional interface {@link TemporalQuery}
     * allowing it to be used as a query via method reference, {@code BritishCutoverDate::from}.
     *
     * @param temporal  the temporal object to convert, not null
     * @return the date in British Cutover calendar system, not null
     * @throws DateTimeException if unable to convert to a {@code BritishCutoverDate}
     */
    public static BritishCutoverDate from(TemporalAccessor temporal) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code BritishCutoverDate} representing a date in the British Cutover calendar
     * system from the proleptic-year and day-of-year fields.
     * <p>
     * This returns a {@code BritishCutoverDate} with the specified fields.
     * The day must be valid for the year, otherwise an exception will be thrown.
     *
     * @param prolepticYear  the British Cutover proleptic-year
     * @param dayOfYear  the British Cutover day-of-year, from 1 to 366
     * @return the date in British Cutover calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-year is invalid for the year
     */
    static BritishCutoverDate ofYearDay(int prolepticYear, int dayOfYear) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Obtains a {@code BritishCutoverDate} representing a date in the British Cutover calendar
     * system from the epoch-day.
     *
     * @param epochDay  the epoch day to convert based on 1970-01-01 (ISO)
     * @return the date in British Cutover calendar system, not null
     * @throws DateTimeException if the epoch-day is out of range
     */
    static BritishCutoverDate ofEpochDay(final long epochDay) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Creates a {@code BritishCutoverDate} validating the input.
     *
     * @param prolepticYear  the British Cutover proleptic-year
     * @param month  the British Cutover month-of-year, from 1 to 12
     * @param dayOfMonth  the British Cutover day-of-month, from 1 to 31
     * @return the date in British Cutover calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-month is invalid for the month-year
     */
    static BritishCutoverDate create(int prolepticYear, int month, int dayOfMonth) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance from an ISO date.
     *
     * @param isoDate  the standard local date, not null
     */
    BritishCutoverDate(LocalDate isoDate) {
        Objects.requireNonNull(isoDate, "isoDate");
        this.isoDate = isoDate;
        this.julianDate = (isoDate.isBefore(CUTOVER) ? JulianDate.from(isoDate) : null);
    }

    /**
     * Creates an instance from a Julian date.
     *
     * @param julianDate  the Julian date before the cutover, not null
     */
    BritishCutoverDate(JulianDate julianDate) {
        Objects.requireNonNull(julianDate, "julianDate");
        this.isoDate = LocalDate.from(julianDate);
        this.julianDate = (isoDate.isBefore(CUTOVER) ? julianDate : null);
    }

    /**
     * Validates the object.
     *
     * @return the resolved date, not null
     */
    private Object readResolve() {
        return new BritishCutoverDate(isoDate);
    }

    //-----------------------------------------------------------------------
    private boolean isCutoverYear() {
        return isoDate.getYear() == CUTOVER_YEAR && isoDate.getDayOfYear() > CUTOVER_DAYS;
    }

    private boolean isCutoverMonth() {
        return isoDate.getYear() == CUTOVER_YEAR && isoDate.getMonthValue() == 9 && isoDate.getDayOfMonth() > CUTOVER_DAYS;
    }

    //-------------------------------------------------------------------------
    @Override
    int getAlignedDayOfWeekInMonth() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    int getAlignedWeekOfMonth() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    int getProlepticYear() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    int getMonth() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    int getDayOfMonth() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    int getDayOfYear() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public ValueRange rangeChrono(ChronoField field) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    ValueRange rangeAlignedWeekOfMonth() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    BritishCutoverDate resolvePrevious(int year, int month, int dayOfMonth) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology of this date, which is the British Cutover calendar system.
     * <p>
     * The {@code Chronology} represents the calendar system in use.
     * The era and other fields in {@link ChronoField} are defined by the chronology.
     *
     * @return the British Cutover chronology, not null
     */
    @Override
    public BritishCutoverChronology getChronology() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the era applicable at this date.
     * <p>
     * The British Cutover calendar system has two eras, 'AD' and 'BC',
     * defined by {@link JulianEra}.
     *
     * @return the era applicable at this date, not null
     */
    @Override
    public JulianEra getEra() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the length of the month represented by this date.
     * <p>
     * This returns the length of the month in days.
     * This takes into account the cutover, returning 19 in September 1752.
     *
     * @return the length of the month in days, from 19 to 31
     */
    @Override
    public int lengthOfMonth() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the length of the year represented by this date.
     * <p>
     * This returns the length of the year in days.
     * This takes into account the cutover, returning 355 in 1752.
     *
     * @return the length of the month in days, from 19 to 31
     */
    @Override
    public int lengthOfYear() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-------------------------------------------------------------------------
    @Override
    public BritishCutoverDate with(TemporalAdjuster adjuster) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public BritishCutoverDate with(TemporalField field, long newValue) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    @Override
    public BritishCutoverDate plus(TemporalAmount amount) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public BritishCutoverDate plus(long amountToAdd, TemporalUnit unit) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public BritishCutoverDate minus(TemporalAmount amount) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public BritishCutoverDate minus(long amountToSubtract, TemporalUnit unit) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-------------------------------------------------------------------------
    // for covariant return type
    @Override
    @SuppressWarnings("unchecked")
    public ChronoLocalDateTime<BritishCutoverDate> atTime(LocalTime localTime) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public ChronoPeriod until(ChronoLocalDate endDateExclusive) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    @Override
    public long toEpochDay() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> R query(TemporalQuery<R> query) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-------------------------------------------------------------------------
    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * A hash code for this date.
     *
     * @return a suitable hash code based only on the Chronology and the date
     */
    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}

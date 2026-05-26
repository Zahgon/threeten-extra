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

import static org.threeten.extra.chrono.Symmetry454Chronology.DAYS_0001_TO_1970;
import static org.threeten.extra.chrono.Symmetry454Chronology.DAYS_IN_MONTH;
import static org.threeten.extra.chrono.Symmetry454Chronology.DAYS_IN_MONTH_LONG;
import static org.threeten.extra.chrono.Symmetry454Chronology.DAYS_IN_QUARTER;
import static org.threeten.extra.chrono.Symmetry454Chronology.DAYS_IN_WEEK;
import static org.threeten.extra.chrono.Symmetry454Chronology.DAYS_IN_YEAR;
import static org.threeten.extra.chrono.Symmetry454Chronology.DAYS_IN_YEAR_LONG;
import static org.threeten.extra.chrono.Symmetry454Chronology.DAYS_PER_CYCLE;
import static org.threeten.extra.chrono.Symmetry454Chronology.DAY_OF_MONTH_RANGE;
import static org.threeten.extra.chrono.Symmetry454Chronology.DAY_OF_YEAR_RANGE;
import static org.threeten.extra.chrono.Symmetry454Chronology.EPOCH_DAY_RANGE;
import static org.threeten.extra.chrono.Symmetry454Chronology.ERA_RANGE;
import static org.threeten.extra.chrono.Symmetry454Chronology.INSTANCE;
import static org.threeten.extra.chrono.Symmetry454Chronology.MONTHS_IN_YEAR;
import static org.threeten.extra.chrono.Symmetry454Chronology.MONTH_OF_YEAR_RANGE;
import static org.threeten.extra.chrono.Symmetry454Chronology.WEEKS_IN_MONTH;
import static org.threeten.extra.chrono.Symmetry454Chronology.WEEKS_IN_YEAR;
import static org.threeten.extra.chrono.Symmetry454Chronology.YEAR_RANGE;
import java.io.Serializable;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.IsoEra;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;

/**
 * A date in the Symmetry454 calendar system.
 * <p>
 * This date operates using the {@linkplain Symmetry454Chronology Symmetry454 calendar}.
 * This calendar system is a proposed reform calendar system, and is not in common use.
 * The Symmetry454 calendar differs from the Gregorian in terms of month length, and the leap year rule.
 * Dates are aligned such that {@code 0001/01/01 (Sym454)} is {@code 0001-01-01 (ISO)}.
 * The alignment of January 1st happens 40 times within a 293 years cycle, skipping 5, 6, 11 or 12 years in between:
 *   1,   7,  18,  24,  29,  35,  46,  52,  57,  63,  74,  80,  85,  91, 103, 114, 120, 125, 131, 142,
 * 148, 153, 159, 170, 176, 181, 187, 198, 210, 216, 221, 227, 238, 244, 249, 255, 266, 272, 277, 283.
 * <p>
 * The implementation is a pure Symmetry454 calendar, as proposed by Dr. Irv Bromberg.
 * The year shares the 12 months with the Gregorian calendar.
 * The months February, May, August, November span 35 days, all other months consist of 28 days.
 * In leap years, December is extended with a full week, the so-called "leap week".
 * Since each month is made of full weeks, the calendar is perennial, with every date fixed always on the same weekday.
 * Each month starts on a Monday and ends on a Sunday; so does each year.
 * The 13th day of a month is always a Saturday.
 * <p>
 * More information is available on Wikipedia at
 * <a href='https://en.wikipedia.org/wiki/Symmetry454'>Symmetry454</a> or on the calendar's
 * <a href='https://individual.utoronto.ca/kalendis/symmetry.htm'>home page</a>.
 * <p>
 *
 * <h3>Implementation Requirements</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class Symmetry454Date extends AbstractDate implements ChronoLocalDate, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -3540913335234762448L;

    /**
     * The proleptic year.
     */
    private final int prolepticYear;

    /**
     * The month of the year.
     */
    private final int month;

    /**
     * The day of the month.
     */
    private final int day;

    /**
     * The day of year.
     */
    private final transient int dayOfYear;

    //-----------------------------------------------------------------------
    /**
     * Obtains the current {@code Symmetry454Date} from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date using the system clock and default time-zone, not null
     */
    public static Symmetry454Date now() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Obtains the current {@code Symmetry454Date} from the system clock in the specified time-zone.
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
    public static Symmetry454Date now(ZoneId zone) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Obtains the current {@code Symmetry454Date} from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@linkplain Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current date, not null
     * @throws DateTimeException if the current date cannot be obtained
     */
    public static Symmetry454Date now(Clock clock) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Obtains a {@code Symmetry454Date} representing a date in the Symmetry454 calendar
     * system from the proleptic-year, month-of-year and day-of-month fields.
     * <p>
     * This returns a {@code Symmetry454Date} with the specified fields.
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     *
     * @param prolepticYear  the Symmetry454 proleptic-year
     * @param month  the Symmetry454 month-of-year, from 1 to 12
     * @param dayOfMonth  the Symmetry454 day-of-month, from 1 to 28, or 1 to 35 in February, May, August, November
     *   and December in a Leap Year
     * @return the date in Symmetry454 calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-month is invalid for the month-year
     */
    public static Symmetry454Date of(int prolepticYear, int month, int dayOfMonth) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code Symmetry454Date} from a temporal object.
     * <p>
     * This obtains a date in the Symmetry454 calendar system based on the specified temporal.
     * A {@code TemporalAccessor} represents an arbitrary set of date and time information,
     * which this factory converts to an instance of {@code Symmetry454Date}.
     * <p>
     * The conversion typically uses the {@link ChronoField#EPOCH_DAY EPOCH_DAY}
     * field, which is standardized across calendar systems.
     * <p>
     * This method matches the signature of the functional interface {@link TemporalQuery}
     * allowing it to be used as a query via method reference, {@code Symmetry454Date::from}.
     *
     * @param temporal  the temporal object to convert, not null
     * @return the date in the Symmetry454 calendar system, not null
     * @throws DateTimeException if unable to convert to a {@code Symmetry454Date}
     */
    public static Symmetry454Date from(TemporalAccessor temporal) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code Symmetry454Date} representing a date in the Symmetry454 calendar
     * system from the proleptic-year and day-of-year fields.
     * <p>
     * This returns a {@code Symmetry454Date} with the specified fields.
     * The day must be valid for the year, otherwise an exception will be thrown.
     *
     * @param prolepticYear  the Symmetry454 proleptic-year
     * @param dayOfYear  the Symmetry454 day-of-year, from 1 to 364/371
     * @return the date in Symmetry454 calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-year is invalid for the year
     */
    static Symmetry454Date ofYearDay(int prolepticYear, int dayOfYear) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Obtains a {@code Symmetry454Date} representing a date in the Symmetry454 calendar
     * system from the epoch-day.
     *
     * @param epochDay  the epoch day to convert based on 1970-01-01 (ISO), corresponds to 1970-01-04 (Sym454)
     * @return the date in Symmetry454 calendar system, not null
     * @throws DateTimeException if the epoch-day is out of range
     */
    static Symmetry454Date ofEpochDay(long epochDay) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Consistency check for dates manipulations after calls to
     *   {@link #plus(long, TemporalUnit)},
     *   {@link #minus(long, TemporalUnit)},
     *   {@link #until(AbstractDate, TemporalUnit)} or
     *   {@link #with(TemporalField, long)}.
     *
     * @param prolepticYear  the Symmetry454 proleptic-year
     * @param month  the Symmetry454 month, from 1 to 12
     * @return the resolved date
     */
    private static Symmetry454Date resolvePreviousValid(int prolepticYear, int month, int day) {
        int monthR = Math.min(month, MONTHS_IN_YEAR);
        int dayR = Math.min(day, (monthR % 3 == 2) || (monthR == 12 && INSTANCE.isLeapYear(prolepticYear)) ? DAYS_IN_MONTH_LONG : DAYS_IN_MONTH);
        return create(prolepticYear, monthR, dayR);
    }

    //-----------------------------------------------------------------------
    /**
     * Factory method, validates the given triplet year, month and dayOfMonth.
     *
     * @param prolepticYear  the Symmetry454 proleptic-year
     * @param month  the Symmetry454 month, from 1 to 12
     * @param dayOfMonth  the Symmetry454 day-of-month, from 1 to 28, or 1 to 35 in February, May, August, November
     *   and December in a Leap Year
     * @return the Symmetry454 date
     * @throws DateTimeException if the date is invalid
     */
    static Symmetry454Date create(int prolepticYear, int month, int dayOfMonth) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance from validated data.
     *
     * @param prolepticYear  the Symmetry454 proleptic-year
     * @param month  the Symmetry454 month, from 1 to 12
     * @param dayOfMonth  the Symmetry454 day-of-month, from 1 to 28, or 1 to 35 in February, May, August, November
     *   and December in a Leap Year
     */
    private Symmetry454Date(int prolepticYear, int month, int dayOfMonth) {
        this.prolepticYear = prolepticYear;
        this.month = month;
        this.day = dayOfMonth;
        this.dayOfYear = DAYS_IN_MONTH * (month - 1) + DAYS_IN_WEEK * (month / 3) + dayOfMonth;
    }

    /**
     * Validates the object.
     *
     * @return Symmetry454Date the resolved date, not null
     */
    private Object readResolve() {
        return Symmetry454Date.of(prolepticYear, month, day);
    }

    //-----------------------------------------------------------------------
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
    int lengthOfYearInMonths() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    int getAlignedDayOfWeekInMonth() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    int getAlignedDayOfWeekInYear() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    int getAlignedWeekOfMonth() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    int getAlignedWeekOfYear() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    int getDayOfWeek() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    long getProlepticWeek() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Checks if the date is within the leap week.
     *
     * @return true if this date is in the leap week
     */
    public boolean isLeapWeek() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    @Override
    public ValueRange range(TemporalField field) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    ValueRange rangeAlignedWeekOfMonth() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    Symmetry454Date resolvePrevious(int newYear, int newMonth, int dayOfMonth) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology of this date, which is the Symmetry454 calendar system.
     * <p>
     * The {@code Chronology} represents the calendar system in use.
     * The era and other fields in {@link ChronoField} are defined by the chronology.
     *
     * @return the Symmetry454 chronology, not null
     */
    @Override
    public Symmetry454Chronology getChronology() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the era applicable at this date.
     * <p>
     * The Symmetry454 calendar system uses {@link IsoEra}.
     *
     * @return the era applicable at this date, not null
     */
    @Override
    public IsoEra getEra() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the length of the month represented by this date.
     * <p>
     * This returns the length of the month in days.
     * Month lengths do not match those of the ISO calendar system.
     * <p>
     * Most months have 28 days, except for February, May, August, November, and
     * December in a leap year, each has 35 days.
     *
     * @return the length of the month in days
     */
    @Override
    public int lengthOfMonth() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private boolean isLongMonth() {
        return this.month % 3 == 2 || (isLeapYear() && this.month == MONTHS_IN_YEAR);
    }

    /**
     * Returns the length of the year represented by this date.
     * <p>
     * This returns the length of the year in days.
     * Year lengths do NOT match those of the ISO calendar system.
     *
     * @return the length of the year in days: 364 or 371
     */
    @Override
    public int lengthOfYear() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-------------------------------------------------------------------------
    @Override
    public Symmetry454Date with(TemporalAdjuster adjuster) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public Symmetry454Date with(TemporalField field, long newValue) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    Symmetry454Date withDayOfYear(int value) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    @Override
    public Symmetry454Date plus(TemporalAmount amount) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public Symmetry454Date plus(long amountToAdd, TemporalUnit unit) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public Symmetry454Date minus(TemporalAmount amount) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public Symmetry454Date minus(long amountToSubtract, TemporalUnit unit) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-------------------------------------------------------------------------
    // for covariant return type
    @Override
    @SuppressWarnings("unchecked")
    public ChronoLocalDateTime<Symmetry454Date> atTime(LocalTime localTime) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Get the number of years from this date to the given day.
     *
     * @param end The end date.
     * @return The number of years from this date to the given day.
     */
    long yearsUntil(Symmetry454Date end) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public ChronoPeriod until(ChronoLocalDate endDateExclusive) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    long weeksUntil(AbstractDate end) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    long monthsUntil(AbstractDate end) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    @Override
    public long toEpochDay() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Display the date in human-readable format.
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}

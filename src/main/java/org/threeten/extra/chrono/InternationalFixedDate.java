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

import static org.threeten.extra.chrono.InternationalFixedChronology.DAYS_0000_TO_1970;
import static org.threeten.extra.chrono.InternationalFixedChronology.DAYS_IN_LONG_MONTH;
import static org.threeten.extra.chrono.InternationalFixedChronology.DAYS_IN_MONTH;
import static org.threeten.extra.chrono.InternationalFixedChronology.DAYS_IN_WEEK;
import static org.threeten.extra.chrono.InternationalFixedChronology.DAYS_IN_YEAR;
import static org.threeten.extra.chrono.InternationalFixedChronology.DAYS_PER_CYCLE;
import static org.threeten.extra.chrono.InternationalFixedChronology.DAY_OF_MONTH_RANGE;
import static org.threeten.extra.chrono.InternationalFixedChronology.DAY_OF_YEAR_LEAP_RANGE;
import static org.threeten.extra.chrono.InternationalFixedChronology.DAY_OF_YEAR_NORMAL_RANGE;
import static org.threeten.extra.chrono.InternationalFixedChronology.EMPTY_RANGE;
import static org.threeten.extra.chrono.InternationalFixedChronology.EPOCH_DAY_RANGE;
import static org.threeten.extra.chrono.InternationalFixedChronology.ERA_RANGE;
import static org.threeten.extra.chrono.InternationalFixedChronology.INSTANCE;
import static org.threeten.extra.chrono.InternationalFixedChronology.MONTHS_IN_YEAR;
import static org.threeten.extra.chrono.InternationalFixedChronology.MONTH_OF_YEAR_RANGE;
import static org.threeten.extra.chrono.InternationalFixedChronology.WEEKS_IN_MONTH;
import static org.threeten.extra.chrono.InternationalFixedChronology.WEEKS_IN_YEAR;
import static org.threeten.extra.chrono.InternationalFixedChronology.YEAR_RANGE;
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
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;

/**
 * A date in the International fixed calendar system.
 * <p>
 * Implements a pure International Fixed calendar (also known as the Cotsworth plan, the Eastman plan,
 * the 13 Month calendar or the Equal Month calendar) a solar calendar proposal for calendar reform designed by
 * Moses B. Cotsworth, who presented it in 1902.
 * <p>
 * It provides for a year of 13 months of 28 days each.
 * Month 6 has 29 days in a leap year, but the additional day is not part of any week.
 * Month 12 always has 29 days, but the additional day is not part of any week.
 * It is therefore a perennial calendar, with every date fixed always on the same weekday.
 * Though it was never officially adopted in any country, it was the official calendar of the Eastman Kodak Company
 * from 1928 to 1989.
 * <p>
 * This date operates using the {@linkplain InternationalFixedChronology International fixed calendar}.
 * This calendar system is a proposed reform calendar system, and is not in common use.
 * The International fixed differs from the Gregorian in terms of month count and length, and the leap year rule.
 * Dates are aligned such that {@code 0001/01/01 (International fixed)} is {@code 0001-01-01 (ISO)}.
 * <p>
 * More information is available in the
 * <a href='https://en.wikipedia.org/wiki/International_Fixed_Calendar'>International Fixed Calendar</a>
 * Wikipedia article.
 *
 * <h3>Implementation Requirements</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class InternationalFixedDate extends AbstractDate implements ChronoLocalDate, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -5501342824322148215L;

    /**
     * Leap Day as day-of-year
     */
    private static final int LEAP_DAY_AS_DAY_OF_YEAR = 6 * DAYS_IN_MONTH + 1;

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

    /**
     * Is the proleptic year a Leap year ?
     */
    private final transient boolean isLeapYear;

    /**
     * Is the day-of-year a Leap Day ?
     */
    private final transient boolean isLeapDay;

    /**
     * Is the day-of-year a Year Day ?
     */
    private final transient boolean isYearDay;

    //-----------------------------------------------------------------------
    /**
     * Obtains the current {@code InternationalFixedDate} from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date using the system clock and default time-zone, not null
     */
    public static InternationalFixedDate now() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Obtains the current {@code InternationalFixedDate} from the system clock in the specified time-zone.
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
    public static InternationalFixedDate now(ZoneId zone) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Obtains the current {@code InternationalFixedDate} from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@linkplain Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current date, not null
     * @throws DateTimeException if the current date cannot be obtained
     */
    public static InternationalFixedDate now(Clock clock) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Obtains a {@code InternationalFixedDate} representing a date in the International fixed calendar
     * system from the proleptic-year, month-of-year and day-of-month fields.
     * <p>
     * This returns a {@code InternationalFixedDate} with the specified fields.
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     *
     * @param prolepticYear  the International fixed proleptic-year
     * @param month  the International fixed month-of-year, from 1 to 13
     * @param dayOfMonth  the International fixed day-of-month, from 1 to 28 (29 for Leap Day or Year Day)
     * @return the date in International fixed calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-month is invalid for the month-year
     */
    public static InternationalFixedDate of(int prolepticYear, int month, int dayOfMonth) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code InternationalFixedDate} from a temporal object.
     * <p>
     * This obtains a date in the International fixed calendar system based on the specified temporal.
     * A {@code TemporalAccessor} represents an arbitrary set of date and time information,
     * which this factory converts to an instance of {@code InternationalFixedDate}.
     * <p>
     * The conversion typically uses the {@link ChronoField#EPOCH_DAY EPOCH_DAY}
     * field, which is standardized across calendar systems.
     * <p>
     * This method matches the signature of the functional interface {@link TemporalQuery}
     * allowing it to be used as a query via method reference, {@code InternationalFixedDate::from}.
     *
     * @param temporal  the temporal object to convert, not null
     * @return the date in the International fixed calendar system, not null
     * @throws DateTimeException if unable to convert to a {@code InternationalFixedDate}
     */
    public static InternationalFixedDate from(TemporalAccessor temporal) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code InternationalFixedDate} representing a date in the International fixed calendar
     * system from the proleptic-year and day-of-year fields.
     * <p>
     * This returns a {@code InternationalFixedDate} with the specified fields.
     * The day must be valid for the year, otherwise an exception will be thrown.
     *
     * @param prolepticYear  the International fixed proleptic-year
     * @param dayOfYear  the International fixed day-of-year, from 1 to 366
     * @return the date in International fixed calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-year is invalid for the year
     */
    static InternationalFixedDate ofYearDay(int prolepticYear, int dayOfYear) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Obtains a {@code InternationalFixedDate} representing a date in the International fixed calendar
     * system from the epoch-day.
     *
     * @param epochDay  the epoch day to convert based on 1970-01-01 (ISO)
     * @return the date in International fixed calendar system, not null
     * @throws DateTimeException if the epoch-day is out of range
     */
    static InternationalFixedDate ofEpochDay(long epochDay) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Consistency check for dates manipulations after calls to
     *   {@link #plus(long, TemporalUnit)},
     *   {@link #minus(long, TemporalUnit)},
     *   {@link #until(AbstractDate, TemporalUnit)} or
     *   {@link #with(TemporalField, long)}.
     *
     * @param prolepticYear  the International fixed proleptic-year
     * @param month  the International fixed month, from 1 to 13
     * @param day  the International fixed day-of-month, from 1 to 28 (29 for Leap Day or Year Day)
     * @return the resolved date
     */
    private static InternationalFixedDate resolvePreviousValid(int prolepticYear, int month, int day) {
        int monthR = Math.min(month, MONTHS_IN_YEAR);
        int dayR = Math.min(day, (monthR == 13 || (monthR == 6 && INSTANCE.isLeapYear(prolepticYear)) ? DAYS_IN_LONG_MONTH : DAYS_IN_MONTH));
        return create(prolepticYear, monthR, dayR);
    }

    //-----------------------------------------------------------------------
    /**
     * Factory method, validates the given triplet year, month and dayOfMonth.
     *
     * @param prolepticYear  the International fixed proleptic-year
     * @param month  the International fixed month, from 1 to 13
     * @param dayOfMonth  the International fixed day-of-month, from 1 to 28 (29 for Leap Day or Year Day)
     * @return the International fixed date
     * @throws DateTimeException if the date is invalid
     */
    static InternationalFixedDate create(int prolepticYear, int month, int dayOfMonth) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance from validated data.
     *
     * @param prolepticYear  the International fixed proleptic-year
     * @param month  the International fixed month, from 1 to 13
     * @param dayOfMonth  the International fixed day-of-month, from 1 to 28 (29 for Leap Day or Year Day)
     */
    private InternationalFixedDate(int prolepticYear, int month, int dayOfMonth) {
        this.prolepticYear = prolepticYear;
        this.month = month;
        this.day = dayOfMonth;
        this.isLeapYear = INSTANCE.isLeapYear(prolepticYear);
        this.isLeapDay = this.month == 6 && this.day == 29;
        this.isYearDay = this.month == 13 && this.day == 29;
        this.dayOfYear = ((month - 1) * DAYS_IN_MONTH + day) + (month > 6 && isLeapYear ? 1 : 0);
    }

    /**
     * Validates the object.
     *
     * @return InternationalFixedDate the resolved date, not null
     */
    private Object readResolve() {
        return InternationalFixedDate.of(prolepticYear, month, day);
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

    /**
     * Returns the day of the week represented by this date.
     * <p>
     * Leap Day and Year Day are not considered week-days, thus return 0.
     *
     * @return the day of the week: between 1 and 7, or 0 (Leap Day, Year Day)
     */
    @Override
    int getDayOfWeek() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    long getProlepticWeek() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private boolean isSpecialDay() {
        return day == DAYS_IN_LONG_MONTH;
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
    InternationalFixedDate resolvePrevious(int newYear, int newMonth, int dayOfMonth) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology of this date, which is the International fixed calendar system.
     * <p>
     * The {@code Chronology} represents the calendar system in use.
     * The era and other fields in {@link ChronoField} are defined by the chronology.
     *
     * @return the International fixed chronology, not null
     */
    @Override
    public InternationalFixedChronology getChronology() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the era applicable at this date.
     * <p>
     * The International fixed calendar system only has one era, 'CE',
     * defined by {@link InternationalFixedEra}.
     *
     * @return the era applicable at this date, not null
     */
    @Override
    public InternationalFixedEra getEra() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Returns the length of the month represented by this date.
     * <p>
     * This returns the length of the month in days.
     * Month lengths do not match those of the ISO calendar system.
     * <p>
     * Months have 28 days, except June which has 29 in leap years
     * and December (month 13) which always has 29 days.
     *
     * @return the length of the month in days
     */
    @Override
    public int lengthOfMonth() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private boolean isLongMonth() {
        return month == 13 || (month == 6 && isLeapYear);
    }

    /**
     * Returns the length of the year represented by this date.
     * <p>
     * This returns the length of the year in days.
     * Year lengths match those of the ISO calendar system.
     *
     * @return the length of the year in days: 365 or 366
     */
    @Override
    public int lengthOfYear() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-------------------------------------------------------------------------
    @Override
    public InternationalFixedDate with(TemporalAdjuster adjuster) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public InternationalFixedDate with(TemporalField field, long newValue) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    InternationalFixedDate withDayOfYear(int value) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    @Override
    public InternationalFixedDate plus(TemporalAmount amount) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public InternationalFixedDate plus(long amountToAdd, TemporalUnit unit) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    InternationalFixedDate plusWeeks(long weeks) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    InternationalFixedDate plusMonths(long months) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    InternationalFixedDate plusYears(long yearsToAdd) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public InternationalFixedDate minus(TemporalAmount amount) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public InternationalFixedDate minus(long amountToSubtract, TemporalUnit unit) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-------------------------------------------------------------------------
    // for covariant return type
    @Override
    @SuppressWarnings("unchecked")
    public ChronoLocalDateTime<InternationalFixedDate> atTime(LocalTime localTime) {
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
    long yearsUntil(InternationalFixedDate end) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * For calculation purposes in a leap year, decrement the day of the year for months 7 and higher - including Year Day.
     * Leave out Leap Day though!
     *
     * @return int day of the year for calculations
     */
    private int getInternalDayOfYear() {
        return isLeapYear && (month > 6) ? dayOfYear - 1 : dayOfYear;
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

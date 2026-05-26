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

import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoField.EPOCH_DAY;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;
import static org.threeten.extra.chrono.AccountingChronology.DAY_OF_YEAR_RANGE;
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
import java.time.temporal.ValueRange;
import java.util.Objects;

/**
 * A date in an Accounting calendar system.
 * <p>
 * This date operates using a given {@linkplain AccountingChronology Accounting calendar}.
 * An Accounting calendar differs greatly from the ISO calendar.
 * The start of the Accounting calendar will vary against the ISO calendar.
 * Depending on options chosen, it can start as early as {@code 0000-01-26 (ISO)} or as late as {@code 0001-01-04 (ISO)}.
 *
 * <h3>Implementation Requirements</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class AccountingDate extends AbstractDate implements ChronoLocalDate, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -126140328940081914L;

    /**
     * Number of days in a week.
     */
    private static final int DAYS_IN_WEEK = 7;

    /**
     * Number of weeks in a regular (non-leap) year.
     */
    private static final int WEEKS_IN_YEAR = 52;

    /**
     * Number of days in a long (400-year) cycle.
     */
    private static final int DAYS_PER_LONG_CYCLE = 400 * 365 + 3 * 24 + 1 * 25;

    /**
     * The chronology for manipulating this date.
     */
    private final AccountingChronology chronology;

    /**
     * The proleptic year.
     */
    private final int prolepticYear;

    /**
     * The month (period).
     */
    private final short month;

    /**
     * The day.
     */
    private final short day;

    //-----------------------------------------------------------------------
    /**
     * Obtains the current {@code AccountingDate} from the system clock in the default time-zone,
     * translated with the given AccountingChronology.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @param chronology  the Accounting chronology to base the date on, not null
     * @return the current date using the system clock and default time-zone, not null
     * @throws DateTimeException if the current date cannot be obtained,
     *  NullPointerException if an AccountingChronology was not provided
     */
    public static AccountingDate now(AccountingChronology chronology) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Obtains the current {@code AccountingDate} from the system clock in the specified time-zone,
     * translated with the given AccountingChronology.
     * <p>
     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @param chronology  the Accounting chronology to base the date on, not null
     * @param zone  the zone ID to use, not null
     * @return the current date using the system clock, not null
     * @throws DateTimeException if the current date cannot be obtained,
     *  NullPointerException if an AccountingChronology was not provided
     */
    public static AccountingDate now(AccountingChronology chronology, ZoneId zone) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Obtains the current {@code AccountingDate} from the specified clock,
     * translated with the given AccountingChronology.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@linkplain Clock dependency injection}.
     *
     * @param chronology  the Accounting chronology to base the date on, not null
     * @param clock  the clock to use, not null
     * @return the current date, not null
     * @throws DateTimeException if the current date cannot be obtained,
     *  NullPointerException if an AccountingChronology was not provided
     */
    public static AccountingDate now(AccountingChronology chronology, Clock clock) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Obtains a {@code AccountingDate} representing a date in the given accounting calendar
     * system from the proleptic-year, month-of-year and day-of-month fields.
     * <p>
     * This returns a {@code AccountingDate} with the specified fields.
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     * @param chronology  the Accounting chronology to base the date on, not null
     * @param prolepticYear  the Accounting proleptic-year
     * @param month  the Accounting month-of-year, from 1 to 12 or 1 to 13
     * @param dayOfMonth  the Accounting day-of-month, from 1 to 35 or 1 to 42
     * @return the date in the given Accounting calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  if the day-of-month is invalid for the month-year,
     *  or if an AccountingChronology was not provided
     */
    public static AccountingDate of(AccountingChronology chronology, int prolepticYear, int month, int dayOfMonth) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Obtains an {@code AccountingDate} from a temporal object.
     * <p>
     * This obtains a date in the specified Accounting calendar system based on the specified temporal.
     * A {@code TemporalAccessor} represents an arbitrary set of date and time information,
     * which this factory converts to an instance of {@code AccountingDate}.
     * <p>
     * The conversion typically uses the {@link ChronoField#EPOCH_DAY EPOCH_DAY}
     * field, which is standardized across calendar systems.
     * <p>
     * This method almost matches the signature of the functional interface {@link TemporalQuery}
     * and must be used as a query via something that supplies the missing parameter,
     * such as a curried method reference, {@code temporal -> AccountingDate.from(chronology, temporal)}
     * (where {@code chronology} resolves to a set up {@code AccountingChronology}).
     *
     * @param chronology  the Accounting chronology to base the date on, not null
     * @param temporal  the temporal object to convert, not null
     * @return the date in Accounting calendar system, not null
     * @throws DateTimeException if unable to convert to an {@code AccountingDate},
     *  NullPointerException if an AccountingChronology was not provided
     */
    public static AccountingDate from(AccountingChronology chronology, TemporalAccessor temporal) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an {@code AccountingDate} representing a date in the given Accounting calendar
     * system from the proleptic-year and day-of-year fields.
     * <p>
     * This returns an {@code AccountingDate} with the specified fields.
     * The day must be valid for the year, otherwise an exception will be thrown.
     *
     * @param chronology  the Accounting chronology to base the date on, not null
     * @param prolepticYear  the Accounting proleptic-year
     * @param dayOfYear  the Accounting day-of-year, from 1 to 371
     * @return the date in Accounting calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-year is invalid for the year,
     *  NullPointerException if an AccountingChronology was not provided
     */
    static AccountingDate ofYearDay(AccountingChronology chronology, int prolepticYear, int dayOfYear) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Obtains an {@code AccountingDate} representing a date in the given Accounting calendar
     * system from the epoch-day.
     *
     * @param chronology  the Accounting chronology to base the date on, not null
     * @param epochDay  the epoch day to convert based on 1970-01-01 (ISO)
     * @return the date in given Accounting calendar system, not null
     * @throws DateTimeException if the epoch-day is out of range,
     *  NullPointerException if an AccountingChronology was not provided
     */
    static AccountingDate ofEpochDay(AccountingChronology chronology, long epochDay) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private static AccountingDate resolvePreviousValid(AccountingChronology chronology, int prolepticYear, int month, int day) {
        day = Math.min(day, lengthOfMonth(chronology, prolepticYear, month));
        return new AccountingDate(chronology, prolepticYear, month, day);
    }

    private static int lengthOfMonth(AccountingChronology chronology, int prolepticYear, int month) {
        return (chronology.isLeapYear(prolepticYear) ? chronology.getDivision().getWeeksInMonth(month, chronology.getLeapWeekInMonth()) : chronology.getDivision().getWeeksInMonth(month)) * DAYS_IN_WEEK;
    }

    /**
     * Creates an {@code AccountingDate} validating the input.
     *
     * @param chronology  the Accounting chronology to base the date on, not null
     * @param prolepticYear  the Accounting proleptic-year
     * @return the date in Accounting calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-year is invalid for the month-year,
     *  NullPointerException if an AccountingChronology was not provided
     */
    static AccountingDate create(AccountingChronology chronology, int prolepticYear, int month, int dayOfMonth) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance from validated data.
     *
     * @param chronology  the Accounting chronology to base the date on, not null
     * @param prolepticYear  the Accounting proleptic-year
     * @param month  the Accounting month (period), from 1 to 12 or 1 to 13
     * @param dayOfMonth  the Accounting day-of-month, from 1 to 35 or 1 to 42
     */
    private AccountingDate(AccountingChronology chronology, int prolepticYear, int month, int dayOfMonth) {
        this.chronology = chronology;
        this.prolepticYear = prolepticYear;
        this.month = (short) month;
        this.day = (short) dayOfMonth;
    }

    /**
     * Validates the object.
     *
     * @return the resolved date, not null
     */
    private Object readResolve() {
        return AccountingDate.create(chronology, prolepticYear, month, day);
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
    AbstractDate withDayOfYear(int value) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    int lengthOfYearInMonths() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    ValueRange rangeAlignedWeekOfMonth() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    AccountingDate resolvePrevious(int newYear, int newMonth, int dayOfMonth) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology of this date, which is an Accounting calendar system.
     * <p>
     * The {@code Chronology} represents the calendar system in use.
     * The era and other fields in {@link ChronoField} are defined by the chronology.
     *
     * @return the Accounting chronology, not null
     */
    @Override
    public AccountingChronology getChronology() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public int lengthOfMonth() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public int lengthOfYear() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-------------------------------------------------------------------------
    @Override
    public AccountingDate with(TemporalAdjuster adjuster) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public AccountingDate with(TemporalField field, long newValue) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    @Override
    public AccountingDate plus(TemporalAmount amount) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public AccountingDate plus(long amountToAdd, TemporalUnit unit) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public AccountingDate minus(TemporalAmount amount) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public AccountingDate minus(long amountToSubtract, TemporalUnit unit) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-------------------------------------------------------------------------
    // for covariant return type
    @Override
    @SuppressWarnings("unchecked")
    public ChronoLocalDateTime<AccountingDate> atTime(LocalTime localTime) {
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
}

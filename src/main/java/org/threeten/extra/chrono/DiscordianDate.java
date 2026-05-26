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

import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_YEAR;
import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoField.EPOCH_DAY;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;
import static org.threeten.extra.chrono.DiscordianChronology.DAYS_IN_MONTH;
import static org.threeten.extra.chrono.DiscordianChronology.DAYS_IN_WEEK;
import static org.threeten.extra.chrono.DiscordianChronology.MONTHS_IN_YEAR;
import static org.threeten.extra.chrono.DiscordianChronology.OFFSET_FROM_ISO_0000;
import static org.threeten.extra.chrono.DiscordianChronology.WEEKS_IN_YEAR;
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
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;

/**
 * A date in the Discordian calendar system.
 * <p>
 * This date operates using the {@linkplain DiscordianChronology Discordian calendar}.
 * This calendar system is used by some adherents to Discordianism.
 * The Discordian differs from the Gregorian in terms of the length of the week and month, and uses an offset year.
 * Dates are aligned such that {@code 0001-01-01 (Discordian)} is {@code -1165-01-01 (ISO)}.
 *
 * <h3>Implementation Requirements</h3>
 * This class is immutable and thread-safe.
 * <p>
 * This class must be treated as a value type. Do not synchronize, rely on the
 * identity hash code or use the distinction between equals() and ==.
 */
public final class DiscordianDate extends AbstractDate implements ChronoLocalDate, Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = -4340508226506164852L;

    /**
     * The difference between the Discordian and ISO epoch day count (Discordian 1167-01-01 to ISO 1970-01-01).
     */
    private static final int DISCORDIAN_1167_TO_ISO_1970 = 719162;

    /**
     * The days per short 4 year cycle.
     */
    private static final int DAYS_PER_SHORT_CYCLE = (365 * 4) + 1;

    /**
     * The days per 100 year cycle.
     */
    private static final int DAYS_PER_CYCLE = (DAYS_PER_SHORT_CYCLE * 25) - 1;

    /**
     * The days per 400 year long cycle.
     */
    private static final int DAYS_PER_LONG_CYCLE = (DAYS_PER_CYCLE * 4) + 1;

    /**
     * Offset in days from start of year to St. Tib's Day.
     */
    private static final int ST_TIBS_OFFSET = 60;

    /**
     * The proleptic year.
     */
    private final int prolepticYear;

    /**
     * The month.
     */
    private final short month;

    /**
     * The day.
     */
    private final short day;

    //-----------------------------------------------------------------------
    /**
     * Obtains the current {@code DiscordianDate} from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date using the system clock and default time-zone, not null
     */
    public static DiscordianDate now() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Obtains the current {@code DiscordianDate} from the system clock in the specified time-zone.
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
    public static DiscordianDate now(ZoneId zone) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Obtains the current {@code DiscordianDate} from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date - today.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@linkplain Clock dependency injection}.
     *
     * @param clock  the clock to use, not null
     * @return the current date, not null
     * @throws DateTimeException if the current date cannot be obtained
     */
    public static DiscordianDate now(Clock clock) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Obtains a {@code DiscordianDate} representing a date in the Discordian calendar
     * system from the proleptic-year, month-of-year and day-of-month fields.
     * <p>
     * This returns a {@code DiscordianDate} with the specified fields.
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     * <p>
     * St. Tib's Day is indicated by specifying 0 for both month and day-of-month.
     *
     * @param prolepticYear  the Discordian proleptic-year
     * @param month  the Discordian month-of-year, from 1 to 5
     * @param dayOfMonth  the Discordian day-of-month, from 1 to 73
     * @return the date in Discordian calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-month is invalid for the month-year
     */
    public static DiscordianDate of(int prolepticYear, int month, int dayOfMonth) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Obtains a {@code DiscordianDate} from a temporal object.
     * <p>
     * This obtains a date in the Discordian calendar system based on the specified temporal.
     * A {@code TemporalAccessor} represents an arbitrary set of date and time information,
     * which this factory converts to an instance of {@code DiscordianDate}.
     * <p>
     * The conversion typically uses the {@link ChronoField#EPOCH_DAY EPOCH_DAY}
     * field, which is standardized across calendar systems.
     * <p>
     * This method matches the signature of the functional interface {@link TemporalQuery}
     * allowing it to be used as a query via method reference, {@code DiscordianDate::from}.
     *
     * @param temporal  the temporal object to convert, not null
     * @return the date in Discordian calendar system, not null
     * @throws DateTimeException if unable to convert to a {@code DiscordianDate}
     */
    public static DiscordianDate from(TemporalAccessor temporal) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains a {@code DiscordianDate} representing a date in the Discordian calendar
     * system from the proleptic-year and day-of-year fields.
     * <p>
     * This returns a {@code DiscordianDate} with the specified fields.
     * The day must be valid for the year, otherwise an exception will be thrown.
     *
     * @param prolepticYear  the Discordian proleptic-year
     * @param dayOfYear  the Discordian day-of-year, from 1 to 366
     * @return the date in Discordian calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-year is invalid for the year
     */
    static DiscordianDate ofYearDay(int prolepticYear, int dayOfYear) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Obtains a {@code DiscordianDate} representing a date in the Discordian calendar
     * system from the epoch-day.
     *
     * @param epochDay  the epoch day to convert based on 1970-01-01 (ISO)
     * @return the date in Discordian calendar system, not null
     * @throws DateTimeException if the epoch-day is out of range
     */
    static DiscordianDate ofEpochDay(final long epochDay) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    private static DiscordianDate resolvePreviousValid(int prolepticYear, int month, int day) {
        switch(month) {
            case 0:
                day = 0;
                if (DiscordianChronology.INSTANCE.isLeapYear(prolepticYear)) {
                    break;
                }
                month = 1;
            // fall through
            default:
                if (day == 0) {
                    day = ST_TIBS_OFFSET;
                }
        }
        return new DiscordianDate(prolepticYear, month, day);
    }

    private static long getLeapYearsBefore(long year) {
        long offsetYear = year - OFFSET_FROM_ISO_0000 - 1;
        return Math.floorDiv(offsetYear, 4) - Math.floorDiv(offsetYear, 100) + Math.floorDiv(offsetYear, 400);
    }

    /**
     * Creates a {@code DiscordianDate} validating the input.
     *
     * @param prolepticYear  the Discordian proleptic-year
     * @param month  the Discordian month-of-year, from 1 to 5
     * @param dayOfMonth  the Discordian day-of-month, from 1 to 73
     * @return the date in Discordian calendar system, not null
     * @throws DateTimeException if the value of any field is out of range,
     *  or if the day-of-year is invalid for the month-year
     */
    static DiscordianDate create(int prolepticYear, int month, int dayOfMonth) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance from validated data.
     *
     * @param prolepticYear  the Discordian proleptic-year
     * @param month  the Discordian month, from 1 to 5
     * @param dayOfMonth  the Discordian day-of-month, from 1 to 73
     */
    private DiscordianDate(int prolepticYear, int month, int dayOfMonth) {
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
        return DiscordianDate.create(prolepticYear, month, day);
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
    int lengthOfWeek() {
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
    DiscordianDate resolvePrevious(int newYear, int newMonth, int dayOfMonth) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    @Override
    public ValueRange range(TemporalField field) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    @Override
    public long getLong(TemporalField field) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    int getDayOfWeek() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    long getProlepticMonth() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    long getProlepticWeek() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the chronology of this date, which is the Discordian calendar system.
     * <p>
     * The {@code Chronology} represents the calendar system in use.
     * The era and other fields in {@link ChronoField} are defined by the chronology.
     *
     * @return the Discordian chronology, not null
     */
    @Override
    public DiscordianChronology getChronology() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * Gets the era applicable at this date.
     * <p>
     * The Discordian calendar system has one era, 'YOLD',
     * defined by {@link DiscordianEra}.
     *
     * @return the era YOLD, not null
     */
    @Override
    public DiscordianEra getEra() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public int lengthOfMonth() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-------------------------------------------------------------------------
    @Override
    public DiscordianDate with(TemporalAdjuster adjuster) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public DiscordianDate with(TemporalField field, long newValue) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-----------------------------------------------------------------------
    @Override
    public DiscordianDate plus(TemporalAmount amount) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public DiscordianDate plus(long amountToAdd, TemporalUnit unit) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    DiscordianDate plusMonths(long months) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    DiscordianDate plusWeeks(long weeks) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public DiscordianDate minus(TemporalAmount amount) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public DiscordianDate minus(long amountToSubtract, TemporalUnit unit) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //-------------------------------------------------------------------------
    // for covariant return type
    @Override
    @SuppressWarnings("unchecked")
    public ChronoLocalDateTime<DiscordianDate> atTime(LocalTime localTime) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    long until(AbstractDate end, TemporalUnit unit) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    long weeksUntil(DiscordianDate end) {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    @Override
    long monthsUntil(AbstractDate end) {
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
    @Override
    public String toString() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}

package org.ojalgo.type;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.ojalgo.structure.Structure1D.IndexMapper;
import org.ojalgo.type.CalendarDate.Resolution;

public abstract class TimeIndex<T extends Comparable<? super T>> {

    public static final TimeIndex<Calendar> CALENDAR = new TimeIndex<>() {

        @Override
        public IndexMapper<Calendar> from( Calendar reference) {
            return new IndexMapper<>() {

                @Override public long toIndex( Calendar key) {
                    return key.getTimeInMillis() - reference.getTimeInMillis();
                }

                @Override public Calendar toKey( long index) {
                     long tmpTimeInMillis = index + reference.getTimeInMillis();
                     var retVal = new GregorianCalendar();
                    retVal.setTimeInMillis(tmpTimeInMillis);
                    return retVal;
                }

            };
        }

        @Override
        public IndexMapper<Calendar> from( Calendar reference,  Resolution resolution) {
            return new IndexMapper<>() {

                @Override public long toIndex( Calendar key) {
                    return (key.getTimeInMillis() - reference.getTimeInMillis()) / resolution.toDurationInMillis();
                }

                @Override public Calendar toKey( long index) {
                     long tmpTimeInMillis = (index * resolution.toDurationInMillis()) + reference.getTimeInMillis();
                     var retVal = new GregorianCalendar();
                    retVal.setTimeInMillis(tmpTimeInMillis);
                    return retVal;
                }

            };
        }

        @Override
        public IndexMapper<Calendar> plain() {
            return new IndexMapper<>() {

                @Override public long toIndex( Calendar key) {
                    return key.getTimeInMillis();
                }

                @Override public Calendar toKey( long index) {
                     var retVal = new GregorianCalendar();
                    retVal.setTimeInMillis(index);
                    return retVal;
                }

            };
        }

        @Override
        public IndexMapper<Calendar> plain( Resolution resolution) {
            return new IndexMapper<>() {

                @Override public long toIndex( Calendar key) {
                    return key.getTimeInMillis() / resolution.toDurationInMillis();
                }

                @Override public Calendar toKey( long index) {
                     long tmpTimeInMillis = index * resolution.toDurationInMillis();
                     var retVal = new GregorianCalendar();
                    retVal.setTimeInMillis(tmpTimeInMillis);
                    return retVal;
                }

            };
        }

    };

    public static final TimeIndex<CalendarDate> CALENDAR_DATE = new TimeIndex<>() {

        @Override
        public IndexMapper<CalendarDate> from( CalendarDate reference) {
            return new IndexMapper<>() {

                @Override public long toIndex( CalendarDate key) {
                    return key.millis - reference.millis;
                }

                @Override public CalendarDate toKey( long index) {
                    return new CalendarDate(index + reference.millis);
                }

            };
        }

        @Override
        public IndexMapper<CalendarDate> from( CalendarDate reference,  Resolution resolution) {
            return new IndexMapper<>() {

                @Override public long toIndex( CalendarDate key) {
                    return (key.millis - reference.millis) / resolution.toDurationInMillis();
                }

                @Override public CalendarDate toKey( long index) {
                    return new CalendarDate((index * resolution.toDurationInMillis()) + reference.millis);
                }

            };
        }

        @Override
        public IndexMapper<CalendarDate> plain() {
            return new IndexMapper<>() {

                @Override public long toIndex( CalendarDate key) {
                    return key.millis;
                }

                @Override public CalendarDate toKey( long index) {
                    return new CalendarDate(index);
                }

            };
        }

        @Override
        public IndexMapper<CalendarDate> plain( Resolution resolution) {
            return new IndexMapper<>() {

                @Override public long toIndex( CalendarDate key) {
                    return key.millis / resolution.toDurationInMillis();
                }

                @Override public CalendarDate toKey( long index) {
                    return new CalendarDate(index * resolution.toDurationInMillis());
                }

            };
        }

    };

    public static final TimeIndex<Date> DATE = new TimeIndex<>() {

        @Override
        public IndexMapper<Date> from( Date reference) {
            return new IndexMapper<>() {

                @Override public long toIndex( Date key) {
                    return key.getTime() - reference.getTime();
                }

                @Override public Date toKey( long index) {
                    return new Date(index + reference.getTime());
                }

            };
        }

        @Override
        public IndexMapper<Date> from( Date reference,  Resolution resolution) {
            return new IndexMapper<>() {

                @Override public long toIndex( Date key) {
                    return (key.getTime() - reference.getTime()) / resolution.toDurationInMillis();
                }

                @Override public Date toKey( long index) {
                    return new Date((index * resolution.toDurationInMillis()) + reference.getTime());
                }

            };
        }

        @Override
        public IndexMapper<Date> plain() {
            return new IndexMapper<>() {

                @Override public long toIndex( Date key) {
                    return key.getTime();
                }

                @Override public Date toKey( long index) {
                    return new Date(index);
                }

            };
        }

        @Override
        public IndexMapper<Date> plain( Resolution resolution) {
            return new IndexMapper<>() {

                @Override public long toIndex( Date key) {
                    return key.getTime() / resolution.toDurationInMillis();
                }

                @Override public Date toKey( long index) {
                    return new Date(index * resolution.toDurationInMillis());
                }

            };
        }

    };

    public static final TimeIndex<Instant> INSTANT = new TimeIndex<>() {

        @Override
        public IndexMapper<Instant> from( Instant reference) {
            return new IndexMapper<>() {

                @Override public long toIndex( Instant key) {
                    return reference.until(key, ChronoUnit.NANOS);
                }

                @Override public Instant toKey( long index) {
                    return reference.plus(index, ChronoUnit.NANOS);
                }

            };
        }

        @Override
        public IndexMapper<Instant> from( Instant reference,  Resolution resolution) {
            return new IndexMapper<>() {

                private final long myResolution = resolution.toDurationInNanos();

                @Override public long toIndex( Instant key) {
                    return reference.until(key, ChronoUnit.NANOS) / myResolution;
                }

                @Override public Instant toKey( long index) {
                    return reference.plus(index * myResolution, ChronoUnit.NANOS);
                }

            };
        }

        @Override
        public IndexMapper<Instant> plain() {
            return new IndexMapper<>() {

                @Override public long toIndex( Instant key) {
                    return key.toEpochMilli();
                }

                @Override public Instant toKey( long index) {
                    return Instant.ofEpochMilli(index);
                }

            };
        }

        @Override
        public IndexMapper<Instant> plain( Resolution resolution) {
            return new IndexMapper<>() {

                private final long myResolution = resolution.toDurationInMillis();

                @Override public long toIndex( Instant key) {
                    return key.toEpochMilli() / myResolution;
                }

                @Override public Instant toKey( long index) {
                    return Instant.ofEpochMilli(index * myResolution);
                }

            };
        }

    };

    public static final TimeIndex<LocalDate> LOCAL_DATE = new TimeIndex<>() {

        @Override
        public IndexMapper<LocalDate> from( LocalDate reference) {
            return new IndexMapper<>() {

                private final long myReference = reference.toEpochDay();

                @Override public long toIndex( LocalDate key) {
                    return key.toEpochDay() - myReference;
                }

                @Override public LocalDate toKey( long index) {
                    return LocalDate.ofEpochDay(myReference + index);
                }

            };
        }

        @Override
        public IndexMapper<LocalDate> from( LocalDate reference,  Resolution resolution) {
            return new IndexMapper<>() {

                private final long myReference = reference.toEpochDay() * DAY_SIZE;
                private final long myResolution = resolution.toDurationInMillis();

                @Override public long toIndex( LocalDate key) {
                    return ((DAY_SIZE * key.toEpochDay()) - myReference) / myResolution;
                }

                @Override public LocalDate toKey( long index) {
                    return LocalDate.ofEpochDay((myReference + (index * myResolution)) / DAY_SIZE);
                }

            };
        }

        @Override
        public IndexMapper<LocalDate> plain() {
            return new IndexMapper<>() {

                @Override public long toIndex( LocalDate key) {
                    return key.toEpochDay();
                }

                @Override public LocalDate toKey( long index) {
                    return LocalDate.ofEpochDay(index);
                }

            };
        }

        @Override
        public IndexMapper<LocalDate> plain( Resolution resolution) {
            return new IndexMapper<>() {

                private final long myResolution = resolution.toDurationInMillis();

                @Override public long toIndex( LocalDate key) {
                    return (DAY_SIZE * key.toEpochDay()) / myResolution;
                }

                @Override public LocalDate toKey( long index) {
                    return LocalDate.ofEpochDay((index * myResolution) / DAY_SIZE);
                }

            };
        }

    };

    public static final TimeIndex<LocalDateTime> LOCAL_DATE_TIME = new TimeIndex<>() {

        @Override
        public IndexMapper<LocalDateTime> from( LocalDateTime reference) {
            return new IndexMapper<>() {

                private final long myReference = reference.toInstant(ZoneOffset.UTC).toEpochMilli();

                @Override public long toIndex( LocalDateTime key) {
                    return key.toInstant(ZoneOffset.UTC).toEpochMilli() - myReference;
                }

                @Override public LocalDateTime toKey( long index) {
                    return LocalDateTime.ofInstant(Instant.ofEpochMilli(myReference + index), ZoneOffset.UTC);
                }

            };
        }

        @Override
        public IndexMapper<LocalDateTime> from( LocalDateTime reference,  Resolution resolution) {
            return new IndexMapper<>() {

                private final long myReference = reference.toEpochSecond(ZoneOffset.UTC);
                private final long myResolution = resolution.toDurationInMillis();

                @Override public long toIndex( LocalDateTime key) {
                    return (key.toEpochSecond(ZoneOffset.UTC) - myReference) / myResolution;
                }

                @Override public LocalDateTime toKey( long index) {
                    return LocalDateTime.ofEpochSecond(myReference + (index * myResolution), 0, ZoneOffset.UTC);
                }

            };
        }

        @Override
        public IndexMapper<LocalDateTime> plain() {
            return new IndexMapper<>() {

                @Override public long toIndex( LocalDateTime key) {
                    return key.toInstant(ZoneOffset.UTC).toEpochMilli();
                }

                @Override public LocalDateTime toKey( long index) {
                    return LocalDateTime.ofInstant(Instant.ofEpochMilli(index), ZoneOffset.UTC);
                }

            };
        }

        @Override
        public IndexMapper<LocalDateTime> plain( Resolution resolution) {
            return new IndexMapper<>() {

                private final long myResolution = resolution.toDurationInMillis();

                @Override public long toIndex( LocalDateTime key) {
                    return key.toEpochSecond(ZoneOffset.UTC) / myResolution;
                }

                @Override public LocalDateTime toKey( long index) {
                    return LocalDateTime.ofEpochSecond(index * myResolution, 0, ZoneOffset.UTC);
                }

            };
        }

    };

    public static final TimeIndex<LocalTime> LOCAL_TIME = new TimeIndex<>() {

        @Override
        public IndexMapper<LocalTime> from( LocalTime reference) {
            return new IndexMapper<>() {

                final long myReference = reference.toNanoOfDay();

                @Override public long toIndex( LocalTime key) {
                    return key.toNanoOfDay() - myReference;
                }

                @Override public LocalTime toKey( long index) {
                    return LocalTime.ofNanoOfDay(myReference + index);
                }

            };
        }

        @Override
        public IndexMapper<LocalTime> from( LocalTime reference,  Resolution resolution) {
            return new IndexMapper<>() {

                final long myReference = reference.toNanoOfDay();
                final long myResolution = resolution.toDurationInNanos();

                @Override public long toIndex( LocalTime key) {
                    return (key.toNanoOfDay() - myReference) / myResolution;
                }

                @Override public LocalTime toKey( long index) {
                    return LocalTime.ofNanoOfDay(myReference + (index * myResolution));
                }

            };
        }

        @Override
        public IndexMapper<LocalTime> plain() {
            return new IndexMapper<>() {

                @Override public long toIndex( LocalTime key) {
                    return key.toNanoOfDay();
                }

                @Override public LocalTime toKey( long index) {
                    return LocalTime.ofNanoOfDay(index);
                }

            };
        }

        @Override
        public IndexMapper<LocalTime> plain( Resolution resolution) {
            return new IndexMapper<>() {

                final long myResolution = resolution.toDurationInNanos();

                @Override public long toIndex( LocalTime key) {
                    return key.toNanoOfDay() / myResolution;
                }

                @Override public LocalTime toKey( long index) {
                    return LocalTime.ofNanoOfDay(index * myResolution);
                }

            };
        }

    };

    public static final TimeIndex<OffsetDateTime> OFFSET_DATE_TIME = new TimeIndex<>() {

        @Override
        public IndexMapper<OffsetDateTime> from( OffsetDateTime reference) {
            return new IndexMapper<>() {

                private final IndexMapper<Instant> myDelegate = INSTANT.from(reference.toInstant());
                private final ZoneOffset myOffset = reference.getOffset();

                @Override public long toIndex( OffsetDateTime key) {
                    return myDelegate.toIndex(key.toInstant());
                }

                @Override public OffsetDateTime toKey( long index) {
                     Instant tmpInstant = myDelegate.toKey(index);
                    if (myOffset != null) {
                        return OffsetDateTime.ofInstant(tmpInstant, myOffset);
                    } else {
                        return OffsetDateTime.ofInstant(tmpInstant, ZoneOffset.UTC);
                    }
                }
            };
        }

        @Override
        public IndexMapper<OffsetDateTime> from( OffsetDateTime reference,  Resolution resolution) {
            return new IndexMapper<>() {

                private final IndexMapper<Instant> myDelegate = INSTANT.from(reference.toInstant(), resolution);
                private final ZoneOffset myOffset = reference.getOffset();

                @Override public long toIndex( OffsetDateTime key) {
                    return myDelegate.toIndex(key.toInstant());
                }

                @Override public OffsetDateTime toKey( long index) {
                     Instant tmpInstant = myDelegate.toKey(index);
                    if (myOffset != null) {
                        return OffsetDateTime.ofInstant(tmpInstant, myOffset);
                    } else {
                        return OffsetDateTime.ofInstant(tmpInstant, ZoneOffset.UTC);
                    }
                }
            };
        }

        @Override
        public IndexMapper<OffsetDateTime> plain() {
            return new IndexMapper<>() {

                private final IndexMapper<Instant> myDelegate = INSTANT.plain();
                private transient ZoneOffset myOffset = null;

                @Override public long toIndex( OffsetDateTime key) {
                    myOffset = key.getOffset();
                    return myDelegate.toIndex(key.toInstant());
                }

                @Override public OffsetDateTime toKey( long index) {
                     Instant tmpInstant = myDelegate.toKey(index);
                    if (myOffset != null) {
                        return OffsetDateTime.ofInstant(tmpInstant, myOffset);
                    } else {
                        return OffsetDateTime.ofInstant(tmpInstant, ZoneOffset.UTC);
                    }
                }
            };
        }

        @Override
        public IndexMapper<OffsetDateTime> plain( Resolution resolution) {
            return new IndexMapper<>() {

                private final IndexMapper<Instant> myDelegate = INSTANT.plain(resolution);

                @Override public long toIndex( OffsetDateTime key) {
                    return myDelegate.toIndex(key.toInstant());
                }

                @Override public OffsetDateTime toKey( long index) {
                     Instant tmpInstant = myDelegate.toKey(index);

                    return OffsetDateTime.ofInstant(tmpInstant, ZoneOffset.UTC);

                }
            };
        }

    };

    public static final TimeIndex<ZonedDateTime> ZONED_DATE_TIME = new TimeIndex<>() {

        @Override
        public IndexMapper<ZonedDateTime> from( ZonedDateTime reference) {
            return new IndexMapper<>() {

                private final IndexMapper<Instant> myDelegate = INSTANT.from(reference.toInstant());
                private final ZoneId myZone = reference.getZone();

                @Override public long toIndex( ZonedDateTime key) {
                    return myDelegate.toIndex(key.toInstant());
                }

                @Override public ZonedDateTime toKey( long index) {
                     Instant tmpInstant = myDelegate.toKey(index);
                    if (myZone != null) {
                        return ZonedDateTime.ofInstant(tmpInstant, myZone);
                    } else {
                        return ZonedDateTime.ofInstant(tmpInstant, ZoneOffset.UTC);
                    }
                }
            };
        }

        @Override
        public IndexMapper<ZonedDateTime> from( ZonedDateTime reference,  Resolution resolution) {
            return new IndexMapper<>() {

                private final IndexMapper<Instant> myDelegate = INSTANT.from(reference.toInstant(), resolution);
                private final ZoneId myZone = reference.getZone();

                @Override public long toIndex( ZonedDateTime key) {
                    return myDelegate.toIndex(key.toInstant());
                }

                @Override public ZonedDateTime toKey( long index) {
                     Instant tmpInstant = myDelegate.toKey(index);
                    if (myZone != null) {
                        return ZonedDateTime.ofInstant(tmpInstant, myZone);
                    } else {
                        return ZonedDateTime.ofInstant(tmpInstant, ZoneOffset.UTC);
                    }
                }
            };
        }

        @Override
        public IndexMapper<ZonedDateTime> plain() {
            return new IndexMapper<>() {

                private final IndexMapper<Instant> myDelegate = INSTANT.plain();
                private transient ZoneId myZone = null;

                @Override public long toIndex( ZonedDateTime key) {
                    myZone = key.getZone();
                    return myDelegate.toIndex(key.toInstant());
                }

                @Override public ZonedDateTime toKey( long index) {
                     Instant tmpInstant = myDelegate.toKey(index);
                    if (myZone != null) {
                        return ZonedDateTime.ofInstant(tmpInstant, myZone);
                    } else {
                        return ZonedDateTime.ofInstant(tmpInstant, ZoneOffset.UTC);
                    }
                }
            };
        }

        @Override
        public IndexMapper<ZonedDateTime> plain( Resolution resolution) {
            return new IndexMapper<>() {

                private final IndexMapper<Instant> myDelegate = INSTANT.plain(resolution);

                @Override public long toIndex( ZonedDateTime key) {
                    return myDelegate.toIndex(key.toInstant());
                }

                @Override public ZonedDateTime toKey( long index) {
                     Instant tmpInstant = myDelegate.toKey(index);
                    return ZonedDateTime.ofInstant(tmpInstant, ZoneOffset.UTC);
                }
            };
        }

    };

    static final long DAY_SIZE = CalendarDateUnit.DAY.toDurationInMillis();

    public abstract IndexMapper<T> from(T reference);

    public abstract IndexMapper<T> from(T reference, Resolution resolution);

    public abstract IndexMapper<T> plain();

    public abstract IndexMapper<T> plain(Resolution resolution);

}

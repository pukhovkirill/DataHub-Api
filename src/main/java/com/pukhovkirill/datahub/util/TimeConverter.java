package com.pukhovkirill.datahub.util;

import java.sql.Timestamp;
import java.time.ZonedDateTime;

public class TimeConverter {

    public static Timestamp toTimestamp(ZonedDateTime dateTime) {
        return Timestamp.valueOf(dateTime.toLocalDateTime());
    }

}

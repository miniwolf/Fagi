package com.fagi.logging.java;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * <p>
 * This class formats logs in the default Fagi format. The format looks as follows:
 * </p>
 * <p>
 * yyyy-MM-dd HH:mm:ss [LOG LEVEL] full.classpath.for.class - LOG MESSAGE
 * </p>
 * <p>
 * The following is an example of a log statement:
 * </p>
 * <p>
 * 2025-07-19 09:42:37 [INFO] com.fagi.server.Server - Starting Server
 * </p>
 * <p>
 * If there is an exception in the log, the stacktrace will start on the next line.
 * </p>
 * <p>
 * This should only be used if the user hasn't specified their own logging configuration file.
 * </p>
 *
 * @author Marcus Haagh
 */
public class JavaLoggerFormatter extends Formatter {
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String format(LogRecord record) {
        String loggerName = record.getLoggerName();

        StringBuilder sb = new StringBuilder();

        sb.append(dateFormat.format(new Date(record.getMillis())));

        sb.append(String.format(
                " [%s] %s - %s%n",
                record.getLevel(),
                loggerName,
                formatMessage(record)
        ));

        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            record
                    .getThrown()
                    .printStackTrace(new PrintWriter(sw));
            sb.append(sw);
        }

        return sb.toString();
    }
}

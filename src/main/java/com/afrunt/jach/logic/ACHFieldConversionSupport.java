/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.afrunt.jach.logic;

import com.afrunt.beanmetadata.FieldConversionSupport;
import com.afrunt.jach.annotation.ACHField;
import com.afrunt.jach.metadata.ACHBeanMetadata;
import com.afrunt.jach.metadata.ACHFieldMetadata;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.DateTimeException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;

/**
 * @author Andrii Frunt
 */
@SuppressWarnings("WeakerAccess")
public interface ACHFieldConversionSupport extends FieldConversionSupport<ACHBeanMetadata, ACHFieldMetadata>, ACHErrorMixIn {
    default Integer valueStringToInteger(String value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        return stringToBigDecimal(value, bm, fm).intValue();
    }

    default BigInteger valueStringToBigInteger(String value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        return stringToBigDecimal(value, bm, fm).toBigInteger();
    }

    default Long valueStringToLong(String value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        return stringToBigDecimal(value, bm, fm).longValue();
    }

    default Short valueStringToShort(String value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        return stringToBigDecimal(value, bm, fm).shortValue();
    }

    default LocalDate valueStringToLocalDate(String value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        if (ACHField.EMPTY_DATE_PATTERN.equals(fm.getDateFormat())) {
            throwError("Date pattern should be specified for field " + fm);
        }
        try {
            DateTimeFormatter formatter = buildDateFormatter(fm.getDateFormat());
            TemporalAccessor parsed = formatter.parse(value);
            // Use year 2000 (a leap year) as fallback for patterns without a year component
            // (e.g. "MMdd"). This avoids crashes on "0229" in non-leap years and keeps the
            // result deterministic. The year is never serialized for year-less patterns.
            int year = parsed.isSupported(ChronoField.YEAR)
                    ? parsed.get(ChronoField.YEAR)
                    : 2000;
            return LocalDate.of(year, parsed.get(ChronoField.MONTH_OF_YEAR), parsed.get(ChronoField.DAY_OF_MONTH));
        } catch (DateTimeException e) {
            throw error("Error parsing date " + value + " with pattern " + fm.getDateFormat() + " for field " + fm, e);
        }
    }

    default BigDecimal valueStringToBigDecimal(String value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        return moveDecimalRight(stringToBigDecimal(value, bm, fm), fm.getDigitsAfterComma());
    }

    default BigDecimal stringToBigDecimal(String value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        if (!StringUtil.isNumeric(value)) {
            throwError(String.format("Cannot parse string %s to number for field %s", value.trim(), fm));
        }

        return new BigDecimal(value.trim());
    }

    default String fieldStringToString(String value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        return padString(value, fm.getLength());
    }

    default String fieldShortToString(Short value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        return padNumber(value, fm.getLength());
    }

    default String fieldIntegerToString(Integer value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        return padNumber(value, fm.getLength());
    }

    default String fieldLongToString(Long value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        return padNumber(value, fm.getLength());
    }

    default String fieldBigIntegerToString(BigInteger value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        return padNumber(value, fm.getLength());
    }

    default String fieldBigDecimalToString(BigDecimal value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        return padNumber(String.valueOf(
                moveDecimalLeft(value, fm.getDigitsAfterComma())
                        .longValue()), fm.getLength());
    }

    default String fieldLocalDateToString(LocalDate value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        return value.format(buildDateFormatter(fm.getDateFormat()));
    }

    /**
     * Builds a DateTimeFormatter that replicates SimpleDateFormat's 80/20 sliding window
     * for two-digit year patterns ("yy"). DateTimeFormatter.ofPattern("yy") uses a fixed
     * base of 2000 (00-99 → 2000-2099), while SimpleDateFormat used 80 years before and
     * 20 years after the current date. This method preserves the old behavior.
     */
    default DateTimeFormatter buildDateFormatter(String pattern) {
        int idx = pattern.indexOf("yy");
        if (idx >= 0 && !pattern.contains("yyy")) {
            String before = pattern.substring(0, idx);
            String after = pattern.substring(idx + 2);
            int baseYear = LocalDate.now().minusYears(80).getYear();
            DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
            if (!before.isEmpty()) {
                builder.appendPattern(before);
            }
            builder.appendValueReduced(ChronoField.YEAR, 2, 2, baseYear);
            if (!after.isEmpty()) {
                builder.appendPattern(after);
            }
            return builder.toFormatter();
        }
        return DateTimeFormatter.ofPattern(pattern);
    }

    default BigDecimal moveDecimalLeft(BigDecimal number, int digitsAfterComma) {
        return number.multiply(BigDecimal.TEN.pow(digitsAfterComma));
    }

    default BigDecimal moveDecimalRight(BigDecimal number, int digitsAfterComma) {
        return number.divide(BigDecimal.TEN.pow(digitsAfterComma));
    }

    default String padString(Object value, int length) {
        return StringUtil.rightPad(value.toString(), length);
    }

    default String padNumber(Object value, int length) {
        return StringUtil.leftPad(value.toString(), length, "0");
    }
}

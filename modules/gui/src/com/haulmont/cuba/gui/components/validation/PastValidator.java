/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

import com.haulmont.cuba.core.global.DateTimeTransformations;
import com.haulmont.cuba.gui.components.ValidationException;
import com.haulmont.cuba.gui.components.validation.time.TimeConstraint;

import java.time.*;
import java.util.Date;

/**
 * Validates that date or time in the past.
 * <p>
 * Note, types that support TimeZones ca be found in {@link DateTimeTransformations#isDateTypeSupportsTimeZones(Class)}.
 *
 * @param <T> {@link Date}, {@link LocalDate}, {@link LocalDateTime}, {@link LocalTime}, {@link OffsetDateTime},
 *            {@link OffsetTime}
 */
public class PastValidator<T> extends AbstractValidator<T> {

    protected boolean checkSeconds = false;

    public PastValidator() {
        this.defaultMessage = messages.getMainMessage("validation.constraints.past");
    }

    /**
     * Constructor for custom error message.
     *
     * @param message error message
     */
    public PastValidator(String message) {
        this.message = message;
    }

    /**
     * Set true if validator should also check seconds and nanos (if supported) in value. Default value is false.
     *
     * @param checkSeconds check seconds
     * @return current instance
     */
    public PastValidator<T> withCheckSeconds(boolean checkSeconds) {
        this.checkSeconds = checkSeconds;
        return this;
    }

    /**
     * @return true if seconds and nanos are checked
     */
    public boolean isCheckSeconds() {
        return checkSeconds;
    }

    @Override
    public void accept(T value) throws ValidationException {
        // consider null value is valid
        if (value == null) {
            return;
        }

        TimeConstraint timeConstraint = ConstraintHelper.getTimeConstraint(value);
        if (timeConstraint == null) {
            throw new IllegalArgumentException("PastValidator doesn't support following type: '" + value.getClass() + "'");
        }

        timeConstraint.setCheckSeconds(checkSeconds);
        if (!timeConstraint.isPast()) {
            throw new ValidationException(getErrorMessage());
        }
    }
}

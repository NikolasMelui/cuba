/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

import com.haulmont.cuba.core.global.DateTimeTransformations;
import com.haulmont.cuba.gui.components.ValidationException;
import com.haulmont.cuba.gui.components.validation.time.TimeValidator;
import org.dom4j.Element;

import java.time.*;
import java.util.Date;

/**
 * Validates that date or time in the past or present.
 * <p>
 * Note, types that support TimeZones can be found in {@link DateTimeTransformations#isDateTypeSupportsTimeZones(Class)}
 *
 * @param <T> {@link Date}, {@link LocalDate}, {@link LocalDateTime}, {@link LocalTime}, {@link OffsetDateTime},
 *            {@link OffsetTime}
 */
public class PastOrPresentValidator<T> extends AbstractValidator<T> {

    protected boolean checkSeconds = false;

    protected String defaultMessage = messages.getMainMessage("validation.constraints.pastOrPresent");

    public PastOrPresentValidator() {
    }

    /**
     * Constructor for custom error message.
     *
     * @param message error message
     */
    public PastOrPresentValidator(String message) {
        this.message = message;
    }

    /**
     * @param element     'pastOrPresent' element
     * @param messagePack message pack
     */
    public PastOrPresentValidator(Element element, String messagePack) {
        this.messagePack = messagePack;
        this.message = loadMessage(element);

        String checkSeconds = element.attributeValue("checkSeconds");
        if (checkSeconds != null) {
            this.checkSeconds = Boolean.parseBoolean(checkSeconds);
        }
    }

    /**
     * Set true if validator should also check seconds and nanos (if supported) in value. Default value is false.
     *
     * @param checkSeconds check seconds
     * @return current instance
     */
    public PastOrPresentValidator<T> withCheckSeconds(boolean checkSeconds) {
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
    public String getDefaultMessage() {
        return defaultMessage;
    }

    @Override
    public void accept(T value) throws ValidationException {
        // consider null value is valid
        if (value == null) {
            return;
        }

        TimeValidator timeConstraint = ValidatorHelper.getTimeConstraint(value);
        if (timeConstraint == null) {
            throw new IllegalArgumentException("PastOrPresentValidator doesn't support following type: '" + value.getClass() + "'");
        }

        timeConstraint.setCheckSeconds(checkSeconds);
        if (!timeConstraint.isPastOrPresent()) {
            throw new ValidationException(getErrorMessage());
        }
    }
}

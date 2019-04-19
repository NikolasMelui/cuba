/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DateTimeTransformations;
import com.haulmont.cuba.gui.components.ValidationException;
import com.haulmont.cuba.gui.components.validation.time.TimeValidator;
import org.dom4j.Element;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.Date;

/**
 * Validates that date or time in the future or present.
 * <p>
 * Note, types that support TimeZones can be found in {@link DateTimeTransformations#isDateTypeSupportsTimeZones(Class)}.
 * <p>
 * In order to provide your own implementation globally, create a subclass and register it in {@code web-spring.xml},
 * for example:
 * <pre>
 *    &lt;bean id="cuba_FutureOrPresentValidator" class="com.haulmont.cuba.gui.components.validation.FutureOrPresentValidator" scope="prototype"/&gt;
 *    </pre>
 * Use {@code create()} static methods instead of constructors when creating the action programmatically.
 *
 * @param <T> {@link Date}, {@link LocalDate}, {@link LocalDateTime}, {@link LocalTime}, {@link OffsetDateTime},
 *            {@link OffsetTime}
 */
@Component(FutureOrPresentValidator.NAME)
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class FutureOrPresentValidator<T> extends AbstractValidator<T> {

    public static final String NAME = "cuba_FutureOrPresentValidator";

    protected boolean checkSeconds = false;

    protected String defaultMessage = messages.getMainMessage("validation.constraints.futureOrPresent");

    public FutureOrPresentValidator() {
    }

    /**
     * Constructor for custom error message.
     *
     * @param message error message
     */
    public FutureOrPresentValidator(String message) {
        this.message = message;
    }

    /**
     * @param element     'futureOrPresent' element
     * @param messagePack message pack
     */
    public FutureOrPresentValidator(Element element, String messagePack) {
        this.messagePack = messagePack;
        this.message = loadMessage(element);

        String checkSeconds = element.attributeValue("checkSeconds");
        if (checkSeconds != null) {
            this.checkSeconds = Boolean.parseBoolean(checkSeconds);
        }
    }

    /**
     * Creates validator with default error message.
     *
     * @param <T> {@link Date}, {@link LocalDate}, {@link LocalDateTime}, {@link LocalTime}, {@link OffsetDateTime},
     *            {@link OffsetTime}
     * @return validator
     */
    public static <T> FutureOrPresentValidator<T> create() {
        return AppBeans.getPrototype(NAME);
    }

    /**
     * Constructor for custom error message.
     *
     * @param message error message
     * @param <T>     {@link Date}, {@link LocalDate}, {@link LocalDateTime}, {@link LocalTime}, {@link OffsetDateTime},
     *                {@link OffsetTime}
     * @return validator
     */
    public static <T> FutureOrPresentValidator<T> create(String message) {
        return AppBeans.getPrototype(NAME, message);
    }

    /**
     * @param element     'futureOrPresent' element
     * @param messagePack message pack
     * @param <T>         {@link Date}, {@link LocalDate}, {@link LocalDateTime}, {@link LocalTime}, {@link OffsetDateTime},
     *                    {@link OffsetTime}
     * @return validator
     */
    public static <T> FutureOrPresentValidator<T> create(Element element, String messagePack) {
        return AppBeans.getPrototype(NAME, element, messagePack);
    }

    /**
     * Set true if validator should also check seconds and nanos (if supported) in value. Default value is false.
     *
     * @param checkSeconds check seconds
     * @return current instance
     */
    public FutureOrPresentValidator<T> withCheckSeconds(boolean checkSeconds) {
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
            throw new IllegalArgumentException("FutureOrPresentValidator doesn't support following type: '" + value.getClass() + "'");
        }

        timeConstraint.setCheckSeconds(checkSeconds);
        if (!timeConstraint.isFutureOrPresent()) {
            throw new ValidationException(getErrorMessage());
        }
    }
}

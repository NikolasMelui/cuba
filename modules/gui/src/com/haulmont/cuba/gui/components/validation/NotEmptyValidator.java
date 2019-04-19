/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

import com.haulmont.cuba.gui.components.ValidationException;
import org.dom4j.Element;

import java.util.Collection;

/**
 * NotEmpty validator checks that value is not null and not empty.
 * <p>
 * For error message it uses Groovy string and it is possible to use '$value' keys for formatted output.
 * <p>
 * Note, that size validator for Collection doesn't use key 'value' for output error message.
 *
 * @param <T> Collection or String
 */
public class NotEmptyValidator<T> extends AbstractValidator<T> {

    protected String defaultMessage = messages.getMainMessage("validation.constraints.notEmpty");

    public NotEmptyValidator() {
    }

    /**
     * Constructor for custom error message.
     *
     * @param message error message
     */
    public NotEmptyValidator(String message) {
        this.message = message;
    }

    /**
     * @param element     notEmpty element
     * @param messagePack message pack
     */
    public NotEmptyValidator(Element element, String messagePack) {
        this.messagePack = messagePack;
        this.message = loadMessage(element);
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }

    @Override
    public void accept(T value) throws ValidationException {
        if (value == null) {
            throw new ValidationException(getErrorMessage());
        }

        Class clazz = value.getClass();
        if (Collection.class.isAssignableFrom(clazz)) {
            if (((Collection) value).isEmpty()) {
                throw new ValidationException(getErrorMessage());
            }
        } else if (clazz.equals(String.class) && ((String) value).isEmpty()) {
            throw new ValidationException(getErrorMessage());
        }
    }
}

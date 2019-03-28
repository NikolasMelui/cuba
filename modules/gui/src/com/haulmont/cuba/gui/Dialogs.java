/*
 * Copyright (c) 2008-2018 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.haulmont.cuba.gui;

import com.haulmont.cuba.gui.app.core.inputdialog.InputDialog;
import com.haulmont.cuba.gui.app.core.inputdialog.InputParameter;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.ContentMode;
import com.haulmont.cuba.gui.components.SizeUnit;
import com.haulmont.cuba.gui.components.inputdialog.InputDialogAction;
import com.haulmont.cuba.gui.screen.FrameOwner;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * Utility dialogs API.
 */
public interface Dialogs {
    /**
     * Creates option dialog builder.
     * <br>
     * Example of showing an option dialog:
     * <pre>{@code
     * dialogs.createOptionDialog()
     *         .withType(MessageType.CONFIRMATION)
     *         .withCaption("Question")
     *         .withMessage("Do you want to discard data?")
     *         .withActions(
     *                 new DialogAction(DialogAction.Type.YES).withHandler(e -> {
     *                     // YES option selected
     *                 }),
     *                 new DialogAction(DialogAction.Type.NO).withHandler(e -> {
     *                     // NO option selected
     *                 })
     *         )
     *         .show();
     * }</pre>
     *
     * @return builder
     */
    OptionDialogBuilder createOptionDialog();

    /**
     * Creates option dialog builder with the passed message type.
     * <br>
     * Example of showing an option dialog:
     * <pre>{@code
     * dialogs.createOptionDialog()
     *         .withType(MessageType.CONFIRMATION)
     *         .withCaption("Question")
     *         .withMessage("Do you want to save data?")
     *         .withActions(
     *                 new DialogAction(DialogAction.Type.YES).withHandler(e -> {
     *                     // YES option selected
     *                 }),
     *                 new DialogAction(DialogAction.Type.NO)
     *                         .withCaption("I am not sure")
     *                         .withHandler(e -> {
     *                     // NO option selected
     *                 }),
     *                 new DialogAction(DialogAction.Type.CANCEL).withHandler(e -> {
     *                     // CANCEL option selected
     *                 })
     *         )
     *         .show();
     * }</pre>
     *
     * @return builder
     */
    OptionDialogBuilder createOptionDialog(MessageType messageType);

    /**
     * Creates message dialog builder.
     * <br>
     * Example of showing a message dialog:
     * <pre>{@code
     * dialogs.createMessageDialog()
     *         .withType(MessageType.WARNING)
     *         .withCaption("Alert")
     *         .withMessage("Report has been saved")
     *         .show();
     * }</pre>
     *
     * @return builder
     */
    MessageDialogBuilder createMessageDialog();

    /**
     * Creates message dialog builder with the passed message type.
     * <br>
     * Example of showing a message dialog:
     * <pre>{@code
     * dialogs.createMessageDialog(MessageType.WARNING)
     *         .withCaption("Alert")
     *         .withMessage("Report has been saved")
     *         .show();
     * }</pre>
     *
     * @return builder
     */
    MessageDialogBuilder createMessageDialog(MessageType messageType);

    /**
     * Creates exception dialog builder.
     * <br>
     * Example of showing an exception dialog:
     * <pre>{@code
     * dialogs.createExceptionDialog()
     *         .withCaption("Alert")
     *         .withMessage("Report has been saved")
     *         .withThrowable(exception)
     *         .show();
     * }</pre>
     *
     * @return builder
     */
    ExceptionDialogBuilder createExceptionDialog();

    /**
     * Creates input dialog builder.
     * <p>
     * Example of showing an input dialog:
     * <pre>{@code
     * dialogs.createInputDialog(this)
     *         .withParameters(
     *                 stringParameter("name").withCaption("Name"),
     *                 intParameter("count").withCaption("Count"))
     *         .withActions(Dialogs.DialogActions.OK_CANCEL)
     *         .withCloseListener(closeEvent ->
     *                 notifications.create(Notifications.NotificationType.TRAY)
     *                         .withCaption("Dialog is closed")
     *                         .show())
     *         .withCaption("Products")
     *         .show();
     * }</pre>
     *
     * @param owner origin screen from input dialog is invoked
     * @return builder
     */
    InputDialogBuilder createInputDialog(FrameOwner owner);

    /**
     * Builder of dialog with option buttons.
     */
    interface OptionDialogBuilder {
        /**
         * Sets caption text.
         *
         * @param caption caption text
         * @return builder
         */
        OptionDialogBuilder withCaption(String caption);
        /**
         * @return caption text
         */
        String getCaption();

        /**
         * Sets message text.
         *
         * @param message message text
         * @return builder
         */
        OptionDialogBuilder withMessage(String message);
        /**
         * @return message text
         */
        String getMessage();

        /**
         * Sets message type, e.g. {@link MessageType#CONFIRMATION} or {@link MessageType#WARNING}.
         *
         * @param type message type
         * @return builder
         */
        OptionDialogBuilder withType(MessageType type);
        /**
         * @return message type
         */
        MessageType getType();

        /**
         * Sets content mode for message, e.g. {@link ContentMode#TEXT}, {@link ContentMode#HTML}
         * or {@link ContentMode#PREFORMATTED}.
         *
         * @param contentMode content mode
         * @return builder
         */
        OptionDialogBuilder withContentMode(ContentMode contentMode);
        /**
         * @return message content mode
         */
        ContentMode getContentMode();

        /**
         * Sets dialog actions.
         *
         * @param actions actions
         * @return builder
         */
        OptionDialogBuilder withActions(Action... actions);
        /**
         * @return dialog actions
         */
        Action[] getActions();

        /**
         * Sets dialog width.
         *
         * @param width width
         * @return builder
         */
        OptionDialogBuilder withWidth(String width);
        /**
         * @return dialog width value
         */
        float getWidth();
        /**
         * @return dialog width unit
         */
        SizeUnit getWidthSizeUnit();

        /**
         * Sets dialog height.
         *
         * @param height height
         * @return builder
         */
        OptionDialogBuilder withHeight(String height);
        /**
         *
         * @return dialog height value
         */
        float getHeight();
        /**
         *
         * @return dialog height unit
         */
        SizeUnit getHeightSizeUnit();

        /**
         * Sets whether dialog should be maximized.
         *
         * @param maximized maximized flag
         * @return builder
         */
        OptionDialogBuilder withMaximized(boolean maximized);
        /**
         * Enables dialog maximized mode.
         *
         * @return builder
         */
        OptionDialogBuilder maximized();
        /**
         * @return true if dialog will be maximized
         */
        boolean isMaximized();

        /**
         * Sets custom CSS style name for dialog.
         *
         * @param styleName style name
         * @return builder
         */
        OptionDialogBuilder withStyleName(String styleName);
        /**
         * @return custom style name
         */
        String getStyleName();

        /**
         * Shows the dialog.
         */
        void show();
    }

    /**
     * Builder of information dialog.
     */
    interface MessageDialogBuilder {
        /**
         * Sets caption text.
         *
         * @param caption caption text
         * @return builder
         */
        MessageDialogBuilder withCaption(String caption);
        /**
         * @return caption text
         */
        String getCaption();

        /**
         * Sets message text.
         *
         * @param message message text
         * @return builder
         */
        MessageDialogBuilder withMessage(String message);
        /**
         * @return message text
         */
        String getMessage();

        /**
         * Sets message type, e.g. {@link MessageType#CONFIRMATION} or {@link MessageType#WARNING}.
         *
         * @param type message type
         * @return builder
         */
        MessageDialogBuilder withType(MessageType type);
        /**
         * @return message type
         */
        MessageType getType();

        /**
         * Sets content mode for message, e.g. {@link ContentMode#TEXT}, {@link ContentMode#HTML}
         * or {@link ContentMode#PREFORMATTED}.
         *
         * @param contentMode content mode
         * @return builder
         */
        MessageDialogBuilder withContentMode(ContentMode contentMode);
        /**
         * @return content mode
         */
        ContentMode getContentMode();

        /**
         * Sets dialog width.
         *
         * @param width width
         * @return builder
         */
        MessageDialogBuilder withWidth(String width);
        /**
         *
         * @return dialog width
         */
        float getWidth();
        /**
         *
         * @return dialog width unit
         */
        SizeUnit getWidthSizeUnit();

        /**
         * Sets dialog height.
         *
         * @param height height
         * @return builder
         */
        MessageDialogBuilder withHeight(String height);
        /**
         * @return dialog height
         */
        float getHeight();
        /**
         * @return dialog height unit
         */
        SizeUnit getHeightSizeUnit();

        /**
         * @return true if window is modal
         */
        boolean isModal();
        /**
         * Sets dialog modality. When a modal window is open, components outside that window cannot be accessed.
         *
         * @param modal modal flag
         * @return builder
         */
        MessageDialogBuilder withModal(boolean modal);
        /**
         * Enables modal mode for dialog.
         *
         * @return builder
         */
        MessageDialogBuilder modal();

        /**
         * @return true if dialog will be maximized
         */
        boolean isMaximized();
        /**
         * Sets whether dialog should be maximized.
         *
         * @param maximized maximized flag
         * @return builder
         */
        MessageDialogBuilder withMaximized(boolean maximized);
        /**
         * Enables dialog maximized mode.
         *
         * @return builder
         */
        MessageDialogBuilder maximized();

        /**
         * @return true if window can be closed by click outside of window content (by modality curtain)
         */
        boolean isCloseOnClickOutside();
        /**
         * Sets if window can be closed by click outside of window content (by modality curtain).
         *
         * @param closeOnClickOutside true if window to be closed by click outside of window content (by modality curtain)
         * @return builder
         */
        MessageDialogBuilder withCloseOnClickOutside(boolean closeOnClickOutside);
        /**
         * Enables closeOnClickOutside mode for window, so window can be closed by click outside of window content
         * (by modality curtain).
         *
         * @return builder
         */
        MessageDialogBuilder closeOnClickOutside();

        /**
         * Sets custom CSS style name for dialog.
         *
         * @param styleName style name
         * @return builder
         */
        MessageDialogBuilder withStyleName(String styleName);
        /**
         * @return custom style name
         */
        String getStyleName();

        /**
         * Shows the dialog.
         */
        void show();
    }

    /**
     * Builder of unhandled exception dialog.
     */
    interface ExceptionDialogBuilder {
        /**
         * Sets exception object.
         *
         * @param throwable throwable
         * @return builder
         */
        ExceptionDialogBuilder withThrowable(Throwable throwable);
        /**
         * @return throwable
         */
        Throwable getThrowable();

        /**
         * Sets caption text.
         *
         * @param caption caption text
         * @return builder
         */
        ExceptionDialogBuilder withCaption(String caption);
        /**
         * @return caption text
         */
        String getCaption();

        /**
         * Sets message text.
         *
         * @param message message text
         * @return builder
         */
        ExceptionDialogBuilder withMessage(String message);
        /**
         * @return message text
         */
        String getMessage();

        /**
         * Shows the dialog.
         */
        void show();
    }

    /**
     * Message type of a dialog.
     */
    enum MessageType {
        /**
         * Confirmation message.
         */
        CONFIRMATION,
        /**
         * Warning message.
         */
        WARNING
    }

    /**
     * Builder for dialogs with inputs.
     *
     */
    interface InputDialogBuilder {

        /**
         * Add input parameter to the dialog. Input parameter will be represented as a field.
         *
         * @param parameter input parameter that will be added to the dialog
         * @return builder
         */
        InputDialogBuilder withParameter(InputParameter parameter);

        /**
         * Sets input parameters.
         *
         * @param parameters input parameters
         * @return builder
         */
        InputDialogBuilder withParameters(InputParameter... parameters);

        Collection<InputParameter> getParameters();

        /**
         * Add close listener to the dialog.
         *
         * @param listener close listener to add
         * @return builder
         */
        InputDialogBuilder withCloseListener(Consumer<InputDialog.InputDialogCloseEvent> listener);

        Consumer<InputDialog.InputDialogCloseEvent> getCloseListener();

        /**
         * Sets dialog actions. If there is no actions are set input dialog will use {@link Dialogs.DialogActions#OK_CANCEL}.
         *
         * @param actions actions
         * @return builder
         * @see InputDialogAction
         */
        InputDialogBuilder withActions(InputDialogAction... actions);

        Collection<Action> getActions();

        /**
         * Sets predefined dialog actions. By default if there is no actions are set using {@link #withActions(InputDialogAction...)}
         * input dialog will use {@link Dialogs.DialogActions#OK_CANCEL}.
         *
         * @param actions actions
         * @return builder
         */
        InputDialogBuilder withActions(DialogActions actions);

        DialogActions getDialogActions();

        /**
         * Sets dialog screen caption
         *
         * @param caption dialog screen caption
         * @return builder
         */
        InputDialogBuilder withCaption(String caption);

        @Nullable
        String getCaption();

        /**
         * Shows the dialog.
         *
         * @return opened input dialog
         */
        InputDialog show();

        /**
         * Builds the input dialog.
         *
         * @return input dialog
         */
        InputDialog build();
    }

    enum DialogActions {
        OK,
        OK_CANCEL,
        YES_NO,
        YES_NO_CANCEL
    }
}
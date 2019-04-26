/*
 * Copyright (c) 2008-2016 Haulmont.
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
package com.haulmont.cuba.gui.components;

import com.haulmont.cuba.core.global.DataLoadContext;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import java.util.EventObject;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Component that makes a data binding to load data by pages. Usually used with {@link Table} or {@link DataGrid}.
 */
public interface RowsCount extends Component.BelongToFrame {

    enum State {
        FIRST_COMPLETE,     // "63 rows"
        FIRST_INCOMPLETE,   // "1-100 rows of [?] >"
        MIDDLE,             // "< 101-200 rows of [?] >"
        LAST                // "< 201-252 rows"
    }

    String NAME = "rowsCount";

    /**
     * @deprecated assign {@link RowsCountTarget} instead.
     */
    @Deprecated
    CollectionDatasource getDatasource();
    /**
     * @deprecated assign {@link RowsCountTarget} instead.
     */
    @Deprecated
    void setDatasource(CollectionDatasource datasource);

    /**
     * @return a component that displays data from the same datasource, usually a {@link Table}. Can be null.
     */
    @Deprecated
    ListComponent getOwner();
    /**
     * @deprecated Use {@link #setRowsCountTarget(RowsCountTarget)} instead.
     */
    @Deprecated
    void setOwner(ListComponent owner);

    /**
     * @return a component that displays data, usually a {@link Table}. Can be null.
     */
    RowsCountTarget getRowsCountTarget();
    void setRowsCountTarget(RowsCountTarget target);

    interface RowsCountTarget {
    }

    /**
     * @return delegate which is used to get the total number of rows when user clicks "total count" or "last page".
     */
    Function<DataLoadContext, Long> getTotalCountDelegate();

    /**
     * Sets delegate which is used to get the total number of rows when user clicks "total count" or "last page".
     */
    void setTotalCountDelegate(Function<DataLoadContext, Long> delegate);

    /**
     * Event that is fired before refreshing the datasource when the user clicks next, previous, etc.
     * <br>
     * You can prevent the datasource refresh by invoking {@link BeforeRefreshEvent#preventRefresh()},
     * for example:
     * <pre>{@code
     * table.getRowsCount().addBeforeDatasourceRefreshListener(event -> {
     *     if (event.getDatasource().isModified()) {
     *         showNotification("Save changes before going to another page");
     *         event.preventRefresh();
     *     }
     * });
     * }</pre>
     */
    class BeforeRefreshEvent extends EventObject {
        protected boolean refreshPrevented = false;

        public BeforeRefreshEvent(RowsCount source) {
            super(source);
        }

        /**
         * If invoked, the component will not refresh the datasource.
         */
        public void preventRefresh() {
            refreshPrevented = true;
        }

        public boolean isRefreshPrevented() {
            return refreshPrevented;
        }
    }

    void addBeforeRefreshListener(Consumer<BeforeRefreshEvent> listener);
    void removeBeforeRefreshListener(Consumer<BeforeRefreshEvent> listener);

    /**
     * A listener to be notified before refreshing the datasource when the user clicks next, previous, etc.
     *
     * @deprecated Use {@link Consumer} with {@link BeforeRefreshEvent} instead.
     */
    @FunctionalInterface
    @Deprecated
    interface BeforeRefreshListener extends Consumer<BeforeRefreshEvent> {
        void beforeDatasourceRefresh(BeforeRefreshEvent event);

        @Override
        default void accept(BeforeRefreshEvent beforeRefreshEvent) {
            beforeDatasourceRefresh(beforeRefreshEvent);
        }
    }
}
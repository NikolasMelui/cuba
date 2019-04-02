/*
 * Copyright (c) 2008-2019 Haulmont.
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
 */

package com.haulmont.cuba.core.sys.environmentcheck;

import java.util.ArrayList;
import java.util.List;

public class JvmCheck implements EnvironmentCheck {

    @Override
    public List<CheckFailedResult> doCheck() {
        String javaVersion = System.getProperty("java.runtime.version");

        String[] javaVersionElements = javaVersion.split("\\.|_|-b|\\+");

        List<CheckFailedResult> result = new ArrayList<>();
        if ("1".equals(javaVersionElements[0]) && Integer.valueOf(javaVersionElements[1]) < 8) {
            result.add(new CheckFailedResult(
                    String.format("Unsupported java version detected: %s; Cuba supports java 8 and higher.", javaVersion),
                    null));
        }
        return result;
    }
}
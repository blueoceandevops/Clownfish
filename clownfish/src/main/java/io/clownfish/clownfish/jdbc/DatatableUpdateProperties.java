/*
 * Copyright 2019 sulzbachr.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.clownfish.clownfish.jdbc;

import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author sulzbachr
 */
public class DatatableUpdateProperties {
    private @Getter @Setter String tablename;
    private @Getter @Setter ArrayList<DatatableNewValue> valuelist;
    private @Getter @Setter ArrayList<DatatableCondition> conditionlist;

    public DatatableUpdateProperties() {
        valuelist = new ArrayList<>();
        conditionlist = new ArrayList<>();
    }

    public DatatableUpdateProperties(String tablename) {
        this.tablename = tablename;
        valuelist = new ArrayList<>();
        conditionlist = new ArrayList<>();
    }
}

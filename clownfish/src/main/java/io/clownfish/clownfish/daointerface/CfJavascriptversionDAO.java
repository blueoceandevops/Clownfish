/*
 * Copyright 2019 rawdog.
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
package io.clownfish.clownfish.daointerface;

import io.clownfish.clownfish.dbentities.CfJavascriptversion;
import java.util.List;

/**
 *
 * @author rawdog
 */
public interface CfJavascriptversionDAO {
    List<CfJavascriptversion> findByJavascriptref(long ref);
    long findMaxVersion(long ref);
    CfJavascriptversion findByPK(long ref, long version);
    List<CfJavascriptversion> findAll();
    boolean create(CfJavascriptversion entity);
    boolean delete(CfJavascriptversion entity);
    boolean edit(CfJavascriptversion entity);
}
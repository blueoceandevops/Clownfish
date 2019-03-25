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
package io.clownfish.clownfish.serviceimpl;

import io.clownfish.clownfish.daointerface.CfListDAO;
import io.clownfish.clownfish.daointerface.CfListcontentDAO;
import io.clownfish.clownfish.dbentities.CfList;
import io.clownfish.clownfish.dbentities.CfListcontent;
import io.clownfish.clownfish.serviceinterface.CfListService;
import io.clownfish.clownfish.serviceinterface.CfListcontentService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author sulzbachr
 */
@Service
@Transactional
public class CfListcontentServiceImpl implements CfListcontentService {
    private final CfListcontentDAO cflistcontentDAO;
    
    @Autowired
    public CfListcontentServiceImpl(CfListcontentDAO cflistcontentDAO) {
        this.cflistcontentDAO = cflistcontentDAO;
    }

    @Override
    public List<CfListcontent> findAll() {
        return this.cflistcontentDAO.findAll();
    }

    @Override
    public List<CfListcontent> findByListref(long listref) {
        return this.cflistcontentDAO.findByListref(listref);
    }

    @Override
    public List<CfListcontent> findByClasscontentref(long classcontentref) {
        return this.cflistcontentDAO.findByClasscontentref(classcontentref);
    }

}

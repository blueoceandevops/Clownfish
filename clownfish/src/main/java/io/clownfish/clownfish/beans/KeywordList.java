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
package io.clownfish.clownfish.beans;

import io.clownfish.clownfish.dbentities.CfKeyword;
import io.clownfish.clownfish.serviceinterface.CfKeywordService;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolationException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author sulzbachr
 */
@Named("keywordList")
@Scope("session")
@Component
public class KeywordList {
    @Autowired CfKeywordService cfkeywordService;
    @Autowired PropertyList propertylist;
    @Autowired AssetList assetlist;
    
    private static Map<String, String> propertymap = null;
    
    private @Getter @Setter List<CfKeyword> keywordlist;
    private @Getter @Setter List<String> items;
    private @Getter @Setter CfKeyword selectedKeyword;
    private @Getter @Setter List<CfKeyword> filteredKeyword;
    
    @PostConstruct
    public void init() {
        keywordlist = cfkeywordService.findAll();
        
        if (propertymap == null) {
            // read all System Properties of the property table
            propertymap = propertylist.fillPropertyMap();
        }
    }

    public void onCreate(ActionEvent actionEvent) {
        try {
            for (String keyword : items) {
                try {
                    CfKeyword testkeyword = cfkeywordService.findByName(keyword);
                } catch (NoResultException ex) {
                    CfKeyword newkeyword = new CfKeyword();
                    newkeyword.setName(keyword);
                    cfkeywordService.create(newkeyword);
                }
            }
            keywordlist = cfkeywordService.findAll();
            assetlist.init();
        } catch (ConstraintViolationException ex) {
            System.out.println(ex.getMessage());
        }
    }
}

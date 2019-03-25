/*
 * Copyright Rainer Sulzbach
 */
package io.clownfish.clownfish.utils;

import io.clownfish.clownfish.dbentities.CfAttribut;
import io.clownfish.clownfish.dbentities.CfAttributcontent;
import io.clownfish.clownfish.dbentities.CfAttributetype;
import io.clownfish.clownfish.dbentities.CfClasscontent;
import io.clownfish.clownfish.serviceinterface.CfAttributService;
import io.clownfish.clownfish.serviceinterface.CfAttributcontentService;
import io.clownfish.clownfish.serviceinterface.CfAttributetypeService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author rawdog
 */
public class ClassUtil {
    @Autowired CfAttributService cfattributService;
    @Autowired CfAttributetypeService cfattributetypeService;
    @Autowired CfAttributcontentService cfattributcontentService;
    
    public ClassUtil() {
    }
    
    public Map getattributmap (CfClasscontent classcontent) {
        List<CfAttributcontent> attributcontentlist = new ArrayList<>();
        //attributcontentlist.addAll(em.createNamedQuery("Knattributcontent.findByClasscontentref").setParameter("classcontentref", classcontent).getResultList());
        attributcontentlist.addAll(cfattributcontentService.findByClasscontentref(classcontent));
        
        Map attributcontentmap = new LinkedHashMap();

        for (CfAttributcontent attributcontent : attributcontentlist) {
            //CfAttribut cfattribut = (CfAttribut) em.createNamedQuery("Knattribut.findById").setParameter("id", attributcontent.getAttributref().getId()).getSingleResult();
            CfAttribut cfattribut = cfattributService.findById(attributcontent.getAttributref().getId());
            //Knattributetype knattributtype = (Knattributetype) em.createNamedQuery("Knattributetype.findById").setParameter("id", knattribut.getAttributetype().getId()).getSingleResult();
            CfAttributetype cfattributtype = cfattributetypeService.findById(cfattribut.getAttributetype().getId());
            switch (cfattributtype.getName()) {
                case "boolean":
                    attributcontentmap.put(cfattribut.getName(), attributcontent.getContentBoolean());
                    break;
                case "string":
                    attributcontentmap.put(cfattribut.getName(), attributcontent.getContentString());
                    break;
                case "hashstring":
                    attributcontentmap.put(cfattribut.getName(), attributcontent.getContentString());
                    break;    
                case "integer":
                    attributcontentmap.put(cfattribut.getName(), attributcontent.getContentInteger());
                    break;
                case "real":
                    attributcontentmap.put(cfattribut.getName(), attributcontent.getContentReal());
                    break;
                case "htmltext":
                    attributcontentmap.put(cfattribut.getName(), attributcontent.getContentText());
                    break;
                case "datetime":
                    attributcontentmap.put(cfattribut.getName(), attributcontent.getContentDate());
                    break;
                case "media":
                    attributcontentmap.put(cfattribut.getName(), attributcontent.getContentInteger());
                    break;
                case "text":
                    attributcontentmap.put(cfattribut.getName(), attributcontent.getContentText());
                    break;    
            }
        }
        return attributcontentmap;
    }
}

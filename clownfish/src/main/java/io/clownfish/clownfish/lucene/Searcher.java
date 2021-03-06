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
package io.clownfish.clownfish.lucene;

import io.clownfish.clownfish.dbentities.CfAsset;
import io.clownfish.clownfish.dbentities.CfClass;
import io.clownfish.clownfish.dbentities.CfClasscontent;
import io.clownfish.clownfish.dbentities.CfListcontent;
import io.clownfish.clownfish.dbentities.CfSite;
import io.clownfish.clownfish.dbentities.CfSitecontent;
import io.clownfish.clownfish.dbentities.CfTemplate;
import io.clownfish.clownfish.serviceinterface.CfAssetService;
import io.clownfish.clownfish.serviceinterface.CfClassService;
import io.clownfish.clownfish.serviceinterface.CfClasscontentService;
import io.clownfish.clownfish.serviceinterface.CfListService;
import io.clownfish.clownfish.serviceinterface.CfListcontentService;
import io.clownfish.clownfish.serviceinterface.CfSiteService;
import io.clownfish.clownfish.serviceinterface.CfSitecontentService;
import io.clownfish.clownfish.serviceinterface.CfSitelistService;
import io.clownfish.clownfish.serviceinterface.CfTemplateService;
import io.clownfish.clownfish.utils.ClassUtil;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.bean.ViewScoped;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author sulzbachr
 */
@ViewScoped
@Component
public class Searcher {
    IndexSearcher indexSearcher;
    MultiFieldQueryParser queryParser;
    Query query;
    ArrayList<CfSite> foundSites;
    ArrayList<CfAsset> foundAssets;
    HashMap<String, String> foundClasscontent;
    @Autowired CfSitecontentService sitecontentservice;
    @Autowired CfSiteService siteservice;
    @Autowired CfListcontentService sitelistservice;
    @Autowired CfListService cflistservice;
    @Autowired CfSitelistService cfsitelistservice;
    @Autowired CfAssetService cfassetservice;
    @Autowired CfClassService cfclassservice;
    @Autowired CfClasscontentService cfclasscontentservice;
    @Autowired CfTemplateService cftemplateservice;
    @Autowired ClassUtil classutil;
    
    final transient Logger logger = LoggerFactory.getLogger(Searcher.class);

    public Searcher() {
        foundSites = new ArrayList<>();
        foundAssets = new ArrayList<>();
        foundClasscontent = new HashMap<>();
    }
   
    public void setIndexPath(String indexDirectoryPath) {
        try {
            Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
            IndexReader reader = DirectoryReader.open(indexDirectory);
            indexSearcher = new IndexSearcher(reader);
            queryParser = new MultiFieldQueryParser(new String[] {LuceneConstants.CONTENT_TEXT, LuceneConstants.CONTENT_STRING, LuceneConstants.ASSET_NAME, LuceneConstants.ASSET_TEXT, LuceneConstants.ASSET_DESCRIPTION}, new StandardAnalyzer());
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Searcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public SearchResult search(String searchQuery, int searchlimit) throws IOException, ParseException {
        SearchResult searchresult = new SearchResult();
        foundSites.clear();
        foundAssets.clear();
        query = queryParser.parse(searchQuery);
        TopDocs hits = indexSearcher.search(query, searchlimit);
        foundClasscontent.clear();
        HashMap searchclasscontentmap = new HashMap<String, ArrayList>();       
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = getDocument(scoreDoc);
            String contenttype = doc.get(LuceneConstants.CONTENT_TYPE);           
            if (0 == contenttype.compareToIgnoreCase("Clownfish/Content")) {
                long classcontentref = Long.parseLong(doc.get(LuceneConstants.CLASSCONTENT_REF));
                // Search directly in site
                List<CfSitecontent> sitelist = sitecontentservice.findByClasscontentref(classcontentref);
                sitelist.stream().map((sitecontent) -> siteservice.findById(sitecontent.getCfSitecontentPK().getSiteref())).filter((foundsite) -> ((!foundSites.contains(foundsite)) && (foundsite.isSearchrelevant()))).forEach((foundsite) -> {
                    foundSites.add(foundsite);
                });
                // Search in sitelists
                List<CfListcontent> listcontent = sitelistservice.findByClasscontentref(classcontentref);
                listcontent.stream().map((listcontententry) -> cflistservice.findById(listcontententry.getCfListcontentPK().getListref())).map((foundlist) -> cfsitelistservice.findByListref(foundlist.getId())).forEach((foundsitelist) -> {
                    foundsitelist.stream().map((sitelistentry) -> siteservice.findById(sitelistentry.getCfSitelistPK().getSiteref())).filter((foundsite) -> ((!foundSites.contains(foundsite)) && (foundsite.isSearchrelevant()))).forEach((foundsite) -> {
                        foundSites.add(foundsite);
                    });
                });
                // Search in classes and put it via template to the output
                CfClasscontent findclasscontent = cfclasscontentservice.findById(classcontentref);
                CfClass findclass = cfclassservice.findById(findclasscontent.getClassref().getId());
                
                if (findclass.isSearchrelevant()) {
                    Map attributmap = classutil.getattributmap(findclasscontent);
                    if (searchclasscontentmap.containsKey(findclass.getName())) {
                        ArrayList searchclassarray = (ArrayList) searchclasscontentmap.get(findclass.getName());
                        if (!searchclassarray.contains(attributmap)) {
                            searchclassarray.add(attributmap);
                            searchclasscontentmap.put(findclass.getName(), searchclassarray);
                        }
                    } else {
                        ArrayList searchclassarray = new ArrayList<Map>();
                        searchclassarray.add(attributmap);
                        searchclasscontentmap.put(findclass.getName(), searchclassarray);
                    }
                }
            } else {
                try {
                    String assetid = doc.getField(LuceneConstants.ID).stringValue();
                    CfAsset asset = cfassetservice.findById(Long.parseLong(assetid));
                    if (!foundAssets.contains(asset)) {
                        foundAssets.add(asset);
                    }
                } catch (Exception ex) {
                    logger.warn(ex.getMessage());
                }
            }
        }
        searchresult.foundSites = foundSites;
        searchresult.foundAssets = foundAssets;
        searchresult.foundClasscontent = searchclasscontentmap;
        return searchresult;
    }
    
    public Document getDocument(ScoreDoc scoreDoc) throws CorruptIndexException, IOException {
        return indexSearcher.doc(scoreDoc.doc);  
    }
}

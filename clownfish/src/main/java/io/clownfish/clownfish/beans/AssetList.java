/*
 * Copyright Rainer Sulzbach
 */
package io.clownfish.clownfish.beans;

import io.clownfish.clownfish.dbentities.CfAsset;
import io.clownfish.clownfish.dbentities.CfKeyword;
import io.clownfish.clownfish.serviceinterface.CfAssetService;
import io.clownfish.clownfish.serviceinterface.CfKeywordService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

/**
 *
 * @author sulzbachr
 */
@Named("assetList")
@ViewScoped
@Component
public class AssetList {
    @Autowired CfAssetService cfassetService;
    @Autowired CfKeywordService cfkeywordService;
    @Autowired PropertyList propertylist;
    
    private static Map<String, String> propertymap = null;
    private @Getter @Setter List<CfAsset> assetlist;
    private @Getter @Setter Boolean selectedAsset;
    private @Getter @Setter String assetName;
    private @Getter @Setter DualListModel<CfKeyword> keywords;

    @PostConstruct
    public void init() {
        assetName = "";
        assetlist = cfassetService.findAll();
        
        List<CfKeyword> keywordSource = cfkeywordService.findAll();
        List<CfKeyword> keywordTarget = new ArrayList<>();
        
        keywords = new DualListModel<>(keywordSource, keywordTarget);
        
        if (propertymap == null) {
            // read all System Properties of the property table
            propertymap = propertylist.fillPropertyMap();
        }
    }
    
    public void handleFileUpload(FileUploadEvent event) throws TikaException, SAXException {
        Logger logger = Logger.getLogger(getClass().getName());
        logger.log(Level.INFO, "UPLOAD: {0}", event.getFile().getFileName());
        String mediapath = propertymap.get("media.folder");
        HashMap<String, String> metamap = new HashMap<>();
        try {
            File result = new File(mediapath + File.separator + event.getFile().getFileName());
            InputStream inputStream;
            try (FileOutputStream fileOutputStream = new FileOutputStream(result)) {
                byte[] buffer = new byte[64535];
                int bulk;
                inputStream = event.getFile().getInputstream();
                while (true) {
                    bulk = inputStream.read(buffer);
                    if (bulk < 0) {
                        break;
                    }
                    fileOutputStream.write(buffer, 0, bulk);
                    fileOutputStream.flush();
                }
            }
            inputStream.close();
            
            //detecting the file type using detect method
            String fileextension = FilenameUtils.getExtension(mediapath + File.separator + event.getFile().getFileName());
            
            Parser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            FileInputStream inputstream = new FileInputStream(result);
            ParseContext context = new ParseContext();

            parser.parse(inputstream, handler, metadata, context);
            //System.out.println(handler.toString());

            //getting the list of all meta data elements 
            String[] metadataNames = metadata.names();
            for(String name : metadataNames) {		        
                //System.out.println(name + ": " + metadata.get(name));
                metamap.put(name, metadata.get(name));
            }
            
            CfAsset newasset = new CfAsset();
            newasset.setName(event.getFile().getFileName());
            newasset.setFileextension(fileextension.toLowerCase());
            newasset.setMimetype(metamap.get("Content-Type"));
            newasset.setImagewidth(metamap.get("Image Width"));
            newasset.setImageheight(metamap.get("Image Height"));
            cfassetService.create(newasset);
            assetlist = cfassetService.findAll();
            assetName = "";
            
            FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (IOException e) {
            Logger.getLogger(AssetList.class.getName()).log(Level.SEVERE, null, e);
            FacesMessage error = new FacesMessage("The files were not uploaded!");
            FacesContext.getCurrentInstance().addMessage(null, error);
        } 
    }
 
    public void onAttach(ActionEvent actionEvent) {
        List<CfKeyword> selectedkeyword = keywords.getTarget();
        try {
            for (Object keyword : selectedkeyword) {
                System.out.println(keyword.toString());
            }
        } catch (ConstraintViolationException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
}
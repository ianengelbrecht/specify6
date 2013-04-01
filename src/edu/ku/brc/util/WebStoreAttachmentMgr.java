/* Copyright (C) 2009, University of Kansas Center for Research
 * 
 * Specify Software Project, specify@ku.edu, Biodiversity Institute,
 * 1345 Jayhawk Boulevard, Lawrence, Kansas, 66045, USA
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package edu.ku.brc.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Element;

import edu.ku.brc.af.core.AppContextMgr;
import edu.ku.brc.helpers.XMLHelper;
import edu.ku.brc.specify.conversion.BasicSQLUtils;
import edu.ku.brc.specify.datamodel.Attachment;
import edu.ku.brc.specify.datamodel.Collection;
import edu.ku.brc.specify.datamodel.Discipline;
import edu.ku.brc.specify.datamodel.Division;
import edu.ku.brc.specify.datamodel.Institution;
import edu.ku.brc.ui.IconEntry;
import edu.ku.brc.ui.IconManager;
import edu.ku.brc.ui.UIRegistry;
import edu.ku.brc.util.thumbnails.Thumbnailer;

/**
 * @author rods
 *
 * @code_status Alpha
 *
 * Nov 1, 2011
 *
 */
public final class WebStoreAttachmentMgr implements AttachmentManagerIface
{
    private static final Logger  log   = Logger.getLogger(WebStoreAttachmentMgr.class);
    private static final String DEFAULT_URL    = "http://specifyassets.nhm.ku.edu/Informatics/getmetadata.php?dt=<dt>&type=<type>&filename=<fname>&coll=<coll>&disp=<disp>&div=<div>&inst=<inst>";
    private static final String ATTACHMENT_URL = "SELECT AttachmentLocation FROM attachment WHERE AttachmentID = ";
    private static MessageDigest sha1 = null;

    
    private Boolean                 isInitialized      = null;
    private byte[]                  bytes              = new byte[100*1024];
    private File                    cacheDir; 
    private FileCache               shortTermCache;
    private HashMap<String, String> attachNameThumbMap = new HashMap<String, String>();
    private HashMap<String, String> attachNameOrigMap  = new HashMap<String, String>();
    private SimpleDateFormat        dateFormat         = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
    
    // URLs
    private String                  readURLStr    = null;
    private String                  writeURLStr   = null;
    private String                  delURLStr     = null;
    private String                  fileGetURLStr = null;
    private String                  fileGetMetaDataURLStr = null;
    
    private String[]                symbols        = {"<coll>", "<disp>", "<div>", "<inst>"};
    private String[]                values  = new String[symbols.length];
    
    static
    {
        try
        {
            sha1 = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * 
     */
    public WebStoreAttachmentMgr()
    {
        super();

    }

    /* (non-Javadoc)
     * @see edu.ku.brc.util.AttachmentManagerIface#isInitialized()
     */
    @Override
    public boolean isInitialized(final String urlStr)
    {
        if (isInitialized == null)
        {
            isInitialized = false;
            
            cacheDir = new File(UIRegistry.getAppDataDir() + File.separator + "attach_cache");
            if (!cacheDir.exists())
            {
                if (!cacheDir.mkdir())
                {
                    cacheDir = null;
                    return isInitialized;
                }
            }
                
            try
            {
                shortTermCache = new FileCache(cacheDir.getAbsolutePath(), "cache.map");
                shortTermCache.setSuffix("");
                shortTermCache.setUsingExtensions(true);
                
                return isInitialized = getURLSetupXML(urlStr);
                
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return isInitialized;
    }
    
    /**
     * @return
     */
    /*private File getConfigFile()
    {
        return new File(UIRegistry.getAppDataDir() + File.separator + "web_asset_store.xml");
    }*/
    
    /**
     * @return
     */
    private boolean getURLSetupXML(final String urlStr)
    {
        try
        {
            if (StringUtils.isNotEmpty(urlStr))
            {
                File tmpFile = File.createTempFile("sp6", ".xml", cacheDir.getAbsoluteFile());
                if (fillFileFromWeb(urlStr, tmpFile))
                {
                    if (getURLSFromFile(tmpFile))
                    {
                        tmpFile.delete();
                        return true;
                    }
                    tmpFile.delete();
                }
            } 
            
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * @param webAssetFile
     * @return
     */
    private boolean getURLSFromFile(final File webAssetFile)
    {
        try
        {
            Element root = XMLHelper.readFileToDOM4J(webAssetFile);
            if (root != null)
            {
                for (Iterator<?> i = root.elementIterator("url"); i.hasNext();) //$NON-NLS-1$
                {
                    Element urlNode = (Element) i.next();
                    String  type    = urlNode.attributeValue("type"); //$NON-NLS-1$
                    String  urlStr  = urlNode.getTextTrim();
                    
                    if (type.equals("read"))
                    {
                        readURLStr = urlStr;
                        
                    } else if (type.equals("write"))
                    {
                        writeURLStr = urlStr;
                        
                    } else if (type.equals("delete"))
                    {
                        delURLStr = urlStr;
                        
                    } else if (type.equals("fileget"))
                    {
                        fileGetURLStr = urlStr;
                        
                    } else if (type.equals("getmetadata"))
                    {
                        fileGetMetaDataURLStr = urlStr;
                    }
                }
            }
            return StringUtils.isNotEmpty(readURLStr) && StringUtils.isNotEmpty(writeURLStr) && StringUtils.isNotEmpty(delURLStr);
            
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.util.AttachmentManagerIface#setStorageLocationIntoAttachment(edu.ku.brc.specify.datamodel.Attachment, boolean)
     */
    @Override
    public boolean setStorageLocationIntoAttachment(Attachment attachment, boolean doDisplayErrors)
    {
        String attName    = attachment.getOrigFilename();
        int    lastPeriod = attName.lastIndexOf('.');
        String suffix     = ".att";
        
        if (lastPeriod != -1)
        {
            // Make sure the file extension (if any) remains the same so the host
            // filesystem still sees the files as the proper types.  This is simply
            // to make the files browsable from a system file browser.
            suffix = ".att" + attName.substring(lastPeriod);
        }
        
        String errMsg          = null;
        String storageFilename = "";
        try
        {
            File storageFile = File.createTempFile("sp6", suffix, cacheDir.getAbsoluteFile());
            //System.err.println("["+storageFile.getAbsolutePath()+"] "+storageFile.canWrite());
            if (storageFile.exists())
            {
                attachment.setAttachmentLocation(storageFile.getName());
                storageFile.deleteOnExit();
                
                return true;
            }
            errMsg = UIRegistry.getLocalizedMessage("ATTCH_NOT_SAVED_REPOS", storageFile.getAbsolutePath());
            log.error("storageFile doesn't exist["+storageFile.getAbsolutePath()+"]");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            
            if (doDisplayErrors)
            {
                errMsg = UIRegistry.getLocalizedMessage("ATTCH_NOT_SAVED_REPOS", storageFilename);
            } else
            {
                // This happens when errors are not displayed.
                e.printStackTrace();
                edu.ku.brc.af.core.UsageTracker.incrHandledUsageCount();
                edu.ku.brc.exceptions.ExceptionTracker.getInstance().capture(FileStoreAttachmentManager.class, e);
            }
        }
        
        if (doDisplayErrors && errMsg != null)
        {
            UIRegistry.showError(errMsg);
        }
        return false;
    }
    
    /**
     * @param urlStr
     * @return
     */
    public String getURLDataAsString(final String urlStr)
    {
        try
        {
            if (StringUtils.isNotEmpty(urlStr))
            {
                URL         url       = new URL(urlStr);
                InputStream inpStream = url.openStream();
                if (inpStream != null)
                {
                    StringBuilder dataStr = new StringBuilder();
                    BufferedInputStream  in  = new BufferedInputStream(inpStream);
                    do
                    {
                        int numBytes = in.read(bytes);
                        if (numBytes == -1)
                        {
                            break;
                        }
                        if (numBytes > 0)
                        {
                            String data = new String(bytes);
                            dataStr.append(data);
                        }
                        
                    } while(true);
                    in.close();
                
                    //System.out.println(dataStr.toString());
                    return dataStr.toString();
                }
            }
            
        } catch (IOException ex)
        {
            log.error(ex.getMessage());
        }

        return null;
    }


    /* (non-Javadoc)
     * @see edu.ku.brc.util.AttachmentManagerIface#getFileEmbddedDate(int)
     */
    @Override
    public Calendar getFileEmbddedDate(int attachmentID)
    {
        String fileName = BasicSQLUtils.querySingleObj(ATTACHMENT_URL + attachmentID);
        if (StringUtils.isNotEmpty(fileName))
        {
            String metaDataURLStr = StringUtils.isNotEmpty(fileGetMetaDataURLStr) ? fileGetMetaDataURLStr :  DEFAULT_URL;
            
            String urlStr  = subAllExtraData(metaDataURLStr, fileName, false, null, "date");
            String dateStr = getURLDataAsString(urlStr);
            
            if (dateStr != null && dateStr.length() == 10)
            {
                try
                {
                    Date convertedDate = dateFormat.parse(dateStr);
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(convertedDate.getTime());
                    return cal;
                    
                } catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }
        }
            
        return null;
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.util.AttachmentManagerIface#getMetaDataAsJSON(int)
     */
    @Override
    public String getMetaDataAsJSON(int attachmentID)
    {
        String fileName = BasicSQLUtils.querySingleObj(ATTACHMENT_URL + attachmentID);
        if (StringUtils.isNotEmpty(fileName))
        {
            String metaDataURLStr = StringUtils.isNotEmpty(fileGetMetaDataURLStr) ? fileGetMetaDataURLStr : DEFAULT_URL;
            
            String urlStr  = subAllExtraData(metaDataURLStr, fileName, false, null, "json");
            String jsonStr = getURLDataAsString(urlStr);
        
            //System.out.println(jsonStr);
            return jsonStr;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.util.AttachmentManagerIface#getOriginal(edu.ku.brc.specify.datamodel.Attachment)
     */
    @Override
    public synchronized File getOriginal(Attachment attachment)
    {
        return getFile(attachment, attachNameOrigMap, false);
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.util.AttachmentManagerIface#getOriginal(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public File getOriginal(String attachLoc, String originalLoc, String mimeType)
    {
        return getFile(attachLoc, originalLoc, mimeType, attachNameOrigMap, false, null);
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.util.AttachmentManagerIface#getOriginalScaled(java.lang.String, java.lang.String, java.lang.String, int)
     */
    @Override
    public File getOriginalScaled(String attachLoc,
                                  String originalLoc,
                                  String mimeType,
                                  int maxSideInPixels)
    {
        return getFile(attachLoc, originalLoc, mimeType, attachNameOrigMap, false, maxSideInPixels);
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.util.AttachmentManagerIface#getThumbnail(edu.ku.brc.specify.datamodel.Attachment)
     */
    @Override
    public synchronized File getThumbnail(final Attachment attachment)
    {
        return getFile(attachment, attachNameThumbMap, true);
    }
    
    /* (non-Javadoc)
     * @see edu.ku.brc.util.AttachmentManagerIface#getThumbnail(edu.ku.brc.specify.datamodel.Attachment)
     */
    @Override
    public synchronized File getThumbnail(final String attachLoc, final String mimeType)
    {
        return getFile(attachLoc, null, mimeType, attachNameThumbMap, true, null);
    }
    
    /**
     * @param fileName
     * @param doDelOnExit
     * @return
     * @throws IOException 
     */
    private File createTempFile(final String fileName, final boolean doDelOnExit) throws IOException
    {
        String fileExt = FilenameUtils.getExtension(fileName);
        File file = File.createTempFile("sp6", '.' + fileExt, cacheDir.getAbsoluteFile());
        if (doDelOnExit)
        {
            file.deleteOnExit();
        }
        return file;
    }

    /**
     * @param attachment
     * @param nameHash
     * @param isThumb
     * @return
     */
    private synchronized File getFile(final Attachment a, 
                                      final HashMap<String, String> nameHash, 
                                      final boolean isThumb)
    {
        return getFile(a.getAttachmentLocation(), a.getOrigFilename(), a.getMimeType(), nameHash, isThumb, null);
    }
    
    /**
     * @param iconName
     * @return
     */
    private File getFileForIconName(final String iconName)
    {
        IconEntry entry = IconManager.getIconEntryByName(iconName);
        if (entry != null)
        {
            try
            {
                return new File(entry.getUrl().toURI());
            } catch (URISyntaxException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @param attachLocation
     * @param originalLoc
     * @param mimeType
     * @param nameHash
     * @param isThumb
     * @param scale
     * @return
     */
    private synchronized File getFile(final String attachLocation,
                                      final String originalLoc,
                                      final String mimeType,
                                      final HashMap<String, String> nameHash, 
                                      final boolean isThumb,
                                      final Integer scale)
    {
        shortTermCache.clear();
        
        String nmExt = isThumb ? ".THB" : "";
        
        String attachLoc = attachLocation;
        if (StringUtils.isNotEmpty(attachLoc) && scale != null)
        {
            attachLoc = getScaledFileName(attachLocation, scale);
        }
        
        boolean isSaved = StringUtils.isNotEmpty(attachLoc);
        boolean hasOrig = StringUtils.isNotEmpty(originalLoc);
        
        // Check to see if it is cached by original name
        if (!isSaved && hasOrig)
        {
            String localThumbName = nameHash.get(originalLoc+nmExt);
            if (StringUtils.isNotEmpty(localThumbName))
            {
                File cachedFile = shortTermCache.getCacheFile(localThumbName);
                if (cachedFile != null && cachedFile.exists())
                {
                    return cachedFile;
                }
            }
        }
        
        // Now check to see if it is cached by the saved name.
        String origFilePath = attachLoc;
        if (isSaved)
        {
            String fileName = nameHash.get(origFilePath+nmExt);
            if (StringUtils.isNotEmpty(fileName))
            {
                File cachedFile = shortTermCache.getCacheFile(fileName);
                if (cachedFile != null && cachedFile.exists())
                {
                    return cachedFile;
                }
            }
            
            File cachedFile = shortTermCache.getCacheFile(origFilePath);
            if (cachedFile != null && cachedFile.exists())
            {
                return cachedFile;
            }
            
            Boolean isNotImage = mimeType == null || !mimeType.startsWith("image/");
            if (isNotImage)
            {
                if (Thumbnailer.getInstance().hasGeneratorForMimeType(mimeType))
                {
                    Thumbnailer tn = Thumbnailer.getInstance();
                    try
                    {
                        File localFile = getFileFromWeb(attachLocation, mimeType, false, null);
                        String path    = localFile.getAbsolutePath();
                        tn.generateThumbnail(path, path, false);
                        return localFile;
                        
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                
                String iconName = Thumbnailer.getIconNameFromExtension(FilenameUtils.getExtension(attachLocation.toLowerCase()));
                if (iconName != null)
                {
                    File iconFile = getFileForIconName(iconName);
                    if (iconFile != null)
                    {
                        return iconFile;
                    }
                } else
                {
                    //System.out.println("Unknown handled mime type: "+mimeType);
                }
                return getFileForIconName("unknown");
            }

            // Not in the cache by either name, so go get the file form the server
            //System.out.println(String.format("[%s]  Mime: %s,  isThmb: %s, Scale: %d", attachLocation, mimeType, isThumb?"Y":"N", scale != null?scale:-1));
            File thmbFile = getFileFromWeb(attachLocation, mimeType, isThumb, scale);
            //System.out.println(thmbFile.getAbsolutePath());
            
            if (thmbFile != null && thmbFile.exists())
            {
                try
                {
                    // cache it
                    String nm = thmbFile.getName();
                    shortTermCache.cacheFile(thmbFile);
                    nameHash.put(attachLoc + nmExt, nm);
                    //thmbFile.delete();
                    
                    //thmbFile = shortTermCache.getCacheFile(nm);
                    
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            return thmbFile;
            
        } 
        
        // ok, it isn't saved yet, make sure original isn't null
        
        if (!hasOrig)
        {
            return null;
        }
        origFilePath = originalLoc;
        
        // Now make a thumb from the original
        File origFile  = new File(origFilePath);
        File thumbFile = null;
        
        try
        {
            String fileExt = FilenameUtils.getExtension(origFilePath);
            thumbFile = createTempFile(fileExt, false);
            
            // Now generate the thumbnail 
            Thumbnailer thumbnailGen   = AttachmentUtils.getThumbnailer();
            thumbnailGen.generateThumbnail(origFile.getAbsolutePath(), 
                                           thumbFile.getAbsolutePath(),
                                           false);
            if (!thumbFile.exists())
            {
                return null;
            }
            
            // Put mapping cache
            nameHash.put(origFilePath+nmExt, thumbFile.getName());
            
            // Put into cache with original name
            shortTermCache.cacheFile(thumbFile);
            
            thumbFile.delete();
            
            return thumbFile;
            
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * @param urlStr
     * @param tmpFile
     * @return
     * @throws IOException
     */
    private boolean fillFileFromWeb(final String urlStr, final File tmpFile)
    {
        try
        {
            URL url = new URL(urlStr);
            InputStream inpStream = url.openStream();
            if (inpStream != null)
            {
                BufferedInputStream  in  = new BufferedInputStream(inpStream);
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmpFile));
                
                do
                {
                    int numBytes = in.read(bytes);
                    if (numBytes == -1)
                    {
                        break;
                    }
                    bos.write(bytes, 0, numBytes);
                    
                } while(true);
                in.close();
                bos.close();
            
                return true;
            }
            
        } catch (IOException ex)
        {
            log.error(ex.getMessage());
            //ex.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * @param fileName
     * @param mimeType
     * @param isThumb
     * @return
     */
    private synchronized File getFileFromWeb(final String fileName, final String mimeType, final boolean isThumb, final Integer scale)
    {
        try
        {
            File tmpFile = createTempFile(fileName, false);
            
            //String urlStr = String.format("http://localhost/cgi-bin/fileget.php?type=%s&filename=%s&mimeType=%s;disp=%s", 
            //                  isThumb ? "thumbs" : "originals", fileName, StringUtils.isNotEmpty(mimeType) ? mimeType : "",
            //                  discipline.getName());
            
            String urlStr = subAllExtraData(readURLStr, fileName, isThumb, scale, null);
            
            log.debug("["+urlStr+"]");
            return fillFileFromWeb(urlStr, tmpFile) ? tmpFile : null;
            
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.util.AttachmentManagerIface#storeAttachmentFile(edu.ku.brc.specify.datamodel.Attachment, java.io.File, java.io.File)
     */
    @Override
    public void storeAttachmentFile(final Attachment attachment, final File attachmentFile, final File thumbnail) throws IOException
    {
        
        if (sendFile(attachmentFile, attachment.getAttachmentLocation(), false))
        {
            sendFile(thumbnail, attachment.getAttachmentLocation(), true);
        } else
        {
            throw new IOException(String.format("File [%s] was not saved on the server!", attachmentFile.getName()));
        }
    }
    
    /* (non-Javadoc)
     * @see edu.ku.brc.util.AttachmentManagerIface#setThumbSize(int)
     */
    @Override
    public void setThumbSize(int sizeInPixels)
    {
        
    }

    /**
     * @param urlStr
     * @param symbol
     * @param value
     */
    private String doSub(final String urlStr, final String symbol, final String value)
    {
        return StringUtils.replace(urlStr, symbol, value);
    }
    
    /**
     * 
     */
    private void fillValuesArray()
    {
        Collection  coll = AppContextMgr.getInstance().getClassObject(Collection.class);
        Discipline  disp = AppContextMgr.getInstance().getClassObject(Discipline.class);
        Division    div  = AppContextMgr.getInstance().getClassObject(Division.class);
        Institution inst = AppContextMgr.getInstance().getClassObject(Institution.class);
 
        values[0] = coll.getCollectionName();
        values[1] = disp.getName();
        values[2] = div.getName();
        values[3] = inst.getName();
        
        for (int i=0;i<values.length;i++)
        {
            values[i] = StringUtils.replace(values[i], " ", "%20");
        }
    }
    
    /**
     * @param urlStr
     */
    private String subAllExtraData(final String urlStr, 
                                   final String fileName, 
                                   final boolean isThumb,
                                   final Integer scale,
                                   final String datatype)
    {
        fillValuesArray(); // with current values
        
        String newURLStr = urlStr;
        for (int i=0;i<values.length;i++)
        {
            newURLStr = doSub(newURLStr, symbols[i], values[i]);
        }
        
        newURLStr = doSub(newURLStr, "<type>", isThumb ? "T" : "O");
        newURLStr = doSub(newURLStr, "<fname>", fileName);
        newURLStr = doSub(newURLStr, "<dt>", datatype);
        
        if (scale != null)
        {
            if (!newURLStr.endsWith("&"))
            {
                newURLStr += "&";
            }
            newURLStr += "scale=" + scale.toString();
        }
        return newURLStr;
    }
    
    /**
     * @param targetFile
     * @param fileName
     * @param isThumb
     * @return
     */
    private synchronized boolean sendFile(final File targetFile, 
                                          final String fileName, 
                                          final boolean isThumb)/*,
                                          final boolean saveInCache)*/
    {
        String targetURL = writeURLStr;//"http://localhost/cgi-bin/fileupload.php";
        PostMethod filePost = new PostMethod(targetURL);

        fillValuesArray();
        
        try
        {
            log.debug("Uploading " + targetFile.getName() + " to " + targetURL);
            
            String sha1Hash = calculateHash(targetFile);

            Part[] parts = {
                    new FilePart(targetFile.getName(), targetFile),
                    new StringPart("type", isThumb ? "T" : "O"),
                    new StringPart("store", fileName),
                    new StringPart("coll", values[0]),
                    new StringPart("hash", sha1Hash == null ? "" : sha1Hash),
                    //new StringPart("disp", values[1]),
                    //new StringPart("div",  values[2]),
                    //new StringPart("inst", values[3]),
                };

            filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
            HttpClient client = new HttpClient();
            client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);

            int status = client.executeMethod(filePost);
            
            //log.debug("---------------------------------------------------");
            log.debug(filePost.getResponseBodyAsString());
            //log.debug("---------------------------------------------------");

            if (status == HttpStatus.SC_OK)
            {
                return true;
            }
            
        } catch (Exception ex)
        {
            log.error("Error:  " + ex.getMessage());
            ex.printStackTrace();
            
        } finally
        {
            filePost.releaseConnection();
        }
        return false;
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.util.AttachmentManagerIface#replaceOriginal(edu.ku.brc.specify.datamodel.Attachment, java.io.File, java.io.File)
     */
    @Override
    public void replaceOriginal(Attachment attachment, File newOriginal, File newThumbnail) throws IOException
    {
        
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.util.AttachmentManagerIface#deleteAttachmentFiles(edu.ku.brc.specify.datamodel.Attachment)
     */
    @Override
    public void deleteAttachmentFiles(final Attachment attachment) throws IOException
    {
        String targetFileName = attachment.getAttachmentLocation();
        if (deleteFileFromWeb(targetFileName, false))
        {
            deleteFileFromWeb(targetFileName, true); // ok to fail deleting thumb
        } else
        {
            UIRegistry.showLocalizedError("ATTCH_NOT_DEL_REPOS", targetFileName);
        }
    }
    
    /**
     * @param fileName
     * @param scale
     * @return
     */
    private String getScaledFileName(final String fileName, final Integer scale)
    {
        String newPath = FilenameUtils.removeExtension(fileName);
        String ext     = FilenameUtils.getExtension(fileName);
        return String.format("%s_%d%s%s", newPath, scale, FilenameUtils.EXTENSION_SEPARATOR_STR, ext);
    }
    
    /**
     * @param fileName
     * @param isThumb
     * @return
     */
    private boolean deleteFileFromWeb(final String fileName, final boolean isThumb)
    {
        try
        {
            //String     targetURL  = String.format("http://localhost/cgi-bin/filedelete.php?filename=%s;disp=%s", targetName, discipline.getName());
            String     targetURL  = subAllExtraData(delURLStr, fileName, isThumb, null, null);
            GetMethod  getMethod  = new GetMethod(targetURL);

            //System.out.println("Deleting " + fileName + " from " + targetURL );

            HttpClient client = new HttpClient();
            client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);

            int status = client.executeMethod(getMethod);
            
            //System.out.println(getMethod.getResponseBodyAsString());

            return status == HttpStatus.SC_OK;
            
        } catch (Exception ex)
        {
            //System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.util.AttachmentManagerIface#regenerateThumbnail(edu.ku.brc.specify.datamodel.Attachment)
     */
    @Override
    public File regenerateThumbnail(final Attachment attachment) throws IOException
    {
        File thumbFile = null;
        
        boolean doLocalFile = false;
        
        String origFilePath = attachment.getAttachmentLocation();
        if (StringUtils.isEmpty(origFilePath))
        {
            doLocalFile = true;
            origFilePath = attachment.getOrigFilename();
            if (StringUtils.isEmpty(origFilePath))
            {
                return null;
            }
        }
        
        File origFile;
        if (doLocalFile)
        {
            origFile = new File(origFilePath);
            
        } else
        {
            origFile = getOriginal(attachment);
        }
        
        if (origFile != null)
        {
            thumbFile = createTempFile(origFile.getName(), true);
            
            Thumbnailer thumbnailGen   = AttachmentUtils.getThumbnailer();
            thumbnailGen.generateThumbnail(origFile.getAbsolutePath(), 
                                           thumbFile.getAbsolutePath(),
                                           false);
            if (thumbFile.exists())
            {
                if (!doLocalFile)
                {
                    sendFile(thumbFile, thumbFile.getName(), true);
                }
                
                try
                {
                    shortTermCache.cacheFile(thumbFile.getName(), thumbFile);
                    
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return thumbFile;
    }
    
    /**
     * @param algorithm
     * @param fileName
     * @return
     * @throws Exception
     */
    private String calculateHash(final File file) throws Exception
    {
        if (sha1 != null)
        {
            FileInputStream     fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            DigestInputStream   dis = new DigestInputStream(bis, sha1);
    
            // read the file and update the hash calculation
            while (dis.read() != -1)
                ;
    
            // get the hash value as byte array
            byte[] hash = sha1.digest();

            dis.close();
            return byteArray2Hex(hash);
        }
        return null;
    }

    /**
     * @param hash
     * @return
     */
    private String byteArray2Hex(byte[] hash)
    {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String s = formatter.toString();
        formatter.close();
        return s;
    }


    /* (non-Javadoc)
     * @see edu.ku.brc.util.AttachmentManagerIface#setDirectory(java.io.File)
     */
    @Override
    public void setDirectory(final File baseDir) throws IOException
    {
        
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.util.AttachmentManagerIface#getDirectory()
     */
    @Override
    public File getDirectory()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.util.AttachmentManagerIface#isDiskBased()
     */
    @Override
    public boolean isDiskBased()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.util.AttachmentManagerIface#getImageAttachmentURL()
     */
    @Override
    public String getImageAttachmentURL()
    {
        return fileGetURLStr;
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.util.AttachmentManagerIface#cleanup()
     */
    @Override
    public void cleanup()
    {
        // no op
    }
}

package edu.ku.brc.af.tasks;

import static edu.ku.brc.ui.UIRegistry.getResourceString;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import edu.ku.brc.af.core.SubPaneIFace;
import edu.ku.brc.af.core.ToolBarItemDesc;
import edu.ku.brc.af.core.UsageTracker;
import edu.ku.brc.af.core.expresssearch.QueryAdjusterForDomain;
import edu.ku.brc.af.prefs.AppPreferences;
import edu.ku.brc.helpers.SwingWorker;
import edu.ku.brc.specify.conversion.BasicSQLUtils;
import edu.ku.brc.util.Pair;

/**
 * This class sends usage stats.
 * 
 * @author jstewart, rods
 * 
 * @code_status Complete
 */
public class StatsTrackerTask extends BaseTask
{
    public  static final String           STATS_TRACKER   = "StatsTracker"; //$NON-NLS-1$
    
    private static boolean                sendAtStartup = false;
    
    protected StatsSwingWorker<?, ?> worker;

    /**
     * Constructor.
     */
    public StatsTrackerTask()
    {
        super(STATS_TRACKER, getResourceString(STATS_TRACKER));
    }
    
    /**
     * Cosntructor.
     * 
     * @param name the name of the task
     * @param title the user-viewable name of the task
     */
    public StatsTrackerTask(final String name, final String title)
    {
        super(name, title);
    }

    /**
     * @return the sendAtStartup
     */
    public static boolean isSendAtStartup()
    {
        return sendAtStartup;
    }

    /**
     * @param sendAtStartup the sendAtStartup to set
     */
    public static void setSendAtStartup(boolean sendAtStartup)
    {
        StatsTrackerTask.sendAtStartup = sendAtStartup;
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.af.tasks.BaseTask#initialize()
     */
    @Override
    public void initialize()
    {
        super.initialize();
        
        if (sendAtStartup)
        {
            sendAtStartUp();
        }
    }
    
    /**
     * Sends stats 5 seconds after start up.
     */
    protected void sendAtStartUp()
    {
        // setup a background task to check for udpates at startup and/or send usage stats at startup
        Timer timer = new Timer("StatTrackerThread", true); //$NON-NLS-1$
        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                // check for updates
                //    show popup if updates available
                //    don't show error popup if we can't contact the server
                //    send usage stats if allowed
                connectToServerNow(false);
            }
        };
        
        // see usage tracking is enabled
        boolean sendUsageStats  = usageTrackingIsAllowed();
        
        // if either feature is enabled, schedule the background task
        if (sendUsageStats)
        {
            timer.schedule(task, 5000);
        }
    }
    
    /**
     * @param localizedMsg
     * @return
     */
    protected void showClosingFrame()
    {
        // no op
    }
    
    protected PropertyChangeListener getPCLForWorker()
    {
        return null;
    }
    
    /**
     * @param doExit
     */
    public void sendStatsAtShutdown(final boolean doExit)
    {
        if (usageTrackingIsAllowed())
        {
            
            showClosingFrame();
            
            worker = new StatsSwingWorker<Object, Object>()
            {
                @Override
                protected Object doInBackground() throws Exception
                {
                    sendStats();
                    
                    return null;
                }
    
                @Override
                protected void done()
                {
                    super.done();
                    
                    if (doExit)
                    {
                        System.exit(0);
                    }
                }
                
            };
            
            PropertyChangeListener pcl = getPCLForWorker();
            if (pcl != null)
            {
                worker.addPropertyChangeListener(pcl);
            }
            worker.execute();
        }
    }
    
    /* (non-Javadoc)
     * @see edu.ku.brc.af.tasks.BaseTask#getStarterPane()
     */
    @Override
    public SubPaneIFace getStarterPane()
    {
        // this task has no visible panes
        return null;
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.af.tasks.BaseTask#getToolBarItems()
     */
    @Override
    public List<ToolBarItemDesc> getToolBarItems()
    {
        // this task has no toolbar items
        return null;
    }

    /**
     * Checks to see if usage tracking is allowed.
     * 
     * @return true if usage tracking is allowed.
     */
    protected boolean usageTrackingIsAllowed()
    {
        // see if the user is allowing us to send back usage data
        AppPreferences appPrefs  = AppPreferences.getRemote();
        Boolean        sendStats = appPrefs.getBoolean("usage_tracking.send_stats", null); //$NON-NLS-1$
        if (sendStats == null)
        {
            sendStats = true;
            appPrefs.putBoolean("usage_tracking.send_stats", sendStats); //$NON-NLS-1$
        }
        
        return sendStats;
    }

    /**
     * Connect to the update/usage tracking server now.  If this method is called based on the action of a user
     * on a UI widget (menu item, button, etc) a popup will ALWAYS be displayed as a result, showing updates available
     * (even if none are) or an error message.  If this method is called as part of an automatic update check or 
     * automatic usage stats connection, the error message will never be displayed.  Also, in that case, the list
     * of updates will only be displayed if at least one update is available AND the user has enabled auto update checking.
     * 
     * @param isUserInitiatedUpdateCheck if true, the user initiated this process through a UI option
     */
    protected void connectToServerNow(final boolean isUserInitiatedUpdateCheck)
    {
        // Create a SwingWorker to connect to the server in the background, then show results on the Swing thread
        SwingWorker workerThread = new SwingWorker()
        {
            @Override
            public Object construct()
            {
                // connect to the server, sending usage stats if allowed, and gathering the latest modules version info
                try
                {
                    sendStats();
                    return null;
                }
                catch (Exception e)
                {
                    // if any exceptions occur, return them so the finished() method can have them
                    return e;
                }
            }
            
            @SuppressWarnings("unchecked") //$NON-NLS-1$
            @Override
            public void finished()
            {
            }
        };
        
        // start the background task
        workerThread.start();
    }
    
    /**
     * Connects to the update/usage tracking server to get the latest version info and/or send the usage stats.
     * 
     * @return a list of modules that have a different version number than the currently installed software
     * @throws Exception if an IO error occured or the response couldn't be parsed
     */
    protected void sendStats() throws Exception
    {
        // check the website for the info about the latest version
        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setParameter("http.useragent", getClass().getName()); //$NON-NLS-1$
        
        // get the URL of the website to check, with usage info appended, if allowed
        String versionCheckURL = getVersionCheckURL();
        
        PostMethod postMethod = new PostMethod(versionCheckURL);
        
        boolean sendUsageStats = usageTrackingIsAllowed();
        
        // get the POST parameters (which includes usage stats, if we're allowed to send them)
        NameValuePair[] postParams = createPostParameters(sendUsageStats);
        postMethod.setRequestBody(postParams);
        
        // connect to the server
        try
        {
            httpClient.executeMethod(postMethod);
        }
        catch (Exception e)
        {
            throw new ConnectionException(e);
        }
        
    }
    
    /**
     * Gets the URL of the version checking / usage tracking server.
     * 
     * @return the URL string
     */
    protected String getVersionCheckURL()
    {
        String baseURL = getResourceString("StatsTrackerTask.URL"); //$NON-NLS-1$
        return baseURL;
    }
    
    /**
     * Adds the string value or an empty string.
     * @param value the value to be added.
     * @return the new string
     */
    protected static String fixParam(final String value)
    {
        return value == null ? "" : value;
    }
    
    /**
     * Executes a Query that returns a count and adds to the return parameters of the http request
     * @param statName the name of the http param
     * @param statsList the list the stat is to be added to
     * @param sql the SQL statement to be executed
     * @return the value of the count query
     */
    protected int addStat(final String statName, final Vector<NameValuePair> statsList, final String sql)
    {
        int count = 0;
        String sqlStr = QueryAdjusterForDomain.getInstance().adjustSQL(sql);
        if (sqlStr != null)
        {
            count = BasicSQLUtils.getCount(sqlStr);
            statsList.add(new NameValuePair(statName, Integer.toString(count)));
        }
        return count;
    }
    
    /**
     * Collection Statistics about the Collection (synchronously).
     * @return list of http named value pairs
     */
    protected Vector<NameValuePair> collectExtraStats()
    {
        return null;
    }
    
    /**
     * Creates an array of POST method parameters to send with the version checking / usage tracking connection.
     * 
     * @param sendUsageStats if true, the POST parameters include usage stats
     * @return an array of POST parameters
     */
    protected NameValuePair[] createPostParameters(final boolean sendUsageStats)
    {
        Vector<NameValuePair> postParams = new Vector<NameValuePair>();
        try
        {

            // get the install ID
            String installID = UsageTracker.getInstallId();
            postParams.add(new NameValuePair("id", installID)); //$NON-NLS-1$
    
            // get the OS name and version
            postParams.add(new NameValuePair("os_name",      System.getProperty("os.name"))); //$NON-NLS-1$
            postParams.add(new NameValuePair("os_version",   System.getProperty("os.version"))); //$NON-NLS-1$
            postParams.add(new NameValuePair("java_version", System.getProperty("java.version"))); //$NON-NLS-1$
            postParams.add(new NameValuePair("java_vendor",  System.getProperty("java.vendor"))); //$NON-NLS-1$
            
            
            if (sendUsageStats)
            {
                Vector<NameValuePair> extraStats = collectExtraStats();
                if (extraStats != null)
                {
                    postParams.addAll(extraStats);
                }
                
                // get all of the usage tracking stats
                List<Pair<String, Integer>> statistics = UsageTracker.getUsageStats();
                for (Pair<String, Integer> stat : statistics)
                {
                    postParams.add(new NameValuePair(stat.first, Integer.toString(stat.second)));
                }
            }
            
            // create an array from the params
            NameValuePair[] paramArray = new NameValuePair[postParams.size()];
            for (int i = 0; i < paramArray.length; ++i)
            {
                paramArray[i] = postParams.get(i);
            }
            
            return paramArray;
        
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }
    
    //--------------------------------------------------------------------------
    //-- 
    //--------------------------------------------------------------------------
    public abstract class StatsSwingWorker<T, V> extends javax.swing.SwingWorker<T, V>
    {
        public void setProgressValue(final int value)
        {
            setProgress(value);
        }
    }
    
    //--------------------------------------------------------------------------
    //-- 
    //--------------------------------------------------------------------------
    public static class ConnectionException extends IOException
    {
        public ConnectionException(@SuppressWarnings("unused") Throwable e)
        {
            super();
        }
    }
}

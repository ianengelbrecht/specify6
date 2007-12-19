/**
* Copyright (C) 2006  The University of Kansas
*
* [INSERT KU-APPROVED LICENSE TEXT HERE]
*
*/
package edu.ku.brc.specify.dbsupport;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.StaleStateException;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.JDBCConnectionException;

import edu.ku.brc.dbsupport.DataProviderSessionIFace;
import edu.ku.brc.dbsupport.HibernateUtil;
import edu.ku.brc.dbsupport.StaleObjectException;

/**
 * This is a wrapper around Hibernate Session so we don't have to pollute our class with Hibernate and we could switch it
 * out for a networked version later.
 * 
 * @author rods
 *
 * @code_status Complete
 *
 */
public class HibernateDataProviderSession implements DataProviderSessionIFace
{
    protected static final Logger log = Logger.getLogger(HibernateDataProviderSession.class);
    
    // Used for checking to see if we have any dangling creates without closes
    protected static int     createsCounts = 0;
    protected static int     closesCounts  = 0;
    protected static boolean SHOW_COUNTS = false; // XXX RELEASE
    
    protected Session     session         = null;
    protected Exception   recentException = null;
    protected Transaction transaction     = null;
    
    protected List<Object> deleteList     = new Vector<Object>();
    
    /**
     * Creates a new Hibernate Session.
     */
    public HibernateDataProviderSession()
    {
        this.session = HibernateUtil.getNewSession();
        if (SHOW_COUNTS)
        {
            createsCounts++;
            log.debug(" Creates: "+createsCounts+"  Closes: "+closesCounts+" Dif: "+(createsCounts-closesCounts));
        }
    }
    
    public HibernateDataProviderSession(Session session)
    {
        if (session != null)
        {
            // this constructor doesn't track opens and closes since they are done outside of this class, in this case
            this.session = session;
            return;
        }
        
        // dupilicate of HibernateDataProviderSession() code
        this.session = HibernateUtil.getNewSession();
        if (SHOW_COUNTS)
        {
            createsCounts++;
            log.debug(" Creates: "+createsCounts+"  Closes: "+closesCounts+" Dif: "+(createsCounts-closesCounts));
        }
    }
    
    /**
     * Returns a native hibernate session.
     * @return a native hibernate session.
     */
    public Session getSession()
    {
        return session;
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#refresh(java.lang.Object)
     */
    public boolean refresh(Object dataObj)
    {
        if (session != null)
        {
            log.debug(session.hashCode());
            session.refresh(dataObj);
 
            return true;
        }
        
        log.error("Session was null.", new NullPointerException("Session was null"));

        return false;
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#delete(java.lang.Object)
     */
    public boolean delete(Object dataObj) throws Exception
    {
        if (session != null)
        {
            log.debug(session.hashCode());
            session.delete(dataObj);
 
            return true;
        }
        
        log.error("Session was null.", new NullPointerException("Session was null"));

        return false;
    }
    
    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#deleteOnSaveOrUpdate(java.lang.Object)
     */
    public void deleteOnSaveOrUpdate(Object dataObj)
    {
        deleteList.add(dataObj);
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#deleteOnSaveOrUpdate(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public <T> T merge(T dataObj) throws StaleObjectException
    {
        if (session != null)
        {
            T mergedObj = null;
            try
            {
                mergedObj = (T)session.merge(dataObj);
            }
            catch (StaleStateException sse)
            {
                throw new StaleObjectException(sse);
            }
            return mergedObj;
        }
        log.error("Session was null.", new NullPointerException("Session was null"));
        return null;
    }


    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#getDataList(java.lang.String)
     */
    public List<?> getDataList(String sqlStr)
    {
        if (session != null)
        {
            Query query = session.createQuery(sqlStr);
            return query.list();
        }
        
        log.error("Session was null.", new NullPointerException("Session was null"));

        return null;

    }
    
    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#getDataList(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getDataList(Class<T> clazz)
    {
        if (session != null)
        {
            Query q = session.createQuery("FROM " + clazz.getName());
            return q.list();

            // this is the old method, which returns multiple copies of any result that
            // has any EAGER loaded relationships
            // Criteria criteria = session.createCriteria(clazz);
            // return criteria.list();
        }
        
        log.error("Session was null.", new NullPointerException("Session was null"));

        return null;
    }
    
    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#getDataList(java.lang.Class, java.lang.String, boolean)
     */
    @SuppressWarnings("unchecked")
    public <T> List<T>  getDataList(Class<T> clsObject, String fieldName, boolean isDistinct)
    {
        Query query = session.createQuery("SELECT DISTINCT " + fieldName + " FROM " + clsObject.getName() + " WHERE " + fieldName + " <> NULL");
        return query.list();
    }

    
    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#getDataList(java.lang.Class, java.lang.String, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getDataList(Class<T> clsObject, String fieldName, Object value, DataProviderSessionIFace.CompareType compareType)
    {
        if (session != null)
        {
            Criteria criteria = session.createCriteria(clsObject);
            criteria.add(compareType == DataProviderSessionIFace.CompareType.Equals ? Restrictions.eq(fieldName, value) : Restrictions.eq(fieldName, value));
            return criteria.list();           
        }
        
        log.error("Session was null.", new NullPointerException("Session was null"));

        return null;
    }
    
    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#getData(java.lang.Class, java.lang.String, java.lang.Object, edu.ku.brc.dbsupport.DataProviderSessionIFace.CompareType)
     */
    @SuppressWarnings("unchecked")
    public <T> T getData(Class<T> clsObject, String fieldName, Object value, DataProviderSessionIFace.CompareType compareType)
    {
        if (session != null)
        {
            Criteria criteria = session.createCriteria(clsObject);
            criteria.add(compareType == DataProviderSessionIFace.CompareType.Equals ? Restrictions.eq(fieldName, value) : Restrictions.eq(fieldName, value));
            List<T> list = criteria.list();
            return list == null || list.size() == 0 ? null : list.get(0);
        }
        
        log.error("Session was null.", new NullPointerException("Session was null"));

        return null;
    }

    
    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#getDataList(java.lang.Class, java.lang.String, java.lang.Object)
     */
    public <T> List<T> getDataList(Class<T> clsObject, String fieldName, Object value)
    {
        if (session != null)
        {
            return getDataList(clsObject, fieldName, value, DataProviderSessionIFace.CompareType.Equals);
        }
        
        log.error("Session was null.", new NullPointerException("Session was null"));

        return null;
    }
    
    protected void checkAndReconnect()
    {
        boolean reconnect = false;
        try
        {
            Connection conn = session.connection();
            Statement  stmt = conn.createStatement();
            ResultSet  rs   = stmt.executeQuery("select * from specifyuser");
            rs.next();
            rs.close();
            stmt.close();
            
        } catch (SQLException ex)
        {
            reconnect = true;
            
        } catch (JDBCConnectionException ex)
        {
            reconnect = true;
        }
        
        if (reconnect)
        {
            log.debug("Reconnecting and rebuilding session factory....");
            session.close();
            HibernateUtil.rebuildSessionFactory();
            session = HibernateUtil.getNewSession();
        }
    }
    
    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#get(java.lang.Class, java.lang.Integer)
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clsObj, Integer id)
    {
        if (session != null)
        {
            return (T)session.get(clsObj, id);
        }
        
        log.error("Session was null.", new NullPointerException("Session was null"));

        return null;
    }
    
    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#getDataCount(java.lang.Class, java.lang.String, java.lang.Object, edu.ku.brc.dbsupport.DataProviderSessionIFace.CompareType)
     */
    public <T> Integer getDataCount(Class<T> clsObject, String fieldName, Object value, DataProviderSessionIFace.CompareType compareType)
    {
        if (session != null)
        {
            Criteria criteria = session.createCriteria(clsObject);
            criteria.add(compareType == DataProviderSessionIFace.CompareType.Equals ? Restrictions.eq(fieldName, value) : Restrictions.eq(fieldName, value));
            criteria.setProjection(Projections.rowCount());
            List<?> countList = criteria.list();
            
            if (countList == null || countList.size() == 0)
            {
                return 0;
                
            }
            // else
            Object countObj = countList.get(0);
            if (countObj instanceof Integer)
            {
                return (Integer)countObj;
            }
        }
        
        log.error("Session was null.", new NullPointerException("Session was null"));

        return 0;
    }
    
    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#load(java.lang.Class, java.lang.Integer)
     */
    @SuppressWarnings("unchecked")
    public <T> T load(Class<T> clsObj, Integer id)
    {
        if (session != null)
        {
            return (T)session.load(clsObj, id);
        }
        
        log.error("Session was null.", new NullPointerException("Session was null"));

        return null;
    }
    
    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#getData(java.lang.String)
     */
    public Object getData(String sqlStr)
    {
        if (session != null)
        {
            List<?> list = getDataList(sqlStr);
            return list != null && list.size() > 0 ? list.get(0) : null;
        }
        
        log.error("Session was null.", new NullPointerException("Session was null"));

        return null;
    }
    
    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#contains(java.lang.Object)
     */
    public boolean contains(final Object obj)
    {
        return session.contains(obj);
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#evict(java.lang.Class)
     */
    public void evict(Class<?> clsObject)
    {
        if (session != null)
        {
            session.evict(clsObject);
            HibernateUtil.getSessionFactory().evict(clsObject);
            
        } else
        {
            log.error("Session was null.", new NullPointerException("Session was null"));
        }
    }
    
    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#evict(java.lang.Object)
     */
    public void evict(Object dataObj)
    {
        if (session != null)
        {
            session.evict(dataObj);
        } else
        {
            log.error("Session was null.", new NullPointerException("Session was null"));
        }
    }
    
    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#placeIntoSession(java.lang.Object)
     */
    public void attach(Object dataObj)
    {
        if (session != null)
        {
            session.lock(dataObj, LockMode.NONE);
            
        } else
        {
            log.error("Session was null.", new NullPointerException("Session was null"));
        }
    }
    
    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#save(java.lang.Object)
     */
    public boolean save(Object dataObj) throws Exception
    {
        if (session != null)
        {
            deleteObjectFromList();
            session.save(dataObj);
            return true;
        }
        
        log.error("Session was null.", new NullPointerException("Session was null"));
        
        return false;
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#saveOrUpdate(java.lang.Object)
     */
    public boolean saveOrUpdate(Object dataObj) throws Exception
    {
        if (session != null)
        {
            deleteObjectFromList();
            session.saveOrUpdate(dataObj);
            return true;
        }
        
        log.error("Session was null.", new NullPointerException("Session was null"));
        
        return false;
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#update(java.lang.Object)
     */
    public boolean update(Object dataObj) throws Exception
    {
        if (session != null)
        {
            deleteObjectFromList();
            session.update(dataObj);
            return true;
        }
        
        log.error("Session was null.", new NullPointerException("Session was null"));
        
        return false;
    }
    
    public QueryIFace createQuery(String hql)
    {
        return new HibernateQuery(hql);
    }

    
    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#createCriteria(java.lang.Class)
     */
    public CriteriaIFace createCriteria(Class<?> cls)
    {
        if (session != null) 
        { 
            return new HibernateCriteria(cls); 
        }
        log.error("Session was null.", new NullPointerException("Session was null"));
        return null;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#beginTransaction()
     */
    public void beginTransaction() throws Exception
    {
        if (session != null)
        {
            transaction = session.beginTransaction();
            if (transaction == null)
            {
                log.error("Transaction was null!"); // Throw Exception?
            }
        } else
        {
            log.error("Session was null.", new NullPointerException("Session was null"));
        }
    }
    
    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#commit()
     */
    public void commit() throws Exception
    {
        if (transaction != null)
        {
            try
            {
                transaction.commit();
                transaction = null;
                
            } catch (StaleObjectStateException soe)
            {
                throw new StaleObjectException(soe);
            }
        } else
        {
            throw new RuntimeException("Transaction was null and shouldn't been.");
        }
    }
    
    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#rollback()
     */
    public void rollback()
    {
        if (transaction != null)
        {
            transaction.rollback();
            transaction = null;
            
        } else
        {
            throw new RuntimeException("Transaction was null and shouldn't been.");
        }
    }

    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#flush()
     */
    public void flush()
    {
        if (session != null)
        {
            session.flush();
            
        } else
        {
            log.error("Session was null.", new NullPointerException("Session was null"));
        }
    }
    
    /* (non-Javadoc)
     * @see edu.ku.brc.dbsupport.DataProviderSessionIFace#close()
     */
    public void close()
    {
        if (SHOW_COUNTS)
        {
            closesCounts++;
            log.info("*Creates: "+createsCounts+"  Closes: "+closesCounts+" Dif: "+(createsCounts-closesCounts));
        }
        
        if (session != null)
        {
            if (transaction != null)
            {
                transaction.rollback();
                transaction = null;
                throw new RuntimeException("Closing Session with open transaction - rolling it back");
            }
            
            session.close();
            session = null;
        } else
        {
            log.error("Session was null.", new NullPointerException("Session was null"));
        }
     }
    
    /**
     * Deletes all the object that were marked for delayed deletion.
     */
    protected void deleteObjectFromList()
    {
        for (Object obj : deleteList)
        {
            if (session.contains(obj))
            {
                session.delete(obj);
            }
        }
        deleteList.clear();
    }
    
    public class HibernateQuery implements QueryIFace
    {
        protected Query queryDelegate;
        
        public HibernateQuery(String hql)
        {
            //log.debug("hql["+hql+"]");
            queryDelegate = session.createQuery(hql);
            if (queryDelegate == null)
            {
                log.error("queryDelegate is null for query for hql["+hql+"]");
            }
        }

        public int executeUpdate()
        {
            return queryDelegate != null ? queryDelegate.executeUpdate() : 0;
        }

        public List<?> list()
        {
            return queryDelegate != null ? queryDelegate.list() : null;
        }

        public void setParameter(String name, Object value)
        {
            if (queryDelegate != null)
            {
                queryDelegate.setParameter(name, value);
            }
        }

        public Object uniqueResult()
        {
            return queryDelegate != null ? queryDelegate.uniqueResult() : null;
        }
    }
    
    public class HibernateCriteria implements CriteriaIFace
    {
        protected Criteria criteriaDelegate;
        
        public HibernateCriteria(Class<?> cls)
        {
            criteriaDelegate = session.createCriteria(cls);
        }
        
        public void add(Object criterion)
        {
            if (criterion instanceof Criterion)
            {
                criteriaDelegate.add((Criterion)criterion);
            }
            else
            {
                log.error("invalid criterion.");
            }
        }
        
        public Object uniqueResult()
        {
            return criteriaDelegate.uniqueResult();
        }
        
        public List<?> list()
        {
            return criteriaDelegate.list();
        }
    }
}

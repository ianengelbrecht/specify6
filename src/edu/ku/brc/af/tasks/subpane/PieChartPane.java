/* This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package edu.ku.brc.af.tasks.subpane;

import static edu.ku.brc.ui.UICacheManager.getResourceString;
import static edu.ku.brc.ui.UIHelper.getInt;
import static edu.ku.brc.ui.UIHelper.getString;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.Icon;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import edu.ku.brc.af.core.Taskable;
import edu.ku.brc.dbsupport.QueryResultsContainer;
import edu.ku.brc.dbsupport.QueryResultsGetter;
import edu.ku.brc.dbsupport.QueryResultsHandlerIFace;
import edu.ku.brc.dbsupport.QueryResultsListener;
import edu.ku.brc.dbsupport.QueryResultsProcessable;
import edu.ku.brc.ui.IconManager;

/**
 * Creates a pane that can listener for Query Results and then create a Pie Chart.
 
 * @code_status Complete
 **
 * @author rods
 *
 */
@SuppressWarnings("serial")
public class PieChartPane extends ChartPane implements QueryResultsListener, QueryResultsProcessable
{
    // Static Data Members
    //private static final Logger log = Logger.getLogger(BarChartPane.class);

    // Data Members
    @SuppressWarnings("unused")
    private QueryResultsGetter       getter;
    @SuppressWarnings("unused")
    private QueryResultsContainer    qrContainer;
    private QueryResultsHandlerIFace handler = null;


    /**
     * Constructor.
     * @param name name of pane
     * @param task the owning task
     */
    public PieChartPane(final String name,
                        final Taskable task)
    {
        super(name, task);

        progressLabel.setText(getResourceString("BuildingPieChart"));

        getter      = new QueryResultsGetter(this);
        qrContainer = new QueryResultsContainer(name);
    }

    /*
     *  (non-Javadoc)
     * @see edu.ku.brc.af.core.Taskable#getIcon()
     */
    @Override
    public Icon getIcon()
    {
        return IconManager.getIcon("Pie_Chart", IconManager.IconSize.Std16);
    }


    //--------------------------------------
    // QueryResultsProcessable
    //--------------------------------------

    /*
     *  (non-Javadoc)
     * @see edu.ku.brc.af.dbsupport.QueryResultsProcessable#setHandler()
     */
    public void setHandler(final QueryResultsHandlerIFace handler)
    {
        this.handler = handler;
    }

    /*
     *  (non-Javadoc)
     * @see edu.ku.brc.af.dbsupport.QueryResultsProcessable#getHandler()
     */
    public QueryResultsHandlerIFace getHandler()
    {
        return handler;
    }

    //--------------------------------------
    // QueryResultsListener
    //--------------------------------------

    /*
     *  (non-Javadoc)
     * @see edu.ku.brc.af.dbsupport.QueryResultsListener#allResultsBack()
     */
    public synchronized void allResultsBack()
    {
        // create a dataset...
        DefaultPieDataset dataset = new DefaultPieDataset();

        java.util.List<Object> list = handler.getDataObjects();
        for (int i=0;i<list.size();i++)
        {
            Object descObj = list.get(i++);
            Object valObj  = list.get(i);
            dataset.setValue(getString(descObj), getInt(valObj));
        }
        list.clear();

        // create a chart...
        JFreeChart chart = ChartFactory.createPieChart(
                title,
                dataset,
                false, // legend?
                true, // tooltips?
                false // URLs?
            );

        /*JPanel outerPanel = new JPanel()
        {
            Dimension dim = new Dimension(400,400);
            public Rectangle getBounds()
            {
              return new Rectangle(getLocation().x, getLocation().y, dim.width, dim.height);
            }
            public void setBounds(Rectangle r)
            {
                setBounds(r.x, r.y, r.width, r.height);
            }
            public void setBounds(int x, int y, int width, int height)
            {
                if (width <= 400)
                    dim.width = width;
                else
                    dim.width = 400;
                if (height <= 400)
                    dim.height = height;
                else
                    dim.height = 400;
                super.setBounds(x, y, width, height);
            }
            public Rectangle getBounds(Rectangle rv)
            {
                rv.setBounds(getLocation().x, getLocation().y, dim.width, dim.height);
                return rv;
            }
            public Dimension getSize()
            {
                return new Dimension(dim);
            }
            public Dimension getSize(Dimension rv)
            {
                rv.setSize(dim);
                return rv;
            }
        };*/
        // create and display a frame...
        SquareChartPanel panel = new SquareChartPanel(chart, true, true, true, true, true);
        setBackground(Color.BLUE);
        //outerPanel.setLayout(new BorderLayout());
        //outerPanel.add(panel);

        removeAll(); // remove progress bar

        PanelBuilder    builder    = new PanelBuilder(new FormLayout("p:g,p,p:g", "f:p:g"));
        CellConstraints cc         = new CellConstraints();
        builder.add(panel, cc.xy(2,1));
        add(builder.getPanel(), BorderLayout.CENTER);

        doLayout();
        repaint();

    }

    /* (non-Javadoc)
     * @see edu.ku.brc.af.dbsupport.QueryResultsListener#resultsInError(edu.ku.brc.af.dbsupport.QueryResultsContainer)
     */
    public void resultsInError(final QueryResultsContainer qrc)
    {
        // do nothing
    }
}

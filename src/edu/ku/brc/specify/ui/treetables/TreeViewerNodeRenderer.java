/**
 * Copyright (C) 2007  The University of Kansas
 *
 * [INSERT KU-APPROVED LICENSE TEXT HERE]
 * 
 */

package edu.ku.brc.specify.ui.treetables;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.log4j.Logger;

import edu.ku.brc.ui.GraphicsUtils;
import edu.ku.brc.ui.IconManager;
import edu.ku.brc.util.Pair;

/**
 * @author jstewart
 * @code_status Alpha
 */
public class TreeViewerNodeRenderer implements ListCellRenderer, ListDataListener
{
    /** Logger for all messages emitted. */
    private static final Logger log = Logger.getLogger(TreeViewerNodeRenderer.class);
            
    // open/close handle icons
    protected Icon open;
    protected Icon closed;
    
    protected TreeViewerListModel model;
    protected JList list;
    
    protected boolean lengthsValid;
    protected SortedMap<Integer,Pair<Integer,Integer>> rankBoundsMap;

    protected Color bgs[];
    
    protected int leadTextOffset;
    protected int tailTextOffset;

    protected TreeNodeUI nodeUI;
    
    protected boolean firstTime = true;
    
    /**
     * 
     */
    public TreeViewerNodeRenderer(TreeViewerListModel model, Color[] bgColors)
    {
        this.model = model;
        
        bgs = new Color[2];
        bgs[0] = bgColors[0];
        bgs[1] = bgColors[1];
        
        open   = IconManager.getIcon("Down",    IconManager.IconSize.NonStd);
        closed = IconManager.getIcon("Forward", IconManager.IconSize.NonStd);
        
        leadTextOffset = 24;
        tailTextOffset = 8;
        
        rankBoundsMap = new TreeMap<Integer, Pair<Integer,Integer>>();
        
        model.addListDataListener(this);
        
        lengthsValid = false;
        
        nodeUI = new TreeNodeUI();
    }
    
    public Color[] getBackgroundColors()
    {
        return this.bgs;
    }

    public Component getListCellRendererComponent(JList l, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        log.debug("getListCellRendererComponent( " + value + " )");
        this.list = l;
        
        if ( !(value instanceof TreeNode))
        {
            return new JLabel("Item must be an instance of TreeNode");
        }
        
        if (firstTime)
        {
            firstTime = false;
            recomputeLengthPerLevel(l.getGraphics());
        }
        
        TreeNode node = (TreeNode)value;
        nodeUI.setNode(node);
        nodeUI.setSelected(isSelected);
        nodeUI.setHasFocus(cellHasFocus);
        nodeUI.setToolTipText(node.getName());
        return nodeUI;
    }
    
    public Pair<Integer,Integer> getTextBoundsForRank(Integer rank)
    {
        if( rank == null )
        {
            return null;
        }
        
        Pair<Integer,Integer> textBounds = new Pair<Integer,Integer>();
        Pair<Integer,Integer> bounds = rankBoundsMap.get(rank);
        if( bounds == null )
        {
            return null;
        }
        
        textBounds.first = bounds.first + leadTextOffset;
        textBounds.second = bounds.second - tailTextOffset;
        
        return textBounds;
    }
    
    public Pair<Integer,Integer> getAnchorBoundsForRank(Integer rank)
    {
        if( rank == null )
        {
            return null;
        }
        
        Pair<Integer,Integer> anchorBounds = new Pair<Integer,Integer>();
        Pair<Integer,Integer> bounds = rankBoundsMap.get(rank);
        if( bounds == null )
        {
            return null;
        }
        
        anchorBounds.first = bounds.first;
        anchorBounds.second = bounds.first + leadTextOffset;
        
        return anchorBounds;
    }
    
    public Pair<Integer,Integer> getColumnBoundsForRank(Integer rank)
    {
        if( rank == null )
        {
            return null;
        }
        
        Pair<Integer,Integer> colBounds = new Pair<Integer,Integer>();
        Pair<Integer,Integer> bounds = rankBoundsMap.get(rank);
        if( bounds == null )
        {
            return null;
        }
        
        colBounds.first = bounds.first;
        colBounds.second = bounds.second;
        
        return colBounds;
    }
    
    protected void recomputeLengthPerLevel( Graphics g )
    {
        rankBoundsMap.clear();
        
        int prevRankEnd = 0;
        List<Integer> visibleRanks = model.getVisibleRanks();
        for( Integer rank: visibleRanks )
        {
            Pair<Integer,Integer> bounds = new Pair<Integer, Integer>();
            bounds.setFirst(prevRankEnd);

            Integer longestStringLength = g.getFontMetrics().stringWidth("WWWWWWWWWW");
            if( longestStringLength != null )
            {
                bounds.setSecond(prevRankEnd + longestStringLength + leadTextOffset + tailTextOffset);
            }
            rankBoundsMap.put(rank,bounds);
            prevRankEnd = bounds.second;
        }
        
        lengthsValid = true;
    }
    
    protected int getWidestRankWidth()
    {
        int longest = 0;
        for(Pair<Integer,Integer> width: rankBoundsMap.values())
        {
            if(width.second > longest)
            {
                longest = width.second;
            }
        }
        return longest;
    }

    public void intervalAdded(ListDataEvent e)
    {
        lengthsValid = false;
    }

    public void intervalRemoved(ListDataEvent e)
    {
        lengthsValid = false;
    }

    public void contentsChanged(ListDataEvent e)
    {
        lengthsValid = false;
    }

    public class TreeNodeUI extends JPanel
    {
        protected TreeNode treeNode;
        protected boolean hasFocus;
        protected boolean selected;
        
        public TreeNodeUI()
        {
            super();
        }
        
        public boolean isHasFocus()
        {
            return hasFocus;
        }

        public void setHasFocus(boolean hasFocus)
        {
            this.hasFocus = hasFocus;
        }

        public TreeNode getTreeNode()
        {
            return treeNode;
        }

        public void setNode(TreeNode node)
        {
            this.treeNode = node;
        }

        public boolean isSelected()
        {
            return selected;
        }

        public void setSelected(boolean selected)
        {
            this.selected = selected;
        }

        @SuppressWarnings("synthetic-access")
        @Override
        public Dimension getPreferredSize()
        {
            // ensure that the lengths are valid
            if( !lengthsValid )
            {
                recomputeLengthPerLevel(list.getGraphics());
            }

            int rank = treeNode.getRank();
            Pair<Integer,Integer> rankBounds = rankBoundsMap.get(rank);
            if(rankBounds == null)
            {
                log.warn("getPreferredSize(): rankBounds is unexpectedly null.  Trying to recover by recomputing bounds.");
                recomputeLengthPerLevel(list.getGraphics());
                rankBounds = rankBoundsMap.get(rank);
                if(rankBounds==null)
                {
                    log.error("getPreferredSize(): recovery attempt failed");
                    return new Dimension(getWidestRankWidth(),list.getFirstVisibleIndex());
                }
                
                log.info("getPreferredSize(): recovery attempt succeeded");
            }
            int width = rankBounds.second;
            Dimension prefSize = new Dimension(width,list.getFixedCellHeight());
            log.debug("getPreferredSize() = " + prefSize);
            return prefSize;
        }
        
        @Override
        protected void paintComponent(Graphics g)
        {
            if( list.getFont() != null )
            {
                this.setFont(list.getFont());
            }

            // ensure that the lengths are valid
            if( !lengthsValid )
            {
                recomputeLengthPerLevel(list.getGraphics());
            }
            
            GraphicsUtils.turnOnAntialiasedDrawing(g);
            g.setColor(list.getForeground());
            
            // draw the alternating color background
            drawBackgroundColors(g);
            
            drawNodeAnchors(g);
            
            // draw the downward lines from ancestors to descendants renderered below this node
            drawTreeLinesToLowerNodes(g);
            
            // draw the open/close icon
            drawOpenClosedIcon(g);
            
            // draw the string name of the node
            drawNodeString(g);
        }
        
        private void drawBackgroundColors(Graphics g)
        {
            Color orig = g.getColor();
            int cellHeight = list.getFixedCellHeight();

            int i = 0;
            for( Integer rank: rankBoundsMap.keySet() )
            {
                Pair<Integer,Integer> startEnd = rankBoundsMap.get(rank);
                g.setColor(bgs[i%2]);
                g.fillRect(startEnd.first,0,startEnd.second,cellHeight);
                ++i;
            }
            
            g.setColor(orig);
        }
        
        private void drawNodeAnchors(Graphics g)
        {
            TreeNode node = treeNode;
            TreeNode parent = model.getNodeById(treeNode.getParentId());
            int cellHeight = list.getFixedCellHeight();
            int midCell = cellHeight/2;

            if( node != model.getVisibleRoot() && parent != null )
            {
                int parentRank = parent.getRank();
                int rank  = node.getRank();
                Pair<Integer,Integer> parentAnchorBounds = getAnchorBoundsForRank(parentRank);
                Pair<Integer,Integer> nodeAnchorBounds = getAnchorBoundsForRank(rank);
                
                if( !model.parentHasChildrenAfterNode(parent, node) )
                {
                    // draw an L-line
                    g.drawLine(parentAnchorBounds.second,0,parentAnchorBounds.second,midCell);
                    g.drawLine(parentAnchorBounds.second,midCell,nodeAnchorBounds.first,midCell);
                }
                else
                {
                    // draw a T-shape
                    g.drawLine(parentAnchorBounds.second,0,parentAnchorBounds.second,cellHeight);
                    g.drawLine(parentAnchorBounds.second,midCell,nodeAnchorBounds.first,midCell);
                }
            }
        }
        
        private void drawTreeLinesToLowerNodes(Graphics g)
        {
            // determine if this node has more peer nodes below it
            // if not, draw an L-shape
            // if so, draw a T-shape

            TreeNode node = treeNode;
            TreeNode parent = model.getNodeById(treeNode.getParentId());
            int cellHeight = list.getFixedCellHeight();

            while( node != model.getVisibleRoot() && parent != null )
            {
                if( model.parentHasChildrenAfterNode(parent, node) )
                {
                    // draw the vertical line for under this parent
                    int width = getAnchorBoundsForRank(parent.getRank()).second;
                    g.drawLine(width, 0, width, cellHeight);
                }
                
                node = parent;
                parent = model.getNodeById(node.getParentId());
            }
        }
        
        private void drawOpenClosedIcon(Graphics g)
        {
            int cellHeight = list.getFixedCellHeight();
            Pair<Integer,Integer> anchorBounds = getAnchorBoundsForRank(treeNode.getRank());
            int anchorStartX = anchorBounds.getFirst();

            // don't do anything for leaf nodes
            if( !treeNode.isHasChildren() )
            {
                return;
            }
            
            Icon openClose = null;
            if( !model.showingChildrenOf(treeNode) )
            {
                openClose = closed;
            }
            else
            {
                openClose = open;
            }

            // calculate offsets for icon
            int iconWidth = openClose.getIconWidth();
            int iconHeight = openClose.getIconHeight();
            int widthDiff = anchorBounds.second - anchorBounds.first - iconWidth;
            int heightDiff = cellHeight - iconHeight;
            openClose.paintIcon(list,g,anchorStartX+(int)(.5*widthDiff),0+(int)(.5*heightDiff));
        }
        
        private void drawNodeString(Graphics g)
        {
            Graphics2D g2d = (Graphics2D)g;
            FontMetrics fm = g.getFontMetrics();
            int cellHeight = list.getFixedCellHeight();
            String name = treeNode.getName();
            int baselineAdj = (int)(1.0/2.0*fm.getAscent() + 1.0/2.0*cellHeight);
            Pair<Integer,Integer> stringBounds = getTextBoundsForRank(treeNode.getRank());
            int stringStartX = stringBounds.getFirst();
            int stringEndX = stringBounds.getSecond();
            int stringLength = stringEndX - stringStartX;
            int stringY = baselineAdj;
            if( selected )
            {
                g2d.setColor(list.getSelectionBackground());
                g2d.fillRoundRect(stringStartX-2, 1, stringLength+4, cellHeight-2, 8, 8);
                g2d.setColor(list.getSelectionForeground());
            }
            
            String clippedName = GraphicsUtils.clipString(fm, name, stringEndX-stringStartX);
//            String clippedName = SwingUtilities2.clipStringIfNecessary(list, fm, name, stringEndX-stringStartX);
            g.drawString(clippedName, stringStartX, stringY);
        }
    }
}

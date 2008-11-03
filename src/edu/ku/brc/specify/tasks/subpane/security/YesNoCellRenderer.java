package edu.ku.brc.specify.tasks.subpane.security;

import static edu.ku.brc.ui.UIRegistry.getResourceString;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class YesNoCellRenderer extends DefaultTableCellRenderer
{
    public final String YES = getResourceString("YES");
    public final String NO  = getResourceString("NO");
    
    public Font boldFont  = null;
    public Font normalFont = null;
    
    /**
     * 
     */
    public YesNoCellRenderer()
    {
        super();
        setHorizontalAlignment(SwingConstants.CENTER);
        
        normalFont = getFont();
        boldFont   = normalFont.deriveFont(Font.BOLD);
    }


    @Override
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column)
    {
        JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value instanceof Boolean)
        {
            label.setForeground(((Boolean)value) ? Color.BLACK : Color.LIGHT_GRAY);
            label.setFont(((Boolean)value) ? boldFont : normalFont);
            label.setText(((Boolean)value) ? YES : NO);
        }
        return label;
    }
    
}

package fpozzi.stopper.view.swing.codastampa;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import fpozzi.stopper.view.swing.Style;

public class CodaStampaTableCellRenderer extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = 1L;
	
	public static final Color selectionBgColor = new Color(92, 173, 255), selectionLeaderBgColor = new Color(58, 140, 228);
	
	private final StyleCodaStampaTable table;
	
	public CodaStampaTableCellRenderer(StyleCodaStampaTable table)
	{
		this.table = table;
	}
	
	public StyleCodaStampaTable getTable()
	{
		return table;
	}

	@Override
	public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);

		if (table.previewManager.getRowBeingPreviewed()==row)
		{
			c.setBackground(Style.balloonBgColor);
			c.setForeground(Color.BLACK);
		}
		else if (isSelected)
		{
			if (table.getSelectionModel().getLeadSelectionIndex() == row)
				c.setBackground(selectionLeaderBgColor);
			else
				c.setBackground(selectionBgColor);
		}
		else
			c.setBackground(null);
		
		
		return c;
	}
}
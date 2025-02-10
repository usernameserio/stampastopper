package fpozzi.stopper.view.swing.codastampa;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.java.balloontip.BalloonTip;
import fpozzi.stopper.model.pdf.PdfStopperRequest;
import fpozzi.stopper.view.swing.Style;
import fpozzi.stopper.view.swing.codastampa.StyleCodaStampaTable.ColumnHeader;
import fpozzi.utils.swing.SmartTipPositioner;

public class SingleColumnPreviewManager
{
	private final StyleCodaStampaTable table;

	private BalloonTip previewTip;
	private Window window;
	private int rowBeingPreviewed;

	public SingleColumnPreviewManager(StyleCodaStampaTable table)
	{
		super();
		this.table = table;

		previewTip = null;

		PreviewMouseListener previewMouseListener = new PreviewMouseListener();

		table.addMouseListener(previewMouseListener);
		table.addMouseMotionListener(previewMouseListener);

		window = null;
		rowBeingPreviewed = -1;
	}

	private Window getWindow()
	{
		if (window == null)
			window = SwingUtilities.getWindowAncestor(table.getCsPanel());
		return window;
	}

	public int getRowBeingPreviewed()
	{
		synchronized (table)
		{
			return rowBeingPreviewed;
		}
	}

	private class PreviewMouseListener extends MouseMotionAdapter implements MouseListener
	{

		@Override
		public void mouseMoved(MouseEvent evt)
		{
			synchronized (table)
			{
				Point position = evt.getPoint();
				int col = table.columnAtPoint(position);
				if (col != table.convertColumnIndexToView(ColumnHeader.ANTEPRIMA.ordinal()))
				{
					cancelPreview();
					return;
				}

				int row = table.rowAtPoint(position);

				if (rowBeingPreviewed != row)
				{
					cancelPreview();

					rowBeingPreviewed = row;

					if (row >= 0)
					{
						PdfStopperRequest request = table.getModel().getRequests().get(table.convertRowIndexToModel(row));
						try
						{

							JLabel previewLabel = new JLabel(new ImageIcon(
									request.getPdfStopper().getPreview(table.getCsPanel().getStyle(),(int)(getWindow().getWidth()*0.7f), (int)(getWindow().getHeight()*0.7f)))
									);
							JPanel previewPanel = new JPanel();
							previewPanel.setOpaque(false);
							previewPanel.add(previewLabel);
							previewLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
							previewTip = new BalloonTip(table.getCsPanel().getCodaStampaPanel(), "Anteprima", Style.makePreviewTipBalloonStyle(),
									false);
							previewTip.setContents(previewPanel);

							Rectangle tableRect = SwingUtilities.convertRectangle(table, table.getBounds(), table.getCsPanel().getCodaStampaPanel());
							Rectangle cellRect = table.getCellRect(row, table.convertColumnIndexToView(ColumnHeader.ANTEPRIMA.ordinal()), false);

							previewTip.setPositioner(new SmartTipPositioner(getWindow(), tableRect.x + cellRect.x + cellRect.width / 2, tableRect.y
									- table.getTableHeader().getHeight() + cellRect.y + cellRect.height / 2, 12, 12, 0, 0));

							table.getModel().fireTableRowsUpdated(row, row);

						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
			cancelPreview();
		}

		private void cancelPreview()
		{
			synchronized (table)
			{
				if (rowBeingPreviewed >= 0)
				{
					int previousRowBeingPreviewed = 
							table.convertRowIndexToModel(rowBeingPreviewed);

					rowBeingPreviewed = -1;

					table.getModel().fireTableRowsUpdated(previousRowBeingPreviewed, previousRowBeingPreviewed);

					if (previewTip != null)
					{

						previewTip.closeBalloon();
						previewTip = null;
					}
				}

			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent arg0)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent arg0)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			// TODO Auto-generated method stub

		}

	}

}

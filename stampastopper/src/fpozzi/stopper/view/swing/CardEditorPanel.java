package fpozzi.stopper.view.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.positioners.RightBelowPositioner;
import net.java.balloontip.utils.TimingUtils;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import fpozzi.gdoshop.model.articolo.CodiceEan;
import fpozzi.stopper.model.Card;
import fpozzi.stopper.model.pdf.CardPdf;
import fpozzi.stopper.view.CardEditorView;
import fpozzi.utils.StringUtils;
import fpozzi.utils.swing.Ean13Field;
import fpozzi.utils.swing.NonEmptyTextField;
import fpozzi.utils.swing.SelectAllFocusAdapter;
import fpozzi.utils.swing.document.AllCapsDocumentFilter;
import fpozzi.utils.swing.document.FilteredDocument;
import fpozzi.utils.swing.document.LengthLimitDocument;

public class CardEditorPanel extends StopperEditorPanel<CardPdf> implements CardEditorView
{

	private static final long serialVersionUID = 1L;
	
	private CardEditorObserver observer;

	private final NonEmptyTextField tagField;
	private final Ean13Field eanField;
	private final JButton searchButton;

	static final String formLayoutColumns[] = new String[] { "right:30dlu, 3dlu, ", "left:70dlu, 3dlu, ", "left:50dlu" };

	static final FormLayout defaultFormLayout = new FormLayout(StringUtils.join(formLayoutColumns, 0, ""), "");;

	private final JPanel formPanel;

	public CardEditorPanel()
	{
		super(new BorderLayout());

		JLabel label;

		formPanel = new JPanel();
		formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
		formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel columnFormPanel;

		DefaultFormBuilder builder;

		builder = new DefaultFormBuilder(defaultFormLayout);
		builder.setDefaultDialogBorder();

		label = (new JLabel("Codice"));
		builder.append(label);
		eanField = new Ean13Field("0000000000000");
		eanField.addFocusListener(new SelectAllFocusAdapter(eanField));
		builder.append(eanField);
		searchButton = new JButton(new AbstractAction()
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				observer.searchCode(eanField.getValue());
			}
		});
		searchButton.setFocusPainted(false);
		searchButton.setRolloverEnabled(false);
		searchButton.setBorderPainted(false);
		searchButton.setMargin(new Insets(2, 7, 2, 7));
		searchButton.setIcon(Icons.FIND.image);
		searchButton.setText(" Trova");
		builder.append(searchButton);
		builder.nextLine();

		label = (new JLabel("Nome"));
		builder.append(label);
		tagField = new NonEmptyTextField("NUOVO");
		tagField.addFocusListener(new SelectAllFocusAdapter(tagField));
		FilteredDocument doc = new LengthLimitDocument(27);
		doc.getFilters().add(AllCapsDocumentFilter.instance);
		tagField.setDocument(doc);
		builder.append(tagField, 3);
		builder.nextLine();

		columnFormPanel = builder.getPanel();
		columnFormPanel.setBorder(BorderFactory.createEmptyBorder());
		formPanel.add(columnFormPanel);

		JScrollPane scrollPane = new JScrollPane(formPanel);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		this.add(scrollPane, BorderLayout.CENTER);

		this.setPreferredSize(new Dimension(270, this.getPreferredSize().height));
	}
	

	@Override
	public void setObserver(CardEditorObserver observer)
	{
		this.observer = observer;		
	}

	@Override
	public CardPdf getPdfStopper()
	{
		CardPdf pdfCard = null;

		String tag = tagField.getText();
		if (tag.isEmpty())
			tag = null;

		String ean = eanField.getText();

		Card card = new Card();
		card.setTag(tag);
		card.setCodiceEan(new CodiceEan(ean));

		pdfCard = new CardPdf(card);

		return pdfCard;
	}

	@Override
	protected void toForm(CardPdf pdfCard)
	{
		tagField.reset();
		eanField.reset();

		if (pdfCard != null)
		{
			Card card = pdfCard.getStopper();

			tagField.setText(card.getTag());
			eanField.setText(card.getCodiceEan().valore);
		}

	}


	@Override
	public void notifySearchResult(String searchResult)
	{
		if (searchResult == null)
		{
			BalloonTip balloonTip = new BalloonTip(searchButton, "Tessera non trovata");
			balloonTip.setPositioner(new RightBelowPositioner(25,10));
			balloonTip.setCloseButton(null);
			TimingUtils.showTimedBalloon(balloonTip, 3000);
			Toolkit.getDefaultToolkit().beep();
		}
		else
			tagField.setValue(searchResult);		
	}


	@Override
	public void setSearchEnabled(boolean enable)
	{
		searchButton.setVisible(enable);
	}

}

package fpozzi.stopper.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import fpozzi.gdoshop.model.articolo.Articolo;
import fpozzi.gdoshop.model.articolo.Codice;
import fpozzi.gdoshop.model.articolo.CodiceEan;
import fpozzi.gdoshop.model.articolo.CodiceInterno;
import fpozzi.gdoshop.model.offerta.PeriodoOfferta;
import fpozzi.stopper.StampaStopperDAO;
import fpozzi.stopper.StampaStopperLogger;
import fpozzi.stopper.StampaStopperProperties;
import fpozzi.stopper.StampaStopperProperties.StampaStopperProperty;
import fpozzi.stopper.model.Card;
import fpozzi.stopper.model.pdf.FruttaPdfStopper;
import fpozzi.stopper.model.pdf.GastroPdfStopper;
import fpozzi.stopper.model.pdf.CardPdf;
import fpozzi.stopper.model.pdf.PdfStopper;
import fpozzi.stopper.model.pdf.PdfStopperRequest;
import fpozzi.stopper.model.pdf.PdfStopperStyle;
import fpozzi.stopper.model.pdf.PdfPromoStopper;
import fpozzi.stopper.model.pdf.TaglioPrezzoPdfStopper;
import fpozzi.stopper.model.pdf.UnsupportedStyleException;
import fpozzi.stopper.view.AddView.AddViewObserver;
import fpozzi.utils.Utils;
import fpozzi.utils.misc.EnumUtils;
import fpozzi.utils.swing.Ean13Field;

public class AddController implements AddViewObserver
{

	private final CodaStampaController codaStampaController;

	private PdfStopperStyle defaultPromoStyle, defaultFruttaStyle, defaultGastroStyle;

	public AddController(CodaStampaController codaStampaController)
	{
		this.codaStampaController = codaStampaController;
		codaStampaController.getView().getAddView().setObserver(this);

		defaultPromoStyle = retrieveDefaultStyle(StampaStopperProperty.DEFAULT_PROMO_STYLE,
				PdfStopperStyle.CARTA_SPECIALE_OFFERTA);
		codaStampaController.getView().getAddView().setDefaultStyle(TaglioPrezzoPdfStopper.class, defaultPromoStyle);

		defaultFruttaStyle = retrieveDefaultStyle(StampaStopperProperty.DEFAULT_FRUTTA_STYLE,
				PdfStopperStyle.META_A4_BN);
		codaStampaController.getView().getAddView().setDefaultStyle(FruttaPdfStopper.class, defaultFruttaStyle);

		defaultGastroStyle = retrieveDefaultStyle(StampaStopperProperty.DEFAULT_GASTRO_STYLE,
				PdfStopperStyle.OTTAVO_A4_BN);
		codaStampaController.getView().getAddView().setDefaultStyle(GastroPdfStopper.class, defaultGastroStyle);

		codaStampaController.getView().getAddView().setDefaultStyle(CardPdf.class, PdfStopperStyle.CARD_COLORI);
	}

	private PdfStopperStyle retrieveDefaultStyle(StampaStopperProperty styleProperty, PdfStopperStyle defaultStyle)
	{
		String stylePropertyValue = StampaStopperProperties.getInstance().getProperty(styleProperty, "");

		if (stylePropertyValue.isEmpty())
		{
			StampaStopperProperties.getInstance().setProperty(styleProperty, defaultStyle.toString());
			StampaStopperProperties.getInstance().store();
		} else
		{
			PdfStopperStyle style;
			try
			{
				style = EnumUtils.valueOfIgnoreCase(PdfStopperStyle.class, stylePropertyValue);
			} catch (Exception e)
			{
				style = defaultStyle;
				StampaStopperProperties.getInstance().setProperty(styleProperty, defaultStyle.toString());
				StampaStopperProperties.getInstance().store();
			}
			defaultStyle = style;
		}
		return defaultStyle;
	}

	@Override
	public void setDefaultStyle(Class<? extends PdfStopper<?>> stopperClass, PdfStopperStyle style)
	{
		if (stopperClass == TaglioPrezzoPdfStopper.class)
		{
			defaultPromoStyle = style;
			StampaStopperProperties.getInstance().setProperty(StampaStopperProperty.DEFAULT_PROMO_STYLE,
					style.toString());
			StampaStopperProperties.getInstance().store();
		} else if (stopperClass == FruttaPdfStopper.class)
		{
			defaultFruttaStyle = style;
			StampaStopperProperties.getInstance().setProperty(StampaStopperProperty.DEFAULT_FRUTTA_STYLE,
					style.toString());
			StampaStopperProperties.getInstance().store();
		} else if (stopperClass == GastroPdfStopper.class)
		{
			defaultGastroStyle = style;
			StampaStopperProperties.getInstance().setProperty(StampaStopperProperty.DEFAULT_GASTRO_STYLE,
					style.toString());
			StampaStopperProperties.getInstance().store();
		}

		codaStampaController.getView().getAddView().setDefaultStyle(stopperClass, style);
	}

	@Override
	public void makeNewRequest(Class<? extends PdfStopper<?>> stopperClass, PdfStopperStyle style)
	{
		PdfStopper<?> defaultPdfStopper = null;

		if (stopperClass == TaglioPrezzoPdfStopper.class)
		{
			defaultPdfStopper = TaglioPrezzoPdfStopper.factory.makeDefaultPdfStopper();
		} else if (stopperClass == FruttaPdfStopper.class)
		{
			defaultPdfStopper = FruttaPdfStopper.factory.makeDefaultPdfStopper();
		} else if (stopperClass == GastroPdfStopper.class)
		{
			defaultPdfStopper = GastroPdfStopper.factory.makeDefaultPdfStopper();
		} else if (stopperClass == CardPdf.class)
		{
			defaultPdfStopper = CardPdf.factory.makeDefaultPdfStopper();
			Card defaultCard = (Card) defaultPdfStopper.getStopper();
			String storedCardPrefix = StampaStopperProperties.getInstance()
					.getProperty(StampaStopperProperty.CARD_PREFIX, "");
			String cardPrefix = storedCardPrefix;
			//
			if (!Utils.isNumeric(cardPrefix))
				cardPrefix = "0000000000000";
			else
			{
				if (cardPrefix.length() > 13)
					cardPrefix = cardPrefix.substring(0, 13);

				while (cardPrefix.length() < 13)
					cardPrefix += "0";

				int parityDigit = Ean13Field.calculateEanParity(cardPrefix);
				cardPrefix = cardPrefix.substring(0, 12) + parityDigit;
			}
			if (!storedCardPrefix.equals(cardPrefix))
			{
				StampaStopperProperties.getInstance().setProperty(StampaStopperProperty.CARD_PREFIX, cardPrefix);
				StampaStopperProperties.getInstance().store();
			}
			defaultCard.setCodiceEan(new CodiceEan(cardPrefix));

		}

		try
		{

			PdfStopperRequest newRequest = new PdfStopperRequest(defaultPdfStopper, style, 1);

			codaStampaController.getView().getAddView().setDefaultStyle(stopperClass, style);

			if (newRequest != null)
			{
				codaStampaController.addRequest(newRequest);
				codaStampaController.getView().selectRequest(newRequest);
			}
		} catch (UnsupportedStyleException e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public List<PeriodoOfferta> getAllPeriodi()
	{
		List<PeriodoOfferta> periodi = Collections.emptyList();
		try
		{
			periodi = Arrays.asList(StampaStopperDAO.getInstance().getPromoDAO().getAllPeriodi());
		} catch (Exception e)
		{
			StampaStopperLogger.get().severe(e.getMessage());
		}
		return periodi;
	}

	@Override
	public void importRequests(PeriodoOfferta periodo)
	{
		try
		{
			List<PdfPromoStopper> stoppers = StampaStopperDAO.getInstance().getPromoDAO().getAll(periodo);
			Calendar cal = GregorianCalendar.getInstance();
			cal.add(Calendar.MONTH, -1);
			Date unMeseFa = cal.getTime();
			
			for (PdfPromoStopper stopper : stoppers)
			{
				int copie = 0;
				Articolo articolo = stopper.getStopper().getArticolo();
					if (articolo.isVenditaAPeso() || 
							(articolo.getDataVendita() != null && !articolo.getDataVendita().before(unMeseFa)) ||
							(articolo.getDataAcquisto()!=null && !articolo.getDataAcquisto().before(unMeseFa)))
						copie++;
				
				codaStampaController.addRequest(new PdfStopperRequest(stopper,
						stopper.convertStyle(PdfStopperStyle.CARTA_SPECIALE_OFFERTA), copie
						));
			}
		} catch (Exception e)
		{
			StampaStopperLogger.get().severe(e.getMessage());
			e.printStackTrace();
		}

	}

	private static FilenameFilter datFileFilter = new FilenameFilter()
	{

		@Override
		public boolean accept(File dir, String name)
		{
			return name.endsWith(".dat");
		}
	};

	@Override
	public List<File> getAllLettoreLaserFiles()
	{
		List<File> files = new LinkedList<File>();
		try
		{
			File gdoShopDir = new File(
					StampaStopperProperties.getInstance().getProperty(StampaStopperProperty.GDOSHOP_DIR));
			if (gdoShopDir.exists() && gdoShopDir.isDirectory())
			{
				File laserScannerDir = new File(gdoShopDir.getAbsolutePath() + "\\BARCODE\\DATA_IN");
				files = Arrays.asList(laserScannerDir.listFiles(datFileFilter));
			}
		} catch (Exception e)
		{
			StampaStopperLogger.get().severe(e.getMessage());
		}
		return files;
	}

	@Override
	public void importRequests(File file)
	{
		try
		{

			List<PdfStopperRequest> requests = new LinkedList<PdfStopperRequest>();

			FileReader input = new FileReader(file);
			BufferedReader bufRead = new BufferedReader(input);
			String riga;
			while ((riga = bufRead.readLine()) != null)
			{
				List<PdfPromoStopper> promoStoppers;
				Codice codice;
				if (riga.startsWith("99"))
				{
					int codiceInternoStart = 2;
					while (riga.charAt(codiceInternoStart) == '9')
						codiceInternoStart++;
					CodiceInterno codiceInterno = new CodiceInterno(riga.substring(codiceInternoStart, 12));
					promoStoppers = StampaStopperDAO.getInstance().getPromoDAO().getAll(codiceInterno);
					codice = codiceInterno;
				} else
				{
					CodiceEan codiceEan = new CodiceEan(riga.substring(0, 13));
					promoStoppers = StampaStopperDAO.getInstance().getPromoDAO().getAll(codiceEan);
					codice = codiceEan;
				}

				if (promoStoppers.isEmpty())
					StampaStopperLogger.get().warning("Nessuna offerta trovata corrispondente al codice " + codice);
				else
				{
					int copie = Integer.valueOf(riga.substring(13, 21).trim());

					for (PdfPromoStopper promoStopper : promoStoppers)
					{
						requests.add(new PdfStopperRequest(promoStopper,
								promoStopper.convertStyle(PdfStopperStyle.CARTA_SPECIALE_OFFERTA), copie));
					}
				}

			}

			bufRead.close();
			input.close();

			for (PdfStopperRequest request : requests)
			{
				codaStampaController.addRequest(request);
			}

			if (codaStampaController.getView().askPermissionToDelete(file))
				file.delete();

		} catch (Exception e)
		{
			StampaStopperLogger.get().severe(e.getMessage());
		}

	}

	@Override
	public void finalize() throws Throwable
	{
		super.finalize();
	}

	public static void main(String args[])
	{
		try
		{
			FileReader input = new FileReader(
					new File("C:\\Users\\lupodellasteppa\\Desktop\\stopper\\BARCODE\\DATA_IN\\Term001.dat"));
			BufferedReader bufRead = new BufferedReader(input);
			String riga;

			while ((riga = bufRead.readLine()) != null)
			{
				String codice = riga.substring(0, 13).trim();
				if (codice.startsWith("99"))
				{
					int codiceInternoStart = 2;
					while (codice.charAt(codiceInternoStart) == '9')
						codiceInternoStart++;
					codice = codice.substring(codiceInternoStart, 12);
				}

				String copie = riga.substring(13, 21).trim();
				System.out.println(codice + " " + copie);
			}
			bufRead.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}

package fpozzi.stopper.serialization;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fpozzi.gdoshop.model.articolo.CodiceEan;
import fpozzi.gdoshop.model.articolo.CodiceInterno;
import fpozzi.gdoshop.model.articolo.Quantita;
import fpozzi.gdoshop.model.articolo.UnitaMisura;
import fpozzi.gdoshop.model.offerta.PeriodoOfferta;
import fpozzi.stopper.model.ArticoloStopper;
import fpozzi.stopper.model.Card;
import fpozzi.stopper.model.FruttaStopper;
import fpozzi.stopper.model.FruttaStopper.Categoria;
import fpozzi.stopper.model.GastronomiaStopper;
import fpozzi.stopper.model.Prezzo;
import fpozzi.stopper.model.PrezzoPromo;
import fpozzi.stopper.model.PromoStopper;
import fpozzi.stopper.model.pdf.CodaStampa;
import fpozzi.stopper.model.pdf.EmptyPdfStopper;
import fpozzi.stopper.model.pdf.FruttaPdfStopper;
import fpozzi.stopper.model.pdf.GastroPdfStopper;
import fpozzi.stopper.model.pdf.CardPdf;
import fpozzi.stopper.model.pdf.PdfStopper;
import fpozzi.stopper.model.pdf.PdfStopperRequest;
import fpozzi.stopper.model.pdf.PdfStopperStyle;
import fpozzi.stopper.model.pdf.PrezzacciPdfStopper;
import fpozzi.stopper.model.pdf.ScontoPercentualePdfStopper;
import fpozzi.stopper.model.pdf.TaglioPrezzoPdfStopper;
import fpozzi.stopper.model.pdf.UnoPiuUnoPdfStopper;
import fpozzi.stopper.model.pdf.UnsupportedStyleException;
import fpozzi.utils.StringUtils;
import fpozzi.utils.misc.EnumUtils;
import fpozzi.utils.xml.PositionalXMLReader;
import fpozzi.utils.xml.XMLException;

public class CodaStampaXMLSerializer
{

	public static final String currentRev = "1.2";

	public static Document serialize(CodaStampa coda) throws ParserConfigurationException
	{

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		Document doc = docBuilder.newDocument();

		Element rootElement = doc.createElement(XMLNames.Elements.root);
		rootElement.setAttribute(XMLNames.Attributes.rev, currentRev);
		doc.appendChild(rootElement);

		Element codaStampaElement;

		for (Entry<PdfStopperStyle, List<PdfStopperRequest>> requestsEntrySet : coda.getRequests().entrySet())
		{
			if (requestsEntrySet.getValue().size() > 0)
			{
				codaStampaElement = doc.createElement(XMLNames.Elements.cs);
				codaStampaElement.setAttribute(XMLNames.Attributes.stile, requestsEntrySet.getKey().toString().toLowerCase());

				rootElement.appendChild(codaStampaElement);

				for (PdfStopperRequest request : requestsEntrySet.getValue())
				{

					codaStampaElement.appendChild(request.makeXMLElement(doc));
				}
			}
		}

		return doc;

	}

	public static CodaStampa deserialize(Document doc) throws XMLException
	{

		CodaStampa coda = new CodaStampa();

		Element rootElement = (Element) doc.getChildNodes().item(0);
		String revisionAttribute = rootElement.getAttribute(XMLNames.Attributes.rev);
		double revision = 1;
		if (!revisionAttribute.isEmpty())
		{
			try
			{
				revision = Double.parseDouble(revisionAttribute);
			}
			catch (NumberFormatException e)
			{
			}
		}

		NodeList codastampaElements = rootElement.getChildNodes(), stopperElements;
		Element codaStampaElement, stopperElement;

		for (int c = 0; (codaStampaElement = (Element) codastampaElements.item(c)) != null; c++)
		{
			String stileAttribute = codaStampaElement.getAttribute(XMLNames.Attributes.stile).trim();
			PdfStopperStyle currentStyle = null;

			try
			{
				if (revision<1.2)
				{
					if (stileAttribute.equalsIgnoreCase("carta_speciale_promo_taglio_prezzo"))
						stileAttribute = "CARTA_SPECIALE_OFFERTA";
					if (stileAttribute.equalsIgnoreCase("carta_speciale_promo_sconto_percentuale"))
						stileAttribute = "CARTA_SPECIALE_SCONTO";
					if (stileAttribute.equalsIgnoreCase("carta_speciale_promo_uno_piu_uno"))
						stileAttribute = "CARTA_SPECIALE_UNOPIUUNO";					
				}
				
				currentStyle = EnumUtils.valueOfIgnoreCase(PdfStopperStyle.class, stileAttribute);
			}
			catch (Exception e)
			{
				throw new XMLException(codaStampaElement, "valore " + stileAttribute + " non valido per l'attributo \"" + XMLNames.Attributes.stile
						+ "\"");
			}

			stopperElements = codaStampaElement.getChildNodes();

			for (int s = 0; (stopperElement = (Element) stopperElements.item(s)) != null; s++)
			{
				PdfStopper<?> pdfStopper = null;
				String stopperType = stopperElement.getNodeName();
				int copie = 1;

				String copieAttribute = stopperElement.getAttribute(XMLNames.Attributes.copie).trim();
				if (!copieAttribute.isEmpty())
					try
					{
						copie = Integer.parseInt(copieAttribute);
						if (copie < 0)
							throw new NumberFormatException();
					}
					catch (NumberFormatException nfe)
					{
						throw new XMLException(stopperElement, "numero di copie non è valido");
					}

				if (stopperType.equals(XMLNames.Elements.stoppervuoto))
				{
					pdfStopper = EmptyPdfStopper.instance;
				}
				else if (stopperType.equals(XMLNames.Elements.card))
				{
					// TAG
					String tag = stringFromChildElement(stopperElement, XMLNames.Elements.tag);
					String eanString = stringFromChildElement(stopperElement, XMLNames.Elements.codiceEan);
					CodiceEan codiceEan = eanString == null ? null : new CodiceEan(eanString);

					Card card = new Card();
					card.setTag(tag);
					card.setCodiceEan(codiceEan);

					pdfStopper = new CardPdf(card);
				}
				else
				{
					// CODICE
					String codiceString = stringFromChildElement(stopperElement, XMLNames.Elements.codice);
					CodiceInterno codice = codiceString == null ? null : new CodiceInterno(codiceString);

					// DESCRIZIONE
					List<String> righeDescrizione = new ArrayList<String>(ArticoloStopper.maxRigheDescrizione);
					Element descrizioneElement = (Element) stopperElement.getElementsByTagName(XMLNames.Elements.descrizione).item(0);
					Element rigaElement;
					NodeList rigaElements = descrizioneElement.getElementsByTagName(XMLNames.Elements.riga);
					for (int i = 0; (rigaElement = (Element) rigaElements.item(i)) != null; i++)
					{
						String rigaText = rigaElement.getTextContent();
						if (revision < 1.1 && stopperType.equals(XMLNames.Elements.stopperpromo))
						{
							rigaText = transformBoldWords(rigaText, StringUtils.allCapitalsToken, 0);
						}

						righeDescrizione.add(rigaText);
					}

					// QUANTITA
					Element quantitaElement;
					NodeList quantitaElements = stopperElement.getElementsByTagName(XMLNames.Elements.quantita);
					List<Quantita> quantita = new ArrayList<Quantita>();
					for (int i = 0; i < quantitaElements.getLength(); i++)
					{
						quantitaElement = (Element) quantitaElements.item(i);
						String udmAttribute = quantitaElement.getAttribute(XMLNames.Attributes.udm);
						String valoreQtaAttribute = quantitaElement.getAttribute(XMLNames.Attributes.valore);
						String multAttribute = quantitaElement.getAttribute(XMLNames.Attributes.mult);

						// UDM
						UnitaMisura udm;
						try
						{
							udm = EnumUtils.valueOfIgnoreCase(UnitaMisura.class, udmAttribute);
						}
						catch (Exception e)
						{
							throw new XMLException(quantitaElement, "unità di misura " + udmAttribute + " non valida");
						}

						// VALORE
						if (valoreQtaAttribute.isEmpty())
						{
							if (!multAttribute.isEmpty())
								throw new XMLException(quantitaElement, "il moltiplicatore non ha senso senza una quantità");
							quantita.add(new Quantita(udm));
						}
						else
						{
							Double valoreQta;
							try
							{
								valoreQta = Double.parseDouble(valoreQtaAttribute);
								if (valoreQta <= 0)
									throw new NumberFormatException();
							}
							catch (Exception e)
							{
								throw new XMLException(quantitaElement, "valore " + valoreQtaAttribute + " non valido per la quantità");
							}
							if (multAttribute.isEmpty())
								quantita.add(new Quantita(udm, valoreQta));
							else
							{
								// MULT
								int mult;
								try
								{
									mult = Integer.parseInt(multAttribute);
									if (mult <= 0)
										throw new NumberFormatException();
								}
								catch (Exception e)
								{
									throw new XMLException(quantitaElement, "valore " + multAttribute + " non valido per il moltiplicatore");
								}
								quantita.add(new Quantita(udm, valoreQta, mult));
							}
						}

					}

					// PREZZO
					Prezzo prezzo = null;
					double prezzoRaw = 0;
					Element prezzoElement = (Element) stopperElement.getElementsByTagName(XMLNames.Elements.prezzo).item(0);
					if (prezzoElement != null)
					{
						String prezzoText = prezzoElement.getTextContent().trim();
						try
						{
							prezzoRaw = Double.parseDouble(prezzoText);
							if (prezzoRaw <= 0)
								throw new NumberFormatException();
						}
						catch (Exception e)
						{
							throw new XMLException(prezzoElement, "valore " + prezzoElement.getTextContent() + " non valido per il prezzo");
						}
					}
					if (prezzoRaw > 0)
						prezzo = new Prezzo(prezzoRaw);

					if (stopperType.equals(XMLNames.Elements.stopperpromo))
					{
						// PREZZO OFFERTA
						double prezzoPromoRaw;
						Element prezzoOffElement = (Element) stopperElement.getElementsByTagName(XMLNames.Elements.prezzoofferta).item(0);
						String prezzoOffText = prezzoOffElement.getTextContent().trim();
						try
						{
							prezzoPromoRaw = Double.parseDouble(prezzoOffText);
							if (prezzoPromoRaw <= 0)
								throw new NumberFormatException();
						}
						catch (Exception e)
						{
							throw new XMLException(prezzoOffElement, "valore " + prezzoOffElement.getTextContent()
									+ " non valido per il prezzo d'offerta");
						}
						PrezzoPromo prezzoPromo = prezzo != null ? new PrezzoPromo(prezzo, prezzoPromoRaw) : new PrezzoPromo(prezzoPromoRaw);

						// Prezzo Per UM Nascosto (OPTIONAL)
						boolean prezzoPerUMNascosto = false;
						Element prezzoPerUMNascostoElement = (Element) stopperElement.getElementsByTagName(XMLNames.Elements.prezzoPerUMNascosto).item(0);
						if (prezzoPerUMNascostoElement != null)
							prezzoPerUMNascosto = true;
						
						// CON CARD (OPTIONAL)
						boolean conCard = false;
						Element conCardElement = (Element) stopperElement.getElementsByTagName(XMLNames.Elements.concard).item(0);
						if (conCardElement != null)
							conCard = true;
						
						// Facoltativo (OPTIONAL)
						boolean facoltativo = false;
						Element facoltativoElement = (Element) stopperElement.getElementsByTagName(XMLNames.Elements.facoltativo).item(0);
						if (facoltativoElement != null)
							facoltativo = true;

						// MAX PEZZI (OPTIONAL)
						int maxPezzi = 0;
						Element maxPezziElement = (Element) stopperElement.getElementsByTagName(XMLNames.Elements.maxpezzi).item(0);
						if (maxPezziElement != null)
						{
							String maxPezziText = maxPezziElement.getTextContent().trim();
							try
							{
								maxPezzi = Integer.parseInt(maxPezziText);
								if (maxPezzi <= 0)
									throw new NumberFormatException();
							}
							catch (Exception e)
							{
								throw new XMLException(maxPezziElement, "valore " + maxPezziElement.getTextContent()
										+ " non valido per numero massimo di pezzi");
							}
						}

						// PERIODO (OPTIONAL)
						PeriodoOfferta periodo = null;
						Element periodoElement = (Element) stopperElement.getElementsByTagName(XMLNames.Elements.periodo).item(0);
						if (periodoElement != null)
						{
							String inizioAttribute = periodoElement.getAttribute(XMLNames.Attributes.inizio).trim();
							String fineAttribute = periodoElement.getAttribute(XMLNames.Attributes.fine).trim();
							try
							{
								periodo = new PeriodoOfferta(inizioAttribute, fineAttribute);
							}
							catch (Exception e)
							{
								throw new XMLException(periodoElement, "valori " + periodoElement.getTextContent()
										+ " non validi per data di inizio o di fine");
							}
						}

						String tipoAttribute = stopperElement.getAttribute(XMLNames.Attributes.tipo).trim();
						if (tipoAttribute.equals(XMLNames.Options.scontopercentuale))
						{
							if (prezzoRaw <= 0)
								throw new XMLException(prezzoElement, "le promo con sconto percentuale non possono avere prezzo = 0");

							int percentualeSconto;
							Element percscontoElement = (Element) stopperElement.getElementsByTagName(XMLNames.Elements.percsconto).item(0);
							if (percscontoElement != null)
							{
								String percscontoText = percscontoElement.getTextContent().trim();
								try
								{
									percentualeSconto = Integer.parseInt(percscontoText);
								}
								catch (Exception e)
								{
									throw new XMLException(percscontoElement, "valore " + percscontoText + " non valido come percentuale di sconto");
								}
								prezzoPromo = new PrezzoPromo(prezzoPromoRaw, percentualeSconto);
							}
						}

						PromoStopper stopper = new PromoStopper();
						stopper.getRigheDescrizione().addAll(righeDescrizione);
						stopper.setCodiceInterno(codice);
						stopper.getQuantita().addAll(quantita);
						stopper.setPrezzo(prezzo);
						stopper.setPrezzoPromo(prezzoPromo);
						stopper.setPeriodo(periodo);
						stopper.setPrezzoPerUMNascosto(prezzoPerUMNascosto);
						stopper.setConCard(conCard);
						stopper.setFacoltativo(facoltativo);
						stopper.setPezziMax(maxPezzi);

						if (tipoAttribute.equals(XMLNames.Options.scontopercentuale))
						{
							pdfStopper = new ScontoPercentualePdfStopper(stopper);
						}
						else if (tipoAttribute.equals(XMLNames.Options.taglioprezzo))
						{
							pdfStopper = new TaglioPrezzoPdfStopper(stopper);
						}
						else if (tipoAttribute.equals(XMLNames.Options.unopiuuno))
						{
							pdfStopper = new UnoPiuUnoPdfStopper(stopper);
						}
						else if (tipoAttribute.equals(XMLNames.Options.sottocosto))
						{
							pdfStopper = new PrezzacciPdfStopper(stopper);
						}
						else
							throw new XMLException(stopperElement, "\"" + tipoAttribute + "\" non è un tipo di stopper promozionale valido");

					}
					else if (stopperType.equals(XMLNames.Elements.stopperfrutta))
					{
						Categoria categoria = Categoria.I;
						{
							Element categoriaElement = (Element) stopperElement.getElementsByTagName(XMLNames.Elements.categoria).item(0);
							if (categoriaElement != null)
								try
								{
									categoria = Categoria.valueOf(categoriaElement.getTextContent().trim());
								}
								catch (IllegalArgumentException e)
								{
								}
						}

						String origine = "Italia";
						{
							Element origineElement = (Element) stopperElement.getElementsByTagName(XMLNames.Elements.origine).item(0);
							if (origineElement != null)
								origine = origineElement.getTextContent().trim();
						}
						
						String calibro = null;
						{
							Element calibroElement = (Element) stopperElement.getElementsByTagName(XMLNames.Elements.calibro).item(0);
							if (calibroElement != null)
								calibro = calibroElement.getTextContent().trim();
						}

						Integer tasto = null;
						{
							Element tastoElement = (Element) stopperElement.getElementsByTagName(XMLNames.Elements.tasto).item(0);
							if (tastoElement != null)
								try
								{
									tasto = Integer.valueOf(tastoElement.getTextContent().trim());
								}
								catch (NumberFormatException e)
								{
								}
						}

						String additivi = null;
						{
							Element additiviElement = (Element) stopperElement.getElementsByTagName(XMLNames.Elements.additivi).item(0);
							if (additiviElement != null)
								additivi = additiviElement.getTextContent();
						}

						FruttaStopper stopper = new FruttaStopper();
						stopper.getRigheDescrizione().addAll(righeDescrizione);
						stopper.setCodiceInterno(codice);
						stopper.getQuantita().addAll(quantita);
						stopper.setPrezzo(prezzo);
						stopper.setCategoria(categoria);
						stopper.setOrigine(origine);
						stopper.setTasto(tasto);
						stopper.setAdditivi(additivi);
						stopper.setCalibro(calibro);
						pdfStopper = new FruttaPdfStopper(stopper);
					}
					else if (stopperType.equals(XMLNames.Elements.stoppergastronomia))
					{
						String ingredienti = null;
						Element ingredientiElement = (Element) stopperElement.getElementsByTagName(XMLNames.Elements.elencoIngredienti).item(0);
						if (ingredientiElement != null)
							ingredienti = ingredientiElement.getTextContent();

						String note = null;
						Element noteElement = (Element) stopperElement.getElementsByTagName(XMLNames.Elements.note).item(0);
						if (noteElement != null)
							note = noteElement.getTextContent();
						
						
						if (revision < 1.1)
						{
							ingredienti = transformBoldWords(ingredienti, StringUtils.asteriskTerminatedToken, 1);
							note = transformBoldWords(note, StringUtils.asteriskTerminatedToken, 1);
						}

						GastronomiaStopper stopper = new GastronomiaStopper();
						stopper.getRigheDescrizione().addAll(righeDescrizione);
						stopper.setCodiceInterno(codice);
						stopper.getQuantita().addAll(quantita);
						stopper.setPrezzo(prezzo);
						stopper.setIngredienti(ingredienti);
						stopper.setNote(note);
						pdfStopper = new GastroPdfStopper(stopper);
					}
				}

				if (pdfStopper != null)
					try
					{
						coda.addRequest(new PdfStopperRequest(pdfStopper, currentStyle, copie));
					}
					catch (UnsupportedStyleException e)
					{
						throw new XMLException(stopperElement, "stile '" + currentStyle + "\' non supportato per questo tipo di stopper");
					}
			}

		}
		return coda;
	}
	
	private static String transformBoldWords(String originalString, Pattern formerBoldWordPattern, int boldWordGroup)
	{
		if (originalString==null)
			return null;
		
		String transformedText = "";
		Matcher matcher = formerBoldWordPattern.matcher(originalString);
		int wordStart = 0;
		while (matcher.find())
		{
			String boldWord = matcher.group();
			transformedText += originalString.substring(wordStart, matcher.start());
			if (!boldWord.trim().isEmpty())
				transformedText += "*" + matcher.group(boldWordGroup) + "*";
			else
				transformedText += boldWord;
			wordStart = matcher.end();
		}
		return transformedText;
	}
	

	public static void store(Document doc, OutputStream os) throws TransformerException
	{

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");

		DOMImplementation domImpl = doc.getImplementation();
		DocumentType doctype = domImpl.createDocumentType(XMLNames.Elements.root, "SYSTEM", "stampastopper.dtd");
		transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
		transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());

		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(os);

		transformer.transform(source, result);

	}

	public static Document read(InputStream is) throws Exception
	{
		return PositionalXMLReader.readXML(is, new File("stampastopper.dtd"));
	}

	public static String stringFromChildElement(Element parentElement, String elementTagName)
	{
		String str = null;
		Element element;
		NodeList nodeList = parentElement.getElementsByTagName(elementTagName);
		if ((element = (Element) nodeList.item(0)) != null)
			str = element.getTextContent().trim();
		return str;
	}
}

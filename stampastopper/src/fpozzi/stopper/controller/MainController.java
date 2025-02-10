package fpozzi.stopper.controller;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import fpozzi.stopper.StampaStopperProperties;
import fpozzi.stopper.StampaStopperLogger;
import fpozzi.stopper.StampaStopperProperties.StampaStopperProperty;
import fpozzi.stopper.model.pdf.CodaStampa;
import fpozzi.stopper.model.pdf.PdfStopperRequest;
import fpozzi.stopper.view.MainView;
import fpozzi.stopper.view.MainView.MainViewObserver;
import fpozzi.utils.Clipboard;
import fpozzi.utils.StringUtils;

public class MainController implements MainViewObserver
{
	private final MainView view;
	
	private final List<CodaStampaController> codaStampaCtrls;

	private final EditorController editorCtrl;

	private CodaStampaController activeCodaStampaCtrl;

	private Clipboard<List<PdfStopperRequest>> clipboard;

	public MainController(MainView view) throws Exception
	{

		this.view = view;
		view.setObserver(this);

		editorCtrl = new EditorController(view.getEditorView());
		
		codaStampaCtrls = new LinkedList<CodaStampaController>();

		activeCodaStampaCtrl = null;

		clipboard = new Clipboard<List<PdfStopperRequest>>();

		String schedeAperteProperty = StampaStopperProperties.getInstance().getProperty(StampaStopperProperty.SCHEDE_APERTE).trim();
		if (schedeAperteProperty.isEmpty())
		{
			open(null);
		}
		else
		{
			String[] schedeAperte = schedeAperteProperty.split(",");
			String schedaAttivaProperty = StampaStopperProperties.getInstance().getProperty(StampaStopperProperty.SCHEDA_ATTIVA, "").trim();
			boolean schedaAttivaEsiste = false;

			File fileScheda;
			for (String nomeFileScheda : schedeAperte)
			{
				fileScheda = new File(nomeFileScheda.trim());
				if (fileScheda.exists())
				{
					open(fileScheda);
					if (nomeFileScheda.equals(schedaAttivaProperty))
						schedaAttivaEsiste = true;
				}
			}

			open(schedaAttivaEsiste? new File(schedaAttivaProperty) : null);
		}
	}

	public MainView getView()
	{
		return view;
	}

	public void setActiveCodaStampaCtrl(CodaStampaController codaStampaCtrl)
	{
		if (activeCodaStampaCtrl != null)
		{
			activeCodaStampaCtrl.getView().detachSelectionObserver(editorCtrl);
		}
		activeCodaStampaCtrl = codaStampaCtrl;
		activeCodaStampaCtrl.getView().attachSelectionObserver(editorCtrl);
		view.setActiveCodaStampaView(activeCodaStampaCtrl.getView());
		editorCtrl.setCurrentStopperRequest(activeCodaStampaCtrl.getView().getSelectedRequest());
	}

	@Override
	public void codaStampaSelected(CodaStampa codaStampa)
	{
		if (activeCodaStampaCtrl != null && activeCodaStampaCtrl.getCodaStampa() == codaStampa)
			return;
		for (CodaStampaController codaStampaCtrl : codaStampaCtrls)
		{
			if (codaStampaCtrl.getCodaStampa() == codaStampa)
			{
				setActiveCodaStampaCtrl(codaStampaCtrl);
				return;
			}
		}

	}

	public void open(File codaStampaFile)
	{
		for (CodaStampaController codaStampaCtrl : codaStampaCtrls)
			if (codaStampaCtrl.getFile() != null && codaStampaCtrl.getFile().equals(codaStampaFile))
			{
				setActiveCodaStampaCtrl(codaStampaCtrl);
				return;
			}
		CodaStampaController newCodaStampaCtrl;
		try
		{
			newCodaStampaCtrl = new CodaStampaController(codaStampaFile, view.makeCodaStampaView());
			codaStampaCtrls.add(newCodaStampaCtrl);
			newCodaStampaCtrl.getView().setClipboard(clipboard);
			view.addCodaStampaView(newCodaStampaCtrl.getView());
			setActiveCodaStampaCtrl(newCodaStampaCtrl);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			StampaStopperLogger.get().severe("Impossibile aprire il file " + codaStampaFile.getName() + ": " + e.getMessage());
		}

	}

	@Override
	public void newCodaStampa()
	{
		try
		{
			open(null);
		}
		catch (Exception e)
		{
			StampaStopperLogger.get().severe("Impossibile aprire il file");
		}
	}

	@Override
	public void save()
	{
		if (activeCodaStampaCtrl != null)
			activeCodaStampaCtrl.save();
	}

	@Override
	public void saveAs(File file)
	{
		if (activeCodaStampaCtrl != null)
			activeCodaStampaCtrl.saveAs(file);
	}

	@Override
	public void saveAll()
	{
		for (CodaStampaController codaStampaCtrl : codaStampaCtrls)
			codaStampaCtrl.save();

	}

	@Override
	public void refresh()
	{
		if (activeCodaStampaCtrl != null)
			activeCodaStampaCtrl.refresh();
	}

	@Override
	public boolean close(CodaStampa codaStampa)
	{
		boolean found;
		int i;
		for (found = false, i = 0; !found && i < codaStampaCtrls.size(); i++)
			found = codaStampaCtrls.get(i).getCodaStampa() == codaStampa;
		if (found)
		{
			if (codaStampaCtrls.get(i - 1).saveAndClose())
				removeControllerAt(i - 1);
			else
				return false;
		}
		return true;
	}

	@Override
	public boolean closeAll()
	{
		int openTabs = codaStampaCtrls.size();
		while (openTabs-- > 0)
		{
			if (activeCodaStampaCtrl.saveAndClose())
			{
				removeControllerAt(codaStampaCtrls.indexOf(activeCodaStampaCtrl));
			}
			else
				return false;
		}
		return true;
	}

	private void removeControllerAt(int ctrlIndex) 
	{
		CodaStampaController removedController = codaStampaCtrls.remove(ctrlIndex);
		removedController.getView().setClipboard(null);
		view.removeCodaStampaView(removedController.getView());
		if (editorCtrl.getCurrentStopperRequest() != null && removedController.getCodaStampa().contains(editorCtrl.getCurrentStopperRequest()))
			editorCtrl.setCurrentStopperRequest(null);
		if (codaStampaCtrls.size() == 0)
			open(null);
		else if (ctrlIndex == codaStampaCtrls.size())
			setActiveCodaStampaCtrl(codaStampaCtrls.get(ctrlIndex - 1));
		else
			setActiveCodaStampaCtrl(codaStampaCtrls.get(ctrlIndex));
	}

	public boolean exit()
	{
		try
		{
			List<CodaStampaController> ctrlBeforeClosing = new LinkedList<CodaStampaController>();
			File fileSchedaAttiva = activeCodaStampaCtrl.getFile();
			ctrlBeforeClosing.addAll(codaStampaCtrls);
			boolean continueClosing = closeAll();
			if (continueClosing)
			{
				List<String> fileSchedeAperte = new LinkedList<String>();
				for (CodaStampaController csCtrl : ctrlBeforeClosing)
					if (csCtrl.getFile() != null)
						fileSchedeAperte.add(csCtrl.getFile().getAbsolutePath());

				StampaStopperProperties.getInstance().setProperty(StampaStopperProperty.SCHEDE_APERTE, StringUtils.join(fileSchedeAperte.toArray(new String[]{}),0, ","));
				StampaStopperProperties.getInstance().setProperty(StampaStopperProperty.SCHEDA_ATTIVA, fileSchedaAttiva != null ? fileSchedaAttiva.getAbsolutePath() : "");
				StampaStopperProperties.getInstance().store();						
			}
						
			return continueClosing;
		}
		catch (Exception e)
		{
			return false;
		}

	}

}

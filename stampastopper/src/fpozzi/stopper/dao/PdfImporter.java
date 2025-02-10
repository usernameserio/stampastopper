package fpozzi.stopper.dao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import fpozzi.stopper.model.pdf.PdfPromoStopper;

public class PdfImporter
{

	public static List<PdfPromoStopper> importFromPdf(final File pdfFile) throws IOException
	{
		PdfReader reader = new PdfReader(pdfFile.getAbsolutePath());

		List<PdfPromoStopper> promoStoppers = new ArrayList<PdfPromoStopper>(reader.getNumberOfPages());

		Scanner scanner;
		String pageText, line;

		try
		{
			
			for (int i = 1; i <= reader.getNumberOfPages(); i++)
			{
				System.out.println(PdfTextExtractor.getTextFromPage(reader, i));
				System.out.println("----------------------------------");
			}

		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			reader.close();
		}

		return promoStoppers;
	}

	public static void main(String[] args)
	{
		try
		{
			importFromPdf(new File("C:\\Users\\lupodellasteppa\\Desktop\\Sigma_0315_LR.pdf"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
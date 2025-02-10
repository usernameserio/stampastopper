package fpozzi.stopper.dao;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.parser.ContentByteUtils;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

public class Import
{

	private final static Pattern actualWordPattern = Pattern.compile("\\((.*?)\\)");

	public static void importFromPdf(final File pdfFile) throws IOException
	{
		PdfReader reader = new PdfReader(pdfFile.getAbsolutePath());

		Matcher matcher;
		String line, extractedText;
		boolean anyMatchFound;
		try
		{
			for (int i = 1; i <= 16; i++)
			{
				byte[] contentBytes = ContentByteUtils.getContentBytesForPage(reader, i);
				RandomAccessFileOrArray raf = new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(contentBytes));
				while ((line = raf.readLine()) != null && !line.equals("BT"))
					;

				extractedText = "";
				while ((line = raf.readLine()) != null && !line.equals("ET"))
				{
					anyMatchFound = false;
					matcher = actualWordPattern.matcher(line);
					while (matcher.find())
					{
						anyMatchFound = true;
						extractedText += matcher.group(1);
					}
					if (anyMatchFound)
						extractedText += "\n";
				}
				System.out.println(extractedText);
				System.out.println("+++++++++++++++++++++++++++");
				String properlyExtractedText = PdfTextExtractor.getTextFromPage(reader, i);
				System.out.println(properlyExtractedText);
				System.out.println("---------------------------");
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
	}
	
	public static void main(String[] args)
	{
		try
		{
			importFromPdf(new File("C:\\Users\\lupodellasteppa\\Desktop\\0116_LR.pdf"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}

package fpozzi.stopper;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import fpozzi.stopper.StampaStopperProperties.StampaStopperProperty;
import fpozzi.stopper.controller.MainController;
import fpozzi.stopper.dao.*;
import fpozzi.stopper.dao.mysql.*;
import fpozzi.stopper.model.pdf.style.fonts.FontFactory;
import fpozzi.stopper.view.swing.MainWindow;
import fpozzi.utils.logging.TextAreaLoggerHandler;

public class StampastopperMain
{
	public static final String version = "0.66";
	public static final String data = "06/02/2024";
	public static final String appTitle = "stampastopper v" + version;

	static
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e)
		{
		}
	}

	public static void main(String[] args)
	{

		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					StampaStopperProperties.Init();
					final StampaStopperProperties properties = StampaStopperProperties.getInstance();

					MysqlDataSource dataSource = new MysqlDataSource();
					dataSource.setServerName(properties.getProperty(StampaStopperProperty.MYSQL_SERVER));
					dataSource.setDatabaseName(properties.getProperty(StampaStopperProperty.MYSQL_DB));
					dataSource.setPort(Integer.parseInt(properties.getProperty(StampaStopperProperty.MYSQL_PORT)));
					dataSource.setUser(properties.getProperty(StampaStopperProperty.MYSQL_USER));
					dataSource.setPassword(properties.getProperty(StampaStopperProperty.MYSQL_PWD));
					dataSource.setZeroDateTimeBehavior("convertToNull");

					MysqlArticoloDAO articoloDAO = new MysqlArticoloDAO(dataSource);
					PromoDAO promoDAO = new MysqlPromoDAO(dataSource, articoloDAO);
					CardDAO cardDAO = new MysqlCardDAO(dataSource);

					StampaStopperDAO.Init(articoloDAO, promoDAO, cardDAO);

					String windowProperty = properties.getProperty(StampaStopperProperty.WINDOW);
					Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
					int x = 0, y = 0, w = 0, h = 0;
					boolean hasValidWindowProperty = false;

					if (windowProperty != null && !windowProperty.trim().isEmpty())
					{
						String[] xywh = windowProperty.split("\\s*,\\s*");
						if (xywh.length == 4)
						{
							try
							{
								x = Integer.parseInt(xywh[0]);
								y = Integer.parseInt(xywh[1]);
								w = Integer.parseInt(xywh[2]);
								h = Integer.parseInt(xywh[3]);
								if (w <= dim.getWidth() && h <= dim.getHeight() && x < dim.getWidth() - 20
										&& y < dim.getHeight())
									hasValidWindowProperty = true;
							} catch (NumberFormatException e)
							{
							}
						}
					}

					if (!hasValidWindowProperty)
					{
						w = 1024;
						h = 840;
						x = (dim.width - w) / 2;
						y = (dim.height - h) / 2;
					}

					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					final MainWindow gui = new MainWindow(appTitle, x, y, w, h);

					List<Image> icons = new ArrayList<Image>();
					Image logo16 = ImageIO.read(getClass().getResource("/img/logo/logo16.png"));
					icons.add(logo16);
					Image logo32 = ImageIO.read(getClass().getResource("/img/logo/logo32.png"));
					icons.add(logo32);
					Image logo48 = ImageIO.read(getClass().getResource("/img/logo/logo48.png"));
					icons.add(logo48);
					Image logo256 = ImageIO.read(getClass().getResource("/img/logo/logo256.png"));
					icons.add(logo256);
					gui.setIconImages(icons);
					StampaStopperLogger.get().addHandler(new TextAreaLoggerHandler(gui.getLogTextArea()));
					StampaStopperLogger.get().info("[" + appTitle + "]");
/*
					Thread thread = new Thread()
					{
						public void run()
						{
							FontFactory.Init();
						}
					};

					thread.start();
*/
					final MainController ctrl = new MainController(gui);

					gui.pack();
					gui.setVisible(true);

					gui.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
					gui.addWindowListener(new WindowAdapter()
					{

						@Override
						public void windowClosing(WindowEvent arg0)
						{
							if (ctrl.exit())
							{
								properties.setProperty(StampaStopperProperty.WINDOW,
										(int) gui.getLocation().getX() + "," + (int) gui.getLocation().getY() + ","
												+ (int) gui.getSize().getWidth() + ","
												+ (int) gui.getSize().getHeight());
								properties.store();
								gui.dispose();
							}
						}
					});
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
}

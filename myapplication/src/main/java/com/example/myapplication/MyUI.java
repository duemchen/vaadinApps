package com.example.myapplication;

import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.servlet.annotation.WebServlet;

import org.json.JSONObject;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.Tooltip;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a html page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("mytheme")
@Widgetset("com.example.myapplication.MyAppWidgetset")
public class MyUI extends UI {

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		final VerticalLayout layout = new VerticalLayout();

		final TextField name = new TextField();
		name.setCaption("Type your name here:");

		Button button = new Button("Click Me");
		button.addClickListener(e -> {
			layout.addComponent(new Label("Thank you, " + name.getValue() + ", it works!"));
		});

		layout.addComponents(name, button);
		layout.setMargin(true);
		layout.setSpacing(true);
		//
		Chart chart = new Chart();
		Configuration conf = chart.getConfiguration();
		conf.setTitle("Temperaturen");
		conf.getChart().setType(ChartType.LINE);
		conf.getyAxis().setTitle("Temperatur");
		conf.getxAxis().setTitle("Uhrzeit");

		Tooltip tooltip = new Tooltip();
		tooltip.setFormatter("this.series.name +' '+ this.y + (this.series.name == 'Rainfall' ? ' mm' : '°C')");
		conf.setTooltip(tooltip);

		DataSeries tempSolar = new DataSeries("Solar");
		DataSeries tempFernVL = new DataSeries("Vorlauf");
		DataSeries tempFernRL = new DataSeries("Rücklauf");
		DataSeries tempSpeicher = new DataSeries("Speicher");
		DataSeries tempWasser = new DataSeries("WarmWasser");
		DataSeries tempFernStadtRL = new DataSeries("FernRück");

		ArrayList<String> liste = new ArrayList<String>();
		String str = "d:/heizung";
		Properties p = new Properties();
		FileInputStream in;
		try {
			File f = new File("properties.txt");
			System.out.println("propertiesFile: "+f.getCanonicalFile());
			if (!f.exists()) {
				p.setProperty("inputfile", "d:/heizung");
				FileOutputStream out = new FileOutputStream("properties.txt");
				p.store(out, null);
			}
			in = new FileInputStream(f);
			p.load(in);
			in.close();
			str = p.getProperty("inputfile");
			System.out.println("inputfile: "+str); // Hier wird ein "Huhu" ausgegeben.			
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		

		fillDateiToArrayList(str, liste);
		// System.out.println(liste);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		// ArrayList<Double> solarL = new ArrayList<>();
		// ArrayList<Double> fernL = new ArrayList<>();
		Date lasttime = new Date();
		for (String s : liste) {
			try {
				JSONObject o = new JSONObject(s);
				JSONObject temps = (JSONObject) o.get("temperatures");

				double koll = temps.getDouble("KOLL_VL");
				double fernVL = temps.getDouble("FERNHEIZ_VORLAUF");
				double fernRL = temps.getDouble("FERNHEIZ_RUECKLAUF");
				double speicher = temps.getDouble("SP1_OBEN");
				double wasser = temps.getDouble("WARMWASSER");
				double fernstadtRL = temps.getDouble("FERN_STADT_RUECK");

				// solarL.add(koll) ;
				// fernL.add(fernVL);

				String stime = o.getString("time");
				Date time = sdf.parse(stime);
				lasttime = time;
				tempSolar.add(new DataSeriesItem(time, koll));
				tempFernVL.add(new DataSeriesItem(time, fernVL));
				tempFernRL.add(new DataSeriesItem(time, fernRL));
				tempSpeicher.add(new DataSeriesItem(time, speicher));
				tempWasser.add(new DataSeriesItem(time, wasser));
				tempFernStadtRL.add(new DataSeriesItem(time,fernstadtRL));
			} catch (Exception e1) {
				System.out.println(e1);

			}
		}
		// 24:00 ! tag fest
		// GregorianCalendar cal = new GregorianCalendar();
		// cal.setTime(lasttime);
		// cal.set(cal.HOUR_OF_DAY, 23);
		// cal.set(cal.MINUTE, 59);
		// cal.set(cal.SECOND, 59);
		// cal.set(cal.MILLISECOND, 0);
		// tempFernVL.add(new DataSeriesItem(cal.getTime(), 0));
		// tempSolar.setData(data);
		// tempFernVL.setData(data);
		conf.addSeries(tempSolar);
		conf.addSeries(tempFernVL);
		conf.addSeries(tempFernRL);
		conf.addSeries(tempSpeicher);
		conf.addSeries(tempWasser);
		conf.addSeries(tempFernStadtRL);
		conf.getxAxis().setType(AxisType.DATETIME);
		layout.addComponent(chart);

		setContent(layout);
	}

	@WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet {
	}

	public static boolean fillDateiToArrayList(String sDatei, ArrayList aL) {
		boolean result = false;
		String line;
		File file = new File(sDatei);
		if (file.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				try {
					while ((line = br.readLine()) != null) {
						aL.add(line);
					}
					// alles korrekt eingelesen
					result = true;
				} finally {
					br.close();
				}
			} catch (IOException e) {
				System.err.println("fillDateiToArrayList: " + e.toString());
			}
		}
		return result;
	}
}

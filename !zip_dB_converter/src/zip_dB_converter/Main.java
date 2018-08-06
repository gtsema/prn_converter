package zip_dB_converter;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Main {

	private static final int WIDTH = 1200;
	private static final int HEIGHT = 900;
	private static final String PATH = ".";
	
	public static void main(String[] args) throws IOException {
			
		if(args.length != 1) {
			System.out.println("You must enter only the file name.");
		} else if(checkFile(args[0])) {
			
			Map<String, Map<Float, Float>> dataset = zipRead(args[0]);
			for(String key : dataset.keySet()) {
				saveToFile(plot(dataset.get(key), key));
			}
			
		} else {
			System.out.println("Something went wrong...");
		}
	}
	
	public static boolean checkFile(String fname) {
		
		String[] files = new File(PATH).list(new FilenameFilter() {
			
			@Override
			public boolean accept(File folder, String name) {
				return name.toLowerCase().endsWith(".zip");
			}
		});
		
		for(String file : files) {
			if((file.equals(fname) || file.split(".zip$")[0].equals(fname))
					&& new File(file).isFile()
					&& new File(file).canRead()) {
				return true;
			}
		}
		return false;
	}

	public static Map<String, Map<Float, Float>> zipRead(String fname) throws FileNotFoundException {
		
		Map<String, Map<Float, Float>> prnFilesParse = new LinkedHashMap<>();
		String filename = fname;
		
		if(!fname.toLowerCase().endsWith(".zip")) {
			filename = filename + ".zip";
		}
		
		try(ZipInputStream zin = new ZipInputStream(new FileInputStream(filename))) {
			
			ZipEntry entry;
			BufferedReader bf = new BufferedReader(new InputStreamReader(zin));
			String data;
			
			while((entry = zin.getNextEntry()) != null) {
				if(entry.getName().endsWith(".prn") && !entry.isDirectory()) {
					
					Map<Float, Float> prnData = new LinkedHashMap<>();
					
					for(int count = 0; (data = bf.readLine()) != null; count++) {
						if(count > 1) {
							prnData.put((Float.parseFloat(data.split("[,]")[0])/1e9f),
									Float.parseFloat(data.split("[,]")[1]));
						}
					}
					prnFilesParse.put(entry.getName(), prnData);
				}
			}
		}
		catch(Exception e) {
			System.out.println("error: " + e.getMessage());
		}
		
		return prnFilesParse;
	}

	public static JFreeChart plot(Map<Float, Float> dataset, String title) {
		
		XYSeries series = new XYSeries("");
		
		for(Map.Entry<Float, Float> entry : dataset.entrySet()) {
			series.add(entry.getKey(), entry.getValue());
		}
					
		XYDataset xyDataset = new XYSeriesCollection(series);
			
		JFreeChart chart = ChartFactory
			    .createXYLineChart("Измерение " + title.split("[.$]")[0],
			        			   "Частота, ГГц",
			        			   "Коэффициент отражения, дБ",
			                       xyDataset, 
			                       PlotOrientation.VERTICAL,
			                       false, false, false);
			
		return chart;
	}

	public static void saveToFile(JFreeChart chart) throws IOException {
		
		BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2 = img.createGraphics();

		chart.draw(g2, new Rectangle2D.Double(0, 0, WIDTH, HEIGHT));

		g2.dispose(); 
		
		File outputfile = new File(PATH + "\\" + chart.getTitle().getText() + "_final.png");
		ImageIO.write(img, "png", outputfile);
	}

}
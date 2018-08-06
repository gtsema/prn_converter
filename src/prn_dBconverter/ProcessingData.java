package prn_dBconverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ProcessingData {
	
	String PATH = null;
	
	public void setPATH(String PATH) {
		this.PATH = PATH;
	}
	
	public float dBconvert(float R) {
		return (float) Math.pow(10, 0.1 * R);
	}
	
	public boolean checkFile() {
		File datafile = new File(PATH);
		return datafile.isFile() && datafile.canRead() ? true : false;
	}
	
	public Map<Float, Float> parser(int mode) {
		
		Map<Float, Float> parseData = new LinkedHashMap<>();
		
		try(BufferedReader bf = new BufferedReader(new FileReader(PATH))) {
			
			String data = null;
			
			for(int count = 0; (data = bf.readLine()) != null; count++) {
				if(count > 1 && mode == 0) {
					parseData.put((Float.parseFloat(data.split("[,]")[0])/1e9f),
								   Float.parseFloat(data.split("[,]")[1]));
				} else if(count > 1 && mode == 1) {
					parseData.put((Float.parseFloat(data.split("[,]")[0])/1e9f),
								   dBconvert(Float.parseFloat(data.split("[,]")[1])));
				}
			}
		} catch (IOException | NumberFormatException e) {
			System.out.println("error: " + e.getMessage());
		}
		return parseData;
	}
	
	public JFreeChart plot(Map<Float, Float> dataset) {
			
		XYSeries series = new XYSeries("");
		
		for(Map.Entry<Float, Float> entry : dataset.entrySet()) {
			series.add(entry.getKey(), entry.getValue());
		}
					
		XYDataset xyDataset = new XYSeriesCollection(series);
			
		JFreeChart chart = ChartFactory
			    .createXYLineChart("Измерение " + new File(PATH).getName().split("[.$]")[0],
			        			   "Частота, ГГц",
			        			   "Коэффициент отражения, дБ",
			                       xyDataset, 
			                       PlotOrientation.VERTICAL,
			                       false, false, false);
			
		return chart;
	}
	
}

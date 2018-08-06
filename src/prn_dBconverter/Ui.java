package prn_dBconverter;

import org.eclipse.swt.widgets.FileDialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jfree.experimental.chart.swt.ChartComposite;

import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Canvas;

public class Ui {

	protected Shell shlPrnDbConverter;

	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlPrnDbConverter.open();
		shlPrnDbConverter.layout();
		while (!shlPrnDbConverter.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public void createContents() {
		
		ProcessingData PD = new ProcessingData();
		
		shlPrnDbConverter = new Shell(SWT.CLOSE | SWT.TITLE | SWT.MIN);
		shlPrnDbConverter.setModified(false);
		shlPrnDbConverter.setSize(800, 550);
		shlPrnDbConverter.setText("prn dB converter");
		
		Canvas canvas = new Canvas(shlPrnDbConverter, SWT.BORDER);
		canvas.setBounds(10, 10, 764, 400);
		
		ChartComposite frame = new ChartComposite(canvas, 0);
		
		Group grpG = new Group(shlPrnDbConverter, SWT.NONE);
		grpG.setFont(SWTResourceManager.getFont("Calibri", 12, SWT.NORMAL));
		grpG.setText("View Mode");
		grpG.setBounds(20, 419, 230, 82);
		
		Button btnDb = new Button(grpG, SWT.RADIO);
		btnDb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!btnDb.getSelection()) {
					frame.setChart(PD.plot(PD.parser(1)));
					frame.chartChanged(null);
				}
			}
		});
		btnDb.setEnabled(false);
		btnDb.setSelection(true);
		btnDb.setFont(SWTResourceManager.getFont("Calibri", 12, SWT.NORMAL));
		btnDb.setBounds(35, 37, 41, 16);
		btnDb.setText("dB");
		
		Button btnPp = new Button(grpG, SWT.RADIO);
		btnPp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!btnPp.getSelection()) {
					frame.setChart(PD.plot(PD.parser(0)));
					frame.chartChanged(null);
				}
			}
		});
		btnPp.setEnabled(false);
		btnPp.setFont(SWTResourceManager.getFont("Calibri", 12, SWT.NORMAL));
		btnPp.setBounds(130, 37, 59, 16);
		btnPp.setText("P1/P2");
		
		Button btnNewButton = new Button(shlPrnDbConverter, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(shlPrnDbConverter, SWT.OPEN);
				String [] filterExtensions = new String [] {"*.prn"};
				dlg.setFilterExtensions(filterExtensions);
				
				String PATH = dlg.open();
			    if (PATH != null) {
			      btnDb.setEnabled(true);
			      btnPp.setEnabled(true);
			      PD.setPATH(PATH);
			      
			      if(PD.checkFile()) {
			    	  frame.setChart(PD.plot(PD.parser(0)));
			    	  frame.setBounds(0, 0, 760, 396);
			    	  frame.chartChanged(null);
			      }
			      
			    }
			}
		});
		btnNewButton.setBounds(272, 447, 100, 35);
		btnNewButton.setText("Open");
		
	}
}

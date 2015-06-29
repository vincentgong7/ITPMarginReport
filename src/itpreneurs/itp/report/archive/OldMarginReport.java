package itpreneurs.itp.report.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class OldMarginReport {
	public static void main(String args[]) throws Exception {
		System.out.println("Test");
		
		ReportConfig config = new ReportConfig("/Users/vincentgong/Desktop/Data for Intercompany Look up.txt");
		OldMarginReport mr = new OldMarginReport(config ,
				"/Users/vincentgong/Desktop/Data for Intercompany Look up.xlsx");
		mr.process();

	}

	private List<SheetModel> sheetList;
	private File workbookFile;

//	private Workbook workbook;
//	private FormulaEvaluator evaluator;
//	private DataFormatter formatter;

	private String MARGIN_TABLE_NAME = "Margin HK3000";
	private String PURCHASE_ORDER_REPORT_TABLE_NAME = "Purchase order report";
	private String IC_SALSE_ORDER_TABLE_NAME = "IC Sales Order";
	private String IC_MARGIN_NL_TABLE_NAME = "IC Margin NL";
	private ReportConfig reportConfig;

	public OldMarginReport(ReportConfig config, String filename) {
		this.workbookFile = new File(filename);
		this.sheetList = new ArrayList<SheetModel>();
		this.reportConfig = config;
	}

	private void process() throws FileNotFoundException,
			InvalidFormatException, IOException {
		ReportParser rp = new ReportParser(this.reportConfig, this.workbookFile);
		this.sheetList = rp.parse();
		
		calculate();
		updateFile();
	}

	
	private void calculate() {
		// TODO Auto-generated method stub
		SheetModel branchMarginSM = getSheetModel(MARGIN_TABLE_NAME);
		Iterator<String> it = branchMarginSM.list.iterator();
		while(it.hasNext()){
			String[] items = it.next().split(",");
			//salesOrder,revenue,grossProfit
			String branchSalesOrderID = items[0];
			String netSalseRevenue = items[1];
			String grossProfit = items[2];
			
			if(netSalseRevenue==null || "".equals(netSalseRevenue)){
				continue;
			}
			
			
		}
	}

	private SheetModel getSheetModel(String sheetModelName) {
		// TODO Auto-generated method stub
		Iterator<SheetModel> it = this.sheetList.iterator();
		while(it.hasNext()){
			SheetModel tsm = it.next();
			if(tsm.name.equals(sheetModelName)){
				return tsm;
			}
		}
		return null;
	}

	private void updateFile() {
		// TODO Auto-generated method stub

	}

	


	

	



	
}

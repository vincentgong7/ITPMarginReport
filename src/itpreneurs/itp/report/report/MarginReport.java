/**
 * 
 */
package itpreneurs.itp.report.report;

import itpreneurs.itp.report.common.MyLineWriter;
import itpreneurs.itp.report.common.MyStringBuffer;
import itpreneurs.itp.report.common.Utils;
import itpreneurs.itp.report.parser.DataContainer;
import itpreneurs.itp.report.parser.MyRow;
import itpreneurs.itp.report.parser.MySheet;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author vincentgong
 *
 */
public class MarginReport {

	private DataContainer dc;
	public static double EUR_USD_EXCHANGE_RATE = 1.10d;

	public MarginReport(DataContainer dc) {
		this.dc = dc;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();

//		 String workbookFileName =
//		 "/Users/vincentgong/Documents/workspaces/Resource/itpreneurs/report/Data_for_Intercompany_Look_up.xlsx";
//		 String configFile =
//		 "/Users/vincentgong/Documents/workspaces/Resource/itpreneurs/report/config.txt";
//		 String outputFileName =
//		 "/Users/vincentgong/Documents/workspaces/Resource/itpreneurs/report/Data_for_Intercompany_Look_up_done.xlsx";

		String folderName = Utils.getPathWithSlash();
//		String folderName="/Users/vincentgong/Documents/workspaces/Resource/itpreneurs/temp/test/";
		String reportFileName = folderName + "report.txt";
		String configFile = folderName + "config.txt";

		// delete the old report file
		File reportFile = new File(reportFileName);
		if (reportFile.exists()) {
			reportFile.delete();
		}

		String outputFolder = "output/";

		File folder = new File(folderName);
		if (!folder.exists()) {
			return;
		}

		MyStringBuffer msb = new MyStringBuffer();
		msb.appendLine("Margin Report Process");
		msb.appendLine();
		msb.appendLine("Start processing, folder: " + folderName);
		msb.appendLine();
		int count = 0;
		
		// for every file in folder
		for (File f : folder.listFiles()) {
			String fileName = f.getName();
			// any EXCEL file, ignoring temporary files (~)
			if ((fileName.endsWith("xlsx") || fileName.endsWith("xls")) && !fileName.startsWith("~$")) {
				count++;
				msb.appendLine("File " + count + ": " + f.getName());
				String outputFileName = folderName + outputFolder + "UPDATED_"
						+ f.getName();
				
				DataContainer dc = new DataContainer(f, outputFileName,
						configFile);
				dc.parseData();
				MarginReport mgr = new MarginReport(dc);
				mgr.process();
				dc.writeToFile();
				msb.appendLine();
			}
		}

		msb.appendLine("Output folder: " + folderName + outputFolder);
		msb.appendLine();
		// calculate the time
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		long totalTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(totalTime);
		totalTimeSeconds++;
		String line = "Processed File(s): " + count + " files, cost time: "
				+ totalTime + "(ms), or: " + totalTimeSeconds + "(s).";
		msb.appendLine(line);
		System.out.println(msb.toString());

		// write the report file
		MyLineWriter.getInstance().writeLine(reportFileName, msb.toString(),
				false);
	}

	public void process() {
	
		MySheet erMs = dc.getSheetbyUniqName("exchange_rate");
		String strExchangeRate = "1.11";
		for(MyRow erMr: erMs.getItemList()){
			strExchangeRate = erMr
					.getVal(erMs
							.getColNewIndex("Exchange Rate"));
		}
		
		System.out.println("Exchange Rate is : "+ strExchangeRate);
		
		double doubleExchangeRate = Double.valueOf(strExchangeRate);
		MarginReport.EUR_USD_EXCHANGE_RATE = doubleExchangeRate;
		
		MySheet ms = dc.getSheetbyUniqName("margin_sheet");

		// step0: build up the new columns and style
		String brachSheetName = ms.getName();
		Sheet targetSheet = this.dc.getWorkbook().getSheet(brachSheetName);

		Row headerRow = targetSheet.getRow(ms.getHederRowNumber());
		Row firstDataRow = targetSheet.getRow(ms.getDataStartRowNumber());
		int lastCellNumOfHeaderRow = headerRow.getLastCellNum();
		int lastCellNumOfDataRow = firstDataRow.getLastCellNum();
		
		System.out.println("In Margin sheet calculation: ");
		System.out.println("Last cell number of header "+lastCellNumOfHeaderRow);
		System.out.println("Last cell number of data row "+lastCellNumOfDataRow);
		
//		if(targetSheet!=null)
//			return;

		// try to hide the original 'GP' and 'GP rate' columns
//		targetSheet
//				.setColumnHidden(ms.getColRowIndex("Gross Profit on Sales"), true);
//		targetSheet
//				.setColumnHidden(ms.getColRowIndex("Gross Profit on Sales %"), true);
		
		// try to build the new 'Total GP' and 'Total GP rate' columns
//		if (lastCellNumOfHeaderRow > lastCellNumOfDataRow) {
//		if (lastCellNumOfHeaderRow > 0) {	
			// the two new header cells existed, only fill the name of each
			// header cells
		Cell icMarginCellHeader = headerRow
				.createCell(lastCellNumOfHeaderRow);
		icMarginCellHeader.setCellValue("Intercompany Margin");
		icMarginCellHeader.setCellStyle(headerRow.getCell(
				lastCellNumOfHeaderRow-1).getCellStyle());	
		
		Cell totalGPCellHeader = headerRow
					.createCell(lastCellNumOfHeaderRow+1);
			totalGPCellHeader.setCellValue("Total Gross Profit on Sales");
			totalGPCellHeader.setCellStyle(headerRow.getCell(
					lastCellNumOfHeaderRow-1).getCellStyle());
			
			Cell totalGPRateCellHeader = headerRow
					.createCell(lastCellNumOfHeaderRow+2);
			totalGPRateCellHeader.setCellValue("Total Gross Profit on Sales %");
			totalGPRateCellHeader.setCellStyle(headerRow.getCell(
					lastCellNumOfHeaderRow-1).getCellStyle());

			// adjust the column width
			targetSheet.autoSizeColumn(lastCellNumOfHeaderRow);
			targetSheet.autoSizeColumn(lastCellNumOfHeaderRow + 1);
//		} 
//		else if (lastCellNumOfHeaderRow == lastCellNumOfDataRow) {
//			// the two new header cells need to be created, the new columns have
//			// no style
//			Cell totalGPCellHeader = headerRow
//					.createCell(lastCellNumOfHeaderRow);
//			totalGPCellHeader.setCellValue("Total Gross Profit on Sales");
//			totalGPCellHeader.setCellStyle(headerRow.getCell(
//					lastCellNumOfHeaderRow - 2).getCellStyle());
//
//			Cell totalGPRateCellHeader = headerRow
//					.createCell(lastCellNumOfHeaderRow + 1);
//			totalGPRateCellHeader.setCellValue("Total Gross Profit on Sales %");
//			totalGPRateCellHeader.setCellStyle(headerRow.getCell(
//					lastCellNumOfHeaderRow - 1).getCellStyle());
//
//			// adjust the column width
//			targetSheet.autoSizeColumn(lastCellNumOfHeaderRow);
//			targetSheet.autoSizeColumn(lastCellNumOfHeaderRow + 1);
//		}

		// step1: for each item in the margin sheet
		for (MyRow mr : ms.getItemList()) {
			// copy the marginGrossProfit and marginGrossProfitRate to the new
			// columns
			// create new cell to the brachMarginSale
			int originRowNumber = mr.originRowNumber;
			Row targetRow = targetSheet.getRow(originRowNumber);
			int lastCellNum = targetRow.getLastCellNum();

//			if(lastCellNum>0) return;
			
			String marginSheetSalesOrderID = mr.getVal(ms
					.getColNewIndex("Sales Document ID"));
			if("1000013544".equals(marginSheetSalesOrderID)){
				System.out.println("this: " + marginSheetSalesOrderID);
			}
			String marginNetSalesRevenue = mr.getVal(ms
					.getColNewIndex("Net Sales Revenue"));
			String marginGrossProfitOnSales = mr.getVal(ms
					.getColNewIndex("Gross Profit on Sales"));
			String marginGrossProfitOnSalesRate = mr.getVal(ms
					.getColNewIndex("Gross Profit on Sales %"));
			String marginProduct = mr.getVal(ms
					.getColNewIndex("Product"));
			
			// create internal company cell and fill it with default value
			Cell icMarginCell = targetRow.createCell(lastCellNum);
//			icMarginCell.setCellStyle(targetRow.getCell(lastCellNum - 2)
//					.getCellStyle());
			icMarginCell.setCellValue(0d);
			
			// create totalGP cell, with content and style
			Cell totalGPCell = targetRow.createCell(lastCellNum+1);
//			totalGPCell.setCellStyle(targetRow.getCell(lastCellNum - 2)
//					.getCellStyle());
			if (marginGrossProfitOnSales != null
					&& marginGrossProfitOnSales.trim().length()>0) {
				
				// in case of "-62.876 EUR", then make it as "-62.876"
				if(marginGrossProfitOnSales.trim().contains(" ")){
					marginGrossProfitOnSales = marginGrossProfitOnSales.trim().split(" ")[0];
//					marginGrossProfitOnSales = marginGrossProfitOnSales.replace(",", "");
				}
				
				double doubleMarginGrossProfitOnSales = Double
						.valueOf(marginGrossProfitOnSales);
				totalGPCell.setCellValue(doubleMarginGrossProfitOnSales);
			}
			else
				totalGPCell.setCellValue("");

			// create totalGPrate cell, with copying the content and style
			Cell totalGPRateCell = targetRow.createCell(lastCellNum + 2);
//			totalGPRateCell.setCellStyle(targetRow.getCell(lastCellNum - 1)
//					.getCellStyle());
			CellStyle cellStyle = this.dc.getWorkbook().createCellStyle();
			cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
			totalGPRateCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			totalGPRateCell.setCellStyle(cellStyle);
			totalGPRateCell.setCellValue(0d);
			
			if (marginGrossProfitOnSalesRate != null
					&& marginGrossProfitOnSalesRate.trim().length()>0) {
//				marginGrossProfitOnSalesRate = marginGrossProfitOnSalesRate.replace(",", ".");
				double doubleMarginGrossProfitOnSalesRate = 0d;
				if(marginGrossProfitOnSalesRate.trim().endsWith("%")){
					marginGrossProfitOnSalesRate = marginGrossProfitOnSalesRate.replace("%", "");
					doubleMarginGrossProfitOnSalesRate = Double
							.valueOf(marginGrossProfitOnSalesRate) / 100;
				}else{
					doubleMarginGrossProfitOnSalesRate = Double
							.valueOf(marginGrossProfitOnSalesRate);
				}
				totalGPRateCell
						.setCellValue(doubleMarginGrossProfitOnSalesRate);
			}
			else
				totalGPRateCell.setCellValue("");

			// skip the rows in which the critical values are empty
			if (marginSheetSalesOrderID == null
					|| marginSheetSalesOrderID.trim().length()<1) { // skip dirty data
				continue;
			}
			if (marginNetSalesRevenue == null
					|| marginNetSalesRevenue.trim().length()<1) { // skip dirty data
				continue;
			}
			if (marginGrossProfitOnSales == null
					|| marginGrossProfitOnSales.trim().length()<1) {
				continue;
			}
			
			if(marginProduct==null || marginProduct.trim().length()<1){
				System.out.println("Product is null in margin report");
				continue;
			}
			else
				System.out.println("product is present for "+marginSheetSalesOrderID+" and it is "+marginProduct);

			// step2: query the sheet: Purchase_order_report_sheet
			MySheet purchaseOrderReportMySheet = dc
					.getSheetbyUniqName("Purchase_order_report_sheet");
			for (MyRow porMr : purchaseOrderReportMySheet.getItemList()) {
				String porSalesOrderID = porMr
						.getVal(purchaseOrderReportMySheet
								.getColNewIndex("Sales Order ID"));
				
//				String poProduct=porMr.getVal(purchaseOrderReportMySheet.getColNewIndex("Product"));
				
				
				if (marginSheetSalesOrderID.trim().equals(
						porSalesOrderID.trim()) 
						// "product" checking
//						&& poProduct.equalsIgnoreCase(marginProduct) 
						) {
					System.out.println("inside matched product and sales order "+marginProduct + " and SO "+marginSheetSalesOrderID);
					
					String porPurchaseOrderID = porMr
							.getVal(purchaseOrderReportMySheet
									.getColNewIndex("Purchase Order ID"));
					if (porPurchaseOrderID == null
							|| porPurchaseOrderID.trim().length()<1) { // skip dirty
																// data
						System.out.println("skipping 2nd step : dirty data for Purchase Order on Purcahse Order");
						continue;
					}

					// step3: query "IC Sales Order" sheet.
					MySheet icSalesOrderSheet = dc
							.getSheetbyUniqName("ic_sales_order_sheet");
					for (MyRow isoMr : icSalesOrderSheet.getItemList()) {
						String isoExternalReference = isoMr
								.getVal(icSalesOrderSheet
										.getColNewIndex("External Reference"));
//						String isoProduct = isoMr
//								.getVal(icSalesOrderSheet
//										.getColNewIndex("Product"));
						
						if (isoExternalReference!=null && porPurchaseOrderID.trim().equals(
								isoExternalReference.trim())
								// "product" checking
//								&& isoProduct.equalsIgnoreCase(poProduct)
								) {
							
							//System.out.println("inside ext ref product and sales order "+marginProduct + " and SO "+marginSheetSalesOrderID);
							String isoSalesOrder = isoMr
									.getVal(icSalesOrderSheet
											.getColNewIndex("Sales Order"));
							if (isoSalesOrder == null
									|| isoSalesOrder.trim().length()<1) { // skip dirty
																	// data
								System.out.println("skipping 3rd step : dirty data for sales order on IC Sales Order");
								continue;
							}

							// step4: query "IC Margin NL" sheet
							MySheet icMarginNLSheet = dc
									.getSheetbyUniqName("ic_margin_nl");
							for (MyRow icMr : icMarginNLSheet.getItemList()) {
								String icSalesDocumentID = icMr
										.getVal(icMarginNLSheet
												.getColNewIndex("Sales Document ID"));
//								String icProduct = icMr
//										.getVal(icMarginNLSheet
//												.getColNewIndex("Product"));
								
								
								if (isoSalesOrder.trim().equals(
										icSalesDocumentID.trim()) 
										 // "product" checking
										//&& icProduct.equalsIgnoreCase(isoProduct)
										) {
									//System.out.println("inside IC NL for product "+marginProduct + " and SO "+icSalesDocumentID);
									
									String icGrossProfitOnSales = icMr
											.getVal(icMarginNLSheet
													.getColNewIndex("Gross Profit on Sales"));
									if (icGrossProfitOnSales == null
											|| icGrossProfitOnSales.trim().length()<1) { // skip
																					// dirty
																					// data
										System.out.println("skipping 4th step : dirty data for gross profit on sales");
										continue;
									}

									// System.out.println(marginSheetSalesOrderID
									// + "	" + marginNetSalesRevenue + " "
									// + marginGrossProfitOnSales + " "
									// + icGrossProfitOnSales);

									// get the double format of all values
									
									// in case of "-62.876 EUR", then make it as "-62.876"
									if(marginGrossProfitOnSales.trim().contains(" ")){
										marginGrossProfitOnSales = marginGrossProfitOnSales.trim().split(" ")[0];
//										marginGrossProfitOnSales = marginGrossProfitOnSales.replace(",", "");
									}
									double doubleBranchMarginGPusd = Double
											.valueOf(marginGrossProfitOnSales);
									
									// in case of "-62.876 EUR", then make it as "-62.876"
									if(icGrossProfitOnSales.trim().contains(" ")){
										icGrossProfitOnSales = icGrossProfitOnSales.trim().split(" ")[0];
//										icGrossProfitOnSales = icGrossProfitOnSales.replace(",", "");
									}
									double doubleIcGPusd = Double
											.valueOf(icGrossProfitOnSales)
											* MarginReport.EUR_USD_EXCHANGE_RATE;
									
									double totalGP = doubleBranchMarginGPusd
											+ doubleIcGPusd;
									
									// in case of "-62.876 EUR", then make it as "-62.876"
									if(marginNetSalesRevenue.trim().contains(" ")){
										marginNetSalesRevenue = marginNetSalesRevenue.trim().split(" ")[0];
//										marginNetSalesRevenue = marginNetSalesRevenue.replace(",", "");
									}
									double doubleBranchRevenueUsd = Double
											.valueOf(marginNetSalesRevenue);
									
									double totalGPonSalesRate = totalGP
											/ doubleBranchRevenueUsd;
									
									System.out.println("Margin Gross Profit " + marginGrossProfitOnSales + ", Intercompany Profit "+icGrossProfitOnSales+" "+(doubleIcGPusd +" USD"));
									System.out.println("Total Gross Profit is "+totalGP);
									System.out.println("Percentage Gross Profit "+totalGPonSalesRate);

									// System.out.println(totalGP + " and "
									// + totalGPonSalesRate + " *100%");
									
									//create internal company cell, with content and style
									icMarginCell.setCellValue(doubleIcGPusd);
									
									// create totalGP cell, with content and
									// style
									totalGPCell.setCellValue(totalGP);

									// create totalGPrate cell, with content and
									// style
									totalGPRateCell
											.setCellValue(totalGPonSalesRate);

								}
							}
						}
					}
				}
			}
		}
		// before write to file
		// remove other sheets
		Workbook wb = this.dc.getWorkbook();
		Sheet sheet;
		while(wb.getNumberOfSheets() > 1){
			 sheet = wb.getSheetAt(0);
			if(sheet!=targetSheet || !targetSheet.equals(sheet)){
				wb.removeSheetAt(0);
			}else{
				wb.removeSheetAt(1);
			}
		}
	}

	public DataContainer getDc() {
		return dc;
	}

	public void setDc(DataContainer dc) {
		this.dc = dc;
	}

}

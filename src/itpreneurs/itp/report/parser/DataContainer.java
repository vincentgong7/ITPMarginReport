/**
 * 
 */
package itpreneurs.itp.report.parser;

import itpreneurs.itp.report.model.ConfigColumn;
import itpreneurs.itp.report.model.ConfigSheet;
import itpreneurs.itp.report.model.MarginReportConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author vincentgong
 *
 */
public class DataContainer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String workbookFileName = "/Users/vincentgong/Documents/workspaces/Resource/itpreneurs/temp/test/MarginReport4-4.xlsx";
		String configFile = "/Users/vincentgong/Documents/workspaces/Resource/itpreneurs/temp/test/config.txt";
		String outputFileName = "/Users/vincentgong/Documents/workspaces/Resource/itpreneurs/temp/test/output/calculated-MarginReport4-4.xlsx";
		DataContainer dc = new DataContainer(workbookFileName, outputFileName,
				configFile);
		dc.parseData();
		System.out.println("done");
	}

	private File workbookFile;
	private File configFile;
	private List<MySheet> sheetList;

	private Workbook workbook = null;
	private DataFormatter formatter = null;
	private FormulaEvaluator evaluator = null;
//	private String separator = null;
	private String outputFile;

	public DataContainer(String workbookFileName, String outputFileName,
			String configFileName) {
		this.configFile = new File(configFileName);
		this.sheetList = new ArrayList<MySheet>();
		this.workbookFile = new File(workbookFileName);
		this.outputFile = outputFileName;
		setup();
	}

	public DataContainer(File workbookFile, String outputFileName, String configFile) {
		this.configFile = new File(configFile);
		this.sheetList = new ArrayList<MySheet>();
		this.workbookFile = workbookFile;
		this.outputFile = outputFileName;
		
		setup();
	}

	public void parseData() {
		try {
			openWorkbook(this.workbookFile);
			preProcess();
			parse();
			// printSheetList();
		} catch (InvalidFormatException | IOException e) {
			e.printStackTrace();
		}
	}

	private void parse() {
		Sheet sheet = null;
		int lastRowNum = 0;

		System.out.println("Start parsing.....");

		for (MySheet ms : this.sheetList) {
			System.out.println("IN SHEET: "+ms.getName());
			
			// get the sheets
			String sheetName = ms.getName();
			sheet = this.workbook.getSheet(sheetName);
			// start parse the meta-data of a sheet
			if (sheet.getPhysicalNumberOfRows() > 0) {

				lastRowNum = sheet.getLastRowNum();
				System.out.println("Last Row is "+lastRowNum);
				// if the numbers are wrong, then skip
				if (lastRowNum < ms.getDataStartRowNumber()) {
					continue;
				}

				// Row dataStartRow = sheet.getRow(ms.getDataStartRowNumber());
				for (int i = ms.getDataStartRowNumber(); i <= lastRowNum; i++) {
					Row row = sheet.getRow(i);
//					System.out.println("Row Number "+ row.getRowNum());
					
					String[] rowValues = new String[ms.getCulumnMap().size()];
					
					Iterator<String> cit = ms.getCulumnMap().keySet()
							.iterator();
					while (cit.hasNext()) {
						String key = cit.next();
//						System.out.println("key is "+key);
						CellPosition cp = ms.getCulumnMap().get(key);
//						System.out.println("rowindex "+cp.rowIndex);
						if(row.getCell(cp.rowIndex)==null){
							rowValues[cp.newIndex]=null;
							continue;
						}
						
						if(cp.rowIndex>-1 && row.getCell(cp.rowIndex)!=null){
							String value = calCell(row.getCell(cp.rowIndex));
							rowValues[cp.newIndex] = value;
						}
						else
							rowValues[cp.newIndex]=null;
					}
					
//					if(ms.getCulumnMap().size()!=rowValues.length){
//						System.out.println();
//						return;
//					}
					MyRow mr = new MyRow(row.getRowNum(), rowValues);
					
//					System.out.println("Row Number:"+mr.originRowNumber);
//					for(int l=0;l<mr.values.length;l++)
//						System.out.println("Row Values are " + mr.values[l]);
					ms.getItemList().add(mr);
				}
			}
		}
	}

	private void preProcess() {
		Sheet sheet = null;
		int lastRowNum = 0;

		System.out.println("Start pre-processing.....");

		for (MySheet ms : this.sheetList) {

			// get the sheets
			String sheetName = ms.getName();
			sheet = this.workbook.getSheet(sheetName);
//			System.out.println("sheet name : "+sheetName);
			// start parse the meta-data of a sheet
			if (sheet.getPhysicalNumberOfRows() > 0) {
				lastRowNum = sheet.getLastRowNum();
				

				// if the numbers are wrong, then skip
				if (lastRowNum < ms.getTitleRowNumber()
						|| lastRowNum < ms.getHederRowNumber()
						|| lastRowNum < ms.getDataStartRowNumber()) {
					continue;
				}
//				lastRowNum++;
				Row titleRow = sheet.getRow(ms.getTitleRowNumber());
				Row headerRow = sheet.getRow(ms.getHederRowNumber());
				// Row dataStartRow = sheet.getRow(ms.getDataStartRowNumber());

				// record the title
				String title = calCell(titleRow.getCell(titleRow
						.getFirstCellNum()));
				ms.setTitle(title);
				
//				System.out.println("title: "+title);
//				System.out.println("lastrow : "+lastRowNum);

				// record the header
				int columnNewIndex = 0;
				Iterator<Cell> cit = headerRow.cellIterator();
				while (cit.hasNext()) {
					Cell cell = cit.next();
					String key = calCell(cell);
					if (ms.getCulumnMap().containsKey(key)) {
						CellPosition cp = ms.getCulumnMap().get(calCell(cell));
						cp.rowIndex = cell.getColumnIndex();
						cp.newIndex = columnNewIndex;
//						System.out.println("Index of "+key+" ROW is "+cp.rowIndex);
//						System.out.println("Index of "+key+" NEWROW is "+cp.newIndex);
						columnNewIndex++;
					}
				}
			}
		}
	}

	private String calCell(Cell cell) {
		if (cell == null) {
			return "";
		} else {
			if (cell.getCellType() != Cell.CELL_TYPE_FORMULA) {
				// return (this.formatter.formatCellValue(cell));
				return (cell.toString());
			} else {
				return (this.formatter.formatCellValue(cell, this.evaluator));
			}
		}
	}

	private void setup() {
		if(this.configFile.getName().endsWith(".xml")){
			setupWithXMLFile();
		}else{
			setupWithPropertiesFile();
		}
		
	}
	
	private void setupWithXMLFile(){
		InputStream is = null;
		try {
			is = new FileInputStream(this.configFile);
			JAXBContext jaxbContext = JAXBContext
					.newInstance(MarginReportConfig.class);
			
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			MarginReportConfig mrc = (MarginReportConfig) jaxbUnmarshaller.unmarshal(is);
			
//			String reportName = mrc.getReport_name();
			for(ConfigSheet cs: mrc.getSheets()){
				List<String> columnNames = new ArrayList<String>();
				
				for(ConfigColumn cc: cs.getColumns()){
					columnNames.add(cc.getColumn_name());
				}
				
				String[] columnNamesStrArrary = new String[columnNames.size()];
				columnNamesStrArrary = columnNames.toArray(columnNamesStrArrary);
				
				MySheet ms = new MySheet(cs.getInternal_sheet_id(), cs.getSheet_name(), cs.getTitle_row_at(),
						cs.getHeader_row_at(), cs.getData_row_start_at(), columnNamesStrArrary);
				
				this.sheetList.add(ms);
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		printSheetList();
	}
	
	private void setupWithPropertiesFile(){
		InputStream is = null;
		try {
			is = new FileInputStream(this.configFile);
			Properties config = new Properties();
			config.load(is);

			// get the config item
			Enumeration<?> e = config.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = config.getProperty(key);

				// margin_sheet = Margin HK3000:0;1;2;Sales Document ID,Net
				// Sales Revenue,Gross Profit on Sales
				// uniqName =
				// sheetName:titleNo;headerNo;dataStartNo;column1,column2,column3,...,columnN
				String uniqName = key; // margin_sheet
				String sheetName = value.split(":")[0]; // Margin HK3000
				String tmpLine = value.split(":")[1];
				int titleRowNumber = Integer.valueOf(tmpLine.split(";")[0]);
				int headerRowNumber = Integer.valueOf(tmpLine.split(";")[1]);
				int dataStartRowNumber = Integer.valueOf(tmpLine.split(";")[2]);
				// Sales Document ID,Net Sales Revenue,Gross Profit on Sales
				String strColumnNames = tmpLine.split(";")[3];
				String[] columnNames;
				if(strColumnNames.contains(",")){
					columnNames = tmpLine.split(";")[3].split(",");
				}else{
					columnNames = new String[1];
					columnNames[0] = strColumnNames;
				}
				
				MySheet ms = new MySheet(uniqName, sheetName, titleRowNumber,
						headerRowNumber, dataStartRowNumber, columnNames);

				this.sheetList.add(ms);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Open an Excel workbook ready for conversion.
	 *
	 * @param file
	 *            An instance of the File class that encapsulates a handle to a
	 *            valid Excel workbook. Note that the workbook can be in either
	 *            binary (.xls) or SpreadsheetML (.xlsx) format.
	 * @throws java.io.FileNotFoundException
	 *             Thrown if the file cannot be located.
	 * @throws java.io.IOException
	 *             Thrown if a problem occurs in the file system.
	 * @throws org.apache.poi.openxml4j.exceptions.InvalidFormatException
	 *             Thrown if invalid xml is found whilst parsing an input
	 *             SpreadsheetML file.
	 */
	public void openWorkbook(File file) throws FileNotFoundException,
			IOException, InvalidFormatException {
		FileInputStream fis = null;
		try {
			System.out.println("Opening workbook [" + file.getName() + "]");

			fis = new FileInputStream(file);

			// Open the workbook and then create the FormulaEvaluator and
			// DataFormatter instances that will be needed to, respectively,
			// force evaluation of forumlae found in cells and create a
			// formatted String encapsulating the cells contents.
			this.workbook = WorkbookFactory.create(fis);
			System.out.println("open done.");
			this.evaluator = this.workbook.getCreationHelper()
					.createFormulaEvaluator();
			this.formatter = new DataFormatter(true);
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
	}

	public void writeToFile() {
		// Write the output to a file
		writeToFile(this.outputFile);
	}

	public void writeToFile(String filename) {
		File f = new File(filename);
		if (!f.exists()) {
			this.buildFolder(f.getParent());
		}
		
		
		// Write the output to a file
		if (this.getWorkbook() instanceof XSSFWorkbook){
			if(filename.endsWith("xls")){
				filename += "x";
			}
		}
		FileOutputStream out;
		try {
			out = new FileOutputStream(filename);
			this.getWorkbook().write(out);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void buildFolder(String path){
		File f = new File(path);
		if(!f.exists()){
			if(!f.getParentFile().exists()){
				this.buildFolder(f.getParent());
			}
			f.mkdir();
		}
	}

	private void printSheetList() {
		System.out.println("Testing the data.");
		for (MySheet ms : this.sheetList) {
			System.out.println(ms.getName());
			System.out.println(ms.getTitle());

			String header = "";
			Iterator<String> cit = ms.getCulumnMap().keySet().iterator();
			while (cit.hasNext()) {
				header = header + cit.next() + "\t";
			}
			System.out.println(header);

			Iterator<MyRow> vit = ms.getItemList().iterator();
			while (vit.hasNext()) {
				String[] array = vit.next().values;
				StringBuilder sb = new StringBuilder();
				for (String s : array) {
					sb.append(s);
				}
				System.out.println(sb.toString());
			}
		}
	}

	public File getWorkbookFile() {
		return workbookFile;
	}

	public void setWorkbookFile(File workbookFile) {
		this.workbookFile = workbookFile;
	}

	public File getConfigFile() {
		return configFile;
	}

	public void setConfigFile(File configFile) {
		this.configFile = configFile;
	}

	public List<MySheet> getSheetList() {
		return sheetList;
	}

	public void setSheetList(List<MySheet> sheetList) {
		this.sheetList = sheetList;
	}

	public Workbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}

	public MySheet getSheetbyUniqName(String uniqName) {
		for (MySheet ms : this.sheetList) {
			if (uniqName.equals(ms.getUniqName())) {
				return ms;
			}
		}
		return null;
	}

	public String getSheetNamebyUniqName(String uniqName) {
		for (MySheet ms : this.sheetList) {
			if (uniqName.equals(ms.getUniqName())) {
				return ms.getName();
			}
		}
		return "";
	}
}

/**
 * 
 */
package itpreneurs.itp.report.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * @author vincentgong
 *
 */
public class ReportParser {

	private ReportConfig reportConfig;
	private Workbook workbook;
	private FormulaEvaluator evaluator;
	private DataFormatter formatter;
	private File workbookFile;

	public ReportParser(ReportConfig reportConfig, File workbookFile) {
		// TODO Auto-generated constructor stub
		this.reportConfig = reportConfig;
		this.workbookFile = workbookFile;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public List<SheetModel> parse() {
		// TODO Auto-generated method stub
		List<SheetModel> list = new ArrayList<SheetModel>();
		
		try {
			openWorkbook(this.workbookFile);
			list =  parseData();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	private List<SheetModel> parseData() throws FileNotFoundException,
			InvalidFormatException, IOException {
		Sheet sheet = null;
		List<SheetModel> list = new ArrayList<SheetModel>();
		// Discover how many sheets there are in the workbook....
		int numSheets = this.workbook.getNumberOfSheets();

		// and then iterate through them.
		for (int i = 0; i < numSheets; i++) {

			// Get a reference to a sheet and check to see if it contains
			// any rows.
			sheet = this.workbook.getSheetAt(i);
			String sheetName = sheet.getSheetName();
			
			if(this.reportConfig.sheetMap.containsKey("sheetName")){
				SheetModel sm = parseSheet(this.reportConfig.sheetMap.get(sheetName), sheet);
				list.add(sm);
			}else{
				continue;
			}
		}
		return list;
	}

	private SheetModel parseSheet(SheetConfig sheetConfig, Sheet sheet) {
		// TODO Auto-generated method stub
		Row row = null;
		int lastRowNum = 0;
		SheetModel sm = new SheetModel(sheet.getSheetName());
		
        if(sheet.getPhysicalNumberOfRows() > 0) {
        	
            lastRowNum = sheet.getLastRowNum();
            
            Row sheetTitleRow = sheet.getRow(0);
            String sheetTitle = getCell(sheetTitleRow.getCell(0));
            
            Row columnTitleRow = sheet.getRow(1);
            for(int i = 0; i<columnTitleRow.getLastCellNum(); i++){
            	String columnTitle = getCell(columnTitleRow.getCell(i));
            	for(ColumnConfig cc : sheetConfig.columnArray){
            		if(cc.ColumnName.equals(columnTitle.trim())){
            			cc.cellIndex = i;
            		}
            	}
            }
            
            for(int j = 2; j <= lastRowNum; j++) {
                row = sheet.getRow(j);
                if(row != null) {
                	
                	String line="";
                	
                	for(int k=0; k<sheetConfig.columnArray.length; k++){
                		ColumnConfig cc = sheetConfig.columnArray[k];
                		line = line + "," + getCell(row.getCell(cc.cellIndex));
                	}
                	
                	line= line.substring(1, line.length());
                	sm.list.add(line);
                }
            }
        }
        return sm;
		
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
	private void openWorkbook(File file) throws FileNotFoundException,
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
			this.evaluator = this.workbook.getCreationHelper()
					.createFormulaEvaluator();
			this.formatter = new DataFormatter(true);
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
	}

	private String getCell(Cell cell) {
		if (cell == null) {
			return "";
		} else {
			if (cell.getCellType() != Cell.CELL_TYPE_FORMULA) {
				return (this.formatter.formatCellValue(cell));
			} else {
				return (this.formatter.formatCellValue(cell, this.evaluator));
			}
		}
	}

}
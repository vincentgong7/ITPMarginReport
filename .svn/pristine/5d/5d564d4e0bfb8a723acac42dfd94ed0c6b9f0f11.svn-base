package itpreneurs.itp.report.archive;

import itpreneurs.itp.report.common.Utils;
import itpreneurs.itp.report.model.ConfigColumn;
import itpreneurs.itp.report.model.ConfigSheet;
import itpreneurs.itp.report.model.MarginReportConfig;
import itpreneurs.itp.report.parser.MySheet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class TestXMLConfig {

	public static void main(String[] args) {
		TestXMLConfig txc = new TestXMLConfig();
//		txc.buildXMLConfug();
		txc.parseXMLConfig();
	}

	private void parseXMLConfig() {

		try {
			File file = new File(Utils.getPathWithSlash() + "config.xml");
			JAXBContext jaxbContext = JAXBContext
					.newInstance(MarginReportConfig.class);
			
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			MarginReportConfig mrc = (MarginReportConfig) jaxbUnmarshaller.unmarshal(file);
			
			String reportName = mrc.getReport_name();
			
			for(ConfigSheet cs: mrc.getSheets()){
				List<String> columnNames = new ArrayList<String>();
				
				for(ConfigColumn cc: cs.getColumns()){
					columnNames.add(cc.getColumn_name());
				}
				
				String[] columnNamesStrArrary = new String[columnNames.size()];
				columnNamesStrArrary = columnNames.toArray(columnNamesStrArrary);
				
				MySheet ms = new MySheet(cs.getInternal_sheet_id(), cs.getSheet_name(), cs.getTitle_row_at(),
						cs.getHeader_row_at(), cs.getData_row_start_at(), columnNamesStrArrary);
				
				System.out.println(ms.toString());
			}
			System.out.println();
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	/*
	 * format: internal_sheet_id = sheet name : title row number; header row
	 * number; starting data row number; column1, column2, column3, …, colunmN
	 * margin_sheet = Margin Report:0;1;2;Sales Document ID,Net Sales
	 * Revenue,Gross Profit on Sales,Gross Profit on Sales %
	 */
	private void buildXMLConfug() {
		// TODO Auto-generated method stub

		MarginReportConfig mrc = buildConfigObject();

		try {
			File file = new File(Utils.getPathWithSlash() + "config.xml");
			JAXBContext jaxbContext = JAXBContext
					.newInstance(MarginReportConfig.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			 
			jaxbMarshaller.marshal(mrc, file);
			jaxbMarshaller.marshal(mrc, System.out);
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private MarginReportConfig buildConfigObject() {
		MarginReportConfig mrc = new MarginReportConfig();
		mrc.setReport_name("Margin Report");

		List<ConfigSheet> sheets = new ArrayList<ConfigSheet>();
		ConfigSheet cs = new ConfigSheet();
		cs.setInternal_sheet_id("margin_sheet");
		cs.setSheet_name("Margin Report");
		cs.setTitle_row_at(0);
		cs.setHeader_row_at(1);
		cs.setData_row_start_at(2);
		
		List<ConfigColumn> columns = new ArrayList<ConfigColumn>();

		columns.add(new ConfigColumn("Sales Document ID"));
		columns.add(new ConfigColumn("Net Sales Revenue"));
		columns.add(new ConfigColumn("Gross Profit on Sales"));
		columns.add(new ConfigColumn("Gross Profit on Sales %"));

		cs.setColumns(columns);
		sheets.add(cs);
		mrc.setSheets(sheets);

		return mrc;
	}

}

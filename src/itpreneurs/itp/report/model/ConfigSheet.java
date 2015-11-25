package itpreneurs.itp.report.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;

public class ConfigSheet {
	/*
	 * format: internal_sheet_id = sheet name : title row number; header row
	 * number; starting data row number; column1, column2, column3, …, colunmN
	 * margin_sheet = Margin Report:0;1;2;Sales Document ID,Net Sales
	 * Revenue,Gross Profit on Sales,Gross Profit on Sales %
	 */
	private String internal_sheet_id;
	private String sheet_name;
	private int title_row_at;
	private int header_row_at;
	private int data_row_start_at;
	private List<ConfigColumn> columns;

	public String getInternal_sheet_id() {
		return internal_sheet_id;
	}

	public void setInternal_sheet_id(String internal_sheet_id) {
		this.internal_sheet_id = internal_sheet_id;
	}

	public String getSheet_name() {
		return sheet_name;
	}

	public void setSheet_name(String sheet_name) {
		this.sheet_name = sheet_name;
	}

	public List<ConfigColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<ConfigColumn> columns) {
		this.columns = columns;
	}

	@XmlAttribute(name="title_row_at")
	public void setTitle_row_at(int title_row_at) {
		this.title_row_at = title_row_at;
	}

	public int getHeader_row_at() {
		return header_row_at;
	}
	@XmlAttribute(name="header_row_at")
	public void setHeader_row_at(int header_row_at) {
		this.header_row_at = header_row_at;
	}

	public int getData_row_start_at() {
		return data_row_start_at;
	}
	@XmlAttribute(name="data_row_start_at")
	public void setData_row_start_at(int data_row_start_at) {
		this.data_row_start_at = data_row_start_at;
	}

	public int getTitle_row_at() {
		return title_row_at;
	}

}

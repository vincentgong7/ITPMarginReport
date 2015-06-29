package itpreneurs.itp.report.parser;

public class CellPosition {
	public CellPosition(String colunmName) {
		this.name = colunmName;
	}
	
	String name;
	int rowIndex;
	int newIndex;
}

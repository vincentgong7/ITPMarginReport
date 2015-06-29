/**
 * 
 */
package itpreneurs.itp.report.archive;

import itpreneurs.itp.report.common.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author vincentgong
 *
 */
public class ReportConfig {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

	private String configFile;
	
	public Map<String, SheetConfig> sheetMap;

	public ReportConfig(String configFile){
		this.configFile = configFile;
		readConfigFile();
		
	}
	
	private void readConfigFile() {
		// TODO Auto-generated method stub
		InputStream is = null;
		try {
			is = new FileInputStream( Utils.getPath() +"/" + this.configFile);
			Properties config = new Properties();
			config.load(is);
			
			// read sheets
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

}

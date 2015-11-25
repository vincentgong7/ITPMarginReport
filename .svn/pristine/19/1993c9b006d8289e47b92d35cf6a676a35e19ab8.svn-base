/**
 * 
 */
package itpreneurs.itp.report.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * {@link MyLineWriter} is a shared tool Class to write one String to a file line by line.
 * Note: It's a Singleton class. Please use it in the proper way. See sample in main().
 * @author vincentgong Singleton Line Writer in java.
 */
public class MyLineWriter {

	private static MyLineWriter instance;

	private MyLineWriter() {
	}

	public static MyLineWriter getInstance() {
		if (instance == null) {
			instance = new MyLineWriter();
		}
		return instance;
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String sampleStr = "This is the sample code.";
		MyLineWriter.getInstance().writeLine("Sample.txt", sampleStr, true);
		
	}
	
	/**
	 * @param append=true: line will be written to the rare of the file.
	 * @throws Exception 
	 */
	public void writeLine(String fileName, String line, boolean append)
			throws Exception {
		File file = new File(fileName);
		writeLine(file, line, append);
	}
	
	public void writeLine(File file, String line, boolean append)
			throws Exception {
		
		boolean newFile = false;
		File f = file;
		if (!f.exists()) {
			this.buildFolder(f.getParent());
			f.createNewFile();
			newFile = true;
		}

//System.out.println(f.getAbsolutePath()); // show the path of the file
		
		BufferedWriter bw;
		if (append) {
			bw = new BufferedWriter(new FileWriter(f, true));
		} else {
			bw = new BufferedWriter(new FileWriter(f));
		}
		
		if(!newFile){
			bw.newLine();
		}
		bw.write(line);
		bw.close();
	}
	
	/**
	 * @param Line will be written to a new file, which means over write the file existed with the same name.
	 * @throws Exception 
	 */
	public void writeLine(String fileName, String line) throws Exception{
		writeLine(fileName, line, true);
	}
	
	public void writeLine(File file, String line) throws Exception{
		writeLine(file, line, true);
	}
	
	public void writeLine(String fileName) throws Exception{
		writeLine(fileName, "", true);
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
	
	public static void copyFile(File sourceFile, File destFile) throws IOException {
	    if(!destFile.exists()) {
	        destFile.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;

	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
	
	public String DateAsString4FileName(){
		Date date = new Date() ;
		return DateAsString4FileName(date);
	}
	
	public String DateAsString4FileName(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss") ;
		String result = dateFormat.format(date);
		return result;
	}
	
}

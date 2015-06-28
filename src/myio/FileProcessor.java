package myio;

import java.awt.Component;
import java.io.File;

import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class FileProcessor {
	
	private File file;
	private JFileChooser filechooser;
	private Formatter writer;
	private Scanner reader;
	private String readram;
	private FileFilter filter;
	@SuppressWarnings("unused")
	private static final String VERSION = "v1.0";
	
	public boolean setFile(Component c, boolean saving){
		
		filechooser = new JFileChooser();
		
		int value;
		if(saving){
			value = filechooser.showSaveDialog(c);		
		}else{
			value = filechooser.showOpenDialog(c);
		}
		if(value == JFileChooser.APPROVE_OPTION){
			file = new File(filechooser.getSelectedFile().getAbsolutePath());
			try {
				if(saving){
					writer = new Formatter(file);	
				}				
				reader = new Scanner(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return true;
		}else{
			return false;
		}
		
	}
	public boolean setFile(Component c, String extension, boolean saving){
		
		final String ext = extension;
		filechooser = new JFileChooser();
		filter = new FileFilter(){
			public boolean accept(File f) {
				return f.isDirectory() || f.getAbsolutePath().endsWith(ext);
			}		
			public String getDescription() {				
				return "Only " + "(" + ext + ")";
			}			
		};		
		filechooser.setAcceptAllFileFilterUsed(false);
		filechooser.addChoosableFileFilter(filter);
		
		int value;
		if(saving){
			value = filechooser.showSaveDialog(c);		
		}else{
			value = filechooser.showOpenDialog(c);
		}
		if(value == JFileChooser.APPROVE_OPTION){
			if(saving){
				String filename[] = filechooser.getSelectedFile().getAbsolutePath().split("\\.");
				
				file = new File(filename[0] + ext);
			}else{
				file = new File(filechooser.getSelectedFile().getAbsolutePath());
			}
			try {
				if(saving){
					writer = new Formatter(file);	
				}
				reader = new Scanner(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return true;
		}else{
			return false;
		}
		
	}
	public void setFile(String path){
		
		file = new File(path);
		try {
			reader = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
				
	}
	
	public boolean setFile(boolean b){		
		return setFile((Component)null, b);	
	}
	
	public String readFile(){
		readram = "";
		reader.useDelimiter(System.getProperty("line.separator"));
		while(reader.hasNext()){
			readram += reader.next() + "\n";
		}
		reader.close();
		return readram;		
	}
	
	public String[] readFile(String delimiter){
		
		return readFile().split(delimiter);	
	}
	
	public void writeFile(String str){
		try {
			writer = new Formatter(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		writer.format(str);
		writer.close();
	}
	
	
}

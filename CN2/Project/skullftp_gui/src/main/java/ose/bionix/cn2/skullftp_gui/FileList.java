package ose.bionix.cn2.skullftp_gui;

// Helper object to store ls()'s output as something edible for JTable
public class FileList {
	private final String name;
	private final String size;
	private final String date;
	private final boolean isdir;

	public FileList(String name, String size, String date, boolean isdir) {
		this.name = name;
		this.size = size;
		this.date = date;
		this.isdir = isdir;
	}
	public String getName() {return name;}
	public String getSize() {return isdir ? "<DIR>" : size;}
	public String getDate() {return date;}
	public boolean isDir() {return isdir;}
}

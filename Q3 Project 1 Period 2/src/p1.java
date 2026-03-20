import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class p1 {
	private static String[][] map;
	private static int col;
	private static int row;
	private static int mazeNum;
	public static void main(String[] args) throws IllegalAccessException{
		
	}
	public static void getMap(String file){
		File mapfile=new File(file); //make the file obj
		try {
			Scanner scan=new Scanner(mapfile);
			row=Integer.parseInt(scan.next()); // first int is row #
			col=Integer.parseInt(scan.next()); // second is column #
			mazeNum=Integer.parseInt(scan.next());
			map=new String[row*mazeNum][col]; //2d array to add elements
			for(int i=0;i<map.length;i++) { //making the map into the 2d array
				String r=scan.next();
				for(int j=0;j<map[0].length;j++) {
					String el=r.substring(j,j+1);
					map[i][j]=el;
				}
			}
			scan.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found: "+e.getMessage());
		}
	}
}
	
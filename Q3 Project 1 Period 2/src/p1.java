import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;

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
	public static void stack() {
	    int z=0;//map #
	    Deque<ArrayList<Integer>> st=new ArrayDeque<>();
	    ArrayList<ArrayList<Integer>> starts=new ArrayList<>(); //beginning
	    HashMap<ArrayList<Integer>,ArrayList<Integer>> path=new HashMap<>(); //keep track of order
	    ArrayList<Integer> buck = new ArrayList<>();//buck cord
	    for (int i=0;i<map.length;i++){ //find buck
	        for (int j=0;j<map[0].length;j++){
	            if (map[i][j].equals("W")){
	                ArrayList<Integer> p = new ArrayList<>(Arrays.asList(i-z*row,j,z));
	                starts.add(p);
	                z++;//change map number
	            }
	        }
	    }
	    int[] d={-1,0,1,0,0,1,0,-1};//north south east west 
	    if(!starts.isEmpty()) st.push(starts.get(0));//add starting to stack
	    while(!st.isEmpty()&&buck.isEmpty()){
	        ArrayList<Integer> cur=st.pop();//pop
	        int cx=cur.get(0),cy=cur.get(1),cz=cur.get(2);
	        for(int i=0;i<d.length;i+=2){
	            int nx=cx+d[i],ny=cy+d[i+1];
	            if(nx>=0&&nx<row&&ny>=0&&ny<col) { //check bound
	                ArrayList<Integer> next=new ArrayList<>(Arrays.asList(nx,ny,cz));
	                String tile=map[nx+cz*row][ny];
	                //check if neighbor is walkable or buck
	                if(!path.containsKey(next)) { //only visits new
	                    if(tile.equals("$")) {
	                        buck.addAll(next); 
	                        path.put(next,cur); //save for tracing later
	                        break;
	                    }else if(tile.equals(".")) {
	                        path.put(next,cur); //marks and record parent
	                        st.push(next); //add to stack to check later
	                    }
	                }
	            }
	        }
	    }
	}
}
	
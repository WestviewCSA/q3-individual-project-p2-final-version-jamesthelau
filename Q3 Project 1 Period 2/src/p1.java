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
	private static String[][] map; //main map
	private static int col,row, numMaze; //variables for which maze, # of rows and columns
	private static ArrayList<ArrayList<Integer>> res=new ArrayList<>(); //
	private static boolean closed=false;
	private static long start,end; //long is a int for larger capacity, these are used to track time
	private static boolean st,q,opt,time,in,out,help;//use as switches, but start off false
	public static void main(String[] args) {
		int count=0;
		for (int i=0;i<args.length;i++) {//go through all all values in args
			String a=args[i]; //these are the command line arguements, finding the flags to switch the boolean to true and adding to the count
			if(a.equals("--stack")) {
				st=true; count++; }
			else if(a.equals("--queue")) {
				q=true;count++;
			}
			else if (a.equals("--opt")) {//if the value ever equals 
				opt = true;count++;}
			else if(a.equals("--time")) time=true;
			else if(a.equals("--in")) in=true;
			else if(a.equals("--out")) out=true;
			else if(a.equals("--Help")) help=true;
		}
		if(help) {//explain the functions
			System.out.println("use the --stack, --queue, and --opt to find a path to the Wolverine Buck");
			System.out.println("--opt to find the shortest path");
			System.out.println("--time to get total runtime");
			System.out.println("--in for coordinate input format");
			System.out.println("--out for coord output format");
			return;
		}
		if(count!=1) {
			System.out.println("Use exactly one input");
			System.exit(-1);
		}
		String f=args[args.length-1];
		
	}
	public static void getMap(String file) {
		File mapfile=new File(file); //make the file obj
		try (Scanner scan=new Scanner(file)) {
			int row=scan.nextInt(); // first int is row #
			int col=scan.nextInt(); // second is column #
			int mazeNum=scan.nextInt();
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
	    for (int i=0;i<map.length;i++) { //find buck
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
	    while(!st.isEmpty()&&buck.isEmpty()) {
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
	public static void queueBased() {
		int z=0;//map #
		Queue<ArrayList<Integer>> q=new LinkedList<>();
		ArrayList<ArrayList<Integer>> starts=new ArrayList<>();//beginning
		HashMap<ArrayList<Integer>,ArrayList<Integer>> path=new HashMap<>();//order tracking
		ArrayList<Integer> buck=new ArrayList<>();//buck cord
		for(int i=0;i<map.length;i++) { //find starts
			for(int j=0;j<map[0].length;j++) {
				if(map[i][j].equals("W")) {
					ArrayList<Integer> p=new ArrayList<>(Arrays.asList(i-z*row,j,z));
					starts.add(p);
					z++;
				}
			}
		}
		int[] d={-1,0,1,0,0,1,0,-1};//north south east west
		if(!starts.isEmpty()) q.add(starts.get(0));//add start to queue
		while(!q.isEmpty()&&buck.isEmpty()) {
			ArrayList<Integer> cur=q.remove();//dequeue
			int cx=cur.get(0),cy=cur.get(1),cz=cur.get(2);
			for(int i=0;i<d.length;i+=2) {
				int nx=cx+d[i],ny=cy+d[i+1];
				if(nx>=0&&nx<row&&ny>=0&&ny<col) { //check bound
					ArrayList<Integer> next=new ArrayList<>(Arrays.asList(nx,ny,cz));
					String tile=map[nx+cz*row][ny];
					if(!path.containsKey(next)) { //only visit new
						if(tile.equals("$")) {
							buck.addAll(next);
							path.put(next,cur); //save for trace
							break;
						}else if(tile.equals(".")||tile.equals("|")) {
							path.put(next,cur); //mark visited
							q.add(next);
							if(tile.equals("|")) { //portal to next maze
								ArrayList<Integer> nexM=starts.get(cz+1);
								q.add(nexM);
								path.put(nexM,next);
							}
						}
					}
				}
			}
		}
		traceback(starts,buck,path);//trace back path
	}
}
	
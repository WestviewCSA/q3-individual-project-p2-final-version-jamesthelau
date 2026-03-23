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
			if(a.equals("--Stack")) {
				st=true;count++;
			}
			else if(a.equals("--Queue")) {
				q=true;count++;
			}
			else if (a.equals("--Opt")) {
				opt=true;count++;
			}
			else if(a.equals("--Time")) {
				time=true;
			}
			else if(a.equals("--Incoordinate")) {
				in=true;
			}
			else if(a.equals("--Outcoordinate")) {
				out=true;
			}
			else if(a.equals("--Help")) {
				help=true;
			}
		}
		if(help) {//explain the functions
			System.out.println("use the --Stack, --Queue, and --Opt to find a path to the Wolverine Buck");
			System.out.println("--Opt to find the shortest path");
			System.out.println("--Time to get total runtime");
			System.out.println("--Incoordinate for coordinate input format");
			System.out.println("--Outcoordinate for coord output format");
			return;
		}
		if(count!=1) {
			System.out.println("Use exactly one input");
			System.exit(-1);
		}
		
		String f=args[args.length-1];
		if(in) {
			getMap(f);
		}
		else {
			getMapGrid(f);
		}
		
		start=System.nanoTime(); //googled these parts but its just a few lines
		if(st) {
			stack();
		}
		else if(q||opt) {
			queueBased();
		}
		end=System.nanoTime();
		
		if(out) {
			printCoor();
		}
		else {
			printMap();
		}
		
		if(time) {
			System.out.println("Runtime: "+((end-start)/1000000000.0)+"s");
		}
		
		//reset instance variables for next test
		row=0;col=0;numMaze=0; //reset dimentions
		map=new String[0][0]; //clear old map data
		res=new ArrayList<>(); //clear path results
		closed=false; //reset solver state
		start=0;end=0; //reset timer
		st=false;q=false;opt=false;time=false;in=false;out=false;help=false; //reset switches
	}
	public static void getMap(String file) {
		try {
			File fl=new File(file);
			Scanner sc=new Scanner(fl); //fixed scanner to read file
			row=sc.nextInt(); //first 3 are dims
			col=sc.nextInt();
			numMaze=sc.nextInt();
			if(row<=0||col<=0||numMaze<=0) {
				return;
			}
			map=new String[row*numMaze][col]; //setup map
			while(sc.hasNext()) { //read symbol then 3 ints
				String s=sc.next();
				int r=sc.nextInt(),c=sc.nextInt(),m=sc.nextInt();
				if(r>=0&&r<row&&c>=0&&c<col&&m>=0&&m<numMaze) { //if it fits
					map[r+m*row][c]=s;
				}
			}
			for(int i=0;i<map.length;i++) { //fill empty spots with dots
				for(int j=0;j<col;j++) {
					if(map[i][j]==null) {
						map[i][j]=".";
					}
				}
			}
			sc.close();
		} catch(Exception e) {
			System.out.println("Error: "+e.getMessage());
		}
	}
	public static void getMapGrid(String file) { //logic for grid format
		try {
			File fl=new File(file);
			Scanner sc=new Scanner(fl); //fixed scanner
			row=sc.nextInt();
			col=sc.nextInt();
			numMaze=sc.nextInt();
			map=new String[row*numMaze][col];
			for(int i=0;i<map.length;i++) { //read rows
				String line=sc.next();
				for(int j=0;j<col;j++) {
					map[i][j]=line.substring(j,j+1); //put char in map
				}
			}
			sc.close();
		} catch(Exception e) {
			System.out.println("Error: "+e.getMessage());
		}
	}
	public static void printMap() {
		if(!closed) { //check if solved
			for(ArrayList<Integer> p:res) { //mark path dots
				int r=p.get(0),c=p.get(1),z=p.get(2);
				if(map[r+z*row][c].equals(".")) {
					map[r+z*row][c]="+";
				}
			}
			for(String[] r:map) { //print every row
				for(String s:r) {
					System.out.print(s);
				}
				System.out.println();
			}
		}
	}

	public static void printCoor() {
		if(!closed) { //check if solved
			for(int i=0;i<res.size();i++) { //go through path
				ArrayList<Integer> p=res.get(i);
				int r=p.get(0),c=p.get(1),z=p.get(2);
				if(map[r+z*row][c].equals(".")) { //only print dot path
					System.out.println("+ "+r+" "+c+" "+z);
				}
			}
		}
	}
	public static void traceback(ArrayList<ArrayList<Integer>> starts,ArrayList<Integer> buck,HashMap<ArrayList<Integer>,ArrayList<Integer>> path){
	    if(buck.isEmpty()){ //check if buck exists
	        closed=true;
	        System.out.println("The Wolverine Store is closed.");
	        return;
	    }
	    ArrayList<Integer> c=new ArrayList<>(Arrays.asList(buck.get(0),buck.get(1),buck.get(2)));
	    ArrayList<Integer> startNode=starts.get(0); //root start
	    while(c!=null&&!c.equals(startNode)){ //trace until reaching start
	        res.add(0,c); //add to front
	        c=path.get(c); //get parent
	    }
	}
	public static void stack() {
		int z=0;//map #
		Deque<ArrayList<Integer>> st=new ArrayDeque<>();
		ArrayList<ArrayList<Integer>> starts=new ArrayList<>(); //beginning
		HashMap<ArrayList<Integer>,ArrayList<Integer>> path=new HashMap<>(); //track order
		ArrayList<Integer> buck=new ArrayList<>();//buck cord
		for(int i=0;i<numMaze;i++) { //find starts for each level
			for(int r=0;r<row;r++) {
				for(int j=0;j<col;j++) {
					if(map[r+i*row][j].equals("W")) {
						starts.add(new ArrayList<>(Arrays.asList(r,j,i)));
					}
				}
			}
		}
		int[] d={-1,0,1,0,0,1,0,-1};//north south east west 
		if(!starts.isEmpty()) {
			st.push(starts.get(0));//add start to stack
			path.put(starts.get(0),null);//start has no parent
		}
		while(!st.isEmpty()&&buck.isEmpty()) {
			ArrayList<Integer> cur=st.pop();//pop
			int cx=cur.get(0),cy=cur.get(1),cz=cur.get(2);
			for(int i=0;i<d.length;i+=2) {
				int nx=cx+d[i],ny=cy+d[i+1];
				if(nx>=0&&nx<row&&ny>=0&&ny<col) { //check bound
					ArrayList<Integer> next=new ArrayList<>(Arrays.asList(nx,ny,cz));
					String tile=map[nx+cz*row][ny];
					if(!path.containsKey(next)) { //only visits new
						if(tile.equals("$")) {
						    buck.clear(); // Ensure it's empty before adding
						    buck.addAll(next); 
						    path.put(next, cur); 
						    break;
						}
						else if(tile.equals(".")||tile.equals("|")){
						    path.put(next,cur); //mark parent
						    st.push(next);
						    if(tile.equals("|")&&cz+1<numMaze){ //portal logic
						        ArrayList<Integer> nexM=starts.get(cz+1);
						        if(!path.containsKey(nexM)){
						            path.put(nexM,next); //link start of next level to portal
						            st.push(nexM);
						        }
						    }
						}
					}
				}
			}
		}
		traceback(starts,buck,path);//final path trace
	}
	public static void queueBased() {
		int z=0;//map #
		Queue<ArrayList<Integer>> q=new LinkedList<>();
		ArrayList<ArrayList<Integer>> starts=new ArrayList<>();//beginning
		HashMap<ArrayList<Integer>,ArrayList<Integer>> path=new HashMap<>();//order tracking
		ArrayList<Integer> buck=new ArrayList<>();//buck cord
		for(int i=0;i<numMaze;i++) { //find starts for each level
			for(int r=0;r<row;r++) {
				for(int j=0;j<col;j++) {
					if(map[r+i*row][j].equals("W")) {
						starts.add(new ArrayList<>(Arrays.asList(r,j,i)));
					}
				}
			}
		}
		int[] d={-1,0,1,0,0,1,0,-1};//north south east west
		if(!starts.isEmpty()) {
			q.add(starts.get(0));//add start to queue
			path.put(starts.get(0),null);//no parent for start
		}
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
						    buck.clear(); // Ensure it's empty before adding
						    buck.addAll(next); 
						    path.put(next, cur); 
						    break;
						}
						else if(tile.equals(".")||tile.equals("|")){
						    path.put(next,cur); //mark parent
						    q.add(next);
						    if(tile.equals("|")&&cz+1<numMaze){ //portal logic
						        ArrayList<Integer> nexM=starts.get(cz+1);
						        if(!path.containsKey(nexM)){
						            path.put(nexM,next); //link start of next level to portal
						            q.add(nexM);
						        }
						    }
						}
					}
				}
			}
		}
		traceback(starts,buck,path);//trace back path
	}
}
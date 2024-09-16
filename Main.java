import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Scanner;
import java.util.LinkedList;

// Path class to create path objects containing the time, cost, and route of the flight path
class Path{
    double time;
    double cost;
    String route;

    //constructor to create Path object and set member arguments
    public Path(double time, double cost, String route) {
        this.time = time;
        this.cost = cost;
        this.route = route;
    }

    //method to return route
    public String getRoute(){
        return route;
    }

    //method to return time
    public double getTime(){
        return time;
    }

    //method to return cost
    public double getCost(){
        return cost;
    }
}

//comparator class to compare paths by cost when adding to priority queue
class pathComparatorCost implements Comparator<Path>{
    //method to compare path1 and path2
    public int compare(Path path1, Path path2){
        if(path1.cost < path2.cost){
            return 1;
        }
        else if(path1.cost > path2.cost){
            return -1;
        }
        else{
            return 0;
        }
    }
}

//comparator class to compare paths by time when adding to priority queue
class pathComparatorTime implements Comparator<Path>{
    //method to compare path1 and path2
    public int compare(Path path1, Path path2){
        if(path1.time < path2.time){
            return -1;
        }
        else if(path1.time > path2.time){
            return 1;
        }
        else{
            return 0;
        }
    }
}

//City class to create city objects containing name, cost, time and reachableCities (City objects not inside reachableCities wont have cost or time
class City{
    String name;
    double cost;
    double time;
    LinkedList<City> reachableCities = new LinkedList<>();

    //constructor to create city object for the linkedList of all the cities
    public City(String name) {
        this.name = name;
    }

    //constructor to create city object for linkedList of reachable cities
    public City(String name, double cost, double time) {
        this.name = name;
        this.cost = cost;
        this.time = time;
    }

    //method to add reachable city to linkedList
    public void addReachableCity(String destinationName, double cost, double time){
        boolean duplicate = false;

        //loop through the reachableCities
        for(City element : reachableCities){
            //if the city is already in reachableCities
            if(element.getName().equals(destinationName)){
                duplicate = true;
            }
        }

        //if duplicate is false, add the city to reachableCities
        if(!duplicate){
            City destination = new City(destinationName, cost, time);
            reachableCities.add(destination);
        }
    }

    //method to return name
    public String getName(){
        return name;
    }

    //method to return time
    public double getTime(){
        return time;
    }

    //method to return cost
    public double getCost(){
        return cost;
    }

    //method to return reachableCities
    public LinkedList<City> getDestinations(){

        return reachableCities;
    }
}

//Main class
public class Main{
    //Main method
    public static void main(String[] args){

        //read in first input file
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter flight data input file: ");
        String flightData = scanner.nextLine();

        //create adjacency matrix
        LinkedList<City> cities = createList(flightData);

        //read in second input file
        System.out.println("Enter requested flight plans input file: ");
        String requestedFlights = scanner.nextLine();

        //Find shortest paths
        FindShortest(cities, requestedFlights);
    }

    //Method to loop through second input file
    public static void FindShortest(LinkedList<City> Cities, String fileName){

        int numLines = 0;
        boolean isTime;

        File file = new File(fileName); //set file

        try {
            //Set scanner to file
            Scanner scanner = new Scanner(file);
            numLines = Integer.parseInt(scanner.nextLine());    //get the number of input lines

            //loop through input file
            for(int i = 0; i < numLines; i++) {

                String timeOrCost = "";
                String line;
                String startCity = "";
                String destinationCity = "";

                line = scanner.nextLine();  //get input/data line

                int pipedCounter = 0;

                //loop through input line, setting startCity, destination city, timeOrCost
                for (int j = 0; j < line.length(); j++) {

                    if (pipedCounter == 0 && line.charAt(j) != '|') {
                        startCity += line.charAt(j);
                    } else if (pipedCounter == 1 && line.charAt(j) != '|') {
                        destinationCity += line.charAt(j);
                    } else if (pipedCounter == 2 && line.charAt(j) != '|') {
                        timeOrCost += line.charAt(j);
                    } else {
                        pipedCounter++;
                    }
                }

                //if you are getting paths by time
                if (timeOrCost.equals("T")) {
                    isTime = true;
                    System.out.println("Flight " + (i+1) + ": " + startCity + ", " + destinationCity + " (Time)");
                    ShortestTime(Cities, startCity, destinationCity, isTime);
                    System.out.println();
                }

                //if you are getting paths by cost
                if(timeOrCost.equals("C")){
                    isTime = false;
                    System.out.println("Flight " + (i+1) + ": " + startCity + ", " + destinationCity + " (Time)");
                    ShortestTime(Cities, startCity, destinationCity, isTime);
                    System.out.println();
                }

            }
        }
        catch(Exception e) {
            System.out.println("File not found");
        }
    }

    //Method that calls DFS function, creates priority queues for cost and time, creates visited array list
    public static void ShortestTime(LinkedList<City> cities, String startCity, String destinationCity, boolean isTime){
        //Create priority queues with comparator
        PriorityQueue<Path> shortestPathsTimes = new PriorityQueue<>(new pathComparatorTime());
        PriorityQueue<Path> shortestPathsCost = new PriorityQueue<>(new pathComparatorCost());

        //created visited array list
        ArrayList<String> visited = new ArrayList<String>();

        City current = findCity(startCity, cities); //find the startCity element in the linkedList of all the cities

        double time = 0;
        double cost = 0;
        String newRoute = startCity;    //set the route to the start city name

        //call DFS function
        DFS(cities, current, newRoute, destinationCity, visited, shortestPathsCost, shortestPathsTimes, cost, time, startCity);

        //If statement to print from either time or cost priority queue
        if(isTime){
            //print error statement if there is no shortest path found
            if(shortestPathsTimes.isEmpty()){
                System.out.println("No shortest paths found");
            }

            //loop for 3 (maximum 3 shortest paths)
            for(int i = 0; i < 3; i++){
                //check if the priority queue is empty
                if(!(shortestPathsTimes.isEmpty())){
                    Path flightPath = shortestPathsTimes.poll();    //get top element
                    printPath(flightPath, (i+1));                   //call print method
                }
            }

        }
        else{
            //print error statement if there is no shortest path found
            if(shortestPathsCost.isEmpty()){
                System.out.println("No shortest paths found");
            }

            //loop for 3 (maximum 3 shortest paths)
            for(int i = 0; i < 3; i++){
                //check if the priority queue is empty
                if(!(shortestPathsTimes.isEmpty())){
                    Path flightPath = shortestPathsTimes.poll();    //get top element
                    printPath(flightPath, (i+1));                   //call print method
                }
            }
        }
    }

    //Method to find all possible paths between start and destination city for cost and time
    public static void DFS(LinkedList<City> Cities, City current, String newRoute, String destinationCity, ArrayList<String> visited, PriorityQueue<Path> byCosts, PriorityQueue<Path> byTimes, double cost, double time, String startCity){

        //check if the current city is already in the visited array list
        if(visited.contains(current.getName())){
            return;     // if it is return
        }

        visited.add(current.getName()); //add current city to the visted array list

        //check if the current city is the destination city
        if(current.getName().equals(destinationCity)){

            //update time, route, and cost
            time += current.getTime();
            newRoute += " -> ";
            newRoute += current.getName();
            cost += current.getCost();

            //create path object
            Path newPath = new Path(time, cost, newRoute);

            //add path to both priority queues
            byCosts.add(newPath);
            byTimes.add(newPath);
        }
        else{
            //otherwise update time and cost
            time += current.getTime();
            cost += current.getCost();

            //check to make sure the current city is not the start city
            if(!(current.getName().equals(startCity))){
                //update route
                newRoute += " -> ";
                newRoute += current.getName();
            }

            City inOriginal = findCity(current.getName(), Cities);  //current is from reachableCities linkedList (embedded linkedList), get the equivalent object from linkedList of all cities
            LinkedList<City> destinations = inOriginal.getDestinations();   //get reachableCities(neighbors)

            //Loop through reachableCities(neighbors)
            for(City element : destinations) {
                //recursively call DFS function
                DFS(Cities, element, newRoute, destinationCity, visited, byCosts, byTimes, cost, time, startCity);
                visited.remove(element.getName());  //remove the element from the visited array list
            }

        }

    }

    //Function to find a city object in a linkedList
    public static City findCity(String cityName, LinkedList<City> Cities){

        //Loop through linkedList
        for(City element : Cities){

            //if the objects name is the city name, return the object
            if(element.getName().equals(cityName)){
                return element;
            }

        }

        return null;
    }

    //Method to print the path
    public static void printPath(Path flightPath, int index){
            System.out.println("Path " + index + ": " + flightPath.getRoute() + ". Time: " +  flightPath.getTime() + " Cost: " + flightPath.getCost());
    }

    //Method to create adjacency list
    public static LinkedList<City> createList(String fileName) {

        String line;
        String cityName = "";
        String destinationName = "";
        String cost = "";
        String time = "";
        int numLines = 0;

        LinkedList<City> cities = new LinkedList<>();   //create linkedList of City class that will contain all the cities

        File file = new File(fileName);                 //set file

        try {
            //set scanner to file
            Scanner scanner = new Scanner(file);
            numLines = Integer.parseInt(scanner.nextLine());    //get number of input lines (lines with data)

            //loop through the input lines
            for (int i = 0; i < numLines; i++) {

                line = scanner.nextLine();
                int pipedCounter = 0;

                //loop through each line, setting cityName, destinationName, cost, time
                for (int j = 0; j < line.length(); j++) {

                    if (pipedCounter == 0 && line.charAt(j) != '|') {
                        cityName += line.charAt(j);
                    }
                    else if (pipedCounter == 1 && line.charAt(j) != '|') {
                        destinationName += line.charAt(j);
                    }
                    else if (pipedCounter == 2 && line.charAt(j) != '|') {
                        cost += line.charAt(j);
                    }
                    else if (pipedCounter == 3 && line.charAt(j) != '|') {
                        time += line.charAt(j);
                    }
                    else {
                        pipedCounter++;
                    }

                }

                boolean duplicate = false;

                //check if the city is already in the linkedList of all cities
                for (City element : cities) {
                    if (element.getName().equals(cityName)) {
                        duplicate = true;
                        element.addReachableCity(destinationName, Double.parseDouble(cost), Double.parseDouble(time));  //add destination to reachableCities
                    }
                }

                //if the city is not in the linkedList of all cities
                if (duplicate == false) {
                    City city = new City(cityName); //create new city object
                    city.addReachableCity(destinationName, Double.parseDouble(cost), Double.parseDouble(time)); //add destination city to reachableCities
                    cities.add(city);   //add object to linkedList of all cities
                }

                duplicate = false;

                //check if destination city is already in the linkedList of all cities
                for (City element : cities) {
                    if (element.getName().equals(destinationName)) {
                        duplicate = true;
                        element.addReachableCity(cityName, Double.parseDouble(cost), Double.parseDouble(time)); //add cityName to reachableCities
                    }
                }


                //if the destination city is not in the linkedList of all cities
                if (duplicate == false) {
                    City destination = new City(destinationName);   //create new city object
                    destination.addReachableCity(cityName, Double.parseDouble(cost), Double.parseDouble(time)); //add cityName to reachableCities
                    cities.add(destination);    //add object to linkedList of all cities
                }

                //reset variables
                cityName = "";
                destinationName = "";
                cost = "";
                time = "";
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + fileName);
            return null;
        }
        return cities;
    }
}

import java.io.*;
import java.util.*;

/**
 * Created by Megha Nagabhushan on 11/26/2017.
 *
 */
public class RequestHandler {


    public RequestHandler() throws IOException {
    }

    public static void main(String[] args) throws IOException {

        RequestHandler requestHandler = new RequestHandler();

        new Thread() {
            public void run() {
                try {
                    requestHandler.startRequest();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        new Thread() {
            public void run() {
                try {
                    requestHandler.completeRequest();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


    public synchronized void startRequest() throws IOException {

        ShortestPath shortestPath=new ShortestPath();

        System.out.printf("%1s  %-7s   %-7s   %-6s   %-6s   %-6s%n","Request ID","Request Type","Request ZipCode","Nearest Zipcode","Dispatched Vehicle ID","Distance from Source");
        //Saving the contents of Emergency Vehicle  File into a Hash Map with vehicle ID as its Key and zipcode,type and availability as its value
        Map<String, String> vehiclesMap = new HashMap<String, String>();
        BufferedReader in = new BufferedReader(new FileReader("data/EmergencyVehicle.txt"));
        String line = "";
        while ((line = in.readLine()) != null) {
            String parts[] = line.split(",");
            vehiclesMap.put(parts[0], parts[1] + "," + parts[2] + "," + parts[3]);
        }
        in.close();

        //Saving requestTable File into a Hash Map with Request ID as the Key and Type, zipcode and assigned Vehicle as its value
        Map<String, String> requestMap = new HashMap<String, String>();
        File reqFile = new File("data/RequestTable");
        BufferedReader breader = new BufferedReader(new FileReader(reqFile));
        String line1 = "";
        while ((line1 = breader.readLine()) != null) {
            String parts[] = line1.split(",");
            requestMap.put(parts[0], parts[1] + "," + parts[2] + "," + parts[3]);
        }
        breader.close();

        for (Map.Entry<String, String> request : requestMap.entrySet()) {
            String requestID = request.getKey();
            String requestValue = request.getValue();
            String requests[] = requestValue.split(",");
            String requestType = requests[0];
            String requestZipcode = requests[1];
            String dispatchedVehicle = "";
            String nearestZipCode = "";
            int distance = 0;
            boolean flag= false;

            String resultInformation = checkEmergency(vehiclesMap,requestZipcode,requestType,flag);
            String[] results = resultInformation.split(",");

            dispatchedVehicle = results[0];
            flag = Boolean.parseBoolean(results[1]);

            while(!flag) {

                String newZipcode = "";
                String newresultInformation = null;
                HashMap<String, Integer> algorithmResult = shortestPath.algorithmImplementation(requestZipcode);
                int[] distances = new int[8];
                ArrayList distanceList = new ArrayList();
                for (Map.Entry<String, Integer> algorithm : algorithmResult.entrySet()) {
                    distanceList.add(algorithm.getValue());
                }
                for (int i =0; i < distanceList.size(); i++)
                    distances[i] = (int) distanceList.get(i);
                QuickSort ob = new QuickSort();
                int n=distances.length;
                ob.sort(distances, 0, n-1);


                for(int i=0 ;i<7;i++){
                    for(Map.Entry<String, Integer> algorithm : algorithmResult.entrySet()){
                        if(algorithm.getValue().equals(distances[i])){
                            newZipcode = algorithm.getKey();
                            nearestZipCode = newZipcode;
                            distance = algorithm.getValue();
                            break;
                        }
                    }
                    newresultInformation = checkEmergency(vehiclesMap,newZipcode,requestType,flag);
                    String[] newresults = newresultInformation.split(",");

                    dispatchedVehicle = newresults[0];
                    flag = Boolean.parseBoolean(newresults[1]);
                    if(flag)break;
                 }

                if(flag==false){
                    dispatchedVehicle="Not Available";
                    break;
                }



            }

            //setting the id of the vehicle that was assigned once the request is processed
            request.setValue(requestType + "," + requestZipcode + "," + dispatchedVehicle);
            printResult(requestID, requestType, requestZipcode, nearestZipCode, dispatchedVehicle, distance);
        }

        //updating the Emergency Vehicle File with the new availability [writing the Hash map into the file]
        File myFoo = new File("data/EmergencyVehicle.txt");
        FileWriter fooWriter = new FileWriter(myFoo, false); // true to append*/
        for(Map.Entry<String, String> vehicleEntry : vehiclesMap.entrySet()) {
            fooWriter.write(vehicleEntry.getKey() + "," + vehicleEntry.getValue() + "\n");
        }
        fooWriter.close();

        //updating the Request Table file with the assigned vehicles [writing the hash map into the file]
        File myFoo2 = new File("data/RequestTable");
        FileWriter fooWriter2 = new FileWriter(myFoo2, false); // true to append*/
        for(Map.Entry<String, String> requestEntry : requestMap.entrySet()) {
            fooWriter2.write(requestEntry.getKey() + "," + requestEntry.getValue() + "\n");
        }
        fooWriter2.close();
    }

    public synchronized void completeRequest() throws IOException {

        //writing down ids of all vehicles that completed the request into an array list
        ArrayList<String> completeList = new ArrayList<>();
        File reqFile = new File("data/RequestComplete");
        BufferedReader breader = new BufferedReader(new FileReader(reqFile));
        String line1 = "";
        while ((line1 = breader.readLine()) != null) {
            completeList.add(line1);
        }
        breader.close();

        //Saving the contents on Emergency Vehicle in a Hash Map with vehicle ID as its Key and zipcode,type and availability as its value
        Map<String, String> vehiclesMap = new HashMap<String, String>();
        BufferedReader in = new BufferedReader(new FileReader("data/EmergencyVehicle.txt"));
        String line = "";
        while ((line = in.readLine()) != null) {
            String parts[] = line.split(",");
            vehiclesMap.put(parts[0], parts[1] + "," + parts[2] + "," + parts[3]);
        }
        in.close();

        //Setting back the availability to 1 once the vehicle returns.
        for (String str : completeList) {
            for (Map.Entry<String, String> entry : vehiclesMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                String linesplits[] = value.split(",");
                String vehicleType = linesplits[1];
                String vehicleZipCode = linesplits[0];
                String vehicleAvailability = linesplits[2];
                if (key.equals(str)) {
                    entry.setValue(vehicleZipCode + "," + vehicleType + ",1");
                }
            }
        }

        //Updating the Emergency Vehicle file with the new availability
        System.out.println("****Complete Request update****");
        File myFoo = new File("data/EmergencyVehicle.txt");
        FileWriter fooWriter = new FileWriter(myFoo, false); // true to append
        for (Map.Entry<String, String> entry : vehiclesMap.entrySet()) {
            fooWriter.write(entry.getKey() + "," + entry.getValue() + "\n");
        }
        fooWriter.close();
    }

    //Printing the final table of results
    public void printResult(String requestID,String requestType,String requestZipcode,String nearestZipCode,String dispatchedVehicle,int distance){
        System.out.printf("%1s  %15s   %15s   %15s   %15s   %15s%n", requestID, requestType, requestZipcode, nearestZipCode, dispatchedVehicle,distance);
    }

    public String checkEmergency(Map<String,String> vehiclesMap, String requestZipcode,String requestType,boolean flag){
        String dispatchedVehicle = "";
        String nearestZipCode = "";
        int distance = 0;
        for (Map.Entry<String, String> entry : vehiclesMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String linesplits[] = value.split(",");
            String vehicleType = linesplits[1];
            String vehicleZipCode = linesplits[0];
            String vehicleAvailability = linesplits[2];

            if (requestType.equals(vehicleType) && requestZipcode.equals(vehicleZipCode) && vehicleAvailability.equals("1")) {
                dispatchedVehicle = key;
                nearestZipCode = requestZipcode;
                distance = 0;
                entry.setValue(vehicleZipCode + "," + vehicleType + ",0");
                flag=true;
                break;
            }
        }
        return dispatchedVehicle+","+flag;
    }
}

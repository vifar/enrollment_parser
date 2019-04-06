package enrollment_parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Scanner;
import java.util.TreeMap;

public class Parser {

    public static void main(String[] args) {
        
        //Get path of csv
        Scanner input = new Scanner(System.in);
        System.out.println("Please enter path of CSV: ");
        String path = input.nextLine();

        try {
        	
            List<Enrollee> enrollees = new ArrayList<Enrollee>();
            
            //Store and parse csv
            input = new Scanner(new File(path));
            while (input.hasNextLine()) {
                //Parse enrollee and add to list of enrollees
                Enrollee tempEnrollee = new Enrollee(input.nextLine());
                enrollees.add(tempEnrollee);
            }

            //Sort by first name & last name ascending
            enrollees.sort(Comparator.comparing(Enrollee::getFirstName).thenComparing(Enrollee::getLastName).thenComparingInt(Enrollee::getVersion));
            
            //Remove duplicate userIds leaving only the one with highest version
            List<Enrollee> removing = new ArrayList<Enrollee>();
            for(int i = 0; i < enrollees.size()-1; i++){
                if(enrollees.get(i).getUserId().equalsIgnoreCase(enrollees.get(i+1).getUserId()) && enrollees.get(i).getVersion() <= enrollees.get(i+1).getVersion()) {
                    removing.add(enrollees.get(i));
                }
            }
            enrollees.removeAll(removing);

            //Split enrollees by insurance company to print to CSVs
            NavigableMap<String, List<Enrollee>> sortedByInsuranceCompany = new TreeMap<String,List<Enrollee>>();
            for (Enrollee enrollee : enrollees) {
                List<Enrollee> enrollee_insuranceCompany = sortedByInsuranceCompany.get(enrollee.getInsuranceComapny());
                if (enrollee_insuranceCompany == null) {
                    sortedByInsuranceCompany.put(enrollee.getInsuranceComapny(), enrollee_insuranceCompany = new ArrayList<Enrollee>());
                }
                enrollee_insuranceCompany.add(enrollee);          
            }

            //Strip path for saving of CSVs
            path = path.substring(0, path.lastIndexOf("\\") + 1);

            //Create each CSV
            for(Map.Entry<String, List<Enrollee>> entry : sortedByInsuranceCompany.entrySet()) {
                File outputFile = new File(path + entry.getKey() + ".csv");
                try {
                    PrintWriter writer = new PrintWriter(outputFile);
                    entry.getValue().forEach( enrollee -> {
                        writer.println(Enrollee.createCSVline(enrollee));
                    });
                    writer.close();
                    System.out.println("Writing: " + outputFile.getPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            System.out.println("Done!");
        } catch (FileNotFoundException e) {
            System.out.println("File not Found!");
            e.printStackTrace();
        }
    }
}


//Enrollee pojo
class Enrollee {

    private String userId;
    private String firstName;
    private String lastName;
    private int version;
    private String insuranceComapny;

    public Enrollee(String input) {
        this.userId = input.split(",")[0];
        this.firstName = input.split(",")[1];
        this.lastName = input.split(",")[2];
        this.version =  Integer.parseInt(input.split(",")[3]);
        this.insuranceComapny = input.split(",")[4];;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getInsuranceComapny() {
        return this.insuranceComapny;
    }

    public void setInsuranceComapny(String insuranceComapny) {
        this.insuranceComapny = insuranceComapny;
    }

    public static String createCSVline(Enrollee enrollee) {
        return enrollee.getUserId() + "," + enrollee.getFirstName() + "," + enrollee.getLastName() 
            + "," + enrollee.getVersion() + "," + enrollee.getInsuranceComapny();
    }

    @Override
    public String toString() {
        return "{" +
            " userId='" + getUserId() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", version='" + getVersion() + "'" +
            ", insuranceComapny='" + getInsuranceComapny() + "'" +
            "}";
    }

}

package CSVtoJSON;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONObject;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSV {
    public static List<Employee> parseCSV(String[] array, String name) {
        List<Employee> staff = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(name))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<Employee>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping("id", "firstName", "lastName", "country", "age");
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }

    public static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> listOfEmployee = new ArrayList<>();
        List<String> arrayOfEmployees = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(fileName);
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes(); //emp1 emp2
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeName().equals("employee")) {
                NodeList employeeNode = node.getChildNodes();
                for (int j = 0; j < employeeNode.getLength(); j++) {
                    Node newNode = employeeNode.item(j);
                    if (Node.ELEMENT_NODE == newNode.getNodeType()) {
                        arrayOfEmployees.add(newNode.getTextContent());
                    }
                }
                listOfEmployee.add(new Employee(Long.parseLong(arrayOfEmployees.get(0)),
                        arrayOfEmployees.get(1),
                        arrayOfEmployees.get(2),
                        arrayOfEmployees.get(3),
                        Integer.parseInt(arrayOfEmployees.get(4))));
                arrayOfEmployees.clear();
            }
        }
        return listOfEmployee;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        Gson gson = builder.create();
        return gson.toJson(list);
    }

    public static void writeString(String json) {
        try (FileWriter writer = new FileWriter("data2.json")) {
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String [] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        //List<Employee> list = parseCSV(columnMapping, fileName);//to get list of employees
        List<Employee> list = parseXML("data.xml");//to get list of employees
        String json = listToJson(list); //to convert list in JSON format
        writeString(json);
        //System.out.println(parseXML("data.xml"));
        }
}

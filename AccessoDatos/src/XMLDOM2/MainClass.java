package XMLDOM2;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;

public class MainClass {
    public static void main(String[] args) throws IOException, ParserConfigurationException, TransformerException {
        try {
            //Creating client objects
            Client c1 = new Client("Patrick", 23);
            Client c2 = new Client("Joshua", 24);
            Client c3 = new Client("Pomares", 20);
            Client c4 = new Client("Juanma", 21);
            Client c5 = new Client("Pep", 30);

            //Creating file to save the objects
            FileOutputStream fos = new FileOutputStream("clients.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            //Writting the clients objects in the clients.data file
            oos.writeObject(c1);
            oos.writeObject(c2);
            oos.writeObject(c3);
            oos.writeObject(c4);
            oos.writeObject(c5);

            MainClass objectIO = new MainClass();
            //Reading the file clients.data and saving the objects to an arraylist
            ArrayList<Client> clients = objectIO.ReadObjectFromFile("src/XMLDOM2/Resources/clients.dat");

            String xmlFilePath = "src/XMLDOM2/Resources/clients.xml";
            objectIO.createDocumentXML(xmlFilePath, clients);

            objectIO.readFromXML(xmlFilePath);
        } catch (IOException | ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }


    }

    /*
     This method receive the path of the xml file to create and a arraylist with the clients objects.
     The main objective of this method is to create a xml file with the info of the each client object
     */
    public void createDocumentXML(String xmlFilePath, @NotNull ArrayList<Client> clients) throws ParserConfigurationException, TransformerException {
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            Element root = document.createElement("clients");
            document.appendChild(root);

            /*
            Looping each client in the arrayList,
            Creating the nodes, and the adding the info is required (name and age)
             */
            for (Client client : clients) {
                Element clientXML = document.createElement("client");
                root.appendChild(clientXML);

                Element nameClient = document.createElement("name");
                clientXML.appendChild(nameClient);
                nameClient.appendChild(document.createTextNode(client.getName()));

                Element ageClient = document.createElement("age");
                clientXML.appendChild(ageClient);
                ageClient.appendChild(document.createTextNode(String.valueOf((client.getAge()))));
            }
            //Creating the xml file with all the nodes associated
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));

            transformer.transform(domSource, streamResult);
        } catch (ParserConfigurationException | DOMException | TransformerFactoryConfigurationError | TransformerException e) {
            e.printStackTrace();
        }

    }
    /*
    This method receive from parameter the path of a xml file, the main objective of this method is to
    loop on all the elements of the DOM and print the age and the name of the clients.
     */
    public void readFromXML(String xmlFilePath) {
        File file = new File(xmlFilePath);
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();
            //Getting a list of all the elements client
            NodeList list = doc.getElementsByTagName("client");
            // Looping the list of client, and printing the name and years.
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String name = element.getElementsByTagName("name").item(0).getTextContent();
                    String age = element.getElementsByTagName("age").item(0).getTextContent();
                    System.out.println(name + " has " + age + " years.");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    // Method that read all the objects from the clients.data file and return an arralist of objects
    public ArrayList<Client> ReadObjectFromFile(String filepath) {
        try {
            FileInputStream fileIn = new FileInputStream(filepath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            boolean flag = true;
            ArrayList<Client> clienteArray = new ArrayList<>();
            while (flag) {
                try {
                    Client obj = (Client) objectIn.readObject();
                    clienteArray.add(obj);
                } catch (Exception EOFException) {
                    flag = false;
                    objectIn.close();
                }
            }
            return clienteArray;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

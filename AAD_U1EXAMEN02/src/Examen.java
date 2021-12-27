import org.jetbrains.annotations.NotNull;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class Examen {

    static int select = -1;     //Variable global para controlar los menus JoptionPane
    static boolean flag = true; //Variable para controlar los bucles while
    static char quotes = '"';   //Variable char para poder usar " dentro de un string

    //Metodo para cargar y parsear el archivo XML
    static Document prepareDocument() throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new File("Alumnes.xml"));
    }

    //Metodo que se encarga de "escribir" los elementos DOM creados previamente, además se encarga de formaterar el codigo XML.
    static void transform(Document document) {
        try {

            //Creamos una instancia de Transformer que se encarga de dar formato a nuestro archivo XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            //Escribimos todos los elementos creados en el archivo XML
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File("Alumnes.xml"));
            transformer.transform(domSource, streamResult);
            //Usando XPATH buscamos los espacios en blanco.
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPathExpression xpathExp = xpathFactory.newXPath().compile(
                    "//text()[normalize-space(.) = '']");
            NodeList emptyTextNodes = (NodeList)
                    xpathExp.evaluate(document, XPathConstants.NODESET);
            // Bucle for que elimina los espacios en blanco.
            for (int i = 0; i < emptyTextNodes.getLength(); i++) {
                Node emptyTextNode = emptyTextNodes.item(i);
                emptyTextNode.getParentNode().removeChild(emptyTextNode);
            }
        } catch (TransformerException | XPathExpressionException e) {
            e.printStackTrace();
        }

    }

    //Metodo encargado de crear una ventana donde se preguntara al usuario por la ID de un alumno.
    static String inputId() {
        return JOptionPane.showInputDialog(
                null,
                "Introduce el id del alumno a modificar",
                JOptionPane.QUESTION_MESSAGE);
    }

    //Metodo encargado de crear el archivo XML y de escribir en el la raiz "registre_alumnes"
    static void createXml() {
        try {
            //Creación del fichero
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            //Creamos el elemento <registre_alumnes>
            Element root = document.createElement("registre_alumnes");
            //Lo añadimos a la raiz del archivo
            document.appendChild(root);
            //Le añadimos un espacio como valor para que no nos cree <registre_alumnes/>
            root.appendChild(document.createTextNode("\n"));
            //Se llama al metodo transform que se encargara de escribir y dar formato al XML.
            transform(document);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    //Metodo que modificara los datos del archivo XML usando la ID del alumno
    static void modifyXml() {
        try {
            String alumnoId = inputId(); //Llamada al metodo que preguntara en una ventana la ID del alumno a modificar.
            //Parseando el documnento para poder escribir en el
            Document document = prepareDocument();
            document.getDocumentElement().normalize();
            //Ventana donde aparecera el menu para las distintas opciones, donde el usuario eligira que modificar.
            String input = JOptionPane.showInputDialog(null, "************************\nElige opción:\n1.- Editar id" +
                    "\n2.- Editar nombre\n" +
                    "3.- Editar curso\n" +
                    "4.- Editar año de nacimiento\n" +
                    "5.- Editar colegio\n" +
                    "0.- Salir\n************************");
            //Lectura de la respuesta del usuario
            select = Integer.parseInt(input);
            //Ventana donde el usuario asignara el valor a sustituir
            String inputData = JOptionPane.showInputDialog(null, "************************\nElige opción:\nIntroduce el valor a cambiar" +
                    "0.- Salir\n************************");
            NodeList alumnes = document.getElementsByTagName("alumnes");
            //Bucle for que recorre todos los elementos Alumne
            for (int i = 0; i < alumnes.getLength(); i++) {
                Node alumne = alumnes.item(i);
                if (alumne.getNodeType() == Node.ELEMENT_NODE) { //Validando si el objeto es realmente un elemento nodo
                    String id = alumne.getAttributes().getNamedItem("codi_alumne").getTextContent(); //Obteniendo el valor del codigo del alumno
                    //Si el id del alumno es igual al id del alumno que a introducido el usuario
                    if (alumnoId.equals(id)) {
                        NodeList childNodes = alumne.getChildNodes(); //Recogiendo todos los elemementos del alumno con X id
                        for (int j = 0; j < childNodes.getLength(); j++) {
                            Node item = childNodes.item(j);
                            if (item.getNodeType() == Node.ELEMENT_NODE) { //Validando si el objeto es realmente un elemento nodo
                                /*Switch que dependiendo de la eleccion del usuario, se modificara el elemento hijo que este
                                  ha escogido.
                                 */
                                switch (select) {
                                    case 1:
                                        NamedNodeMap attribute = item.getAttributes();
                                        Node nodeAttr = attribute.getNamedItem("id");
                                        nodeAttr.setTextContent(inputData);
                                        break;
                                    case 2:
                                        if ("nom_alumne".equalsIgnoreCase(item.getNodeName())) {
                                            item.setTextContent(inputData);
                                        }
                                        break;
                                    case 3:
                                        if ("curs".equalsIgnoreCase(item.getNodeName())) {
                                            item.setTextContent(inputData);
                                        }
                                        break;
                                    case 4:
                                        if ("any_naixement".equalsIgnoreCase(item.getNodeName())) {
                                            item.setTextContent(inputData);
                                        }
                                        break;
                                    case 5:
                                        if ("colegi".equalsIgnoreCase(item.getNodeName())) {
                                            item.setTextContent(inputData);
                                        }
                                        break;
                                }
                            }
                        }
                    }
                }
            }

            transform(document);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Metodo que ejecutara la secuencia XPATH que se le pasa por parametro com String.
    static void query(String expressionXpath) {
        try {
            //Hacemos el input en donde leera el archivo
            InputSource inputSource = new InputSource(new FileInputStream("Alumnes.xml"));
            //Creamos instancia de XpathFactory y lo preparamos.
            XPathFactory factory = XPathFactory.newInstance();
            XPath xPath = factory.newXPath();
            //Le pasamos la expresion que se le pasa por parametro
            XPathExpression expression = xPath.compile(expressionXpath);
            //Creamos un nodelist que recolectara los resultados del Xpath
            NodeList list = (NodeList) expression.evaluate(inputSource, XPathConstants.NODESET);
            //Bucle for que recorre el nodo y muestra por pantalla los resultados del xpath
            for (int i = 0; i < list.getLength(); i++) {
                System.out.println(list.item(i).getTextContent());
            }
        } catch (XPathExpressionException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Metodo que pregunta al usuario que consulta quiere realizar, y llama al metodo query() para que ejecute la consulta
    static void queryXML() {
        try {
            //Ventana creada con JOptionPane que muestra las distintas consultas al usuario
            String input = JOptionPane.showInputDialog(null, "************************\nElige opción:\n1.- Consultar todos los nombres de los alumnos" +
                    "\n2.- Consultar los alumnos que vayan al colegio Cide\n" +
                    "3.- Consultar el nombre del alumno con codigo 3\n" +
                    "4.- Consultar los alumnos nacidos antes de 1990\n" +
                    "0.- Salir\n************************");
            //Respuesta del usuario
            select = Integer.parseInt(input);
            //Switch que dependiendo de la respuestas del usuario llama al metodo query() encargado de realizar las consultas
            switch (select) {
                case 1 -> query("//nom_alumne");
                case 2 -> query("//alumnes[colegi=" + quotes + "cide" + quotes + "]//nom_alumne");
                case 3 -> query("//alumnes[@codi_alumne=" + quotes + "3" + quotes + "]//nom_alumne");
                case 4 -> query("//alumnes[any_naixement<=1990]//nom_alumne");
            }
            System.out.println("---------------------------------------");
        } catch (HeadlessException e) {
            e.printStackTrace();
        }
    }

    //Metodo encargado de eliminar a un alumno usando su ID como referencia.
    static void removeXml() {
        try {
            //Preparacion del documento
            Document document = prepareDocument();
            String alumnoId = inputId(); //Obteniendo la ID del alumno mediante una ventana de JOPtionPane
            document.getDocumentElement().normalize();
            NodeList alumnes = document.getElementsByTagName("alumnes"); //Obteniendo los elementos con etiqueta <alumnes>
            //Bucle for que recorre la lista de nodos, de los elementos <alumnes>
            for (int i = 0; i < alumnes.getLength(); i++) {
                Node alumne = alumnes.item(i);
                if (alumne.getNodeType() == Node.ELEMENT_NODE) {
                    String id = alumne.getAttributes().getNamedItem("codi_alumne").getTextContent(); //Obteniendo la ID del alumno
                    if (alumnoId.equals(id)) { //Si la id del alumno es igual a la ID del alumno que ha ingresado el usuario...
                        alumne.getParentNode().removeChild(alumne);//Eliminar el elemento padre <alumne>
                    }
                }
            }
            transform(document);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
    }

    //Metodo encargado de cargar el menu principal del programa.
    static void menu() {
        while (select != 0) {
            try {
                //Ventana del menu principal
                String input = JOptionPane.showInputDialog(null, "************************\nElige opción:\n1.- Crear fichero XML" +
                        "\n2.- Introducir datos en el fichero XML\n" +
                        "3.- Mostrar el contenido del fichero XML\n" +
                        "4.- Modificar datos\n" +
                        "5.- Consultas\n" +
                        "6.- Eliminar un registro\n" +
                        "0.- Salir\n************************");
                //Lectura de la eleccion del usuario
                select = Integer.parseInt(input);
                //Switch que dependiendo de la eleccion del usuario llamara al metodo correspondiente
                switch (select) {
                    case 1 -> createXml();
                    case 2 -> {
                        //Bucle con algoritmo de bandera, el cual llama al metodo de crear alumno hasta que el usuario
                        //decide parar, pulsando la tecla n cuando se le pregunta si quiere seguir creando alumnos.
                        while (flag) {
                            ArrayList<String> alumnoData = getDataFromUser(); //Recogida de datos proveniente del usuario para crear un alumno
                            writeXml(alumnoData.get(0), alumnoData.get(1), alumnoData.get(2), alumnoData.get(3), alumnoData.get(4));
                            if (alumnoData.get(5).equals("n")) { //Si la respuesta del usario de si quiere seguir introduciendo alumnos es "n" se
                                flag = false;                    //para el bucle while.
                            }
                        }

                    }
                    case 3 -> showXml();
                    case 4 -> modifyXml();
                    case 5 -> queryXML();
                    case 6 -> removeXml();
                    case 0 -> select = 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Metodo encargado de realizar las preguntas al usuario para poder crear un elemento alumno.
    static @NotNull
    ArrayList<String> getDataFromUser() {
        ArrayList<String> alumnoData = new ArrayList<String>();//Creando la arraylist donde se almacenara la informacion del alumno a crear
        //Preguntando el id del alumno
        String inputId = JOptionPane.showInputDialog(null, "************************\nIntroduzca el id del alumno" +
                "\n************************");
        alumnoData.add(inputId);//Añadiendo la respuesta del usuario al arraylist
        //Preguntando el nombre del alumno
        String inputNombre = JOptionPane.showInputDialog(null, "************************\nIntroduzca el nombre del alumno" +
                "\n************************");
        alumnoData.add(inputNombre);//Añadiendo la respuesta del usuario al arraylist
        //Preguntando el curso del alumno
        String inputCurso = JOptionPane.showInputDialog(null, "************************\nIntroduzca el curso del alumno" +
                "\n************************");
        alumnoData.add(inputCurso);//Añadiendo la respuesta del usuario al arraylist
        //Preguntando el año de nacimiento del alumno
        String inputAnyoNacimiento = JOptionPane.showInputDialog(null, "************************\nIntroduzca el año de nacimiento del alumno" +
                "\n************************");
        alumnoData.add(inputAnyoNacimiento);//Añadiendo la respuesta del usuario al arraylist
        //Preguntando el colegio del alumno
        String inputColegio = JOptionPane.showInputDialog(null, "************************\nIntroduzca el colegio del alumno" +
                "\n************************");
        alumnoData.add(inputColegio);//Añadiendo la respuesta del usuario al arraylist
        boolean flagged = true;
        //Bucle while el controla que el usuario al recibir la pregunta de si quiere seguir introduciendo, introduza el caracter s o n ( Si o No )
        while (flagged) {
            String salir = JOptionPane.showInputDialog(null, "************************\n¿Quiere seguir intrdociendo alumnes? use s / n (Si o no)" +
                    "\n************************");
            if (salir.equals("s") || salir.equals("n")) {
                flagged = false;
                alumnoData.add(salir); //Se almacena la respuesta del usuario.
            }
        }

        return alumnoData;
    }

    //Metodo encargado de crear los elementos con los datos que corresponden.
    static void writeXml(String idAlumne, String nombreAlumno, String curso, String anyoNacimiento, String colegio) {
        try {
            //Preparando y parseando el documento
            Document document = prepareDocument();
            Element root = document.getDocumentElement(); //Obteniendo el elemento raiz del archivo (registre_alumnes)
            //Creando el elemento alumnes
            Element alumnosXML = document.createElement("alumnes");
            root.appendChild(alumnosXML);//Añadiendo el elemento a la raiz
            (alumnosXML).setAttribute("codi_alumne", idAlumne);//Creando y asignando un valor a un atributo
            //Crenado el elemento nom_alumne
            Element nombreXML = document.createElement("nom_alumne");
            alumnosXML.appendChild(nombreXML);//Añadiendo el elemento nom_alumne al elemento alumnes
            nombreXML.appendChild(document.createTextNode(nombreAlumno));//Añadiendo texto al elemento nom_alumne
            //Creando el elemento curs
            Element cursoXML = document.createElement("curs");
            alumnosXML.appendChild(cursoXML);//Añadiendo el elemento curs al elemento alumnes
            cursoXML.appendChild(document.createTextNode(curso));//Añadiendo texto al elemento curs
            //Creando el elemento any_naixement
            Element anyoXML = document.createElement("any_naixement");
            alumnosXML.appendChild(anyoXML);//Añadiendo el elemento any_naixement al elemento alumnes
            anyoXML.appendChild(document.createTextNode(anyoNacimiento));//Añadiendo texto al elemento any_naixement
            //Creando el elemento colegi
            Element colegioXML = document.createElement("colegi");
            alumnosXML.appendChild(colegioXML);//Añadiendo el elemento colegi al elemento alumnes
            colegioXML.appendChild(document.createTextNode(colegio));//Añadiendo texto al elemento colegi

            transform(document);

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

    }

    //Metodo encargado de printar por pantalla los datos del XML
    static void showXml() {
        try {
            Document documento = prepareDocument();
            // Preparación de xpath. Creamos una nueva instancia
            XPath xpath = XPathFactory.newInstance().newXPath();
            //Ejecitamos la expression del xpath
            String xpathQuery = (String) xpath.evaluate("//*", documento, XPathConstants.STRING);
            //Printamos el xpath por pantalla
            System.out.println(xpathQuery);
            //Ventana de que se ha realizado la accion correctamente
            JOptionPane.showMessageDialog(
                    null,
                    "Listado por terminal el XML correctamente");

        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        menu();
    }
}


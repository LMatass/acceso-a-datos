package Futbol;

import java.io.File;
import java.util.Scanner;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Futbol {
    public static void main(String[] args) throws Exception {

        // Variable booleana para controlar el while
        boolean fin = false;

        while (!fin) {
            // Mostramos una ventana emergente con un dialogo y las opciones SI y NO.
            int n = JOptionPane.showConfirmDialog(
                    null,
                    "Desea consultar el contenido del XML completo?",
                    "Consulta del XML",
                    JOptionPane.YES_NO_OPTION);

            // Si la opcion elegida es SI...
            if (n == JOptionPane.YES_OPTION) {
                // Almacenamos en una variable del tipo String llamada xPathExpression, la expresion xpath de busqueda.
                // En este caso busca la etiqueta "futbol" y todo su contenido
                        String xPathExpression = "/futbol//*";

                // Carga del documento xml
                // Creamos una nueva instancia de DocuementBuilderFactory para construir el documento
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                // Guardamos en la variable "documento" el contenido del fichero xml parseado.
                Document documento = builder.parse(new File("D:\\IDEProjects\\AccessoDatos\\src\\Futbol\\futbol.xml"));

                // Preparación de xpath. Creamos una nueva instancia
                XPath xpath = XPathFactory.newInstance().newXPath();

                // Consultas: recorremos los nodos del fichero con XPath
                NodeList nodos = (NodeList) xpath.evaluate(xPathExpression, documento, XPathConstants.NODESET);

                // Creamos un bucle para recorrer todos los nodos
                for (int i = 0; i < nodos.getLength(); i++) {
                    // y mostramos un mensaje cada vez que encuentra un nodo, con el nombre del nodo (es decir, la etiqueta)
                    // y el/los atributo/s que tiene dicha etiqueta. En este caso busca el atributo "nombre"
                    System.out.println(nodos.item(i).getNodeName() + " : " +
                            nodos.item(i).getAttributes().getNamedItem("nombre"));
                }
                // Mostramos un separador despues de mostrar tod@ el contenido del XML
                System.out.println("---------------------------------------------------");

                String Futbol = JOptionPane.showInputDialog("Que desea ver (selección/liga/equipo/jugador)?:");
                String datoX = JOptionPane.showInputDialog("Cual?:");

                String xPathPlanet = ("//" + Futbol + "[@nombre='" + datoX + "']");

                // Carga del documento xml
                DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder2 = factory2.newDocumentBuilder();
                Document documento2 = builder2.parse(new File("D:\\IDEProjects\\AccessoDatos\\src\\Futbol\\futbol.xml"));

                // Preparación de xpath
                XPath xpath2 = XPathFactory.newInstance().newXPath();

                // Consultas
                NodeList nodos2 = (NodeList) xpath2.evaluate(xPathPlanet, documento2, XPathConstants.NODESET);

                for (int i = 0; i < nodos2.getLength(); i++) {
                    System.out.println(nodos2.item(i).getNodeName() + " : " +
                            nodos2.item(i).getAttributes().getNamedItem("nombre"));
                }
            } else {

                String Futbol = JOptionPane.showInputDialog("Que desea ver (selección/liga/equipo/jugador)?:");
                String datoX = JOptionPane.showInputDialog("Cual?:");

                String xPathPlanet = ("//" + Futbol + "[@nombre='" + datoX + "']");

                // Carga del documento xml
                DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder2 = factory2.newDocumentBuilder();
                                    Document documento2 = builder2.parse(new File("D:\\IDEProjects\\AccessoDatos\\src\\Futbol\\futbol.xml" +
                        "" +
                        ""));

                // Preparación de xpath
                XPath xpath2 = XPathFactory.newInstance().newXPath();

                // Consultas
                NodeList nodos2 = (NodeList) xpath2.evaluate(xPathPlanet, documento2, XPathConstants.NODESET);

                for (int i = 0; i < nodos2.getLength(); i++) {
                    System.out.println(nodos2.item(i).getNodeName() + " : " +
                            nodos2.item(i).getAttributes().getNamedItem("nombre"));
                }
            }
            int seguir = JOptionPane.showConfirmDialog(
                    null,
                    "Desea hacer otra consulta?!",
                    "",
                    JOptionPane.YES_NO_OPTION);
            if (seguir == JOptionPane.YES_OPTION) {
                fin = false;
            } else
                System.exit(0);
        }
    }
}
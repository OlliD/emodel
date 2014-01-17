package de.unibi.agai.emodel.emotionstrategyselector.robotconnector;


import de.unibi.flobi.Actuators;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.xom.*;

/**
 *
 * @author akipp
 */
public class HeadPositions {

    private Map<String, PositionElement> positions = new HashMap<String, PositionElement>();
    private static final Logger LOGGER = Logger.getLogger(HeadPositions.class.getName());
    private String filename;

    public HeadPositions() {
        filename = System.getenv("prefix") + "/share/HeadControl/positions.xml";
        //filename = "/homes/odamm/develop/trunk/src/main/resources/positions.xml";

        System.out.println("Path: " + System.getenv("prefix") + "/share/HeadControl/positions.xml");
        try {
            parsePositions();
        } catch (ParsingException ex) {
            Logger.getLogger(HeadPositions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HeadPositions.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public PositionElement getPosition(String action) throws IllegalArgumentException {

        if (!positions.containsKey(action)) {
            throw new IllegalArgumentException("Action " + action + " not in list.");
        }
        return positions.get(action);

    }

    public Map<String, PositionElement> getPositions() {
        return positions;
    }

    public void parsePositions() throws ParsingException, ValidityException, IOException {
        Builder b = new Builder();
        Document doc;

        File in = new File(filename);
        if (in.exists()) {
            doc = b.build(in);
        } else {
            InputStream inStream = HeadPositions.class.getClassLoader().getResourceAsStream(filename);
            if (inStream == null) {
                throw new IOException("File not Found");
            }
            doc = b.build(inStream);
        }

        Elements childElements = doc.getRootElement().getChildElements("Position");

        for (int i = 0; i < childElements.size(); i++) {

            String positionName = childElements.get(i).getAttribute("id").getValue().toString();
            int ramp = Integer.valueOf(childElements.get(i).getAttribute("ramp").getValue());
            int speed = Integer.valueOf(childElements.get(i).getAttribute("maxSpeed").getValue());

            LOGGER.log(Level.FINER, "Adding position {0}", positionName);

            Elements elActuators = childElements.get(i).getChildElements();

            Map<Actuators, Float> actuatorList = new EnumMap<Actuators, Float>(Actuators.class);
            Map<SpeedNames, List<Integer>> speednames = new EnumMap<SpeedNames,List<Integer>>(SpeedNames.class);

            LOGGER.log(Level.FINER, "Actuators for position {0} with actuator count {1}", new Object[]{positionName, elActuators.size()});




            for (int j = 0; j < elActuators.size(); j++) {
                Actuators a;
                Float value;
                
                if (elActuators.get(0).getLocalName().equals("Actuator")){
                String actName = elActuators.get(j).getAttribute("id").getValue().toString();
                a = Actuators.valueOf(actName);
                value = Float.valueOf(elActuators.get(j).getAttribute("target").getValue().toString());
                actuatorList.put(a, value);
                LOGGER.log(Level.FINER, "Adding actuator {0} with value {1}", new Object[]{a, value});
                } else if (elActuators.equals("Speed")) {
                    speednames.put(SpeedNames.neck, new ArrayList<Integer>());
                    speednames.get(SpeedNames.neck).add(0,5);
                }

            }

            PositionElement pe = new PositionElement(actuatorList, ramp, speed, positionName, speednames);
            pe.printValues();
            positions.put(positionName, pe);
        }
    }

    public void savePosition(String posName, Map<Actuators, Float> actuatorList, int ramp, int speed) throws ParsingException, ValidityException, IOException {
        Builder b = new Builder();
        Document doc;

        File in = new File(filename);
        if (in.exists()) {
            doc = b.build(in);
        } else {
            InputStream inStream = HeadPositions.class.getClassLoader().getResourceAsStream(filename);
            if (inStream == null) {
                throw new IOException("File not Found " + filename);
            }
            doc = b.build(inStream);
        }

        System.out.println("Build new Position Element " + posName);
        Elements pos = doc.getRootElement().getChildElements();
        System.out.println(doc.getRootElement().getChildElements().size());
        for (int i = 0; i < doc.getRootElement().getChildElements().size(); i++) {
            
            Attribute a = doc.getRootElement().getChildElements().get(i).getAttribute("id");
            if (a.getValue().equals(posName)) {
                System.out.println(a.getValue());
                System.out.println("Id: " + i);
                doc.getRootElement().getChildElements().get(i).detach();
            } else {
                System.out.println(i + " no match " + a.getValue());
            }
        }

        System.out.println(doc.toXML());



        Element newPos = new Element("Position");
        Attribute aPosId = new Attribute("id", posName);
        Attribute aRamp = new Attribute("ramp", String.valueOf(ramp));
        Attribute aSpeed = new Attribute("maxSpeed", String.valueOf(speed));

        newPos.addAttribute(aPosId);
        newPos.addAttribute(aRamp);
        newPos.addAttribute(aSpeed);

        for (Actuators a : actuatorList.keySet()) {
            Element act = new Element("Actuator");
            Attribute id = new Attribute("id", a.name());
            Attribute target = new Attribute("target", String.valueOf(actuatorList.get(a)));
            act.addAttribute(id);
            act.addAttribute(target);
            newPos.appendChild(act);
        }

        doc.getRootElement().appendChild(newPos);

        System.out.println("Save to File " + filename);

        FileOutputStream fos = new FileOutputStream(filename);

        Serializer output = new Serializer(fos, "UTF-8");
        output.setIndent(4);
        output.write(doc);
        output.flush();
        fos.close();


        System.out.println("Everything written.");
    }
}

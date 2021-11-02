package com.web.template.Utils;

import java.io.*;
import java.util.*;

/**
 * Created by codecraft on 08/11/16.
 */
public class PropertyReader {
    Properties readProp = new Properties();
    Properties setProp = new Properties();
    InputStream inputStream=null;
    //Sigle map instance that contain consolidated list of keys and values of all property reader
    HashMap<String,String> result = new HashMap<String, String>();
    List<String> listOfFiles = new LinkedList<String>();

    //    private static final Logger logger = LogManager.getLogger(PropertyReader.class);
    private PropertyReader(){};

    private static PropertyReader Obj;

    public static PropertyReader getInstance()
    {
        if(Obj==null)
        {
            Obj= new PropertyReader();
        }

        return Obj;
    }

    /**
     * Function can fetch the values of property files present in the resource folder marked as "source root"
     * @param file
     * @return
     */
    public  synchronized HashMap<String, String> getPropValues(String file)
    {
//        logger.info(Thread.currentThread().getStackTrace()[1].getMethodName() + "is executed");
        try
        {
            if(!listOfFiles.contains(file))
            {
                inputStream = PropertyReader.getInstance().getClass().getClassLoader().getResourceAsStream(file);
                listOfFiles.add(file);
                if (inputStream != null) {
                    readProp.load(inputStream);
                } else {
                    throw new FileNotFoundException("property file '" + file + "' not found in the classpath");
                    //System.exit(1);
                }
                // get the property values
                Set propNames = readProp.stringPropertyNames();
                Iterator<String> iterator = propNames.iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    result.put(key, readProp.getProperty(key));
                }

                if (inputStream != null)
                    inputStream.close();
            }
        } catch (Exception e) {
//            logger.error("Exception occured while parsing property file: " + e);

        }
        return result;

    }

    /**
     * Function to fetch a value alone for a particular key. Return null if key do not exist.
     */
    public  synchronized String getPropValues(String file, String key)
    {
//        logger.info(Thread.currentThread().getStackTrace()[1].getMethodName() + "is executed");
        try
        {
            if(result.containsKey(key))
                return result.get(key);
            else
            {
                Optional<String> keyValue= Optional.ofNullable(getPropValues(file).get(key));
                return keyValue.orElse("Null");
            }
        } catch (Exception e) {
//            logger.error("Exception: " + e);
            return "Null";

        }

    }

    public  synchronized  void setPropsValue(String file, String key, String value)
    {
        try{
            setProp.load(new InputStreamReader(new FileInputStream(file)));
        }catch (FileNotFoundException e){
            System.out.println("File Not Found");
        }catch (IOException e){
            System.out.println("Failed to write to credentials.properties");
        }

        try {
            setProp.remove(key);
            setProp.setProperty(key, value);
            setProp.store(new FileOutputStream(file,false), null);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public  synchronized void setPropsValue(String file, String path, String key, String value)
    {
        try{
            setProp.load(new InputStreamReader(new FileInputStream(file)));
        }catch (FileNotFoundException e){
            System.out.println("File Not Found");
        }catch (IOException e){
            System.out.println("Failed to write to credentials.properties");
        }

        try {
            setProp.remove(key);
            setProp.setProperty(key, value);
            setProp.store(new FileOutputStream(file,false), null);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static HashMap<String, String> getProp(String file) {
        InputStream inputStream;
        HashMap<String, String> result = new HashMap<String, String>();

        try {
            Properties prop = new Properties();
            String propFileName = file;
//            inputStream = PropertyReader.class.getClassLoader().getResourceAsStream(propFileName);
            inputStream = new FileInputStream(System.getProperty("user.dir")+propFileName);
            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
            // get the property values
            Set propNames = prop.stringPropertyNames();
            Iterator<String> iterator = propNames.iterator();
            while (iterator.hasNext())
            {
                String key = iterator.next();
                result.put(key , prop.getProperty(key));
            }

            inputStream.close();


        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return result;
    }
}

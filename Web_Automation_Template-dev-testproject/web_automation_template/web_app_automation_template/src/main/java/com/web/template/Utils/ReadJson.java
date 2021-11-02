package com.web.template.Utils;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

;


public class ReadJson
{
    //    private static final Logger logger = LogManager.getLogger(ReadJson.class);
    private static ReadJson Obj;

    private ReadJson(){};

    public static ReadJson getInstance()
    {
        if(Obj==null)
            Obj = new ReadJson();
        return Obj;
    }

    /**
     * Function accept file name(with extension) present in the resource folder"/main/java/resource". Resource folder that is marked as source.
     */
    public List<String> parsingJSONFile(String fileName, String parentNode, String searchKey)
    {
        JSONParser jp = new JSONParser();
        List<String> listOfValues = new LinkedList<String>();

        try
        {
            JSONObject jsonObject = (JSONObject)jp.parse(new FileReader(ReadJson.getInstance().getClass().getClassLoader().getResource(fileName).getPath()));
            //Default return type is JSONArray
            return  parsingJSONArray(jsonObject,parentNode,searchKey);



        }catch(Exception e)
        {
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }
    }

    /**e
     * Incase of need to accept JSONObject and JSONArray, then change the param as JsonElement.
     * Prefer to use this funnction with return value more than one.
     * @param jsonObject
     * @param parentNode
     * @param searchKey
     * @return
     */
    public List<String> parsingJSONArray(JSONObject jsonObject, String parentNode, String searchKey)
    {
        JSONParser jp = new JSONParser();
        List<String> listOfValues = new LinkedList<String>();

        try
        {
            //Default return type is JSONArray
            JSONArray arrayObj= (JSONArray) jsonObject.get(parentNode);
            if(arrayObj.size()>0)
            {
                arrayObj.forEach(obj-> {
                    JSONObject childObj = (JSONObject) obj;
//                    logger.info(childObj.get(searchKey));
                    listOfValues.add(childObj.get(searchKey).toString());
                });

            }
            return listOfValues;

        }catch(Exception e)
        {
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }
    }
    /**
     * Function accept file name(with extension) present in the resource folder"/main/java/resource". Resource folder that is marked as source.
     * This function return SystemName and FirstName with parentNode as TestCaseID
     *
     *"TestCaseID":{
     *     "System Name": "ITKAutomation2",
     *     "First Name": "ITK ",
     *
     *   }
     */
    public Map<String, String> parsingJSONForAParentNode(String fileName, String parentNode, String searchKey)
    {
        JSONParser jp = new JSONParser();
        List<String> listOfValues = new LinkedList<String>();

        try
        {
            JSONObject jsonObject = (JSONObject)jp.parse(new FileReader(ReadJson.getInstance().getClass().getClassLoader().getResource(fileName).getPath()));
            JSONObject obj=(JSONObject)jsonObject.get(parentNode);
            System.out.println("abcd: ="+obj);

            if(searchKey==null) {
                return (Map<String, String>) obj.keySet().stream()
                        .collect(Collectors.toMap(x -> x, x -> {
                            if (obj.get(x).getClass().toString().contains("java.lang.String"))
                                return obj.get(x);
                            return x;
                        }));
            }
            System.out.println("searchKey:= "+searchKey);
            JSONObject childObj=(JSONObject)obj.get(searchKey);

            return (Map<String, String>) childObj.keySet().stream() .collect(Collectors.toMap(x -> x, x ->childObj.get(x)));

        }catch(Exception e)
        {
            e.printStackTrace();
            Collections.emptyMap();
        }
        return null;
    }



    public String getAValueFromJSON(String fileName, String parentNode, String searchKey)
    {
        try
        {
            JSONParser jp = new JSONParser();

            JSONObject jsonObject = (JSONObject)jp.parse(new FileReader(ReadJson.getInstance().getClass().getClassLoader().getResource(fileName).getPath()));
            JSONObject parentObj= (JSONObject) jsonObject.get(parentNode);
            return Optional.ofNullable(parentObj.get(searchKey).toString()).get();

        }catch(Exception e)
        {
            e.printStackTrace();
            return "Null";
        }
    }


    public String getValueFromJsonKey(String fileName, String searchKey)
    {
        try
        {
            JSONParser jp = new JSONParser();

            JSONObject jsonObject = (JSONObject)jp.parse(new FileReader(ReadJson.getInstance().getClass().getClassLoader().getResource(fileName).getPath()));
            return Optional.ofNullable(jsonObject.get(searchKey).toString()).get();

        }catch(Exception e)
        {
            e.printStackTrace();
            return "Null";
        }
    }


    public HashMap<String, Object> getJSONForParentNode(String fileName, String parentNode)
    {
        JSONParser jp = new JSONParser();
        List<String> listOfValues = new LinkedList<String>();

        try
        {
            JSONObject jsonObject = (JSONObject)jp.parse(new FileReader(ReadJson.getInstance().getClass().getClassLoader().getResource(fileName).getPath()));
            JSONObject obj=(JSONObject)jsonObject.get(parentNode);

            return obj;

        }catch(Exception e)
        {
            e.printStackTrace();
            Collections.emptyMap();
        }
        return null;
    }

}

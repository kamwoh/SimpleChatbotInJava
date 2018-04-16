package chatbot.utils;

import chatbot.utils.json.JSONArray;
import chatbot.utils.json.JSONObject;
import chatbot.utils.json.JSONUtils;
import chatbot.utils.json.parser.ParseException;
import jdk.nashorn.internal.ir.debug.JSONWriter;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static String getCurrentTimeInString() {
        SimpleDateFormat sdf = new SimpleDateFormat("y-MM-d HH:mm:ss");
        return sdf.format(new Date());
    }

    public static String[] readFileInLines() {
        return null;
    }

//    public static void serializeHashMapToJSON(HashMap map, String filePath) throws IOException {
//        BufferedWriter f = new BufferedWriter(new FileWriter(filePath));
//        JSONObject obj = new JSONObject();
//
//        for (Object entry : map.entrySet()) {
//            Map.Entry entry1 = (Map.Entry) entry;
//            if (entry1.getValue() instanceof Double[][]) {
//                Double[][] d = (Double[][]) entry1.getValue();
//                JSONArray list = new JSONArray();
//
//                int count = 0;
//                System.out.println("d size -> " + d.length + " " + d[0].length + " -> " + d.length * d[0].length);
//
//                for (int i = 0; i < d.length; i++) {
//                    JSONArray list1 = new JSONArray();
//                    for (int j = 0; j < d[i].length; j++) {
//                        list1.add(d[i][j]);
//                    }
//
//                    list.add(list1.toJSONString());
//                    count += ((String) list.get(list.size() - 1)).getBytes().length;
//                    System.out.println("current size -> " + count / 1024 / 1024);
//                }
//
//                obj.put(entry1.getKey(), list.toJSONString());
//                System.out.println(((String) obj.get(entry1.getKey())).getBytes().length);
//            } else {
//                obj.put(entry1.getKey(), entry1.getValue().toString());
//            }
//        }
//
//        f.write(obj.toJSONString());
//        f.flush();
//        f.close();
//    }
//
//    public static HashMap deserializeHashMapFromJSON(String filePath) throws IOException, ClassNotFoundException, ParseException {
//        JSONObject obj = (JSONObject) JSONUtils.parseJsonFile(filePath);
//        HashMap map = new HashMap();
//
//        for (Object entry : obj.entrySet()) {
//            Map.Entry entry1 = (Map.Entry) entry;
//            if (entry1.getValue() instanceof JSONArray) {
//                JSONArray arr = (JSONArray) entry1.getValue();
//                Double[][] out = new Double[arr.size()][((JSONArray) arr.get(0)).size()];
//
//                for (int i = 0; i < arr.size(); i++) {
//                    JSONArray row = (JSONArray) arr.get(i);
//                    for (int j = 0; j < row.size(); j++) {
//                        out[i][j] = (Double) row.get(j);
//                    }
//                }
//
//                map.put(entry1.getKey(), out);
//            } else
//                map.put(entry1.getKey(), entry1.getValue());
//        }
//
//        return map;
//    }

    public static void serializeHashMap(HashMap map, String filePath) throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(map);
        oos.close();
        fos.close();
    }

    public static HashMap deserializeHashMap(String filePath) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(filePath);
        ObjectInputStream ois = new ObjectInputStream(fis);
        HashMap map = (HashMap) ois.readObject();
        ois.close();
        fis.close();
        return map;
    }
}

package me.nanigans.potterworldspells.Utils.Config;

import me.nanigans.potterworldspells.PotterWorldSpells;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonUtils {

    private static final PotterWorldSpells plugin = PotterWorldSpells.getPlugin(PotterWorldSpells.class);

    private final File jsonPath;

    public JsonUtils(String path){
        jsonPath = new File(plugin.getDataFolder()+"/config.json");
    }

    public Object getData(String path) throws IOException, ParseException {

        if(!this.jsonPath.exists()) return null;
        String[] paths = path.split("\\.");
        JSONParser jsonParser = new JSONParser();
        Object parsed = jsonParser.parse(new FileReader(this.jsonPath));
        JSONObject jsonObject = (JSONObject) parsed;

        JSONObject currObject = (JSONObject) jsonObject.clone();

        for (String s : paths) {

            if(currObject.get(s) instanceof JSONObject)
                currObject = (JSONObject) currObject.get(s);
            else return currObject.get(s);

        }

        return currObject;

    }

    public static List<?> fromString(String string) {
        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        Object[] result = new Object[strings.length];
        System.arraycopy(strings, 0, result, 0, result.length);
        return new ArrayList(Arrays.asList(result));
    }

}

package uk.co.olbois.facecraft.model.consoleoutput;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ConsoleOutput {

    private Long id;
    private String message;

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public static ConsoleOutput parse(String json) {
        Gson gson = new GsonBuilder().create();

        ConsoleOutput co = gson.fromJson(json, ConsoleOutput.class);

        JsonElement ele = gson.fromJson(json, JsonElement.class);
        JsonObject jsonAsObj = ele.getAsJsonObject();
        JsonElement links = jsonAsObj.get("_links");
        String url = links.getAsJsonObject().get("self").getAsJsonObject().get("href").getAsString();
        String[] arr = url.split("/");

        co.id = Long.parseLong(arr[arr.length - 1]);

        return co;
    }

    public static ConsoleOutput[] parseArray(String json) {
        Gson gson = new GsonBuilder().create();

        JsonElement ele = gson.fromJson(json, JsonElement.class);
        JsonArray consoleOutputsFromJson = ele.getAsJsonObject().get("_embedded").getAsJsonObject().get("consoleoutputs").getAsJsonArray();

        ConsoleOutput[] consoleOutputs = new ConsoleOutput[consoleOutputsFromJson.size()];
        int count = 0;
        for(JsonElement server : consoleOutputsFromJson){
            consoleOutputs[count++] = parse(server.getAsJsonObject().toString());
        }

        return consoleOutputs;
    }

}

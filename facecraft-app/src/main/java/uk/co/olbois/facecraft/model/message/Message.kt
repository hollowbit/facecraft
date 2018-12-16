package uk.co.olbois.facecraft.model.message

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import uk.co.olbois.facecraft.model.SampleUser
import java.util.*

class Message(var username : String, var senderType : String, var content : String, var time : Date, var url : String?) {


    companion object {

        fun parse(json: String): Message {
            val gson = GsonBuilder()
                    .create()

            val s: Message = gson.fromJson(json, Message::class.java)

            val ele = gson.fromJson(json, JsonElement::class.java)
            val jsonAsObj = ele.asJsonObject
            val links = jsonAsObj.get("_links")
            val url = links.asJsonObject.get("self").asJsonObject.get("href").asString
            s.url = url
            //s.serversOwnedUrl = links.asJsonObject.get("serversOwned").asJsonObject.get("href").asString
            //s.serversPartOfUrl = links.asJsonObject.get("serversPartOf").asJsonObject.get("href").asString
            val arr = url.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            //s.id = java.lang.Long.parseLong(arr[arr.size - 1])

            return s
        }

        fun parseArray(json: String): Array<Message?> {
            val gson = GsonBuilder()
                    .create()

            val ele = gson.fromJson(json, JsonElement::class.java)
            val messagesFromJson = ele.asJsonObject.get("_embedded").asJsonObject.get("messages").asJsonArray

            val messages = arrayOfNulls<Message>(messagesFromJson.size())
            var count = 0
            for (message in messagesFromJson) {
                messages[count++] = parse(message.asJsonObject.toString())
            }

            return messages
        }
    }
}
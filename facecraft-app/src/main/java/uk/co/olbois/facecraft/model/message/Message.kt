package uk.co.olbois.facecraft.model.message

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import uk.co.olbois.facecraft.model.SampleUser
import java.util.*

class Message(var username : String, var senderType : String, var content : String, var time : Date, var server : String, var url : String?) {


    fun format(): String {
        val gson = GsonBuilder()
                .create()

        return gson.toJson(this)
    }

    companion object {

        fun parse(json: String): Message {
            val gson = GsonBuilder()
                    .create()

            // get message from json
            val s: Message = gson.fromJson(json, Message::class.java)

            val ele = gson.fromJson(json, JsonElement::class.java)
            val jsonAsObj = ele.asJsonObject

            // get the links and self from
            val links = jsonAsObj.get("_links")
            val url = links.asJsonObject.get("self").asJsonObject.get("href").asString
            s.url = url
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
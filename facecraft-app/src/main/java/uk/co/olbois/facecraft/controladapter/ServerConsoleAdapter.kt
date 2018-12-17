package uk.co.olbois.facecraft.controladapter

interface ServerConsoleAdapter {

    fun sendMessageToServer(message : String, callback : (String) -> Unit)

}
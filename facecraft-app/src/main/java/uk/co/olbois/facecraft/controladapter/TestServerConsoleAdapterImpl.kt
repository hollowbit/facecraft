package uk.co.olbois.facecraft.controladapter

class TestServerConsoleAdapterImpl : ServerConsoleAdapter {

    override fun sendMessageToServer(message: String, callback: (String) -> Unit) {
            callback.invoke("Pong: $message")
    }

}
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import javafx.application.Application
import javafx.event.ActionEvent
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

class ClientApplication : Application() {
    override fun start(stage: Stage) {
        runBlocking {
            launch {
                val fxmlLoader = FXMLLoader(ClientApplication::class.java.getResource("application-view.fxml"))
                val scene = Scene(fxmlLoader.load(), 320.0, 240.0)
                stage.title = "Hello!"
                stage.scene = scene
                stage.show()
            }

            val selectorManager = SelectorManager(Dispatchers.IO)
            val socket = aSocket(selectorManager).tcp().connect("127.0.0.1", 9002)

            val receiveChannel = socket.openReadChannel()
            val sendChannel = socket.openWriteChannel(autoFlush = true)

            CoroutineScope(Dispatchers.IO).launch {
                while (true) {
                    val greeting = receiveChannel.readUTF8Line()
                    if (greeting != null) {
                        println(greeting)
                    } else {
                        println("Server closed a connection")
                        socket.close()
                        selectorManager.close()
                        exitProcess(0)
                    }
                }
            }

            CoroutineScope(Dispatchers.Default).launch {
                while (true) {
                    val myMessage = readln()
                    sendChannel.writeStringUtf8("$myMessage\n")
                }
            }
        }
    }

    fun onHelloButtonClick(actionEvent: ActionEvent) {
    }
}

fun main() {
    Application.launch(ClientApplication::class.java)
}
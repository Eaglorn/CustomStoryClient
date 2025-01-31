import javafx.application.Application
import javafx.event.ActionEvent
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

class ClientApplication : Application() {
    override fun start(stage: Stage) {
        runBlocking {
            launch() {
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

            launch(Dispatchers.IO) {
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

            launch {
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
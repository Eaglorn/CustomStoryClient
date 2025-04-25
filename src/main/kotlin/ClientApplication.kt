import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import javafx.application.Application
import javafx.event.ActionEvent
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import com.github.luben.zstd.Zstd
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.readFully
import io.ktor.utils.io.readInt
import io.ktor.utils.io.writeFully
import kotlinx.coroutines.Dispatchers
import ru.eaglorn.Message.ChatMessage
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import kotlin.compareTo

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
            println("Connected to server")

            val receiveChannel = socket.openReadChannel()
            val sendChannel = socket.openWriteChannel(autoFlush = true)

            val compressedWelcome = ByteArray(1024)
            val welcomeSize = receiveChannel.readAvailable(compressedWelcome, 0, compressedWelcome.size)
            val decompressedWelcome = Zstd.decompress(compressedWelcome, 1024)
            val welcomeMessage = ChatMessage.parseFrom(decompressedWelcome)
            println("Server: ${welcomeMessage.message}")

            val userName = "ClientUser"
            val userMessage = ChatMessage.newBuilder()
                .setName(userName)
                .setMessage("Hello, server!")
                .build()

            val compressedMessage = Zstd.compress(userMessage.toByteArray())
            val messageSizeBytes = ByteBuffer.allocate(4).putInt(compressedMessage.size).array()
            sendChannel.writeFully(messageSizeBytes)
            sendChannel.writeFully(compressedMessage)

            val responseSize = receiveChannel.readInt()
            val compressedResponse = ByteArray(responseSize)
            receiveChannel.readFully(compressedResponse)

            val decompressedResponse = Zstd.decompress(compressedResponse, 1024)
            val responseMessage = ChatMessage.parseFrom(decompressedResponse)
            println("Server: ${responseMessage.message}")

            socket.close()
        }
    }

    fun onHelloButtonClick(actionEvent: ActionEvent) {
    }
}

fun main() {
    Application.launch(ClientApplication::class.java)
}

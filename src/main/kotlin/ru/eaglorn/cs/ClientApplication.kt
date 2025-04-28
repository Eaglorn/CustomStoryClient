package ru.eaglorn.cs

import javafx.application.Application
import javafx.stage.Stage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.writeFully
import kotlinx.coroutines.launch

class ClientApplication : Application() {
    override fun start(stage: Stage) {
        runBlocking {
            launch {
                val selectorManager = SelectorManager(Dispatchers.IO)
                val socket = aSocket(selectorManager).tcp().connect("127.0.0.1", 9002)
                println("Connected to server")

                val sendChannel = socket.openWriteChannel(autoFlush = true)
                sendChannel.writeFully("Client connected successfully".toByteArray())

                socket.close()
            }
        }
    }
}

fun main() {
    Application.launch(ClientApplication::class.java)
}

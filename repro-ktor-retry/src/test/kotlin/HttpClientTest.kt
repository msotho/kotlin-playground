import com.example.HttpClient

import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HttpClientTest {
    lateinit var httpClient: HttpClient

    private val server = MockWebServer()
    private val dispatcher: Dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            return when (request.path) {
                "/v1/timeout" -> MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBodyDelay(4, TimeUnit.SECONDS)
                    .setBody("{\"message\":\"delayed response\"}")

                else -> MockResponse().setResponseCode(404)
            }
        }
    }
    @BeforeAll
    fun setUp() {
        server.dispatcher = dispatcher
    }

    @BeforeEach
    fun initClient() {
        httpClient = HttpClient(
            baseUrl = "${server.url("/v1")}",
            token = "token",
            timeout = 1.seconds,
            retries = 1
        )
    }

    @AfterAll
    fun tearDown() {
        server.shutdown()
    }

    @Serializable
    data class MyRequest(@SerialName("name") val yourName: String)

    @Serializable
    data class MyResponse(@SerialName("message") val message: String)

    @Test
    fun `should handle the timeout exception`() = runTest {
        val request = MyRequest("test")
        var thrown: Throwable? = null
        try {
            httpClient.use { client ->
                client.post<MyRequest, MyResponse>("/timeout", request).getOrThrow()
            }
        } catch (e: Exception) {
            thrown = e
        }

        assertEquals(HttpRequestTimeoutException::class, thrown?.javaClass?.kotlin)

        println("Request count: ${server.requestCount}")

        val timeoutRequests = (0 until server.requestCount).map { server.takeRequest() }

        assertEquals(2, timeoutRequests.size)
    }

}
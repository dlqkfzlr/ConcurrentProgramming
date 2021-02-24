package m.woong.concurrentprogramming

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.w3c.dom.Element
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

class MainActivity : AppCompatActivity() {
    val feeds = listOf(
        "https://www.npr.org/rss/rss.php?id=1001",
        "http://rss.cnn.com/rss/cnn_topstories.rss",
        "http://feeds.foxnews.com/foxnews/politics?format=xml",
        "fake"
    )
    private val dispatcher = newFixedThreadPoolContext(2, "IO")
    private val factory = DocumentBuilderFactory.newInstance()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        asyncLoadNews()

    }

    override fun onResume() {
        super.onResume()
    }

    @SuppressLint("SetTextI18n")
    private fun asyncLoadNews() = GlobalScope.launch {
        val requests = mutableListOf<Deferred<List<String>>>()
        feeds.mapTo(requests) {
            asyncFetchRssHeadlines(it, dispatcher)
        }
        requests.forEach {  // 각각의 asyncFetchRssHeadlines가 모두 완료될 때까지 대기하는 코드
            it.join()
        }
        val headlines = requests
            .filter { !it.isCancelled }
            .flatMap { it.getCompleted() }

        val failed = requests.filter { it.isCancelled }
            .size

        val newsCount = findViewById<TextView>(R.id.newsCount)
        val warnings = findViewById<TextView>(R.id.warnings)
        val obtained = requests.size - failed

        GlobalScope.launch(Dispatchers.Main) {
            newsCount.text = "Found ${headlines.size} News" +
                    "in ${obtained} feeds"
            if (failed > 0) warnings.text = "Failed to fetch $failed feeds"
        }
    }

    private fun asyncFetchRssHeadlines(
        feed: String,
        dispatcher: CoroutineDispatcher
    ) = GlobalScope.async(dispatcher) {
        val builder = factory.newDocumentBuilder()
        val xml = builder.parse(feed)
        val news = xml.getElementsByTagName("channel").item(0)
        (0 until news.childNodes.length)
            .map { news.childNodes.item(it) }
            .filter { Node.ELEMENT_NODE == it.nodeType }
            .map { it as Element }
            .filter { "item" == it.tagName }
            .map {
                it.getElementsByTagName("title").item(0).textContent
            }
    }
}
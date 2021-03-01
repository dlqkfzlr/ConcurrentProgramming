package m.woong.concurrentprogramming

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import m.woong.concurrentprogramming.adapter.ArticleAdapter
import m.woong.concurrentprogramming.databinding.ActivityMainBinding
import m.woong.concurrentprogramming.model.Article
import m.woong.concurrentprogramming.model.Feed
import org.w3c.dom.Element
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

class MainActivity : AppCompatActivity() {
    val feeds = listOf(
        Feed("npr", "https://www.npr.org/rss/rss.php?id=1001"),
        Feed("cnn", "http://rss.cnn.com/rss/cnn_topstories.rss"),
        Feed("fox", "http://feeds.foxnews.com/foxnews/politics?format=xml"),
        Feed("inv", "htt:myNewsFeed")
    )
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewAdapter: ArticleAdapter
    private val dispatcher = newFixedThreadPoolContext(2, "IO")
    private val factory = DocumentBuilderFactory.newInstance()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewAdapter = ArticleAdapter()
        binding.articles.adapter = viewAdapter

        asyncLoadNews()
    }

    override fun onResume() {
        super.onResume()
    }

    @SuppressLint("SetTextI18n")
    private fun asyncLoadNews() = GlobalScope.launch {
        val requests = mutableListOf<Deferred<List<Article>>>()
        feeds.mapTo(requests) {
            asyncFetchArticles(it, dispatcher)
        }
        requests.forEach {  // 각각의 asyncFetchRssHeadlines가 모두 완료될 때까지 대기하는 코드
            it.join()
        }
        val articles = requests
            .filter { !it.isCancelled }
            .flatMap { it.getCompleted() }

        val failedCount = requests.filter { it.isCancelled }
            .size

        val obtained = requests.size - failedCount

        launch(Dispatchers.Main) {
            binding.progressBar.visibility = View.GONE
            Log.d("DEBUG", "articles개수:$articles")
            viewAdapter.submitList(articles)
        }
    }

    private fun asyncFetchArticles(
        feed: Feed,
        dispatcher: CoroutineDispatcher
    ) = GlobalScope.async(dispatcher) {
        delay(1000)
        val builder = factory.newDocumentBuilder()
        val xml = builder.parse(feed.url)
        val news = xml.getElementsByTagName("channel").item(0)
        (0 until news.childNodes.length)
            .asSequence()
            .map { news.childNodes.item(it) }
            .filter { Node.ELEMENT_NODE == it.nodeType }
            .map { it as Element }
            .filter { "item" == it.tagName }
            .map {
                val title = it.getElementsByTagName("title").item(0).textContent
                var summary = it.getElementsByTagName("description").item(0).textContent
                if(!summary.startsWith("<div")
                    && summary.contains("<div")) {
                    summary = summary.substring(0, summary.indexOf("<div"))
                }
                Article(feed.name, title, summary)
            }
            .toList()
    }
}
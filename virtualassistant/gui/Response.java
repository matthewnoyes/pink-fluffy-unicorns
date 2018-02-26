package virtualassistant.gui;

import java.util.*;
import virtualassistant.data.news.NewsObj;

public class Response extends Message {

private LinkedList<NewsObj> news;

public Response(String response, LinkedList<NewsObj> news) {
				super(response);
				this.news = news;
}

public LinkedList<NewsObj> getNews() {
				return news;
}

}

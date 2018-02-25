package virtualassistant.gui;

import virtualassistant.data.news.NewsObj;

public class Response extends Message {

private NewsObj[] news;

public Response(String response, NewsObj[] news) {
				super(response);
				this.news = news;
}

public NewsObj[] getNews() {
				return news;
}

}

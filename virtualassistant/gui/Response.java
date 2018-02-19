public class Response extends Message {

private News[] news;

public Response(String response, News[] news) {
				super(response);
				this.news = news;
}

public News[] getNews() {
				return news;
}

}

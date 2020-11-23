package hft.cwi.etl.crawler;

import java.net.URI;

public class CrawlerSeed {
	private URI uri;
	private int level;

	public CrawlerSeed(final URI anUri, final int aLevel) {
		this.uri = anUri;
		this.level = aLevel;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(final URI uri) {
		this.uri = uri;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(final int level) {
		this.level = level;
	}
	
}

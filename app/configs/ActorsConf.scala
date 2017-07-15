package configs

import com.google.inject.AbstractModule
import indexer.{MovieEnricher, MovieIndexer, MovieSuggestionIndexer}
import play.api.libs.concurrent.AkkaGuiceSupport

class ActorsConf extends AbstractModule with AkkaGuiceSupport {

  override def configure = {
    bindActor[MovieEnricher](MovieEnricher.Name)
    bindActor[MovieIndexer](MovieIndexer.Name)
    bindActor[MovieSuggestionIndexer](MovieSuggestionIndexer.Name)
  }

}

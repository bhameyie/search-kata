package com.bhameyie.suggester.api.rest

import akka.actor.ActorSystem
import akka.testkit.{DefaultTimeout, ImplicitSender, TestActors, TestKit}
import com.bhameyie.suggester.api.domain.SearchController
import com.bhameyie.suggester.api.domain.protocols.CityLookupResult.{MultipleFound, NoMatchFound, RankedResults, SingleMatch}
import com.bhameyie.suggester.api.domain.protocols.SearchCriteria.{SearchByQuery, SearchByQueryAndCoordinates}
import com.bhameyie.suggester.api.domain.protocols._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class SearchControllerSpec extends TestKit(ActorSystem("ControllerSpec")) with ImplicitSender
  with WordSpecLike with DefaultTimeout with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "On receipt of NoMatchFound, Controller" should{
    "forward a Nothing result when no match found" in {
      val req = NoMatchFound(testActor,"q")

      val sut = system.actorOf(SearchController(null, null))

      sut ! req

      expectMsg[SearchResult.Nothing.type](SearchResult.Nothing)
    }
  }

  "On receipt of SingleMatch, Controller" should{
    "forward a single result with maximum score" in {
      val req = SingleMatch(testActor,"Adzhaks",CityRecord(GeoSpatialId(0),"","ds",Coordinates(3,4),None))

      val sut = system.actorOf(SearchController(null, null))

      sut ! req

      expectMsg[SearchResult.Matches](SearchResult.Matches(List(RankedCityRecord(1,req.cityRecord))))
    }
  }

  "On receipt of RankedResults, Controller" should{
    "forward ranked results with no change" in{
      val req= RankedResults(
        testActor,"nla",List(RankedCityRecord(5,CityRecord(GeoSpatialId(0),"nla","ds",Coordinates(3,4),None)))
      )

      val sut = system.actorOf(SearchController(null, null))

      sut ! req

      expectMsg[SearchResult.Matches](SearchResult.Matches(req.records))

    }
  }

  "On receipt of MultipleFound, Controller" should{
    "request scoring from ranker" in {
      val blackhole = system.actorOf(TestActors.blackholeProps)

      val req= MultipleFound(blackhole,"London",List(CityRecord(GeoSpatialId(0),"nla","ds",Coordinates(3,4),None)))

      val sut = system.actorOf(SearchController(testActor, null))

      sut ! req

      expectMsg[ComputeCityRankings](ComputeCityRankings(blackhole,req.searchedName,req.records))
    }
  }


  "On receipt of FindCity request, Controller" should {
    "forward a SearchByQuery ti finder when only name present" in {
      val req = FindCity("seoul", None)

      val sut = system.actorOf(SearchController(null, testActor))

      sut ! req

      expectMsg[SearchByQuery](SearchByQuery(testActor, req.name))

    }

    "forward a SearchByQueryAndCoordinates ti finder" in {
      val req = FindCity("seoul", Some(Coordinates(1,3)))

      val sut = system.actorOf(SearchController(null, testActor))

      sut ! req

      expectMsg[SearchByQueryAndCoordinates](SearchByQueryAndCoordinates(testActor, req.name,req.coordinates.get))

    }
  }
}

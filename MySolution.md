
This repository is comprised of a `data` folder, an `importer` folder and a `search-api` folder.

The tools I've used for this kata are `Akka-http`, `Akka-streams`, `Scala` and `MongoDB`.
I scala for hosting and performance reasons, whereas I used MongoDb for its geolocation capabilities.
To compile, type the command `sbt compile stage`

### Data

In it you'll find the training data was used to build up the search api. More data can be obtained from the [Geonames](http://geonames.org) and imported using the `Importer`.

### Importer

The `Importer` is a small utility whose purpose it pump data into the MongoDb `cityrecords` collection.

``` bash
importer 0.1
Usage: importer [options]

  -f, --data-file <data file>
                           data-file is the tsv file containing cities without header
  -c, --codes-file <admin codes file>
                           codes-file is the tsv file containing the admins codes without header
  -u, --mongo-url <value>  mongo-url is the mongodb url (e.g. mongodb://localhost
  -d, --mongo-db <value>   mongo-db is the mongodb url (e.g. suggester
```

E.g. `importer -f data/small.tsv -c data/small_codes.tsv -u mongodb://localhost -d suggester`

### Search Api

The actual focus of the kata, it is a fairly basic `Akka-Http` Rest service. 

GET /suggestions?q=A

```json

[
    {
        "name": "Ajax, Ontario, CA",
        "latitude": "43.85012",
        "longitude": "-79.03288",
        "score": 0.78
    },
    {
        "name": "Adzhaks, Ontario, CA",
        "latitude": "43.85012",
        "longitude": "-79.03288",
        "score": 0.74
    }
]
```

The score is independent from the proximity of the calling user. However, sorting order takes into consideration how close the caller claims he is to the cities that matches a particular name. The reasoning behind it is that a user is likely to want the closer results if coordinates are supplied in the query.

#### What is missing

- Perf tests :(. Didn't get to them by my deadline. From testing the API via Postman, most requests take between 45 to 150 ms to complete.

#### Some enhancement ideas

- Trending search. It should be easy to add such a thing.
  The cheapest way would be using Akka's internal bus to publish an event (e.g. `context.system.eventStream.publish(SuggestionRequest(query, optionalCoordinates))`), then have an actor subscribe to that and increment some counter.

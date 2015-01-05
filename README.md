etymonline_rest_api
===================

This project is a RESTful API over the [Online Etymology Dictionary](http://www.etymonline.com/).
Please note that this project is not affiliated with the owners and operators of the Online Etymology Dictionary
in any way.

The goal of this project is to provide a RESTful API for etymology searches with the Online Etymology Dictionary
as the first source of etymologies and possibly [Wiktionary](http://en.wiktionary.org/) later too. A prime motivator
is that currently etymonline.com is not very mobile friendly; pinching and zooming are required to navigate the site.
The RESTful API makes it easier to add various front-ends, which will come later: HTML5, iOS, Android, etc.
Currently, screen-scraping is used to query etymonline.com since this API does not have database access.

The technology stack used for the REST API is Scala oriented: sbt as the build tool, Spray Client for the
HTTP client, Spray Routing to expose RESTful resources, and Spray Can to act as the server.
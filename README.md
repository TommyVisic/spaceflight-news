# spaceflight-news
A news feed app powered by the Spaceflight News API

<table>
  <tr>
    <td valign="top"><img src="https://github.com/TommyVisic/spaceflight-news/assets/3027722/c0088e77-80ac-4167-9721-11a477637b9b"/></td>
    <td valign="top"><img src="https://github.com/TommyVisic/spaceflight-news/assets/3027722/8124f7a6-60c4-4914-b680-170267c0832b"/></td>
  </tr>
</table>

## Features
* Written from scratch in Kotlin & Compose
* Fetches articles from the Spaceflight News REST API with Retrofit.
* Support for pagination with Paging3.
* Caches articles in a Room database, which acts as the single source of truth.
* Automatically fetches fresh data if cache is too old or if more articles are needed.
* Handles loss of internet connection gracefully.
* Loads article images with COIL.
* Dependency injection with Dagger Hilt.
* Demonstrates MVVM architecture.
* Demonstrates Kotlin Flows & Coroutines.
* Works in dark & light modes (driven by the OS settings)

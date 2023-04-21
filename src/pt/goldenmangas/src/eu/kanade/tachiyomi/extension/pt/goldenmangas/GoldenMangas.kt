package eu.kanade.tachiyomi.extension.mangahost

import eu.kanade.tachiyomi.multisrc.madara.Madara
import okhttp3.Response

class MangaHost : Madara(
    "MangaHost",
    "https://mangahost4.com.br",
    "pt-BR"
) {
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        val searchUrl = "$baseUrl/search?q=$query&page=$page"
        return GET(searchUrl, headers)
    }

    override fun searchMangaParse(response: Response): MangasPage {
        val document = response.asJsoup()
        val mangas = document.select(searchMangaSelector()).map { element ->
            SManga.create().apply {
                title = element.select(searchTitleSelector()).text()
                thumbnail_url = element.select(searchThumbnailSelector()).attr("src")
                setUrlWithoutDomain(element.select(searchUrlSelector()).attr("href"))
            }
        }
        val hasNextPage = !document.select(searchNextPageSelector()).isNullOrEmpty()
        return MangasPage(mangas, hasNextPage)
    }
}

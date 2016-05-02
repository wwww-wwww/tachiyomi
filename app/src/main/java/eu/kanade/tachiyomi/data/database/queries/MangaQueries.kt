package eu.kanade.tachiyomi.data.database.queries

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery
import com.pushtorefresh.storio.sqlite.queries.Query
import com.pushtorefresh.storio.sqlite.queries.RawQuery
import eu.kanade.tachiyomi.data.database.DbProvider
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.data.database.resolvers.LibraryMangaGetResolver
import eu.kanade.tachiyomi.data.database.tables.ChapterTable
import eu.kanade.tachiyomi.data.database.tables.MangaCategoryTable
import eu.kanade.tachiyomi.data.database.tables.MangaTable

interface MangaQueries : DbProvider {

    fun getMangas() = db.get()
            .listOfObjects(Manga::class.java)
            .withQuery(Query.builder()
                    .table(MangaTable.TABLE)
                    .build())
            .prepare()

    fun getLibraryMangas() = db.get()
            .listOfObjects(Manga::class.java)
            .withQuery(RawQuery.builder()
                    .query(libraryQuery)
                    .observesTables(MangaTable.TABLE, ChapterTable.TABLE, MangaCategoryTable.TABLE)
                    .build())
            .withGetResolver(LibraryMangaGetResolver.INSTANCE)
            .prepare()

    open fun getFavoriteMangas() = db.get()
            .listOfObjects(Manga::class.java)
            .withQuery(Query.builder()
                    .table(MangaTable.TABLE)
                    .where("${MangaTable.COLUMN_FAVORITE} = ?")
                    .whereArgs(1)
                    .orderBy(MangaTable.COLUMN_TITLE)
                    .build())
            .prepare()

    fun getManga(url: String, sourceId: Int) = db.get()
            .`object`(Manga::class.java)
            .withQuery(Query.builder()
                    .table(MangaTable.TABLE)
                    .where("${MangaTable.COLUMN_URL} = ? AND ${MangaTable.COLUMN_SOURCE} = ?")
                    .whereArgs(url, sourceId)
                    .build())
            .prepare()

    fun getManga(id: Long) = db.get()
            .`object`(Manga::class.java)
            .withQuery(Query.builder()
                    .table(MangaTable.TABLE)
                    .where("${MangaTable.COLUMN_ID} = ?")
                    .whereArgs(id)
                    .build())
            .prepare()

    fun insertManga(manga: Manga) = db.put().`object`(manga).prepare()

    fun insertMangas(mangas: List<Manga>) = db.put().objects(mangas).prepare()

    fun deleteManga(manga: Manga) = db.delete().`object`(manga).prepare()

    fun deleteMangas(mangas: List<Manga>) = db.delete().objects(mangas).prepare()

    fun deleteMangasNotInLibrary() = db.delete()
            .byQuery(DeleteQuery.builder()
                    .table(MangaTable.TABLE)
                    .where("${MangaTable.COLUMN_FAVORITE} = ?")
                    .whereArgs(0)
                    .build())
            .prepare()

}
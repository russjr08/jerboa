package com.jerboa

import com.jerboa.api.API
import com.jerboa.datatypes.ListingType
import com.jerboa.datatypes.SortType
import com.jerboa.datatypes.api.GetPost
import com.jerboa.datatypes.api.GetPosts
import com.jerboa.datatypes.api.GetSite
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testGetSite() = runBlocking {
        val api = API.getInstance()
        val form = GetSite(null)
        val out = api.getSite(form.serializeToMap()).body()!!

        assertEquals("Lemmy", out.site_view!!.site.name)
    }

    @Test
    fun testGetPosts() = runBlocking {
        // TODO
        val api = API.getInstance()
        val form = GetPosts(
            ListingType.All.toString(),
            SortType.Active.toString(),
            null,
            null,
            null,
            null,
            null,
            null
        )
        val out = api.getPosts(form.serializeToMap()).body()!!
        println(out.posts[0])
        assertNotNull(out.posts)
    }

    @Test
    fun testGetPost() = runBlocking {
        val api = API.getInstance()
        val form = GetPost(
            id = 139549,
            auth = null
        )
        val out = api.getPost(form.serializeToMap()).body()!!
        println(out.comments)
        assertNotNull(out)
    }

/*
    @Test
    fun testLogin() = runBlocking {
        val api = API.getInstance()
        val form = Login(username_or_email = "tester12345", password = "tester12345")
        val out = api.login(form).body()!!
        assertNotNull(out.jwt)
    }
*/
}

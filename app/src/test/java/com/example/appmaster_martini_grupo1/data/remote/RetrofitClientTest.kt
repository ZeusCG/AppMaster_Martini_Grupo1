package com.example.appmaster_martini_grupo1.data.remote

import org.junit.Assert.assertNotNull
import org.junit.Test

class RetrofitClientTest {

    @Test
    fun expone_las_dos_apis() {
        assertNotNull(RetrofitClient.climaApi)
        assertNotNull(RetrofitClient.contactoApi)
    }
}

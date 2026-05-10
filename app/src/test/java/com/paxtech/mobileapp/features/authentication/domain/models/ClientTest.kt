package com.paxtech.mobileapp.features.authentication.domain.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ClientTest {

    @Test
    fun `should hold all client fields`() {
        val client = Client(id = 1, firstName = "Gael", lastName = "Ramirez", userId = 99)

        assertEquals(1, client.id)
        assertEquals("Gael", client.firstName)
        assertEquals("Ramirez", client.lastName)
        assertEquals(99, client.userId)
    }

    @Test
    fun `data class equality should compare values`() {
        val a = Client(1, "Gael", "Ramirez", 99)
        val b = Client(1, "Gael", "Ramirez", 99)
        val c = Client(2, "Gael", "Ramirez", 99)

        assertEquals(a, b)
        assertNotEquals(a, c)
    }

    @Test
    fun `copy should keep unchanged fields and update specified ones`() {
        val original = Client(1, "Gael", "Ramirez", 99)

        val renamed = original.copy(firstName = "Alejandro")

        assertEquals("Alejandro", renamed.firstName)
        assertEquals("Ramirez", renamed.lastName)
        assertEquals(99, renamed.userId)
        assertEquals(1, renamed.id)
    }
}

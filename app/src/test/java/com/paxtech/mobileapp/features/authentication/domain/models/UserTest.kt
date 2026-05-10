package com.paxtech.mobileapp.features.authentication.domain.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UserTest {

    @Test
    fun `should expose id email and optional token`() {
        val user = User(id = 1, email = "gael@paxtech.com", token = "jwt.token.here")

        assertEquals(1, user.id)
        assertEquals("gael@paxtech.com", user.email)
        assertEquals("jwt.token.here", user.token)
    }

    @Test
    fun `token should default to null when not provided`() {
        val user = User(id = 2, email = "anon@paxtech.com")

        assertNull(user.token)
    }

    @Test
    fun `data class equality should be based on values`() {
        val a = User(id = 1, email = "a@paxtech.com")
        val b = User(id = 1, email = "a@paxtech.com")
        val c = User(id = 2, email = "a@paxtech.com")

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertNotEquals(a, c)
    }

    @Test
    fun `copy should override only specified fields`() {
        val original = User(id = 1, email = "old@paxtech.com")

        val updated = original.copy(email = "new@paxtech.com")

        assertEquals(1, updated.id)
        assertEquals("new@paxtech.com", updated.email)
    }
}

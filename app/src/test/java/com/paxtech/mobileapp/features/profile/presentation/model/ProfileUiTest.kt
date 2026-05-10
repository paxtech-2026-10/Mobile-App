package com.paxtech.mobileapp.features.profile.presentation.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProfileUiTest {

    private fun sampleProfile(fullName: String = "Gael Ramirez") = ProfileUi(
        id = "profile-1",
        fullName = fullName,
        email = "gael@paxtech.com",
        phoneNumber = "1246264",
        countryCode = "+51",
        gender = GenderUi.MALE,
        avatarUrl = null
    )

    @Test
    fun `firstName should be the substring before the first space`() {
        val profile = sampleProfile("Gael Ramirez")

        assertEquals("Gael", profile.firstName)
    }

    @Test
    fun `firstName should be the whole name when there is no space`() {
        val profile = sampleProfile("Gael")

        assertEquals("Gael", profile.firstName)
    }

    @Test
    fun `firstName should preserve compound first names up to first space`() {
        val profile = sampleProfile("Maria Jose Ramirez")

        assertEquals("Maria", profile.firstName)
    }

    @Test
    fun `default avatarUrl should be null`() {
        val profile = sampleProfile()

        assertNull(profile.avatarUrl)
    }

    @Test
    fun `GenderUi should expose human-readable labels`() {
        assertEquals("Femenino", GenderUi.FEMALE.label)
        assertEquals("Masculino", GenderUi.MALE.label)
        assertEquals("Otro", GenderUi.OTHER.label)
    }

    @Test
    fun `GenderUi default should be FEMALE`() {
        assertEquals(GenderUi.FEMALE, GenderUi.default)
    }
}

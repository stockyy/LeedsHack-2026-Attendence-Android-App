package com.university.app

import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import java.nio.charset.Charset

object NfcManager {

    /**
     * Gets a unique string for a mock database.
     * Works for Credit Cards, Tesco cards, and standard NFC tags.
     */
    fun getUniqueId(tag: Tag): String {
        // Get the hardware identifier (UID)
        val idBytes = tag.id
        return if (idBytes != null && idBytes.isNotEmpty()) {
            // Convert to Hex String (e.g., "04A1B2C3")
            idBytes.joinToString("") { "%02X".format(it) }
        } else {
            "UNKNOWN_TAG"
        }
    }

    // Keep your existing method if you still need to read actual text tags
    fun getTextFromNfc(tag: Tag): String? {
        val ndef = Ndef.get(tag) ?: return null
        val ndefMessage = ndef.cachedNdefMessage ?: return null
        val record = ndefMessage.records.firstOrNull() ?: return null
        return parseTextRecord(record)
    }

    private fun parseTextRecord(record: NdefRecord): String {
        val payload = record.payload
        val textEncoding = if ((payload[0].toInt() and 128) == 0) "UTF-8" else "UTF-16"
        val languageCodeLength = payload[0].toInt() and 63 // Corrected mask for lang code
        return String(
            payload,
            languageCodeLength + 1,
            payload.size - languageCodeLength - 1,
            Charset.forName(textEncoding)
        )
    }
}
package com.university.app

import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import java.nio.charset.Charset

object NfcManager {
    fun getTextFromNfc(tag: Tag): String? {
        val ndef = Ndef.get(tag) ?: return null
        val ndefMessage = ndef.cachedNdefMessage ?: return null
        val record = ndefMessage.records.firstOrNull() ?: return null
        return parseTextRecord(record)
    }

    private fun parseTextRecord(record: NdefRecord): String {
        val payload = record.payload
        val textEncoding = if ((payload[0].toInt() and 128) == 0) "UTF-8" else "UTF-16"
        val languageCodeLength = payload[0].toInt() and 51
        return String(
            payload,
            languageCodeLength + 1,
            payload.size - languageCodeLength - 1,
            Charset.forName(textEncoding)
        )
    }
}
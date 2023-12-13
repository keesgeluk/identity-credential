package com.android.identity.serverretrieval

import co.nstant.`in`.cbor.CborBuilder
import co.nstant.`in`.cbor.model.Array
import co.nstant.`in`.cbor.model.DataItem
import co.nstant.`in`.cbor.model.Number
import co.nstant.`in`.cbor.model.UnicodeString
import com.android.identity.internal.Util

data class ServerRetrievalInformation(
    val version: Long,
    val issuerURL: String,
    val serverRetrievalToken: String
) {
    companion object {
        fun fromCbor(cbor: ByteArray): ServerRetrievalInformation {
            val cmDataItem: DataItem = Util.cborDecode(cbor)
            require(cmDataItem is Array) { "Top-level CBOR is not an array" }
            val items: List<DataItem> = cmDataItem.dataItems
            require(items.size == 3) { "Expected array with 3 elements, got " + items.size }
            require(items[0] is Number) { "First item is not a number" }
            val version = (items[0] as Number).value.toLong()
            require(items[1] is UnicodeString && items[2] is UnicodeString) { "Last two items are not unicode strings" }
            val url = (items[1] as UnicodeString).string
            val token = (items[2] as UnicodeString).string
            return ServerRetrievalInformation(
                version,
                url,
                token
            )
        }
    }

    fun toCbor(): ByteArray {
        return Util.cborEncode(
            CborBuilder()
                .addArray()
                .add(version)
                .add(issuerURL)
                .add(serverRetrievalToken)
                .end()
                .build().get(0)
        )
    }
}
package rhron255.projects.k_dns.protocol

import java.nio.ByteBuffer

class DnsHeader {
    companion object {
        const val BYTES_IN_HEADER = 12

        fun fromBytes(byteArray: ByteArray): DnsHeader {
            val bytes = ByteBuffer.wrap(byteArray)
            val header = ByteArray(BYTES_IN_HEADER)
            bytes.get(header)
            val headerBytes = ByteBuffer.wrap(header)
            return DnsHeader(headerBytes)
        }
    }

    val queryID: Short
    val bitFields: Short
    val questionCount: Short
    val answerCount: Short
    val recordCount: Short
    val additionalResourceCount: Short


    constructor (byteBuffer: ByteBuffer) {
        queryID = byteBuffer.getShort()
        bitFields = byteBuffer.getShort()
        questionCount = byteBuffer.getShort()
        answerCount = byteBuffer.getShort()
        recordCount = byteBuffer.getShort()
        additionalResourceCount = byteBuffer.getShort()
    }
}
package rhron255.projects.k_dns.protocol

import rhron255.projects.k_dns.protocol.header.DnsHeaderMasks.*
import rhron255.projects.k_dns.protocol.header.DnsOpcode
import rhron255.projects.k_dns.protocol.header.DnsResponseCode
import java.nio.ByteBuffer
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

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
    val questionCount: Short
    val isQuestion: Boolean
    val opcode: DnsOpcode
    val authoritativeAnswer: Boolean
    val truncation: Boolean
    val recursionDesired: Boolean
    val recursionAvailable: Boolean
    val responseCode: DnsResponseCode
    val answerCount: Short
    val recordCount: Short
    val additionalResourceCount: Short

    constructor(
        queryID: Short,
        questionCount: Short,
        isQuestion: Boolean,
        opcode: DnsOpcode,
        authoritativeAnswer: Boolean,
        truncation: Boolean,
        recursionDesired: Boolean,
        recursionAvailable: Boolean,
        responseCode: DnsResponseCode,
        answerCount: Short,
        recordCount: Short,
        additionalResourceCount: Short
    ) {
        this.queryID = queryID
        this.questionCount = questionCount
        this.isQuestion = isQuestion
        this.opcode = opcode
        this.authoritativeAnswer = authoritativeAnswer
        this.truncation = truncation
        this.recursionDesired = recursionDesired
        this.recursionAvailable = recursionAvailable
        this.responseCode = responseCode
        this.answerCount = answerCount
        this.recordCount = recordCount
        this.additionalResourceCount = additionalResourceCount
    }

    constructor (byteBuffer: ByteBuffer) {
        queryID = byteBuffer.getShort()
        with(byteBuffer.getShort()) {
            isQuestion = (this and QUERY_OR_RESPONSE.mask) == 0.toShort()
            opcode = DnsOpcode.from((this and OPCODE.mask).toInt() shr 11)
            authoritativeAnswer = (this and AUTHORITATIVE_ANSWER.mask) != 0.toShort()
            truncation = (this and TRUNCATION.mask) != 0.toShort()
            recursionDesired = (this and RECURSION_DESIRED.mask) != 0.toShort()
            recursionAvailable = (this and RECURSION_AVAILABLE.mask) != 0.toShort()
            responseCode = DnsResponseCode.from((this and RESPONSE_CODE.mask))
        }
        questionCount = byteBuffer.getShort()
        answerCount = byteBuffer.getShort()
        recordCount = byteBuffer.getShort()
        additionalResourceCount = byteBuffer.getShort()
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("DnsHeader{\n")
        stringBuilder.append("queryID=$queryID,")
        stringBuilder.append("opcode=$opcode,")
        if (authoritativeAnswer) {
            stringBuilder.append("AA,")
        }
        if (truncation) {
            stringBuilder.append("TC,")
        }
        if (recursionDesired) {
            stringBuilder.append("RD,")
        }
        if (recursionAvailable) {
            stringBuilder.append("RA,")
        }
        stringBuilder.append("responseCode=$responseCode,")
        stringBuilder.append("questionCount=$questionCount,")
        stringBuilder.append("answerCount=$answerCount,")
        stringBuilder.append("recordCount=$recordCount,")
        stringBuilder.append("additionalResourceCount=$additionalResourceCount\n}")

        return stringBuilder.toString()
    }

    fun toBytes() = ByteBuffer.allocate(BYTES_IN_HEADER).apply {
        putShort(queryID)
        putShort(0.toShort().apply {
            if (isQuestion) this and QUERY_OR_RESPONSE.mask.inv() else this or QUERY_OR_RESPONSE.mask
            this or (opcode.ordinal shl 11).toShort()
            if (authoritativeAnswer) this or AUTHORITATIVE_ANSWER.mask else this and AUTHORITATIVE_ANSWER.mask.inv()
            if (truncation) this or TRUNCATION.mask else this and TRUNCATION.mask.inv()
            if (recursionDesired) this or RECURSION_DESIRED.mask else this and RECURSION_AVAILABLE.mask.inv()
            if (recursionAvailable) this or RECURSION_AVAILABLE.mask else this and RECURSION_AVAILABLE.mask.inv()
            this or (responseCode.ordinal).toShort()
        })
        putShort(questionCount)
        putShort(answerCount)
        putShort(recordCount)
        putShort(additionalResourceCount)
    }

}
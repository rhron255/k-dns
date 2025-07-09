package rhron255.projects.k_dns.protocol

import rhron255.projects.k_dns.protocol.header.DnsHeader
import java.nio.ByteBuffer

class DnsMessage {
    val header: DnsHeader
    val questions: List<DnsQuestion>
    val answers: List<ResourceRecord>

    constructor(byteBuffer: ByteBuffer) {
        header = DnsHeader(byteBuffer)
        questions = buildList {
            for (i in 0 until header.questionCount) {
                add(DnsQuestion(byteBuffer))
            }
        }
        answers = buildList {
            for (i in 0 until header.answerCount) {
                add(ResourceRecord(byteBuffer))
            }
        }
    }

    constructor(byteArray: ByteArray) : this(ByteBuffer.wrap(byteArray))
    constructor(
        header: DnsHeader,
        questions: List<DnsQuestion>,
        answers: List<ResourceRecord>
    ) {
        this.header = header
        this.questions = questions
        this.answers = answers
    }

    override fun toString(): String {
        return "DnsMessage{\n" +
                "$header\n" +
                "$questions\n" +
                "$answers\n" +
                "}"
    }

    fun toBytes(): ByteArray {
        val headerBytes = header.toBytes()
        val bytes = ByteBuffer.allocate(
            headerBytes.size +
                    questions.sumOf { it.toBytes().size } +
                    answers.sumOf { it.toBytes().size }
        )
        bytes.put(headerBytes)
        questions.forEach { bytes.put(it.toBytes()) }
        answers.forEach { bytes.put(it.toBytes()) }
        return bytes.array()
    }
}
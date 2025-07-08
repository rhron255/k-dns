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

    override fun toString(): String {
        return "DnsMessage{\n" +
                "$header\n" +
                "$questions\n" +
                "$answers\n" +
                "}"
    }
}